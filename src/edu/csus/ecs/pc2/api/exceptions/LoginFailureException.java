package edu.csus.ecs.pc2.api.exceptions;

/**
 * This class defines the Exception which is thrown when a failure occurs during
 * a client's attempt to connect to a PC<sup>2</sup> server through the PC<sup>2</sup> API.
 * <P>
 * There are many conditions which can cause connection/login failure, including but not limited
 * to 
 * <ul>
 *      <li> lack of proper configuration information identifying the network connection to
 *              be used in contacting the server; </li>
 *      <li> inability of the underlying PC<sup>2</sup> transport
 *              mechanism to accomplish the network connection; </li>
 *      <li> incorrect authentication (login name/password) credentials. </li>
 * </ul>
 * <P>
 * Information regarding the specific type of failure is accessible using the standard
 * <code>getMessage()</code> method associated with the Exception. 
 * 
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LoginFailureException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -6675979618354284924L;

    public LoginFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginFailureException(Throwable cause) {
        super(cause);
    }

    public LoginFailureException(String message) {
        super(message);
    }
    

}
