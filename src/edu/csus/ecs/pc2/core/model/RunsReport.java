package edu.csus.ecs.pc2.core.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;

/**
 * Internal dump report.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class RunsReport implements IReport {

    private IModel model;

    private IController controller;

    private Log log;

    private void writeReport(PrintWriter printWriter) {
        
        // Runs
        printWriter.println();
        Run[] runs = model.getRuns();
        Arrays.sort(runs, new RunComparator());
        printWriter.println("-- " + runs.length + " runs --");
        for (Run run : runs) {
            printWriter.println("  Run " + run.getNumber()+" (Site "+run.getSiteNumber()+") "+run.getElementId());
            printWriter.println("    At   : "+run.getElapsedMins());
            printWriter.println("    State: "+run.getStatus());
            if ( run.isJudged() ) {
                printWriter.println("    Judgement   : "+run.getJudgementRecord().getJudgementId());
                printWriter.println("    By          : "+run.getJudgementRecord().getJudgerClientId());
                printWriter.println("    When judged : "+run.getJudgementRecord().getWhenJudgedTime());
                printWriter.println("    Judg Mins   : "+run.getJudgementRecord().getJudgedMinutes());
                printWriter.println("    Judgement Id: "+run.getJudgementRecord().getJudgementId());
            }
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
            printWriter.println("Exception generating report " + e.getMessage());
        }
    }

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Runs";
    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Runs Report";
    }

}
