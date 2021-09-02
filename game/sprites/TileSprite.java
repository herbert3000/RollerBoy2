package game.sprites;

import game.*;
import java.awt.*;
import java.io.*;

public class TileSprite extends Sprite {

    int tx;
    int ty;

    public TileSprite() {
        super.width = 32;
        super.height = 32;
        super.isSolid = false;
        super.canPassSolid = false;
        super.isCollidable = false;
    }

    @Override
	public void paint(Graphics g) {
        Point point = Sprite.world.getWorldWindow().getPos();
        int i = (int)(super.x - point.x);
        int j = (int)(super.y - point.y);
        Tile a1 = Sprite.world.getTileSet().getTile(tx, ty);
        int k = a1.x;
        int l = a1.y;
        int i1 = 32;
        if (j + 32 >= Game.ylen)
            i1 = Game.ylen - j;
        else
        if (j < 0) {
            i1 = 32 + j;
            l -= j;
            j = 0;
        }
        g.copyArea(k, l, 32, i1, i - k, j - l);
    }

    @Override
	public boolean isMovable() {
        return false;
    }

    @Override
	public Image getReferenceImage() {
        if (Sprite.world == null)
            return ImageManager.getImage("tiles/solidShade.gif");
        else
            return Sprite.world.getTileSet().getTile(tx, ty).image;
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        tx = datainputstream.readInt();
        ty = datainputstream.readInt();
    }

    @Override
	public boolean iCantClipMyslef() {
        return false;
    }

    @Override
	public Sprite getCopy() {
        TileSprite tilesprite = new TileSprite();
        tilesprite.tx = tx;
        tilesprite.ty = ty;
        tilesprite.freezable = super.freezable;
        return tilesprite;
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeInt(tx);
        dataoutputstream.writeInt(ty);
    }

    @Override
	public void updateSpriteLogic(double d) { }
}
