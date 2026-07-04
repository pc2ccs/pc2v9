// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.yaml.snakeyaml.error.MarkedYAMLException;

import edu.csus.ecs.pc2.core.exception.YamlLoadException;
import edu.csus.ecs.pc2.core.log.Log;

/**
 * Parameters for the representation of a group of test data
 *
 * @author John Buck, PC^2 Team, pc2@ecs.csus.edu
 */
public class TestDataGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SAMPLE_GROUP = "sample";
    public static final String SECRET_GROUP = "secret";
    public static final String TESTDATA_YAML = "testdata.yaml";
    private static final String ON_REJECT_KEY = "on_reject";
    private static final String GRADING_KEY = "grading";
    private static final String GRADER_FLAGS_KEY = "grader_flags";
    private static final String INPUT_VALIDATOR_FLAGS_KEY = "input_validator_flags";
    private static final String OUTPUT_VALIDATOR_FLAGS_KEY = "output_validator_flags";
    private static final String ACCEPT_SCORE_KEY = "accept_score";
    private static final String REJECT_SCORE_KEY = "reject_score";
    private static final String RANGE_KEY = "range";

    public enum OnRejectTypes {
        BREAK,
        CONTINUE;
    }

    public enum GradingTypes {
        DEFAULT,
        CUSTOM;
    }

    // Defaults
    private static final double DEFAULT_ACCEPT_SCORE = 1.0;
    private static final double DEFAULT_REJECT_SCORE = 0.0;
    private static final double DEFAULT_RANGE_MIN = Double.NEGATIVE_INFINITY;
    private static final double DEFAULT_RANGE_MAX = Double.POSITIVE_INFINITY;


    private OnRejectTypes on_reject = OnRejectTypes.BREAK;
    private GradingTypes grading = GradingTypes.DEFAULT;
    private String grader_flags = "";
    private String input_validator_flags = "";
    private String output_validator_flags = "";
    private double accept_score = DEFAULT_ACCEPT_SCORE;
    private double reject_score = DEFAULT_REJECT_SCORE;
    private double range_min = DEFAULT_RANGE_MIN;
    private double range_max = DEFAULT_RANGE_MAX;

    private String groupName = null;

    private String dataDirectoryName = null;

    private ArrayList<TestDataGroup> subGroups = new ArrayList<TestDataGroup>();
    private ArrayList<TestCaseInfo> testCases = new ArrayList<TestCaseInfo>();
    private TestDataGroup parent = null;

    // This is the total of all test cases in this group and all groups under it.
    int totalTestCases = 0;

    public TestDataGroup(String groupName, String baseDataDirectoryName, TestDataGroup parentGroup) {
        this.groupName = groupName;
        this.parent = parentGroup;
        this.dataDirectoryName = baseDataDirectoryName;

        if(parentGroup != null) {
            on_reject = parentGroup.on_reject;
            grading = parentGroup.grading;
            input_validator_flags = parentGroup.input_validator_flags;
            output_validator_flags = parentGroup.output_validator_flags;
            accept_score = parentGroup.accept_score;
            reject_score = parentGroup.reject_score;
            range_min = parentGroup.range_min;
            range_max = parentGroup.range_max;
        }
    }

    /**
     * Read the data group's specification file
     * @param testDataYamlFile - file name to read
     * @return true if the file exists and was processed successfully
     *         false if no file exists
     * @throws YamlLoadException on errors
     */
    public boolean processDataYaml(String testDataYamlFile) {
        boolean result = false;
        Map<String, Object> content = null;

        if(new File(testDataYamlFile).isFile()) {
            try {
                content = ContestImportUtilities.loadYaml(testDataYamlFile);
            } catch (MarkedYAMLException e) {
                throw new YamlLoadException("DataGroup Yaml parsing error", e, testDataYamlFile);
            }
            if(content != null) {

                String val = ContestImportUtilities.fetchValue(content, ON_REJECT_KEY);
                if(val != null) {
                    if(val.equalsIgnoreCase(OnRejectTypes.BREAK.toString())) {
                        on_reject = OnRejectTypes.BREAK;
                    } else if(val.equalsIgnoreCase(OnRejectTypes.CONTINUE.toString())) {
                        on_reject = OnRejectTypes.CONTINUE;
                    } else {
                        throw new YamlLoadException("TestDatagroup Yaml error: invalid value for '" + ON_REJECT_KEY + "' property for " + getGroupName() + " in " + testDataYamlFile);
                    }
                }

                val = ContestImportUtilities.fetchValue(content, GRADING_KEY);
                if(val != null) {
                    if(val.equalsIgnoreCase(GradingTypes.CUSTOM.toString())) {
                        grading = GradingTypes.CUSTOM;
                    } else if(val.equalsIgnoreCase(GradingTypes.DEFAULT.toString())) {
                        grading = GradingTypes.DEFAULT;
                    } else {
                        throw new YamlLoadException("TestDatagroup Yaml error: invalid value for '" + GRADING_KEY + "' property for " + getGroupName() + " in " + testDataYamlFile);
                    }
                }
                grader_flags = ContestImportUtilities.fetchValue(content, GRADER_FLAGS_KEY);
                input_validator_flags = ContestImportUtilities.fetchValue(content, INPUT_VALIDATOR_FLAGS_KEY);
                output_validator_flags = ContestImportUtilities.fetchValue(content, OUTPUT_VALIDATOR_FLAGS_KEY);

                Double dval = ContestImportUtilities.fetchDoubleValue(content, ACCEPT_SCORE_KEY);
                if(dval != null) {
                    accept_score = dval.doubleValue();
                }
                dval = ContestImportUtilities.fetchDoubleValue(content, REJECT_SCORE_KEY);
                if(dval != null) {
                    reject_score = dval.doubleValue();
                }

                val = ContestImportUtilities.fetchValue(content, RANGE_KEY);
                if(val != null) {
                    String [] rangeParts = val.split("\\s+");

                    if(rangeParts.length != 2) {
                        throw new YamlLoadException("TestDatagroup Yaml error: bad '" + RANGE_KEY + "' property for " + getGroupName() + " in " + testDataYamlFile);
                    } else {
                        double r1, r2;
                        try {
                            range_min = Double.parseDouble(rangeParts[0]);
                            range_max = Double.parseDouble(rangeParts[1]);
                        } catch(Exception e) {
                            throw new YamlLoadException("TestDatagroup Yaml error: bad values for '" + RANGE_KEY + "' property for " + getGroupName() + " in " + testDataYamlFile);
                        }

                    }
                }
                result = true;
            }
        }
        return(result);
    }

    /**
     * Processes the data directory (sample and secret groups) and recurses
     * The dataDirectoryName is the "data" directory and may only have "sample" and "secret" and a
     * testdata.yaml.  Anything else is ignored (at the top level).
     *
     * @param dataDirectoryName - path to the "data" directory - can be relative.
     * @param log - for logging
     * @return true if all test cases were successfully read AND there ARE test cases.
     */
    public boolean readTestCases(Log log) {
        TestDataGroup group;
        String testDataYaml;

        // First process top level testdata yaml file
        testDataYaml = this.dataDirectoryName + File.separator + TESTDATA_YAML;

        if(processDataYaml(testDataYaml) == false) {
            log.log(Log.INFO, "Did not find " + testDataYaml + " for top level data - using default");
        }
        group = readTestDataGroup(SAMPLE_GROUP, this, log);
        if(group == null) {
            log.log(Log.INFO, "Did not find sample data group in " + this.dataDirectoryName);
        } else {
            subGroups.add(group);
            totalTestCases += group.getTotalTestCases();
        }
        group = readTestDataGroup(SECRET_GROUP, this, log);
        if(group == null) {
            throw new YamlLoadException("Did not find required secret data group in " + this.dataDirectoryName);
        }
        subGroups.add(group);
        totalTestCases += group.getTotalTestCases();
        return(totalTestCases > 0);
    }

    /**
     * Reads the test cases for a group, processes its optional testdata.yaml, and
     * processes subgroups.
     *
     * @param groupName path relative to "data" of the group
     * @return TestDataGroup representing the groupName
     */
    private TestDataGroup readTestDataGroup(String groupName, TestDataGroup parent, Log log) {

        TestDataGroup newGroup = null;
        String groupDirectoryName = dataDirectoryName + File.separator + groupName;
        File dir = new File(groupDirectoryName);

        if(dir.isDirectory()) {
            // First process this group's testdata yaml file
            String testDataYaml = groupDirectoryName + File.separator + TESTDATA_YAML;
            newGroup = new TestDataGroup(groupName, dataDirectoryName, parent);

            if(newGroup.processDataYaml(testDataYaml) == false) {
                log.log(Log.INFO, "Did not find " + testDataYaml + " for top level data - using default from " + parent.getGroupName());
            }
            String [] files = dir.list();
            String ansFileName, inFileName;

            if(files != null) {
                ArrayList<String> subDirs = new ArrayList<String>();
                HashSet<String> ansFiles = new HashSet<String>();

                Arrays.sort(files);
                // Make up a hash set of answer files
                for(String file : files) {
                    if(file.endsWith(TestCaseInfo.TEST_CASE_ANSWER_EXTENSION)) {
                        ansFiles.add(file);
                    }
                }
                for(String file : files) {
                    inFileName = groupDirectoryName + File.separator + file;
                    if(file.endsWith(TestCaseInfo.TEST_CASE_INPUT_EXTENSION)) {
                        ansFileName = file.substring(0, file.lastIndexOf('.')) + TestCaseInfo.TEST_CASE_ANSWER_EXTENSION;
                        // Ignore .in files with no .ans file
                        if(!ansFiles.contains(ansFileName)) {
                            log.log(Log.WARNING, "There is an input file (" + file + ") with no answer file in " + groupDirectoryName);
                            continue;
                        }
                        // Make sure they are both text files
                        if(!(new File(inFileName).isFile())) {
                            log.log(Log.WARNING, "The input file (" + inFileName + ") is not a file - ignored");
                            continue;
                        }
                        ansFileName = groupDirectoryName + File.separator + ansFileName;
                        if(!(new File(ansFileName).isFile())) {
                            log.log(Log.WARNING, "The answer file (" + ansFileName + ") is not a file - input and answer are ignored");
                            continue;
                        }
                        newGroup.testCases.add(new TestCaseInfo(inFileName, ansFileName, newGroup));
                        newGroup.totalTestCases++;
                    } else if(!ansFiles.contains(file)){
                        // remember list of directories (new groups) we found - we do this after doing all the .in and .ans files
                        if(new File(inFileName).isDirectory()) {
                            subDirs.add(groupName + File.separator + file);
                        }
                    }
                }
                TestDataGroup newSubGroup;
                // Process new subgroups
                for(String subDir : subDirs) {
                    newSubGroup = newGroup.readTestDataGroup(subDir, newGroup, log);
                    if(newSubGroup != null) {
                        newGroup.subGroups.add(newSubGroup);
                        totalTestCases += newSubGroup.getTotalTestCases();
                    }
                }
            }
        }
        return newGroup;
    }

    /**
     * Append all subcases in (and under) this testgroup to the supplied ArrayList
     *
     * @param arInfo List of all TestCaseInfo's in and under this group.
     */
    private void appendTestCaseInfo(ArrayList<TestCaseInfo> arInfo) {
        arInfo.addAll(testCases);
        for(TestDataGroup group : subGroups) {
            group.appendTestCaseInfo(arInfo);
        }
    }

    public ArrayList<TestCaseInfo> getAllTestCaseInfo() {
        ArrayList<TestCaseInfo> arInfo = new ArrayList<TestCaseInfo>();
        appendTestCaseInfo(arInfo);
        return(arInfo);
    }

    public TestDataGroup getParent() {
        return parent;
    }

    public int getTotalTestCases() {
        return totalTestCases;
    }

    public String getDataDirectoryName() {
        return dataDirectoryName;
    }

    public ArrayList<TestDataGroup> getTestDataGroups() {
        return subGroups;
    }

    public ArrayList<TestCaseInfo> getTestCaseInfo() {
        return testCases;
    }

    public boolean isOnRejectBreak() {
        return on_reject.equals(OnRejectTypes.BREAK);
    }

    public boolean isOnRejectContinue() {
        return on_reject.equals(OnRejectTypes.CONTINUE);
    }

    public boolean isGradingDefault() {
        return grading.equals(GradingTypes.DEFAULT);
    }

    public boolean isGradingCustom() {
        return grading.equals(GradingTypes.CUSTOM);
    }

    public void setAcceptScore(double score) {
        accept_score = score;
    }

    public double getAcceptScore() {
        return accept_score;
    }

    public void setRejectScore(double score) {
        reject_score = score;
    }

    public double getRejectScore() {
        return reject_score;
    }

    /**
     * @return the Grader Flags
     */
    public String getGraderFlags() {
        return grader_flags;
    }

    /**
     * @return the Input Validator Flags
     */
    public String getInputValidatorFlags() {
        return input_validator_flags;
    }

    /**
     * @return the Output Validator Flags
     */
    public String getOutputValidatorFlags() {
        return output_validator_flags;
    }

    /**
     * @return the minimum score
     */
    public double getRangeMin() {
        return range_min;
    }

    /**
     * @return the maximum score
     */
    public double getRangeMax() {
        return range_max;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }
}
