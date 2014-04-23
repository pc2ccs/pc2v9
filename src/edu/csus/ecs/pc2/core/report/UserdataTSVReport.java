package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.Userdata;

/**
 * userdata.tsv report/output.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class UserdataTSVReport implements IReportFile {

    /**
     * 
     */
    private static final long serialVersionUID = -5820127797123643672L;

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
        return "userdata.tsv";
    }

    @Override
    public void createReportFile(String filename, Filter filter) throws IOException {

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
    public String[] createReport(Filter filter) {
        Userdata userdata = new Userdata();
        String[] lines = userdata.getUserData(contest);
        return lines;
    }

    @Override
    public String createReportXML(Filter filter) throws IOException {
        return null;
    }

    @Override
    public void writeReport(PrintWriter printWriter) throws Exception {

        Userdata userdata = new Userdata();
        String[] lines = userdata.getUserData(contest);
        for (String string : lines) {
            printWriter.println(string);
        }
    }

    @Override
    public String getReportTitle() {
        return "userdata.tsv";
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
