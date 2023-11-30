// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ResultsExportReportTest extends AbstractTestCase {

    private SampleContest sampleContest = new SampleContest();

    /**
     * Add runs into contest
     * @param contest
     * @throws Exception
     */
    private void addRuns(IInternalContest contest) throws Exception {

        String[] runsData = {

                "1,1,A,1,No", //20
                "2,1,A,3,Yes", //3 (first yes counts Minutes only)
                "3,1,A,5,No", //20
                "4,1,A,7,Yes", //20  
                "5,1,A,9,No", //20

                "6,1,B,11,No", //20  (all runs count)
                "7,1,B,13,No", //20  (all runs count)

                "8,2,A,30,Yes", //30

                "9,2,B,35,No", //20 (all runs count)
                "10,2,B,40,No", //20 (all runs count)
                "11,2,B,45,No", //20 (all runs count)
                "12,2,B,50,No", //20 (all runs count)
                "13,2,B,55,No", //20 (all runs count)

                "14,3,A,130,Yes", //30
                "15,3,B,30,Yes", //30
                "16,4,A,230,Yes", //30
                "17,5,A,30,Yes", //30
                "18,5,B,130,Yes", //30

        };

        addRuns(contest, runsData);

    }

    /**
     * Test report where results directory is from ClientSettings.
     * @throws Exception
     */
    public void testCreateResultsExportReportWithClientSettings() throws Exception {

        /**
         * target dir fir report
         */
        String dir = getOutputDataDirectory(this.getName());
        ensureDirectory(dir);
        
//        startExplorer(dir);

        Log log = new Log(dir, getName() + ".log");
        StaticLog.setLog(log);

        IInternalContest contest = sampleContest.createStandardContest();
        IInternalController controller = new InternalController(contest);

        addRuns(contest);

        /**
         * Must put output directory into client settings
         */
        ClientSettings clientSettings = contest.getClientSettings();
        if (clientSettings == null) {
            clientSettings = new ClientSettings(contest.getClientId());
        }
        clientSettings.put(ClientSettings.PC2_RESULTS_DIR, dir);
        contest.updateClientSettings(clientSettings);

        /**
         * Export pc2 results to pc2ResultsDir 
         */

        ResultsExportReport report = new ResultsExportReport();
        report.setContestAndController(contest, controller);

        String reportFile = dir + File.separator + "results.report.txt";
        report.createReportFile(reportFile, new Filter());

        String resultsReportFile = dir + File.separator + "results.report.txt";
        assertFileExists(resultsReportFile, "Results report");
    }

    /**
     * Test report with results directory in constructor.
     * 
     * @throws Exception
     */
    public void testCreateResultsExportReport() throws Exception {

        /**
         * target dir fir report
         */
        String dir = getOutputDataDirectory(this.getName());
        ensureDirectory(dir);

        //        startExplorer(dir);

        Log log = new Log(dir, getName() + ".log");
        StaticLog.setLog(log);

        IInternalContest contest = sampleContest.createStandardContest();
        IInternalController controller = new InternalController(contest);

        String pc2ResultsDir = dir;

        /**
         * Export pc2 results to pc2ResultsDir 
         */

        ResultsExportReport report = new ResultsExportReport(contest, controller, pc2ResultsDir);
        report.setContestAndController(contest, controller);

        String reportFile = dir + File.separator + "results.report.txt";
        report.createReportFile(reportFile, new Filter());

        String resultsReportFile = dir + File.separator + "results.report.txt";
        assertFileExists(resultsReportFile, "Results report");

        //        catFile (resultsReportFile);

        assertFileExists(dir + File.separator + "results.tsv", "TODO");
        assertFileExists(dir + File.separator + "scoreboard.json", "TODO");

    }

    /**
     * Write file contents to console (System.out)
     * @param resultsReportFile
     * @throws IOException 
     */
    public void catFile(String resultsReportFile) throws IOException {
        String[] lines = Utilities.loadFile(resultsReportFile);
        for (String line : lines) {
            System.out.println(line);
        }
    }
}
