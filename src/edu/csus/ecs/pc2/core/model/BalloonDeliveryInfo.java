/**
 * 
 */
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonDeliveryInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4340028000313282358L;
    private long timeSent;
    private ClientId clientId;
    private ElementId problemId;
    
    /**
     * 
     */
    public BalloonDeliveryInfo(ClientId inClientId, ElementId inProblemId, long inTimeSent) {
        super();
        clientId = inClientId;
        problemId = inProblemId;
        timeSent = inTimeSent;
    }

    /**
     * @return Returns the clientId.
     */
    public ClientId getClientId() {
        return clientId;
    }

    /**
     * @param clientId The clientId to set.
     */
    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    /**
     * @return Returns the problemId.
     */
    public ElementId getProblemId() {
        return problemId;
    }

    /**
     * @param problemId The problemId to set.
     */
    public void setProblemId(ElementId problemId) {
        this.problemId = problemId;
    }

    /**
     * @return Returns the timeSent.
     */
    public long getTimeSent() {
        return timeSent;
    }

    /**
     * @param timeSent The timeSent to set.
     */
    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }

    public String getKey() {
        return clientId.getTripletKey()+" "+problemId.toString();
    }
}
