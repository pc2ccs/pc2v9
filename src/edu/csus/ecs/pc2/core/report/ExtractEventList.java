package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.playback.PlaybackEvent;
import edu.csus.ecs.pc2.core.model.playback.PlaybackManager;

/**
 * Print all problems info.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExtractEventList implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 977130815676827828L;

    private static final String DELIMITER = "|";

    private IInternalContest contest;

    private IInternalController controller;
    
    private String extractDirectory;
    
    private String reportFilename;

    private Log log;

    private Filter filter;
    
    private String reportDirectory = "reports";


    private void writeRow(PrintWriter printWriter, Run run) throws Exception {

        writeValues(printWriter, PlaybackManager.ACTION_KEY, PlaybackEvent.Action.RUN_SUBMIT.toString());
        writeValues(printWriter, PlaybackManager.ID_KEY, run.getNumber());
        writeValues(printWriter, PlaybackManager.ELAPSED_KEY, run.getElapsedMins());
        writeValues(printWriter, PlaybackManager.LANGUAGE_KEY, contest.getLanguage(run.getLanguageId()).getDisplayName());
        writeValues(printWriter, PlaybackManager.PROBLEM_KEY, contest.getProblem(run.getProblemId()).getDisplayName());
        writeValues(printWriter, PlaybackManager.SITE_KEY, run.getSiteNumber());
        writeValues(printWriter, PlaybackManager.SUBMIT_CLIENT_KEY, run.getSubmitter().getName());
        
        printWriter.flush();
        
        RunFiles runFiles = contest.getRunFiles(run);
        SerializedFile mainfile = runFiles.getMainFile();
        String mainFileName = mainfile.getName();
        
        String targetDirectory = extractDirectory + File.separator + "site" + run.getSiteNumber() + "run" + run.getNumber();
        if (!new File(targetDirectory).isDirectory()) {
            new File(targetDirectory).mkdirs();
        }
        
        String outputFileName = targetDirectory + File.separator + mainFileName; 
        
        mainfile.writeFile(outputFileName);

        // remove report/ from targetdirectory to make it relative to the load list file (report file)
        outputFileName = removeUpTo(targetDirectory, File.separator).substring(1) + File.separator + mainFileName;
        
        writeValues(printWriter, PlaybackManager.MAINFILE_KEY, outputFileName);
        
        printWriter.println();
    }

    private void writeValues(PrintWriter printWriter, String key, long number) {
        printWriter.print(key + "=" + number + DELIMITER + " ");

    }

    private void writeValues(PrintWriter printWriter, String key, String value) {
        printWriter.print(key + "=" + value + DELIMITER + " ");
    }

    public void writeReport(PrintWriter printWriter) {

        printWriter.println();

        Run[] runs = contest.getRuns();

        Arrays.sort(runs, new RunComparator());

        printWriter.println("------------------------------------------------------------");
        printWriter.println();
        printWriter.println("# "+getReportTitle()); 
        printWriter.println("# Created on "+new Date());
        printWriter.println("# Created by: "+new VersionInfo().getSystemVersionInfo());
        
        printWriter.println();
        
        Vector<String> failedRunExtract = new Vector<String>();

        extractDirectory = removeUpTo(reportFilename, reportDirectory+File.separator) + ".files";

        for (Run run : runs) {
            try {
                writeRow(printWriter, run);
            } catch (Exception e) {
                failedRunExtract.add("Failed run: " + run);
                printWriter.println();
                printWriter.println("# error extracting run " + run + " " + e.getMessage());
            }
        }
        
        String [] list = (String[]) failedRunExtract.toArray(new String[failedRunExtract.size()]);
        
        printWriter.println();
        printWriter.println("# "+list.length+" errors extracting runs");
        
        for (String s : list){
            printWriter.println("# "+s);
        }
        
        printWriter.println();
        printWriter.println("# EOF "+getReportTitle()); 
        printWriter.println("------------------------------------------------------------");

    }

    protected String removeUpTo(String source, String targetString) {

        int index = source.indexOf(targetString);

        if (index > 0) {
            String debug2 = source.substring(index);
            return debug2;
        } else {
            return source;
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {
        
        reportFilename = filename;

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(reportFilename, false), true);

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
        return "Playback List";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Playback List Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
