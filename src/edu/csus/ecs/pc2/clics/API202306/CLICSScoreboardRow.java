// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.standings.ProblemSummaryInfo;
import edu.csus.ecs.pc2.core.standings.TeamStanding;

/**
 * Contains information about a single entry (row) in the scoreboard.
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
     * Provide empty constructor for Jackson deserialization
     */
    @JsonCreator
    public CLICSScoreboardRow() {

    }

    /**
     * Fill in API scoreboard row information properties (for scoreboard endpoint)
     *
     * @param probEleToShortName hashmap for mapping problem elementid to shortname
     * @param teamStanding xml representation of the standings for a team
     */
    public CLICSScoreboardRow(IInternalContest model, HashMap<String, String> probEleToShortName, TeamStanding teamStanding) {
        team_id = teamStanding.getTeamId();
        rank = Utilities.nullSafeToInt(teamStanding.getRank(), 0);
        score = new CLICSScore(model, teamStanding);

        ArrayList<CLICSProblemScore> pslist = new ArrayList<CLICSProblemScore>();

        for( ProblemSummaryInfo psi : teamStanding.getProblemSummaryInfos()) {
            pslist.add(new CLICSProblemScore(model, probEleToShortName, psi));
        }
        problems = pslist.toArray(new CLICSProblemScore[0]);
    }

    public int getRank() {
        return rank;
    }

    public String getTeam_id() {
        return team_id;
    }

    public CLICSScore getScore() {
        return score;
    }

    public List<CLICSProblemScore> getProblems() {
        return Arrays.asList(problems);
    }
}
