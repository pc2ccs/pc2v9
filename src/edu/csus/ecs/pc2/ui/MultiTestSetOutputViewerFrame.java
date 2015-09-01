package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;

import java.awt.Dimension;

/**
 *  Multiple data set viewer Window.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: AutoJudgeStatusFrame.java 2849 2014-10-14 22:48:40Z boudreat $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/AutoJudgeStatusFrame.java $
public class MultiTestSetOutputViewerFrame extends javax.swing.JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1253631987477621456L;

    private MultiTestSetOutputViewerPane multiSetOutputViewerPane = null;

    /**
     * Constructor which initializes this Frame to contain a {@link #multiSetOutputViewerPane}.
     * Invokers are expected to call {@link #setContestAndController(IInternalContest, IInternalController)}
     * and then {@link #setData(Run, Problem, ProblemDataFiles)} prior to calling
     * {@link #setVisible(boolean)} on the frame.
     * 
     */
    public MultiTestSetOutputViewerFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this Frame to contain a {@link #multiSetOutputViewerPane}.
     * Invokers are expected to call {@link #setContestAndController(IInternalContest, IInternalController)}
     * and then {@link #setData(Run, Problem, ProblemDataFiles)} prior to calling
     * {@link #setVisible(boolean)} on the frame.
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(800, 400));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setContentPane(getMultiTestSetOutputViewerPane());
        this.setTitle("Test Results");
        this.setLocationRelativeTo(null);
        
        // TODO Bug 918

    }

    public MultiTestSetOutputViewerPane getMultiTestSetOutputViewerPane() {
        if (multiSetOutputViewerPane == null) {
            multiSetOutputViewerPane = new MultiTestSetOutputViewerPane();
        }
        return multiSetOutputViewerPane;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {

        getMultiTestSetOutputViewerPane().setContestAndController(inContest, inController);

    }

    public String getPluginTitle() {
        return "Multiple Test Set Output View Frame";
    }
    

    public void setData(Run run, RunFiles runFiles, Problem problem, ProblemDataFiles problemDataFiles) {

        getMultiTestSetOutputViewerPane().setData(run, runFiles, problem, problemDataFiles);
        setTitle ("Test Results for Run " + run.getNumber());
    }
    
    /**
     * Set new team output filenames.
     */
    public void setTeamOutputFileNames(String [] filenames){
        getMultiTestSetOutputViewerPane().setTeamOutputFileNames(filenames);
    }


} // @jve:decl-index=0:visual-constraint="10,10"
