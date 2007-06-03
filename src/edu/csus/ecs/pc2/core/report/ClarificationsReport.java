package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * Print list of clarifications.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

public class ClarificationsReport implements IReport {

    private IContest contest;

    private IController controller;

    private Log log;

    private void writeReport(PrintWriter printWriter) {
        
        // Clarifications
        printWriter.println();
        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());
        printWriter.println("-- " + clarifications.length + " clarifications --");
        for (Clarification clarification : clarifications) {
            
            printWriter.println();
            printWriter.println("  Clarification "+clarification.getNumber()+" (Site "+clarification.getSiteNumber()+") "+clarification.getElementId());
            printWriter.println("         Elapsed : "+clarification.getElapsedMins());
            printWriter.println("         State   : "+clarification.getState());
            printWriter.println("         To ALL? : "+clarification.isSendToAll());
            printWriter.println("         Question: "+clarification.getQuestion());
            printWriter.println("         Answer  : "+clarification.getAnswer());
            
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

    public void createReportFile(String filename, Filter filter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {
            printHeader(printWriter);

            try {
                writeReport(printWriter);
            } catch (Exception e) {
                printWriter.println("Exception in report: "+e.getMessage());
                e.printStackTrace(printWriter);
            }

            printFooter(printWriter);

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
        }
    }

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Clarifications";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Clarifications Report";
    }

}
