// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.util.HashMap;

import edu.csus.ecs.pc2.core.exception.YamlLoadException;
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
        String inputTestDirectory = getDataDirectory(this.getName()) + File.separator + "data" + File.separator;
        String testDir = getOutputDataDirectory();
        String secretDir = inputTestDirectory + "secret" + File.separator;
        String sampleDir = inputTestDirectory + "sample" + File.separator;
        String testDataFile = secretDir + TestDataGroup.TESTDATA_YAML;
        String group1DataFile = secretDir + "group1" + File.separator + TestDataGroup.TESTDATA_YAML;
        String group2DataFile = secretDir + "group2" + File.separator + TestDataGroup.TESTDATA_YAML;
        String group3DataFile = secretDir + "group3" + File.separator + TestDataGroup.TESTDATA_YAML;
        
        removeDirectory(testDir);
        ensureDirectory(testDir);
        
        TestDataGroup secret = new TestDataGroup("secret", null);
        assertEquals("Expecting test data group name ", "secret", secret.getGroupName());
        assertFalse("Expecting fullfeeback to be false", secret.isFullFeedback());
        assertEquals("Expecting empty output validator args for secret group ", "", secret.getOutputValidatorArgs());
        assertTrue("Expecting no input validator args", secret.getInputValidatorArgs().isEmpty());
        if(!secret.processDataYaml(testDataFile)) {
            this.failTest("Missing " + testDataFile);
        }
        TestDataGroupScoringInfo secretinfo = secret.getScoring();
        assertFalse("Expecting score, not unbounded", secretinfo.isUnbounded());
        assertEquals("Expecting score ", secretinfo.getScore(), 250);
        assertEquals("Expecting aggregation type ", TestDataGroupScoringInfo.AggregationType.MIN.name(), secretinfo.getAggregation().name());
        assertEquals("Expecting prerequisite ", "sample", secretinfo.getRequire_pass()[0]);
        
        // now test to see if the basic stuff was updated after reading the testdata.yaml file
        assertEquals("Expecting output validator arguments ", "outputarg", secret.getOutputValidatorArgs());
        HashMap<String, String> inValArgs = secret.getInputValidatorArgs();
        assertEquals("Expecting inputValidatorArgument count ", 1, inValArgs.size());
        assertEquals("Expecting inputValidatorArgument ", "test arg", inValArgs.get("*"));
        assertFalse("Expecting fullfeeback to be false", secret.isFullFeedback());

        // group1 is under secret
        TestDataGroup group1 = new TestDataGroup("secret/group1", secret);
        assertEquals("Expecting test data group name ", "secret/group1", group1.getGroupName());
        assertFalse("Expecting fullfeeback to be false", group1.isFullFeedback());
        // test inheritance
        secret.setFullFeedback(true);
        group1 = new TestDataGroup("secret/group1", secret);
        assertEquals("Expecting test data group name ", "secret/group1", group1.getGroupName());
        assertTrue("Expecting fullfeeback to be true", group1.isFullFeedback());
        secret.setFullFeedback(false);
        assertEquals("Expecting group1 output validator arguments ", "outputarg", group1.getOutputValidatorArgs());
        
        if(!group1.processDataYaml(group1DataFile)) {
            this.failTest("Missing " + group1DataFile);
        }
        TestDataGroupScoringInfo group1secretinfo = group1.getScoring();
        assertFalse("Expecting score, not unbounded", group1secretinfo.isUnbounded());
        assertEquals("Expecting score ", group1secretinfo.getScore(), 145);
        assertEquals("Expecting aggregation type ", TestDataGroupScoringInfo.AggregationType.MIN.name(), group1secretinfo.getAggregation().name());
        assertEquals("Expecting prerequisite 1 ", "sample", group1secretinfo.getRequire_pass()[0]);
        
        // now test to see if the basic stuff was updated after reading the testdata.yaml file
        assertEquals("Expecting output validator arguments ", "outputarg group1", group1.getOutputValidatorArgs());
        inValArgs = group1.getInputValidatorArgs();
        assertEquals("Expecting inputValidatorArgument count ", 1, inValArgs.size());
        assertEquals("Expecting inputValidatorArgument ", "test arg group1", inValArgs.get("*"));
        assertFalse("Expecting fullfeeback to be false", group1.isFullFeedback());
    

        // group2 is under secret
        TestDataGroup group2 = new TestDataGroup("secret/group2", secret);
        assertEquals("Expecting test data group name ", "secret/group2", group2.getGroupName());
        assertFalse("Expecting fullfeeback to be false", group2.isFullFeedback());
        // test inheritance
        secret.setFullFeedback(true);
        group2 = new TestDataGroup("secret/group2", secret);
        assertEquals("Expecting test data group name ", "secret/group2", group2.getGroupName());
        assertTrue("Expecting fullfeeback to be true", group2.isFullFeedback());
        secret.setFullFeedback(false);
        assertEquals("Expecting group2 output validator arguments ", "outputarg", group2.getOutputValidatorArgs());
        
        if(!group2.processDataYaml(group2DataFile)) {
            this.failTest("Missing " + group2DataFile);
        }
        TestDataGroupScoringInfo group2secretinfo = group2.getScoring();
        assertFalse("Expecting score, not unbounded", group2secretinfo.isUnbounded());
        assertEquals("Expecting score ", group2secretinfo.getScore(), 55);
        assertEquals("Expecting aggregation type ", TestDataGroupScoringInfo.AggregationType.SUM.name(), group2secretinfo.getAggregation().name());
        assertEquals("Expecting prerequisite 1 ", "sample", group2secretinfo.getRequire_pass()[0]);
        assertEquals("Expecting prerequisite 1 ", "secret/group1", group2secretinfo.getRequire_pass()[1]);
        
        // now test to see if the basic stuff was updated after reading the testdata.yaml file
        assertEquals("Expecting output validator arguments ", "outputarg group2", group2.getOutputValidatorArgs());
        inValArgs = group2.getInputValidatorArgs();
        assertEquals("Expecting inputValidatorArgument count ", 2, inValArgs.size());
        assertEquals("Expecting inputValidatorArgument 1 ", "group2 ival1arg", inValArgs.get("ival1"));
        assertEquals("Expecting inputValidatorArgument 2 ", "group2 ival2arg", inValArgs.get("ival2"));
        assertFalse("Expecting fullfeeback to be false", group2.isFullFeedback());
        
        // group3 is under secret and has no testdata.yaml
        TestDataGroup group3 = new TestDataGroup("secret/group3", secret);
        assertEquals("Expecting test data group name ", "secret/group3", group3.getGroupName());
        assertFalse("Expecting fullfeeback to be false", group3.isFullFeedback());
        // test inheritance - causes group's fullfeedback to be true
        secret.setFullFeedback(true);
        group3 = new TestDataGroup("secret/group3", secret);
        assertEquals("Expecting test data group name ", "secret/group3", group3.getGroupName());
        assertTrue("Expecting fullfeeback to be true", group3.isFullFeedback());
        secret.setFullFeedback(false);
        assertEquals("Expecting group3 output validator arguments ", "outputarg", group3.getOutputValidatorArgs());
        
        try {
            if(group3.processDataYaml(group3DataFile)) {
                this.failTest("File is present and should not be " + group3DataFile);
            }
        } catch(YamlLoadException e) {
            // This is good.
        }
        TestDataGroupScoringInfo group3secretinfo = group3.getScoring();
        assertFalse("Expecting score, not unbounded", group3secretinfo.isUnbounded());
        assertEquals("Expecting score ", group3secretinfo.getScore(), 0);
        // SUM is the default, and agg type is NOT inherited.
        assertEquals("Expecting aggregation type ", TestDataGroupScoringInfo.AggregationType.SUM.name(), group3secretinfo.getAggregation().name());
        
        assertEquals("Expecting prequisites ", 0, group3secretinfo.getRequire_pass().length);
        // now test to see if the basic stuff was updated after reading the testdata.yaml file
        assertEquals("Expecting output validator arguments ", "outputarg", group3.getOutputValidatorArgs());
        inValArgs = group3.getInputValidatorArgs();
        assertEquals("Expecting inputValidatorArgument count ", 1, inValArgs.size());
        assertEquals("Expecting inputValidatorArgument ", "test arg", inValArgs.get("*"));
        assertTrue("Expecting fullfeeback to be true", group3.isFullFeedback());
        
    }
}
