package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.ScoreboardFile;

/**
 * scoreboard.tsv report/output.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ScoreboardTSVReport implements IReportFile {

    /**
     * 
     */
    private static final long serialVersionUID = 5168385483863061398L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    @Override
    public String getPluginTitle() {
        return "scoreboard.tsv";
    }

    @Override
    public void createReportFile(String filename, Filter aFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {
            try {
                printHeader(printWriter);

                writeReport(printWriter);

                printFooter(printWriter);

                printWriter.close();
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
        }

    }

    @Override
    public String[] createReport(Filter aFilter) {
        try {
            ScoreboardFile scoreboardFile = new ScoreboardFile();
            String[] lines = scoreboardFile.createTSVFileLines(contest);
            return lines;
        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            return new String [] { "Exception writing report "+e.getMessage() };
        }
    }

    @Override
    public String createReportXML(Filter aFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    @Override
    public void writeReport(PrintWriter printWriter) throws Exception {

        ScoreboardFile scoreboardFile = new ScoreboardFile();
        String[] lines = scoreboardFile.createTSVFileLines(contest);
        for (String string : lines) {
            printWriter.println(string);
        }
    }

    @Override
    public String getReportTitle() {
        return "scoreboard.tsv";
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(Filter filter) {

    }

    @Override
    public void printHeader(PrintWriter printWriter) {

    }

    @Override
    public void printFooter(PrintWriter printWriter) {

    }

    @Override
    public boolean suppressHeaderFooter() {
        return true;
    }
}
