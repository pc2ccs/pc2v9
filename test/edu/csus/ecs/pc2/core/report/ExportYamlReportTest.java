package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test export contest YAML
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExportYamlReportTest extends TestCase {

    private boolean debugMode = false;

    private SampleContest sampleContest = new SampleContest();

    private SimpleDateFormat formatter = new SimpleDateFormat("HH.mm.ss MMM dd");

    public static final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";

    private String baseTestDirectory = "testing";

    /**
     * Test directory name for this JUnit run.
     */
    private String testDirectory = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (testDirectory == null) {
            testDirectory = baseTestDirectory + File.separator + "exportYaml-" + formatter.format(new Date());
            new File(testDirectory).mkdirs();
        }

        if (debugMode) {
            System.out.println("Creating to dir: " + testDirectory);
        }
    }

    public void testOne() throws Exception {

        IInternalContest contest = sampleContest.createContest(3, 3, 12, 5, true);

        IInternalController controller = sampleContest.createController(contest, true, false);

        Problem[] problems = contest.getProblems();

        sampleContest.addDataFiles(contest, testDirectory, problems[0], "sumit.dat", "sumit.ans");
        sampleContest.addDataFiles(contest, testDirectory, problems[1], "quads.in", "quads.ans");
        sampleContest.addDataFiles(contest, testDirectory, problems[4], "london.dat", "london.ans");
        sampleContest.addInternalValidator(contest, problems[4], 3);

        ExportYamlReport report = new ExportYamlReport();
        report.setDirectoryName(testDirectory);
        report.setContestAndController(contest, controller);

        String filename = testDirectory + File.separator + "exportYaml.txt";
        report.createReportFile(filename, new Filter());

        if (debugMode) {
            System.out.println("Wrote to " + filename);
        }
        report = null;

    }

}
