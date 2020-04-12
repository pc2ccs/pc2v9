package config;

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
import org.glassfish.jersey.servlet.ServletContainer;

import communication.WTIWebsocketMediator;
import io.swagger.jaxrs.config.DefaultJaxrsConfig;


public class WebServer {
	private static ServerInit ini;
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
	
	private static Handler getWebApp() {
		
		ResourceHandler webContent = new ResourceHandler();
		webContent.setResourceBase("./WebContent/WTI-UI/");
		
		ContextHandler webApp = new ContextHandler();
		webApp.setHandler(webContent);
		
		return webApp;
	}
	
	private static Handler getSwaggerHandler() {

		ResourceHandler webContent = new ResourceHandler();
		webContent.setResourceBase("./WebContent/webapp");
		
		ContextHandler swagger = new ContextHandler("/swagger");
		swagger.setHandler(webContent);
		
		return swagger;
	}

	private static Handler getJerseyHandler() {

		//Add basic api servlet
		ServletContextHandler api = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		api.setContextPath("/api"); 

		ServletHolder servletHolder = api.addServlet(ServletContainer.class, "/*");
		servletHolder.setInitParameter("jersey.config.server.provider.classnames", 
				"org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider, controllers.TeamsController, controllers.ContestController, org.glassfish.jersey.jackson.JacksonFeature");
		servletHolder.setInitParameter("jersey.config.server.provider.packages", "jerseyConfig; io.swagger.jaxrs.json; io.swagger.jaxrs.listing");

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
