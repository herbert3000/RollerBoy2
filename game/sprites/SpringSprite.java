package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class SpringSprite extends Sprite {

    private static Image framesu[];
    private static Image frameslr[];
    private static String loadedImageNameu;
    private static String loadedImageNamelr;
    private Image frames[];
    private float currentFrame;
    private int baseFrame;
    int dir;
    float t;
    float t2;

    public SpringSprite() {
        t2 = -1F;
        super.width = 32;
        super.height = 32;
        super.isSolid = false;
        super.canPassSolid = false;
        super.isCollidable = true;
    }

    @Override
	public void paint(Graphics g) {
        if (dir == 0) {
            super.y -= super.height;
        } else {
            if (dir == 2)
                super.x -= 22F;
            super.y -= 22F;
        }
        drawImage(g, frames[baseFrame + (int)currentFrame]);
        if (dir == 0) {
            super.y += super.height;
            return;
        }
        if (dir == 2)
            super.x += 22F;
        super.y += 22F;
    }

    public void fireSpring() {
        if (t > 0.5F) {
            currentFrame = 1.0F;
            t2 = 0.0F;
            t = 0.0F;
            SoundManager.ref(Game.getReference()).play("spring");
        }
    }

    @Override
	public boolean isMovable() {
        return false;
    }

    @Override
	public Image getReferenceImage() {
        if (dir == 0)
            return ImageManager.getImage("sprites/springu_ref.gif");
        if (dir == 1)
            return ImageManager.getImage("sprites/springd_ref.gif");
        if (dir == 2)
            return ImageManager.getImage("sprites/springl_ref.gif");
        if (dir == 3)
            return ImageManager.getImage("sprites/springr_ref.gif");
        else
            return null;
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        dir = datainputstream.readInt();
        loadImages();
    }

    @Override
	public Sprite getCopy() {
        SpringSprite springsprite = new SpringSprite();
        springsprite.freezable = super.freezable;
        springsprite.dir = dir;
        springsprite.loadImages();
        return springsprite;
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeInt(dir);
    }

    void loadImages() {
        if (dir == 0) {
            String s = "springup" + getTileSetSuffix() + ".gif";
            if (!s.equals(loadedImageNameu)) {
                flushFrames(framesu);
                framesu = getFrames(s, super.width, super.height * 2);
                loadedImageNameu = s;
            } else {
                stepFramesProgress();
            }
            frames = framesu;
            return;
        }
        String s1 = "springlr" + getTileSetSuffix() + ".gif";
        if (!s1.equals(loadedImageNamelr)) {
            flushFrames(frameslr);
            frameslr = getFrames(s1, 54, 54);
            loadedImageNamelr = s1;
        } else {
            stepFramesProgress();
        }
        frames = frameslr;
    }

    @Override
	public void updateSpriteLogic(double d) {
        if (dir == 0)
            baseFrame = 0;
        else if (dir == 2)
            baseFrame = 0;
        else if (dir == 3)
            baseFrame = 2;
        t += d;
        if (t2 != -1F) {
            if (t2 > 0.5F)
                currentFrame = 0.0F;
            t2 += d;
        }
    }
}
