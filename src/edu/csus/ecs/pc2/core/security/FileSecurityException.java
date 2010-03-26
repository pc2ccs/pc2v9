package edu.csus.ecs.pc2.core.security;

/**
 * Exception for File Security class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: FileSecurity.java 1953 2009-11-15 01:26:06Z laned $
 */

//$HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/model/IInternalContest.java $
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
