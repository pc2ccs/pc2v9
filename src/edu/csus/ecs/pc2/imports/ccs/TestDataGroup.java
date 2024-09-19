// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.error.MarkedYAMLException;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.exception.YamlLoadException;

/**
 * Parameters for the representation of a group of test data
 *
 * @author John Buck, PC^2 Team, pc2@ecs.csus.edu
 */
public class TestDataGroup {
    
    public static final String TESTDATA_YAML = "testdata.yaml";
    private static final String SCORING_KEY = "scoring";
    private static final String SCORE_KEY = "score";
    private static final String UNBOUNDED_SCORE = "unbounded";
    private static final String AGGREGATION_TYPE = "aggregation";
    private static final String REQUIRE_PASS = "require-pass";
    private static final String INPUT_VALIDATOR_ARGS = "input_validator_args";
    private static final String OUTPUT_VALIDATOR_ARGS = "output_validator_args";
    private static final String FULL_FEEDBACK = "full_feedback";
    
    private String groupName = null;
    
    private HashMap<String, String> inputValidatorArgs = new HashMap<String, String>();
    
    private TestDataGroupScoringInfo scoring = new TestDataGroupScoringInfo();
    
    private String outputValidatorArgs = "";
    
    private boolean staticValidation = false;
    
    private boolean fullFeedback = false;

    public TestDataGroup(String groupName, TestDataGroup parentGroup) {
        this.groupName = groupName;
        
        if(parentGroup != null) {
            // Copy inherited fields
            fullFeedback = parentGroup.isFullFeedback();
            // Shallow copy is fine - strings don't change
            getInputValidatorArgs().putAll(parentGroup.getInputValidatorArgs());
            outputValidatorArgs = parentGroup.outputValidatorArgs;
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
        
        try {
            content = ContestImportUtilities.loadYaml(testDataYamlFile);
        } catch (MarkedYAMLException e) {
            throw new YamlLoadException("DataGroup Yaml parsing error", e, testDataYamlFile);
        }
        if(content != null) {
            //  The scoring key is a map that has an entry for each test data group giving it's scoring parameters
            Map<String, Object> scoremap = ContestImportUtilities.fetchMap(content, SCORING_KEY);
            
            String score = ContestImportUtilities.fetchValue(scoremap, SCORE_KEY);
            if(score == null) {
                throw new YamlLoadException("TestDatagroup Yaml error: missing 'score' property for " + getGroupName() + " in " + testDataYamlFile);
            }
            if(score.equals(UNBOUNDED_SCORE)) {
                getScoring().setUnbounded(true);
            } else {
                getScoring().setScore(StringUtilities.getIntegerValue(score, -1));
                if(getScoring().getScore() == -1) {
                    throw new YamlLoadException("TestDatagroup Yaml error: bad 'score' property for " + getGroupName() + " in " + testDataYamlFile);
                }
            }
            String aggregation = ContestImportUtilities.fetchValue(scoremap, AGGREGATION_TYPE);
            if(aggregation != null) {
                boolean aFound = false;
                for (TestDataGroupScoringInfo.AggregationType c : TestDataGroupScoringInfo.AggregationType.values()) {
                    if (c.name().equalsIgnoreCase(aggregation)) {
                        getScoring().setAggregation(c);
                        aFound = true;
                    }
                }
                if(!aFound) {
                    throw new YamlLoadException("TestDatagroup Yaml error: bad 'aggregation' property for " + getGroupName() + " in " + testDataYamlFile);
                }
            }
            Object [] prereq = ContestImportUtilities.fetchList(scoremap, REQUIRE_PASS).toArray();
            String [] preReqArray = new String[prereq.length];
            int i = 0;
            for(Object oReq : prereq) {
                preReqArray[i++] = (String)oReq;
            }
            getScoring().setRequire_pass(preReqArray);
            
            HashMap<String, Object> inValMap = (HashMap<String, Object>)ContestImportUtilities.fetchMap(content, INPUT_VALIDATOR_ARGS);
            if(inValMap == null) {
                String inValArgs = ContestImportUtilities.fetchValue(content, INPUT_VALIDATOR_ARGS);
                if(!"".equals(inValArgs)) {
                    inputValidatorArgs.clear();
                    inputValidatorArgs.put("*", inValArgs);
                }
            } else {
                inputValidatorArgs.clear();

                for (Map.Entry<String, Object> entry : inValMap.entrySet()) {
                    inputValidatorArgs.put(entry.getKey(), (String)entry.getValue());
                }
            }
            outputValidatorArgs = ContestImportUtilities.fetchValue(content, OUTPUT_VALIDATOR_ARGS, outputValidatorArgs);
            fullFeedback = ContestImportUtilities.fetchBooleanValue(content, FULL_FEEDBACK, fullFeedback);
            result = true;
        }
        
        return(result);
    }
    
    public boolean readTestCases() {
        return false;
    }
    
    /**
     * @return the staticValidation
     */
    public boolean isStaticValidation() {
        return staticValidation;
    }

    /**
     * @param staticValidation the staticValidation to set
     */
    public void setStaticValidation(boolean staticValidation) {
        this.staticValidation = staticValidation;
    }

    /**
     * @return the fullFeedback
     */
    public boolean isFullFeedback() {
        return fullFeedback;
    }

    /**
     * @param fullFeedback the fullFeedback to set
     */
    public void setFullFeedback(boolean fullFeedback) {
        this.fullFeedback = fullFeedback;
    }

    /**
     * @return the outputValidatorArgs
     */
    public String getOutputValidatorArgs() {
        return outputValidatorArgs;
    }

    /**
     * @param outputValidatorArgs the outputValidatorArgs to set
     */
    public void setOutputValidatorArgs(String outputValidatorArgs) {
        this.outputValidatorArgs = outputValidatorArgs;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @return the scoring
     */
    public TestDataGroupScoringInfo getScoring() {
        return scoring;
    }

    /**
     * @return the inputValidatorArgs
     */
    public HashMap<String, String> getInputValidatorArgs() {
        return inputValidatorArgs;
    }
}
