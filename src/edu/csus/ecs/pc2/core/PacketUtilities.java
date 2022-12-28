package edu.csus.ecs.pc2.core;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Packet Handling Utilities.
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class PacketUtilities {

    /**
     * 
     * @param run
     * @param contest
     * @param controller
     * @param packetHandler
     * @param sourceClientId
     * @param connectionHandlerID
     * @param packet
     */
    public static void checkoutRun(Run run, IInternalContest contest, IInternalController controller, PacketHandler packetHandler, ClientId judgeId, ConnectionHandlerID connectionHandlerID,
            Packet packet) {

        controller.getLog().log(Log.INFO, "Found Run for auto judge judge=" + judgeId + " Run is " + run);
        contest.removeAvailableAutoJudge(judgeId);

        try {
            controller.getLog().log(Level.INFO, "Attempting to checkout" + run + " to judge " + judgeId);

            // process run as if judge/client had sent a request run
            packetHandler.checkoutRun(packet, run, judgeId, false, true, connectionHandlerID);
        } catch (Exception e) {
            controller.getLog().log(Level.INFO, "Unable to checkout run " + run + " to judge " + judgeId, e);
        }
    }

    public static void checkoutRun(Run run, IInternalContest contest, IInternalController controller, PacketHandler packetHandler, ClientId judgeId) throws ClassNotFoundException, IOException, FileSecurityException {

        RunFiles runFiles = contest.getRunFiles(run);
        ClientId source = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);

        Enumeration<ConnectionHandlerID> connEnum = contest.getConnectionHandlerIDs(judgeId);

        // since only one judge login can only be on
        if (connEnum.hasMoreElements()) {
            ConnectionHandlerID connectionHandlerID = connEnum.nextElement();
            Packet packet = PacketFactory.createSubmittedRun(source, judgeId, run, runFiles, run.getOverRideElapsedTimeMS(), run.getOverrideNumber());
            checkoutRun(run, contest, controller, packetHandler, judgeId, connectionHandlerID, packet);
        } else {
            controller.getLog().log(Level.INFO, "Unable to checkout (unable to find ConnectionHandlerID) for  " + run + " to judge " + judgeId);
        }

    }

}
