package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IPacketListener;
import edu.csus.ecs.pc2.core.model.PacketEvent;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;

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

        inController.addPacketListener(new IPacketListener() {

            public void packetReceived(PacketEvent event) {
                PacketFactory.dumpPacket(System.out, event.getPacket(), "PacketMonitorFrameTest " + event.getAction().toString());
                addRow(event.getPacket());
            }

            public void packetSent(PacketEvent event) {
                PacketFactory.dumpPacket(System.out, event.getPacket(), "PacketMonitorFrameTest " + event.getAction().toString());
                addRow(event.getPacket());
            }
        });

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
            Object[] cols = { "Type", "##", "Time", "From", "To", "Contents"};
            packetListBox.addColumns(cols);

            // Sorts for columns

            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Type
            setColumnSorter(packetListBox, 0, sorter, 1);

            // Time
            setColumnSorter(packetListBox, 1, numericStringSorter, 2);

            // Time
            setColumnSorter(packetListBox, 2, numericStringSorter, 3);

            // From
            setColumnSorter(packetListBox, 3, sorter, 4);

            // To
            setColumnSorter(packetListBox, 4, sorter, 5);
            
            // Contents
            setColumnSorter(packetListBox, 5, sorter, 6);

            packetListBox.autoSizeAllColumns();

            cols = null;

        }
        return packetListBox;
    }

    void addRow(final Packet packet) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object row[] = createRow(packet);
                packetListBox.insertRow(row, 0);
                packetListBox.autoSizeAllColumns();
            }
        });
    }
    
    String getContentsDescription (Packet packet){
        String s = "";
        
        Object obj = packet.getContent();
        if (obj instanceof Properties) {
            Properties prop = (Properties) obj;
            Enumeration<?> enumeration = prop.keys();
            

            while (enumeration.hasMoreElements()) {
                String element = (String) enumeration.nextElement();
                String className = prop.get(element).getClass().getName();
                className = className.replaceFirst("edu.csus.ecs.pc2.core.model.", "");
                s += className + " ";
                if (prop.size() == 1) {
                    s += prop.get(element);
                }
            }
        } else {

            s = "  Contains: " + obj.toString() + " " + obj;
        }
        
        return s;
        
    }

    Object[] createRow(Packet packet) {

        Object[] objArray = new Object[packetListBox.getColumnCount()];

        long elapsed = 0;
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        if (run != null) {
            elapsed = run.getElapsedMins();
        }
        Clarification clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
        if (clarification != null) {
            elapsed = clarification.getElapsedMins();
        }

        objArray[0] = packet.getType().toString();
        objArray[1] = new Long(packet.getPacketNumber()).toString();
        objArray[2] = new Long(elapsed).toString();
        objArray[3] = getClientName(packet.getSourceId());
        objArray[4] = getClientName(packet.getDestinationId());
        objArray[5] = getContentsDescription(packet);;
        
        return objArray;
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
            clearButton.setMnemonic(KeyEvent.VK_C);
            clearButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getPacketListBox().removeAllRows();
                }
            });
        }
        return clearButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
