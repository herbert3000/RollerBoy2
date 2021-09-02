package game;

import java.awt.Image;

public class Tile {

    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    public static final int TYPE_1 = 1;
    public static final int TYPE_2 = 2;
    public static final int TYPE_3 = 3;
    public int L;
    public Image image;
    public boolean solid;
    public boolean flag2;
    public int x;
    public int y;
    public boolean bgTile;
    public int S;

    Tile(Image image, boolean flag1, boolean flag2) {
        S = -1;
        if (image.getWidth(null) != WIDTH || image.getHeight(null) != HEIGHT) {
            throw new RuntimeException("Tile image size " + image.getWidth(null) + "x" + image.getHeight(null) + " not " + WIDTH + "x" + HEIGHT);
        } else {
            this.image = image;
            this.solid = flag1;
            this.flag2 = flag2;
            L = TYPE_1;
            return;
        }
    }

    Tile(Image image, boolean flag) {
        this(image, flag, false);
    }

    Tile() {
        S = -1;
        L = TYPE_2;
    }

    public void H(int x, int y) {
        if (image != null) {
            image.flush();
            image = null;
        }
        this.x = x;
        this.y = y;
        L = TYPE_3;
    }
}
