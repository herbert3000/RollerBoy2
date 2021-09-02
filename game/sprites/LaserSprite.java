package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class LaserSprite extends EnemySprite implements TotallyPassable {

    private static Image frames[];
    private static Image laser;
    private boolean isFire;
    //private int currFrame;
    private float targetDx;
    private float targetDy;
    private int currentFrame;
    int dir;
    float fireTimer;

    public LaserSprite(float x, float y, float dx, float dy) {
        //currFrame = 0;
        fireTimer = 0.0F;
        super.width = 32;
        super.height = 5;
        super.isSolid = false;
        super.canPassSolid = false;
        isFire = true;
        targetDx = dx * 250F;
        targetDy = dy * 250F;
        initPos(x, y);
    }

    public LaserSprite() {
        //currFrame = 0;
        fireTimer = 0.0F;
        super.width = 32;
        super.height = 31;
        super.isSolid = false;
        super.canPassSolid = true;
        super.isCollidable = false;
        if (laser == null)
            laser = ImageManager.getImage("sprites/laser.gif");
        if (Sprite.loader != null)
            Sprite.loader.progress();
        if (frames == null) {
            frames = getFrames("lasergun.gif", super.width, super.height);
        } else {
            stepFramesProgress();
        }
    }

    @Override
	public boolean canBeJumpedUpon() {
        return false;
    }

    @Override
	public void paint(Graphics g) {
        if (isFire) {
            drawImage(g, laser);
        } else {
            drawImage(g, frames[currentFrame]);
        }
    }

    @Override
	public void jumpedUpon(Sprite sprite) { }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if (isFire && !(sprite instanceof TotallyPassable) && !(sprite instanceof BombSprite))
            explode();
    }

    @Override
	public Image getReferenceImage() {
        if (dir == 0)
            return ImageManager.getImage("sprites/laser_left_ref.gif");
        else
            return ImageManager.getImage("sprites/laser_right_ref.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        dir = datainputstream.readInt();
    }

    @Override
	public Sprite getCopy() {
        LaserSprite lasersprite = new LaserSprite();
        lasersprite.freezable = super.freezable;
        lasersprite.dir = dir;
        return lasersprite;
    }

    @Override
	public void initPos(float x, float y) {
        fireTimer = (x / 100F) % 1.4F + (y / 100F) % 1.4F;
        super.initPos(x, y);
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeInt(dir);
    }

    @Override
	public void updateSpriteLogic(double d) {
        fireTimer += d;
        if (isFire) {
            super.accy = -super.dy + targetDy;
            super.accx = -super.dx + targetDx;
            if (super.collLeft || super.collRight || super.collUp || super.collDown)
                explode();
            return;
        }
        currentFrame = (int)(fireTimer * 5F) % 2;
        if (dir != 0)
            currentFrame += 2;
        PlayerSprite playersprite = PlayerSprite.getReference();
        float f = (((Sprite) (playersprite)).x + ((Sprite) (playersprite)).width / 2) - (super.x + super.width / 2);
        float f2 = (((Sprite) (playersprite)).y + ((Sprite) (playersprite)).height / 2) - (super.y + super.height / 2);
        float f4 = f * f + f2 * f2;
        if (fireTimer > 1.5F) {
            fireTimer = 0.0F;
            if(f4 < 250000F) {
                f4 = (float)Math.sqrt(f4);
                float f1;
                if(dir == 0)
                    f1 = -1F;
                else
                    f1 = 1.0F;
                float f3 = 0.0F;
                addSprite(new LaserSprite(super.x + 12F, super.y + 12F, f1, f3));
            }
        }
    }

    private void explode() {
        if (!isKilled()) {
            addSprite(new ExplosionSprite(super.x - 10F, super.y - 10F));
            kill();
            SoundManager.ref(Game.getReference()).play(super.x, super.y, "exp");
        }
    }

    @Override
	public float playerPainInflict(float f, float f1) {
        return !isFire ? 0.0F : 1.0F;
    }
}
