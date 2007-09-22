package edu.csus.ecs.pc2.core.model;

import java.awt.image.SampleModel;


/**
 * Single Problem Definition.
 * 
 * This contains settings for a problem.  Data files
 * are not in this class, data files are in the {@link edu.csus.ecs.pc2.core.model.ProblemDataFiles}
 * class.
 * 
 * @see edu.csus.ecs.pc2.core.list.ProblemList
 * @see edu.csus.ecs.pc2.core.model.ProblemDataFiles
 * 
 * @author pc2@ecs.csus.edu
 * 
 */
// $HeadURL$
public class Problem implements IElementObject {

    public static final String SVN_ID = "$Id$";

    /**
     * PC<sup>2 Validator Command Line.
     */
    public static final String INTERNAL_VALIDATOR_NAME = "pc2.jar edu.csus.ecs.pc2.validator.Validator";


    /**
     * 
     */
    private static final long serialVersionUID = 1708763261096488240L;

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
    private String dataFileName = null;

    /**
     * Judge's answer file name.
     * 
     * This is the name of the file that the validator will read/use to compare against the submitted run output.
     */
    private String answerFileName = null;

    /**
     * 
     */
    private boolean active = true;

    /**
     * Input is from stdin.
     */
    private boolean readInputDataFromSTDIN = false;

    /**
     * Seconds per problem run.
     */
    private int timeOutInSeconds;

    // Validator fields

    /**
     * Is this problem using a validator?
     */
    private boolean validatedProblem = false;

    /**
     * Using the internal (default) validator ?
     */
    private boolean usingPC2Validator = false;

    /**
     * Which PC2 Validator Option?.
     */
    private int whichPC2Validator = 0;

    /**
     * Validator command line.
     * 
     * This contains a command and field names.
     */
    private String validatorCommandLine;

    /**
     * The validator command.
     * <P>
     * Ex. java -cp Validator.jar Validator.
     */
    private String validatorProgramName;

    /**
     * Use international judgement method.
     */
    private boolean internationalJudgementReadMethod = true;

    // TODO Implement Commands to be executed before a problem is run
    //
    // /**
    // * This is the command executed before the run is executed.
    // */
    // private String executionPrepCommand = "";
    //
    // /**
    // * This is an optional program/script used by executionPrepCommand.
    // */
    // private SerializedFile executeionPrepFile;

    /**
     * PC2 option to ignore spaces on validation.
     */
    private boolean ignoreSpacesOnValidation = false;

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
     * Create a problem with the display name.
     * 
     * @param displayName
     */
    public Problem(String displayName) {
        super();
        this.displayName = displayName;
        elementId = new ElementId(displayName);
        setSiteNumber(0);
    }

