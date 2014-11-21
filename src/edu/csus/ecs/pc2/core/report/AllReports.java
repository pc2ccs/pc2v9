package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.ReportNameByComparator;
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
    
    public IReport [] getAllReports(){
        Vector <IReport> reports = new Vector <IReport> ();
        
        reports.add(new AccountsReport());
        reports.add(new BalloonSummaryReport());

//        reports.add(new AllReports());
        reports.add(new ContestSettingsReport());
        reports.add(new ContestReport());

        reports.add(new ContestAnalysisReport());
        reports.add(new SolutionsByProblemReport());
        reports.add(new ListRunLanguages());
        
        reports.add(new FastestSolvedSummaryReport());
        reports.add(new FastestSolvedReport());

        reports.add(new StandingsReport());
        reports.add(new LoginReport());
        reports.add(new ProfilesReport());
        reports.add(new PluginsReport());
        
        reports.add(new RunsReport());
        reports.add(new ClarificationsReport());
        reports.add(new ProblemsReport());
        reports.add(new LanguagesReport());

        reports.add(new JudgementReport());
        reports.add(new RunsByTeamReport());
        reports.add(new BalloonSettingsReport());
        reports.add(new ClientSettingsReport());
        reports.add(new GroupsReport());

        reports.add(new EvaluationReport());

        reports.add(new OldRunsReport());
        reports.add(new RunsReport5());

        reports.add(new AccountPermissionReport());
        reports.add(new BalloonDeliveryReport());
        reports.add(new ExtractPlaybackLoadFilesReport());
        
        reports.add(new RunJudgementNotificationsReport());
        reports.add(new JudgementNotificationsReport());
        
        reports.add(new ProfileCloneSettingsReport());
        reports.add(new SitesReport());
        
        reports.add(new FinalizeReport());
        
        reports.add(new ExportYamlReport());
        
        reports.add(new InternalDumpReport());
        
        reports.add(new HTMLReport());
        
        reports.add(new CategoryReport());
        
        reports.add(new RunStatisticsReport());
        
        reports.add(new PlaybackDumpReport());
        
        reports.add(new AccountsTSVReportTeamAndJudges());

        reports.add(new AccountsTSVReport());
        
        reports.add(new SubmissionsTSVReport());
        
        reports.add(new JSONReport());

        reports.add(new EventFeed2013Report());
        
        reports.add(new UserdataTSVReport());
        
        reports.add(new GroupsTSVReport());
        
        reports.add(new TeamsTSVReport());
        
        reports.add(new ScoreboardTSVReport());

        IReport [] listOfReports = (IReport[]) reports.toArray(new IReport[reports.size()]);
        Arrays.sort(listOfReports, new ReportNameByComparator());
        return listOfReports;
    }
    
    private IReport [] getXMLReports(){
        Vector <IReport> reports = new Vector <IReport> ();
        
        reports.add(new ContestReport());
        reports.add(new StandingsReport());

        IReport [] listOfReports = (IReport[]) reports.toArray(new IReport[reports.size()]);
        Arrays.sort(listOfReports, new ReportNameByComparator());
        return listOfReports;
    }
    
    public void writeReport(PrintWriter printWriter) {
        
        IReport [] reports = getAllReports();

        for (IReport report : reports) {
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
        
        reports = getXMLReports();

        for (IReport report : reports) {
            try {

                if (report != null){
                    report.setContestAndController(contest, controller);
                    
                    printWriter.println("**** " + report.getReportTitle()+" Report");
                    printWriter.println();
                    String xmlString = report.createReportXML(filter);
                    
                    printWriter.println("-- Start XML --");
                    printWriter.println(xmlString);
                    printWriter.println();
                    printWriter.println("-- End XML --");
                    printWriter.println();
                }
                
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }
        }
        
        
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println("On: "+Utilities.getL10nDateTime());
        printWriter.println();
        
    }

    public void printFooter(PrintWriter printWriter) {
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

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
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
