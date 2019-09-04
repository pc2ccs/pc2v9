// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.exception;

/**
 * Exception while cloning profile.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileCloneException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ProfileCloneException() {
        super();
    }

    public ProfileCloneException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileCloneException(String message) {
        super(message);
    }

    public ProfileCloneException(Throwable cause) {
        super(cause);
    }
    

}
