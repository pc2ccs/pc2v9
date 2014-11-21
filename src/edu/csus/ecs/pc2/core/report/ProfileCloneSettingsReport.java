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
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;

/**
 * Print All Group Information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileCloneSettingsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -287199138291014045L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    public void writeReport(PrintWriter printWriter) {

        ProfileCloneSettings settings = contest.getProfileCloneSettings();
        if (settings != null){
            printCloneSettings(printWriter, settings);
        } else {
            printWriter.println("No Profile Clone settings (not a cloned profile)");
        }
        
        printWriter.println();
        
        /**
         * Print all profiles too
         */
        
        printWriter.println();
        ProfilesReport profilesReport = new ProfilesReport();
        profilesReport.setContestAndController(contest, controller);
        profilesReport.writeReport(printWriter);
        profilesReport = null;
        
    }

    public void printCloneSettings(PrintWriter printWriter, ProfileCloneSettings settings) {
        
        printWriter.println("               Name : " + settings.getName());
        printWriter.println("        Description : " + settings.getDescription());
        printWriter.println();
        printWriter.println("Cloned from profile : " + settings.getSourceProfile());

        printWriter.println();

        printWriter.println("   Copy Accounts       " + settings.isCopyAccounts());
        printWriter.println("   Copy Contest Settings " + settings.isCopyContestSettings());
        printWriter.println("   Copy Groups         " + settings.isCopyGroups());
        printWriter.println("   Copy Judgements     " + settings.isCopyJudgements());
        printWriter.println("   Copy Languages      " + settings.isCopyLanguages());
        printWriter.println("   Copy Notifications  " + settings.isCopyNotifications());
        printWriter.println("   Copy Problems       " + settings.isCopyProblems());
        printWriter.println("   Copy Runs           " + settings.isCopyRuns());
        printWriter.println("   Copy Clarifications " + settings.isCopyClarifications());
        
       
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
        return "Active Profile Clone Settings";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Profile Clone Settings Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
