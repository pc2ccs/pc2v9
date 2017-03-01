package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * This class provides a GUI for configuring the embedded Jetty webserver. It allows specifying the port on which Jetty will listen and the REST service endpoints to which Jetty will respond. (Note
 * that REST endpoints are handled using Jersey, the JAX-RS implementation.)
 * 
 * By default the Jetty webserver is configured to listen on port 50080.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class WebServerPane extends JPanePlugin {

    private static final long serialVersionUID = -7805284416449568136L;

    public static final int DEFAULT_WEB_SERVER_PORT_NUMBER = WebServer.DEFAULT_WEB_SERVER_PORT_NUMBER;

    private JPanel buttonPanel = null;

    private JButton startButton = null;

    private JButton stopButton = null;

    private JPanel centerPanel = null;

    private JLabel portLabel = null;

    private JTextField portTextField = null;

    private JLabel webServerStatusLabel = null;

    private JCheckBox chckbxScoreboard;

    private JLabel lblEnabledWebServices;

    private JCheckBox chckbxProblems;

    private JCheckBox chckbxLanguages;

    private JCheckBox chckbxStarttime;

    private JCheckBox chckbxTeams;

    private WebServer webServer = null;

    /**
     * Constructs a new WebServerPane.
     * 
     */
    public WebServerPane() {
        super();
        initialize();
    }

    /**
     * This method initializes the WebServerPane.
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(505, 250));
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        this.add(getCenterPanel(), BorderLayout.CENTER);

        updateGUI();
    }

    @Override
    public String getPluginTitle() {
        return "Web Server Pane";
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
            startButton.setToolTipText("Start Web Server");
            startButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startWebServer();
                }
            });
        }
        return startButton;
    }

    /**
     * Starts a Jetty webserver running on the port specified in the GUI textfield, and registers a set of default REST (Jersey/JAX-RS) services with Jetty. TODO: need to provide support for
     * dynamically reconfiguring the registered services.
     * 
     */
    private void startWebServer() {

        if (portTextField.getText() == null) {
            showMessage("You must enter a port number");
            return;
        }

        if (portTextField.getText().length() == 0) {
            showMessage("You must enter a port number");
            return;
        }

        Properties properties = new Properties();

        properties.put(WebServer.PORT_NUMBER_KEY, portTextField.getText());
        properties.put(WebServer.SCOREBOARD_SERVICE_ENABLED_KEY, Boolean.toString(chckbxScoreboard.isSelected()));
        properties.put(WebServer.PROBLEMS_SERVICE_ENABLED_KEY, Boolean.toString(chckbxProblems.isSelected()));

        properties.put(WebServer.LANGUAGES_SERVICE_ENABLED_KEY, Boolean.toString(chckbxLanguages.isSelected()));
        properties.put(WebServer.STARTTIME_SERVICE_ENABLED_KEY, Boolean.toString(chckbxStarttime.isSelected()));

        properties.put(WebServer.TEAMS_SERVICE_ENABLED_KEY, Boolean.toString(chckbxTeams.isSelected()));

        getWebServer().startWebServer(getContest(), getController(), properties);

        updateGUI();
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
            stopButton.setToolTipText("Stop Web Server");
            stopButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    stopWebServer();
                }
            });
        }
        return stopButton;
    }

    /**
     * Stops the Jetty web server if it is running. Also destroys the web server. TODO: shouldn't really destroy the webserver; just stop it and cache the reference so that it can be quickly
     * restarted. (However, need to consider what happens if the user selects a different set of services to be enabled...)
     */
    protected void stopWebServer() {
        
        getWebServer().stop();
        updateGUI();
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
            gblCenterPanel.columnWidths = new int[] { 198, 57, 167, 0 };
            gblCenterPanel.rowHeights = new int[] { 36, 23, 32, 23, 0, 0, 0 };
            gblCenterPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
            gblCenterPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
            centerPanel.setLayout(gblCenterPanel);
            webServerStatusLabel = new JLabel();
            webServerStatusLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            webServerStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
            webServerStatusLabel.setText("Web Server NOT running");
            GridBagConstraints gbcEventFeedServerStatusLabel = new GridBagConstraints();
            gbcEventFeedServerStatusLabel.fill = GridBagConstraints.BOTH;
            gbcEventFeedServerStatusLabel.insets = new Insets(0, 0, 5, 0);
            gbcEventFeedServerStatusLabel.gridwidth = 3;
            gbcEventFeedServerStatusLabel.gridx = 0;
            gbcEventFeedServerStatusLabel.gridy = 0;
            centerPanel.add(webServerStatusLabel, gbcEventFeedServerStatusLabel);
            portLabel = new JLabel();
            portLabel.setPreferredSize(new Dimension(52, 26));
            portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            portLabel.setText("Web Server port");
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
            centerPanel.add(getPortTextField(), gbcunFilteredPortTextField);
            GridBagConstraints gbc_lblEnabledWebServices = new GridBagConstraints();
            gbc_lblEnabledWebServices.anchor = GridBagConstraints.EAST;
            gbc_lblEnabledWebServices.insets = new Insets(0, 0, 5, 5);
            gbc_lblEnabledWebServices.gridx = 0;
            gbc_lblEnabledWebServices.gridy = 2;
            centerPanel.add(getLblEnabledWebServices(), gbc_lblEnabledWebServices);
            GridBagConstraints gbc_chckbxScoreboard = new GridBagConstraints();
            gbc_chckbxScoreboard.anchor = GridBagConstraints.WEST;
            gbc_chckbxScoreboard.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxScoreboard.gridx = 1;
            gbc_chckbxScoreboard.gridy = 2;
            centerPanel.add(getChckbxScoreboard(), gbc_chckbxScoreboard);
            GridBagConstraints gbc_chckbxTeams = new GridBagConstraints();
            gbc_chckbxTeams.anchor = GridBagConstraints.WEST;
            gbc_chckbxTeams.insets = new Insets(0, 0, 5, 0);
            gbc_chckbxTeams.gridx = 2;
            gbc_chckbxTeams.gridy = 2;
            centerPanel.add(getChckbxTeams(), gbc_chckbxTeams);
            GridBagConstraints gbc_chckbxProblems = new GridBagConstraints();
            gbc_chckbxProblems.anchor = GridBagConstraints.WEST;
            gbc_chckbxProblems.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxProblems.gridx = 1;
            gbc_chckbxProblems.gridy = 3;
            centerPanel.add(getChckbxProblems(), gbc_chckbxProblems);
            GridBagConstraints gbc_chckbxLanguages = new GridBagConstraints();
            gbc_chckbxLanguages.anchor = GridBagConstraints.WEST;
            gbc_chckbxLanguages.insets = new Insets(0, 0, 5, 0);
            gbc_chckbxLanguages.gridx = 2;
            gbc_chckbxLanguages.gridy = 3;
            centerPanel.add(getChckbxLanguages(), gbc_chckbxLanguages);
            GridBagConstraints gbc_chckbxStarttime = new GridBagConstraints();
            gbc_chckbxStarttime.anchor = GridBagConstraints.WEST;
            gbc_chckbxStarttime.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxStarttime.gridx = 1;
            gbc_chckbxStarttime.gridy = 4;
            centerPanel.add(getChckbxStarttime(), gbc_chckbxStarttime);

        }
        return centerPanel;
    }

    /**
     * This method initializes portTextField to contain the default web server port number.
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPortTextField() {
        if (portTextField == null) {
            portTextField = new JTextField();
            portTextField.setDocument(new IntegerDocument());
            portTextField.setText(Integer.toString(DEFAULT_WEB_SERVER_PORT_NUMBER));
        }
        return portTextField;
    }

    /**
     * Updates the state of the web server status label and Start/Stop buttons to correspond to the state of the Jetty Server.
     */
    private void updateGUI() {

        boolean serverRunning = getWebServer().isServerRunning();
        
        getStartButton().setEnabled(!serverRunning);
        getStopButton().setEnabled(serverRunning);
        updateWebServerSettings(serverRunning);

        if (serverRunning) {
            webServerStatusLabel.setText("Web Server is running...");
        } else {
            webServerStatusLabel.setText("Web Server STOPPED");
        }
    }

    private void updateWebServerSettings(boolean serverRunning) {
        // if server is running, do not allow these settings to be changed
        getPortTextField().setEditable(!serverRunning);
        getChckbxScoreboard().setEnabled(!serverRunning);
        getChckbxProblems().setEnabled(!serverRunning);
        getChckbxTeams().setEnabled(!serverRunning);
        getChckbxLanguages().setEnabled(!serverRunning);
        getChckbxStarttime().setEnabled(!serverRunning);
    }

    private JCheckBox getChckbxScoreboard() {
        if (chckbxScoreboard == null) {
            chckbxScoreboard = new JCheckBox("/scoreboard");
            chckbxScoreboard.setSelected(true);
            chckbxScoreboard.setHorizontalAlignment(SwingConstants.LEFT);
            chckbxScoreboard.setToolTipText("Enable getting contest scoreboard");
        }
        return chckbxScoreboard;
    }

    private JLabel getLblEnabledWebServices() {
        if (lblEnabledWebServices == null) {
            lblEnabledWebServices = new JLabel("Enable Web Services:");
        }
        return lblEnabledWebServices;
    }

    private JCheckBox getChckbxProblems() {
        if (chckbxProblems == null) {
            chckbxProblems = new JCheckBox("/problems");
            chckbxProblems.setSelected(true);
            chckbxProblems.setToolTipText("Enable getting contest problems");
            chckbxProblems.setHorizontalAlignment(SwingConstants.LEFT);
        }
        return chckbxProblems;
    }

    private JCheckBox getChckbxLanguages() {
        if (chckbxLanguages == null) {
            chckbxLanguages = new JCheckBox("/languages");
            chckbxLanguages.setSelected(true);
            chckbxLanguages.setHorizontalAlignment(SwingConstants.LEFT);
            chckbxLanguages.setToolTipText("Enable getting contest languages");
        }
        return chckbxLanguages;
    }

    private JCheckBox getChckbxStarttime() {
        if (chckbxStarttime == null) {
            chckbxStarttime = new JCheckBox("/starttime");
            chckbxStarttime.setSelected(true);
            chckbxStarttime.setHorizontalAlignment(SwingConstants.LEFT);
            chckbxStarttime.setToolTipText("Enable getting/setting contest start time");
        }
        return chckbxStarttime;
    }

    private JCheckBox getChckbxTeams() {
        if (chckbxTeams == null) {
            chckbxTeams = new JCheckBox("/teams");
            chckbxTeams.setSelected(true);
            chckbxTeams.setToolTipText("Enable getting contest teams");
            chckbxTeams.setHorizontalAlignment(SwingConstants.LEFT);
        }
        return chckbxTeams;
    }
    
    protected WebServer getWebServer() {
        if (webServer == null){
            webServer = new WebServer();
        }
        return webServer;
    }

}
