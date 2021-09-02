package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class Test2Sprite extends Sprite {

	private enum State { UP, DOWN, PAUSE };
    //private float orgX;
    private float orgY;
    float len;
    private static Image frames[];
    private static String loadedImageName;
    private int currentFrame;
    private State turnToState;
    private State state;
    private float time;
    private float lastY;
    private float beenStuckFor;
    private float pauseTimer;

    public Test2Sprite() {
        len = 2.0F;
        state = State.DOWN;
        super.width = 96;
        super.height = 96;
        super.isSolid = true;
        super.canPassSolid = false;
        String s = "kugghiss" + getTileSetSuffix() + ".gif";
        if (!s.equals(loadedImageName)) {
            flushFrames(frames);
            frames = getFrames(s, super.width, super.height);
            loadedImageName = s;
            return;
        } else {
            stepFramesProgress();
            return;
        }
    }

    @Override
	public void paint(Graphics g) {
        drawImage(g, frames[currentFrame]);
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/kugghiss_ref.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        len = datainputstream.readFloat();
    }

    @Override
	public Sprite getCopy() {
        Test2Sprite test2sprite = new Test2Sprite();
        test2sprite.freezable = super.freezable;
        test2sprite.len = len;
        return test2sprite;
    }

    @Override
	public void initPos(float x, float y) {
        //orgX = x;
        orgY = y;
        super.initPos(x, y);
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeFloat(len);
    }

    @Override
	public void updateSpriteLogic(double d) {
        time += d;
        switch(state) {
        default:
            break;

        case UP:
            super.accy = -170F - super.dy;
            if (super.y <= (orgY - len * 32F) + 1.0F) {
                SoundManager.ref(Game.getReference()).play("stop");
                state = State.PAUSE;
                turnToState = State.DOWN;
                pauseTimer = 0.0F;
            }
            break;

        case DOWN:
            super.accy = 170F - super.dy;
            if (super.y >= orgY + len * 32F) {
                SoundManager.ref(Game.getReference()).play("stop");
                state = State.PAUSE;
                turnToState = State.UP;
                pauseTimer = 0.0F;
            }
            break;

        case PAUSE:
            super.accy = -super.dy;
            if (pauseTimer > 1.5F)
                state = turnToState;
            break;
        }
        pauseTimer += d;
        if (lastY - super.y == 0.0F && state != State.PAUSE) {
            if (beenStuckFor > 1.0F) {
                if (state == State.DOWN)
                    state = State.UP;
                else
                    state = State.DOWN;
                beenStuckFor = 0.0F;
            }
        } else {
            beenStuckFor = 0.0F;
            if (state != State.PAUSE && time > 0.1F) {
                currentFrame++;
                currentFrame %= frames.length;
                time = 0.0F;
            }
        }
        beenStuckFor += d;
        lastY = super.y;
    }
}
