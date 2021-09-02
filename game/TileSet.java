package game;

import java.awt.*;
import java.io.*;
import java.net.URL;
//import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.Vector;
import litecom.gfxe.Loader;
import litecom.gfxe.ImageSplitter;
import litecom.Trace;

public class TileSet {

    public static String baseDir = "tilesets/";
    private Vector<Vector<Tile>> tilesVector;
    private int width;
    private int height;
    private String tilesetName;
    private boolean _fld0118;
    private Point point;

    public TileSet() {
        tilesVector = new Vector<Vector<Tile>>();
        point = new Point(0, 0);
    }

    public TileSet(String tilesetName) throws IOException {
        this(tilesetName, null);
    }

    public TileSet(String tilesetName, Loader thread) throws IOException {
        tilesVector = new Vector<Vector<Tile>>();
        point = new Point(0, 0);
        this.tilesetName = tilesetName;
        readTileset(tilesetName, thread);
    }

    public Tile getTile(int j, int k, int l) {
        return getTile(l * width + k + j);
    }

    public Image createGameCanvas(int width, int height) {
        Trace.indent(this, "createGameCanvas(...)");
        Trace.out(this, "applXlen: " + width + ", applYlen: " + height);
        int numTiles = getNumTiles();
        Trace.out(this, "nTiles: " + numTiles);
        int w = width;
        int h = height * 2 + (numTiles / (w / 32) + 1) * 32;
        Trace.out(this, "canvas dim: " + w + "x" + h);
        Image image = Game.getReference().createImage(w, h);
        Graphics g = image.getGraphics();
        Trace.out(this, "adding tiles...");
        int x = 0;
        int y = height * 2;
        
        for (int i = 0; i < tilesVector.size(); i++) {
            Vector<Tile> vector = tilesVector.elementAt(i);
            for (int j = 0; j < vector.size(); j++) {
                Tile tile = vector.elementAt(j);
                if (tile.image != null) {
                    g.drawImage(tile.image, x, y, null);
                }
                tile.H(x, y);
                if ((x += 32) + 32 >= w) {
                    x = 0;
                    y += 32;
                }
            }
        }
        
        Trace.out(this, "done");
        Trace.outdent(this, "createGameCanvas(...) done");
        return image;
    }

    private void parseLine(String line, Loader thread) {
        if (line.length() == 0) return;
        
        if (line.startsWith("PP:")) {
            line = line.substring(3);
            StringTokenizer st = new StringTokenizer(line, ", ");
            _fld0118 = !st.nextToken().equals("n");
            setPPGroup(Integer.parseInt(st.nextToken().trim()), Integer.parseInt(st.nextToken().trim()));
            return;
        }
        
        StringTokenizer st = new StringTokenizer(line, ", ");
        String token = st.nextToken().trim();
        Trace.indent(this, "Loading tiles from: " + token);
        ImageSplitter j = new ImageSplitter(Game.getReference(), token, 32, 32, 0, 0, null, false);
        if (thread != null)
            thread.progress(60);
        Image aimage[] = j.getImages();
        Dimension dimension = j.getSourceImageDimension();
        if (aimage.length == 0)
            throw new RuntimeException("Could not load " + token);
        Trace.out(this, "Got " + aimage.length + " images.");
        int k = dimension.width / 32;
        if (k > width)
            width = k;
        int l = height;
        int i1 = 0;
        tilesVector.addElement(new Vector<Tile>());
        for (int i = 0; i < aimage.length; i++) {
            String s;
            if (st.hasMoreTokens())
                s = st.nextToken().trim();
            else
                s = "p";
            char c = s.charAt(0);
            Tile tile = null;
            switch(c) {
            case 112: // 'p'
                tile = new Tile(aimage[i], false);
                break;

            case 115: // 's'
                tile = new Tile(aimage[i], true);
                break;

            case 111: // 'o'
                tile = new Tile(aimage[i], true, true);
                break;

            case 100: // 'd'
                tile = new Tile();
                break;

            default:
                throw new RuntimeException("Unknown tiletype '" + c + "'");
            }
            (tilesVector.elementAt(l)).addElement(tile);
            if (++i1 == k) {
                tilesVector.addElement(new Vector<Tile>());
                l++;
                i1 = 0;
                height++;
            }
        }
        
        getTile(0).bgTile = true;
        Trace.outdent(this, "Loaded " + aimage.length + " tiles, tileset size: " + getDimension());
    }

