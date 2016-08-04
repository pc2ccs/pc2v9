package edu.csus.ecs.pc2.imports.ccs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import edu.csus.ecs.pc2.ccs.CCSConstants;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.YamlLoadException;
import edu.csus.ecs.pc2.core.imports.LoadICPCTSVData;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AutoJudgeSetting;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
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
 * Load contest from Yaml using SnakeYaml methods.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestSnakeYAMLLoader implements IContestLoader {

    /**
     * Full content of yaml file.
     */
    private Map<String, Object> yamlContent = null;

    /**
     * Load Problem Data File Contents
     */
    private boolean loadProblemDataFiles = true;

    @SuppressWarnings("unchecked")
    public Map<String, Object> loadYaml(String filename) {
        try {
            Yaml yaml = new Yaml();
            return (Map<String, Object>) yaml.load(new FileInputStream(filename));
        } catch (MarkedYAMLException e) {
            throw new YamlLoadException(getSnakeParserDetails(e));
        } catch (FileNotFoundException e) {
            throw new YamlLoadException("File not found " + filename);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> loadYaml(String[] yamlLines) {
        try {
            Yaml yaml = new Yaml();
            String fullString = StringUtilities.join("\n", yamlLines);
            InputStream stream = new ByteArrayInputStream(fullString.getBytes(StandardCharsets.UTF_8));
            return (Map<String, Object>) yaml.load(stream);
        } catch (MarkedYAMLException e) {
            throw new YamlLoadException(getSnakeParserDetails(e));
        }
    }

    /**
     * Create a simple string with parse info.
     * 
     * @param markedYAMLException
     * @return
     */

    String getSnakeParserDetails(MarkedYAMLException markedYAMLException) {

        Mark mark = markedYAMLException.getProblemMark();

        int lineNumber = mark.getLine() + 1; // starts at zero
        int columnNumber = mark.getColumn() + 1; // starts at zero

        return "Parse error at line=" + lineNumber + " column=" + columnNumber + " message=" + markedYAMLException.getProblem();

    }

    @Override
    public IInternalContest fromYaml(IInternalContest contest, String directoryName) {
        return fromYaml(contest, directoryName, true);
    }

    @Override
    public IInternalContest fromYaml(IInternalContest contest, String directoryName, boolean loadDataFileContents) {
        String[] contents;
        try {
            contents = loadFileWithIncludes(directoryName, directoryName + File.separator + DEFAULT_CONTEST_YAML_FILENAME);
        } catch (IOException e) {
            throw new YamlLoadException(e);
        }
        return fromYaml(contest, contents, directoryName, loadDataFileContents);
    }

    /**
     * Load files with #include.
     * 
     * @param dirname
     *            if null will ignore #include files.
     * @param filename
     *            YAML input file
     * @throws IOException
     */
    @Override
    public String[] loadFileWithIncludes(String dirname, String filename) throws IOException {

        if (!new File(filename).isFile()) {
            throw new FileNotFoundException(filename);
        }

        ArrayList<String> outs = new ArrayList<String>();

        String[] lines = Utilities.loadFile(filename);

        for (String line : lines) {

            outs.add(line);
            if (dirname != null && line.trim().startsWith("#include")) {
                String[] parts = line.split("\"");
                String includeFilename = dirname + File.separator + parts[1];
                String[] includeLines = Utilities.loadFile(includeFilename);
                for (String string : includeLines) {
                    outs.add(string);
                }
                outs.add("# end include " + includeFilename);
            }
        }

        return (String[]) outs.toArray(new String[outs.size()]);
    }

    @Override
    public String getContestTitle(String contestYamlFilename) throws IOException {
        return fetchValue(new File(contestYamlFilename), IContestLoader.CONTEST_NAME_KEY);
    }

    protected String fetchValue(File file, String key) {
        Map<String, Object> content = getContent(file.getAbsolutePath());
        return (String) content.get(key);
    }

    private Map<String, Object> getContent(String filename) {
        if (yamlContent == null) {
            yamlContent = loadYaml(filename);
        }

        return yamlContent;
    }

    @Override
    public String getJudgesCDPBasePath(String contestYamlFilename) throws IOException {
        return fetchFileValue(contestYamlFilename, JUDGE_CONFIG_PATH_KEY);
    }

    private String fetchFileValue(String filename, String key) {
        return fetchValue(new File(filename), key);
    }

    /**
     * Insures that contest is instantiated.
     * 
     * Creates contest if contest is null, otherwise returns contest.
     * 
     * @param contest
     * @return
     */
    private IInternalContest createContest(IInternalContest contest) {
        if (contest == null) {
            contest = new InternalContest();
            contest.setSiteNumber(1);
        }
        return contest;
    }

    private void setTitle(IInternalContest contest, String title) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setContestTitle(title);
    }

    /**
     * Parse and convert dateString to Date.
     * 
     * <br>
     * SOMEDAY handle format: yyyy-MM-dd HH:mmZ
     * 
     * @param dateString
     *            date string in form: yyyy-MM-dd HH:mm or yyyy-MM-dd HH:mmZ
     * @return date for input string
     * @throws ParseException
     */
    public static Date parseStartTime(String dateString) throws ParseException {
        // String pattern = "yyyy-MM-dd HH:mmZ";
        String pattern = "yyyy-MM-dd HH:mm";

        String dateTime = dateString;

        if (dateString.length() == pattern.length() + 1) {
            // Strip off Z
            dateTime = dateString.substring(0, pattern.length());
        }
        SimpleDateFormat parser = new SimpleDateFormat(pattern);
        Date date = parser.parse(dateTime);
        return date;
    }

    private void setCDPPath(IInternalContest contest, String path) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setJudgeCDPBasePath(path);
    }

    // private int getIntValue(String string, int defaultNumber) {
    //
    // int number = defaultNumber;
    //
    // if (string != null && string.length() > 0) {
    // number = Integer.parseInt(string.trim());
    // }
    //
    // return number;
    // }

    /**
     * Returns boolean for input string.
     * 
     * Matches (case insensitive) yes, no, true, false.
     * 
     * @param string
     * @param defaultBoolean
     * @return default if string does not match (case-insensitive) string
     */
    @Override
    public boolean getBooleanValue(String string, boolean defaultBoolean) {

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

    private void setContestStartDateTime(IInternalContest contest, Date date) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setStartDate(date);
    }

    @Override
    public IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String directoryName, boolean loadDataFileContents) {

        contest = createContest(contest);

        // name: ACM-ICPC World Finals 2011

        Map<String, Object> content = loadYaml(yamlLines);

        if (content == null) {
            return contest;
        }

        String contestTitle = fetchValue(content, CONTEST_NAME_KEY);
        if (contestTitle != null) {
            setTitle(contest, contestTitle);
        }

        String judgeCDPath = fetchValue(content, JUDGE_CONFIG_PATH_KEY);
        if (judgeCDPath != null) {
            setCDPPath(contest, judgeCDPath);
        }

        Integer defaultTimeout = fetchIntValue(content, TIMEOUT_KEY, DEFAULT_TIME_OUT);

        for (String line : yamlLines) {
            if (line.startsWith(CONTEST_NAME_KEY + DELIMIT)) {
                setTitle(contest, unquoteAll(line.substring(line.indexOf(DELIMIT) + 1).trim()));

            }
        }

        loadDataFileContents = fetchBooleanValue(content, PROBLEM_LOAD_DATA_FILES_KEY, loadDataFileContents);

        String shortContestName = fetchValue(content, SHORT_NAME_KEY);
        if (shortContestName != null) {
            setShortContestName(contest, shortContestName);
        }

        String contestLength = fetchValue(content, CONTEST_DURATION);
        if (contestLength != null) {
            setContestLength(contest, contestLength);
        }

        String scoreboardFreezeTime = fetchValue(content, SCOREBOARD_FREEZE);
        if (scoreboardFreezeTime != null) {
            setScoreboardFreezeTime(contest, scoreboardFreezeTime);
        }

        String startTime = fetchValue(content, CONTEST_START_TIME);
        if (startTime != null) {
            try {
                Date date = parseStartTime(startTime);
                setContestStartDateTime(contest, date);
            } catch (ParseException e) {
                throw new YamlLoadException("Invalid start-time value '" + startTime + " expected form yyyy-MM-dd HH:mm, " + e.getMessage(), e);
            }
        }

        Language[] languages = getLanguages(yamlLines);
        for (Language language : languages) {
            contest.addLanguage(language);
        }

        String defaultValidatorCommandLine = fetchValue(content, DEFAULT_VALIDATOR_KEY);

        String overrideValidatorCommandLine = fetchValue(content, OVERRIDE_VALIDATOR_KEY);

        if (overrideValidatorCommandLine == null) {

            // if no override defined, then maybe use mtsv

            File mtsvFile = new File(directoryName + File.separator + MTSV_PROGRAM_NAME);
            if (mtsvFile.exists()) {
                /**
                 * If mtsv is in the same directory as the contest.yaml, then use the mtsv command line.
                 */
                overrideValidatorCommandLine = mtsvFile.getAbsolutePath() + " " + MTSV_OVERRIDE_VALIDATOR_ARGS;
            }
        }

        boolean overrideUsePc2Validator = false;
        String usingValidator = fetchValue(content, ContestYAMLLoader.USING_PC2_VALIDATOR);

        if (usingValidator != null && usingValidator.equalsIgnoreCase("true")) {
            overrideUsePc2Validator = true;
        }

        /**
         * Manual Review global override.
         */
        boolean manualReviewOverride = fetchBooleanValue(content, MANUAL_REVIEW_KEY, false);

        Problem[] problems = getProblems(yamlLines, defaultTimeout, loadDataFileContents, defaultValidatorCommandLine, overrideValidatorCommandLine, overrideUsePc2Validator, manualReviewOverride);

        if (loadProblemDataFiles) {
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
        if (problems.length == 0) {
            /**
             * If no problems in input YAML assume problems are already defined in contest/model.
             */
            autoJudgeSettings = getAutoJudgeSettings(yamlLines, contest.getProblems());

        } else {
            autoJudgeSettings = getAutoJudgeSettings(yamlLines, problems);

        }

        for (AutoJudgeSetting auto : autoJudgeSettings) {
            addAutoJudgeSetting(contest, auto);
        }

        PlaybackInfo playbackInfo = getReplaySettings(yamlLines);

        if (playbackInfo != null) {
            contest.addPlaybackInfo(playbackInfo);
        }

        return contest;

    }

    private void setScoreboardFreezeTime(IInternalContest contest, String scoreboardFreezeTime) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setFreezeTime(scoreboardFreezeTime);
    }

    private void setContestLength(IInternalContest contest, String contestLength) {
        ContestTime time = contest.getContestTime();
        if (time == null) {
            time = new ContestTime();
            time.setSiteNumber(contest.getSiteNumber());
        }
        time.setContestLengthSecs(parseTimeIntoSeconds(contestLength, Constants.DEFAULT_CONTEST_LENGTH_SECONDS));
        contest.updateContestTime(time);
        ;
    }

    /**
     * Parses input string and returns number of seconds.
     * 
     * form can be integer or HH:MM:SS
     * 
     * @param defaultLongValue
     * 
     * @param timeString
     * @param defaultLongValue
     *            - default value if parsing error or no valid time format found
     * @return
     */
    public long parseTimeIntoSeconds(String timeString, long defaultLongValue) {

        if (timeString.indexOf(':') > 0) {
            // likely form: HH:MM:SS

            // TODO TODAY CLEANUP - make Utilties.stringToLongSecs static
            // long secs = Utilities.stringToLongSecs(timeString);
            long secs = stringToLongSecs(timeString);
            return secs;
        } else {
            // is a integer or long

            try {
                long l = Long.parseLong(timeString);
                return l;
            } catch (Exception e) {
                return defaultLongValue;
            }
        }

    }

    private void setShortContestName(IInternalContest contest, String shortContestName) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setContestShortName(shortContestName);

    }

    /**
     * Get boolean value for input key in map.
     * 
     * Returns defaultVaue if no entry matches key.
     * 
     * @param content
     * @param key
     * @param defaultValue
     * @return defaultValue or value from item in map.
     */
    private boolean fetchBooleanValue(Map<String, Object> content, String key, boolean defaultValue) {
        Object object = content.get(key);
        Boolean value = false;
        if (object == null) {
            return defaultValue;
        } else if (object instanceof Boolean) {
            value = (Boolean) content.get(key);
        } else if (object instanceof String) {
            value = getBooleanValue((String) content.get(key), defaultValue);
        }
        return value;
    }

    private void addAutoJudgeSetting(IInternalContest contest, AutoJudgeSetting auto) {

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

    @SuppressWarnings("unchecked")
    private Account[] getAccounts(String[] yamlLines) {

        Vector<Account> accountVector = new Vector<Account>();
        AccountList accountList = new AccountList();

        Map<String, Object> yamlContent = loadYaml(yamlLines);
        ArrayList<Map<String, Object>> list = fetchList(yamlContent, ACCOUNTS_KEY);

        if (list != null) {
            for (Object object : list) {

                Map<String, Object> map = (Map<String, Object>) object;

                String accountType = fetchValue(map, "account");
                checkField(accountType, "Account Type");

                ClientType.Type type = ClientType.Type.valueOf(accountType.trim());
                Integer startNumber = fetchIntValue(map, "start", 1);
                Integer count = fetchIntValue(map, "count", 1);
                Integer siteNumber = fetchIntValue(map, "site", 1);

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

            }

        }

        return (Account[]) accountVector.toArray(new Account[accountVector.size()]);

    }

    private String fetchValue(Map<String, Object> content, String key) {
        if (content == null) {
            return null;
        }
        Object value = content.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) content.get(key);
        } else {
            return content.get(key).toString();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PlaybackInfo getReplaySettings(String[] yamlLines) {

        PlaybackInfo info = new PlaybackInfo();

        Map<String, Object> yamlContent = loadYaml(yamlLines);
        ArrayList<Map<String, Object>> list = fetchList(yamlContent, REPLAY_KEY);

        if (list != null) {
            Map<String, Object> map = (Map<String, Object>) list.get(0);

            String siteTitle = fetchValue(map, "title");

            String filename = fetchValue(map, "file");

            boolean started = fetchBooleanValue(map, "auto_start", false);

            Integer waitTimeBetweenEventsMS = fetchIntValue(map, "pacingMS", 1000);

            Integer minEvents = fetchIntValue(map, "minevents", 1);

            Integer siteNumber = fetchIntValue(map, "site");

            // Site site = new Site(siteTitle, siteNumber);

            info.setDisplayName(siteTitle);
            info.setFilename(filename);
            info.setStarted(started);
            info.setWaitBetweenEventsMS(waitTimeBetweenEventsMS);
            info.setMinimumPlaybackRecords(minEvents);
            info.setSiteNumber(siteNumber);
        }

        return info;

    }

    @SuppressWarnings("unused")
    private boolean fetchBooleanValue(Map<String, Object> content, String key) {
        return fetchBooleanValue(content, key, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Site[] getSites(String[] yamlLines) {

        Map<String, Object> yamlContent = loadYaml(yamlLines);
        ArrayList<Map<String, Object>> list = fetchList(yamlContent, SITES_KEY);
        ArrayList<Site> sitesVector = new ArrayList<Site>();

        if (list != null) {
            for (Object object : list) {

                Map<String, Object> map = (Map<String, Object>) object;
                /*
                 * <pre> sites: - number: 1 name: Site 1 IP: localhost port: 50002 </pre>
                 */

                String siteTitle = fetchValue(map, "name");

                Integer siteNumber = fetchIntValue(map, "number");

                Site site = new Site(siteTitle, siteNumber);

                String hostName = fetchValueDefault(map, "IP", "");
                Integer portString = fetchIntValue(map, "port");

                String password = fetchValue(map, "password");
                if (password == null) {
                    password = "site" + siteNumber.toString();
                }

                site.setPassword(password.trim());

                Properties props = new Properties();
                props.put(Site.IP_KEY, hostName);
                props.put(Site.PORT_KEY, portString.toString());
                site.setConnectionInfo(props);

                sitesVector.add(site);

            }

        }

        return (Site[]) sitesVector.toArray(new Site[sitesVector.size()]);

    }

    private String fetchValueDefault(Map<String, Object> map, String key, String defaultValue) {
        String value = fetchValue(map, key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    private Integer fetchIntValue(Map<String, Object> map, String key, int defaultValue) {
        Integer value = null;
        if (map != null) {
            value = (Integer) map.get(key);
        }
        if (value != null) {
            try {
                return value;
            } catch (Exception e) {
                syntaxError("Expecting number after " + key + ": field, found '" + value + "'");
            }
        }
        return defaultValue;
    }

    private Integer fetchIntValue(Map<String, Object> map, String key) {
        Integer value = (Integer) map.get(key);
        if (value != null) {
            try {
                return value;
            } catch (Exception e) {
                syntaxError("Expecting number after " + key + ": field, found '" + value + "'");
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchMap(Map<String, Object> content, String key) {
        return (Map<String, Object>) content.get(key);
    }

    @Override
    public void loadProblemInformationAndDataFiles(IInternalContest contest, String baseDirectoryName, Problem problem, boolean overrideUsePc2Validator) {
        loadProblemInformationAndDataFiles(contest, baseDirectoryName, problem, overrideUsePc2Validator, false);
    }

    @Override
    public void loadProblemInformationAndDataFiles(IInternalContest contest, String baseDirectoryName, Problem problem, boolean overrideUsePc2Validator, boolean overrideManualReview) {

        // TODO CCS code this: do not add problem to contest model, new new parameter flag

        String problemDirectory = baseDirectoryName + File.separator + problem.getShortName();

        problem.setExternalDataFileLocation(problemDirectory);

        String problemYamlFilename = problemDirectory + File.separator + DEFAULT_PROBLEM_YAML_FILENAME;

        Map<String, Object> content = loadYaml(problemYamlFilename);
        
        String problemLaTexFilename = problemDirectory + File.separator + "problem_statement" + File.separator + DEFAULT_PROBLEM_LATEX_FILENAME;

        String problemTitle = fetchValue(content, PROBLEM_NAME_KEY);

        if (new File(problemLaTexFilename).isFile()) {
            problemTitle = getProblemNameFromLaTex(problemLaTexFilename);
        }

        Map<String, Object> validatorContent = fetchMap(content, VALIDATOR_KEY);

        boolean pc2FormatProblemYamlFile = false;
        String usingValidator = fetchValue(validatorContent, ContestYAMLLoader.USING_PC2_VALIDATOR);

        if (usingValidator != null && usingValidator.equalsIgnoreCase("true")) {
            pc2FormatProblemYamlFile = true;
        }

        if (overrideUsePc2Validator) {
            pc2FormatProblemYamlFile = true;
        }

        if (problemTitle == null && (pc2FormatProblemYamlFile)) {
            problemTitle = fetchValue(content, "name");
        }

        if (problemTitle == null) {
            syntaxError("No problem name found for " + problem.getShortName() + " in " + problemLaTexFilename);
        }

        problem.setDisplayName(problemTitle);

        String dataFileBaseDirectory = problemDirectory + File.separator + "data" + File.separator + "secret";

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        if (pc2FormatProblemYamlFile) {
            String answerFileName = fetchValue(content, "datafile");
            String dataFileName = fetchValue(content, "answerfile");

            loadPc2ProblemFiles(contest, dataFileBaseDirectory, problem, problemDataFiles, dataFileName, answerFileName);
        } else {
            loadCCSProblemFiles(contest, dataFileBaseDirectory, problem, problemDataFiles);
        }

        Map<String, Object> limitsContent = fetchMap(content, LIMITS_KEY);
        Integer timeOut = fetchIntValue(limitsContent, TIMEOUT_KEY);
        if (timeOut != null) {
            problem.setTimeOutInSeconds(timeOut);
        }

        if (!pc2FormatProblemYamlFile) {
            // TODO CCS add CCS validator derived based on build script

            /**
             * - Use CCS build command to build validator ('build' script name) - add validator created by build command - add CCS run script ('run' script name)
             */

            // default-validator: /home/pc2/Desktop/codequest12_problems/default_validator
            // override-validator: /usr/local/bin/mtsv {:problemletter} {:resfile} {:basename}

            // `$validate_cmd $inputfile $answerfile $feedbackfile < $teamoutput `;

            addCCSValidator(problem, problemDataFiles, baseDirectoryName);

        } else {
            addDefaultPC2Validator(problem, 1);
        }
        
        assignJudgingType(content, problem, overrideManualReview);


        boolean manualReview = fetchBooleanValue(content, MANUAL_REVIEW_KEY, false);
        if (overrideManualReview) {
            manualReview = true;
        }

        if (manualReview) {
            problem.setManualReview(true);
        }

        // TODO CCS - send preliminary - add bug - fix.
        // boolean sendPreliminary = fetchBooleanValue(content, SEND_PRELIMINARY_JUDGEMENT_KEY, false);
        // if (sendPreliminary){
        // problem.setPrelimaryNotification(true);
        // }

    }

    @Override
    public void dumpSerialzedFileList(Problem problem, String logPrefixId, SerializedFile[] sfList) {
        // TODO Auto-generated method stub

    }

    @Override
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

    @Override
    public String[] loadGeneralClarificationAnswers(String[] yamlLines) {
        return fetchStringList(yamlLines, CLAR_CATEGORIES_KEY);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String[] fetchStringList(String[] yamlLines, String key) {
        Map<String, Object> content = loadYaml(yamlLines);
        ArrayList list = fetchList(content, key);
        if (list == null) {
            return new String[0];
        } else {
            return (String[]) list.toArray(new String[list.size()]);
        }
    }

    @Override
    public String[] getGeneralAnswers(String[] yamlLines) {
        return fetchStringList(yamlLines, DEFAULT_CLARS_KEY);
    }

    @Override
    public String[] getClarificationCategories(String[] yamlLines) {
        return fetchStringList(yamlLines, CLAR_CATEGORIES_KEY);
    }

    @SuppressWarnings("rawtypes")
    private ArrayList fetchList(Map<String, Object> content, String key) {
        return (ArrayList) content.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Language[] getLanguages(String[] yamlLines) {
        ArrayList<Language> languageList = new ArrayList<Language>();

        Map<String, Object> yamlContent = loadYaml(yamlLines);
        ArrayList<Map<String, Object>> list = fetchList(yamlContent, LANGUAGE_KEY);

        if (list != null) {
            for (Object object : list) {

                Map<String, Object> map = (Map<String, Object>) object;

                String name = fetchValue(map, "name");

                if (name == null) {
                    syntaxError("Language name field missing in languages section");
                } else {
                    Language language = new Language(name);

                    Language lookedupLanguage = LanguageAutoFill.languageLookup(name);
                    String compilerName = fetchValue(map, "compilerCmd");

                    if (compilerName == null && lookedupLanguage != null) {
                        language = lookedupLanguage;
                        language.setDisplayName(name);
                    } else if (compilerName == null) {
                        throw new YamlLoadException("Language \"" + name + "\" missing compiler command line");
                    } else {

                        String compilerArgs = fetchValue(map, "compiler-args");
                        String interpreter = fetchValue(map, "runner");
                        String interpreterArgs = fetchValue(map, "runner-args");
                        String exeMask = fetchValue(map, "exemask");
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

                    boolean active = fetchBooleanValue(map, "active", true);
                    language.setActive(active);

                    boolean useJudgeCommand = fetchBooleanValue(map, USE_JUDGE_COMMAND_KEY, true);
                    language.setUsingJudgeProgramExecuteCommandLine(useJudgeCommand);

                    String judgeExecuteCommandLine = fetchValue(map, JUDGE_EXECUTE_COMMAND_KEY);
                    if (judgeExecuteCommandLine != null) {
                        language.setJudgeProgramExecuteCommandLine(judgeExecuteCommandLine);
                    }

                    // TODO handle interpreted languages, seems it should be in the export

                    // boolean

                    if (valid(language, name)) {
                        languageList.add(language);
                    }

                }
            }

        }

        return (Language[]) languageList.toArray(new Language[languageList.size()]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Problem[] getProblems(String[] yamlLines, int seconds, boolean loadDataFileContents, String defaultValidatorCommand, String overrideValidatorCommandLine, boolean overrideUsePc2Validator,
            boolean manualReviewOverride) {

        Vector<Problem> problemList = new Vector<Problem>();

        Map<String, Object> yamlContent = loadYaml(yamlLines);
        ArrayList<Map<String, Object>> list = fetchList(yamlContent, PROBLEMS_KEY);

        if (list != null) {
            for (Object object : list) {

                Map<String, Object> map = (Map<String, Object>) object;

                String problemKeyName = fetchValue(map, SHORT_NAME_KEY);

                if (problemKeyName == null) {
                    syntaxError("Missing " + SHORT_NAME_KEY + " in probset section");
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

                String problemTitle = fetchValue(map, PROBLEM_NAME_KEY);
                if (problemTitle == null) {
                    problemTitle = problemKeyName;
                }

                Problem problem = new Problem(problemTitle);

                int actSeconds = fetchIntValue(map, TIMEOUT_KEY, seconds);
                problem.setTimeOutInSeconds(actSeconds);

                problem.setShowCompareWindow(false);

                problem.setShortName(problemKeyName);
                if (!problem.isValidShortName()) {
                    throw new YamlLoadException("Invalid short problem name '" + problemKeyName + "'");
                }

                String problemLetter = fetchValue(map, "letter");
                String colorName = fetchValue(map, "color");
                String colorRGB = fetchValue(map, "rgb");

                // TODO CCS assign Problem variables for color and letter
                problem.setLetter(problemLetter);
                problem.setColorName(colorName);
                problem.setColorRGB(colorRGB);

                // assign global judging type values.
                assignDefaultJudgingTypes(yamlLines, problem, manualReviewOverride);
                
                // assign individual judging type values.
                assignJudgingType(map, problem, manualReviewOverride);

                boolean loadFilesFlag = fetchBooleanValue(map, PROBLEM_LOAD_DATA_FILES_KEY, loadDataFileContents);
                problem.setUsingExternalDataFiles(!loadFilesFlag);

                String validatorCommandLine = fetchValue(map, VALIDATOR_KEY);

                if (validatorCommandLine == null) {
                    validatorCommandLine = defaultValidatorCommand;
                }
                if (overrideValidatorCommandLine != null) {
                    validatorCommandLine = overrideValidatorCommandLine;
                }
                problem.setValidatorCommandLine(validatorCommandLine);

                problemList.addElement(problem);
            }
        }

        return (Problem[]) problemList.toArray(new Problem[problemList.size()]);
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

    protected int getIntegerValue(String string, int defaultNumber) {

        int number = defaultNumber;

        if (string != null && string.length() > 0) {
            number = Integer.parseInt(string.trim());
        }

        return number;
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

    @SuppressWarnings("unchecked")
    @Override
    public AutoJudgeSetting[] getAutoJudgeSettings(String[] yamlLines, Problem[] problems) {

        ArrayList<AutoJudgeSetting> ajList = new ArrayList<AutoJudgeSetting>();

        Map<String, Object> yamlContent = loadYaml(yamlLines);
        ArrayList<Map<String, Object>> list = fetchList(yamlContent, AUTO_JUDGE_KEY);

        if (list != null) {

            // TODO get accounts from contest too
            Account[] accounts = getAccounts(yamlLines);

            for (Object object : list) {

                Map<String, Object> map = (Map<String, Object>) object;

                String accountType = fetchValue(map, "account");
                ClientType.Type type = ClientType.Type.valueOf(accountType.trim());

                int siteNumber = fetchIntValue(map, "site", 1);

                // TODO 669 check for syntax errors
                // syntaxError(AUTO_JUDGE_KEY + " name field missing in languages section");

                String numberString = fetchValue(map, "number");
                String problemLettersString = fetchValue(map, "letters");

                boolean active = fetchBooleanValue(map, "active", true);

                int[] judgeClientNumbers = null;
                if ("all".equalsIgnoreCase(numberString)) {
                    if (accounts.length == 0) {

                        throw new YamlLoadException("'all' not allowed, no judge accounts defined in YAML");
                    }
                    judgeClientNumbers = getJudgeAccountNumbers(accounts);

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

            }
        }

        return (AutoJudgeSetting[]) ajList.toArray(new AutoJudgeSetting[ajList.size()]);

    }

    @Override
    public Problem[] getProblems(String[] contents, int defaultTimeOut, boolean loadDataFileContents, String defaultValidatorCommandLine) {
        return getProblems(contents, defaultTimeOut, loadDataFileContents, defaultValidatorCommandLine, null, false, false);
    }

    @Override
    public IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String directoryName) {
        return fromYaml(contest, yamlLines, directoryName, false);
    }

    @Override
    public Problem[] getProblems(String[] contents, int defaultTimeOut) {
        return getProblems(contents, defaultTimeOut, true, null, null, false, false);
    }

    @Override
    public String[] getFileNames(String directoryName, String extension) {

        ArrayList<String> list = new ArrayList<String>();
        File dir = new File(directoryName);

        String[] entries = dir.list();
        if (entries == null) {
            return new String[0];
        }
        Arrays.sort(entries);

        for (String name : entries) {
            if (name.endsWith(extension)) {
                list.add(name);
            }
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    @Override
    public Problem[] getProblemsFromLetters(Problem[] problems, String problemLettersString) {

        String[] list = problemLettersString.split(",");
        Problem[] out = new Problem[list.length];

        for (int i = 0; i < list.length; i++) {

            char letter = list[i].trim().toUpperCase().charAt(0);
            int offset = letter - 'A';

            if (offset < 0 || offset >= problems.length) {
                throw new YamlLoadException("getProblemsFromLetters: There is no problem definition # " + (offset + 1) + " only " + offset + " problems defined?");
            }
            out[i] = problems[offset];
        }
        return out;
    }

    @Override
    public String getProblemNameFromLaTex(String filename) {

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
     * Remove trailing and leading quote char
     * 
     * @param string
     * @param quoteChar
     * @return
     */
    @Override
    public String unquote(String string, String quoteChar) {
        if (string.startsWith(quoteChar)) {
            String newString = string.substring(1);
            if (newString.endsWith(quoteChar)) {
                newString = newString.substring(0, newString.length() - 1);
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
    protected String unquoteAll(String string) {
        if (string.startsWith("'")) {
            return unquote(string, "'");

        } else if (string.startsWith("\"")) {
            return unquote(string, "\"");

        } else {
            return string;
        }
    }

    private void syntaxError(String string) {
        YamlLoadException exception = new YamlLoadException("Syntax error: " + string);
        exception.printStackTrace();
        throw exception;
    }

    private Problem addCCSValidator(Problem problem, ProblemDataFiles problemDataFiles, String baseDirectoryName) {

        problem.setCcsMode(true);

        problem.setValidatedProblem(true);
        problem.setUsingPC2Validator(false);
        problem.setReadInputDataFromSTDIN(true);

        if (problem.getValidatorProgramName() == null) {
            problem.setValidatorProgramName(CCSConstants.DEFAULT_CCS_VALIATOR_NAME);
        }

        // if we use the internal Java CCS validator use this.
        // problem.setValidatorCommandLine("java -cp {:pc2jarpath} " + CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND);
        if (problem.getValidatorCommandLine() == null) {
            problem.setValidatorCommandLine(CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND);
        }

        String validatorName = baseDirectoryName + File.separator + problem.getValidatorProgramName();

        try {
            /**
             * If file is there load it
             */
            if (new File(validatorName).isFile()) {
                problemDataFiles.setValidatorFile(new SerializedFile(validatorName));
            }
        } catch (Exception e) {
            throw new YamlLoadException("Unable to load validator for problem " + problem.getShortName() + ": " + validatorName, e);
        }

        // problem.setValidatorCommandLine("java -cp {:pc2jarpath} " + CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND);

        return problem;
    }

    @Override
    public void loadPc2ProblemFiles(IInternalContest contest, String dataFileBaseDirectory, Problem problem, ProblemDataFiles problemDataFiles2, String dataFileName, String answerFileName) {

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        if (dataFileName == null) {
            syntaxError("Missing datafile for pc2 problem " + problem.getShortName());
        }

        if (dataFileName == null) {
            syntaxError("Missing datafile for pc2 problem " + problem.getShortName());
        }

        addDataFiles(problem, problemDataFiles, dataFileBaseDirectory, dataFileName, answerFileName);
        contest.addProblem(problem, problemDataFiles);
    }

    @Override
    public String getCCSDataFileDirectory(String yamlDirectory, Problem problem) {
        return getCCSDataFileDirectory(yamlDirectory, problem.getShortName());
    }

    @Override
    public String getCCSDataFileDirectory(String yamlDirectory, String shortDirName) {
        return yamlDirectory + File.separator + shortDirName + File.separator + "data" + File.separator + "secret";
    }

    @Override
    public Problem loadCCSProblemFiles(IInternalContest contest, String dataFileBaseDirectory, Problem problem, ProblemDataFiles problemDataFiles) {

        if (problem.getShortName() == null) {
            throw new YamlLoadException("  For " + problem + " missing problem short name");
        }

        /**
         * Data files are external so data files should not be loaded into problem data files.
         */
        boolean loadExternalFile = problem.isUsingExternalDataFiles();

        String[] inputFileNames = getFileNames(dataFileBaseDirectory, ".in");

        String[] answerFileNames = getFileNames(dataFileBaseDirectory, ".ans");

        if (inputFileNames.length == 0) {
            throw new YamlLoadException("No input (.in) file names found for " + problem.getDisplayName() + " in dir " + dataFileBaseDirectory);
        }

        if (answerFileNames.length == 0) {
            throw new YamlLoadException("No answer (.ans) file names found for " + problem.getDisplayName() + " in dir " + dataFileBaseDirectory);
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

            }

            if (dataFiles.size() > 0) {

                SerializedFile[] data = (SerializedFile[]) dataFiles.toArray(new SerializedFile[dataFiles.size()]);
                SerializedFile[] answer = (SerializedFile[]) answerFiles.toArray(new SerializedFile[answerFiles.size()]);

                // dumpSerialzedFileList (problem, "Judges data", data);
                // dumpSerialzedFileList (problem, "Judges answer", answer);

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

    private void validateCCSData(IInternalContest contest, Problem problem) {

        // TODO CCS Load Validator

        // TODO CCS somehow find validator, compile, and test.
        // the somehow is because there may be more than one validator in the validator directory

        // TODO 1. Check files (all files present as required + check problem.yaml)
        // TODO 2. Check compile (check that all programs compile)
        // TODO 3. Check input (run input validators)
        // TODO 4. Check solutions (run all solutions check that they get the expected verdicts)

    }

    private void addDataFiles(Problem problem, ProblemDataFiles problemDataFiles, String dataFileBaseDirectory, String dataFileName, String answerFileName) {

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

        // load judge answer file
        if (answerFileName != null) {
            String answerFilePath = dataFileBaseDirectory + File.separator + answerFileName;
            if (fileNotThere(answerFilePath)) {
                throw new YamlLoadException("Missing data file " + answerFilePath);
            }

            problem.setAnswerFileName(answerFileName);

            SerializedFile serializedFile = new SerializedFile(answerFilePath, problem.isUsingExternalDataFiles());
            problemDataFiles.setJudgesAnswerFile(serializedFile);
        }

    }

    private boolean fileNotThere(String name) {
        return !new File(name).isFile();
    }

    private void syntaxWarning(String message) {
        System.err.println("Warning - " + message);
    }

    /**
     * Check for existence of file, if does not exist throw exception with message.
     * 
     * @param filename
     * @param message
     * 
     */
    private void checkForFile(String filename, String message) {

        if (!(new File(filename).isFile())) {
            throw new YamlLoadException(message);
        }
    }

    /**
     * Load problem data files.
     * 
     * @param loadProblemDataFiles
     *            false means ignore problem data files
     */
    public void setLoadProblemDataFiles(boolean loadProblemDataFiles) {
        this.loadProblemDataFiles = loadProblemDataFiles;
    }

    public boolean isLoadProblemDataFiles() {
        return loadProblemDataFiles;
    }

    private boolean valid(Language language, String prefix) {
        checkField(language.getDisplayName(), prefix + " Compiler Display name");
        checkField(language.getCompileCommandLine(), prefix + " Compile Command line");
        return true;
    }

    private void checkField(String value, String fieldName) {
        if (value == null) {
            syntaxError("Missing " + fieldName);
        } else if (value.trim().length() == 0) {
            syntaxError("Missing " + fieldName);
        }
    }
    
    @Override
    public void assignDefaultJudgingTypes(String[] yaml, Problem problem, boolean overrideManualReviewFlag) {

        Map<String, Object> map = loadYaml(yaml);
        assignJudgingType(map, problem, overrideManualReviewFlag);
    }
    
    /**
     * Assign individual problem judging type based on map values.
     * 
     * @param map
     * @param problem
     * @param overrideManualReviewFlag
     */
    protected void assignJudgingType(Map<String, Object> map, Problem problem, boolean overrideManualReviewFlag) {

//        if (map == null || map.entrySet().isEmpty()){
//            System.out.println("debug problem "+problem.getShortName()+" has NO map");
//        } else {
//            System.out.println("debug problem "+problem.getShortName()+" "+map);
//        }

        boolean sendPreliminary = fetchBooleanValue(map, SEND_PRELIMINARY_JUDGEMENT_KEY, false);
        
        if (sendPreliminary) {
            problem.setPrelimaryNotification(true);
        }

        boolean computerJudged = fetchBooleanValue(map, COMPUTER_JUDGING_KEY, true);
        problem.setComputerJudged(computerJudged);

        boolean manualReview = fetchBooleanValue(map, MANUAL_REVIEW_KEY, false);

        if (overrideManualReviewFlag) {
            manualReview = true;
        }

        if (manualReview) {
            problem.setManualReview(true);
        }

//        printKeyFound("debug ", map, COMPUTER_JUDGING_KEY);
//        printKeyFound("debug ", map, MANUAL_REVIEW_KEY);
//        printKeyFound("debug ", map, SEND_PRELIMINARY_JUDGEMENT_KEY);
//        System.out.println("debug  " + toStringTwo(problem));
//        System.out.println();

    }

    protected void printKeyFound(String message, Map<String, Object> map, String key) {
        Object object = map.get(key);
        String value = "MISSING";
        if (object != null) {
            value = object.toString();
        }
        System.out.println(message + " " + key + " = " + value);
    }

    protected String toStringTwo(Problem problem) {
        
        return "Problem "+problem.getShortName()+"  cj/man/prelim = "+problem.isComputerJudged()+ //
                " / " +problem.isManualReview() + //
                " / " +problem.isPrelimaryNotification();
    }

    /**
     * Convert String to second. Expects input in form: ss or mm:ss or hh:mm:ss
     * 
     * @param s
     *            string to be converted to seconds
     * @return -1 if invalid time string, 0 or >0 if valid
     */
    public long stringToLongSecs(String s) {

        // TODO TODAY CLEANUP - make Utilties.stringToLongSecs static - then remove stringToLongSecs method

        if (s == null || s.trim().length() == 0) {
            return -1;
        }

        String[] fields = s.split(":");
        long hh = 0;
        long mm = 0;
        long ss = 0;

        switch (fields.length) {
            case 3:
                hh = Utilities.stringToLong(fields[0]);
                mm = Utilities.stringToLong(fields[1]);
                ss = Utilities.stringToLong(fields[2]);
                break;
            case 2:
                mm = Utilities.stringToLong(fields[0]);
                ss = Utilities.stringToLong(fields[1]);
                break;
            case 1:
                ss = Utilities.stringToLong(fields[0]);
                break;

            default:
                break;
        }

        // System.out.println(" values "+hh+":"+mm+":"+ss);

        long totsecs = 0;
        if (hh != -1) {
            totsecs = hh;
        }
        if (mm != -1) {
            totsecs = (totsecs * 60) + mm;
        }
        if (ss != -1) {
            totsecs = (totsecs * 60) + ss;
        }

        // System.out.println(" values "+hh+":"+mm+":"+ss+" secs="+totsecs);

        if (hh == -1 || mm == -1 || ss == -1) {
            return -1;
        }

        return totsecs;
    }

    /**
     * Find sample conetst by name.
     * 
     * @param name
     * @return
     */
    protected String findSampleContestYaml(String name) {

        // System.out.println("findSampleContestYaml ( "+name+")");

        String sampleDir = "samps" + File.separator + "contests";

        String conestYamleFilename = sampleDir + File.separator + name + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;

        if (new File(conestYamleFilename).isFile()) {
            return conestYamleFilename;
        } else {
            return null;
        }
    }



    /**
     * Load groups.tsv and teams.tsv.
     * 
     * @param contest
     * @param cdpConfigDirectory
     * @throws Exception
     */
    private boolean loadCCSTSVFiles(IInternalContest contest, File cdpConfigDirectory) throws Exception {

        boolean loaded = false;

        String teamsTSVFile = cdpConfigDirectory.getAbsolutePath() + File.separator + LoadICPCTSVData.TEAMS_FILENAME;

        String groupsTSVFile = cdpConfigDirectory.getAbsolutePath() + File.separator + LoadICPCTSVData.GROUPS_FILENAME;

        // only load if both tsv files are present.

        if (new File(teamsTSVFile).isFile() && new File(groupsTSVFile).isFile()) {

            LoadICPCTSVData loadTSVData = new LoadICPCTSVData();
            loadTSVData.setContestAndController(contest, null);
            loaded = loadTSVData.loadFiles(teamsTSVFile, false, false);
        }

        return loaded;
    }

    public static Site createFirstSite(int siteNumber, String hostName, int portNumber) {
        Site site = new Site("Site " + siteNumber, siteNumber);
        Properties props = new Properties();
        props.put(Site.IP_KEY, hostName);
        props.put(Site.PORT_KEY, "" + portNumber);
        site.setConnectionInfo(props);
        site.setPassword("site" + siteNumber);
        return site;
    }

    
    @Override
    public File findCDPConfigDirectory(File entry) {
        File cdpConfigDirectory = null;

        if (entry.isDirectory()) {

            // found a directory
            cdpConfigDirectory = new File(entry.getAbsoluteFile() + File.separator + CONFIG_DIRNAME);

        } else if (entry.isFile()) {

            // a file

            if (IContestLoader.DEFAULT_CONTEST_YAML_FILENAME.equals(entry.getName())) {
                // found contest.yaml

                cdpConfigDirectory = entry.getParentFile();
            }

        } else {

            // A CDP in the samples directory

            String sampleContestYamlFile = findSampleContestYaml(entry.getName());

            if (sampleContestYamlFile != null) {
                File yamlFile = new File(sampleContestYamlFile);
                String configDirPath = yamlFile.getParentFile().getAbsoluteFile().toString();
                cdpConfigDirectory = new File(configDirPath);
            }

        }
        return cdpConfigDirectory;
    }



    @Override
    public IInternalContest initializeContest(IInternalContest contest, File entry) throws Exception {
        
        if (contest == null){
            throw new IllegalArgumentException("contest is null");
        }
        
        File cdpConfigDirectory = findCDPConfigDirectory(entry);
        
        if (cdpConfigDirectory == null){
            throw new Exception("Cannot find CDP for "+entry);
        } else {
            contest = fromYaml(contest, cdpConfigDirectory.getAbsolutePath());
            
            if (contest.getSites().length == 0){
                // Create default site.
                Site site = createFirstSite(contest.getSiteNumber(), "localhost", Constants.DEFAULT_PC2_PORT);
                contest.addSite(site);
            }
            
            loadCCSTSVFiles (contest, cdpConfigDirectory);
        }
        
        return contest;
    }
    
}
