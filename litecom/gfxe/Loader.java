package litecom.gfxe;

import java.awt.*;
import litecom.Trace;

public class Loader extends Thread {

    public boolean initPhaseDone;
    private int progress;
    private int mSteps;
    private LoaderTarget loaderTarget;
    private String name;
    private int barXLen;
    private int barYLen;
    private int fadeIn;

    public Loader(LoaderTarget loaderTarget) {
        mSteps = 0;
        name = "game";
        fadeIn = 0;
        this.loaderTarget = loaderTarget;
    }

    public synchronized void paint(Graphics g, int x, int y) {
        paint(g, x, y, true);
    }

    public synchronized void paint(Graphics g, int x, int y, boolean flag) {
        barXLen = x - 20;
        barYLen = barXLen / 10;
        if (flag) {
            g.setColor(Color.black);
            g.fillRect(0, 0, x, y);
        }
        g.setColor(Color.green);
        if (fadeIn < 255)
            fadeIn++;
        if (mSteps == 0) {
            FontMetrics fontmetrics = g.getFontMetrics();
            String s = "Loading " + name + " : " + progress + " steps.";
            g.drawString(s, x / 2 - fontmetrics.stringWidth(s) / 2, y / 2);
        } else {
            int x2 = x / 2 - barXLen / 2;
            int y2 = y / 2 - barYLen / 2;
            g.drawString(name, x / 2 - g.getFontMetrics().stringWidth(name) / 2, y2 / 2);
            if (progress > 0) {
                g.drawRect(x2, y2, barXLen, barYLen);
                g.fillRect(x2, y2, (int)(((float)progress / (float)mSteps) * barXLen), barYLen);
            }
        }
        Thread.yield();
    }

    public void setMessage(String name) {
        this.name = name;
    }

    public String getMessage() {
        return name;
    }

    public int getProgress() {
        return progress;
    }

    @Override
	public void run() {
        Trace.out(this, "run - start");
        loaderTarget.realInit();
        initPhaseDone = true;
        Trace.out(this, "run - end - steps: " + progress);
    }

    public void setSteps(int i) {
        mSteps = i;
    }

    public int getSteps() {
        return mSteps;
    }

    public synchronized void progress() {
        progress++;
        if (loaderTarget instanceof LoaderTarget2) {
            ((LoaderTarget2)loaderTarget).doRepaint();
            Thread.yield();
        }
    }

    public synchronized void progress(int i) {
        progress += i;
        if (loaderTarget instanceof LoaderTarget2) {
            ((LoaderTarget2)loaderTarget).doRepaint();
            Thread.yield();
        }
    }
}
