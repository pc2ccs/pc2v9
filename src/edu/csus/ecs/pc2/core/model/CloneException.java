package edu.csus.ecs.pc2.core.model;

/**
 * Exception during clone or switch of profile.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class CloneException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -4046621347201691994L;
    
    public CloneException(String message, Throwable cause) {
        super(message, cause);
    }
}
