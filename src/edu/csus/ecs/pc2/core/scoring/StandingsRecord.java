package edu.csus.ecs.pc2.core.scoring;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
     * @return Returns the summaryRow.
     */
    @JsonProperty
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

}
