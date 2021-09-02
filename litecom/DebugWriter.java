package litecom;

import java.applet.Applet;
import java.io.OutputStream;

public class DebugWriter extends OutputStream {

    static  {
        new DebugWriter();
    }

    public static boolean debug;
    //public static boolean U;
    private static boolean windowActive;
    public static DebugWindow debugWindow;
    public static DebugWriter debugWriter;
    private String string;

    public DebugWriter() {
        string = "";
        debugWriter = this;
    }

    public static void L(String s) {
        print(s);
    }

    @Override
	public void write(int i) {
        if (debug) {
            String s;
            if (i == 10 && !windowActive)
                s = "\r\n";
            else
                s = "" + (char)i;
            string += s;
            if (i == 10) {
                if (windowActive)
                    debugWindow.textArea.append(string);
                else
                    System.out.print(string);
                string = "";
            }
        }
    }

    public static void init(Applet applet) {
        if (applet.getParameter("debug") != null && applet.getParameter("debug").equals("true"))
            debug = true;
        else
            debug = true;
        if (debug && ("" + applet.getDocumentBase()).startsWith("http") && System.getProperty("browser") != null && System.getProperty("browser").startsWith("ActiveX"))
            createDebugWindow();
    }

    public static void destroy() {
        if (debugWindow != null)
            debugWindow.setVisible(false);
        windowActive = false;
    }

    public static void createDebugWindow() {
        if (debugWindow == null && !windowActive)
            debugWindow = new DebugWindow();
        windowActive = true;
    }

    public static void print(String text) {
        if (debug) {
            if (windowActive) {
                debugWindow.textArea.append(text + "\n");
                return;
            }
            System.out.print("[DEBUG] " + text + "\r\n");
        }
    }

    public static void print(Object obj, String s) {
        if (debug)
            print(obj.getClass().getName() + ": " + s);
    }
}
