// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.scoring;

/**
 * @author John Buck
 *
 */
public class CitationRankInformation {
    private int lastGoldRank;
    private int lastSilverRank;
    private int lastBronzeRank;
    private int lastHighestHonorsRank;
    private int lastHighHonorsRank;
    private int lastHonorsRank;
    private int firstHonorableMentionRank;

    public int getLastGoldRank() {
        return lastGoldRank;
    }
    public void setLastGoldRank(int lastGoldRank) {
        this.lastGoldRank = lastGoldRank;
    }
    public int getLastSilverRank() {
        return lastSilverRank;
    }
    public void setLastSilverRank(int lastSilverRank) {
        this.lastSilverRank = lastSilverRank;
    }
    public int getLastBronzeRank() {
        return lastBronzeRank;
    }
    public void setLastBronzeRank(int lastBronzeRank) {
        this.lastBronzeRank = lastBronzeRank;
    }
    public int getLastHighestHonorsRank() {
        return lastHighestHonorsRank;
    }
    public void setLastHighestHonorsRank(int lastHighestHonorsRank) {
        this.lastHighestHonorsRank = lastHighestHonorsRank;
    }
    public int getLastHighHonorsRank() {
        return lastHighHonorsRank;
    }
    public void setLastHighHonorsRank(int lastHighHonorsRank) {
        this.lastHighHonorsRank = lastHighHonorsRank;
    }
    public int getLastHonorsRank() {
        return lastHonorsRank;
    }
    public void setLastHonorsRank(int lastHonorsRank) {
        this.lastHonorsRank = lastHonorsRank;
    }
    public int getFirstHonorableMentionRank() {
        return firstHonorableMentionRank;
    }
    public void setFirstHonorableMentionRank(int firstHonorableMentionRank) {
        this.firstHonorableMentionRank = firstHonorableMentionRank;
    }

}
