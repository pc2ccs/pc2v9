// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonCreator;
<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonInclude;
=======
>>>>>>> dc2ae230a (i_1006 Fix event feed for point scoring)
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.standings.ProblemSummaryInfo;

/**
 * Contains information about the score a team received for a single problem.
 * This corresponds to the object the CLICS specification calls "problem data".
 * "problem data" objects are found the rows of the scoreboard in the CLICS "scoreboard rows".
 *
 * @author John Buck
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSProblemScore {

    @JsonProperty
    private String problem_id;

    @JsonProperty
    private int num_judged;

    @JsonProperty
    private int num_pending;

    @JsonProperty
    private boolean solved;

    @JsonProperty
    private Double score;

    @JsonProperty
    private int time;

    /**
     * Provide empty constructor for Jackson deserialization
     */
    @JsonCreator
    public CLICSProblemScore() {

    }

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
            // Problem solution time is in minutes
            time = StringUtilities.getIntegerValue(psi.getSolutionTime(), 0);
            String scoreVal = psi.getScore();
            if(scoreVal != null) {
                try {
                    score = Double.parseDouble(scoreVal);
                } catch (Exception e) {
                    // Bad double supplied - nothing to do, just don't set it
                    System.err.println("Bad score: " + scoreVal);
                }
            }
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

    public String getProblem_id() {
        return problem_id;
    }

    public int getNum_judged() {
        return num_judged;
    }

    public int getNum_pending() {
        return num_pending;
    }

    public boolean isSolved() {
        return solved;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
