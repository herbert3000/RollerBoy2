package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;

public class HeartSprite extends Sprite implements TotallyPassable {

    private static Image frames[];
    private float currentFrame;
    private float orgY;
    private boolean effectedByGrav;
    private float startTimer;
    float t;

    public HeartSprite(float x, float y, float dx, float dy) {
        this();
        initPos(x, y);
        super.dx = dx;
        super.dy = dy;
        effectedByGrav = true;
        startTimer = 1.0F;
    }

    public HeartSprite() {
        effectedByGrav = false;
        super.width = 27;
        super.height = 27;
        super.isSolid = false;
        super.canPassSolid = false;
        super.isCollidable = true;
        if (frames == null) {
            frames = getFrames("heart.gif", super.width, super.height);
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public void paint(Graphics g) {
        drawImage(g, frames[(int)currentFrame]);
    }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if ((sprite instanceof PlayerSprite) && startTimer < 0.0F && !isKilled()) {
            boolean flag = ((PlayerSprite)sprite).addHealth(1.0F);
            kill();
            addSprite(new GlensSprite(super.x, super.y));
            addSprite(new PointSprite(super.x, super.y, flag ? 600 : 250));
            SoundManager.ref(Game.getReference()).play("heart");
        }
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/heart_ref.gif");
    }

    @Override
	public Sprite getCopy() {
        HeartSprite heartsprite = new HeartSprite();
        heartsprite.freezable = super.freezable;
        return heartsprite;
    }

    @Override
	public void initPos(float x, float y) {
        t = x / 100F + y / 90F;
        orgY = y;
        super.initPos(x, y);
    }

    @Override
	public void updateSpriteLogic(double d) {
        t += d;
        startTimer -= d;
        if (!effectedByGrav) {
            float f = (float)(orgY + Math.sin(t * 5F) * 7D);
            super.accy = (float)((f - super.y) / d - super.dy);
        } else {
            super.accy = 2.0F;
        }
        currentFrame = (float)(((Math.sin(t * 12F) + 1.0D) / 2D) * frames.length);
        if (currentFrame > frames.length - 1)
            currentFrame = frames.length - 1;
    }
}
