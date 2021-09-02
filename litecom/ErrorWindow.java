package litecom;

import java.awt.*;
import java.io.PrintStream;

public class ErrorWindow extends Frame {
	
	private static final long serialVersionUID = -8313941886835597261L;
    public static boolean active = false;

	public ErrorWindow(Throwable throwable, String s) {
        super("Error - " + s);
        while (active) 
            return;
        setLayout(new GridLayout(5, 1));
        add(new Label("An error has occured!"));
        add(new Label("Error message: " + s));
        add(new Label("Exception: " + throwable));
        add(new Label("Exception message: " + throwable.getMessage()));
        add(new Label("Please report this to maciek@litecom.se."));
        throwable.printStackTrace(new PrintStream(DebugWriter.debugWriter, true));
        setSize(300, 115);
        setVisible(true);
        active = true;
    }

    @Override
	public boolean handleEvent(Event event) {
    	if (event.id == 201) { // Event.WINDOW_DESTROY
    		setVisible(false);
            active = false;
            return true;
    	}
    	return false;
    }
}
