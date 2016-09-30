package edu.csus.ecs.pc2.core.exception;


/**
 * Exception during YAML parse or load.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

public class YamlLoadException extends RuntimeException {

    private static final long serialVersionUID = -1743376475305165103L;
    
    private String filename;

    public YamlLoadException() {
        super();
    }

    public YamlLoadException(String message, Throwable cause, String filename) {
        super(message, cause);
        this.filename = filename;
    }
    
    public YamlLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public YamlLoadException(String message) {
        super(message);
    }

//    public YamlLoadException(Throwable cause) {
//        super(cause);
//    }
    
    public String getFilename() {
        return filename;
    }
}
