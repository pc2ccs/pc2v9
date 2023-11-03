// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.eventFeed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
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
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.servlet.ServletContainer;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.web.ICLICSResourceConfig;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Web Server.
 *
 * This server listens on the input port and when a connection is made it creates a service that for each contest event will do REST web services.
 *
 * @author pc2@ecs.csus.edu
 */
public class WebServer implements UIPlugin {

    private static final long serialVersionUID = -731087652687843222L;

    public static final int DEFAULT_WEB_SERVER_PORT_NUMBER = 50443;

    public static final String DEFAULT_CLICS_API_VERSION = "202003";

    public static final String DEFAULT_CLICS_API_PACKAGE_PREFIX = "edu.csus.ecs.pc2.clics";

    public static final String PC2_KEYSTORE_FILE = "cacerts.pc2";

    // roles
    public static final String WEBAPI_ROLE_PUBLIC = "public";
    public static final String WEBAPI_ROLE_BALLOON = "balloon";
    public static final String WEBAPI_ROLE_ANALYST = "analyst";
    public static final String WEBAPI_ROLE_BLUE = "blue";
    public static final String WEBAPI_ROLE_ADMIN = "admin";
    public static final String WEBAPI_ROLE_TEAM = "team";
    public static final String WEBAPI_ROLE_JUDGE = "judge";

    private Server jettyServer = null;

    private String keystorePassword = "i don't care";

    private IInternalContest contest;

    private IInternalController controller;

    private Log log = null;

    private WebServerPropertyUtils wsProperties = null;

    private ICLICSResourceConfig apiResource = null;

    private static final Provider bcProvider = new BouncyCastleProvider();;

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    // based on https://stackoverflow.com/questions/43960761/how-to-store-and-reuse-keypair-in-java/43965528#43965528
    // which was based on https://stackoverflow.com/questions/29852290/self-signed-x509-certificate-with-bouncy-castle-in-java
    public static Certificate selfSign(KeyPair keyPair, String subjectDN) throws OperatorCreationException, CertificateException, IOException
    {
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);

        X500Name dnName = new X500Name(subjectDN);

        // Using the current timestamp as the certificate serial number
        BigInteger certSerialNumber = new BigInteger(Long.toString(now));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        // 1 Yr validity
        calendar.add(Calendar.YEAR, 1);

        Date endDate = calendar.getTime();

