// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResult;
import edu.csus.ecs.pc2.core.model.inputValidation.VivaInputValidatorSettings;
import edu.csus.ecs.pc2.ui.EditProblemSandboxPane;
import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidatorSettings;
import edu.csus.ecs.pc2.validator.customValidator.CustomValidatorSettings;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorSettings;

/**
 * Problem Definition.
 *
 * This contains settings for a problem.  Data files
 * are not in this class, data files are in the {@link edu.csus.ecs.pc2.core.model.ProblemDataFiles}
 * class.
 *
 * @see edu.csus.ecs.pc2.core.list.ProblemList
 * @see edu.csus.ecs.pc2.core.model.ProblemDataFiles
 *
 * @author pc2@ecs.csus.edu
 */
public class Problem implements IElementObject {

    private static final long serialVersionUID = 1708763261096488240L;

    public static final int DEFAULT_TIMEOUT_SECONDS = 10;

    /**
     * The default value for the per-problem maximum output size.
     * Zero indicates no per-problem limit has been set (and therefore the current
     * global setting should be used).
     */
    public static final int DEFAULT_MAX_OUTPUT_FILE_SIZE_KB = 0;


    public static final int DEFAULT_MEMORY_LIMIT_MB = 0 ;   //zero memory limit = "none", i.e. the problem can use all available memory

    public static final SandboxType DEFAULT_SANDBOX_TYPE = SandboxType.NONE ;

    /**
     * Problem title.
     */
    private String displayName = null;

    /**
     * Unique id for problem.
     */
    private ElementId elementId = null;

    /**
     * Sequence number (rank) for problem.
     */
    private int number;

    /**
     * Judge's data file name.
     *
     * This is the name of the file that the submitted run will read from.
     */
    private String judgesInputDataFileName = null;

    /**
     * Judge's answer file name.
     *
     * This is the name of the file that the validator will read/use to compare against the submitted run output.
     */
    private String answerFileName = null;

    /**
     * List of judge's file names, for multiple test cases.
     *
     */
    private String [] testCaseDataFilenames = new String[0];

    /**
     * List of judge's answer file names, for multiple test cases.
     */
    private String [] testCaseAnswerFilenames = new String[0];;

    /**
     * Whether or not the problem should be shown to teams.
     */
    private boolean active = true;

    /**
     * Input is from stdin.
     */
    private boolean readInputDataFromSTDIN = false;

    /**
     * Seconds per problem run.
     */
    private int timeOutInSeconds = DEFAULT_TIMEOUT_SECONDS;
    
    /**
     * Maximum output allowed to be produced by a solution for this problem.
     * Note that a value of zero indicates that this problem does not have its own
     * problem-specific limit and that the global (contest-wide) limit should be used instead.
     */
    private long maxOutputSizeKB = DEFAULT_MAX_OUTPUT_FILE_SIZE_KB ;

    /**
     * Maximum output allowed to be produced by a solution for this problem.
     * Note that a value of zero indicates that this problem does not have its own
     * problem-specific limit and that the global (contest-wide) limit should be used instead.
     */
    private long maxOutputSizeKB = DEFAULT_MAX_OUTPUT_FILE_SIZE_KB ;

    /**
     * This enum defines the types of Output Validators which a Problem can have.
     */
    public enum VALIDATOR_TYPE {
        /**
         * The Problem has no associated Output Validator; it is not a Validated Problem.
         */
        NONE,
        /**
         * The Problem uses the PC2 Validator, also known as the "Internal" Validator.
         */
        PC2VALIDATOR,
        /**
         * The Problem uses the PC2 implementation of the CLICS Validator.
         */
        CLICSVALIDATOR,
        /**
         * The Problem uses a Custom (user-provided) Output Validator.
         */
        CUSTOMVALIDATOR
        }

    //The type of output validator associated with this Problem.
    private VALIDATOR_TYPE validatorType = VALIDATOR_TYPE.NONE ;

    //the settings for each possible type of output validator used by the problem
    private PC2ValidatorSettings pc2ValidatorSettings ;
    private ClicsValidatorSettings clicsValidatorSettings ;
    private CustomValidatorSettings customValidatorSettings ;

    /**
     * If true, when loading data sets, load sample data sets before loading other judge's test data sets.
     */
    private boolean loadDataFilesSamplesFirst = false;

    /**
     * If true, when loading data sets, load sample data sets before loading other judge's test data sets.
     */
    private boolean loadDataFilesSamplesFirst = false;
    
    /**
     * This enum defines the types of Input Validators which a Problem can have.
     *
     * Note that it is possible to use more than one Input Validator to check a problem's data files;
     * however, each problem when it is saved on the PC2 server has a single "current Input Validator"
     * type associated with it at that time - specifically, the most recently-selected Input Validator type.
     *
     * Note also that it is possible to SELECT an Input Validator (thus making that Input Validator type
     * the "currently selected Input Validator type") without actually RUNNING the selected Input Validator.
     * Clients should use {@link #getCurrentInputValidatorType()} to determine the most recently selected
     * Input Validator type.   Methods {@link #isProblemHasCustomInputValidator()} and
     * {@link #isProblemHasVivaInputValidatorPattern()} can be used to determine whether the problem actually
     * has the corresponding Input Validator type associated with it (independent of whether that is the currently
     * SELECTED Input Validator).  Methods {@link #isVivaInputValidatorHasBeenRun()} and
     * {@link #isCustomInputValidatorHasBeenRun()} can be used to determine whether the corresponding Input Validator
     * has actually been executed.
     *
     * @see #getCurrentInputValidatorType()
     * @see #isProblemHasCustomInputValidator()
     * @see #isProblemHasVivaInputValidatorPattern()
     * @see #isVivaInputValidatorHasBeenRun()
     * @see #isCustomInputValidatorHasBeenRun()
     *
     * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
     */
    public enum INPUT_VALIDATOR_TYPE {
        /**
         * The Problem has no associated Input Validator.
         */
        NONE,
        /**
         * The Problem uses the VIVA Input Validator.
         */
        VIVA,
        /**
         * The Problem uses a Custom (user-provided) Input Validator.
         */
        CUSTOM
        }

    /**
     * This enum defines the possible Input Validation Status values which a problem may have.
     * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
     *
     */
    public enum InputValidationStatus {
        /**
         * Nothing is known about the Input Validation Status for this problem.
         */
        UNKNOWN,
        /**
         * An Input Validator has been defined for the problem, but no Input Validator has been run on the data files in this problem.
         */
        NOT_TESTED,
        /**
         * An Input Validator has been run on the data files in this problem and they all passed.
         */
        PASSED,
        /**
         * An Input Validator has been run on the data files in this problem and one or more data files failed.
         */
        FAILED,
        /**
         * An internal error occurred. One reason for this could be that an attempt was made to run an Input Validator
         * but it failed.
         */
        ERROR
    }

    /**
     * The type of Input Validator currently associated with the problem.
     */
    private INPUT_VALIDATOR_TYPE currentInputValidatorType = INPUT_VALIDATOR_TYPE.NONE;

    //custom input validator settings - only relevant if the problem was saved with a Custom Input Validator
    private boolean problemHasCustomInputValidator = false;
    private boolean customInputValidatorHasBeenRun = false;
//    private String customInputValidatorProgramName = "";  //program name should be determined by the Custom Input Validator Serialized File
    private String customInputValidatorCommandLine = "";
//    private String customInputValidatorFilesOnDiskFolderName = "";
    private SerializedFile customInputValidatorSerializedFile = null;
    private InputValidationStatus customInputValidationStatus = InputValidationStatus.UNKNOWN;
    private Vector<InputValidationResult> customInputValidationResults = null;

    //VIVA Input validator settings - only relevant if the problem was saved with Viva Input Validator Settings
    private VivaInputValidatorSettings vivaSettings = null;


    /**
     * Use international judgement method.
     */
    private boolean internationalJudgementReadMethod = true;

    /**
     * This is the command executed before the run is executed.
     */
    private String executionPrepCommand = null;

    /**
     * Display validation output window to judges.
     */
    private boolean showValidationToJudges = false;

    /**
     * Hide Output window from Judges.
     */
    private boolean hideOutputWindow = false;

    /**
     * Show PC2 Compare Window?.
     *
     */
    private boolean showCompareWindow = false;

    /**
     * should the problem be "Auto Judged", this will require a validator be defined.
     */
    private boolean computerJudged = false;

    /**
     * Should the problem be send to a human for review after it has been autojudged.
     * only used if computerJudged is TRUE
     */
    private boolean manualReview = false;

    /**
     * should a team be notified of the Computer Judgement (immediately)
     * only used if manualReview is TRUE
     */
    private boolean prelimaryNotification = false;

    private String shortName = "";

    private String letter = null;

    private String colorName;

    private String colorRGB;

    /**
     * Files are not stored on pc2 server, they are at an external location
     * pointed to by {@link #getDataLoadYAMLPath()}.
     */
    private boolean usingExternalDataFiles = false;

