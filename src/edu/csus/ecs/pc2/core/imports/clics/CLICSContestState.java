// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * CLICS ContestState JSON element.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */

//The following annotation tells the Jackson ObjectMapper not to include "type information" when it serializes
//objects of this class (the default is to include the fully-qualified class name as an additional JSON element).
//(Note that exclusion of such type information means that generated JSON cannot subsequently be accurately DESERIALIZED...)
@JsonTypeInfo(use = Id.NONE)

// TODO REFACTOR Use this class to replace package edu.csus.ecs.pc2.core.standings.json.ContestState
public class CLICSContestState {

    //    String    "finalized": "2021-04-19T01:30:6.337+0000", 
    //    String    "started": "2021-04-19T00:00:0.000+0000", 
    //    String    "thawed": "2021-04-19T01:30:6.337+0000", 
    //    String    "frozen": "2021-04-19T01:00:0.000+0000", 
    //    String    "ended": "2021-04-19T01:30:0.000+0000", 
    //    String    "end_of_updates": "2021-04-19T01:30:6.337+0000"

    @JsonProperty
    private String finalized;

    @JsonProperty
    private String started;

    @JsonProperty
    private String thawed;

    @JsonProperty
    private String frozen;

    @JsonProperty
    private String ended;

    @JsonProperty
    private String end_of_updates;

    public String getFinalized() {
        return finalized;
    }

    public void setFinalized(String finalized) {
        this.finalized = finalized;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getThawed() {
        return thawed;
    }

    public void setThawed(String thawed) {
        this.thawed = thawed;
    }

    public String getFrozen() {
        return frozen;
    }

    public void setFrozen(String frozen) {
        this.frozen = frozen;
    }

    public String getEnded() {
        return ended;
    }

    public void setEnded(String ended) {
        this.ended = ended;
    }

    public String getEnd_of_updates() {
        return end_of_updates;
    }

    public void setEnd_of_updates(String end_of_updates) {
        this.end_of_updates = end_of_updates;
    }
}
