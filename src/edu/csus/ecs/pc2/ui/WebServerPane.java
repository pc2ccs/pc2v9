package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.servlet.ServletContainer;

import sun.security.x509.CertificateExtensions;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.services.web.LanguageService;
import edu.csus.ecs.pc2.services.web.ProblemService;
import edu.csus.ecs.pc2.services.web.ScoreboardService;
import edu.csus.ecs.pc2.services.web.StarttimeService;
import edu.csus.ecs.pc2.services.web.TeamService;

import javax.swing.JCheckBox;

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

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_WEB_SERVER_PORT_NUMBER = 50443;

    private JPanel buttonPanel = null;

    private JButton startButton = null;

    private JButton stopButton = null;

    private JPanel centerPanel = null;

    private JLabel portLabel = null;

    private JTextField portTextField = null;

    private Server jettyServer = null;

    private JLabel webServerStatusLabel = null;

    private JCheckBox chckbxScoreboard;

    private JLabel lblEnabledWebServices;

    private JCheckBox chckbxProblems;

    private JCheckBox chckbxLanguages;

    private JCheckBox chckbxStarttime;

    private JCheckBox chckbxTeams;

    private String keystorePassword = "i don't care";

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
     * Starts a Jetty webserver running on the port specified in the GUI textfield, and registers a set of default REST (Jersey/JAX-RS) services with Jetty. 
     * TODO: need to provide support for dynamically reconfiguring the registered services.
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

            File keystoreFile = new File("cacerts.pc2");
            if (!keystoreFile.exists()) {
                createKeyStoreAndKey(keystoreFile);
            }

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            // adds Jersey ServletContainer with a ResourceConfig customized with enabled REST service classes
            context.addServlet(new ServletHolder(new ServletContainer(getResourceConfig())), "/*");

            jettyServer = new Server();

            HttpConfiguration httpConfig = new HttpConfiguration();
            httpConfig.setSecureScheme("https");
            httpConfig.setSecurePort(port);
            httpConfig.setOutputBufferSize(32768);

            // set to trustAll
            SslContextFactory sslContextFactory = new SslContextFactory(true);
            sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
            sslContextFactory.setKeyStorePassword(keystorePassword);
            sslContextFactory.setKeyManagerPassword(keystorePassword);
            // suggestions from http://www.eclipse.org/jetty/documentation/current/configuring-ssl.html
            sslContextFactory.setIncludeCipherSuites("TLS_DHE_RSA.*", "TLS_ECDHE.*");
            sslContextFactory.setExcludeProtocols("SSL", "SSLv2", "SSLv2Hello", "SSLv3");
            sslContextFactory.setRenegotiationAllowed(false);

            HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
            httpsConfig.addCustomizer(new SecureRequestCustomizer());
            
            ServerConnector https = new ServerConnector(jettyServer, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(httpsConfig));
            https.setPort(port);
            // do not timeout
            https.setIdleTimeout(0);
            
            // only enable https
            jettyServer.setConnectors(new Connector[] { https });
            
            context.setSecurityHandler(basicAuth("scott", "tiger", "my realm", new String[]{"admin"}) );

            jettyServer.setHandler(context);

            // ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
            // jerseyServlet.setInitOrder(0);
            //
            // // Tells the Jersey Servlet which REST service/classes to load.
            // // Note that class names must be semi-colon separated in the second String parameter.
            // jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", getServiceClassesList());

            jettyServer.start();

        } catch (NumberFormatException e) {
            showMessage("Unable to start web services: invalid port number: " + e.getMessage());
            e.printStackTrace();
            getLog().log(Log.INFO, e.getMessage(), e);
        } catch (IOException e1) {
            showMessage("Unable to start web services: " + e1.getMessage());
            e1.printStackTrace();
            getLog().log(Log.INFO, e1.getMessage(), e1);
        } catch (Exception e2) {
            showMessage("Unable to start web services: " + e2.getMessage());
            e2.printStackTrace();
            getLog().log(Log.INFO, e2.getMessage(), e2);

        }

        updateGUI();
    }

    private void createKeyStoreAndKey(File keystoreFile) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, FileNotFoundException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] password = keystorePassword.toCharArray();
        ks.load(null, password);
        try {
            // taken from https://svn.forgerock.org/opendj/trunk/opends/src/server/org/opends/server/util/Platform.java
            String certAndKeyGen;
            // and this is why you are not suppose to use sun classes
            if (System.getProperty("java.version").matches("^1\\.[67]\\..*")) {
                certAndKeyGen = "sun.security.x509" + ".CertAndKeyGen";
            } else {
                // Java 8 moved the CertAndKeyGen class to sun.security.tools.keytool
                certAndKeyGen = "sun.security.tools.keytool" + ".CertAndKeyGen";
            }
            String x500Name = "sun.security.x509" + ".X500Name";
            Class<?> certKeyGenClass = Class.forName(certAndKeyGen);
            Class<?> x500NameClass = Class.forName(x500Name);
            Constructor<?> certKeyGenCons = certKeyGenClass.getConstructor(String.class,
                    String.class);
            Constructor<?> x500NameCons = x500NameClass.getConstructor(String.class);
            Object keypair = certKeyGenCons.newInstance("RSA",  "SHA256WithRSA");
            String dn = "CN=pc2 jetty, OU=PC^2, O=PC^2, L=Unknown, ST=Unknown, C=Unknown";
            Object subject = x500NameCons.newInstance(dn);
            Method certAndKeyGenGenerate = certKeyGenClass.getMethod(
                    "generate", int.class);
            certAndKeyGenGenerate.invoke(keypair, 2048);
            Method certAndKeyGenPrivateKey = certKeyGenClass.getMethod(
                    "getPrivateKey");
            PrivateKey rootPrivateKey = (PrivateKey)certAndKeyGenPrivateKey.invoke(keypair);
            Method getSelfCertificate = certKeyGenClass.getMethod(
                    "getSelfCertificate", x500NameClass, long.class);

            X509Certificate[] chain = new X509Certificate[1];
            // create with a length of 1 (non-leap) year
            long days = (long) 365 * 24 * 3600;
            // Generate self signed certificate
            chain[0] = (X509Certificate) getSelfCertificate.invoke(keypair,
                    subject, days);

            Principal issuer = chain[0].getSubjectDN();
            String issuerSigAlg = chain[0].getSigAlgName();
            byte[] inCertBytes = chain[0].getTBSCertificate();
            X509CertInfo info = new X509CertInfo(inCertBytes);
            info.set(X509CertInfo.ISSUER, (X500Name) issuer);

            CertificateExtensions exts = new CertificateExtensions();
            exts.set(SubjectKeyIdentifierExtension.NAME, new SubjectKeyIdentifierExtension(new KeyIdentifier(chain[0].getPublicKey()).getIdentifier()));
            info.set(X509CertInfo.EXTENSIONS, exts);
            
            X509CertImpl outCert = new X509CertImpl(info);
            outCert.sign(rootPrivateKey, issuerSigAlg);

            ks.setCertificateEntry("jetty", outCert);
            ks.setKeyEntry("jetty", rootPrivateKey, keystorePassword.toCharArray(), chain);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Store away the keystore
        FileOutputStream fos = new FileOutputStream(keystoreFile);
        ks.store(fos, password);
        fos.close();
    }

    /**
     * This method constructs a Jersey {@link ResourceConfig} containing a Resource (Service class) for each REST service marked as "enabled" by the user on the WebServerPane GUI. Each Resource is
     * constructed with the current contest and controller so that it has access to the contest data.
     * 
     * @return a ResourceConfig containing the enabled REST service resources
     */
    private ResourceConfig getResourceConfig() {

        // create and (empty) ResourceConfig
        ResourceConfig resConfig = new ResourceConfig();
        resConfig.register(RolesAllowedDynamicFeature.class);
        
        // add each of the enabled services to the config:

        if (getChckbxScoreboard().isSelected()) {
            resConfig.register(new ScoreboardService(getContest(), getController()));
        }

        if (getChckbxProblems().isSelected()) {
            resConfig.register(new ProblemService(getContest(), getController()));
        }

        if (getChckbxLanguages().isSelected()) {
            resConfig.register(new LanguageService(getContest(), getController()));
        }

        if (getChckbxStarttime().isSelected()) {
            resConfig.register(new StarttimeService(getContest(), getController()));
        }

        if (getChckbxTeams().isSelected()) {
            resConfig.register(new TeamService(getContest(), getController()));
        }

        return resConfig;
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
     * Stops the Jetty web server if it is running. Also destroys the web server. 
     * TODO: shouldn't really destroy the webserver; just stop it and cache the reference so that it can be quickly
     * restarted. (However, need to consider what happens if the user selects a different set of services to be enabled...)
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
            GridBagConstraints gbc_chckbxStarttime = new GridBagConstraints();
            gbc_chckbxStarttime.anchor = GridBagConstraints.WEST;
            gbc_chckbxStarttime.insets = new Insets(0, 0, 5, 0);
            gbc_chckbxStarttime.gridx = 2;
            gbc_chckbxStarttime.gridy = 3;
            centerPanel.add(getChckbxStarttime(), gbc_chckbxStarttime);
            GridBagConstraints gbc_chckbxLanguages = new GridBagConstraints();
            gbc_chckbxLanguages.anchor = GridBagConstraints.WEST;
            gbc_chckbxLanguages.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxLanguages.gridx = 1;
            gbc_chckbxLanguages.gridy = 4;
            centerPanel.add(getChckbxLanguages(), gbc_chckbxLanguages);

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

        boolean serverRunning;
        if (jettyServer == null) {
            serverRunning = false;
        } else {
            serverRunning = jettyServer.isRunning();
        }
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
        getChckbxLanguages().setEnabled(false);
        getChckbxScoreboard().setEnabled(!serverRunning);
        getChckbxStarttime().setEnabled(false);
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
            chckbxLanguages.setHorizontalAlignment(SwingConstants.LEFT);
            chckbxLanguages.setToolTipText("Enable getting contest languages");
        }
        return chckbxLanguages;
    }

    private JCheckBox getChckbxStarttime() {
        if (chckbxStarttime == null) {
            chckbxStarttime = new JCheckBox("/starttime");
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
    
    private static SecurityHandler basicAuth(String username, String password, String realm, String[] roles) {

        HashLoginService l = new HashLoginService();
        l.putUser(username, Credential.getCredential(password), roles);
        l.setName(realm);
        File f = new File("realm.properties");
        if (f.exists() && f.isFile() && f.canRead()) {
            System.err.println("Loading "+f.getAbsolutePath());
            // per 9.2 docs this is seconds not ms and 60*1000 didn't seem to work
            l.setRefreshInterval(60); // seconds or ms?
            l.setConfig(f.getAbsolutePath());
            try {
                l.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (!f.exists()) {
            System.err.println("WARNING: "+f.getAbsolutePath()+" does not exist");
        } else if (!f.isFile()) {
            System.err.println("WARNING: "+f.getAbsolutePath()+" is not a file");
        } else {
            System.err.println("WARNING: Cannot read "+f.getAbsolutePath());
        }
        
        Constraint constraintPublic = new Constraint();
        constraintPublic.setName(Constraint.__BASIC_AUTH);
        constraintPublic.setRoles(new String[]{"public","balloon","blue","admin"});
        constraintPublic.setAuthenticate(true);
         
        ConstraintMapping cmRoot = new ConstraintMapping();
        cmRoot.setConstraint(constraintPublic);
        cmRoot.setPathSpec("/");

        Constraint constraintAdmin = new Constraint();
        constraintAdmin.setName(Constraint.__BASIC_AUTH);
        constraintAdmin.setRoles(new String[]{"admin"});
        constraintAdmin.setAuthenticate(true);

        ConstraintMapping cmStartTime = new ConstraintMapping();
        cmStartTime.setConstraint(constraintAdmin);
        cmStartTime.setPathSpec("/starttime");
        
        ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
        csh.setAuthenticator(new BasicAuthenticator());
        csh.setRealmName("myrealm");
        csh.addConstraintMapping(cmRoot);
        csh.addConstraintMapping(cmStartTime);
        csh.setLoginService(l);
        
        return csh;
        
    }
}
