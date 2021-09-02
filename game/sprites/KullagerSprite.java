package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;

public class KullagerSprite extends Sprite implements TotallyPassable {

    private static Image frames[];
    private float currentFrame;
    //private float orgY;
    float t;
    float t2;

    public KullagerSprite() {
        super.width = 32;
        super.height = 32;
        super.isSolid = false;
        super.canPassSolid = false;
        super.isCollidable = true;
        if (frames == null) {
            frames = getFrames("kullager.gif", super.width, super.height);
            return;
        } else {
            stepFramesProgress();
            return;
        }
    }

    @Override
	public void paint(Graphics g) {
        drawImage(g, frames[(int)currentFrame]);
    }

    @Override
	public boolean isMovable() {
        return false;
    }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if ((sprite instanceof PlayerSprite) && !isKilled()) {
            addSprite(new GlensSprite(super.x, super.y));
            addSprite(new PointSprite(super.x, super.y, 1000));
            SoundManager.ref(Game.getReference()).play("kullager");
            ((PlayerSprite)sprite).addCoins(10);
            kill();
        }
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/kullager_ref.gif");
    }

    @Override
	public Sprite getCopy() {
        KullagerSprite kullagersprite = new KullagerSprite();
        kullagersprite.freezable = super.freezable;
        return kullagersprite;
    }

    @Override
	public void initPos(float x, float y) {
        currentFrame = (x / 32F / 2.0F + y / 32F / 2.0F) % frames.length;
        //orgY = y;
        super.initPos(x, y);
    }

    @Override
	public void updateSpriteLogic(double d) {
        t += d;
        t2 += d;
        if (t2 > 0.1) {
            currentFrame++;
            if (currentFrame > frames.length - 1)
                currentFrame = 0.0F;
            t2 = 0.0F;
        }
    }
}
