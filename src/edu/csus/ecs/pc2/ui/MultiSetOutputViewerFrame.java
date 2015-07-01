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
public class MultiSetOutputViewerFrame extends javax.swing.JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1253631987477621456L;

    private MultiSetOutputViewerPane multiSetOutputViewerPane = null;

    /**
     * This method initializes
     * 
     */
    public MultiSetOutputViewerFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(519, 251));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setContentPane(getMultiSetOutputViewerPane());
        this.setTitle("Multiple  Status");
        
        // TODO Bug 918

    }

    public MultiSetOutputViewerPane getMultiSetOutputViewerPane() {
        if (multiSetOutputViewerPane == null) {
            multiSetOutputViewerPane = new MultiSetOutputViewerPane();
        }
        return multiSetOutputViewerPane;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {

        getMultiSetOutputViewerPane().setContestAndController(inContest, inController);

    }

    public String getPluginTitle() {
        return "Multi Set Output View Frame";
    }

} // @jve:decl-index=0:visual-constraint="10,10"
