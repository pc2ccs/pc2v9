// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.StringUtilities;

/**
 * This class encapsulates information related to shadowing a remote Contest Control System.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowInformation {
    
    private boolean shadowModeEnabled ;
    private String remoteCCSURL ;
    private String remoteCCSLogin ;
    private String remoteCCSPassword ;
    private String lastEventID ;
    
    
    public boolean isShadowModeEnabled() {
        return shadowModeEnabled;
    }
    public void setShadowModeEnabled(boolean shadowModeEnabled) {
        this.shadowModeEnabled = shadowModeEnabled;
    }
    public String getRemoteCCSURL() {
        return remoteCCSURL;
    }
    public void setRemoteCCSURL(String remoteCCSURL) {
        this.remoteCCSURL = remoteCCSURL;
    }
    public String getRemoteCCSLogin() {
        return remoteCCSLogin;
    }
    public void setRemoteCCSLogin(String remoteCCSLogin) {
        this.remoteCCSLogin = remoteCCSLogin;
    }
    public String getRemoteCCSPassword() {
        return remoteCCSPassword;
    }
    public void setRemoteCCSPassword(String remoteCCSPassword) {
        this.remoteCCSPassword = remoteCCSPassword;
    }
    public String getLastEventID() {
        return lastEventID;
    }
    public void setLastEventID(String lastEventID) {
        this.lastEventID = lastEventID;
    }
    
    /**
     * Returns true if the fields of the specified {@link ShadowInformation} object are
     * the same as those in this ShadowInformation object; false otherwise.
     * 
     * @param otherShadowInfo the ShadowInformation object to be compared with this one
     * 
     * @return true if the other object is equivalent to this object
     */
    public boolean isSameAs(ShadowInformation otherShadowInfo) {
        try {
            if (shadowModeEnabled != otherShadowInfo.isShadowModeEnabled()) {
                return false;
            }
            if (! StringUtilities.stringSame(remoteCCSURL, otherShadowInfo.remoteCCSURL)) {
                return false;
            }            
            if (! StringUtilities.stringSame(remoteCCSLogin, otherShadowInfo.remoteCCSLogin)) {
                return false;
            }
            if (! StringUtilities.stringSame(remoteCCSPassword, otherShadowInfo.remoteCCSPassword)) {
                return false;
            }
            if (! StringUtilities.stringSame(lastEventID, otherShadowInfo.lastEventID)) {
                return false;
            }
             
            return true;
            
        } catch (Exception e) {
            e.printStackTrace(System.err); // TODO log this exception 
            return false;
        }
    }


}
