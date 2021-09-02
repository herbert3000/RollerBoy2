package game;

import game.sprites.CoinSprite;
import game.sprites.PlayerSprite;
import java.awt.*;
//import java.util.Vector;

//import litecom.gfxe.Timer;
import litecom.Trace;

public class GameAnimator extends Animator {

    private World world;
    private boolean printDebug;
    private boolean printFrameRate;
    private boolean slomo;
    private boolean printDebugLayer;
    //private boolean doDelay;
    private boolean pause;
    private int slowdown;
    private static Image digits[];
    private static Image hearts[];
    //private float heartAnim;
    private int digitsWidth;
    private boolean firstTime;
    private int levelNumber;
    private Font smallFont;
    private float levelEndTimer;
    private float points;
    private float coinAnimTimer;
    private float heartBlinkTimer;
    private static float liveShakesTimer = 3F;
    private int startStripes;
    private int stripeWidth;
    private float stripes[];
    private float stripesDel[];
    private float endOfLevelTimer;
    //private float startOfLevelTimer;
    private float heartStatus[];
    int cheatIndex;
    int currC;
    private String cheatStr[] = {"leif", "gajb", "hilfe"};
    private boolean nPressed;

    public GameAnimator(World world, Level level, int levelNumber) {
        printFrameRate = false;
        printDebugLayer = false;
        slowdown = 0;
        digitsWidth = 9;
        firstTime = true;
        smallFont = new Font("Arial, Helvetica, Helv", 0, 10);
        levelEndTimer = 0.0F;
        startStripes = 10;
        stripeWidth = -1;
        stripes = new float[startStripes];
        stripesDel = new float[startStripes];
        //startOfLevelTimer = 0.0F;
        heartStatus = new float[150];
        currC = -1;
        this.levelNumber = levelNumber;
        this.world = world;
        world.setLevel(level);
        new DebugLayer(world.getWorldWindow());
        if (digits == null)
            digits = LoadLevelAnimator.digits;
        if (hearts == null)
            hearts = LoadLevelAnimator.hearts;
    }

    public static void shakeLives() {
        liveShakesTimer = 0.0F;
    }

    private void cheat(int i) {}

    @Override
	public void blur() {}

    @Override
	public void focus() {
        if (PlayerSprite.getReference() == null) {
            throw new RuntimeException("No player on level");
        } else {
            PlayerSprite.getReference().setCurrentLevel(levelNumber);
            Sprite.setWorld(world);
            SoundManager.ref(super.game);
            super.game.getFrameTimer().reset();
        }
    }

    private void drawDigits(Graphics g, String s, int x, int y) {
        for (int i = 0; i < s.length(); i++) {
            g.drawImage(digits[s.charAt(i) - 48], x, y, null);
            x += digitsWidth;
        }
    }

    @Override
	public synchronized void update(Graphics g, double d) {
        if (firstTime) {
            g.setFont(new Font("Arial, Helvetica, Helv", 1, 12));
            firstTime = false;
        }
        if (d > 0.12D)
            d = 0.12D;
        if (slomo) {
            try {
                Thread.sleep(100L); // 500L
            } catch(InterruptedException _ex) { }
            d = 0.02D;
        }
        if (slowdown > 0)
            try {
                Thread.sleep(30 * slowdown);
            } catch(InterruptedException _ex) { }
        try {
            Thread.sleep(5L);
        } catch(InterruptedException _ex) { }
        if (!pause)
            world.update(d);
        world.paint(g);
        drawHud((float)d, g);
        if (PlayerSprite.getReference().isDead()) {
            PlayerSprite playersprite = PlayerSprite.getReference();
            playersprite.addLife(-1);
            if (playersprite.getLives() < 0) {
                PlayerSprite.clearReference();
                super.game.setAnimator(new GameOverAnimator(playersprite));
            } else {
                playersprite.restartLevel();
            }
        }
        if (printDebugLayer)
            DebugLayer.getReference().paint(g);
        if (printFrameRate) {
            g.setColor(Color.white);
            g.drawString(Text.fps + Game.getReference().getFPS(), 10, 20);
            g.fillRect(10, 24, (int)(2000D * d), 6);
            g.fillRect(10, 31, (int)(2000D * super.game.getFrameTimer().getRealFrameSpeed()), 6);
        }
        if (printDebug) {
            printDebug(g, d, true);
            printDebug(g, d, false);
        }
    }

