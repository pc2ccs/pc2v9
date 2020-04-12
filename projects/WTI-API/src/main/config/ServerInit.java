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


public class ServerInit {

	private static ServerInit init = null;
	private int portNum;
	private String socketSource;
	private Logging logger;
	
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
		} catch(FileNotFoundException e) {
			this.logger.logInfo("pc2v9.ini File missing; reverting to default WTI server port");
			setDefaults();
		} catch (NumberFormatException e) {
            this.logger.logInfo("No parsable integer wtiport value found in pc2v9.ini; reverting to default WTI server port");
            setDefaults();
		} catch (IOException e) {
			this.logger.logInfo(e.getLocalizedMessage());
			setDefaults();
		}
	}
	
	private void setDefaults() {
		this.portNum = 8080;
		this.socketSource ="/websocket";
	}
	
	public int getPortNum() {
		return this.portNum;
	}
	
	public String getWsName() {
		return this.socketSource;
	}
	
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
	
	public static String getLocalIp() {
		// Source: https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			return socket.getLocalAddress().getHostAddress();
		}
		catch (Exception ex) { }
		
		return null;
	}

}
