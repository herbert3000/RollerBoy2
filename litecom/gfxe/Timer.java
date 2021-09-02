package litecom.gfxe;

import litecom.Trace;

public class Timer {

    private long lastTick;
    private long frameLastTick;
    private long firstTick;
    private int nFrames;
    private int totalFrames;
    //private int sleep;
    private double frameRate;
    private boolean firstTime;
    private double lastTime[];
    private boolean lastFrameChangeSkipped;
    int counter;
    double lastD;
    long startTick;
    double frameCountInterval;

    public Timer() {
        nFrames = 0;
        totalFrames = 0;
        //sleep = 0;
        firstTime = true;
        lastTime = new double[15];
        counter = 0;
        lastD = 0.05;
        frameCountInterval = 1.0;
    }

    public int getFrameRate() {
        return (int)frameRate;
    }

    public double getRealFrameSpeed() {
        long millis = System.currentTimeMillis();
        double d = ((double)millis - (double)lastTick) / 1000D;
        return d;
    }

    public int getAverageFrameRate() {
        return (int)((double)totalFrames / (double)((System.currentTimeMillis() - firstTick) / 1000L));
    }

    public void reset() {
        totalFrames = -5;
    }

    public double getFrameSpeed() {
        long millis = System.currentTimeMillis();
        if (firstTime) {
            firstTick = millis;
            lastTick = millis;
            frameLastTick = millis;
            firstTime = false;
        }
        double d = ((double)millis - (double)lastTick) / 1000D;
        if (Math.abs(d - lastD) > 0.5D && !lastFrameChangeSkipped) {
            Trace.out(this, "skipping big frame rate change: lastd:" + lastD + ", " + d);
            if (totalFrames >= 0)
                lastTime[totalFrames % lastTime.length] = lastD;
            lastFrameChangeSkipped = true;
        } else {
            if (totalFrames >= 0)
                lastTime[totalFrames % lastTime.length] = d;
            lastFrameChangeSkipped = false;
        }
        nFrames++;
        totalFrames++;
        if (millis - frameLastTick > 1000L) {
            frameRate = (double)nFrames / (double)((millis - frameLastTick) / 1000L);
            nFrames = 0;
            frameLastTick = millis;
        }
        if (totalFrames > 0) {
            double d1 = totalFrames < lastTime.length ? totalFrames : lastTime.length - 1;
            d = 0.0D;
            for (int i = 0; i < d1; i++)
                d += lastTime[i];

            d /= d1;
        }
        if (d == 0.0D)
            d = lastD;
        lastD = d;
        lastTick = millis;
        return d;
    }
}
