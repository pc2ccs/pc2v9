package edu.csus.ecs.pc2.profile;

/**
 * Exception loading profile information
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileLoadException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3395885302767402680L;

    public ProfileLoadException() {
        super();
    }

    public ProfileLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileLoadException(String message) {
        super(message);
    }

    public ProfileLoadException(Throwable cause) {
        super(cause);
    }

}
