package edu.csus.ecs.pc2.core.scoring;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: NewScoringAlgorithmTest.java 194 2011-05-15 03:03:40Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/core/scoring/NewScoringAlgorithmTest.java $
public class NewScoringAlgorithmTest extends TestCase {

    private boolean debugMode = false;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testBasic() throws Exception {

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(2, 2, 6, 12, true);

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();

        StandingsRecord[] standingsRecords = scoringAlgorithm.getStandingsRecords(contest, new Properties());

        int numberAccounts = contest.getAccounts(Type.TEAM).size();

        assertEquals("getStandingsRecords accounts and records unequal", numberAccounts, standingsRecords.length);

        createJudgedRun(contest, 0, true, 12);

        standingsRecords = scoringAlgorithm.getStandingsRecords(contest, new Properties());

        if (debugMode) {
            for (StandingsRecord standingsRecord : standingsRecords) {
                System.out.println(standingsRecord.getRankNumber() + " " + standingsRecord.getNumberSolved() + " " + standingsRecord.getPenaltyPoints() + " " + standingsRecord.getClientId());
            }
        }

    }

    /**
     * Submit and judge a run.
     * 
     * @param contest
     * @param judgementIndex
     * @param solved
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Run createJudgedRun(IInternalContest contest, int judgementIndex, boolean solved, int elapsedMinutes) throws IOException, ClassNotFoundException, FileSecurityException {
        Run run = getARun(contest, elapsedMinutes);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");

        int runId = contest.getRuns().length + 1;
        run.setNumber(runId);
        contest.addRun(run, runFiles, null);

        ClientId who = contest.getAccounts(ClientType.Type.JUDGE).firstElement().getClientId();
        assertFalse("Could not retrieve first judge ", who == null);

        checkOutRun(contest, run, who);

        Judgement judgement = contest.getJudgements()[judgementIndex];

        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), who, solved, false);
        contest.addRunJudgement(run, judgementRecord, null, who);

        return run;

    }

    /**
     * Create a new run in the contest.
     * 
     * @param contest
     * @param elapsedMinutes
     * @return created run.
     */
    private Run getARun(IInternalContest contest, int elapsedMinutes) {
        Problem problem = contest.getProblems()[0];
        Language language = contest.getLanguages()[0];

        Account account = contest.getAccounts(ClientType.Type.TEAM).firstElement();

        ClientId id = account.getClientId();
        Run run = new Run(id, language, problem);
        run.setElapsedMins(elapsedMinutes);
        return run;
    }

    private void checkOutRun(IInternalContest contest, Run run, ClientId judgeId) {
        try {
            contest.checkoutRun(run, judgeId, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testGroupWinners() throws Exception {

        SampleContest sampleContest = new SampleContest();

        String[] groupNames = { "paragon", "north", "west", "south", "anderson" };

        int startr = 0;

        int teamsPerGroup = 4;

        IInternalContest contest = sampleContest.createContest(2, 2, teamsPerGroup * groupNames.length + 5, 12, true);

        for (String name : groupNames) {
            Group group = new Group(name);
            contest.addGroup(group);
            assignGroups(contest, group, startr, teamsPerGroup);
            startr += teamsPerGroup;
        }

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
        StandingsRecord[] standingsRecords = scoringAlgorithm.getRegionalWinners(contest, new Properties());

        if (debugMode){
            System.out.println("debug records " + standingsRecords.length);
        }
        
        assertEquals("Expecting one team per group", teamsPerGroup * groupNames.length, standingsRecords.length);

        for (Group group : contest.getGroups()) {

            StandingsRecord record = scoringAlgorithm.getRegionalWinner(contest, new Properties(), group);
            /**
             * Since there are ties for first place there is no clear winner so getRegionalWinner return null.
             */
            assertNull(record);
        }

        // TODO CCS figure out assertNull fails below
        
//        Run aRun = createJudgedRun(contest, 0, true, 12);
//        aRun.setSubmitter(getTeamAccounts(contest)[0].getClientId()); // assign to team 1
//
//        for (Group group : contest.getGroups()) {
//
//            StandingsRecord record = scoringAlgorithm.getRegionalWinner(contest, new Properties(), group);
//            /**
//             * Since there are ties for first place there is no clear winner so getRegionalWinner return null.
//             */
//            assertNull(record);
//        }

    }

    Account[] getTeamAccounts(IInternalContest contest) {
        Vector<Account> vector = contest.getAccounts(Type.TEAM);
        Account[] accounts = (Account[]) vector.toArray(new Account[vector.size()]);
        Arrays.sort(accounts, new AccountComparator());
        return accounts;
    }

    private void assignGroups(IInternalContest contest, Group group, int startr, int count) {
        Account[] accounts = getTeamAccounts(contest);
        for (int i = startr; i < startr + count; i++) {
            accounts[i].setGroupId(group.getElementId());
            if (debugMode) {
                System.out.println("debug Account  " + accounts[i].getClientId() + " " + accounts[i].getGroupId());
            }
        }
    }

}
