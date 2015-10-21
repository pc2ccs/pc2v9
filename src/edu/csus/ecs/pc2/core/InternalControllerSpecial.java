package edu.csus.ecs.pc2.core;

import java.io.Serializable;
import java.util.ArrayList;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.transport.IBtoA;
import edu.csus.ecs.pc2.core.transport.ITransportManager;
import edu.csus.ecs.pc2.core.transport.ITwoToOne;
import edu.csus.ecs.pc2.core.transport.TransportException;

/**
 * Special Controller which saves packets rather than sending packets.
 * <P>
 * This replaces the ConnectionManager.send with a method
 * that saves every {@link Packet} to a list.   
 * <P>
 * Likely used for testing API and Controller features.
 * <P>
 * <b>Note:</b> the Contest client must <u>not</u> be a server client type
 * for these methods to work.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: InternalController.java 2920 2015-02-05 15:54:18Z laned $
 */
// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/InternalController.java $
public class InternalControllerSpecial extends InternalController {

    private PacketConnectionManager manager = new PacketConnectionManager();

    public InternalControllerSpecial(IInternalContest contest) {
        super(contest);
        setConnectionManager(manager);
        
        Log logger = new Log("InternalControllerSpecial.log");
        setLog(logger);
    }

    /**
     * Get list of all packet sent.
     * 
     * @return all packets sent.
     */
    public Packet[] getPacketList() {
        return manager.getPacketList();
    }

    /**
     * 
     * @return last packet sent or null if no packets sent.
     */
    public Packet getLastPacket() {
        Packet[] list = getPacketList();
        if (list.length > 0) {
            return list[list.length - 1];
        } else {
            return null;
        }

    }
    
    /**
     * Removes all saved packets.
     */
    public void clearPacketList() {
        manager.clearPacketList();
    }

    /**
     * A manager that collects packets as sent.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class PacketConnectionManager implements ITransportManager {

        /**
         * List o' packets.
         */
        private ArrayList<Packet> packetList = new ArrayList<>();

        /**
         * Get list of packets sent.
         * 
         * @return
         */
        public Packet[] getPacketList() {
            return (Packet[]) packetList.toArray(new Packet[packetList.size()]);
        }

        /**
         * Removes all saved packets.
         */
        public void clearPacketList() {
            packetList = new ArrayList<>();
        }

        @Override
        public void connectToMyServer() throws TransportException {
            // nada
        }

        @Override
        public void send(Serializable serializable) throws TransportException {
            packetList.add((Packet) serializable);
        }

        @Override
        public void send(Serializable serializable, ConnectionHandlerID connectionHandlerID) throws TransportException {
            packetList.add((Packet) serializable);
        }

        @Override
        public ConnectionHandlerID connectToServer(String serverIP, int port) throws TransportException {
            // nada
            return null;
        }

        @Override
        public void startClientTransport(String serverIP, int port, IBtoA appCallBack) {
            // nada
        }

        @Override
        public void shutdownTransport() {
            // nada
        }

        @Override
        public void setLog(Log log) {
            // nada
        }

        @Override
        public void accecptConnections(int listeningPort) throws TransportException {
            // nada
        }

        @Override
        public void unregisterConnection(ConnectionHandlerID myConnectionID) {
            // nada
        }

        @Override
        public void startServerTransport(ITwoToOne appCallBack) {
            // nada
        }
    }
}
