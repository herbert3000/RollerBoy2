package game.sprites;

import game.Sprite;
import java.awt.Graphics;
import java.awt.Image;

public class ExplosionSprite extends Sprite {

    private static Image frames[];
    private float currentFrame;

    static  {
        if (Sprite.loader != null)
            Sprite.loader.progress();
    }

    public ExplosionSprite(float x, float y) {
        this();
        initPos(x, y);
    }

    public ExplosionSprite() {
        super.width = 32;
        super.height = 32;
        super.isSolid = false;
        super.canPassSolid = false;
        super.isCollidable = false;
        super.freezable = false;
        if (frames == null) {
            frames = getFrames("explosion.gif", super.width, super.height);
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public void paint(Graphics g) {
        int i = (int)currentFrame;
        if (i >= frames.length)
            i = frames.length - 1;
        drawImage(g, frames[i]);
    }

    @Override
	public Image getReferenceImage() {
        return null;
    }

    @Override
	public Sprite getCopy() {
        ExplosionSprite explosionsprite = new ExplosionSprite();
        explosionsprite.freezable = super.freezable;
        return explosionsprite;
    }

    @Override
	public void initPos(float x, float y) {
        super.initPos(x, y);
    }

    @Override
	public void updateSpriteLogic(double d) {
        currentFrame += d * 10D;
        if (currentFrame > frames.length)
            kill();
    }
}
