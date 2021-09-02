package game;

import game.sprites.PlayerSprite;
import java.awt.Graphics;
//import java.util.Enumeration;
import java.util.Vector;

public final class SpriteManager {

    public static final int noLayers = 4;
    private Level level;
    //private WorldWindow worldWindow;
    private Vector<Sprite> layer[];
    private int nSprites;
    private SpriteEnumerator spriteEnum;
    public static int activeSprites = 0;
    public static int allSprites = 0;

    @SuppressWarnings("unchecked")
	public SpriteManager(Level level) {
        layer = new Vector[4];
        this.level = level;
        int i = 0;
        do
            layer[i] = new Vector<Sprite>(100);
        while (++i < 4);
        Sprite.setSpriteManager(this);
    }

    public SpriteManager(Level level, WorldWindow worldWindow) {
        this(level);
        //this.worldWindow = worldWindow;
    }

    public Sprite getSprite(int layerId, int index) {
        return layer[layerId].elementAt(index);
    }

    public void stampSprite(Sprite sprite) {
        int x = (int)(sprite.x / 32F);
        int y = (int)(sprite.y / 32F);
        int w = sprite.width / 32;
        int h = sprite.height / 32;
        if (sprite.width % 32 != 0)
            w++;
        if (sprite.height % 32 != 0)
            h++;
        if (sprite.x % 32F != 0.0F)
            w++;
        if (sprite.y % 32F != 0.0F)
            h++;
        if (w == 0)
            w++;
        if (h == 0)
            h++;
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                Block block = level.getBlock(i, j);
                if (block != null)
                    block.addSprite(sprite);
            }
        }
    }

    public void paint(Graphics g) {
        Graphics g1 = g.create(0, 0, Game.xlen, Game.ylen);
        int i = 0;
        do {
            for (int j = 0; j < layer[i].size(); j++) {
                Sprite sprite = getSprite(i, j);
                if (!sprite.frozen)
                    if (sprite.iCantClipMyslef())
                        sprite.paint(g1);
                    else
                        sprite.paint(g);
            }
        } while(++i < 4);
    }

    public void removeSprite(Sprite sprite) {
        layer[sprite.layer].removeElement(sprite);
        nSprites--;
    }

    public SpriteEnumerator getSpriteEnumerator() {
        return new SpriteEnumerator(this);
    }

    public static Sprite newInstance(String s) {
        Sprite sprite = null;
        try {
            Class<?> class1 = Class.forName("game.sprites." + s + "Sprite");
            sprite = (Sprite)class1.newInstance();
        } catch(Exception exception) {
            throw new RuntimeException("Could not instance " + s + " sprite - " + exception);
        }
        return sprite;
    }

    public static Object newSpritePropertiesInstance(String s) {
        Object obj = null;
        try {
            Class<?> class1 = Class.forName("game.sprites." + s + "Properties");
            obj = class1.newInstance();
        } catch(Exception exception) {
            throw new RuntimeException("Could not instance " + s + " spriteProperties - " + exception);
        }
        return obj;
    }

    public void update(double d) {
        if (spriteEnum == null)
            spriteEnum = getSpriteEnumerator();
        spriteEnum.init();
        PlayerSprite playerSprite = PlayerSprite.getReference();
        activeSprites = 0;
        allSprites = 0;
        while (spriteEnum.hasMoreElements()) {
            Sprite sprite = spriteEnum.nextElement();
            sprite.collLeft = false;
            sprite.collRight = false;
            sprite.collUp = false;
            sprite.collDown = false;
            float f = ((Sprite) (playerSprite)).x - sprite.x;
            float f1 = ((Sprite) (playerSprite)).y - sprite.y;
            allSprites++;
            if (f * f + f1 * f1 < Game.xlen * Game.xlen) { // original: 202500F
                sprite.setFreeze(false);
                activeSprites++;
            } else if (sprite.isFreezable()) {
                sprite.setFreeze(true);
            } else {
                sprite.setFreeze(false);
                activeSprites++;
            }
        }
        spriteEnum.init();
        while (spriteEnum.hasMoreElements()) {
            Sprite sprite = spriteEnum.nextElement();
            if (sprite.isSolid && !sprite.frozen)
                sprite.update(d);
        }
        spriteEnum.init();
        while (spriteEnum.hasMoreElements())  {
            Sprite sprite = spriteEnum.nextElement();
            if (!sprite.isSolid && !sprite.frozen)
                sprite.update(d);
        }
        spriteEnum.init();
        while (spriteEnum.hasMoreElements()) {
            Sprite sprite = spriteEnum.nextElement();
            if (!sprite.frozen) {
                sprite.updateSpriteLogic(d);
                sprite.buttonCheck();
                sprite.oldDx = sprite.dx;
                sprite.oldDy = sprite.dy;
                sprite.dx += sprite.accx;
                sprite.dy += sprite.accy;
            }
        }
        int i = 0;
        do {
            Sprite asprite[] = new Sprite[layer[i].size()];
            layer[i].copyInto(asprite);
            for (int j = 0; j < asprite.length; j++) {
                Sprite sprite = asprite[j];
                if (!sprite.frozen && sprite.isKilled()) {
                    sprite.unstamp();
                    removeSprite(sprite);
                }
            }
        } while(++i < 4);
    }

    public void addSprite(Sprite sprite) {
        layer[sprite.layer].addElement(sprite);
        nSprites++;
    }

    public int getLastLayerSize() {
        return layer[3].size();
    }

    public boolean areStampedSpritesUnder(Sprite sprite, int index) {
        int x = (int)(sprite.x / 32F);
        int y = (int)(sprite.y / 32F);
        int w = sprite.width / 32;
        int h = sprite.height / 32;
        if (sprite.x % 32F != 0.0F)
            w++;
        if (sprite.y % 32F != 0.0F)
            h++;
        if (w == 0)
            w++;
        if (h == 0)
            h++;
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                Block block = level.getBlock(i, j);
                if (block != null && block.getNoSprites() > 0) {
                    for (int i2 = 0; i2 < block.getNoSprites(); i2++)
                        if (block.getSprite(i2).layer == index)
                            return true;
                }
            }
        }
        return false;
    }

    public int getNoSprites() {
        return nSprites;
    }

    public int getNoSprites(int i) {
        return layer[i].size();
    }

    public void init() {
        SpriteDuplicateChecker spriteDuplicateChecker = new SpriteDuplicateChecker();
        for (int i = 0; i < level.getHeight(); i++) {
            for (int j = 0; j < level.getWidth(); j++) {
                Block block = level.getBlock(j, i);
                for (int k = 0; k < block.getNoSprites(); k++) {
                    Sprite sprite = block.getSprite(k);
                    if (!spriteDuplicateChecker.contains(sprite)) {
                        spriteDuplicateChecker.addSprite(sprite);
                        addSprite(sprite);
                    }
                }
            }
        }
    }

    /*
    private void checkSanity() {
        Sprite sprite;
        for (SpriteEnumerator spriteEnumerator = getSpriteEnumerator(); spriteEnumerator.hasMoreElements(); stampSprite(sprite)) {
            sprite = (Sprite)spriteEnumerator.nextElement();
            unstampSprite(sprite);
            sprite.checkColl("sanity check failed! (" + sprite + " - ");
        }
    }
    */

    public void unstampSprite(Sprite sprite) {
        int x = (int)(sprite.x / 32F);
        int y = (int)(sprite.y / 32F);
        int w = sprite.width / 32;
        int h = sprite.height / 32;
        if (sprite.width % 32 != 0)
            w++;
        if (sprite.height % 32 != 0)
            h++;
        if (sprite.x % 32F != 0.0F)
            w++;
        if (sprite.y % 32F != 0.0F)
            h++;
        if (w == 0)
            w++;
        if (h == 0)
            h++;
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                Block block = level.getBlock(i, j);
                if (block != null)
                    block.removeSprite(sprite);
            }
        }
    }
}
