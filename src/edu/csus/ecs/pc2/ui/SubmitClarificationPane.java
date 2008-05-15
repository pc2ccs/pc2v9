package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;

/**
 * Submit Clarification Pane.
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class SubmitClarificationPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6395977089692171705L;

    private Log log;

    private JPanel problemPane = null;

    private JComboBox problemComboBox = null;

    private JPanel questionPane = null;

    private JTextArea questionTextArea = null;

    private JButton submitClarificationButton = null;

    /**
     * This method initializes
     * 
     */
    public SubmitClarificationPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(null);
        this.setSize(new java.awt.Dimension(456, 285));
        this.add(getProblemPane(), null);
        this.add(getQuestionPane(), null);

        this.add(getSubmitClarificationButton(), null);
    }


    @Override
    public String getPluginTitle() {
        return "Submit Clarifications Pane";
    }

    /**
     * This method initializes problemPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProblemPane() {
        if (problemPane == null) {
            problemPane = new JPanel();
            problemPane.setLayout(new BorderLayout());
            problemPane.setBounds(new java.awt.Rectangle(19, 13, 336, 54));
            problemPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Problem", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            problemPane.add(getProblemComboBox(), java.awt.BorderLayout.CENTER);
        }
        return problemPane;
    }

    /**
     * This method initializes problemComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProblemComboBox() {
        if (problemComboBox == null) {
            problemComboBox = new JComboBox();
        }
        return problemComboBox;
    }

    /**
     * This method initializes questionPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getQuestionPane() {
        if (questionPane == null) {
            questionPane = new JPanel();
            questionPane.setLayout(new BorderLayout());
            questionPane.setBounds(new java.awt.Rectangle(19,80,406,125));
            questionPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Question", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            questionPane.add(getQuestionTextArea(), java.awt.BorderLayout.CENTER);
        }
        return questionPane;
    }

    /**
     * This method initializes questionTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getQuestionTextArea() {
        if (questionTextArea == null) {
            questionTextArea = new JTextArea();
        }
        return questionTextArea;
    }

    /**
     * This method initializes submitClarificationButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSubmitClarificationButton() {
        if (submitClarificationButton == null) {
            submitClarificationButton = new JButton();
            submitClarificationButton.setBounds(new java.awt.Rectangle(20,219,158,34));
            submitClarificationButton.setText("Submit Clarification");
            submitClarificationButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    submitClarification();
                }
            });
        }
        return submitClarificationButton;
    }
    private void reloadProblems(){
        
        getProblemComboBox().removeAllItems();
        Problem problemN = new Problem("Select Problem");
        getProblemComboBox().addItem(problemN);

        getProblemComboBox().addItem(getContest().getGeneralProblem());
        
        for (Problem problem : getContest().getProblems()) {
            getProblemComboBox().addItem(problem);
        }

    }
    private void populateGUI() {

        reloadProblems();
        setButtonsActive(getContest().getContestTime().isContestRunning());
    }

    /**
     * Enable or disable submission buttons.
     * 
     * @param turnButtonsOn
     *            if true, buttons enabled.
     */
    private void setButtonsActive(final boolean turnButtonsOn) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getSubmitClarificationButton().setEnabled(turnButtonsOn);
            }
        });
        FrameUtilities.regularCursor(this);
    }
    
    protected void submitClarification() {

        Problem problem = ((Problem) getProblemComboBox().getSelectedItem());

        if (getProblemComboBox().getSelectedIndex() < 1) {
            showMessage("Please select problem");
            return;
        }

        String question = questionTextArea.getText().trim();

        if (question.length() < 1) {
            showMessage("Please enter a question");
            return;
        }

        // TODO replace with clarification confirmation 
        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Submit Clarification?", "Submit Clarification Confirm");

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            log.info("submit clarification for " + problem + " " + question);
            getController().submitClarification(problem, question);
            questionTextArea.setText("");

        } catch (Exception e) {
            // TODO need to make this cleaner
            showMessage("Error sending clar, contact staff");
            log.log(Log.SEVERE, "Exception sending clarification ", e);
        }

    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            log.info("debug ContestTime site " + event.getSiteNumber() + " ADDED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            log.info("debug ContestTime site " + event.getSiteNumber() + " REMOVED ");
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            log.info("debug ContestTime site " + event.getSiteNumber() + " CHANGED ");
        }

        public void contestStarted(ContestTimeEvent event) {
            log.info("debug ContestTime site " + event.getSiteNumber() + " STARTED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestStopped(ContestTimeEvent event) {
            log.info("debug ContestTime site " + event.getSiteNumber() + " STOPPED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getProblemComboBox().addItem(event.getProblem());
                }
            });
        }

        public void problemChanged(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int selectedIndex = getProblemComboBox().getSelectedIndex();
                    reloadProblems();
                    if (selectedIndex > -1) {
                        getProblemComboBox().setSelectedIndex(selectedIndex);
                    }
                }
            });
        }

        public void problemRemoved(ProblemEvent event) {
            log.info("debug Problem REMOVED  " + event.getProblem());
        }
    }

    
    private boolean isThisSite(int siteNumber) {
        return siteNumber == getContest().getSiteNumber();
    }
    
    private void showMessage(String string) {
        JOptionPane.showMessageDialog(this, string);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        this.log = getController().getLog();
        
        getContest().addContestTimeListener(new ContestTimeListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
                setVisible(true);
            }
        });
        
        
    }

} // @jve:decl-index=0:visual-constraint="10,10"
