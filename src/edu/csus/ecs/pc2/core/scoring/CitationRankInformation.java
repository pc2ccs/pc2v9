// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.scoring;

/**
 * CitationRankInformation - class to hold ranks for the various citations.
 * The medal places, gold, silver and bronze are what they are, independent of rank.
 * The "honors" ranks (Bill rules) may be comprised of multiple ranks.
 * Honorable mention rank is the smallest rank for any team in honorable mention.
 *
 * @author John Buck
 *
 */
public class CitationRankInformation {
    private int lastGoldPlace;
    private int lastSilverPlace;
    private int lastBronzePlace;
    private int lastHighestHonorsRank;
    private int lastHighHonorsRank;
    private int lastHonorsRank;
    private int firstHonorableMentionRank;

    public int getLastGoldPlace() {
        return lastGoldPlace;
    }
    public void setLastGoldPlace(int lastGoldPlace) {
        this.lastGoldPlace = lastGoldPlace;
    }
    public int getLastSilverPlace() {
        return lastSilverPlace;
    }
    public void setLastSilverPlace(int lastSilverPlace) {
        this.lastSilverPlace = lastSilverPlace;
    }
    public int getLastBronzePlace() {
        return lastBronzePlace;
    }
    public void setLastBronzePlace(int lastBronzePlace) {
        this.lastBronzePlace = lastBronzePlace;
    }
    public int getLastHighestHonorsRank() {
        return lastHighestHonorsRank;
    }
    /**
     * Updates highest honors rank if supplied rank is bigger than current one
     * @param lastHighestHonorsRank
     */
    public void updateLastHighestHonorsRank(int lastHighestHonorsRank) {
        if(lastHighestHonorsRank > this.lastHighestHonorsRank) {
            this.lastHighestHonorsRank = lastHighestHonorsRank;
        }
    }
    public int getLastHighHonorsRank() {
        return lastHighHonorsRank;
    }
    /**
     * Updates high honors rank if supplied rank is bigger than current one
     * @param lastHighHonorsRank
     */
    public void updateLastHighHonorsRank(int lastHighHonorsRank) {
        if(lastHighHonorsRank > this.lastHighHonorsRank) {
            this.lastHighHonorsRank = lastHighHonorsRank;
        }
    }
    public int getLastHonorsRank() {
        return lastHonorsRank;
    }
    /**
     * Updates honors rank if supplied rank is bigger than current one
     * @param lastHonorsRank
     */
    public void updateLastHonorsRank(int lastHonorsRank) {
        if(lastHonorsRank > this.lastHonorsRank) {
            this.lastHonorsRank = lastHonorsRank;
        }
    }
    public int getFirstHonorableMentionRank() {
        return firstHonorableMentionRank;
    }
    /**
     * Updates honorable mention rank if rank is smaller than current one.
     * This keeps track of the lowest rank for HM so all teams with this rank and
     * higher are HM.
     *
     * @param firstHonorableMentionRank
     */
   public void updateFirstHonorableMentionRank(int firstHonorableMentionRank) {
        if(this.firstHonorableMentionRank == 0 || firstHonorableMentionRank < this.firstHonorableMentionRank) {
            this.firstHonorableMentionRank = firstHonorableMentionRank;
        }
    }
}
