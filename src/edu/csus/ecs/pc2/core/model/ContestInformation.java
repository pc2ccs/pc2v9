package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * Contest Wide Information.
 * 
 * 
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class ContestInformation implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -7333255582657988200L;

    public static final String SVN_ID = "$Id$";

    private String contestTitle;

    private String contestURL;
    
    public String getContestTitle() {
        return contestTitle;
    }

    public void setContestTitle(String contestTitle) {
        this.contestTitle = contestTitle;
    }

    public String getContestURL() {
        return contestURL;
    }

    public void setContestURL(String contestURL) {
        this.contestURL = contestURL;
    }
}
