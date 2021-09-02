package litecom;

import java.io.OutputStream;
//import java.io.PrintStream;

public class Trace extends OutputStream {

    static {
        new Trace();
    }

    public static Trace currentInstance;
    private static final String traceID = "[TRACE]";
    public static boolean killOutput = false;
    public static int indentFilter = -1;
    public static final boolean t = false;
    private static int currIndent = 0;
    private String outBuffer;

    public Trace() {
        outBuffer = "";
        currentInstance = this;
    }

    public static void outdent() {
        currIndent--;
    }

    public static void indent() {
        currIndent++;
    }

    public static void outdent(Object obj, String s) {
        outdent();
        out(obj, s);
    }

    public static void outdent(String s) {
        outdent();
        out(s);
    }

    private static String makeIndent() {
        String s = " ";
        for (int i = 0; i < currIndent; i++)
            s += "   ";

        return s;
    }

    public static void indent(Object obj, String s) {
        out(obj, s);
        indent();
    }

    public static void indent(String s) {
        out(s);
        indent();
    }

    @Override
	public void write(int i) {
        String s;
        if (i == 10)
            s = "\r\n" + traceID + makeIndent();
        else
            s = "" + (char)i;
        outBuffer += s;
        if (i == 10) {
            System.out.print(outBuffer);
            outBuffer = "";
        }
    }

    private static String makeClassName(Object obj) {
        String s = obj.getClass().getName();
        return s.substring(s.lastIndexOf('.') + 1);
    }

    public static void out(Object obj, String s) {
        print(makeClassName(obj), s);
    }

    public static void out(String s) {
        print("static", s);
    }

    private static void print(String s, String s1) {
        if (killOutput)
            return;
        if (indentFilter != -1 && currIndent >= indentFilter) {
            return;
        } else {
            System.out.print(traceID + makeIndent() + s1 + " (" + s + ")\r\n");
            return;
        }
    }
}
