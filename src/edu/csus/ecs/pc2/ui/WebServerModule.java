package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.exception.CommandLineErrorException;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.ws.ResponseHandler;
import edu.csus.ecs.pc2.ws.WebServer;

/**
 * non-GUI Web Server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class WebServerModule {
    
    private ServerConnection serverConnection = null;

    private int port = 80;

    private boolean debugMode = false;
    
    private String login;

    private String password;

    private IContest contest;
    
    public WebServerModule(String[] args) throws CommandLineErrorException {
        loadVariables(args);
    }
    
    public void startWebServer() {
//        checkRequiredParams();
        
        try {
            
            serverConnection = new ServerConnection();

            contest = serverConnection.login(login, password);
            
            System.out.println("For: " + contest.getMyClient().getDisplayName() + " (" + contest.getMyClient().getLoginName() + ")");
            System.out.println();

            WebServer webServer = new WebServer();
            ResponseHandler responseHandler = new ResponseHandler();
            responseHandler.setContestAndServerConnection(serverConnection, contest);
            webServer.setResponseHandler(responseHandler);
            webServer.startServer(port);
        } catch (Exception e) {
            System.err.println("Error - "+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    protected void loadVariables(String[] args) throws CommandLineErrorException {

        if (args.length == 0 || args[0].equals("--help")) {
            usage();
            System.exit(4);
        }

        String[] opts = { "--login", "--password", "--port"};

        ParseArguments arguments = new ParseArguments(args, opts);

        debugMode = arguments.isOptPresent("--debug");

        if (debugMode) {
            arguments.dumpArgs(System.err);
        }

        String cmdLineLogin = null;

        String cmdLinePassword = null;

        if (arguments.isOptPresent("--login")) {
            cmdLineLogin = arguments.getOptValue("--login");
        }
        
        if (arguments.isOptPresent("--port")) {
            port = arguments.getLongOptionValue("--port").intValue();
        }

        if (arguments.isOptPresent("--password")) {
            cmdLinePassword = arguments.getOptValue("--password");
        }

        setLoginPassword(cmdLineLogin, cmdLinePassword);

    }
    
    /**
     * Expand shortcut names.
     * 
     * @param loginName
     */
    private void setLoginPassword(String loginName, String inPassword) {

        ClientId id = InternalController.loginShortcutExpansion(1, loginName);
        if (id != null) {

            login = id.getName();
            password = inPassword;

            if (password == null) {
                password = login;
            }

        }
    }

    private void usage() {
        String[] usage = { //
        "Usage WebServer [--help] ", //
                "Usage WebServer [-F propfile] [--port ##] --login login --password password", //
                "", //
                "--help   this listing", //
                "", //
                "$Id$", //
        };

        for (String s : usage) {
            System.out.println(s);
        }
    }

    protected void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {

        try {
            WebServerModule webModule = new WebServerModule(args);
            webModule.startWebServer();
        } catch (Exception e) {
            System.err.println("Error " + e.getMessage());
            e.printStackTrace(System.err);
        }

    }

}
