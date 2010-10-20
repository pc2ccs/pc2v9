package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.archive.PacketFormatter;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProfileListener;
import edu.csus.ecs.pc2.core.model.ProfileEvent;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;

/**
 * Packet Viewer.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketViewerFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3020304335848475659L;

    private Packet packet;

    private JPanel mainPane = null;

    private JPanel buttonPane = null;

    private JButton closeButton = null;

    private JPanel topPane = null;

    private JLabel topLabel = null;

    private JScrollPane scrollPane = null;

    private JTree tree = null;

    public PacketViewerFrame(Packet packet, JScrollPane pane) {
        super();
        initialize();
        this.packet = packet;
        showPacketInfo(pane);
    }

    public PacketViewerFrame(Packet packet) {
        this(packet, getPacketTree(packet));
    }

    public static final JScrollPane getPacketTree(Packet packet) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Packet " + packet.getType());
        PacketFormatter.buildContentTree(top, packet);
        JTree jTree = new JTree(top, true);
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane treeView = new JScrollPane(jTree);
        treeView.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return treeView;
    }

    /**
     * Name for client, especially servers.
     * 
     * @param clientId
     * @return name
     */
    protected Object getClientName(ClientId clientId) {
        if (clientId == null) {
            return "<null>";
        } else {
            if (clientId.getClientType().equals(ClientType.Type.SERVER)) {
                if (clientId.equals(PacketFactory.ALL_SERVERS)) {
                    return "All Servers";
                } else {
                    return "Site " + clientId.getSiteNumber();
                }
            } else {
                return clientId.getName();
            }
        }
    }

    private void showPacketInfo(JScrollPane pane) {
        setTitle("Packet Viewer " + packet.getPacketNumber() + " " + packet.getType() + " " + " " + getClientName(packet.getSourceId()) + " to " + getClientName(packet.getDestinationId()));
        
        String original = "";
        if (packet.getOriginalPacketNumber() == packet.getPacketNumber()){
            original = " (Orig #"+packet.getOriginalPacketNumber()+")";
        }
        
        topLabel.setText(packet.toString()+original);
        
        JTree newTree = (JTree) pane.getViewport().getComponent(0);
        getTree().setModel(newTree.getModel());
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(590, 239));
        this.setContentPane(getMainPane());
        this.setTitle("Packet Viewer");

        FrameUtilities.centerFrame(this);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                closeWindow();
            }
        });

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {

        inContest.addProfileListener(new ProfileListenerImplementation());
    }

    public String getPluginTitle() {
        return "Packet Viewer Frame";
    }

    protected void closeWindow() {
        setVisible(false);
        dispose();
    }

    /**
     * Profile Listener Implementation
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class ProfileListenerImplementation implements IProfileListener {

        public void profileAdded(ProfileEvent event) {
        }

        public void profileChanged(ProfileEvent event) {
        }

        public void profileRemoved(ProfileEvent event) {
        }

        public void profileRefreshAll(ProfileEvent profileEvent) {
            closeWindow();
        }
    }

    /**
     * This method initializes mainPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPane() {
        if (mainPane == null) {
            mainPane = new JPanel();
            mainPane.setLayout(new BorderLayout());
            mainPane.add(getButtonPane(), BorderLayout.SOUTH);
            mainPane.add(getTopPane(), BorderLayout.NORTH);
            mainPane.add(getScrollPane(), BorderLayout.CENTER);
        }
        return mainPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout());
            buttonPane.setPreferredSize(new Dimension(35, 35));
            buttonPane.add(getCloseButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes closeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setText("Close");
            closeButton.setMnemonic(KeyEvent.VK_C);
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    closeWindow();
                }
            });
        }
        return closeButton;
    }

    /**
     * This method initializes topPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPane() {
        if (topPane == null) {
            topLabel = new JLabel();
            topLabel.setText("JLabel");
            topPane = new JPanel();
            topPane.setLayout(new GridBagLayout());
            topPane.add(topLabel, new GridBagConstraints());
        }
        return topPane;
    }

    /**
     * This method initializes scrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getTree());
        }
        return scrollPane;
    }

    /**
     * This method initializes tree
     * 
     * @return javax.swing.JTree
     */
    private JTree getTree() {
        if (tree == null) {
            tree = new JTree();
        }
        return tree;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
