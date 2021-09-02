package game;

import java.awt.Graphics;
import java.awt.Point;

public class TilePanner {

    private Level level;
    private WorldWindow worldWindow;
    private Point oldPos;
    private boolean debugSolid;
    private Point clipMin;
    private Point clipMax;

    public TilePanner() {
        oldPos = null;
        debugSolid = false;
        clipMin = new Point(0, 0);
        clipMax = new Point(0, 0);
    }

    public TilePanner(Level level, WorldWindow worldWindow) {
        oldPos = null;
        debugSolid = false;
        clipMin = new Point(0, 0);
        clipMax = new Point(0, 0);
        this.level = level;
        this.worldWindow = worldWindow;
    }

    public void paint(Graphics g) {
        if (oldPos == null) {
            fullPaint(g);
            oldPos = new Point(0, 0);
        } else {
            repaint(g);
        }
        blitGameArea(g);
        if (debugSolid)
        	shadeSolidTiles(g);
        
        Point point = worldWindow.getPos();
        oldPos.move(point.x, point.y);
    }

    public void repaint(Graphics g) {
        int x1 = Game.xlen;
        int y1 = Game.ylen;
        
        if (oldPos == null)
            throw new RuntimeException("no paint before repaint");
        Point point = worldWindow.getPos();
        int x2 = oldPos.x - point.x;
        int y2 = oldPos.y - point.y;
        if (Math.abs(x2) > x1 * 0.8 || Math.abs(y2) > y1 * 0.8) {
            fullPaint(g);
            return;
        }
        g.copyArea(x2 >= 0 ? 0 : -x2, y2 >= 0 ? y1 : y1 - y2, x1 - Math.abs(x2), y1 - Math.abs(y2), x2, y2);
        if (x2 < 0) {
            clipMin.x = x1 + x2;
            clipMin.y = y2 <= 0 ? 0 : y2 - 1;
            clipMax.x = x1 - 1;
            clipMax.y = y2 >= 0 ? y1 - 1 : (y1 + y2) - 1;
            Point point1 = worldWindow.screen2Block(new Point(x1 + x2, 0));
            Point point2 = worldWindow.block2Screen(point1);
            int xx = -x2 / 32 + 1;
            int yy = y1 / 32 + 2;
            if (point1.x != worldWindow.screen2Block(new Point(x1 - 1, 0)).x)
                xx++;
            int px = point2.x;
            for (int i = 0; i < yy; i++) {
                point2.x = px;
                for (int j = 0; j < xx; j++) {
                    blitTile(level.getBlock(point1.x + j, point1.y + i), point2.x, point2.y, g);
                    point2.x += 32;
                }
                point2.y += 32;
            }
        } else if (x2 > 0) {
            clipMin.x = 0;
            clipMin.y = y2 <= 0 ? 0 : y2 - 1;
            clipMax.x = x2 - 1;
            clipMax.y = y2 >= 0 ? y1 - 1 : (y1 + y2) - 1;
            Point point1 = worldWindow.screen2Block(new Point(0, 0));
            Point point2 = worldWindow.block2Screen(point1);
            int xx = x2 / 32 + 1;
            int yy = y1 / 32 + 2;
            if (point1.x != worldWindow.screen2Block(new Point(x2, 0)).x)
                xx++;
            int px = point2.x;
            for (int i = 0; i < yy; i++) {
                point2.x = px;
                for (int j = 0; j < xx; j++) {
                    blitTile(level.getBlock(point1.x + j, point1.y + i), point2.x, point2.y, g);
                    point2.x += 32;
                }
                point2.y += 32;
            }
        }
        if (y2 < 0) {
            clipMin.x = 0;
            clipMin.y = (y1 + y2) - 1;
            clipMax.x = x1 - 1;
            clipMax.y = y1 - 1;
            Point point1 = worldWindow.screen2Block(new Point(0, y1 + y2));
            Point point2 = worldWindow.block2Screen(point1);
            int xx = x1 / 32 + 2;
            int yy = -y2 / 32 + 1;
            if (point1.y != worldWindow.screen2Block(new Point(0, y1 - 1)).y)
                yy++;
            int px = point2.x;
            for (int i = 0; i < yy; i++) {
                point2.x = px;
                for (int j = 0; j < xx; j++) {
                    blitTile(level.getBlock(point1.x + j, point1.y + i), point2.x, point2.y, g);
                    point2.x += 32;
                }
                point2.y += 32;
            }
        } else if (y2 > 0) {
            clipMin.x = 0;
            clipMin.y = 0;
            clipMax.x = x1 - 1;
            clipMax.y = y2;
            Point point1 = worldWindow.screen2Block(new Point(0, 0));
            Point point2 = worldWindow.block2Screen(point1);
            int xx = x1 / 32 + 2;
            int yy = y2 / 32 + 1;
            if (point1.y != worldWindow.screen2Block(new Point(0, y2)).y)
                yy++;
            int px = point2.x;
            for (int i = 0; i < yy; i++) {
                point2.x = px;
                for (int j = 0; j < xx; j++) {
                    blitTile(level.getBlock(point1.x + j, point1.y + i), point2.x, point2.y, g);
                    point2.x += 32;
                }
                point2.y += 32;
            }
        }
    }

