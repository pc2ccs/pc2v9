package edu.csus.ecs.pc2.services.eventFeed;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Non-GUI Event Feeder.
 * 
 * @author $Author$ pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeederModule implements UIPlugin {

    public static final int DEFAULT_EVENT_FEED_PORT_NUMBER = 4713;

    private static final long serialVersionUID = 4459685961401012498L;

    public static final String WEB_SERVICES_PROPERTIES_FILENAME = "pc2ws.properties";

    private int port = DEFAULT_EVENT_FEED_PORT_NUMBER;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private EventFeedServer eventFeedServer = new EventFeedServer();

    public String getPluginTitle() {
        return "Event Feed Server (non-GUI)";
    }

    public EventFeederModule() {
        VersionInfo versionInfo = new VersionInfo();
        System.out.println(versionInfo.getSystemName());
        System.out.println(versionInfo.getSystemVersionInfo());
        System.out.println("Build " + versionInfo.getBuildNumber());
        System.out.println("Date: " + getL10nDateTime());
        System.out.println("Working directory is " + Utilities.getCurrentDirectory());
        System.out.println();

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
        log = controller.getLog();

        try {
            showMessage("Starting Event Feed server on port " + port + " ...");
            eventFeedServer.startSocketListener(port, getContest(), false);
            showMessage("Event Feed server listening on port " + port);

        } catch (IOException e) {
            showMessage("Unable to start event feed server: " + e.getMessage());
            e.printStackTrace(System.err);
            getLog().log(Log.INFO, e.getMessage(), e);
        }
        
        if (Utilities.fileExists(WEB_SERVICES_PROPERTIES_FILENAME)) {
            try {
                
                showMessage("Found web server properties file "+WEB_SERVICES_PROPERTIES_FILENAME+" loading...");
                Properties wsProps = loadPropertiesFile(WEB_SERVICES_PROPERTIES_FILENAME);
                showMessage("Loaded "+wsProps);

                showMessage("Starting Web Server");

                WebServer server = new WebServer();
                server.startWebServer(inContest, inController, wsProps);
            } catch (Exception e) {
                showMessage("Unable to start web server "+e.getMessage(), e);
            }
        } else {
            showMessage("Note: no web services will be started ");
        }
    }

    protected String getL10nDateTime() {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        return (dateFormatter.format(new Date()));
    }
    
    private void showMessage(final String message, Exception ex) {
        getLog().log(Log.INFO, message, ex);
        System.out.println(new Date() + " " +message);
        ex.printStackTrace();
    }

    private void showMessage(String message) {
        System.out.println(new Date() + " " +message);
        getLog().info(message);
    }

    public Log getLog() {
        return log;
    }

    public IInternalContest getContest() {
        return contest;
    }

    public IInternalController getController() {
        return controller;
    }

    public void setPort(int port) {
        this.port = port;
    }
    

    
    /**
     * Loads properties from the specified file and returns them in a Properties object.
     * @param filename - the file to load from
     * @return a Properties object containing the Properties given in the file
     */

    public static Properties loadPropertiesFile(String filename) {

        Properties properties = new Properties();
        try {
            FileReader reader = new FileReader(new File(filename));
            properties.load(reader);
                //logging:
                System.out.println("Log:  Loaded " + properties.size() + " properties from '" + filename + "'");
        } catch (Exception e) {
            // exception ignored. No need to report, if exception then fall through.
        }

        return properties;
    }

}
