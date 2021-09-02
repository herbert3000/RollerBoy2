package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class AmobaSprite extends EnemySprite {

	private enum State { LEFT, RIGHT, TURN, DYING };
    //private float orgX;
    //private float orgY;
    private static Image frames[];
    private int currentFrame;
    float len;
    //private boolean doJump;
    private float beenStuckFor;
    private float lastX;
    private float lastY;
    private boolean blink;
    private float killedTimer;
    private float blinkTimer;
    private State state;
    private State turnToState;
    private float timer;

    public AmobaSprite() {
        len = 2.0F;
        beenStuckFor = 0.0F;
        lastX = 0.0F;
        lastY = 0.0F;
        state = State.LEFT;
        timer = 0.0F;
        super.width = 22;
        super.height = 12;
        super.isSolid = false;
        super.canPassSolid = false;
        if (frames == null) {
            frames = getFrames("gronamoba.gif", super.width, super.height);
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public void paint(Graphics g) {
        if (killedTimer < 0.8D || blink)
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
            SoundManager.ref(Game.getReference()).play("amoba-kill");
            addSprite(new PointSprite(super.x, super.y, 400, PlayerSprite.getReference().getKillMultiplier()));
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
        return ImageManager.getImage("sprites/gronamoba_ref.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        len = datainputstream.readFloat();
    }

    @Override
	public Sprite getCopy() {
        AmobaSprite amobasprite = new AmobaSprite();
        amobasprite.len = len;
        amobasprite.freezable = super.freezable;
        return amobasprite;
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
        dataoutputstream.writeFloat(len);
    }

    @Override
	public void updateSpriteLogic(double d) {
        switch(state) {
        default:
            break;

        case LEFT:
            super.accx = -70 - super.dx;
            currentFrame = 3 + (int)((timer * 6.0) % 2.0);
            break;

        case RIGHT:
            super.accx = 70F - super.dx;
            currentFrame = 2 - (int)((timer * 6.0) % 2.0);
            break;

        case TURN:
            currentFrame = 0;
            super.accx = -super.dx;
            if (timer > 0.1 + Math.random() * 0.2)
                state = turnToState;
            break;

        case DYING:
            super.isCollidable = false;
            currentFrame = 5;
            super.accx = -super.dx;
            killedTimer += d;
            if (blinkTimer > 0.1) {
                blinkTimer = 0.0F;
                blink = !blink;
            }
            blinkTimer += d;
            if (killedTimer > 1.5)
                kill();
            break;
        }
        timer += d;
        super.accy = 2.6F;
        if (lastY - super.y == 0.0F)
            super.accy = -super.dy + 2.6F;
        lastY = super.y;
        if (lastX - super.x == 0.0F && state != State.DYING) {
            if (beenStuckFor > 0.3 + Math.random() * 0.4) {
                if (state == State.LEFT)
                    turn(State.RIGHT);
                else
                    turn(State.LEFT);
                beenStuckFor = 0.0F;
            }
        } else {
            beenStuckFor = 0.0F;
        }
        beenStuckFor += d;
        if (super.collLeft)
            turn(State.RIGHT);
        if (super.collRight)
            turn(State.LEFT);
        lastX = super.x;
    }

    @Override
	public float playerPainInflict(float f, float f1) {
        return state != State.DYING ? 1.0F : 0.0F;
    }
}
