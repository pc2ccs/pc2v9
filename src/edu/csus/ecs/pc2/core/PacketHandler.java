package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SubmittedRun;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.packet.PacketType.Type;

/**
 * Process all incoming packets.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public final class PacketHandler {

    private PacketHandler() {

    }

    /**
     * 
     * @param controller
     * @param model
     * @param packet
     */
    public static void handlePacket(IController controller, IModel model, Packet packet) {

        Type packetType = packet.getType();

        info("handlePacket " + packet);

        if (packetType.equals(Type.MESSAGE)) {

            PacketFactory.dumpPacket(System.err, packet);

        } else if (packetType.equals(Type.RUN_SUBMISSION)) {

            // TODO change to Run
            // Run submittedRun = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);

            SubmittedRun submittedRun = (SubmittedRun) packet.getContent();
            Run run = model.addRun(submittedRun);

            Packet confirmPacket = PacketFactory.createRunSubmissionConfirm(model.getClientId(), packet.getSourceId(), run);
            controller.sendToClient(confirmPacket);

        } else if (packetType.equals(Type.LOGIN_FAILED)) {
            String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);
            info("Login Failed: " + message);
        } else if (packetType.equals(Type.LOGIN_SUCCESS)) {
            loadDataIntoModel(packet, controller, model);
        } else {

            Exception exception = new Exception("PacketHandler.handlePacket Unhandled packet " + packet);
            info("Exception " + exception.getMessage());
            exception.printStackTrace(System.err);
        }

    }

    private static void loadDataIntoModel(Packet packet, IController controller, IModel model) {

         ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
         if (clientId != null) {
             controller.setClientId(clientId);
         }

        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        if (siteNumber != null) {
            controller.setSiteNumber(siteNumber.intValue());
        }
        
        info ("Site set to "+model.getSiteNumber());
        

        Language[] languages = (Language[]) PacketFactory.getObjectValue(packet, PacketFactory.LANGUAGE_LIST);
        if (languages != null) {
            for (Language language : languages) {
                model.addLanguage(language);
            }
        }

        Problem[] problems = (Problem[]) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM_LIST);
        if (problems != null) {
            for (Problem problem : problems) {
                model.addProblem(problem);
            }
        }

        ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
        if (contestTime != null) {
            controller.setContestTime(contestTime);
        }

    }
    public static void info(String s) {
        System.err.println(Thread.currentThread().getName() + " " + s);
    }
}