    private void blitTile(Block block, int i, int j, Graphics g) {
        if (block == null)
            return;
        if (i > clipMax.x || j > clipMax.y)
            return;
        if ((i + 32) - 1 < clipMin.x || (j + 32) - 1 < clipMin.y)
            return;
        int x = block.tile.x;
        int y = block.tile.y;
        int width = 32;
        int height = 32;
        if (clipMin.x != 0 && i < clipMin.x) {
            i = clipMin.x - i;
            x += i;
            width -= i;
            i = clipMin.x;
        }
        if ((i + width) - 1 > clipMax.x)
            width -= (i + width) - 1 - clipMax.x;
        if (j < clipMin.y) {
            j = clipMin.y - j;
            y += j;
            height -= j;
            j = clipMin.y;
        }
        if ((j + height) - 1 > clipMax.y)
            height -= (j + height) - 1 - clipMax.y;
        j += Game.ylen;
        g.copyArea(x, y, width, height, i - x, j - y);
    }

    private void blitGameArea(Graphics g) {
        g.copyArea(0, Game.ylen, Game.xlen, Game.ylen, 0, -Game.ylen);
    }

    public void fullPaint(Graphics g) {
        Point point = worldWindow.getPos();
        Point point1 = worldWindow.world2Block(point);
        int i = -(point.x % 32);
        int k = -(point.y % 32);
        for (int l = 0; l <= Game.xlen / 32 + 1; l++) {
            for (int i1 = 0; i1 <= Game.ylen / 32 + 1; i1++) {
                Block block = level.getBlock(point1.x + l, point1.y + i1);
                if (block != null) {
                    int j1 = block.tile.x;
                    int k1 = block.tile.y;
                    int l1 = l * 32 + i;
                    int i2 = i1 * 32 + k + Game.ylen;
                    int j2 = 32;
                    if (i2 + 32 >= Game.ylen * 2)
                        j2 = Game.ylen * 2 - i2;
                    else if (i2 < Game.ylen) {
                        i2 -= Game.ylen;
                        j2 = 32 + i2;
                        k1 -= i2;
                        i2 = Game.ylen;
                    }
                    g.copyArea(j1, k1, 32, j2, l1 - j1, i2 - k1);
                }
            }
        }
    }

    public void toggleDebugSolid() {
        debugSolid = !debugSolid;
    }

    public void shadeSolidTiles(Graphics g) {
        g = g.create(0, 0, Game.xlen, Game.ylen);
        Point point = worldWindow.getPos();
        Point point1 = worldWindow.world2Block(point);
        int i = -(point.x % 32);
        int k = -(point.y % 32);
        for (int l = 0; l <= Game.xlen / 32 + 1; l++) {
            for (int i1 = 0; i1 <= Game.ylen / 32 + 1; i1++) {
                Block block = level.getBlock(point1.x + l, point1.y + i1);
                if (block != null && (block.isSolid() || block.getNoSprites() > 0)) {
                    int j1 = l * 32 + i;
                    int k1 = i1 * 32 + k;
                    g.drawImage(ImageManager.getImage("tiles/solidShade.gif"), j1, k1, null);
                }
            }
        }
    }
}
