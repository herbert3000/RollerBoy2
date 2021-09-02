package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class SensorSprite extends Sprite implements TotallyPassable {

    int flag;
    private boolean flagState;

    public SensorSprite() {
        super.width = 32;
        super.height = 32;
        super.isSolid = false;
        super.canPassSolid = false;
        if (Sprite.loader != null)
            Sprite.loader.progress();
    }

    @Override
	public void paint(Graphics g) { }

    @Override
	public boolean isMovable() {
        return false;
    }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        flagState = true;
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/sensor.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        flag = datainputstream.readInt();
    }

    @Override
	public Sprite getCopy() {
        SensorSprite sensorsprite = new SensorSprite();
        sensorsprite.freezable = super.freezable;
        sensorsprite.flag = flag;
        return sensorsprite;
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeInt(flag);
    }

    @Override
	public void updateSpriteLogic(double d) {
        if (flagState)
            Sprite.world.setFlag(flag, true);
        flagState = false;
    }
}