    /**
     * Base location where external data files are stored.
     */
    private String externalDataFileLocation = null;

    /**
     * Problem State.
     *
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    public enum State {
        /**
         * Active.
         */
        ENABLED,
        /**
         * Not accepting runs
         */
        PAUSED,
        /**
         * Not accepting runs.
         */
        DISABLED
    }

    private State state = State.ENABLED;

    /**
     * A flag indicating whether, during execution of a submission for this problem,
     * execution should be terminated on the first failed test case (or rather, all test cases
     * should be executed even when some have failed.)
     */
    private boolean stopOnFirstFailedTestCase = false;

    /**
     * A list of groups that can view/use this problem.
     *
     * One use is to limit which teams can view a group.
     */
    private List<Group> groups = new ArrayList<Group>();

    /**
     * Fields related to Sandbox support.
     */
    public enum SandboxType {
        /**
         * No sandbox being used.
         */
        NONE,
        /**
         * Using the PC2 Internal sandbox.
         */
        PC2_INTERNAL_SANDBOX,
        /**
         * Using an external (user-defined) sandbox.
         */
        EXTERNAL_SANDBOX
    }

    private int memoryLimitMB = DEFAULT_MEMORY_LIMIT_MB;
    private SandboxType sandboxType = DEFAULT_SANDBOX_TYPE;
    private String sandboxCmdLine = null;
    private String sandboxProgramName = null;

    /**
     * For interactive problems - this is a read-only property, so, no mutator
     */
    private static final String interactiveCommandLine = Constants.PC2_INTERACTIVE_COMMAND_LINE;

    /**
     * Fields related to Sandbox support.
     */
    public enum SandboxType {
        /**
         * No sandbox being used.
         */
        NONE, 
        /**
         * Using the PC2 Internal sandbox.
         */
        PC2_INTERNAL_SANDBOX, 
        /**
         * Using an external (user-defined) sandbox.
         */
        EXTERNAL_SANDBOX 
    }
    
    private int memoryLimitMB = DEFAULT_MEMORY_LIMIT_MB;
    private SandboxType sandboxType = DEFAULT_SANDBOX_TYPE;
    private String sandboxCmdLine = null;
    private String sandboxProgramName = null;
    
    /**
     * For interactive problems - this is a read-only property, so, no mutator
     */
    private static final String interactiveCommandLine = Constants.PC2_INTERACTIVE_COMMAND_LINE;
    
    /**
     * Create a problem with the display name.
     *
     * @param displayName
     */
    public Problem(String displayName) {
        super();
        this.displayName = displayName;
        elementId = new ElementId(displayName);
        setSiteNumber(0);
        this.pc2ValidatorSettings = new PC2ValidatorSettings();
        this.clicsValidatorSettings = new ClicsValidatorSettings();
        this.customValidatorSettings = new CustomValidatorSettings();
        this.customInputValidationStatus = InputValidationStatus.UNKNOWN;
        this.customInputValidationResults = new Vector<InputValidationResult>();
        this.vivaSettings = new VivaInputValidatorSettings();
        updateSandboxInfo();
    }

    public Problem copy(String newDisplayName) {
        Problem clone = new Problem(newDisplayName);
        // inherited field
        clone.setSiteNumber(getSiteNumber());

        // local fields
        clone.setDisplayName(newDisplayName);
        // TODO is number really used?
        clone.setNumber(getNumber());
        // TODO FATAL files need the corresponding ProblemDataFile populated...
        clone.setDataFileName(StringUtilities.cloneString(judgesInputDataFileName));
        clone.setAnswerFileName(StringUtilities.cloneString(answerFileName));
        clone.setActive(isActive());
        clone.setReadInputDataFromSTDIN(isReadInputDataFromSTDIN());
        clone.setTimeOutInSeconds(getTimeOutInSeconds());
        clone.setMaxOutputSizeKB(getMaxOutputSizeKB());

        //output validator settings
        clone.setValidatorType(this.getValidatorType());

        if (this.getPC2ValidatorSettings()!=null) {
            clone.setPC2ValidatorSettings(this.getPC2ValidatorSettings().clone());
        } else {
            clone.setPC2ValidatorSettings(null);
        }
        if (this.getClicsValidatorSettings()!=null) {
            clone.setCLICSValidatorSettings(this.getClicsValidatorSettings().clone());
        } else {
            clone.setCLICSValidatorSettings(null);
        }
        if (this.getCustomOutputValidatorSettings()!=null) {
            clone.setCustomOutputValidatorSettings(this.getCustomOutputValidatorSettings().clone());
        } else {
            clone.setCustomOutputValidatorSettings(null);
        }

        //input validator settings
        clone.setCurrentInputValidatorType(this.getCurrentInputValidatorType());

        clone.setProblemHasCustomInputValidator(this.isProblemHasCustomInputValidator());
        clone.setCustomInputValidatorHasBeenRun(this.isCustomInputValidatorHasBeenRun());
        clone.setCustomInputValidationStatus(this.getCustomInputValidationStatus());
        clone.setCustomInputValidatorCommandLine(StringUtilities.cloneString(this.getCustomInputValidatorCommandLine()));
        clone.setCustomInputValidatorFile(this.getCustomInputValidatorSerializedFile());
//        clone.setInputValidatorFilesOnDiskFolder(this.getInputValidatorFilesOnDiskFolder());

        //This statement is commented out because there is no longer a separate "problemHasVivaInputValidatorPattern" flag in the Problem class;
        // having a Viva pattern (or not) is determined by the value in the Pattern field in the (VivaInputValidatorSettings for the) Problem.
        // This was done to avoid the possibility of an "invalid state" where a user sets "problemHasVivaInputValidator" to (say) false after
        // having set a non-zero-length pattern in the Problem.
        //        clone.setProblemHasVivaInputValidatorPattern(this.isProblemHasVivaInputValidatorPattern());
        clone.setVivaInputValidatorHasBeenRun(this.isVivaInputValidatorHasBeenRun());
        clone.setVivaInputValidationStatus(this.getVivaInputValidationStatus());
        clone.setVivaInputValidatorPattern(this.getVivaInputValidatorPattern());

        //input validator results (which might be empty)
        Iterable <InputValidationResult> inputVResults = this.getVivaInputValidatorResults();
        for (InputValidationResult ivr : inputVResults) {
            clone.addVivaInputValidationResult(ivr);
        }
        inputVResults = this.getCustomInputValidatorResults();
        for (InputValidationResult ivr : inputVResults) {
            clone.addCustomInputValidationResult(ivr);
        }

        clone.setInternationalJudgementReadMethod(isInternationalJudgementReadMethod());

        // TODO Implement Commands to be executed before a problem is run
        // private String executionPrepCommand = "";
        // private SerializedFile executionPrepFile;

        clone.setShowValidationToJudges(isShowValidationToJudges());
        clone.setHideOutputWindow(isHideOutputWindow());
        clone.setShowCompareWindow(isShowCompareWindow());
        clone.setComputerJudged(isComputerJudged());
        clone.setManualReview(isManualReview());
        clone.setPrelimaryNotification(isPrelimaryNotification());
        clone.letter = StringUtilities.cloneString(letter);
        clone.shortName = StringUtilities.cloneString(shortName);

        clone.externalDataFileLocation = StringUtilities.cloneString(getExternalDataFileLocation());
        clone.usingExternalDataFiles = usingExternalDataFiles;

        if (getNumberTestCases() > 1){
            for (int i = 0 ; i < getNumberTestCases(); i++){
                String datafile = StringUtilities.cloneString(getDataFileName(i + 1));
                String answerfile = StringUtilities.cloneString(getAnswerFileName(i + 1));
                clone.addTestCaseFilenames(datafile, answerfile);
            }
        }

        clone.setColorName(getColorName());
        clone.setColorRGB(getColorRGB());

        for (Group group : groups) {
            clone.addGroup(group);
        }

        clone.setSandboxType(this.getSandboxType());
        clone.setMemoryLimitMB(this.getMemoryLimitMB());

        return clone;
    }


    /**
     * @see Object#equals(java.lang.Object).
     */
    @Override
    public boolean equals(Object obj) {
        if (this==obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Problem) {
            Problem otherProblem = (Problem) obj;
            return elementId.equals(otherProblem.elementId);
        } else {
            throw new ClassCastException("expected a Problem found: " + obj.getClass().getName());
        }
    }


    /**
     * Output the title for the problem.
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Output details of the problem.
     */
    public String toStringDetails() {
        String retStr = "Problem[";

        //basic configuration settings
        retStr += "displayName=" + displayName;
        retStr += "; elementId=" + elementId;
        retStr += "; number=" + number;
        retStr += "; dataFileName=" + judgesInputDataFileName;
        retStr += "; answerFileName=" + answerFileName;
        retStr += "; testCaseDataFilenames=" + testCaseDataFilenames;
        retStr += "; testCaseAnswerFilenames=" + testCaseAnswerFilenames;
        retStr += "; active=" + active;
        retStr += "; readInputDataFromSTDIN=" + readInputDataFromSTDIN;
        retStr += "; timeOutInSeconds=" + timeOutInSeconds;

        //output validator settings
        boolean validatedProblem = getValidatorType()==VALIDATOR_TYPE.NONE;
        retStr += "; validatedProblem=" + validatedProblem;
        retStr += "; validatorType=" + getValidatorType();
        retStr += "; pc2ValidatorSettings=" + getPC2ValidatorSettings();
        retStr += "; clicsValidatorSettings=" + getClicsValidatorSettings();
        retStr += "; customValidatorSettings=" + getCustomOutputValidatorSettings();

        //input validator settings
        retStr += "; currentInputValidatorType=" + getCurrentInputValidatorType();
        retStr += "; problemHasVivaInputValidatorPattern=" + isProblemHasVivaInputValidatorPattern();
        retStr += "; vivaInputValidationStatus=" + getVivaInputValidationStatus();
        retStr += "; problemHasCustomInputValidator=" + isProblemHasCustomInputValidator();
        retStr += "; customInputValidationStatus=" + getCustomInputValidationStatus();

        SerializedFile customInputValidatorFile = getCustomInputValidatorSerializedFile();
        String customInputValidatorName = "";
        if (customInputValidatorFile!=null) {
            customInputValidatorName = customInputValidatorFile.getName();
        }
        retStr += "; customInputValidatorProgramName=" + customInputValidatorName;

        retStr += "; customInputValidatorCommandLine=" + customInputValidatorCommandLine;

        //misc additional settings
        retStr += "; internationalJudgementReadMethod=" + internationalJudgementReadMethod;
        retStr += "; executionPrepCommand=" + executionPrepCommand;
        retStr += "; showValidationToJudges=" + showValidationToJudges;
        retStr += "; hideOutputWindow=" + hideOutputWindow;
        retStr += "; showCompareWindow=" + showCompareWindow;
        retStr += "; computerJudged=" + computerJudged;
        retStr += "; manualReview=" + manualReview;
        retStr += "; prelimaryNotification=" + prelimaryNotification;
        retStr += "; shortName=" + shortName;
        retStr += "; letter=" + letter;
        retStr += "; colorName=" + colorName;
        retStr += "; colorRGB=" + colorRGB;
        retStr += "; usingExternalDataFiles=" + usingExternalDataFiles;
        retStr += "; externalDataFileLocation=" + externalDataFileLocation;
        retStr += "; state=" + state;

        retStr += "; sandboxType=" + this.getSandboxType();
        retStr += "; sandboxCmdLine=" + this.getSandboxCmdLine();
        retStr += "; sandboxProgramName=" + this.getSandboxProgramName();
        retStr += "; memoryLimit=" + this.getMemoryLimitMB();

        retStr += "; interactiveCommandLine=" + this.getInteractiveCommandLine();

        retStr += "]";
        return retStr;
    }

    /**
     * @return Returns the elementId.
     */
    @Override
    public ElementId getElementId() {
        return elementId;
    }

    /**
     * @return Returns the displayName.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName
     *            The displayName to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Return whether is active (is not hidden).
     * @return true if active, false if hidden/deleted.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return Returns the answerFileName.
     */
    public String getAnswerFileName() {
        return answerFileName;
    }

    public String getAnswerFileName(int testCaseNumber) {
        if (testCaseNumber == 1 && testCaseAnswerFilenames.length == 0){
            return answerFileName;
        }
        return testCaseAnswerFilenames[testCaseNumber - 1];
    }


    /**
     * @return Returns the dataFileName.
     */
    public String getDataFileName() {
        return judgesInputDataFileName;
    }

    /**
     * Get test case data file name.
     *
     * Test case numbers start at 1.
     *
     * @return returns data file name for test case.
     */
    public String getDataFileName(int testCaseNumber) {
        if (testCaseNumber == 1 && testCaseDataFilenames.length == 0){
            return judgesInputDataFileName;
        }
        return testCaseDataFilenames[testCaseNumber - 1];
    }


    /**
     * @return Returns the internationalJudgementReadMethod flag
     */
    public boolean isInternationalJudgementReadMethod() {
        return internationalJudgementReadMethod;
    }

    /**
     * @return Returns the readInputDataFromSTDIN flag
     */
    public boolean isReadInputDataFromSTDIN() {
        return readInputDataFromSTDIN;
    }

    /**
     * @return Returns the showValidationToJudges flag
     */
    public boolean isShowValidationToJudges() {
        return showValidationToJudges;
    }

    /**
     * @return Returns the timeOutInSeconds
     */
    public int getTimeOutInSeconds() {
        return timeOutInSeconds;
    }

    /**
     * Returns the problem-specific maximum allowed output file size, in KB.
     * A returned value of zero indicates that the problem has no problem-specific output size limit,
     * in which case the value of the global output size limit should be used instead.
     *
     * @return the problem-specific maximum allowed output size limit in KB.
     */
    public long getMaxOutputSizeKB() {
        return maxOutputSizeKB;
    }

    /**
     * Set the maximum output size (in KB) allowed by this problem.
     * A value of zero (which is the default) indicates that no problem-specific output size limit
     * has been set and that the global value (as returned by {@link IInternalContest#getContestInformation()}) should be used.
     *
     * @param maxOutputSizeKB the maximum output size, in KB, to set for this problem.
     */
    public void setMaxOutputSizeKB(long maxOutputSizeKB) {
        this.maxOutputSizeKB = maxOutputSizeKB;
    }

    /**
     * Returns the state variable indicating what type of Validator this Problem is using.
     * The returned value will be an element of the enumerated type {@link edu.csus.ecs.pc2.core.Problem.VALIDATOR_TYPE};
     * note that this enumeration includes "NONE" to indicate that a Problem has no Validator attached.
     *
     * @see {@link edu.csus.ecs.pc2.core.Problem.VALIDATOR_TYPE}
     * @see {@link #isValidatedProblem()}
     */
    public VALIDATOR_TYPE getValidatorType() {
        return this.validatorType;
    }

   /**
     * Sets the state variable indicating what type of Validator this problem is using.
     * Note that one possible value of this variable is "NONE", indicating the Problem is not validated.
     *
     * @see #isValidatedProblem()
     *
     * @param valType a {@link edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE} indicating the
     *              type of validator used by this Problem
     */
    public void setValidatorType(VALIDATOR_TYPE valType) {
        this.validatorType = valType;
    }
    /**
     * Returns whether the Problem is using the PC2Validator (as opposed to a Custom Validator,
     * the CLICS Validator, or no Validator).
     *
     * @return true if the Problem is using the PC2Validator
     */
    public boolean isUsingPC2Validator() {
        return getValidatorType()==VALIDATOR_TYPE.PC2VALIDATOR;
    }

    /**
     * Returns whether this Problem is using the CLICS Validator (as opposed to a Custom Validator,
     * the  PC2Validator, or no Validator).
     *
     * @return true if the Problem is using the CLICS Validator
     */
    public boolean isUsingCLICSValidator() {
        return getValidatorType()==VALIDATOR_TYPE.CLICSVALIDATOR;
    }

    /**
     * Returns whether this Problem is using a Custom (user-supplied) Validator
     * (as opposed to the CLICS Validator, the PC2Validator, or no Validator).
     *
     * @return true if the Problem is using a Custom validator
     */
    public boolean isUsingCustomValidator() {
        return getValidatorType()==VALIDATOR_TYPE.CUSTOMVALIDATOR;
    }

    /**
     * @return whether this Problem has a validator or not.
     */
    public boolean isValidatedProblem() {
        return ! (getValidatorType()==VALIDATOR_TYPE.NONE);
    }

    /**
     * Returns the Output Validator Command Line associated with this Problem, if the Problem is using an Output Validator;
     * returns null otherwise.
     *
     * @return the validatorCommandLine for the Problem's output validator,
     *                  or null if the Problem is not using an Output Validator.
     *
     * @throws {@link RuntimeException} if the Problem is marked as using an Output Validator but no corresponding Validator
     *              Settings could be found.
     */
    public String getOutputValidatorCommandLine() {
        if (!isValidatedProblem()) {
            return null;
        }

        //search for ValidatorSettings for the currently-specified Validator; if found, return the ValidatorCommandLine
        // from those Settings
        String validatorCommandLine = null;
        boolean found = false;
        if (isUsingPC2Validator()) {
            if (getPC2ValidatorSettings()!=null) {
                validatorCommandLine = getPC2ValidatorSettings().getValidatorCommandLine();
                found = true;
            }
        } else if (isUsingCLICSValidator()) {
            if (getClicsValidatorSettings()!=null) {
                validatorCommandLine = getClicsValidatorSettings().getValidatorCommandLine();
                found = true;
            }
        } else if (isUsingCustomValidator()) {
            if (getCustomOutputValidatorSettings()!=null) {
                validatorCommandLine = getCustomOutputValidatorSettings().getCustomValidatorCommandLine();
                found = true;
            }
        }

        if (!found) {
            throw new RuntimeException("getValidatorCommandLine(): unable to locate Settings for currently-specified Validator '"
                    + getValidatorType() + "'");
        } else {
            return validatorCommandLine;
        }
    }

    /**
     * Sets the Output Validator Command Line associated with the type of Output Validator configured for this Problem.
     * Note that this Problem class does not maintain a separate "Output Validator Command Line" field;
     * rather, the current Output Validator Command Line is always stored within a "Settings" object
     * corresponding to the currently-assigned Validator type.
     *
     * @param commandLine the new command line for the currently-specified Output Validator type associated with the Problem
     *
     * @see PC2ValidatorSettings
     * @see ClicsValidatorSettings
     * @see CustomValidatorSettings
     *
     * @throws {@link RuntimeException} if the Problem is not marked as using a Validator, or is marked as using a Validator
     *           but no corresponding Validator Settings object could be found.
     */
    public void setOutputValidatorCommandLine(String commandLine) {

        if (!isValidatedProblem()) {
            throw new RuntimeException("setValidatorCommandLine(): no Validator configured for Problem");
        }

        //search for ValidatorSettings for the currently-specified Validator; if found, set the ValidatorCommandLine
        // into those Settings
        boolean found = false;
        if (isUsingPC2Validator()) {
            if (getPC2ValidatorSettings()!=null) {
                getPC2ValidatorSettings().setValidatorCommandLine(commandLine);
                found = true;
            }
        } else if (isUsingCLICSValidator()) {
            if (getClicsValidatorSettings()!=null) {
                getClicsValidatorSettings().setValidatorCommandLine(commandLine);
                found = true;
            }
        } else if (isUsingCustomValidator()) {
            if (getCustomOutputValidatorSettings()!=null) {
                getCustomOutputValidatorSettings().setValidatorCommandLine(commandLine);
                found = true;
            }
        }

        if (!found) {
            throw new RuntimeException("setValidatorCommandLine(): unable to locate Settings for currently-specified Validator");
        }
    }

    /**
     * @param active
     *            The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @param answerFileName
     *            The answerFileName to set.
     */
    public void setAnswerFileName(String answerFileName) {
        this.answerFileName = answerFileName;
    }

    /**
     * @param dataFileName
     *            The dataFileName to set.
     */
    public void setDataFileName(String dataFileName) {
        this.judgesInputDataFileName = dataFileName;
    }

    /**
     * @param elementId
     *            The elementId to set.
     */
    public void setElementId(ElementId elementId) {
        this.elementId = elementId;
    }

    /**
     * @param internationalJudgementReadMethod
     *            The internationalJudgementReadMethod to set.
     */
    public void setInternationalJudgementReadMethod(boolean internationalJudgementReadMethod) {
        this.internationalJudgementReadMethod = internationalJudgementReadMethod;
    }

    /**
     * @param readInputDataFromSTDIN
     *            The readInputDataFromSTDIN to set.
     */
    public void setReadInputDataFromSTDIN(boolean readInputDataFromSTDIN) {
        this.readInputDataFromSTDIN = readInputDataFromSTDIN;
    }

    /**
     * @param showValidationToJudges
     *            The showValidationToJudges to set.
     */
    public void setShowValidationToJudges(boolean showValidationToJudges) {
        this.showValidationToJudges = showValidationToJudges;
    }

    /**
     * @param timeOutInSeconds
     *            The timeOutInSeconds to set.
     */
    public void setTimeOutInSeconds(int timeOutInSeconds) {
        this.timeOutInSeconds = timeOutInSeconds;
    }

    /**
     * @return Returns the hideOutputWindow.
     */
    public boolean isHideOutputWindow() {
        return hideOutputWindow;
    }

    /**
     * @param hideOutputWindow
     *            The value to which the hideOutputWindow should be set.
     */
    public void setHideOutputWindow(boolean hideOutputWindow) {
        this.hideOutputWindow = hideOutputWindow;
    }

    /**
     * Returns the Output Validator Program Name if the Problem has an Output Validator attached; otherwise returns null.
     *
     * @return the validatorProgramName if there is an Output Validator for the Problem, or null if not
     *
     * @throws {@link RuntimeException} if the Problem is marked as having an Output Validator but no Validator Settings could be found
     */
    public String getOutputValidatorProgramName() {

        if (!isValidatedProblem()) {
            return null;
        }

        // search for ValidatorSettings for the currently-specified Validator; if found, return the ValidatorProgramName
        // from those Settings
        String validatorProgName = null;
        boolean found = false;
        if (isUsingPC2Validator()) {
            if (getPC2ValidatorSettings() != null) {
                validatorProgName = getPC2ValidatorSettings().getValidatorProgramName();
                found = true;
            }
        } else if (isUsingCLICSValidator()) {
            if (getClicsValidatorSettings() != null) {
                validatorProgName = getClicsValidatorSettings().getValidatorProgramName();
                found = true;
            }
        } else if (isUsingCustomValidator()) {
            if (getCustomOutputValidatorSettings() != null) {
                validatorProgName = getCustomOutputValidatorSettings().getCustomValidatorProgramName();
                found = true;
            }
        }

        if (!found) {
            throw new RuntimeException("getValidatorProgramName(): unable to locate Settings for currently-specified Validator");
        } else {
            return validatorProgName;
        }
    }

    /**
     * Sets the Validator Program Name for the Output Validator attached to the Problem.
     * Note that this Problem class does not maintain a separate "Validator Program Name" field;
     * rather, the Output Validator Program Name is stored within the "Settings" object associated with the
     * type of Output Validator attached to the Problem.
     *
     * @param validatorProgramName a String specifying the new Output Validator Program Name
     *
     * @see PC2ValidatorSettings
     * @see ClicsValidatorSettings
     * @see CustomValidatorSettings
     *
     * @throws {@link RuntimeException} if the Problem is not marked as having an Output Validator when an attempt is made to set
     *          set the Output Validator Program name, or if the Problem is marked as having an Output Validator but no Validator Settings
     *          object could be found
     */
    public void setOutputValidatorProgramName(String validatorProgramName) {

        if (!this.isValidatedProblem()) {
            throw new RuntimeException("Cannot set a Validator Program Name on a Problem marked as not using a Validator");
        }

        // search for ValidatorSettings for the currently-specified Validator; if found, set the ValidatorProgramName
        // into those Settings
        boolean found = false;
        if (isUsingPC2Validator()) {
            if (getPC2ValidatorSettings() != null) {
                getPC2ValidatorSettings().setValidatorProgramName(validatorProgramName);
                found = true;
            }
        } else if (isUsingCLICSValidator()) {
            if (getClicsValidatorSettings() != null) {
                getClicsValidatorSettings().setValidatorProgramName(validatorProgramName);
                found = true;
            }
        } else if (isUsingCustomValidator()) {
            if (getCustomOutputValidatorSettings() != null) {
                getCustomOutputValidatorSettings().setValidatorProgramName(validatorProgramName);
                found = true;
            }
        }

        if (!found) {
            throw new RuntimeException("setValidatorProgramName(): unable to locate Settings for currently-specified Validator");
        }
    }

    /**
     * Returns the Problem Number.
     * @return the Problem Number
     */
    protected int getNumber() {
        return number;
    }

    /**
     * Sets the Problem Number.
     * @param number the number for the Problem
     */
    protected void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    @Override
    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    @Override
    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    /**
     * Returns an indication of which option has been selected when using the PC2Validator.
     *
     * @return an integer indicating which PC2Validator option has been specified,
     *              or -1 if no PC2Validator Settings for the Problem could be found
     */
    public int getWhichPC2Validator() {

        if (getPC2ValidatorSettings() != null) {
            return getPC2ValidatorSettings().getWhichPC2Validator();
        } else {
            return -1;
        }
    }

    /**
     * Sets the value indicating which option has been selected when using the PC2Validator.
     *
     * @param whichPC2Validator -- the integer value to which the PC2Validator option should be set
     *
     * @throws {@link RuntimeException} if there is no PC2 Validator Settings object attached to the Problem
     */
    public void setWhichPC2Validator(int whichPC2Validator) {

        if (getPC2ValidatorSettings()!=null) {
            getPC2ValidatorSettings().setWhichPC2Validator(whichPC2Validator);
        } else {
            throw new RuntimeException("setWhichPC2Validator(): no PC2 Validator Settings found in the Problem");
        }
    }

    @Override
    public int hashCode() {
        return getElementId().toString().hashCode();
    }

    public boolean isSameAs(Problem otherProblem) {

        try {
            if (otherProblem == null){
                return false;
            }
            if (! StringUtilities.stringSame(displayName, otherProblem.getDisplayName())){
                return false;
            }
            if (isActive() != otherProblem.isActive()) {
                return false;
            }
            if (timeOutInSeconds != otherProblem.getTimeOutInSeconds()) {
                return false;
            }

            if (! StringUtilities.stringSame(judgesInputDataFileName, otherProblem.getDataFileName())) {
                return false;
            }
            if (! StringUtilities.stringSame(answerFileName, otherProblem.getAnswerFileName())) {
                return false;
            }
            if (!readInputDataFromSTDIN == otherProblem.isReadInputDataFromSTDIN()) {
                return false;
            }

            if (this.isValidatedProblem() != otherProblem.isValidatedProblem()) {
                return false;
            }

            if (this.getValidatorType() != otherProblem.getValidatorType()) {
                return false;
            }

            // check for one PC2ValidatorSettings being null while the other is not (i.e., XOR says they are different)
            if (this.getPC2ValidatorSettings()==null ^ otherProblem.getPC2ValidatorSettings()==null) {
                return false;
            }
            // check that if both Settings are non-null, they are the same (if one is non-null, the other must also be, due to the XOR above)
            if (this.getPC2ValidatorSettings() != null) {
                if (!this.getPC2ValidatorSettings().equals(otherProblem.getPC2ValidatorSettings())) {
                    return false;
                }
            }

            // check for one ClicsValidatorSettings being null while the other is not (i.e., XOR says they are different)
            if (this.getClicsValidatorSettings()==null ^ otherProblem.getClicsValidatorSettings()==null) {
                return false;
            }
            // check that if both Settings are non-null, they are the same (if one is non-null, the other must also be, due to the XOR above)
            if (this.getClicsValidatorSettings() != null) {
                if (!this.getClicsValidatorSettings().equals(otherProblem.getClicsValidatorSettings())) {
                    return false;
                }
            }

            // check for one CustomOutputValidatorSettings being null while the other is not (i.e., XOR says they are different)
            if (this.getCustomOutputValidatorSettings()==null ^ otherProblem.getCustomOutputValidatorSettings()==null) {
                return false;
            }
            // check that if both Settings are non-null, they are the same (if one is non-null, the other must also be, due to the XOR above)
            if (this.getCustomOutputValidatorSettings() != null) {
                if (!this.getCustomOutputValidatorSettings().equals(otherProblem.getCustomOutputValidatorSettings())) {
                    return false;
                }
            }

            //check general Input Validation settings
            if (this.getVivaInputValidationStatus() != otherProblem.getVivaInputValidationStatus()) {
                return false;
            }

            if (this.getCustomInputValidationStatus() != otherProblem.getCustomInputValidationStatus()) {
                return false;
            }

            if (this.getCurrentInputValidatorType() != otherProblem.getCurrentInputValidatorType()) {
                return false;
            }

            //check for differences in Custom Input Validator settings
            if (this.isProblemHasCustomInputValidator() != otherProblem.isProblemHasCustomInputValidator()) {
                return false;
            }
            if (!this.getCustomInputValidatorProgramName().equals(otherProblem.getCustomInputValidatorProgramName())) {
                return false;
            }
            if (!this.getCustomInputValidatorCommandLine().equals(otherProblem.getCustomInputValidatorCommandLine())) {
                return false;
            }
            if (!this.isCustomInputValidatorHasBeenRun()==otherProblem.isCustomInputValidatorHasBeenRun()) {
                return false;
            }

            //check for differences in Custom Input Validator results?

            //check for differences in Viva Input Validator settings
            if (!Arrays.equals(this.getVivaInputValidatorPattern(), otherProblem.getVivaInputValidatorPattern())) {
                return false;
            }
            if (!this.isVivaInputValidatorHasBeenRun()==otherProblem.isVivaInputValidatorHasBeenRun()) {
                return false;
            }

            if (showValidationToJudges != otherProblem.isShowValidationToJudges()) {
                return false;
            }

            if (hideOutputWindow != otherProblem.isHideOutputWindow()) {
                return false;
            }
            if (showCompareWindow != otherProblem.isShowCompareWindow()) {
                return false;
            }
            if (computerJudged != otherProblem.isComputerJudged()) {
                return false;
            }
            if (manualReview != otherProblem.isManualReview()) {
                return false;
            }
            if (prelimaryNotification != otherProblem.isPrelimaryNotification()) {
                return false;
            }

            if (getSiteNumber() != otherProblem.getSiteNumber()){
                return false;
            }

            if (! StringUtilities.stringSame(shortName, otherProblem.getShortName())){
                return false;
            }

            if (! StringUtilities.stringSame(externalDataFileLocation, otherProblem.getExternalDataFileLocation())){
                return false;
            }

            if (usingExternalDataFiles != otherProblem.usingExternalDataFiles) {
                return false;
            }

            if (! StringUtilities.stringSame(shortName, otherProblem.getShortName())){
                return false;
            }
            // TODO 917 - do isameAs when test case filenames can be added.
//            if (! StringUtilities.stringArraySame(testCaseDataFilenames, problem.getTestCaseDataFilenames())) {
//                return false;
//            }
//            if (! StringUtilities.stringArraySame(testCaseAnswerFilenames, problem.getTestCaseAnswerFilenames())) {
//                return false;
//            }

            if (!this.isStopOnFirstFailedTestCase() == otherProblem.isStopOnFirstFailedTestCase()) {
                return false;
            }

            if (! StringUtilities.stringSame(colorName, otherProblem.getColorName())){
                return false;
            }

            if (! StringUtilities.stringSame(colorRGB, otherProblem.getColorRGB())){
                return false;
            }

            if (! StringUtilities.stringSame(colorRGB, otherProblem.getColorRGB())){
                return false;
            }

            if ( ! groups.equals(otherProblem.getGroups())){
                return false;
            }

            if (! (this.getMaxOutputSizeKB() == otherProblem.getMaxOutputSizeKB()) ) {
                return false;
            }

            //check for equivalence in Sandbox configuration
            if (this.getSandboxType() != otherProblem.getSandboxType()) {
                return false ;
            }

            //check for same memory limits
            if (this.getMemoryLimitMB() != otherProblem.getMemoryLimitMB()) {
                return false;
            }

            //all comparisons pass; problems are equivalent
            return true;

        } catch (Exception e) {
            StaticLog.getLog().log(Log.WARNING, "Exception comparing Problem "+e.getMessage(), e);
            e.printStackTrace(System.err);
            return false;
        }
    }

    public boolean isShowCompareWindow() {
        return showCompareWindow;
    }

    public void setShowCompareWindow(boolean showCompareWindow) {
        this.showCompareWindow = showCompareWindow;
    }

    public boolean isComputerJudged() {
        return computerJudged;
    }

    public void setComputerJudged(boolean computerJudged) {
        this.computerJudged = computerJudged;
    }

    public boolean isManualReview() {
        return manualReview;
    }

    public void setManualReview(boolean manualReview) {
        this.manualReview = manualReview;
    }

    public boolean isPrelimaryNotification() {
        return prelimaryNotification;
    }

    public void setPrelimaryNotification(boolean prelimaryNotification) {
        this.prelimaryNotification = prelimaryNotification;
    }

    public boolean isInteractive() {
        return isUsingCustomValidator() && customValidatorSettings.isUseInteractiveValidatorInterface();
    }

    /**
     * Get short name for problem.
     *
     * @return
     */
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Valid short name?. <br>
     *
     * A valid short name does not contain path or drive delimiters, these symbols : / \
     *
     * @param name
     * @return true if valid, false otherwise.
     */
    public boolean isValidShortName(String name) {
        boolean invalidName = name == null || name.contains(File.separator) || //
                name.contains(":") || name.contains("/") || name.contains("\\");
        return !invalidName;
    }

    public boolean isValidShortName() {
        return isValidShortName(shortName);
    }

    /**
     * Get letter for problem.
     *
     * @return
     */
    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    /**
     * Add data and answer filenames to list of test cases.
     *
     * @see #removeAllTestCaseFilenames()
     * @param datafile
     * @param answerfile
     */
    public void addTestCaseFilenames (String datafile, String answerfile){

        String[] newArray;

        if (datafile != null) {
            newArray = StringUtilities.appendString(testCaseDataFilenames, datafile);
            testCaseDataFilenames = newArray;
        }

        if (answerfile != null){
            newArray = StringUtilities.appendString(testCaseAnswerFilenames, answerfile);
            testCaseAnswerFilenames = newArray;
        }
    }


    /**
     * Remove all test case filenames.
     */
    public void removeAllTestCaseFilenames(){
        testCaseDataFilenames = new String[0];
        testCaseAnswerFilenames = new String[0];
    }

    public int getNumberTestCases() {
        // this needs to look at the longest of the testCase Data and Answer Filesnames lists
        if (testCaseDataFilenames != null && testCaseDataFilenames.length > 0) {
            int count = testCaseDataFilenames.length;
            if (testCaseAnswerFilenames != null && testCaseAnswerFilenames.length > count) {
                count = testCaseAnswerFilenames.length;
            }
            return count;
        } else if (getDataFileName() != null || getAnswerFileName() != null) {
            return 1;
        } else {
            return 0;
        }
    }

    public String getExecutionPrepCommand() {
        return executionPrepCommand;
    }

    public void setExecutionPrepCommand(String executionPrepCommand) {
        this.executionPrepCommand = executionPrepCommand;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorRGB(String colorRGB) {
        this.colorRGB = colorRGB;
    }

    public String getColorRGB() {
        return colorRGB;
    }


    /**
     * @return true if not storing files on pc2 server.
     */
    public boolean isUsingExternalDataFiles() {
        return usingExternalDataFiles;
    }

    /**
     * @param usingExternalDataFiles
     */
    public void setUsingExternalDataFiles(boolean usingExternalDataFiles) {
        this.usingExternalDataFiles = usingExternalDataFiles;
    }

    /**
     * Local file path for external data files.
     *
     * @param dataLoadYAMLPath
     */
    public void setExternalDataFileLocation(String externalDataFileLocation) {
        this.externalDataFileLocation = externalDataFileLocation;
    }

    public String getExternalDataFileLocation() {
        return externalDataFileLocation;
    }

    /**
     * Return external judges data file (location).
     *
     * Searches both the dir found in {@link #getExternalDataFileLocation()} or
     * in that location plus the CCS standard location for that file.
     *
     * @param dataSetNumber
     * @return null if not found, else the path for the file.
     */
    public File locateJudgesDataFile (int dataSetNumber){
        return locateDataFile(getDataFileName(dataSetNumber));
    }

    /**
     * Return external judges answer file (location).
     *
     * Searches both the dir found in {@link #getExternalDataFileLocation()} or
     * in that location plus the CCS standard location for that file.
     *
     * @param dataSetNumber
     * @return
     */
    public File locateJudgesAnswerFile (int dataSetNumber){
        return locateDataFile(getDataFileName(dataSetNumber));
    }

    /**
     * Get external CCS standard location for data files.
     *
     * @return
     */
    public String getCCSfileDirectory() {
        if (getExternalDataFileLocation() == null){
            return null;
        } else {
            return getExternalDataFileLocation() + File.separator + "data" + File.separator + "secret";
        }
    }

    private File locateDataFile(String filename) {
        String directoryName = getExternalDataFileLocation();
        if (directoryName == null) {
            return null;
        }
        File dir = new File(directoryName);
        if (!dir.isDirectory()) {
            return null;
        }

        String name = getCCSfileDirectory() + File.separator + filename;
        File file = new File(name);

        if (file.isFile()) {
            return file;
        }

        name = directoryName + File.separator + filename;
        file = new File(name);

        if (file.isFile()) {
            return file;
        }

        return null;
    }

    // TODO 917 - isSameAs methods
//    /**
//     * @return the testCaseDataFilenames
//     */
//    private String[] getTestCaseDataFilenames() {
//        return testCaseDataFilenames;
//    }
//
//    /**
//     * @return the testCaseAnswerFilenames
//     */
//    private String[] getTestCaseAnswerFilenames() {
//        return testCaseAnswerFilenames;
//    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setElementId(Problem problem) {
        problem.elementId = elementId;
    }

    /**
     * Returns the current {@link PC2ValidatorSettings} object attached to this Problem.
     *
     * @return the current PC2ValidatorSettings object
     */
    public PC2ValidatorSettings getPC2ValidatorSettings() {
        return this.pc2ValidatorSettings;
    }

    /**
     * Sets the {@link PC2ValidatorSettings} for this Problem to the specified settings object.
     *
     * @param settings the PC2ValidatorSettings object to attach to this Problem
     */
    public void setPC2ValidatorSettings(PC2ValidatorSettings settings) {
        this.pc2ValidatorSettings = settings ;
    }

    /**
     * Returns a {@link ClicsValidatorSettings} object containing the options which this
     * Problem should apply when using the CLICS validator.
     *
     * @return the clicsValidatorSettings for this problem
     */
    public ClicsValidatorSettings getClicsValidatorSettings() {
        return clicsValidatorSettings;
    }

    /**
     * Sets the {@link ClicsValidatorSettings} for this problem to the specified value.
     *
     * @param settings the CLICS Validator Settings to set
     */
    public void setCLICSValidatorSettings(ClicsValidatorSettings settings) {
        this.clicsValidatorSettings = settings;
    }

    /**
     * Returns a {@link CustomValidatorSettings} object describing the custom validator
     * settings associated with this problem (if any).
     * @return the customValidatorSettings for this problem
     */
    public CustomValidatorSettings getCustomOutputValidatorSettings() {
        return customValidatorSettings;
    }

    /**
     * Sets the {@link CustomValidatorSettings} for this problem to the specified value.
     * @param customValidatorSettings the customValidatorSettings to set
     */
    public void setCustomOutputValidatorSettings(CustomValidatorSettings settings) {
        this.customValidatorSettings = settings;
    }

    //*** Methods associated with Custom Input Validators ***

    /**
     * Returns an indication of whether or not this Problem has a Custom Input Validator attached.
     *
     * @return true if the problem has a Custom Input validator
     */
    public boolean isProblemHasCustomInputValidator() {
        return problemHasCustomInputValidator;
    }

    /**
     * Sets the flag indicating whether or not the Problem has a Custom Input Validator.
     *
     * @param problemHasCustomInputValidator the value to which the flag should be set
     */
    public void setProblemHasCustomInputValidator(boolean problemHasCustomInputValidator) {
        this.problemHasCustomInputValidator = problemHasCustomInputValidator;
    }

    /**
     * Returns the Input Validation status of the problem with respect to running a Custom Input Validator.
     * Note that a problem may have more than one Input Validator applied to it (e.g., a Custom Input Validator
     * and/or the VIVA Input Validator); this method returns the Input Validation status determined by the
     * execution (if any) of a Custom Input Validator.
     *
     * Use method {@link #getCurrentInputValidatorType()} to determine
     * the current type of Input Validator associated with the problem (that is, the most recently selected
     * Input Validator type).
     *
     * @return an element of {@link InputValidationStatus} indicating the Custom Input Validation status of the problem.
     *
     * @see #getCurrentInputValidatorType()
     * @see #getVivaInputValidationStatus()
     *
     */
    public InputValidationStatus getCustomInputValidationStatus() {
        return customInputValidationStatus;
    }

    /**
     * Sets the Custom Input Validation status of this problem to the specified value.
     *
     * @param status the value to which the Custom Input Validation status for the problem should be set.
     */
    public void setCustomInputValidationStatus (InputValidationStatus status) {
        this.customInputValidationStatus = status;
    }

    /**
     * Returns the name of the Custom Input Validator for the Problem, or the empty string if the Problem has no
     * defined Custom Input Validator (that is, if the Custom Input Validator SerializedFile associated with the Problem is null).
     *
     * @return the Custom Input Validator Program Name for the Problem, or an empty string
     */
    public String getCustomInputValidatorProgramName() {
        if (customInputValidatorSerializedFile == null) {
            return "";
        } else {
            return getCustomInputValidatorSerializedFile().getName();
        }
    }

    //this method should not exist; the "Custom Input Validator Program Name" is defined by the name contained in the
    // Custom Input Validator Serialized File and should not be able to be set independently of that file.
