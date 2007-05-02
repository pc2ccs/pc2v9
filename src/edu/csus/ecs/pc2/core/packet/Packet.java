package edu.csus.ecs.pc2.core.packet;

import java.io.Serializable;
import java.util.Date;

import edu.csus.ecs.pc2.core.model.ClientId;

/**
 * A Packet which contains information sent between pc2 modules.
 * 
 * Packets are sent and received from the transport layer. Packets are created using {@link PacketFactory} for consistency. <br>
 * <br>
 * Each packet has a packet type {@link PacketType.Type}, who the packet is from {@link #getSourceId()}, who the packet is for
 * {@link #getDestinationId()} and the contents {@link #getContent()}. <br>
 * <br>
 * 
 * @see PacketType.Type
 * @see PacketFactory
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class Packet implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1201529987796311669L;

    public static final String SVN_ID = "$Id$";

    private ClientId sourceId = null;

    private ClientId destinationId = null;

    private PacketType.Type type = PacketType.Type.UNKNOWN;

    private Serializable content = null;
    
    private Date createDate = new Date();

    // TODO change this back to protected, soon.
    public Packet(PacketType.Type type, ClientId source, ClientId destination, Serializable content) {
        sourceId = source;
        destinationId = destination;
        this.content = content;
        this.type = type;
    }

    /**
     * Create a packet.
     * 
     * @param type
     * @param source
     * @param destination
     */
    protected Packet(PacketType.Type type, ClientId source, ClientId destination) {
        sourceId = source;
        destinationId = destination;
        this.content = null;
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Serializable content) {
        this.content = content;
    }

    public ClientId getDestinationId() {
        return destinationId;
    }

    protected void setDestinationId(ClientId destinationId) {
        this.destinationId = destinationId;
    }

    public ClientId getSourceId() {
        return sourceId;
    }

    protected void setSourceId(ClientId sourceId) {
        this.sourceId = sourceId;
    }

    public PacketType.Type getType() {
        return type;
    }

    public String toString() {
        return "Packet[" + type + "] " + sourceId + " -> " + destinationId;
    }

    private Date getCreateDate() {
        return createDate;
    }

    private void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

}
