package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;

public class CoinSprite extends Sprite implements TotallyPassable {

    public static Image frames[];
    private float currentFrame;
    float t;
    float t2;

    public CoinSprite() {
        super.width = 24;
        super.height = 24;
        super.isSolid = false;
        super.canPassSolid = false;
        super.isCollidable = true;
        if (frames == null) {
            frames = getFrames("coin.gif", super.width, super.height);
        } else {
            stepFramesProgress();
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
            addSprite(new PointSprite(super.x, super.y, 100));
            SoundManager.ref(Game.getReference()).play("coin");
            PlayerSprite.getReference().addCoin();
            kill();
        }
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/coin_ref.gif");
    }

    @Override
	public Sprite getCopy() {
        CoinSprite coinsprite = new CoinSprite();
        coinsprite.freezable = super.freezable;
        return coinsprite;
    }

    @Override
	public void initPos(float x, float y) {
        currentFrame = (x / 32F + y / 32F) % frames.length;
        super.initPos(x, y);
    }

    @Override
	public void updateSpriteLogic(double d) {
        t += d;
        t2 += d;
        if (t2 > 0.1F) {
            currentFrame++;
            if (currentFrame > frames.length - 1)
                currentFrame = 0.0F;
            t2 = 0.0F;
        }
    }
}