    public int indexOf(Tile tile) {
        for (int i = 0; i < tilesVector.size(); i++) {
            Vector<Tile> vector = tilesVector.elementAt(i);
            for (int j = 0; j < vector.size(); j++) {
                if (tile == vector.elementAt(j))
                    return i * width + j;
            }
        }
        return -1;
    }

    public void setPPGroup(int j, int k) {
        Trace.out(this, "Setting pp group at: " + j + ", " + k);
        int l = k * width + j;
        getTile(j + 3, k).S = l;
        getTile(j + 2, k + 1).S = l;
        getTile(j + 4, k + 1).S = l;
        getTile(j + 5, k + 1).S = l;
        getTile(j, k + 2).S = l;
        getTile(j + 1, k + 2).S = l;
        getTile(j + 3, k + 2).S = l;
        getTile(j + 5, k + 2).S = l;
        if (!_fld0118) {
            getTile(j + 1, k + 3).S = l;
            getTile(j + 1, k + 4).S = l;
            getTile(j + 2, k + 2).S = l;
        } else {
            getTile(j + 4, k + 5).S = l;
            getTile(j + 4, k + 6).S = l;
            getTile(j + 4, k + 3).S = l;
        }
        getTile(j + 2, k + 3).S = l;
        getTile(j + 6, k + 3).S = l;
        getTile(j + 2, k + 4).S = l;
        getTile(j + 3, k + 4).S = l;
        getTile(j + 5, k + 4).S = l;
    }

    public void readTileset(String tilesetName, Loader thread) throws IOException {
        this.tilesetName = tilesetName;
        if (tilesetName.indexOf('.') != -1 || tilesetName.indexOf('/') != -1 || tilesetName.indexOf('\\') != -1)
            throw new RuntimeException("TileSet name may not contain '.', '/' or '\\'.");
        String fullFilename = baseDir + tilesetName + ".tileset";
        if (Game.documentBase != null) {
            URL url = new URL(Game.documentBase, fullFilename);
            parseFile(new BufferedReader(new InputStreamReader(url.openConnection().getInputStream())), thread);
            return;
        } else {
            parseFile(new BufferedReader(new InputStreamReader(new FileInputStream(fullFilename))), thread);
            return;
        }
    }

	private void parseFile(BufferedReader bufferedReader, Loader thread) throws IOException {
        String line;
        while((line = bufferedReader.readLine()) != null) 
            parseLine(line.trim(), thread);
    }

    public String getName() {
        return tilesetName;
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    public Point _mth0107(int j) {
        if (j == 0) {
            point.x = 3;
            point.y = 0;
        }
        if (j == 1) {
            point.x = 2;
            point.y = 1;
        }
        if (j == 2) {
            point.x = 4;
            point.y = 1;
        }
        if (j == 3) {
            point.x = 5;
            point.y = 1;
        }
        if (j == 4) {
            point.x = 0;
            point.y = 2;
        }
        if (j == 5) {
            point.x = 1;
            point.y = 2;
        }
        if (j == 7) {
            point.x = 3;
            point.y = 2;
        }
        if (j == 9) {
            point.x = 5;
            point.y = 2;
        }
        if (!_fld0118) {
            if (j == 10) {
                point.x = 1;
                point.y = 3;
            }
            if (j == 13) {
                point.x = 1;
                point.y = 4;
            }
            if (j == 6) {
                point.x = 2;
                point.y = 2;
            }
        } else {
            if (j == 10) {
                point.x = 4;
                point.y = 5;
            }
            if (j == 13) {
                point.x = 4;
                point.y = 6;
            }
            if (j == 6) {
                point.x = 4;
                point.y = 3;
            }
        }
        if (j == 11) {
            point.x = 2;
            point.y = 3;
        }
        if (j == 12) {
            point.x = 6;
            point.y = 3;
        }
        if (j == 14) {
            point.x = 2;
            point.y = 4;
        }
        if (j == 15) {
            point.x = 3;
            point.y = 4;
        }
        if (j == 16) {
            point.x = 5;
            point.y = 4;
        }
        return point;
    }

    public int getNumTiles() {
        int j = 0;
        for (int k = 0; k < tilesVector.size(); k++)
            j += (tilesVector.elementAt(k)).size();

        return j;
    }

    public Tile getTile(int index) {
        return getTile(index % width, index / width);
    }

    public Tile getTile(int col, int row) {
        if (row < 0 || row >= height)
            return null;
        Vector<Tile> vector = tilesVector.elementAt(row);
        if (col < 0 || col >= vector.size())
            return null;
        else
            return vector.elementAt(col);
    }
}
