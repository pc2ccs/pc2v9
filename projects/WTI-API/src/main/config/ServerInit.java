package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;

import edu.csus.ecs.pc2.core.log.Log;


/**
 * This class encapsulates initialization values for the WTI server, including the port number on which
 * the server listens for browser connections, the base name to be used for the websocket connections between
 * the server and browser sessions, and the scoreboard account/pw information which the WTI server uses to
 * fetch scoreboard information from the PC2 server.
 *
 * The initialization values are read from the pc2v9.ini file in the WTI server's startup folder; if no such
 * file is present then default values are assigned.
 *
 * @author EWU WTI Student Project Team
 *
 */
public class ServerInit {

	// File where WTI properties are configured
    public final String WTI_INI_FILE_KEY = "pc2v9.ini";
	// Property keys for WTI
    public final String WTI_HTTP_PORT_KEY = "wtiport";
    private final String WTI_WEBSOCKET_NAME_KEY = "wtiwsName";
    private final String WTI_SCOREBOARD_ACCT_KEY = "wtiscoreboardaccount";
    private final String WTI_SCOREBOARD_PASS_KEY = "wtiscoreboardPassword";
    private final String WTI_PUBLIC_IP_OVERRIDE_KEY = "wtiOverridePublicIP";
    // eg. allowedOSName1, allowedOSName2, ..., allowedOSNameLinux
    private final String WTI_ALLOWED_OS_NAME_PREFIX_KEY = "allowedOSName";
    // Property keys for running WTI over https
    private final String WTI_USE_SSL_KEY = "useSSL";
    private final String WTI_KEY_STORE_PATH_KEY = "keyStoreFilePath";
    private final String WTI_KEY_STORE_PASSWORD_KEY = "keyStorePassword";
    private final String WTI_CERT_ALIAS_KEY = "certificateAlias";

    private static final boolean DEF_USE_SSL = false;
    private static final int DEF_WTI_HTTP_PORT = 8080;
    private static final String DEF_WEBSOCK_PATH = "/websocket";
    private static final String DEF_SCOREBOARD_ACCT = "scoreboard2";
    private static final String DEF_SCOREBOARD_PASS = "scoreboard2";
    private static final String DEF_KEY_STORE_PASSWORD = "contest";

	private static ServerInit init = null;
	private static String publicIPOverride;
	private static List<String> allowedOSNames;

	private boolean useSSL = DEF_USE_SSL;
	private int portNum;
	private String socketSource;
	private String scoreboardAccount;
	private String scoreboardPassword;

	private String keyStoreFilePath;
	private String keyStorePassword = DEF_KEY_STORE_PASSWORD;
	private String certAlias;

	private Log logger;

	private ServerInit() {
		this.logger = Logging.getLogger();
		this.readIniFile();
	}

	public static ServerInit createServerInit() {
		if (init == null)
			init = new ServerInit();
		return init;
	}

	private void readIniFile() {

		Properties p = new Properties();
		String exceptKey = "none";

		try {
		      //p.load(new FileInputStream("WebTeamInterface.ini"));
              p.load(new FileInputStream(WTI_INI_FILE_KEY));

              exceptKey = WTI_USE_SSL_KEY;
              String sslValue = p.getProperty(WTI_USE_SSL_KEY);
              if(sslValue != null && Integer.parseInt(sslValue) > 0) {
            	  this.useSSL = true;
              } else {
            	  this.useSSL = false;
              }
              exceptKey = WTI_HTTP_PORT_KEY;
		      this.portNum = Integer.parseInt(p.getProperty(WTI_HTTP_PORT_KEY));

		      this.socketSource = p.getProperty(WTI_WEBSOCKET_NAME_KEY);
		      this.scoreboardAccount = p.getProperty(WTI_SCOREBOARD_ACCT_KEY, DEF_SCOREBOARD_ACCT);
		      this.scoreboardPassword = p.getProperty(WTI_SCOREBOARD_PASS_KEY, DEF_SCOREBOARD_PASS);

		      publicIPOverride = p.getProperty(WTI_PUBLIC_IP_OVERRIDE_KEY);
		      allowedOSNames = getAllowedOSNames(p);

		      keyStoreFilePath = p.getProperty(WTI_KEY_STORE_PATH_KEY);
		      keyStorePassword = p.getProperty(WTI_KEY_STORE_PASSWORD_KEY);
		      certAlias = p.getProperty(WTI_CERT_ALIAS_KEY);

		      String infoMsg = "Found the following properties in " + WTI_INI_FILE_KEY + ": " + p;
		      System.out.println (infoMsg);
		      logger.info(infoMsg);

		} catch(FileNotFoundException e) {
			this.logger.info(WTI_INI_FILE_KEY + " File missing; reverting to default WTI port/socket/scoreboard values");
			setDefaults();
		} catch (NumberFormatException e) {
            this.logger.info("No parsable integer '" + exceptKey + "' value found in " + WTI_INI_FILE_KEY + "; reverting to default WTI port/socket/scoreboard values");
            setDefaults();
		} catch (IOException e) {
			this.logger.info(e.getLocalizedMessage());
			setDefaults();
		}
	}

