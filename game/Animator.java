package game;

import java.awt.*;

import litecom.Trace;

public class Animator implements Runnable {

    protected int xlen;
    protected int ylen;
    protected Game game;
    private boolean done;
    private Animator animator;

    public Animator() {
        done = false;
    }

    public void setParent(int xlen, int ylen, Game game) {
        this.xlen = xlen;
        this.ylen = ylen;
        this.game = game;
    }

    public void setParam(String s) {}

    public boolean okToSleepBetweenFrames() {
        return true;
    }
 
    public void blur() {}

    public void focus() {
        Thread thread = new Thread(this);
        thread.setPriority(1);
        thread.start();
    }

    public void update(Graphics g, double d) {
        g.setFont(new Font("Arial, Helvetica, Helv", 1, 15));
        FontMetrics fontmetrics = g.getFontMetrics();
        g.setColor(Game.bgColor);
        g.fillRect(0, 0, xlen, ylen);
        g.setColor(Color.white);
        String s = Text.loadingRollerBoy2;
        g.drawString(s, xlen / 2 - fontmetrics.stringWidth(s) / 2, ylen / 2);
        
        try {
            Thread.sleep(100L); // 500L
        } catch(Exception _ex) { }
        if (done)
            game.setAnimator(animator);
    }

    @Override
	public void run() {
        game.repaint();
        Toolkit.getDefaultToolkit().sync();
        new ImageManager();
        try {
            animator = new IntroAnimator(); // (Animator)Class.forName("game.IntroAnimator").newInstance();
        } catch(Exception exception) {
            throw new RuntimeException("Could not load intro animator: " + exception);
        }
        done = true;
    }

    public void action(Event event, Object obj) {}
    public void keyDown(Event event, int keyCode) {}
    public void keyUp(Event event, int keyCode) {}
    public void mouseDown(Event event, int x, int y) {}
    public void mouseUp(Event event, int x, int y) {}
    public void mouseDrag(Event event, int x, int y) {}
    public void mouseMove(Event event, int x, int y) {}
}
