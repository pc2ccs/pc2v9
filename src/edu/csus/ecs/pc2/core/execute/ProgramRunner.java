// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Actually runs the program.
 * 
 * @author ICPC
 *
 */
public class ProgramRunner {

    
    private IOCollector stdoutCollector = null;

    private IOCollector stderrCollector = null;

    private Process process = null;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;
    
    /**
     * extra buffer space for the error message to be included in any output.
     */
    private static final int ERRORLENGTH = 50;
    
    public ProgramRunner(IInternalContest inContest, IInternalController inController) {
        super();

        this.contest = inContest;
        this.controller = inController;

        log = controller.getLog();

    }


    /**
     * Executes the specified command line using the given {@link ExecutionData} in the
     * specified execute directory; writes the stdout and stderr of the execution to the
     * specified files.
     * 
     * @param executionData
     * @param executeDirectoryName
     * @param cmdline
     * @param msTimeout timeout in milliseconds.
     * @param executionTimer an optional timer limiting the execution time
     * @param stdinFilename the optional file which if non-null is sent to the command's standard input channel
     * @param stdoutFilename the file to which the command's standard output will be written
     * @param stderrFilename the file to which the command's standard error channel will be written
     * 
     * @return the process started.
     * 
     * @throws {@link ExecuteException} if any Exception occurs while running the command
     */
    public int runProgram(ExecutionData executionData, String executeDirectoryName, String cmdline,
            int msTimeout, ExecuteTimer executionTimer, String stdinFilename, String stdoutFilename, String stderrFilename) throws ExecuteException {

        //TODO:  currently, this method (ProgramRunner.runProgram()) is ONLY invoked by method
        // CustomInputValidatorRunner.runCustomInputValidator().  For that use-case, the value
        // of getMaxFileSize() being passed to the IOCollector (see below) is really meaningless, because that
        // value (which is obtained by a call to contest.getContestInformation().getMaxFileSize())
        // specifies the maximum output size for a SUBMISSION; it has nothing to do with running
        // an Input Validator.  The effect of this at the moment is just that an Input Validator 
        // will be limited to producing no more output than that allowed by a team submission.
        // This is not correct, but it doesn't seem terribly bad because Input Validators are not
        // typically expected to produce huge amounts of output.
        // However, if this method is ever used for running any OTHER programs, or if the value returned
        // by contest.getContestInformation().getMaxFileSize() was small, this could become
        // an issue.  The issue becomes further complicated by the fact that a change associated with
        // GitHub Issue #564 -- Support per-problem output limits -- means that each Problem now has
        // its own output limit; method getMaxFileSize() in this class does not respect this (and
        // currently doesn't even have access to the current Problem, so the entire calling sequence
        // would need to be adjusted.) 
        // See method getMaxFileSize() in class Executable for insight into how to obtain per-problem
        // output size limits.
        
        String message;
        int returnValue = -1 ;

        try {
            File runDir = new File(executeDirectoryName);
            if (runDir.isDirectory()) {

                if (executionTimer != null) {
                    executionTimer.setDoAutoStop(true);
                }

                /**
                 * Create/Start buffers.
                 */
                BufferedOutputStream stdoutlog = new BufferedOutputStream(new FileOutputStream(stdoutFilename, false));
                BufferedOutputStream stderrlog = new BufferedOutputStream(new FileOutputStream(stderrFilename, false));

                log.info("Running '" + cmdline + "' in directory '" + runDir + "'");  

                String[] env = null;
                long startSecs = System.currentTimeMillis();
                process = Runtime.getRuntime().exec(cmdline, env, runDir);

                // TODO get field.for pid

                // This reads from the stdout of the child process
                BufferedInputStream childOutput = new BufferedInputStream(process.getInputStream());
                // The reads from the stderr of the child process
                BufferedInputStream childError = new BufferedInputStream(process.getErrorStream());

                stdoutCollector = new IOCollector(log, childOutput, stdoutlog, executionTimer, getMaxFileSize() + ERRORLENGTH);
                stderrCollector = new IOCollector(log, childError, stderrlog, executionTimer, getMaxFileSize() + ERRORLENGTH);

                if (executionTimer != null) {
//                    executionTimer.setProcessToWatch(process);
                    executionTimer.setProc(process);
                    executionTimer.startTimer();
                }

                stdoutCollector.start();
                stderrCollector.start();

                if (stdinFilename != null ) {
                    log.info("Using STDIN from file " + stdinFilename);

                    BufferedOutputStream out = new BufferedOutputStream(process.getOutputStream());
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(stdinFilename));
                    byte[] buf = new byte[32768];
                    int c;
                    try {
                        while ((c = in.read(buf)) != -1) {
                            out.write(buf, 0, c);
                        }
                    } catch (java.io.IOException e) {
                        log.info("Caught a " + e.getMessage() + " while sending input file to process's stdin channel");
                    }

                    in.close();
                    out.close();
                }
                
                // waiting for the process to finish execution...

                returnValue = process.waitFor();
                log.info("execution process returned exit code " + returnValue);
                executionData.setExecuteExitValue(returnValue);
                stdoutCollector.join();
                stderrCollector.join();

                process.destroy();

                if (executionTimer != null) {
                    executionTimer.stopTimer();
                    executionData.setRunTimeLimitExceeded(executionTimer.isRunTimeLimitExceeded());
                    if (executionTimer.isRunTimeLimitExceeded()) {
                        log.info("Program exceeded specified time limit of " + msTimeout + " msecs: actual run time = " + executionData.getExecuteTimeMS() + " msec");
                    }
                }


                stdoutlog.close();
                stderrlog.close();

                executionData.setExecuteSucess(true);
                executionData.setExecuteTimeMS(System.currentTimeMillis() - startSecs);
                executionData.setExecuteProgramOutput(new SerializedFile(stdoutFilename));
                executionData.setExecuteStderr(new SerializedFile(stderrFilename));

//                executionData.setExitCode(process.exitValue());
                executionData.setExecuteExitValue(process.exitValue());
              
                
            } else {
                message = "Execute Directory does not exist " + executeDirectoryName;
                executionData.setExecutionException(new ExecuteException(message));
                log.severe(message);
            }
        } catch (IOException e) {
            message = "Exec failure: IOException executing '" + cmdline + "': " + e.getMessage();
            executionData.setExecutionException(new ExecuteException(message, e.getCause()));
            log.severe(message);
            throw new ExecuteException("Exception attempting to run program: " + e.getMessage());
        } catch (Exception e) {
            message = "Exec failure: Exception executing '" + cmdline + "': " + e.getMessage();
            executionData.setExecutionException(new ExecuteException(message, e.getCause()));
            log.severe(message);
            throw new ExecuteException("Exception attempting to run program: " + e.getMessage());
        }

        return returnValue;
    }

    /**
     * Get max output file size.
     * 
     * @return
     */
    private long getMaxFileSize() {
        return contest.getContestInformation().getMaxOutputSizeInBytes();
    }
}
