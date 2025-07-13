// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.util.IJSONTool;
import edu.csus.ecs.pc2.imports.ccs.TestDataGroup;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Contains information about a Problem.
 * This corresponds to the object returned by the CLICS Problems endpoint.
 *
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonFilter("rtFilter")
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

    @JsonProperty
    private double max_score;

    // The next two will be 'null' for now until we implement the new json CPF
    @JsonProperty("package")
    private CLICSFileReference [] packagezip;

    @JsonProperty
    private CLICSFileReference [] statement;

    private boolean isPointScoring = false;

    /**
     * Fill in properties for a Problem description.
     *
     * @param model The contest
     * @param problem The problem
     * @param ordinal Order in which problem appears in display list
     */
    public CLICSProblem(IInternalContest model, Problem problem, int ordinal) {
        // {"id":"asteroids","label":"A","name":"Asteroid Rangers","ordinal":1,"color":"blue","rgb":"#00000f","test_data_count":10,"time_limit":2 }
        id = IJSONTool.getProblemId(problem);
        label = problem.getLetter();
        name = problem.getDisplayName();
        this.ordinal = ordinal;
        // optional attribute color
        if (!StringUtilities.isEmpty(problem.getColorName())) {
            color = problem.getColorName();
        }
        // optional attribute rgb
        if (!StringUtilities.isEmpty(problem.getColorRGB())) {
            rgb = problem.getColorRGB();
        }
        test_data_count = problem.getNumberTestCases();
        time_limit = problem.getTimeOutInSeconds();
        if(model.getContestInformation().isScoreboardTypeScore()) {
            isPointScoring = true;
            ProblemDataFiles problemDataFiles = model.getProblemDataFile(problem);
            if(problemDataFiles != null) {
                TestDataGroup [] testDataGroups = problemDataFiles.getJudgesDataGroups();
                // Really, there's only 1 top level TestDataGruop
                if(testDataGroups != null && testDataGroups.length > 0) {
                    max_score = testDataGroups[0].getRangeMax();
                }
            }
        }
    }

    public String toJSON() {
        Set<String> exceptProps = new HashSet<String>();

        getExceptProps(exceptProps);
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            // for this problem, create filter to omit inappropriate properties,
            // 'max_score' in this case if not Point Scoring contest
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
            FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter).setFailOnUnknownId(false);
            mapper.setFilters(fp);
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for CLICSProblem " + e.getMessage();
        }
    }

    /**
     * Get set of properties for which we do not want to serialize into JSON.
     * This is so we don't serialize max_score for pass-fail contests
     *
     * @param exceptProps Set to fill in with property names to omit
     */
    public void getExceptProps(Set<String> exceptProps) {
        if(!isPointScoring){
            exceptProps.add("max_score");
        }
    }
}
