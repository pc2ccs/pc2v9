package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;

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

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;
    
    private Filter filter;
    
    public void writeReport(PrintWriter printWriter) {
        
        IReport[] listOfReports;
        
        listOfReports = new IReport[14];
        int repNo = 0;
        
        listOfReports[repNo++] = new ContestAnalysisReport();
        listOfReports[repNo++] = new SolutionsByProblemReport();
        listOfReports[repNo++] = new ListRunLanguages();
        listOfReports[repNo++] = new FastestSolvedReport();
        
        listOfReports[repNo++] = new RunsByTeamReport();
        
        listOfReports[repNo++] = new RunsReport();
        
        listOfReports[repNo++] = new ProblemsReport();
        listOfReports[repNo++] = new LanguagesReport();
        listOfReports[repNo++] = new AccountsReport();
        
        listOfReports[repNo++] = new ClarificationsReport();
        listOfReports[repNo++] = new OldRunsReport();
        
        listOfReports[repNo++] = new ProfilesReport();
        
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

    private void printHeader(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println("On: "+Utilities.getL10nDateTime());
        printWriter.println();
        
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end "+getReportTitle()+" report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        filter = inFilter;

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

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "All Reports Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
