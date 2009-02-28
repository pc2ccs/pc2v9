package edu.csus.ecs.pc2.core.model.playback;

/**
 * Exception parsing a playback text input line.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PlaybackParseException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PlaybackParseException() {
        // TODO Auto-generated constructor stub
    }

    public PlaybackParseException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public PlaybackParseException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public PlaybackParseException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public PlaybackParseException(int lineNumber, String string) {
        super("Line "+lineNumber+": "+string);
    }

}
