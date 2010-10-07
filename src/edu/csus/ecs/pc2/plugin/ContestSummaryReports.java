package edu.csus.ecs.pc2.plugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.core.Plugin;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.report.BalloonSummaryReport;
import edu.csus.ecs.pc2.core.report.ClarificationsReport;
import edu.csus.ecs.pc2.core.report.ContestAnalysisReport;
import edu.csus.ecs.pc2.core.report.ContestReport;
import edu.csus.ecs.pc2.core.report.ContestSettingsReport;
import edu.csus.ecs.pc2.core.report.EvaluationReport;
import edu.csus.ecs.pc2.core.report.FastestSolvedReport;
import edu.csus.ecs.pc2.core.report.FastestSolvedSummaryReport;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.report.LanguagesReport;
import edu.csus.ecs.pc2.core.report.ListRunLanguages;
import edu.csus.ecs.pc2.core.report.OldRunsReport;
import edu.csus.ecs.pc2.core.report.ProblemsReport;
import edu.csus.ecs.pc2.core.report.RunsByTeamReport;
import edu.csus.ecs.pc2.core.report.RunsReport;
import edu.csus.ecs.pc2.core.report.SolutionsByProblemReport;
import edu.csus.ecs.pc2.core.report.StandingsReport;

/**
 * Save selected reports to disk.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestSummaryReports extends Plugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3201034876635916394L;

    private IReport[] listOfReports = null;

    private String reportDirectory = null;

    private Filter filter = new Filter();

    /**
     * Key for number of remaining minutes. 
     */
    public static final String BEFORE_END_ELAPSED_TIME_MINS = "minsbeforeend";

    public ContestSummaryReports() {
        loadDefaultReport();
        addProperty(BEFORE_END_ELAPSED_TIME_MINS, new Integer(30));
    }

    private void loadDefaultReport() {
        // populate list of reports
        Vector<IReport> reports = new Vector<IReport>();

        reports.add(new FastestSolvedSummaryReport());
        reports.add(new FastestSolvedReport());

        reports.add(new BalloonSummaryReport());
        reports.add(new ContestReport());

        reports.add(new ContestAnalysisReport());
        reports.add(new SolutionsByProblemReport());
        reports.add(new RunsByTeamReport());

        reports.add(new ListRunLanguages());

        reports.add(new StandingsReport());

        reports.add(new RunsReport());
        reports.add(new ClarificationsReport());
        reports.add(new ProblemsReport());
        reports.add(new LanguagesReport());

        reports.add(new EvaluationReport());

        reports.add(new OldRunsReport());
        reports.add(new ContestSettingsReport());

        listOfReports = (IReport[]) reports.toArray(new IReport[reports.size()]);
    }

    @Override
    public String getPluginTitle() {
        return "Save Contest Reports";
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    public static String getFileName(IReport selectedReport) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return "report." + reportName + "." + simpleDateFormat.format(new Date()) + ".txt";
    }

    /**
     * Generate Reports.
     * 
     * Will create the report directory if it does not exist.
     * 
     * @throws IOException
     */
    public void generateReports() {

        File reportDirectoryFile = new File(getReportDirectory());
        reportDirectoryFile.mkdirs();

        for (IReport report : listOfReports) {

            String filename = getFileName(report);

            try {
                report.setContestAndController(getContest(), getController());

                filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;

                report.createReportFile(filename, filter);
            } catch (IOException e) {
                logException("Exception printing report to " + filename, e);
            }
        }
    }

    private void logException(String string, Exception e) {

        if (getController().getLog() == null) {
            System.err.println(string);
            e.printStackTrace(System.err);
        } else {
            getController().getLog().log(Log.WARNING, string, e);
        }
    }

    /**
     * Get the report directory.
     * 
     * If the reportDirectory exists and is a directory use that. <br>
     * If the reportDirectory is not null but does not exist will use <profile path>/<reportDirector> <br>
     * If the reportDirectory is null use a default directory <profile path>/reports <br>
     * 
     * @return directory name where reports will be written.
     */
    public String getReportDirectory() {
        if (reportDirectory != null) {
            if (new File(reportDirectory).isDirectory()) {
                return reportDirectory;
            } else {
                return getContest().getProfile().getProfilePath() + File.separator + reportDirectory;
            }

        } else {
            return getContest().getProfile().getProfilePath() + File.separator + "reports";
        }
    }

    
    /**
     * Set alternate reports to be created 
     * @param reports
     */
    public void setReportList(IReport[] reports) {
        listOfReports = reports;
    }

    public IReport [] getReportList() {
        return listOfReports;
    }

    /**
     * Set alternative report output directory.
     * @param reportDirectory
     */
    public void setReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public boolean isLateInContest() {

        Integer minutes = (Integer) getPluginProperties().get(BEFORE_END_ELAPSED_TIME_MINS);
        long remainingSecs = getContest().getContestTime().getRemainingSecs();
        
        return (remainingSecs / 60) < (minutes * 60);
    }

}
