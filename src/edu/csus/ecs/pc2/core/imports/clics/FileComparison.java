// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.report.ComparisonState;
import edu.csus.ecs.pc2.core.report.FileComparisonUtilities;

/**
 * Comparison information between two files.
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FileComparison {

    @JsonProperty
    private String firstFilename;

    @JsonProperty
    private String secondFilename;

    @JsonProperty
    long numberRows;
    
    @JsonProperty
    long numberDifferences;

    @JsonProperty
    long numberComparedRecorderds;
    
    @JsonProperty
    private List<FieldCompareRecord> comparedFields = new ArrayList<FieldCompareRecord>();

    public FileComparison(String firstFilename, String secondFilename) {
        super();
        this.firstFilename = firstFilename;
        this.secondFilename = secondFilename;
    }

    public FileComparison(String firstFilename, String secondFilename, List<FieldCompareRecord> comparedFields) {
        super();
        this.firstFilename = firstFilename;
        this.secondFilename = secondFilename;
        this.comparedFields = comparedFields;
    }

    public FieldCompareRecord addfieldCompareRecord(FieldCompareRecord fieldCompareRecord) {
        
        comparedFields.add(fieldCompareRecord);
        
        numberComparedRecorderds ++;
        if (!ComparisonState.SAME.equals(fieldCompareRecord.getState())){
                numberDifferences++;
        }
        return fieldCompareRecord;
    }

    public String getFirstFilename() {
        return firstFilename;
    }

    public void setFirstFilename(String firstFilename) {
        this.firstFilename = firstFilename;
    }

    public String getSecondFilename() {
        return secondFilename;
    }

    public void setSecondFilename(String secondFilename) {
        this.secondFilename = secondFilename;
    }

    public long getNumberDifferences() {
        return numberDifferences;
    }

    public List<FieldCompareRecord> getComparedFields() {
        return comparedFields;
    }

    public void setComparedFields(List<FieldCompareRecord> comparedFields) {
        this.comparedFields = comparedFields;
    }
    
    public long getNumberComparedRecorderds() {
        return numberComparedRecorderds;
    }
    
    public void setNumberComparedRecorderds(long numberComparedRecorderds) {
        this.numberComparedRecorderds = numberComparedRecorderds;
    }
    
    public long getNumberRows() {
        return numberRows;
    }
    
    public void setNumberRows(long numberRows) {
        this.numberRows = numberRows;
    }
    
    /**
     * Returns a JSON string representation of this SerializedFile object.
     * 
     * @return a String containing a JSON representation of this object, or null if an error occurs during creation of the JSON string
     */
    // TODO REFACTOR Move into a JSON Utility class
    public String toJSON() {

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

}
