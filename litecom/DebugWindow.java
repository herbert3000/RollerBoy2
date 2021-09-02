package litecom;

import java.awt.*;

class DebugWindow extends Frame {

	private static final long serialVersionUID = 1317930357194127296L;
    public TextArea textArea;

	public DebugWindow() {
        super("Debug");
        setLayout(new BorderLayout());
        textArea = new TextArea();
        textArea.setEditable(false);
        add("Center", textArea);
        setSize(500, 300);
        setVisible(true);
    }

    @Override
	public boolean handleEvent(Event event) {
    	if (event.id == 201) { // Event.WINDOW_DESTROY
    		DebugWriter.destroy();
    		return true;
    	}
    	return false;
    }
}
