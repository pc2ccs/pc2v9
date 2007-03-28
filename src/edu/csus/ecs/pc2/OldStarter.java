package edu.csus.ecs.pc2;

import edu.csus.ecs.pc2.core.Controller;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.transport.QuickTransport;
import edu.csus.ecs.pc2.core.transport.StaticTransport;
import edu.csus.ecs.pc2.server.ui.ServerView;
import edu.csus.ecs.pc2.team.ui.TeamView;


/**
 * Starter class.
 * 
 * This class mimics the main driver for a contest. It instantiates a {@link edu.csus.ecs.pc2.team.TeamModel} object which is an abstraction for the
 * complete set of contest data.
 * <P>
 * The {@link edu.csus.ecs.pc2.team.TeamModel} implements two interfaces, {@link edu.csus.ecs.pc2.IObservable} and {@link edu.csus.ecs.pc2.core.model.IModel}.
 * {@link edu.csus.ecs.pc2.IObservable} specifies methods for an external class to register as an observer of the model. {@link edu.csus.ecs.pc2.core.model.IModel}
 * represents the public interface into the contest data.
 * <P>
 * 
 * Next the Starter instantiates a {@link edu.csus.ecs.pc2.core.Controller} object, which is an abstraction for all the functions performed by the
 * contest control engine (which might be comprised of several modules). The {@link edu.csus.ecs.pc2.core.Controller} receives the previously
 * constructed model as a parameter so that it can invoke methods to manipulate data in the model.
 * <P>
 * 
 * The {@link edu.csus.ecs.pc2.core.Controller} implements interface {@link edu.csus.ecs.pc2.core.IController} which represents arbitrary functions provided by
 * the contest engine.
 * 
 * <P>
 * Finally, the Starter instantiates a {@link edu.csus.ecs.pc2.team.TeamView} object, which is an abstraction for GUIs which reference contest data.
 * The {@link edu.csus.ecs.pc2.team.TeamView} is passed the model and the controller.
 * <P>
 * 
 * The {@link edu.csus.ecs.pc2.team.TeamView} object implements interface {@link edu.csus.ecs.pc2.IObserver} and registers itself as an observer of the model.
 * Changes to the model invoked through the {@link edu.csus.ecs.pc2.core.model.IModel} interface cause the model to invoke the update method in the
 * {@link edu.csus.ecs.pc2.team.TeamView} observer.
 * <P>
 * 
 * The {@link edu.csus.ecs.pc2.team.TeamView} object gets input from the user and invokes functions in the controller interface, similar to the way a
 * contest GUI obtains input from the user and invokes contest engine functions. The controller responds to requests from the view
 * by invoking functions to update the model through the {@link edu.csus.ecs.pc2.core.model.IModel} interface, similar to the way functions in the
 * contest engine make changes in the contest data. Changes to the model in turn invoke callbacks to the {@link edu.csus.ecs.pc2.team.TeamView}
 * observer, similar to the way that changes in contest data update contest GUIs.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class OldStarter {

    public static final String SVN_ID = "$Id$";

    protected OldStarter() {
        // this constructor required by CheckStyle.

    }
    
    public static void main(String[] args) {

        VersionInfo versionInfo = new VersionInfo();
        System.out.println(versionInfo.getSystemVersionInfo());
        System.out.println();
       
//        LoginFrame loginFrame = new LoginFrame().setVisible(true);
//        String id = loginFrame.getId();
//        String pwd = loginFrame.getPassword();
//       
        
        String id = "site1";
        String password = "site1";
        
        IModel serverModel  = Controller.login(id,password);
        IController serverController = new Controller(serverModel);

//        String guiClassName = getProperty (id);
        // TODO START class named guiClassName
        
        IModel teamModel = Controller.login(id,password);
        IController teamController = new Controller(teamModel);

        // Create static transport server to client, client to server.

        StaticTransport.setQuickTransport(new QuickTransport(serverController, teamController));

        TeamView viewWindow = new TeamView(teamModel, teamController);
        System.out.println("Started TeamView class ");
        
        ServerView serverView = new ServerView(serverModel, serverController);
        System.out.println("Started ServerView class ");

        // Put server window to right of TeamView window
        serverView.windowToRight(viewWindow);

    }
}
