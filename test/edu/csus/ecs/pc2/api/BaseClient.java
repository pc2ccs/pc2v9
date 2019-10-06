package edu.csus.ecs.pc2.api;

import java.util.ArrayList;
import java.util.Arrays;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.ParseArguments;

/**
 * A Client using the API to login.
 * 
 * @author Douglas A. Lane <laned@ecs.csus.edu>
 */
public abstract class BaseClient {

    /**
     * Upon successful login invokes this method.
     */
    public abstract void onLoginAction();

    /**
     * Print program usage information
     */
    public abstract void printProgramUsageInformation();

    protected ParseArguments bcParseArguments = null;

    private IContest contest;

    private ServerConnection serverConnection;

    private String[] optionsWithParameter = REQUIRED_OPTIONS_LIST;

    public static final String[] REQUIRED_OPTIONS_LIST = {
            //
            "--login", "--password", // pc2 login password
    };

    /**
     * Add additional command line options.
     * 
     * @param optionList
     */
    public void addOptions(String[] optionList) {
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(REQUIRED_OPTIONS_LIST));
        list.addAll(Arrays.asList(optionList));
        optionsWithParameter = (String[]) list.toArray(new String[list.size()]);
    }

    public IContest getContest() {
        return contest;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    /**
     * Command line options that have a parameter.
     * @return
     */
    public String[] getOptionsWithParameter() {
        return optionsWithParameter;
    }

    /**
     * Login and perform onLoginAction().
     * 
     * @param user
     * @param password
     * @throws LoginFailureException
     * @throws NotLoggedInException 
     */
    public void login(String[] args) throws LoginFailureException, NotLoggedInException {
        serverConnection = new ServerConnection();

        bcParseArguments = new ParseArguments(args, getOptionsWithParameter());

        if (bcParseArguments.isOptPresent("--help")) {
            printProgramUsageInformation();
            System.exit(0);
        }

        String loginParam = bcParseArguments.getOptValue("--login");
        
        String user = InternalController.loginShortcutExpansion(0, loginParam).getName();
        
        System.out.println("debug 22 user = "+user);

        if (user == null || user.trim().length() == 0) {
            System.err.println("Missinng login name, --login option");
            System.exit(4);
        }

        String password = bcParseArguments.getOptValue("--password");

        if (password == null || password.trim().length() == 0) {
            password = user;
        }

        contest = serverConnection.login(user, password);
        onLoginAction();
    }

}
