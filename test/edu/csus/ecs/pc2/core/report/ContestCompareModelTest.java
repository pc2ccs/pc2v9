// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSEventType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
// TODO i 536 cleanup, remove debug 22, etc.
public class ContestCompareModelTest extends AbstractTestCase {

    private SampleContest sampleContest = new SampleContest();

    public void testJSONComparisonNotMatch() throws Exception {

//        String testDirectory = getDataDirectory(this.getName());

        String testDirectory = getDataDirectory();

        IInternalContest contest = sampleContest.createStandardContest();
        String eventfeedFile = testDirectory + File.separator + "nac22prac4.event-feed.part.conf.json";
        
        eventfeedFile = "/contests/2022/2022Finals/pretest2/event-feed.pretest2.part.json";

//        editFile(eventfeedFile);

        String[] lines = Utilities.loadFile(eventfeedFile);

        ContestCompareModel comp = new ContestCompareModel(contest, lines);
        
        CLICSEventType [] types = { CLICSEventType.TEAMS, CLICSEventType.PROBLEMS, CLICSEventType.LANGUAGES, CLICSEventType.JUDGEMENT_TYPES };
//        CLICSEventType [] types = {  CLICSEventType.LANGUAGES};
        
        for (CLICSEventType type : types) {
            String mess = comp.compareSummary(type.toString(), type, comp.getComparisonRecords(type));
            System.out.println(mess);
            
        }
        
//        List<ContestCompareRecord> compList = comp.getComparisonRecords(CLICSEventType.LANGUAGES);
//        for (ContestCompareRecord ccr : compList) {
//            System.out.println(ccr.getState() + " " + ccr.getId() + " " + ccr.getFieldName() + " " + ccr.getvs());
//        }
//        
//        List<ContestCompareRecord> nonList = comp.getNonMatchingComparisonRecords(CLICSEventType.LANGUAGES);
        
//        assertFalse(comp.isMatch());
    }

    public void testJSONComparisonMatch() throws Exception {

        String testDirectory = getDataDirectory();

        IInternalContest contest = sampleContest.createStandardContest();

        String eventfeedFile = testDirectory + File.separator + "nac22prac4.event-feed.part.conf.json";

        String[] lines = Utilities.loadFile(eventfeedFile);

        ContestCompareModel comp = new ContestCompareModel(contest, lines);
        
        CLICSEventType [] types = { CLICSEventType.TEAMS, CLICSEventType.PROBLEMS, CLICSEventType.LANGUAGES, CLICSEventType.JUDGEMENT_TYPES };
       
        
        for (CLICSEventType type : types) {
            String mess = comp.compareSummary(type.toString(), type, comp.getComparisonRecords(type));
            System.out.println("debug 22 comp sum "+mess);
        }
        
        String mess = comp.compareSummary("all", null, comp.getComparisonRecords());
        System.out.println(mess);
        
//        List<ContestCompareRecord> recs = comp.getContestCompareRecords();
//        
//        List<ContestCompareRecord> langRefs = comp.getComparisonRecords(CLICSEventType.LANGUAGES);
//        
//        String message = compareSummary(CLICSEventType.LANGUAGES, langRefs);
//        
//        for (ContestCompareRecord contestCompareRecord : langRefs) {
//            System.out.println(contestCompareRecord.toJSON());
//            System.out.println(contestCompareRecord);
//            System.out.println();
//        }

//        assertTrue(comp.isMatch());

    }
    
   

}
