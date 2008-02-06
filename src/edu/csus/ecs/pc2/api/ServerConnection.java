package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.implementation.Contest;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;

/**
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 * <p>
 * This class represents a connection to a PC<sup>2</sup> server. Instantiating the class creates a local {@link ServerConnection} object which can then be used to connect to the PC<sup>2</sup>
 * server via the {@link ServerConnection#login(String, String)} method. The PC<sup>2</sup> server must already be running, and the local client must have a <code>pc2v9.ini</code> file specifying
 * valid server connection information, prior to invoking {@link ServerConnection#login(String, String)} method.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ServerConnection {

    private InternalController controller;

    private IInternalContest internalContest;

    private Contest contest = null;

    /**
     * Construct a local {@link ServerConnection} object which can subsequently be used to connect to a currently-running PC<sup>2</sup> server.
     * 
     */
    public ServerConnection() {
        super();
    }

    /**
     * Login to the PC<sup>2</sup> server represented by this ServerConnection using the specified login account name and password. If the login is successful, the method returns an
     * {@link edu.csus.ecs.pc2.api.IContest} object which can then be used to obtain information about the contest being controlled by the server. If the login fails the method throws
     * {@link edu.csus.ecs.pc2.api.exceptions.LoginFailureException}, in which case the message contained in the exception can be used to determine the nature of the login failure.
     * <P>
     * Note that invoking {@link ServerConnection#login(String, String)} causes an attempt to establish a network connection to a PC<sup>2</sup> server using the connection information specified in
     * the <code>pc2v9.ini</code> file in the current directory. The PC<sup>2</sup> server must <I>already be running</i> prior to invoking {@link ServerConnection#login(String, String)}, and
     * the <code>pc2v9.ini</code> must specify legitmate server connection information; otherwise, {@link edu.csus.ecs.pc2.api.exceptions.LoginFailureException} is thrown. See the PC<sup>2</sup>
     * Contest Administrator's Guide for information regarding specifying server connection information in <code>pc2v9.ini</code> files.
     * <P>
     * The following code snippet shows typical usage for connecting to and logging in to a server. <A NAME="loginsample"></A>
     * 
     * <pre>
     * String login = &quot;team4&quot;;
     * String password = &quot;team4&quot;;
     * try {
     *     ServerConnection serverConnection = new ServerConnection();
     *     IContest contest = serverConnection.login(login, password);
     *     //... code here to invoke methods in &quot;contest&quot;;
     *     serverConnection.logoff();
     * } catch (LoginFailureException e) {
     *     System.out.println(&quot;Could not login because &quot; + e.getMessage());
     * }
     * </pre>
     * 
     * @param login
     *            client login name (for example: &quot;team5&quot; or &quot;judge3&quot;)
     * @param password
     *            password for the login name
     * @throws LoginFailureException
     *             if login fails, the message contained in the exception will provide and indication of the reason for the failure.
     */
    public IContest login(String login, String password) throws LoginFailureException {

        internalContest = new InternalContest();
        controller = new InternalController(internalContest);

        controller.setUsingMainUI(false);
        try {
            controller.start(new String[0]);
            internalContest = controller.clientLogin(login, password);

            contest = new Contest(internalContest);

            return contest;

        } catch (Exception e) {
            throw new LoginFailureException(e.getMessage());
        }
    }

    /**
     * Logoff/disconnect from the PC<sup>2</sup> server.
     * 
     * @return true if logged off, else false.
     */
    public boolean logoff() {
        try {
            if (controller != null) {
                controller.logoffUser(internalContest.getClientId());
                contest.setLoggedIn(false);
                return true;
            }
        } catch (Exception e) {
            // TODO print exception ??
            return false;
        }
        return false;
    }

}
