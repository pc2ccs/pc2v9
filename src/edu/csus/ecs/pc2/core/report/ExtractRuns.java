package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Extract run from server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExtractRuns {

    private IInternalContest contest;

    private String extractDirectory = "extract";
    
    private VersionInfo versionInfo = new VersionInfo();

    /**
     * Create run directory name and extract run source and info into extract dir.
     * 
     * @param runId
     * @return boolean true if files were extracted
     * @throws IOException
     *             if unable to create directory or write file.
     */
    public boolean extractRun(ElementId runId) throws IOException {

        Run run = contest.getRun(runId);

        String targetDirectory = extractDirectory + File.separator + "site" + run.getSiteNumber() + "run" + run.getNumber();
        if (!new File(targetDirectory).isDirectory()) {
            new File(targetDirectory).mkdirs();
        }

        String filename = targetDirectory + File.separator + "pc2.run" + run.getNumber() + ".txt";
        writeInfoFile(filename, run);

        RunFiles runFiles = contest.getRunFiles(run);
        if (runFiles != null){
            SerializedFile serializedFile = runFiles.getMainFile();
            
            filename = targetDirectory + File.separator + serializedFile.getName();
            serializedFile.writeFile(filename);
            if (runFiles.getOtherFiles() != null){
                for (SerializedFile file : runFiles.getOtherFiles()) {
                    filename = targetDirectory + File.separator + file.getName();
                    file.writeFile(filename);
                }
            }
            return true;
            
        } else {
            return false;
        }

    }

    /**
     * 
     * @param filename
     * @param run
     * @throws FileNotFoundException
     */

    private void writeInfoFile(String filename, Run run) throws FileNotFoundException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        printWriter.println();

        ContestTime contestTime = contest.getContestTime();

        // Format for Run info
        //
        // Contest on Tue Mar 11 18:38:15 PST 2008
        // 
        // Site : 1
        // Run : 2
        // 
        // Team : 14
        // Prob : Sumit
        // Lang : Java
        // Elaps : 11
        // 
        // At : Sun Feb 24 11:26:47 PST 2008
        // 
        // Main: sumit.java
        //  
        // done at Tue Sep 30 20:00:49 PDT 2008
        printWriter.println();
        printWriter.println(versionInfo.getSystemName());
        printWriter.println(versionInfo.getSystemVersionInfo());

        if (contestTime != null) {
            printWriter.println();
            printWriter.println("Contest on " + contestTime.getResumeTime().getTime());
        }

        printWriter.println();
        printWriter.println("Run   : " + run.getNumber());
        printWriter.println("Site  : " + run.getSiteNumber());
        printWriter.println("Team  : " + run.getSubmitter());
        printWriter.println("Prob  : " + contest.getProblem(run.getProblemId()));
        printWriter.println("Lang  : " + contest.getLanguage(run.getLanguageId()));
        printWriter.println("Elaps : " + run.getElapsedMins());
        printWriter.println();
        printWriter.println("done at "+ new Date());
        
        
        printWriter.close();
    }

    public ExtractRuns(IInternalContest contest) {
        super();
        this.contest = contest;
    }

    /**
     * Get the directory name where files will be extracted.
     * 
     * @return
     */
    public String getExtractDirectory() {
        return extractDirectory;
    }

    /**
     * Set the directory where runs will be extracted
     * 
     * @param extractDirectory
     */
    public void setExtractDirectory(String extractDirectory) {
        this.extractDirectory = extractDirectory;
    }

}
