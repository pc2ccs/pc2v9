package edu.csus.ecs.pc2.core.security;

import java.util.Vector;

import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.security.SecurityMessageEvent.Action;

/**
 * Handler for Security message/exceptions.
 * 
 * Security Messages are messages that are warnings that require contest administrator attention.
 * <P>
 * The listeners for these events will receive a {@link edu.csus.ecs.pc2.core.security.SecurityMessageEvent}. 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SecurityMessageHandler {

    private Log log = null;

    private String logName = null;

    private Vector<ISecurityMessageListener> securityMessageListenerList = new Vector<ISecurityMessageListener>();

    public SecurityMessageHandler(ClientId clientId) {
        logName = stripChar(clientId.toString(), ' ') + ".security";
    }

    public String getPluginTitle() {
        return "Security Violation Message Handler";
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
     * Log security message and alert listeners.
     * @param clientId
     * @param message
     * @param eventName a name for the event like "submitted run" or "judged run"
     * @param exception
     */
    public void newMessage(ClientId clientId, String message, String eventName, Exception exception) {

        if (eventName == null) {
            eventName = "";
        }

        if (exception != null) {
            getLog().log(Log.SEVERE, "From: " + clientId + " " + eventName + " " + message, exception);
        } else {
            getLog().log(Log.SEVERE, "From: " + clientId + " " + eventName + " " + message);
        }

        SecurityMessageEvent securityMessageEvent = new SecurityMessageEvent(Action.NEW, clientId, message, eventName, exception);
        fireSecurityMessageListener(securityMessageEvent);
    }

    /**
     * Log security message and alert listeners
     * @param clientId
     * @param message
     * @param eventName a name for the event like "submitted run" or "judged run"
     * @param exception
     */
    public void newMessage(ClientId clientId, String message, String eventName, ContestSecurityException exception) {

        if (eventName == null) {
            eventName = "";
        }

        if (exception != null) {
            getLog().log(Log.SEVERE, "SecurityException From:  " + clientId + " " + eventName + " " + message, exception);
            getLog().log(Log.SEVERE, "SecurityException Sec. Message: " + exception.getSecurityMessage() + " ConnHandId " + exception.getConnectionHandlerID());
        } else {
            getLog().log(Log.SEVERE, "From: " + clientId + " " + eventName + " " + message);
        }

        SecurityMessageEvent securityMessageEvent = new SecurityMessageEvent(Action.NEW, clientId, message, eventName, exception);
        fireSecurityMessageListener(securityMessageEvent);
    }

    public Log getLog() {
        
        if (log == null){
            log = new Log(logName);
        }

        return log;
    }

    /**
     * Add security message Listener.
     * 
     * This listener will be given an {@link edu.csus.ecs.pc2.core.security.SecurityMessageEvent}
     * when a security message is added/logged. 
     * 
     * @param securityMessageListener
     */
    public void addSecurityMessageListener(ISecurityMessageListener securityMessageListener) {
        securityMessageListenerList.addElement(securityMessageListener);
    }

    /**
     * Remove security message listener.
     * @param securityMessageListener
     */
    public void removeSecurityMessageListener(ISecurityMessageListener securityMessageListener) {
        securityMessageListenerList.removeElement(securityMessageListener);
    }

    private void fireSecurityMessageListener(SecurityMessageEvent securityMessageEvent) {
        for (int i = 0; i < securityMessageListenerList.size(); i++) {

            switch (securityMessageEvent.getAction()) {
                case NEW:
                    securityMessageListenerList.elementAt(i).newMessage(securityMessageEvent);
                    break;
                default:
                    securityMessageListenerList.elementAt(i).newMessage(securityMessageEvent);
                    break;
            }
        }
    }
    

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
        log = null;
    }

}
