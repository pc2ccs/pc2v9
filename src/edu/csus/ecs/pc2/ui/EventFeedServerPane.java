package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.transport.EventFeedServer;

/**
 * Event Feed Server Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedServerPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 4739343418897893446L;

    public static final int DEFAULT_EVENT_FEED_PORT_NUMBER = 4713;

    public static final int DEFAULT_FILTERED_EVENT_FEED_PORT_NUMBER = 4714;

    private JPanel buttonPanel = null;

    private JButton startButton = null;

    private JButton stopButton = null;

    private JPanel centerPanel = null;

    private JLabel portLabel = null;

    private JTextField filteredPortTextField = null;

    private JTextField unFilteredPortTextField = null;

    private EventFeedServer eventFeedServer = new EventFeedServer(); // @jve:decl-index=0:

    private EventFeedServer filteredEventFeedServer = new EventFeedServer(); // @jve:decl-index=0:

    private JLabel eventFeedServerStatusLabel = null;

    private JButton viewButton = null;

    /**
     * This method initializes
     * 
     */
    public EventFeedServerPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(505, 250));
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        this.add(getCenterPanel(), BorderLayout.CENTER);

        enableButtons();
    }
    
    @Override
    public String getPluginTitle() {
        return "Event Feed Server Pane";
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.setPreferredSize(new Dimension(35, 35));
            buttonPanel.add(getStartButton(), null);
            buttonPanel.add(getStopButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes startButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartButton() {
        if (startButton == null) {
            startButton = new JButton();
            startButton.setText("Start");
            startButton.setMnemonic(KeyEvent.VK_S);
            startButton.setToolTipText("Start Event Feed Servers");
            startButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startEventServer();
                }
            });
        }
        return startButton;
    }

    protected void startEventServer() {
        startEventServer(eventFeedServer, unFilteredPortTextField, false);
        startEventServer(filteredEventFeedServer, filteredPortTextField, true);
    }

    private void startEventServer(EventFeedServer server, JTextField textField, boolean filteredFeed) {
        
        if (textField.getText() == null) {
            showMessage("You must enter a port number");
            return;
        }

        if (textField.getText().length() == 0) {
            showMessage("You must enter a port number");
            return;
        }

        int port = Integer.parseInt(textField.getText());
        try {
            server.startSocketListener(port, getContest(), filteredFeed);
        } catch (IOException e) {
            showMessage("Unable to start: "+e.getMessage());
            e.printStackTrace(); // TODO debug 22 - remove after debugging
            getLog().log(Log.INFO,e.getMessage(),e);
        }


        enableButtons();
    }

    private void showMessage(String string) {
        JOptionPane.showMessageDialog(this, string);
    }

    /**
     * This method initializes stopButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStopButton() {
        if (stopButton == null) {
            stopButton = new JButton();
            stopButton.setText("Stop");
            stopButton.setMnemonic(KeyEvent.VK_T);
            stopButton.setToolTipText("Stop Event Feed Servers");
            stopButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    stopEventServer();
                }
            });
        }
        return stopButton;
    }

    protected void stopEventServer() {

        if (eventFeedServer.isListening()) {
            try {
                eventFeedServer.halt();
            } catch (IOException e) {
                e.printStackTrace();
                getLog().log(Log.INFO,e.getMessage(),e);
            }
        }
        if (! eventFeedServer.isListening()) {
            eventFeedServerStatusLabel.setText("Event Feed NOT running");
        }

        if (filteredEventFeedServer.isListening()) {
            try {
                filteredEventFeedServer.halt();
            } catch (IOException e) {
                e.printStackTrace();
                getLog().log(Log.INFO,e.getMessage(),e);
            }
        }
        if (! eventFeedServer.isListening()) {
            eventFeedServerStatusLabel.setText("Event Feed NOT running");
        }

        enableButtons();
    }

    /**
     * This method initializes centerPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel();
            GridBagLayout gblCenterPanel = new GridBagLayout();
            gblCenterPanel.columnWidths = new int[]{198, 57, 167, 0};
            gblCenterPanel.rowHeights = new int[]{36, 23, 32, 23, 0};
            gblCenterPanel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
            gblCenterPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
            centerPanel.setLayout(gblCenterPanel);
            eventFeedServerStatusLabel = new JLabel();
            eventFeedServerStatusLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            eventFeedServerStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
            eventFeedServerStatusLabel.setText("Event Feed NOT running");
            GridBagConstraints gbcEventFeedServerStatusLabel = new GridBagConstraints();
            gbcEventFeedServerStatusLabel.fill = GridBagConstraints.BOTH;
            gbcEventFeedServerStatusLabel.insets = new Insets(0, 0, 5, 0);
            gbcEventFeedServerStatusLabel.gridwidth = 3;
            gbcEventFeedServerStatusLabel.gridx = 0;
            gbcEventFeedServerStatusLabel.gridy = 0;
            centerPanel.add(eventFeedServerStatusLabel, gbcEventFeedServerStatusLabel);
            portLabel = new JLabel();
            portLabel.setPreferredSize(new Dimension(52, 26));
            portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            portLabel.setText("UN-filtered event feed port");
            GridBagConstraints gbcportLabel = new GridBagConstraints();
            gbcportLabel.fill = GridBagConstraints.BOTH;
            gbcportLabel.insets = new Insets(0, 0, 5, 5);
            gbcportLabel.gridx = 0;
            gbcportLabel.gridy = 1;
            centerPanel.add(portLabel, gbcportLabel);
            GridBagConstraints gbcunFilteredPortTextField = new GridBagConstraints();
            gbcunFilteredPortTextField.fill = GridBagConstraints.HORIZONTAL;
            gbcunFilteredPortTextField.insets = new Insets(0, 0, 5, 5);
            gbcunFilteredPortTextField.gridx = 1;
            gbcunFilteredPortTextField.gridy = 1;
            centerPanel.add(getUnfilteredPortTextField(), gbcunFilteredPortTextField);
            GridBagConstraints gbcviewButton = new GridBagConstraints();
            gbcviewButton.anchor = GridBagConstraints.NORTHWEST;
            gbcviewButton.insets = new Insets(0, 0, 5, 0);
            gbcviewButton.gridx = 2;
            gbcviewButton.gridy = 1;
            centerPanel.add(getViewButton(), gbcviewButton);

            JLabel lblFilteredEventFeed = new JLabel();
            lblFilteredEventFeed.setText("Filtered event feed port");
            lblFilteredEventFeed.setPreferredSize(new Dimension(52, 26));
            lblFilteredEventFeed.setHorizontalAlignment(SwingConstants.RIGHT);
            GridBagConstraints gbclblFilteredEventFeed = new GridBagConstraints();
            gbclblFilteredEventFeed.fill = GridBagConstraints.BOTH;
            gbclblFilteredEventFeed.insets = new Insets(0, 0, 0, 5);
            gbclblFilteredEventFeed.gridx = 0;
            gbclblFilteredEventFeed.gridy = 3;
            centerPanel.add(lblFilteredEventFeed, gbclblFilteredEventFeed);
            GridBagConstraints gbcfilteredPortTextField = new GridBagConstraints();
            gbcfilteredPortTextField.fill = GridBagConstraints.HORIZONTAL;
            gbcfilteredPortTextField.insets = new Insets(0, 0, 0, 5);
            gbcfilteredPortTextField.gridx = 1;
            gbcfilteredPortTextField.gridy = 3;
            centerPanel.add(getFilteredPortTextField(), gbcfilteredPortTextField);

            JButton button = new JButton();
            button.setToolTipText("Show a snapshot of the filtered event feed");
            button.setText("Snapshot");
            button.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showEventFeedInViewer(true);
                }
            });
            GridBagConstraints gbcbutton = new GridBagConstraints();
            gbcbutton.anchor = GridBagConstraints.NORTHWEST;
            gbcbutton.gridx = 2;
            gbcbutton.gridy = 3;
            centerPanel.add(button, gbcbutton);
        }
        return centerPanel;
    }

    /**
     * This method initializes unFilteredPortTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getUnfilteredPortTextField() {
        if (unFilteredPortTextField == null) {
            unFilteredPortTextField = new JTextField();
            unFilteredPortTextField.setDocument(new IntegerDocument());
            unFilteredPortTextField.setText(Integer.toString(DEFAULT_EVENT_FEED_PORT_NUMBER));
        }
        return unFilteredPortTextField;
    }

    private JTextField getFilteredPortTextField() {
        if (filteredPortTextField == null) {
            filteredPortTextField = new JTextField();
            filteredPortTextField.setDocument(new IntegerDocument());
            filteredPortTextField.setText(Integer.toString(DEFAULT_FILTERED_EVENT_FEED_PORT_NUMBER));
        }
        return filteredPortTextField;
    }


    private void enableButtons () {
        boolean serverRunning = eventFeedServer.isListening();
        getStartButton().setEnabled(! serverRunning);
        getStopButton().setEnabled(serverRunning);

        if (serverRunning){
            eventFeedServerStatusLabel.setText("Event Feed servers running");
        } else {
            eventFeedServerStatusLabel.setText("Event Feed servers STOPPED");
        }
    }

    /**
     * This method initializes viewButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewButton() {
        if (viewButton == null) {
            viewButton = new JButton();
            viewButton.setToolTipText("Show a snapshot of the Un-filtered event feed");
            viewButton.setText("Snapshot");
            viewButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showEventFeedInViewer(false);
                }
            });
        }
        return viewButton;
    }

    private void showSnapshotOfEventViewer(boolean showFilteredFeed) {

        // EventFeedXML eventFeedXML = new EventFeedXML();
        // String [] lines = { eventFeedXML.toXML(getContest()) };

        // SOMEDAY CCS snapshot of event feed
//        int port = Integer.parseInt(getFilteredPortTextField().getText());
//        if (showFilteredFeed) {
//            port = Integer.parseInt(getUnfilteredPortTextField().getText());
//        }
//
//        String[] lines = SocketUtilities.readLinesFromPort(port);
//
//        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(getController().getLog());
//        multipleFileViewer.addTextintoPane("Event Feed", lines);
//        multipleFileViewer.setTitle("PC^2 Event Feed at " + new Date());
//        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
//        multipleFileViewer.setVisible(true);
        
        JOptionPane.showMessageDialog(this, "Snapshot not implemented, yet");
        
    }

    /**
     * show Event Viewer.
     * @param showFilteredFeed true if only show filtered feed
     */
    protected void showEventFeedInViewer(boolean showFilteredFeed) {
        showSnapshotOfEventViewer(showFilteredFeed);
    }
} // @jve:decl-index=0:visual-constraint="10,10"
