package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.playback.PlaybackRecord;
import edu.csus.ecs.pc2.core.model.playback.ReplayEvent;
import edu.csus.ecs.pc2.core.model.playback.ReplayEventDetails;

/**
 * Dump Playback information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PlaybackDumpReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 2976642766561948728L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Playback Dump Info";
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {

            try {
                printHeader(printWriter);

                writeReport(printWriter);

                printFooter(printWriter);

            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

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
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Playback Dump Info Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void writeReport(PrintWriter printWriter) {

        PlaybackInfo[] playbackInfos = contest.getPlaybackInfos();

        printWriter.println();
        printWriter.println("-- " + playbackInfos.length + " playbacks --");

        for (PlaybackInfo playbackInfo : playbackInfos) {
            printWriter.println();
            writeRow(printWriter, playbackInfo);

            PlaybackRecord[] records = contest.getPlaybackManager().getPlaybackRecords();
            dumpPlaybackRecords(printWriter, records);
        }
    }

    private void dumpPlaybackRecords(PrintWriter printWriter, PlaybackRecord[] records) {

        printWriter.println("   -- " + records.length + " playback records --");

        for (PlaybackRecord record : records) {
            printWriter.print("   " + record.getSequenceNumber());
            printWriter.print(" " + record.getEventStatus());
            printWriter.print(" " + record.getEventType());

            ReplayEvent event = record.getReplayEvent();
            printWriter.print(" " + event.getClientId());

            ReplayEventDetails details = event.getEventDetails();
            if (details == null) {
                printWriter.print(" (no event detail)");
            } else {

                Run run = details.getRun();
                if (run != null) {
                    printWriter.print(" details = " + run);
                }
                Clarification clar = details.getClarification();
                if (clar != null) {
                    printWriter.print(" details = " + clar);
                }
            }
            printWriter.println();
        }

    }

    private void writeRow(PrintWriter printWriter, PlaybackInfo playbackInfo) {

        printWriter.println("   started      : " + Utilities.yesNoString(playbackInfo.isStarted()));
        printWriter.println("   date started : " + playbackInfo.getDateStarted());
        printWriter.println("   element id    : " + playbackInfo.getElementId());

    }

    private void writeContestTime(PrintWriter printWriter) {
        printWriter.println();
        GregorianCalendar resumeTime = contest.getContestTime().getResumeTime();
        if (resumeTime == null) {
            printWriter.println("Contest date/time: never started");
        } else {
            printWriter.println("Contest date/time: " + resumeTime.getTime());
        }
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");

        writeContestTime(printWriter);
        printWriter.println();
    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }
}
