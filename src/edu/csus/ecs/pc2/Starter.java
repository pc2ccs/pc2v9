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
 * This class creates a contest data {@link edu.csus.ecs.pc2.core.model.IModel}, then
 * a  controller {@link edu.csus.ecs.pc2.core.IController}.   Then it passes the
 * command line arguments to {@link edu.csus.ecs.pc2.core.Controller#start(String[])} and
 * that starts a Login Frame. 
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public final class Starter  {

    public static final String SVN_ID = "$Id$";
    
    private Starter(){
        // constructor per checkstyle suggestion.
    }

    /**
     * Start a contest module.
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        IModel model = new Model();
        IController controller = new Controller (model);
        controller.start(args);
    }
}
