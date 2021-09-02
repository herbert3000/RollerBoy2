package game;

//import java.applet.Applet;
import java.awt.*;
//import java.io.PrintStream;

import game.sprites.PlayerSprite;
import litecom.Trace;
import litecom.gfxe.*;

public class LoadLevelAnimator extends Animator implements LoaderTarget2, Runnable {

    private String name;
    private World world;
    private Level level;
    private static Image gameCanvas;
    private static String gameCanvasTileSetName;
    private PlayerSprite player;
    private Loader loader;
    private int levelNumber;
    private int expectedSteps;
    private Image bg;
    private boolean userClicked;
    private boolean ready;
    private Image amobaNose[];
    private String message;
    public static Image digits[];
    public static Image hearts[];
    public static Image intro[];
    float timer;
    float readyTimer;
    float scrollSpeed;
    float scroll;
    private int pingpong[] = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 
        3, 3, 2, 1
    };
    private float noseTimer;
    private float pleaseWaitBubbleTimer;

    public LoadLevelAnimator(String s, PlayerSprite player) {
        this(s);
        this.player = player;
    }

    public LoadLevelAnimator(String s) {
        timer = 0.0F;
        readyTimer = 0.0F;
        scrollSpeed = 0.0F;
        scroll = 0.0F;
        noseTimer = 0.0F;
        init(s);
    }

    public LoadLevelAnimator() {
        timer = 0.0F;
        readyTimer = 0.0F;
        scrollSpeed = 0.0F;
        scroll = 0.0F;
        noseTimer = 0.0F;
    }

    public void doRepaint() {
        super.game.repaint();
    }

    private void drawCenteredString(String s, int i, Graphics g) {
        g.setColor(Color.black);
        g.drawString(s, (super.xlen / 2 - g.getFontMetrics().stringWidth(s) / 2) + 2, i + 2);
        g.setColor(Color.white);
        g.drawString(s, super.xlen / 2 - g.getFontMetrics().stringWidth(s) / 2, i);
    }

    private int getLevelNumber(String s) {
        return Text.getLevelNumber(s);
    }

    public void focus() {
        if (levelNumber == 1)
            bg = super.game.getImage(Game.codeBase, "gfx/loading_downtown" + Text.langSuffix + ".gif");
        else
        if (levelNumber == 7)
            bg = super.game.getImage(Game.codeBase, "gfx/loading_homeworld" + Text.langSuffix + ".gif");
        else
        if (levelNumber == 17)
            bg = super.game.getImage(Game.codeBase, "gfx/loading_ufo" + Text.langSuffix + ".gif");
        else
            bg = null;
        loader = new Loader(this);
        loader.setSteps(expectedSteps);
        loader.setMessage("base classes");
        loader.start();
        Thread thread = new Thread(this);
        thread.start();
    }

    private synchronized void setMessage(String s) {
        message = s;
    }

    private synchronized String getMessage() {
        if (message == null && loader != null)
            return loader.getMessage();
        else
            return message;
    }

    public void realInit() {
        try {
            System.gc();
            setMessage("main game code");
            Trace.out(this, " *** loading level");
            if (loader != null)
                loader.progress(100);
            if (loader != null)
                loader.progress(50);
            setMessage(null);
            level = new Level(name, player, loader);
            setMessage("basic sprites");
            Trace.out(this, "*** level loaded");
            if (loader != null)
                loader.progress();
            initClass("game.sprites.GlensSprite");
            if (loader != null)
                loader.progress();
            initClass("game.sprites.BombSprite");
            setMessage("hud graphics");
            if (loader != null)
                loader.progress(10);
            ImageManager.getImage("lives_ont2.gif");
            if (loader != null)
                loader.progress(10);
            ImageManager.getImage("lives_ont.gif");
            if (loader != null)
                loader.progress(10);
            ImageManager.getImage("levelCompleted.gif");
            if (loader != null)
                loader.progress(10);
            ImageManager.getImage("lives.gif");
            if (loader != null)
                loader.progress();
            ImageManager.getImage("lives2.gif");
            if (loader != null)
                loader.progress(10);
            ImageManager.getImage("coins.gif");
            if (loader != null)
                loader.progress();
            setMessage("sprite management");
            initClass("game.SpriteEnumerator");
            if (loader != null)
                loader.progress();
            setMessage("debug code");
            initClass("game.DebugLayer");
            if (loader != null)
                loader.progress(10);
            setMessage("fonts");
            if (digits == null)
                digits = (new ImageSplitter(Game.getReference(), "gfx/digits.gif", 8, 12, null)).getImages();
            if (loader != null)
                loader.progress(10);
            if (hearts == null)
                hearts = (new ImageSplitter(Game.getReference(), "gfx/heart.gif", 20, 18, null)).getImages();
            if (loader != null)
                loader.progress(10);
            setMessage("aux sprites");
            initClass("game.sprites.ExplosionSprite");
            if (loader != null)
                loader.progress(10);
            initClass("game.sprites.PointSprite");
            if (loader != null)
                loader.progress(10);
            initClass("game.sprites.CheckpointSprite");
            if (loader != null)
                loader.progress(10);
            setMessage("sound system");
            initClass("game.SoundManager");
            if (loader != null)
                loader.progress(10);
            setMessage("sound list");
            initClass("game.SoundList");
            if (loader != null)
                loader.progress(10);
            setMessage("link sprite");
            initClass("game.sprites.LinkSprite");
            if (loader != null)
                loader.progress(10);
            setMessage("button");
            initClass("game.sprites.ButtonSprite");
            if (loader != null)
                loader.progress(10);
            setMessage("end of level marker");
            initClass("game.sprites.EndOfLevelSprite");
            if (loader != null)
                loader.progress(10);
            setMessage("world window");
            initClass("game.WorldWindow");
            if (loader != null)
                loader.progress(10);
            setMessage("scroller");
            initClass("game.TilePanner");
            if (loader != null)
                loader.progress(10);
            setMessage("sprite duplicate checker");
            initClass("game.SpriteDuplicateChecker");
            if (loader != null)
                loader.progress(10);
            setMessage("game over animator");
            initClass("game.GameOverAnimator");
            if (loader != null)
                loader.progress(10);
            setMessage("game canvas");
            Trace.out(this, "*** sprites inited");
            if (gameCanvas == null || !gameCanvasTileSetName.equals(((Level)level).getTileSet().getName())) {
                if (gameCanvas != null) {
                    Trace.out(this, "FLUSHING: old game canvas");
                    gameCanvas.flush();
                    gameCanvas = null;
                }
                Trace.out(this, "CREATING: new game canvas");
                gameCanvasTileSetName = ((Level)level).getTileSet().getName();
                gameCanvas = ((Level)level).createGameCanvas(super.xlen, super.ylen);
            }
            Trace.out(this, "*** game canvas ready");
            if (loader != null)
                loader.progress(10);
            setMessage("world");
            world = new World();
            Trace.out(this, "*** world ready");
            if (loader != null)
                loader.progress(10);
            Trace.out(this, "*** Progress from gen code: " + LevelStepCounts.steps[getLevelNumber(name) - 1] + ", real: " + loader.getProgress());
            Trace.out(this, "done!");
            ready = true;
            System.gc();
            return;
        } catch(Exception exception) {
            System.out.println("Exception caught when trying to load level:");
            exception.printStackTrace();
            RuntimeException runtimeexception = new RuntimeException("Could not load level: " + exception);
            super.game.setCurrentError(runtimeexception);
            throw runtimeexception;
        }
    }

    public void update(Graphics g, double d) {
        timer += d;
        g.setColor(Game.bgColor);
        g.fillRect(0, 0, super.xlen, super.ylen);
        if (bg != null) {
        	int x = super.xlen / 2 - 225;
        	int y = super.ylen / 2 - 95;
        	
            if (readyTimer == 0.0F) {
                g.drawImage(bg, x, y, super.game);
            } else {
                g.drawImage(bg, x, y + (int)scroll, super.game);
                if (userClicked) {
                    scrollSpeed += d * 3D;
                    scroll += scrollSpeed;
                }
            }
        } else {
            Image image = ImageManager.getImage("levelCompleted.gif");
            int i = super.xlen / 2 - image.getWidth(null) / 2;
            int j = super.ylen / 2 - image.getHeight(null) / 2;
            if (readyTimer != 0.0F && userClicked) {
                j += (int)scroll;
                scrollSpeed += d * 3D;
                scroll += scrollSpeed;
            }
            g.drawImage(image, i, j, null);
        }
        g.setFont(new Font("Arial, Helvetica, Helv", 1, 20));
        String s = Text.getVisualizedLevelNumber(levelNumber);
        if (!userClicked && readyTimer != 0.0F && !userClicked)
            if (levelNumber == 1 || levelNumber == 5 || levelNumber == 7 || levelNumber == 11 || levelNumber == 17 || levelNumber == 21) {
                if (levelNumber != 1)
                    drawCenteredString(Text.passwordFor + " " + s + ": " + LevelStepCounts.passwords[levelNumber - 1], super.ylen - 60, g);
                drawCenteredString(Text.clickToStart, super.ylen - 30, g);
            } else {
                userClicked = true;
            }
        if (readyTimer == 0.0F) {
            drawCenteredString(Text.loading + " " + s, super.ylen - 60, g);
            g.setFont(new Font("Arial, Helvetica, Helv", 1, 10));
            String s1 = getMessage();
            if (s1 != null)
                drawCenteredString(Text.loading + " " + s1 + "...", super.ylen - 40, g);
        }
        if (levelNumber == 1 && timer > 20F)
            userClicked = true;
        if (loader.getProgress() > 0 && readyTimer == 0.0F) {
            int x = 10;
            int y = super.ylen - 24;
            int w = super.xlen - x * 2;
            int h = 14;
            g.setColor(Color.black);
            g.drawRect(x, y, w, h);
            g.drawImage(ImageManager.getImage("loading_bar_start.gif"), x + 2, y + 2, null);
            int l = x + (int)((float)(w - 15 - 1) * ((float)loader.getProgress() / (float)loader.getSteps()));
            int i1 = x + 2 + ImageManager.getImage("loading_bar_start.gif").getWidth(null);
            g.drawImage(ImageManager.getImage("loading_bar_middle.gif"), i1, y + 2, l - i1, ImageManager.getImage("loading_bar_middle.gif").getHeight(null), null);
            if (amobaNose != null) {
                Image image1 = amobaNose[pingpong[(int)(noseTimer * 7F) % pingpong.length]];
                noseTimer += d;
                g.drawImage(image1, l, y + 2, null);
                if (noseTimer > 30F) {
                    Image image2 = super.game.getImage(Game.codeBase, "gfx/pleaseWait" + Text.langSuffix + ".gif");
                    if (image2.getWidth(null) > 1) {
                        g.drawImage(image2, l - image2.getWidth(null), y - image2.getHeight(null), null);
                        pleaseWaitBubbleTimer += d;
                        if (pleaseWaitBubbleTimer > 4F) {
                            pleaseWaitBubbleTimer = 0.0F;
                            noseTimer = 0.0F;
                        }
                    }
                }
            }
        }
        if (!ready)
            try {
                Thread.sleep(250L);
                return;
            } catch(Exception _ex) {
                return;
            }
        if (readyTimer == 0.0F)
            super.game.getFrameTimer().reset();
        else
        if (scroll > (float)super.ylen)
            super.game.setAnimator(new GameAnimator(world, level, levelNumber), gameCanvas);
        readyTimer += d;
    }

    public void setParam(String s) {
        init(s);
    }

    public void mouseDown(Event event, int i, int j) {
        if (readyTimer != 0.0F)
            userClicked = true;
    }

    public void run() {
        if (amobaNose == null) {
            Trace.out(this, "loading amoba nose");
            amobaNose = (new ImageSplitter(Game.getReference(), "gfx/loading_bar_end.gif", 15, 12, null)).getImages();
        }
    }

    public void init(String s) {
        Trace.out(this, "<init>");
        name = s;
        levelNumber = getLevelNumber(s);
        expectedSteps = LevelStepCounts.steps[levelNumber - 1] + 428;
    }

    private void initClass(String s) {
        try {
            Class.forName(s).newInstance();
        } catch(Exception _ex) {}
    }

    public void keyDown(Event event, int i) {
        if (readyTimer != 0.0F)
            userClicked = true;
    }
}
