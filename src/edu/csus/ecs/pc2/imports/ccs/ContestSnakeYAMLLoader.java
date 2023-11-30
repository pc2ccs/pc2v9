// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.JudgementLoader;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.YamlLoadException;
import edu.csus.ecs.pc2.core.export.MailMergeFile;
import edu.csus.ecs.pc2.core.imports.LoadAccounts;
import edu.csus.ecs.pc2.core.imports.LoadICPCTSVData;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AutoJudgeSetting;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.INPUT_VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;
import edu.csus.ecs.pc2.core.model.Problem.SandboxType;
import edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.security.Permission.Type;
import edu.csus.ecs.pc2.tools.PasswordGenerator;
import edu.csus.ecs.pc2.tools.PasswordType2;
import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidatorSettings;
import edu.csus.ecs.pc2.validator.customValidator.CustomValidatorSettings;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorSettings;

/**
 * Load contest/model from Yaml and files in a CDP.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestSnakeYAMLLoader implements IContestLoader {

    /**
     * Full content of yaml file.
     */
    private Map<String, Object> fullYamlContent = null;

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
            throw new YamlLoadException(getSnakeParserDetails(e), e, filename);
        } catch (FileNotFoundException e) {
            throw new YamlLoadException("File not found " + filename, e, filename);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> loadYaml(String filename, String[] yamlLines) {
        try {
            Yaml yaml = new Yaml();
            String fullString = StringUtilities.join("\n", yamlLines);
            InputStream stream = new ByteArrayInputStream(fullString.getBytes(StandardCharsets.UTF_8));
            return (Map<String, Object>) yaml.load(stream);
        } catch (MarkedYAMLException e) {
            throw new YamlLoadException(getSnakeParserDetails(e), e, filename);
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
        String contestYamlFilename = getContestYamlFilename(directoryName);
        try {
            // SOMEDAY would it be easier to load all yaml files instead?
            contents = loadFileWithIncludes(directoryName, contestYamlFilename);
            contestYamlFilename = DEFAULT_SYSTEM_YAML_FILENAME;
            if (new File(directoryName + File.separator + contestYamlFilename).exists()) {
                String[] lines = Utilities.loadFile(directoryName + File.separator + contestYamlFilename);
                contents = concat(contents, lines);
            }
            contestYamlFilename = DEFAULT_PROBLEM_SET_YAML_FILENAME;
            if (new File(directoryName + File.separator + contestYamlFilename).exists()) {
                String[] lines = Utilities.loadFile(directoryName + File.separator + contestYamlFilename);
                contents = concat(contents, lines);
            }
            contestYamlFilename = "system.pc2.yaml";
            if (new File(directoryName + File.separator + contestYamlFilename).exists()) {
                String[] lines = Utilities.loadFile(directoryName + File.separator + contestYamlFilename);
                contents = concat(contents, lines);
            }
        } catch (IOException e) {
            throw new YamlLoadException("Problem loading yaml " + e.toString(), e, contestYamlFilename);
        }
        return fromYaml(contest, contents, directoryName, loadDataFileContents);
    }

    private String[] concat(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] c = new String[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private String getContestYamlFilename(String directoryName) {
        return directoryName + File.separator + DEFAULT_CONTEST_YAML_FILENAME;
    }

    /**
     * Load from files, support #include FILENAME.
     * 
     * Loads text files, if a #include FILENAME  specified will
     * replace the #incldue with the contents of FILENAME.
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
        File contestYaml = new File(contestYamlFilename);
        
        // Try CLICS name first.  Fun fact: CLICS_CONTEST_NAME == CONTEST_NAME_KEY, but may not someday
        String contestTitle = fetchValue(contestYaml, IContestLoader.CLICS_CONTEST_NAME);
        // only if the CLICS name isn't there do we try the old one.  non-null means it is there.
        if(contestTitle == null) {
            contestTitle = fetchValue(contestYaml, IContestLoader.CONTEST_NAME_KEY);
        }
        return(contestTitle);
    }

    protected String fetchValue(File file, String key) {
        Map<String, Object> content = getContent(file.getAbsolutePath());
        return (String) content.get(key);
    }

    private Map<String, Object> getContent(String filename) {
        if (fullYamlContent == null) {
            fullYamlContent = loadYaml(filename);
        }

        return fullYamlContent;
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
        contest.updateContestInformation(contestInformation);

    }


    private void setSandboxCommand(IInternalContest contest, String sandboxCommandLine) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setSandboxCommandLine(sandboxCommandLine);
        contest.updateContestInformation(contestInformation);
    }
    
    private int getMemoryLimitMB(IInternalContest contest) {
        ContestInformation contestInformation = contest.getContestInformation();
        return(contestInformation.getMemoryLimitInMeg());
    }

    private void setMemoryLimitMB(IInternalContest contest, int memoryLimitMB) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setMemoryLimitInMeg(memoryLimitMB);
        contest.updateContestInformation(contestInformation);
    }


    private void setCcsTestMode(IInternalContest contest, boolean ccsTestMode) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setCcsTestMode(ccsTestMode);
        contest.updateContestInformation(contestInformation);
    }

    protected void setLoadSampleJudgesData(IInternalContest contest, boolean loadSampleFiles) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setLoadSampleJudgesData(loadSampleFiles);
        contest.updateContestInformation(contestInformation);
    }

    private void setStopOnFirstFailedTestCase(IInternalContest contest, boolean stopOnFirstFail) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setStopOnFirstFailedtestCase(stopOnFirstFail);
        contest.updateContestInformation(contestInformation);
    }    

    /**
     * Parse and convert dateString to Date.
     * 
     * 
     * @param dateString
     *            date string in form: yyyy-MM-dd HH:mm or yyyy-MM-dd HH:mmZ
     * @return date for input string
     * @throws ParseException
     */
    public static Date parseSimpleDate(String dateString) throws ParseException {
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
        contest.updateContestInformation(contestInformation);

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

    /**
     * Set Scoring properties value.
     * @param contest
     * @param keyName key in Scoring Properties
     * @param value value to be assigned
     */
    private void setScoringPropertyValue (IInternalContest contest, String keyName, String value)
    {
        ContestInformation contestInformation = contest.getContestInformation();
        Properties props = contestInformation.getScoringProperties();
        props.put(keyName, value);
        contestInformation.setScoringProperties(props);
    }

    private void setContestStartDateTime(IInternalContest contest, Date date) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setScheduledStartDate(date);
        contestInformation.setAutoStartContest(isBeforeNow(date));
    }

    /**
     * Input date before now, aka current date/time.
     * 
     * @param date
     * @return
     */
    protected boolean isBeforeNow(Date date) {
        Date now = new Date();
        return now.before(date);
    }

    @Override
    public IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String directoryName, boolean loadDataFileContents) {

        contest = createContest(contest);

        // name: ACM-ICPC World Finals 2011

        String contestFileName = getContestYamlFilename(directoryName);

        Map<String, Object> content = loadYaml(contestFileName, yamlLines);

        if (content == null) {
            return contest;
        }

        setTitle(contest, null);

        String contestTitle = fetchValue(content, CONTEST_NAME_KEY);
        if (contestTitle != null) {
            setTitle(contest, contestTitle);
        }

        boolean ccsTestMode = fetchBooleanValue(content, CCS_TEST_MODE, false);
        if (ccsTestMode) {
            setCcsTestMode(contest, ccsTestMode);
        }
        
        boolean loadSamples = fetchBooleanValue(content, LOAD_SAMPLE_JUDGES_DATA, true);
        setLoadSampleJudgesData(contest, loadSamples);
        
        boolean stopOnFirstFail = fetchBooleanValue(content, STOP_ON_FIRST_FAILED_TEST_CASE_KEY, false);
        setStopOnFirstFailedTestCase (contest, stopOnFirstFail);
        
        /**
         * assign shadow values
         */
        
        ContestInformation contestInformation = getContestInformation(contest);
        
        //set allow-multiple-team-logins mode
        boolean allowMultipleTeamLogins = fetchBooleanValue(content, ALLOW_MULTIPLE_TEAM_LOGINS_KEY, contestInformation.isAllowMultipleLoginsPerTeam());
        contestInformation.setAllowMultipleLoginsPerTeam(allowMultipleTeamLogins);
        
        // Load team scoreboard string (the one with variables)
        String teamScoreboadDisplayString = fetchValue(content, TEAM_SCOREBOARD_DISPLAY_FORMAT_STRING, contestInformation.getTeamScoreboardDisplayFormat());
        contestInformation.setTeamScoreboardDisplayFormat(teamScoreboadDisplayString);
        
        // enable shadow mode
        boolean shadowMode = fetchBooleanValue(content, SHADOW_MODE_KEY, contestInformation.isShadowMode());
        contestInformation.setShadowMode(shadowMode);
        
        String altAccountsLoadFilename = fetchValue(content, LOAD_ACCOUNTS_FILE_KEY, null);
        contestInformation.setOverrideLoadAccountsFilename(altAccountsLoadFilename);
        
        // base URL for CCS REST service
        String  ccsUrl= fetchValue(content, CCS_URL_KEY, contestInformation.getPrimaryCCS_URL());
        contestInformation.setPrimaryCCS_URL(ccsUrl);
        
        // CCS REST login
        String ccsLogin = fetchValue(content, CCS_LOGIN_KEY, contestInformation.getPrimaryCCS_user_login());
        contestInformation.setPrimaryCCS_user_login(ccsLogin);
        
        // CCS REST password
        String ccsPassoword = fetchValue(content, CCS_PASSWORD_KEY, contestInformation.getPrimaryCCS_user_pw());
        contestInformation.setPrimaryCCS_user_pw(ccsPassoword);
        

        String lastEventId = fetchValue(content, CCS_LAST_EVENT_ID_KEY, contestInformation.getLastShadowEventID());
        contestInformation.setLastShadowEventID(lastEventId);

        // save ContesInformation to model
        contest.updateContestInformation(contestInformation);
        

        String judgeCDPath = fetchValue(content, JUDGE_CONFIG_PATH_KEY);
        if (judgeCDPath != null) {
            setCDPPath(contest, judgeCDPath);
        } else {
            setCDPPath(contest, directoryName);
        }

        Integer defaultTimeout = fetchIntValue(content, TIMEOUT_KEY, DEFAULT_TIME_OUT);
        
        int currentGlobalMemoryLimit = getMemoryLimitMB(contest);
        Integer globalMemoryLimit = fetchIntValue(content, MEMORY_LIMIT_IN_MEG_KEY, currentGlobalMemoryLimit);
        if(currentGlobalMemoryLimit != globalMemoryLimit) {
            setMemoryLimitMB(contest, globalMemoryLimit);
        }
        
        for (String line : yamlLines) {
            if (line.startsWith(CLICS_CONTEST_NAME + DELIMIT) || line.startsWith(CONTEST_NAME_KEY + DELIMIT)) {
                setTitle(contest, unquoteAll(line.substring(line.indexOf(DELIMIT) + 1).trim()));

            }
        }

        loadDataFileContents = fetchBooleanValue(content, PROBLEM_LOAD_DATA_FILES_KEY, loadDataFileContents);

        String shortContestName = fetchValue(content, CLICS_CONTEST_ID);
        // only if CLICS id is not present do we try the older short-name
        if(shortContestName == null) {
            shortContestName = fetchValue(content, SHORT_NAME_KEY);
        }
        // only set short name if string is present AND not empty
        if (!StringUtilities.isEmpty(shortContestName)) {
            setShortContestName(contest, shortContestName);
        }

        if (null != fetchValue(content, AUTO_STOP_CLOCK_AT_END_KEY)) {
            // only set value if key present

            boolean autoStopClockAtEnd = fetchBooleanValue(content, AUTO_STOP_CLOCK_AT_END_KEY, false);
            setAutoStopClockAtEnd(contest, autoStopClockAtEnd);
        }

        String contestLength = fetchValue(content, CLICS_CONTEST_DURATION);
        // if CLICS duration not present, try old duration.
        // note as of CLICS spec 2022-07, the CLICS key is the same as the old one
        // we leave the old test here in case the CLICS key changes at some point.
        if(contestLength == null) {
            contestLength = fetchValue(content, CONTEST_DURATION_KEY);
        }
        if (contestLength != null) {
            setContestLength(contest, contestLength);
        }
        
        boolean isRunning  = fetchBooleanValue(content, "running", false);
        if (isRunning){
            ContestTime time = contest.getContestTime();
            if (time == null) {
                time = new ContestTime();
                time.setSiteNumber(contest.getSiteNumber());
            }
            time.startContestClock();
            contest.updateContestTime(time);
        }

        // There are several ways to specify the freeze length.  We try them here
        // in order of preference: CLICS, old, new  (does that make sense? shouldn't
        // new be tried before old? -- JB)
        String scoreboardFreezeTime = fetchValue(content, CLICS_CONTEST_FREEZE_DURATION);
        
        // This is absolutely ridiculous, but backward compatible *sigh*
        if(scoreboardFreezeTime == null) {
            // Old yaml name
            scoreboardFreezeTime = fetchValue(content, SCOREBOARD_FREEZE_KEY);
    
            if(scoreboardFreezeTime == null) {
                // New yaml name
                scoreboardFreezeTime = fetchValue(content, SCOREBOARD_FREEZE_LENGTH_KEY);
            }
        }
        // Only set time if not null or empty
        if (!StringUtilities.isEmpty(scoreboardFreezeTime)) {
            setScoreboardFreezeTime(contest, scoreboardFreezeTime);
        }

        Object startTimeObject = fetchObjectValue(content, CLICS_CONTEST_START_TIME);
        // if clics start time not present(!), try the old one
        if(startTimeObject == null) {
            startTimeObject = fetchObjectValue(content, CONTEST_START_TIME_KEY);
        }

        Date date = null;
        if (startTimeObject != null && startTimeObject instanceof Date) {
            setContestStartDateTime(contest, (Date) startTimeObject);
        } else {

            String startTime = fetchValue(content, CLICS_CONTEST_START_TIME);
            // only if CLICS start time is NOT there do we try the old one
            if(startTime == null) {
                startTime = fetchValue(content, CONTEST_START_TIME_KEY);
            }

            if (startTime != null) {

                /**
                 * Support previous format yyyy-MM-dd HH:mm or yyyy-MM-dd HH:mmZ
                 */

                try {
                    date = parseSimpleDate(startTime);
                    setContestStartDateTime(contest, date);
                } catch (ParseException e) {
                    date = null;
                    /**
                     * No longer a failure, will attempt to parse using ISO 8601 format
                     */
                }

                /**
                 * Parse ISO 8601 format date.
                 */

                if (date == null) {

                    try {

                        date = parseISO8601Date(startTime);
                        setContestStartDateTime(contest, date);

                    } catch (IllegalArgumentException e) {
                        throw new YamlLoadException("Invalid start-time value '" + startTime + " expected ISO 8601 format, " + e.getMessage(), e, contestFileName);
                    }
                }
            }
        }
        
        // If the contest type is present in contest.yaml, verify it
        String scoreType = fetchValue(content, CLICS_CONTEST_SCOREBOARD_TYPE);
        if(scoreType != null && !scoreType.equals("pass-fail")) {
            throw new YamlLoadException("Invalid " + CLICS_CONTEST_SCOREBOARD_TYPE + ": " + scoreType + ", expected pass-fail");
        }
        Object privatehtmlOutputDirectory = fetchObjectValue(content, OUTPUT_PRIVATE_SCORE_DIR_KEY);
        if (privatehtmlOutputDirectory != null) {
            if (privatehtmlOutputDirectory instanceof String) {
                setScoringPropertyValue(contest, DefaultScoringAlgorithm.JUDGE_OUTPUT_DIR, (String) privatehtmlOutputDirectory);
            } else {
                throw new YamlLoadException("Invalid value/type for " + OUTPUT_PRIVATE_SCORE_DIR_KEY + " <" + privatehtmlOutputDirectory + ">");
            }
        }

        Object publichtmlOutputDirectory = fetchObjectValue(content, OUTPUT_PUBLIC_SCORE_DIR_KEY);
        if (publichtmlOutputDirectory != null) {
            if (publichtmlOutputDirectory instanceof String) {
                setScoringPropertyValue(contest, DefaultScoringAlgorithm.PUBLIC_OUTPUT_DIR, (String) publichtmlOutputDirectory);
            } else {
                throw new YamlLoadException("Invalid value/type for " + OUTPUT_PUBLIC_SCORE_DIR_KEY + " <" + publichtmlOutputDirectory + ">");
            }
        }

        Object maxOutputSize = fetchObjectValue(content, MAX_OUTPUT_SIZE_K_KEY);
        if (maxOutputSize != null) {

            if (maxOutputSize instanceof Integer) {
                int maxSizeInK = ((Integer) maxOutputSize).intValue();
                if (maxSizeInK > 0) {
                    setMaxOutputSize(contest, maxSizeInK * Constants.BYTES_PER_KIBIBYTE);
                } else {
                    throw new YamlLoadException("Invalid max-output-size-K value '" + maxOutputSize + " size must be > 0 ", null, contestFileName);
                }
            } else {
                throw new YamlLoadException("Invalid max-output-size-K value '" + maxOutputSize + " size must an integer", null, contestFileName);
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
        String usingValidator = fetchValue(content, IContestLoader.USING_PC2_VALIDATOR);

        if (usingValidator != null && usingValidator.equalsIgnoreCase("true")) {
            overrideUsePc2Validator = true;
        }

        /**
         * Manual Review global override.
         */
        boolean manualReviewOverride = false;

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> judgingTypeContent = (LinkedHashMap<String, Object>) content.get(JUDGING_TYPE_KEY);
        if (judgingTypeContent != null) {
            manualReviewOverride = fetchBooleanValue(judgingTypeContent, MANUAL_REVIEW_KEY, false);
        } else {
            manualReviewOverride = fetchBooleanValue(content, MANUAL_REVIEW_KEY, false);
        }

        //get the current default global output size for the contest so getProblems() can use it if no
        // Problem-specific output limit is defined
        Long defaultMaxOutputBytes = new Long(contest.getContestInformation().getMaxOutputSizeInBytes());
        
        Problem[] problems = getProblems(yamlLines, defaultTimeout, defaultMaxOutputBytes, loadDataFileContents, defaultValidatorCommandLine, overrideValidatorCommandLine, overrideUsePc2Validator, manualReviewOverride);

        if (loadProblemDataFiles) {
            for (Problem problem : problems) {
                loadProblemInformationAndDataFiles(contest, directoryName, problem, overrideUsePc2Validator, manualReviewOverride);
                if (overrideValidatorCommandLine != null) {
                    problem.setOutputValidatorCommandLine(overrideValidatorCommandLine);
                }
            }
        }
        
        //update each of the problems with Input Validators as specified in the corresponding problem.yaml file, or if none is
        // specified in problem.yaml then look for custom input validators in the "input_format_validators" folder.
        //Note: ideally these calls to assignInputValidators() would be done inside method getProblems() as part of creating each Problem.
        //However, that would require changing the interface signature for getProblems(), because assigning Input Validators requires 
        // reading the problem.yaml file for each problem, which in turn requires knowing the base directory beneath which the problems are
        // defined -- but that directory is not currently passed to any version of interface method getProblems().
        // Since changing the interface signatures is a breaking change (not that there aren't already others under development),
        // it was decided to assign the Input Validators separately in a method that gets passed the problem directory name.
        for (Problem problem : problems) {
            assignInputValidators(contest, problem, directoryName);
        }
        
        Site[] sites = getSites(yamlLines);
        for (Site site : sites) {
            Site existingSite = contest.getSite(site.getSiteNumber());
            if (existingSite == null) {
                contest.addSite(site);
            } else {
                // updateSite works by elementId, so we have to modify the existing object
                existingSite.setDisplayName(site.getDisplayName());
                existingSite.setPassword(site.getPassword());
                Properties props = new Properties();
                props.put(Site.IP_KEY, site.getConnectionInfo().getProperty(Site.IP_KEY));
                props.put(Site.PORT_KEY, site.getConnectionInfo().getProperty(Site.PORT_KEY));
                existingSite.setConnectionInfo(props);
                contest.updateSite(existingSite);
            }
        }

        String[] categories = loadGeneralClarificationAnswers(yamlLines);
        for (String name : categories) {
            contest.addCategory(new Category(name));
        }

        // String[] answers = getGeneralAnswers(yamlLines);
        // SOMEDAY CCS load general answer catagories into contest

        Account[] accounts = getAccounts(yamlLines);
        
        Map<String, Object> passwordYamlMap = fetchMap(content, "passwords");
        
        if (passwordYamlMap != null) {

            String passTypeString = fetchValueDefault(passwordYamlMap, "type", PasswordType2.LETTERS_AND_DIGITS.toString());
            PasswordType2 passwordType = PasswordType2.valueOf(passTypeString.toUpperCase());

            String lengthString = fetchValueDefault(passwordYamlMap, "length", "8");
            int length = getIntegerValue(lengthString, 8);

            String prefix = fetchValueDefault(passwordYamlMap, "prefix", "");
            
            /**
             * Assign team passwords
             */
            Account[] updatedAccounts = assignPasswords(contest, accounts, length, passwordType, prefix);

            String targetDirectory = ".";
            if (directoryName != null) {
                targetDirectory = directoryName;
            }

            /**
             * Override output directory for files
             */
            targetDirectory = fetchValueDefault(passwordYamlMap, "outdirname", targetDirectory);

            String passfilename = fetchValueDefault(passwordYamlMap, "passfile", targetDirectory + File.separator + MailMergeFile.PASSWORD_LIST_FILENNAME);
            
            /**
             * Write OS login passwords file (just a list of passwords in a text file)
             */

            generateOSPasswords(passfilename, updatedAccounts.length, passwordType, length, prefix);

            String mergefilename = fetchValueDefault(passwordYamlMap, "mergefile", targetDirectory + File.separator + MailMergeFile.DEFAULT_MERGE_OUTPUT_FILENAME);

            try {
                /**
                 * Write mail merge file
                 */
                MailMergeFile.writeFile(mergefilename, passfilename, Arrays.asList(updatedAccounts));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            // TODO TODAY write accounts.tsv file
        }
        
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
        
        ClientId[] proxyClientIds = getShadowProxyClientIds(yamlLines);
//        System.out.println("debug  There are "+proxyClientIds.length+" shadow proxy client definitions in yaml in dir "+directoryName);
        
        if (proxyClientIds.length > 0) {
            for (ClientId clientId : proxyClientIds) {
                Account account = contest.getAccount(clientId);
                if (account != null) {
                    account.addPermission(Type.SHADOW_PROXY_TEAM);
                    contest.updateAccount(account);
//                    System.out.println("debug  Added proxy account "+account.getClientId().toString());
                } else {
                    syntaxError("No such account for proxy of " + clientId.getClientType().toString() + " " + clientId.getClientNumber() + " at site " + clientId.getSiteNumber());
                    ;
                }
            }
        }

        PlaybackInfo playbackInfo = getReplaySettings(yamlLines);

        if (playbackInfo != null) {
            contest.addPlaybackInfo(playbackInfo);
        }

        return contest;

    }

    
    /**
     * Generate and write OS password file.
     * @param passfilename
     * @param count
     * @param passwordType
     * @param length
     * @param prefix
     */
    private void generateOSPasswords(String passfilename, int count, PasswordType2 passwordType, int length, String prefix) {
        
        boolean joePassword = PasswordType2.JOE.equals(passwordType);
        
        List<String> passwords = null;
        if (joePassword){
            passwords = PasswordGenerator.generateJoePasswords("team", count);
        } else {
            passwords = PasswordGenerator.generatePasswords(count, passwordType, length, prefix);
        }

        String[] lines = (String[]) passwords.toArray(new String[passwords.size()]);

        try {
            Utilities.writeLinesToFile(passfilename, lines);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Assign passwords to team accounts.
     * 
     * @param contest
     * @param accounts
     * @param length
     * @param passwordType
     * @param prefix
     * @return
     */
    protected Account[] assignPasswords(IInternalContest contest, Account[] accounts, int length, PasswordType2 passwordType, String prefix) {

        List<Account> teamAccounts = getTeamAccounts(accounts);
        
        if (teamAccounts.size() == 0){
            
            teamAccounts = getTeamAccounts(contest.getAccounts());
            
        }

        if (teamAccounts.size() > 0) {

            // passwords:
            //  - settings
            //            length: 8
            // # default is from letters and numbers from description
            // #   type: joe
            // #   type: digits
            //            prefix: bark
            // 
            // # If account not specified defaults to TEAMS and JUDGES
            //   
            //  - account: TEAM
            // # default all judges
            //  - account: JUDGE
            //            site: 1

            boolean joePassword = PasswordType2.JOE.equals(passwordType);

            if (joePassword) {
                for (Account account : teamAccounts) {
                    account.setPassword(account.getClientId().getName());
                }
            } else {
                List<String> passwords = PasswordGenerator.generatePasswords(teamAccounts.size(), passwordType, length, prefix);

                int i = 0;
                for (Account account : teamAccounts) {
                    account.setPassword(passwords.get(i));
                    i++;
                }
            }
        }
        
        // TODO assign judge and other accounts
        
        return (Account[]) teamAccounts.toArray(new Account[teamAccounts.size()]);
    }

    /**
     * Get all team accounts.
     * @param accounts
     * @return all team accounts.
     */
    private List<Account> getTeamAccounts(Account[] accounts) {
        List<Account> list = new ArrayList<>();
        for (Account account : accounts) {
            if (ClientType.Type.TEAM.equals(account.getClientId().getClientType())){
                list.add(account);
            }
        }
        return list;
    }

    /**
     * Find group for input string.
     * 
     * @param groups
     * @param groupInfo
     *            group external id or group name
     * @return null if no match, else the group
     */
    protected Group lookupGroupInfo(Group[] groups, String string) {

        for (Group group : groups) {
            if (group.getDisplayName().equals(string.trim())) {
                return group;
            } else {
                int id = toInt(string, -1);
                if (group.getGroupId() == id) {
                    return group;
                }
            }

        }
        return null;
    }

    /**
     * 
     * @param stop
     *            - true, auto stop at end of contest
     */
    private void setAutoStopClockAtEnd(IInternalContest contest, boolean stop) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setAutoStopContest(stop);
        contest.updateContestInformation(contestInformation);
    }

    private void setMaxOutputSize(IInternalContest contest, int maxOutputBytes) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setMaxOutputSizeInBytes(maxOutputBytes);
        contest.updateContestInformation(contestInformation);
    }

    public Date parseISO8601Date(String startTime) {
        Calendar cal = DatatypeConverter.parseDateTime(startTime);
        Date date = cal.getTime();
        return date;
    }
  
    protected ContestInformation getContestInformation(IInternalContest contest){
        ContestInformation contestInformation = contest.getContestInformation();
        return contestInformation;
    }

    private void setScoreboardFreezeTime(IInternalContest contest, String scoreboardFreezeTime) {
        ContestInformation contestInformation = contest.getContestInformation();
        // adapted from core.util.JSONTool
        // seems the yaml is converting the 1:00:00 into 3600
        if (scoreboardFreezeTime.length() > 2) {
            if (!scoreboardFreezeTime.contains(":")) {
                try {
                    long seconds = Long.parseLong(scoreboardFreezeTime);
                    scoreboardFreezeTime = ContestTime.formatTime(seconds);
                } catch (NumberFormatException e) {
                    syntaxError("Failed to parse scoreboard freeze time `" + scoreboardFreezeTime + "`, expected seconds "+ e.getMessage());
                }
            }
        }
        contestInformation.setFreezeTime(scoreboardFreezeTime);
        contest.updateContestInformation(contestInformation);
    }

    private void setContestLength(IInternalContest contest, String contestLength) {
        ContestTime time = contest.getContestTime();
        if (time == null) {
            time = new ContestTime();
            time.setSiteNumber(contest.getSiteNumber());
        }
        time.setContestLengthSecs(parseTimeIntoSeconds(contestLength, Constants.DEFAULT_CONTEST_LENGTH_SECONDS));
        contest.updateContestTime(time);
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

            // TODO REFACTOR CLEANUP - make Utilties.stringToLongSecs static
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
        contest.updateContestInformation(contestInformation);

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

        Map<String, Object> yamlContent = loadYaml(null, yamlLines);
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
        
        // Load permissions from yaml into accounts
        Account[] fullAccountList = accountVector.toArray(new Account[accountVector.size()]);
        PermissionYamlLoader loader = new PermissionYamlLoader(yamlLines, fullAccountList);
        return loader.getAccountsArray();

    }

    private Object fetchObjectValue(Map<String, Object> content, String key) {
        if (content == null) {
            return null;
        }
        Object value = content.get(key);
        return value;
    }

    /**
     * Fetch value from a map.
     * 
     * @param content
     * @param key
     * @return null if content does not contain a value for the key, else the value for the key.
     */
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
    
    private String fetchValue(Map<String, Object> content, String key, String defaultValue) {
        if (content == null) {
            return null;
        }
        Object value = content.get(key);
        if (value == null) {
            return defaultValue;
        } else if (value instanceof String) {
            return (String) content.get(key);
        } else {
            return content.get(key).toString();
        }
    }


    private boolean isValuePresent(Map<String, Object> content, String key) {
        if (content == null) {
            return false;
        }
        Object value = content.get(key);
        return value != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PlaybackInfo getReplaySettings(String[] yamlLines) {

        PlaybackInfo info = new PlaybackInfo();

        Map<String, Object> yamlContent = loadYaml(null, yamlLines);
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

        Map<String, Object> yamlContent = loadYaml(null, yamlLines);
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

    private Long fetchLongValue(Map<String, Object> map, String key, long defaultValue) {
        Long value = null;
        if (map != null) {
            value = (Long) map.get(key);
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
        if (map == null) {
            // SOMEDAY figure out why map would every be null
            return null;
        }
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
        Object object = content.get(key);
        if (object != null) {
            if (object instanceof Map) {
                return (Map<String, Object>) content.get(key);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void loadProblemInformationAndDataFiles(IInternalContest contest, String baseDirectoryName, Problem problem, boolean overrideUsePc2Validator) {
        loadProblemInformationAndDataFiles(contest, baseDirectoryName, problem, overrideUsePc2Validator, false);
    }

    @Override
    public void loadProblemInformationAndDataFiles(IInternalContest contest, String baseDirectoryName, Problem problem, boolean overrideUsePc2Validator, boolean overrideManualReview) {

        Group[] groups = contest.getGroups();  // fetch once instead of fetching for each problem (in loop)
        
        // SOMEDAY CCS code this: do not add problem to contest model, new new parameter flag

        String problemDirectory = baseDirectoryName + File.separator + problem.getShortName();

        problem.setExternalDataFileLocation(problemDirectory);

        String problemYamlFilename = problemDirectory + File.separator + DEFAULT_PROBLEM_YAML_FILENAME;

        Map<String, Object> content = loadYaml(problemYamlFilename);

        String problemLaTexFilename = problemDirectory + File.separator + "problem_statement" + File.separator + DEFAULT_PROBLEM_LATEX_FILENAME;

        String problemTitle = fetchValue(content, PROBLEM_NAME_KEY);

        if (new File(problemLaTexFilename).isFile()) {
            problemTitle = getProblemNameFromLaTex(problemLaTexFilename);
        } else {
            problemLaTexFilename = problemDirectory + File.separator + "problem_statement" + File.separator + DEFAULT_ENGLISH_PROBLEM_LATEX_FILENAME;
            problemTitle = getProblemNameFromLaTex(problemLaTexFilename);
        }
        
        boolean usingCustomValidator = false;
        Map<String, Object> validatorContent = fetchMap(content, VALIDATOR_KEY);
        if (validatorContent != null) {
            usingCustomValidator = fetchBooleanValue(validatorContent, IContestLoader.USING_CUSTOM_VALIDATOR, false);
        }
        
        // check for CLICS "validation" property; provides an alternate way to specify a customer validator and,
        // the ONLY way to specify if the problem is interactive.
        String validationType = fetchValue(content, VALIDATION_TYPE);
        boolean isInteractive = false;
        if (validationType != null) {
            // validationType is a list of validation options
            String[] valOpts = validationType.split("\\s");
            // Must be one of: "default", "custom", "custom interactive"
            // PC2 does not support "custom score", which IS spec compliant; we issue a different error for that
            if (valOpts.length >= 1) {
                if(valOpts[0].equals(Constants.VALIDATION_CUSTOM)) {
                    usingCustomValidator = true;
                    if(valOpts.length >= 2) {
                        if(valOpts[1].equals(Constants.VALIDATION_INTERACTIVE)) {
                            // Note that interactive problems require a custom validator
                            isInteractive = true;
                        } else if(valOpts[1].equals(Constants.VALIDATION_SCORE)) {
                            syntaxError("Unsupported validation type: custom score");
                        } else {
                            syntaxError("Unknown valudation type: custom " + valOpts[1]);
                        }
                    }
                } else if(!valOpts[0].equals(Constants.VALIDATION_DEFAULT)) {
                    syntaxError("Unknown validation type " + valOpts[0] + " specified");
                }
            } else {
                syntaxError(VALIDATION_TYPE + " property found but no type was specified");
            }
        }

        boolean pc2FormatProblemYamlFile = false;
        String usingValidator = fetchValue(validatorContent, IContestLoader.USING_PC2_VALIDATOR);

        if (usingValidator != null && usingValidator.equalsIgnoreCase("true")) {
            pc2FormatProblemYamlFile = true;
        }

        if (overrideUsePc2Validator) {
            pc2FormatProblemYamlFile = true;
        }

        // TODO: I am not sure why this test is contingent on pc2FormatProblemYamlFile. (JB)
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
            String dataFileName = fetchValue(content, "datafile");
            String answerFileName = fetchValue(content, "answerfile");

            loadPc2ProblemFiles(contest, dataFileBaseDirectory, problem, problemDataFiles, dataFileName, answerFileName);
        } else {
            loadCCSProblemFiles(contest, dataFileBaseDirectory, problem, problemDataFiles);
        }

        //
        assignValidatorSettings(content, problem);
        
        // Make sure the validator settings are acceptable for interactive problems
        if(isInteractive) {
            if(!usingCustomValidator) {
                throw new YamlLoadException("For problem short name " + problem.getShortName() + 
                        ", a custom validator is required for an interactive problem");
                
            }
            if(!problem.getCustomOutputValidatorSettings().isUseClicsValidatorInterface()) {
                throw new YamlLoadException("For problem short name " + problem.getShortName() + 
                        ", the custom validator must be a CLICS compliant for an interactive problem");
                
            } else {
                // A note here about how interactive validation works.  The interactive validator must be CLICS
                // compliant and return an exit code of 42 or 43, and possibly generating a feedback file for
                // each test case.  PC2 is nominally aware of interactive validators and only knows enough to
                // call a script (pc2sandbox_interactive.sh or pc2_interactive.sh) to perform a testcase run.
                // The results of that testcase are written to a known results file (and feedback directory) by
                // the pc2sandbox_interactive.sh/pc2_interactive.sh script (be it accepted, wrong answer, RTE, TLE, MLE etc).
                // PC2 then knows to call a special validator during the validation phase (pc2validate_interactive.sh) to
                // copy the results file/feedback dir into the place that pc2 expects it so it can determine if the
                // submission is correct or not.  This special validator is a PC2 compliant (not CLICS) so we can
                // return complete result info in one XML file, other than AC or WA.  CLICS validators do not allow that
                // without munging the feedback dir.  The important thing is, the actual testcase is validated by a CLICS
                // validator.
                
                // We set the custom output validator settings for interactive.  The CLICS spec does not have
                // a validator section in the YAML, as such, we first verified that the custom validator is CLICS compliant.
                // We then we set output validator type to clics interactive.
                problem.getCustomOutputValidatorSettings().setUseInteractiveValidatorInterface();
            }
        }
        
        //read any PC2-format limits specified at the top level of the problem.yaml file
        Integer timeoutSecs = fetchIntValue(content, TIMEOUT_KEY);
        if (timeoutSecs != null) {
            problem.setTimeOutInSeconds(timeoutSecs);
        }
        Integer maxOutputPC2 = fetchIntValue(content, MAX_OUTPUT_SIZE_K_KEY);
        if (maxOutputPC2 != null) {
            problem.setMaxOutputSizeKB(maxOutputPC2);
        }
        
        Integer memoryLimit = fetchIntValue(content, MEMORY_LIMIT_IN_MEG_KEY, Problem.DEFAULT_MEMORY_LIMIT_MB);
        problem.setMemoryLimitMB(memoryLimit);
        
        String sandboxCommandLine = fetchValue(content, SANDBOX_COMMAND_LINE_KEY, "");
        problem.setSandboxCmdLine(sandboxCommandLine);
        
        String sandboxProgramName = fetchValue(content, SANDBOX_PROGRAM_NAME_KEY, "");
        problem.setSandboxProgramName(sandboxProgramName);
        
        String sandboxTypeString = fetchValue(content, SANDBOX_TYPE_KEY);
        if (sandboxTypeString != null) {

            try {
                SandboxType type = SandboxType.valueOf(sandboxTypeString);
                problem.setSandboxType(type);
            } catch (Exception e) {
                throw new YamlLoadException("For problem short name " + problem.getShortName() + //
                        ", unknown sandbox type " + sandboxTypeString + " " + e.getMessage(), e);
            }

        } else {
            problem.setSandboxType(SandboxType.NONE);
        }

        //get the map (if any) of the CLICS "limits" section in the problem.yaml file
        Map<String, Object> limitsContent = fetchMap(content, LIMITS_KEY);

        //if there is a CLICS "limits" section in the problem.yaml, read any values in that section and use 
        // them to override any PC2-formatted values (just read in, above)
        if (limitsContent != null) {
            
            //check for a CLICS timeout limit
            //NOTE:  CLICS does not support directly specifying a problem time limit (like PC2 does with the "timeout" key).
            //Rather, the CLCIS problem package format (at https://icpc.io/problem-package-format/spec/problem_package_format#limits)
            //specifies two time-related limit values:  "time_multiplier" and "time_safety_margin".  The overall idea is that the CCS should
            //first read and execute all the "acccepted judge's solutions" (contained in the CDP shortname/submissions/accepted folder).
            //Next, the CCS should apply the "time_multiplier" to the slowest of the accepted judge's solutions and use that as the "timeout"
            //value, allowing for a variance specified by the "time-safety_margin".  PC2 needs to implement this for CLICS compatibility, but
            //it currently doesn't have any support for reading/executing the judge's accepted solutions to get these values,
            //so we'll reject any time_multiplier and time_safety_margin values that are present.
            
            //Note also:  the following code is COMMENTED OUT because some of the existing PC2 JUnits contain "limits:" sections
            // which DO have CLICS "time_multiplier" and/or "time_saftety_margin" entries in them (even though PC2 doesn't currently
            // support those attibutes).  The problem is that the presence of those attributes in these JUnit test files results
            // in those JUnits throwing YamlLoadExceptions.
//            Integer clics_time_multiplier = fetchIntValue(limitsContent, CLICS_TIME_MULTIPLIER_KEY);
//            if (clics_time_multiplier != null) {
//                //TODO: replace the following exception with code to properly handle the CLICS time_multiplier value.
//                throw new YamlLoadException("Unsupported CLICS attribute in " + problemYamlFilename + " 'limits' section: " + CLICS_TIME_MULTIPLIER_KEY);
//            }
//            Integer clics_time_safety_margin = fetchIntValue(limitsContent, CLICS_TIME_SAFETY_MARGIN_KEY);
//            if (clics_time_safety_margin != null) {
//                //TODO: replace the following exception with code to properly handle the CLICS time_safety_margin value.
//                throw new YamlLoadException("Unsupported CLICS attribute in " + problemYamlFilename + " 'limits' section: " + CLICS_TIME_SAFETY_MARGIN_KEY);
//            }
            
            //check for a timeout limit within the CLICS "limits:" section. 
            // Note that the presence of a "timeout:" entry within a CLICS
            // "limits:" section is non-CLICS standard -- but we want to support it in PC2.
            Integer clicsTimeout = fetchIntValue(limitsContent, TIMEOUT_KEY);
            if (clicsTimeout != null) {
                problem.setTimeOutInSeconds(clicsTimeout);
            }
            
            //check for a CLICS maxoutput limit - the value is in MiB
            Integer clicsMaxOutput = fetchIntValue(limitsContent, CLICS_MAX_OUTPUT_KEY);
            if (clicsMaxOutput != null) {
                problem.setMaxOutputSizeKB(clicsMaxOutput * Constants.KIBIBYTE_PER_MEBIBYTE);
            }
            
            Integer clicsMemoryLimit = fetchIntValue(limitsContent, MEMORY_LIMIT_CLICS, Problem.DEFAULT_MEMORY_LIMIT_MB);
            problem.setMemoryLimitMB(clicsMemoryLimit);
        }
        
        if (!usingCustomValidator) {
            if (!pc2FormatProblemYamlFile) {
                // SOMEDAY CCS add CCS validator derived based on build script
                /**
                 * - Use CCS build command to build validator ('build' script name) - add validator created by build command - add CCS run script ('run' script name)
                 */

                // default-validator: /home/pc2/Desktop/codequest12_problems/default_validator
                // override-validator: /usr/local/bin/mtsv {:problemletter} {:resfile} {:basename}

                // `$validate_cmd $inputfile $answerfile $feedbackfile < $teamoutput `;

                addClicsOutputValidator(problem, problemDataFiles, baseDirectoryName);
                
            } else {
                addDefaultPC2Validator(problem, 1);
            }
        } else {
            // using Custom Output Validator
            String outputValidatorNameFromYaml = fetchValue(validatorContent, "validatorProg");
            
            if (outputValidatorNameFromYaml != null) {
                Problem cleanProblem = contest.getProblem(problem.getElementId());
                ProblemDataFiles problemDataFile = contest.getProblemDataFile(problem);
                
                String outputValidatorName = findOutputValidatorFile (baseDirectoryName, problem, outputValidatorNameFromYaml);
                
                if (!StringUtilities.isEmpty(outputValidatorName)) {
                    
                    // Check for output validator if defined
                    
                    if (!new File(outputValidatorName).isFile()) {
                        throw new YamlLoadException("Missing output validator for problem " + problem.getShortName() + ", expecting at: " + outputValidatorName);
                    }

                    SerializedFile outputValidatorFile = new SerializedFile(outputValidatorName);
                    if (outputValidatorFile.getSHA1sum() != null) {
                        
                        // Save validator internal file
                        problemDataFile.setOutputValidatorFile(outputValidatorFile);
                        
                        // save validator program name
                        problem.setOutputValidatorProgramName(outputValidatorName);
                        contest.updateProblem(cleanProblem, problemDataFile);
                    } else {
                        // Halt loading and throw YamlLoadException
                        syntaxError("Error: problem " + problem.getLetter() + " - " + problem.getShortName() + " custom validator import failed: " + outputValidatorFile.getErrorMessage());
                    }
                }

            }
        }
        
        boolean globlStopOnFirstFail = contest.getContestInformation().isStopOnFirstFailedtestCase();

        boolean stopOnFirstFail = fetchBooleanValue(content, STOP_ON_FIRST_FAILED_TEST_CASE_KEY, globlStopOnFirstFail);
        problem.setStopOnFirstFailedTestCase(stopOnFirstFail);

        assignJudgingType(content, problem, overrideManualReview);

        boolean showOutputWindow = fetchBooleanValue(content, SHOW_OUTPUT_WINDOW, true);
        problem.setHideOutputWindow(!showOutputWindow);

        boolean showCompareWindow = fetchBooleanValue(content, SHOW_COMPARE_WINDOW, false);
        problem.setShowCompareWindow(showCompareWindow);

        boolean hideProblem = fetchBooleanValue(content, HIDE_PROBLEM, false);
        problem.setActive(!hideProblem);

        boolean showValidationResults = fetchBooleanValue(content, SHOW_VALIDATION_RESULTS, true);
        problem.setShowValidationToJudges(showValidationResults);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> judgingTypeContent = (LinkedHashMap<String, Object>) content.get(JUDGING_TYPE_KEY);
        if (judgingTypeContent != null) {
            assignJudgingType(judgingTypeContent, problem, overrideManualReview);
        }

        // override CCS standard read input data from stdin
        Map<String, Object> problemInputContent = fetchMap(content, PROBLEM_INPUT_KEY);
        if (problemInputContent != null) {
            boolean readFromSTDIN = fetchBooleanValue(problemInputContent, READ_FROM_STDIN_KEY, true);
            problem.setReadInputDataFromSTDIN(readFromSTDIN);
        }

        String groupListString = fetchValue(content, GROUPS_KEY);
        if (groupListString != null) {

            if (groupListString.trim().length() == 0) {
                syntaxError("Empty group list");
            }

            String[] fields = groupListString.split(";");
            if (groupListString.indexOf(';') == -1) {
                fields = groupListString.split(",");
            }

            for (String groupInfo : fields) {
                groupInfo = groupInfo.trim();
                Group group = lookupGroupInfo(groups, groupInfo);
                if (group == null) {
                    if (groups == null || groups.length == 0) {
                        syntaxError("For "+problem.getShortName()+" ERROR No groups defined. (groups.tsv not loaded?), error when trying to find group for '" + groupInfo + "' from yaml value '" + groupListString + "' ");
                    } else {
                        syntaxError("Undefined group '" + groupInfo + "' for group list '" + groupListString + "' ");
                    }
                }

                problem.addGroup(group);
            }
        }

        // SOMEDAY CCS - send preliminary - add bug - fix.
        // boolean sendPreliminary = fetchBooleanValue(content, SEND_PRELIMINARY_JUDGEMENT_KEY, false);
        // if (sendPreliminary){
        // problem.setPrelimaryNotification(true);
        // }

    }

    /**
     * Assign validator config settings.
     * 
     * @param content
     * @param problem
     * @param contest
     */
    protected void assignValidatorSettings(Map<String, Object> content, Problem problem) {

        // PC2 CLICS validator section
        // validator:
        // validatorProg: pc2.jar edu.csus.ecs.pc2.validator.Validator
        // validatorCmd: "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} -pc2 1 true"
        // usingInternal: true
        // validatorOption: 1

        Object object = content.get(VALIDATOR_KEY);

        if (object instanceof LinkedHashMap) {
            // Handle validator yaml section

            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) object; // fetchList(content, VALIDATOR_KEY);

            boolean customType = fetchBooleanValue(map, IContestLoader.USING_CUSTOM_VALIDATOR, false);
            // boolean pc2Type = fetchBooleanValue(map, IContestLoader.USING_PC2_VALIDATOR, false);
            String validatorProg = fetchValue(map, "validatorProg");

            String validatorCmd = fetchValue(map, "validatorCmd");
            // junit does not expect NONE to be set....
            // if (pc2Type) {
            problem.setValidatorType(VALIDATOR_TYPE.PC2VALIDATOR);
            problem.setOutputValidatorProgramName(validatorProg);
            String validatorOption = fetchValue(map, "validatorOption");

            PC2ValidatorSettings settings = new PC2ValidatorSettings();
            if (validatorOption != null) {
                settings.setWhichPC2Validator(Integer.parseInt(validatorOption));
            }

            settings.setIgnoreCaseOnValidation(true);

            if (validatorCmd != null) {
                settings.setValidatorCommandLine(validatorCmd);
            }
            problem.setPC2ValidatorSettings(settings);
            // }
            if (customType) {
                problem.setValidatorType(VALIDATOR_TYPE.CUSTOMVALIDATOR);
                problem.setOutputValidatorProgramName(validatorProg);
                CustomValidatorSettings customSettings = new CustomValidatorSettings();
                boolean clicsMode = fetchBooleanValue(map, IContestLoader.USE_CLICS_CUSTOM_VALIDATOR_INTERFACE, true);
                if (clicsMode) {
                    customSettings.setUseClicsValidatorInterface();
                } else {
                    customSettings.setUsePC2ValidatorInterface();
                }
                
                customSettings.setValidatorCommandLine(validatorCmd);
                
                if (problem.getOutputValidatorProgramName() != null) {
                    // TODO REFACTOR There are two locations that store the validator program name, there should be one.
                    customSettings.setValidatorProgramName(problem.getOutputValidatorProgramName());
                }
                
                customSettings.setValidatorProgramName(validatorProg);
                problem.setCustomOutputValidatorSettings(customSettings);
            }
            // String usingInternal = fetchValue(map, "usingInternal");

            return; // =================== RETURN
        }

        // PC2 CLICS validator flags
        // validator_flags: options are: [case_sensitive] [space_change_sensitive] [float_absolute_tolerance FLOAT] [float_tolerance FLOAT]
        // ex. validator_flags: float_tolerance 1e-6

        String validatorFlags = fetchValue(content, IContestLoader.VALIDATOR_FLAGS_KEY);
        if (validatorFlags != null && validatorFlags.trim().length() > 0) {

            try {
                ClicsValidatorSettings settings = new ClicsValidatorSettings(validatorFlags);
                problem.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);
                problem.setCLICSValidatorSettings(settings);
            } catch (RuntimeException e) {
                throw new YamlLoadException("For problem " + problem.getShortName() + ", invalid validator flags '" + validatorFlags + "' " + e.getMessage(), e.getCause());
            }

        }

        // CLICS validator
        // validator: options are: [case_sensitive] [space_change_sensitive] [float_absolute_tolerance FLOAT] [float_tolerance FLOAT]
        // ex. validator: float_tolerance 1e-6

        String validatorParameters = fetchValue(content, IContestLoader.VALIDATOR_KEY);
        if (validatorParameters != null && validatorParameters.trim().length() > 0) {
            try {
                ClicsValidatorSettings settings = new ClicsValidatorSettings(validatorParameters);
                problem.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);
                problem.setCLICSValidatorSettings(settings);
            } catch (RuntimeException e) {
                throw new YamlLoadException("For problem " + problem.getShortName() + ", invalid validator flags '" + validatorParameters + "' " + e.getMessage(), e.getCause());
            }
        }

    }

    @Override
    public void dumpSerialzedFileList(Problem problem, String logPrefixId, SerializedFile[] sfList) {
        // SOMEDAY Auto-generated method stub

    }

    @Override
    public Problem addDefaultPC2Validator(Problem problem, int optionNumber) {

        problem.setValidatorType(VALIDATOR_TYPE.PC2VALIDATOR);

        PC2ValidatorSettings settings = new PC2ValidatorSettings();
        settings.setWhichPC2Validator(optionNumber);
        settings.setIgnoreCaseOnValidation(true);
        settings.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND + " -pc2 " + settings.getWhichPC2Validator() + " " + settings.isIgnoreCaseOnValidation());
        settings.setValidatorProgramName(Constants.PC2_VALIDATOR_NAME);

        problem.setPC2ValidatorSettings(settings);
        return problem;
    }

    @Override
    public String[] loadGeneralClarificationAnswers(String[] yamlLines) {
        return fetchStringList(yamlLines, CLAR_CATEGORIES_KEY);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String[] fetchStringList(String[] yamlLines, String key) {
        Map<String, Object> content = loadYaml(null, yamlLines);
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
    
    public ClientId [] getShadowProxyClientIds(String[] yamlLines) {
        ArrayList<ClientId> clientIdList = new ArrayList<ClientId>();
        Map<String, Object> yamlContent = loadYaml(null, yamlLines);
        @SuppressWarnings("unchecked")
        ArrayList<Map<String, Object>> list = fetchList(yamlContent, "team-proxy-accounts");

        if (list != null) {
            for (Object object : list) {

                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) object;

                String accountType = fetchValue(map, "account");
                checkField(accountType, "Account Type");

                ClientType.Type type = ClientType.Type.valueOf(accountType.trim());
                Integer siteNumber = fetchIntValue(map, "site", 1);
                String numberString = fetchValue(map, "number");
                
                int[] clientNumbers = getNumberList(numberString.trim());
                

                for (int i = 0; i < clientNumbers.length; i++) {

                    int clientNumber = clientNumbers[i];
                    ClientId newId = new ClientId(siteNumber, type, clientNumber);
                    clientIdList.add(newId);
                }
            }
        }
        return (ClientId[]) clientIdList.toArray(new ClientId[clientIdList.size()]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Language[] getLanguages(String[] yamlLines) {
        ArrayList<Language> languageList = new ArrayList<Language>();

        // System.out.println(Utilities.join("\n", yamlLines));

        Map<String, Object> yamlContent = loadYaml(null, yamlLines);
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
                    String compilerName = fetchValue(map, "compiler");
                    String pc2CompilerCommandLine = fetchValue(map, PC2_COMPILER_CMD);
                    
         

                    language.setDisplayName(name);
                    
                    if (compilerName != null) {

                        // CLICS Language 
                        compilerName = fetchValue(map, "compiler");
                        String compilerArgs = fetchValue(map, "compiler-args");
                        String runner = fetchValue(map, "runner");
                        String runnerArgs = fetchValue(map, "runner-args");

                        checkField(compilerName, "Language \"" + name + "\" missing compiler key/value");

                        if (compilerArgs == null) {
                            language.setCompileCommandLine(compilerName);
                        } else {
                            language.setCompileCommandLine(compilerName + " " + compilerArgs);
                        }

                        String programExecuteCommandLine = null;
                        if (runner == null) {
                            /**
                             * Assume a.out if no runner
                             */
                            runner = "a.out";
                        }
                        
                        if (runnerArgs == null) {
                            programExecuteCommandLine = runner;
                        } else {
                            programExecuteCommandLine = runner + " " + runnerArgs;
                        }

                        language.setProgramExecuteCommandLine(programExecuteCommandLine);

                    } else if (pc2CompilerCommandLine != null) {

                        //    - name: 'Java'
                        //        active: true
                        //        compilerCmd: 'javac -encoding UTF-8 -sourcepath . -d . {:mainfile}'
                        //        exemask: '{:basename}.class'
                        //        execCmd: 'java {:basename}'
                        //        use-judge-cmd: true
                        //        judge-exec-cmd:  'java {:basename}'

                        language.setCompileCommandLine(pc2CompilerCommandLine);

                        String programExecuteCommandLine = fetchValue(map, PC2_EXEC_CMD);
                        language.setProgramExecuteCommandLine(programExecuteCommandLine);

                        String exeMask = fetchValue(map, "exemask");
                        language.setExecutableIdentifierMask(exeMask);

                    } else if (lookedupLanguage != null) {
                        language = lookedupLanguage;
                    } else {
                        syntaxError("Language \"" + name + "\" missing language definition (compiler command line and program execution command line)");
                    }

                    checkField(language.getCompileCommandLine(), "Language \"" + name + "\" missing compiler command line");
                    checkField(language.getProgramExecuteCommandLine(), "Language \"" + name + "\" missing programm execution command line");

                    boolean active = fetchBooleanValue(map, "active", true);
                    language.setActive(active);

                    boolean useJudgeCommand = fetchBooleanValue(map, USE_JUDGE_COMMAND_KEY, false);
                    language.setUsingJudgeProgramExecuteCommandLine(useJudgeCommand);

                    boolean isInterpreted = fetchBooleanValue(map, INTERPRETED_LANGUAGE_KEY, language.isInterpreted());
                    language.setInterpreted(isInterpreted);

                    String judgeExecuteCommandLine = fetchValue(map, JUDGE_EXECUTE_COMMAND_KEY);
                    if (judgeExecuteCommandLine != null) {
                        language.setJudgeProgramExecuteCommandLine(judgeExecuteCommandLine);
                    } else {
                        language.setUsingJudgeProgramExecuteCommandLine(false);
                    }
                    
                    String clicsLanguageId = fetchValue(map, "clics-id");
                    if (clicsLanguageId != null){
                        language.setID(clicsLanguageId);
                    }

                    // SOMEDAY handle interpreted languages, seems it should be in the export

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
    public Problem[] getProblems(String[] yamlLines, int seconds, long maxOutputBytes, boolean loadDataFileContents, String defaultValidatorCommand, String overrideValidatorCommandLine, boolean overrideUsePc2Validator,
            boolean manualReviewOverride) {

        Vector<Problem> problemList = new Vector<Problem>();

        Map<String, Object> yamlContent = loadYaml(null, yamlLines);
        // use problemset yaml key
        ArrayList<Map<String, Object>> list = fetchList(yamlContent, PROBLEMS_KEY);

        if (list == null) {
            // use problems yaml key
            list = fetchList(yamlContent, PROBLEMSET_PROBLEMS_KEY);
        }

        //at this point if "list" is not null then it should contain an entry for each problem defined in 
        //the "problemset" section of the contest.yaml file.  Each problem entry in "list" has four yaml-defined
        // key/value pairs, with keys "letter", "short-name", "color", and "rgb".
        if (list != null) {
            
            //process each problem entry in list
            for (Object object : list) {
                
                //get a map of the problem key/value pairs (see comment above)
                Map<String, Object> problemMap = (Map<String, Object>) object;

                //make sure the problem has a "short-name"
                String problemKeyName = fetchValue(problemMap, SHORT_NAME_KEY);
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

                //get the problem name
                String problemTitle = fetchValue(problemMap, PROBLEM_NAME_KEY);
                if (problemTitle == null) {
                    problemTitle = problemKeyName;
                }
                //initialize a new problem with the specified name
                Problem problem = new Problem(problemTitle);

                //set newly-loaded problems to use computer judging by default (not sure why? jlc)
                problem.setComputerJudged(true);

                //set problem time limit.  If the problem.yaml file for the current problem (codified in the "problemMap")
                // contains a "TIMEOUT_KEY", use the timeout value from the problem.yaml; otherwise use the passed-in default.
                int actSeconds = fetchIntValue(problemMap, TIMEOUT_KEY, seconds);
                problem.setTimeOutInSeconds(actSeconds);

                //set problem output limit.  If the problem.yaml file for the current problem (codified in the "problemMap")
                // contains an "OUTPUT" key, use the timeout value from the problem.yaml; otherwise use the passed-in default.
                Long actualMaxOutputBytes = fetchLongValue(problemMap, MAX_OUTPUT_SIZE_K_KEY, maxOutputBytes);
                problem.setMaxOutputSizeKB(actualMaxOutputBytes/Constants.BYTES_PER_KIBIBYTE);
                
                //TODO:  add code to check for the CLICS-compliant key "output:" in the "limits: section
                // of problem.yaml if the PC2 "MAX_OUTPUT_SIZE_K_KEY doesn't exist

                problem.setShowCompareWindow(false);

                problem.setShortName(problemKeyName);
                if (!problem.isValidShortName()) {
                    throw new YamlLoadException("Invalid short problem name '" + problemKeyName + "'");
                }

                String problemLetter = fetchValue(problemMap, "letter");
                String colorName = fetchValue(problemMap, "color");
                String colorRGB = fetchValue(problemMap, "rgb");

                // SOMEDAY CCS assign Problem variables for color and letter
                problem.setLetter(problemLetter);
                problem.setColorName(colorName);
                problem.setColorRGB(colorRGB);

                /**
                 * Assign each problem default values from contest.yaml level
                 */
                assignJudgingType(yamlContent, problem, manualReviewOverride);

                LinkedHashMap<String, Object> judgingTypeContent = (LinkedHashMap<String, Object>) yamlContent.get(JUDGING_TYPE_KEY);
                if (judgingTypeContent != null) {
                    assignJudgingType(judgingTypeContent, problem, manualReviewOverride);
                }

                assignJudgingType(problemMap, problem, manualReviewOverride);

                //if the problem.yaml file for the current problem (codified in the "problemMap") has a "load-data-files"
                // key, use that to set the "loadFilesFlag"; if not, use the passed-in default.
                boolean loadFilesFlag = fetchBooleanValue(problemMap, PROBLEM_LOAD_DATA_FILES_KEY, loadDataFileContents);
                problem.setUsingExternalDataFiles(!loadFilesFlag);

                //if the problem.yaml file for the current problem (codified in the "problemMap") has a VALIDATOR_KEY
                // key, use that to obtain the "outputValidatorCommandLine"; if not, use the passed-in defaults.
                String outputValidatorCommandLine = fetchValue(problemMap, VALIDATOR_KEY);

                if (outputValidatorCommandLine == null) {
                    outputValidatorCommandLine = defaultValidatorCommand;
                }
                if (overrideValidatorCommandLine != null) {
                    outputValidatorCommandLine = overrideValidatorCommandLine;
                }
                problem.setValidatorType(VALIDATOR_TYPE.PC2VALIDATOR);
                problem.setOutputValidatorCommandLine(outputValidatorCommandLine);                

                problemList.addElement(problem);
            }
        }

        return (Problem[]) problemList.toArray(new Problem[problemList.size()]);
    }

    /**
     * Assign individual problem input validator(s) to a problem.  This method 
     * reads the "problem.yaml" file associated with the specified problem (as determined by the "problem short-name")
     * and uses the settings in that problem.yaml file to determine what Input Validators to assign to the problem.
     * The method always arranges that a Viva Input Validator is configured for the problem (although the Viva Pattern
     * for the problem will be empty if no pattern is specified in the problem.yaml file).  
     * If the problem.yaml file specifies a "custom input validator", that validator
     * is configured into the problem; if not, the method searches the "input_format_validators" folder for an input validator
     * and configures that into the problem.
     * 
     * @param contest the contest in which the specified problems are configured.
     * @param problem the Problem which is to be updated with Input Validators.
     * @param problemsBaseDir the name of the directory where Problems are stored under their short-name values.
     * 
     * @throws YamlLoadException 
     *              if the specified problem has a null or empty-string short-name;
     *              if the problem directory could not be found;
     *              if an error or exception occurred loading a custom input validator program.
     */
    protected void assignInputValidators(IInternalContest contest, Problem problem, String problemsBaseDir) {
        
        String probName = problem.getShortName();
        
        if (probName==null || probName.equals("")) {
            String errMsg = "Error during YAML loading: encountered contest problem with null/empty short name";
            YamlLoadException exception = new YamlLoadException(errMsg);
            throw exception;
        }

        String problemDir = problemsBaseDir + File.separator + probName;
        File probDir = new File(problemDir);
        if (probDir.exists() && probDir.isDirectory()) {

            //get the entire problem.yaml file as a YAML map
            String problemYamlFileName = probDir + File.separator + DEFAULT_PROBLEM_YAML_FILENAME;
            Map<String, Object> problemYamlMap = loadYaml(problemYamlFileName);

            //get the "input_validator" section from the problem.yaml file map
            Map<String, Object> inputValidatorMap = fetchMap(problemYamlMap, INPUT_VALIDATOR_KEY);

            //we haven't (yet) set the default Input Validator type
            boolean defaultInputValidatorTypeHasBeenSet = false;
            
             //we haven't (yet) loaded a custom input validator into the problem
            boolean customInputValidatorProgramHasBeenSet = false;
            
            //we haven't (yet) set a Viva pattern in the problem
            boolean vivaPatternHasBeenSet = false;
            
            //check if there is an "input_validator" section in the problem.yaml file
            if (inputValidatorMap != null) {

                //yes; process the "input_validator" section settings, assigning defaults for unspecified settings
                
                // if there is a default Input Validator type (NONE, VIVA, or CUSTOM) specified, set that in the problem
                String defaultIVType = fetchValue(inputValidatorMap, DEFAULT_INPUT_VALIDATOR_KEY);
                if (defaultIVType != null) {
                    String defaultIVTypeIgnoreCase = defaultIVType.toLowerCase();
                    switch (defaultIVTypeIgnoreCase) {
                        case "none":
                            problem.setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE.NONE);
                            break;
                        case "viva":
                            problem.setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE.VIVA);
                            break;
                        case "custom":
                            problem.setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE.CUSTOM);
                            break;
                        default:
                            syntaxError("Unknown value for " + DEFAULT_INPUT_VALIDATOR_KEY + ": " + defaultIVType);
                    }
                    defaultInputValidatorTypeHasBeenSet = true;
                    
                } 

                // if there is a custom input validator command specified in the YAML map, set it in the problem
                String customInputValidatorCommandLine = fetchValue(inputValidatorMap, CUSTOM_INPUT_VALIDATOR_COMMAND_LINE_KEY);
                if (customInputValidatorCommandLine != null) {
                    problem.setCustomInputValidatorCommandLine(customInputValidatorCommandLine);
                } else {
                    problem.setCustomInputValidatorCommandLine("");
                }

                // if there is a custom input validator program specified in the YAML map, attempt to read the file into a SerializedFile
                String customInputValidatorProgName = fetchValue(inputValidatorMap, CUSTOM_INPUT_VALIDATOR_PROGRAM_NAME_KEY);
                if (customInputValidatorProgName != null) {
                    String pathToCustomProg = getInputValidatorDir(problemsBaseDir, problem) + File.separator 
                            + customInputValidatorProgName;
                    SerializedFile customIVProg = new SerializedFile(pathToCustomProg);
                    // check for errors/exceptions during file loading
                    try {
                        if (Utilities.serializedFileError(customIVProg)) {
                            String errMsg = "Unable to load custom input validator program '" + customInputValidatorProgName + "': " 
                                            + customIVProg.getErrorMessage();
                            YamlLoadException exception = new YamlLoadException(errMsg);
                            throw exception;
                        }
                    } catch (Exception e) {
                        String errMsg = "Exception loading custom input validator program '" + customInputValidatorProgName + "': " 
                                        + e.getMessage();
                        YamlLoadException exception = new YamlLoadException(errMsg);
                        throw exception;
                    }
                    // the custom input validator was successfully loaded; add it to the problem
                    problem.setCustomInputValidatorFile(customIVProg);
                    problem.setProblemHasCustomInputValidator(true);
                    problem.setCustomInputValidationStatus(InputValidationStatus.NOT_TESTED);
                    problem.setCustomInputValidatorHasBeenRun(false);
                    customInputValidatorProgramHasBeenSet = true;
                    
                } else {
                    // no custom input validator was specified in the problem.yaml "input_validator" section
                    problem.setCustomInputValidatorFile(null);
                    problem.setProblemHasCustomInputValidator(false);
                    problem.setCustomInputValidationStatus(InputValidationStatus.NOT_TESTED);
                    problem.setCustomInputValidatorHasBeenRun(false);
                }
                
                // if there is a VIVA pattern file specified in the YAML map, attempt to read the file into a SerializedFile

                String vivaPatternFileName = fetchValue(inputValidatorMap, VIVA_PATTERN_FILE_KEY);
                if (vivaPatternFileName != null) {
                    SerializedFile vivaPatternSF = new SerializedFile(vivaPatternFileName);
                    // check for errors/exceptions during file loading
                    try {
                        if (Utilities.serializedFileError(vivaPatternSF)) {
                            syntaxError("Unable to load VIVA pattern file '" + vivaPatternFileName + "': " + vivaPatternSF.getErrorMessage());
                        }
                    } catch (Exception e) {
                        syntaxError("Exception loading VIVA pattern file '" + vivaPatternFileName + "': " + e.getMessage());
                    }
                    // the Viva pattern file was successfully loaded; add it to the problem
                    String[] patternLines = new String(vivaPatternSF.getBuffer()).split("\n");
                    problem.setVivaInputValidatorPattern(patternLines);
                    vivaPatternHasBeenSet = true;
                } 

                // if there is a VIVA pattern specified directly in the YAML map, add it to the problem.
                // Note that this means a pattern directly specified in the YAML file supersedes any
                // reference to a pattern FILE also in the YAML (since that would have been loaded above and
                // this will override it).
                String vivaPattern = fetchValue(inputValidatorMap, VIVA_PATTTERN_KEY);
                if (vivaPattern != null) {
                    // a Viva pattern was found in the YAML file; add it to the problem
                    String[] patternLines = vivaPattern.split("\n");
                    problem.setVivaInputValidatorPattern(patternLines);
                    vivaPatternHasBeenSet = true;
                }

            }

            // check if a custom input validator got set by the problem.yaml "input_validator" section
            // (it can only have been set if there was such a section)
            if (!customInputValidatorProgramHasBeenSet) {

                // no custom IV has been set; try to load one from the "input_format_validators" folder
                customInputValidatorProgramHasBeenSet = addCustomInputValidator(problem, contest.getProblemDataFile(problem), problemsBaseDir);
                
                //if addCustomInputValidator() loaded a custom Input Validator, it also set the problem's custom IV status.
                // if not, do so here
                if (!customInputValidatorProgramHasBeenSet) {
                    
                    // we didn't find a custom input validator anywhere
                    problem.setProblemHasCustomInputValidator(false);
                    problem.setCustomInputValidatorCommandLine(null);
                    problem.setCustomInputValidatorFile(null);
                    problem.setCustomInputValidationStatus(InputValidationStatus.NOT_TESTED);
                    problem.setCustomInputValidatorHasBeenRun(false);
                    
                } 
            }
            
            //if the user didn't set the default Input Validator type via the problem.yaml file, set it here
            if (!defaultInputValidatorTypeHasBeenSet) {
                if (vivaPatternHasBeenSet) {
                    problem.setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE.VIVA);
                } else if (customInputValidatorProgramHasBeenSet) {
                    problem.setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE.CUSTOM);
                } else {
                    problem.setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE.NONE);
                }
            }
                       
            //This block of code is commented out because whether or not the problem has a Viva pattern is now determined by whether it has a non-zero-length pattern field;
            // a separate variable "problemHasVivaInputValidatorPattern" in the Problem class is not warranted (and is actually dangerous; it allows the problem 
            // to enter an invalid state where the boolean variable is set to one indication but the actual pattern indicates the opposite).
//            // if the user set the Viva pattern via the problem.yaml file, mark the problem to so indicate
//            if (vivaPatternHasBeenSet) {
//                problem.setProblemHasVivaInputValidatorPattern(true);
//            } else {
//                // no Viva pattern was found (either because it wasn't explicitly specified in the problem.yaml
//                // "input_validator:" section, or because there was no such section in the problem.yaml file; 
//                // in either case, mark the problem as such
//                problem.setProblemHasVivaInputValidatorPattern(false);
//            }
            //in either case, Viva has not been run and has not produced any validation status; mark the problem such
            problem.setVivaInputValidationStatus(InputValidationStatus.NOT_TESTED);
            problem.setVivaInputValidatorHasBeenRun(false);

            
        } else {
            //the problem folder either doesn't exist or is not a directory
            String errMsg = "Error during YAML loading: unable to locate problem folder '" + problemDir + "'";
            YamlLoadException exception = new YamlLoadException(errMsg);
            throw exception;
        }

    }

//    /**
//     * Returns a map containing the Input Validator settings from the problem.yaml file for the problem whose 
//     * short-name is contained in the specified problemMap.  
//     * 
//     * @param problemMap a problemset map containing the short-name of the problem.
//     * 
//     * @return a map of input validator settings for the specified problem.
//     */
//    protected LinkedHashMap<String, Object> getInputValidatorMap(Map<String, Object> problemMap) {
//        
//        //get problem short-name out of the received map
//            
//        LinkedHashMap<String,Object> inputValidatorMap = (LinkedHashMap<String, Object>) yamlContent.get(JUDGING_TYPE_KEY);
//            
//        return inputValidatorMap ;
//    }

    public String getInputValidatorDir(String baseDirectoryName, Problem problem) {

        // from https://clics.ecs.baylor.edu/index.php/Problem_format
        // squares/input_format_validators/squares_input_checker1.py
        // squares/input_format_validators/squares_input_checker2/check.c
        // squares/input_format_validators/squares_input_checker2/data.h

        return baseDirectoryName + File.separator + problem.getShortName() + File.separator + "input_format_validators";
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

    protected int toInt(String string, int defaultNumber) {

        try {

            if (string != null && string.length() > 0) {
                return Integer.parseInt(string.trim());
            }
        } catch (Exception e) {
            // ignore, will return default if a parsing error
        }

        return defaultNumber;

    }

    protected int getIntegerValue(String string, int defaultNumber) {
        return StringUtilities.getIntegerValue(string, defaultNumber);
    }

    protected int[] getNumberList(String numberString) {
        return StringUtilities.getNumberList(numberString);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AutoJudgeSetting[] getAutoJudgeSettings(String[] yamlLines, Problem[] problems) {

        ArrayList<AutoJudgeSetting> ajList = new ArrayList<AutoJudgeSetting>();

        Map<String, Object> yamlContent = loadYaml(null, yamlLines);
        ArrayList<Map<String, Object>> list = fetchList(yamlContent, AUTO_JUDGE_KEY);

        if (list != null) {

            // SOMEDAY get accounts from contest too
            Account[] accounts = getAccounts(yamlLines);

            for (Object object : list) {

                Map<String, Object> map = (Map<String, Object>) object;

                String accountType = fetchValue(map, "account");
                ClientType.Type type = ClientType.Type.valueOf(accountType.trim());

                int siteNumber = fetchIntValue(map, "site", 1);

                // SOMEDAY 669 check for syntax errors
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
        return getProblems(contents, defaultTimeOut, Constants.DEFAULT_MAX_OUTPUT_SIZE_K*1024, loadDataFileContents, defaultValidatorCommandLine, null, false, false);
    }

    @Override
    public IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String directoryName) {
        return fromYaml(contest, yamlLines, directoryName, false);
    }
    
    @Override
    public Problem[] getProblems(String[] yamlLines, int defaultTimeOut) {
        return getProblems(yamlLines, defaultTimeOut, Constants.DEFAULT_MAX_OUTPUT_SIZE_K*1024);
    }
    
    @Override
    public Problem[] getProblems(String[] yamlLines, int defaultTimeOut, boolean loadDataFileContents, String defaultValidatorCommand, String overrideValidatorCommandLine, boolean overrideUsePc2Validator, boolean manualReviewOverride) {
        return getProblems(yamlLines, defaultTimeOut, Constants.DEFAULT_MAX_OUTPUT_SIZE_K*1024, loadDataFileContents, defaultValidatorCommand, overrideValidatorCommandLine, overrideUsePc2Validator, manualReviewOverride);
    }

    @Override
    public Problem[] getProblems(String[] contents, int defaultTimeOut, long defaultMaxOutputSize) {
        return getProblems(contents, defaultTimeOut, defaultMaxOutputSize, true, null, null, false, false);
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
        throw exception;
    }

    /**
     * Adds the CLICS output validator as the output validator for the specified problem.
     * 
     * Developer's note: this method at one time also had code which loaded a Clics INPUT Validator
     * and added it to the specified problem.  Since this method is about loading the OUTPUT Validator,
     * that code was moved to a separate method {@link #addCustomInputValidator(Problem, ProblemDataFiles, String)}.
     * 
     * @param problem the {@link Problem} to which the CLICS output validator is to be added.
     * @param problemDataFiles the {@link ProblemDataFiles} associated with the specified problem.
     * @param baseDirectoryName the config of the directory where problems are found, ex /home/ubtuntu/current/config
     * 
     * @return an updated Problem (the problem is also modified via the received reference parameter).
     * 
     * @see #addCustomInputValidator(Problem, ProblemDataFiles, String)
     */
    private Problem addClicsOutputValidator(Problem problem, ProblemDataFiles problemDataFiles, String baseDirectoryName) {

        problem.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);

        problem.setReadInputDataFromSTDIN(true);

        // problem.setValidatorProgramName(Constants.CLICS_VALIDATOR_NAME);

        // if we use the internal Java CCS validator use this.
        // problem.setValidatorCommandLine("java -cp {:pc2jarpath} " + CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND);
        if (problem.getOutputValidatorCommandLine() == null) {
            problem.setOutputValidatorCommandLine(Constants.DEFAULT_CLICS_VALIDATOR_COMMAND);
        }

        
        return problem;
    }

    /**
     * Return path of output validator
     * 
     * @param baseDirectoryName
     *            CDP base directory
     * @param problem
     * @return null if
     */
    protected String findOutputValidatorFile(String baseDirectoryName, Problem problem, String validatorFile) {

        if (StringUtilities.isEmpty(validatorFile)) {
            return validatorFile;
        }

        /**
         * Return path if validator is an absolute path.
         * 
         * Ex. /home/ubuntu/current/config/alt_output_validator
         */
        if (new File(validatorFile).isFile()) {
            return validatorFile;
        }

        /**
         * Check for validator under output_validators/
         * 
         * Ex. /home/ubuntu/current/config/bells/output_validators/bells_validator/validator
         */
        validatorFile = baseDirectoryName + File.separator + problem.getShortName() + File.separator + //
                OUTPUT_VALIDATORS + File.separator + validatorFile;

        if (new File(validatorFile).isFile()) {
            return validatorFile;
        }

        /**
         * Check for under CDP/basename
         * 
         * Ex. /home/ubuntu/current/config/bells/validator
         */
        String baseValidatorFile = baseDirectoryName + File.separator + validatorFile;

        if (new File(baseValidatorFile).isFile()) {
            return baseValidatorFile;
        }

        return validatorFile;
    }

    /**
     * Adds a Custom Input Validator (also called an Input Format Validator) to the specified {@link Problem} and
     * its associated {@link ProblemDataFiles} if an appropriate Input Validator can be found.
     * 
     * This method is based on searching for Input Validators defined by the CLICS Problem Package Format specification
     * (https://icpc.io/problem-package-format/spec/problem_package_format).  CLICS Input Validators are Custom Input Validators (in PC2 terminology) which are found in the CLICS-defined
     * Problem Package Format under the folder "<B><I>input_format_validators</i></b>".  This method searches for such a folder
     * under the specified Problem definition folder; if found, it searches that folder for input validators and 
     * assigns the first validator found, if any, to the specified Problem and its associated ProblemDataFiles.
     * 
     * Note that the CLICS problem package format specification defines that a problem may have MULTIPLE input format validators.
     * If more than one input format validator is found in the <B><I>input_format_validators</i></b> folder, the FIRST such
     * input validator is chosen, and a warning message is displayed on the standard output.
     * 
     * If no CLICS Custom Input Validator can be found, this method silently does nothing.
     * 
     * //TODO: update PC2 to support the ability to execute multiple CLICS input format validators.
     *
     * @param problem the Problem to which an Input Format Validator is to be assigned.
     * @param problemDataFiles the ProblemDataFiles associated with the specified problem.
     * @param problemsBaseDir the folder under which problems are stored by their short-name.
     * 
     * @return true if the method found an input validator and loaded it into the problem; false otherwise.
     * 
     * @throws YamlLoadException if an error or exception occurs while loading an Input Validator file.
     */
    private boolean addCustomInputValidator(Problem problem, ProblemDataFiles problemDataFiles, String problemsBaseDir) {
        
        //search for an input validator beneath the specified folder
        String inputValidatorName = findInputValidator(problemsBaseDir, problem);

        try {
            /**
             * If file is there load it
             */
            if (inputValidatorName != null && new File(inputValidatorName).isFile()) {
                SerializedFile customInputValidatorFile = new SerializedFile(inputValidatorName);
                
                //make sure there were no errors constructing the SerializedFile
                if (Utilities.serializedFileError(customInputValidatorFile)) {
                    String msg = "Error loading Input Validator: " + customInputValidatorFile.getErrorMessage() ;
                    YamlLoadException ex = new YamlLoadException(msg);
                    throw ex;
                }

                //insert the input validator file into the Problem and the ProblemDataFiles
                problem.setCustomInputValidatorFile(customInputValidatorFile);
                problemDataFiles.setCustomInputValidatorFile(customInputValidatorFile);

                //set the custom input validator command line to the input validator file basename as a default
                String basename = customInputValidatorFile.getName();
                problem.setCustomInputValidatorCommandLine(basename);
                
                //if the input validator file is a DOS script, update the command line to properly invoke it
                if (basename.toLowerCase().endsWith(".bat") || basename.toLowerCase().endsWith(".cmd")) {
                    problem.setCustomInputValidatorCommandLine("cmd /c " + basename);
                }
                
                //if the input validator file is a Java ".class" file, update the command line to properly invoke it
                if (basename.toLowerCase().endsWith(".class")) {
                    basename = basename.replaceFirst(".class", "");
                    problem.setCustomInputValidatorCommandLine("java " + basename);
                }

                //input validator program name now comes from the SerializedFile; there is no separate "name" field any more.
//                // set validator name to short filename
//                problem.setCustomInputValidatorProgramName(customInputValidatorFile.getName());
                
                //update the problem's input validator state
                problem.setProblemHasCustomInputValidator(true);
                problem.setCustomInputValidationStatus(InputValidationStatus.NOT_TESTED);
                problem.setCustomInputValidatorHasBeenRun(false);
                
                return true;

            } 
        } catch (Exception e) {
            throw new YamlLoadException("Unable to load input format validator for problem " + problem.getShortName() + ": " + inputValidatorName, e);
        }

        return false;
    }


    /**
     * Get file directory entries with relative dir path
     * 
     * @param directory
     *            - directory to search and to prepend onto the matching filenames
     * @return
     */
    // SOMEDAY move this to Utilities class

    public static String[] getFileEntries(String directory) {
        ArrayList<String> list = new ArrayList<>();
        File[] files = new File(directory).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                list.add(directory + File.separator + file.getName());
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    protected String findInputValidator(String baseDirectoryName, Problem problem) {

        String inputValidatorDir = getInputValidatorDir(baseDirectoryName, problem);

        String inputValidatorFilename = null;

        if (new File(inputValidatorDir).isDirectory()) {
            // there is a input format validator dir

            // search for validator
            String[] filenames = getFileEntries(inputValidatorDir);

            if (filenames.length == 1) {
                // only found one file
                inputValidatorFilename = filenames[0];
            } else if (filenames.length > 1) {

                ArrayList<String> validatorList = new ArrayList<String>();

                // Loop through files looking for potential validator programs

                for (String name : filenames) {
                    if (name.toLowerCase().endsWith("build")) {
                        // Cannot be build
                        continue;
                    } else if (name.toLowerCase().endsWith("readme")) {
                        // Cannot be README
                        continue;
                    } else {

                        if (Utilities.isExecutableExtension(name)) {
                            validatorList.add(name);
                        }
                    }
                }

                if (validatorList.size() == 1) {
                    inputValidatorFilename = validatorList.get(0);

                } else if (validatorList.size() > 1) {
                    inputValidatorFilename = validatorList.get(0);

                    for (String string : validatorList) {
                        System.out.println("Warning dup input format validator " + Utilities.getCurrentDirectory() + File.separator + string);
                    }
                    System.out.println("Using: " + inputValidatorFilename);
                    // YamlLoadException yex = new YamlLoadException("Too many input format validators found for " + problem.getShortName() + " found " + validatorList.size());
                    // throw yex;
                }
            }
        }

        return inputValidatorFilename;
    }

    @Override
    public void loadPc2ProblemFiles(IInternalContest contest, String dataFileBaseDirectory, Problem problem, ProblemDataFiles problemDataFiles2, String dataFileName, String answerFileName) {

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        if (dataFileName == null) {
            syntaxError("Missing datafile for pc2 problem " + problem.getShortName());
        }

        if (answerFileName == null) {
            syntaxError("Missing answerfile for pc2 problem " + problem.getShortName());
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
        
        boolean loadSamples = contest.getContestInformation().isLoadSampleJudgesData();
        
        String sampleDataDirectory = dataFileBaseDirectory.replaceAll("secret$", "sample");

        String[] inputFileNames = getFileNames(dataFileBaseDirectory, ".in");

        String[] answerFileNames = getFileNames(dataFileBaseDirectory, ".ans");

        if (inputFileNames.length == 0) {
            throw new YamlLoadException("No input (.in) file names found for " + problem.getDisplayName() + " in dir " + dataFileBaseDirectory);
        }

        if (answerFileNames.length == 0) {
            throw new YamlLoadException("No answer (.ans) file names found for " + problem.getDisplayName() + " in dir " + dataFileBaseDirectory);
        }

        if (inputFileNames.length == answerFileNames.length) {

            ArrayList<SerializedFile> dataFiles = new ArrayList<SerializedFile>();
            ArrayList<SerializedFile> answerFiles = new ArrayList<SerializedFile>();
            
            if (loadSamples) {
                loadDataFiles(problem, dataFiles, answerFiles, sampleDataDirectory, loadExternalFile);
            }
            
            // Load all secret files
            loadDataFiles(problem, dataFiles, answerFiles, dataFileBaseDirectory, loadExternalFile);

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

    /**
     * Per problem, add files names into datafiles and answerfiles.
     * 
     * @param problem 
     * @param dataFiles input data files
     * @param answerFiles input answer files
     * @param fileSourceDirectory - location for the secret or sample files
     * @param loadExternalFile - load as external data fils
     */
    protected void loadDataFiles(Problem problem, ArrayList<SerializedFile> dataFiles, ArrayList<SerializedFile> answerFiles, String fileSourceDirectory, boolean loadExternalFile) {
        
        String[] inputFileNames = getFileNames(fileSourceDirectory, ".in");
        String[] answerFileNames = getFileNames(fileSourceDirectory, ".ans");
        
        if (inputFileNames.length == answerFileNames.length) {

            // Sort file names before loading into datafiles and answerfiles
            Arrays.sort(inputFileNames);
            Arrays.sort(answerFileNames);

            for (int idx = 0; idx < inputFileNames.length; idx++) {

                problem.addTestCaseFilenames(inputFileNames[idx], answerFileNames[idx]);

                String answerShortFileName = inputFileNames[idx].replaceAll(".in$", ".ans");

                String dataFileName = fileSourceDirectory + File.separator + inputFileNames[idx];
                String answerFileName = dataFileName.replaceAll(".in$", ".ans");

                checkForFile(dataFileName, "Missing " + inputFileNames[idx] + " file for " + problem.getShortName() + " in " + fileSourceDirectory);
                checkForFile(answerFileName, "Missing " + answerShortFileName + " file for " + problem.getShortName() + " in " + fileSourceDirectory);

                dataFiles.add(new SerializedFile(dataFileName, loadExternalFile));
                answerFiles.add(new SerializedFile(answerFileName, loadExternalFile));
            }

        } else {
            throw new YamlLoadException("  For " + problem.getShortName() + " Missing data files -  there are " + inputFileNames.length + " .in files and " + //
                    answerFileNames.length + " .ans files " + " in " + fileSourceDirectory);
        }

    }

    private void validateCCSData(IInternalContest contest, Problem problem) {

        // SOMEDAY CCS Load Validator

        // SOMEDAY CCS somehow find validator, compile, and test.
        // the somehow is because there may be more than one validator in the validator directory

        // SOMEDAY 1. Check files (all files present as required + check problem.yaml)
        // SOMEDAY 2. Check compile (check that all programs compile)
        // SOMEDAY 3. Check input (run input validators)
        // SOMEDAY 4. Check solutions (run all solutions check that they get the expected verdicts)

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

    /**
     * Check for field, if value missing throw exception
     * @param value
     * @param fieldName
     */
    private void checkField(String value, String fieldName) {
        if (value == null) {
            syntaxError("Missing " + fieldName);
        } else if (value.trim().length() == 0) {
            syntaxError("Missing " + fieldName);
        }
    }

    public void assignJudgingType(String[] yaml, Problem problem, boolean overrideManualReviewFlag) {
        Map<String, Object> map = loadYaml(null, yaml);
        assignJudgingType(map, problem, overrideManualReviewFlag);
    }

    /**
     * Assign individual problem judging type based on map values.
     */
    protected void assignJudgingType(Map<String, Object> map, Problem problem, boolean overrideManualReviewFlag) {

        // if (map == null || map.entrySet().isEmpty()){
        // System.out.println("debug problem "+problem.getShortName()+" has NO map");
        // } else {
        // System.out.println("debug problem "+problem.getShortName()+" "+map);
        // }

        if (isValuePresent(map, SEND_PRELIMINARY_JUDGEMENT_KEY)) {
            boolean sendPreliminary = fetchBooleanValue(map, SEND_PRELIMINARY_JUDGEMENT_KEY, false);
            problem.setPrelimaryNotification(sendPreliminary);
        }

        if (isValuePresent(map, COMPUTER_JUDGING_KEY)) {
            boolean computerJudged = fetchBooleanValue(map, COMPUTER_JUDGING_KEY, false);
            problem.setComputerJudged(computerJudged);
        }

        boolean manualReview = problem.isManualReview();

        if (isValuePresent(map, MANUAL_REVIEW_KEY)) {
            manualReview = fetchBooleanValue(map, MANUAL_REVIEW_KEY, false);
        }

        if (overrideManualReviewFlag) {
            manualReview = true;
        }

        if (manualReview) {
            problem.setManualReview(true);
        }
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

        return "Problem " + problem.getShortName() + "  cj/man/prelim = " + problem.isComputerJudged() + //
                " / " + problem.isManualReview() + //
                " / " + problem.isPrelimaryNotification();
    }

    /**
     * Convert String to second. Expects input in form: ss or mm:ss or hh:mm:ss
     * 
     * @param s
     *            string to be converted to seconds
     * @return -1 if invalid time string, 0 or greater if valid
     */
    public long stringToLongSecs(String s) {

        // TODO REFACTOR CLEANUP - make Utilties.stringToLongSecs static - then remove stringToLongSecs method

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

        String conestYamleFilename = getSampleContesYaml(name);

        if (new File(conestYamleFilename).isFile()) {
            return conestYamleFilename;
        } else {
            return null;
        }
    }

    /**
     * Get sample yaml in sample config dir.
     * 
     * 
     * @param name
     * @return
     */
    protected String getSampleContesYaml(String name) {

        String sampleDir = "samps" + File.separator + "contests";
        return sampleDir + File.separator + name + File.separator + "config" + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
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
        String teams2TSVFile = cdpConfigDirectory.getAbsolutePath() + File.separator + LoadICPCTSVData.TEAMS2_TSV;

        String groupsTSVFile = cdpConfigDirectory.getAbsolutePath() + File.separator + LoadICPCTSVData.GROUPS_FILENAME;

        // only load if both a teams TSV and groups TSV files are present.

        if ((new File(teamsTSVFile).isFile() || new File(teams2TSVFile).isFile()) && new File(groupsTSVFile).isFile()) {

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
            
            String contestYamlFile = entry.getPath() + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
            if (new File(contestYamlFile).exists()) {
                // if contest.yaml in current directory, then this is the equiv of config directory.
                return entry;
            } else {
                // found a CDP base directory
                cdpConfigDirectory = new File(entry.getAbsoluteFile() + File.separator + CONFIG_DIRNAME);
            }

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

        if (contest == null) {
            throw new IllegalArgumentException("contest is null");
        }

        File cdpConfigDirectory = findCDPConfigDirectory(entry);

        if (cdpConfigDirectory == null) {
            throw new Exception("Cannot find CDP for " + entry);
        } else {

            loadCCSTSVFiles(contest, cdpConfigDirectory);

            contest = fromYaml(contest, cdpConfigDirectory.getAbsolutePath());

            if (contest.getSites().length == 0) {
                // Create default site.
                Site site = createFirstSite(contest.getSiteNumber(), "localhost", Constants.DEFAULT_PC2_PORT);
                contest.addSite(site);
            }

            if (contest.getJudgements().length == 0) {
                // judgements not loaded from yaml

                String rejectIniFile = cdpConfigDirectory + File.separator + Constants.JUDGEMENT_INIT_FILENAME;
                if (Utilities.fileExists(rejectIniFile)) {
                    String result = JudgementLoader.loadJudgements(contest, false, cdpConfigDirectory.getAbsolutePath());
                    StaticLog.info(result);
                }
            }

            // Load accounts load file

            String loadAccountsFile = cdpConfigDirectory + File.separator + Constants.ACCOUNTS_LOAD_FILENAME;
            String altFilename = contest.getContestInformation().getOverrideLoadAccountsFilename();
            if (altFilename != null) {
                loadAccountsFile = altFilename;

                /**
                 * If altFilename is not a full/partal path, check whether alt file is under config/
                 */
                if (!Utilities.fileExists(altFilename)) {
                    String altFileInConfig = cdpConfigDirectory + File.separator + altFilename;
                    if (Utilities.fileExists(altFileInConfig)) {
                        loadAccountsFile = altFileInConfig;
                    }
                }

                if (!Utilities.fileExists(loadAccountsFile)) {
                    // if alternate file specifed - bu cannot find file - fatal error.

                    StaticLog.getLog().log(Level.SEVERE, "Missing accounts load file at " + altFilename);
                    throw new FileNotFoundException(altFilename);
                }
            }

            if (Utilities.fileExists(loadAccountsFile)) {
                StaticLog.info("Loading from No accounts load file " + loadAccountsFile);
                loadAccountLoadFile(contest, loadAccountsFile);
            } else {
                StaticLog.getLog().log(Level.FINEST, "No accounts load file found at " + loadAccountsFile + ", ok");
            }
        }

        return contest;
    }

    protected void loadAccountLoadFile(IInternalContest contest, String loadfilename) throws Exception {
        LoadAccounts loader = new LoadAccounts();

        Vector<Account> teams = contest.getAccounts(ClientType.Type.TEAM);
        Account[] teamAccounts = (Account[]) teams.toArray(new Account[teams.size()]);

        Group[] groups = contest.getGroups();

        Account[] accList = loader.fromTSVFileWithNewAccounts(contest, loadfilename, teamAccounts, groups);

        List<Account> newAccounts = new ArrayList<Account>();
        List<Account> updatedAccount = new ArrayList<Account>();

        for (Account account : accList) {
            if (null != contest.getAccount(account.getClientId())) {
                updatedAccount.add(account);
            } else {
                newAccounts.add(account);
            }
        }

        for (Account account : newAccounts) {
            contest.addAccount(account);
        }
        
        for (Account account : updatedAccount) {
            contest.updateAccount(account);
        }
        
        System.out.println("Update " + updatedAccount.size() + " accounts, add " + newAccounts.size() + " accounts from " + loadfilename);
        StaticLog.info("Update " + updatedAccount.size() + " accounts, add " + newAccounts.size() + " accounts from " + loadfilename);

    }

}
