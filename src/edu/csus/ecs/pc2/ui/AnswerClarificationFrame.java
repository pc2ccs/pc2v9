package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.ClarificationEvent.Action;

/**
 * Answer Clar Frame.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AnswerClarificationFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3349295529036840178L;

    private IInternalContest contest;

    private IInternalController controller;

    private Clarification clarification = null;

    private AnswerClarificationPane answerClarificationPane = null;

    /**
     * This method initializes
     * 
     */
    public AnswerClarificationFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549, 312));
        this.setContentPane(getAnswerClarificationPane());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Select Clarification Judgement");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                getAnswerClarificationPane().handleCancelButton();
            }
        });
        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getAnswerClarificationPane().setContestAndController(contest, controller);
        getAnswerClarificationPane().setParentFrame(this);
        getAnswerClarificationPane().setDefaultAnswerText(contest.getContestInformation().getJudgesDefaultAnswer());

        contest.addContestInformationListener(new ContestInformationListenerImplementation());
        contest.addClarificationListener(new ClarificationListenerImplementation());
    }

    public void setClarification(Clarification theClarification) {
        if (theClarification == null) {
            setTitle("Clarification not loaded");
        } else {
            getAnswerClarificationPane().setClarification(theClarification, false);
            setTitle("Select Answer for clarification " + theClarification.getNumber() + " (Site " + theClarification.getSiteNumber() + ")");
            clarification = theClarification;
            controller.checkOutClarification(clarification, false);
        }
    }

    public String getPluginTitle() {
        return "Edit Clarification Frame";
    }

    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */

    // $HeadURL$
    public class ClarificationListenerImplementation implements IClarificationListener {

        public void clarificationAdded(ClarificationEvent event) {
            // ignore
        }
        
        public void refreshClarfications(ClarificationEvent event) {
            // ignore
            // TODO dispose of this frame, uncheck out clars
        }

        public void clarificationChanged(ClarificationEvent event) {
            if (clarification != null) {
                if (event.getClarification().getElementId().equals(clarification.getElementId())) {

                    if (event.getAction().equals(Action.CLARIFICATION_NOT_AVAILABLE)) {
                        getAnswerClarificationPane().showMessage("Clarification " + clarification.getNumber() + " not available ");
                        getAnswerClarificationPane().enableUpdateButtons(false);
                        getAnswerClarificationPane().regularCursor();
                    } else {
                        if (event.getSentToClientId() != null && event.getSentToClientId().equals(contest.getClientId())) {
                            getAnswerClarificationPane().setClarification(event.getClarification(), true);
                            clarification = null;
                        }
                    }
                }
            }
        }

        public void clarificationRemoved(ClarificationEvent event) {
            // TODO Auto-generated method stub
        }
    }

    /**
     * Is responsible for updating the default answer field on the AnswerClarificationPane.
     * Note the Pane is responsible for updating it UI.
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    public class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            // TODO Auto-generated method stub
            
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            // TODO Auto-generated method stub
            if (event.getContestInformation() != null) {
                String answer = event.getContestInformation().getJudgesDefaultAnswer();
                getAnswerClarificationPane().setDefaultAnswerText(answer);
            }
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub
            
        }
        
    }
    /**
     * This method initializes answerClarificationPane
     * 
     * @return edu.csus.ecs.pc2.ui.AnswerClarificationPane
     */
    private AnswerClarificationPane getAnswerClarificationPane() {
        if (answerClarificationPane == null) {
            answerClarificationPane = new AnswerClarificationPane();
        }
        return answerClarificationPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
