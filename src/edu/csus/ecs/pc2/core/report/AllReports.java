package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * All Reports, very long contains most reports.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AllReports implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 7194244117875846407L;

    private IContest contest;

    private IController controller;

    private Log log;
    
    private Filter filter;
    
    public void writeReport(PrintWriter printWriter) {
        
        IReport[] listOfReports;
        
//        listOfReports = new IReport[13];
        listOfReports = new IReport[9];
        int repNo = 0;
        
//        listOfReports[repNo++] = new ContestAnalysisReport();
//        
//        listOfReports[repNo++] = new SolutionsByProblemReport();
//        listOfReports[repNo++] = new ListRunLanguages();
//        listOfReports[repNo++] = new FastestSolvedReport();
        
        listOfReports[repNo++] = new RunsByTeamReport();
        
        listOfReports[repNo++] = new RunsReport();
        
        listOfReports[repNo++] = new ProblemsReport();
        listOfReports[repNo++] = new LanguagesReport();
        listOfReports[repNo++] = new AccountsReport();
        
        listOfReports[repNo++] = new ClarificationsReport();
        listOfReports[repNo++] = new OldRunsReport();
        
        listOfReports[repNo++] = new BalloonSummaryReport();

        listOfReports[repNo++] = new JudgementReport();

        for (IReport report : listOfReports) {
            try {

                if (report != null){
                    report.setContestAndController(contest, controller);
                    printWriter.println("**** " + report.getReportTitle()+" Report");
                    report.writeReport(printWriter);
                    printWriter.println();
                }
                
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }
        }
    }

    /**
     * Read filename and output to printWriter.
     * @param printWriter
     * @param filename
     */
    private void outputFile(PrintWriter printWriter, String filename) {
        
        try {
            String [] lines = Utilities.loadFile(filename);
            for (String line : lines){
                printWriter.println(line);
            }
        } catch (IOException e) {
            printWriter.println("Exception in report: " + e.getMessage());
            e.printStackTrace(printWriter);
        }
        
        
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + new Date());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println("On: "+new Date());
        printWriter.println();
        
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end "+getReportTitle()+" report");
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
        return "All Reports";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "All Reports Report";
    }

    public Filter getFilter() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setFilter(Filter filter) {
        // TODO Auto-generated method stub
        
    }
}