        // Use appropriate signature algorithm based on your keyPair algorithm.
        String signatureAlgorithm = "SHA256WithRSA";

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair
                .getPublic().getEncoded());

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(dnName,
                certSerialNumber, startDate, endDate, dnName, subjectPublicKeyInfo);

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).setProvider(
                bcProvider).build(keyPair.getPrivate());

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        Certificate selfSignedCert = new JcaX509CertificateConverter()
                .getCertificate(certificateHolder);

        return selfSignedCert;
    }

    private void createKeyStoreAndKey(File keystoreFile) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, NoSuchProviderException {

        try {
            KeyStore store = KeyStore.getInstance("PKCS12", "BC");
            store.load(null, null);
            char[] password = keystorePassword.toCharArray();

            KeyPair keyPair = generateKeyPair();
            Certificate selfSign = selfSign(keyPair, "CN=pc2ef,O=PC^2,OU=PC^2");

            Certificate[] chain = new Certificate[1];
            chain[0] = selfSign;
            store.setKeyEntry("jetty", keyPair.getPrivate(), password, chain);

            try (FileOutputStream fos = new FileOutputStream(PC2_KEYSTORE_FILE)) {
                store.store(fos, password);
                fos.close();
            }
        } catch (Exception ex) {
            if (getController().isUsingGUI()) {
                String msg = "Warning: exception occurred during KeyStore creation: " + ex.getMessage();
                msg += "\nStack trace:";
                StackTraceElement[] stackTraceElements = ex.getStackTrace();
                for (StackTraceElement line : stackTraceElements) {
                    if (line.toString().contains("edu.csus.ecs.pc2")) {
                        msg += "\n" + line;
                    }
                }
                msg += "\n...";
                JOptionPane.showMessageDialog(null, msg, "Error creating WebServer KeyStore", JOptionPane.WARNING_MESSAGE);
            }
            log.throwing("WebServer", "createKeyStoreAndFile", ex);
            ex.printStackTrace();
        }
    }

    /**
     * Start Web Server.
     * <P>
     * Starts a Jetty webserver running on the port specified in the GUI textfield, and registers a set of default REST (Jersey/JAX-RS) services with Jetty.
     * TODO: need to provide support for dynamically reconfiguring the registered services.     */
    public void startWebServer(IInternalContest aContest, IInternalController aController, Properties properties) {

        setContestAndController(aContest, aController);
        wsProperties = new WebServerPropertyUtils(properties);

        try {
            Security.addProvider(bcProvider);

            int port = wsProperties.getIntegerProperty(WebServerPropertyUtils.PORT_NUMBER_KEY, DEFAULT_WEB_SERVER_PORT_NUMBER);

            showMessage("Binding to port " + port);

            File keystoreFile = new File(PC2_KEYSTORE_FILE);

            if (!keystoreFile.exists()) {
                createKeyStoreAndKey(keystoreFile);
            }

            String apiVer = wsProperties.getStringProperty(WebServerPropertyUtils.CLICS_API_VERSION, DEFAULT_CLICS_API_VERSION);
            String apiPackage = wsProperties.getStringProperty(WebServerPropertyUtils.CLICS_API_PACKAGE, null);
            if(apiPackage == null) {
                apiPackage = DEFAULT_CLICS_API_PACKAGE_PREFIX + "." + "API" + apiVer;
            }
            // eg, edu.csus.ecs.pc2.clics.API202306.ResourceConfig202306, or, some user specified package.ResourceConfig202306
            String apiClass = apiPackage + ".ResourceConfig" + apiVer;

            apiResource = getAPIClass(apiClass);

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            // adds Jersey ServletContainer with a ResourceConfig customized with enabled REST service classes
            context.addServlet(new ServletHolder(new ServletContainer(apiResource.getResourceConfig(getContest(), getController(), wsProperties))), "/*");

            jettyServer = new Server();

            HttpConfiguration httpConfig = new HttpConfiguration();
            httpConfig.setSecureScheme("https");
            httpConfig.setSecurePort(port);
            httpConfig.setOutputBufferSize(32768);

            // set to trustAll
            SslContextFactory sslContextFactory = new SslContextFactory(true);
            sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
            sslContextFactory.setKeyStoreType("PKCS12");
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
            showMessage("Started web server on port " + port);

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
        System.out.println(new Date() + " " + message);
        ex.printStackTrace();
    }

    private void showMessage(String message) {
        System.out.println(new Date() + " " + message);
        getLog().info(message);
    }

    private Logger getLog() {
        return log;
    }

    private SecurityHandler basicAuth() {

        HashLoginService l = new HashLoginService();

        // First, load PC2 accounts into jetty - this will allow PC2 users to use the API with their login id
        //    Only teams, admins and judges
        Account[] accounts = contest.getAccounts();
        String role;
        Type clientType;

        for (Account account: accounts) {
            clientType = account.getClientId().getClientType();
            if (clientType == Type.TEAM) {
                role = WEBAPI_ROLE_TEAM;
            } else if (clientType == Type.ADMINISTRATOR) {
                role = WEBAPI_ROLE_ADMIN;
            } else if (clientType == Type.JUDGE) {
                role = WEBAPI_ROLE_JUDGE;
            } else {
                continue;
            }
            l.putUser(account.getClientId().getName(), new Password(account.getPassword()), new String [] { role });
        }

        // now load any special ones from the realm file
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
        constraintPublic.setRoles(new String[] {
            WEBAPI_ROLE_PUBLIC, WEBAPI_ROLE_BALLOON, WEBAPI_ROLE_ANALYST, WEBAPI_ROLE_BLUE, WEBAPI_ROLE_ADMIN, WEBAPI_ROLE_TEAM, WEBAPI_ROLE_JUDGE
        });

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

    public IInternalContest getContest() {
        return contest;
    }

    public IInternalController getController() {
        return controller;
    }

    public static Properties createSampleProperties() {

        Properties prop = new Properties();

        prop.put(WebServerPropertyUtils.PORT_NUMBER_KEY, DEFAULT_WEB_SERVER_PORT_NUMBER + "");

        prop.put(WebServerPropertyUtils.STARTTIME_SERVICE_ENABLED_KEY, "yes");

        prop.put(WebServerPropertyUtils.FETCH_RUN_SERVICE_ENABLED_KEY, "yes");

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


    private ICLICSResourceConfig getAPIClass(String className) {

        try {
            ICLICSResourceConfig resourceCfg = loadAPIClass(className);
            return resourceCfg;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            log.log(Log.WARNING, "Unable to load CLICS API class = " + className);
        }

        return null;
    }

    /**
     * Find and create an instance of ICLICSResourceConfig from className.
     * <P>
     * Code snippet.
     * <pre>
     * String className = "edu.csus.ecs.pc2.clics.API202306";
     * ICLICSResourceConfig iRes = loadUIClass(className);
      * </pre>
     *
     * @param className
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */

    private static ICLICSResourceConfig loadAPIClass(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Class<?> newClass = Class.forName(className);
        Object object = newClass.newInstance();
        if (object instanceof ICLICSResourceConfig) {
            return (ICLICSResourceConfig) object;
        }
        object = null;
        throw new SecurityException(className + " loaded, but not an instanceof ICLICSResourceConfig");
    }

}
