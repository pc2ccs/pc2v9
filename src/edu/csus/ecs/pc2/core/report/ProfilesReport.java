package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.GregorianCalendar;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Pluralize;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileComparatorByName;

/**
 * Print list of profiles.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfilesReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 808321237990590312L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();
    
    private boolean usingServer = false;
    
    private void writeProfile(PrintWriter printWriter, Profile profile) {
        printWriter.println("Profile name  : " + profile.getName());
        printWriter.println("  description : " + profile.getDescription());
        printWriter.println("  create date : " + profile.getCreateDate().toString());
        printWriter.println("  site number : " + profile.getSiteNumber());
        printWriter.println("   element id : " + profile.getElementId());
        printWriter.println("       active : " + profile.isActive());
        printWriter.println("   contest id : " + profile.getContestId());
        printWriter.println("         path : " + profile.getProfilePath());
        
        if (usingServer) {
            String dirname = profile.getProfilePath();

            if (!new File(dirname).isDirectory()) {
                printWriter.println("                Profile directory does NOT exist");
            } else {
                printWriter.println("                Profile directory found");
                dirname = dirname + File.separator + "db." + contest.getSiteNumber();
                printWriter.println("      db path : " + dirname);
                if (!new File(dirname).isDirectory()) {
                    printWriter.println("                DB directory does NOT exist");
                } else {
                    printWriter.println("                DB directory found");

                }
            }
        }
        
    }

    private void writeActiveProfile(PrintWriter printWriter) {

        Profile profile = contest.getProfile();
        writeProfile(printWriter, profile);
        printWriter.println("    Contest ID: "+contest.getContestIdentifier());
    }

    public void writeReport(PrintWriter printWriter) {

        usingServer = isServer();
        
        printWriter.println("-- Active Profile");
        writeActiveProfile(printWriter);

        Profile[] profiles = contest.getProfiles();
        Arrays.sort(profiles, new ProfileComparatorByName());

        printWriter.println();
        printWriter.println("-- " + profiles.length + " "+Pluralize.simplePluralize("Profile", profiles.length)+" --");

        for (Profile profile : profiles) {
            printWriter.println();
            writeProfile(printWriter, profile);
        }
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
        
        printWriter.println();
        GregorianCalendar resumeTime = contest.getContestTime().getResumeTime();
        if (resumeTime == null) {
            printWriter.println("Contest date/time: never started");
        } else {
            printWriter.println("Contest date/time: " + resumeTime.getTime());

        }
    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {
            try {
                printHeader(printWriter);
                
                writeReport(printWriter);
                
                printFooter(printWriter);

                printWriter.close();
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
        }
    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Profiles";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Profiles Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
    
    private boolean isServer() {
        return contest.getClientId() != null && isServer(contest.getClientId());
    }

    private boolean isServer(ClientId clientId) {
        return clientId.getClientType().equals(ClientType.Type.SERVER);
    }

}
