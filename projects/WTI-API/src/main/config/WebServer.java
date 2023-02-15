package config;

import java.net.URISyntaxException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.servlet.ServletContainer;

import communication.WTIWebsocketMediator;
import controllers.ContestController;
import controllers.TeamsController;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.core.log.Log;
import io.swagger.jaxrs.config.DefaultJaxrsConfig;

/**
 * This class encapsulates a Jetty webserver which acts as the WTI server listening for connections from team browsers.
 * 
 * The Jetty server is initialized with a set of {@link Handler}s for websocket connections, Swagger connections,
 * webcontent resources, and Jersey (JAX-RS) connections.
 * 
 * @author EWU WTI Student Project Team
 *
 */
public class WebServer {
	//the initialization values for the server
	private static ServerInit ini;
	
	/**
	 * Starts a Jetty server using the initialization values specified in the received {@link ServerInit} object.
	 * 
	 * Verifies that the {@link ContestController} which will be used by Jetty will be able to successfully login to
	 * the PC2 scoreboard account specified in the configuration.  If so, 
	 * initializes the Jetty server with resource handlers, starts it listening on the port specified in the 
	 * received {@link ServerInit} object, and blocks (via a join()) waiting for the server to be shut down.
	 * 
	 * @param initServer a {@link ServerInit} object containing initialization values for the server being started
	 * 
	 * @throws LoginFailureException if a failure occurs when attempting to log in to the PC2 server using the
	 * 								configured scoreboard account
	 * @throws Exception if any other Exception occurs during webserver startup
	 */
	public static void startServer(ServerInit initServer) throws LoginFailureException, Exception {
		ini = initServer;
		
		Log logger = ini.getLogger();
		
		try {
			//make sure the ContestController created by the server is going to be able to login using the configured PC2 scoreboard account
			if (!verifyPC2ScoreboardLogin()) {
				throw new LoginFailureException("PC2 Scoreboard login failed");
			}

			// get the endpoint handlers which will be installed in Jetty
			logger.info("Constructing Jetty service handlers");
			HandlerList handlers = new HandlerList();
			handlers.addHandler(getWebsocketHandler());
			handlers.addHandler(getSwaggerHandler());
			handlers.addHandler(getWebApp());
			handlers.addHandler(getJerseyHandler());

			//create a new Jetty server
			logger.info("Creating Jetty server");
			Server server = new Server(ini.getPortNum());
			System.out.println("Starting on port "+ini.getPortNum());

			//install the endpoint handlers in Jetty
			logger.info("Installing service handlers in Jetty");
			server.setHandler(handlers);
			
			//start Jetty listening for endpoint references
			logger.info("Starting Jetty server");
			server.start();
			
			//block until all server threads are done (which won't normally happen - so, wait forever)
			server.join();

		} catch (LoginFailureException ex) {
			System.err.println("WTI server failed to login with PC2 Scoreboard account: " + ex);
			logger.severe("WTI server failed to login with PC2 Scoreboard account: " + ex);
			throw ex;
		} catch (Exception ex) {
			System.err.println(ex);
			logger.severe("Exception during WTI server startup: " + ex);
			throw ex;

		}
	}

