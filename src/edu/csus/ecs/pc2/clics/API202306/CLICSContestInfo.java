// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Contest Info.
 * 
 * @author John Buck
 *
 */
public class CLICSContestInfo {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String formal_name;

    @JsonProperty
    private String start_time;

    @JsonProperty
    private String countdown_pause_time;

    @JsonProperty
    private String duration;

    @JsonProperty
    private String scoreboard_freeze_duration;

    @JsonProperty
    private String scoreboard_thaw_time;

    @JsonProperty
    private String scoreboard_type;

    @JsonProperty
    private int penalty_time;

    /**
     * Fill in properties for a contest.
     * 
     * @param model The contest to use
     */
    public CLICSContestInfo(IInternalContest model) {
        ContestInformation ci = model.getContestInformation();
        
        id = model.getContestIdentifier();
        name = ci.getContestShortName();
        formal_name = ci.getContestTitle();
        if(name == null) {
            name = formal_name;
        }
        duration = model.getContestTime().getContestLengthStr();
        scoreboard_freeze_duration = ci.getFreezeTime();
        if (scoreboard_freeze_duration.length() > 2) {
            if (!scoreboard_freeze_duration.contains(":")) {
                try {
                    long seconds = Long.parseLong(scoreboard_freeze_duration);
                    scoreboard_freeze_duration = ContestTime.formatTime(seconds);
                } catch (NumberFormatException e) {
                    System.out.println("attempting to parse " + scoreboard_freeze_duration + " failed with " + e.getMessage());
                }
            }
        }
        if (model.getContestTime().isContestStarted()) {
            start_time = Utilities.getIso8601formatterWithMS().format(model.getContestTime().getContestStartTime().getTime());
        } else {
            // contest has not started, check for a scheduledStartTime
            Calendar calendar = ci.getScheduledStartTime();
            if (calendar != null) {
                Date date = calendar.getTime();
                start_time = Utilities.getIso8601formatterWithMS().format(date);
            } else {
                start_time = null;
            }
        }
        penalty_time = Integer.valueOf(ci.getScoringProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO, "20"));
        scoreboard_type = "pass-fail";
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for contest info " + e.getMessage();
        }
    }
}
