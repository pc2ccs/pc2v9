// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2;

/**
 * Appliction Constants.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class AppConstants {
    
    /*
     * Command line options in alphabetical order
     */
    public static final String LOAD_EF_JUDGEMENTS_OPTION_STRING = "--addefjs";

    public static final String CONTEST_PASSWORD_OPTION_STRING = "--contestpassword";
    
    public static final String DEBUG_OPTION_STRING = "--debug";

    public static final String FILE_OPTION_STRING = "-F";

    public static final String FIRST_SERVER_OPTION_STRING = "--first";
    
    public static final String HELP_OPTION_STRING = "--help";

    public static final String CONTEST_ID_OPTION_STRING = "--id";

    public static final String INI_FILENAME_OPTION_STRING = "--ini";
    
    public static final String LOAD_OPTION_STRING = "--load";

    public static final String LOGIN_OPTION_STRING = "--login";

    public static final String NO_GUI_OPTION_STRING = "--nogui";
    
    public static final String NO_SAVE_OPTION_STRING = "--nosave";

    public static final String PASSWORD_OPTION_STRING = "--password";

    public static final String PORT_OPTION_STRING = "--port";

    public static final String PROFILE_OPTION_STRING = "--profile";
    
    /**
     * Proxy this server, used with {{@link #REMOTE_SERVER_OPTION_STRING}}
     */
    public static final String PROXYME_OPTION_STRING = "--proxyme";
    
    public static final String REMOTE_SERVER_OPTION_STRING = "--remoteServer";
    
    public static final String SERVER_OPTION_STRING = "--server";

    public static final String SKIP_INI_OPTION_STRING = "--skipini";
    
    public static final String TEAM1_OPTION_STRING = "--team1";
    
    public static final String MAIN_UI_OPTION_STRING = "--ui";

//  private static final String LOAD_YAML_OPTION_STRING = "--loadyaml";
  
    /**
     * The following options are supposed to be temporary and were added to
     * increase performance for very large contests
     */
  
    /**
     * Do not show the "connections" pane (lots of account logins can make the
     * grid spend lots of CPU time and therefore make the system sluggish)
     */
    public static final String NO_CONNECTIONS_PANE_OPTION_STRING = "--noconnectionspane";
    
    /**
     * Do not create log file messages (temporary to increase performance)
     */
    public static final String NOLOGGING_OPTION_STRING = "--nologging";
    
    /**
     * Do not show the "logins" pane (lots of account logins can make the
     * grid spend lots of CPU time and therefore make the system sluggish)
     */
    public static final String NO_LOGINS_PANE_OPTION_STRING = "--nologinspane";

    /**
     * Do not show standings panes on AdminView (temporary to increase performance)
     */
    public static final String NOSTANDINGS_OPTION_STRING = "--nostandings";
    

    /**
     * Array of all options
     */
    public static final String [] allOptionStrings = {
        LOAD_EF_JUDGEMENTS_OPTION_STRING,
        CONTEST_PASSWORD_OPTION_STRING,
        DEBUG_OPTION_STRING,
        FILE_OPTION_STRING,
        FIRST_SERVER_OPTION_STRING,
        HELP_OPTION_STRING,
        CONTEST_ID_OPTION_STRING,
        INI_FILENAME_OPTION_STRING,
        LOAD_OPTION_STRING,
        LOGIN_OPTION_STRING,
        NO_GUI_OPTION_STRING,
        NO_SAVE_OPTION_STRING,
        PASSWORD_OPTION_STRING,
        PORT_OPTION_STRING,
        PROFILE_OPTION_STRING,
        PROXYME_OPTION_STRING,
        REMOTE_SERVER_OPTION_STRING,
        SERVER_OPTION_STRING,
        SKIP_INI_OPTION_STRING,
        TEAM1_OPTION_STRING,
        MAIN_UI_OPTION_STRING,
        NO_CONNECTIONS_PANE_OPTION_STRING,
        NOLOGGING_OPTION_STRING,
        NO_LOGINS_PANE_OPTION_STRING,
        NOSTANDINGS_OPTION_STRING
    };
    /*
     * .ini file section.key constant
     */
    /**
     * override port for the server to listen on.
     * 
     */
    public static final String SERVER_PORT_KEY = "server.port";

    /**
     * remote server host name.
     * <P>
     * The form of the value is: host:port.
     * <P>
     * port is optional.
     */
    public static final String REMOTE_SERVER_KEY = "server.remoteServer";
    
    /**
     * indicates whether this site wants to be proxied.
     * 
     * Example:  server.proxyme=true
     */
    public static final String PROXY_ME_SERVER_KEY = "server.proxyme";

    /**
     * Host/IP for the client to contact.
     * 
     * The form of the value is: host:port.
     * <P>
     * port is optional.
     * 
     */
    public static final String CLIENT_SERVER_KEY = "client.server";

    /**
     * the client port is the port a client connects to on the server
     */
    public static final String CLIENT_PORT_KEY = "client.port";

    
    /**
     * Other constants
     */
    public static final String DATETIME_PATTERN_LONG = "E, dd MMM yyyy HH:mm:ss z";
    
}
