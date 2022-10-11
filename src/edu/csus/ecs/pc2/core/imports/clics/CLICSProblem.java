// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * A JSON representation for the CLICS API problems json data.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class CLICSProblem {

    // {time_limit=1, color=null, name=Match Game, id=matchgame, label=A, test_data_count=48, rgb=null, ordinal=1}
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String time_limit;

    @JsonProperty
    private String color;

    @JsonProperty
    private String label;

    @JsonProperty
    private Integer test_data_count;

    @JsonProperty
    private String rgb;

    @JsonProperty
    private Integer ordinal;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTime_limit() {
        return time_limit;
    }

    public String getColor() {
        return color;
    }

    public String getLabel() {
        return label;
    }

    public Integer getTest_data_count() {
        return test_data_count;
    }

    public String getRgb() {
        return rgb;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public String toJSON() throws JsonProcessingException {

        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }

}
