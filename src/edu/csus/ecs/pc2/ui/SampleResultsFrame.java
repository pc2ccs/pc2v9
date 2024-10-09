// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.list.SubmissionSample;

/**
 *  Judge's sample results details
 *
 * @author John Buck
 */

public class SampleResultsFrame extends javax.swing.JFrame implements UIPlugin {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private SampleResultsPane multiSetOutputViewerPane = null;

    /**
     * Constructor which initializes this Frame to contain a {@link #multiSetOutputViewerPane}.
     * Invokers are expected to call {@link #setContestAndController(IInternalContest, IInternalController)}
     * and then {@link #setData(Run, RunFiles, Problem, ProblemDataFiles)} prior to calling
     * {@link #setVisible(boolean)} on the frame.
     *
     */
    public SampleResultsFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this Frame to contain a {@link #multiSetOutputViewerPane}.
     * Invokers are expected to call {@link #setContestAndController(IInternalContest, IInternalController)}
     * and then {@link #setData(SubmissionSample)} prior to calling
     * {@link #setVisible(boolean)} on the frame.
     *
     */
    private void initialize() {
        this.setSize(new Dimension(860, 400));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setContentPane(getMultiTestSetOutputViewerPane());
        this.setTitle("Sample Results");
        this.setLocationRelativeTo(null);
    }

    public SampleResultsPane getMultiTestSetOutputViewerPane() {
        if (multiSetOutputViewerPane == null) {
            multiSetOutputViewerPane = new SampleResultsPane();
        }
        return multiSetOutputViewerPane;
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {

        getMultiTestSetOutputViewerPane().setContestAndController(inContest, inController);

    }

    @Override
    public String getPluginTitle() {
        return "Judge's Sample Submission Frame";
    }


    public void setData(SubmissionSample sub) {
        getMultiTestSetOutputViewerPane().setData(sub);
        setTitle ("Sample Results for Run " + sub.getRun().getNumber());
    }

    public void clearData()
    {
        getMultiTestSetOutputViewerPane().resetResultsTable();
    }

} // @jve:decl-index=0:visual-constraint="10,10"
