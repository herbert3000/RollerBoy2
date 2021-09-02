package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;

public class BombSprite extends EnemySprite {

    private static Image frames[];
    private float currentFrame;

    static  {
        if (Sprite.loader != null)
            Sprite.loader.progress();
    }

    public BombSprite(float f, float f1) {
        this();
        f -= super.width / 2;
        f1 -= super.height / 2;
        initPos(f, f1);
    }

    public BombSprite() {
        super.width = 11;
        super.height = 19;
        super.isSolid = false;
        super.canPassSolid = false;
        super.freezable = false;
        if (frames == null) {
            frames = getFrames("bomb.gif", super.width, super.height);
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public void paint(Graphics g) {
        drawImage(g, frames[2 - (int)currentFrame]);
    }

    @Override
	public void jumpedUpon(Sprite sprite) { }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if (!(sprite instanceof HeliSprite) && !(sprite instanceof TotallyPassable))
            explode();
    }

    @Override
	public Image getReferenceImage() {
        return null;
    }

    @Override
	public Sprite getCopy() {
        return new BombSprite();
    }

    @Override
	public void updateSpriteLogic(double d) {
        currentFrame += d * 17D;
        currentFrame %= 2.9F;
        super.accy = (float)(200D * d);
        if (super.collLeft || super.collRight || super.collDown || super.collUp)
            explode();
    }

    private void explode() {
        if (!isKilled()) {
            addSprite(new ExplosionSprite(super.x - 10F, super.y));
            kill();
            SoundManager.ref(Game.getReference()).play(super.x, super.y, "exp");
        }
    }

    @Override
	public float playerPainInflict(float f, float f1) {
        return 1.0F;
    }
}
