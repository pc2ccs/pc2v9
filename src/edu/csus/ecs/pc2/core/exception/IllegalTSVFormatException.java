/**
 * 
 */
package edu.csus.ecs.pc2.core.exception;


/**
 * Exceptoni for parsing tab delimited lines.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class IllegalTSVFormatException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3975359538185399401L;

    public IllegalTSVFormatException(String message) {
        super(message);
    }

}
