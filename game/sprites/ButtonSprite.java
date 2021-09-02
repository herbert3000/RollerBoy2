package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class ButtonSprite extends Sprite {

	private enum State { STILL, PUSHING, RELEASING, PUSHED };
    private static Image frames[];
    private static String loadedImageName;
    //private float orgX;
    //private float orgY;
    private boolean pob;
    public int flag;
    private State state;
    //private float timer;
    private boolean firstTime;

    public ButtonSprite() {
        pob = false;
        state = State.STILL;
        //timer = 0.0F;
        firstTime = true;
        super.width = 32;
        super.height = 25;
        super.isSolid = true;
        super.canPassSolid = false;
        String s = "button" + getTileSetSuffix() + ".gif";
        if (!s.equals(loadedImageName)) {
            flushFrames(frames);
            frames = getFrames(s, super.width, 32);
            loadedImageName = s;
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public void paint(Graphics g) {
        super.y -= 7F;
        drawImage(g, frames[state != State.STILL ? 1 : 0]);
        super.y += 7F;
    }

    @Override
	public boolean isMovable() {
        return false;
    }

    public void somethingOnButton() {
        pob = true;
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/button.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        flag = datainputstream.readInt();
    }

    @Override
	public Sprite getCopy() {
        ButtonSprite buttonsprite = new ButtonSprite();
        buttonsprite.freezable = super.freezable;
        buttonsprite.flag = flag;
        return buttonsprite;
    }

    @Override
	public void initPos(float x, float y) {
        //orgX = x;
        //orgY = y;
        super.initPos(x, y);
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeInt(flag);
    }

    @Override
	public void updateSpriteLogic(double d) {
        if (firstTime) {
            firstTime = false;
            super.y += 7F;
            //orgY += 7F;
        }
        switch(state) {
        default:
            break;

        case STILL:
            if (pob) {
                SoundManager.ref(Game.getReference()).play("button");
                state = State.PUSHING;
            }
            break;

        case PUSHING:
            state = State.PUSHED;
            break;

        case RELEASING:
            state = State.STILL;
            break;

        case PUSHED:
            if (!pob)
                state = State.RELEASING;
            Sprite.world.setFlag(flag, true);
            break;
        }
        pob = false;
        //timer += d;
    }
}
