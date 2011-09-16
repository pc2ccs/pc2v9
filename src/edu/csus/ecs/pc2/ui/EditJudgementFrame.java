package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import java.awt.Dimension;


/**
 * Edit Judgement Frame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditJudgementFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -6248957592340866836L;

    private IInternalContest contest;

    private IInternalController controller;

    private EditJudgementPane judgementPane = null;

    /**
     * This method initializes
     * 
     */
    public EditJudgementFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(431, 176));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getJudgementPane());
        this.setTitle("Edit Judgement");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getJudgementPane().setParentFrame(this);
        getJudgementPane().setContestAndController(contest, controller);

    }

    public void setJudgement(Judgement judgement) {
        if (judgement == null) {
            setTitle("Add New Judgement");
        } else {
            setTitle("Edit Judgement " + judgement.getDisplayName());
        }
        getJudgementPane().setJudgement(judgement);
    }

    public String getPluginTitle() {
        return "Edit Judgement Frame";
    }

    /**
     * This method initializes judgementPane
     * 
     * @return edu.csus.ecs.pc2.ui.JudgementPane
     */
    private EditJudgementPane getJudgementPane() {
        if (judgementPane == null) {
            judgementPane = new EditJudgementPane();
        }
        return judgementPane;
    }
    
    public void setDeleteCheckBoxEnabled(boolean enabled) {
        getJudgementPane().setDeleteCheckBoxEnabled(enabled);
    }

}  //  @jve:decl-index=0:visual-constraint="30,50"
