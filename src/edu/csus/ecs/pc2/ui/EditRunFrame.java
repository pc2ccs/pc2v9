package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
// $Id$

public class EditRunFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6716035052687410328L;

    private IInternalContest contest;

    private IInternalController controller;

    private RunPane runPane = null;
    
    private Run run = null;

    /**
     * This method initializes
     * 
     */
    public EditRunFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549,329));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getRunPane());
        this.setTitle("New Run");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                getRunPane().handleCancelButton();
            }
        });
        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getRunPane().setContestAndController(contest, controller);
        getRunPane().setParentFrame(this);
        
        contest.addRunListener(new RunListenerImplementation());
    }

    public void setRun(Run theRun) {
        if (theRun == null) {
            setTitle("Add New Run");
        } else {
            setTitle("Edit Run " + theRun.getNumber() + " (Site " + theRun.getSiteNumber() + ")");
            run = theRun;
            controller.checkOutRun(theRun, true, false);
        }
        getRunPane().setRun(theRun);
    }

    public String getPluginTitle() {
        return "Edit Run Frame";
    }

    /**
     * This method initializes runPane
     * 
     * @return edu.csus.ecs.pc2.ui.RunPane
     */
    private RunPane getRunPane() {
        if (runPane == null) {
            runPane = new RunPane();
        }
        return runPane;
    }
    

    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */

    // $HeadURL$
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            // ignore
        }

        public void runChanged(RunEvent event) {
            if (run != null) {
                if (event.getRun().getElementId().equals(run.getElementId())) {

                    if (event.getAction().equals(Action.RUN_NOT_AVAILABLE)) {
                        getRunPane().showMessage("Run " + run.getNumber() + " not available ");
                    } else {
                        getRunPane().setRunAndFiles(event.getRun(), event.getRunFiles());
                    }
                }
            }
        }

        public void runRemoved(RunEvent event) {
            // TODO Auto-generated method stub
        }
    }    

} // @jve:decl-index=0:visual-constraint="10,10"
