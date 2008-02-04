package edu.csus.ecs.pc2;

import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Starter class.
 * 
 * The Starter class is the main driver for all PC<sup>2</sup> modules.
 * <P>
 * This class creates a contest data {@link edu.csus.ecs.pc2.core.model.IInternalContest}, then
 * a  controller {@link edu.csus.ecs.pc2.core.IInternalController}.   Then it passes the
 * command line arguments to {@link edu.csus.ecs.pc2.core.InternalController#start(String[])} and
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
        
        IInternalContest model = new InternalContest();
        InternalController controller = new InternalController (model);
        
        if (args.length > 0 && args[0].equals("--team1")){
            try {
                controller.setUsingMainUI(false);
                controller.start(args);
                @SuppressWarnings("unused") 
                IInternalContest contest = controller.clientLogin("t1", "");
//                System.out.println("Logged in as "+contest.getClientId()+" length = "+contest.getSites().length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            controller.start(args);
        }
    }
}
