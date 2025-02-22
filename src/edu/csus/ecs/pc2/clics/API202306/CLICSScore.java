// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.standings.TeamStanding;

/**
 * Contains information about the score for a team on the scoreboard.
 *
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSScore {

    @JsonProperty
    private int num_solved;

    @JsonProperty
    private int total_time;

// Not used for pass/fail contest and we don't want it appearing in the json
//    @JsonProperty
//    private int score;

    @JsonProperty
    private String time;

    /**
     * Fill in the properties for a team's score
     *
     * @param teamStanding The team's scoring information
     */
    public CLICSScore(TeamStanding teamStanding) {
        num_solved = Utilities.nullSafeToInt(teamStanding.getSolved(), 0);
        total_time = Utilities.nullSafeToInt(teamStanding.getPoints(), 0);
        if(num_solved > 0) {
            // Problem solution time is in minutes.
            time = teamStanding.getLastSolved();
        }
    }

}
