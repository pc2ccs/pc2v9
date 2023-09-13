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

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteUtilities;
import edu.csus.ecs.pc2.core.imports.clics.FieldCompareRecord;
import edu.csus.ecs.pc2.core.imports.clics.FileComparison;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.FileComparisonUtilities.AwardKey;
import edu.csus.ecs.pc2.core.report.FileComparisonUtilities.ResultTSVKey;
import edu.csus.ecs.pc2.core.report.FileComparisonUtilities.ScoreboardKey;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

/**
 * Export Results files report.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class ResultsExportReport implements IReport {
    
    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    private String pc2ResultsDir = null;
    
    private String primaryCCSResultsDir = null;

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

    }

    @Override
    public String getPluginTitle() {
        return "Results Compare Report";
    }

    @Override
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
            
        }

        catch (RuntimeException rte) {
            // TODO REFACTOR move this entire catch handling of rte into a Utility class
            Throwable throwable = rte;
            if (rte.getCause() != null) {
                throwable = rte.getCause();
            }
            log.log(Log.INFO, "Exception writing report", throwable);
            printWriter.println("Exception generating report " + rte.getMessage());
            throwable.printStackTrace(printWriter);
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
    
    public String getFileName(IReport selectedReport, String extension) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS"); // or maybe? "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return "report." + reportName + "." + simpleDateFormat.format(new Date()) + "." + extension;
    }
    
    @Override
    public String[] createReport(Filter filter) {
        
        List<String> outList = new ArrayList<String>();
        
        
        String resultsFilename = getPc2ResultsDir() + File.separator + ResultsFile.RESULTS_FILENAME;
        String scoreboardJsonFilename = getPc2ResultsDir() + File.separator + Constants.SCOREBOARD_JSON_FILENAME;
        String awardsFileName = getPc2ResultsDir() + File.separator + Constants.AWARDS_JSON_FILENAME;
        
        ExportFilesUtiltiites.writeResultsFiles(contest, getPc2ResultsDir());

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
                "Wrote results files to:", //
                resultsFilename, //
                scoreboardJsonFilename, //
                awardsFileName, //
                "", //

        };
        
        outList.addAll(0, Arrays.asList(reportLinss));
        
        String sourceDir = getPc2ResultsDir();
        String targetDir = getPrimaryCCSResultsDir(); 

        ResultTSVKey resultTSVKey = new FileComparisonUtilities.ResultTSVKey();

        AwardKey awardsKey = new FileComparisonUtilities.AwardKey();

        ScoreboardKey scoreboardKey = new FileComparisonUtilities.ScoreboardKey();

        FileComparison resultsCompare = FileComparisonUtilities.createTSVFileComparison(ResultsFile.RESULTS_FILENAME, sourceDir, targetDir, resultTSVKey);
        List<String> lines = createFileComparisonReport(ResultsFile.RESULTS_FILENAME, resultsCompare);

        outList.addAll(lines);

        FileComparison awardsFileCompare = FileComparisonUtilities.createJSONFileComparison(Constants.AWARDS_JSON_FILENAME, sourceDir, targetDir, awardsKey);
        lines = createFileComparisonReport(Constants.AWARDS_JSON_FILENAME, awardsFileCompare);
        
        outList.addAll(lines);

        FileComparison scoreboardJsonCompare = FileComparisonUtilities.createJSONFileComparison(Constants.SCOREBOARD_JSON_FILENAME, sourceDir, targetDir, scoreboardKey);
        lines = createFileComparisonReport(Constants.SCOREBOARD_JSON_FILENAME, scoreboardJsonCompare);

        outList.addAll(lines);

        return (String[]) outList.toArray(new String[outList.size()]);
    }

    private List<String> createFileComparisonReport(String filename, FileComparison fileComparison) {
        
        List<String> lines = new ArrayList<String>();
        
        lines.add("File: "+filename);
        lines.add("");
        
        List<FieldCompareRecord> fields = fileComparison.getComparedFields();
        long differentFields = fileComparison.getNumberDifferences();
        int totalFieldsCompared = fields.size();
        
        String compSummary = "FAILED "+differentFields+" differences";
        if (differentFields == 0)
        {
            if (totalFieldsCompared == 0) {
                compSummary = "FILE no fields compared";
            } else {
                compSummary = "PASS all "+totalFieldsCompared+ " match";
            }
        }
        lines.add("  Summary  "+compSummary);
        
        lines.add("");
        return lines;
    }

    @Override
    public String createReportXML(Filter filter) throws IOException {
        throw new RuntimeException("createReportXML not implemented");
    }

    @Override
    public void writeReport(PrintWriter printWriter) throws Exception {

        try {

            String[] lines = createReport(filter);

            for (String line : lines) {
                printWriter.println(line);
            }

        }
        catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Error/problem generating report " + e.getMessage());
            e.printStackTrace(printWriter);
        }
    }

    @Override
    public String getReportTitle() {
        return "Results Export Files";
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
        if (pc2ResultsDir == null) {
//            ExecuteUtilities.ensureDirectory(Constants.REPORT_DIRECTORY_NAME);
            pc2ResultsDir = Constants.REPORT_DIRECTORY_NAME + File.separator +getOutputDirectoryName();
            ExecuteUtilities.ensureDirectory(pc2ResultsDir);
        }
        return pc2ResultsDir;
    }

    /**
     * Report output directory name.
     */
    public String getOutputDirectoryName() {
        String reportFilename = getFileName(this, "txt");
        String string = StringUtilities.removeUpTo(reportFilename, Constants.REPORT_DIRECTORY_NAME +File.separator) + ".files";;
        return string;
    }

    public String getPrimaryCCSResultsDir() {
        return primaryCCSResultsDir;
    }
    
    public void setPrimaryCCSResultsDir(String primaryCCSResultsDir) {
        this.primaryCCSResultsDir = primaryCCSResultsDir;
    }
}
