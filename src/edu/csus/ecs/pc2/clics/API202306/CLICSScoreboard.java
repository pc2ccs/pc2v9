// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.standings.ContestStandings;
import edu.csus.ecs.pc2.core.standings.ProblemSummaryInfo;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilites;
import edu.csus.ecs.pc2.core.standings.TeamStanding;
import edu.csus.ecs.pc2.core.standings.json.ProblemScoreRow;
import edu.csus.ecs.pc2.core.standings.json.ScoreboardJsonUtility;
import edu.csus.ecs.pc2.core.standings.json.StandingScore;
import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Scoreboard
 * Contains information about the scoreboard
 * 
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSScoreboard {

    @JsonProperty
    private String time;

    @JsonProperty
    private String contest_time;

    @JsonProperty
    private CLICSContestState state;

    @JsonProperty
    private CLICSScoreboardRow [] rows;
    
    /**
     * Fill in the scoreboard information
     * 
     */
    public CLICSScoreboard(IInternalContest model, IInternalController controller)  throws IllegalContestState, JAXBException, IOException {
        
        DefaultScoringAlgorithm scoringAlgorithm = new DefaultScoringAlgorithm();

        Properties properties = ScoreboardUtilites.getScoringProperties(model);

        // legacy - standings are created as XML, and we convert that to JSON. 
        String xml = scoringAlgorithm.getStandings(model, properties, StaticLog.getLog());
        ContestStandings contestStandings = ScoreboardUtilites.createContestStandings(xml);
        
        // This is what we want to return:
        //        {
        //            "time": "2014-06-25T14:13:07.832+01",
        //            "contest_time": "4:13:07.832",
        //            "state": {
        //              "started": "2014-06-25T10:00:00+01",
        //              "ended": null,
        //              "frozen": "2014-06-25T14:00:00+01",
        //              "thawed": null,
        //              "finalized": null,
        //              "end_of_updates": null
        //            },
        //            "rows": [
        //              {"rank":1,"team_id":"123","score":{"num_solved":3,"total_time":340},"problems":[
        //                {"problem_id":"1","num_judged":3,"num_pending":1,"solved":false},
        //                {"problem_id":"2","num_judged":1,"num_pending":0,"solved":true,"time":20},
        //                {"problem_id":"3","num_judged":2,"num_pending":0,"solved":true,"time":55},
        //                {"problem_id":"4","num_judged":0,"num_pending":0,"solved":false},
        //                {"problem_id":"5","num_judged":3,"num_pending":0,"solved":true,"time":205}
        //              ]}
        //            ]
        //          }
        time = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT);
        contest_time = model.getContestTime().getElapsedTimeStr();
        state = new CLICSContestState(model);
     
        ArrayList<CLICSScoreboardRow>rowsArray = new ArrayList<CLICSScoreboardRow>();
        HashMap<String, String> probEleToShort = new HashMap<String, String>();
      
        // create a mapping of each problem's element ID to its shortname.
        // we will use shortname as the problem id in the problem list for each team's solutions
        for(Problem problem: model.getProblems()) {
            probEleToShort.put(problem.getElementId().toString(), problem.getShortName());
        }
        // list of xml entries for each team's standing
        for (TeamStanding teamStanding : contestStandings.getTeamStandings()) {
          rowsArray.add(new CLICSScoreboardRow(probEleToShort, teamStanding));
          rows = rowsArray.toArray(new CLICSScoreboardRow[0]);
        }
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for scoreboard info " + e.getMessage();
        }
    }
}
