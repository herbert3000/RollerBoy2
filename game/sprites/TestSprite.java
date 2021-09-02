package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class TestSprite extends Sprite {

    private float orgX;
    private float orgY;
    private static Image frames[];
    private static String loadedImageName;
    private int currentFrame;
    float xs;
    float ys;
    float xPeriod;
    float yPeriod;
    float xAmp;
    float yAmp;
    //private final int STATE_UP = 0;
    //private final int STATE_DOWN = 1;
    //private int state;
    float olddx;
    float olddy;
    private float time;

    public TestSprite() {
        xs = 0.0F;
        ys = 0.0F;
        xPeriod = 2.0F;
        yPeriod = 2.0F;
        xAmp = 2.0F;
        yAmp = 2.0F;
        //state = 1;
        super.width = 96;
        super.height = 55;
        super.isSolid = true;
        super.canPassSolid = false;
        String s = "hiss2" + getTileSetSuffix() + ".gif";
        if (!s.equals(loadedImageName)) {
            flushFrames(frames);
            frames = getFrames(s, super.width, super.height);
            loadedImageName = s;
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public void paint(Graphics g) {
        drawImage(g, frames[currentFrame]);
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/hiss2_ref.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        xs = datainputstream.readFloat();
        xPeriod = datainputstream.readFloat();
        xAmp = datainputstream.readFloat();
        ys = datainputstream.readFloat();
        yPeriod = datainputstream.readFloat();
        yAmp = datainputstream.readFloat();
    }

    @Override
	public Sprite getCopy() {
        TestSprite testsprite = new TestSprite();
        testsprite.freezable = super.freezable;
        testsprite.xs = xs;
        testsprite.xPeriod = xPeriod;
        testsprite.xAmp = xAmp;
        testsprite.ys = ys;
        testsprite.yPeriod = yPeriod;
        testsprite.yAmp = yAmp;
        return testsprite;
    }

    @Override
	public void initPos(float x, float y) {
        orgX = x;
        orgY = y;
        super.initPos(x, y);
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeFloat(xs);
        dataoutputstream.writeFloat(xPeriod);
        dataoutputstream.writeFloat(xAmp);
        dataoutputstream.writeFloat(ys);
        dataoutputstream.writeFloat(yPeriod);
        dataoutputstream.writeFloat(yAmp);
    }

    @Override
	public void updateSpriteLogic(double d) {
        if ("-ufo".equals(getTileSetSuffix())) {
            if (time > 0.3F) {
                currentFrame++;
                currentFrame %= 2;
                time = 0.0F;
            }
        } else if (time > 0.06F) {
            currentFrame++;
            currentFrame %= 2;
            time = 0.0F;
        }
        time += d;
        float f = (float)(orgX + Math.sin(xs) * xAmp * 32D);
        float f1 = (float)(orgY + Math.sin(ys) * yAmp * 32D);
        xs += (6.2831853071795862D / xPeriod) * d;
        ys += (6.2831853071795862D / yPeriod) * d;
        super.accx = (float)((f - super.x) / d - super.dx);
        super.accy = (float)((f1 - super.y) / d - super.dy);
        if (super.accy > 1000F)
            super.accy = 1000F;
        if (super.accy < -1000F)
            super.accy = -1000F;
        if (super.accx > 1000F)
            super.accx = 1000F;
        if (super.accx < -1000F)
            super.accx = -1000F;
        if (super.dx > 0.0F && olddx < 0.0F || super.dx < 0.0F && olddx > 0.0F || super.dy > 0.0F && olddy < 0.0F || super.dy < 0.0F && olddy > 0.0F)
            SoundManager.ref(Game.getReference()).play(super.x, super.y, "sinehiss");
        olddx = super.dx;
        olddy = super.dy;
    }
}
