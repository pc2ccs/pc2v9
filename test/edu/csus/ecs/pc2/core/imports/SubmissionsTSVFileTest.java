package edu.csus.ecs.pc2.core.imports;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SubmissionsTSVFileTest extends AbstractTestCase {
    
    public void testLoadFile() throws Exception {
        
        SubmissionsTSVFile tsv = new SubmissionsTSVFile();
        
        String datafile = getTestFilename("runs1.tsv");
//        editFile(datafile);
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        contest.addProblem(new Problem("hello"));
        
        tsv.setContest(contest);
        
        Run[] runs = tsv.loadRuns(datafile);
        assertEquals("expecting runs ", 13, runs.length);
//        for (Run run : runs) {
//            System.out.println("debug run "+run);
//        }
    }
}
