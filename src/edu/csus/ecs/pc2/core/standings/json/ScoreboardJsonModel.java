// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings.json;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.standings.ContestStandings;
import edu.csus.ecs.pc2.core.standings.ProblemSummaryInfo;
import edu.csus.ecs.pc2.core.standings.TeamStanding;

//The following annotation tells the Jackson ObjectMapper not to include "type information" when it serializes
//objects of this class (the default is to include the fully-qualified class name as an additional JSON element).
//(Note that exclusion of such type information means that generated JSON cannot subsequently be accurately DESERIALIZED...)
@JsonTypeInfo(use=Id.NONE)

//The following annotation tells the Jackson ObjectMapper the order in which it should output fields when serializing 
//  objects of this class into JSON.
@JsonPropertyOrder({"event_id","time","contest_time", "state", "rows"})

public class ScoreboardJsonModel {
    
    @JsonProperty
    private String event_id;
    @JsonProperty
    private String contest_time;
    @JsonProperty
    private ContestState state = new ContestState();
    @JsonProperty
    @JsonTypeInfo(use=Id.NONE)
    private List <TeamScoreRow> rows;
    @JsonProperty
    private String time;
    
    @JsonCreator
    public ScoreboardJsonModel() { }
    
    public ScoreboardJsonModel(ContestStandings contestStandings) {
        
//        "event_id": "3ee531c5-9aa9-4bf8-8a93-a00e12ab2907", 
//        "contest_time": "1:30:06.337", 

        setEvent_id(UUID.randomUUID().toString());
        setContest_time("");
        
//        "finalized": "2021-04-19T01:30:6.337+0000", 
//        "started": "2021-04-19T00:00:0.000+0000", 
//        "unfrozen": "2021-04-19T01:30:6.337+0000", 
//        "frozen": "2021-04-19T01:00:0.000+0000", 
//        "ended": "2021-04-19T01:30:0.000+0000", 
//        "end_of_updates": "2021-04-19T01:30:6.337+0000"

        state.setEnded("");
        state.setFinalized("");
        state.setFrozen("");
        state.setStarted("");
        state.setThawed("");
        state.setEnd_of_updates("");

   //   "rows": [
   //   {
//        "team_id": 325958, 
//        "score": {
//          "total_time": 145, 
//          "num_solved": 12
//        }, 
   // 
//        List<ProblemScoreRow> problems
   // 
//        "problems": [
//          {
//            "solved": true, 
//            "num_judged": 1, 
//            "time": 12, 
//            "problem_id": "candlebox", 
//            "num_pending": 0
//          },
        
        rows = new ArrayList<TeamScoreRow>();
        
        List<TeamStanding> teamStand = contestStandings.getTeamStandings();
        for (TeamStanding teamStanding : teamStand) {
            TeamScoreRow row = new TeamScoreRow();
            
            row.setTeam_id(toInt(teamStanding.getTeamId()));
            
            StandingScore score = new StandingScore();
            score.setNum_solved(toInt(teamStanding.getSolved()));
            score.setTotal_time(toLong(teamStanding.getPoints(), 0));
            row.setScore(score);
            
            List<ProblemScoreRow> problemList = new ArrayList<ProblemScoreRow>();
            for ( ProblemSummaryInfo psi : teamStanding.getProblemSummaryInfos()) {
                ProblemScoreRow scoreRow  = new ProblemScoreRow();

                //  <problem attempts="50" bestSolutionTime="13" id="3" lastSolutionTime="87" numberSolved="35" title="Careful Ascent"/>
                
                scoreRow.setNum_judged(toInt(psi.getAttempts()));
                
                String problemIdNumber = psi.getProblemId();
                // TODO properly assign short name
                String problemShortName = "Id = "+problemIdNumber;
                scoreRow.setProblem_id(problemShortName); // problem short name
                
                scoreRow.setNum_pending(toInt(psi.getIsPending()));
                
                scoreRow.setSolved(toBool(psi.getIsSolved(), false));
                
                problemList.add(scoreRow);
            }
                
            row.setProblems(problemList );
            rows.add(row);
        }
    }

    private long toLong(String string, int defaultLong) {

        try {
            return Long.parseLong(string.trim().toLowerCase());
        } catch (Exception e) {
            return defaultLong;
        }
    }

    private boolean toBool(String string, boolean defaultBool) {
        try {
            return Boolean.parseBoolean(string.trim().toLowerCase());
        } catch (Exception e) {
            return defaultBool;
        }
    }

    /**
     * Null save convert to integer
     * @param string
     * @return integer in string, or zero if there is an error
     */
    private int toInt(String string) {
        return Utilities.nullSafeToInt(string,0);
    }

    public String getEvent_id() {
        return event_id;
    }
    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
    public String getContest_time() {
        return contest_time;
    }
    public void setContest_time(String contest_time) {
        this.contest_time = contest_time;
    }
    public ContestState getState() {
        return state;
    }
    public void setState(ContestState state) {
        this.state = state;
    }
    public List<TeamScoreRow> getRows() {
        return rows;
    }
    public void setRows(List<TeamScoreRow> rows) {
        this.rows = rows;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}
