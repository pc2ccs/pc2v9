package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Handler for priority messages.
 * 
 * Priority Mesaages are messages that are warnings that 
 * might require contest administrator attention.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PriorityMessageHandler implements UIPlugin {

    private IInternalContest contest;

    @SuppressWarnings("unused")
    private IInternalController controller;

    private Log log;

    /**
     * 
     */
    private static final long serialVersionUID = -1913676615562732681L;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.controller = inController;
        this.contest = inContest;

        String logName = stripChar(contest.getClientId().toString(), ' ') + ".priority";
        log = new Log(logName);
    }
    
    

    public String getPluginTitle() {
        return "Priority Message Handler";
    }

    /**
     * Strip the input string of all characterToRemove.
     * 
     * @param s
     * @param characterToRemove
     * @return the input string minus any occurrences of characterToRemove
     */
    protected String stripChar(String s, char characterToRemove) {
        int idx = s.indexOf(characterToRemove);
        while (idx > -1) {
            StringBuffer sb = new StringBuffer(s);
            idx = sb.indexOf(characterToRemove + "");
            while (idx > -1) {
                sb.deleteCharAt(idx);
                idx = sb.indexOf(characterToRemove + "");
            }
            return sb.toString();
        }
        return s;
    }



    /**
     * Print new message to log.
     * 
     * @param clientId who triggered this message
     * @param message message included
     * @param eventName name for event
     * @param exception optional exception
     */
    public void newMessage(ClientId clientId, String message, String eventName, Exception exception) {
        
        if (eventName == null){
            eventName = "";
        }
        
        if (exception != null) {
            log.log(Log.SEVERE, "From: " + clientId + " " + eventName + " " + message, exception);
        } else {
            log.log(Log.SEVERE, "From: " + clientId + " " + eventName + " " + message);
        }
    }
    
    public void newMessage(ClientId clientId, String message, String eventName, ContestSecurityException exception) {
        
        if (eventName == null){
            eventName = "";
        }
        
        if (exception != null) {
            log.log(Log.SEVERE, "SecurityException From:  " + clientId + " " + eventName + " " + message, exception);
            log.log(Log.SEVERE, "SecurityException Sec. Message: "+exception.getSecurityMessage()+" ConnHandId "+exception.getConnectionHandlerID());
        } else {
            log.log(Log.SEVERE, "From: " + clientId + " " + eventName + " " + message);
        }
    }
    

}
