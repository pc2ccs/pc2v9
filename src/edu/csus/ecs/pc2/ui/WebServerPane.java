// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.ws.rs.core.SecurityContext;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.CommaSeparatedValueParser;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;
import edu.csus.ecs.pc2.services.eventFeed.WebServerPropertyUtils;
import edu.csus.ecs.pc2.services.web.IEventFeedStreamer;

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

    private static final String CLICS_VERSIONS_KEY = "clics.apiVersionsSupported";

    private static final String [] DEF_CLICS_API_VERSIONS = { "2023-06", "2020-03" };

    private static final String CREATE_EF_METHOD = "createEventFeedJSON";

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
    private JCheckBox chckbxClicsContestApi;
    private JComboBox<String> combobxClicsAPIVersion;

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
                @Override
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

        properties.put(WebServerPropertyUtils.PORT_NUMBER_KEY, portTextField.getText());
        properties.put(WebServerPropertyUtils.CLICS_CONTEST_API_SERVICES_ENABLED_KEY, Boolean.toString(getChckbxClicsContestApi().isSelected()));
        properties.put(WebServerPropertyUtils.STARTTIME_SERVICE_ENABLED_KEY, Boolean.toString(getChckbxStarttime().isSelected()));
        properties.put(WebServerPropertyUtils.FETCH_RUN_SERVICE_ENABLED_KEY, Boolean.toString(getChckbxFetchRuns().isSelected()));
        String apiVer = getComboBxClicsAPIVersion().getSelectedItem().toString();
        //convert human readable api version, eg 2023-06 to 202306 since this is how we look up the package and class.
        apiVer = StringUtilities.removeAllOccurrences(apiVer, '-');
        properties.put(WebServerPropertyUtils.CLICS_API_VERSION,  apiVer);

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
                @Override
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
            gblCenterPanel.rowHeights = new int[] { 36, 23, 32, 23, 0, 0, 0, 0 };
            gblCenterPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
            gblCenterPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
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
            GridBagConstraints gbc_chckbxClicsContestApi = new GridBagConstraints();
            gbc_chckbxClicsContestApi.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxClicsContestApi.gridx = 1;
            gbc_chckbxClicsContestApi.gridy = 2;
            centerPanel.add(getChckbxClicsContestApi(), gbc_chckbxClicsContestApi);

            GridBagConstraints gbc_combobxClicsContestApiVersion = new GridBagConstraints();
            gbc_combobxClicsContestApiVersion.fill = GridBagConstraints.HORIZONTAL;
            gbc_combobxClicsContestApiVersion.insets = new Insets(0, 0, 5, 5);
            gbc_combobxClicsContestApiVersion.gridx = 2;
            gbc_combobxClicsContestApiVersion.gridy = 2;
            centerPanel.add(getComboBxClicsAPIVersion(), gbc_combobxClicsContestApiVersion);

            GridBagConstraints gbc_chckbxStarttime = new GridBagConstraints();
            gbc_chckbxStarttime.anchor = GridBagConstraints.WEST;
            gbc_chckbxStarttime.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxStarttime.gridx = 1;
            gbc_chckbxStarttime.gridy = 3;
            centerPanel.add(getChckbxStarttime(), gbc_chckbxStarttime);
            GridBagConstraints gbc_chckbxFetchRuns = new GridBagConstraints();
            gbc_chckbxFetchRuns.anchor = GridBagConstraints.WEST;
            gbc_chckbxFetchRuns.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxFetchRuns.gridx = 1;
            gbc_chckbxFetchRuns.gridy = 4;
            centerPanel.add(getChckbxFetchRuns(), gbc_chckbxFetchRuns);
            GridBagConstraints gbc_viewJSONButton = new GridBagConstraints();
            gbc_viewJSONButton.gridx = 2;
            gbc_viewJSONButton.gridy = 6;
            centerPanel.add(getViewJSONButton(), gbc_viewJSONButton);

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
        getChckbxClicsContestApi().setEnabled(!serverRunning);
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
            viewJSONButton = new JButton("View Event Feed");
            viewJSONButton.setToolTipText("Show the data which will be output on the Event Feed API when the Webserver is started");
            viewJSONButton.addActionListener(new ActionListener() {
                @Override
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

            String apiChoice = getComboBxClicsAPIVersion().getSelectedItem().toString();
            String apiVer = StringUtilities.removeAllOccurrences(apiChoice, '-');
            String apiPackage = WebServer.DEFAULT_CLICS_API_PACKAGE_PREFIX + "." + "API" + apiVer;

            // eg, edu.csus.ecs.pc2.clics.API202306.EventFeedSstreamer
            String apiClass = apiPackage + ".EventFeedStreamer";

//            IEventFeedStreamer apiStreamer = getAPIClass(apiClass);
            Method createEF = getAPIcreateEventFeedJSON(apiClass);

            if(createEF != null) {
                MultipleFileViewer multipleFileViewer = new MultipleFileViewer(getController().getLog());
                String title = "CLICS " + apiChoice + " Event Feed JSON (at " + dateString + ", build " + buildNumber + ")";
                Object args[] = { getContest(), getController(), null, null };
                String json = (String)createEF.invoke(null, args);
                String[] lines = json.split(NL);
                multipleFileViewer.addTextintoPane(title, lines);
                multipleFileViewer.setTitle("PC^2 Report (Build " + new VersionInfo().getBuildNumber() + ")");
                FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
                multipleFileViewer.setVisible(true);
            } else {
                getLog().log(Level.WARNING, "Unable to get API " + apiClass + " method " + CREATE_EF_METHOD);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            getLog().log(Level.WARNING, "Unable to view EF JSON", e);
        }

    }

    private JCheckBox getChckbxClicsContestApi() {
        if (chckbxClicsContestApi == null) {
            chckbxClicsContestApi = new JCheckBox("CLICS Contest API");
            chckbxClicsContestApi.setSelected(true);
        }
        return chckbxClicsContestApi;
    }

    private JComboBox<String> getComboBxClicsAPIVersion() {
        if(combobxClicsAPIVersion == null) {
            String [] choices;
            // try to get supported API versions from INI file
            try {
                choices = CommaSeparatedValueParser.parseLine(IniFile.getValue(CLICS_VERSIONS_KEY));
            } catch (Exception e) {
                // use somewhat known defaults
                choices = DEF_CLICS_API_VERSIONS;
            }

            combobxClicsAPIVersion = new JComboBox<String>(choices);
            combobxClicsAPIVersion.setEditable(true);
        }
        return(combobxClicsAPIVersion);
    }

    private Method getAPIcreateEventFeedJSON(String className) {
        Method createEventFeedJSON = null;
        try {
            createEventFeedJSON = loadAPIMethod(className, CREATE_EF_METHOD);
        } catch (Exception e) {
            getController().getLog().log(Log.WARNING, "Unable to load CLICS API class = " + className + " method " + CREATE_EF_METHOD, e);
        }

        return createEventFeedJSON;
    }

    /**
     * Find and create an instance of ICLICSResourceConfig from className.
     * <P>
     * Code snippet.
     * <pre>
     * String className = "edu.csus.ecs.pc2.clics.API202306.EventFeedStreamer";
     * IEventFeedStreamer iRes = loadAPIClass(className);
     * </pre>
     *
     * @param className
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */

    private IEventFeedStreamer loadAPIClass(String className) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        Class<?> newClass = Class.forName(className);
        // Arguments for constructor
        Class [] cArgs = new Class[4];
        cArgs[0] = IInternalContest.class;
        cArgs[1] = IInternalController.class;
        cArgs[2] = HttpServletRequest.class;
        cArgs[3] = SecurityContext.class;

        Object object = newClass.getDeclaredConstructor(cArgs).newInstance(getContest(), getController(), null, null);
        if (object instanceof IEventFeedStreamer) {
            return (IEventFeedStreamer) object;
        }
        object = null;
        throw new SecurityException(className + " loaded, but not an instanceof IEventFeedStreamer");
    }

    private Method loadAPIMethod(String className, String method) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        Class<?> newClass = Class.forName(className);
        // Arguments for constructor
        Class [] cArgs = new Class[4];
        cArgs[0] = IInternalContest.class;
        cArgs[1] = IInternalController.class;
        cArgs[2] = HttpServletRequest.class;
        cArgs[3] = SecurityContext.class;

        return(newClass.getDeclaredMethod(method, cArgs));
    }
}
