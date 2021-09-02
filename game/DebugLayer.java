package game;

import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

public class DebugLayer {

	public static final int TYPE_LINE = 0;
    public static final int TYPE_STRING = 1;
    public static final int TYPE_PRINT = 2;
    public static final int TYPE_ARROW = 3;
    private Vector<DebugLayerEntry> entries;
    private WorldWindow worldWindow;
    private int printerX;
    private int printerY;
    private static DebugLayer reference;

    public DebugLayer() {
        entries = new Vector<DebugLayerEntry>();
        printerX = 5;
        printerY = 15;
    }

    public DebugLayer(WorldWindow worldWindow) {
        entries = new Vector<DebugLayerEntry>();
        printerX = 5;
        printerY = 15;
        this.worldWindow = worldWindow;
        reference = this;
    }

    public static DebugLayer getReference() {
        return reference;
    }

    private void reset() {
        entries.removeAllElements();
        printerX = 5;
        printerY = 15;
    }

    private void draw(Graphics g, DebugLayerEntry debugLayerEntry) {
        Point point = worldWindow.getPos();
        g.setColor(debugLayerEntry.color);
        switch(debugLayerEntry.type) {
        case TYPE_LINE:
            g.drawLine((int)(debugLayerEntry.x1 - point.x), (int)(debugLayerEntry.y1 - point.y), (int)(debugLayerEntry.x2 - point.x), (int)(debugLayerEntry.y2 - point.y));
            return;

        case TYPE_ARROW:
            float f = debugLayerEntry.x1 - point.x;
            float f1 = debugLayerEntry.y1 - point.y;
            float f2 = debugLayerEntry.x2 - point.x;
            float f3 = debugLayerEntry.y2 - point.y;
            float f4 = f2 - f;
            float f5 = f3 - f1;
            float f6 = (float)Math.atan2(f5, f4);
            g.drawLine((int)f, (int)f1, (int)f2, (int)f3);
            g.drawLine((int)f2, (int)f3, (int)(f2 - Math.cos(f6 + 0.5D) * 5D), (int)(f3 - Math.sin(f6 + 0.5D) * 5D));
            g.drawLine((int)f2, (int)f3, (int)(f2 - Math.cos(f6 - 0.5D) * 5D), (int)(f3 - Math.sin(f6 - 0.5D) * 5D));
            return;

        case TYPE_STRING:
            g.drawString(debugLayerEntry.str, (int)(debugLayerEntry.x1 - point.x), (int)(debugLayerEntry.y1 - point.y));
            // fall through

        case TYPE_PRINT:
            g.drawString(debugLayerEntry.str, printerX, printerY);
            printerY += 10;
            return;

        default:
            return;
        }
    }

    public static void add(DebugLayerEntry.Type type, Color color, float x1, float y1, float x2, float y2) {
        DebugLayerEntry debugLayerEntry = new DebugLayerEntry(type, color);
        debugLayerEntry.x1 = x1;
        debugLayerEntry.y1 = y1;
        debugLayerEntry.x2 = x2;
        debugLayerEntry.y2 = y2;
        reference.entries.addElement(debugLayerEntry);
    }

    public static void add(DebugLayerEntry.Type type, Color color, String s, float x, float y) {
        DebugLayerEntry debugLayerEntry = new DebugLayerEntry(type, color);
        debugLayerEntry.x1 = x;
        debugLayerEntry.y1 = y;
        debugLayerEntry.str = s;
        reference.entries.addElement(debugLayerEntry);
    }

    public static void add(DebugLayerEntry.Type type, Color color, String s) {
        DebugLayerEntry debugLayerEntry = new DebugLayerEntry(type, color);
        debugLayerEntry.str = s;
        reference.entries.addElement(debugLayerEntry);
    }

    public void paint(Graphics g) {
        Graphics g1 = g.create(0, 0, Game.xlen, Game.ylen);
        Enumeration<DebugLayerEntry> enumeration = entries.elements();
        while (enumeration.hasMoreElements()) {
        	draw(g1, enumeration.nextElement());
        }
        reset();
    }
}
