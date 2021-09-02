package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;

public class CheckpointSprite extends Sprite implements TotallyPassable {

    public CheckpointSprite() {
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
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/checkpoint.gif");
    }

    @Override
	public Sprite getCopy() {
        CheckpointSprite checkpointsprite = new CheckpointSprite();
        return checkpointsprite;
    }

    @Override
	public void updateSpriteLogic(double d) { }
}
