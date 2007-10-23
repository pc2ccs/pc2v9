package edu.csus.ecs.pc2.core.scoring;

import edu.csus.ecs.pc2.core.model.ClientId;

/**
 *
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class StandingsRecord {

    public static final String SVN_ID = "$Id$";

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
    public int getNumberSolved() {
        return numberSolved;
    }

    /**
     * @param numberSolved
     *            The numberSolved to set.
     */
    public void setNumberSolved(int numberSolved) {
        this.numberSolved = numberSolved;
    }

    /**
     * @return Returns the penaltyPoints.
     */
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
    public int getGroupRankNumber() {
        return groupRankNumber;
    }

    /**
     * @param groupRankNumber The groupRankNumber to set.
     */
    public void setGroupRankNumber(int groupRankNumber) {
        this.groupRankNumber = groupRankNumber;
    }

}