    /**
     * @see Object#equals(java.lang.Object).
     */
    public boolean equals(Object obj) {
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
    public String toString() {
        return displayName;
    }

    /**
     * @return Returns the elementId.
     */
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
     * @return Returns the active.
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

    /**
     * @return Returns the dataFileName.
     */
    public String getDataFileName() {
        return dataFileName;
    }

    /**
     * @return Returns the ignoreSpacesOnValidation.
     */
    public boolean isIgnoreSpacesOnValidation() {
        return ignoreSpacesOnValidation;
    }

    /**
     * @return Returns the internationalJudgementReadMethod.
     */
    public boolean isInternationalJudgementReadMethod() {
        return internationalJudgementReadMethod;
    }

    /**
     * @return Returns the readInputDataFromSTDIN.
     */
    public boolean isReadInputDataFromSTDIN() {
        return readInputDataFromSTDIN;
    }

    /**
     * @return Returns the showValidationToJudges.
     */
    public boolean isShowValidationToJudges() {
        return showValidationToJudges;
    }

    /**
     * @return Returns the timeOutInSeconds.
     */
    public int getTimeOutInSeconds() {
        return timeOutInSeconds;
    }

    /**
     * @return Returns the usingPC2Validator.
     */
    public boolean isUsingPC2Validator() {
        return usingPC2Validator;
    }

    /**
     * @return Returns the validatedProblem.
     */
    public boolean isValidatedProblem() {
        return validatedProblem;
    }

    /**
     * @return Returns the validatorCommandLine.
     */
    public String getValidatorCommandLine() {
        return validatorCommandLine;
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
        this.dataFileName = dataFileName;
    }

    /**
     * @param elementId
     *            The elementId to set.
     */
    public void setElementId(ElementId elementId) {
        this.elementId = elementId;
    }

    /**
     * @param ignoreSpacesOnValidation
     *            The ignoreSpacesOnValidation to set.
     */
    public void setIgnoreSpacesOnValidation(boolean ignoreSpacesOnValidation) {
        this.ignoreSpacesOnValidation = ignoreSpacesOnValidation;
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
     * @param usingPC2Validator
     *            The usingPC2Validator to set.
     */
    public void setUsingPC2Validator(boolean usingPC2Validator) {
        this.usingPC2Validator = usingPC2Validator;
    }

    /**
     * @param validatedProblem
     *            The validatedProblem to set.
     */
    public void setValidatedProblem(boolean validated) {
        this.validatedProblem = validated;
    }

    /**
     * @param validatorCommandLine
     *            The validatorCommandLine to set.
     */
    public void setValidatorCommandLine(String validatorCommandLine) {
        this.validatorCommandLine = validatorCommandLine;
    }

    /**
     * @return Returns the hideOutputWindow.
     */
    public boolean isHideOutputWindow() {
        return hideOutputWindow;
    }

    /**
     * @param hideOutputWindow
     *            The hideOutputWindow to set.
     */
    public void setHideOutputWindow(boolean hideOutputWindow) {
        this.hideOutputWindow = hideOutputWindow;
    }

    /**
     * @return Returns the validatorProgramName.
     */
    public String getValidatorProgramName() {
        return validatorProgramName;
    }

    /**
     * @param validatorProgramName
     *            The validatorProgramName to set.
     */
    public void setValidatorProgramName(String validatorProgramName) {
        this.validatorProgramName = validatorProgramName;
    }

    protected int getNumber() {
        return number;
    }

    protected void setNumber(int number) {
        this.number = number;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    public int getWhichPC2Validator() {
        return whichPC2Validator;
    }

    public void setWhichPC2Validator(int whichPC2Validator) {
        this.whichPC2Validator = whichPC2Validator;
    }

    public int hashCode() {
        return getElementId().toString().hashCode();
    }
    
    /**
     * Compares string, handles if either string is null.
     * 
     * @param s1
     * @param s2
     * @return true if both null or equal, false otherwise
     */
    // TODO move this into a string utility class.
    private boolean stringSame (String s1, String s2){
        if (s1 == null && s2 == null) {
            return true;
        }
        
        if (s1 == null && s2 != null){
            return false;
        }
        
        return s1.equals(s2);
            
    }

    public boolean isSameAs(Problem problem) {

        try {
            if (! stringSame(displayName, problem.getDisplayName())){
                return false;
            }
            if (isActive() != problem.isActive()) {
                return false;
            }
            if (timeOutInSeconds != problem.getTimeOutInSeconds()) {
                return false;
            }

            if (! stringSame(dataFileName, problem.getDataFileName())) {
                return false;
            }
            if (! stringSame(answerFileName, problem.getAnswerFileName())) {
                return false;
            }
            if (!readInputDataFromSTDIN == problem.isReadInputDataFromSTDIN()) {
                return false;
            }
            
            if (validatedProblem != problem.isValidatedProblem()) {
                return false;
            }
            if (usingPC2Validator != problem.isUsingPC2Validator()) {
                return false;
            }
            if (whichPC2Validator != problem.getWhichPC2Validator()) {
                return false;
            }
            if (! stringSame(validatorProgramName, problem.getValidatorProgramName())) {
                return false;
            }
            if (! stringSame(validatorCommandLine, problem.getValidatorCommandLine())) {
                return false;
            }
            if (ignoreSpacesOnValidation != problem.isIgnoreSpacesOnValidation()) {
                return false;
            }
            if (showValidationToJudges != problem.isShowValidationToJudges()) {
                return false;
            }
            
            if (hideOutputWindow != problem.isHideOutputWindow()) {
                return false;
            }
            if (showCompareWindow != problem.isShowCompareWindow()) {
                return false;
            }
            
            if (getSiteNumber() != problem.getSiteNumber()){
                return false;
            }

            return true;
        } catch (Exception e) {
            // TODO Log to static exception Log
            e.printStackTrace();
            return false;
        }
    }

    public boolean isShowCompareWindow() {
        return showCompareWindow;
    }

    public void setShowCompareWindow(boolean showCompareWindow) {
        this.showCompareWindow = showCompareWindow;
    }

}
