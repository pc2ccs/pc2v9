package edu.csus.ecs.pc2.api.exceptions;

/**
 * Signals that an attempt has been made to call a method when a client is not logged in.
 * 
 * @see edu.csus.ecs.pc2.api.ServerConnection#getMyClient()
 * 
 * <P>
 * Information regarding the specific type of failure is accessible using the standard
 * <code>getMessage()</code> method associated with the Exception. 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotLoggedInException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -4748257040536583989L;

    public NotLoggedInException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public NotLoggedInException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public NotLoggedInException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public NotLoggedInException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
