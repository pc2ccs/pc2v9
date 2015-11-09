package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;

/**
 * This class defines an extended kind of {@link Problem}: a Problem which contains multiple test sets.
 * The class derives from {@link Problem} and therefore contains all attributes of a regular {@link Problem};
 * it also contains a collection of test cases which are objects of type {@link TestCaseFileNameSet}.
 * Each {@link TestCaseFileNameSet} contains a test case number plus test case data file and test case
 * answer file names (either or both of which may be null).
 * 
 * 
 * @see edu.csus.ecs.pc2.core.list.ProblemList
 * @see edu.csus.ecs.pc2.core.model.ProblemDataFiles
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: Problem.java 3176 2015-10-11 04:00:37Z laned $
 */
// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/model/Problem.java $
public class ProblemNew extends Problem {

    private static final long serialVersionUID = 1L;
    
    /**
     * The collection of test case file name sets (pairs) for the Problem.
     */
    private TestCaseFileNameCollection testCases = new TestCaseFileNameCollection();
    

    /**
     * Create a problem with the display name.
     * 
     * @param displayName
     */
    public ProblemNew(String displayName) {
        super(displayName);
    }

    /**Copy (clone) this ProblemNew into a new ProblemNew instance
     * 
     */
    public ProblemNew copy(String newDisplayName) {
        ProblemNew clone = new ProblemNew(newDisplayName);

        //copy fields inherited from parent (Problem) class to clone
        clone.setAnswerFileName(StringUtilities.cloneString(super.getAnswerFileName()));
        //TODO:  need a setter to set the value returned by getCCSfileDirectory();
        clone.setColorName(StringUtilities.cloneString(super.getColorName()));
        clone.setColorRGB(StringUtilities.cloneString(super.getColorRGB()));
        clone.setDataFileName(StringUtilities.cloneString(super.getDataFileName()));
        //TODO: need a setter for the value returned by super.getDataFileName(testCaseNumber)
        clone.setDisplayName(StringUtilities.cloneString(super.getDisplayName()));
        //clone.setElementId(super.getElementId());  //elementID is set by the ProblemNew constructor's call to super()  
        clone.setExecutionPrepCommand(StringUtilities.cloneString(super.getExecutionPrepCommand()));
        clone.setExternalDataFileLocation(super.getExternalDataFileLocation());
        clone.setLetter(StringUtilities.cloneString(super.getLetter()));
        clone.setNumber(super.getNumber()); // TODO is number really used?
        //TODO: need a setter for the value returned by super.getNumberTestCases()
        //clone.setNumTestCases(super.getNumberTestCases());
        clone.setShortName(StringUtilities.cloneString(super.getShortName()));
        clone.setSiteNumber(super.getSiteNumber());
        clone.setState(super.getState());
        clone.setTimeOutInSeconds(super.getTimeOutInSeconds());
        clone.setValidatorCommandLine(StringUtilities.cloneString(super.getValidatorCommandLine()));
        clone.setValidatorProgramName(StringUtilities.cloneString(super.getValidatorProgramName()));
        clone.setWhichPC2Validator(super.getWhichPC2Validator());
        
        clone.setActive(super.isActive());
        clone.setCcsMode(super.isCcsMode());
        clone.setComputerJudged(super.isComputerJudged());
        clone.setHideOutputWindow(super.isHideOutputWindow());
        clone.setIgnoreSpacesOnValidation(super.isIgnoreSpacesOnValidation());
        clone.setInternationalJudgementReadMethod(super.isInternationalJudgementReadMethod());
        clone.setManualReview(super.isManualReview());
        clone.setPrelimaryNotification(super.isPrelimaryNotification());
        clone.setReadInputDataFromSTDIN(super.isReadInputDataFromSTDIN());
        clone.setShowCompareWindow(super.isShowCompareWindow());
        clone.setShowValidationToJudges(super.isShowValidationToJudges());
        clone.setUsingExternalDataFiles(super.isUsingExternalDataFiles());
        clone.setUsingPC2Validator(super.isUsingPC2Validator());
        clone.setValidatedProblem(super.isValidatedProblem());
        //TODO need a setter for ValidShortName
        //clone.setValidShortName(super.isValidShortName());
  
        //copy fields from this subclass to clone
        clone.setTestCaseFiles((TestCaseFileNameCollection)testCases.clone());
        clone.setInternationalJudgementReadMethod(isInternationalJudgementReadMethod());

        return clone;
    }

