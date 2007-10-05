package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * Submission Biff Frame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class SubmissionBiffFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1330276750746647066L;

    private IContest contest;

    private IController controller;

    private SubmissionBiffPane submissionBiffPane = null;

    /**
     * This method initializes
     * 
     */
    public SubmissionBiffFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(505,159));
        this.setContentPane(getSubmissionBiffPane());
        this.setTitle("Unjudged Submissions");

    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;

        submissionBiffPane.setContestAndController(contest, controller);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setTitle("PC^2 Unjudged Submission Count Judge " + contest.getTitle());
            }
        });

    }

    public String getPluginTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * This method initializes submissionBiffPane
     * 
     * @return edu.csus.ecs.pc2.ui.SubmissionBiffPane
     */
    private SubmissionBiffPane getSubmissionBiffPane() {
        if (submissionBiffPane == null) {
            submissionBiffPane = new SubmissionBiffPane();
        }
        return submissionBiffPane;
    }

    public void setFontSize (int pointSize){
        getSubmissionBiffPane().setFontSize(pointSize);
    }


} // @jve:decl-index=0:visual-constraint="10,10"
