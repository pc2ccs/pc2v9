// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * A JSON representation for the CLICS API languages json data.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class CLICSLanguage {

    /**
     * short name, ex python3
     */
    @JsonProperty
    private String id;

    /**
     * Language title, ex Java or Python 3
     */
    @JsonProperty
    private String name;
    
    @JsonProperty
    String entry_point_required;

    @JsonProperty
    String entry_point_name;
    
    @JsonProperty
    String [] extensions;

    @JsonProperty
    String compiler;

    @JsonProperty
    String runner;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    

    public String [] getExtensions() {
        return extensions;
    }

    public String getCompiler() {
        return compiler;
    }

    public String getRunner() {
        return runner;
    }
    
    public String getEntry_point_required() {
        return entry_point_required;
    }

    public String getEntry_point_name() {
        return entry_point_name;
    }

    public String toJSON() throws JsonProcessingException {

        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }

}
