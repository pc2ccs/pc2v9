package edu.csus.ecs.pc2.core.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.log.Log;

/**
 * Internal dump report.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class ClarificationsReport implements IReport {

    private IModel model;

    private IController controller;

    private Log log;

    private void writeReport(PrintWriter printWriter) {
        
        // Clarifications
        printWriter.println();
        Clarification[] clarifications = model.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());
        printWriter.println("-- " + clarifications.length + " clarifications --");
        for (Clarification clarification : clarifications) {
            
            printWriter.println();
            printWriter.println("  Clarification "+clarification.getNumber()+" (Site "+clarification.getSiteNumber()+") "+clarification.getElementId());
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

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Clarifications Report";
    }

}
