// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.PrintWriter;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

public class ResultsCSVReport extends AbstractReport {

    /**
     * 
     */
    private static final long serialVersionUID = -6988627277661871941L;

    private ResultsFile resultsFile = new ResultsFile();

    @Override
    public String getReportTitle() {
        return "results.csv report";
    }

    @Override
    public String[] createReport(Filter filter) {
        return resultsFile.createCSVFileLines(getContest());
    }

    @Override
    public String getPluginTitle() {
        return "results.csv";
    }

    public void writeReport(PrintWriter printWriter) {

        printWriter.println();

        try {

            printHeader(printWriter);

            try {

                String[] lines = resultsFile.createCSVFileLines(getContest());

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
