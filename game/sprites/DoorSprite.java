package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class DoorSprite extends Sprite {

	private enum State { STILL_AT_FROM, MOVING_TO, MOVING_FROM, STILL_AT_TO };
    private float orgX;
    private float orgY;
    int dir;
    float amp;
    float speed;
    float toWait;
    float fromWait;
    boolean loop;
    private State state;
    private float timer;

    public DoorSprite() {
        state = State.STILL_AT_FROM;
        timer = 0.0F;
        super.width = 32;
        super.height = 32;
        super.isSolid = true;
        super.canPassSolid = false;
        getReferenceImage();
        if (Sprite.loader != null)
            Sprite.loader.progress();
    }

    private boolean isDone(float f) {
        if (f == -300F)
            return false;
        if (f < 0.0F)
            return Sprite.world.getFlag(-(int)f);
        return timer > f;
    }

    @Override
	public void paint(Graphics g) {
        drawImage(g, getReferenceImage());
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/door.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        dir = datainputstream.readInt();
        toWait = datainputstream.readFloat();
        fromWait = datainputstream.readFloat();
        amp = datainputstream.readFloat();
        speed = datainputstream.readFloat();
        loop = datainputstream.readBoolean();
    }

    @Override
	public Sprite getCopy() {
        DoorSprite doorsprite = new DoorSprite();
        doorsprite.dir = dir;
        doorsprite.toWait = toWait;
        doorsprite.fromWait = fromWait;
        doorsprite.amp = amp;
        doorsprite.speed = speed;
        doorsprite.loop = loop;
        doorsprite.freezable = super.freezable;
        return doorsprite;
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
        dataoutputstream.writeInt(dir);
        dataoutputstream.writeFloat(toWait);
        dataoutputstream.writeFloat(fromWait);
        dataoutputstream.writeFloat(amp);
        dataoutputstream.writeFloat(speed);
        dataoutputstream.writeBoolean(loop);
    }

    @Override
	public void updateSpriteLogic(double d) {
        float f;
        float f1;
        float f2;
        float f3;
        if (dir == 0) {
            f = super.y;
            f1 = super.dy;
            f2 = super.accy;
            f3 = orgY;
        } else {
            f = super.x;
            f1 = super.dx;
            f2 = super.accx;
            f3 = orgX;
        }
        switch(state) {
        case STILL_AT_FROM:
            f2 = -f1 + (float)((f3 - f) / d);
            if (isDone(toWait)) {
                if (loop)
                    toWait = 0.0F;
                if (!loop)
                    SoundManager.ref(Game.getReference()).play(super.x, super.y, "start");
                state = State.MOVING_TO;
                timer = 0.0F;
            }
            break;

        case MOVING_TO:
            f2 = (amp / Math.abs(amp)) * speed - f1;
            if (amp < 0.0F) {
                if ((int)f <= (int)(f3 + amp * 32F)) {
                    f2 = 0.0F;
                    if (!loop)
                        SoundManager.ref(Game.getReference()).play(super.x, super.y, "stop");
                    state = State.STILL_AT_TO;
                    timer = 0.0F;
                }
            } else if ((int)f >= (int)(f3 + amp * 32F)) {
                f2 = 0.0F;
                if (!loop)
                    SoundManager.ref(Game.getReference()).play(super.x, super.y, "stop");
                state = State.STILL_AT_TO;
                timer = 0.0F;
            }
            break;

        case STILL_AT_TO:
            f2 = -f1 + (float)(((f3 + amp * 32F) - f) / d);
            if (isDone(fromWait)) {
                if (!loop)
                    SoundManager.ref(Game.getReference()).play(super.x, super.y, "start");
                state = State.MOVING_FROM;
                timer = 0.0F;
                if (loop) {
                    DoorSprite doorsprite = (DoorSprite)getCopy();
                    doorsprite.initPos(orgX, orgY);
                    kill();
                    addSprite(doorsprite);
                    addSprite(new GlensSprite(super.x, super.y, true));
                }
            }
            break;

        case MOVING_FROM:
            f2 = -(amp / Math.abs(amp)) * speed - f1;
            if (amp < 0.0F) {
                if ((int)f >= (int)f3) {
                    f2 = 0.0F;
                    state = State.STILL_AT_FROM;
                    timer = 0.0F;
                }
            } else if ((int)f <= (int)f3) {
                if (!loop)
                    SoundManager.ref(Game.getReference()).play(super.x, super.y, "stop");
                f2 = 0.0F;
                state = State.STILL_AT_FROM;
                timer = 0.0F;
            }
            break;
        }
        if (dir == 0) {
            super.y = f;
            super.dy = f1;
            super.accy = f2;
            orgY = f3;
        } else {
            super.x = f;
            super.dx = f1;
            super.accx = f2;
            orgX = f3;
        }
        timer += d;
    }
}
