package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

	private static ServerInit init = null;
	private int portNum;
	private String socketSource;
	private String scoreboardAccount;
	private String scoreboardPassword;
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
		try {
		      //p.load(new FileInputStream("WebTeamInterface.ini"));
              p.load(new FileInputStream("pc2v9.ini"));
		      this.portNum = Integer.parseInt(p.getProperty("wtiport"));
		      this.socketSource = p.getProperty("wtiwsName");
		      this.scoreboardAccount = p.getProperty("wtiscoreboardaccount", "scoreboard2");
		      this.scoreboardPassword = p.getProperty("wtiscoreboardpassword", "scoreboard2");
		} catch(FileNotFoundException e) {
			this.logger.info("pc2v9.ini File missing; reverting to default WTI port/socket/scoreboard values");
			setDefaults();
		} catch (NumberFormatException e) {
            this.logger.info("No parsable integer wtiport value found in pc2v9.ini; reverting to default WTI port/socket/scoreboard values");
            setDefaults();
		} catch (IOException e) {
			this.logger.info(e.getLocalizedMessage());
			setDefaults();
		}
	}
	
	private void setDefaults() {
		this.portNum = 8080;
		this.socketSource ="/websocket";
		this.scoreboardAccount = "scoreboard2";
		this.scoreboardPassword = "scoreboard2";
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
	 * This method constructs a JSON string containing the HTTP and WebSocket URLs which the WTI-UI front-end
	 * code will use to contact this WTI server.  The JSON string is saved in a (hard-coded) file location
	 * where the WTI-UI project expects to find it.
	 */
	public static void updateUIAppConfig() {
		try {
			ServerInit ini = ServerInit.createServerInit();
			String localIpAddress = getLocalIp();
			if (localIpAddress == null) throw new Exception("could not get local ip address.");
			
			String baseUrl = new StringBuilder("http://")
					.append(localIpAddress)
					.append(":")
					.append(ini.getPortNum())
					.append("/api")
					.toString();
			
			String websocketUrl = new StringBuilder("ws://")
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
	 * The returned address is obtained by calling {@link InetAddress#getHostAddress()} on the address
	 * returned by {@link DatagramSocket#getLocalAddress()}.
	 * 
	 * @return a String containing an IP address
	 */
	public static String getLocalIp() {
		// Source: https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			return socket.getLocalAddress().getHostAddress();
		}
		catch (Exception ex) { }
		
		return null;
	}

	/** Returns the {@link pc2.core.log.Log} logger being used by this WTI server
	 * 
	 * @return a {@link pc2.core.log.Log} logger.
	 */
	public Log getLogger() {
		return logger;
	}

}
