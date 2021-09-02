package game;

import java.io.IOException;
import java.util.Hashtable;
import litecom.Trace;
import litecom.gfxe.Loader;

public class TileSetCache {

    private static Hashtable<String, TileSet> cache = new Hashtable<String, TileSet>();
    private static Hashtable<String, Integer> loaderSteps = new Hashtable<String, Integer>();

    public static TileSet get(String s) throws IOException {
        return get(s, null);
    }

    public static TileSet get(String s, Loader loader) throws IOException {
        TileSet tileset = (TileSet)cache.get(s);
        if (tileset == null) {
            Trace.out("cache miss for " + s);
            int i = loader == null ? 0 : loader.getProgress();
            tileset = new TileSet(s, loader);
            reset();
            cache.put(s, tileset);
            if(loader != null)
                loaderSteps.put(s, new Integer(loader.getProgress() - i));
        } else {
            if (loader != null) {
                int j = ((Integer)loaderSteps.get(s)).intValue();
                loader.progress(j);
            }
            Trace.out("cache hit for " + s);
        }
        return tileset;
    }

    public static void reset() {
        cache = new Hashtable<String, TileSet>();
    }
}
