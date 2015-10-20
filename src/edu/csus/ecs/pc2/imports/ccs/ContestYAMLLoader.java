package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.ccs.CCSConstants;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.YamlLoadException;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AutoJudgeSetting;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * Create contest from YAML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ContestYAMLLoader.java 225 2011-09-02 05:22:43Z laned $
 */

// TODO CCS REALLY IMPORTANT USER INTERFACE WORK - MUST SHOW SYNTAX ERRORS AND INPUT FILE PROBLEMS TO USER!!!

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/imports/ccs/ContestYAMLLoader.java $
public class ContestYAMLLoader {
    
//    private boolean debugMode = true;

    public static final String CONTEST_NAME_KEY = "name";

    public static final String SHORT_NAME_KEY = "short-name";

    public static final String CONTEST_START_TIME = "start-time";

    public static final String CONTEST_DURATION = "duration";

    public static final String SCOREBOARD_FREEZE = "scoreboard-freeze";

    public static final String LANGUAGE_KEY = "languages";

    public static final String DEFAULT_CLARS_KEY = "default-clars";

    public static final String CLAR_CATEGORIES_KEY = "clar-categories";

    public static final String PROBLEMS_KEY = "problemset";
    
    public static final String JUDGING_TYPE_KEY = "judging-type";
    
    public static final String MANUAL_REVIEW_KEY = "manual-review";

    public static final String COMPUTER_JUDGING_KEY = "computer-judged";
    
    public static final String SEND_PRELIMINARY_JUDGEMENT_KEY = "send-prelim-judgement";;
    
    public static final String ACCOUNTS_KEY = "accounts";

    public static final String SITES_KEY = "sites";

    public static final String REPLAY_KEY = "replay";
    
    /**
     * Base path/location where config (CDP) problem files found.
     */
    public static final String JUDGE_CONFIG_PATH_KEY = "judge-config-path";

    /**
     * Run execution time limit, in seconds.
     */
    public static final String TIMEOUT_KEY = "timeout";

    public static final String LIMITS_KEY = "limits";

    private static final String DELIMIT = ":";

    public static final String DEFAULT_CONTEST_YAML_FILENAME = "contest.yaml";

    public static final String DEFAULT_PROBLEM_YAML_FILENAME = "problem.yaml";

    public static final String DEFAULT_PROBLEM_LATEX_FILENAME = "problem.tex";

    /**
     * problem title key in problem.yaml
     */
    private static final String PROBLEM_NAME_KEY = "title";

    /**
     * Problem input (data files) key.
     */
    private static final String PROBLEM_INPUT_KEY = "input";

    /**
     * Default time out for run execution.
     */
    public static final int DEFAULT_TIME_OUT = 30;

    public static final String AUTO_JUDGE_KEY = "auto-judging";

    public static final String INPUT_KEY = "input";

    /**
     * Use external data files?   (if no, then no file contents loaded into pc2).  
     */
    public static final String PROBLEM_LOAD_DATA_FILES_KEY = "load-data-files";
    
    /**
     * Contest wide validator name.
     * 
     * This will be overridden by the individual problem validator name.  If
     * there is no default validator name specified the internal validator
     * will be used.
     * 
     */
    private static final String DEFAULT_VALIDATOR_KEY = "default-validator";

    /**
     * The override validator name, this supercedes all other validators.
     * 
     * No other validator def will override this value.
     */
    private static final String OVERRIDE_VALIDATOR_KEY = "override-validator";
    
//  default-validator: /home/pc2/yaml/default_validator
    /**
     * mtsv command line args.
     */
//  override-validator: /home/pc2/mtsv {:problemletter} {:resfile} {:basename} {:timelimit}
    private static final String MTSV_OVERRIDE_VALIDATOR_ARGS = "{:problemletter} {:resfile} {:basename} {:timelimit}";
    
    /**
     * mtsv program name.
     */
    private static final String MTSV_PROGRAM_NAME = "mtsv";
    
    /**
     * Validator per problem.
     */
    public static final String VALIDATOR_KEY = "validator";

    public static final String USING_PC2_VALIDATOR = "use-internal-validator";

    private static final String READ_FROM_STDIN_KEY = "readFromSTDIN";

    /**
     * Load Problem Data File Contents
     */
    private boolean loadProblemDataFiles = true;

    
    /**
     * Load contest data from contest.yaml.
     * 
     * @param contest
     * @param directoryName
     *            directory to load files from.
     * @return contest
     * 
     */
    public IInternalContest fromYaml(IInternalContest contest, String directoryName)  {
        // load external
       return fromYaml(contest, directoryName, false);
    }

    /**
     * Load contest, optionally load problem data files.
     * 
     * @param contest
     * @param directoryName
     *            directory to load files from.
     * @param loadDataFileContents true - load files, false do not load files (files considered external). 
     * @return contest
     * 
     */
    public IInternalContest fromYaml(IInternalContest contest, String directoryName, boolean loadDataFileContents)  {
        String[] contents;
        try {
            contents = loadFileWithIncludes(directoryName,  directoryName + File.separator + DEFAULT_CONTEST_YAML_FILENAME);
        } catch (IOException e) {
            throw new YamlLoadException(e);
        }
        return fromYaml(contest, contents, directoryName, loadDataFileContents);
    }
    
    
    /**
     * Load files with #include.
     * 
     * @param dirname if null will ignore #include files.
     * @param filename YAML input file
     * @throws IOException
     */
    public String[] loadFileWithIncludes(String dirname, String filename) throws IOException {
        
        if (! new File(filename).isFile()) {
            throw new FileNotFoundException(filename);
        }

        ArrayList<String> outs = new ArrayList<String>();

        String[] lines = Utilities.loadFile(filename);

        for (String line : lines) {

            outs.add(line);
            if (dirname != null && line.trim().startsWith("#include")) {
                String [] parts = line.split("\"");
                String includeFilename = dirname + File.separator + parts[1];
                String [] includeLines =  Utilities.loadFile(includeFilename);
                for (String string : includeLines) {
                    outs.add(string);
                }
                outs.add("# end include "+includeFilename);
            }
        }

        return (String[]) outs.toArray(new String[outs.size()]);
    }

    /**
     * Get title from YAML file.
     * 
     * @param contestYamlFilename
     * @return
     * @throws IOException
     */
    public String getContestTitle(String contestYamlFilename) throws IOException {
        String[] contents = loadFileWithIncludes(null, contestYamlFilename);
        String contestTitle = getSequenceValue(contents, CONTEST_NAME_KEY);
        return contestTitle;
    }
    
    public String getJudgesCDPBasePath(String contestYamlFilename) throws IOException {
        String[] contents = loadFileWithIncludes(null, contestYamlFilename);
        String contestTitle = getSequenceValue(contents, JUDGE_CONFIG_PATH_KEY);
        return contestTitle;
    }
    
