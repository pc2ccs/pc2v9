package edu.csus.ecs.pc2.core.packet;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import edu.csus.ecs.pc2.core.log.StaticLog;
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
 * @version $Id$
 */

// $HeadURL$
public class Packet implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1201529987796311669L;

    private ClientId sourceId = null;

    private ClientId destinationId = null;

    private PacketType.Type type = PacketType.Type.UNKNOWN;

    private Serializable content = null;
    
    private Date createDate = new Date();
    
    private static int packetCounter = 0;
    
    private int packetNumber = 0;
    
    private String hostAddress = "(unset)";

    private String hostName = "(unset)";
    
    private String contestIdentifier;

    // TODO change this back to protected, soon.
    public Packet(PacketType.Type type, ClientId source, ClientId destination, Serializable content) {
        sourceId = source;
        destinationId = destination;
        this.content = content;
        this.type = type;
        packetCounter++;
        packetNumber = packetCounter;
        setIP();
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
        packetCounter++;
        packetNumber = packetCounter;
        setIP();
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
        return "Packet[" + type + "] #"+packetNumber +" " + sourceId + " -> " + destinationId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public int getPacketNumber() {
        return packetNumber;
    }

    public void setPacketNumber(int packetNumber) {
        this.packetNumber = packetNumber;
    }
    
    /**
     * Set the host and 
     */
    private void setIP() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostAddress = inetAddress.getHostAddress();
            hostName = inetAddress.getHostName();
        } catch (UnknownHostException e) {
            StaticLog.log("Problem setting IP", e);
        }
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getHostName() {
        return hostName;
    }

    /**
     * Get contest/Profile identifier.
     * @return
     */
    public String getContestIdentifier() {
        return contestIdentifier;
    }

    /**
     * Set a contest/Profile identifier.
     * @param contestIdentifier
     */
    public void setContestIdentifier(String contestIdentifier) {
        this.contestIdentifier = contestIdentifier;
    }

}
