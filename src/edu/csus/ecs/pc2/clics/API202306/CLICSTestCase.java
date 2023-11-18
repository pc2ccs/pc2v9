// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Judgment test case
 * Contains information about a single test case (run)
 * 
 * @author John Buck
 *
 */
public class CLICSTestCase {

    @JsonProperty
    private String id;

    @JsonProperty
    private String judgement_id;

    @JsonProperty
    private int ordinal;

    @JsonProperty
    private String judgement_type_id;

    @JsonProperty
    private String time;

    @JsonProperty
    private String contest_time;

    @JsonProperty
    private double run_time;

    /**
     * Fills in the test case properties
     * 
     * @param model The contest
     * @param testCase The test case
     * @param ord Ordinal test case number
     */
    public CLICSTestCase(IInternalContest model, RunTestCase testCase) {

        // {"id":"1312","judgement_id":"189549","ordinal":28,"judgement_type_id":"TLE",
        //       "time":"2014-06-25T11:22:42.420+01","contest_time":"1:22:42.420"}
        id = testCase.getElementId().toString();
        judgement_id = testCase.getRunElementId().toString();
        ordinal = testCase.getTestNumber();
        judgement_type_id= JSONTool.getJudgementType(model.getJudgement(testCase.getJudgementId()));
        // SOMEDAY get the time from the server instead of the judge
        time = Utilities.getIso8601formatterWithMS().format(testCase.getDate().getTime());
        // note this is the contest_time as seen on the judge
        contest_time = ContestTime.formatTimeMS(testCase.getContestTimeMS());
        run_time = ((double)testCase.getElapsedMS())/1000.;
    }
    
    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for version info " + e.getMessage();
        }
    }
}
