package edu.csus.ecs.pc2.core.model;

import java.io.FileNotFoundException;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemFilesUtilitiesTest extends AbstractTestCase {

    private SampleContest sampleContest = new SampleContest();

    public void testInternal() {

        IInternalContest contest = sampleContest.createContest(1, 3, 12, 12, true);

        Problem problem = contest.getProblems()[0];

        boolean actual = ProblemFilesUtilities.verifyProblemFiles(contest, problem);

        assertFalse("Should be missing internal files", actual);

    }

    public void testExternal() throws FileNotFoundException {

        IInternalContest contest = sampleContest.createContest(1, 3, 12, 12, true);

        Problem problem = contest.getProblems()[0];

        boolean actual = ProblemFilesUtilities.verifyProblemFiles(contest, problem);
        assertFalse("Problem should not have data files", actual);

        problem.setUsingExternalDataFiles(true);

        actual = ProblemFilesUtilities.verifyProblemFiles(contest, problem);
        assertFalse("Problem should not have external files", actual);

        ensureDirectory(getDataDirectory());

        String filename = getTestFilename("pfut.data.txt");
        filename = sampleContest.createSampleDataFile(filename);
        assertFileExists(filename);
        problem.setDataFileName(filename);

        filename = getTestFilename("pfut.answer.txt");
        String answerFileName = sampleContest.createSampleDataFile(filename);
        assertFileExists(answerFileName);
        problem.setAnswerFileName(answerFileName);

        actual = ProblemFilesUtilities.verifyProblemFiles(contest, problem);
        assertTrue("Problem should have external files", actual);
    }
}