    /**
     * Makes the specified Collection as the current collection of Test Case File Name Sets for this Problem.
     * @param testCases - a Collection of TestCaseFileNameSets 
     */
    private void setTestCaseFiles(TestCaseFileNameCollection testCases) {
       this.testCases = testCases;
    }

    /**
     * @see Object#equals(java.lang.Object).
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ProblemNew) {
            ProblemNew otherProblem = (ProblemNew) obj;
            return this.getElementId().equals(otherProblem.getElementId());
        } else {
            throw new ClassCastException("expected a Problem found: " + obj.getClass().getName());
        }
    }

    
    /**
     * Returns the answer file name associated with the specified test case. 
     * Note that if there is more than one test case with the same test case number,
     * the answer file name associated with the first such test case is returned 
     * (note also that this is probably an error in the system/config).
     * 
     * Test case numbers start at 1.
     * 
     * @return returns the answer file name for the first test case matching the specified test case number, or null if either
     *          no such test case number exists or if the answer file name for the test case is null.
     */
    public String getAnswerFileName(int testCaseNumber) {
        if (testCaseNumber < 1) {
            StaticLog.getLog().log(Log.WARNING, "Problem.getAnswerFileName(): illegal test case number: " + testCaseNumber + " (must be >= 1)");
            return null;
        } else {
            for (TestCaseFileNameSet tc : testCases) {
                if (tc.getTestCaseNum() == testCaseNumber) {
                    return tc.getAnswerFileName();
                }
            }
            // no such test case found
            return null;
        }
    }


    /**
     * Returns the data file name associated with the specified test case. 
     * Note that if there is more than one test case with the same test case number,
     * the data file name associated with the first such test case is returned 
     * (note also that this is probably an error in the system/config).
     * 
     * Test case numbers start at 1.
     * 
     * @return returns the data file name for the first test case matching the specified test case number, or null if either
     *          no such test case number exists or if the data file name for the test case is null.
     */
    public String getDataFileName(int testCaseNumber) {
        if (testCaseNumber < 1) {
            StaticLog.getLog().log(Log.WARNING, "Problem.getDataFileName(): illegal test case number: " + testCaseNumber + " (must be >= 1)");
            return null;
        } else {
            for (TestCaseFileNameSet tc : testCases) {
                if (tc.getTestCaseNum() == testCaseNumber) {
                    return tc.getDataFileName();
                }
            }
            // no such test case found
            return null;
        }
    }



