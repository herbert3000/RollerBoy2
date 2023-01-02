package game.sprites;

import java.awt.Image;
import java.awt.Graphics;
import java.applet.Applet;
import litecom.gfxe.ImageSplitter;
import game.Game;
import game.LoadLevelAnimator;
import game.ImageManager;
import game.TotallyPassable;
import game.Sprite;

public class CutSceneUFOSprite extends Sprite implements TotallyPassable
{
    private boolean showBubble;
    private static final int[] pingpong;
    float timer;

    public CutSceneUFOSprite() {
        this.showBubble = false;
        super.width = 32;
        super.height = 32;
        super.isSolid = false;
        super.canPassSolid = false;
        ImageManager.getImage("iverescuedu.gif");
        ImageManager.getImage("myhero.gif");
        if (Sprite.loader != null) {
            Sprite.loader.progress();
        }
        if (LoadLevelAnimator.intro == null) {
            LoadLevelAnimator.intro = new ImageSplitter((Applet)Game.getReference(), "gfx/intro.gif", 32, 32, null).getImages();
        }
    }

    public void paint(final Graphics graphics) {
        this.drawImage(graphics, LoadLevelAnimator.intro[2]);
        if (this.showBubble && this.timer < 3.0f) {
            final float n = -100.0f + (PlayerSprite.getReference().x - super.x);
            final float n2 = -50.0f + (PlayerSprite.getReference().y - super.y);
            super.x += n;
            super.y += n2;
            this.drawImage(graphics, ImageManager.getImage("iverescuedu.gif"));
            super.x -= n;
            super.y -= n2;
        }
        if (this.showBubble && this.timer > 3.0f) {
            final float n3 = -100.0f;
            final float n4 = -50.0f;
            super.x += n3;
            super.y += n4;
            this.drawImage(graphics, ImageManager.getImage("myhero.gif"));
            super.x -= n3;
            super.y -= n4;
        }
        if (this.showBubble && this.timer > 5.0f) {
            final float n5 = -4.0f + (PlayerSprite.getReference().x - super.x) / 2.0f;
            final float n6 = -4.0f + (PlayerSprite.getReference().y - super.y) / 2.0f;
            super.x += n5;
            super.y += n6;
            this.drawImage(graphics, LoadLevelAnimator.hearts[CutSceneUFOSprite.pingpong[(int)(this.timer * 10.0f) % CutSceneUFOSprite.pingpong.length]]);
            super.x -= n5;
            super.y -= n6;
        }
    }

    public boolean isMovable() {
        return false;
    }

    public void spriteCollision(final Sprite sprite, final float n, final float n2) {
        if (sprite instanceof PlayerSprite) {
            this.showBubble = true;
        }
    }

    public Image getReferenceImage() {
        return ImageManager.getImage("sprites/cs2_ref.gif");
    }

    static {
        pingpong = new int[] { 0, 1, 2, 1, 0 };
    }

    public Sprite getCopy() {
        final CutSceneUFOSprite cutSceneUFOSprite = new CutSceneUFOSprite();
        cutSceneUFOSprite.freezable = super.freezable;
        return cutSceneUFOSprite;
    }

    public void updateSpriteLogic(final double n) {
        if (this.timer > 6.0f) {
            PlayerSprite.getReference().completeLevel();
        }
        if (this.showBubble) {
            this.timer += (float)n;
        }
    }
}
