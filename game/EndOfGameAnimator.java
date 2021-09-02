package game;

import game.sprites.PlayerSprite;
//import java.applet.Applet;
import java.awt.*;

public class EndOfGameAnimator extends Animator {

    //private String name;
    private PlayerSprite player;
    //private boolean gotHighscore;
    //private boolean gotHighscoreAllTime;
    //private TextField textField;
    //private Button button;
    //private Panel panel;
    private Image image;
    private String text[] = {
        "You saved Rollerboy from the evil aliens.", "The aliens felt very ashamed and returned to", "their home planet. Rollerboy, Dreadlockgirl and", "Ninjaboy lived happily ever after...", "", "", "Programming and level design", "Maciek Drejak", "", "Graphics", 
        "Jesper Skoog", "", "Additional level design", "Michael Palka", "", "Quality Assurance", "Linda Dahlqvist", "Anders Hejdenberg", "Anja Komi", "Daniel Larsson", 
        "Alexander Malm", "Maria Thorman", "", "BETA Testers", "Marcus Dahlqvist", "Victor Dahlqvist", "sdfdsa sdsdfsdfsf", "", "Stunt Coordinaror", "Chuck Norris", 
        "", "Stunts", "Tom Sellec", "Burt Reynolds", "", "2019 Release", "herbert3000"
    };
    //private boolean noMore;
    private Font font;
    private float scrolly;
    private boolean firstTime;

    public EndOfGameAnimator() {
        firstTime = true;
    }

    public EndOfGameAnimator(PlayerSprite playerSprite) {
        firstTime = true;
        player = playerSprite;
    }

    private void drawShaded(Graphics g, String s, int x, int y) {
        g.setColor(Color.black);
        g.drawString(s, x + 1, y + 1);
        g.setColor(Color.white);
        g.drawString(s, x, y);
    }

    @Override
	public void blur() {
    	/*
        if (panel != null)
            super.game.remove(panel);
        super.game.validate();
        */
    }

    @Override
	public void focus() {
        image = super.game.getImage(Game.codeBase, "gfx/endofgame.gif");
        //gotHighscore = player.getPoints() != 0 && player.getPoints() > super.game.minScore10();
        //gotHighscoreAllTime = player.getPoints() != 0 && player.getPoints() > super.game.minScore100();
        /*
        if (gotHighscore || gotHighscoreAllTime) {
            super.game.setLayout(new BorderLayout());
            panel = new Panel();
            Label label = new Label("Enter highscore:");
            label.setForeground(Color.white);
            panel.add(label);
            panel.add(textField = new TextField(20));
            textField.setBackground(Color.white);
            textField.setForeground(Color.black);
            panel.add(button = new Button("OK"));
            super.game.add("South", panel);
            super.game.validate();
            textField.requestFocus();
        }
        */
    }

    @Override
	public void update(Graphics g, double d) {
        PlayerSprite.clearReference();
        g.setColor(Game.bgColor);
        g.fillRect(0, 0, super.xlen, super.ylen);
        if (image != null)
            g.drawImage(image, 0, 0, null);
        if (font == null)
            font = new Font("Arial, Helvetica, Helv", 1, 15);
        g.setFont(font);
        drawScroll(g, d);
    }

    private void drawScroll(Graphics g, double d) {
        if (firstTime) {
            scrolly = super.ylen;
            firstTime = false;
        }
        FontMetrics fontmetrics = g.getFontMetrics();
        int i = (int)scrolly;
        for (int j = 0; j < text.length; j++) {
            if (i > -10 && i < super.ylen + 10)
                drawShaded(g, text[j], super.xlen / 2 - fontmetrics.stringWidth(text[j]) / 2, i);
            i += 20;
        }
        scrolly -= d * 15D;
    }

    @Override
	public void action(Event event, Object obj) {
    	/*
        if ((event.target == textField || event.target == button) && !noMore) {
            if (gotHighscore)
                super.game.sendScore10(textField.getText(), player);
            if (gotHighscoreAllTime)
                super.game.sendScore100(textField.getText(), player);
            super.game.setAnimator(new IntroAnimator());
            noMore = true;
        }
        */
    }

    @Override
	public void mouseDown(Event event, int x, int y) {
    	//super.game.getHighscoreHandler().sendScore(player);
        super.game.setAnimator(new IntroAnimator());
    	/*
        if (!gotHighscore && !gotHighscoreAllTime)
            super.game.setAnimator(new IntroAnimator());
            */
    }

    @Override
	public void keyDown(Event event, int keyCode) {
    	//super.game.getHighscoreHandler().sendScore(player);
        super.game.setAnimator(new IntroAnimator());
    	/*
        if (!gotHighscore && !gotHighscoreAllTime)
            super.game.setAnimator(new IntroAnimator());
            */
    }
}
