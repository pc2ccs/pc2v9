package config;

public class JettyConfig {

	public static void main(String[] args) throws Exception {
		
		ServerInit server = ServerInit.createServerInit();
		ServerInit.updateUIAppConfig();
		WebServer.startServer(server);
		
	}

}
