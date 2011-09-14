package edu.csus.ecs.pc2.core.export;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test for YAML export.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ExportYAMLTest.java 207 2011-07-08 18:32:38Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/export/ExportYAMLTest.java $
public class ExportYAMLTest extends TestCase {

    private boolean debugMode = false;

    private String baseTestDirectory = "testing";

    /**
     * Test directory name for this JUnit run.
     */
    private String testDirectory = null;

    private SampleContest sampleContest = new SampleContest();

    private SimpleDateFormat formatter = new SimpleDateFormat("HH.mm.ss MMM dd");

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (testDirectory == null) {
            testDirectory = baseTestDirectory + File.separator + "eyaml" + File.separator + formatter.format(new Date());
            new File(testDirectory).mkdirs();
        }

        if (debugMode) {
            System.out.println("Creating to dir: " + testDirectory);
        }
    }

    public void testOne() throws Exception {

        IInternalContest contest = sampleContest.createContest(3, 3, 12, 5, true);

//        addShortNames(contest);

        Problem[] problems = contest.getProblems();
        
        if (debugMode){
            for (Problem problem : contest.getProblems()) {
                System.out.println(problem.getDisplayName() + " " +problem.getShortName());
            }
        }

        sampleContest.addDataFiles(contest, testDirectory, problems[0], "sumit.dat", "sumit.ans");
        sampleContest.addDataFiles(contest, testDirectory, problems[1], "quads.in", "quads.ans");
        sampleContest.addDataFiles(contest, testDirectory, problems[4], "london.dat", "london.ans");
        sampleContest.addInternalValidator(contest, problems[4], 3);

        ExportYAML exportYAML = new ExportYAML();

        exportYAML.exportFiles(testDirectory, contest);

        exportYAML = null;
    }

//    private void addShortNames(IInternalContest contest) throws Exception {
//
//        int idx = 1;
//        for (Problem problem : contest.getProblems()) {
//            problem.setShortName("short" + idx);
//            idx++;
//        }
//    }

}
