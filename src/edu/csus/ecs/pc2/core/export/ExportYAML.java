package edu.csus.ecs.pc2.core.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

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

    private static final String PAD4 = "    ";

    private static final String PAD2 = "  ";

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

        String contestFileName = IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
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

        // from CCS
        // name Name of contest
        // short-name Short name of contest
        // start-time Date and time in ISO 8601 format (wall-clock time that the contest starts)
        // duration Duration as h:mm:ss (length of contest, in contest time)
        // scoreboard-freeze Time when scoreboard will be frozen in contest time as h:mm:ss
        // event-feed-port Port number for the Event Feed
        // default-clars Sequence of pre-defined clarification answers. The first will be pre-selected
        // clar-categories Sequence of categories for clarifications.
        // languages Sequence of mappings with keys as defined below
        // penaltytime Penalty minutes for each incorrect run (optional, default 20)

        ContestInformation info = contest.getContestInformation();

        // name: ACM-ICPC World Finals 2011
        // short-name: ICPC WF 2011

        contestWriter.println("name: " + quote(info.getContestTitle()));
        contestWriter.println("short-name: " + quote(info.getContestShortName()));

        ContestTime contestTime = contest.getContestTime();
        if (contestTime == null) {
            contestTime = new ContestTime();
            contest.addContestTime(contestTime);
        }

        // TODO DOC wikitize these
        // pc2 specific values
        contestWriter.println("elapsed: " + contestTime.getElapsedTimeStr());
        contestWriter.println("remaining: " + contestTime.getRemainingTimeStr());
        contestWriter.println("running: " + contestTime.isContestRunning());

        // start-time: 2011-02-04 01:23Z
        if (info.getScheduledStartDate() == null) {
            info.setScheduledStartDate(new Date());
        }
        contestWriter.println("start-time: " + formatDate(info.getScheduledStartDate()));

        // duration: 5:00:00
        contestWriter.println("duration: " + contestTime.getContestLengthStr());

        // TODO CCS scoreboard-freeze: 4:00:00
        // scoreboard-freeze: 4:00:00

        contestWriter.println("scoreboard-freeze: " + info.getFreezeTime());

        contestWriter.println("# " + IContestLoader.PROBLEM_LOAD_DATA_FILES_KEY + ": false");

        String judgeCDPBasePath = info.getJudgeCDPBasePath();
        if (! StringUtilities.isEmpty(judgeCDPBasePath)){
            contestWriter.println(IContestLoader.JUDGE_CONFIG_PATH_KEY +": "+judgeCDPBasePath);
        }
        
        contestWriter.println();

        // TODO CCS write default clar

        // default-clars:
        // - No comment, read problem statement.
        // - This will be answered during the answers to questions session.
        contestWriter.println(IContestLoader.DEFAULT_CLARS_KEY + ":");
        // TODO CCS this needs to be an array
        contestWriter.println(PAD2 + "- " + info.getJudgesDefaultAnswer());
        contestWriter.println();

        // clar-categories:
        // - General
        // - SysOps
        // - Operations

        Category[] categories = contest.getCategories();

        if (categories.length > 0) {
            contestWriter.println(IContestLoader.CLAR_CATEGORIES_KEY + ":");
            for (Category category : categories) {
                contestWriter.println(PAD2 + "- " + category.getDisplayName());
            }
            contestWriter.println();
        }

        Language[] languages = contest.getLanguages();

        if (languages.length > 0) {
            contestWriter.println(IContestLoader.LANGUAGE_KEY + ":");
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
            contestWriter.println(PAD2 + "- name: " + quote(language.getDisplayName()));
            contestWriter.println(PAD4 + "active: " + language.isActive());
            contestWriter.println(PAD4 + "compilerCmd: " + quote(language.getCompileCommandLine()));
            contestWriter.println(PAD4 + "exemask: " + quote(language.getExecutableIdentifierMask()));
            contestWriter.println(PAD4 + "execCmd: " + quote(language.getProgramExecuteCommandLine()));

            String runner = getRunner(language.getProgramExecuteCommandLine());
            String runnerArguments = getRunnerArguments(language.getProgramExecuteCommandLine());

            if (runner != null) {
                contestWriter.println(PAD4 + "runner: " + quote(runner));
                contestWriter.println(PAD4 + "runner-args: " + quote(runnerArguments));
            }
            
            contestWriter.println(PAD4 + IContestLoader.INTERPRETED_LANGUAGE_KEY + ": " + language.isInterpreted());
            contestWriter.println(PAD4 + "use-judge-cmd: " + language.isUsingJudgeProgramExecuteCommandLine());
            contestWriter.println(PAD4 + "judge-exec-cmd: " + quote(language.getJudgeProgramExecuteCommandLine()));

            contestWriter.println();
        }

        Problem[] problems = contest.getProblems();
        
        /**
         * Write problem section to contest.yaml
         */
        writeProblemSetYaml(contestWriter, contest, directoryName, IContestLoader.PROBLEMS_KEY, problems);
        
        /**
         * Write problemset.yaml file. 
         */
        String confiDir = new File(contestFileName).getParent();
        
        String problemSetFilename = confiDir + File.separator + IContestLoader.DEFAULT_PROBLEM_SET_YAML_FILENAME;
        PrintWriter problemSetWriter = new PrintWriter(new FileOutputStream(problemSetFilename, false), true);

        problemSetWriter.println("# Contest Configuration, Problem Set, version 1.0 ");
        problemSetWriter.println("# PC^2 Version: " + new VersionInfo().getSystemVersionInfo());
        problemSetWriter.println("# Created: " + getDateTimeString());
        
        writeProblemSetYaml(problemSetWriter, contest, directoryName, IContestLoader.PROBLEMSET_PROBLEMS_KEY, problems);
        
        problemSetWriter.flush();
        problemSetWriter.close();
        problemSetWriter = null;

        Vector<Account> accountVector = contest.getAccounts(ClientType.Type.JUDGE);
        Account[] judgeAccounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(judgeAccounts, new AccountComparator());

        int ajCount = 0;
        for (Account account : judgeAccounts) {

            ClientSettings clientSettings = contest.getClientSettings(account.getClientId());
            if (clientSettings != null) {

                if (clientSettings.isAutoJudging() || clientSettings.getAutoJudgeFilter() != null) {
                    ajCount++;
                    if (ajCount == 1) {
                        contestWriter.println(IContestLoader.AUTO_JUDGE_KEY + ":");
                    }

                    ClientId clientId = account.getClientId();

                    contestWriter.println(PAD2 + "- account: " + clientId.getClientType());
                    contestWriter.println(PAD4 + "site: " + clientId.getSiteNumber());
                    contestWriter.println(PAD4 + "number: " + clientId.getClientNumber());
                    contestWriter.println(PAD4 + "letters: " + getProblemLetters(contest, clientSettings.getAutoJudgeFilter()));
                    contestWriter.println(PAD4 + "enabled: " + Utilities.yesNoString(clientSettings.isAutoJudging()).toLowerCase());
                    contestWriter.println();
                }
            }
        }

        PlaybackInfo[] playbackInfos = contest.getPlaybackInfos();

        if (playbackInfos.length > 0) {

            contestWriter.println(IContestLoader.REPLAY_KEY + ":");

            for (PlaybackInfo playbackInfo : playbackInfos) {

                contestWriter.println(PAD2 + "- title: " + playbackInfo.getDisplayName());
                contestWriter.println(PAD4 + "    file: " + playbackInfo.getFilename());
                contestWriter.println(PAD4 + "  auto_start: " + Utilities.yesNoString(playbackInfo.isStarted()).toLowerCase());
                contestWriter.println(PAD4 + "   minevents: " + playbackInfo.getMinimumPlaybackRecords());
                contestWriter.println(PAD4 + "pacingMS: " + playbackInfo.getWaitBetweenEventsMS());
                contestWriter.println(PAD4 + "    site: " + playbackInfo.getSiteNumber());
                contestWriter.println();
            }
        }

        Site[] sites = contest.getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        contestWriter.println(IContestLoader.SITES_KEY + ":");
        for (Site site : sites) {
            contestWriter.println(PAD2 + "- number: " + site.getSiteNumber());
            contestWriter.println(PAD4 + "name: " + quote(site.getDisplayName()));
            contestWriter.println(PAD4 + "password: " + site.getPassword());

            String hostName = site.getConnectionInfo().getProperty(Site.IP_KEY);
            String portStr = site.getConnectionInfo().getProperty(Site.PORT_KEY);
            contestWriter.println(PAD4 + "IP: " + hostName);
            contestWriter.println(PAD4 + "port: " + portStr);
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
                        contestWriter.println(IContestLoader.ACCOUNTS_KEY + ":");
                        accountHeader = true;
                    }
                    contestWriter.println(PAD2 + "- account: " + type.toString());
                    contestWriter.println(PAD4 + "site: " + site.getSiteNumber());
                    contestWriter.println(PAD4 + "count: " + accounts.size());
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
     * Write problem set section.
     * 
     * @param writer
     * @param contest
     * @param directoryName
     * @param problemsKey problemset for contest.yaml or problems for problemset.yaml 
     * @param problems
     * @throws IOException
     */
    private void writeProblemSetYaml(PrintWriter writer, IInternalContest contest, String directoryName, String problemsKey, Problem [] problems) throws IOException {

        if (problems.length > 0) {
            writer.println(problemsKey + ":");
        }
        
        // 
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
            if (problem.getLetter() != null) {
                letter = problem.getLetter();
            }
            writer.println(PAD2 + "- letter: " + letter);
            String shortName = createProblemShortName(name);
            if (problem.getShortName() != null && problem.getShortName().trim().length() > 0) {
                shortName = problem.getShortName();
            }
            writer.println(PAD4 + "short-name: " + shortName);
            writer.println(PAD4 + "name: " + quote(name));

            String colorName = getProblemBalloonColor(contest, problem);
            if (colorName != null) {
                writer.println(PAD4 + "color: " + colorName);
            }
            // else no color, nothing to print.

            writer.println(PAD4 + IContestLoader.PROBLEM_LOAD_DATA_FILES_KEY + ": " + (!problem.isUsingExternalDataFiles()));

            String[] filesWritten = writeProblemYAML(contest, directoryName, problem, shortName);

            if (filesWritten.length > 0) {
                writer.println("#     " + filesWritten.length + " data files written");
                for (String filename : filesWritten) {
                    writer.println("#     wrote " + Utilities.unixifyPath(filename));
                }
            }
            id++;

            writer.println();
        }
        
    }

    /**
     * Surround by a single quote
     * 
     * @param string
     * @return
     */
    private String quote(String string) {
        return "'" + string + "'";
    }

    /**
     * 
     * @param date
     * @return empty string if date is null, other wise
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "";
        } else {
            return formatter.format(date);
        }
    }

    private String getProblemLetters(IInternalContest contest, Filter filter) {

        ArrayList<String> list = new ArrayList<String>();

        Problem[] problems = contest.getProblems();

        int id = 1;
        for (Problem problem : problems) {
            if (filter.matches(problem)) {
                list.add(getProblemLetter(id));
            }
            id++;
        }

        StringBuffer buffer = join(", ", list);

        return buffer.toString();
    }

    protected static StringBuffer join(String delimiter, List<String> list) {

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < list.size() - 1; i++) {
            buffer.append(list.get(i));
            buffer.append(delimiter);
        }
        if (list.size() > 0) {
            buffer.append(list.get(list.size() - 1));
        }
        return buffer;
    }

    /**
     * Create disk file for input SerializedFile.
     * 
     * Returns true if file is written to disk and is not null.
     * 
     * @param file
     * @param outputFileName
     * @return true if file written to disk, false if external or not written to disk.
     * @throws IOException
     */
    boolean createFile(SerializedFile file, String outputFileName) throws IOException {
        if (file != null && outputFileName != null && ! file.isExternalFile()) {
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
        String targetDirectoryName = directoryName + File.separator + shortName;
        String problemFileName = targetDirectoryName + File.separator + IContestLoader.DEFAULT_PROBLEM_YAML_FILENAME;
        ProblemDataFiles problemDataFiles = contest.getProblemDataFile(problem);
        return writeProblemYAML(contest, problem, problemFileName, problemDataFiles);
    }

    /**
     * Write problem YAML to file.
     * 
     * @param contest
     * @param problem
     * @param filename
     * @return list of files written.
     * @throws IOException
     */
    public String[] writeProblemYAML(IInternalContest contest, Problem problem, String filename, ProblemDataFiles problemDataFiles) throws IOException {

        Vector<String> filesWritten = new Vector<String>();

        String parentDirectoryName = new File(filename).getParent();

        new File(parentDirectoryName).mkdirs();

        PrintWriter problemWriter = new PrintWriter(new FileOutputStream(filename, false), true);
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

        problemWriter.println("name: " + quote(problem.getDisplayName()));
        problemWriter.println("source: ");
        problemWriter.println("author: ");
        problemWriter.println("license: ");
        problemWriter.println("rights_owner: ");
        
        problemWriter.println();
        
        problemWriter.println(IContestLoader.PROBLEM_LOAD_DATA_FILES_KEY + ": " + (!isExternalFiles(problemDataFiles)));

        problemWriter.println();
        String dataFile = problem.getDataFileName();
        if (dataFile != null) {
            // answerfile: sumit.ans
            problemWriter.println("datafile: " + dataFile);
        }
        
        String answerFileName = problem.getAnswerFileName();
        if (dataFile != null) {
            // answerfile: sumit.ans
            problemWriter.println("datafile: " + answerFileName);
        }

        problemWriter.println();
        
        problemWriter.println(IContestLoader.LIMITS_KEY + ":");
        problemWriter.println(PAD4 + "timeout: " + problem.getTimeOutInSeconds());
        problemWriter.println();

        if (problem.isValidatedProblem()) {
            problemWriter.println(IContestLoader.VALIDATOR_KEY+": ");
            problemWriter.println(PAD4 + "validatorProg: " + quote(problem.getValidatorProgramName()));
            problemWriter.println(PAD4 + "validatorCmd: " + quote(problem.getValidatorCommandLine()));
            problemWriter.println(PAD4 + "usingDefaultValidator: " + problem.isUsingCLICSDefaultValidator());
            problemWriter.println();
            problemWriter.println(PAD4 + IContestLoader.USING_PC2_VALIDATOR + ": " + problem.isUsingPC2Validator());
            problemWriter.println();
        }
        
        problemWriter.println(IContestLoader.JUDGING_TYPE_KEY + ":");
        
        problemWriter.println(PAD4 + IContestLoader.COMPUTER_JUDGING_KEY +": " + problem.isComputerJudged());

        if (problem.isComputerJudged()) {
            // if computer judged, may or may not be manual judged 
            problemWriter.println(PAD4 + IContestLoader.MANUAL_REVIEW_KEY + ": " + problem.isManualReview());
        } else {
            // if not computer judged then MUST be manaul judged
            problemWriter.println(PAD4 + IContestLoader.MANUAL_REVIEW_KEY + ": true");
        }
        problemWriter.println(PAD4 + IContestLoader.SEND_PRELIMINARY_JUDGEMENT_KEY + ": " + problem.isPrelimaryNotification());

        problemWriter.println();

        problemWriter.println(IContestLoader.INPUT_KEY + ":");
        problemWriter.println(PAD4 + "readFromSTDIN: " + problem.isReadInputDataFromSTDIN());
        problemWriter.println();

        String problemLaTexFilename = parentDirectoryName + File.separator + "problem_statement" + File.separator + IContestLoader.DEFAULT_PROBLEM_LATEX_FILENAME;
        writeProblemTitleToFile(problemLaTexFilename, problem.getDisplayName());

        /**
         * Create data files target directory.
         */

        String dataFileDirectoryName = parentDirectoryName + File.separator + "data" + File.separator + "secret";
        new File(dataFileDirectoryName).mkdirs();

        boolean foundProblemFiles = false;

        if (problemDataFiles != null) {

            for (SerializedFile serializedFile : problemDataFiles.getJudgesDataFiles()) {
                String outputFileName = dataFileDirectoryName + File.separator + serializedFile.getName();
                createFile(serializedFile, outputFileName);
                problemWriter.println("#     wrote (D)" + Utilities.unixifyPath(outputFileName));
                filesWritten.addElement(outputFileName);
            }

            for (SerializedFile serializedFile : problemDataFiles.getJudgesAnswerFiles()) {
                String outputFileName = dataFileDirectoryName + File.separator + serializedFile.getName();
                createFile(serializedFile, outputFileName);
                problemWriter.println("#     wrote (A)" + Utilities.unixifyPath(outputFileName));
                filesWritten.addElement(outputFileName);
            }

            foundProblemFiles = filesWritten.size() > 0;
        }

        if (!foundProblemFiles) {
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

    /**
     * Return true if files are not loaded/stored internally.
     * 
     * @param problemDataFiles
     * @return
     */
    private boolean isExternalFiles(ProblemDataFiles problemDataFiles) {

        boolean result = false;

        if (problemDataFiles != null) {

            // test first one whatever that is (internal/external), return that.

            SerializedFile file = problemDataFiles.getJudgesDataFile();
            if (file != null) {
                result = file.isExternalFile();
            }

            // in some problems, example hello world, there is only an answer fil.
            file = problemDataFiles.getJudgesAnswerFile();
            if (file != null) {
                result = file.isExternalFile();
            }

        }
        return result;
    }

    /**
     * Write problem title to (LaTeX) file
     * 
     * @param filename
     * @param title
     * @throws FileNotFoundException
     */
    protected void writeProblemTitleToFile(String filename, String title) throws FileNotFoundException {

        String parentDirectoryName = new File(filename).getParent();
        new File(parentDirectoryName).mkdirs();

        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);

        String titlePattern2 = "\\problemname{";

        String commentPattern = "%% plainproblemtitle:";

        // \problemtitle{Problem Name}

        writer.println(titlePattern2 + title + "}");

        // %% plainproblemtitle: Problem Name

        writer.println(commentPattern + title);

        writer.close();
        writer = null;
    }

    /**
     * Get problem letter for input integer.
     * 
     * getProblemLetter(1) is 'A'
     * 
     * @param id
     *            a one based problem number.
     * @return
     */
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
        String newName = name.trim().split(" ")[0].trim().toLowerCase(); // + (System.nanoTime() % 1000);
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
