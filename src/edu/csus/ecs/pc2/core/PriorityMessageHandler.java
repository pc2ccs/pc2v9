package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Handler for priority messages. 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PriorityMessageHandler implements UIPlugin {

    private IContest contest;

    @SuppressWarnings("unused")
    private IController controller;

    private Log log;

    /**
     * 
     */
    private static final long serialVersionUID = -1913676615562732681L;

    public void setContestAndController(IContest inContest, IController inController) {
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
     * @return
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

}
