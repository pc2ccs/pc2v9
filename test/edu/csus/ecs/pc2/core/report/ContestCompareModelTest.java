// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.csus.ecs.pc2.core.InternalControllerSpecial;
import edu.csus.ecs.pc2.core.JudgementLoader;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSEventType;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class ContestCompareModelTest extends AbstractTestCase {

    private SampleContest sampleContest = new SampleContest();

    /**
     * Testing where EF and contest model do not match.
     * 
     * @throws Exception
     */
    public void testJSONComparisonNotMatch() throws Exception {

        String testDirectory = getDataDirectory();

        IInternalContest contest = sampleContest.createStandardContest();
        String eventfeedFile = testDirectory + File.separator + "nac22prac4.event-feed.part.conf.json";

        String[] lines = Utilities.loadFile(eventfeedFile);

        ContestCompareModel comp = new ContestCompareModel(contest, lines);

        if (isDebugMode()) {
            CLICSEventType[] types = { CLICSEventType.TEAMS, CLICSEventType.PROBLEMS, CLICSEventType.LANGUAGES, CLICSEventType.JUDGEMENT_TYPES };
            for (CLICSEventType type : types) {
                String mess = comp.compareSummary(type.toString(), type, comp.getComparisonRecords(type));
                System.out.println(mess);
            }
        }

        List<ContestCompareRecord> records = comp.getComparisonRecords();
        assertEquals("Expecting comparison records/differences ", 285, records.size());

        CLICSEventType type = CLICSEventType.CONTEST;
        assertEquals("Expecting one " + type + " comparison records ", 1, comp.getComparisonRecords(type).size());

        type = CLICSEventType.TEAMS;
        assertEquals("Expecting " + type + " comparison records ", 224, comp.getComparisonRecords(type).size());

        List<ContestCompareRecord> nonMatchRecords = comp.getNonMatchingComparisonRecords();
        assertEquals("Expecting non matching records", 284, nonMatchRecords.size());

        assertFalse(comp.isMatch());
    }
    
    protected IInternalContest loadAndInitializeContest(IInternalContest contest, String entryLocation) throws Exception{

        if (contest == null) {
            contest = new InternalContest();
            contest.setClientId(new ClientId(1, Type.SERVER, 0));
        }
        
        IContestLoader loader = new ContestSnakeYAMLLoader();

        // Load Configuration from CDP
        loader.initializeContest(contest, new File(entryLocation));

        if (contest.getJudgements().length == 0) {
            // judgements not loaded from yaml nor config/reject.ini
            // load from "."
            JudgementLoader.loadJudgements(contest, false, ".");
        }
        
        if (contest.getJudgements().length == 0) {
            // judgements not loaded from cdp, yaml or ./reject.ini 
            JudgementLoader.loadDefaultJudgements(contest);
        }
        
        return contest;
    }

    /**
     * Testing where EF and contest model DO match.
     * 
     * @throws Exception
     */
    public void testJSONComparisonMatch() throws Exception {
        
        IInternalContest contest = loadAndInitializeContest(null, "mini");
        assertNotNull(contest);
        
        Judgement[] judgements = contest.getJudgements();
        assertEquals("Expecting judgement count ", 8, judgements.length);

        // Write event feed JSON to stdout
//        EventFeedJSONReport report = new EventFeedJSONReport();
//        report.setContestAndController(contest, new NullController());
//        PrintWriter stream = new PrintWriter(System.out);
//        report.writeReport(stream);

        String testDirectory = getDataDirectory(getName());
        ensureDirectory(testDirectory);
        
        String eventfeedFile = testDirectory + File.separator + "ccs2.event.feed.json";
//        editFile(eventfeedFile);
        
        assertFileExists(eventfeedFile);

        String[] lines = Utilities.loadFile(eventfeedFile);
        assertTrue("Expecting lines in "+eventfeedFile, lines.length > 0);

        ContestCompareModel comp = new ContestCompareModel(contest, lines);

        
        if (! comp.isMatch() )
        {
            List<ContestCompareRecord> compareRecords = comp.getComparisonRecords();
            
            String sameString = ComparisonState.SAME.toString();
            
            for (ContestCompareRecord contestCompareRecord : compareRecords) {
                if (! sameString.equals(contestCompareRecord.getState().toString())){
                    System.out.println("debug rec " + contestCompareRecord.getEventType() + " " + contestCompareRecord);
                }
            }
        }
        
        setDebugMode(true);
        if (isDebugMode()) {
            CLICSEventType[] types = { CLICSEventType.TEAMS, CLICSEventType.PROBLEMS, CLICSEventType.LANGUAGES, CLICSEventType.JUDGEMENT_TYPES };

            String sameString = ComparisonState.SAME.toString();

            for (CLICSEventType type : types) {
                List<ContestCompareRecord> compareRecords = comp.getComparisonRecords(type);
                String mess = comp.compareSummary(type.toString(), type, compareRecords);
                System.out.println("debug comp sum " + mess);
                for (ContestCompareRecord rec : compareRecords) {
                    if (!sameString.equals(rec.getState().toString())) {
                        System.out.println("debug rec " + type + " " + rec.toString());
                    }
                }
            }

            String mess = comp.compareSummary("all", null, comp.getComparisonRecords());
            System.out.println(mess);
        }


        // TODO i 536 complete unit test  (debug 22)
        
//        assertTrue("Model and event feed do not match", comp.isMatch());

    }


    // old code - testing with WF pretest API
//    /**
//     * test wf pretest 2 contest.
//     * 
//     * This is a debugging unit test that only works if cdpPath exists.
//     * 
//     * @throws Exception
//     */
//    public void testPretest2() throws Exception {
//
//        String cdpPath = "c:\\repos\\icpc\\ccsconfig-dhaka\\contests\\pretest2";
//        
////        cdpPath = "c:\\repos\\icpc\\ccsconfig-dhaka\\contests\\prete"; // debug 22
//        
//        if (! new File(cdpPath).isDirectory()) {
//            return;
//        }
//        
//        IInternalContest contest = loadAndInitializeContest(null, cdpPath);
//        assertNotNull(contest);
//        
//         Vector<Account> teamAccounts = contest.getAccounts(Type.TEAM);
//         assertTrue("No accounts defined ", teamAccounts.size() > 0);
//
//         System.out.println("debug 22 ta "+teamAccounts.size());
//         Account account = teamAccounts.firstElement();
//         
//         assertNotEquals("account should have title", account.getDisplayName(), account.getTeamName());
//
//        Judgement[] judgements = contest.getJudgements();
//        assertEquals("Expecting judgement count ", 8, judgements.length);
//        
//        InternalControllerSpecial controller = new InternalControllerSpecial(contest);
//        
//        Log log = new Log("logs/"+getName()+".log");
//        StaticLog.setLog(log);
//        
////        ContestCompareModel comp = new ContestCompareModel(contest);
////        assertTrue(comp.isMatch());
//        String outDir = getOutputDataDirectory(getName());
//        ensureDirectory(outDir);
//        assertDirectoryExists(outDir);
//
//        String efName = "stuf-event-feed.1664499410895.json";
////        editFile(efName);
//
//        String reportFile = createContestCompareReport(contest, controller, efName, getOutputDataDirectory(getName()));
//        editFile(reportFile);
//        
//    }
    
    public void testReport() throws Exception {
        
        String cdpPath = "c:\\repos\\icpc\\ccsconfig-dhaka\\contests\\pretest2";
        
        IInternalContest contest = loadAndInitializeContest(null, cdpPath);
        assertNotNull(contest);

        InternalControllerSpecial controller = new InternalControllerSpecial(contest);
        
        String outDir = getOutputDataDirectory(getName());
        ensureDirectory(outDir);
        assertDirectoryExists(outDir);
        
        String efName = "stuf-event-feed.1664499410895.json";
//        editFile(efName);
        
        String reportFile = createContestCompareReport(contest, controller,efName, getOutputDataDirectory(getName()));
//        editFile(reportFile);
    }

    private String createContestCompareReport(IInternalContest contest, InternalControllerSpecial controller,  String overRideEventFilename, String outputDataDirectory) throws IOException {
        ContestCompareReport report = new ContestCompareReport();
        report.setContestAndController(contest, controller);
        report.setOverRideEventFilename(overRideEventFilename);
        
        String outfile =  outputDataDirectory + File.separator + "testCompReport.txt";
        report.createReportFile(outfile, new Filter());
        return outfile;
    }
}
