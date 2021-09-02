package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;

public class BrickSprite extends Sprite {

    private boolean derbis;
    float timer;

    public BrickSprite(float x, float y, float dx) {
        super.width = 10;
        super.height = 10;
        super.isSolid = false;
        super.canPassSolid = true;
        super.isCollidable = false;
        initPos(x, y);
        super.dx = dx;
        super.dy = -200F;
        derbis = true;
    }

    public BrickSprite() {
        super.width = 32;
        super.height = 32;
        super.isSolid = true;
        super.canPassSolid = false;
        getReferenceImage();
        if (Sprite.loader != null)
            Sprite.loader.progress();
    }

    @Override
	public void paint(Graphics g) {
        if (derbis) {
            drawImage(g, ImageManager.getImage("sprites/brick-break.gif"));
        } else {
            drawImage(g, getReferenceImage());
        }
    }

    @Override
	public boolean isMovable() {
        return derbis;
    }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if ((!(sprite instanceof PlayerSprite) && !(sprite instanceof EnemySprite) || f1 == 1.0F) && !isKilled()) {
            kill();
            SoundManager.ref(Game.getReference()).play(super.x, super.y, "exp");
            addSprite(new GlensSprite(super.x, super.y, true));
            addSprite(new BrickSprite(super.x + super.width / 2, super.y, -100F));
            addSprite(new BrickSprite(super.x + super.width / 2, super.y, 100F));
        }
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/brick.gif");
    }

    @Override
	public Sprite getCopy() {
        BrickSprite bricksprite = new BrickSprite();
        bricksprite.freezable = super.freezable;
        return bricksprite;
    }

    @Override
	public void updateSpriteLogic(double d) {
        if (derbis) {
            super.accy = (float)(600D * d);
            if (timer > 1.0F)
                kill();
        }
        timer += d;
    }
}
