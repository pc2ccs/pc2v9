// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.standings.ProblemSummaryInfo;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Contains information about the score a team received for a single problem.
 * This corresponds to the object the CLICS specification calls "problem data".
 * "problem data" objects are found the rows of the scoreboard in the CLICS "scoreboard rows".
 *
 * @author John Buck
 *
 */

@JsonFilter("rtFilter")
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
    private double score;

    @JsonProperty
    private int time;

    private boolean isPointScoring = false;

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
    public CLICSProblemScore(IInternalContest model, HashMap<String, String> probEleToShort, ProblemSummaryInfo psi) {
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
        }
        if(model != null) {
            isPointScoring = model.getContestInformation().isScoreboardTypeScore();
        }
    }

    public String toJSON() {
        Set<String> exceptProps = new HashSet<String>();

        getExceptProps(exceptProps);
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            // for this problem's score, create filter to omit inappropriate properties,
            // 'score' in this case if not Point Scoring contest
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
            FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter).setFailOnUnknownId(false);
            mapper.setFilters(fp);
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for CLICSProblemScore " + e.getMessage();
        }
    }

    /**
     * Get set of properties for which we do not want to serialize into JSON.
     * This is so we don't serialize score for pass-fail contests
     *
     * @param exceptProps Set to fill in with property names to omit
     */
    public void getExceptProps(Set<String> exceptProps) {
        if(!isPointScoring){
            exceptProps.add("score");
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
