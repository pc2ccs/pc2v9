package edu.csus.ecs.pc2.core.report;

import java.io.PrintWriter;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

public class ResultsTSVReport extends AbstractReport {

    /**
     * 
     */
    private static final long serialVersionUID = 6023408706004696580L;

    private ResultsFile resultsFile = new ResultsFile();

    @Override
    public String getReportTitle() {
        return "results.tsv report";
    }

    @Override
    public String[] createReport(Filter filter) {
        return resultsFile.createTSVFileLines(getContest());
    }

    @Override
    public String getPluginTitle() {
        return "results.tsv";
    }

    public void writeReport(PrintWriter printWriter) {

        printWriter.println();

        try {

            printHeader(printWriter);

            try {

                String[] lines = resultsFile.createTSVFileLines(getContest());

                for (String line : lines) {
                    printWriter.println(line);
                }
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printFooter(printWriter);

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
        }
    }
}
