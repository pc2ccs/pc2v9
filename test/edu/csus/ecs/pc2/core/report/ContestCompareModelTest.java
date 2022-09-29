// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.util.List;

import edu.csus.ecs.pc2.core.JudgementLoader;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSEventType;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
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

        CLICSEventType[] types = { CLICSEventType.TEAMS, CLICSEventType.PROBLEMS, CLICSEventType.LANGUAGES, CLICSEventType.JUDGEMENT_TYPES };

        for (CLICSEventType type : types) {
            String mess = comp.compareSummary(type.toString(), type, comp.getComparisonRecords(type));
            System.out.println(mess);

        }

        List<ContestCompareRecord> records = comp.getComparisonRecords();

        assertEquals("Expecting comparison records ", 266, records.size());

        CLICSEventType type = CLICSEventType.CONTESTS;
        assertEquals("Expecting one " + type + " comparison records ", 1, comp.getComparisonRecords(type).size());

        type = CLICSEventType.TEAMS;
        assertEquals("Expecting " + type + " comparison records ", 224, comp.getComparisonRecords(type).size());

        List<ContestCompareRecord> nonMatchRecords = comp.getNonMatchingComparisonRecords();
        assertEquals("Expecting non matching records", 265, nonMatchRecords.size());

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

//        setDebugMode(true);
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

        assertTrue("Model and event feed do not match", comp.isMatch());

    }
}
