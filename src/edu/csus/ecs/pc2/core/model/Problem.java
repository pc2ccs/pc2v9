package edu.csus.ecs.pc2.core.model;

import java.io.File;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;

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
 * @version $Id$
 */
// $HeadURL$
public class Problem implements IElementObject {

    /**
     * PC<sup>2 Validator Command Line.
     */
    public static final String INTERNAL_VALIDATOR_NAME = "pc2.jar edu.csus.ecs.pc2.validator.Validator";

    /**
     * 
     */
    private static final long serialVersionUID = 1708763261096488240L;

    public static final int DEFAULT_TIMEOUT_SECONDS = 30;

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
     * List of judge's file names, for multiple test cases.
     *  
     */
    private String [] testCaseDataFilenames = new String[0];
    
    /**
     * List of judge's answer file names, for multiple test cases.
     */
    private String [] testCaseAnswerFilenames = new String[0];;
    
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
    private int timeOutInSeconds = DEFAULT_TIMEOUT_SECONDS;

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

    /**
     * This is the command executed before the run is executed.
     */
    private String executionPrepCommand = null;
    
    /**
     * Is this problem to be executed and validated per the CCS specification.
     */
    private boolean ccsMode = false;
     
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

    public Problem copy(String newDisplayName) {
        Problem clone = new Problem(newDisplayName);
        // inherited field
        clone.setSiteNumber(getSiteNumber());
        
        // local fields
        clone.setDisplayName(newDisplayName);
        // TODO is number really used?
        clone.setNumber(getNumber());
        // TODO FATAL files need the corresponding ProblemDataFile populated...
        clone.setDataFileName(StringUtilities.cloneString(dataFileName));
        clone.setAnswerFileName(StringUtilities.cloneString(answerFileName));
        clone.setActive(isActive());
        clone.setReadInputDataFromSTDIN(isReadInputDataFromSTDIN());
        clone.setTimeOutInSeconds(getTimeOutInSeconds());
        clone.setValidatedProblem(isValidatedProblem());
        clone.setUsingPC2Validator(isUsingPC2Validator());
        clone.setWhichPC2Validator(getWhichPC2Validator());
        clone.setValidatorCommandLine(StringUtilities.cloneString(validatorCommandLine));
        clone.setValidatorProgramName(StringUtilities.cloneString(validatorProgramName));
        clone.setInternationalJudgementReadMethod(isInternationalJudgementReadMethod());

        // TODO Implement Commands to be executed before a problem is run
        // private String executionPrepCommand = "";
        // private SerializedFile executionPrepFile;
        
        clone.setIgnoreSpacesOnValidation(isIgnoreSpacesOnValidation());
        clone.setShowValidationToJudges(isShowValidationToJudges());
        clone.setHideOutputWindow(isHideOutputWindow());
        clone.setShowCompareWindow(isShowCompareWindow());
        clone.setComputerJudged(isComputerJudged());
        clone.setManualReview(isManualReview());
        clone.setPrelimaryNotification(isPrelimaryNotification());
        clone.letter = StringUtilities.cloneString(letter);
        clone.shortName = StringUtilities.cloneString(shortName);
        
        if (getNumberTestCases() > 1){
            for (int i = 0 ; i < getNumberTestCases(); i++){
                String datafile = StringUtilities.cloneString(getDataFileName(i + 1));
                String answerfile = StringUtilities.cloneString(getAnswerFileName(i + 1));
                clone.addTestCaseFilenames(datafile, answerfile);
            }
        }
        
        return clone;
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
        return dataFileName;
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
            return dataFileName;
        }
        return testCaseDataFilenames[testCaseNumber - 1];
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
     * @param validated
     *            Set to true if the problem uses a validator.
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
    


    public boolean isSameAs(Problem problem) {

        try {
            if (problem == null){
                return false;
            }
            if (! StringUtilities.stringSame(displayName, problem.getDisplayName())){
                return false;
            }
            if (isActive() != problem.isActive()) {
                return false;
            }
            if (timeOutInSeconds != problem.getTimeOutInSeconds()) {
                return false;
            }

            if (! StringUtilities.stringSame(dataFileName, problem.getDataFileName())) {
                return false;
            }
            if (! StringUtilities.stringSame(answerFileName, problem.getAnswerFileName())) {
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
            if (! StringUtilities.stringSame(validatorProgramName, problem.getValidatorProgramName())) {
                return false;
            }
            if (! StringUtilities.stringSame(validatorCommandLine, problem.getValidatorCommandLine())) {
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
            if (computerJudged != problem.isComputerJudged()) {
                return false;
            }
            if (manualReview != problem.isManualReview()) {
                return false;
            }
            if (prelimaryNotification != problem.isPrelimaryNotification()) {
                return false;
            }
            
            if (getSiteNumber() != problem.getSiteNumber()){
                return false;
            }
            
            if (! StringUtilities.stringSame(shortName, problem.getShortName())){
                return false;
            }
            
            if (! StringUtilities.stringSame(externalDataFileLocation, problem.getExternalDataFileLocation())){
                return false;
            }
            
            if (usingExternalDataFiles != problem.usingExternalDataFiles) {
                return false;
            }

            if (! StringUtilities.stringSame(shortName, problem.getShortName())){
                return false;
            }
            // TODO 917 - do isameAs when test case filenames can be added.
//            if (! StringUtilities.stringArraySame(testCaseDataFilenames, problem.getTestCaseDataFilenames())) {
//                return false;
//            }
//            if (! StringUtilities.stringArraySame(testCaseAnswerFilenames, problem.getTestCaseAnswerFilenames())) {
//                return false;
//            }
            
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
        if (testCaseDataFilenames != null && testCaseDataFilenames.length > 0) {
            return testCaseDataFilenames.length;
        } else if (getAnswerFileName() != null && getDataFileName() != null) {
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

    /**
     * Is this a CCS standard problem?.
     * 
     * A CCS standard problem will:
     * <li> Execute the team submission and send the test data via stdin.
     * <li> Validate the program output by sending the team output via stdin to the validator.
     * 
     * @return
     */
    public boolean isCcsMode() {
        return ccsMode;
    }

    public void setCcsMode(boolean ccsMode) {
        this.ccsMode = ccsMode;
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
}
