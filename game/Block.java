package game;

import java.io.*;
import java.util.Vector;

public final class Block {

    private int maxSprites;
    public Tile tile;
    private Sprite sprites[];
    private int nSprites;

    public Block(Sprite sprite) {
        maxSprites = 0;
        addSprite(sprite);
    }

    public Block(Tile tile) {
        maxSprites = 0;
        this.tile = tile;
    }

    public Block(DataInputStream datainputstream, TileSet tileset, Vector<Sprite> sprites) throws IOException {
        maxSprites = 0;
        load(datainputstream, tileset, sprites);
    }

    public Block(Block block1) {
        maxSprites = 0;
        this.tile = block1.tile;
        for (int i = 0; i < block1.getNoSprites(); i++)
            addSprite(block1.getSprite(i));
    }

    public Object getVertLineCollision(float f, float f1, int i) {
        if (tile.solid)
            return tile;
        Sprite sprite1 = null;
        for (int j = 0; j < nSprites; j++) {
            Sprite sprite2 = sprites[j];
            if (f1 <= (sprite2.y + sprite2.height) - 1.0F && (f1 + i) - 1.0F >= sprite2.y && f >= sprite2.x && f <= (sprite2.x + sprite2.width) - 1.0F && sprite2.isCollidable)
                if (sprite2.isSolid)
                    sprite1 = sprite2;
                else if (sprite1 == null)
                    sprite1 = sprite2;
        }
        return sprite1;
    }

    public Sprite getSprite(int index) {
        if (index >= nSprites)
            throw new RuntimeException("index >= nSprites - " + index + " >= " + nSprites);
        else
            return sprites[index];
    }

    public boolean isSolid() {
        if (tile.solid)
            return true;
        
        for (int i = 0; i < nSprites; i++) {
            if (sprites[i].isSolid)
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String s = super.toString();
        if (getNoSprites() > 0) {
            s += "[";
            for (int i = 0; i < getNoSprites(); i++) {
                s += getSprite(i);
                if (i < getNoSprites() - 1)
                    s += ", ";
            }
            s += "]";
        }
        return s;
    }

    public void removeSprite(Sprite sprite) {
        for (int i = 0; i < nSprites; i++) {
            if (sprites[i] == sprite) {
                for (int j = i; j < nSprites - 1; j++){
                    sprites[j] = sprites[j + 1];
                }
                sprites[nSprites - 1] = null;
                nSprites--;
            }
    	}
    }

    public void removeAllSprites() {
        for (int i = 0; i < nSprites; i++) {
            sprites[i] = null;
        }
        nSprites = 0;
    }

    public void addSprite(Sprite sprite) {
        nSprites++;
        if (nSprites > maxSprites) {
            if (maxSprites == 0)
                maxSprites = 1;
            else
                maxSprites *= 2;
            Sprite asprite[] = sprites;
            sprites = new Sprite[maxSprites];
            for (int i = 0; i < nSprites - 1; i++) {
                sprites[i] = asprite[i];
            }
        }
        sprites[nSprites - 1] = sprite;
    }

    public void load(DataInputStream dataInputStream, TileSet tileset, Vector<Sprite> sprites) throws IOException {
        tile = tileset.getTile(dataInputStream.readShort());
        int i = dataInputStream.readInt();
        for (int j = 0; j < i; j++) {
            addSprite(sprites.elementAt(dataInputStream.readInt()));
        }
    }

    public boolean equals(Block block) {
        if (block.tile != tile)
            return false;
        if (nSprites != block.getNoSprites())
            return false;
        for (int i = 0; i < nSprites; i++)
            if (sprites[i] != block.getSprite(i))
                return false;

        return true;
    }

    public void save(DataOutputStream dataOutputStream, TileSet tileset, Vector<Sprite> sprites) throws IOException {
        dataOutputStream.writeShort(tileset.indexOf(tile));
        dataOutputStream.writeInt(getNoSprites());
        for (int i = 0; i < getNoSprites(); i++) {
            dataOutputStream.writeInt(sprites.indexOf(getSprite(i)));
        }
    }

    public Object getHorizLineCollision(float f, float f1, int i) {
        if(tile.solid)
            return tile;
        Sprite sprite1 = null;
        for (int j = 0; j < nSprites; j++) {
            Sprite sprite2 = sprites[j];
            if (f <= (sprite2.x + sprite2.width) - 1.0F && (f + i) - 1.0F >= sprite2.x && f1 >= sprite2.y && f1 <= (sprite2.y + sprite2.height) - 1.0F && sprite2.isCollidable) {
                if (sprite2.isSolid)
                    sprite1 = sprite2;
                else if (sprite1 == null)
                    sprite1 = sprite2;
            }
        }
        return sprite1;
    }

    public int getNoSprites() {
        return nSprites;
    }
}
