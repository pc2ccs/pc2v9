package edu.csus.ecs.pc2;

import edu.csus.ecs.pc2.core.Controller;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Model;

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
public class Starter  {

    public static final String SVN_ID = "$Id$";

    /**
     * Show version info and start Login window.
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        IModel model = new Model();
        IController controller = new Controller (model);
        controller.start(args);
    }
}
