package game;

import java.io.IOException;
import java.util.Hashtable;
import litecom.gfxe.Loader;
import litecom.Trace;

public class TilesetManager {

    private static Hashtable<String, TileSet> tilesetHastable = new Hashtable<String, TileSet>();
    private static Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();

    public static TileSet getTileset(String tilesetName) throws IOException {
        return getTileset(tilesetName, null);
    }

    public static TileSet getTileset(String tilesetName, Loader thread) throws IOException {
        TileSet tileManager = tilesetHastable.get(tilesetName);
        if (tileManager == null) {
            Trace.out("cache miss for " + tilesetName);
            int i = thread == null ? 0 : thread.getProgress();
            tileManager = new TileSet(tilesetName, thread);
            createTilesetHashtable();
            tilesetHastable.put(tilesetName, tileManager);
            if(thread != null)
                hashtable.put(tilesetName, new Integer(thread.getProgress() - i));
        } else {
            if (thread != null) {
                int j = hashtable.get(tilesetName).intValue();
                thread.progress(j);
            }
            Trace.out("cache hit for " + tilesetName);
        }
        return tileManager;
    }

    public static void createTilesetHashtable() {
        tilesetHastable = new Hashtable<String, TileSet>();
    }
}
