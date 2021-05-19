// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

//The following annotation tells the Jackson ObjectMapper not to include "type information" when it serializes
//objects of this class (the default is to include the fully-qualified class name as an additional JSON element).
//(Note that exclusion of such type information means that generated JSON cannot subsequently be accurately DESERIALIZED...)
@JsonTypeInfo(use=Id.NONE)
public class TeamScoreRow {
    
    //    {"rank":1, 
    //     "team_id":325958,

    @JsonProperty
    private int rank;
    
    @JsonProperty
    private int team_id;
    
    //     "score":
    // {"num_solved":12,     
    // "total_time":145},
    
    @JsonProperty
    private StandingScore score;
    
    //    "problems":[{"problem_id":"candlebox","num_judged":1,"num_pending":0,"solved":true,"time":12},
    
    @JsonProperty 
    private List<ProblemScoreRow> problems;
    
    

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public StandingScore getScore() {
        return score;
    }

    public void setScore(StandingScore score) {
        this.score = score;
    }

    public List<ProblemScoreRow> getProblems() {
        return problems;
    }

    public void setProblems(List<ProblemScoreRow> problems) {
        this.problems = problems;
    }

}
