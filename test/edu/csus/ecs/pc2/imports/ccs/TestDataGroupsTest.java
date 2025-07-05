// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.util.ArrayList;

import edu.csus.ecs.pc2.core.exception.YamlLoadException;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;

/**
 * Test the Data Groups classes
 *
 * @author John Buck
 *
 */
public class TestDataGroupsTest extends AbstractTestCase {
    private String loadDir = "testdata" + File.separator;

    private SampleContest sample = new SampleContest();

    @Override
    protected void setUp() throws Exception {
        String projectPath = JUnitUtilities.locate(loadDir);
        if (projectPath == null) {
            throw new Exception("Unable to locate " + loadDir);
        }
        File dir = new File(projectPath + File.separator + loadDir);
        if (dir.exists()) {
            loadDir = dir.toString() + File.separator;
        } else {
            System.err.println("could not find " + loadDir);
            throw new Exception("Unable to locate " + loadDir);
        }
        super.setUp();
    }

    /**
     * Tests whether parsing of the testdata.yaml files work
     *
     * @throws Exception
     */
    public void testTestDataGroup() throws Exception {
        String inputTestDirectory = getDataDirectory(this.getName()) + File.separator + "data";
        String testDir = getOutputDataDirectory();
        String secretDir = inputTestDirectory + File.separator + "secret" + File.separator;
        String sampleDir = inputTestDirectory + File.separator + "sample" + File.separator;
        String testDataFile = secretDir + TestDataGroup.TESTDATA_YAML;
        String group1DataFile = secretDir + "group1" + File.separator + TestDataGroup.TESTDATA_YAML;
        String group2DataFile = secretDir + "group2" + File.separator + TestDataGroup.TESTDATA_YAML;
        String group3DataFile = secretDir + "group3" + File.separator + TestDataGroup.TESTDATA_YAML;

        removeDirectory(testDir);
        ensureDirectory(testDir);

        TestDataGroup secret = new TestDataGroup("secret", inputTestDirectory, null);
        assertEquals("Expecting test data group name ", "secret", secret.getGroupName());
        assertTrue("Expecting on_reject of break", secret.isOnRejectBreak());
        assertTrue("Expecting grading of default", secret.isGradingDefault());
        assertTrue("Expecting no output validator flags for secret group ",
                secret.getOutputValidatorFlags() == null || secret.getOutputValidatorFlags().isEmpty());
        assertTrue("Expecting no input validator flags for secret group",
                secret.getInputValidatorFlags() == null || secret.getInputValidatorFlags().isEmpty());
        assertEquals("Expecting accept_score of 1 ", 1.0, secret.getAcceptScore());
        assertEquals("Expecting reject_score of 0 ", 0.0, secret.getRejectScore());
        // read yaml file
        if(!secret.processDataYaml(testDataFile)) {
            this.failTest("Missing " + testDataFile);
        }
        assertTrue("Expecting on_reject of continue", secret.isOnRejectContinue());
        assertTrue("Expecting grading of custom", secret.isGradingCustom());
        assertEquals("Expecting output validator flags for secret group ", "nothing here either", secret.getOutputValidatorFlags());
        assertTrue("Expecting no input validator flags for secret group", secret.getInputValidatorFlags() == null || secret.getInputValidatorFlags().isEmpty());
        assertEquals("Expecting accept_score of 100 ", 100.0, secret.getAcceptScore());
        assertEquals("Expecting reject_score of 1 ", 1.0, secret.getRejectScore());
        assertEquals("Expecting min range to be 0 ", 0.0, secret.getRangeMin());
        assertEquals("Expecting maz range to be 100 ", 100.0, secret.getRangeMax());


        // group1 is under secret
        TestDataGroup group1 = new TestDataGroup("secret/group1", inputTestDirectory, secret);
        assertEquals("Expecting test data group name ", "secret/group1", group1.getGroupName());

        // test inheritance
        double defaultAcceptScore = secret.getAcceptScore();
        secret.setAcceptScore(12345.5);
        group1 = new TestDataGroup("secret/group1", inputTestDirectory, secret);
        assertEquals("Expecting test data group name ", "secret/group1", group1.getGroupName());
        assertEquals("Expecting accept_score to be 12345.5 ", 12345.5, group1.getAcceptScore());
        // set it back
        secret.setAcceptScore(defaultAcceptScore);
        assertEquals("Expecting group1 output validator flags ", "nothing here either", group1.getOutputValidatorFlags());

        if(!group1.processDataYaml(group1DataFile)) {
            this.failTest("Missing " + group1DataFile);
        }
        assertTrue("Expecting on_reject of break ", group1.isOnRejectBreak());
        assertEquals("Expecting accept_score of 145 ", 145.0, group1.getAcceptScore());

        // now test to see if the basic stuff was updated after reading the testdata.yaml file
        assertEquals("Expecting output validator flags ", "outputarg group1", group1.getOutputValidatorFlags());
        assertEquals("Expecting input validator flags ", "test arg group1", group1.getInputValidatorFlags());
        assertEquals("Expecting min range to be 0 ", 0.0, group1.getRangeMin());
        assertEquals("Expecting max range to be 25 ", 25.0, group1.getRangeMax());


        // group2 is under secret
        TestDataGroup group2 = new TestDataGroup("secret/group2", inputTestDirectory, secret);
        assertEquals("Expecting test data group name ", "secret/group2", group2.getGroupName());
        // test inheritance
        defaultAcceptScore = secret.getAcceptScore();
        secret.setAcceptScore(12345.5);
        group2 = new TestDataGroup("secret/group2", inputTestDirectory, secret);
        assertEquals("Expecting test data group name ", "secret/group2", group2.getGroupName());
        assertEquals("Expecting accept_score to be 12345.5 ", 12345.5, group2.getAcceptScore());
        // set it back
        secret.setAcceptScore(defaultAcceptScore);
        assertEquals("Expecting group2 output validator flags ", "nothing here either", group2.getOutputValidatorFlags());

        if(!group2.processDataYaml(group2DataFile)) {
            this.failTest("Missing " + group2DataFile);
        }
        assertTrue("Expecting on_reject of continue ", group2.isOnRejectContinue());

        // now test to see if the basic stuff was updated after reading the testdata.yaml file
        assertEquals("Expecting accept_score of 75 ", 75.0, group2.getAcceptScore());
        assertEquals("Expecting output validator flags ", "outputarg group2", group2.getOutputValidatorFlags());
        assertEquals("Expecting input validator flags ", "group2 ival1arg ival2arg", group2.getInputValidatorFlags());
        assertEquals("Expecting min range to be 0 ", 0.0, group2.getRangeMin());
        assertEquals("Expecting max range to be 100000 ", 100000.0, group2.getRangeMax());

        // group3 is under secret and has no testdata.yaml
        TestDataGroup group3 = new TestDataGroup("secret/group3", inputTestDirectory, secret);
        assertEquals("Expecting test data group name ", "secret/group3", group3.getGroupName());

        try {
            if(group3.processDataYaml(group3DataFile)) {
                this.failTest("File is present and should not be " + group3DataFile);
            }
        } catch(YamlLoadException e) {
            // This is good.
        }
        // these should be copied directly from secret group
        assertTrue("Expecting on_reject of continue", group3.isOnRejectContinue());
        assertTrue("Expecting grading of custom", group3.isGradingCustom());
        assertEquals("Expecting output validator flags for secret group ", "nothing here either", group3.getOutputValidatorFlags());
        assertTrue("Expecting no input validator flags for secret group", group3.getInputValidatorFlags() == null || group3.getInputValidatorFlags().isEmpty());
        assertEquals("Expecting accept_score of 100 ", 100.0, group3.getAcceptScore());
        assertEquals("Expecting reject_score of 1 ", 1.0, group3.getRejectScore());
        assertEquals("Expecting min range to be 0 ", 0.0, group3.getRangeMin());
        assertEquals("Expecting maz range to be 100 ", 100.0, group3.getRangeMax());

    }

