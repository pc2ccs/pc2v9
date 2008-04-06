package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;
import java.util.Date;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;

/**
 * Stand Alone Transport Test Program.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TransportManagerTest1 {

    private Log log = new Log("TransManTest1");

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class CallBack implements IBtoA {

        public void receiveObject(Serializable object) {
            info("receiveObject  " + object);
        }

        public void connectionError(Serializable object, ConnectionHandlerID connectionHandlerID, String causeDescription) {
            info("connectionError  " + object);
            info("connectionError connectionHandlerID = " + connectionHandlerID + " cause: " + causeDescription);
        }

        public void connectionDropped() {
            info("connectionDropped ");
        }
    }

    /**
     * Attach to server login as judge 4.
     * 
     * @param server
     * @param port
     */
    public void doTest(String server, int port) {

        Utilities.setDebugMode(true);

        // Login as client using transport manager

        try {
            info(" new TransportManager");
            TransportManager transportManager = new TransportManager(log);
            info("Server  : " + server);
            info("Port    : " + port);
            info("startClientTransport");

            transportManager.startClientTransport(server, port, new CallBack());

            info("connectToMyServer");
            transportManager.connectToMyServer();
            info("Connected");

            ClientId clientId = new ClientId(0, Type.JUDGE, 4);
            ClientId serverId = new ClientId(0, Type.SERVER, 0);

            info("Logging in as " + clientId);
            Packet packet = PacketFactory.createLogin(clientId, clientId.getName(), serverId, true);

            transportManager.send(packet);

        } catch (Exception e) {
            info("Exception ", e);
        }

    }

    private void info(String s) {
        getLog().info(s);
        if (Utilities.isDebugMode()) {
            System.out.println(new Date() + " " + Thread.currentThread().getName() + " " + s);
            System.out.flush();
        }

    }

    public void info(String s, Exception exception) {
        getLog().log(Log.INFO, s, exception);
        if (Utilities.isDebugMode()) {
            System.out.println(new Date() + " " + Thread.currentThread().getName() + " " + s);
            exception.printStackTrace(System.out);
            System.out.flush();
        }
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public static void main(String[] args) {

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("--help") || args[0].toLowerCase().startsWith("-h")) {
                System.out.println("Usage java TransportManagerTest1 [-h] [server port] ");
                System.exit(4);
            }
        }

        if (args.length == 2) {
            new TransportManagerTest1().doTest(args[0], Integer.parseInt(args[1]));
        } else {
            new TransportManagerTest1().doTest("localhost", 50002);

        }
    }
}
