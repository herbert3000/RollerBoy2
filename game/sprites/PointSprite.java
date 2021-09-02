package game.sprites;

import game.*;
import java.awt.*;

public class PointSprite extends Sprite {

    //private static Image frames[];
    //private float currentFrame;
    private int points;
    private static String bonus = "COMBO!";
    private int mul;
    float timer;

    public PointSprite() {
        super.width = 20;
        super.height = 10;
        super.isSolid = false;
        super.canPassSolid = true;
        super.isCollidable = false;
    }

    public PointSprite(float x, float y, int points) {
        this(x, y, points, 1);
    }

    public PointSprite(float x, float y, int points, int mul) {
        this();
        this.points = points * mul;
        this.mul = mul;
        initPos(x, y);
        if (mul > 1)
            SoundManager.ref(Game.getReference()).play("combo");
        PlayerSprite.getReference().addPoints(points);
    }

    @Override
	public void paint(Graphics g) {
        Point point = Sprite.world.getWorldWindow().getPos();
        int i = (int)(super.x - point.x);
        int j = (int)(super.y - point.y);
        if (i + super.width < 0)
            return;
        if (i >= Game.xlen)
            return;
        if (j + super.height < 0)
            return;
        if (j >= Game.ylen)
            return;
        if (mul > 1) {
            g.setColor(Color.black);
            g.drawString(bonus, (i + 1) - 11, (j + 1) - 15);
            g.setColor(Color.white);
            g.drawString(bonus, i - 11, j - 15);
        }
        g.setColor(Color.black);
        g.drawString("" + points, i + 1, j + 1);
        g.setColor(Color.white);
        g.drawString("" + points, i, j);
    }

    @Override
	public Image getReferenceImage() {
        return null;
    }

    @Override
	public Sprite getCopy() {
        PointSprite pointsprite = new PointSprite();
        pointsprite.freezable = super.freezable;
        return pointsprite;
    }

    @Override
	public void updateSpriteLogic(double d) {
        super.accy = -110F - super.dy;
        timer += d;
        if (timer > 0.4F)
            kill();
    }
}
