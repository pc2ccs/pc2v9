package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Base class for most UIPlugin panes.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public abstract class JPanePlugin extends JPanel implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 8555136838892968753L;

    private IInternalController controller;

    private IInternalContest contest;
    
    private JFrame parentFrame = null;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.controller = inController;
        this.contest = inContest;
    }
    

    public abstract String getPluginTitle();

    public IInternalController getController() {
        return controller;
    }

    public void setController(IInternalController controller) {
        this.controller = controller;
    }

    public IInternalContest getContest() {
        return contest;
    }

    public void setContest(IInternalContest contest) {
        this.contest = contest;
    }


    public JFrame getParentFrame() {
        return parentFrame;
    }


    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
}
