// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * A JSON representation for the CLICS API contests json data.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class CLICSContests {

//  {
//  "token": "1567",
//  "id": null,
//  "type": "contest",
//  "data": {
//    "formal_name": "Benelux Algorithm Programming Contest 2022",
//    "penalty_time": 20,
//    "start_time": "2022-10-22T12:30:00+02:00",
//    "end_time": "2022-10-22T17:30:00+02:00",
//    "duration": "5:00:00.000",
//    "scoreboard_freeze_duration": "1:00:00.000",
//    "id": "bapc2022",
//    "external_id": "bapc2022",
//    "name": "Benelux Algorithm Programming Contest 2022",
//    "shortname": "bapc2022"
//  },
//  "time": "2022-10-21T12:35:37.218+02:00"
//}


    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String duration;

    @JsonProperty
    private String start_time;
    
    @JsonProperty
    private String end_time;

    @JsonProperty
    private String scoreboard_freeze_duration;

    @JsonProperty
    private String formal_name;

    @JsonProperty
    private String penalty_time;
    
    @JsonProperty
    private String shortname;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public String getStart_time() {
        return start_time;
    }
    
    public String getEnd_time() {
        return end_time;
    }

    public String getScoreboard_freeze_duration() {
        return scoreboard_freeze_duration;
    }

    public String getFormal_name() {
        return formal_name;
    }

    public String getPenalty_time() {
        return penalty_time;
    }
    
    public String getShortname() {
        return shortname;
    }

    public String toJSON() throws JsonProcessingException {

        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }
}
