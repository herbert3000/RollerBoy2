package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import litecom.gfxe.ImageSplitter;

public class CutSceneDTSprite extends Sprite implements TotallyPassable {

    private boolean showBubble;
    float timer;

    public CutSceneDTSprite() {
        showBubble = false;
        super.width = 32;
        super.height = 32;
        super.isSolid = false;
        super.canPassSolid = false;
        ImageManager.getImage("rbisntinthecity.gif");
        if (Sprite.loader != null)
            Sprite.loader.progress();
        if (LoadLevelAnimator.intro == null)
            LoadLevelAnimator.intro = (new ImageSplitter(Game.getReference(), "gfx/intro.gif", 32, 32, null)).getImages();
    }

    @Override
	public void paint(Graphics g) {
        PlayerSprite.getReference();
        if (PlayerSprite.getPlayerType() == 0)
            drawImage(g, LoadLevelAnimator.intro[3]);
        else
            drawImage(g, LoadLevelAnimator.intro[4]);
        if (showBubble) {
            int j = -100;
            int l = -50;
            super.x += j;
            super.y += l;
            drawImage(g, ImageManager.getImage("rbisntinthecity.gif"));
            super.x -= j;
            super.y -= l;
        }
    }

    @Override
	public boolean isMovable() {
        return false;
    }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if (sprite instanceof PlayerSprite)
            showBubble = true;
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/cs1_ref.gif");
    }

    @Override
	public Sprite getCopy() {
        CutSceneDTSprite cutscenedtsprite = new CutSceneDTSprite();
        cutscenedtsprite.freezable = super.freezable;
        return cutscenedtsprite;
    }

    @Override
	public void updateSpriteLogic(double d) {
        if (timer > 5F)
            PlayerSprite.getReference().completeLevel();
        if (showBubble)
            timer += d;
    }
}
