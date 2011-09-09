package edu.csus.ecs.pc2.imports.ccs;

/**
 * Invalid Value.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: InvalidValueException.java 181 2011-04-11 03:21:46Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/imports/ccs/InvalidValueException.java $
public class InvalidValueException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4113238683585425501L;

    public InvalidValueException() {
        super();
    }

    public InvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(Throwable cause) {
        super(cause);
    }

}
