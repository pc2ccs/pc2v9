package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Submission Biff, shows XX Runs and XX Clars.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SubmissionBiffPane extends JPanePlugin {

    private static final long serialVersionUID = -1652565796449008642L;

    private JLabel runAndClarCountsLabel = null;

    private int unjudgedRunsCount = 0;

    private int unansweredClarificationsCount = 0;

    private Filter newSubmissionFilter = new Filter();

    /**
     * This method initializes
     * 
     */
    public SubmissionBiffPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        runAndClarCountsLabel = new JLabel();
        runAndClarCountsLabel.setText("XXX Runs YYY Clars");
        runAndClarCountsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        runAndClarCountsLabel.setForeground(java.awt.Color.red);
        runAndClarCountsLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 24));
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(504, 57));
        this.add(runAndClarCountsLabel, java.awt.BorderLayout.CENTER);

    }

    public void updateCountDisplay() {

        String updateString = "";
        if (unjudgedRunsCount == 1) {
            updateString = unjudgedRunsCount + " Run";
        } else if (unjudgedRunsCount > 1) {
            updateString = unjudgedRunsCount + " Runs";
        }

        if (unansweredClarificationsCount == 1) {
            updateString = updateString + "   " + unansweredClarificationsCount + " Clar";
        } else if (unansweredClarificationsCount > 1) {
            updateString = updateString + "   " + unansweredClarificationsCount + " Clars";
        }

        updateMessage(updateString);

    }

    private void updateMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                runAndClarCountsLabel.setText(string);
            }
        });
    }

    @Override
    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        getContest().addRunListener(new RunListenerImplementation());
        getContest().addClarificationListener(new ClarificationListenerImplementation());
        
        newSubmissionFilter.addClarificationState(ClarificationStates.NEW);
        newSubmissionFilter.addRunState(RunStates.NEW);
        
        unansweredClarificationsCount = getNewClarificationsCount();
        unjudgedRunsCount = getNewRunsCount();
        updateCountDisplay();

    }

    @Override
    public String getPluginTitle() {
        return "Submission Biff Pane";
    }

    int getNewClarificationsCount(){

        int counter = 0;

        Clarification[] clarifications = getContest().getClarifications();
        for (Clarification clarification : clarifications) {
            if (newSubmissionFilter.matches(clarification)) {
                counter++;
            }
        }
        
        return counter;
    }

    public void updateClarificationCounts(ClarificationEvent event) {
        unansweredClarificationsCount = getNewClarificationsCount();
        updateCountDisplay();
    }

    int getNewRunsCount(){
        int counter = 0;

        Run[] runs = getContest().getRuns();
        for (Run run : runs) {
            if (newSubmissionFilter.matches(run)) {
                counter++;
            }
        }
        return counter;
    }

    public void updateRunCount(RunEvent event) {
        unjudgedRunsCount = getNewRunsCount();
        updateCountDisplay();
    }

    /**
     * Run Event Listener.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            updateRunCount(event);
        }

        public void runChanged(RunEvent event) {
            updateRunCount(event);
        }

        public void runRemoved(RunEvent event) {
            updateRunCount(event);
        }

    }

    /**
     * Clarification Event Listener.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class ClarificationListenerImplementation implements IClarificationListener {

        public void clarificationAdded(ClarificationEvent event) {
            updateClarificationCounts(event);
        }

        public void clarificationChanged(ClarificationEvent event) {
            updateClarificationCounts(event);
        }

        public void clarificationRemoved(ClarificationEvent event) {
            updateClarificationCounts(event);
        }

    }
    
    public void setFontSize (int pointSize){
        runAndClarCountsLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, pointSize));
    }

}

