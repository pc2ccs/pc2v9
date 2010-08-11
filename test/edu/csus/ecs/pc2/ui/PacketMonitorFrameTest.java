package edu.csus.ecs.pc2.ui;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IPacketListener;
import edu.csus.ecs.pc2.core.model.PacketEvent;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;

/**
 * packet Monitor JUnit
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketMonitorFrameTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static void main(String[] args) {

        executeOne();
        
    }

    /**
     * Test for PacketMonitorFrame and addPacketListener.
     * 
     * Creates frame, generates packets and 
     * 
     */
    public static void executeOne () {

        SampleContest sampleContest = new SampleContest();

        IInternalContest inContest = sampleContest.createContest(4, 5, 22, 11, true);
        IInternalController inController = sampleContest.createController(inContest, true, false);

        inController.addPacketListener(new IPacketListener() {

            public void packetReceived(PacketEvent event) {
                PacketFactory.dumpPacket(System.out, event.getPacket(), "PacketMonitorFrameTest " + event.getAction().toString());
            }

            public void packetSent(PacketEvent event) {
                PacketFactory.dumpPacket(System.out, event.getPacket(), "PacketMonitorFrameTest " + event.getAction().toString());
            }
        });

        PacketMonitorFrame frame = new PacketMonitorFrame();
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setContestAndController(inContest, inController);
        frame.setVisible(true);

        Run[] runs = sampleContest.createRandomRuns(inContest, 4, true, true, true);

        int runid = 1;
        
        for (Run run : runs) {
            ClientId source = run.getSubmitter();
            Packet packet = PacketFactory.createRunSubmissionConfirm(source, inContest.getClientId(), run);
            inController.outgoingPacket(packet);
            run.setNumber(runid);
            runid ++;
        }
        
        ClientId serverId = new ClientId(inContest.getSiteNumber(), Type.SERVER, 0);
        
        for (Run run : runs) {
            Packet packet = PacketFactory.createRunAvailable(inContest.getClientId(), serverId, run);
            inController.outgoingPacket(packet);
        }
    }
}
