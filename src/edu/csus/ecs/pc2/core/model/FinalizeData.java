package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * CCS Finalization Data.
 * 
 * This data contains information about which ranks (teams) get
 * medals, and provides information for the CCS Event Feed XML
 * finalized element.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FinalizeData implements Serializable {

    private static final long serialVersionUID = 6863510451427617120L;

    private int goldRank;

    private int silverRank;

    private int bronzeRank;

    private String comment;

    /**
     * @return last rank for gold.
     */
    public int getGoldRank() {
        return goldRank;
    }

    public void setGoldRank(int goldRank) {
        this.goldRank = goldRank;
    }

    /**
     * @return last rank for silver.
     */
    public int getSilverRank() {
        return silverRank;
    }

    public void setSilverRank(int silverRank) {
        this.silverRank = silverRank;
    }

    /**
     * @return last rank for bronze.
     */
    public int getBronzeRank() {
        return bronzeRank;
    }

    public void setBronzeRank(int bronzeRank) {
        this.bronzeRank = bronzeRank;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