    /**
     * Load/Create contest from YAML lines.
     * 
     * @param contest update/overwrite contest, if null creates new contest.
     * @param yamlLines input YAML lines from contest.yaml
     * @param directoryName location for contest.yaml and problem data files 
     * @param loadDataFileContents if true load the file's contents, if true buffer = null.
     */
    public IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String directoryName, boolean loadDataFileContents)  {

        contest = createContest(contest);

        // name: ACM-ICPC World Finals 2011

        String contestTitle = getSequenceValue(yamlLines, CONTEST_NAME_KEY);
        if (contestTitle != null) {
            setTitle(contest, contestTitle);
        }
        
        String judgeCDPath = getSequenceValue(yamlLines, JUDGE_CONFIG_PATH_KEY);
        if (judgeCDPath != null) {
            setCDPPath(contest, judgeCDPath);
        }

        int defaultTimeout = getIntegerValue(getSequenceValue(yamlLines, TIMEOUT_KEY), DEFAULT_TIME_OUT);

        for (String line : yamlLines) {
            if (line.startsWith(CONTEST_NAME_KEY + DELIMIT)) {
                setTitle(contest, unquoteAll(line.substring(line.indexOf(DELIMIT) + 1).trim()));

            }
        }
        
        loadDataFileContents = getBooleanValue(getSequenceValue(yamlLines, PROBLEM_LOAD_DATA_FILES_KEY), loadDataFileContents);
        
        // TODO CCS add contest settings
        // TODO CCS short-name: ICPC WF 2011
        // TODO CCS start-time: 2011-02-04 01:23Z
        // TODO CCS duration: 5:00:00
        // TODO CCS scoreboard-freeze: 4:00:00
        
        Language[] languages = getLanguages(yamlLines);
        for (Language language : languages) {
            contest.addLanguage(language);
        }

        String defaultValidatorCommandLine = getSequenceValue(yamlLines, DEFAULT_VALIDATOR_KEY); 
        
        String overrideValidatorCommandLine = getSequenceValue(yamlLines, OVERRIDE_VALIDATOR_KEY);
        
        
        if (overrideValidatorCommandLine == null){

            // if no override defined, then maybe use mtsv
            
            File mtsvFile = new File(directoryName + File.separator + MTSV_PROGRAM_NAME);
            if (mtsvFile.exists()) {
                /**
                 * If mtsv is in the same directory as the contest.yaml, then use the mtsv command line.
                 */
                overrideValidatorCommandLine = mtsvFile.getAbsolutePath() + " " + MTSV_OVERRIDE_VALIDATOR_ARGS;
            }
        }
        
        
        String[] sectionLines = getSectionLines(USING_PC2_VALIDATOR, yamlLines);

        boolean overrideUsePc2Validator = false;
        String usingValidator = getSequenceValue(sectionLines, ContestYAMLLoader.USING_PC2_VALIDATOR);
        
        if (usingValidator != null && usingValidator.equalsIgnoreCase("true")){
            overrideUsePc2Validator = true;
        }

        /**
         * Manual Review global override.
         */
        boolean manualReviewOverride = getBooleanValue(getSequenceValue(yamlLines, MANUAL_REVIEW_KEY), false);

        Problem[] problems = getProblems(yamlLines, defaultTimeout, loadDataFileContents, defaultValidatorCommandLine, overrideValidatorCommandLine, overrideUsePc2Validator, manualReviewOverride);
        
        if (loadProblemDataFiles){
            for (Problem problem : problems) {
                loadProblemInformationAndDataFiles(contest, directoryName, problem, overrideUsePc2Validator, manualReviewOverride);
            }
        }
        
        Site[] sites = getSites(yamlLines);
        for (Site site : sites) {
            contest.addSite(site);
        }

        String[] categories = loadGeneralClarificationAnswers(yamlLines);
        for (String name : categories) {
            contest.addCategory(new Category(name));
        }

        // String[] answers = getGeneralAnswers(yamlLines);
        // TODO CCS load general answer catagories into contest

        Account[] accounts = getAccounts(yamlLines);
        contest.addAccounts(accounts);

        AutoJudgeSetting[] autoJudgeSettings = null;
        if (problems.length == 0){
            /**
             * If no problems in input YAML assume problems are already defined
             * in contest/model.
             */
           autoJudgeSettings = getAutoJudgeSettings(yamlLines, contest.getProblems());
            
        } else{
           autoJudgeSettings = getAutoJudgeSettings(yamlLines, problems);

        }

        for (AutoJudgeSetting auto : autoJudgeSettings) {
            addAutoJudgeSetting(contest, auto);
        }

        PlaybackInfo playbackInfo = getReplaySettings(yamlLines);
        
        if (playbackInfo != null){
            contest.addPlaybackInfo(playbackInfo);
        }

        return contest;
    }

 
    private void setCDPPath(IInternalContest contest, String path) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setJudgeCDPBasePath(path);
    }

    public PlaybackInfo getReplaySettings(String[] yamlLines) {

        String[] sectionLines = getSectionLines(REPLAY_KEY, yamlLines);
        
        if (sectionLines.length == 0) {
            return null;
        }
        
        PlaybackInfo info = new PlaybackInfo();

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        // replay:
        // - title: Default Playback Name
        // file:
        // auto_start: no
        // minevents: 108
        // site: 1

        while (sequenceLines.length > 0) {

            String title = getSequenceValue(sequenceLines, "- title").trim();
            info.setDisplayName(title);

            String filename = getSequenceValue(sequenceLines, "file").trim();
            info.setFilename(filename);

            String startedStr = getSequenceValue(sequenceLines, "auto_start");
            boolean started = getBooleanValue(startedStr, false);
            info.setStarted(started);

            String waitString = getSequenceValue(sequenceLines, "pacingMS").trim();
            int waitTimeBetweenEventsMS = getIntegerValue(waitString, 1000);
            info.setWaitBetweenEventsMS(waitTimeBetweenEventsMS);

            String countString = getSequenceValue(sequenceLines, "minevents").trim();
            int minEvents = getIntegerValue(countString, 1);
            info.setMinimumPlaybackRecords(minEvents);

            String siteString = getSequenceValue(sequenceLines, "site");
            int siteNumber = getIntegerValue(siteString, 1);
            info.setSiteNumber(siteNumber);

            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }

        return info;
    }

    private void addAutoJudgeSetting(IInternalContest contest, AutoJudgeSetting auto)  {

        Account account = contest.getAccount(auto.getClientId());
        if (account == null) {
            throw new YamlLoadException("No such account for auto judge setting, undefined account is " + auto.getClientId());
        }

        ClientSettings clientSettings = contest.getClientSettings(auto.getClientId());
        if (clientSettings == null) {
            clientSettings = new ClientSettings(auto.getClientId());
        }
        clientSettings.setAutoJudgeFilter(auto.getProblemFilter());
        clientSettings.setAutoJudging(auto.isActive());

        // dumpAJSettings(clientSettings.getClientId(), clientSettings.isAutoJudging(), clientSettings.getAutoJudgeFilter());

        contest.addClientSettings(clientSettings);
    }

    // TODO 669 remove after debugged
    @SuppressWarnings("unused")
    private void dumpAJSettings(ClientId clientId, boolean autoJudging, Filter autoJudgeFilter) {
        ElementId[] ids = autoJudgeFilter.getProblemIdList();
        System.out.println("Auto Judge b4  " + clientId + "  " + ids.length + " problems, aj on = " + autoJudging);

        System.out.print("     sent      ");
        for (ElementId id : ids) {
            System.out.print(id + " ");
        }
        System.out.println();
    }

    public Site[] getSites(String[] yamlLines) {

        String[] sectionLines = getSectionLines(SITES_KEY, yamlLines);

        Vector<Site> sitesVector = new Vector<Site>();

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        while (sequenceLines.length > 0) {

            /*
             * <pre> sites: - number: 1 name: Site 1 IP: localhost port: 50002 </pre>
             */

            String siteNumberString = getSequenceValue(sequenceLines, "- number");
            String siteTitle = getSequenceValue(sequenceLines, "name").trim();

            int siteNumber = getIntegerValue(siteNumberString.trim(), 0);

            Site site = new Site(siteTitle, siteNumber);

            String hostName = getSequenceValue(sequenceLines, "IP").trim();
            String portString = getSequenceValue(sequenceLines, "port").trim();

            String password = getSequenceValue(sequenceLines, "password");
            if (password == null) {
                password = "site" + siteNumberString;
            }
            site.setPassword(password.trim());

            Properties props = new Properties();
            props.put(Site.IP_KEY, hostName);
            props.put(Site.PORT_KEY, portString);
            site.setConnectionInfo(props);

            sitesVector.addElement(site);

            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }

        return (Site[]) sitesVector.toArray(new Site[sitesVector.size()]);

    }

    private Account[] getAccounts(String[] yamlLines) {

        String[] sectionLines = getSectionLines(ACCOUNTS_KEY, yamlLines);

        Vector<Account> accountVector = new Vector<Account>();

        AccountList accountList = new AccountList();

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        while (sequenceLines.length > 0) {
            // TODO CCS do a proper parsing of Yaml to handle preceding comment lines 
            /**
             * There is a bug where the section parsing does not ignore blank and
             * comment lines preceding a section.
             */
               
            if (! sequenceLines[0].trim().startsWith("-")) {
                idx += sequenceLines.length;
                sequenceLines = getNextSequence(sectionLines, idx);
                continue;
            }

            // * - account: TEAM

            String accountType = getSequenceValue(sequenceLines, "- account");
            ClientType.Type type = ClientType.Type.valueOf(accountType.trim());

            int startNumber = 1;
            String startNumberString = getSequenceValue(sequenceLines, "start");
            if (startNumberString != null){
                startNumber = getIntegerValue(startNumberString.trim(), 1);
            }
            String countString = getSequenceValue(sequenceLines, "count").trim();
            int count = getIntegerValue(countString, 1);
            String siteString = getSequenceValue(sequenceLines, "site");
            int siteNumber = getIntegerValue(siteString, 1);

            /**
             * <pre>
             * 
             * accounts:
             *   -account: TEAM
             *       site: 1
             *      start: 100
             *      count: 14
             * 
             *   -account: JUDGE
             *       site: 1
             *      count: 12
             * </pre>
             */

            Vector<Account> newAccounts = accountList.generateNewAccounts(type, count, startNumber, PasswordType.JOE, siteNumber, true);
            
            accountVector.addAll(newAccounts);

            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }

        return (Account[]) accountVector.toArray(new Account[accountVector.size()]);
    }

    /**
     * Loads problem data files and validator from problem.yaml file.
     * 
     * The problem.yaml is found at baseDirectoryName, unless problem is not null then
     * the problem.yaml is found at the problem.getShortName() under baseDirectoryName.
     * 
     * @param contest
     * @param baseDirectoryName location for contest.yaml and problem short names
     * @param problem
     * @param dataFiles
     */
    public void loadProblemInformationAndDataFiles(IInternalContest contest, String baseDirectoryName, Problem problem, boolean overrideUsePc2Validator) {
        loadProblemInformationAndDataFiles(contest, baseDirectoryName, problem, overrideUsePc2Validator, false);
    }
    
    /**
     * Load problem into and data files for input problem.
     * 
     * Read problem.yaml.
     * 
     * @param contest
     * @param baseDirectoryName
     * @param problem
     * @param overrideUsePc2Validator
     * @param overrideManualReview
     */
    public void loadProblemInformationAndDataFiles(IInternalContest contest, String baseDirectoryName, Problem problem, boolean overrideUsePc2Validator, boolean overrideManualReview) {

        // TODO CCS code this: do not add problem to contest model, new new parameter flag

        String problemDirectory = baseDirectoryName + File.separator + problem.getShortName();

        problem.setExternalDataFileLocation(problemDirectory);

        String problemYamlFilename = problemDirectory + File.separator + DEFAULT_PROBLEM_YAML_FILENAME; // read problem.yaml.
        String[] contents;
        try {
            contents = loadFileWithIncludes(baseDirectoryName, problemYamlFilename);
        } catch (IOException e) {
            throw new YamlLoadException(e);
        }

        if (contents.length == 0) {
            throw new YamlLoadException("Can not load problem.yaml: " + problemYamlFilename);
        }

        String problemLaTexFilename = problemDirectory + File.separator + "problem_statement" + File.separator + DEFAULT_PROBLEM_LATEX_FILENAME;
        
        String problemTitle = getSequenceValue(contents, PROBLEM_NAME_KEY);
        
        if (new File(problemLaTexFilename).isFile()) {
            problemTitle = getProblemNameFromLaTex(problemLaTexFilename);
        }

        boolean pc2FormatProblemYamlFile = isPC2FormatProblemYaml(contents);
        
//        if (overrideUsePc2Validator){
//            pc2FormatProblemYamlFile = true; 
//        }

        String[] sectionLines = getSectionLines(PROBLEM_INPUT_KEY, contents);

        if (problemTitle == null && (pc2FormatProblemYamlFile)) {
            problemTitle = getSequenceValue(sectionLines, "name");
        }
        
        if (problemTitle == null) {
            syntaxError("No problem name found for " + problem.getShortName()+" in "+problemLaTexFilename);
        }
        
        problem.setDisplayName(problemTitle);

        String dataFileBaseDirectory = problemDirectory + File.separator + "data" + File.separator + "secret";

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
        
        if (pc2FormatProblemYamlFile) {
            loadPc2ProblemFiles(contest, dataFileBaseDirectory, problem, problemDataFiles, sectionLines);
        } else {
            problem.setComputerJudged(true); // CCS default computer judged
            loadCCSProblemFiles(contest, dataFileBaseDirectory, problem, problemDataFiles, sectionLines);
        }

        sectionLines = getSectionLines(LIMITS_KEY, contents);
        if (sectionLines.length > 1) {
            String timeOut = getSequenceValue(sectionLines, TIMEOUT_KEY);
            if (timeOut != null) {
                problem.setTimeOutInSeconds(Integer.parseInt(timeOut.trim()));
            }
        }

        if (!pc2FormatProblemYamlFile) {
            problem.setComputerJudged(true);

            // TODO CCS add CCS validator derived based on build script

            /**
             * - Use CCS build command to build validator ('build' script name) - add validator created by build command - add CCS run script ('run' script name)
             */

            // default-validator: /home/pc2/Desktop/codequest12_problems/default_validator
            // override-validator: /usr/local/bin/mtsv {:problemletter} {:resfile} {:basename}

            // `$validate_cmd $inputfile $answerfile $feedbackfile < $teamoutput `;

            addCCSValidator(problem, problemDataFiles, baseDirectoryName);

        } else {
            problem.setComputerJudged(true);
            addDefaultPC2Validator(problem, 1);
        }
        
        sectionLines = getSectionLines(JUDGING_TYPE_KEY, contents);

        assignJudgingType(sectionLines, problem, overrideManualReview);
        
        sectionLines = getSectionLines(INPUT_KEY, contents);
        
        String stdinLoadString = getSequenceValue(contents, READ_FROM_STDIN_KEY);

        boolean readFromStdin = getBooleanValue(stdinLoadString, true);
        problem.setReadInputDataFromSTDIN(readFromStdin);
    }

    protected void assignJudgingType(String[] sectionLines, Problem problem, boolean overrideManualReviewFlag) {
        
        boolean sendPreliminary = getBooleanValue(getSequenceValue(sectionLines, SEND_PRELIMINARY_JUDGEMENT_KEY), false);
        if (sendPreliminary){
            problem.setPrelimaryNotification(true);
        }
        
        boolean computerJudged = getBooleanValue(getSequenceValue(sectionLines, COMPUTER_JUDGING_KEY), problem.isComputerJudged());
        problem.setComputerJudged(computerJudged);
        
        boolean manualReview = getBooleanValue(getSequenceValue(sectionLines, MANUAL_REVIEW_KEY), false);
        
        if (overrideManualReviewFlag){
            manualReview = true;
        }
        
        if (manualReview){
            problem.setManualReview(true);
        }
        
    }

    /**
     * 
     * @param contents yaml contents
     * @return
     */
    protected boolean isPC2FormatProblemYaml(String[] contents) {

        boolean pc2Format = false;

        String[] sectionLines = getSectionLines(VALIDATOR_KEY, contents);
        String usingValidator = getSequenceValue(sectionLines, ContestYAMLLoader.USING_PC2_VALIDATOR);

        if (usingValidator != null && usingValidator.equalsIgnoreCase("true")) {
            pc2Format = true;
        }

        return pc2Format;
    }

    private void syntaxWarning(String message) {
        System.err.println("Warning - "+message);
    }

    protected String getProblemNameFromLaTex(String filename) {

        String[] lines;
        try {
            lines = loadFileWithIncludes(null, filename);
        } catch (IOException e) {
            return null;
        }

        String name = null;

        String titlePattern = "\\problemtitle{";
        
        String titlePattern2 = "\\problemname{";

        String commentPattern = "%% plainproblemtitle:";

        for (String line : lines) {
            // Now create matcher object.

            if (line.indexOf("problemtitle") != -1) {

                // %% plainproblemtitle: Problem Name
                if (line.trim().startsWith(commentPattern)) {
                    name = line.trim().substring(commentPattern.length()).trim();
                    break;
                }

                // \problemtitle{Problem Name}

                if (line.trim().startsWith(titlePattern)) {
                    name = line.trim().substring(titlePattern.length()).trim();
                    // name = name.replace(Pattern.quote(")"), "");
                    // name = name.replace(")", "");
                    name = name.substring(0, name.length() - 1);
                    break;
                }
                
            }
            
            if (line.indexOf("problemname") != -1) {
                
                // \problemname{Problem Name}
                

                if (line.trim().startsWith(titlePattern2)) {
                    name = line.trim().substring(titlePattern2.length()).trim();
                    // name = name.replace(Pattern.quote(")"), "");
                    // name = name.replace(")", "");
                    name = name.substring(0, name.length() - 1);
                    break;
                }
            }
        }
        return name;
    }

    /**
     * Load CCS data files, and validator into contest.
     * 
     * @param contest
     * @param dataFileBaseDirectory - directory where data files are.
     * @param problem
     * @param problemDataFiles 
     * @param sectionLines
     * 
     */
    protected Problem loadCCSProblemFiles(IInternalContest contest, String dataFileBaseDirectory, Problem problem, ProblemDataFiles problemDataFiles, String[] sectionLines)  {

        if (problem.getShortName() == null) {
            throw new YamlLoadException("  For " + problem + " missing problem short name");
        }
        
        @SuppressWarnings("unused")
        long totBytes = 0;
        
        /**
         * Data files are external so data files should not be loaded into problem data files.
         */
        boolean loadExternalFile  = problem.isUsingExternalDataFiles();
        
        String[] inputFileNames = getFileNames(dataFileBaseDirectory, ".in");
        String[] answerFileNames = getFileNames(dataFileBaseDirectory, ".ans");

        if (inputFileNames.length == 0) {
            throw new YamlLoadException("Expecting input (.in) files for " + problem.getDisplayName() + " in dir " + dataFileBaseDirectory);
        }

        if (answerFileNames.length == 0) {
            throw new YamlLoadException("Expecting answer (.ans) files forr " + problem.getDisplayName() + " in dir " + dataFileBaseDirectory);
        }

        if (inputFileNames.length == answerFileNames.length) {

            Arrays.sort(inputFileNames);

            ArrayList<SerializedFile> dataFiles = new ArrayList<SerializedFile>();
            ArrayList<SerializedFile> answerFiles = new ArrayList<SerializedFile>();

            for (int idx = 0; idx < inputFileNames.length; idx++) {

                problem.addTestCaseFilenames(inputFileNames[idx], answerFileNames[idx]);

                String dataFileName = dataFileBaseDirectory + File.separator + inputFileNames[idx];
                String answerFileName = dataFileName.replaceAll(".in$", ".ans");

                if (idx == 0) {
                    problem.setDataFileName(Utilities.basename(dataFileName));
                    problem.setAnswerFileName(Utilities.basename(answerFileName));
                }

                String answerShortFileName = inputFileNames[idx].replaceAll(".in$", ".ans");

                checkForFile(dataFileName, "Missing " + inputFileNames[idx] + " file for " + problem.getShortName() + " in " + dataFileBaseDirectory);
                checkForFile(answerFileName, "Missing " + answerShortFileName + " file for " + problem.getShortName() + " in " + dataFileBaseDirectory);

                dataFiles.add(new SerializedFile(dataFileName, loadExternalFile));
                answerFiles.add(new SerializedFile(answerFileName, loadExternalFile));
                
                totBytes += new File(dataFileName).length();
                totBytes += new File(answerFileName).length();
            }

            if (dataFiles.size() > 0) {

                SerializedFile[] data = (SerializedFile[]) dataFiles.toArray(new SerializedFile[dataFiles.size()]);
                SerializedFile[] answer = (SerializedFile[]) answerFiles.toArray(new SerializedFile[answerFiles.size()]);

//                dumpSerialzedFileList (problem, "Judges data", data);
//                dumpSerialzedFileList (problem, "Judges answer", answer);
                
                problemDataFiles.setJudgesDataFiles(data);
                problemDataFiles.setJudgesAnswerFiles(answer);
                
                problem.setDataFileName(inputFileNames[0]);
                problem.setAnswerFileName(inputFileNames[0].replaceAll(".in$", ".ans"));
            } else {
                syntaxWarning("There were no data files found/loaded for " + problem.getShortName());
            }

            problem.setReadInputDataFromSTDIN(true);

        } else {
            throw new YamlLoadException("  For " + problem.getShortName() + " Missing files -  there are " + inputFileNames.length + " .in files and " + //
                    answerFileNames.length + " .ans files " + " in " + dataFileBaseDirectory);
        }


        if (inputFileNames.length == 0) {
            throw new YamlLoadException("  For " + problem.getShortName() + " Missing files -  there are " + inputFileNames.length + " .in files and " + //
                    answerFileNames.length + " .ans files " + " in " + dataFileBaseDirectory);
        }

        contest.addProblem(problem, problemDataFiles);

        validateCCSData(contest, problem);
        
        return problem;
    }

    public void dumpSerialzedFileList(Problem problem, String logPrefixId, SerializedFile[] sfList) {

        System.out.println(logPrefixId + ": There are "+sfList.length+"files in list for problem "+problem);
        int count = 1;
        for (SerializedFile serializedFile : sfList) {
            System.out.println(logPrefixId + ": " + count + " " + serializedFile);
            count++;
        }
    }

    private void validateCCSData(IInternalContest contest, Problem problem) {
        
        // TODO CCS Load Validator
        
        // TODO CCS somehow find validator, compile, and test.
        // the somehow is because there may be more than one validator in the validator directory

        // TODO 1. Check files (all files present as required + check problem.yaml)
        // TODO 2. Check compile (check that all programs compile)
        // TODO 3. Check input (run input validators)
        // TODO 4. Check solutions (run all solutions check that they get the expected verdicts)
        
    }

    /**
     * Check for existence of file, if does not exist throw exception with message.
     * 
     * @param filename
     * @param message
     * 
     */
    private void checkForFile(String filename, String message)  {

        if (!(new File(filename).isFile())) {
            throw new YamlLoadException(message);
        }
    }

    /**
     * Get list of filenames with extension in directory, return in sorted order.
     * 
     * @param directoryName
     * @param extension
     * @return
     */
    protected String[] getFileNames(String directoryName, String extension) {

        Vector<String> list = new Vector<String>();
        File dir = new File(directoryName);

        String[] entries = dir.list();
        if (entries == null) {
            return new String[0];
        }
        Arrays.sort(entries);

        for (String name : entries) {
            if (name.endsWith(extension)) {
                list.addElement(name);
            }
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Load pc2 data files.
     * 
     * @param contest
     * @param dataFileBaseDirectory - directory where data files are.
     * @param problem
     * @param problemDataFiles2 
     * @param sectionLines
     */
    private void loadPc2ProblemFiles(IInternalContest contest, String dataFileBaseDirectory, Problem problem, ProblemDataFiles problemDataFiles2, String[] sectionLines)  {

        String dataFileName = getSequenceValue(sectionLines, "datafile");
        String answerFileName = getSequenceValue(sectionLines, "answerfile");

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
        
//        if (dataFileName == null){
//            syntaxError("Missing datafile for pc2 problem "+problem.getShortName());
//        }
//
//        if (dataFileName == null){
//            syntaxError("Missing datafile for pc2 problem "+problem.getShortName());
//        }
        
        pc2AddDataFiles(problem, problemDataFiles, dataFileBaseDirectory, dataFileName, answerFileName);
        contest.addProblem(problem, problemDataFiles);
    }

    /**
     * Load pc2 specific data/answer files.
     * 
     * There is no requirement that data sets match for pc2 problems. 
     * 
     * @param problem
     * @param problemDataFiles
     * @param dataFileBaseDirectory
     * @param dataFileName
     * @param answerFileName
     */
    protected void pc2AddDataFiles(Problem problem, ProblemDataFiles problemDataFiles, String dataFileBaseDirectory, String dataFileName, String answerFileName)  {
        
        @SuppressWarnings("unused")
        long totBytes = 0;
        
        
        // load judge data file
        if (dataFileName != null) {
            String dataFilePath = dataFileBaseDirectory + File.separator + dataFileName;
            
            if (fileNotThere(dataFilePath)) {
                throw new YamlLoadException("Missing data file " + dataFilePath);
            }

            problem.setDataFileName(dataFileName);
            problem.setReadInputDataFromSTDIN(false);

            SerializedFile serializedFile = new SerializedFile(dataFilePath, problem.isUsingExternalDataFiles());
            problemDataFiles.setJudgesDataFile(serializedFile);
        } 
        
        
        /**
         * Load .in or .dat files
         */
        
        String[] inputFileNames = getFileNames(dataFileBaseDirectory, ".in");
        if (inputFileNames.length == 0) {
            inputFileNames = getFileNames(dataFileBaseDirectory, ".dat"); // or the input file could be extension .dat
        }
        
        if (inputFileNames.length > 0) {
            SerializedFile[] serializedFiles = new SerializedFile[inputFileNames.length];
            int count = 0;

            for (String inFilename : inputFileNames) {
                totBytes += new File(dataFileBaseDirectory + File.separator +inFilename).length();
                serializedFiles[count] = new SerializedFile(dataFileBaseDirectory + File.separator +inFilename, problem.isUsingExternalDataFiles());
                count++;
            }
            problemDataFiles.setJudgesDataFiles(serializedFiles);
            
            if (dataFileName == null){
                problem.setDataFileName(serializedFiles[0].getName());
            }
        }
        

        if (answerFileName != null) {
            
            String answerFilePath = dataFileBaseDirectory + File.separator + answerFileName;
            if (fileNotThere(answerFilePath)) {
                throw new YamlLoadException("Missing data file " + answerFilePath);
            }

            problem.setAnswerFileName(answerFileName);

            SerializedFile serializedFile = new SerializedFile(answerFilePath, problem.isUsingExternalDataFiles());
            problemDataFiles.setJudgesAnswerFile(serializedFile);
        }
        

        /**
         * Load .ans files
         */
        String[] answerFileNames = getFileNames(dataFileBaseDirectory, ".ans");

        if (answerFileNames.length > 0) {
            SerializedFile[] answerSerializedFiles = new SerializedFile[answerFileNames.length];
            int count = 0;

            for (String ansFilename : answerFileNames) {
                totBytes += new File(dataFileBaseDirectory + File.separator +ansFilename).length();
                answerSerializedFiles[count] = new SerializedFile(dataFileBaseDirectory + File.separator + ansFilename, problem.isUsingExternalDataFiles());
                count++;
            }
            problemDataFiles.setJudgesAnswerFiles(answerSerializedFiles);
            
            if (answerFileName == null){
                problem.setAnswerFileName(answerSerializedFiles[0].getName());
            }
        }
        

        if (answerFileName != null) {
            
            String answerFilePath = dataFileBaseDirectory + File.separator + answerFileName;
            if (fileNotThere(answerFilePath)) {
                throw new YamlLoadException("Missing data file " + answerFilePath);
            }

            problem.setAnswerFileName(answerFileName);

            SerializedFile serializedFile = new SerializedFile(answerFilePath, problem.isUsingExternalDataFiles());
            problemDataFiles.setJudgesAnswerFile(serializedFile);
        } 
        
//        
//        ProblemDataFiles pdfiles = problemDataFiles;
//        if (pdfiles != null) {
//            long totalBytes = 0;
//            for (SerializedFile serializedFile : pdfiles.getJudgesAnswerFiles()) {
//                totalBytes += serializedFile.getBuffer().length;
//                System.out.println("debug totalBytes = "+totalBytes);
//            }
//            for (SerializedFile serializedFile : pdfiles.getJudgesDataFiles()) {
//                totalBytes += serializedFile.getBuffer().length;
//                System.out.println("debug totalBytes A = "+totalBytes);
//            }
//            System.out.println("debug totalBytes is "+totalBytes);
//        }
    }

    private boolean fileNotThere(String name) {
        return !new File(name).isFile();
    }

    public Problem addDefaultPC2Validator(Problem problem, int optionNumber) {

        problem.setCcsMode(false);
        
        problem.setValidatedProblem(true);
        problem.setUsingPC2Validator(true);
        problem.setWhichPC2Validator(optionNumber);
        problem.setIgnoreSpacesOnValidation(true);

        problem.setValidatorCommandLine(Constants.DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND + " -pc2 " + problem.getWhichPC2Validator() + " " + problem.isIgnoreSpacesOnValidation());
        problem.setValidatorProgramName(Problem.INTERNAL_VALIDATOR_NAME);

        return problem;
    }

    private Problem addCCSValidator(Problem problem, ProblemDataFiles problemDataFiles, String baseDirectoryName) {

        problem.setCcsMode(true);

        problem.setValidatedProblem(true);
        problem.setUsingPC2Validator(false);
        problem.setReadInputDataFromSTDIN(true);


        if (problem.getValidatorProgramName() == null){
            problem.setValidatorProgramName(CCSConstants.DEFAULT_CCS_VALIATOR_NAME);
        }

        // if we use the internal Java CCS validator use this.
        // problem.setValidatorCommandLine("java -cp {:pc2jarpath} " + CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND);
        if (problem.getValidatorCommandLine() == null){
            problem.setValidatorCommandLine(CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND);
        }

        String validatorName = baseDirectoryName + File.separator + problem.getValidatorProgramName();

        try {
            /**
             * If file is there load it 
             */
            if (new File(validatorName).isFile()){
                problemDataFiles.setValidatorFile(new SerializedFile(validatorName));
            }
        } catch (Exception e) {
            throw new YamlLoadException("Unable to load validator for problem " + problem.getShortName() + ": " + validatorName, e);
        }

        // problem.setValidatorCommandLine("java -cp {:pc2jarpath} " + CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND);

        return problem;
    }

    /**
     * Load list of strings.
     * 
     * @param key
     * @param yamlLines
     * @return
     */
    public String[] loadStringList(String key, String[] yamlLines) {
        String[] sectionLines = getSectionLines(key, yamlLines);

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        Vector<String> outArrays = new Vector<String>();

        while (sequenceLines.length > 0) {
            String line = sequenceLines[0].trim();
            if (line.startsWith("-")) {
                line = line.substring(1);
            }
            outArrays.addElement(line.trim());
            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }
        return (String[]) outArrays.toArray(new String[outArrays.size()]);

    }

    public String[] loadGeneralClarificationAnswers(String[] yamlLines) {
        return loadStringList(CLAR_CATEGORIES_KEY, yamlLines);
    }

    public String[] getGeneralAnswers(String[] yamlLines) {
        return loadStringList(DEFAULT_CLARS_KEY, yamlLines);
    }

    /**
     * Get list of Clarification Categories from YAML.
     * 
     * @param yamlLines
     * @return
     */
    public String[] getClarificationCategories(String[] yamlLines) {
        String[] sectionLines = getSectionLines(CLAR_CATEGORIES_KEY, yamlLines);

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        Vector<String> outArrays = new Vector<String>();

        while (sequenceLines.length > 0) {
            String line = sequenceLines[0].trim();
            if (line.startsWith("-")) {
                line = line.substring(1);
            }
            outArrays.addElement(line.trim());
            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }
        return (String[]) outArrays.toArray(new String[outArrays.size()]);
    }

    /**
     * Get/create {@link Language}s from YAML lines.
     * 
     * @param yamlLines
     * @return list of {@link Language}s
     */
    public Language[] getLanguages(String[] yamlLines)  {

        String[] sectionLines = getSectionLines(LANGUAGE_KEY, yamlLines);

        Vector<Language> languageList = new Vector<Language>();

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        while (sequenceLines.length > 0) {
            String name = getSequenceValue(sequenceLines, "name");

            if (name == null) {
                syntaxError("Language name field missing in languages section");
            } else {
                Language language = new Language(name);

                Language lookedupLanguage = LanguageAutoFill.languageLookup(name);
                String compilerName = getSequenceValue(sequenceLines, "compilerCmd");

                if (compilerName == null && lookedupLanguage != null) {
                    language = lookedupLanguage;
                    language.setDisplayName(name);
                } else if (compilerName == null) {
                    throw new YamlLoadException("Language \"" + name + "\" missing compiler command line");
                } else {

                    String compilerArgs = getSequenceValue(sequenceLines, "compiler-args");
                    String interpreter = getSequenceValue(sequenceLines, "runner");
                    String interpreterArgs = getSequenceValue(sequenceLines, "runner-args");
                    String exeMask = getSequenceValue(sequenceLines, "exemask");
                    // runner + runner-args, so what is execCmd for ?
                    // String execCmd = getSequenceValue(sequenceLines, "execCmd");

                    if (compilerArgs == null) {
                        language.setCompileCommandLine(compilerName);
                    } else {
                        language.setCompileCommandLine(compilerName + " " + compilerArgs);
                    }
                    language.setExecutableIdentifierMask(exeMask);

                    String programExecuteCommandLine = null;
                    if (interpreter == null) {
                        programExecuteCommandLine = "a.out";
                    } else {
                        if (interpreterArgs == null) {
                            programExecuteCommandLine = interpreter;
                        } else {
                            programExecuteCommandLine = interpreter + " " + interpreterArgs;
                        }
                    }
                    language.setProgramExecuteCommandLine(programExecuteCommandLine);
                }

                String activeStr = getSequenceValue(sequenceLines, "active");
                boolean active = getBooleanValue(activeStr, true);
                language.setActive(active);

                String useStr = getSequenceValue(sequenceLines, "use-judge-cmd");
                active = getBooleanValue(useStr, false);
                language.setUsingJudgeProgramExecuteCommandLine(active);
                
                String judgeExecuteCommandLine = getSequenceValue(sequenceLines, "judge-exec-cmd");
                if (judgeExecuteCommandLine != null){
                    language.setJudgeProgramExecuteCommandLine(judgeExecuteCommandLine);
                }

                // TODO handle interpreted languages, seems it should be in the export

                if (valid(language, name)) {
                    languageList.addElement(language);
                }

            }

            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }

        return (Language[]) languageList.toArray(new Language[languageList.size()]);
    }

    private boolean valid(Language language, String prefix)  {
        checkField(language.getDisplayName(), prefix + " Compiler Display name");
        checkField(language.getCompileCommandLine(), prefix + " Compile Command line");
        return true;
    }

    private void checkField(String field, String fieldName)  {
        if (field == null) {
            throw new YamlLoadException("Missing " + fieldName);
        } else if (field.trim().length() == 0) {
            throw new YamlLoadException("Missing " + fieldName);
        }
    }

    /**
     * Get/Load {@link Problem}s from YAML lines.
     * 
     * Load from problemset section.
     * 
     * @param yamlLines
     * @param seconds
     *            timeout for run execution in seconds
     * @param loadDataFileContents 
     * @return list of {@link Problem}
     * 
     */
    public Problem[] getProblems(String[] yamlLines, int seconds, boolean loadDataFileContents, String defaultValidatorCommand, String overrideValidatorCommandLine, boolean overrideUsePc2Validator,
            boolean todobool) {

        String[] linesFromSection = getSectionLines(PROBLEMS_KEY, yamlLines);
        
        if (linesFromSection.length == 0){
            /**
             * No problems defined, no point in finding them.
             */
            return new Problem[0];
        }

        Vector<Problem> problemList = new Vector<Problem>();

        int idx = 1;
        String[] sequenceLines = getNextSequence(linesFromSection, idx);

        while (sequenceLines.length > 0) {
            
            // TODO CCS replace with Snakeyaml because poor parsing of Yaml to handle preceding comment lines 
            /**
             * There is a bug where the section parsing does not ignore blank and
             * comment lines preceding a section.
             */
               
            if (! sequenceLines[0].trim().startsWith("-")) {
                idx += sequenceLines.length;
                sequenceLines = getNextSequence(linesFromSection, idx);
                continue;
            }
            
            String problemKeyName = getSequenceValue(sequenceLines, SHORT_NAME_KEY);
            
            if (problemKeyName == null){
                syntaxError("Missing "+SHORT_NAME_KEY+" in probset section");
            }
            
            /**
             * <pre>
             *  problemset:
             *    - letter:     A
             *      short-name: apl
             *      color:      yellow
             *      rgb:        #ffff00
             * # optional title
             *      title:      APL Rules!
             * </pre>
             */
            
            String problemTitle = getSequenceValue(sequenceLines, PROBLEM_NAME_KEY);
            if (problemTitle == null){
                problemTitle = problemKeyName;
            }

            Problem problem = new Problem(problemTitle);

            int actSeconds = getIntegerValue(getSequenceValue(sequenceLines, TIMEOUT_KEY), seconds);
            problem.setTimeOutInSeconds(actSeconds);
            
            problem.setShowCompareWindow(false);

            problem.setShortName(problemKeyName);
            if (! problem.isValidShortName()) {
                throw new YamlLoadException("Invalid short problem name '"+problemKeyName+"'");
            }

             String problemLetter = getSequenceValue(sequenceLines, "letter");
             String colorName = getSequenceValue(sequenceLines, "color");
             String colorRGB = getSequenceValue(sequenceLines, "rgb");

            // TODO CCS assign Problem variables for color and letter
            problem.setLetter(problemLetter);
            problem.setColorName(colorName);
            problem.setColorRGB(colorRGB);
            
            String internalFilesUsesString = getSequenceValue(sequenceLines, PROBLEM_LOAD_DATA_FILES_KEY);
            /**
             * Loading files internally flag.
             */
            boolean loadFilesFlag = getBooleanValue(internalFilesUsesString, loadDataFileContents);
            problem.setUsingExternalDataFiles(! loadFilesFlag);
            
            String validatorCommandLine = getSequenceValue(sequenceLines, VALIDATOR_KEY);

            if (validatorCommandLine == null) {
                validatorCommandLine = defaultValidatorCommand;
            }
            if (overrideValidatorCommandLine != null) {
                validatorCommandLine = overrideValidatorCommandLine;
            }
            problem.setValidatorCommandLine(validatorCommandLine);
            
            problemList.addElement(problem);

            idx += sequenceLines.length;
            sequenceLines = getNextSequence(linesFromSection, idx);
            
            /**
             * Set input file/stdin method
             */
            problem.setReadInputDataFromSTDIN(true); // default stdin
        }

        return (Problem[]) problemList.toArray(new Problem[problemList.size()]);
    }

    /**
     * Return true if are no actual def lines.
     * 
     * 
     * 
     * @param sequenceLines
     * @return true if section composed of blank and comment lines only.
     */
//    private boolean noActualSectionDef(String[] sequenceLines) {
//        int commentLines = 0;
//        
//        for (String string : sequenceLines) {
//            if ("".equals(string.trim()) || string.trim().startsWith("#")){
//                commentLines ++;
//            }
//        }
//        return commentLines == sequenceLines.length;
//    }

    private void syntaxError(String string)  {
        YamlLoadException exception = new YamlLoadException("Syntax error: " + string);
        exception.printStackTrace();
        throw exception;
    }

    /**
     * Get the value, null if no key found.
     * 
     * @param lines
     * @param key
     * @return null if not found, otherwise the value for the key
     */
    private String getSequenceValue(String[] lines, String key) {
        for (String line : lines) {
            String keyString = key + DELIMIT;
            if (line.trim().startsWith(keyString)) {
                return unquoteAll(line.trim().substring(keyString.length()).trim());
            }

            keyString = "- " + key + DELIMIT;
            if (line.trim().startsWith(keyString)) {
                return unquoteAll(line.trim().substring(keyString.length()).trim());
            }
        }
        return null;
    }

    /**
     * Remove trailing and leading quote char
     * @param string
     * @param quoteChar 
     * @return
     */
    protected String unquote(String string, String quoteChar) {
        if (string.startsWith(quoteChar)){
            String newString = string.substring(1);
            if (newString.endsWith(quoteChar)){
                newString = newString.substring(0,newString.length()-1);
            }
            return newString;
        }
        return string;
    }
    
    /**
     * Unquotes either ' or ".
     * 
     * If the first character is a ' then will strip leading/trailing ' <br>
     * or If the first character is a " then will strip leading/trailing ". <br>
     * 
     * @param string
     * @return
     */
    protected String unquoteAll(String string){
        if (string.startsWith("'")){
            return unquote(string, "'");
            
        } else if (string.startsWith("\"")){
            return unquote(string, "\"");
            
        } else {
            return string;
        }
    }

    public String[] getNextSequence(String[] sectionLines, int idx) {

        Vector<String> lines = new Vector<String>();
        
        for (int i = idx; i < sectionLines.length; i++) {
            String line = sectionLines[i];
            if (i > idx && line.trim().startsWith("-")) {
                break;
            }
            lines.addElement(line);
        }

        return (String[]) lines.toArray(new String[lines.size()]);
    }

    public boolean isNewSection(String line) {
        if (line == null) {
            return false;
        }
        if (line.indexOf(DELIMIT) > 1) {
            char firstChar = line.charAt(0);
            return !((firstChar == ' ') || (firstChar == '-') || (firstChar == '#'));
        }
        return false;
    }

    /**
     * get all section lines including section name line.
     * 
     * @param key
     * @param yamlLines
     * @return all lines in a section.
     */
    public String[] getSectionLines(String key, String[] yamlLines) {

        Vector<String> lines = new Vector<String>();

        boolean inSection = false;

        for (String line : yamlLines) {

            if (isNewSection(line)) {
                inSection = false;
                if (line.startsWith(key + DELIMIT)) {
                    inSection = true;
                }
            } // no else

            if (inSection) {
                lines.addElement(line);
            }
        }

        return (String[]) lines.toArray(new String[lines.size()]);
    }

    private void setTitle(IInternalContest contest, String title) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setContestTitle(title);
    }

    public AutoJudgeSetting[] getAutoJudgeSettings(String[] yamlLines, Problem[] problems)  {

        String[] sectionLines = getSectionLines(AUTO_JUDGE_KEY, yamlLines);
        if (sectionLines == null) {
            System.err.println("section AJ settings sectionLines is null, looking for "+AUTO_JUDGE_KEY);
        }
        Account [] accounts = getAccounts(yamlLines);

        ArrayList<AutoJudgeSetting> ajList = new ArrayList<AutoJudgeSetting>();

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        while (sequenceLines.length > 0) {
            // TODO CCS do a proper parsing of Yaml to handle preceding comment lines 
            /**
             * There is a bug where the section parsing does not ignore blank and
             * comment lines preceding a section.
             */
               
            if (! sequenceLines[0].trim().startsWith("-")) {
                idx += sequenceLines.length;
                sequenceLines = getNextSequence(sectionLines, idx);
                continue;
            }
            String accountType = getSequenceValue(sequenceLines, "- account");
            ClientType.Type type = ClientType.Type.valueOf(accountType.trim());

            String siteString = getSequenceValue(sequenceLines, "site");

            int siteNumber = getIntegerValue(siteString, 1);

            // TODO 669 check for syntax errors
            // syntaxError(AUTO_JUDGE_KEY + " name field missing in languages section");

            String numberString = getSequenceValue(sequenceLines, "number");
            String problemLettersString = getSequenceValue(sequenceLines, "letters");

            String activeStr = getSequenceValue(sequenceLines, "active");
            boolean active = getBooleanValue(activeStr, true);
            
            // TODO 669 code load method
            int[] judgeClientNumbers = null;
            if ("all".equalsIgnoreCase(numberString)) {
                if (accounts.length == 0) {
                    throw new YamlLoadException("'all' not allowed, no judge accounts defined in YAML");     
                }
                judgeClientNumbers = getJudgeAccountNumbers (accounts);
               
            } else {
                judgeClientNumbers = getNumberList(numberString.trim());
            }
            
            for (int i = 0; i < judgeClientNumbers.length; i++) {
                int clientNumber = judgeClientNumbers[i];

                String name = accountType.toUpperCase() + clientNumber;

                AutoJudgeSetting autoJudgeSetting = new AutoJudgeSetting(name);
                ClientId id = new ClientId(siteNumber, type, clientNumber);
                autoJudgeSetting.setClientId(id);
                autoJudgeSetting.setActive(active);

                Filter filter = new Filter();

                if ("all".equalsIgnoreCase(problemLettersString.trim())) {
                    for (Problem problem : problems) {
                        filter.addProblem(problem);
                    }
                } else {
                    for (Problem problem : getProblemsFromLetters(problems, problemLettersString)) {
                        filter.addProblem(problem);
                    }
                }

                autoJudgeSetting.setProblemFilter(filter);
                ajList.add(autoJudgeSetting);
            }

            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }

        return (AutoJudgeSetting[]) ajList.toArray(new AutoJudgeSetting[ajList.size()]);
    }

    /**
     * Return list of judge account numbers for list, in ascending order.
     * 
     * @param accounts
     * @return array of judge numbers, in ascending order.
     */
    private int[] getJudgeAccountNumbers(Account[] accounts) {
        int count = 0;
        for (Account account : accounts) {
            if (account.getClientId().getClientType().equals(ClientType.Type.JUDGE)) {
                count++;
            }
        }

        int[] out = new int[count];
        count = 0;
        for (Account account : accounts) {
            if (account.getClientId().getClientType().equals(ClientType.Type.JUDGE)) {
                out[count] = account.getClientId().getClientNumber();
                count++;
            }
        }

        Arrays.sort(out);
        return out;

    }

    protected Problem[] getProblemsFromLetters(Problem[] problems, String problemLettersString)  {

        String[] list = problemLettersString.split(",");
        Problem[] out = new Problem[list.length];

        for (int i = 0; i < list.length; i++) {

            char letter = list[i].trim().toUpperCase().charAt(0);
            int offset = letter - 'A';

            if (offset < 0 || offset >= problems.length) {
                throw new YamlLoadException("getProblemsFromLetters: There is no problem definition # " + (offset+1)+" only "+offset+" problems defined?");
            }
            out[i] = problems[offset];
        }
        return out;
    }

    protected int[] getNumberList(String numberString) {

        String[] list = numberString.split(",");
        if (list.length == 1) {
            int[] out = new int[1];
            out[0] = getIntegerValue(list[0], 0);
            // if (out[0] < 1) {
            // // TODO 669 throw invalid number in list exception
            // }
            return out;
        } else {
            int[] out = new int[list.length];
            int i = 0;
            for (String n : list) {
                out[i] = getIntegerValue(n, 0);
                // if (out[i] < 1) {
                // // TODO 669 throw invalid number in list exception
                // }
                i++;
            }
            return out;
        }
    }

    private int getIntegerValue(String string, int defaultNumber) {

        int number = defaultNumber;

        if (string != null && string.length() > 0) {
            number = Integer.parseInt(string.trim());
        }

        return number;
    }

    /**
     * Returns boolean for input string.
     * 
     * Matches (case insensitive) yes, no, true, false.
     * 
     * @param string
     * @param defaultBoolean
     * @return default if string does not match (case-insensitive) string
     */
    protected boolean getBooleanValue(String string, boolean defaultBoolean) {

        boolean value = defaultBoolean;

        if (string != null && string.length() > 0) {
            string = string.trim();
            if ("yes".equalsIgnoreCase(string)) {
                value = true;
            } else if ("no".equalsIgnoreCase(string)) {
                value = false;
            } else if ("true".equalsIgnoreCase(string)) {
                value = true;
            } else if ("false".equalsIgnoreCase(string)) {
                value = false;
            }
        }

        return value;
    }

    /**
     * Insures that contest is instantiated.
     * 
     * Creates contest if contest is null, otherwise returns contest.
     * 
     * @param contest
     * @return
     */
    private IInternalContest createContest (IInternalContest contest) {
        if (contest == null) {
            contest = new InternalContest();
            contest.setSiteNumber(1);
        }
        return contest;
    }
    
    /**
     * 
     * @param contents
     * @param defaultTimeOut
     * @param overrideValidatorCommandLine 
     * @param defaultValidatorCommandLine 
     * @param loadDataFileContents 
     * @return
     * 
     */
    public Problem[] getProblems(String[] contents, int defaultTimeOut, boolean loadDataFileContents, String defaultValidatorCommandLine)  {
        return getProblems(contents, defaultTimeOut, loadDataFileContents, defaultValidatorCommandLine, null, false, false);
    }

    /**
     * 
     * @param contest
     * @param yamlLines
     * @param directoryName
     * @return
     * 
     */
    public IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String directoryName)  {
        return fromYaml(contest, yamlLines, directoryName, false);
    }

    public Problem[] getProblems(String[] contents, int defaultTimeOut)  {
        return getProblems(contents, defaultTimeOut, false, null, null, false, false);
    }

    /**
     * Load problem data files.
     * 
     * @param loadProblemDataFiles false means ignore problem data files
     */
    public void setLoadProblemDataFiles(boolean loadProblemDataFiles) {
        this.loadProblemDataFiles = loadProblemDataFiles;
    }
    
    public boolean isLoadProblemDataFiles() {
        return loadProblemDataFiles;
    }

}
