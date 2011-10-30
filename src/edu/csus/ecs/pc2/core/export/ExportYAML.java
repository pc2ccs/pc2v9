package edu.csus.ecs.pc2.core.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * Create CCS contest.yaml and problem.yaml files.
 * 
 * Creates contest.yaml and problem.yaml files along with all the data files per the CCS specification.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ExportYAML.java 223 2011-09-02 02:13:59Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/export/ExportYAML.java $
public class ExportYAML {

    public static final String CONTEST_FILENAME = "contest.yaml";

    public static final String PROBLEM_FILENAME = "problem.yaml";

    private String dateTimeFormat = "yyyy-MM-dd HH:mm:ss z";

    private SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);

    /**
     * Write CCS Yaml files to directory.
     * 
     * Creates files:
     * 
     * <pre>
     * directoryName/contest.yaml
     * directoryName/shortname1/problem.yaml
     * directoryName/shortname1/data/secret/sumit.dat
     * directoryName/shortname1/data/secret/sumit.ans
     * directoryName/shortname2/problem.yaml
     * directoryName/shortname3/problem.yaml
     * </pre>
     * 
     * @param directoryName
     * @param contest
     * @throws IOException
     */
    public void exportFiles(String directoryName, IInternalContest contest) throws IOException {

        String contestFileName = CONTEST_FILENAME;
        if (directoryName != null && directoryName.length() != 0) {
            contestFileName = directoryName + File.separator + contestFileName;
        }

        writeContestYAMLFiles(contest, directoryName, contestFileName);
    }

    private String getDateTimeString() {
        return formatter.format(new Date());
    }

    /**
     * Write contest and problem yaml and files to directory.
     * 
     * @param contest
     * @param directoryName
     * @param contestFileName
     * @throws IOException
     */
    public void writeContestYAMLFiles(IInternalContest contest, String directoryName, String contestFileName) throws IOException {

        PrintWriter contestWriter = new PrintWriter(new FileOutputStream(contestFileName, false), true);
        // PrintStream contestWriter = System.out;

        // # Contest configuration
        // ---

        contestWriter.println("# Contest Configuration, version 1.0 ");
        contestWriter.println("# PC^2 Version: " + new VersionInfo().getSystemVersionInfo());
        contestWriter.println("# Created: " + getDateTimeString());
        contestWriter.println("--- ");

        contestWriter.println();

        // name: ACM-ICPC World Finals 2011
        // short-name: ICPC WF 2011
        // start-time: 2011-02-04 01:23Z
        // duration: 5:00:00
        // scoreboard-freeze: 4:00:00

        contestWriter.println("name: " + contest.getContestInformation().getContestTitle());
        contestWriter.println("short-name: ");

        ContestTime contestTime = contest.getContestTime();
        if (contestTime == null) {
            contestTime = new ContestTime();
            contest.addContestTime(contestTime);
        }

        if (contestTime.getElapsedSecs() > 0) {
            contestWriter.println("start-time: " + formatter.format(contestTime.getResumeTime().getTime()));
            contestWriter.println("duration: " + contestTime.getContestLengthStr());
        }
        contestWriter.println("elapsed: " + contestTime.getElapsedTimeStr());
        contestWriter.println("remaining: " + contestTime.getRemainingTimeStr());
        contestWriter.println("running: " + contestTime.isContestRunning());

        contestWriter.println("scoreboard-freeze: ");

        contestWriter.println();

        // TODO CCS write default clar

        // default-clars:
        // - No comment, read problem statement.
        // - This will be answered during the answers to questions session.
        contestWriter.println("default-clars:");
        // TODO CCS this needs to be an array
        contestWriter.println(" - "+contest.getContestInformation().getJudgesDefaultAnswer());
        contestWriter.println();
        
        // clar-categories:
        // - General
        // - SysOps
        // - Operations

        Category[] categories = contest.getCategories();

        if (categories.length > 0) {
            contestWriter.println("clar-categories:");
            for (Category category : categories) {
                contestWriter.println(" - " + category.getDisplayName());
            }
            contestWriter.println();
        }

        Language[] languages = contest.getLanguages();

        if (languages.length > 0) {
            contestWriter.println("languages:");
        }

        // languages:
        // - name: C++
        // compiler: /usr/bin/g++
        // compiler-args: -O2 -Wall -o a.out -static {files}
        //
        // - name: Java
        // compiler: /usr/bin/javac
        // compiler-args: -O {files}
        // runner: /usr/bin/java
        // runner-args:

        for (Language language : languages) {
            contestWriter.println(" - name: " + language.getDisplayName());
            contestWriter.println("   active: " + language.isActive());
            contestWriter.println("   compilerCmd: " + language.getCompileCommandLine());
            contestWriter.println("   exemask: " + language.getExecutableIdentifierMask());
            contestWriter.println("   execCmd: " + language.getProgramExecuteCommandLine());

            String runner = getRunner(language.getProgramExecuteCommandLine());
            String runnerArguments = getRunnerArguments(language.getProgramExecuteCommandLine());

            if (runner != null) {
                contestWriter.println("   runner: " + runner);
                contestWriter.println("   runner-args: " + runnerArguments);

            }
            contestWriter.println();
        }

        Problem[] problems = contest.getProblems();

        if (problems.length > 0) {
            contestWriter.println("problemset:");
        }

        // problemset:
        //
        // - letter: B
        // short-name: barcodes
        // color: red
        // rgb: #ff0000

        int id = 1;

        for (Problem problem : problems) {

            String name = problem.getDisplayName();

            String letter = getProblemLetter(id);
            if (problem.getLetter() != null){
                letter = problem.getLetter();
            }
            contestWriter.println("  - letter: " + letter); 
            String shortName = createProblemShortName(name);
            if (problem.getShortName() != null && problem.getShortName().trim().length() > 0){
                shortName = problem.getShortName();
            }
            contestWriter.println("    short-name: " + shortName);

            String colorName = getProblemBalloonColor(contest, problem);
            if (colorName != null) {
                contestWriter.println("    color: " + colorName);
            }
            // else no color, nothing to print.

             String[] filesWritten = writeProblemYAML(contest, directoryName, problem, shortName);

            if (filesWritten.length > 0) {
                contestWriter.println("#     " + filesWritten.length + " data files written");
                for (String filename : filesWritten){
                    contestWriter.println("#     wrote " + filename);
                }
            }
            id++;

            contestWriter.println();
        }

        Site[] sites = contest.getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        contestWriter.println("sites:");
        for (Site site : sites) {
            contestWriter.println(" - number: " + site.getSiteNumber());
            contestWriter.println("   name: " + site.getDisplayName());
            contestWriter.println("   password: " + site.getPassword());

            String hostName = site.getConnectionInfo().getProperty(Site.IP_KEY);
            String portStr = site.getConnectionInfo().getProperty(Site.PORT_KEY);
            contestWriter.println("   IP: " + hostName);
            contestWriter.println("   port: " + portStr);
            contestWriter.println();
        }

        contestWriter.println();

        ClientType.Type[] types = { ClientType.Type.TEAM, ClientType.Type.JUDGE, ClientType.Type.SCOREBOARD, };

        boolean accountHeader = false;

        for (Site site : sites) {
            for (ClientType.Type type : types) {
                Vector<Account> accounts = contest.getAccounts(type, site.getSiteNumber());
                if (accounts.size() > 0) {
                    if (!accountHeader) {
                        // only print it once, and only if we have some accounts to dump
                        contestWriter.println("accounts:");
                        accountHeader=true;
                    }
                    contestWriter.println("  - account: " + type.toString());
                    contestWriter.println("    site: " + site.getSiteNumber());
                    contestWriter.println("    count: " + accounts.size());
                    contestWriter.println();
                }
            }
        }
        // only add this blank line if we dumped accounts
        if (accountHeader) {
            contestWriter.println();
        }
        contestWriter.println("# EOF Contest Configuration");

        contestWriter.flush();
        contestWriter.close();
        contestWriter = null;
    }

    /**
     * Create disk file for input SerializedFile.
     * 
     * Returns true if file is written to disk and is not null.
     * 
     * @param file
     * @param outputFileName
     * @return true if file written to disk.
     * @throws IOException
     */
    boolean createFile(SerializedFile file, String outputFileName) throws IOException {
        if (file != null && outputFileName != null) {
            file.writeFile(outputFileName);
            return new File(outputFileName).isFile();
        }
        return false;
    }

    /**
     * Write problem yaml and data files files to directory.
     * 
     * @param contest
     * @param directoryName
     *            directory to write files to.
     * @param problem
     *            problem to write files
     * @param shortName
     *            short name (used as problem directory name)
     * @return list of files written.
     * @throws IOException
     */
    public String[] writeProblemYAML(IInternalContest contest, String directoryName, Problem problem, String shortName) throws IOException {

        Vector<String> filesWritten = new Vector<String>();

        String targetDirectoryName = directoryName + File.separator + shortName;

        new File(targetDirectoryName).mkdirs();

        String problemFileName = targetDirectoryName + File.separator + PROBLEM_FILENAME;

        PrintWriter problemWriter = new PrintWriter(new FileOutputStream(problemFileName, false), true);
        // PrintStream problemWriter = System.out;

        //
        // # Problem configuration
        // name: Squares to Circles
        // source: ICPC Mid-Atlantic Regional Contest
        // author: John von Judge
        // license: cc by-sa
        // rights_owner: ICPC

        problemWriter.println("# Problem configuration, version 1.0 ");
        problemWriter.println("# PC^2 Version: " + new VersionInfo().getSystemVersionInfo());
        problemWriter.println("# Created: " + getDateTimeString());
        problemWriter.println("--- ");

        problemWriter.println();

        problemWriter.println("name: " + problem.getDisplayName());
        problemWriter.println("source: ");
        problemWriter.println("author: ");
        problemWriter.println("license: ");
        problemWriter.println("rights_owner: ");

        problemWriter.println();

        problemWriter.println("limits: ");
        problemWriter.println("   timeout: " + problem.getTimeOutInSeconds());
        problemWriter.println();

        if (problem.isValidatedProblem()) {
            problemWriter.println("validator: ");
            problemWriter.println("   validatorProg: " + problem.getValidatorProgramName());
            problemWriter.println("   validatorCmd: " + problem.getValidatorCommandLine());
            problemWriter.println("   usingInternal: " + problem.isUsingPC2Validator());
            problemWriter.println("   validatorOption: " + problem.getWhichPC2Validator());
            problemWriter.println();
        }

        problemWriter.println("input: ");
        problemWriter.println("   readFromSTDIN: " + problem.isReadInputDataFromSTDIN());
        problemWriter.println();

        /**
         * Create data files target directory.
         */

        String dataFileDirectoryName = targetDirectoryName + File.separator + "data" + File.separator + "secret";
        new File(dataFileDirectoryName).mkdirs();
        
        ProblemDataFiles [] dataFileList = contest.getProblemDataFiles();
        
        boolean foundProblemFiles = false;
        
        for (ProblemDataFiles dataFiles : dataFileList) {
            
            
            if (dataFiles.getProblemId().equals(problem.getElementId())) {
                
                for ( SerializedFile serializedFile: dataFiles.getJudgesDataFiles()) {
                    String  outputFileName = dataFileDirectoryName + File.separator + serializedFile.getName();
                    createFile(serializedFile, outputFileName);
                    problemWriter.println("#     wrote (D)" + outputFileName);
                    filesWritten.addElement(outputFileName);
                }
                
                for (SerializedFile serializedFile : dataFiles.getJudgesAnswerFiles()){
                    String  outputFileName = dataFileDirectoryName + File.separator + serializedFile.getName();
                    createFile(serializedFile, outputFileName);
                    problemWriter.println("#     wrote (A)" + outputFileName);
                    filesWritten.addElement(outputFileName);
                }
                
                foundProblemFiles = true;
            }
        }
        
        if (! foundProblemFiles) {
            problemWriter.println("# No data files to write (present/defined)  ");   
        }

        // limits:
        // time_multiplier: 5
        // time_safety_margin: 2
        // memory: 4096
        // output: 16
        // compile_time: 240
        // validation_time: 240
        // validation_memory: 3072
        // validation_filesize: 4
        //
        // validator: space_change_sensitive float_absolute_tolerance 1e-6

        problemWriter.println();

        problemWriter.flush();
        problemWriter.close();
        problemWriter = null;

        return (String[]) filesWritten.toArray(new String[filesWritten.size()]);
    }

    protected String getProblemLetter(int id) {
        char let = 'A';
        let += (id - 1);
        return Character.toString(let);
    }

    /**
     * Create a problem short name.
     * 
     * @param name
     *            Problem full name
     * @return
     */
    private String createProblemShortName(String name) {
        String newName = name.trim().split(" ")[0].trim().toLowerCase(); //  + (System.nanoTime() % 1000);
        return newName;
    }

    private String getProblemBalloonColor(IInternalContest contest, Problem problem) {
        BalloonSettings balloonSettings = contest.getBalloonSettings(contest.getSiteNumber());
        String name = null;
        if (balloonSettings != null) {
            name = balloonSettings.getColor(problem);
        }
        return name;
    }

    protected String getRunner(String programExecuteCommandLine) {
        if (programExecuteCommandLine.startsWith("{:")) {
            return null;
        } else {
            String firstArg = programExecuteCommandLine.trim().split(" ")[0];
            return firstArg;
        }
    }

    protected String getRunnerArguments(String programExecuteCommandLine) {
        if (programExecuteCommandLine.startsWith("{:")) {
            return null;
        } else {
            return programExecuteCommandLine.trim().substring(getRunner(programExecuteCommandLine).length()).trim();
        }
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

}