    public boolean isSameAs(Problem problem) {

        try {
            if (problem == null){
                return false;
            }
            if (!(problem instanceof ProblemNew)) {
                return false;
            }
            if (! StringUtilities.stringSame(this.getDisplayName(), problem.getDisplayName())){
                return false;
            }
            if (isActive() != problem.isActive()) {
                return false;
            }
            if (getTimeOutInSeconds() != problem.getTimeOutInSeconds()) {
                return false;
            }

            if (! StringUtilities.stringSame(getDataFileName(), problem.getDataFileName())) {
                return false;
            }
            if (! StringUtilities.stringSame(getAnswerFileName(), problem.getAnswerFileName())) {
                return false;
            }
            if (isReadInputDataFromSTDIN() != problem.isReadInputDataFromSTDIN()) {
                return false;
            }
            
            if (isValidatedProblem() != problem.isValidatedProblem()) {
                return false;
            }
            if (isUsingPC2Validator() != problem.isUsingPC2Validator()) {
                return false;
            }
            if (getWhichPC2Validator() != problem.getWhichPC2Validator()) {
                return false;
            }
            if (! StringUtilities.stringSame(getValidatorProgramName(), problem.getValidatorProgramName())) {
                return false;
            }
            if (! StringUtilities.stringSame(getValidatorCommandLine(), problem.getValidatorCommandLine())) {
                return false;
            }
            if (isIgnoreSpacesOnValidation() != problem.isIgnoreSpacesOnValidation()) {
                return false;
            }
            if (isShowValidationToJudges() != problem.isShowValidationToJudges()) {
                return false;
            }
            
            if (isHideOutputWindow() != problem.isHideOutputWindow()) {
                return false;
            }
            if (isShowCompareWindow() != problem.isShowCompareWindow()) {
                return false;
            }
            if (isComputerJudged() != problem.isComputerJudged()) {
                return false;
            }
            if (isManualReview() != problem.isManualReview()) {
                return false;
            }
            if (isPrelimaryNotification() != problem.isPrelimaryNotification()) {
                return false;
            }
            
            if (getSiteNumber() != problem.getSiteNumber()){
                return false;
            }
            
            if (! StringUtilities.stringSame(getShortName(), problem.getShortName())){
                return false;
            }
            
            if (! StringUtilities.stringSame(getExternalDataFileLocation(), problem.getExternalDataFileLocation())){
                return false;
            }
            
            if (isUsingExternalDataFiles() != problem.isUsingExternalDataFiles()) {
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

            
    /**
     * Add a test case consisting of a test case number, plus data and/or answer filenames, to the Problem's collection of test case file names.
     * Calling this method inserts a new TestCaseFileNameSet into the collection.
     * If the specified test case number is less than 1 or if a test case of that number already exists in the collection,
     * the method logs an error and returns false (without affecting the collection).
     * Either or both of the specified (received) data file and answer file names may be null, in which case
     * the new TestCaseFileNameSet has null for the corresponding field.
     *  
     * @see #removeAllTestCaseFilenames()
     * @param testCaseNum The unique identifier number for this test case
     * @param datafile The data input file for this test case (may be null)
     * @param answerfile The judge's answer file for this test case (may be null)
     * @return true if the test case was successfully added; false otherwise
     */
    public boolean addTestCaseFileNames (int testCaseNum, String datafile, String answerfile){
        
        if (testCaseNum<1) {
            return false;
        }
        //verify there's not already a test case of the specified number
        for (TestCaseFileNameSet tc : testCases) {
            if (tc.getTestCaseNum() == testCaseNum) {
                StaticLog.getLog().log(Log.WARNING, "Problem.addTestCaseFileNames(): illegal attempt to add existing test case number: " + testCaseNum) ;
                return false;
            }
        }
        //we've been given a legit new test case; add it to the collection
        TestCaseFileNameSet tc = new TestCaseFileNameSet();
        tc.setTestCaseNum(testCaseNum);
        tc.setDataFileName(StringUtilities.cloneString(datafile));
        tc.setAnswerFileName(StringUtilities.cloneString(answerfile));
        return testCases.add(tc);
    }
    
    
    /**
     * Remove all test cases from the Problem's collection of test cases.
     */
    public void removeAllTestCaseFileNames(){
        testCases = new TestCaseFileNameCollection();
    }
    
    /**
     * Returns the number of test cases currently associated with this Problem.
     * Note that what is returned is a count of the number of Test Case File Name Sets
     * associated with the problem; each Test Case File Name Set may have a data file name,
     * an answer file name, both, or neither (that is, one or both entries in the set may
     * be null).   Note also that test case NUMBERs do not necessarily correspond to the count
     * of test cases; for example, there could be three test cases whose numbers are 1, 5, and 9.
     * 
     * @return the number test cases associated with this Problem
     */
    public int getNumberTestCases() {
        return testCases.size();
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
    
}
