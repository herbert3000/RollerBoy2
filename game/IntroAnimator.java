package game;

//import java.applet.Applet;
import java.awt.*;
//import java.util.StringTokenizer;
//import java.util.Vector;
import litecom.gfxe.ImageSplitter;
//import litecom.gfxe.Timer;
import litecom.Trace;

public class IntroAnimator extends Animator implements Runnable {

    //private String name;
    //private Image gameCanvas;
    private Image intro[];
    private String highscores[][];
    //private FontMetrics fm;
    //private Font font;
    private Image image;
    private Image buttonsImage;
    private TextField textField;
    private Button ok;
    private Button cancel;
    private Panel panel;
    private boolean passwordPanelShowing;
    private int buttonYpos;
    private int jumpToLevel;
    //private String jumpToLevelName;
    private Graphics loadScoresG;
    int xRank;
    int xName;
    int xScore;
    int xLevel;
    double scoreY;
    double startY;
    float blah;
    private float introTimer;
    private boolean mouseDown;
    int dragY;

    public IntroAnimator() {
        passwordPanelShowing = false;
        jumpToLevel = -1;
        //jumpToLevelName = null;
        blah = 0.0F;
        introTimer = -1F;
        dragY = -1;
    }

    private void initIntro() {
        intro = (new ImageSplitter(Game.getReference(), "gfx/intro.gif", 32, 32, null)).getImages();
    }

    private void drawShaded(Graphics g, String s, int x, int y) {
        g.setColor(Color.black);
        g.drawString(s, x + 1, y + 1);
        g.setColor(Color.white);
        g.drawString(s, x, y);
    }

    private void doStartScreen(Graphics g, double d) {
    	int x = super.xlen / 2 - 225;
    	int y = super.ylen / 2 - 150;
    	
        blah += d;
        
        /*
        if (loadScoresG == null) {
            loadScoresG = g;
            synchronized(this) {
                notifyAll();
            }
        }
        */
        
        g.setColor(Game.bgColor);
        g.fillRect(0, 0, super.xlen, super.ylen);
        
        if (image != null)
            g.drawImage(image, x, y, null);
        
        g.setColor(Color.black);
        g.drawString(Text.version + " " + Game.version, 5, super.ylen - 4);
        g.setColor(Color.white);
        g.drawString(Text.version + " " + Game.version, 4, super.ylen - 4 - 1);
        
        /*
        if (highscores != null) {
            drawScores(g, d);
        } else {
            String s = Text.loadingHighscores;
            g.setColor(Color.black);
            g.drawString(s, (super.xlen / 2 - g.getFontMetrics().stringWidth(s) / 2) + 1, (super.ylen - 20 - 1) + 1);
            g.setColor(Color.white);
            g.drawString(s, super.xlen / 2 - g.getFontMetrics().stringWidth(s) / 2, super.ylen - 20 - 1);
            try {
                Thread.sleep(100L); // 300L
            } catch(InterruptedException _ex) { }
        }
        */
        
        buttonYpos = y + 10;
        if (buttonsImage != null)
            g.drawImage(buttonsImage, super.xlen / 2 - buttonsImage.getWidth(null) / 2, buttonYpos, null);
        
        if (jumpToLevel >= 1) {
            Animator animator = null;
            try {
                animator = new SelectCharacterAnimator(jumpToLevel);
            } catch(Exception exception) {
                throw new RuntimeException("Could not load select character animator: " + exception);
            }
            super.game.setAnimator(animator);
        }
    }

    private void startGame() {
        Animator animator = null;
        try {
            animator = new SelectCharacterAnimator();
        } catch(Exception exception) {
            throw new RuntimeException("Could not load select character animator: " + exception);
        }
        super.game.setAnimator(animator);
    }

    @Override
	public void run() {
    	/*
        Trace.out(this, "caching select char animator class");
        try {
        	new SelectCharacterAnimator();
        } catch(Exception exception) {
            throw new RuntimeException("Could not load select character animator: " + exception);
        }
        try {
            while(loadScoresG == null) 
                synchronized(this) {
                    wait();
                }
        } catch(InterruptedException _ex) { }
        if (loadScoresG != null)
            showHighscores(loadScoresG);
        if (intro == null)
            initIntro();
            */
    }

    @Override
	public void action(Event event, Object obj) {
        if (event.target == textField || event.target == ok) {
            String s = textField.getText();
            for (int i = 0; i < LevelStepCounts.passwords.length; i++) {
                if (!LevelStepCounts.passwords[i].equalsIgnoreCase(s))
                    continue;
                jumpToLevel = i + 1;
                break;
            }
            removePasswordPanel();
            return;
        }
        if (event.target == cancel)
            removePasswordPanel();
    }