    /**
     * Tests whether reading a data folder and its subgroups works
     *
     * @throws Exception
     */
    public void testReadDataGroups() throws Exception {
        // Use same directory as for previous test
        String inputTestDirectory = getDataDirectory("testTestDataGroup") + File.separator + "data";
        String testDir = getOutputDataDirectory();

        ensureStaticLog();
        TestDataGroup secret = new TestDataGroup("data", inputTestDirectory, null);
        assertTrue(secret.readTestCases(StaticLog.getLog()));
        assertEquals("Expecting total test case of 16 ", 16, secret.getTotalTestCases());
        ArrayList<TestDataGroup> arGroups = secret.getTestDataGroups();
        assertEquals("Expecting subgroup count of 1 ", 1, arGroups.size());
        TestDataGroup group = arGroups.get(0);
        assertEquals("Expecting 3 testcases in group 1 ", 3, group.getTestDataGroups().get(0).getTotalTestCases());
        assertEquals("Expecting 4 testcases in group 2 ", 4, group.getTestDataGroups().get(1).getTotalTestCases());
        assertEquals("Expecting 9 testcases in group 3 ", 9, group.getTestDataGroups().get(2).getTotalTestCases());

        ArrayList<TestCaseInfo> arInfo = secret.getAllTestCaseInfo();
        System.out.println("There are " + arInfo.size() + " test cases:");
        for(TestCaseInfo tc : arInfo) {
            System.out.println(tc.toString());
        }
    }
}
