package game.sprites;

import game.Sprite;
import java.awt.Graphics;
import java.awt.Image;

public class LinkSprite extends Sprite {

    private float shiftX;
    private float shiftY;
    private Sprite parent;
    private Image image;

    public LinkSprite() { }

    public LinkSprite(Sprite sprite, Image image, float shiftX, float shiftY) {
        this.image = image;
        parent = sprite;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        super.freezable = sprite.isFreezable();
        super.width = image.getWidth(null);
        super.height = image.getHeight(null);
        super.isSolid = false;
        super.canPassSolid = true;
        super.isCollidable = false;
    }

    @Override
	public void paint(Graphics g) {
        drawImage(g, image);
    }

    @Override
	public Image getReferenceImage() {
        return null;
    }

    @Override
	public Sprite getCopy() {
        LinkSprite linksprite = new LinkSprite();
        linksprite.freezable = super.freezable;
        return linksprite;
    }

    @Override
	public void updateSpriteLogic(double d) {
        super.x = parent.x + shiftX;
        super.y = parent.y + shiftY;
    }
}
