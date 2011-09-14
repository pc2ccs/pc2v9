package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.core.transport.EventFeedServer;

/**
 * Event Feed Server Pane.
 * 
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

    private JPanel buttonPanel = null;

    private JButton startButton = null;

    private JButton stopButton = null;

    private JPanel centerPanel = null;

    private JLabel portLabel = null;

    private JTextField portTextField = null;

    private EventFeedServer eventFeedServer = new EventFeedServer(); // @jve:decl-index=0:

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
            JOptionPane.showMessageDialog(this, "You must enter a port number");
        }

        int port = Integer.parseInt(portTextField.getText());
        try {
            eventFeedServer.startSocketListener(port, getContest());
        } catch (IOException e) {
            // TODO CCS Log this exception
            JOptionPane.showMessageDialog(this, "Unable to start: "+e.getMessage());
            e.printStackTrace();
        }
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
            eventFeedServer.halt();
        }
    }

    /**
     * This method initializes centerPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.ipadx = 51;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(58, 7, 137, 215);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(58, 170, 137, 6);
            gridBagConstraints.gridy = 0;
            gridBagConstraints.ipady = -6;
            gridBagConstraints.gridx = 0;
            portLabel = new JLabel();
            portLabel.setPreferredSize(new Dimension(52, 26));
            portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            portLabel.setText("Port");
            centerPanel = new JPanel();
            centerPanel.setLayout(new GridBagLayout());
            centerPanel.add(portLabel, gridBagConstraints);
            centerPanel.add(getPortTextField(), gridBagConstraints1);
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
        }
        return portTextField;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
