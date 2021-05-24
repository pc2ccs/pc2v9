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
public class TeamScoreRow implements Comparable<TeamScoreRow> {
    
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

    /**
     * Returns an indication of whether the significant scoreboard values in this TeamScoreRow match
     * the corresponding values in a specified other TeamScoreRow.
     * 
     * Note that this method only compares the TeamScoreRow rank, teamId, score, and totalTime to determine
     * a match; other values such as the problems associated with the scoreboard row are NOT considered
     * when determining whether the scores match.
     * 
     * @param otherRow the TeamScorerow to be checked for a match with this TeamScoreRow.
     * 
     * @return true if the rank, teamId, score, and total time for this TeamScoreRow matches the other TeamScoreRow; 
     *          false otherwise.
     */
    public boolean scoreMatches(TeamScoreRow otherRow) {
        
        //ranks must be the same
        if (this.getRank() != otherRow.getRank()) {
            return false;
        }
        
        //team Id's must be the same
        if (this.getTeam_id() != otherRow.getTeam_id()) {
            return false;
        }
        
        //StandingScores must not be null
        if (this.getScore()==null || otherRow.getScore()==null) {
            return false;
        }
        
        //scores must be the same
        StandingScore thisScore = this.getScore();
        StandingScore otherScore = otherRow.getScore();
        
        if (thisScore.getNum_solved() != otherScore.getNum_solved()) {
            return false;
        }
        
        //total time must be the same
        if (thisScore.getTotal_time() != otherScore.getTotal_time()) {
            return false;
        }
        
        //all values which this method is desigend to check are equal
        return true;
    }

    /**
     * Compares this TeamScoreRow with another TeamScoreRow, returning an indication of 
     * whether this TeamScoreRow is "less than" (-1), "equal to" (1), or "greater than" (+1) the other TeamScoreRow.
     * 
     * The comparison is done by first considering the ranks in the two TeamScoreRows; if the ranks are
     * equal then the number of problems solved is considered; if those are also equal then the total time 
     * (penalty points) is considered.  If all those are equal, the return value is zero.
     * 
     * Note that ideally, for teams which are equal in rank, problems solved, and total time, the return value
     * should be based on the alphabetical ordering of the team names (rather than returning zero).
     * However, this would require having access to the team's display name, which is not currently available in
     * a TeamScoreRow.
     * 
     * @param other the other TeamScoreRow against which this TeamScoreRow is to be compared.
     * 
     * @return -1, 0, or 1, depending on whether this TeamScoreRow is considered "less than", "equal to", or
     *          "greater than" the specified other TeamScoreRow respectively.
     */
    @Override
    public int compareTo(TeamScoreRow other) {

        int thisRank = this.getRank();
        int otherRank = other.getRank();
        if (thisRank==otherRank) {
            return compareNumberSolved(other);
        } else if (thisRank>otherRank) {
            return 1 ;
        } else {
            return -1 ;
        }
    }

    private int compareNumberSolved(TeamScoreRow other) {
        int thisNumSolved = this.getScore().getNum_solved();
        int otherNumSolved = other.getScore().getNum_solved();
        if (thisNumSolved==otherNumSolved) {
            return compareTotalTime(other);
        } else if (thisNumSolved>otherNumSolved) {
            return -1 ;
        } else {
            return 1 ;
        }
    }

    private int compareTotalTime(TeamScoreRow other) {
        long thisTime = this.getScore().getTotal_time();
        long otherTime = other.getScore().getTotal_time();
        if (thisTime==otherTime) {
            //at this point the two TeamScoreRows are equal in rank, number solved, and total time
            return 0;
        } else if (thisTime>otherTime) {
            return 1 ;
        } else {
            return -1 ;
        }
    }
    
}
