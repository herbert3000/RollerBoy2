package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class AIArrowSprite extends Sprite implements TotallyPassable {

    int dir;
    
    public AIArrowSprite() {
        super.width = 32;
        super.height = 32;
        super.isSolid = false;
        super.canPassSolid = false;
        if (Sprite.loader != null)
            Sprite.loader.progress();
    }

    @Override
	public String toString() {
        return "dir: " + dir + " - " + super.toString();
    }

    @Override
	public void paint(Graphics g1) { }

    @Override
	public boolean isMovable() {
        return false;
    }

    @Override
	public Image getReferenceImage() {
        if (dir == 0)
            return ImageManager.getImage("sprites/arrowUp.gif");
        if (dir == 1)
            return ImageManager.getImage("sprites/arrowDown.gif");
        if (dir == 2)
            return ImageManager.getImage("sprites/arrowLeft.gif");
        if (dir == 3)
            return ImageManager.getImage("sprites/arrowRight.gif");
        else
            return null;
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        dir = datainputstream.readInt();
    }

    @Override
	public Sprite getCopy() {
        AIArrowSprite aiArrowSprite = new AIArrowSprite();
        aiArrowSprite.freezable = super.freezable;
        aiArrowSprite.dir = dir;
        return aiArrowSprite;
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeInt(dir);
    }

    @Override
	public void updateSpriteLogic(double d) { }
}
