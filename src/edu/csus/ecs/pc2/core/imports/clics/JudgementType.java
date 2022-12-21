// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * A JSON representation for the CLICS API judgement-types json data.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */

//@JsonIgnoreProperties(ignoreUnknown = true)
public class JudgementType {

    // judgement-types data = {penalty=false, name=Judge Error, solved=false, id=JE}
   
    /**
     * Judgement Acronym
     */
    @JsonProperty
    private String id;
    
    @JsonProperty
    private String name;

    @JsonProperty
    private boolean penalty;
    
    @JsonProperty
    private boolean solved;
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    /**
     * Does run judgement have a time penalty
     * @return
     */
    public boolean isPenalty() {
        return penalty;
    }
    
    public String toJSON() throws JsonProcessingException {

        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }

}
