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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.services.web.LanguageService;
import edu.csus.ecs.pc2.services.web.ProblemService;
import edu.csus.ecs.pc2.services.web.ScoreboardService;
import edu.csus.ecs.pc2.services.web.StarttimeService;

import javax.swing.JCheckBox;


/**
 * This class provides a GUI for configuring the embedded Jetty webserver.
 * It allows specifying the port on which Jetty will listen and the REST service
 * endpoints to which Jetty will respond.  (Note that REST endpoints are handled
 * using Jersey, the JAX-RS implementation.)
 * 
 * By default the Jetty webserver is configured to listen on port 50080.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class WebServerPane extends JPanePlugin {


    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_WEB_SERVER_PORT_NUMBER = 50080;

    private JPanel buttonPanel = null;

    private JButton startButton = null;

    private JButton stopButton = null;

    private JPanel centerPanel = null;

    private JLabel portLabel = null;

    private JTextField portTextField = null;
 
    private Server jettyServer = null ;

    private JLabel webServerStatusLabel = null;
    private JCheckBox chckbxscoreboard;
    private JLabel lblEnabledWebServices;
    private JCheckBox chckbxproblems;
    private JCheckBox chckbxlanguages;
    private JCheckBox chckbxstarttime;

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
     * Starts a Jetty webserver running on the port specified in the GUI textfield, and registers
     * a set of default REST (Jersey/JAX-RS) services with Jetty.
     * TODO:  need to provide support for dynamically reconfiguring the registered services.
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

        try {
            int port = Integer.parseInt(portTextField.getText());

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            jettyServer = new Server(port);
            jettyServer.setHandler(context);

            ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
            jerseyServlet.setInitOrder(0);

            // Tells the Jersey Servlet which REST service/classes to load.
            //  Note that class names must be semi-colon separated in the second String parameter.
            jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", getServiceClassesList());

            jettyServer.start();
            
        } 
        catch (NumberFormatException e) {
            showMessage("Unable to start web services: invalid port number: "+ e.getMessage());
            e.printStackTrace(); 
            getLog().log(Log.INFO, e.getMessage(), e);
        }
        catch (IOException e1) {
            showMessage("Unable to start web services: "+ e1.getMessage());
            e1.printStackTrace(); 
            getLog().log(Log.INFO, e1.getMessage(), e1);
        }
        catch (Exception e2) {
            showMessage("Unable to start web services: "+ e2.getMessage());
            e2.printStackTrace(); 
            getLog().log(Log.INFO, e2.getMessage(), e2);
  
        }

        updateGUI();
    }
    
    /**
     * Returns a String of semicolon-separated class names for the services which should be
     * registered with Jetty.
     * @return a String of service class names
     */
    private String getServiceClassesList() {
        
        //the list of service class names
        String classList = "";
        
        //check the next service
        if (getChckbxscoreboard().isSelected()) {
            //need to add it; add a semicolon to the end of the list if the list is not empty
            if (classList.length()>0) {
                classList += ";";
            }
            //add the service class name to the list
            classList += ScoreboardService.class.getCanonicalName();
        }
        
        //check the next service
        if (getChckbxproblems().isSelected()) {
            //need to add it; add a semicolon to the end of the list if the list is not empty
            if (classList.length()>0) {
                classList += ";";
            }
            //add the service class name to the list
            classList += ProblemService.class.getCanonicalName();
        }
        
        //check the next service
        if (getChckbxlanguages().isSelected()) {
            //need to add it; add a semicolon to the end of the list if the list is not empty
            if (classList.length()>0) {
                classList += ";";
            }
            //add the service class name to the list
            classList += LanguageService.class.getCanonicalName();
        }
        
        //check the next service
        if (getChckbxstarttime().isSelected()) {
            //need to add it; add a semicolon to the end of the list if the list is not empty
            if (classList.length()>0) {
                classList += ";";
            }
            //add the service class name to the list
            classList += StarttimeService.class.getCanonicalName();
        }
        
        return classList;
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
     * Stops the Jetty web server if it is running.
     * Also destroys the web server.
     * TODO:  shouldn't really destroy the webserver; just stop it and cache the reference so
     * that it can be quickly restarted.  (However, need to consider what happens if the user
     * selects a different set of services to be enabled...)
     */
    protected void stopWebServer() {

        if (jettyServer != null) {
            try {
                jettyServer.stop();
            } catch (Exception e1) {
                showMessage("Unable to stop Jetty webserver: " + e1.getMessage());
                e1.printStackTrace();
                getLog().log(Log.INFO, e1.getMessage(), e1);
            }
            jettyServer.destroy();
        }
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
            gblCenterPanel.columnWidths = new int[]{198, 57, 167, 0};
            gblCenterPanel.rowHeights = new int[]{36, 23, 32, 23, 0, 0, 0};
            gblCenterPanel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
            gblCenterPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
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
            GridBagConstraints gbc_chckbxscoreboard = new GridBagConstraints();
            gbc_chckbxscoreboard.anchor = GridBagConstraints.WEST;
            gbc_chckbxscoreboard.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxscoreboard.gridx = 1;
            gbc_chckbxscoreboard.gridy = 2;
            centerPanel.add(getChckbxscoreboard(), gbc_chckbxscoreboard);
            GridBagConstraints gbc_chckbxstarttime = new GridBagConstraints();
            gbc_chckbxstarttime.anchor = GridBagConstraints.WEST;
            gbc_chckbxstarttime.insets = new Insets(0, 0, 5, 0);
            gbc_chckbxstarttime.gridx = 2;
            gbc_chckbxstarttime.gridy = 2;
            centerPanel.add(getChckbxstarttime(), gbc_chckbxstarttime);
            GridBagConstraints gbc_chckbxproblems = new GridBagConstraints();
            gbc_chckbxproblems.anchor = GridBagConstraints.WEST;
            gbc_chckbxproblems.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxproblems.gridx = 1;
            gbc_chckbxproblems.gridy = 3;
            centerPanel.add(getChckbxproblems(), gbc_chckbxproblems);
            GridBagConstraints gbc_chckbxlanguages = new GridBagConstraints();
            gbc_chckbxlanguages.anchor = GridBagConstraints.WEST;
            gbc_chckbxlanguages.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxlanguages.gridx = 1;
            gbc_chckbxlanguages.gridy = 4;
            centerPanel.add(getChckbxlanguages(), gbc_chckbxlanguages);

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
     * Updates the state of the web server status label and Start/Stop buttons to 
     * correspond to the state of the Jetty Server.
     */
    private void updateGUI () {
        
        boolean serverRunning;
        if (jettyServer==null) {
            serverRunning = false;
        } else {
            serverRunning = jettyServer.isRunning();
        }
        getStartButton().setEnabled(! serverRunning);
        getStopButton().setEnabled(serverRunning);

        if (serverRunning){
            webServerStatusLabel.setText("Web Server is running...");
        } else {
            webServerStatusLabel.setText("Web Server STOPPED");
        }
    }

    private JCheckBox getChckbxscoreboard() {
        if (chckbxscoreboard == null) {
        	chckbxscoreboard = new JCheckBox("/Scoreboard");
        	chckbxscoreboard.setHorizontalAlignment(SwingConstants.LEFT);
        	chckbxscoreboard.setToolTipText("Enable getting contest scoreboard");
        }
        return chckbxscoreboard;
    }
    private JLabel getLblEnabledWebServices() {
        if (lblEnabledWebServices == null) {
        	lblEnabledWebServices = new JLabel("Enabled Web Services:");
        }
        return lblEnabledWebServices;
    }
    private JCheckBox getChckbxproblems() {
        if (chckbxproblems == null) {
        	chckbxproblems = new JCheckBox("/Problems");
        	chckbxproblems.setToolTipText("Enable getting contest problems");
        	chckbxproblems.setHorizontalAlignment(SwingConstants.LEFT);
        }
        return chckbxproblems;
    }
    private JCheckBox getChckbxlanguages() {
        if (chckbxlanguages == null) {
        	chckbxlanguages = new JCheckBox("/Languages");
        	chckbxlanguages.setHorizontalAlignment(SwingConstants.LEFT);
        	chckbxlanguages.setToolTipText("Enable getting contest languages");
        }
        return chckbxlanguages;
    }
    private JCheckBox getChckbxstarttime() {
        if (chckbxstarttime == null) {
        	chckbxstarttime = new JCheckBox("/Starttime");
        }
        return chckbxstarttime;
    }
}
