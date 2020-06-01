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
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
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
	 * Initializes the Jetty server with resource handlers, starts it listening on the port specified in the 
	 * received {@link ServerInit} object, and blocks (via a join()) waiting for the server to be shut down.
	 * 
	 * @param initServer a {@link ServerInit} object containing initialization values for the server being started
	 */
	public static void startServer(ServerInit initServer) {
		ini = initServer;
		
		HandlerList handlers = new HandlerList();
		handlers.addHandler(getWebsocketHandler());
		handlers.addHandler(getSwaggerHandler());
		handlers.addHandler(getWebApp());
		handlers.addHandler(getJerseyHandler());

		try {

			Server server = new Server(ini.getPortNum());

			server.setHandler(handlers);
			server.start();
			server.join();

		} catch (Exception ex) {
			System.err.println(ex);
		}
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
