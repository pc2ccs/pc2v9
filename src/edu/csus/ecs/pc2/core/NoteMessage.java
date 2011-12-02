package edu.csus.ecs.pc2.core;

import java.io.Serializable;

/**
 * A single error message or information message.
 * 
 * Intended for use for syntax errors in files.
 * 
 * @see NoteList
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: NoteMessage.java 205 2011-06-30 02:49:12Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/core/NoteMessage.java $
public class NoteMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4288527152786522644L;

    private String filename;

    private int lineNumber;

    private int columnNumber;

    private Type type;

    private String comment;

    private Exception exception;

    /**
     * A set of note types.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id: NoteMessage.java 205 2011-06-30 02:49:12Z laned $
     */

    // $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/core/NoteMessage.java $
    public enum Type {
        /**
         * General information.
         */
        INFORMATION,
        /**
         * Problem was detected but ignored.
         */
        IGNORED,
        /**
         * Summary info.
         */
        SUMMARY,
        /**
         * Warning.
         */
        WARNING,
        /**
         * Error.
         */
        ERROR,
    }

    public NoteMessage(Type type, String filename, int lineNumber, String comment, int columnNumber, Exception exception) {
        super();
        this.filename = filename;
        this.lineNumber = lineNumber;
        this.type = type;
        this.comment = comment;
        this.exception = exception;
        this.columnNumber = columnNumber;
    }

    public NoteMessage(Type type, String filename, int lineNumber, String comment) {
        this(type, filename, lineNumber, comment, 0, null);
    }

    public NoteMessage(Type type, String filename, int lineNumber, String comment, Exception exception) {
        this(type, filename, lineNumber, comment, 0, exception);
    }

    public String getFilename() {
        return filename;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public Type getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }

    public Exception getException() {
        return exception;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

}
