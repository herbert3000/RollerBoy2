package game;

//import java.applet.Applet;
import java.awt.*;

public class SelectCharacterAnimator extends Animator {

    public static int playerType;
    public static final int PLAYER_ROLLERBOY = 0;
    public static final int PLAYER_RASTAGIRL = 1;
    private Image image;
    private Image selRasta;
    private Image selNinja;
    private MediaTracker mediaTracker;
    int state;
    private String levelName;
    public boolean rastaSelected;
    
    public SelectCharacterAnimator() {
    	this(1);
    }

    public SelectCharacterAnimator(int levelIndex) {
        state = 0;
        levelName = Text.createLevelName(levelIndex);
    }

    @Override
	public void focus() {
        mediaTracker = new MediaTracker(super.game);
        image = super.game.getImage(Game.codeBase, "gfx/select.gif");
        mediaTracker.addImage(image, 0);
        selRasta = super.game.getImage(Game.codeBase, "gfx/dreadselect.gif");
        selNinja = super.game.getImage(Game.codeBase, "gfx/ninjaselect.gif");
    }

    @Override
	public void update(Graphics g, double d) {
        g.setColor(Game.bgColor);
        g.fillRect(0, 0, super.xlen, super.ylen);
        g.setFont(new Font("Arial, Helvetica, Helv", 1, 20));
        g.setColor(Color.white);
        if (state == 0) {
            String s = Text.oneSecond;
            g.drawString(s, super.xlen / 2 - g.getFontMetrics().stringWidth(s) / 2, super.ylen / 2);
        }
        if (image != null) {
        	int x = super.xlen / 2 - 225;
        	int y = super.ylen / 2 - 150;
        	
            g.drawImage(image, x, y, null);
            if (mediaTracker != null && mediaTracker.checkAll(true))
                if (rastaSelected) {
                    if (selRasta != null)
                    	g.drawImage(selRasta, x + 106, y + 137, null);
                } else if (selRasta != null)
                	g.drawImage(selNinja, x + 288, y + 141, null);
        }
        if (state == 2) {
        	LoadLevelAnimator animator = null;
            try {
                animator = new LoadLevelAnimator();
            } catch(Exception exception) {
                throw new RuntimeException("Could not load load level animator: " + exception);
            }
            animator.setParam(levelName);
            super.game.setAnimator(animator);
        }
        if (state > 0) {
            String s1 = Text.pleaseWait;
            g.drawString(s1, super.xlen / 2 - g.getFontMetrics().stringWidth(s1) / 2, super.ylen / 2);
            state = 2;
        }
    }

    @Override
	public void mouseDown(Event event, int x, int y) {
        if (x > super.xlen / 2)
            playerType = PLAYER_ROLLERBOY;
        else
            playerType = PLAYER_RASTAGIRL;
        state = 1;
    }

    @Override
	public void mouseMove(Event event, int x, int y) {
        if (x > super.xlen / 2)
            rastaSelected = false;
        else
        	rastaSelected = true;
    }
}
