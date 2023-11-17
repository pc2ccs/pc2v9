// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Problem
 * Contains information about a Problem
 * 
 * @author John Buck
 *
 */
public class CLICSProblem {

    @JsonProperty
    private String id;

    @JsonProperty
    private String uuid;

    @JsonProperty
    private String label;

    @JsonProperty
    private String name;

    @JsonProperty
    private int ordinal;

    @JsonProperty
    private String rgb;

    @JsonProperty
    private String color;

    @JsonProperty
    private int time_limit;

    @JsonProperty
    private int test_data_count;

// only for "score" type contests, N/A for pc2/wf pass-fail type contests    
//    @JsonProperty
//    private int max_score;

    // The next two will be 'null' for now until we implement the new json CPF
    @JsonProperty("package")
    private CLICSFileReference [] packagezip;
    
    @JsonProperty
    private CLICSFileReference [] statement;

    /**
     * Fill in properties for a Problem description.
     * 
     * @param model The contest
     * @param problem The problem 
     * @param ordinal Order in which problem appears in display list
     */
    public CLICSProblem(IInternalContest model, Problem problem, int ordinal) {
        // {"id":"asteroids","label":"A","name":"Asteroid Rangers","ordinal":1,"color":"blue","rgb":"#00000f","test_data_count":10,"time_limit":2 }
        id = JSONTool.getProblemId(problem);
        label = problem.getLetter();
        name = problem.getDisplayName();
        this.ordinal = ordinal;
        // optional attribute color
        if (JSONTool.notEmpty(problem.getColorName())) {
            color = problem.getColorName();
        }
        // optional attribute rgb
        if (JSONTool.notEmpty(problem.getColorRGB())) {
            rgb = problem.getColorRGB();
        }
        test_data_count = problem.getNumberTestCases();
        time_limit = problem.getTimeOutInSeconds();
    }
    
    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for problem info " + e.getMessage();
        }
    }
}