//    /**
//     * Sets the name of the Custom Input Validator program for this Problem.
//     *
//     * @param customInputValidatorProgramName the name of the Custom Input Validator program
//     */
//    public void setCustomInputValidatorProgramName(String inputValidatorProgramName) {
//        this.customInputValidatorProgramName = inputValidatorProgramName;
//    }

    /**
     * Returns the Custom Input Validator command for the Problem (that is, the command used to
     * invoke the Custom Input Validator), or the empty string if the Problem has no
     * defined Custom Input Validator (that is, if the Custom Input Validator command is null or the empty string).
     *
     * @return the Custom Input Validator Command Line
     */
    public String getCustomInputValidatorCommandLine() {
        if (customInputValidatorCommandLine == null) {
            return "";
        } else {
            return customInputValidatorCommandLine;
        }
    }

    /**
     * Sets the command line used to invoke a Custom Input Validator.
     *
     * @param customInputValidatorCommandLine the customInputValidatorCommandLine to set.
     */
    public void setCustomInputValidatorCommandLine(String inputValidatorCommandLine) {
        this.customInputValidatorCommandLine = inputValidatorCommandLine;
    }

    //TODO: these methods may have been useful before VIVA support was added; not sure if they are still needed... jlc
    // It is also the case that they may only be useful if/when the "Run Input Validators" facility is fully implemented (currently, it's not).