    private void drawHud(float d, Graphics g) {
        PlayerSprite playersprite = PlayerSprite.getReference();
        coinAnimTimer += d;
        Image image;
        if (PlayerSprite.getPlayerType() == 1)
            image = playersprite.isImmortal() ? ImageManager.getImage("lives_ont2.gif") : ImageManager.getImage("lives2.gif");
        else
            image = playersprite.isImmortal() ? ImageManager.getImage("lives_ont.gif") : ImageManager.getImage("lives.gif");
        int x = 10;
        int y = Game.ylen - 10 - image.getHeight(null);
        g.drawImage(image, x, y, null);
        x += image.getWidth(null) + 5;
        liveShakesTimer += d;
        int l = 0;
        if (liveShakesTimer < 2.0F) {
            float f1 = (2.0F - liveShakesTimer) * 20F;
            x = (int)(x + (Math.random() * f1 - f1 / 2.0F));
            l = (int)(Math.random() * f1 - f1 / 2.0F);
        }
        drawDigits(g, Integer.toString(playersprite.getLives()), x, y + 4 + l);
        image = ImageManager.getImage("coins.gif");
        x = Game.xlen - 10 - image.getWidth(null);
        g.drawImage(image, x, y, null);
        if (CoinSprite.frames == null)
            new CoinSprite();
        image = CoinSprite.frames[(int)(coinAnimTimer * 8F) % CoinSprite.frames.length];
        g.drawImage(image, x + 23, y - 1, null);
        String s = Integer.toString(playersprite.getNoCoins());
        x -= digitsLen(s);
        drawDigits(g, s, x, y + 4);
        if (points < playersprite.getPoints())
            points += 50F * d + (playersprite.getPoints() - points) / 10F;
        if (points > playersprite.getPoints())
            points = playersprite.getPoints();
        s = Integer.toString((int)points);
        x = Game.xlen / 2 - digitsLen(s) / 2;
        drawDigits(g, s, x, y + 4);
        image = hearts[0];
        y = 10;
        x = 10;
        for (int i = 0; i < heartStatus.length; i++) {
            if (i < playersprite.getHealth())
                heartStatus[i] += d * 5F;
            else
                heartStatus[i] -= d * 5F;
            if (heartStatus[i] < 0.0F) heartStatus[i] = 0.0F;
            if (heartStatus[i] > 3F) heartStatus[i] = 3F;
            if (heartStatus[i] >= 1.0F && (playersprite.getHealth() != 1.0F || heartBlinkTimer % 0.5D <= 0.125D))
                g.drawImage(hearts[2 - (int)(heartStatus[i] - 1.0F)], x, y, null);
            x += image.getWidth(null) + 3;
        }
        
        if (playersprite.getHealth() != 1.0F)
            heartBlinkTimer = -1F;
        heartBlinkTimer += d;
        if (heartBlinkTimer > 2D)
            heartBlinkTimer = 0.0F;
        float f2 = SoundManager.ref(super.game).getProgress();
        if (f2 < 1.0F) {
            String s2 = Text.loadingSounds;
            g.setColor(Color.black);
            g.drawString(s2, super.xlen - g.getFontMetrics().stringWidth(s2) - 5, g.getFontMetrics().getAscent() + 5);
            Font font2 = g.getFont();
            g.setFont(smallFont);
            s2 = Text.havingControlProblems;
            g.drawString(s2, super.xlen - g.getFontMetrics().stringWidth(s2) - 5, g.getFontMetrics().getAscent() + 30);
            g.setFont(font2);
            int l1 = 100;
            g.drawRect(super.xlen - l1 - 5, 20, l1, 5);
            g.setColor(Color.white);
            g.fillRect((super.xlen - l1 - 5) + 1, 21, (int)(f2 * l1) - 1, 4);
        }
        if (playersprite.hasCompletedLevel())
            levelEndTimer += d;
        if (playersprite.hasCompletedLevel()) {
            if (endOfLevelTimer > 2.0F) {
                g.setColor(Game.bgColor);
                g.fillRect(0, 0, (int)((endOfLevelTimer - 2.0F) * super.xlen), super.ylen);
            }
            Image image1 = ImageManager.getImage("levelCompleted.gif");
            float f3 = levelEndTimer;
            if (f3 > 2.0F)
                f3 = 2.0F;
            int i2 = (int)(Math.sin(f3 * 5F) * (200F - f3 * 100F));
            int j2 = (int)(Math.cos(f3 * 5F) * (200F - f3 * 100F));
            Graphics g2 = g.create(0, 0, super.xlen, super.ylen);
            g2.drawImage(image1, (super.xlen / 2 - image1.getWidth(null) / 2) + i2, (super.ylen / 2 - image1.getHeight(null) / 2) + j2, null);
            Font font2 = g.getFont();
            g.setFont(new Font("Arial, Helvetica, Helv", 1, 20));
            String s4 = null;
            if (playersprite.getCoinsCollectedOnThisLevel() == world.getLevel().getNCoins())
                s4 = Text.youFoundAll + " " + playersprite.getCoinsCollectedOnThisLevel() + " " + Text.coins;
            else
                s4 = Text.youFound + " " + playersprite.getCoinsCollectedOnThisLevel() + " " + Text.coinsOnThisLevel;
            int k2 = super.xlen / 2 - g.getFontMetrics().stringWidth(s4) / 2;
            int l2 = super.ylen - 60;
            g.setColor(Color.black);
            g.drawString(s4, k2 + 2, l2 + 2);
            g.setColor(Color.white);
            g.drawString(s4, k2, l2);
            g.setFont(font2);
            if (endOfLevelTimer > 3F)
                nextLevel();
            endOfLevelTimer += d;
        }
        if (stripeWidth == -1) {
            stripeWidth = super.xlen / startStripes + 1;
            for (int j1 = 0; j1 < startStripes; j1++)
                stripes[j1] = (-j1) * 0.3F;

        }
        if (stripes[stripes.length - 1] < super.ylen) {
        	g.setColor(Game.bgColor);
            for (int k1 = 0; k1 < startStripes; k1++)
                if (stripes[k1] < 0.0F) {
                    stripes[k1] += d * 2.0F;
                    g.fillRect(k1 * stripeWidth, 0, stripeWidth, super.ylen);
                } else if (k1 % 2 == 0) {
                    g.fillRect(k1 * stripeWidth, (int)stripes[k1], stripeWidth, (int)((super.ylen - stripes[k1]) + 1.0F));
                    stripes[k1] += stripesDel[k1] * d;
                    stripesDel[k1] += d * 500F;
                } else {
                    g.fillRect(k1 * stripeWidth, 0, stripeWidth, (int)(super.ylen - stripes[k1]));
                    stripes[k1] += stripesDel[k1] * d;
                    stripesDel[k1] += d * 500F;
                }
        }
        if (pause) {
            Font font1 = g.getFont();
            g.setFont(new Font("Arial, Helvetica, Helv", 1, 20));
            g.setColor(Color.white);
            String s3 = Text.gamePaused;
            g.drawString(s3, super.xlen / 2 - g.getFontMetrics().stringWidth(s3) / 2, super.ylen / 2);
            g.setFont(font1);
        }
        //startOfLevelTimer += d;
    }

