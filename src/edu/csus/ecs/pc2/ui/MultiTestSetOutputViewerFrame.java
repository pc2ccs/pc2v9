package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

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
     * This method initializes
     * 
     */
    public MultiTestSetOutputViewerFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(800, 600));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setContentPane(getMultiTestSetOutputViewerPane());
        this.setTitle("Multiple Test Set Results");
        
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
    
    public static void main (String [] args) {
        MultiTestSetOutputViewerFrame mf = new MultiTestSetOutputViewerFrame();
        mf.setVisible(true);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