//    /**
//     * Returns the name of the folder containing the input files for this Problem.
//     * Note that is value is only meaningful if the user has run the Input Validator
//     * and selected "Files on disk in folder" as the input file source.
//     *
//     */
//    public String getInputValidatorFilesOnDiskFolder() {
//        return this.customInputValidatorFilesOnDiskFolderName;
//    }
//
//    /**
//     * Sets the value of the InputFilesOnDiskFolder variable for this problem.
//     *
//     * @param inputValFilesOnDiskFolder
//     */
//    public void setInputValidatorFilesOnDiskFolder(String inputValFilesOnDiskFolder) {
//        this.customInputValidatorFilesOnDiskFolderName = inputValFilesOnDiskFolder;
//
//    }

    /**
     * Returns an {@link Iterable} for the current Custom Input Validator {@link InputValidationResults} for this problem.
     * The returned object may be empty (that it, the Iterable may have no elements) but will never be null.
     *
     * Note that a Problem may have been tested with both the VIVA Input Validator and a user-defined Custom
     * Input Validator; however, the results returned by this method will always be those generated by the
     * most recent execution of the Custom Input Validator (if any).
     *
     * @return an {@link Iterable} containing Custom Input Validator InputValidationResults, or null if no such results exist.
     */
    public Iterable<InputValidationResult> getCustomInputValidatorResults() {
        if (this.customInputValidationResults == null) {
            this.customInputValidationResults = new Vector<InputValidationResult>() ;
        }
        return this.customInputValidationResults;
    }

    /**
     * Returns the number of Custom Input Validator {@link InputValidationResult}s currently stored in this Problem.
     */
    public int getNumCustomInputValidationResults() {
        if (this.customInputValidationResults == null) {
            this.customInputValidationResults = new Vector<InputValidationResult>();
        }
        return this.customInputValidationResults.size();
    }

    /**
     * Adds the specified {@link InputValidationResult} to the current set of Custom Input Validation Results
     * for the Problem.
     *
     * @param result the InputValidationResult to be added.
     *
     */
    public void addCustomInputValidationResult(InputValidationResult result) {
        if (getCustomInputValidatorResults() == null) {
            this.customInputValidationResults = new Vector<InputValidationResult>();
        }
        customInputValidationResults.add(result);
    }

    /**
     * Clears (removes) all Custom Input Validator {@link InputValidationResults} currently stored in this Problem.
     * Note that calling this method does not affect any VIVA Input Validation results stored in the Problem.
     *
     * @see #clearVivaInputValidationResults()
     */
    public void clearCustomInputValidationResults() {
        this.customInputValidationResults = null;
    }


    //*** Methods associated with the VIVA Input Validator ***

    /**
     * Returns an indication of whether or not this Problem has a Viva Input Validator pattern attached.
     *
     * @return true if the problem has a Viva Input validator pattern.
     */
    public boolean isProblemHasVivaInputValidatorPattern() {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        return vivaSettings.isProblemHasVivaInputValidatorPattern();
    }

    //This method is commented out because there is no longer a separate "problemHasVivaInputValidatorPattern" flag in the Problem class;
    // having a Viva pattern (or not) is determined by the value in the Pattern field in the (VivaInputValidatorSettings for the) Problem.
    // This was done to avoid the possibility of an "invalid state" where a user sets "problemHasInputValidator" to (say) false after having
    // set a non-zero-length pattern in the Problem.