    @Override
	public void mouseDown(Event event, int x, int y) {
        mouseDown = true;
    }

    @Override
	public void mouseUp(Event event, int x, int y) {
        mouseDown = false;
        dragY = -1;
        if (introTimer != -1F)
            introTimer = 100F;
        if (buttonsImage != null) {
            if (y > buttonYpos && y < buttonYpos + buttonsImage.getHeight(null))
                if (x < super.xlen / 2)
                    showIntro();
                else
                    showPasswordPanel();
            super.game.showStatus("");
        }
    }

    @Override
	public void blur() {
        if (passwordPanelShowing)
            removePasswordPanel();
    }

    @Override
	public void focus() {
        xRank = 10;
        xName = 40;
        xScore = 100;
        xLevel = super.xlen - 40;
        startY = super.ylen + 30;
        scoreY = startY;
        image = super.game.getImage(Game.documentBase, "gfx/title_rollerboy2.gif");
        buttonsImage = super.game.getImage(Game.documentBase, "gfx/startpassword.gif");
        super.game.getFrameTimer().reset();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
	public void update(Graphics g, double d) {
        if (introTimer == -1F) {
            doStartScreen(g, d);
        } else {
            if (intro == null)
                initIntro();
            
            for (int x = 0; x < super.xlen / 32 + 1; x++) {
                for (int y = 0; y < super.ylen / 32 + 1; y++) {
                    g.drawImage(intro[1], x * 32, y * 32, null);
                }
            }
            
            for (int x = 0; x < super.xlen / 32 + 1; x++)
                g.drawImage(intro[0], x * 32, super.ylen - 32, null);

            int xOff = super.xlen / 2 - 225;
            g.drawImage(intro[4], 60 + xOff, super.ylen - 64, null);
            g.drawImage(intro[3], 270 + xOff, super.ylen - 64, null);
            if (introTimer < 3F)
                g.drawImage(intro[2], 160 + xOff, super.ylen - 64, null);
            else if (introTimer > 3F && introTimer < 8F) {
                int x = super.xlen / 2 - 97;
                float f = (introTimer - 3F) / 3F;
                if (f > 1.0F)
                    f = 1.0F;
                else
                    x = (int)(x + Math.sin(introTimer * 5F) * (1.0F - (introTimer - 3F) / 3F) * 40D);
                int y = (int)(f * (super.ylen - 96)) - 32;
                g.drawImage(intro[5], x, y, null);
                g.drawImage(intro[6], x + 32, y, null);
                g.drawImage(intro[7], x + 64, y, null);
                g.drawImage(intro[2], 160 + xOff, super.ylen - 64, null);
                if (introTimer > 7F) {
                    g.drawImage(intro[8], x + 32, y + 32, null);
                    g.drawImage(intro[8], x + 32, y + 64, null);
                }
            } else if (introTimer > 8F && introTimer < 9F) {
                g.drawImage(intro[2], 160 + xOff, (int)(super.ylen - 64 - (introTimer - 8F) * 32F * 2.0F), null);
                int x = super.xlen / 2 - 97;
                int y = super.ylen - 96 - 32;
                g.drawImage(intro[5], x, y, null);
                g.drawImage(intro[6], x + 32, y, null);
                g.drawImage(intro[7], x + 64, y, null);
                g.drawImage(intro[8], x + 32, y + 32, null);
                g.drawImage(intro[8], x + 32, y + 64, null);
            } else {
                int x = super.xlen / 2 - 97;
                int y = (int)((1.0F - (introTimer - 9F) / 3F) * (super.ylen - 96)) - 32;
                x = (int)(x + Math.sin(introTimer * 7F) * ((introTimer - 9F) / 3F) * 40D);
                g.drawImage(intro[5], x, y, null);
                g.drawImage(intro[6], x + 32, y, null);
                g.drawImage(intro[7], x + 64, y, null);
            }
            g.setFont(new Font("Arial, Helvetica, Helv", 1, 16));
            if (introTimer < 3.5F) {
                g.setColor(Color.black);
                String s1 = Text.rollerboyIsHaningOut;
                g.drawString(s1, 10, 25);
                g.setColor(Color.white);
            }
            if (introTimer > 3.5F && introTimer < 6F) {
                g.setColor(Color.black);
                String s2 = Text.whenSuddenly;
                g.drawString(s2, 10, 25);
                g.setColor(Color.white);
            }
            String s3 = Text.clickToSkipIntro;
            g.drawString(s3, super.xlen - g.getFontMetrics().stringWidth(s3) - 5, super.ylen - 10);
            if (introTimer > 12F)
                startGame();
            introTimer += d;
        }
        try {
            Thread.sleep(5L);
        } catch(InterruptedException _ex) {}
    }

    public void showHighscores(Graphics g) {
    	//HighscoreHandler highscoreHandler = super.game.getHighscoreHandler();
    	//highscoreHandler.loadHighScores();
    	highscores = new String[0][0];
    	
    	/*
        font = new Font("Arial, Helvetica, Helv", 1, 10);
        g.setFont(font);
        fm = g.getFontMetrics();
        highscoreHandler.loadHighScores();
        Vector<String[]> vector = new Vector<String[]>();
        String as[] = new String[0];
        String as1[] = { Text.top30 };
        vector.addElement(as1);
        vector.addElement(as);
        addScoresToList(vector, highscoreHandler.getHighscores10());
        as1 = new String[1];
        as1[0] = Text.top100;
        vector.addElement(as);
        vector.addElement(as1);
        vector.addElement(as);
        addScoresToList(vector, highscoreHandler.getHighscores100());
        highscores = new String[vector.size()][];
        for (int i = 0; i < vector.size(); i++) {
            String as2[] = vector.elementAt(i);
            highscores[i] = new String[as2.length];
            for (int j = 0; j < as2.length; j++)
                highscores[i][j] = as2[j];
        }
        super.game.getFrameTimer().reset();
        */
    }

    /*
    private void addScoresToList(Vector<String[]> vector, Vector<?> vector1) {
        String as[] = {
            Text.rank, Text.score, Text.name, Text.level
        };
        vector.addElement(as);
        for (int j = 0; j < vector1.size(); j++) {
            String as1[] = new String[4];
            StringTokenizer stringtokenizer = new StringTokenizer("" + vector1.elementAt(j), "\t");
            String s1 = stringtokenizer.nextToken();
            String s2 = stringtokenizer.nextToken();
            String s3 = stringtokenizer.nextToken();
            as1[0] = "" + (j + 1);
            as1[1] = s1;
            for (; fm.stringWidth(s2) > xLevel - xScore; s2 = s2.substring(0, s2.length() - 1));
            as1[2] = s2;
            as1[3] = s3;
            vector.addElement(as1);
        }
    }
    */

    private void drawScores(Graphics g, double d) {
        int i = (int)(scoreY % 10D);
        int j = (int)(-scoreY / 10D);
        int k = j + 100;
        if (j > highscores.length) {
            scoreY = startY;
            return;
        }
        try {
            for (int l = j; l < k; l++) {
                if (l > highscores.length)
                    break;
                if (l >= 0 && l < highscores.length && i > 0 && i < super.ylen + 10) {
                    String as[] = highscores[l];
                    if (as.length > 0)
                        drawShaded(g, as[0], xRank, i);
                    if (as.length > 1)
                        drawShaded(g, as[1], xName, i);
                    if (as.length > 2)
                        drawShaded(g, as[2], xScore, i);
                    if (as.length > 3)
                        drawShaded(g, as[3], xLevel, i);
                }
                i += 10;
            }
        } catch(Exception exception) {
            throw new RuntimeException("Could not parse scores - " + exception);
        }
        if (!mouseDown)
            scoreY -= d * 25D;
    }

    private void showPasswordPanel() {
        if (passwordPanelShowing)
            return;
        
        super.game.setLayout(new BorderLayout());
        panel = new Panel();
        Label label = new Label(Text.enterLevelPassword);
        label.setForeground(Color.white);
        panel.add(label);
        panel.add(textField = new TextField(6));
        textField.setBackground(Color.white);
        textField.setForeground(Color.black);
        panel.add(ok = new Button(Text.ok));
        panel.add(cancel = new Button(Text.cancel));
        super.game.add("South", panel);
        super.game.validate();
        textField.requestFocus();
        passwordPanelShowing = true;
    }

    private void removePasswordPanel() {
        if (panel != null)
            super.game.remove(panel);
        super.game.validate();
        passwordPanelShowing = false;
    }

    private void showIntro() {
    	removePasswordPanel();
        introTimer = -0.9F;
        super.game.getFrameTimer().reset();
    }

    @Override
	public void mouseDrag(Event event, int x, int y) {
        if (dragY != -1)
            scoreY -= dragY - y;
        dragY = y;
    }

    @Override
	public void mouseMove(Event event, int x, int y) {
        dragY = -1;
        if (introTimer == -1F && buttonsImage != null) {
            if (y > buttonYpos && y < buttonYpos + buttonsImage.getHeight(null))
                if (x < super.xlen / 2) {
                    super.game.showStatus(Text.clickHereToStartNew);
                    return;
                } else {
                    super.game.showStatus(Text.clickHereToEnterPassword);
                    return;
                }
            super.game.showStatus("");
        }
    }
}
