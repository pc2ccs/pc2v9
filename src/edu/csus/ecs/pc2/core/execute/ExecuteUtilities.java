// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Plugin;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExecuteUtilities extends Plugin {
    
    /**
     * 
     */
    private static final long serialVersionUID = -9167576117688387694L;
    
    /**
     * Regular Expression to match include file names.
     */
    public static final String INCLUDE_RE = "(\\.h$)|(\\.hh$)|(\\.H$)";

    /**
     * Pattern for INCLUDE_RE.
     */
    private static Pattern includeRePattern = Pattern.compile(INCLUDE_RE);

    private Run run;
    
    private RunFiles runFiles;
    
    private Problem problem;
    
    private Language language;
    
    private ProblemDataFiles problemDataFiles;
    
    private Log log;
    
    private ExecutionData executionData;
    
    private String resultsFileName;

    private static String debugMessage = null;
    
    public static final String DEFAULT_PC2_JAR_PATH = "./build/prod";
    
    public static final String PC2_JAR_FILENAME = "pc2.jar";
    
    public ExecuteUtilities(IInternalContest contest, IInternalController controller, Run run, RunFiles runFiles, Problem problem, Language language) {
        super();
        
        setContestAndController(contest, controller);
        
        this.run = run;
        this.runFiles = runFiles;
        this.problem = problem;
        this.language = language;
        
        log = getController().getLog();
        
        if (run == null) {
            throw new IllegalArgumentException("Run is null");
        }
        
        resultsFileName = createResultsFileName(run);
    }

    /**
     * Replace all instances of beforeString with afterString.
     * 
     * If before string is not found, then returns original string.
     * 
     * @param origString
     *            string to be modified
     * @param beforeString
     *            string to search for
     * @param afterString
     *            string to replace beforeString
     * @return original string with all beforeString instances replaced with afterString
     */
    public static String replaceString(String origString, String beforeString, String afterString) {
        debugMessage = null;
        
        if (origString == null) {
            return origString;
        }

        int startIdx = origString.lastIndexOf(beforeString);

        if (startIdx == -1) {
            return origString;
        }

        StringBuffer buf = new StringBuffer(origString);

        while (startIdx != -1) {
            buf.replace(startIdx, startIdx + beforeString.length(), afterString);
            startIdx = origString.lastIndexOf(beforeString, startIdx - 1);
        }

        return buf.toString();
    }
    
    /**
     * Replace beforeString with int.
     * 
     * For details see {@link #replaceString(String, String, String)}
     * 
     * @param origString
     *            string to be modified
     * @param beforeString
     *            string to search for
     * @param afterInt
     *            integer to replace beforeString
     * @return string after replacement.
     */
    public static String replaceString(String origString, String beforeString, int afterInt) {
        
        debugMessage = null;

        String afterString = new Integer(afterInt).toString();
        return replaceString(origString, beforeString, afterString);
    }

    /**
     * Return string minus last extension. <br>
     * Finds last . (period) in input string, strips that period and all other characters after that last period. If no period is found in string, will return a copy of the original string. <br>
     * Unlike the Unix basename program, no extension is supplied.
     * 
     * @param original
     *            the input string
     * @return a string with all text after last . removed
     */
    public static String removeExtension(String original) {
        String outString = new String(original);

        // Strip off all text after and including final dot

        int dotIndex = outString.lastIndexOf('.', outString.length() - 1);
        if (dotIndex != -1) {
            outString = outString.substring(0, dotIndex);
        }

        return outString;

    }
    
    
    /**
     * Match string to RE.
     * 
     * @param str
     * @param reString
     * @return true if str matches regular expression reString
     */
    public static boolean matchRegularExpression (String str, String reString) {
        Pattern p = Pattern.compile(reString);
        Matcher m = p.matcher(str);
        return m.find();
        
    }
    
    /**
     * Match String to {@link #INCLUDE_RE}.
     * 
     * @param str
     * @return true if string matches {{@link #INCLUDE_RE}.
     */
    public static boolean matchIncludeRe(String str) {
        return includeRePattern.matcher(str).find();
    }
    
    /**
     * Return all compilable submitted file names.
     * 
     * @param files
     * @return main file and any additional filenames that end in: .c, .cpp or .C
     */
    public static String getAllSubmittedFilenames(RunFiles files){
        String filelist = files.getMainFile().getName();
        
        if (files.getOtherFiles() != null && files.getOtherFiles().length > 0){
            for (SerializedFile file : files.getOtherFiles()) {
                if (! matchIncludeRe(file.getName())) {
                    filelist += " " + file.getName();
                }
            }
        }
        return filelist;
    }
    
    /**
     * return string with all field variables filled with values.
     * 
     * Each variable will be filled in with values.
     * 
     * <pre>
     *             valid fields are:
     *              {:mainfile} - submitted file (hello.java)
     *              {:basename} - mainfile without extension (hello)
     *              {:validator} - validator program name
     *              {:language}
     *              {:problem}
     *              {:teamid}
     *              {:siteid}
     *              {:infile}
     *              {:outfile}
     *              {:ansfile}
     *              {:pc2home}
     * </pre>
     * 
     * @param origString -
     *            original string to be substituted.
     * @return a new String with all occurrences of substitution variables replaced by the corresponding values
     */
    public String substituteAllStrings(String origString) {
        
        String newString = "";
        String nullArgument = "-"; /* this needs to change */

        if (run == null) {
            throw new IllegalArgumentException("Run is null");
        }

        if (runFiles.getMainFile() == null) {
            return origString;
        }
        newString = replaceString(origString, "{:mainfile}", runFiles.getMainFile().getName());
        newString = replaceString(newString, "{files}", getAllSubmittedFilenames(runFiles));
        newString = replaceString(newString, "{:basename}", removeExtension(runFiles.getMainFile().getName()));

        if (problem != null){
            
            String validatorCommand = null;

            if (problem.getOutputValidatorProgramName() != null) {
                validatorCommand = problem.getOutputValidatorProgramName();
            }
            
            if (problemDataFiles != null) {
                SerializedFile validatorFile = problemDataFiles.getOutputValidatorFile();
                if (validatorFile != null) {
                    validatorCommand = validatorFile.getName(); // validator
                }
            }
            
            if (validatorCommand != null) {
                newString = replaceString(newString, "{:validator}", validatorCommand);
            }
        } else {
            debugLog("Problem is null");
        }

        // SOMEDAY LanguageId and ProblemId are now a long string not an int,
        // what should we do?

        if (run.getLanguageId() != null) {
            Language[] langs = getContest().getLanguages();
            int index = 0;
            String displayName = "";
            for (int i = 0; i < langs.length; i++) {
                if (langs[i] != null && langs[i].getElementId().equals(run.getLanguageId())) {
                    displayName = langs[i].getDisplayName().toLowerCase().replaceAll(" ", "_");
                    index = i + 1;
                    break;
                }
            }
            if (index > 0) {
                newString = replaceString(newString, "{:language}", index);
                newString = replaceString(newString, "{:languageletter}", Utilities.convertNumber(index));
                newString = replaceString(newString, "{:languagename}", displayName);
            } else {
                debugLog("No language defined for "+run.getLanguageId());
            }
        }
        
        
        if (run.getProblemId() != null) {
            Problem[] problems = getContest().getProblems();
            int index = 0;
            for (int i = 0; i < problems.length; i++) {
                if (problems[i] != null && problems[i].getElementId().equals(run.getProblemId())) {
                    index = i + 1;
                    break;
                }
            }
            if (index > 0) {
                newString = replaceString(newString, "{:problem}", index);
                newString = replaceString(newString, "{:problemletter}", Utilities.convertNumber(index));
            } else {
                debugLog("No problem defined for "+run.getProblemId());
            }
        }
        
        if (run.getSubmitter() != null) {
            newString = replaceString(newString, "{:teamid}", run.getSubmitter().getClientNumber());
            newString = replaceString(newString, "{:siteid}", run.getSubmitter().getSiteNumber());
        }

        if (problem != null) {
            if (problem.getDataFileName() != null && !problem.getDataFileName().equals("")) {
                newString = replaceString(newString, "{:infile}", problem.getDataFileName());
            } else {
                newString = replaceString(newString, "{:infile}", nullArgument);
            }
            if (problem.getAnswerFileName() != null && !problem.getAnswerFileName().equals("")) {
                newString = replaceString(newString, "{:ansfile}", problem.getAnswerFileName());
            } else {
                newString = replaceString(newString, "{:ansfile}", nullArgument);
            }
            newString = replaceString(newString, "{:timelimit}", Long.toString(problem.getTimeOutInSeconds()));
        }

        if (executionData != null) {
            if (executionData.getExecuteProgramOutput() != null) {
                if (executionData.getExecuteProgramOutput().getName() != null) {
                    newString = replaceString(newString, "{:outfile}", executionData.getExecuteProgramOutput().getName());
                } else {
                    newString = replaceString(newString, "{:outfile}", nullArgument);
                }
            }
            newString = replaceString(newString, "{:exitvalue}", Integer.toString(executionData.getExecuteExitValue()));
            newString = replaceString(newString, "{:executetime}", Long.toString(executionData.getExecuteTimeMS()));
        }
        
        String pc2home = getPC2Home();
        if (pc2home != null && pc2home.length() > 0) {
            newString = replaceString(newString, "{:pc2home}", pc2home);
        }
        
        if (resultsFileName != null) {
            newString = replaceString(newString, "{:resfile}", resultsFileName);
        }

        return newString;
    }

    /**
     * Perform conditional substitutions
     * 
     * @param origString
     * @param condVar
     * @return - possibly modified string
     */
    public static String replaceStringConditional(String origString, String condVar) {
        
        int subStartIdx, endIdx, suffLen;
        String suffString;
        
        if (origString == null || condVar == null) {
            return origString;
        }
        // if substitute variable has an = sign, we try to match the substring up to and including the
        // equals sign, eg: {:ensuresuffix=Kt}, we want to match: {:ensuresuffix=
        int subIdx = condVar.indexOf('=');
        
        // Need at least two chars to left of =
        if(subIdx < 2) {
            return origString;
        }       

        // don't care about anything after the =
        String subMatch = condVar.substring(0, subIdx+1);
        
        int startIdx = origString.lastIndexOf(subMatch);

        if (startIdx == -1) {
            return origString;
        }

        StringBuffer buf = new StringBuffer(origString);

        while (startIdx != -1) {
            subStartIdx = startIdx + subMatch.length();
            endIdx = origString.indexOf('}', subStartIdx);
            
            // Missing closing } for substitute variable?
            if(endIdx == -1) {
                break;
            }
            
            // this is the string we may have to add, but first see if it's there already
            suffString = origString.substring(subStartIdx, endIdx);
            suffLen = suffString.length();
            
            // if there are not enough chars before the substitute var to compare, or, the trailing characters
            // don't match, then we have to replace the substitute var with the new suffix string, otherwise, we
            // we just delete the substitute var altogether
            if(startIdx < suffLen || origString.substring(startIdx - suffLen, startIdx).compareTo(suffString) != 0) {
                // suffix does not match, so we have to insert it
                buf.replace(startIdx,  endIdx+1, suffString);
            } else {
                buf.delete(startIdx,  endIdx+1);
            }
            
            startIdx = origString.lastIndexOf(subMatch, startIdx - 1);
        }

        return buf.toString();
    }
    
    public static String getPC2Home() {
        String pc2home = new VersionInfo().locateHome();
        return pc2home;
    }


    private void debugLog(String string) {
        
        debugMessage = string;
        
        Exception ex = new Exception("ERROR "+string);
        if (log != null){
            log. log(Log.DEBUG, ex.getMessage(),  ex);
        } else {
            ex.printStackTrace(System.err);
        }
    }
    
    /**
     * Create a unique results file.
     */
    public static String createResultsFileName (Run run){
        return createResultsFileName(run.getNumber());
    }
    
    
    /**
     * Create a unique results file.
     */
    public static String createResultsFileName (int runNumber){
        String secs = new Long((new Date().getTime()) % 100).toString();
        String resultsFileName = runNumber+ secs + "XRSAM.txt";
        return resultsFileName;
    }

    public Run getRun() {
        return run;
    }

    public void setRun(Run run) {
        this.run = run;
    }

    public RunFiles getRunFiles() {
        return runFiles;
    }

    public void setRunFiles(RunFiles runFiles) {
        this.runFiles = runFiles;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public ProblemDataFiles getProblemDataFiles() {
        return problemDataFiles;
    }

    public void setProblemDataFiles(ProblemDataFiles problemDataFiles) {
        this.problemDataFiles = problemDataFiles;
    }

    public ExecutionData getExecutionData() {
        return executionData;
    }

    public void setExecutionData(ExecutionData executionData) {
        this.executionData = executionData;
    }
    
    public Language getLanguage() {
        return language;
    }
    
    public void setLanguage(Language language) {
        this.language = language;
    }
    
    public static String getDebugMessage() {
        return debugMessage;
    }

    @Override
    public String getPluginTitle() {
        return "Variable Substitutions";
    }

    public String getResultsFileName() {
        return resultsFileName;
    }
    
    public void setResultsFileName(String resultsFileName) {
        this.resultsFileName = resultsFileName;
    }

    /**
     * Remove all files from specified directory, including subdirectories.
     * 
     * @param dirName
     *            directory to be cleared.
     * @return true if directory was cleared.
     */
    public static boolean clearDirectory(String dirName) {
        File dir = null;
        boolean result = true;

        dir = new File(dirName);
        String[] filesToRemove = dir.list();
        for (int i = 0; i < filesToRemove.length; i++) {
            File fn1 = new File(dirName + File.separator + filesToRemove[i]);
            if (fn1.isDirectory()) {
                // recurse through any directories
                result &= clearDirectory(dirName + File.separator + filesToRemove[i]);
            }
            result &= fn1.delete();
        }
        return (result);
    }
    
    /**
     * Removes the specified directory (folder) from the file system, including first recursively removing
     * all files and all sub-directories (and their contents).
     * 
     * @param dirName the name of the directory to be removed
     * 
     * @return true if the directory was successfully cleared and removed; false if the 
     *      specified directory is null or the empty string, does not exist, 
     *      could not be cleared, could not be removed, or if the specified name is not a directory
     */
    public static boolean removeDirectory(String dirName) {
        
        if (dirName==null || dirName.equals("")) {
            return false;
        }
        
        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            return false;
        }
        
        boolean success = clearDirectory(dirName);
        if (!success) {
            return false;
        } 
        
        success = dir.delete();
        
        return success;
    }
    
    
    @Override
    public void dispose() {
        
        // nothing to dispose
    }

    /**
     * Ensures directory.
     * 
     * If directory cannot be created then returns false.
     * 
     * @param directoryName
     * @return true if directory exists or was created, false otherwise.
     */
    public static boolean ensureDirectory(String directoryName) {
        File dir = new File(directoryName);
        if (!dir.exists() && !dir.mkdirs()) {
            return false;
        }

        return dir.isDirectory();
    }

    /**
     * Find the pc2.jar file path.
     * 
     */
    public static String findPC2JarPath() {

        String jarDir = ".";
        try {
            String defaultPath = new File(DEFAULT_PC2_JAR_PATH).getCanonicalPath();
            // for CruiseControl, will not be needed with jenkins
            if (! new File(defaultPath).exists()) {
                defaultPath = "/software/pc2/cc/projects/pc2v9/build/prod";
            }
            jarDir = defaultPath;
            String cp = System.getProperty("java.class.path");
            
            String[] dirlist = cp.split(":");
            for (String token : dirlist) {

                File dir = new File(token);
                if (dir.exists() && dir.isFile() && dir.toString().endsWith(PC2_JAR_FILENAME)) {
                    jarDir = new File(dir.getParent()).getCanonicalPath() + File.separator;
                    break;
                }
            }

            if (DEFAULT_PC2_JAR_PATH.equals(jarDir)) {
                
                File dir = new File("dist/" + PC2_JAR_FILENAME);
                if (dir.isFile()) {
                    jarDir = new File(dir.getParent()).getCanonicalPath() + File.separator;
                } else {
                    
                    dir = new File(PC2_JAR_FILENAME);
                    if (dir.isFile()) {
                        jarDir = new File(dir.getParent()).getCanonicalPath() + File.separator;
                    }
                }
            }
        } catch (IOException e) {
            /**
             * This likely happened with getCanonicalPath, this exception/condition can be ignored.
             */
            System.err.println(e.getMessage());
        }
        return jarDir;
    }

    /**
     * Copy a file and optionally log exceptions.
     * @param fileOne
     * @param fileTwo
     * @param log if not null then will log exception/error info to log.
     * @return true if file copied, else false.
     */
    public static boolean copyFile(String fileOne, String fileTwo, Log log) {
        try {
            Files.copy(new File(fileOne).toPath(), new File(fileTwo).toPath());
            return true;
        } catch (Exception e) {
            if (log != null){
                log.warning("Unable to copy file " + fileOne + " " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Did the team's run solve the problem ?.
     * 
     * @param executionData  the ExecutionData object which was produced by running the team's program
     * @return true if validation returned accepted
     */
    public static boolean didTeamSolveProblem(ExecutionData executionData) {
        
        if (!executionData.isCompileSuccess() || !executionData.isExecuteSucess()){
            return false;
        }
        
        if (executionData.isRunTimeLimitExceeded()){
            return false;
        }

        // validator program failed to run 
        if (! executionData.isValidationSuccess()){
            return false;
        }

        
        if (executionData.getExecutionException() != null){
            return false;
        }
        
        if (executionData.getValidationResults() != null){
            
            String results = executionData.getValidationResults();
            
            // bug 280 ICPC Validator Interface Standard calls for "accepted" in any case.
            if (results.trim().equalsIgnoreCase("accepted")) {
                return true;
            }
        }
        return false;
    }
    
    public static String toString(ExecutionData executionData) {

        return " compileSuccess=" + executionData.isCompileSuccess() + //
                ", executeSucess=" + executionData.isExecuteSucess() + //
                ", runTimeLimitExceeded=" + executionData.isRunTimeLimitExceeded() + //
                ", validationSuccess=" + executionData.isValidationSuccess() + //
                ", executionException=" + executionData.getExecutionException() + //
                ", validationResults=" + executionData.getValidationResults() //
        ;
    }
    
    public static void dump(ExecutionData executionData) {

        System.out.println( " compileSuccess=" + executionData.isCompileSuccess() + //
                ", executeSucess=" + executionData.isExecuteSucess() + //
                ", runTimeLimitExceeded=" + executionData.isRunTimeLimitExceeded() + //
                ", validationSuccess=" + executionData.isValidationSuccess() + //
                ", executionException=" + executionData.getExecutionException() + //
                ", validationResults=" + executionData.getValidationResults() //
                )
        ;
        
        if (executionData.getExecutionException() != null){
            executionData.getExecutionException().printStackTrace();
        }
    }
    
        

    /**
     * Write String Array to file.
     * @param filename
     * @param datalines
     * @throws FileNotFoundException
     */
    public static void writeFileContents(String filename, String[] datalines) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        for (String s : datalines) {
            writer.println(s);
        }
        writer.close();
        writer = null;
    }

    /**
     * Write ArrayList to file.
     * @param filename
     * @param datalines
     * @throws FileNotFoundException
     */
    public static void writeFileContents(String filename, ArrayList<String> datalines) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        for (String s : datalines) {
            writer.println(s);
        }
        writer.close();
        writer = null;
    }
    
    
    /**
     * Cast a CheckedException as an unchecked one.
     *
     * @param throwable to cast
     * @param <T>       the type of the Throwable
     * @return this method will never return a Throwable instance, it will just throw it.
     * @throws T the throwable as an unchecked throwable
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException rethrow(Throwable throwable) throws T {
        throw (T) throwable;
    }
    
}
