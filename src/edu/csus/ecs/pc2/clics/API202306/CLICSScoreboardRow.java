// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.standings.ProblemSummaryInfo;
import edu.csus.ecs.pc2.core.standings.TeamStanding;

/**
 * CLICS Scoreboard row
 * Contains information about a single entry in the scoreboard
 * 
 * @author John Buck
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSScoreboardRow {

    @JsonProperty
    private int rank;

    @JsonProperty
    private String team_id;

    @JsonProperty
    private CLICSScore score;
    
    @JsonProperty
    private CLICSProblemScore [] problems;

    /**
     * Fill in API scoreboard row information properties (for scoreboard endpoint)
     * 
     * @param probEleToShort hashmap for mapping problem elementid to shortname
     * @param teamStanding xml representation of the standings for a team
     */
    public CLICSScoreboardRow(HashMap<String, String> probEleToShort, TeamStanding teamStanding) {
        team_id = teamStanding.getTeamId();
        rank = Utilities.nullSafeToInt(teamStanding.getRank(), 0);
        score = new CLICSScore(teamStanding);
        
        ArrayList<CLICSProblemScore> pslist = new ArrayList<CLICSProblemScore>();
        
        for( ProblemSummaryInfo psi : teamStanding.getProblemSummaryInfos()) {
            pslist.add(new CLICSProblemScore(probEleToShort, psi));
        }
        problems = pslist.toArray(new CLICSProblemScore[0]);
    }
}
