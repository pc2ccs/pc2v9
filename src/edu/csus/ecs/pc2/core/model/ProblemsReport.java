package edu.csus.ecs.pc2.core.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;

/**
 * Internal dump report.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class ProblemsReport implements IReport {

    private IModel model;

    private IController controller;

    private Log log;

    private void writeRow(PrintWriter printWriter, Problem problem, ProblemDataFiles problemDataFiles){
        printWriter.println("  Problem   " + problem + " id=" + problem.getElementId());
        printWriter.println("       Data file  " + problem.getDataFileName() );
        printWriter.println("       And  file  " + problem.getAnswerFileName() );
        printWriter.println("     Val cmd line " + problem.getValidatorCommandLine() );
        printWriter.println("     Val option   " + problem.getWhichPC2Validator() );
        if (problemDataFiles != null){
            SerializedFile [] judgesDataFiles = problemDataFiles.getJudgesDataFiles();
            SerializedFile [] judgesAnswerFiles = problemDataFiles.getJudgesAnswerFiles();
            
            printWriter.println("                  "+judgesDataFiles.length+" judge data files");
            printWriter.println("                  "+judgesAnswerFiles.length+" judge answer files");
            
        }else {
            printWriter.println("                  * No data files *");
        }
    }

    private void writeReport(PrintWriter printWriter) {
        

        // Problem
        printWriter.println();
        printWriter.println("-- " + model.getProblems().length + " problems --");
        for (Problem problem : model.getProblems()) {
            ProblemDataFiles problemDataFiles = model.getProblemDataFiles(problem);
            writeRow(printWriter, problem, problemDataFiles);
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

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Problems";
    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Problems Report";
    }

}
