package edu.csus.ecs.pc2.imports.ccs;

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

    public static final String DEFAULT_CONTEST_YAML_FILENAME = "contest.yaml";

    public static final String DEFAULT_PROBLEM_YAML_FILENAME = "problem.yaml";

    public static final String DEFAULT_PROBLEM_SET_YAML_FILENAME = "problemset.yaml";
    
    public static final String DEFAULT_PROBLEM_LATEX_FILENAME = "problem.tex";

    
    // CDP directories
    
    public static final String SUBMISSIONS_DIRNAME = "submissions";
    
    public static final String CONFIG_DIRNAME = "config";


    // Other constants.

    static final String DELIMIT = ":";

    static final String MTSV_OVERRIDE_VALIDATOR_ARGS = "{:problemletter} {:resfile} {:basename} {:timelimit}";

    static final int DEFAULT_TIME_OUT = 30;

    static final String MTSV_PROGRAM_NAME = "mtsv";

    // Section Names
    
    static final String CONTEST_NAME_KEY = "name";

    static final String SHORT_NAME_KEY = "short-name";

    static final String CONTEST_START_TIME = "start-time";

    static final String CONTEST_DURATION = "duration";

    static final String SCOREBOARD_FREEZE = "scoreboard-freeze";

    static final String LANGUAGE_KEY = "languages";

    static final String DEFAULT_CLARS_KEY = "default-clars";

    static final String CLAR_CATEGORIES_KEY = "clar-categories";

    static final String PROBLEMS_KEY = "problemset";

    static final String MANUAL_REVIEW_KEY = "manual-review";
    
    static final String COMPUTER_JUDGING_KEY = "computer-judged";

    static final String ACCOUNTS_KEY = "accounts";

    static final String SITES_KEY = "sites";

    static final String REPLAY_KEY = "replay";

    static final String JUDGE_CONFIG_PATH_KEY = "judge-config-path";

    static final String TIMEOUT_KEY = "timeout";

    static final String LIMITS_KEY = "limits";

    static final String PROBLEM_NAME_KEY = "title";

    static final String PROBLEM_INPUT_KEY = "input";

    static final String AUTO_JUDGE_KEY = "auto-judging";

    static final String INPUT_KEY = "input";

    static final String PROBLEM_LOAD_DATA_FILES_KEY = "load-data-files";

    static final String DEFAULT_VALIDATOR_KEY = "default-validator";

    static final String OVERRIDE_VALIDATOR_KEY = "override-validator";

    static final String VALIDATOR_KEY = "validator";

    static final String USING_PC2_VALIDATOR = "use-internal-validator";

    static final String SEND_PRELIMINARY_JUDGEMENT_KEY = "send-prelim-judgement";

    static final String USE_JUDGE_COMMAND_KEY = "use-judge-cmd";

    static final String JUDGE_EXECUTE_COMMAND_KEY = "judge-exec-cmd";

    static final String JUDGING_TYPE_KEY = "judging-type";

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
            boolean todobool);

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
     * @param sectionLines
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

    void assignJudgingType(String[] yaml, Problem problem, boolean overrideManualReviewFlag);
}
