package game.sprites;

import game.TotallyPassable;
import game.Sprite;
import java.awt.Graphics;
import java.awt.Image;

public class GlensSprite extends Sprite implements TotallyPassable {

    private static Image framesGlens[];
    private static Image framesDust[];
    private float currentFrame;
    private boolean dust;

    static  {
        if(Sprite.loader != null)
            Sprite.loader.progress();
    }

    public GlensSprite(float f, float f1, boolean flag) {
        this(f, f1);
        dust = flag;
    }

    public GlensSprite(float x, float y) {
        this();
        initPos(x, y);
    }

    public GlensSprite() {
        super.width = 32;
        super.height = 32;
        super.isSolid = false;
        super.canPassSolid = false;
        super.isCollidable = false;
        super.freezable = false;
        if (framesGlens == null) {
            framesGlens = getFrames("glens.gif", super.width, super.height);
            framesDust = getFrames("damm.gif", super.width, super.height);
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public void paint(Graphics g) {
        if (dust) {
            drawImage(g, framesDust[(int)currentFrame]);
            return;
        } else {
            drawImage(g, framesGlens[(int)currentFrame]);
            return;
        }
    }

    @Override
	public Image getReferenceImage() {
        return null;
    }

    @Override
	public Sprite getCopy() {
        GlensSprite glenssprite = new GlensSprite();
        glenssprite.freezable = super.freezable;
        return glenssprite;
    }

    @Override
	public void initPos(float x, float y) {
        super.initPos(x, y);
    }

    @Override
	public void updateSpriteLogic(double d) {
        if (dust)
            currentFrame += d * 15D;
        else
            currentFrame += d * 10D;
        if (dust) {
            if ((int)currentFrame > framesDust.length - 1)
                kill();
        } else if ((int)currentFrame > framesGlens.length - 1)
            kill();
        super.accy = -0.5F;
    }
}
