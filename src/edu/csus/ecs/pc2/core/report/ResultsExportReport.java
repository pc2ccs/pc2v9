// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

/**
 * Export Results files report.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class ResultsExportReport implements IReport {

    private String pc2ResultsDir = null;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    /**
     * 
     */
    private static final long serialVersionUID = -796328654541676730L;

    public ResultsExportReport(IInternalContest contest, IInternalController controller, String pc2ResultsDir) {
        super();
        this.pc2ResultsDir = pc2ResultsDir;
        setContestAndController(contest, controller);
    }

    public ResultsExportReport() {
        ;
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();

        /**
         * Fetch directories from ClientSettings.
         */
        ClientSettings settings = contest.getClientSettings(contest.getClientId());

        if (pc2ResultsDir == null) {
            String pc2rsdir = settings.getProperty(ClientSettings.PC2_RESULTS_DIR);
            pc2ResultsDir = pc2rsdir;
        }
    }

    @Override
    public String getPluginTitle() {
        return "Results Compare Report";
    }

    @Override
    public void createReportFile(String filename, Filter inFilter) throws IOException {
        
        if (pc2ResultsDir == null) {
            // write to reports dir, if no dir specified and noe dir from ClientSettings
            File outFile = new File(filename);
            
            pc2ResultsDir = outFile.getParent();
            ExecuteUtilities.ensureDirectory(pc2ResultsDir);
        }
        
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
            
        }

        catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }
    }

    
    public String getReportFileName (IReport selectedReport, String extension) {
       
        return getReportBaseFileName(selectedReport, "txt");
    }
    
    public String getAltReportDirname (IReport selectedReport, String extension) {
        
        return getReportBaseFileName(selectedReport, "files");
    }
    
    public String getReportBaseFileName(IReport selectedReport, String extension) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        
        if (extension != null &&  extension.length() >= 0) {
            extension = "." + extension;
        }
            
        return "report." + reportName + "." + simpleDateFormat.format(new Date()) + extension;
    }
    
    @Override
    public String[] createReport(Filter filter) {
        
        List<String> outList = new ArrayList<String>();
        
        String resultsFilename = getPc2ResultsDir() + File.separator + ResultsFile.RESULTS_FILENAME;

        String scoreboardJsonFilename = pc2ResultsDir + File.separator + Constants.SCOREBOARD_JSON_FILENAME;

        String awardsFileName = pc2ResultsDir + File.separator + Constants.AWARDS_JSON_FILENAME;

        /**
         * Export results.tsv
         */
        
        IReport report = new ResultsTSVReport();
        String reportMessage = writeReportFile (report, resultsFilename);
        outList.add(reportMessage);
        
        /**
         * Export scoreboard.json
         */
        
        report = new ScoreboardJSONReport();
        reportMessage = writeReportFile(report, scoreboardJsonFilename);
        outList.add(reportMessage);
        
        // TODO 760 output results.tsv
        
        // TODO 760 results.csv file if/when available Issue 351
        // TODO 351 results.csv file if/when available Issue 351
        
        // TODO 760 awards.json add when available Issue 383, CLICS Add awards.json file/report #383
        
        String finalizedStatus = "No.  (Warning - contest is not finalized)";
        
        FinalizeData finalizedDAta = contest.getFinalizeData();
        if (finalizedDAta != null) {
            if ( finalizedDAta.isCertified() ) {
                
                finalizedStatus = "Yes.  Finalized at "+
                        finalizedDAta.getCertificationDate();
            }
        }
        
        
        
        String[] reportLinss = { //

                "pc2 results dir       : " + getPc2ResultsDir(), //
                "", //
                "Contest finalized     : "+finalizedStatus, //
                "", //

        };
        
        outList.addAll(0, Arrays.asList(reportLinss));
        return (String[]) outList.toArray(new String[outList.size()]);

    }

    private String writeReportFile(IReport report, String outputFilename) {

        try {

            report.setContestAndController(contest, controller);
            report.createReportFile(outputFilename, filter);

            return "Wrote " + report.getReportTitle() + " to " + outputFilename;

        } catch (Exception e) {
            log.log(Level.WARNING, "Error writing report " + report.getPluginTitle() + e.getMessage(), e);
            return "Unable to write report " + report.getPluginTitle() + " " + e.getMessage();
        }

    }

    @Override
    public String createReportXML(Filter filter) throws IOException {
        throw new RuntimeException("createReportXML not implemented");
    }

    @Override
    public void writeReport(PrintWriter printWriter) throws Exception {

        try {

            if (pc2ResultsDir == null || !(new File(pc2ResultsDir).isDirectory())) {
                throw new RuntimeException("pc2 Results directory not defined or not a directory");
            }

            String[] lines = createReport(filter);

            for (String line : lines) {
                printWriter.println(line);
            }

        }
        catch (RuntimeException rte) {
            log.log(Log.INFO, "Exception writing report", rte);
            printWriter.println("Error/problem generating report " + rte.getMessage());
        }
    }

    @Override
    public String getReportTitle() {
        return "Results Compare Report";
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public void printHeader(PrintWriter printWriter) {

        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");

    }

    @Override
    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report: " + getReportTitle());

    }

    public String getPc2ResultsDir() {
        return pc2ResultsDir;
    }
}