    private void printDebug(Graphics g, double d, boolean flag) {
        byte byte0 = 15;
        int i = byte0;
        int k = 5;
        if (flag) {
            k--;
            i++;
            g.setColor(Color.black);
        } else {
            g.setColor(Color.white);
        }
        g.drawString("frame time: " + d, k, i);
        i += byte0;
        PlayerSprite playersprite = PlayerSprite.getReference();
        g.drawString("player contact flags: ", k, i);
        i += byte0;
        g.drawString("up: " + ((Sprite) (playersprite)).collUp, k + 20, i);
        i += byte0;
        g.drawString("down: " + ((Sprite) (playersprite)).collDown, k + 20, i);
        i += byte0;
        g.drawString("left: " + ((Sprite) (playersprite)).collLeft, k + 20, i);
        i += byte0;
        g.drawString("right: " + ((Sprite) (playersprite)).collRight, k + 20, i);
        i += byte0;
        i += byte0;
        g.drawString("Active sprites: " + SpriteManager.activeSprites + "/" + SpriteManager.allSprites, k, i);
        i += byte0;
    }

    public void printSpriteDump() {
        world.printSpriteDump();
    }

    @Override
	public boolean okToSleepBetweenFrames() {
        return false;
    }

    private int digitsLen(String s) {
        return s.length() * digitsWidth;
    }

