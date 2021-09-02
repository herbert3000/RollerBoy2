package game;

import java.applet.Applet;
import java.awt.*;
import java.net.URL;

import litecom.Trace;
import litecom.gfxe.Timer;

public class Game extends Applet implements Runnable {

	private static final long serialVersionUID = 481664357246504689L;
	public static final String version = "1.1.2b Build 5229";
    public static final int downtownLevels = 6;
    public static final int homeworldLevel = 10;
    public static final int ufoLevels = 8;
    public static URL codeBase;
    public static URL documentBase;
    public static int xlen;
    public static int ylen;
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final boolean debugMode = false;
    public static final boolean productionRelease = true;
    public static final boolean endUserEditorRelease = false;
    public static final boolean iceLevelExpansionPack = false;
    public static String userLevelPrefix;
    private Object frameTimer;
    private Thread thread;
    public Graphics offGfx;
    private Image offImg;
    private Image basicOffImg;
    private double maxStepTime;
    private Animator animator;
    private static Game reference;
    private boolean stopMainLoop;
    private boolean stepTrace;
    public static Color bgColor = new Color(16, 95, 103);
    private int sem_i;
    private boolean firstTimeRun;
    private Throwable currentError;
    //private HighscoreHandler highscoreHandler;

    public Game() {
    	//highscoreHandler = new HighscoreHandler(this);
        maxStepTime = 0.6;
        sem_i = 0;
        firstTimeRun = true;
        currentError = null;
    }

    @Override
	public void start() {
        trace(this, "start()");
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        trace(this, "start() done");
    }

    @Override
	public void update(Graphics g) {
        paint(g);
    }

    @Override
	public void paint(Graphics g) {
        g.drawImage(offImg, 0, 0, null);
        sem_set();
    }

    @Override
	public void stop() {
        trace(this, "stop()");
        trace(this, "Average framerate: " + getFrameTimer().getAverageFrameRate());
        thread = null;
        trace(this, "stop() done");
    }

    @Override
	public void run() {
        trace(this, "run() - firstTime: " + firstTimeRun);
        if (firstTimeRun) {
            setAnimator(new Animator());
            firstTimeRun = false;
        }
        frameTimer = new Timer();
        requestFocus();
        while (thread != null) {
            stopMainLoop = stepTrace;
            double d = getFrameTimer().getFrameSpeed();
            if (d > maxStepTime)
                d = maxStepTime;
            if (d == 0.0D)
                System.out.println("d=0!");
            animate(d);
            Toolkit.getDefaultToolkit().sync();
            repaint();
            //sem_reset();
            Thread.yield();
            if (animator != null && ((Animator)animator).okToSleepBetweenFrames())
                try {
                    Thread.sleep(10L);
                } catch(InterruptedException _ex) { }
            while (stopMainLoop) 
                try {
                    Thread.sleep(100L);
                } catch(InterruptedException _ex) { }
        }
        trace(this, "run() done");
    }

    @Override
	public void init() {
    	//highscoreHandler.init();
    	
    	resize(Math.max(getWidth(), 450), Math.max(getHeight(), 300)); // resize to correct dimensions
    	
    	if (getParameter("debug") != null && getParameter("debug").equals("true")) {
    		Trace.killOutput = false;
    	} else {
    		//Trace.killOutput = true;
    		Trace.killOutput = false;
    	}
    	
    	//Frame frame = (Frame)this.getParent().getParent();
    	//frame.setTitle("RollerBoy2");
    	
        trace(this, "init()");
        System.out.println("RollerBoy2 version " + version + " (c)1998-2000 Maciek Drejak");
        reference = this;
        xlen = getSize().width;
        ylen = getSize().height;
        codeBase = getCodeBase();
        documentBase = getDocumentBase();
        basicOffImg = createImage(xlen, ylen);
        offImg = basicOffImg;
        offGfx = offImg.getGraphics();
        setBackground(bgColor);
        offGfx.setColor(bgColor);
        offGfx.fillRect(0, 0, xlen, ylen);
        offGfx.setFont(new Font("Arial, Helvetica, Helv", 1, 15));
        FontMetrics fontmetrics = offGfx.getFontMetrics();
        offGfx.setColor(Color.white);
        String s = Text.pleaseWait;
        offGfx.drawString(s, xlen / 2 - fontmetrics.stringWidth(s) / 2, ylen / 2);
        trace(this, "init() done");
    }

