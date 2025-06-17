package config;

import java.net.URISyntaxException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
	
	//holds the current state of the contest -- in particular, whether the contest has been started or not
	private static ContestState contestState = new ContestState();
	
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
			handlers.addHandler(getContestStartedHandler(contestState));
			handlers.addHandler(getWebsocketHandler());
			handlers.addHandler(getSwaggerHandler());
			handlers.addHandler(getWebAppHandler(contestState));
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

	/**
	 * Defines a Servlet ContextHandler which is intended only for internal operations, such as a "contestStarted" call posted
	 * by the ContestController.
	 * 
	 * @param contestState the current state of the contest
	 * @return a ServletContextHandler for "internal" paths such as /internal/contestStarted
	 */
	private static Handler getContestStartedHandler(ContestState contestState) {
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		handler.setContextPath("/internal");
		handler.addServlet(new ServletHolder(new ContestStartedServlet(contestState)), "/contestStarted");
		return handler;
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
	 * The actual ResourceHandler is wrapped inside a handler which rejects accesses to problem writeups
	 * if the contest has not started.
	 * 
	 * @return a {@link ContextHandler} which handles accesses to the WTI-UI web content.
	 */
	private static Handler getWebAppHandler(ContestState contestState) {

	    ResourceHandler webContentResourceHandler = new ResourceHandler();
	    webContentResourceHandler.setResourceBase("./WebContent/WTI-UI/");

	    //install a handler which is wrapped with protection against serving the contest problem writeups
	    // if the contest has not started.  This rejects references to any WTI-UI path starting with "/problems/",
	    // but allows access to any other path under ./WebContent/WTI-UI to proceed.
	    ProtectProblemWriteupsHandler protectedHandler = new ProtectProblemWriteupsHandler(contestState);
	    protectedHandler.setHandler(webContentResourceHandler);

	    ContextHandler webAppContextHandler = new ContextHandler();
	    webAppContextHandler.setContextPath("/");
	    webAppContextHandler.setHandler(protectedHandler);

	    return webAppContextHandler;
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
	

	/**
	 * This class provides a wrapper around the default webapp ContextHandler such that the wrapper
	 * checks all incoming web requests received at the default WTI-UI base address;
	 * any requests for files under "/problems" (that is, contest problem writeups) are rejected if the contest
	 * has not started while any other requests are simply forwarded to the normal handler.
	 * 
	 * @author John Clevenger  (with help from his buddy ChatGPT...)
	 *
	 */
	public static class ProtectProblemWriteupsHandler extends HandlerWrapper {

		
	    private final ContestState contestState;

	    public ProtectProblemWriteupsHandler(ContestState contestState) {
	        this.contestState = contestState;
	    }		
		
		
	    @Override
	    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
	            throws IOException, ServletException {

	        // Check if the URL path is under the "problems" folder
	        if (target != null && target.startsWith("/problems/")) {
	        	//reject requests for problem writeups if the contest hasn't started
	            if (!contestState.hasStarted()) {
	                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	                response.getWriter().write("403:Forbidden -- contest has not started");
	                baseRequest.setHandled(true);
	                return;
	            }
	        }

	        // Otherwise, delegate to the wrapped handler
	        super.handle(target, baseRequest, request, response);
	    }

	}


	/**
	 * This class defines a Java {@link HttpServlet} which handles POST requests to the /internal/contestStarted endpoint of
	 * the webserver; that is, the servlet's <code>doPost()</code> method is invoked whenever some "external" client
	 * makes an HTTP POST request to the webserver's /internal/contestStarted endpoint.
	 * The servlet is constructed and installed into the Jetty server by the {@link startServer()} method of the WebServer class.
	 * 
	 * The only legitimate source of a POST to /internal/contestStarted is the WTI-API's <code>ContestController</code>
	 * class.  In particular, that class constructs a {@link ScoreboardChangeListener} which listens for PC2 API
	 * events which could affect the Scoreboard -- which includes contest configuration updates such as changes in the contest clock.   
	 * When the ContestController's ScoreboardChangeListener gets a ClockEvent, it checks to see if the contest has started
	 * (i.e., elapsed contest time is greater than zero).  If so, it invokes a POST request to http://localhost:<port>/contestStarted
	 * (where <port> is the port on which the Jetty webserver is listening).  The effect is that the ContestController invokes 
	 * THIS servlet's <code>doPost()</code> method. 
	 * 
	 * 
	 * This class receives a reference to a {@link ContestState} object upon construction.  The initial state of that ContestState
	 * object is that the contest "has not started".  This servlet first checks to verify that the POST request came from
	 * localhost on the same machine that this webserver is running on.  If not, it returns 403 (Forbidden) and logs the attempt.
	 * If the request did come from localhost, the servlet updates the ContestState object to indicate that the contest has 
	 * started -- thus allowing other components of the webserver to know when that event has occurred.
	 * 
	 * Note that currently the ContestState object is only passed into this servlet and into the {@link ProtectProblemWriteupsHandler} 
	 * class (which protects against accesses to Contest Problem writeups before the contest has started.  If other classes
	 * (for example, the Jersey REST API endpoint classes) wanted to know if the contest has started they could be updated to
	 * receive the same ContestState object (see method Webserver.startServer(); in particular, the section of code which 
	 * constructs the endpoint handlers which are installed into Jetty).
	 * 
	 * Note also the following (slight) vulnerability:  if someone hacks into the machine on which you are running the WTI Server,
	 * they could potentially send a POST /contestStarted event from localhost, which this servlet would accept as legitimate.  This would have
	 * the effect of marking the contest as "started" from the point of view of the WTI Server -- even if it has not actually
	 * been started in the PC^2 Admin.  However, if someone has hacked into your WTI Server, you probably have bigger problems to worry about... 
	 * 
	 * @author John Clevenger (with help from his buddy ChatGPT...)
	 *
	 */
	public static class ContestStartedServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;
		
	    private final ContestState contestState;

	    /**
	     * Constructs a {@link HttpServlet} to handle POST requests made to the Jetty webserver <code>/contestStarted</code> endpoint.
	     * @param contestState a {@link ContestState} object which tracks the state of the contest -- in particular, whether the contest
	     * 						has been started or not.
	     */
	    public ContestStartedServlet(ContestState contestState) {
	        this.contestState = contestState;
	    }

		@Override
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			
			//verify that the POST request for /contestStarted came from localhost (i.e., from within this webserver)
	        String remoteAddr = req.getRemoteAddr();

	        if (!remoteAddr.equals("127.0.0.1") && !remoteAddr.equals("::1")) { // !IPv4 && !IPv6
	            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
	            resp.getWriter().write("Forbidden: Only localhost may trigger this action.");
	            
	            //TODO:  LOG the IP which generated this (bogus) request
	            
	            return;
	        }

	        contestState.setStarted(true);
	        
	        //return an "OK" response to the invoker
	        resp.setStatus(HttpServletResponse.SC_OK);
	        resp.getWriter().write("Contest marked as started.");
	    }
		
		//Useful since it allows a tester to use a browser to access
		// "<IP>:<port>/contestStarted" to verify this servlet is running...)
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		    resp.getWriter().write("ContestStartedServlet is alive.");
		}

	}
	
}
