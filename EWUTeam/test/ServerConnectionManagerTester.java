import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;

/**
 * Unit Tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ServerConnectionManagerTester {
    ServerConnectionManager scm;

    // TODO Find a way to test methods without server running.
    /**
     * Is server running.
     * 
     * Determines whether certain methods are called that reuqire a running server.
     * 
     * Set this to true if server running.
     */
    private boolean serverRunning = false;

    @Before
    public void setup() {
        scm = new ServerConnectionManager();
    }

    @After
    public void teardown() {
        scm = null;
    }

    // test: get nonexistent team
    // result: throw NotLoggedInException
    @Test(expected = NotLoggedInException.class)
    public void getTeamNotLoggedIn() throws NotLoggedInException {
        scm.getTeam("not a team");
    }

    // test: add a single team, then call getTeam
    // result: retrieved team is the same team that was added
    // requires: pc2 server running,
    // team created with username "team1" and password "team1"
    @Test
    public void getSingleTeamSuccess() {
        try {
            if (serverRunning) {
                scm.addTeam("1", "team1", "team1");
                ServerConnection sc = scm.getTeam("1");
                Assert.assertTrue(sc.getMyClient().getLoginName().equals("team1"));
            }
        } catch (LoginFailureException e) {
            Assert.fail("are you connected to the pc2 server?");
        } catch (NotLoggedInException e) {
            Assert.fail("are you connected to the pc2 server?");
        }
    }

    // test: add nonexistent team
    // result: throws LoginFailureException
    @Test(expected = LoginFailureException.class)
    public void addTeamFailure() throws LoginFailureException {
        scm.addTeam("1", "not a team", "not a password");
    }

    // test: add team successfully
    // result: no exceptions thrown
    // requires: pc2 server running
    // team with username "team1" and password "team1"
    @Test
    public void addTeamSuccess() {
        try {
            if (serverRunning) {
                scm.addTeam("1", "team1", "team1");
            }
        } catch (LoginFailureException e) {

            Assert.fail("login failure exception thrown");
        }
    }

    // test: remove nonexistent team
    // result: throw NotLoggedInException
    @Test(expected = NotLoggedInException.class)
    public void removeTeamFailure() throws NotLoggedInException {
        scm.removeTeam("not a key");
    }

    // test: remove existing team that is not logged in
    // result: throw NotLoggedInException
    // requires: pc2 server running
    // team with username "team1" and password "team1"
    @Test(expected = NotLoggedInException.class)
    public void removeTeamNotLoggedIn() throws NotLoggedInException {
        try {
            if (serverRunning) {
                scm.addTeam("1", "team1", "team1");
                scm.getTeam("1").logoff();
            }
        } catch (LoginFailureException e) {
            Assert.fail("Could not log team in");
        } catch (NotLoggedInException e) {
            Assert.fail("Could not get team");
        }

        scm.removeTeam("1");
    }

    // test: remove team
    // result: successfully remove/logoff team
    // requires: pc2 server running
    // team with username "team1" and password "team1"
    @Test
    public void removeTeamSuccess() {
        try {
            if (serverRunning) {
                scm.addTeam("1", "team1", "team1");
                scm.removeTeam("1");
            }

        } catch (LoginFailureException e) {
            Assert.fail("Could not log in");
        } catch (NotLoggedInException e) {
            Assert.fail("Could not log out");
        }
    }

}
