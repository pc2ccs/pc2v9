package edu.csus.ecs.pc2.ui;

import java.util.ArrayList;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO move under test/

// $HeadURL$
public class EditProblemPaneTest extends AbstractTestCase {

    protected SampleContest sample = new SampleContest();

    public void testSimpleLoadSave() throws Exception {

        // TODO 917 uncomment when edit problem pane is done.
//        String testDirectoryName = getOutputDataDirectory(this.getName());
//        EditProblemPane pane = new EditProblemPane();
//
//        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);
//        IInternalController controller = sample.createController(contest, testDirectoryName, true, false);
//
//        addConsoleHandler(controller.getLog());
//
//        Problem problem = new Problem("Problem " + this.getName());
//
//        pane.setContestAndController(contest, controller);
//
//        pane.setProblem(problem);
//
//        Problem newProblem = pane.getProblemFromFields(null, null);
//
//        // compareProblems(problem, newProblem);
//
//        pane = null;

    }

    @SuppressWarnings("unused")
    private void compareProblems(Problem problem, Problem newProblem) throws Exception {

        Exception[] exceptions = compare(problem, newProblem);

        if (exceptions.length > 1) {

            for (Exception exception : exceptions) {
                System.out.println(exception);
            }
        }

        if (exceptions.length > 0) {
            throw exceptions[0];
        }

        assertTrue("Expect Same problem ", problem.isSameAs(newProblem));

    }

