// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.standings.TeamStanding;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Contains information about the score for a team on the scoreboard.
 *
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonFilter("rtFilter")
public class CLICSScore {

    @JsonProperty
    private int num_solved;

    @JsonProperty
    private int total_time;

    @JsonProperty
    private double score;

    @JsonProperty
    private int time;

    private boolean isPointScoring = false;

    /**
     * Provide empty constructor for Jackson deserialization
     */
    @JsonCreator
    public CLICSScore() {

    }

    /**
     * Fill in the properties for a team's score
     *
     * @param teamStanding The team's scoring information
     * @throws NumberFormatException if bad scores are in the standings
     */
    public CLICSScore(IInternalContest model, TeamStanding teamStanding) {
        num_solved = Utilities.nullSafeToInt(teamStanding.getSolved(), 0);
        total_time = Utilities.nullSafeToInt(teamStanding.getPoints(), 0);
        if(num_solved > 0) {
            // Problem solution time is in minutes.
            time = Integer.parseInt(teamStanding.getLastSolved());
            score = Double.parseDouble(teamStanding.getScore());
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
            // for this team score, create filter to omit inappropriate properties,
            // 'score' in this case if not Point Scoring contest
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
            FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter).setFailOnUnknownId(false);
            mapper.setFilters(fp);
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for CLICSScore " + e.getMessage();
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

    public int getNum_solved() {
        return num_solved;
    }

    public int getTotal_time() {
        return total_time;
    }

    public int getTime() {
        return time;
    }

    /**
     * @return the score
     */

    public Double getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

}
