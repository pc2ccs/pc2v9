// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.


package edu.csus.ecs.pc2.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.SandboxType;

/**
 * This class is meant to deal with any compatibility issues between different
 * platforms such as Windows, Unix, Linux, MacOS, etc. Right now it's used only
 * for sandbox support, but there is no reason more things can't be added in the
 * future if they require special OS support.
 * 
 * @author John Buck, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class OSCompatibilityUtilities {
    
    /**
     * Check if the currently loaded contest requires a sandbox to execute submissions.
     * This is done by scanning all the problems in the contest to see if any of them
     * have a sandbox type that is not SandboxType.NONE
     * 
     * @param contest
     *            the contest to check.
     *            
     * @return true if sandbox needed
     */
    public static boolean isSandboxNeeded(IInternalContest contest)
    {
        boolean isNeeded = false;
        
        if(contest != null) {
            Problem [] problems = contest.getProblems();
            
            // scan problems to see if any one needs a sandbox
            for (Problem problem : problems) {
                if(needsSandbox(problem)) {
                    isNeeded = true;
                    break;
                }
            }
        }
        
        return(isNeeded);
    }
    
    /**
     * Get list of problems that can not be judged on this system since sandbox
     * support is not available.
     * 
     * @param contest
     *            the contest to check.
     *            
     * @return List<Problem> If all problems can be judged on this machine, the n
     *            returns an empty list.
     */
    public static List<Problem> getUnableToJudgeList(IInternalContest contest, Log log)
    {
        List<Problem> probList = new ArrayList<Problem>();
        
        if(contest != null) {
            Problem [] problems = contest.getProblems();
            
            // scan problems to see if any one needs a sandbox
            for (Problem problem : problems) {
                if(needsSandbox(problem)) {
                    probList.add(problem);
                }
            }
            // if there are some problems that need a sandbox, make sure
            // the system supports sandboxes.  if they're supported, then
            // just clear out the list
            if(!probList.isEmpty() && areSandboxesSupported(contest, log)) {
                probList.clear();
            }
        }
        
        return(probList);
    }
    
    /**
     * Check if a problem requires an OS level sandbox
     * 
     * @param problem
     *            the problem to check.
     *            
     * @return true if the problem needs an os sandbox
     */
    public static boolean needsSandbox(Problem prob)
    {
        return(prob.getSandboxType() != SandboxType.NONE);
    }
    
    
    /**
     * Check if the current OS supports sandboxes (eg cgroups on linux).
     * This is done by calling a script to perform OS specific checks.
     * 
     * @param contest
     *            the contest to check.
     * @param log
     *            optional Log to send info/warnings to (may be null to supress
     *            any info/warnings.
     *            
     * @return true if the system supports executing submissions all the problems
     */
    public static boolean areSandboxesSupported(IInternalContest contest, Log log)
    {
        boolean canRun = false;
                    
        String sysCheckSandboxScript;
        // determine if system is set up to run a sandbox.  First determine
        // which script to run.
        if(isRunningOnWindows()) {
            sysCheckSandboxScript = Constants.WINDOWS_CHECK_SANDBOX_SCRIPT;
        } else {
            sysCheckSandboxScript = Constants.UNIX_CHECK_SANDBOX_SCRIPT;
        }
        
        //use the VersionInfo class to get the PC2 installation directory
        VersionInfo versionInfo = new VersionInfo();
        String home = versionInfo.locateHome();
        
        //point to the PC2 Internal Sandbox file (under "/scripts" in the home, i.e. installation, directory)
        String sysCheckSandboxScriptFullName = home + File.separator + Constants.PC2_SCRIPT_DIRECTORY + File.separator + sysCheckSandboxScript;
        
        if(log != null) {
            log.info("Checking for sandbox support: executing " + sysCheckSandboxScriptFullName + " to verify OS compatibilty");
        }
        try {
            // script must exist in order to execute it; we want a separate error if it's not there vs. not executable
            File f = new File(sysCheckSandboxScriptFullName);
            if (f.exists()) {
                
                // execute script using default env and current directory
                Process checkProcess = Runtime.getRuntime().exec(sysCheckSandboxScriptFullName, null, null);
                
                if(checkProcess == null) {
                    if(log != null) {
                        log.warning("Can not execute " + sysCheckSandboxScriptFullName);
                    }
                } else {
                    int exitCode = checkProcess.waitFor();
                    
                    if(log != null) {
                        log.info("Execution of " + sysCheckSandboxScriptFullName + " returns exit code " + exitCode);
                    }
                    if(exitCode == 0) {
                        canRun = true;
                    } else {
                        log.info("This system does not support sandbox execution");
                    }
                }
                
            } else {
                if(log != null) {
                    log.warning("Can not find " + sysCheckSandboxScriptFullName + " - sandboxes are not supported");
                }
                // absence of script indicates the system can not support sandboxes
            }
        } catch(Exception e)
        {
            // no matter what happened to cause the exception, it means no sandbox support
            if(log != null) {
                log.log(Log.INFO, "Exception during areSandboxesSupported() ", e);
            } else {
                if(Utilities.isDebugMode()){
                    e.printStackTrace();
                }
                StaticLog.getLog().log(Log.INFO, "Exception during areSandboxesSupported() ", e);
            }
        }
        
        return(canRun);
    }
    
    /**
     * Check if the current OS can support executing submissions for problems
     * that may require a sandbox.
     * 
     * @param contest
     *            the contest to check.
     * @param log
     *            optional Log to send info/warnings to (may be null to supress
     *            any info/warnings.
     *            
     * @return true if the system supports executing submissions all the problems
     */
    public static boolean canRunAllSubmissions(IInternalContest contest, Log log)
    {
        boolean canRun = true;
        
        if(isSandboxNeeded(contest)){
            canRun = areSandboxesSupported(contest, log);
        }
        return(canRun);
    }
    
    /**
     * Determine if current OS is MS Windows based.
     * 
     * @return true if the system is MS Windows based
     */
    public static boolean isRunningOnWindows()
    {
        // fetch OS name to see if it has windows in it
        return(System.getProperty("os.name").toLowerCase().contains("windows"));
    }
}
