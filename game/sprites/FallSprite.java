package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Vector;

public class FallSprite extends Sprite {

	private enum State { STILL, FALLING };
    float orgX;
    float orgY;
    private static Vector<FallSprite> fallList = new Vector<FallSprite>();
    private static String loadedImageName;
    private int yImageShift;
    State state;
    private float timer;
    boolean isKilled;

    @Override
	public void kill() {
        isKilled = true;
        super.kill();
    }

    public FallSprite() {
        state = State.STILL;
        timer = 0.0F;
        if (getTileSetSuffix().equals("-dt")) {
            super.width = 32;
            super.height = 7;
            yImageShift = -5;
        } else {
            yImageShift = 0;
            super.width = 32;
            super.height = 16;
        }
        String s = "fall" + getTileSetSuffix() + ".gif";
        if (!s.equals(loadedImageName)) {
            loadedImageName = s;
            getReferenceImage();
        }
        if (Sprite.loader != null)
            Sprite.loader.progress();
        super.isSolid = true;
        super.canPassSolid = false;
    }

    @Override
	public void paint(Graphics g) {
        super.y += yImageShift;
        drawImage(g, getReferenceImage());
        super.y -= yImageShift;
    }

    public static void restoreFallList() {
        FallSprite afallsprite[] = new FallSprite[fallList.size()];
        fallList.copyInto(afallsprite);
        for (int i = 0; i < afallsprite.length; i++) {
            FallSprite fallsprite = afallsprite[i];
            if (fallsprite.state != State.STILL || fallsprite.isKilled) {
                fallList.removeElement(fallsprite);
                FallSprite fallsprite1 = new FallSprite();
                fallsprite1.initPos(fallsprite.orgX, fallsprite.orgY);
                Sprite.spriteManager.addSprite(fallsprite1);
            }
        }
    }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if ((sprite instanceof PlayerSprite) && f1 == -1F) {
            timer = 0.0F;
            state = State.FALLING;
        }
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/" + loadedImageName);
    }

    @Override
	public Sprite getCopy() {
        FallSprite fallsprite = new FallSprite();
        fallsprite.freezable = super.freezable;
        return fallsprite;
    }

    @Override
	public void initPos(float x, float y) {
        orgX = x;
        orgY = y;
        fallList.addElement(this);
        super.initPos(x, y);
    }

    public static void resetFallList() {
        fallList.removeAllElements();
    }

    @Override
	public void updateSpriteLogic(double d) {
        if (state == State.FALLING) {
            if (timer > 0.3)
                super.accy = (float)(300D * d);
        }
        if (super.collDown) {
            addSprite(new GlensSprite(super.x, super.y, true));
            SoundManager.ref(Game.getReference()).play(super.x, super.y, "exp");
            kill();
        }
        timer += d;
    }
}
