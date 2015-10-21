package edu.csus.ecs.pc2.core.execute;

/**
 * Exception in attempt to setup or execute a run.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExecuteException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 4121203215386723703L;
 
    public ExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecuteException(String message) {
        super(message);
    }

    

}
