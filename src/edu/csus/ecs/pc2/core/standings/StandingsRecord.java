// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * A set of standings data for a team.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class StandingsRecord {

    @JsonProperty
    private int teamId;

    @JsonProperty
    private int groupRank;

    @JsonProperty
    private long points;

    @JsonProperty
    private int problemsAttempted;

    @JsonProperty
    private int rank;

    @JsonProperty
    private int scoringAdjustment;

    @JsonProperty
    private int solved;

    @JsonProperty
    private int teamExternalId;

    @JsonProperty
    private int teamGroupExternalId;

    @JsonProperty
    private String source = "<no source defined>";

    StandingsRecord create(TeamStanding teamStanding, String source) {

        if (isEmpty(teamStanding.getTeamId())) {
            throw new IllegalArgumentException("TeamsStandings teamId cannot be empty/null");
        }

        StandingsRecord record = new StandingsRecord();
        record.setSource(source);

        record.setTeamId(toInt(teamStanding.getTeamId(), 0));
        record.setGroupRank(toInt(teamStanding.getGroupRank(), 0));
        record.setPoints(toLong(teamStanding.getPoints(), 0));
        record.setProblemsAttempted(toInt(teamStanding.getProblemsAttempted(), 0));
        record.setRank(toInt(teamStanding.getRank(), 0));
        record.setScoringAdjustment(toInt(teamStanding.getScoringAdjustment(), 0));
        record.setSolved(toInt(teamStanding.getSolved(), 0));
        record.setTeamExternalId(toInt(teamStanding.getTeamExternalId(), 0));
        record.setTeamGroupExternalId(toInt(teamStanding.getTeamGroupExternalId(), 0));

        return record;
    }
    
    StandingsRecord create(String apiTeamsJSON, String source) {
        
        StandingsRecord record = new StandingsRecord();
        record.setSource(source);
        
        return record;
        
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getGroupRank() {
        return groupRank;
    }

    public void setGroupRank(int groupRank) {
        this.groupRank = groupRank;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public int getProblemsAttempted() {
        return problemsAttempted;
    }

    public void setProblemsAttempted(int problemsAttempted) {
        this.problemsAttempted = problemsAttempted;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getScoringAdjustment() {
        return scoringAdjustment;
    }

    public void setScoringAdjustment(int scoringAdjustment) {
        this.scoringAdjustment = scoringAdjustment;
    }

    public int getSolved() {
        return solved;
    }

    public void setSolved(int solved) {
        this.solved = solved;
    }

    public int getTeamExternalId() {
        return teamExternalId;
    }

    public void setTeamExternalId(int teamExternalId) {
        this.teamExternalId = teamExternalId;
    }

    public int getTeamGroupExternalId() {
        return teamGroupExternalId;
    }

    public void setTeamGroupExternalId(int teamGroupExternalId) {
        this.teamGroupExternalId = teamGroupExternalId;
    }

    private long toLong(String string, int defaultLong) {

        try {
            return Long.parseLong(string.trim().toLowerCase());
        } catch (Exception e) {
            return defaultLong;
        }
    }

    private int toInt(String string, int defaultInt) {
        try {
            return Integer.parseInt(string.trim().toLowerCase());
        } catch (Exception e) {
            return defaultInt;
        }
    }

    public String toJSON() throws JsonProcessingException {
        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }

    @Override
    public String toString() {
        try {
            return toJSON();
        } catch (Exception e) {
            return "<invalid " + e.getMessage() + ">";
        }
    }

    boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

}
