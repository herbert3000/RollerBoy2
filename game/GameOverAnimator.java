package game;

import game.sprites.PlayerSprite;
import java.awt.*;

public class GameOverAnimator extends Animator {

    //private String name;
    private PlayerSprite player;
    private boolean gotHighscore;
    private boolean gotHighscoreAllTime;
    //private TextField textField;
    //private Button button;
    //private Panel panel;
    //private boolean noMore;

    public GameOverAnimator(PlayerSprite playerSprite) {
        this.player = playerSprite;
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
    	//gotHighscore = player.getPoints() != 0 && player.getPoints() > super.game.getHighscoreHandler().minScore10();
        //gotHighscoreAllTime = player.getPoints() != 0 && player.getPoints() > super.game.getHighscoreHandler().minScore100();
        //if (!gotHighscore)
        //    if (!gotHighscoreAllTime);
    	
    	/*
        gotHighscore = player.getPoints() != 0 && player.getPoints() > super.game.minScore10();
        gotHighscoreAllTime = player.getPoints() != 0 && player.getPoints() > super.game.minScore100();
        if (gotHighscore || gotHighscoreAllTime) {
            super.game.setLayout(new BorderLayout());
            panel = new Panel();
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
	public void update(Graphics g, double delta) {
        Image image;
        if (gotHighscore || gotHighscoreAllTime)
            image = ImageManager.getImage("highscore.gif");
        else
            image = ImageManager.getImage("gameover.gif");
        int x = super.xlen / 2 - image.getWidth(null) / 2;
        int y = super.ylen / 2 - image.getHeight(null) / 2;
        g.drawImage(image, x, y, null);
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
