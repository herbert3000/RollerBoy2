package game;

import java.awt.Color;

class DebugLayerEntry {

	public enum Type { TYPE_LINE, TYPE_STRING, TYPE_PRINT, TYPE_ARROW };
    public Type type;
    public Color color;
    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public String str;

    public DebugLayerEntry(Type type, Color color) {
        this.type = type;
        this.color = color;
    }
}
