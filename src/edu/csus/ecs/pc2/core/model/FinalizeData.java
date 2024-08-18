// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * CCS Finalization Data.
 *
 * When a contest is finalized it is "certified" and a result is that
 * the contest data is locked down, no changes that would change
 * score results will be allowed.  This condition can be checked
 * using the {@link #isCertified()} method.
 *
 * <br><br>
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

    private boolean certified = false;

    private Date certificationDate = null;

    private boolean useWFGroupRanking = true;

    private boolean customizeHonorsSolvedCount = false;

    private int highestHonorSolvedCount = 0;

    private int highHonorSolvedCount = 0;

    private int honorSolvedCount = 0;

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

    /**
     * Is contest certified?.
     *
     * @param certified true certifies, false un-certifies.
     */
    public void setCertified(boolean certified) {
        this.certified = certified;
        if (certified) {
            certificationDate = new Date();
        } else {
            certificationDate = null;
        }

    }

    /**
     * Has is the contest been finalized/certified?.
     *
     * @return true if certified/finalized.
     */
    public boolean isCertified() {
        return certified;
    }

    /**
     * Date when certified.
     *
     * @return
     */
    public Date getCertificationDate() {
        return certificationDate;
    }

    /**
     * Set whether or not to show rankings in results files as groups based on # of problems
     * solved, after the medal ranks.
     *
     * @param useWFGroupRanking
     */
    public void setUseWFGroupRanking(boolean useWFGroupRanking) {
        this.useWFGroupRanking = useWFGroupRanking;
    }

    /**
     * Return whether or not WF group rankings (based on # problems solved) should be used for results files
     *
     * @return true if WF group rankings are to be used
     */
    public boolean isUseWFGroupRanking() {
        return useWFGroupRanking;
    }

    /**
     * Set whether to customize the minimum number of problems solved for each Honors field
     *
     * @param customizeHonorsSolvedCount
     */
    public void setCustomizeHonorsSolvedCount(boolean customizeHonorsSolvedCount) {
        this.customizeHonorsSolvedCount = customizeHonorsSolvedCount;
    }

    /**
     * Return whether or not minimum problems solved for honors fields is to be customized or not
     *
     * @return true if WF group rankings are to be used
     */
    public boolean isCustomizeHonorsSolvedCount() {
        return customizeHonorsSolvedCount;
    }

    /**
     * Set the customized values of minimum number of problems solved for Highest Honors, High Honors and Honors fields
     *
     * @param highestHonorSolvedCount
     * @param highHonorSolvedCount
     * @param honorSolvedCount
     */
    public void setHonorsSolvedCount(int highestHonorSolvedCount, int highHonorSolvedCount, int honorSolvedCount) {
        this.highestHonorSolvedCount = highestHonorSolvedCount;
        this.highHonorSolvedCount = highHonorSolvedCount;
        this.honorSolvedCount = honorSolvedCount;
    }

    /**
     * Return  the customized minimum problems required to solve to be Highest Honors
     *
     * @return the customized minimum problems required to solve to be Highest Honors
     */
    public int getHighestHonorSolvedCount() {
        return highestHonorSolvedCount;
    }

    /**
     * Return  the customized minimum problems required to solve to be High Honors
     *
     * @return the customized minimum problems required to solve to be High Honors
     */
    public int getHighHonorSolvedCount() {
        return highHonorSolvedCount;
    }

    /**
     * Return  the customized minimum problems required to solve to be Honors
     *
     * @return the customized minimum problems required to solve to be Honors
     */
    public int getHonorSolvedCount() {
        return honorSolvedCount;
    }
}
