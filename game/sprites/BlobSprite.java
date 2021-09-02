package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class BlobSprite extends EnemySprite {

	private enum State { LEFT, RIGHT, TURN, DYING };
    //private float orgX;
    //private float orgY;
    private static Image frames[];
    private int currentFrame;
    boolean still;
    private State state;
    private State turnToState;
    private boolean blink;
    private float killedTimer;
    private float blinkTimer;
    private float timer;
    private int pingPong[] = { 0, 1, 2, 1, 0 };

    public BlobSprite() {
        state = State.TURN;
        turnToState = State.LEFT;
        timer = 0.0F;
        super.width = 32;
        super.height = 22;
        super.isSolid = false;
        super.canPassSolid = false;
        if (frames == null) {
            frames = getFrames("blobb.gif", super.width, super.height);
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public void paint(Graphics g) {
        if (killedTimer < 0.8 || blink)
            drawImage(g, frames[currentFrame]);
    }

    private void turn(State state) {
        turnToState = state;
        this.state = State.TURN;
        timer = 0.0F;
    }

    @Override
	public void jumpedUpon(Sprite sprite) {
        if (state != State.DYING) {
            SoundManager.ref(Game.getReference()).play("blob-kill");
            addSprite(new PointSprite(super.x, super.y, 200, PlayerSprite.getReference().getKillMultiplier()));
            addSprite(new GlensSprite(super.x, super.y, true));
            state = State.DYING;
            timer = 0.0F;
        }
    }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if (sprite instanceof AIArrowSprite) {
            AIArrowSprite aiarrowsprite = (AIArrowSprite)sprite;
            if (aiarrowsprite.dir == 2 && state == State.RIGHT) {
                turn(State.LEFT);
                timer = 0.0F;
                return;
            }
            if (aiarrowsprite.dir == 3 && state == State.LEFT) {
                turn(State.RIGHT);
                return;
            }
            if (aiarrowsprite.dir == 0)
                if (!super.collDown);
        }
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/blobb_ref.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        still = datainputstream.readBoolean();
    }

    @Override
	public Sprite getCopy() {
        BlobSprite blobsprite = new BlobSprite();
        blobsprite.still = still;
        blobsprite.freezable = super.freezable;
        return blobsprite;
    }

    @Override
	public void initPos(float x, float y) {
        //orgX = x;
        //orgY = y;
        timer = x / 50F;
        super.initPos(x, y);
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeBoolean(still);
    }

    @Override
	public void updateSpriteLogic(double d) {
        switch(state) {
        default:
            break;

        case LEFT:
            super.accx = -17F - super.dx;
            currentFrame = 1 + pingPong[(int)((timer * 5F) % pingPong.length)];
            break;

        case RIGHT:
            super.accx = 17F - super.dx;
            currentFrame = 1 + pingPong[(int)((timer * 5F) % pingPong.length)];
            break;

        case TURN:
            currentFrame = (int)(timer * 3F) % 2;
            super.accx = -super.dx;
            if (timer > 0.2 && !still)
                state = turnToState;
            break;

        case DYING:
            super.isCollidable = false;
            currentFrame = 4;
            super.accx = -super.dx;
            killedTimer += d;
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
        super.accy = 2.6F;
    }

    @Override
	public float playerPainInflict(float f, float f1) {
        return state != State.DYING ? 1.0F : 0.0F;
    }
}