    private void animate(double d) {
        Graphics g = offGfx;
        if (currentError != null) {
            g.setFont(new Font("Arial, Helvetica", 1, 10));
            g.setColor(Color.blue.darker());
            g.fillRect(0, 0, xlen, ylen);
            g.setColor(Color.gray.brighter());
            g.drawString(Text.criticalProgramFault, 10, 20);
            g.drawString("" + currentError, 10, 40);
            if (currentError.getMessage() != null)
                g.drawString("" + currentError.getMessage(), 10, 60);
            g.drawString(Text.pleaseConsult, 10, 80);
            g.drawString(Text.troubleShootingURL, 10, 100);
            g.drawString(Text.forHelp, 10, 120);
            try {
                Thread.sleep(500L);
                return;
            } catch(Exception _ex) {
                return;
            }
        }
        if (animator != null)
            try {
                animator.update(g, d);
            } catch(Throwable throwable) {
                System.err.print("ERROR: " + throwable + "\r\n");
                throwable.printStackTrace();
                System.err.print("\r\n");
                currentError = throwable;
            }
    }

    public static Game getReference() {
        return reference;
    }

    public Animator getAnimator() {
        return animator;
    }

    public void setAnimator(Animator animator) {
        setAnimator(animator, null);
    }

    public void setAnimator(Animator a, Image image) {
        if (animator != null)
            ((Animator)animator).blur();
        if (image == null)
            offImg = basicOffImg;
        else
            offImg = image;
        offGfx = offImg.getGraphics();
        offGfx.setColor(bgColor);
        offGfx.fillRect(0, 0, xlen, ylen);
        animator = a;
        ((Animator)animator).setParent(xlen, ylen, this);
        ((Animator)animator).focus();
    }

    public int getFPS() {
        return getFrameTimer().getFrameRate();
    }
    
    public void toggleStepTrace() {
        stepTrace = !stepTrace;
    }

    private void trace(Object obj, String s) {
        if (!Trace.killOutput)
            System.out.println("class game: " + s);
    }

    public Timer getFrameTimer() {
        return (Timer)frameTimer;
    }

    public void setCurrentError(Throwable error) {
        this.currentError = error;
    }

    public synchronized void sem_set() {
        sem_i = 1;
        notifyAll();
    }

    public synchronized void sem_reset() {
        while (sem_i <= 0) 
            try {
                wait();
            } catch(InterruptedException _ex) { }
        sem_i = 0;
    }

    public synchronized void sem_up() {
        sem_i++;
        notifyAll();
    }

    public synchronized void sem_down() {
        while (sem_i <= 0) 
            try {
                wait();
            } catch(InterruptedException _ex) { }
        sem_i--;
    }

    @Override
	public boolean keyDown(Event event, int i) {
        stopMainLoop = false;
        Animator animator = getAnimator();
        if (animator == null) {
            return false;
        } else {
            animator.keyDown(event, i);
            return false;
        }
    }

    @Override
	public boolean keyUp(Event event, int keyCode) {
        Animator animator = getAnimator();
        if (animator == null)
            return false;
        
        animator.keyUp(event, keyCode);
        return false;
    }

    @Override
	public boolean action(Event event, Object obj){
        Animator animator = getAnimator();
        if (animator == null)
        	return false;
        
        animator.action(event, obj);
        return false;
    }

    @Override
	public boolean mouseDown(Event event, int x, int y) {
        Animator animator = getAnimator();
        if (animator == null)
            return false;

        animator.mouseDown(event, x, y);
        return true;
    }

    @Override
	public boolean mouseUp(Event event, int x, int y) {
        Animator animator = getAnimator();
        if (animator == null)
            return false;
        
        animator.mouseUp(event, x, y);
        return true;
    }

    @Override
	public boolean mouseMove(Event event, int x, int y) {
        Animator animator = getAnimator();
        if (animator == null) {
            return false;
        } else {
            animator.mouseMove(event, x, y);
            return true;
        }
    }

    @Override
	public boolean mouseDrag(Event event, int x, int y) {
        Animator animator = getAnimator();
        if (animator == null) {
            return false;
        } else {
            animator.mouseDrag(event, x, y);
            return true;
        }
    }

	//public HighscoreHandler getHighscoreHandler() {
		//return highscoreHandler;
	//}
}
