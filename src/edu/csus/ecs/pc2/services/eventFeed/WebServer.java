package edu.csus.ecs.pc2.services.eventFeed;

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
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

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
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.web.ClarificationService;
import edu.csus.ecs.pc2.services.web.ContestService;
import edu.csus.ecs.pc2.services.web.EventFeedService;
import edu.csus.ecs.pc2.services.web.FetchRunService;
import edu.csus.ecs.pc2.services.web.GroupService;
import edu.csus.ecs.pc2.services.web.JudgementService;
import edu.csus.ecs.pc2.services.web.JudgementTypeService;
import edu.csus.ecs.pc2.services.web.LanguageService;
import edu.csus.ecs.pc2.services.web.OrganizationService;
import edu.csus.ecs.pc2.services.web.ProblemService;
import edu.csus.ecs.pc2.services.web.RunService;
import edu.csus.ecs.pc2.services.web.ScoreboardService;
import edu.csus.ecs.pc2.services.web.StarttimeService;
import edu.csus.ecs.pc2.services.web.SubmissionService;
import edu.csus.ecs.pc2.services.web.TeamService;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Web Server.
 * 
 * This server listens on the input port and when a connection is made it creates a service that for each contest event will do REST web services.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class WebServer implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -731087652687843222L;

    public static final int DEFAULT_WEB_SERVER_PORT_NUMBER = 50443;

    public static final String PC2_KEYSTORE_FILE = "cacerts.pc2";

    // keys for web service properties

    public static final String PORT_NUMBER_KEY = "port";

    public static final String STARTTIME_SERVICE_ENABLED_KEY = "enableStartTime";

    public static final String FETCH_RUN_SERVICE_ENABLED_KEY = "enableFetchRun";

    private Properties wsProperties = new Properties();

    private Server jettyServer = null;

    private String keystorePassword = "i don't care";

    private IInternalContest contest;

    private IInternalController controller;

    private Log log = null;

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
            Constructor<?> certKeyGenCons = certKeyGenClass.getConstructor(String.class, String.class);
            Constructor<?> x500NameCons = x500NameClass.getConstructor(String.class);
            Object keypair = certKeyGenCons.newInstance("RSA", "SHA256WithRSA");
            String dn = "CN=pc2 jetty, OU=PC^2, O=PC^2, L=Unknown, ST=Unknown, C=Unknown";
            Object subject = x500NameCons.newInstance(dn);
            Method certAndKeyGenGenerate = certKeyGenClass.getMethod("generate", int.class);
            certAndKeyGenGenerate.invoke(keypair, 2048);
            Method certAndKeyGenPrivateKey = certKeyGenClass.getMethod("getPrivateKey");
            PrivateKey rootPrivateKey = (PrivateKey) certAndKeyGenPrivateKey.invoke(keypair);
            Method getSelfCertificate = certKeyGenClass.getMethod("getSelfCertificate", x500NameClass, long.class);

            X509Certificate[] chain = new X509Certificate[1];
            // create with a length of 1 (non-leap) year
            long days = (long) 365 * 24 * 3600;
            // Generate self signed certificate
            chain[0] = (X509Certificate) getSelfCertificate.invoke(keypair, subject, days);

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
     * Start Web Server.
     * <P>
     * Starts a Jetty webserver running on the port specified in the GUI textfield, and registers a set of default REST (Jersey/JAX-RS) services with Jetty. TODO: need to provide support for
     * dynamically reconfiguring the registered services.
     */
    public void startWebServer(IInternalContest aContest, IInternalController aController, Properties properties) {

        setContestAndController(aContest,aController);
        wsProperties = properties;

        try {
            int port = getIntegerProperty(PORT_NUMBER_KEY, DEFAULT_WEB_SERVER_PORT_NUMBER);

            File keystoreFile = new File(PC2_KEYSTORE_FILE);

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

            context.setSecurityHandler(basicAuth());

            jettyServer.setHandler(context);

            // ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
            // jerseyServlet.setInitOrder(0);
            //
            // // Tells the Jersey Servlet which REST service/classes to load.
            // // Note that class names must be semi-colon separated in the second String parameter.
            // jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", getServiceClassesList());

            jettyServer.start();
            showMessage("Started web server on port "+ port);

        } catch (NumberFormatException e) {
            showMessage("Unable to start web services: invalid port number: " + e.getMessage(), e);
        } catch (IOException e1) {
            showMessage("Unable to start web services: " + e1.getMessage(), e1);
        } catch (Exception e2) {
            showMessage("Unable to start web services: " + e2.getMessage(), e2);
        }
    }
    
    private void showMessage(final String message, Exception ex) {
        getLog().log(Log.INFO, message, ex);
        System.out.println(new Date() + " " +message);
        ex.printStackTrace();
    }

    private void showMessage(String message) {
        System.out.println(new Date() + " " +message);
        getLog().info(message);
    }
    
    private Logger getLog() {
        return log;
    }


    private SecurityHandler basicAuth() {

        HashLoginService l = new HashLoginService();
        File f = new File("realm.properties");
        if (f.exists() && f.isFile() && f.canRead()) {
            showMessage("Loading " + f.getAbsolutePath());
            // per 9.2 docs this is seconds not ms and 60*1000 didn't seem to work
            l.setRefreshInterval(60); // seconds or ms?
            l.setConfig(f.getAbsolutePath());
            try {
                l.start();
            } catch (Exception e) {
                showMessage(e.getMessage(), e);
            }
        } else if (!f.exists()) {
            showMessage("WARNING: " + f.getAbsolutePath() + " does not exist");
        } else if (!f.isFile()) {
            showMessage("WARNING: " + f.getAbsolutePath() + " is not a file");
        } else {
            showMessage("WARNING: Cannot read " + f.getAbsolutePath());
        }

        Constraint constraintPublic = new Constraint();
        constraintPublic.setName(Constraint.__BASIC_AUTH);
        constraintPublic.setRoles(new String[] { "public", "balloon", "analyst", "blue", "admin" });
        constraintPublic.setAuthenticate(true);

        ConstraintMapping cmRoot = new ConstraintMapping();
        cmRoot.setConstraint(constraintPublic);
        cmRoot.setPathSpec("/");

        Constraint constraintAdmin = new Constraint();
        constraintAdmin.setName(Constraint.__BASIC_AUTH);
        constraintAdmin.setRoles(new String[] { "admin" });
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

        if (getBooleanProperty(STARTTIME_SERVICE_ENABLED_KEY, false)) {
            resConfig.register(new StarttimeService(getContest(), getController()));
            showMessage("Starting /starttime web service");
        }

        if (getBooleanProperty(FETCH_RUN_SERVICE_ENABLED_KEY, false)) {
            resConfig.register(new FetchRunService(getContest(), getController()));
            showMessage("Starting /fetchRun web service");
        }
        // contest API always enabled if the web server is started
        resConfig.register(new ContestService(getContest(),getController()));
        showMessage("Starting /contest web service");
        resConfig.register(new ScoreboardService(getContest(), getController()));
        showMessage("Starting /contest/scoreboard web service");
        resConfig.register(new LanguageService(getContest(), getController()));
        showMessage("Starting /contest/languages web service");
        resConfig.register(new TeamService(getContest(), getController()));
        showMessage("Starting /contest/teams web service");
        resConfig.register(new GroupService(getContest(),getController()));
        showMessage("Starting /contest/groups web service");
        resConfig.register(new OrganizationService(getContest(),getController()));
        showMessage("Starting /contest/organizations web service");
        resConfig.register(new JudgementTypeService(getContest(),getController()));
        showMessage("Starting /contest/judgement-types web service");
        resConfig.register(new ClarificationService(getContest(),getController()));
        showMessage("Starting /contest/clarifications web service");
        resConfig.register(new SubmissionService(getContest(),getController()));
        showMessage("Starting /contest/submissions web service");
        resConfig.register(new ProblemService(getContest(), getController()));
        showMessage("Starting /contest/problems web service");
        resConfig.register(new JudgementService(getContest(), getController()));
        showMessage("Starting /contest/judgements web service");
        resConfig.register(new RunService(getContest(), getController()));
        showMessage("Starting /contest/runs web service");
        resConfig.register(new EventFeedService(getContest(), getController()));
        showMessage("Starting /contest/event-feed web service");

        return resConfig;
    }

    protected boolean getBooleanProperty(String key, boolean b) {

        String value = wsProperties.getProperty(key);

        if (value == null) {
            return b;
        } else {
            return "true".equalsIgnoreCase(value.trim()) || //
                    "yes".equalsIgnoreCase(value.trim()) || //
                    "on".equalsIgnoreCase(value.trim()) || //
                    "enabled".equalsIgnoreCase(value.trim());
        }

    }

    protected int getIntegerProperty(String key, int defaultValue) {

        String value = wsProperties.getProperty(key);

        if (value == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                return defaultValue;
            }
        }
    }

    public IInternalContest getContest() {
        return contest;
    }

    public IInternalController getController() {
        return controller;
    }

    public static Properties createSampleProperties() {

        Properties prop = new Properties();

        prop.put(PORT_NUMBER_KEY, DEFAULT_WEB_SERVER_PORT_NUMBER + "");

        prop.put(STARTTIME_SERVICE_ENABLED_KEY, "yes");

        prop.put(FETCH_RUN_SERVICE_ENABLED_KEY, "yes");

        return prop;
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {

        this.contest = inContest;
        this.controller = inController;
        this.log = controller.getLog();
    }

    @Override
    public String getPluginTitle() {
        return "Web Server (non-GUI)";
    }

    public void stop() {
        try {
            jettyServer.stop();
        } catch (Exception e1) {
            showMessage("Unable to stop Jetty webserver: " + e1.getMessage());
            e1.printStackTrace();
            getLog().log(Log.INFO, e1.getMessage(), e1);
        }
        jettyServer.destroy();
    }

    public boolean isServerRunning() {
        
        boolean serverRunning;
        
        if (jettyServer == null) {
            serverRunning = false;
        } else {
            serverRunning = jettyServer.isRunning();
        }
        
        return serverRunning;
    }

}
