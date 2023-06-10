// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.util.Vector;

/**
 * A set of methods that manage messages.
 * 
 * Implements Observable design pattern for messages.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class MessageManager {

    private static Vector<IMessageRecordListener> messageListenerList = new Vector<IMessageRecordListener>();

    /**
     * Add a listener.
     */
    public static void addMessageListener(IMessageRecordListener listener) {
        messageListenerList.addElement(listener);
    }

    /**
     * Fire/use all listeners
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
