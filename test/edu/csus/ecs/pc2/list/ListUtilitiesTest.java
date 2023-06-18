// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.list;

import java.io.File;
import java.util.List;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.ui.SubmissionSampleLocation;
import edu.csus.ecs.pc2.ui.SubmissionSolutionList;


/**
 * Unit test.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ListUtilitiesTest extends AbstractTestCase {

    /**
     * Test filterByJudgingTypes for only accepted judges solutions
     * 
     * @throws Exception
     */
    public void testfilterByJudgingTypesAccepted() throws Exception {

        String contestDir = "tenprobs";
        String cdpDir = getContestSampleCDPDirname(contestDir);
        
//        startExplorer(cdpDir);

        IInternalContest contest = loadFullSampleContest(null, new File(cdpDir));

        SubmissionSolutionList submissionSolutionList = new SubmissionSolutionList();
        submissionSolutionList.add(new SubmissionSampleLocation("Yes", "accepted"));

        assertEquals("Expecting judging type location count ", 1, submissionSolutionList.size());

        List<File> files = ListUtilities.getAllCDPsubmissionFileNames(contest, cdpDir);
        assertEquals("Expecting total judge sample sources file count for " + contestDir, 20, files.size());

        List<File> newFiles = ListUtilities.filterByJudgingTypes(files, submissionSolutionList);

        assertNotNull(newFiles);
        assertEquals("Expecting total judging type sample file count for " + contestDir, 10, newFiles.size());

    }

    /**
     * Test filterByJudgingTypes for accepted judging types.
     * @throws Exception
     */
    public void testfilterByAcceptedJudgingTypes() throws Exception {

        String contestDir = "problemflagtest";
        String cdpDir = getContestSampleCDPDirname(contestDir);

        IInternalContest contest = loadFullSampleContest(null, new File(cdpDir));

        SubmissionSolutionList submissionSolutionList = new SubmissionSolutionList();
        submissionSolutionList.add(new SubmissionSampleLocation("foo", "accepted"));

        assertEquals("Expecting judging type location count ", 1, submissionSolutionList.size());

        List<File> files = ListUtilities.getAllCDPsubmissionFileNames(contest, cdpDir);
        assertEquals("Expecting total judge sample sources file count for " + contestDir, 264, files.size());

        List<File> newFiles = ListUtilities.filterByJudgingTypes(files, submissionSolutionList);

        assertNotNull(newFiles);
        assertEquals("Expecting total judging type sample file count for " + contestDir, 56, newFiles.size());
    }
    
    
    /**
     * Test filterByJudgingTypes for all judging types.
     * @throws Exception
     */
    public void testfilterByJudgingTypes() throws Exception {

        String contestDir = "problemflagtest";
        String cdpDir = getContestSampleCDPDirname(contestDir);

        IInternalContest contest = loadFullSampleContest(null, new File(cdpDir));

        SubmissionSolutionList submissionSolutionList = new SubmissionSolutionList(new File(cdpDir));

        assertEquals("Expecting judging type location count ", 5, submissionSolutionList.size());

        List<File> files = ListUtilities.getAllCDPsubmissionFileNames(contest, cdpDir);
        assertEquals("Expecting total judge sample sources file count for " + contestDir, 264, files.size());

        List<File> newFiles = ListUtilities.filterByJudgingTypes(files, submissionSolutionList);
        
//        for (File file : newFiles) {
//            System.out.println("debug 22 ffile = "+file.getAbsolutePath());
//        }

        assertNotNull(newFiles);
        assertEquals("Expecting total judging type sample file count for " + contestDir, 264, newFiles.size());
    }

}
