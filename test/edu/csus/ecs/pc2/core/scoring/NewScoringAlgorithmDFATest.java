package edu.csus.ecs.pc2.core.scoring;

import java.io.IOException;
import java.util.Properties;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * New Scoring Algorithm test based on DFA JUnit.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: NewScoringAlgorithmDFATest.java 165 2010-10-27 21:38:30Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/core/scoring/NewScoringAlgorithmDFATest.java $
public class NewScoringAlgorithmDFATest extends AbstractTestCase {

    private boolean debugMode = false;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void testBasicDFA() throws Exception {

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(2, 2, 6, 12, true);

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();

        StandingsRecord[] standingsRecords = scoringAlgorithm.getStandingsRecords(contest, new Properties());

        int numberAccounts = contest.getAccounts(Type.TEAM).size();

        assertEquals("getStandingsRecords accounts and records unequal", numberAccounts, standingsRecords.length);

        createJudgedRun(contest, 0, true, 12);
        
        DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
        Log log = createLog(getName());
        
        String newXml = scoringAlgorithm.getStandings(contest, new Properties(), log);
        String defXml = defaultScoringAlgorithm.getStandings(contest, new Properties(), log);
        
        if (debugMode){
            System.out.println(defXml);
            System.out.println();
            System.out.println(newXml);
        }
        
//        assertEquals(defXml, newXml);
//        standingsRecords = scoringAlgorithm.getStandingsRecords(contest, new Properties());

    

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

        if (debugMode){
            for (StandingsRecord standingsRecord : standingsRecords) {
                System.out.println(standingsRecord.getRankNumber() + " " + standingsRecord.getNumberSolved() + " "
                        + standingsRecord.getPenaltyPoints() + " " + standingsRecord.getClientId());
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
    public Run createJudgedRun(IInternalContest contest, int judgementIndex, boolean solved, int elapsedMinutes)
            throws IOException, ClassNotFoundException, FileSecurityException {
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
}
