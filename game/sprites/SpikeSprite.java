package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class SpikeSprite extends EnemySprite {

	private enum State { UP, DOWN, LEFT, RIGHT, KILLED };
    public float speed;
    private static Image frames[];
    private static String loadedImageName;
    private int currentFrame;
    public State state;
    private boolean isJumpable;
    private boolean blink;
    private float killedTimer;
    private float blinkTimer;
    private float timer;

    public SpikeSprite() {
        isJumpable = false;
        timer = 0.0F;
        super.width = 25;
        super.height = 25;
        super.isSolid = false;
        super.canPassSolid = false;
        String s = "punk" + getTileSetSuffix() + ".gif";
        if (!s.equals(loadedImageName)) {
            frames = getFrames(s, super.width, super.height);
            loadedImageName = s;
        } else {
            stepFramesProgress();
        }
        if (getTileSetSuffix().equals("-dt"))
            isJumpable = true;
    }

    @Override
	public boolean canBeJumpedUpon() {
        return isJumpable;
    }

    @Override
	public String toString() {
        return "state: " + state + " - " + super.toString();
    }

    @Override
	public void paint(Graphics g) {
        if (killedTimer < 0.8 || blink)
            drawImage(g, frames[currentFrame]);
    }

    private void turn(State state) {
        this.state = state;
    }

    @Override
	public void jumpedUpon(Sprite sprite) {
        if (state != State.KILLED) {
            SoundManager.ref(Game.getReference()).play("blob-kill");
            addSprite(new PointSprite(super.x, super.y, 300, PlayerSprite.getReference().getKillMultiplier()));
            addSprite(new GlensSprite(super.x, super.y, true));
            state = State.KILLED;
            super.isCollidable = false;
        }
    }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if (sprite instanceof AIArrowSprite) {
            AIArrowSprite aiarrowsprite = (AIArrowSprite)sprite;
            if (aiarrowsprite.dir == 2) {
                turn(State.LEFT);
            } else if (aiarrowsprite.dir == 3) {
                turn(State.RIGHT);
            } else if (aiarrowsprite.dir == 0) {
                turn(State.UP);
            } else if(aiarrowsprite.dir == 1) {
                turn(State.DOWN);
            }
        }
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/spike_ref.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        speed = datainputstream.readFloat();
        int s = datainputstream.readInt();
        switch (s) {
        case 0:
        	state = State.UP;
        	break;
        case 1:
        	state = State.DOWN;
        	break;
        case 2:
        	state = State.LEFT;
        	break;
        case 3:
        	state = State.RIGHT;
        	break;
        default:
        	state = State.KILLED;
        }
    }

    @Override
	public Sprite getCopy() {
        SpikeSprite spikesprite = new SpikeSprite();
        spikesprite.freezable = super.freezable;
        spikesprite.speed = speed;
        spikesprite.state = state;
        return spikesprite;
    }

    @Override
	public void initPos(float x, float y) {
        super.initPos(x, y);
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeFloat(speed);
        int s = -1;
        switch (state) {
        case UP:
        	s = 0;
        	break;
        case DOWN:
        	s = 1;
        	break;
        case LEFT:
        	s = 2;
        	break;
        case RIGHT:
        	s = 3;
        	break;
		default:
			break;
        }
        dataoutputstream.writeInt(s);
    }

    @Override
	public void updateSpriteLogic(double d) {
        currentFrame = (int)((timer * 6F) % 2.0F);
        switch(state) {
        default:
            break;

        case LEFT:
            super.accx = -speed - super.dx;
            super.accy = -super.dy;
            break;

        case RIGHT:
            super.accx = speed - super.dx;
            super.accy = -super.dy;
            break;

        case UP:
            super.accx = -super.dx;
            super.accy = -speed - super.dy;
            break;

        case DOWN:
            super.accx = -super.dx;
            super.accy = speed - super.dy;
            break;

        case KILLED:
            super.accx = -super.dx;
            super.accy = -super.dy;
            killedTimer += d;
            currentFrame = 2;
            if (blinkTimer > 0.1) {
                blinkTimer = 0.0F;
                blink = !blink;
            }
            blinkTimer += d;
            if (killedTimer > 1.5F)
                kill();
            break;
        }
        timer += d;
        if (super.collLeft)
            turn(State.RIGHT);
        if (super.collRight)
            turn(State.LEFT);
        if (super.collUp)
            turn(State.DOWN);
        if (super.collDown)
            turn(State.UP);
    }

    @Override
	public float playerPainInflict(float f, float f1) {
        if (state == State.KILLED)
            return 0.0F;
        if (isJumpable && f1 != 1.0F && f == 0.0F) {
            addSprite(new GlensSprite(super.x, super.y, true));
            state = State.KILLED;
            kill();
        }
        return 1.0F;
    }
}
