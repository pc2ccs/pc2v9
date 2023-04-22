// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.io.File;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Test for Problem class.
 * 
 * @author pc2@ecs.csus.edu
 */
public class ProblemTest extends AbstractTestCase {

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

        p2.setValidatorType(VALIDATOR_TYPE.PC2VALIDATOR);
        p2.getPC2ValidatorSettings().setWhichPC2Validator(3);
        p2.getPC2ValidatorSettings().setIgnoreCaseOnValidation(true);

        p2.setOutputValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND + " -pc2 " + p2.getWhichPC2Validator() 
                + " " + p2.getPC2ValidatorSettings().isIgnoreCaseOnValidation());
        p2.setOutputValidatorProgramName(Constants.PC2_VALIDATOR_NAME);

        p2.setReadInputDataFromSTDIN(false);
        p2.setShowCompareWindow(true);
        p2.setTimeOutInSeconds(120);

        p2.setLetter("F");

        p2.setSiteNumber(4);
        
        assertTrue("Expecting that all can view this problem ",p2.isAllView());
        
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
        p2.setValidatorType(VALIDATOR_TYPE.NONE);
        assertFalse("Is not same as, setValidatedProblem ", p1.isSameAs(p2));
        assertFalse("Is not same as, setValidatedProblem ", p2.isSameAs(p1));

        p2 = getProblemAnew();
        p2.setTimeOutInSeconds(1);
        assertFalse("Is not same as, setTimeOutInSeconds 1 ", p1.isSameAs(p2));
        assertFalse("Is not same as, setTimeOutInSeconds 1 ", p2.isSameAs(p1));

        p2 = getProblemAnew();
        p2.setMaxOutputSizeKB(10);
        assertFalse("Is not same as, setMaxOutputSizeKB 10 ", p1.isSameAs(p2));
        assertFalse("Is not same as, setMaxOutputSizeKB 10 ", p2.isSameAs(p1));

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

        //these tests are no longer valid since the default validator settings were changed to 'null'
//        p2 = getProblemAnew();
//        p2.setValidatorProgramName(null);
//        checkString("setValidatorProgramName null", p1.getValidatorProgramName(), p2.getValidatorProgramName(), p1, p2);

//        p2 = getProblemAnew();
//        p2.setValidatorProgramName("foo");
//        checkString("setValidatorProgramName foo", p1.getValidatorProgramName(), p2.getValidatorProgramName(), p1, p2);

        p2 = getProblemAnew();
        p2.setReadInputDataFromSTDIN(true);
        checkBoolean("setReadInputDataFromSTDIN foo", p1.isReadInputDataFromSTDIN(), p2.isReadInputDataFromSTDIN(), p1, p2);

        p2 = getProblemAnew();
        p2.setValidatorType(VALIDATOR_TYPE.NONE);
        checkBoolean("setValidatedProblem foo", p1.isValidatedProblem(), p2.isValidatedProblem(), p1, p2);

        p2 = getProblemAnew();
        p2.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);
        checkBoolean("setUsingPC2Validator", p1.isUsingPC2Validator(), p2.isUsingPC2Validator(), p1, p2);

        p2 = getProblemAnew();
        p2.setValidatorType(VALIDATOR_TYPE.CUSTOMVALIDATOR);
        checkBoolean("setUsingPC2Validator", p1.isUsingPC2Validator(), p2.isUsingPC2Validator(), p1, p2);

        //this test is no longer valid since the default validator settings were changed to 'null'
