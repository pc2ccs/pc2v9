package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.implementation.Contest;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;

/**
 * Methods to make requests of server.
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
     * Login/Authenticate into the contest (server).
     * 
     * The exception message will indicate the nature of the login failure.
     * <P>
     * Code snippet for login and logoff.
     * 
     * <pre>
     * 
     * String login = &quot;team4&quot;;
     * String password = &quot;team4&quot;;
     * try {
     *     ServerConnection serverConnection = new ServerConnection();
     *     IContest contest = serverConnection.login(login, password);
     *     System.out.println(&quot;Logged in as &quot; + contest.getClient().getTitle());
     *     System.out.println(&quot;Number of runs &quot; + contest.getRuns().length);
     *     serverConnection.logoff();
     * 
     * } catch (LoginFailureException e) {
     *     System.out.println(&quot;Could not login because &quot; + e.getMessage());
     *     e.printStackTrace();
     * }
     * 
     * </pre>
     * 
     * @param login
     *            client login name (ex. team5, judge3)
     * @param password
     *            password for the login name
     * @throws LoginFailureException
     *             if login failure, message indicating why it failed.
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
     * Loggoff/disconnect from server.
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
