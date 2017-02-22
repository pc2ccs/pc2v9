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

/**
 * Contest Loader interface and Constants.
 * 
 * Constants and methods used in loading YAML.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public interface IContestLoader {

    // default filenames

    String DEFAULT_CONTEST_YAML_FILENAME = "contest.yaml";

    String DEFAULT_PROBLEM_YAML_FILENAME = "problem.yaml";

    String DEFAULT_PROBLEM_SET_YAML_FILENAME = "problemset.yaml";
    
    String DEFAULT_PROBLEM_LATEX_FILENAME = "problem.tex";

    
    // CDP directories
    
    String SUBMISSIONS_DIRNAME = "submissions";
    
    String CONFIG_DIRNAME = "config";


    // Other constants.

    String DELIMIT = ":";

    String MTSV_OVERRIDE_VALIDATOR_ARGS = "{:problemletter} {:resfile} {:basename} {:timelimit}";

    int DEFAULT_TIME_OUT = 30;

    String MTSV_PROGRAM_NAME = "mtsv";

    // Section Names
    
    String CONTEST_NAME_KEY = "name";

    String SHORT_NAME_KEY = "short-name";

    String CONTEST_START_TIME_KEY = "start-time";

    String CONTEST_DURATION_KEY = "duration";

    String SCOREBOARD_FREEZE_KEY = "scoreboard-freeze";

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

    String LIMITS_KEY = "limits";

    String PROBLEM_NAME_KEY = "title";

    String PROBLEM_INPUT_KEY = "input";

    String AUTO_JUDGE_KEY = "auto-judging";

    String INPUT_KEY = "input";

    String PROBLEM_LOAD_DATA_FILES_KEY = "load-data-files";

    String DEFAULT_VALIDATOR_KEY = "default-validator";

    String OVERRIDE_VALIDATOR_KEY = "override-validator";

    String VALIDATOR_KEY = "validator";
    
    String VALIDATOR_FLAGS_KEY = "validator_flags";

    String USING_PC2_VALIDATOR = "use-internal-validator";

    String SEND_PRELIMINARY_JUDGEMENT_KEY = "send-prelim-judgement";

    String USE_JUDGE_COMMAND_KEY = "use-judge-cmd";

    String JUDGE_EXECUTE_COMMAND_KEY = "judge-exec-cmd";

    String JUDGING_TYPE_KEY = "judging-type";

    String EVENT_FEED_DIRNAME = "eventFeed";

    String EVENT_FEED_XML_FILENAME = "events.xml";

    String USE_JUDGE_CMD_KEY = "use-judge-cmd";

    String INTERPRETED_LANGUAGE_KEY = "interpreted";

    String READ_FROM_STDIN_KEY = "readFromSTDIN";

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

    Problem[] getProblems(String[] contents, int defaultTimeOut, boolean loadDataFileContents, String defaultValidatorCommandLine);

    Problem[] getProblems(String[] yamlLines, int seconds, boolean loadDataFileContents, String defaultValidatorCommand, String overrideValidatorCommandLine, boolean overrideUsePc2Validator,
            boolean manualReviewOverride);

    Problem[] getProblemsFromLetters(Problem[] contestProblems, String string);

    PlaybackInfo getReplaySettings(String[] yamlLines);

    Site[] getSites(String[] yamlLines);

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

    boolean getBooleanValue(String string, boolean defaultBoolean);

    void assignDefaultJudgingTypes(String[] yaml, Problem problem, boolean overrideManualReviewFlag);
    
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
     * @param entry
     *            a directory, filename or PC^2 sample CCS contest directory name.
     * @return location for file or null if not found.
     */
    File findCDPConfigDirectory(File entry);
}
