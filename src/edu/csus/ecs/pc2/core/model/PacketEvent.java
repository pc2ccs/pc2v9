package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.packet.Packet;

/**
 * A packet event.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketEvent {
    
    /**
     * Packet States.
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * Received a packet.
         */
        RECEIVED, 
        
        /**
         * Send a packet.
         */
        SENT,
    }

    private Action action;
    
    private Packet packet;

    public PacketEvent(Action action, Packet packet) {
        super();
        this.action = action;
        this.packet = packet;
    }

    public Action getAction() {
        return action;
    }

    public Packet getPacket() {
        return packet;
    }
    
}