    public Exception[] compare(Problem problem, Problem newProblem) {

        ArrayList<Exception> list = new ArrayList<Exception>();

        try {

            if (problem == null) {
                throw new InvalidFieldValue("source problem is null");
            }
            if (!StringUtilities.stringSame(newProblem.getDisplayName(), problem.getDisplayName())) {
                list.add(new InvalidFieldValue("getDisplayName " + newProblem.getDisplayName() + " vs " + problem.getDisplayName()));
            }
            if (newProblem.isActive() != problem.isActive()) {
                list.add(new InvalidFieldValue("if (isActive() != problem.isActive()) {"));
            }
            if (newProblem.getTimeOutInSeconds() != problem.getTimeOutInSeconds()) {
                list.add(new InvalidFieldValue("if (timeOutInSeconds != problem.getTimeOutInSeconds()) {"));
            }

            if (!StringUtilities.stringSame(newProblem.getDataFileName(), problem.getDataFileName())) {
                list.add(new InvalidFieldValue("if (!StringUtilities.stringSame(dataFileName, problem.getDataFileName())) {"));
            }
            if (!StringUtilities.stringSame(newProblem.getAnswerFileName(), problem.getAnswerFileName())) {
                list.add(new InvalidFieldValue("if (!StringUtilities.stringSame(answerFileName, problem.getAnswerFileName())) {"));
            }
            if (!newProblem.isReadInputDataFromSTDIN() == problem.isReadInputDataFromSTDIN()) {
                list.add(new InvalidFieldValue("if (!readInputDataFromSTDIN == problem.isReadInputDataFromSTDIN()) {"));
            }

            if (newProblem.isValidatedProblem() != problem.isValidatedProblem()) {
                list.add(new InvalidFieldValue("if (validatedProblem != problem.isValidatedProblem()) {"));
            }
            if (newProblem.isUsingPC2Validator() != problem.isUsingPC2Validator()) {
                list.add(new InvalidFieldValue("if (usingPC2Validator != problem.isUsingPC2Validator()) {"));
            }
            if (newProblem.getWhichPC2Validator() != problem.getWhichPC2Validator()) {
                list.add(new InvalidFieldValue("if (whichPC2Validator != problem.getWhichPC2Validator()) {"));
            }
            if (!StringUtilities.stringSame(newProblem.getValidatorProgramName(), problem.getValidatorProgramName())) {
                list.add(new InvalidFieldValue("if (!StringUtilities.stringSame(validatorProgramName, problem.getValidatorProgramName())) {"));
            }
            if (!StringUtilities.stringSame(newProblem.getValidatorCommandLine(), problem.getValidatorCommandLine())) {
                list.add(new InvalidFieldValue("if (!StringUtilities.stringSame(validatorCommandLine, problem.getValidatorCommandLine())) {"));
            }
            if (newProblem.isIgnoreCaseOnValidation() != problem.isIgnoreCaseOnValidation()) {
                list.add(new InvalidFieldValue("if (ignoreSpacesOnValidation != problem.isIgnoreSpacesOnValidation()) {"));
            }
            if (newProblem.isShowValidationToJudges() != problem.isShowValidationToJudges()) {
                list.add(new InvalidFieldValue("if (showValidationToJudges != problem.isShowValidationToJudges()) {"));
            }

            if (newProblem.isHideOutputWindow() != problem.isHideOutputWindow()) {
                list.add(new InvalidFieldValue("if (hideOutputWindow != problem.isHideOutputWindow()) {"));
            }
            if (newProblem.isShowCompareWindow() != problem.isShowCompareWindow()) {
                list.add(new InvalidFieldValue("if (showCompareWindow != problem.isShowCompareWindow()) {"));
            }
            if (newProblem.isComputerJudged() != problem.isComputerJudged()) {
                list.add(new InvalidFieldValue("if (computerJudged != problem.isComputerJudged()) {"));
            }
            if (newProblem.isManualReview() != problem.isManualReview()) {
                list.add(new InvalidFieldValue("if (manualReview != problem.isManualReview()) {"));
            }
            if (newProblem.isPrelimaryNotification() != problem.isPrelimaryNotification()) {
                list.add(new InvalidFieldValue("if (prelimaryNotification != problem.isPrelimaryNotification()) {"));
            }

            if (newProblem.getSiteNumber() != problem.getSiteNumber()) {
                list.add(new InvalidFieldValue("if (getSiteNumber() != problem.getSiteNumber()) {"));
            }

            if (!StringUtilities.stringSame(newProblem.getShortName(), problem.getShortName())) {
                list.add(new InvalidFieldValue("if (!StringUtilities.stringSame(shortName, problem.getShortName())) {"));
            }

            if (!StringUtilities.stringSame(newProblem.getExternalDataFileLocation(), problem.getExternalDataFileLocation())) {
                list.add(new InvalidFieldValue("if (!StringUtilities.stringSame(externalDataFileLocation, problem.getExternalDataFileLocation())) {"));
            }

            if (newProblem.isUsingExternalDataFiles() != problem.isUsingExternalDataFiles()) {
                list.add(new InvalidFieldValue("if (usingExternalDataFiles != problem.usingExternalDataFiles) {"));
            }

            if (!StringUtilities.stringSame(newProblem.getShortName(), problem.getShortName())) {
                list.add(new InvalidFieldValue("if (!StringUtilities.stringSame(shortName, problem.getShortName())) {"));
            }

            // TODO 917 - do isameAs when test case filenames can be added.
            // if (! StringUtilities.stringArraySame(testCaseDataFilenames, problem.getTestCaseDataFilenames())) {
            // }
            // if (! StringUtilities.stringArraySame(testCaseAnswerFilenames, problem.getTestCaseAnswerFilenames())) {
            // }

        } catch (Exception e) {
            list.add(e);
        }

        return (Exception[]) list.toArray(new Exception[list.size()]);

    }

    public void testCompDirs() throws Exception {
        
        // TODO 917 uncomment when edit problem pane is done. 

//        EditProblemPane pane = new EditProblemPane();
//        String result = pane.compareDirectories("export/A", "export/B");
//        
//        assertEquals("All 3 matching", result);
        
        
    }
    
    
    /**
     * Test showFilesDiff.
     * 
     * @throws Exception
     */
    public void aTestDiffView() throws Exception {
        
        
        // TODO 917 uncomment when edit problem pane is done.
//        String hello = getSamplesSourceFilename(HELLO_SOURCE_FILENAME);
//        String sumit = getSamplesSourceFilename(SUMIT_SOURCE_FILENAME);
//        
//        new EditProblemPane().showFilesDiff(hello, sumit);
//        
//        int ms = 1000;
//        Thread.sleep(4 * ms);
        
        
    }


}
