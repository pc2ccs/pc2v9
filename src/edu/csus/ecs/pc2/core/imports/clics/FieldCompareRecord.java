// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.util.logging.Level;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.report.ComparisonState;
import edu.csus.ecs.pc2.core.report.FileComparisonUtilities;

/**
 * Information about a comparison of two fields.
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FieldCompareRecord {

    @JsonProperty
    private ComparisonState state = ComparisonState.UNDEFINED;

    @JsonProperty
    private String key = null;

    @JsonProperty
    private String fieldName;

    @JsonProperty
    private String valueOne;

    @JsonProperty
    private String valueTwo;

    @JsonProperty
    private String details;

    public FieldCompareRecord(String fieldName, String valueOne, String valueTwo, String details) {
        this(fieldName, valueOne, valueTwo, details, null);
    }
    
    public FieldCompareRecord(String fieldName, String valueOne, String valueTwo, String details, String key) {
        super();
        this.fieldName = fieldName;
        this.valueOne = valueOne;
        this.valueTwo = valueTwo;
        this.details = details;
        this.key = key;

        createComparisonState(valueOne, valueTwo);
    }

    public void createComparisonState(String value1, String value2) {
//        TODO REFACTOR move this into ComparisonState

        if (value1 == null && value2 == null) {
            state = ComparisonState.BOTH_MISSING;
        } else if (value1 == null) {
            state = ComparisonState.MISSING_SOURCE;
        } else if (value2 == null) {
            state = ComparisonState.MISSING_TARGET;
        } else if (value1.contentEquals(value2)) {
            state = ComparisonState.SAME;
        } else {
            state = ComparisonState.NOT_SAME;
        }
    }

    public ComparisonState getState() {
        return state;
    }

    public void setState(ComparisonState state) {
        this.state = state;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValueOne() {
        return valueOne;
    }

    public void setValueOne(String valueOne) {
        this.valueOne = valueOne;
    }

    public String getValueTwo() {
        return valueTwo;
    }

    public void setValueTwo(String valueTwo) {
        this.valueTwo = valueTwo;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns a JSON string representation of this SerializedFile object.
     * 
     * @return a String containing a JSON representation of this object, or null if an error occurs during creation of the JSON string
     */
    // TODO REFACTOR Move into a JSON Utility class
    public String toJSON() {

        // TODO REFACTOR Update getObjectMapper to use configs below.
        ObjectMapper objectMapper = FileComparisonUtilities.getObjectMapper();

        try {
            String jsonString = objectMapper.writeValueAsString(this);
            return jsonString;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            if (StaticLog.getLog() != null) {
                StaticLog.getLog().log(Level.WARNING, "Error writing JSON for object " + this.getClass().getName(), e);
            }
            return null;
        }
    }
    
    @Override
    public String toString() {
        return toJSON();
    }

}
