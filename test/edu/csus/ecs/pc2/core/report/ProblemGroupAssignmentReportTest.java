package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;

public class ProblemGroupAssignmentReportTest extends AbstractTestCase {

    private ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();
    
    /**
     * cat or write file to console System.out
     * 
     * @param filename
     */
    public void catFile(String filename) {
        Utilities.catFile(new PrintWriter(System.out, true), filename);
    }

    public void testPrintreport() throws Exception {

        String testDirectory = getOutputDataDirectory(this.getName());

        StaticLog.setLog(new Log(testDirectory, "testPrintreport.log"));

        ensureDirectory(testDirectory);
//        startExplorer(testDirectory);

        String sampleContestDirName = "valtest";

        String dirname = getContestSampleCDPDirname(sampleContestDirName);
        dirname = "/test/cdps/PacificNWReal2020/";

        File cdpConfigDirectory = new File(dirname);
        IInternalContest contest = new InternalContest();
        loader.initializeContest(contest, cdpConfigDirectory);

        IInternalController controller = new SampleContest().createController(contest, true, false);

        ProblemGroupAssignmentReport report = new ProblemGroupAssignmentReport();
        report.setContestAndController(contest, controller);

        long ms = System.currentTimeMillis();
        String filename = testDirectory + File.separator + "ProblemGroupAssignmentReport." + ms + ".txt";
        report.createReportFile(filename, new Filter());

        editFile(filename);
//        catFile(filename);
    }

}
