package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.ui.EditProblemPane;

/**
 * Test for Problem class.
 * 
 * @author pc2@ecs.csus.edu
 * 
 * @Version $Id$
 */

// $HeadURL$
public class ProblemTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();

    }

    /**
     * Get a new populated Problem
     * 
     * @return Problem
     */
    private Problem getProblemAnew() {
        Problem p2 = new Problem("Problem One");
        p2.setActive(true);
        p2.setAnswerFileName("sumit.dat");
        p2.setDataFileName("sumit.dat");
        p2.setHideOutputWindow(true);

        p2.setValidatedProblem(true);
        p2.setUsingPC2Validator(true);
        p2.setWhichPC2Validator(3);
        p2.setIgnoreSpacesOnValidation(true);

        p2.setValidatorCommandLine(EditProblemPane.DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND + " -pc2 " + p2.getWhichPC2Validator() + " " + p2.isIgnoreSpacesOnValidation());
        p2.setValidatorProgramName(Problem.INTERNAL_VALIDATOR_NAME);

        p2.setReadInputDataFromSTDIN(false);
        p2.setShowCompareWindow(true);
        p2.setTimeOutInSeconds(120);

        p2.setSiteNumber(4);

        return p2;
    }

    public void checkString(String title, String expected, String found, Problem problem1, Problem problem2) {
        assertFalse("Is same as, should not be: " + title + " expected " + expected + " found " + found, problem1.isSameAs(problem2));
        assertFalse("Is same as, should not be:  " + title + " expected " + found + " found " + expected, problem2.isSameAs(problem1));
    }

    // private void checkString(String string, boolean b, boolean c, Problem p1, Problem p2) {
    public void checkBoolean(String title, boolean expected, boolean found, Problem problem1, Problem problem2) {
        assertFalse("Is same as, should not be:  " + title + " expected " + expected + " found " + found, problem1.isSameAs(problem2));
        assertFalse("Is same as, should not be:  " + title + " expected " + found + " found " + expected, problem2.isSameAs(problem1));
    }

    // private void checkString(String string, int siteNumber, int siteNumber2, Problem p1, Problem p2) {
    public void checkString(String title, int expected, int found, Problem problem1, Problem problem2) {
        assertFalse("Is same as, should not be:  " + title + " expected " + expected + " found " + found, problem1.isSameAs(problem2));
        assertFalse("Is same as, should not be:  " + title + " expected " + found + " found " + expected, problem2.isSameAs(problem1));
    }

    public void testIsSameIs() {

        Problem p1 = getProblemAnew();
        Problem p2 = getProblemAnew();

        assertTrue("Is same As", p1.isSameAs(p2));
        assertFalse("Is not same As, null parameter", p1.isSameAs(null));

        p2 = getProblemAnew();
        p2.setValidatedProblem(false);
        assertFalse("Is not same as, setValidatedProblem ", p1.isSameAs(p2));
        assertFalse("Is not same as, setValidatedProblem ", p2.isSameAs(p1));

        p2 = getProblemAnew();
        p2.setTimeOutInSeconds(1);
        assertFalse("Is not same as, setTimeOutInSeconds 1 ", p1.isSameAs(p2));
        assertFalse("Is not same as, setTimeOutInSeconds 1 ", p2.isSameAs(p1));

        p2 = getProblemAnew();
        p2.setDataFileName(null);
        checkString("setDataFileName null", p1.getDataFileName(), p2.getDataFileName(), p1, p2);

        p2 = getProblemAnew();
        p2.setDataFileName("foo");
        checkString("setDataFileName foo", p1.getDataFileName(), p2.getDataFileName(), p1, p2);

        p2 = getProblemAnew();
        p2.setAnswerFileName(null);
        checkString("setAnswerFileName null", p1.getAnswerFileName(), p2.getAnswerFileName(), p1, p2);

        p2 = getProblemAnew();
        p2.setAnswerFileName("foo");
        checkString("setAnswerFileName foo", p1.getAnswerFileName(), p2.getAnswerFileName(), p1, p2);

        p2 = getProblemAnew();
        p2.setValidatorProgramName(null);
        checkString("setValidatorProgramName null", p1.getValidatorProgramName(), p2.getValidatorProgramName(), p1, p2);

        p2 = getProblemAnew();
        p2.setValidatorProgramName("foo");
        checkString("setValidatorProgramName foo", p1.getValidatorProgramName(), p2.getValidatorProgramName(), p1, p2);

        p2 = getProblemAnew();
        p2.setReadInputDataFromSTDIN(true);
        checkBoolean("setReadInputDataFromSTDIN foo", p1.isReadInputDataFromSTDIN(), p2.isReadInputDataFromSTDIN(), p1, p2);

        p2 = getProblemAnew();
        p2.setValidatedProblem(false);
        checkBoolean("setValidatedProblem foo", p1.isValidatedProblem(), p2.isValidatedProblem(), p1, p2);

        p2 = getProblemAnew();
        p2.setUsingPC2Validator(false);
        checkBoolean("setUsingPC2Validator", p1.isUsingPC2Validator(), p2.isUsingPC2Validator(), p1, p2);

        p2 = getProblemAnew();
        p2.setValidatorCommandLine(null);
        checkString("setValidatorCommandLine", p1.getValidatorCommandLine(), p2.getValidatorCommandLine(), p1, p2);

        p2 = getProblemAnew();
        p2.setShowValidationToJudges(true);
        checkBoolean("setShowValidationToJudges", p1.isShowValidationToJudges(), p2.isShowValidationToJudges(), p1, p2);

        p2 = getProblemAnew();
        p2.setHideOutputWindow(false);
        checkBoolean("setHideOutputWindow", p1.isHideOutputWindow(), p2.isHideOutputWindow(), p1, p2);

        p2 = getProblemAnew();
        p2.setShowCompareWindow(false);
        checkBoolean("setShowCompareWindow", p1.isShowCompareWindow(), p2.isShowCompareWindow(), p1, p2);

        p2 = getProblemAnew();
        p2.setIgnoreSpacesOnValidation(false);
        checkBoolean("setIgnoreSpacesOnValidation", p1.isIgnoreSpacesOnValidation(), p2.isIgnoreSpacesOnValidation(), p1, p2);

        p2 = getProblemAnew();
        p2.setWhichPC2Validator(2);
        checkString("setWhichPC2Validator 2", p1.getWhichPC2Validator(), p2.getWhichPC2Validator(), p1, p2);

        p2 = getProblemAnew();
        p2.setWhichPC2Validator(12);
        checkString("setWhichPC2Validator 12", p1.getWhichPC2Validator(), p2.getWhichPC2Validator(), p1, p2);

        p2 = getProblemAnew();
        p2.setSiteNumber(9);
        checkString(" setSiteNumber 9", p1.getSiteNumber(), p2.getSiteNumber(), p1, p2);

        p2 = getProblemAnew();
        p2.setDisplayName("Different Problem Name");
        checkString("setDisplayName", p1.getDisplayName(), p2.getDisplayName(), p1, p2);
        
        p2 = getProblemAnew();
        p2.setDataLoadYAMLPath("/tmp");
        checkString("setDataLoadYAMLPath", p1.getDataLoadYAMLPath(), p2.getDataLoadYAMLPath(), p1, p2);
        
        p2 = getProblemAnew();
        p2.setUsingExternalDataFiles(true);
        checkBoolean("setUsingExternalDataFiles", p1.isUsingExternalDataFiles(), p2.isUsingExternalDataFiles(), p1, p2);
    }
    
    public void testCopy() {
        Problem p1 = getProblemAnew();
        // if display name is different isSameAs will fail
        Problem p2 = p1.copy(p1.getDisplayName());
        assertTrue("copy failed to clone", p1.isSameAs(p2));
        // TODO consider adding introspection to verify all fields were copied properly
    }

    
    // TODO when short name implemented use this test
//    public void testValidShortName() {
//
//        Problem p1 = getProblemAnew();
//
//        String [] badPathNames = { //
//                File.separator+ "temp",
//                "C:temp",
//        };
//
//        for (String name : badPathNames) {
//            try {
//                p1.setShortName(name);
//            } catch (Exception e) {
//                name = "ok";
//            }
//        }
//    }
    
}