//    /**
//     * Sets the flag indicating whether or not the Problem has a Viva Input Validator pattern.
//     *
//     * @param hasVivaPattern the value to which the flag should be set.
//     */
//    public void setProblemHasVivaInputValidatorPattern(boolean hasVivaPattern) {
//        if (vivaSettings==null) {
//            vivaSettings = new VivaInputValidatorSettings();
//        }
//        vivaSettings.setProblemHasVivaInputValidatorPattern(hasVivaPattern);
//    }

    /**
     * Returns the Input Validation status of the problem with respect to running the VIVA Input Validator.
     * Note that a problem may have more than one Input Validator applied to it (e.g., a Custom Input Validator
     * and/or the VIVA Input Validator); this method returns the Input Validation status determined by the
     * execution (if any) of the VIVA Input Validator.
     *
     * Use method {@link #getCurrentInputValidatorType()} to determine
     * the current type of Input Validator associated with the problem.
     *
     * @return an element of {@link InputValidationStatus} indicating the VIVA Input Validation status of the problem.
     *
     * @see #getCurrentInputValidatorType()
     * @see #getCustomInputValidationStatus()
     *
     */
    public InputValidationStatus getVivaInputValidationStatus() {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        return vivaSettings.getVivaInputValidationStatus();
    }

    /**
     * Sets the VIVA Input Validation status of this problem to the specified value.
     *
     * @param status the value to which the VIVA Input Validation status for the problem should be set.
     */
    public void setVivaInputValidationStatus (InputValidationStatus status) {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        vivaSettings.setVivaInputValidationStatus(status);
    }


    /**
     * Returns an {@link Iterable} for the current VIVA {@link InputValidationResults} for this problem.
     * The returned object may be empty (that it, the Iterable may have no elements) but will never be null.
     *
     * Note that a Problem may have been tested with both the VIVA Input Validator and a user-defined Custom
     * Input Validator; however, the results returned by this method will always be those generated by the
     * most recent execution of the VIVA Input Validator.
     *
     * @return an {@link Iterable} containing VIVA InputValidationResults.
     */
    public Iterable<InputValidationResult> getVivaInputValidatorResults() {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        return vivaSettings.getVivaInputValidationResults();
    }

    /**
     * Returns the number of VIVA Input Validator {@link InputValidationResult}s currently stored in this Problem.
     */
    public int getNumVivaInputValidationResults() {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        return vivaSettings.getNumVivaInputValidationResults();
    }

    /**
     * Adds the specified {@link InputValidationResult} to the current set of VIVA Input Validation Results
     * for the Problem.
     *
     * @param result the InputValidationResult to be added.
     */
    public void addVivaInputValidationResult(InputValidationResult result) {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        vivaSettings.addVivaInputValidationResult(result);
    }

    /**
     * Clears (removes) all VIVA {@link InputValidationResults} currently stored in this Problem.
     * Note that calling this method does not affect any Custom Input Validation results stored in the Problem.
     *
     * @see #clearCustomInputValidationResults()
     */
    public void clearVivaInputValidationResults() {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        vivaSettings.clearVivaInputValidationResults();
    }

    /**
     * Returns the flag indicating whether this problem is configured to stop execution
     * on encountering a failed test case.
     *
     * @return true if execution should stop after the first failed test case; false if execution should continue
     *              (i.e. if all test cases should be executed even if some fail).
     */
    public boolean isStopOnFirstFailedTestCase() {
        return stopOnFirstFailedTestCase;
    }

    /**
     * Sets the flag indicating whether execution of submissions for this problem should stop on encountering the first failed test case.
     *
     * @param stopOnFirstFailedTestCase true indicates execution should stop on first failed test case; false indicates all test cases
     *          should be executed
     */
    public void setStopOnFirstFailedTestCase(boolean stopOnFirstFailedTestCase) {
        this.stopOnFirstFailedTestCase = stopOnFirstFailedTestCase;
    }

    /**
     * Set so all users can view this problem.
     */
    public void clearGroups(){
        groups = new ArrayList<>();
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void addGroup(Group group){
        groups.add(group);
    }

    /**
     * Is this group permitted to view/use this problem?.
     * @param group
     * @return
     */
    public boolean canView (Group group){
        boolean view = (groups.size() == 0);
        if (!view && group != null) {
            for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
                Group g2 = iterator.next();
                if (group.getDisplayName().equals(g2.getDisplayName())) {
                    view = true;
                    break;
                }
            }
        }
        return (view);
    }

    /**
     * Are any of the groups in wantedGroups allowed to view/use this problem?
     *
     * @param wantedGroups
     * @return true if any group can see the problem, false otherwise
     */
    public boolean canView(List<Group> wantedGroups) {
        // If no specific groups are assigned to this problem or we're not interested
        // in specific groups, then the problem is viewable.
        boolean view = (groups.size() == 0 || wantedGroups == null);

        if(!view) {
            for(Group group : wantedGroups) {
                view = canView(group);
                if(view) {
                    break;
                }
            }
        }
        return(view);
    }

    /**
     * Are there no groups assigned to this problem?
     *
     * @see canView
     * @see getGroups
     * @return true if no groups in list, false otherwise.
     */
    public boolean isAllView(){
        return (groups.size() == 0);
    }

    /**
     * Returns a String array containing the VIVA Input Validator pattern associated with this problem,
     * or null if no VIVA pattern has been assigned.
     * Note that the VIVA pattern is an array of String, one pattern line per array element.
     *
     * @return a String [] containing the Viva pattern, or null.
     */
    public String [] getVivaInputValidatorPattern() {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        return vivaSettings.getVivaInputValidatorPattern();
    }

    /**
     * Sets the Viva Input Validator pattern for this problem to the specified String array.
     */
    public void setVivaInputValidatorPattern(String [] pattern) {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        vivaSettings.setVivaInputValidatorPattern(pattern) ;
    }

    /**
     * Returns the {@link SerializedFile} containing the Custom Input Validator assigned to this problem,
     * or null if no Custom Input Validator has been assigned.
     *
     * @return a SerializedFile containing the Custom Input Validator assigned to this problem, or null.
     */
    public SerializedFile getCustomInputValidatorSerializedFile() {
        return customInputValidatorSerializedFile ;
    }

    /**
     * Sets the {@link SerializedFile} containing the Custom Input Validator assigned to this problem.
     *
     * @param inputValidatorFile a SerializedFile containing a Custom Input Validator program.
     */
    public void setCustomInputValidatorFile(SerializedFile inputValidatorFile) {
        this.customInputValidatorSerializedFile = inputValidatorFile;
    }

    /**
     * Returns the {@link INPUT_VALIDATOR_TYPE} currently associated with this problem --
     * that is, the most recently selected Input Validator type.
     *
     * Note that it is possible to SELECT an Input Validator (thus making that Input Validator type
     * the "currently selected Input Validator type") without actually RUNNING the selected Input Validator.
     * Clients should use this method to determine the most recently selected
     * Input Validator type; method {@link #isVivaInputValidatorHasBeenRun()} or
     * {@link #isCustomInputValidatorHasBeenRun()} can be used to determine whether the current Input Validator
     * (as returned by this method) has actually been executed.
     *
     * @return the type of Input Validator associated with this problem.
     *
     * @see #isVivaInputValidatorHasBeenRun()
     * @see #isCustomInputValidatorHasBeenRun()
     *
     */
    public INPUT_VALIDATOR_TYPE getCurrentInputValidatorType() {
        return currentInputValidatorType;
    }

    /**
     * Sets the {@link INPUT_VALIDATOR_TYPE} associated with this problem.
     *
     * @param currentInputValidatorType the type of Input Validator to be associated with this problem.
     */
    public void setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE currentInputValidatorType) {
        this.currentInputValidatorType = currentInputValidatorType;
    }

    /**
     * @return the customInputValidatorHasBeenRun flag
     */
    public boolean isCustomInputValidatorHasBeenRun() {
        return customInputValidatorHasBeenRun;
    }

    /**
     * @param customInputValidatorHasBeenRun the value to which the customInputValidatorHasBeenRun flag should be set
     */
    public void setCustomInputValidatorHasBeenRun(boolean customInputValidatorHasBeenRun) {
        this.customInputValidatorHasBeenRun = customInputValidatorHasBeenRun;
    }

    /**
     * @return the vivaInputValidatorHasBeenRun flag for this Problem.
     */
    public boolean isVivaInputValidatorHasBeenRun() {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        return vivaSettings.isVivaInputValidatorHasBeenRun();
    }

    /**
     * @param vivaInputValidatorHasBeenRun the value to which the vivaInputValidatorHasBeenRun flag should be set
     */
    public void setVivaInputValidatorHasBeenRun(boolean vivaInputValidatorHasBeenRun) {
        if (vivaSettings==null) {
            vivaSettings = new VivaInputValidatorSettings();
        }
        vivaSettings.setVivaInputValidatorHasBeenRun(vivaInputValidatorHasBeenRun);
    }

    /**
     * Returns the currently configured memory limit (in MB) for this Problem.
     * A memory limit of zero indicates "no limit".
     * Note that memory limits are not enforced unless a sandbox has been selected
     * on the Edit Problem GUI (or via YAML configuration).
     *
     * @return
     */
    public int getMemoryLimitMB() {
        return memoryLimitMB;
    }

    /**
     * Sets the memory limit for this problem. Setting a memory limit of zero indicates "no limit",
     * meaning that the problem is constrained only by the memory provided by the hardware, the OS,
     * and the specific language runtime system.
     *
     * Note that setting a memory limit does not automatically imply that such limit is enforced;
     * enforcing a memory limit requires selection of a problem sandbox capable of doing that.
     * (See {@link EditProblemSandboxPane}.)
     *
     * If a value less than zero is passed in the memory limit is set to zero (unlimited).
     *
     * @param memLimitInMB the memory limit for the problem, in MB; must be >= 0, where 0=unlimited.
     */
    public void setMemoryLimitMB(int memLimitInMB) {
        if (memLimitInMB < 0) {
            this.memoryLimitMB = 0;
            //TODO: pass a Log into the Problem constructor so conditions like this can be logged properly.
//            getLog().warning("Memory limit < 0 specified; setting to 0 (unlimited)");
        } else {
            this.memoryLimitMB = memLimitInMB;
        }
    }

    /**
     * Update sandbox command line program names based on whether the problem
     * is using the internal sandbox and if it's interactive
     * This routine makes sure that if we're using the internal sandbox, that the sandbox command line
     * and sandbox program (script name) are correct.
     */
    private void updateSandboxInfo()
    {
        // we only set the sandbox program and command line if we're using the internal sandbox
        // or, we haven't set anything yet.  We do the latter to prevent NPE's for someone requesting
        // the sandbox name or command line even if the sandbox strings have not been defined yet
        if(this.getSandboxType() == SandboxType.PC2_INTERNAL_SANDBOX || sandboxProgramName == null) {
            if(isInteractive()) {
                sandboxProgramName = Constants.PC2_INTERNAL_SANDBOX_INTERACTIVE_NAME;
                sandboxCmdLine = Constants.PC2_INTERNAL_SANDBOX_INTERACTIVE_COMMAND_LINE;
            } else {
                sandboxProgramName = Constants.PC2_INTERNAL_SANDBOX_PROGRAM_NAME;
                sandboxCmdLine = Constants.PC2_INTERNAL_SANDBOX_COMMAND_LINE;
            }
        }
    }

    /**
     * Returns a String containing the name of the sandbox program associated with this Problem.
     * Note that the value returned by this method is only relevant if the value returned by
     * {@link #getSandboxType()} is something other than {@link SandboxType#NONE}.
     *
     * @return the currently-defined sandbox program name.
     */
    public String getSandboxProgramName() {
        updateSandboxInfo();
        return sandboxProgramName;
    }

    /**
     * Sets the name of the sandbox program used by this Problem.
     * Note that setting a sandbox program name does NOT in and of itself cause the specified sandbox to be
     * used; the Admin must configure/enable the sandbox using the Edit Problem dialog (or via YAML configuration).
     *
     * @param sandboxProgramName the name of the sandbox program to be used by this Problem, when sandbox usage is enabled.
     */
    public void setSandboxProgramName(String sandboxProgram) {
        this.sandboxProgramName = sandboxProgram;
    }

    /**
     * Returns the String containing the command used to invoke the sandbox configured for this problem.
     * Note that the returned value is meaningless if the Problem has not been configured to use a sandbox.
     *
     * @return the command line used to invoke the sandbox for this problem, when sandbox usage is enabled.
     */
    public String getSandboxCmdLine() {
        updateSandboxInfo();
        return sandboxCmdLine;
    }

    /**
     * Sets the command line used to invoke the sandbox associated with this Problem.
     * Note that setting the sandbox command line does not in and of itself enable the use of a sandbox; the
     * Admin must enable the sandbox via the Edit Problem dialog (or via YAML configuration).
     * Note also that the value of sandboxCmdLine is meaningless if the Problem is currently configured
     * with {@link SandboxType#NONE}.
     *
     * @param sandboxCmdLine the command line used to invoke the Problem sandbox.
     */
    public void setSandboxCmdLine(String sandboxCmdLine) {
        this.sandboxCmdLine = sandboxCmdLine;
    }

    /**
     * Returns a boolean flag which indicates whether this Problem has been configured to use a sandbox.
     *
     * @return false if the currently configured SandboxType for the problem is {@link Problem.SandboxType#NONE};
     *          true if any other sandbox type has been configured.
     */
    public boolean isUsingSandbox() {
        return sandboxType != SandboxType.NONE;
    }

    /**
     * Returns the type of sandbox configured in this Problem; an element of {@link Problem.SandboxType}
     * which might be {@link SandboxType#NONE}.
     *
     * @return an element of {@link Problem.SandboxType}.
     */
    public SandboxType getSandboxType() {
        if (sandboxType == null) {
            sandboxType = SandboxType.NONE;
        }
        return sandboxType;
    }

    /**
     * Sets the type of sandbox being used by this Problem.  If the specified type of sandbox is
     * {@link SandboxType#PC2_INTERNAL_SANDBOX}, also sets the Sandbox Command Line and Sandbox Program Name
     * to their PC2 Internal Sandbox values.
     *
     * @param sandboxType the type of sandbox to be used by this Problem, which might be {@link Sandbox#NONE}.
     */
    public void setSandboxType(SandboxType sandboxType) {
        this.sandboxType = sandboxType;

        //if we're setting the PC2 internal sandbox, also set the sandbox command line and program name
        updateSandboxInfo();
    }

    /**
     * Gets the command line to use to run interactive problems.  Currently, this is a read-only
     * value since it does not make sense to change it at this time.
     */
    public String getInteractiveCommandLine() {
        return interactiveCommandLine;
    }

    public boolean isLoadDataFilesSamplesFirst() {
        return loadDataFilesSamplesFirst;
    }

    public void setLoadDataFilesSamplesFirst(boolean loadDataFilesSamplesFirst) {
        this.loadDataFilesSamplesFirst = loadDataFilesSamplesFirst;
    }

}
