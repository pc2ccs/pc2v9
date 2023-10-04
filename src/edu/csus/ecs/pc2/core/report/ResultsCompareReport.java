// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.FieldCompareRecord;
import edu.csus.ecs.pc2.core.imports.clics.FileComparison;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.FileComparisonUtilities.AwardKey;
import edu.csus.ecs.pc2.core.report.FileComparisonUtilities.ResultTSVKey;
import edu.csus.ecs.pc2.core.report.FileComparisonUtilities.ScoreboardJSONKey;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

/**
 * Compare contest results files report.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ResultsCompareReport implements IReport {

    private String primaryCCSResultsDir = null;

    private String pc2ResultsDir = null;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    private boolean fullDetails = false;
    
    private String missingSourceMessage = ComparisonState.MISSING_SOURCE.toString(); 
    
    private String missingTargetMessage = ComparisonState.MISSING_TARGET.toString();

    /**
     * 
     */
    private static final long serialVersionUID = -796328654541676730L;

    public ResultsCompareReport(IInternalContest contest, IInternalController controller, String primaryCCSResultsDir, String pc2ResultsDir, boolean fullDetails) {
        this(contest, controller, primaryCCSResultsDir, pc2ResultsDir, fullDetails, null, null);
    }
    
    public ResultsCompareReport(IInternalContest contest, IInternalController controller, String primaryCCSResultsDir, String pc2ResultsDir, boolean fullDetails, String missingSourceMessage,
            String missingTargetMessage) {
        super();
        this.primaryCCSResultsDir = primaryCCSResultsDir;
        this.pc2ResultsDir = pc2ResultsDir;
        this.fullDetails = fullDetails;
        if (missingTargetMessage != null) {
            this.missingTargetMessage = missingTargetMessage;
        }
        if (missingSourceMessage != null) {
            this.missingSourceMessage = missingSourceMessage;
        }
        setContestAndController(contest, controller);
    }
    

    public ResultsCompareReport() {
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

        if (primaryCCSResultsDir == null) {
            String primaryResDir = settings.getProperty(ClientSettings.PRIMARY_CCS_RESULTS_DIR);
            primaryCCSResultsDir = primaryResDir;
        }

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

        catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }

    }

    private List<String> createFileComparisonReport(String filename, FileComparison fileComparison) {

        List<String> lines = new ArrayList<String>();

        lines.add("");
        lines.add("File: " + filename);
        lines.add("");

        List<FieldCompareRecord> fields = fileComparison.getComparedFields();
        long differentFields = fileComparison.getNumberDifferences();
        int totalFieldsCompared = fields.size();

        String compSummary = "FAIL there were "+differentFields + " field differences";
        if (differentFields == 0) {
            if (totalFieldsCompared == 0) {
                compSummary = "FAIL No fields were compared";
            } else {
                compSummary = "All MATCH " + totalFieldsCompared + " fields match";
            }
        }
        lines.add("  Comparision summary " + compSummary);
        lines.add("");

        String previousKey = "";
        int foundCount = 0;

        if (differentFields != 0) {

            Collections.sort(fields, new FieldCompareRecordComparator());

            for (FieldCompareRecord fieldCompareRecord : fields) {

                String theKey = fieldCompareRecord.getKey();
                if (!previousKey.equals(theKey)) {
                    previousKey = theKey;
                    foundCount = 0;
                }

                if (!fieldCompareRecord.getState().equals(ComparisonState.SAME)) {
                    
                    foundCount ++;
                    if (foundCount == 1) {
                        lines.add("");
                        lines.add("Row key: " + theKey);
                    }

                    String stateString = fieldCompareRecord.getState().toString();
                    
                    String formattedLine = String.format("   field %-18s %8s '%s' '%s'", fieldCompareRecord.getFieldName(), stateString, fieldCompareRecord.getValueOne(),
                            fieldCompareRecord.getValueTwo());

                    switch (fieldCompareRecord.getState()) {
                        case MISSING_SOURCE:
                            stateString = getMissingSourceMessage();
                            formattedLine = String.format("   field %-18s %8s '%s'", fieldCompareRecord.getFieldName(), stateString, fieldCompareRecord.getValueTwo());
                            break;
                        case MISSING_TARGET:
                            stateString = getMissingTargetMessage();
                            formattedLine = String.format("   field %-18s %8s '%s'", fieldCompareRecord.getFieldName(), stateString, fieldCompareRecord.getValueOne());
                            break;
                        case NOT_SAME:
                            stateString = "Not identical";
                            formattedLine = String.format("   field %-18s %8s '%s' '%s'", fieldCompareRecord.getFieldName(), stateString, fieldCompareRecord.getValueOne(),
                                    fieldCompareRecord.getValueTwo());
                            break;
                        default:
                            break;
                    }

                    lines.add(formattedLine);
                }
            }
        }

        return lines;
    }

    @Override
    public String[] createReport(Filter filter) {


        String[] filesToCompare = { //
                ResultsFile.RESULTS_FILENAME, //
                Constants.SCOREBOARD_JSON_FILENAME, //
                Constants.AWARDS_JSON_FILENAME, //
        };

        String sourceDir = pc2ResultsDir;
        String targetDir = primaryCCSResultsDir;
        if (targetDir == null) {
            throw new RuntimeException("Primary CCS directory is not assigned (is null)");
        }

        List<String> outList = new ArrayList<String>();

        if (new File(targetDir).isDirectory()) {

            ResultTSVKey resultTSVKey = new FileComparisonUtilities.ResultTSVKey();
            AwardKey awardsKey = new FileComparisonUtilities.AwardKey();
            ScoreboardJSONKey scoreboardKey = new FileComparisonUtilities.ScoreboardJSONKey();

            FileComparison resultsCompare = FileComparisonUtilities.createTSVFileComparison(ResultsFile.RESULTS_FILENAME, sourceDir, targetDir, resultTSVKey);
            FileComparison awardsFileCompare = FileComparisonUtilities.createAwardJSONFileComparison(Constants.AWARDS_JSON_FILENAME, sourceDir, targetDir, awardsKey);
            FileComparison scoreboardJsonCompare = FileComparisonUtilities.createScoreboardJSONFileComparison(Constants.SCOREBOARD_JSON_FILENAME, sourceDir, targetDir, scoreboardKey);

            long totalDifferences = //
                    resultsCompare.getNumberDifferences() + //
                    awardsFileCompare.getNumberDifferences() + //
                    scoreboardJsonCompare.getNumberDifferences();

            if (totalDifferences == 0) {
                outList.add("Summary:   All PASS - no differences found");
                outList.add("");
                
                outList.add(String.format("%3d", scoreboardJsonCompare.getNumberRows())+ " rows "+Constants.SCOREBOARD_JSON_FILENAME);
                outList.add(String.format("%3d", awardsFileCompare.getNumberRows())+ " rows "+Constants.AWARDS_JSON_FILENAME);
                outList.add(String.format("%3d", resultsCompare.getNumberRows())+ " rows "+ResultsFile.RESULTS_FILENAME);
            } else {
                outList.add("Summary:   FAIL - there are " + totalDifferences + "  differences found");
                outList.add("");

                outList.add(String.format("%3d", scoreboardJsonCompare.getNumberDifferences())+ " differences "+Constants.SCOREBOARD_JSON_FILENAME);
                outList.add(String.format("%3d", awardsFileCompare.getNumberDifferences())+ " differences "+Constants.AWARDS_JSON_FILENAME);
                outList.add(String.format("%3d", resultsCompare.getNumberDifferences())+ " differences "+ResultsFile.RESULTS_FILENAME);
            }


            String[] infoLines = { //
                    "", //
                    "Primary CCS results dir: " + getPrimaryCCSResultsDir(), //
                    "PC2 results dir        : " + getPc2ResultsDir(), //
                    "", //
            };
            outList.addAll(Arrays.asList(infoLines));

            if (fullDetails) {

                List<String> lines = createFileComparisonReport(ResultsFile.RESULTS_FILENAME, resultsCompare);
                outList.addAll(lines);
                lines = createFileComparisonReport(Constants.AWARDS_JSON_FILENAME, awardsFileCompare);
                outList.addAll(lines);
                lines = createFileComparisonReport(Constants.SCOREBOARD_JSON_FILENAME, scoreboardJsonCompare);
                outList.addAll(lines);
            }
        }

        return (String[]) outList.toArray(new String[outList.size()]);
    }

    @Override
    public String createReportXML(Filter filter) throws IOException {
        throw new RuntimeException("createReportXML not implemented");
    }

    @Override
    public void writeReport(PrintWriter printWriter) throws Exception {

        if (primaryCCSResultsDir == null || !(new File(primaryCCSResultsDir).isDirectory())) {
            throw new RuntimeException("Primary CCS Results directory not defined or not a directory");
        }

        if (pc2ResultsDir == null || !(new File(pc2ResultsDir).isDirectory())) {
            throw new RuntimeException("pc2 Results directory not defined or not a directory");
        }

        String[] lines = createReport(filter);

        for (String line : lines) {
            printWriter.println(line);
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

    public String getPrimaryCCSResultsDir() {
        return primaryCCSResultsDir;
    }

    public String getPc2ResultsDir() {
        return pc2ResultsDir;
    }
    
    public boolean isFullDetails() {
        return fullDetails;
    }
    
    public void setFullDetails(boolean fullDetails) {
        this.fullDetails = fullDetails;
    }
    
    public String getMissingSourceMessage() {
        return missingSourceMessage;
    }
    
    public String getMissingTargetMessage() {
        return missingTargetMessage;
    }
    
    public void setMissingSourceMessage(String missingSourceMessage) {
        this.missingSourceMessage = missingSourceMessage;
    }
    
    public void setMissingTargetMessage(String missingTargetMessage) {
        this.missingTargetMessage = missingTargetMessage;
    }
}
