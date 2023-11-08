// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Contest State
 * 
 * @author John Buck
 *
 */
// 2023-06 Spec specifically says to return a time or null, so always include all properties
// But it also says the fields are optional, so, I assume, if it's null, that means the CCS
// supports that property, which PC2 does.  If it did not support the property, then it would
// not be included in the JSON.  (At least that is my interpretation -- JohnB)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CLICSContestState {

    @JsonProperty
    private String started;

    @JsonProperty
    private String frozen;

    @JsonProperty
    private String ended;

    @JsonProperty
    private String thawed;

    @JsonProperty
    private String finalized;

    @JsonProperty
    private String end_of_updates;

    /**
     * Fill in properties for contest state as per 2023-06 spec
     * 
     * @param model place to get contest times from
     */
    public CLICSContestState(IInternalContest model) {
        
        if (model.getContestTime().isContestStarted()) {
            ContestInformation ci = model.getContestInformation();
            ContestTime ct = model.getContestTime();
            
            started = Utilities.getIso8601formatterWithMS().format(ct.getContestStartTime().getTime());
            if (model.getContestTime().isPastEndOfContest()) {
                Calendar endedDate = JSONTool.calculateElapsedWalltime(model, ct.getContestLengthMS());
                if (endedDate != null) {
                    ended = Utilities.getIso8601formatterWithMS().format(endedDate.getTimeInMillis());
                }
            }
            String scoreboardFreezeDuration = ci.getFreezeTime();
            Date thawedDate = null;
            
            if (scoreboardFreezeDuration != null && scoreboardFreezeDuration.trim().length() > 0) {
                long elapsed = ct.getElapsedSecs();
                long freezeTimeSecs = Utilities.getFreezeTime(model);
                // FIXME this date should be stored in ContestInformation
                if (elapsed >= freezeTimeSecs) {
                    Calendar freezeCal = JSONTool.calculateElapsedWalltime(model, freezeTimeSecs * 1000);
                    if (freezeCal != null) {
                        frozen = Utilities.getIso8601formatterWithMS().format(freezeCal.getTime());
                    }
                }
                if (ci.isUnfrozen()) {
                    thawedDate = model.getContestInformation().getThawed();
                    if (thawedDate != null) {
                        thawed = Utilities.getIso8601formatterWithMS().format(thawedDate);
                    }
                }
            }
            // FIXME this should only be showed if the contest is thawed for public users
            if (model.getFinalizeData() != null) {
                finalized = Utilities.getIso8601formatterWithMS().format(model.getFinalizeData().getCertificationDate());
                // If contest was thawed after finalized, then use thaw time
                if(thawed != null && thawedDate != null && thawedDate.after(model.getFinalizeData().getCertificationDate())) {
                    end_of_updates = thawed;
                } else {
                    end_of_updates = finalized;
                }
            }
        }
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for contest state info " + e.getMessage();
        }
    }
}
