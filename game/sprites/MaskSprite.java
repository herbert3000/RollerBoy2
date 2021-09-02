package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;

public class MaskSprite extends EnemySprite {

	private enum State { LEFT, RIGHT, TURN, DYING };
    private static Image frames[];
    private int currentFrame;
    private int dieFrame;
    private boolean blink;
    private float killedTimer;
    private float blinkTimer;
    private State state;
    private State turnToState;
    private float timer;
    private static final int pingpong[] = { 0, 1, 2, 1, 0, 0 };

    public MaskSprite() {
        state = State.LEFT;
        timer = 0.0F;
        super.width = 61;
        super.height = 15;
        super.isSolid = false;
        super.canPassSolid = false;
        if (frames == null) {
            frames = getFrames("alien_mask.gif", super.width, super.height);
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
            SoundManager.ref(Game.getReference()).play("mask-kill");
            addSprite(new PointSprite(super.x, super.y, 150, PlayerSprite.getReference().getKillMultiplier()));
            addSprite(new GlensSprite(super.x + 20F, super.y, true));
            if (state == State.RIGHT)
                dieFrame = 8;
            else
                dieFrame = 7;
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
        return ImageManager.getImage("sprites/alien_mask_ref.gif");
    }

    @Override
	public Sprite getCopy() {
        MaskSprite masksprite = new MaskSprite();
        masksprite.freezable = super.freezable;
        return masksprite;
    }

    @Override
	public void updateSpriteLogic(double d) {
        switch(state) {
        default:
            break;

        case LEFT:
            int i = (int)((timer * 6F) % pingpong.length);
            if (i == 0 || i >= 3)
                super.accx = -35F - super.dx;
            else
                super.accx = -super.dx;
            currentFrame = pingpong[i];
            break;

        case RIGHT:
            int j = (int)((timer * 6F) % pingpong.length);
            if (j == 0 || j >= 3)
                super.accx = 35F - super.dx;
            else
                super.accx = -super.dx;
            currentFrame = 4 + (2 - pingpong[j]);
            break;

        case TURN:
            currentFrame = 3;
            super.accx = -super.dx;
            if (timer > 0.2)
                state = turnToState;
            break;

        case DYING:
            super.isCollidable = false;
            currentFrame = dieFrame;
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
