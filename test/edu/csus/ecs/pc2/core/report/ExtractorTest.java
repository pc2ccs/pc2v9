package edu.csus.ecs.pc2.core.report;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import junit.framework.TestCase;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExtractorTest extends TestCase {

    /**
     * Test accounts.tsv report
     * @throws Exception 
     */
    public void testAccountsReport() throws Exception {

        SampleContest sample = new SampleContest();

        int teams = 22;
        int judges = 12;
        int scoreboards = 1;

        IInternalContest contest = sample.createContest(1, 2, teams, judges, false);
        IInternalController controller = sample.createController(contest, true, false);

        Extractor extractor = new Extractor();
        String[] lines = extractor.getReportLines(Extractor.ACCOUNTS_TSV_FILENAME, contest, controller);

        int expectedLines = 1 + teams + judges + scoreboards;

        if (expectedLines != lines.length){
            for (String string : lines) {
                System.out.println("'"+string);
            }
        }

        assertEquals("Expected number of lines ", expectedLines, lines.length);

        assertTrue("Expect header ", lines[0].startsWith("accounts"));
        assertTrue("Expect second line  is team ", lines[1].startsWith("team"));

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
        
        String[] lines = extractor.getReportLines(Extractor.SCOREBOARD_TSV_FILENAME, contest, controller);

        int expectedLines = 1 + teams;

//        for (String string : lines) {
//            System.out.println("'" + string);
//        }

        assertEquals("Expected number of lines ", expectedLines, lines.length);
        
        assertTrue("Expect header ", lines[0].startsWith("scoreboard"));

    }
}
