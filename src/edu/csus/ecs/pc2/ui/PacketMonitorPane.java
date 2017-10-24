package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;
import com.ibm.webrunner.j2mclb.util.TableModel;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.archive.PacketFormatter;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IPacketListener;
import edu.csus.ecs.pc2.core.model.PacketEvent;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.util.ReverseNumericStringComparator;

/**
 * Packet Monitor Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketMonitorPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1276113801345035959L;

    private JPanel buttonPane = null;

    private JButton detailsButton = null;

    private JButton reportButton = null;

    private MCLB packetListBox = null;

    private JButton clearButton = null;
    
    private long sequenceNumber = 1;
    
    private int maxLines = 500;
    
    private Log packetLog = null; // TODO  remove
    
    private IPacketListener listener = null;

    /**
     * This method initializes
     * 
     */
    public PacketMonitorPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(662, 169));
        this.add(getButtonPane(), BorderLayout.SOUTH);

        this.add(getPacketListBox(), BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Packet Monitor";
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new Dimension(40, 40));
            buttonPane.add(getClearButton(), null);
            buttonPane.add(getDetailsButton(), null);
            buttonPane.add(getReportButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes detailsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getDetailsButton() {
        if (detailsButton == null) {
            detailsButton = new JButton();
            detailsButton.setText("Details");
            detailsButton.setMnemonic(KeyEvent.VK_D);
            detailsButton.setToolTipText("Show Details about Selected Packet");
            detailsButton.setVisible(false);
            detailsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printDetails();
                }
            });
        }
        return detailsButton;
    }

    protected void printDetails() {

        JOptionPane.showMessageDialog(this, "Show Details");
        // FIXME code printDetails

    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        String moduleInfo = inContest.getTitle();

        getParentFrame().setTitle(getParentFrame().getTitle() + " " + moduleInfo);

        if (packetLog == null) { 

            System.err.println("debug - started log: " + "packetMonitorPane." + inContest.getClientId().getName());
            getController().getLog().info("debug - started log: " + "packetMonitorPane." + inContest.getClientId().getName());

            packetLog = new Log("packetMonitorPane." + inContest.getClientId().getName());
            packetLog.info("");
            packetLog.info(new VersionInfo().getSystemVersionInfo());
            packetLog.info("");
        }

        if (listener == null) {
            listener = new PacketListenerImplementation();
            inController.addPacketListener(listener);
        }
    }

    /**
     * format packet line for log.
     * 
     * @param direction
     * @param packet
     * @return log line.
     */
    protected String formatLogLine(String direction, Packet packet) {

        StringBuffer buf = new StringBuffer();
        
        buf.append(sequenceNumber);
        buf.append(' ');
        buf.append(direction);
        buf.append(' ');
        buf.append(packet.getPacketNumber());
        buf.append(' ');
        buf.append(packet.getOriginalPacketNumber());
        buf.append(' ');
        buf.append(packet.getType().toString());
        buf.append(' ');
        buf.append(getClientName(packet.getSourceId()));
        buf.append(' ');
        buf.append(getClientName(packet.getDestinationId()));

        return new String(buf);
    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Report");
            reportButton.setMnemonic(KeyEvent.VK_R);
            reportButton.setVisible(false);
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printReport();
                }
            });
        }
        return reportButton;
    }

    protected void printReport() {
        JOptionPane.showMessageDialog(this, "Show Reports");
        // FIXME code printReport
    }

    /**
     * This method initializes packetListBox
     * 
     * @return com.ibm.webrunner.j2mclb.MultiColumnListbox
     */
    private MCLB getPacketListBox() {
        if (packetListBox == null) {
            packetListBox = new MCLB();
            Object[] cols = { "Seq", "Type", "##", "Orig", "Time", "From", "To", "Contents"};
            packetListBox.addColumns(cols);

            packetListBox.setRowHeight(packetListBox.getRowHeight() * 4);
            
            // Sorts for columns

            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            HeapSorter reverseNumericSorter = new HeapSorter();
            reverseNumericSorter.setComparator(new ReverseNumericStringComparator());
            
            int idx = 0;

            // Seq
            setColumnSorter(packetListBox, idx++, reverseNumericSorter, 1);

            // Type
            setColumnSorter(packetListBox, idx++, sorter, 2);

            // Packet Number
            setColumnSorter(packetListBox, idx++, numericStringSorter, 3);

            // Original Packet Number
            setColumnSorter(packetListBox, idx++, numericStringSorter, 4);
            
            // Time
            setColumnSorter(packetListBox, idx++, numericStringSorter, 5);
            
            // From
            setColumnSorter(packetListBox, idx++, sorter, 6);

            // To
            setColumnSorter(packetListBox, idx++, sorter, 7);
            
            // Contents
            setColumnSorter(packetListBox, idx++, sorter, 8);

            packetListBox.autoSizeAllColumns();

            cols = null;
            
            packetListBox.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() > 1) {
                        showSelectedRowsPacket();
                    }
                }
            });

        }
        return packetListBox;
    }

    protected void showSelectedRowsPacket() {
        
//        Object[] cols = { "Seq", "Type", "##", "Orig", "Time", "From", "To", "Contents"};
        
        try {
            int selectedRow = getPacketListBox().getSelectedIndex();
            Packet packet = (Packet) getPacketListBox().getKeys()[selectedRow];
            
            Object [] selectedColumns = getPacketListBox().getSelectedRow();
            JScrollPane scroll = (JScrollPane) selectedColumns[7];
            
            PacketViewerFrame frame = new PacketViewerFrame (packet, scroll);
            frame.setVisible(true);
            
        } catch (Exception e) {
            getController().logWarning("Unable to show packet ", e);
        }
        
    }

    void addRow(final Packet packet) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                
                Object [] row = buildPacketRow(packet);
                
                truncateTo(maxLines);
                
                packetListBox.insertRow(row, packet, 0);
                packetListBox.autoSizeAllColumns();
                
                // FIXME remove from list box if > 100
