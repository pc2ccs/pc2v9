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
 * Create a Replay/Playback list and team source files.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExtractPlaybackLoadFilesReport implements IReport {

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
    
    private String universalFileSeparator = "/";


    private void writeRow(PrintWriter printWriter, Run run) throws Exception {

        RunFiles runFiles = contest.getRunFiles(run);
        SerializedFile mainfile = runFiles.getMainFile();
        String mainFileName = mainfile.getName();
        
        String targetDirectory = extractDirectory + universalFileSeparator + "site" + run.getSiteNumber() + "run" + run.getNumber();
        if (!new File(targetDirectory).isDirectory()) {
            new File(targetDirectory).mkdirs();
        }
        
        String outputFileName = targetDirectory + universalFileSeparator + mainFileName; 
        
        mainfile.writeFile(outputFileName);

        // remove report/ from targetdirectory to make it relative to the load list file (report file)
        outputFileName = removeUpTo(targetDirectory, File.separator).substring(1) + universalFileSeparator + mainFileName;
        
        writeValues(printWriter, PlaybackManager.ACTION_KEY, PlaybackEvent.Action.RUN_SUBMIT.toString());
        writeValues(printWriter, PlaybackManager.ID_KEY, run.getNumber());
        writeValues(printWriter, PlaybackManager.ELAPSED_KEY, run.getElapsedMins());
        writeValues(printWriter, PlaybackManager.LANGUAGE_KEY, contest.getLanguage(run.getLanguageId()).getDisplayName());
        writeValues(printWriter, PlaybackManager.PROBLEM_KEY, contest.getProblem(run.getProblemId()).getDisplayName());
        writeValues(printWriter, PlaybackManager.SITE_KEY, run.getSiteNumber());
        writeValues(printWriter, PlaybackManager.SUBMIT_CLIENT_KEY, run.getSubmitter().getName());
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

        printWriter.println("# ------------------------------------------------------------");
        printWriter.println();
        printWriter.println("# "+getReportTitle()); 
        printWriter.println("# Created on "+new Date());
        printWriter.println("# Created by: "+new VersionInfo().getSystemVersionInfo());
        
        printWriter.println();
        
        Vector<String> failedRunExtract = new Vector<String>();

        extractDirectory = removeUpTo(reportFilename, reportDirectory+File.separator) + ".files";

        int nonExtractedRuns = 0;
        
        for (Run run : runs) {
            try {
                if (isThisSite(run.getSiteNumber())){
                    writeRow(printWriter, run);
                } else {
                    printWriter.println("# Remote run site "+run.getSiteNumber()+" not extracted "+run);
                    failedRunExtract.add("Remote run: " + run);
                    nonExtractedRuns++;
                }
            } catch (Exception e) {
                failedRunExtract.add("Failed run: " + run);
                nonExtractedRuns++;
                printWriter.println("# error extracting run " + run + " " + e.getMessage());
            }
        }
        
        String [] list = (String[]) failedRunExtract.toArray(new String[failedRunExtract.size()]);
        
        printWriter.println();
        if (nonExtractedRuns > 0){
            printWriter.println("# "+(nonExtractedRuns)+" extracting runs NOT extracted");
            for (String s : list){
                printWriter.println("# "+s);
            }
        } else {
            printWriter.println("# All "+(list.length)+" runs extracted");
            
        }
        
        printWriter.println();
        printWriter.println("# EOF "+getReportTitle()); 
        printWriter.println("# ------------------------------------------------------------");

    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == contest.getSiteNumber();
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
//        printWriter.println(new VersionInfo().getSystemName());
//        printWriter.println("Date: " + Utilities.getL10nDateTime());
//        printWriter.println(new VersionInfo().getSystemVersionInfo());
//        printWriter.println();
        printWriter.println("# " +getReportTitle() + " Report");
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("# end ");
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
        return "Extract Replay Runs";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Extract Replay Runs";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
