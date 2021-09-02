package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;

public class KanonSprite extends EnemySprite implements TotallyPassable {

    private static Image fireFrames[];
    private boolean isFire;
    //private int currFrame;
    private float targetDx;
    private float targetDy;
    float fireTimer;

    public KanonSprite(float x, float y, float dx, float dy) {
        //currFrame = 0;
        fireTimer = 0.0F;
        super.width = 11;
        super.height = 11;
        super.isSolid = false;
        super.canPassSolid = false;
        isFire = true;
        targetDx = dx * 150F;
        targetDy = dy * 150F;
        initPos(x, y);
    }

    public KanonSprite() {
        //currFrame = 0;
        fireTimer = 0.0F;
        super.width = 32;
        super.height = 22;
        super.isSolid = false;
        super.canPassSolid = true;
        super.isCollidable = false;
        getReferenceImage();
        if (Sprite.loader != null)
            Sprite.loader.progress();
        if (fireFrames == null) {
            fireFrames = getFrames("skott.gif", 11, 11);
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public boolean canBeJumpedUpon() {
        return false;
    }

    @Override
	public void paint(Graphics g) {
        if (isFire) {
            drawImage(g, fireFrames[(int)(fireTimer * 7F) % fireFrames.length]);
        } else {
            drawImage(g, getReferenceImage());
        }
    }

    @Override
	public void jumpedUpon(Sprite sprite) { }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if (isFire && !(sprite instanceof TotallyPassable))
            explode();
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/kanon.gif");
    }

    @Override
	public Sprite getCopy() {
        KanonSprite kanonsprite = new KanonSprite();
        kanonsprite.freezable = super.freezable;
        return kanonsprite;
    }

    @Override
	public void initPos(float x, float y) {
        fireTimer = (x / 100F) % 1.4F;
        super.initPos(x, y);
    }

    @Override
	public void updateSpriteLogic(double d) {
        fireTimer += d;
        if (isFire) {
            super.accy = -super.dy + targetDy;
            super.accx = -super.dx + targetDx;
            if (super.collLeft || super.collRight || super.collUp || super.collDown)
                explode();
            return;
        }
        PlayerSprite playersprite = PlayerSprite.getReference();
        float x = (((Sprite) (playersprite)).x + ((Sprite) (playersprite)).width / 2) - (super.x + super.width / 2);
        float y = (((Sprite) (playersprite)).y + ((Sprite) (playersprite)).height / 2) - (super.y + super.height / 2);
        float f = x * x + y * y;
        if (f < 62500F && fireTimer > 1.5F) {
            f = (float)Math.sqrt(f);
            addSprite(new GlensSprite(super.x, super.y, true));
            addSprite(new KanonSprite(super.x + 12F, super.y + 12F, x / f, y / f));
            fireTimer = 0.0F;
        }
    }

    private void explode() {
        if (!isKilled()) {
            addSprite(new ExplosionSprite(super.x - 10F, super.y - 10F));
            kill();
            SoundManager.ref(Game.getReference()).play(super.x, super.y, "exp");
        }
    }

    @Override
	public float playerPainInflict(float f, float f1) {
        return !isFire ? 0.0F : 1.0F;
    }
}
