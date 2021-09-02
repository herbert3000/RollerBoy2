package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;

public class EndOfLevelSprite extends Sprite implements TotallyPassable {

    public EndOfLevelSprite() {
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
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/endoflevel.gif");
    }

    @Override
	public Sprite getCopy() {
        EndOfLevelSprite endoflevelsprite = new EndOfLevelSprite();
        endoflevelsprite.freezable = super.freezable;
        return endoflevelsprite;
    }

    @Override
	public void updateSpriteLogic(double d) { }
}
