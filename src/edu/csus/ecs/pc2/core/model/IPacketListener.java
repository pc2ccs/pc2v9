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
