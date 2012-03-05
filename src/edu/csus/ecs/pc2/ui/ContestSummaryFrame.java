package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;
import javax.swing.WindowConstants;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestSummaryFrame extends JFramePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 3012630412331154365L;

    private ContestSummaryPane contestSummaryPane = null;

    /**
     * This method initializes
     * 
     */
    public ContestSummaryFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(1024,760));
        this.setContentPane(getContestSummaryPane());
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        FrameUtilities.centerFrame(this);

    }

    @Override
    public String getPluginTitle() {
        return "Contest Summary Frame";
    }

    /**
     * This method initializes contestSummaryPane
     * 
     * @return edu.csus.ecs.pc2.ui.ContestSummaryPane
     */
    private ContestSummaryPane getContestSummaryPane() {
        if (contestSummaryPane == null) {
            contestSummaryPane = new ContestSummaryPane();
        }
        return contestSummaryPane;
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        getContestSummaryPane().setContestAndController(inContest, inController);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
