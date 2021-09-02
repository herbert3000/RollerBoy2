package game;

import game.sprites.CoinSprite;
import game.sprites.FallSprite;
import game.sprites.KullagerSprite;
import game.sprites.PlayerSprite;

import java.awt.Image;
import java.awt.Point;
import java.io.*;
import java.net.URL;
//import java.net.URLConnection;
import java.util.Vector;
import litecom.gfxe.Loader;
import litecom.Trace;

public final class Level {

    private static final int MAGIC = 0xFEEB;
    private static final int VERSION = 2;
    private static final int PASSWORD_LEN = 6;
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String levelPath = "levels/";
    private Block block[][];
    private TileSet tileSet;
    private int width;
    private int height;
    private String levelPassword;
    private int nCoins;
    private Block lastCollidedBlock;

    public Level() {
        levelPassword = generatePassword();
    }

    public Level(int width, int height, TileSet tileSet) {
        this();
        this.tileSet = tileSet;
        this.width = width;
        this.height = height;
        block = new Block[width][height];
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++)
                block[w][h] = new Block(tileSet.getTile(0, 0));
        }
        Sprite.setLevel(this);
    }

    public Level(Level level) {
        this();
        this.tileSet = level.getTileSet();
        width = level.getWidth();
        height = level.getHeight();
        block = new Block[width][height];
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++)
                block[w][h] = new Block(level.getBlock(w, h));
        }
        Sprite.setLevel(this);
    }

    public Level(String levelName) throws Exception {
        this();
        load(levelName, null, null);
    }

    public Level(String levelName, Object obj, Loader loader) throws Exception {
        this();
        Sprite.setLoader(loader);
        load(levelName, obj, loader);
    }

    public void crop(int i, int j, int width, int height) {
        Block ablock[][] = new Block[width][height];
        for (int i1 = i; i1 < i + width; i1++) {
            for (int j1 = j; j1 < j + height; j1++)
                ablock[i1 - i][j1 - j] = block[i1][j1];

        }
        block = ablock;
        this.width = width;
        this.height = height;
        updateSpritePositions();
    }

    public Image createGameCanvas(int width, int height) {
        return getTileSet().createGameCanvas(width, height);
    }

    public void updateSpritePositions() {
        SpriteDuplicateChecker spriteDuplicateChecker = new SpriteDuplicateChecker();
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[i].length; j++) {
                Block block1 = block[i][j];
                for (int k = 0; k < block1.getNoSprites(); k++) {
                    Sprite sprite = block1.getSprite(k);
                    if (!spriteDuplicateChecker.contains(sprite)) {
                        spriteDuplicateChecker.addSprite(sprite);
                        sprite.initPos(i * 32, j * 32);
                    }
                }
            }
        }
    }

    public Object getVertLineCollision(float f, float f1, int i) {
        int j = (int)f;
        int k = (int)f1;
        int l = j / 32;
        Object obj = null;
        for (int i1 = k / 32; i1 <= ((k + i) - 1) / 32; i1++) {
            if (l < 0 || l >= width || i1 < 0 || i1 >= height) {
                lastCollidedBlock = null;
                return new Tile();
            }
            Block block1 = getBlock(l, i1);
            Object obj1 = block1.getVertLineCollision(f, f1, i);
            if (obj1 != null)
                if (obj == null) {
                    lastCollidedBlock = block1;
                    obj = obj1;
                } else if (obj1 instanceof Tile) {
                    obj = obj1;
                    lastCollidedBlock = block1;
                } else if (((Sprite)obj1).isSolid) {
                    lastCollidedBlock = block1;
                    obj = obj1;
                }
        }
        return obj;
    }

    public int getMaxSpriteHeight() {
        int i = 0;
        for (int j = 0; j < block.length; j++) {
            for (int k = 0; k < block[j].length; k++) {
                Block block1 = block[j][k];
                for (int l = 0; l < block1.getNoSprites(); l++) {
                    Sprite sprite = block1.getSprite(l);
                    if (sprite.height > i)
                        i = sprite.height;
                }
            }
        }
        return i;
    }

    public Block getBlock(Point point) {
        return getBlock(point.x, point.y);
    }

    public Block getBlock(int w, int h) {
        if (w < 0 || h < 0 || w >= width || h >= height)
            return null;
        
        return block[w][h];
    }

    public void setBlock(int w, int h, Block block1) {
        if (w < 0 || h < 0 || w >= width || h >= height)
            return;
        
        block[w][h] = block1;
    }

    public void expand(int w, int h) {
        width += Math.abs(w);
        height += Math.abs(h);
        Block ablock[][] = new Block[width][height];
        for (int i = 0; i < ablock.length; i++) {
            for (int j = 0; j < ablock[i].length; j++) {
                ablock[i][j] = new Block(tileSet.getTile(0));
            }
        }
        
        if (w > 0)
            w = 0;
        else
            w = -w;
        if (h > 0)
            h = 0;
        else
            h = -h;
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[i].length; j++) {
                ablock[i + w][j + h] = block[i][j];
            }
        }
        
        block = ablock;
        updateSpritePositions();
    }

    private String fixLevelName(String levelName) {
        if (levelName.indexOf('.') != -1 || levelName.indexOf('/') != -1 || levelName.indexOf('\\') != -1)
            throw new RuntimeException("Level name may not contain '.', '/' or '\\'.");
        else
            return levelPath + levelName + ".level";
    }

    public String getPassword() {
        return levelPassword;
    }

    public int getNCoins() {
        return nCoins;
    }

    public void load(String levelName) throws Exception {
        load(levelName, null, null);
    }

    public void load(String levelName, Object obj, Loader loader) throws Exception {
        if (obj == null) {
            Trace.out(this, "Resetting player reference");
            PlayerSprite.clearReference();
        }
        Sprite.setLoader(loader);
        levelName = fixLevelName(levelName);
        if (Game.documentBase != null) {
            Trace.out(this, "loading level using documentBase: " + Game.documentBase + " and filename " + levelName);
            URL url = new URL(Game.documentBase, levelName);
            DataInputStream dataInputStream = null;
            int i = 0;
            do {
                try {
                    dataInputStream = new DataInputStream(url.openConnection().getInputStream());
                } catch(IOException ioexception) {
                    System.out.println("Could not load level: ");
                    ioexception.printStackTrace();
                }
                if (dataInputStream != null)
                    break;
                System.out.println("Retrying level load...");
                try {
                    Thread.sleep(1000L);
                } catch(InterruptedException _ex) { }
            } while (++i < 3);
            if (dataInputStream == null) {
                throw new Exception("Could not load level!");
            } else {
                load(dataInputStream, obj, loader);
                return;
            }
        } else {
            load(new DataInputStream(new FileInputStream(levelName)), obj, loader);
            return;
        }
    }

    public void load(DataInputStream dataInputStream, Object playerSprite, Loader loader) throws Exception {
        if (loader != null)
        	loader.setMessage("level header");
        Sprite.setLoader(loader);
        FallSprite.resetFallList();
        Trace.indent(this, "Loading level:");
        if (dataInputStream.readInt() != MAGIC)
            throw new Exception("Not a level file");
        int version = dataInputStream.readInt();
        if (version == VERSION) {
            levelPassword = dataInputStream.readUTF();
        } else if (version == 1) {
            System.err.println("Warning: old version of level");
            levelPassword = dataInputStream.readUTF();
        } else {
            throw new Exception("Incompatible version of level file");
        }
        if (loader != null) loader.setMessage("level name");
        String tilesetName = dataInputStream.readUTF();
        if (loader != null) loader.progress(100);
        if (loader != null) loader.setMessage("tile set");
        tileSet = TilesetManager.getTileset(tilesetName, loader);
        if (loader != null) loader.progress();
        width = dataInputStream.readInt();
        height = dataInputStream.readInt();
        if (loader != null) loader.setMessage("sprites");
        Sprite.setLevel(this);
        Trace.indent(this, "sprites:");
        Vector<Sprite> sprites = new Vector<Sprite>();
        int numSprites = dataInputStream.readInt();
        for (int i = 0; i < numSprites; i++) {
            Sprite sprite = SpriteManager.newInstance(dataInputStream.readUTF());
            if (sprite.isPlayer() && playerSprite != null)
                sprite = (Sprite)playerSprite;
            sprite.load(dataInputStream);
            sprites.addElement(sprite);
        }
        
        Trace.outdent();
        if (loader != null)
            loader.setMessage("blocks");
        block = new Block[width][height];
        if (version == 1) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++)
                    block[w][h] = new Block(dataInputStream, tileSet, sprites);
                
                if (loader != null)
                    loader.progress();
            }
        } else {
            Trace.out(this, "Loading packed blocks");
            int w = 0;
            int h = 0;
            int i = 0;
            Block block1 = null;
            while (h < height) {
                if (i == 0) {
                    i = dataInputStream.readUnsignedShort();
                    block1 = new Block(dataInputStream, tileSet, sprites);
                }
                block[w][h] = new Block(block1);
                i--;
                if (++w >= width) {
                    w = 0;
                    h++;
                    if (loader != null)
                        loader.progress();
                }
            }
        }
        nCoins = 0;
        SpriteDuplicateChecker spriteDuplicateChecker = new SpriteDuplicateChecker();
        for (int w = 0; w < getWidth(); w++) {
            for (int h = 0; h < getHeight(); h++) {
                Block block1 = getBlock(w, h);
                for (int s = 0; s < block1.getNoSprites(); s++) {
                    Sprite sprite1 = block1.getSprite(s);
                    if (!spriteDuplicateChecker.contains(sprite1))
                        if (sprite1 instanceof CoinSprite)
                            nCoins++;
                        else if (sprite1 instanceof KullagerSprite)
                            nCoins += 10;
                    spriteDuplicateChecker.addSprite(sprite1);
                }
            }
        }
        Trace.outdent(this, "Done!");
    }

    public void removeBorders() {
        boolean flag = false;
        int w = 0;
        int h = 0;
        for (h = 0; h < height; h++) {
            for (w = 0; w < width; w++) {
                Block block1 = getBlock(w, h);
                if (block1.getNoSprites() > 0)
                    flag = true;
                if (!block1.tile.bgTile)
                    flag = true;
            }
            if (flag)
                break;
        }
        
        int i = h;
        flag = false;
        for (h = height - 1; h >= 0; h--) {
            for (w = 0; w < width; w++) {
                Block block2 = getBlock(w, h);
                if (block2.getNoSprites() > 0)
                    flag = true;
                if (!block2.tile.bgTile)
                    flag = true;
            }
            if (flag)
                break;
        }
        
        int h2 = height - 1 - h;
        flag = false;
        for (w = 0; w < width; w++) {
            for(int i2 = 0; i2 < height; i2++) {
                Block m3 = getBlock(w, i2);
                if (m3.getNoSprites() > 0)
                    flag = true;
                if (!m3.tile.bgTile)
                    flag = true;
            }
            if (flag)
                break;
        }
        
        int k = w;
        flag = false;
        for (w = width - 1; w >= 0; w--) {
            for (int j2 = 0; j2 < height; j2++) {
                Block m4 = getBlock(w, j2);
                if (m4.getNoSprites() > 0)
                    flag = true;
                if (!m4.tile.bgTile)
                    flag = true;
            }
            if (flag)
                break;
        }
        
        int w2 = width - 1 - w;
        crop(k, i, width - k - w2, height - h2 - i);
    }

    public void save(String levelName) throws Exception {
        levelName = fixLevelName(levelName);
        save(new DataOutputStream(new FileOutputStream(levelName)));
    }

    public String generatePassword() {
        String pw = "";
        int i = 0;
        do
            pw += PASSWORD_CHARS.charAt((int)(Math.random() * (PASSWORD_CHARS.length() - 1)));
        while (++i < PASSWORD_LEN);
        return pw;
    }

    public void save(DataOutputStream dataOutputStream) throws Exception {
        dataOutputStream.writeInt(MAGIC);
        dataOutputStream.writeInt(VERSION);
        dataOutputStream.writeUTF(levelPassword);
        dataOutputStream.writeUTF(tileSet.getName());
        dataOutputStream.writeInt(width);
        dataOutputStream.writeInt(height);
        Vector<Sprite> sprites = new Vector<Sprite>();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                Block block1 = getBlock(w, h);
                for (int i = 0; i < block1.getNoSprites(); i++) {
                    Sprite sprite1 = block1.getSprite(i);
                    if (!sprites.contains(sprite1))
                        sprites.addElement(sprite1);
                }
            }
        }
        
        dataOutputStream.writeInt(sprites.size());
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.elementAt(i);
            dataOutputStream.writeUTF(sprite.getName());
            sprite.save(dataOutputStream);
        }
        
        int w = 0;
        int h = 0;
        int j1 = 0;
        Block block1 = null;
        while (h < height)  {
            if (block1 == null) {
                j1 = 1;
                block1 = block[w][h];
            } else if (block[w][h].equals(block1) || j1 > 60000) {
                j1++;
            } else {
                dataOutputStream.writeShort(j1);
                block1.save(dataOutputStream, tileSet, sprites);
                j1 = 1;
                block1 = block[w][h];
            }
            if (++w >= width) {
                w = 0;
                h++;
            }
        }
        if (j1 > 0) {
            dataOutputStream.writeShort(j1);
            block1.save(dataOutputStream, tileSet, sprites);
            j1 = 0;
        }
    }

    public Object getHorizLineCollision(float f, float f1, int i) {
        int w = (int)f;
        int h = (int)f1;
        int h2 = h / 32;
        Object obj = null;
        for (int w2 = w / 32; w2 <= ((w + i) - 1) / 32; w2++) {
            if (w2 < 0 || w2 >= width || h2 < 0 || h2 >= height)
                return new Tile();
            Block block1 = getBlock(w2, h2);
            Object obj1 = block1.getHorizLineCollision(f, f1, i);
            if (obj1 != null)
                if (obj == null) {
                    lastCollidedBlock = block1;
                    obj = obj1;
                } else if (obj1 instanceof Tile) {
                    lastCollidedBlock = block1;
                    obj = obj1;
                } else if (((Sprite)obj1).isSolid) {
                    lastCollidedBlock = block1;
                    obj = obj1;
                }
        }
        return obj;
    }

    public int getHeight() {
        return height;
    }

    public Block getLastCollidedBlock() {
        return lastCollidedBlock;
    }

    public TileSet getTileSet() {
        return tileSet;
    }

    public int getWidth() {
        return width;
    }
}
