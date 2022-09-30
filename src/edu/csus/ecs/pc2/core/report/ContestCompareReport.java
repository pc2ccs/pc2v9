// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.csus.ecs.pc2.AppConstants;
import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSEventType;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Print Contest Comparison Report.
 * 
 * @author pc2@ecs.csus.edu
 */
public class ContestCompareReport implements IReport {

    private static final long serialVersionUID = -6826589119249925151L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;
    
    private Filter filter;
    
    // Get a file name from the pc2v9.ini file, ini key server.eventFeedFile
    private String overRideEventFilename = getINIValue(AppConstants.OVERRIDE_EVENT_FEED_FILE);
    
    /**
     * Get value from .ini file if it exists.
     * 
     * @param key
     * @return
     */
    // TODO REFACTOR move somewhere like new class IniUtilities
    private final static String getINIValue(String key) {
        if (IniFile.isFilePresent()) {
            return IniFile.getValue(key);
        } else {
            return "";
        }
    }

    public void writeReport(PrintWriter printWriter) throws JsonParseException, JsonMappingException, IOException {
        
        ContestInformation contestInformation = contest.getContestInformation();
        
        printWriter.println("Shadow mode      : " + Utilities.yesNoString(contestInformation.isShadowMode()));
        printWriter.println("CCS URL          : " + contestInformation.getPrimaryCCS_URL());
        printWriter.println("CCS Login        : " + contestInformation.getPrimaryCCS_user_login());
        
        printWriter.println();
        
        ContestCompareModel comp = null;
        
        if (StringUtilities.isEmpty(overRideEventFilename)) {
            /**
             * Compare with Primary CCS event feed
             */
            comp = new ContestCompareModel(contest);
        } else {
            /**
             * Compare with event feed on disk
             */
            printWriter.println("Reading event feed from file "+overRideEventFilename);
            printWriter.println();
            
            String[] lines = Utilities.loadFile(overRideEventFilename);
            comp = new ContestCompareModel(contest, lines);
        }
        
        printWriter.println();
        
        printWriter.println("Contest models match ? " +comp.isMatch());
        
        printWriter.println();
        
        printWriter.println("Comparison Summary");

        CLICSEventType[] types = { CLICSEventType.CONTEST, CLICSEventType.TEAMS, CLICSEventType.PROBLEMS, CLICSEventType.LANGUAGES, CLICSEventType.JUDGEMENT_TYPES };

        for (CLICSEventType type : types) {
            String mess = comp.compareSummary(type.toString(), type, comp.getComparisonRecords(type));
            printWriter.println(mess);
        }

        printWriter.println();
        
        printWriter.println("Comparison Details");

        for (CLICSEventType type : types) {
            
            List<ContestCompareRecord> compList = comp.getNonMatchingComparisonRecords(type);
            printWriter.println("**   There are " + compList.size() + " different " + type + " records");

            for (ContestCompareRecord ccr : compList) {
                if (!ccr.isIdentical()) {
                    printWriter.println(ccr.getEventType() + " " + ccr.getState() + " " + ccr.getId() + " " + ccr.getFieldName() + " " + ccr.getvs());
                }
            }

            printWriter.println();
        }
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
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

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "Compare Primary with model";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Compare Primary with model Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
    
    /**
     * Read event feed from file rather than from Primary API event feed.
     * 
     * @param overRideEventFilename
     */
    public void setOverRideEventFilename(String overRideEventFilename) {
        this.overRideEventFilename = overRideEventFilename;
    }
    
    public String getOverRideEventFilename() {
        return overRideEventFilename;
    }

}
