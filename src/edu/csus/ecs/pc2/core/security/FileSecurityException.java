package edu.csus.ecs.pc2.core.security;

/**
 * 
 * @author pc2@ecs.csus.edu
 *
 */

public class FileSecurityException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 7132828387770550086L;

    public FileSecurityException() {
        super();
    }

    public FileSecurityException(String message) {
        super(message);
    }

    public FileSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileSecurityException(Throwable cause) {
        super(cause);
    }
}
