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
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        contest.addProblem(new Problem("hello"));
        
        tsv.setContest(contest);
        
        Run[] foo = tsv.loadRuns(datafile);
        assertEquals("expecting runs ", 13, foo.length);
        for (Run run : foo) {
            System.out.println("debug 22 run "+run);
        }
    }
}
