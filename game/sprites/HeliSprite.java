package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class HeliSprite extends EnemySprite {

	private enum State { ALIVE, DYING };
    private float orgX;
    private float orgY;
    private static Image frames[];
    private int currentFrame;
    private int currentFrameCount;
    boolean dropBombs;
    private int pingpong[] = { 0, 1, 2, 1 };
    private float timer;
    private float xs;
    private float ys;
    private State state;
    private float bombTimer;

    public HeliSprite() {
        timer = 0.0F;
        state = State.ALIVE;
        bombTimer = (float)Math.random();
        super.width = 28;
        super.height = 22;
        super.isSolid = false;
        super.canPassSolid = true;
        if (frames == null) {
            frames = getFrames("heli.gif", super.width, super.height);
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public void paint(Graphics g) {
        drawImage(g, frames[currentFrame]);
    }

    @Override
	public void jumpedUpon(Sprite sprite) {
        SoundManager.ref(Game.getReference()).play("heli-kill");
        addSprite(new PointSprite(super.x, super.y, 500, PlayerSprite.getReference().getKillMultiplier()));
        addSprite(new GlensSprite(super.x, super.y, true));
        state = State.DYING;
        timer = 0.0F;
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/heli_ref.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        dropBombs = datainputstream.readBoolean();
    }

    @Override
	public Sprite getCopy() {
        HeliSprite helisprite = new HeliSprite();
        helisprite.freezable = super.freezable;
        helisprite.dropBombs = dropBombs;
        return helisprite;
    }

    @Override
	public void initPos(float x, float y) {
        orgX = x;
        orgY = y;
        xs = x / 100F + y / 170F;
        ys = x / 200F + y / 90F;
        bombTimer = (x / 100F) % 1.4F;
        super.initPos(x, y);
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeBoolean(dropBombs);
    }

    @Override
	public void updateSpriteLogic(double d) {
        switch(state) {
        case ALIVE:
            if (timer > 0.1F) {
                currentFrameCount++;
                timer = 0.0F;
            }
            currentFrame = pingpong[currentFrameCount % pingpong.length];
            float f = (float)(orgX + Math.sin(xs) * 12D);
            float f1 = (float)(orgY + Math.sin(ys) * 20D);
            xs += 4.8332196443426598D * d;
            ys += 3.1415926535897931D * d;
            super.accx = (float)((f - super.x) / d - super.dx);
            super.accy = (float)((f1 - super.y) / d - super.dy);
            break;

        case DYING:
            super.isCollidable = false;
            super.accy = 3F;
            super.accx = -super.dx;
            if (timer > 2.0F)
                kill();
            currentFrame = 3;
            break;
        }
        if (dropBombs && state != State.DYING) {
            bombTimer += d;
            if (bombTimer > 1.4F) {
                bombTimer = 0.0F;
                addSprite(new BombSprite(super.x + 8F, super.y + 30F));
            }
        }
        timer += d;
    }

    @Override
	public float playerPainInflict(float f, float f1) {
        return 1.0F;
    }
}
