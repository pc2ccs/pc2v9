package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;
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
public class SelectJudgementFrame extends JFrame implements UIPlugin {


    /**
     * 
     */
    private static final long serialVersionUID = -3349295529036840178L;

    private IModel model;

    private IController controller;

    private Run run = null;

    private SelectJudgementPane selectJudgementPane = null;

    /**
     * This method initializes
     * 
     */
    public SelectJudgementFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549, 312));
        this.setContentPane(getSelectJudgementPane());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Select Run Judgement");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                getSelectJudgementPane().handleCancelButton();
            }
        });
        FrameUtilities.centerFrame(this);

    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;

        getSelectJudgementPane().setModelAndController(model, controller);
        getSelectJudgementPane().setParentFrame(this);

        model.addRunListener(new RunListenerImplementation());
    }

    public void setRun(Run theRun) {
        if (theRun == null) {
            setTitle("Run not loaded");
        } else {
            setTitle("Select Judgement for run " + theRun.getNumber() + " (Site " + theRun.getSiteNumber() + ")");
            run = theRun;
            controller.checkOutRun(theRun, false);
        }
        getSelectJudgementPane().setRun(theRun);
    }

    public String getPluginTitle() {
        return "Edit Run Frame";
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

                    if (event.getAction().equals(Action.RUN_NOT_AVIALABLE)) {
                        getSelectJudgementPane().showMessage("Run " + run.getNumber() + " not available ");
                    } else {
                        getSelectJudgementPane().setRunAndFiles(event.getRun(), event.getRunFiles());
                    }
                }
            }
        }

        public void runRemoved(RunEvent event) {
            // TODO Auto-generated method stub
        }
    }

    /**
     * This method initializes selectJudgementPane
     * 
     * @return edu.csus.ecs.pc2.ui.SelectJudgementPane
     */
    private SelectJudgementPane getSelectJudgementPane() {
        if (selectJudgementPane == null) {
            selectJudgementPane = new SelectJudgementPane();
        }
        return selectJudgementPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
