package edu.csus.ecs.pc2.core;

import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ProblemComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Test for Notifications XML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationXMLTest extends TestCase {

    private final boolean debugMode = true;

    private IInternalContest contest = null;
    
    private SampleContest sample = null;

    private ClientId scoreboardClient;
    
    private ClientId judgeId = null;

    protected void setUp() throws Exception {
        super.setUp();

        sample = new SampleContest();
        contest = sample.createContest(1, 1, 22, 12, true);

        contest.generateNewAccounts(Type.SCOREBOARD.toString(), 2, true);
        ClientId scoreClientId = new ClientId(contest.getSiteNumber(), Type.SCOREBOARD, 2);
        Account account = contest.getAccount(scoreClientId);
        scoreboardClient = account.getClientId();

        /**
         * Add random runs
         */

        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);

        Group group1 = new Group("Mississippi");
        group1.setGroupId(1024);
        contest.addGroup(group1);

        Group group2 = new Group("Arkansas");
        group2.setGroupId(2048);
        contest.addGroup(group2);

        Account[] teams = getTeamAccounts();

        assignTeamGroup(group1, 0, teams.length / 2);
        assignTeamGroup(group2, teams.length / 2, teams.length - 1);

        /**
         * Add Run Judgements.
         */
        judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        
        Judgement judgement;
        String sampleFileName = sample.getSampleFile();

        for (Run run : runs) {
            RunFiles runFiles = new RunFiles(run, sampleFileName);

            contest.acceptRun(run, runFiles);

            run.setElapsedMins((run.getNumber() - 1) * 9);
            
            judgement = sample.getRandomJudgement(contest, run.getNumber() % 2 == 0); // ever other run is judged Yes.
            sample.addJudgement(contest, run, judgement, judgeId);
        }
    }

    /**
     * Assign group to team startIdx to endIdx.
     * 
     * @param group
     * @param startIdx
     * @param endIdx
     */
    private void assignTeamGroup(Group group, int startIdx, int endIdx) {
        Account[] teams = getTeamAccounts();
        for (int i = startIdx; i < endIdx; i++) {
            teams[i].setGroupId(group.getElementId());
        }
    }

    /**
     * Return list of accounts sorted by team id.
     * 
     * @return
     */
    private Account[] getTeamAccounts() {
        Vector<Account> teams = contest.getAccounts(Type.TEAM);
        Account[] accounts = (Account[]) teams.toArray(new Account[teams.size()]);
        Arrays.sort(accounts, new AccountComparator());
        return accounts;
    }

    public String[] getColors() {
        String[] listOColors = { "Aqua", "Azure", "Black", "Blue", "Brown", "Cyan", "Fuchsia", "Gray", "Green", "Jade", "Lime", "Maroon", "Navy", "Olive", "Orange", "Purple", "Red", "Silver", "Tan",
                "Teal", "Violet", "White", "Yellow" };
        return listOColors;
    }
    
    private Run getRunByIndex(int index) {
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());
        return runs[index];
    }

    public void testNotification() throws Exception {

        NotificationXML notificationXML = new NotificationXML();
        Run run = getRunByIndex(0);

        if (debugMode) {
            System.out.println(" -- testNotification ");
        }

        XMLMemento xmlMemento = null;

        try {
            /**
             * Test what happens when there are no balloon settings set for this site. To pass should throw an exception.
             */
            xmlMemento = notificationXML.createElement(contest, run);
            fail("Balloon Settings are set for site " + contest.getSiteNumber());

        } catch (Exception e) {
            pass();
        }

        BalloonSettings balloonSettings = new BalloonSettings("BalloonSet" + contest.getSiteNumber(), contest.getSiteNumber());
        balloonSettings.setBalloonClient(scoreboardClient);
        balloonSettings.setSiteNumber(contest.getSiteNumber());

        Problem[] problems = contest.getProblems();
        Arrays.sort(problems, new ProblemComparator(contest));
        balloonSettings.addColorList(problems, getColors());

        contest.updateBalloonSettings(balloonSettings);

        ClientSettings settings = new ClientSettings(scoreboardClient);
        contest.updateClientSettings(settings);

        try {
            /**
             * Balloon delivery information should not be present. To pass should throw an exception.
             */
            xmlMemento = notificationXML.createElement(contest, run);
            fail("Unexpectedly Balloon Delivery information is present");

        } catch (Exception e) {
            pass();
        }
        
//        Judgement judgement = sample.getYesJudgement(contest);
//        System.out.println("debug 22 J = "+judgeId);
//        sample.addJudgement(contest, run, judgement, judgeId);

        /**
         * Add balloon delivery information for run.
         */

        sample.addBalloonNotification(contest, run);
        
//        Run [] runs = sample.cloneRun(contest, 1, run);
//        Run run2 = runs[0];
//        
//        System.out.println("debug22 "+run2+" "+judgeId+" "+run2.getProblemId()+" "+run2.getLanguageId());
//
//        sample.addJudgement(contest, run2, judgement, judgeId);
//        
//        addDelivery (contest, run2);
        
        xmlMemento = notificationXML.createElement(contest, run);
        
        String xml = xmlMemento.saveToString();
        if (debugMode) {
            System.out.println(xml);
        }

    }
    
    /**
     * a pass.
     * 
     * Passes a test, the opposite of fail().
     * 
     */
    private void pass() {
        
    }


}
