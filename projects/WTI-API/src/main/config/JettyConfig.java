package config;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.core.log.LogType;
import edu.csus.ecs.pc2.core.log.PC2LogManager;

/**
 * This class is the main starting point for the WTI project's server.  It creates and initializes a Jetty server
 * containing handlers for all WTI endpoints.
 * 
 * @author EWU WTI Student Project team
 *
 */
public class JettyConfig {

	public static void main(String[] args)  {
		
		try {
		    
		    // TODO 746 provide a way to provide ONE_LOG_PER_CLIENT, thus
//		    if (option to use individual logs per client){
//		        new PC2LogManager(LogType.ONE_LOG_PER_CLIENT);
//		    }
		    new PC2LogManager(LogType.ONE_LOG_FOR_ALL_CLIENTS);
		    
			//create an initial configuration for the WTI Jetty server, including logging and reading/caching pc2v9.ini properties
			ServerInit serverInit = ServerInit.createServerInit();
			
			//build the configuration which will be used to initialize the WTI-UI Angular app
			ServerInit.updateUIAppConfig();
			
			//start the Jetty webserver
			WebServer.startServer(serverInit);
			
		} catch (LoginFailureException e) {
			System.err.println("Cannot log WTI Server into PC2 Server scoreboard account");
			System.exit(1);
		} catch (Exception ex) {
			System.err.println ("Exception starting WTI Server: " + ex);
			System.exit(2);
		}
		
	}

}
