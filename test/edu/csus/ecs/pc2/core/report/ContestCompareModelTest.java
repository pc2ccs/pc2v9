// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.csus.ecs.pc2.core.InternalControllerSpecial;
import edu.csus.ecs.pc2.core.JudgementLoader;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.LoadAccounts;
import edu.csus.ecs.pc2.core.imports.clics.CLICSEventType;
import edu.csus.ecs.pc2.core.model.Account;
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
        assertFileExists(eventfeedFile);

        String[] lines = Utilities.loadFile(eventfeedFile);
        assertTrue(lines.length > 0);

        ContestCompareModel comp = new ContestCompareModel(contest, lines);

        if (isDebugMode()) {
            CLICSEventType[] types = { CLICSEventType.TEAMS, CLICSEventType.PROBLEMS, CLICSEventType.LANGUAGES, CLICSEventType.JUDGEMENT_TYPES };
            for (CLICSEventType type : types) {
                String mess = comp.compareSummary(type.toString(), type, comp.getComparisonRecords(type));
                System.out.println(mess);
            }
        }

        List<ContestCompareRecord> records = comp.getComparisonRecords();
        assertEquals("Expecting comparison records/differences ", 295, records.size());

        CLICSEventType type = CLICSEventType.CONTEST;
        assertEquals("Expecting one " + type + " comparison records ", 5, comp.getComparisonRecords(type).size());

        type = CLICSEventType.TEAMS;
        assertEquals("Expecting " + type + " comparison records ", 224, comp.getComparisonRecords(type).size());

        List<ContestCompareRecord> nonMatchRecords = comp.getNonMatchingComparisonRecords();
        assertEquals("Expecting non matching records", 294, nonMatchRecords.size());

        assertFalse(comp.isMatch());
    }
    
    /**
     * Load and initialize data into model.
     * 
     * @param contest
     * @param entryLocation
     * @return
     * @throws Exception
     */
    protected IInternalContest loadAndInitializeContest(IInternalContest contest, String entryLocation) throws Exception{

        if (contest == null) {
            contest = new InternalContest();
            contest.setClientId(new ClientId(1, Type.SERVER, 0));
        }
        
        IContestLoader loader = new ContestSnakeYAMLLoader();
        ensureStaticLog();

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
     * Testing mini contest model comparison with event feed filename.
     * 
     * @throws Exception
     */
    public void testJSONComparisonMatch() throws Exception {
        
        IInternalContest contest = loadAndInitializeContest(null, "mini");
        assertNotNull(contest);
        
        String testDirectory = getDataDirectory(getName());
        ensureDirectory(testDirectory);
        
        String eventfeedFile = testDirectory + File.separator + "mini.event.feed.json";
        assertFileExists(eventfeedFile);
//        startExplorer(testDirectory); 
//        editFile(eventfeedFile);
        
        Judgement[] judgements = contest.getJudgements();
        assertEquals("Expecting judgement count ", 8, judgements.length);

        // Write event feed JSON to stdout
//        EventFeedJSONReport report = new EventFeedJSONReport();
//        report.setContestAndController(contest, new NullController());
//        PrintWriter stream = new PrintWriter(System.out);
//        report.writeReport(stream);

        // write event feed to file 
//      EventFeedJSONReport report = new EventFeedJSONReport();
//      report.setContestAndController(contest, new NullController());
//      report.createReportFile(eventfeedFile, new Filter());
        
        String[] lines = Utilities.loadFile(eventfeedFile);
        assertTrue("Expecting lines in "+eventfeedFile, lines.length > 0);

        ContestCompareModel comp = new ContestCompareModel(contest, lines);

        // todo when match uncomment this
//        if (!comp.isMatch() ) {
            if (comp.isMatch() ) {
            /**
             * If the modes do not match print differences
             */
            System.out.println(getName()+" Contest do not match, ef file = "+eventfeedFile);

            CLICSEventType[] types = { CLICSEventType.CONTEST, CLICSEventType.TEAMS, //
                    CLICSEventType.PROBLEMS, CLICSEventType.LANGUAGES, CLICSEventType.JUDGEMENT_TYPES };

            String sameString = ComparisonState.SAME.toString();

            for (CLICSEventType type : types) {
                List<ContestCompareRecord> compareRecords = comp.getComparisonRecords(type);
                String mess = comp.compareSummary(type.toString(), type, compareRecords);
                System.out.println("comp sum " + mess);
                for (ContestCompareRecord rec : compareRecords) {
                    if (!sameString.equals(rec.getState().toString())) {
                        System.out.println("mismatch rec " + type + " " + rec.toString());
                    }
                }
            }

            String mess = comp.compareSummary("all", null, comp.getComparisonRecords());
            System.out.println(mess);
        }

        assertEquals("Models do not match", 325, comp.getComparisonRecords().size());
        
        // TODO when contest info matches then uncomment this
//        assertTrue("Model and event feed do not match", comp.isMatch());

    }

    /**
     * Test compare of pretest CDP and existing event feed file.
     * 
     * @throws Exception
     */
    public void testReport() throws Exception {
        
        String cdpPath = "c:\\repos\\icpc\\ccsconfig-dhaka\\contests\\pretest2";
        
        if (! new File(cdpPath).isDirectory()) {
            // only test if there is a pretest dir found
            return;
        }
        
        IInternalContest contest = loadAndInitializeContest(null, cdpPath);
        assertNotNull(contest);

        // Load Accounts
        String loadAccountFilename = "/repos/icpc/ccsconfig-dhaka/contests/pretest2/config/load_accounts_real.tsv";
        assertFileExists(loadAccountFilename);
        Account[] updateAccounts = LoadAccounts.updateAccountsFromFile(contest, loadAccountFilename);
        contest.updateAccounts(updateAccounts);

        InternalControllerSpecial controller = new InternalControllerSpecial(contest);
        
        String outDir = getOutputDataDirectory(getName());
        ensureDirectory(outDir);
        assertDirectoryExists(outDir);
        
        String efName = "stuf-event-feed.1664499410895.json";
//        editFile(efName);
        
        String reportFile = createContestCompareReport(contest, controller, efName, getOutputDataDirectory(getName()));
//        editFile(reportFile);
//        writeReport(contest, controller,efName);
        
        assertFileExists(reportFile);
        
    }

    private String createContestCompareReport(IInternalContest contest, InternalControllerSpecial controller,  String overRideEventFilename, String outputDataDirectory) throws IOException {
        ContestCompareReport report = new ContestCompareReport();
        report.setContestAndController(contest, controller);
        report.setOverRideEventFilename(overRideEventFilename);
        
        String outfile =  outputDataDirectory + File.separator + "testCompReport.txt";
        report.createReportFile(outfile, new Filter());
        return outfile;
    }
    
    private void writeReport(IInternalContest contest, InternalControllerSpecial controller,  String overRideEventFilename) throws IOException {
        ContestCompareReport report = new ContestCompareReport();
        report.setContestAndController(contest, controller);
        report.setOverRideEventFilename(overRideEventFilename);
        report.writeReport(System.out);
        System.out.flush();
    }
}
