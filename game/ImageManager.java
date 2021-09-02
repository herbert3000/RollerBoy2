package game;

import java.applet.Applet;
import java.awt.*;
import java.util.Hashtable;
import litecom.Trace;

public class ImageManager {

    public final String imagePath = "gfx/";
    private Hashtable<String, Image> images;
    private Applet applet;
    private static ImageManager reference;

    public ImageManager() {
        images = new Hashtable<String, Image>();
        if (Game.getReference() != null) {
            Trace.out(this, "<init> - applet");
            updateAppletRef();
        } else {
            Trace.out(this, "<init> - application");
        }
    }

    public static ImageManager getReference() {
        if (reference == null)
            reference = new ImageManager();
        return reference;
    }

    private Image loadImage(String imageName) {
        if (applet != null) {
            updateAppletRef();
            Image image = applet.getImage(applet.getDocumentBase(), imagePath + imageName);
            MediaTracker mediatracker = new MediaTracker(applet);
            mediatracker.addImage(image, 0);
            try {
                mediatracker.waitForAll();
            } catch(InterruptedException _ex) { }
            return image;
        }
        Image image = Toolkit.getDefaultToolkit().getImage(imagePath + imageName);
        MediaTracker mediatracker = new MediaTracker(new Button());
        mediatracker.addImage(image, 0);
        try {
            mediatracker.waitForAll();
        } catch(InterruptedException _ex) { }
        return image;
    }

    private void updateAppletRef() {
        applet = Game.getReference();
    }

    /*
    private void loadImages() {
        Trace.out(this, "Loading " + ImageList.imageList.length + " images...");
        updateAppletRef();
        MediaTracker mediatracker = applet != null ? new MediaTracker(applet) : new MediaTracker(new Button());
        for (int i = 0; i < ImageList.imageList.length; i++) {
            String s = ImageList.imageList[i];
            Image image = getImage(s);
            mediatracker.addImage(image, 0);
            images.put(s, image);
        }

        try {
            mediatracker.waitForAll();
        } catch(InterruptedException _ex) { }
        Trace.out(this, "Done!");
    }
    */

    public static Image getImage(String s) {
        return getReference().getImage2(s);
    }

    public Image getImage2(String s) {
        Image image = images.get(s);
        if (image == null) {
            Trace.out(this, "image cache miss, loading: " + s);
            image = loadImage(s);
            images.put(s, image);
        }
        return image;
    }
}
