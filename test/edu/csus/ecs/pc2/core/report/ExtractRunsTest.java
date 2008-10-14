package edu.csus.ecs.pc2.core.report;

import java.io.IOException;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * Extract Runs test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class ExtractRunsTest {

    private ExtractRunsTest() {

    }

    private static String stripChar(String s, char ch) {
        int idx = s.indexOf(ch);
        while (idx > -1) {
            StringBuffer sb = new StringBuffer(s);
            idx = sb.indexOf(ch + "");
            while (idx > -1) {
                sb.deleteCharAt(idx);
                idx = sb.indexOf(ch + "");
            }
            return sb.toString();
        }
        return s;
    }

    public static void main(String[] args) {

        SampleContest sampleContest = new SampleContest();

        int siteNumber = 6;

        IInternalContest contest = sampleContest.createContest(siteNumber, 3, 10, 10, true);

        new FileSecurity("db." + siteNumber);
        try {
            FileSecurity.saveSecretKey(new String("foo").toCharArray());
            // FileSecurity.verifyPassword(new String("foo").toCharArray());
        } catch (FileSecurityException e1) {
            e1.printStackTrace();
            System.exit(4);
        }

        Log log = new Log(stripChar(contest.getClientId().toString(), ' '));
        StaticLog.setLog(log);

        contest.initializeStartupData(contest.getSiteNumber());
        contest.initializeSubmissions(contest.getSiteNumber());

        ContestTime contestTime = contest.getContestTime();
        contestTime.setElapsedMins(52);
        contestTime.startContestClock();
        contest.updateContestTime(contestTime);

        SerializedFile sFile = new SerializedFile("samps/Sumit.java");

        Run[] allRuns = sampleContest.createRandomRuns(contest, 12, true, true, true);
        for (Run run : allRuns) {
            RunFiles runFiles = new RunFiles(run, sFile, new SerializedFile[0]);
            contest.addRun(run, runFiles, null);
        }

        System.out.println("Contest with " + contest.getRuns().length + " runs");

        Judgement solvedJudgement = contest.getJudgements()[0];
        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        Run[] runs = contest.getRuns();

        for (Run run : runs) {
            // Set them to all solved
            JudgementRecord judgementRecord = new JudgementRecord(solvedJudgement.getElementId(), judgeId, true, false);
            run.addJudgement(judgementRecord);
            run.setStatus(RunStates.JUDGED);
        }

        ExtractRuns extractRuns = new ExtractRuns(contest);

        extractRuns.setExtractDirectory("extract");

        for (Run run : runs) {
            try {
                extractRuns.extractRun(run.getElementId());
                System.out.println("Extracted " + run.getNumber());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
