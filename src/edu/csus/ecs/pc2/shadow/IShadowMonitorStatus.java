// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

/**
 * An interface to provide status updates from the shadow remote feed monitor.
 * A user of the RemoteEventFeedMonitor has the option to get upcalls for various
 * counters that change, such as the number of events processed or the number of
 * records read.  In addition, the user can keep track of disconnects and reconnects.
 * 
 * @author John Buck
 *
 */
public interface IShadowMonitorStatus {

    /**
     * Updates last processed token
     * 
     * @param token the last token received and processed on the feed
     */
    public void updateShadowLastToken(String token);
    
    /**
     * Updates number of records received
     * 
     * @param number of records received
     */
    public void updateShadowNumberofRecords(int nRec);
    
    /**
     * Could not establish connection to primary
     * 
     * @param token last successfully processed event
     */
    public void connectFailed(String token);
    
    /**
     * Connection to primary succeeded
     * 
     * @param token where to start feed from (after this)
     */
    public void connectSucceeded(String token);
    
    /**
     * Unexpected close of remote
     * 
     * @param errMsg string describing what happened
     */
    public void errorDisconnect(String errMsg);
}
