package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

import java.awt.Dimension;

/**
 * Packet Monitor Frame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketMonitorFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -372214336604817803L;

    private PacketMonitorPane packetMonitorPane = new PacketMonitorPane();

    /**
     * This method initializes
     * 
     */
    public PacketMonitorFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(729, 280));
        this.setContentPane(getPacketMonitorPane());
        this.setTitle("Packet Monitor");

        FrameUtilities.centerFrame(this);
    }

    /**
     * This method initializes packetMonitorPane
     * 
     * @return edu.csus.ecs.pc2.ui.PacketMonitorPane
     */
    private PacketMonitorPane getPacketMonitorPane() {
        if (packetMonitorPane == null) {
            packetMonitorPane = new PacketMonitorPane();
        }
        return packetMonitorPane;
    }

    public String getPluginTitle() {
        return "Packet Monitor Frame";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        packetMonitorPane.setParentFrame(this);
        packetMonitorPane.setContestAndController(inContest, inController);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
