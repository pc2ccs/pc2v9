package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Language;

/**
 * Print all languages info.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LanguagesReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 3558660040036850297L;

    private IContest contest;

    private IController controller;

    private Log log;

    private Filter filter;

    private void writeRow(PrintWriter printWriter, Language language) {
        printWriter.println("  Language  '" + language + "' v" + language.getElementId().getVersionNumber() + " id=" + language.getElementId());
        printWriter.println("    site number         : " + language.getSiteNumber());
        printWriter.println("    compiler command    : " + language.getCompileCommandLine());
        printWriter.println("    executable mask     : " + language.getExecutableIdentifierMask());
        printWriter.println("    program execute cmd : " + language.getProgramExecuteCommandLine());
    }

    public void writeReport(PrintWriter printWriter) {
        // Language
        printWriter.println();
        printWriter.println("-- " + contest.getLanguages().length + " languages --");
        for (Language language : contest.getLanguages()) {
            printWriter.println();
            writeRow(printWriter, language);
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + new Date());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {
            printHeader(printWriter);

            try {
                writeReport(printWriter);
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printFooter(printWriter);

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }
    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Languages";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Languages Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
