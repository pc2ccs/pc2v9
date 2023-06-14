// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

/**
 * A set of methods for message listeners.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public interface IMessageRecordListener {

    /**
     * Message added.
     * 
     * @param record
     */
    void messageAdded(MessageRecord record);

}
