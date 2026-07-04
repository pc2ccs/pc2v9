// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.scoring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.api.BaseClient;
import edu.csus.ecs.pc2.core.model.ClientId;

/**
 * A single record for a team of standings info.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StandingsRecord {

    /**
     * Division Rank Number.
     */
    private int divisionRankNumber = 0;

    /**
     * Rank Number.
     */
    private int rankNumber;

    /**
     * Rank within the Group.
     */
    private int groupRankNumber = 0;

    /**
     * Penalty Points.
     *
     */
    private long penaltyPoints;

    /**
     * Point score
     *
     */
    private double score;

    /**
     * Number of problems solved.
     */
    private int numberSolved;

    /**
     * When the 1st problem was solved
     */
    private long firstSolved  = -1;

    /**
     * When the last problem was solved
     */
    private long lastSolved;

    /**
     * Identifier for the team.
     *
     * Use {@link BaseClient#getClientTitle(ClientId) to get title or use {@link ClientId#getName()} to get a short name.
     */
    private ClientId clientId;

    /**
     * Problem summary info
     */
    private SummaryRow summaryRow = new SummaryRow();

    /**
     * @return Returns the numberSolved.
     */
    @JsonProperty
    public int getNumberSolved() {
        return numberSolved;
    }

    /**
     * @param numberSolved
     *            The numberSolved to set.
     */
    @JsonProperty
    public void setNumberSolved(int numberSolved) {
        this.numberSolved = numberSolved;
    }

    /**
     * @return Returns the penaltyPoints.
     */
    @JsonProperty
    public long getPenaltyPoints() {
        return penaltyPoints;
    }

    /**
     * @param penaltyPoints
     *            The penaltyPoints to set.
     */
    public void setPenaltyPoints(long penaltyPoints) {
        this.penaltyPoints = penaltyPoints;
    }

    /**
     * @return Returns the rankNumber.
     */
    @JsonProperty
    public int getRankNumber() {
        return rankNumber;
    }

    /**
     * @param rankNumber
     *            The rankNumber to set.
     */
    public void setRankNumber(int rankNumber) {
        this.rankNumber = rankNumber;
    }

    /**
     * @return Returns the firstSolved.
     */
    @JsonProperty
    public long getFirstSolved() {
        return firstSolved;
    }

    /**
     * @param firstSolved
     *            The firstSolved to set.
     */
    public void setFirstSolved(long firstSolved) {
        this.firstSolved = firstSolved;
    }

    /**
     * @return Returns the lastSolved.
     */
    @JsonProperty
    public long getLastSolved() {
        return lastSolved;
    }

    /**
     * @param lastSolved
     *            The lastSolved to set.
     */
    public void setLastSolved(long lastSolved) {
        this.lastSolved = lastSolved;
    }

    /**
     * @return Returns the clientId.
     */
    @JsonProperty
    public ClientId getClientId() {
        return clientId;
    }

    /**
     * @param clientId
     *            The clientId to set.
     */
    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    /**
     * Returns the SummaryRow contained in this StandingsRecord.
     * This method is marked as "@JsonIgnore" so that JSON serialization
     * will not use the summaryRow directly; this is important because
     * the SummaryRow contains a Map, which JSON serializes as an Object
     * instead of an Iterable (e.g. array).  
     * 
     * @see #getProblemSummaryInfo()
     * 
     * @return Returns the summaryRow.
     */
    @JsonIgnore
    public SummaryRow getSummaryRow() {
        return summaryRow;
    }

    /**
     * @param summaryRow
     *            The summaryRow to set.
     */
    public void setSummaryRow(SummaryRow summaryRow) {
        this.summaryRow = summaryRow;
    }

    /**
     * @return Returns the groupRankNumber.
     */
    @JsonProperty
    public int getGroupRankNumber() {
        return groupRankNumber;
    }

    /**
     * @param groupRankNumber The groupRankNumber to set.
     */
    public void setGroupRankNumber(int groupRankNumber) {
        this.groupRankNumber = groupRankNumber;
    }

    /**
     * Returns a String representation of this object in JSON format.
     */
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();

        String jsonString = "{}";
        try {
            jsonString = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // TODO: log exception
            e.printStackTrace();
        }
        return jsonString;
    }

    public int getDivisionRankNumber() {
        return divisionRankNumber;
    }
    /**
     * @param divisionRankNumber The groupRankNumber to set.
     */
    public void setDivisionRankNumber(int divisionRankNumber) {
        this.divisionRankNumber = divisionRankNumber;
    }

    /**
     * @return the score
     */
    public double getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }
    
    /**
     * Returns the per-problem summary information as an ordered list.
     * This is intended for JSON serialization and UI consumers (e.g. WTI / Angular),
     * since Angular requires Iterables for *ngFor.
     * 
     * Note that this method is never called directly by any PC2 code; rather,
     * it gets invoked by Jackson during the serialization process (that is,
     * when some code executes (something like):
     *      ObjectMapper mapper = new ObjectMapper();
     *      String json = mapper.writeValueAsString(standingsRecord);
     *      
     * Note also that this is the reason the "@JsonIgnore" annotations was added
     * to method getSummaryRow() (above); that annotation keeps the Jackson
     * serializer from using a method which returns a Object when what we want
     * to serialize for StandingsRecord components are iterables (e.g. arrays).
     * 
     */
    @JsonProperty("problemSummaryInfo")
    public java.util.List<ProblemSummaryInfo> getProblemSummaryInfo() {
        return summaryRow == null ? java.util.Collections.emptyList()
                                  : summaryRow.toOrderedList();
    }

}