	//verifies that the provided (or default) PC2 scoreboard login credentials work
	private static boolean verifyPC2ScoreboardLogin() {
		
		ini.getLogger().fine("Verifying PC2 scoreboard account login...");
		
		//create a scoreboard account connection to the PC2 server
		ServerConnection scoreboardServerConn = new ServerConnection();
	
		//get the credentials to be used to login to the PC2 server, either those given in the WTI pc2v9.ini file or the defaults
		String sbAccount = ini.getScoreboardAccount();
		if (sbAccount==null || sbAccount.equals("")) {
			sbAccount = ContestController.DEFAULT_PC2_SCOREBOARD_ACCOUNT;
		}
		String sbPassword = ini.getScoreboardPassword();
		if (sbPassword==null || sbPassword.equals("")) {
			sbPassword = ContestController.DEFAULT_PC2_SCOREBOARD_PASSWORD;
		}
		
		//try to login to the PC2 server
		try {
			ini.getLogger().fine("Attempting to login to PC2 scoreboard account '" + sbAccount + "'");
			scoreboardServerConn.login(sbAccount, sbPassword);
		} catch (LoginFailureException e) {
			ini.getLogger().severe("WTI Login failed for scoreboard account '" + sbAccount + "': " + e.getMessage());
			return false;
		} 
		
		ini.getLogger().fine("Successfully logged in to PC2 scoreboard account");
		
		//log the scoreboard account back out so the ContestController can re-login
		try {
			ini.getLogger().fine("Logging back out of PC2 scoreboard account pending team connections");
			scoreboardServerConn.logoff();
		} catch (NotLoggedInException e) {
			ini.getLogger().severe("Illegal state: got a NotLoggedInException during scoreboard logout after successful login:" + e.getMessage());
			System.err.println("Illegal state: got a NotLoggedInException during scoreboard logout after successful login: " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (Exception e) {
            ini.getLogger().severe("Exception during scoreboard logout after successful login: " + e.getMessage());
            System.err.println("Exception during scoreboard logout after successful login: " + e.getMessage());
            e.printStackTrace();
            return false;
		    
		}
		
		return true;
	}

	/**
	 * Returns a {@link Handler} for websocket connections.  
	 * 
	 * The "context path" for the handler is set to be the websocket name specified in the {@link ServerInit} 
	 * object used to start the server. An instance of {@link WTIWebsocketMediator} is set as an endpoint handler
	 * for the handler.
	 * 
	 * @return a {@link ServletContextHandler} 
	 */
	private static ServletContextHandler getWebsocketHandler() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(ini.getWsName());
		try {
			ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);
			wscontainer.addEndpoint(WTIWebsocketMediator.class);
			
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (DeploymentException e) {
			e.printStackTrace();
		} 
		return context;
	}
	
	/**
	 * Returns a {@link Handler} for webapp content.
	 * 
	 * The webcontent resource base in the Handler is set to the WebContent folder of the WTI-UI project.
	 * 
	 * @return a {@link ContextHandler}
	 */
	private static Handler getWebApp() {
		
		ResourceHandler webContent = new ResourceHandler();
		webContent.setResourceBase("./WebContent/WTI-UI/");
		
		ContextHandler webApp = new ContextHandler();
		webApp.setHandler(webContent);
		
		return webApp;
	}
	
	/**
	 * Returns a {@link Handler} for Swagger content.
	 * 
	 * The webcontent resource base in the Handler is set to the WebContent folder of the WTI-UI project;
	 * the context path for the Handler is set to "/swagger".
	 * 
	 * @return a {@link ContextHandler}
	 */
	private static Handler getSwaggerHandler() {

		ResourceHandler webContent = new ResourceHandler();
		webContent.setResourceBase("./WebContent/webapp");
		
		ContextHandler swagger = new ContextHandler("/swagger");
		swagger.setHandler(webContent);
		
		return swagger;
	}

	/**
	 * Returns a {@link Handler} for Jersey (JAX-RS) connections.
	 * 
	 * The context path for the Handler is set to "/api"; the Handler has {@link ServletHolder}s containing 
	 * JacksonJaxbJsonProvider, {@link TeamsController}, {@link ContestController}, {@link JacksonFeature},
	 * Swagger, and CORS servlets.
	 * 
	 * @return a {@link ServletContextHandler}
	 * @throws LoginFailureException if the ContestController servlet could not log in to the PC2 server
	 * @throws URISyntaxException if the URI built from the pc2v9.ini WTI attributes is invalid
	 */
	private static Handler getJerseyHandler()  {

		//Add basic api servlet
		ServletContextHandler api = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		api.setContextPath("/api"); 

		ServletHolder servletHolder = api.addServlet(ServletContainer.class, "/*");
		servletHolder.setInitParameter("jersey.config.server.provider.classnames", 
				"org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider, controllers.TeamsController, controllers.ContestController, org.glassfish.jersey.jackson.JacksonFeature");
		servletHolder.setInitParameter("jersey.config.server.provider.packages", "jerseyConfig; io.swagger.jaxrs.json; io.swagger.jaxrs.listing");
		servletHolder.setInitOrder(1); //force servlet to initialize when handler first starts
		
		
		ServletHolder swaggerServlet = api.addServlet(DefaultJaxrsConfig.class, "/swagger-core");
		swaggerServlet.setInitOrder(2);
		swaggerServlet.setInitParameter("api.version", "1.0.0");
		swaggerServlet.setInitParameter("swagger.api.basepath", String.format("http://%s:%s/api", ServerInit.getLocalIp(), ini.getPortNum()));
		swaggerServlet.setInitParameter("swagger.api.title", "Web Team Interface");
		
		// Enable Cors
		FilterHolder cors = api.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,OPTIONS,DELETE");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "*");

		return api;
	}
}