//                while (packetListBox.getRowCount() > 100){
                
            }
        });
    }
    
    Object[] buildPacketRow(Packet packet) {

        Object[] objArray = new Object[packetListBox.getColumnCount()];

        objArray[0] = new Long(sequenceNumber++).toString();

        long elapsed = 0;
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        if (run != null) {
            elapsed = run.getElapsedMins();
        }
        Clarification clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
        if (clarification != null) {
            elapsed = clarification.getElapsedMins();
        }

        objArray[1] = packet.getType().toString();
        objArray[2] = Integer.toString(packet.getPacketNumber());
        // Object[] cols = { "Seq", "Type", "##", "Orig", "Time", "From", "To", "Contents"};
        objArray[3] = Integer.toString(packet.getOriginalPacketNumber());

        objArray[4] = Long.toString(elapsed);
        objArray[5] = getClientName(packet.getSourceId());
        objArray[6] = getClientName(packet.getDestinationId());
        objArray[7] = getPacketTree(packet);

        return objArray;
    }

    private JScrollPane getPacketTree(Packet packet) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Packet " + packet.getType());
        PacketFormatter.buildContentTree(top, packet);
        JTree tree = new JTree(top, true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane treeView = new JScrollPane(tree);
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

    /**
     * Set sorter for column in listbox.
     * 
     * @param listBox
     *            MCLB
     * @param columnNumber
     *            the column to apply the sort to.
     * @param sorter
     *            the sorter
     * @param sortRank
     *            which column will be sorted first, second, etc.
     */
    private void setColumnSorter(MCLB listBox, int columnNumber, HeapSorter sorter, int sortRank) {
        listBox.getColumnInfo(columnNumber).setSorter(sorter);
        listBox.getColumnInfo(columnNumber).getSorter().setSortOrder(sortRank);
    }

    /**
     * This method initializes clearButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getClearButton() {
        if (clearButton == null) {
            clearButton = new JButton();
            clearButton.setText("Clear");
            clearButton.setToolTipText("Remove all packets in list");
            clearButton.setMnemonic(KeyEvent.VK_C);
            clearButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getPacketListBox().removeAllRows();
                    sequenceNumber = 1;
                }
            });
        }
        return clearButton;
    }
    
    private void truncateTo(int numLines) {
        TableModel tableModel = getPacketListBox().getModel();
        if (tableModel.getRowCount() > numLines) {
            int lastRow = tableModel.getRowCount();
            for (int i = lastRow; i >= numLines; i--) {
                tableModel.removeRow(i);
            }
        }
    }
    
    public int getMaxLines() {
        return maxLines;
    }
    
    public void setMaxLines(int maxLines) {
        if (maxLines < 10) {
            throw new IllegalArgumentException("Max lines must be 10 or greater " + maxLines + " invalid)");
        }
        this.maxLines = maxLines;
    }
    
    /**
     * Packet Listener implementation.
     *  
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class PacketListenerImplementation implements IPacketListener {

        public void packetReceived(PacketEvent event) {
            // PacketFactory.dumpPacket(System.out, event.getPacket(), "PacketMonitorPane " + event.getAction().toString());
            packetLog.info(formatLogLine("in ", event.getPacket())); // TODO remove this line
            addRow(event.getPacket());
        }

        public void packetSent(PacketEvent event) {
            // PacketFactory.dumpPacket(System.out, event.getPacket(), "PacketMonitorPane " + event.getAction().toString());
            packetLog.info(formatLogLine("out", event.getPacket())); // TODO remove this line
            addRow(event.getPacket());
        }

    }

    
} // @jve:decl-index=0:visual-constraint="10,10"
