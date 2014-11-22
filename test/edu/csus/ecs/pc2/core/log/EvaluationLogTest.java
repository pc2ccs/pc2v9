package edu.csus.ecs.pc2.core.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EvaluationLogTest extends AbstractTestCase {

    public final static long MS_DAY = 24 * 60 * 60 * 1000;

    public final static long FOUR_DAYS = 4 * MS_DAY;

    /**
     * Test whether new Run.getDate() method is used rather than current date.
     * 
     * @throws Exception
     */
    public void testNewGetDateMethodBug845() throws Exception {

        String logFileName = getTestFilename("example.evals.log");

        // delete output log file if exists
        File file = new File(logFileName);
        if (file.isFile()) {
            file.delete();
        }

        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFileName)));

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(3, 3, 22, 12, true);

        Run[] runs = sample.createRandomRuns(contest, 3, true, true, false);
        Run run = runs[0];
        Date date = new Date();
        Date newDate = new Date(date.getTime() - FOUR_DAYS);

        run.setDate(newDate);

        // public static void printEvaluationLine(PrintWriter printWriter, Run run, IInternalContest inContest) {

        // Create row in test file.
        EvaluationLog.printEvaluationLine(printWriter, run, contest);
        printWriter.close();

        String[] lines = Utilities.loadFile(logFileName);
//        for (String string : lines) {
//            System.out.println(string);
//        }

        String currentDay = date.toString().substring(0, 10);

        // day from log
        String testDay = lines[0].substring(0, 10);
        
//        System.out.println("rundate    = "+run.getDate());
//        System.out.println("newDate    = "+newDate);
//        System.out.println("currentDay = "+currentDay);
//        System.out.println("testDay    = "+testDay);

        /**
         * The day in the file is four days ago, it should not match the current date.
         */
        assertNotEquals("Same day ", currentDay, testDay);
    }

    
    /**
     * Test whether new Run.getDate() method is used rather than current date.
     * 
     * @throws Exception
     */
    public void testNewGetDateMethodBug845WithJudgement() throws Exception {

        String logFileName = getTestFilename("testWithJudgement.evals.log");

        // delete output log file if exists
        File file = new File(logFileName);
        if (file.isFile()) {
            file.delete();
        }

        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFileName)));

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(3, 3, 22, 12, true);

        Run[] runs = sample.createRandomRuns(contest, 3, true, true, false);
        
        Run run = runs[0];
        Date date = new Date();
        Date newDate = new Date(date.getTime() - FOUR_DAYS);

        run.setDate(newDate);
        run.setNumber(1);
        contest.addRun(run);
        
        Judgement judgement = sample.getYesJudgement(contest);
        ClientId judgeId = sample.getJudgeAccounts(contest)[0].getClientId();
        sample.addJudgement(contest, run, judgement, judgeId);

        // Create row in test file.
        EvaluationLog.printEvaluationLine(printWriter, run, contest);
        printWriter.close();

        String[] lines = Utilities.loadFile(logFileName);

        String currentDay = date.toString().substring(0, 10);

        // day from log
        String testDay = lines[0].substring(0, 10);
        
//        System.out.println("rundate    = "+run.getDate());
//        System.out.println("newDate    = "+newDate);
//        System.out.println("currentDay = "+currentDay);
//        System.out.println("testDay    = "+testDay);

        /**
         * The day in the file is four days ago, it should not match the current date.
         */
        assertNotEquals("Same day ", currentDay, testDay);
        
        /**
         * Check Judgement
         */
        String [] fields = lines[0].split("[|]");
        
        //0 = Mon Nov 17 20:50:11 PST 2014
        //1 = Site 0
        //2 = Run 1
        //3 = Team 9
        //4 = Routing--6114101333900717789
        //5 = Solved true
        //6 = Proxy 0
        //7 = Deleted false
        //8 = Judgement Yes.
        //9 = ValJud false
        //10 = AccHit false
        //11 = Judge JUDGE1 @ site 3

        String judgementName = fields[8].replaceFirst("Judgement ", "");
        assertEquals("Judgement", judgement.getDisplayName(), judgementName);

        assertEquals("Expecting elapsed time field", 13, fields.length);

        String elapsedField = fields[12];
        String expected = "elapsed ";
        String actual = elapsedField.substring(0, expected.length());
        assertEquals("Expecting elapsed field label", expected, actual);
    }
}
