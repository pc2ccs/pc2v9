package edu.csus.ecs.pc2.core.report;

import java.io.IOException;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExtractorTest extends AbstractTestCase {

    private SampleContest sampleContest = new SampleContest();

    /**
     * Test accounts.tsv report
     * @throws Exception 
     */
    public void testAccountsReport() throws Exception {

        SampleContest sample = new SampleContest();

        int teams = 22;
        int judges = 12;
        int scoreboards = 1;
        int admins = 1;

        IInternalContest contest = sample.createContest(1, 2, teams, judges, false);
        IInternalController controller = sample.createController(contest, true, false);

        Extractor extractor = new Extractor();
        String[] lines = extractor.getReportLines(Extractor.ACCOUNTS_OPTION, contest, controller);

        int expectedLines = 1 + admins + teams + judges + scoreboards; // account line and account counts

        if (expectedLines != lines.length) {
            int count = 0;
            for (String string : lines) {
                count++;
                System.out.println(count + " '" + string);
            }
        }

        assertEquals("Expected number of lines ", expectedLines, lines.length);

        assertTrue("Expect header ", lines[0].startsWith("accounts"));
        assertTrue("Expect third line  is team ", lines[2].startsWith("team"));

        assertTrue("Expect last account is judge ", lines[lines.length - 2].startsWith("judge"));
        assertTrue("Expect last account is scoreboard ", lines[lines.length - 1].startsWith("scoreboard"));

    }

    public void testScoreboardTSV() throws Exception {
        SampleContest sample = new SampleContest();

        int teams = 22;
        int judges = 12;

        IInternalContest contest = sample.createContest(1, 2, teams, judges, false);
        IInternalController controller = sample.createController(contest, true, false);

        Extractor extractor = new Extractor();
        contest.setClientId(contest.getAccounts(Type.JUDGE).firstElement().getClientId());
        
        String[] lines = extractor.getReportLines(Extractor.SCOREBOARD_OPTION, contest, controller);

        int expectedLines = 1 + teams;

//        for (String string : lines) {
//            System.out.println("'" + string);
//        }

        assertEquals("Expected number of lines ", expectedLines, lines.length);
        
        assertTrue("Expect header ", lines[0].startsWith("scoreboard"));

    }
    
    
    
    public void testJSON() throws Exception {
        
        SampleContest sample = new SampleContest();

        int teams = 22;
        int judges = 12;

        IInternalContest contest = sample.createContest(1, 2, teams, judges, false);
        IInternalController controller = sample.createController(contest, true, false);

        Extractor extractor = new Extractor();
        contest.setClientId(contest.getAccounts(Type.JUDGE).firstElement().getClientId());
        
        loadRuns(contest);
        
//        Run[] runs = contest.getRuns();
//        for (Run run : runs) {
//            System.out.println("debug 22 runs "+run);
//        }

        String[] lines = extractor.getReportLines(Extractor.JSON_OPTION, contest, controller);

        int expectedLines = 1;

//        for (String string : lines) {
//            System.out.println("'" + string+"'");
//        }

        assertEquals("Expected number of lines ", expectedLines, lines.length);
        
        
        String jsonString = lines[0];
        int commaCount = countCharacters(jsonString, ',');
        assertEquals("Expected number of commas in JSON Array", 69, commaCount);

        
    }

    private void loadRuns(IInternalContest contest) throws IOException, ClassNotFoundException, FileSecurityException, RunUnavailableException {
        
        String[] runsData = { "1,1,A,1,No,Yes", // 20 (a No before first yes)
                "2,1,A,3,Yes,Yes", // 3 (first yes counts Minute points but never Run Penalty points)
                "3,1,A,5,No,Yes", // zero -- after Yes
                "4,1,A,7,Yes,Yes", // zero -- after Yes
                "5,1,A,9,No,Yes", // zero -- after Yes
                "6,1,B,11,No,Yes", // zero -- not solved
                "7,1,B,13,No,Yes", // zero -- not solved
                "8,2,A,30,Yes,Yes", // 30 (minute points; no Run points on first Yes)
                "9,2,B,35,No,Yes", // zero -- not solved
                "10,2,B,40,No,Yes", // zero -- not solved
                "11,2,B,45,No,Yes", // zero -- not solved
                "12,2,B,50,No,Yes", // zero -- not solved
                "13,2,B,55,No,Yes", // zero -- not solved

                "14,9,A,55,Yes,Yes", //
                "15,9,B,155,Yes,Yes", //
                "16,9,C,255,Yes,Yes", //
                "17,9,D,355,Yes,Yes", //
                "18,9,E,455,Yes,Yes", //

        };

        for (String runInfoLine : runsData) {
            sampleContest.addARun((InternalContest) contest, runInfoLine);
        }
        
    }
}