//        p2 = getProblemAnew();
//        p2.setValidatorCommandLine(null);
//        checkString("setValidatorCommandLine", p1.getValidatorCommandLine(), p2.getValidatorCommandLine(), p1, p2);

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
        p2.getPC2ValidatorSettings().setIgnoreCaseOnValidation(false);
        checkBoolean("setIgnoreSpacesOnValidation", p1.getPC2ValidatorSettings().isIgnoreCaseOnValidation(), 
                                                    p2.getPC2ValidatorSettings().isIgnoreCaseOnValidation(), p1, p2);

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
        p2.setExternalDataFileLocation("/tmp");
        checkString("setDataLoadYAMLPath", p1.getExternalDataFileLocation(), p2.getExternalDataFileLocation(), p1, p2);

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

    public void testgetNumberTestCases() {

        Problem probTestCase = new Problem("one");

        assertEquals("Test Cases count expected ", 0, probTestCase.getNumberTestCases());

        probTestCase.addTestCaseFilenames("data", "ansa");

        assertEquals("Test Cases count expected ", 1, probTestCase.getNumberTestCases());

        int count = 5;
        for (int i = probTestCase.getNumberTestCases(); i < count; i++) {
            probTestCase.addTestCaseFilenames("data" + i, "ansa" + i);
        }
        assertEquals("Test Cases count expected ", count, probTestCase.getNumberTestCases());

        probTestCase.removeAllTestCaseFilenames();
        assertEquals("Test Cases count expected ", 0, probTestCase.getNumberTestCases());

        Problem prob = new Problem("two");

        assertEquals("Test Cases count expected ", 0, prob.getNumberTestCases());

        prob.setDataFileName("data.dat");

        assertEquals("Test Cases count expected ", 1, prob.getNumberTestCases());

        prob.setAnswerFileName("data.ans");

        assertEquals("Test Cases count expected ", 1, prob.getNumberTestCases());

        Problem prob3 = getProblemAnew();

        assertEquals("Test Cases count expected ", 1, prob3.getNumberTestCases());

    }

    public void testInValidShortNames() {

        Problem p1 = getProblemAnew();

        String[] badPathNames = { //
        File.separator + "temp", //
                "C:temp", //
                "/tmp/name", //
                "\\tmp\\name", //
                null, //
        };

        for (String name : badPathNames) {
            p1.setShortName(name);
            assertFalse("Expecting bad problem short name '" + name + "'", p1.isValidShortName());
        }
    }

    public void testValidShortNames() {

        Problem p1 = getProblemAnew();

        String[] dataFile = { //
        "", //
                "A", //
                "_", //
                "()!@#$%^&{}|;',.?", //
                "sumit", //
                "FOO", //

        };

        for (String name : dataFile) {
            p1.setShortName(name);
            assertTrue("Expecting good problem short name '" + name + "'", p1.isValidShortName());
        }
    }

    public void testDefaultTimeout() throws Exception {

        Problem problem = new Problem("Foo");

        assertEquals("Time limit", Problem.DEFAULT_TIMEOUT_SECONDS, problem.getTimeOutInSeconds());
        assertEquals("Time limit", 30, problem.getTimeOutInSeconds());

    }
    
    public void testDefaultMaxOutputKB() throws Exception {

        Problem problem = new Problem("Bar");

        assertEquals("Max output", Problem.DEFAULT_MAX_OUTPUT_FILE_SIZE_KB, problem.getMaxOutputSizeKB());
    }
    
   /**
     * Test isSameas for balloon color and rgb.
     * 
     * @throws Exception
     */
    public void testSameAsBalloonColors() throws Exception {
        
        Problem p1 = getProblemAnew();
        Problem p2 = getProblemAnew();

        assertTrue("Is same As", p1.isSameAs(p2));
        
        p1.setColorName("White");
        assertFalse("Expecting diff from color", p1.isSameAs(p2));
        
        p1 = getProblemAnew();
        p1.setColorRGB("#444");
        assertFalse("Expecting diff from RGB", p1.isSameAs(p2));
        
        assertFalse("Is not same As, null parameter", p1.isSameAs(null));
        
    }
    
    /**
     * Test adding groups to a problem.
     * 
     * @throws Exception
     */
    public void testAddGroup() throws Exception {
        
        SampleContest sample =new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        sample.assignSampleGroups(contest, "Group Thing One", "Group Thing Two");
        
        Problem[] problems = contest.getProblems();
        
        Problem lastProbblem = problems[problems.length - 1];
        Group[] groups = contest.getGroups();
        
        
        Group group1 = groups[0];
        
        assertEquals("Number groups ", 2, groups.length);
        
        assertTrue (lastProbblem.isAllView());
        lastProbblem.addGroup(group1);
        
        assertTrue (lastProbblem.canView(group1));
        assertFalse (lastProbblem.canView(null));
        assertFalse (lastProbblem.canView(groups[1]));
        
        lastProbblem.clearGroups();
        assertTrue (lastProbblem.isAllView());
        
        Account[] teams = SampleContest.getTeamAccounts(contest);
        
        Group teamGroup = contest.getGroup(teams[0].getGroupId());
        
        lastProbblem.addGroup(teamGroup);
        assertTrue (lastProbblem.canView(teamGroup));
        
        Problem middleProblem = problems[problems.length / 2];
        for (Group group : groups) {
            middleProblem.addGroup(group);
        }
        
        assertFalse (middleProblem.isAllView());
        for (Group group : groups) {
            assertTrue (middleProblem.canView(group));
        }
        
        
//        ProblemsReport report = new ProblemsReport();
//        IInternalController controller = sample.createController(contest, true, false);
//        report.setContestAndController(contest, controller);
//        report.createReportFile("sample55",  null);
//        editFile("sample55");
    }
}
