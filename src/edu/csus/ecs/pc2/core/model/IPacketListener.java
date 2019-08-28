// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

/**
 * Methods required to implement a packet listener.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IPacketListener {

    /**
     * A packet has been received.
     * 
     * @param event
     */
    void packetReceived(PacketEvent event);

    /**
     * A packet has been sent.
     * 
     * @param event
     */
    void packetSent(PacketEvent event);

}
