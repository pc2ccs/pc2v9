package edu.csus.ecs.pc2;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;

/**
 * Methods to make requests of server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Controller implements IController {

    private Contest contest = null;

    /**
     * Create a controller based on the contest.
     * 
     * @param contest
     */
    public Controller(Contest contest) {
        super();
        this.contest = contest;
    }

    /**
     * Login/Authenticate into the contest (server).
     * 
     * The exception message will indicate the nature of the login failure.
     * 
     * @see IContest#isLoggedIn()
     * 
     * @param login
     *            client login name (ex. team5, judge3)
     * @param password
     *            password for the login name
     * @throws LoginFailureException
     *             if login failure, message indicating why it failed.
     */
    public static Contest login(String login, String password) throws LoginFailureException {
        // TODO code
        return null;
    }

    public boolean logoff() {
        // TODO code
        return true;
    }

}
