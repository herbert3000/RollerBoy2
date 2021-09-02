package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;

public class SpetsSprite extends EnemySprite {

    boolean firstTime;

    @Override
	public boolean canBeJumpedUpon() {
        return false;
    }

    public SpetsSprite() {
        firstTime = true;
        super.width = 32;
        super.height = 30;
        super.isSolid = true;
        super.canPassSolid = false;
        getReferenceImage();
        if (Sprite.loader != null)
            Sprite.loader.progress();
    }

    @Override
	public void paint(Graphics g) {
        drawImage(g, getReferenceImage());
    }

    @Override
	public boolean isMovable() {
        return false;
    }

    @Override
	public void jumpedUpon(Sprite sprite) { }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/spets.gif");
    }

    @Override
	public Sprite getCopy() {
        SpetsSprite spetssprite = new SpetsSprite();
        spetssprite.freezable = super.freezable;
        return spetssprite;
    }

    @Override
	public void initPos(float x, float y) {
        super.initPos(x, y);
    }

    @Override
	public void updateSpriteLogic(double d) {
        if (firstTime) {
            firstTime = false;
            super.y += 2.0F;
        }
    }

    @Override
	public float playerPainInflict(float f, float f1) {
        return !isKilled() && f1 == 1.0F ? 1.0F : 0.0F;
    }
}
