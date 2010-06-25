package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * Packet Explorer, stand alone app.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketExplorerPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5943773283797591796L;

    private JPanel centerPane = null;

    private JPanel southPane = null;

    private MCLB packetListBox = null;

    private JLabel dirLabel = null;

    private JTextField dirNameTextField = null;

    private JButton refreshButton = null;

    private JPanel northPane = null;

    private JLabel messageLabel = null;

    @SuppressWarnings("unused")
    private IInternalContest contest;

    @SuppressWarnings("unused")
    private IInternalController controller;
    
    private String contestPassword = null;

    private IStorage storage;

    public PacketExplorerPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(453,262));
        this.setLayout(new BorderLayout());
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getSouthPane(), java.awt.BorderLayout.SOUTH);
        this.add(getNorthPane(), java.awt.BorderLayout.NORTH);
        
        showMessage(Utilities.getCurrentDirectory());
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getPacketListBox(), java.awt.BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * This method initializes southPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSouthPane() {
        if (southPane == null) {
            dirLabel = new JLabel();
            dirLabel.setText("Directory");
            southPane = new JPanel();
            southPane.setPreferredSize(new java.awt.Dimension(45, 45));
            southPane.add(dirLabel, null);
            southPane.add(getDirNameTextField(), null);
            southPane.add(getRefreshButton(), null);
        }
        return southPane;
    }

    /**
     * This method initializes packetListBox
     * 
     * @return com.ibm.webrunner.j2mclb.MCLB
     */
    private MCLB getPacketListBox() {
        if (packetListBox == null) {
            packetListBox = new MCLB();
            Object[] cols = { "Type", "Time", "From", "To", "Contains" };
            packetListBox.addColumns(cols);

            // Sorts for columns

            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Type
            setColumnSorter(packetListBox, 0, sorter, 1);

            // Time
            setColumnSorter(packetListBox, 1, numericStringSorter, 2);

            // From
            setColumnSorter(packetListBox, 2, sorter, 3);

            // To
            setColumnSorter(packetListBox, 3, sorter, 4);

            // Contents
            setColumnSorter(packetListBox, 4, sorter, 5);
            
            packetListBox.autoSizeAllColumns();
            
            cols = null;
        }
        return packetListBox;
    }

    /**
     * This method initializes dirNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDirNameTextField() {
        if (dirNameTextField == null) {
            dirNameTextField = new JTextField();
            dirNameTextField.setPreferredSize(new java.awt.Dimension(200, 20));
            dirNameTextField.setText("packets");
        }
        return dirNameTextField;
    }

    /**
     * This method initializes refreshButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRefreshButton() {
        if (refreshButton == null) {
            refreshButton = new JButton();
            refreshButton.setText("Refresh");
            refreshButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    refreshDir();
                }
            });
        }
        return refreshButton;
    }

    
    /**
     * @param file
     *            to read as a packet
     * @return Packet from file
     * @throws Exception 
     */
    protected Packet fetchPC2Packet(File file) throws Exception {
        Object obj = storage.load(file.getCanonicalPath());
        if (obj instanceof Packet) {
            return (Packet) obj;
        } else { 
            throw new Exception("Not a packet: "+file.getName()+" is "+obj.getClass().toString());
        }
    }

    /**
     * 
     */
    protected void refreshDir() {
        String dirname = getDirNameTextField().getText().trim();
        
        packetListBox.removeAllRows();

        if (dirname.length() < 1) {
            showMessage("Enter a directory name");
            return;
        }

        File dir = new File(dirname);

        if (!dir.isDirectory()) {
            showMessage(dirname + " is not a directory.");
            return;
        }
        
        if (contestPassword == null){
            String password = JOptionPane.showInputDialog(this, "Enter Contest Password", "Password entry",JOptionPane.QUESTION_MESSAGE);
            try {
                FileSecurity fileSecurity = new FileSecurity("db.1");
                fileSecurity.verifyPassword(password.toCharArray());
                contestPassword = password;
            } catch (FileSecurityException e) {
                JOptionPane.showMessageDialog(this, "Password did not match "+e.getMessage());
                return;
            }
        }
        
        showMessage("Reading files from "+dirname);

        String[] filenames = dir.list();
        Arrays.sort(filenames);
        showMessage("Found "+filenames.length+" files in "+dirname);
        int packetsFound = 0;
        
        try {
            for (int i = 0; i < filenames.length; i++) {
                String packetFilename = dirname + File.separator + filenames[i];
                File packfile = new File(packetFilename);

                if (packfile.isFile()) {

                    System.out.println(packetFilename);

                    Packet packet = null;
                    
                    try {
                        packet = fetchPC2Packet(packfile);
                    } catch (Exception e) {
                        System.out.println(packfile.getName()+" " +e.getMessage());
                    }
                    
                    if (packet != null) {
                        addPacketToList(packet);
                        packetsFound++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showMessage("Found "+packetsFound+" packets in '"+dirname+"' ("+filenames.length+ " files)");

    }

    private void showMessage(final String string) {
       SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            messageLabel.setText(string);
        }
    });

    }

    /**
     * Adds packet to packetListBox.
     * 
     * @param packet
     *            to display
     */
    private void addPacketToList(final Packet packet) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objArray = new Object[packetListBox.getColumnCount()];
                // Object[] cols = {"Type", "Time", "From", "To", "Contains" };
                Object content = packet.getContent();

                long elapsed = 0;
                Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
                if (run != null) {
                    elapsed = run.getElapsedMins();
                }
                Clarification clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
                if (clarification != null) {
                    elapsed = clarification.getElapsedMins();
                }

                System.out.println(packet);

                objArray[0] = packet.getType().toString();
                objArray[1] = new Long(elapsed).toString();
                objArray[2] = getClientName(packet.getSourceId());
                objArray[3] = getClientName(packet.getDestinationId());

                if (content instanceof Properties) {
                    Properties props = (Properties) content;
                    String msg = "";
                    Enumeration<?> enumeration = props.keys();

                    while (enumeration.hasMoreElements()) {
                        String element = (String) enumeration.nextElement();
                        msg = msg + element + " ";
                    }
                    objArray[4] = "Properties: " + msg;
                } else {
                    objArray[4] = content.getClass().getName();
                }
                packetListBox.addRow(objArray, packet);
                packetListBox.autoSizeAllColumns();
            }
        });
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
     * This method initializes northPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPane() {
        if (northPane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("_");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            northPane = new JPanel();
            northPane.setLayout(new BorderLayout());
            northPane.setPreferredSize(new java.awt.Dimension(35, 35));
            northPane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return northPane;
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

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        setVisible (true);
    }
    
    public String getPluginTitle() {
        return "Packet Explorer";
    }

} // @jve:decl-index=0:visual-constraint="10,10"
