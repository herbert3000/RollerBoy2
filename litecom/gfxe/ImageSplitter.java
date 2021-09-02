package litecom.gfxe;

import java.applet.Applet;
import java.awt.*;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.util.Vector;
import litecom.Trace;

public class ImageSplitter {

    private Image image;
    private int xlen;
    private int ylen;
    private int xspace;
    private int yspace;
    private Loader loader;
    private boolean containsTransparency;
    private Applet a;
    public Vector<Image> imgs;

    public ImageSplitter(Applet applet, String name, int xlen, int ylen, int xspace, int yspace, Loader loader) {
        this(applet, name, xlen, ylen, xspace, yspace, loader, true);
    }

    public ImageSplitter(Applet a, String name, int xlen, int ylen, int xspace, int yspace, Loader loader,  boolean containsTransparency) {
        imgs = new Vector<Image>();
        Trace.out(this, "<init> - Splitting: " + name);
        Trace.out(this, "mem total: " + Runtime.getRuntime().totalMemory());
        Trace.out(this, "mem free: " + Runtime.getRuntime().freeMemory());
        this.xlen = xlen;
        this.ylen = ylen;
        this.xspace = xspace;
        this.yspace = yspace;
        this.containsTransparency = containsTransparency;
        this.a = a;
        if (a != null)
            image = a.getImage(a.getDocumentBase(), name);
        else
            image = Toolkit.getDefaultToolkit().getImage(name);
        
        this.loader = loader;
        waitForImage(image);
        if (loader != null)
            loader.progress();
        
        splitImages();
    }

    public ImageSplitter(Applet applet, String name, int xlen, int ylen, Loader loader) {
        this(applet, name, xlen, ylen, 0, 0, loader);
    }

    public ImageSplitter(Applet applet, String name, int xlen, int ylen) {
        this(applet, name, xlen, ylen, 0, 0, null);
    }

    public ImageSplitter(String name, int xlen, int ylen) {
        this(null, name, xlen, ylen, 0, 0, null);
    }

    private void splitImages() {
        int x = xspace;
        int y = yspace;
        do {
            if (x + xlen > image.getWidth(null)) {
                x = xspace;
                y += ylen + yspace;
            }
            if (y + ylen <= image.getHeight(null)) {
                Image image1;
                if(containsTransparency || a == null) {
                    CropImageFilter cropimagefilter = new CropImageFilter(x, y, xlen, ylen);
                    FilteredImageSource filteredimagesource = new FilteredImageSource(image.getSource(), cropimagefilter);
                    image1 = Toolkit.getDefaultToolkit().createImage(filteredimagesource);
                    waitForImage(image1);
                } else {
                    image1 = a.createImage(xlen, ylen);
                    Graphics g = image1.getGraphics();
                    g.drawImage(image, -x, -y, null);
                }
                if (loader != null)
                    loader.progress();
                imgs.addElement(image1);
                x += xlen + xspace;
            } else {
                image.flush();
                return;
            }
        } while(true);
    }

    public void waitForImage(Image image) {
        MediaTracker mediatracker = new MediaTracker(new Frame());
        mediatracker.addImage(image, 0);
        try {
            mediatracker.waitForAll();
            return;
        } catch(Exception _ex) {
            return;
        }
    }

    public Dimension getSourceImageDimension() {
        return new Dimension(image.getWidth(null), image.getHeight(null));
    }

    public Image getImage(int i) {
        return imgs.elementAt(i);
    }

    public Image[] getImages() {
        Image aimage[] = new Image[imgs.size()];
        for (int i = 0; i < imgs.size(); i++)
            aimage[i] = imgs.elementAt(i);

        return aimage;
    }
}