    private void nextLevel() {
        PlayerSprite playersprite = PlayerSprite.getReference();
        playersprite.stepToNextLevel();
        if (playersprite.getCurrentLevel() > LevelStepCounts.steps.length)
            super.game.setAnimator(new EndOfGameAnimator(playersprite));
        else
            super.game.setAnimator(new LoadLevelAnimator(Text.createLevelName(playersprite.getCurrentLevel()), playersprite));
        Image image = ImageManager.getImage("levelCompleted.gif");
        super.game.offGfx.drawImage(image, super.xlen / 2 - image.getWidth(null) / 2, super.ylen / 2 - image.getHeight(null) / 2, null);
    }

    @Override
	public void keyDown(Event event, int keyCode) {
        PlayerSprite playersprite = PlayerSprite.getReference();
        
        switch(keyCode) {
        
        case 72: // 'H'
        case 104: // 'h'
        case 1006: 
            playersprite.setKey(1, true);
            break;

        case 90: // 'Z'
        case 122: // 'z'
            playersprite.kill();
            break;

        case 75: // 'K'
        case 107: // 'k'
        case 1007: 
            playersprite.setKey(2, true);
            break;

        case 32: // ' '
        case 85: // 'U'
        case 117: // 'u'
        case 1004: 
            playersprite.setKey(0, true);
            break;

        case 100: // 'd'
            printDebug = !printDebug;
            break;

        case 102: // 'f'
            printFrameRate = !printFrameRate;
            break;

        case 108: // 'l'
            printDebugLayer = !printDebugLayer;
            break;

        case 77: // 'M'
        case 109: // 'm'
        	if (nPressed)
        		playersprite.completeLevel();
            break;

        case 78: // 'N'
        case 110: // 'n'
        	nPressed = true;
            break;

        case 80: // 'P'
        case 112: // 'p'
            pause = !pause;
            break;

        case 83: // 'S'
        case 115: // 's'
            SoundManager.ref(super.game).enableSound();
            break;
        }
        if (currC == -1) {
            for (int k = 0; k < cheatStr.length; k++)
                if (keyCode == cheatStr[k].charAt(0)) {
                    currC = k;
                    cheatIndex = 1;
                    return;
                }
            return;
        }
        if (keyCode != cheatStr[currC].charAt(cheatIndex)) {
            currC = -1;
            return;
        }
        cheatIndex++;
        if (cheatIndex == cheatStr[currC].length()) {
            Trace.out(this, "Cheat: " + currC);
            cheat(currC);
            currC = -1;
            cheatIndex = 0;
        }
    }

    @Override
	public void keyUp(Event event, int keyCode) {
        PlayerSprite playerSprite = PlayerSprite.getReference();
        
        switch(keyCode) {
        
        case 72: // 'H'
        case 104: // 'h'
        case 1006: 
            playerSprite.setKey(1, false);
            return;

        case 75: // 'K'
        case 107: // 'k'
        case 1007: 
            playerSprite.setKey(2, false);
            return;

        case 32: // ' '
        case 85: // 'U'
        case 117: // 'u'
        case 1004: 
            playerSprite.setKey(0, false);
            return;

        case 78: // 'N'
        case 110: // 'n'
        	nPressed = false;
        }
    }
}
