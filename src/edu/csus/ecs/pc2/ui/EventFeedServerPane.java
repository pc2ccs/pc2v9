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
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.transport.EventFeedServer;
import edu.csus.ecs.pc2.core.util.SocketUtilities;

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


    private JPanel buttonPanel = null;

    private JButton startButton = null;

    private JButton stopButton = null;

    private JPanel centerPanel = null;

    private JLabel portLabel = null;

    private JTextField portTextField = null;

    private EventFeedServer eventFeedServer = new EventFeedServer(); // @jve:decl-index=0:

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
            buttonPanel.add(getViewButton(), null);
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
            startButton.setToolTipText("Start Event Feed Server");
            startButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startEventServer();
                }
            });
        }
        return startButton;
    }

    protected void startEventServer() {

        if (portTextField.getText() == null) {
            showMessage("You must enter a port number");
            return;
        }
        
        if (portTextField.getText().length() == 0) {
            showMessage("You must enter a port number");
            return;
        }

        int port = Integer.parseInt(portTextField.getText());
        try {
            eventFeedServer.startSocketListener(port, getContest());
        } catch (IOException e) {
            // TODO CCS Log this exception
            showMessage("Unable to start: "+e.getMessage());
            e.printStackTrace();
        }
        
        if (eventFeedServer.isListening()) {
            eventFeedServerStatusLabel.setText("Event Feed running on port "+eventFeedServer.getPort());
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
            stopButton.setToolTipText("Stop Event Feed Server");
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
                // TODO CCS handle exception
                e.printStackTrace();
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
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.insets = new Insets(18, 17, 22, 17);
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.ipadx = 425;
            gridBagConstraints3.ipady = 17;
            gridBagConstraints3.gridwidth = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.ipadx = 51;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new Insets(23, 9, 96, 199);
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new Insets(23, 182, 96, 8);
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.ipady = -6;
            gridBagConstraints1.gridx = 0;
            eventFeedServerStatusLabel = new JLabel();
            eventFeedServerStatusLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            eventFeedServerStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
            eventFeedServerStatusLabel.setText("Event Feed NOT running");
            portLabel = new JLabel();
            portLabel.setPreferredSize(new Dimension(52, 26));
            portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            portLabel.setText("Port");
            centerPanel = new JPanel();
            centerPanel.setLayout(new GridBagLayout());
            centerPanel.add(portLabel, gridBagConstraints1);
            centerPanel.add(getPortTextField(), gridBagConstraints2);
            centerPanel.add(eventFeedServerStatusLabel, gridBagConstraints3);
        }
        return centerPanel;
    }

    /**
     * This method initializes portTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPortTextField() {
        if (portTextField == null) {
            portTextField = new JTextField();
            portTextField.setDocument(new IntegerDocument());
            portTextField.setText(Integer.toString(DEFAULT_EVENT_FEED_PORT_NUMBER));
        }
        return portTextField;
    }
    
    private void enableButtons () {
        boolean serverRunning = eventFeedServer.isListening();
        getStartButton().setEnabled(! serverRunning);
        getStopButton().setEnabled(serverRunning);
    }

    /**
     * This method initializes viewButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewButton() {
        if (viewButton == null) {
            viewButton = new JButton();
            viewButton.setText("View");
            viewButton.setMnemonic(KeyEvent.VK_V);
            viewButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showEventFeedInViewer();
                }
            });
        }
        return viewButton;
    }

    protected void showEventFeedInViewer() {
        
        int port = Integer.parseInt(getPortTextField().getText());
        
        final String [] lines = SocketUtilities.readLinesFromPort(port);
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MultipleFileViewer multipleFileViewer = new MultipleFileViewer(getController().getLog());
                multipleFileViewer.addTextintoPane("Event Feed", lines);
                multipleFileViewer.setTitle("PC^2 Event Feed at "+new Date());
                FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
                multipleFileViewer.setVisible(true);
            }
        });
        
    }

} // @jve:decl-index=0:visual-constraint="10,10"
