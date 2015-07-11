/**
 * 
 */
package edu.csus.ecs.pc2.core;

import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.packet.PacketType.Type;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * Test ContestLoader class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestLoaderTest extends TestCase {

    private ContestLoader loader = new ContestLoader();

    private final ClientId serverId = new ClientId(1, ClientType.Type.SERVER, 0);

    private SampleContest sample = new SampleContest();

    private Account[] generateNewAccounts(String clientTypeName, int count, int startNumber, int siteNumber, boolean active) {
        ClientType.Type type = ClientType.Type.valueOf(clientTypeName.toUpperCase());
        AccountList accountList = new AccountList();
        Vector<Account> newAccounts = accountList.generateNewAccounts(type, count, startNumber, PasswordType.JOE, siteNumber, true);
        return (Account[]) newAccounts.toArray(new Account[newAccounts.size()]);
    }

    private Packet createPacket(String key, Object value) {

        Properties prop = new Properties();
        prop.put(key, value);

        Packet packet = new Packet(Type.UPDATE_CLIENT_PROFILE, serverId, PacketFactory.ALL_SERVERS, prop);
        return packet;
    }

    /**
     * Test method for
     * {@link edu.csus.ecs.pc2.core.ContestLoader#addAllAccountsToModel(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController, edu.csus.ecs.pc2.core.packet.Packet)}.
     */
    public void testAddAllAccountsToModel() {

        int numberAccounts = 12;

        IInternalContest contest = sample.createContest(2, 5, 0, 12, true);
        IInternalController controller = sample.createController(contest, true, false);

        ClientType.Type type = ClientType.Type.TEAM;
        Account[] list = generateNewAccounts(type.toString(), numberAccounts, 1, 1, true);
        Packet packet = createPacket(PacketFactory.ACCOUNT_ARRAY, list);

        loader.addAllAccountsToModel(contest, controller, packet);

        assertEquals("Should have added same number of accounts ", numberAccounts, contest.getAccounts(type).size());
    }

    // public void testAddAllClarificationsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testAddAllClientSettingsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testAddAllConnectionIdsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testAddAllContestTimesToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // /**
    // * Test method for
    // * {@link edu.csus.ecs.pc2.core.ContestLoader#addAllRunsToModel(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController, edu.csus.ecs.pc2.core.packet.Packet)}.
    // */
    // public void testAddAllRunsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testAddBalloonSettingsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testAddContestInformationToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }

    public void testAddGeneralProblemToModel() {

        IInternalContest contest = sample.createContest(2, 5, 0, 12, true);
        IInternalController controller = sample.createController(contest, true, false);

        String name = "General";
        Problem problem = new Problem(name);
        Packet packet = createPacket(PacketFactory.GENERAL_PROBLEM, problem);

        loader.addGeneralProblemToModel(contest, controller, packet);

        assertNotNull("Should have added general problem ", contest.getGeneralProblem());
        String generalName = contest.getGeneralProblem().getDisplayName();

        assertEquals("Should have added general problem with same name ", name, generalName);
    }

    // /**
    // * Test method for
    // * {@link edu.csus.ecs.pc2.core.ContestLoader#addGroupsToModel(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController, edu.csus.ecs.pc2.core.packet.Packet)}.
    // */
    // public void testAddGroupsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // /**
    // * Test method for
    // * {@link edu.csus.ecs.pc2.core.ContestLoader#addJudgementsToModel(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController, edu.csus.ecs.pc2.core.packet.Packet)}.
    // */
    // public void testAddJudgementsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // /**
    // * Test method for
    // * {@link edu.csus.ecs.pc2.core.ContestLoader#addLanguagesToModel(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController, edu.csus.ecs.pc2.core.packet.Packet)}.
    // */
    // public void testAddLanguagesToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // /**
    // * Test method for
    // * {@link edu.csus.ecs.pc2.core.ContestLoader#addLoginsToModel(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController, edu.csus.ecs.pc2.core.packet.Packet)}.
    // */
    // public void testAddLoginsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }

    /**
     * Test method for
     * {@link edu.csus.ecs.pc2.core.ContestLoader#addProblemsToModel(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController, edu.csus.ecs.pc2.core.packet.Packet)}.
     */
    public void testAddProblemsToModel() {

        IInternalContest contest = sample.createContest(3, 5, 0, 12, true);
        IInternalController controller = sample.createController(contest, true, false);

        String[] names = { "Sumit2", "Quadrangles2", "Routing2", "Faulty Towers2" };

        int originalNumber = contest.getProblems().length;

        int newNumber = names.length + originalNumber;

        Problem[] list = new Problem[names.length];
        int i = 0;
        for (String name : names) {
            list[i++] = new Problem(name);
        }

        Packet packet = createPacket(PacketFactory.PROBLEM_LIST, list);
        loader.addProblemsToModel(contest, controller, packet);

        assertEquals("Should be N problems  ", newNumber, contest.getProblems().length);
    }

    // /**
    // * Test method for
    // * {@link edu.csus.ecs.pc2.core.ContestLoader#addProfilesToModel(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController, edu.csus.ecs.pc2.core.packet.Packet)}.
    // */
    // public void testAddProfilesToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testAddRemoteAccountsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testAddRemoteAllClientSettingsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testAddRemoteClarificationsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testAddRemoteContestTimesToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testAddRemoteLoginsToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }

    public void testAddRemoteRunsToModel() throws IOException, ClassNotFoundException, FileSecurityException {

        int siteNumber = 4;
        IInternalContest contest = sample.createContest(siteNumber, 5, 0, 12, true);
        assertEquals("expecting site ", siteNumber, contest.getSiteNumber());
        
        IInternalController controller = sample.createController(contest, true, false);

        ClientType.Type type = ClientType.Type.TEAM;
        Account[] list = generateNewAccounts(type.toString(), 22, 1, siteNumber, true);
        for (Account account : list) {
            contest.addAccount(account);
        }
        list = generateNewAccounts(type.toString(), 22, 1, 2, true);
        for (Account account : list) {
            contest.addAccount(account);
        }
         
        int numRuns = 12;

        Run[] remoteRuns = sample.createRandomRuns(contest, numRuns, true, true, true, 2);
        for (Run run : remoteRuns) {
            assertEquals("Remote run site ", 2, run.getSiteNumber());
        }
        assertEquals("Should be runs ", numRuns, remoteRuns.length);
        
        Packet packet = createPacket(PacketFactory.RUN_LIST, remoteRuns);

        loader.addRemoteRunsToModel(contest, controller, packet);
        
        assertEquals("expecting site ", siteNumber, contest.getSiteNumber());
        assertEquals("Should be N remote runs ", numRuns, contest.getRuns().length);

        // Add local runs

        Run[] localRuns = sample.createRandomRuns(contest, 12, true, true, true, contest.getSiteNumber());
        for (Run run : localRuns) {
            contest.addRun(run);
        }

        assertEquals("Should be N runs ", numRuns * 2, contest.getRuns().length);
    }

    // /**
    // * Test method for
    // * {@link edu.csus.ecs.pc2.core.ContestLoader#addSitesToModel(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController, edu.csus.ecs.pc2.core.packet.Packet)}.
    // */
    // public void testAddSitesToModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testInitializeContestTime() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testUpdateContestTimeInModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // public void testLoadDataIntoModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }
    //
    // /**
    // * Test method for
    // * {@link edu.csus.ecs.pc2.core.ContestLoader#setProfileIntoModel(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController, edu.csus.ecs.pc2.core.packet.Packet)}.
    // */
    // public void testSetProfileIntoModel() {
    // fail("Not yet implemented"); // SOMEDAY code this JUnit
    // }

}
