package edu.csus.ecs.pc2.core.log;

import java.util.Properties;

import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.Contest;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;

/**
 * Create a message (toString) for input classes.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public final class MessageFactory {

    private MessageFactory() {

    }

    public static String createMessage(Contest contest, Packet packet) {

        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        if (run != null) {
            return run.toString();
        }

        Clarification clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
        if (clarification != null) {
            return clarification.toString();
        }

        ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
        if (contestTime != null) {
            return contestTime.toString();
        }

        Object object = packet.getContent();

        if (object instanceof Properties) {

            Properties properties = (Properties) object;
            String[] keys = (String[]) properties.keySet().toArray(new String[properties.keySet().size()]);
            StringBuffer stringBuffer = new StringBuffer("Items:");

            for (String name : keys) {
                stringBuffer.append(' ');
                stringBuffer.append(name);
            }
            return new String(stringBuffer);

        } else if (object != null) {
            return object.getClass().getName().toString();
        } else {
            return "<null contents>";
        }
    }
}
