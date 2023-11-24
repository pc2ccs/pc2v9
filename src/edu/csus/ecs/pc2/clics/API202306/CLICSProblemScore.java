// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.standings.ProblemSummaryInfo;

/**
 * CLICS Score (for a problem)
 * Contains information about the score a team received for a single problem
 * 
 * @author John Buck
 *
 */

public class CLICSProblemScore {

    @JsonProperty
    private String problem_id;

    @JsonProperty
    private int num_judged;

    @JsonProperty
    private int num_pending;

    @JsonProperty
    private boolean solved;

// Not needed for pass-fail contest
//  @JsonProperty
//  private double score;

    @JsonProperty
    private String time;

    /**
     * Fill in API problem score information properties (for scoreboard endpoint)
     * 
     * @param probEleToShort hashmap for mapping problem elementid to shortname
     * @param versionInfo
     */
    public CLICSProblemScore(HashMap<String, String> probEleToShort, ProblemSummaryInfo psi) {
        num_judged = Utilities.nullSafeToInt(psi.getAttempts(), 0);
        num_pending = Utilities.nullSafeToInt(psi.getIsPending(), 0);
        problem_id = psi.getProblemId();
        // look up problem short name since this is what we use in the problem endpoint.
        if(probEleToShort.containsKey(problem_id)) {
            problem_id = probEleToShort.get(problem_id);
        }
        solved = toBool(psi.getIsSolved(), false);
        if(solved) {
            // Problem solution time is in minutes, so multiply by 60 seconds.
            time = ContestTime.formatTime(StringUtilities.getIntegerValue(psi.getSolutionTime(), 0)*60);
        }
    }

    /**
     * Convert a string that represents a boolean into a boolean scalar
     * 
     * @param strBool string containing a boolean representation
     * @param defaultBool if strBool is not valid, use this
     * @return
     */
    private boolean toBool(String strBool, boolean defaultBool) {
        try {
            return Boolean.parseBoolean(strBool.trim());
        } catch (Exception e) {
            return defaultBool;
        }
    }
}
