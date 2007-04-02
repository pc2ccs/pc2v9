package edu.csus.ecs.pc2;

import edu.csus.ecs.pc2.core.Controller;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.transport.TransportException;
import edu.csus.ecs.pc2.ui.JudgeView;
import edu.csus.ecs.pc2.ui.LoginFrame;
import edu.csus.ecs.pc2.ui.ServerView;
import edu.csus.ecs.pc2.ui.TeamView;

/**
 * Starter class.
 * 
 * The Starter class is the main driver for all PC<sup>2</sup> modules.
 * <P>
 * The class presents the user with a {@link edu.csus.ecs.pc2.ui.LoginFrame}, upon selecting login the LoginFrame class invokes the
 * {@link #run()} method and uses {@link edu.csus.ecs.pc2.core.Controller#login}, that method returns a
 * {@link edu.csus.ecs.pc2.core.model.IModel}, a model on succesful login.
 * <P>
 * Upon a Exception or invalid login a message will appear on the LoginFrame message area.
 * <P>
 * Upon a successful login, based on {@link edu.csus.ecs.pc2.core.model.ClientType.Type} a class name will be looked up in a {@link java.util.Properties} file.
 * The class will be instanciated and passed a {@link edu.csus.ecs.pc2.core.model.IModel} and
 * {@link edu.csus.ecs.pc2.core.IController}.
 * <P>
 * At this point the LoginFrame is set not visible and the class that was loaded takes over focus.
 * 
 * @author pc2@ecs.csus.edu
 */

// TODO write code for a command line login
// $HeadURL$
public class Starter implements Runnable {

    public static final String SVN_ID = "$Id$";

    private static final String SITE_OPTION = "--site";

    private LoginFrame loginFrame = new LoginFrame();
    
    private ParseArguments parseArguments = new ParseArguments();

    protected Starter() {
        // this constructor required by CheckStyle.

    }
    
    public Starter (String [] stringArray){
        String [] arguments = {"--site"};
        parseArguments = new ParseArguments(stringArray, arguments);
    }

    /**
     * Show version info and start Login window.
     * 
     * @param args
     */
    public static void main(String[] args) {

        VersionInfo versionInfo = new VersionInfo();
        System.out.println(versionInfo.getSystemVersionInfo());
        System.out.println();
        
        Starter starter = new Starter(args);
        
        starter.startLoginFrame();

    }

    /**
     * Show the login frame.
     * 
     */
    private void startLoginFrame() {
        loginFrame.setRunnable(this);
        loginFrame.setVisible(true);
    }

    /**
     * Attempt to login and show main frame for client.
     * 
     * This is a call back method for the LoginFrame when the Login button is used.
     */
    public void run() {
        String id = loginFrame.getLogin();
        String password = loginFrame.getPassword();
        login(id, password, true);
    }

    /**
     * Login to PC^2, either with UI or not.
     * 
     * @param id -
     *            login name
     * @param password -
     *            login password
     * @param showDefaultUI
     *            assume LoginFrame is used and that UI is presented to user.
     */
    public void login(String id, String password, boolean showDefaultUI) {

        try {
            IModel model = Controller.login(id, password);
            IController controller = new Controller(model);
            if (parseArguments.isOptPresent(SITE_OPTION)){
                Long long1 = parseArguments.getLongOptionValue(SITE_OPTION);
                model.setSiteNumber(long1.intValue());
            }

            if (showDefaultUI) {
                if (model.getFrameName().equals("ServerView")) {
                    ServerView serverView = new ServerView();
                    serverView.setModelController(model, controller);
                    
                    loginFrame.setVisible(false); // hide LoginFrame
                } else if (model.getFrameName().equals("TeamView")) {
                    TeamView teamView = new TeamView(model, controller);
                    teamView.setModelController(model, controller);
                    
                    loginFrame.setVisible(false); // hide LoginFrame
                } else if (model.getFrameName().equals("JudgeView")) {
                    JudgeView judgeView = new JudgeView(model, controller);
                    judgeView.setModelController(model, controller);
                    
                    loginFrame.setVisible(false); // hide LoginFrame
                } else {
                    throw new Exception("Could not find class to display " + model.getFrameName());
                }
            }

        } catch (TransportException transportException) {
            // TODO log this
            System.err.println("TransportException: " + transportException.getMessage());
            String message = "Unable to contact server, contact staff";
            System.err.println(message);

            if (showDefaultUI) {
                loginFrame.setStatusMessage(message);
            }

        } catch (SecurityException securityException) {
            // TODO log this
            System.err.println("SecurityException: " + securityException.getMessage());
            securityException.printStackTrace(System.err);
            if (showDefaultUI) {
                loginFrame.setStatusMessage(securityException.getMessage());
            }
        } catch (Exception e) {
            // TODO log this
            System.err.println("Trouble showing frame " + e.getMessage());
            e.printStackTrace(System.err);
            if (showDefaultUI) {
                loginFrame.setStatusMessage("Trouble logging in try again ");
            }
        }
    }
}
