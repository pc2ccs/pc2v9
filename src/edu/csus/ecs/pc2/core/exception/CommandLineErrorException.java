package edu.csus.ecs.pc2.core.exception;

/**
 * Error on Command Line.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class CommandLineErrorException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1108025892117854649L;

    public CommandLineErrorException(String message) {
        super(message);
    }

    public CommandLineErrorException(Throwable cause) {
        super(cause);
    }

    public CommandLineErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
