package edu.csus.ecs.pc2;

import edu.csus.ecs.pc2.core.Controller;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.ui.LoginFrame;
import edu.csus.ecs.pc2.ui.ServerView;
import edu.csus.ecs.pc2.ui.TeamView;

/**
 * Starter class.
 * 
 * This class mimics the main driver for a contest. It instantiates a {@link edu.csus.ecs.pc2.core.model.Model} object which is an
 * abstraction for the complete set of contest data.
 * <P>
 * The {@link edu.csus.ecs.pc2.core.model.Model} implements two interfaces, {@link edu.csus.ecs.pc2.IObservable} and
 * {@link edu.csus.ecs.pc2.core.model.IModel}. {@link edu.csus.ecs.pc2.IObservable} specifies methods for an external class to
 * register as an observer of the model. {@link edu.csus.ecs.pc2.core.model.IModel} represents the public interface into the contest
 * data.
 * <P>
 * 
 * Next the Starter instantiates a {@link edu.csus.ecs.pc2.core.Controller} object, which is an abstraction for all the functions
 * performed by the contest control engine (which might be comprised of several modules). The
 * {@link edu.csus.ecs.pc2.core.Controller} receives the previously constructed model as a parameter so that it can invoke methods
 * to manipulate data in the model.
 * <P>
 * 
 * The {@link edu.csus.ecs.pc2.core.Controller} implements interface {@link edu.csus.ecs.pc2.core.IController} which represents
 * arbitrary functions provided by the contest engine.
 * 
 * <P>
 * Finally, the Starter instantiates a {@link edu.csus.ecs.pc2.ui.TeamView} object, which is an abstraction for GUIs which
 * reference contest data. The {@link edu.csus.ecs.pc2.ui.TeamView} is passed the model and the controller.
 * <P>
 * 
 * The {@link edu.csus.ecs.pc2.ui.TeamView} object implements interface {@link edu.csus.ecs.pc2.IObserver} and registers itself as
 * an observer of the model. Changes to the model invoked through the {@link edu.csus.ecs.pc2.core.model.IModel} interface cause the
 * model to invoke the update method in the {@link edu.csus.ecs.pc2.ui.TeamView} observer.
 * <P>
 * 
 * The {@link edu.csus.ecs.pc2.ui.TeamView} object gets input from the user and invokes functions in the controller interface,
 * similar to the way a contest GUI obtains input from the user and invokes contest engine functions. The controller responds to
 * requests from the view by invoking functions to update the model through the {@link edu.csus.ecs.pc2.core.model.IModel}
 * interface, similar to the way functions in the contest engine make changes in the contest data. Changes to the model in turn
 * invoke callbacks to the {@link edu.csus.ecs.pc2.ui.TeamView} observer, similar to the way that changes in contest data update
 * contest GUIs.
 * 
 * @author pc2@ecs.csus.edu
 */

// TODO write code for a command line login
// $HeadURL$
public class Starter implements Runnable {

    public static final String SVN_ID = "$Id$";

    private LoginFrame loginFrame = new LoginFrame();

    protected Starter() {
        // this constructor required by CheckStyle.

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

        Starter starter = new Starter();
        
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
     * @param id - login name
     * @param password - login password
     * @param showDefaultUI assume LoginFrame is used and that UI is presented to user.
     */
    public void login(String id, String password, boolean showDefaultUI) {

        try {
            IModel model = Controller.login(id, password);
            IController controller = new Controller(model);

            if (showDefaultUI) {
                if (model.getFrameName().equals("ServerView")) {
                    new ServerView(model, controller);
                    loginFrame.setVisible(false); // hide LoginFrame
                } else if (model.getFrameName().equals("TeamView")) {
                    new TeamView(model, controller);
                    loginFrame.setVisible(false); // hide LoginFrame
                } else {
                    throw new Exception("Could not find class to display " + model.getFrameName());
                }
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