	/**
	 * Returns a list of property values for every entry in the specified Properties object
	 * whose key starts with the String "allowedOSName".
	 *
	 * @param p a Properties object which potentially contains entries giving allowed OS names.
	 * @return a List<String> containing the values for all entries in the specified Properties whose key starts with "allowedOSName".
	 * 			The returned list may be empty but will never be null.
	 */
	private List<String> getAllowedOSNames(Properties p) {

		List<String> allowedNames = new ArrayList<String>();
		for (Object key : p.keySet()) {
			String keyName = key.toString();
			if (keyName.startsWith(WTI_ALLOWED_OS_NAME_PREFIX_KEY)) {
				allowedNames.add(p.getProperty(keyName));
			}
		}
		return allowedNames;
	}

	private void setDefaults() {
		this.useSSL = DEF_USE_SSL;
		this.portNum = DEF_WTI_HTTP_PORT;
		this.socketSource = DEF_WEBSOCK_PATH;
		this.scoreboardAccount = DEF_SCOREBOARD_ACCT;
		this.scoreboardPassword = DEF_SCOREBOARD_PASS;
	}

	/**
	 * Returns true if the WTI server should listen for HTTPS browser (team) connections as opposed
	 * to HTTP browser connections.
	 * @return a boolean
	 */
	public boolean isUseSSL() {
		return useSSL;
	}

	/**
	 * Returns the port number on which the WTI server should listen for browser (team) connections.
	 * @return an integer port number
	 */
	public int getPortNum() {
		return this.portNum;
	}

	/**
	 * Returns the String which is the base name for websocket connections between the WTI server and client (browser) sessions.
	 * @return a String containing the websocket base name
	 */
	public String getWsName() {
		return this.socketSource;
	}

	/**
	 * Returns a String containing the PC2 account name which the WTI server should use to login to the PC2 server to fetch
	 * scoreboard information.
	 * @return a String containing a PC2 scoreboard account
	 */
	public String getScoreboardAccount() {
		return this.scoreboardAccount;
	}

	/**
	 * Returns a String containing the password for the PC2 scoreboard account.
	 * @return a password String
	 */
	public String getScoreboardPassword() {
		return this.scoreboardPassword;
	}

	/**
	 * Returns a String containing the SSL keystore file path.
	 * @return a full path String
	 */
	public String getKeystoreFile() {
		return this.keyStoreFilePath;
	}

	/**
	 * Returns a String containing the SSL keystore password.
	 * @return the password String
	 */
	public String getKeystorePassword() {
		return this.keyStorePassword;
	}

	/**
	 * Returns a String containing the alias of the certificate to use from the keystore.
	 * @return the certificate alias String
	 */
	public String getCertAlias() {
		return this.certAlias;
	}

	/**
	 * This method constructs a JSON string containing the HTTP and WebSocket URLs which the WTI-UI front-end
	 * code will use to contact this WTI server.  The JSON string is saved in a (hard-coded) file location
	 * where the WTI-UI project expects to find it.
	 */
	public static void updateUIAppConfig() {
		try {
			ServerInit ini = ServerInit.createServerInit();
			String localIpAddress = getLocalIp();
			if (localIpAddress == null) throw new Exception("could not get local ip address.");

			String baseUrl = new StringBuilder(ini.isUseSSL() ? "https://" : "http://")
					.append(localIpAddress)
					.append(":")
					.append(ini.getPortNum())
					.append("/api")
					.toString();

			String websocketUrl = new StringBuilder(ini.isUseSSL() ? "wss://" : "ws://")
					.append(localIpAddress)
					.append(":")
					.append(ini.getPortNum())
					.append("/websocket/WTISocket")
					.toString();

			JsonObject newJson = Json.createObjectBuilder()
					.add("baseUrl", baseUrl)
					.add("websocketUrl", websocketUrl)
					.build();

			// Save the JSON URL string to the file location where the WTI-UI code expects to find it.
			// Ideally this would do a find-and-replace on the existing file, in order to preserve other variables!!
			File appConfig = new File("WebContent/WTI-UI/assets/appconfig.json");
			if (appConfig.exists()) appConfig.delete();
			PrintWriter output = new PrintWriter(appConfig);
			output.println(newJson);
			output.close();
		}
		catch (Exception ex) {

		}
	}

	/**
	 * Returns a String containing the local IP address of the machine on which the WTI server is running.
	 * The returned address is initially obtained by calling {@link InetAddress#getHostAddress()} on the address
	 * returned by {@link DatagramSocket#getLocalAddress()}. This is the address returned unless the pc2v9.ini
	 * file contains an entry "wtiOverridePublicIP", in which case the value of that entry is returned instead.
	 *
	 * @return a String containing an IP address, or null if an exception occurs while fetching the local IP address.
	 */
	public static String getLocalIp() {
		if (publicIPOverride != null) {
			return publicIPOverride;
		} else {
			// Source: https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
			try (final DatagramSocket socket = new DatagramSocket()) {
				socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
				return socket.getLocalAddress().getHostAddress();
			} catch (Exception ex) {
				Logging.getLogger().log(Log.SEVERE, "Exception getting local IP address: " + ex.getMessage());
			}
			return null;
		}
	}

	/** Returns the {@link pc2.core.log.Log} logger being used by this WTI server
	 *
	 * @return a {@link pc2.core.log.Log} logger.
	 */
	public Log getLogger() {
		return logger;
	}

	/**
	 * Returns a List<String> of the OS names which have been specified in the pc2v9 ini file with keys starting with
	 * "allowedOSName" -- for example, "allowedOSName1=Windows", or "allowedOSName2=Linux".
	 * @return a List<String> of allowed OS names.
	 */
	public static List<String> getAllowedOSNames() {
		return allowedOSNames;
	}

}
