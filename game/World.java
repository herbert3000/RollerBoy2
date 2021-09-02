package game;

import game.sprites.PlayerSprite;
import java.awt.Graphics;
import java.awt.Point;
//import java.io.PrintStream;
//import java.util.Enumeration;

public class World {
	
    private Level level;
    private TilePanner tilePanner;
    private WorldWindow worldWindow;
    private SpriteManager spriteManager;
    private boolean flag[];
    private boolean flagSwap[];
    float t;
    float h;
    float winx;
    float winy;
    float targetWinx;
    float targetWiny;
    float frameTime;
    int logicFrameCounter;

    public World() {
        flag = new boolean[256];
        flagSwap = new boolean[256];
        t = 0.0F;
        h = 0.1F;
        frameTime = 0.0F;
        logicFrameCounter = 0;
    }

    public World(Level level) {
        flag = new boolean[256];
        flagSwap = new boolean[256];
        t = 0.0F;
        h = 0.1F;
        frameTime = 0.0F;
        logicFrameCounter = 0;
        setLevel(level);
    }

    public void paint(Graphics graphics) {
        tilePanner.paint(graphics);
        spriteManager.paint(graphics);
    }

    public boolean getFlag(int i) {
        return flagSwap[i];
    }

    public void setFlag(int i, boolean flag1) {
        flag[i] = flag1;
    }

    public void update(double delta) {
        if (PlayerSprite.getReference() == null)
            throw new RuntimeException("No player on level!");
        
        spriteManager.update(delta);
        boolean aflag[] = flagSwap;
        flagSwap = flag;
        flag = aflag;
        for (int i = 0; i < flag.length; i++)
            flag[i] = false;
        
        float f = 0.003333333F;
        frameTime += delta;
        targetWinx = ((Sprite) (PlayerSprite.getReference())).x - Game.xlen / 2;
        targetWiny = ((Sprite) (PlayerSprite.getReference())).y - Game.ylen / 2;
        
        while (frameTime >= f) {
            winx += (targetWinx - winx) * 0.02F;
            winy += (targetWiny - winy) * 0.02F;
            frameTime -= f;
            logicFrameCounter++;
        }
        
        if (PlayerSprite.getReference().getHealth() > 0.0F)
            worldWindow.setPoint(new Point((int)winx, (int)winy));
    }

    public TilePanner getTilePanner() {
        return tilePanner;
    }

    public void printSpriteDump() {
        System.out.println("Full sprite dump:");
        System.out.println("  active sprites: " + spriteManager.getNoSprites());
        System.out.println("  ----");
        
        SpriteEnumerator spriteEnumerator = spriteManager.getSpriteEnumerator();
        while (spriteEnumerator.hasMoreElements()) {
        	Sprite sprite = spriteEnumerator.nextElement();
            System.out.println("  " + sprite);
            System.out.println("  width: " + sprite.width);
            System.out.println("  height: " + sprite.height);
            System.out.println("  dx: " + sprite.dx);
            System.out.println("  dy: " + sprite.dy);
        }
        
        System.out.println("Sprite dump done.");
    }

    public WorldWindow getWorldWindow() {
        return worldWindow;
    }

    public TileSet getTileSet() {
        return level.getTileSet();
    }

    public void setLevel(Level level) {
        this.level = level;
        worldWindow = new WorldWindow(level);
        tilePanner = new TilePanner(level, worldWindow);
        spriteManager = new SpriteManager(level, worldWindow);
        spriteManager.init();
        Sprite.setWorld(this);
    }

    public Level getLevel() {
        return level;
    }
}
