// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * A record comparing two field values.
 * 
 * Provides info for the field name, a comparison 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ContestCompareRecord {

    @JsonProperty
    private String eventType;

    @JsonProperty
    private String id;

    @JsonProperty
    private ComparisonState state = ComparisonState.UNDEFINED;

    @JsonProperty
    private String fieldName;

    @JsonProperty
    private String valueOne;

    @JsonProperty
    private String valueTwo;

    /**
     * Create Compare Record.
     * 
     * @param eventType event type name, ex. teams, problems.
     * @param id unique id for the record
     * @param fieldName name of field, ex. name
     * @param valueOne contest model value
     * @param valueTwo event feed value
     */
    public ContestCompareRecord(String eventType, String id, String fieldName, String valueOne, String valueTwo) {
        this.eventType = eventType;
        this.id = id;
        this.fieldName = fieldName;
        this.valueOne = valueOne;
        this.valueTwo = valueTwo;

        if (valueOne == null && valueTwo == null) {
            state = ComparisonState.BOTH_MISSING;
        } else if (valueOne == null) {
            // data 2 not null
            state = ComparisonState.MISSING_SOURCE;
        } else if (valueTwo == null) {
            state = ComparisonState.MISSING_TARGET;
        } else if (valueOne.contentEquals(valueTwo)) {
            state = ComparisonState.SAME;
        } else {
            state = ComparisonState.NOT_SAME;
        }
    }

    /**
     * Get the comparison of the fields. 
     * 
     * @return the comparison of the fields.
     */
    public ComparisonState getState() {
        return state;
    }

    /**
     * Provide comparison info.
     */
    @Override
    public String toString() {

        String context = getEventType() + " " + getId() + " ";

        switch (state) {
            case BOTH_MISSING:
                return context + "Both source and target do not exists (are null)";
            case MISSING_SOURCE:
                return context + "Missing source value, found target value=" + valueTwo;
            case MISSING_TARGET:
                return context + "Missing target value, found source value=" + valueOne;
            case NOT_SAME:
                return context + "Different values '" + valueOne + "' feed value '" + valueTwo + "'";
            case SAME:
                return context + "Identical value '" + valueOne + "'";
            default:
                return context + "Unknown state " + state.toString();
        }

    }

    public String toJSON() throws JsonProcessingException {
        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }
    
    public String getEventType() {
        return eventType;
    }

    public String getId() {
        return id;
    }

    public String getFieldName() {
        return fieldName;
    }

    /**
     * Get description of values,  this vs that.
     * @return
     */
    public String getvs() {
        String val1 = "'"+valueOne+"'";
        if (valueOne == null) {
            val1 = "null";
        }
        String val2 = "'"+valueTwo+"'";
        if (valueTwo == null) {
            val2 = "null";
        }
        
        return val1 + " vs "+val2;
    }

}
