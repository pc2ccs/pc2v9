package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Extract Runs test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class ExtractRunsTest extends AbstractTestCase {

    private static final int NUMBER_RUNS = 22;
    
    private boolean debugMode = false;

    public void testExtractRuns() throws Exception {
        
        SampleContest sampleContest = new SampleContest();

        int siteNumber = 6;

        IInternalContest contest = sampleContest.createContest(siteNumber, 3, 10, 10, true);

        String storeDirectory = getOutputDataDirectory() + File.separator +"DB";
        ensureDirectory(storeDirectory);
        
        try {
            FileSecurity fileSecurity = new FileSecurity(storeDirectory);
            contest.setStorage(fileSecurity);
            
            String password = "foo";
            fileSecurity.saveSecretKey(password.toCharArray());
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

        // Directory where test data is
//        String testDir = "testdata";
//        String projectPath=JUnitUtilities.locate(testDir);
//        if (projectPath == null) {
//            projectPath = "."; //$NON-NLS-1$
//            System.err.println("ExtractRunsTest: Unable to locate "+testDir);
//        }

//        String loadFile = projectPath + File.separator+ testDir + File.separator + "Sumit.java";
        String loadFile = getSamplesSourceFilename(SUMIT_SOURCE_FILENAME);
        File dir = new File(loadFile);
        if (!dir.exists()) {
            System.err.println("could not find " + loadFile);
        }
        SerializedFile sFile = new SerializedFile(dir.getAbsolutePath());

        Run[] allRuns = sampleContest.createRandomRuns(contest, NUMBER_RUNS, true, true, true);
        for (Run run : allRuns) {
            
            RunFiles runFiles = new RunFiles(run, sFile, new SerializedFile[0]);
            
            try {
                contest.acceptRun(run, runFiles);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (FileSecurityException e) {
                e.printStackTrace();
            }
        }

        assertEquals ("Number of runs", NUMBER_RUNS, contest.getRuns().length);

        Judgement solvedJudgement = contest.getJudgements()[0];
        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        Run[] runs = contest.getRuns();

        for (Run run : runs) {
            // Set them to all solved
            JudgementRecord judgementRecord = new JudgementRecord(solvedJudgement.getElementId(), judgeId, true, false);
            run.addJudgement(judgementRecord);
            run.setStatus(RunStates.JUDGED);
        }
        
        Run anyRun = contest.getRuns()[0];
        
        assertTrue ("Run should be judged ", anyRun.isJudged());

        ExtractRuns extractRuns = new ExtractRuns(contest);

        ensureOutputDirectory();
        extractRuns.setExtractDirectory(getOutputDataDirectory());
        
        System.out.println("Extracting to directory: "+extractRuns.getExtractDirectory());

        Exception holdException = null;

        int exceptions = 0;
        int extracted = 0;

        Arrays.sort(runs,new RunComparator());
        
        for (Run run : runs) {
            try {
                extractRuns.extractRun(run.getElementId());
                extracted ++;
                if (debugMode){
                    System.out.println("Extracted " + run.getNumber());
                }
            } catch (Exception e) {
                exceptions ++;
                if (holdException == null){
                    holdException = e;
                }
            }
        }
        
        if (exceptions > 1){
            System.err.println(getName()+" There were "+exceptions+" exceptions");
        }

        if (exceptions > 0){
            throw holdException;
        }
        
        assertEquals("Number extracted runs", runs.length, extracted);

    }

}
