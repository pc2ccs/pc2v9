package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;
import edu.csus.ecs.pc2.services.web.EventFeedStreamer;

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
    
    private static final String NL = System.getProperty("line.separator");

    private JPanel buttonPanel = null;

    private JButton startButton = null;

    private JButton stopButton = null;

    private JPanel centerPanel = null;

    private JLabel portLabel = null;

    private JTextField portTextField = null;

    private JLabel webServerStatusLabel = null;

    private JLabel lblEnabledWebServices;

    private JCheckBox chckbxStarttime;

    private WebServer webServer = null;

    private JCheckBox chckbxFetchRuns;
    private JButton viewJSONButton;

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
        properties.put(WebServer.STARTTIME_SERVICE_ENABLED_KEY, Boolean.toString(chckbxStarttime.isSelected()));
        properties.put(WebServer.FETCH_RUN_SERVICE_ENABLED_KEY, Boolean.toString(chckbxFetchRuns.isSelected()));

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
            GridBagConstraints gbc_viewJSONButton = new GridBagConstraints();
            gbc_viewJSONButton.anchor = GridBagConstraints.WEST;
            gbc_viewJSONButton.insets = new Insets(0, 0, 5, 0);
            gbc_viewJSONButton.gridx = 2;
            gbc_viewJSONButton.gridy = 2;
            centerPanel.add(getViewJSONButton(), gbc_viewJSONButton);
            GridBagConstraints gbc_chckbxStarttime = new GridBagConstraints();
            gbc_chckbxStarttime.anchor = GridBagConstraints.WEST;
            gbc_chckbxStarttime.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxStarttime.gridx = 1;
            gbc_chckbxStarttime.gridy = 4;
            centerPanel.add(getChckbxStarttime(), gbc_chckbxStarttime);
            GridBagConstraints gbc_chckbxFetchRuns = new GridBagConstraints();
            gbc_chckbxFetchRuns.anchor = GridBagConstraints.WEST;
            gbc_chckbxFetchRuns.insets = new Insets(0, 0, 5, 0);
            gbc_chckbxFetchRuns.gridx = 2;
            gbc_chckbxFetchRuns.gridy = 4;
            centerPanel.add(getChckbxFetchRuns(), gbc_chckbxFetchRuns);

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
        getChckbxStarttime().setEnabled(!serverRunning);
        getChckbxFetchRuns().setEnabled(!serverRunning);
    }

    private JLabel getLblEnabledWebServices() {
        if (lblEnabledWebServices == null) {
            lblEnabledWebServices = new JLabel("Enable Web Services:");
        }
        return lblEnabledWebServices;
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

    protected WebServer getWebServer() {
        if (webServer == null) {
            webServer = new WebServer();
        }
        return webServer;
    }

    private JCheckBox getChckbxFetchRuns() {
        if (chckbxFetchRuns == null) {
            chckbxFetchRuns = new JCheckBox("/submission_files");
            chckbxFetchRuns.setSelected(true);
        }
        return chckbxFetchRuns;
    }
    private JButton getViewJSONButton() {
        if (viewJSONButton == null) {
        	viewJSONButton = new JButton("View");
        	viewJSONButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        viewJSONEventFeed();
        	    }
        	});
        }
        return viewJSONButton;
    }

    protected void viewJSONEventFeed() {
        
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss z");
            String dateString = formatter.format(new Date());
            
            String buildNumber = new VersionInfo().getBuildNumber();
            
            MultipleFileViewer multipleFileViewer = new MultipleFileViewer(getController().getLog());
            String title = "Event Feed JSON (at "+dateString+", build "+buildNumber+")";
            String json = EventFeedStreamer.createEventFeedJSON(getContest(), getController());
            String [] lines = json.split(NL);
            multipleFileViewer.addTextintoPane(title, lines);
            multipleFileViewer.setTitle("PC^2 Report (Build " + new VersionInfo().getBuildNumber() + ")");
            FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
            multipleFileViewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            getLog().log(Level.WARNING, "Unable to view EF JSON", e);
        }
        
    }
}
