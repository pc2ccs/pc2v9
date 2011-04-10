package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * CCS Finalization Data.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FinalizeData implements Serializable {

    // TODO move this into pc2v9 project

    /**
     * 
     */
    private static final long serialVersionUID = 6863510451427617120L;

    private int goldRank;

    private int silverRank;

    private int bronzeRank;

    private int numberBronzes;

    private String comment;

    public int getGoldRank() {
        return goldRank;
    }

    public void setGoldRank(int goldRank) {
        this.goldRank = goldRank;
    }

    public int getSilverRank() {
        return silverRank;
    }

    public void setSilverRank(int silverRank) {
        this.silverRank = silverRank;
    }

    public int getBronzeRank() {
        return bronzeRank;
    }

    public void setBronzeRank(int bronzeRank) {
        this.bronzeRank = bronzeRank;
    }

    public int getNumberBronzes() {
        return numberBronzes;
    }

    public void setNumberBronzes(int numberBronzes) {
        this.numberBronzes = numberBronzes;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
