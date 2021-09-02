package game;

import java.awt.Point;

public class WorldWindow {

    private Level level;
    private Point pos;

    public WorldWindow() {
        pos = new Point(0, 0);
    }

    public WorldWindow(Level level) {
        pos = new Point(0, 0);
        this.level = level;
    }

    public Point screen2Block(Point point) {
        return new Point((point.x + pos.x) / 32, (point.y + pos.y) / 32);
    }

    public Point world2Block(Point point) {
        return new Point(point.x / 32, point.y / 32);
    }

    public Point block2World(Point point) {
        return new Point(point.x * 32, point.y * 32);
    }

    public Point block2Screen(Point point) {
        return new Point(point.x * 32 - pos.x, point.y * 32 - pos.y);
    }

    public void setPoint(Point point) {
        pos.move(point.x, point.y);
        if (pos.x < 0) pos.x = 0;
        if (pos.y < 0) pos.y = 0;
        
        int i = level.getWidth() * 32 - Game.xlen;
        if (pos.x >= i) pos.x = i - 1;
        i = level.getHeight() * 32 - Game.ylen;
        if (pos.y >= i) pos.y = i - 1;
    }

    public Point getPos() {
        return pos;
    }
}
