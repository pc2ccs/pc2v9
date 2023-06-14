// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.util.Vector;

/**
 * Manage a set of message listeners.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class MessageManager {

    /**
     * The list of message listeners.
     */
    private static Vector<IMessageRecordListener> messageListenerList = new Vector<IMessageRecordListener>();

    /**
     * Add a listener into listener list
     */
    public static void addMessageListener(IMessageRecordListener listener) {
        messageListenerList.addElement(listener);
    }
    
    /**
     * Remove  a listener into listener list
     */
    public static void deleteMessageListener(IMessageRecordListener listener) {
        messageListenerList.removeElement(listener);
    }


    /**
     * Send message to all listeners.
     * 
     * @param record - message
     */
    public static void fireMessageListener(MessageRecord record) throws Exception {
        for (int i = 0; i < messageListenerList.size(); i++) {
            messageListenerList.elementAt(i).messageAdded(record);
        }

        if (messageListenerList.size() == 0) {
            throw new Exception("No listeners added for message: " + record);
        }
    }
}
