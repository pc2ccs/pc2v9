// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.core.model.AutoJudgeSetting;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.util.ScoreboardVariableReplacer;

/**
 * Contest Loader interface and Constants.
 * 
 * Constants and methods used in loading YAML into contest/model.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public interface IContestLoader {

    String DEFAULT_CONTEST_YAML_FILENAME = "contest.yaml";

    String DEFAULT_PROBLEM_YAML_FILENAME = "problem.yaml";

    String DEFAULT_PROBLEM_SET_YAML_FILENAME = "problemset.yaml";
    
    String DEFAULT_PROBLEM_LATEX_FILENAME = "problem.tex";
    
    String DEFAULT_ENGLISH_PROBLEM_LATEX_FILENAME = "problem.en.tex";

    String DEFAULT_SYSTEM_YAML_FILENAME = "system.yaml";
    
    // CDP directories
    
    String SUBMISSIONS_DIRNAME = "submissions";
    
    String CONFIG_DIRNAME = "config";

    // Sandbox fields
    
    final String SANDBOX_PROGRAM_NAME_KEY = "sandbox-program-name";

    final String SANDBOX_COMMAND_LINE_KEY = "sandbox-command-line";

    final String SANDBOX_TYPE_KEY = "sandbox-type";

    // Other constants.

    String DELIMIT = ":";

    String MTSV_OVERRIDE_VALIDATOR_ARGS = "{:problemletter} {:resfile} {:basename} {:timelimit}";

    int DEFAULT_TIME_OUT = 30;

    String MTSV_PROGRAM_NAME = "mtsv";

    // CLICS (2022-07) compliant contest.yaml property names - these work for the latest draft (2023-06) as well
    final String CLICS_CONTEST_ID = "id";
    final String CLICS_CONTEST_NAME = "name";
    final String CLICS_CONTEST_START_TIME = "start_time";
    final String CLICS_CONTEST_DURATION = "duration";
    final String CLICS_CONTEST_FREEZE_DURATION = "scoreboard_freeze_duration";
    final String CLICS_CONTEST_SCOREBOARD_TYPE = "scoreboard_type";
    // This is not currently used, but penalty_time SHOULD be used instead of
    // reading it from properties. It is here for completeness, and, it happens
    // to be a required value in the yaml, but we do not enforce that.
    final String CLICS_CONTEST_PENALTY_TIME = "penalty_time";
    
    // Section Names
    
    String CONTEST_NAME_KEY = "name";

    String SHORT_NAME_KEY = "short-name";

    String CONTEST_START_TIME_KEY = "start-time";
    
    String MAX_OUTPUT_SIZE_K_KEY = "max-output-size-K";
    
    String CLICS_MAX_OUTPUT_KEY = "output";
    
    String CLICS_TIME_MULTIPLIER_KEY = "time_multiplier";
    
    String CLICS_TIME_SAFETY_MARGIN_KEY = "time_safety_margin";
    
    final String OUTPUT_PRIVATE_SCORE_DIR_KEY = "output-private-score-dir";

    final String OUTPUT_PUBLIC_SCORE_DIR_KEY = "output-public-score-dir";

    String CONTEST_DURATION_KEY = "duration";
    
    String AUTO_STOP_CLOCK_AT_END_KEY = "auto-stop-clock-at-end";

    String SCOREBOARD_FREEZE_KEY = "scoreboard-freeze";
    
    String SCOREBOARD_FREEZE_LENGTH_KEY = "scoreboard-freeze-length";

    String LANGUAGE_KEY = "languages";

    String DEFAULT_CLARS_KEY = "default-clars";

    String CLAR_CATEGORIES_KEY = "clar-categories";

    /**
     * Name for problem set in contest.yaml
     */
    String PROBLEMS_KEY = "problemset";
    
    /**
     * name for problem set in problemset.yaml
     */
    String PROBLEMSET_PROBLEMS_KEY = "problems";

    String MANUAL_REVIEW_KEY = "manual-review";
    
    String COMPUTER_JUDGING_KEY = "computer-judged";

    String ACCOUNTS_KEY = "accounts";

    String SITES_KEY = "sites";

    String REPLAY_KEY = "replay";

    String JUDGE_CONFIG_PATH_KEY = "judge-config-path";

    String TIMEOUT_KEY = "timeout";
    
    final String MEMORY_LIMIT_IN_MEG_KEY = "memory-limit-in-Meg";
    
    final String MEMORY_LIMIT_CLICS = "memory";
    
    String LIMITS_KEY = "limits";

    String PROBLEM_NAME_KEY = "title";

    String PROBLEM_INPUT_KEY = "input";

    String AUTO_JUDGE_KEY = "auto-judging";

    String CCS_TEST_MODE = "ccs-test-mode";
    
    String LOAD_SAMPLE_JUDGES_DATA = "load-sample-judges-data";
    
    String INPUT_KEY = "input";

    String PROBLEM_LOAD_DATA_FILES_KEY = "load-data-files";
    
    String GROUPS_KEY = "groups";

    String DEFAULT_VALIDATOR_KEY = "default-validator";

    String OVERRIDE_VALIDATOR_KEY = "override-validator";

    String VALIDATOR_KEY = "validator";
    
    String VALIDATOR_FLAGS_KEY = "validator_flags";

    String USING_PC2_VALIDATOR = "use-internal-validator";

    String SEND_PRELIMINARY_JUDGEMENT_KEY = "send-prelim-judgement";
    
    String STOP_ON_FIRST_FAILED_TEST_CASE_KEY = "stop-on-first-failed-test-case";

    String USE_JUDGE_COMMAND_KEY = "use-judge-cmd";

    String JUDGE_EXECUTE_COMMAND_KEY = "judge-exec-cmd";

    String JUDGING_TYPE_KEY = "judging-type";

    String EVENT_FEED_DIRNAME = "eventFeed";

    String EVENT_FEED_XML_FILENAME = "events.xml";

    String USE_JUDGE_CMD_KEY = "use-judge-cmd";

    String INTERPRETED_LANGUAGE_KEY = "interpreted";

    String READ_FROM_STDIN_KEY = "readFromSTDIN";

    /**
     * output validators directory name.
     */
    final String OUTPUT_VALIDATORS = "output_validators";

    
    //keys for YAML entries specifying data for Input Validators:
    String INPUT_VALIDATOR_KEY = "input_validator"; //the section header for Input Validator info
    String DEFAULT_INPUT_VALIDATOR_KEY = "defaultInputValidator"; //the key for specifying the type of Input Validator selected by default
    String CUSTOM_INPUT_VALIDATOR_PROGRAM_NAME_KEY = "customInputValidatorProg";    //custom input validator program name
    String CUSTOM_INPUT_VALIDATOR_COMMAND_LINE_KEY = "customInputValidatorCmd"; //command to invoke custom input validator
    String VIVA_PATTTERN_KEY = "vivaPattern";   //the key for specifying a VIVA pattern in YAML
    String VIVA_PATTERN_FILE_KEY = "vivaPatternFile";   //the key for specifying a file containing a VIVA pattern

    // per problem problem.yaml settings
    String SHOW_OUTPUT_WINDOW = "showOutputWindow";

    String SHOW_COMPARE_WINDOW = "showCompare";

    String SHOW_VALIDATION_RESULTS = "showValidationResults";

    String HIDE_PROBLEM = "hideProblem";

    String USING_CLICS_VALIDATOR = "use-clics-validator";

    String USING_CUSTOM_VALIDATOR = "use-custom-validator";
    
    String USE_CLICS_CUSTOM_VALIDATOR_INTERFACE = "use-clics-custom-validator-interface";
    
    String PC2_EXEC_CMD = "execCmd";
    
    String PC2_COMPILER_CMD = "compilerCmd";
    
    String CCS_LAST_EVENT_ID_KEY = "ccs-last-event-id";

    String CCS_PASSWORD_KEY = "ccs-password";

    String CCS_LOGIN_KEY = "ccs-login";

    String CCS_URL_KEY = "ccs-url";

    String SHADOW_MODE_KEY = "shadow-mode";
    
    String ALLOW_MULTIPLE_TEAM_LOGINS_KEY = "allow-multiple-team-logins";
    
    String LOAD_ACCOUNTS_FILE_KEY = "load-accounts-file";

    /**
     * 
     * @see ScoreboardVariableReplacer#substituteDisplayNameVariables(String, IInternalContest, edu.csus.ecs.pc2.core.model.Account)
     */
    String TEAM_SCOREBOARD_DISPLAY_FORMAT_STRING  = "team-scoreboard-display-format-string";

    Problem addDefaultPC2Validator(Problem problem, int optionNumber);

    void dumpSerialzedFileList(Problem problem, String logPrefixId, SerializedFile[] sfList);

    /**
     * Load contest data from contest.yaml.
     * 
     * @param contest
     * @param directoryName
     *            directory to load files from.
     * @return contest
     * 
     */
    IInternalContest fromYaml(IInternalContest contest, String directoryName);

    /**
     * Load contest, optionally load problem data files.
     * 
     * @param contest
     * @param directoryName
     *            directory to load files from.
     * @param loadDataFileContents
     *            true - load files, false do not load files (files considered external).
     * @return contest
     */
    IInternalContest fromYaml(IInternalContest contest, String directoryName, boolean loadDataFileContents);

    IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String directoryName);

    IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String directoryName, boolean loadDataFileContents);

    AutoJudgeSetting[] getAutoJudgeSettings(String[] yamlLines, Problem[] problems);

    String[] getClarificationCategories(String[] yamlLines);

    String getContestTitle(String contestYamlFilename) throws IOException;

    String[] getFileNames(String directoryName, String string);

    String[] getGeneralAnswers(String[] yamlLines);

    String getJudgesCDPBasePath(String contestYamlFilename) throws IOException;

    Language[] getLanguages(String[] yamlLines);

    String getProblemNameFromLaTex(String filename);

    Problem[] getProblems(String[] contents, int defaultTimeOut);

    Problem[] getProblems(String[] contents, int defaultTimeOut, long defaultMaxOutputSizeInBytes);

    Problem[] getProblems(String[] contents, int defaultTimeOut, boolean loadDataFileContents, String defaultValidatorCommandLine);

    Problem[] getProblems(String[] yamlLines, int seconds, boolean loadDataFileContents, String defaultValidatorCommand, String overrideValidatorCommandLine, boolean overrideUsePc2Validator,
            boolean manualReviewOverride);

    Problem[] getProblems(String[] yamlLines, int seconds, long maxOutputSizeInBytes, boolean loadDataFileContents, String defaultValidatorCommand, String overrideValidatorCommandLine, boolean overrideUsePc2Validator,
            boolean manualReviewOverride);

    Problem[] getProblemsFromLetters(Problem[] contestProblems, String string);

    PlaybackInfo getReplaySettings(String[] yamlLines);

    Site[] getSites(String[] yamlLines);

    /**
     * Are data file contents to ba loaded into configuration?.
     * 
     * @return true if data files are not external
     */
    boolean isLoadProblemDataFiles();

    /**
     * Load CCS data files, and validator into contest.
     * 
     * @param contest
     * @param dataFileBaseDirectory
     *            the directory where the .in and .ans files are found.
     * @param problem
     * @param problemDataFiles
     * 
     */
    Problem loadCCSProblemFiles(IInternalContest contest, String dataFileBaseDirectory, Problem problem, ProblemDataFiles problemDataFiles);

    String[] loadFileWithIncludes(String dirname, String filename) throws IOException;

    String[] loadGeneralClarificationAnswers(String[] yamlLines);

    void loadPc2ProblemFiles(IInternalContest contest, String dataFileBaseDirectory, Problem problem, ProblemDataFiles problemDataFiles2, String dataFileName, String answerFileName);

    void loadProblemInformationAndDataFiles(IInternalContest contest, String baseDirectoryName, Problem problem, boolean overrideUsePc2Validator);

    void loadProblemInformationAndDataFiles(IInternalContest contest, String baseDirectoryName, Problem problem, boolean overrideUsePc2Validator, boolean overrideManualReview);

    void setLoadProblemDataFiles(boolean loadProblemDataFiles);

    // TODO REFACTOR move this into StringUtilities
    String unquote(String input, String string);

    /**
     * Directory for judge's input/answer files.
     * 
     * @param yamlDirectory
     *            directory where problem dir/files are located.
     * @param problem
     * @return directory for judge's input/answer files.
     */
    String getCCSDataFileDirectory(String yamlDirectory, Problem problem);

    /**
     * Return directory for judge's input/answer files.
     * 
     * @param yamlDirectory
     *            directory where problem dir/files are located.
     * @param shortDirName
     *            problem short name.
     * @return directory for judge's input/answer files.
     */
    String getCCSDataFileDirectory(String yamlDirectory, String shortDirName);

    // TODO REFACTOR move this into StringUtilities
    boolean getBooleanValue(String string, boolean defaultBoolean);

    /**
     * Load contest with settings from file/CDP.
     * @param contest
     * @param entry can be CDP directory, contest.yaml file, or other initialize file.
     * @throws Exception 
     */
    IInternalContest initializeContest (IInternalContest contest, File entry) throws Exception;

    /**
     * Locates the CDP config directory.
     * 
     * Attempts to find CDP config directory at:
     * <li> current directory
     * <li> parent directory
     * <li> samps/contests/<BASENAME> directory, ex "mini" finds samps/contests/<BASENAME>/config
     * 
     * If no directory found, returns null.
     * 
     * @param entry
     *            a directory, filename or PC^2 sample CCS contest directory name.
     * @return null or the location of the CDP config/ directory.
     */
    File findCDPConfigDirectory(File entry);
}
