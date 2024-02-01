// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.CategoryEvent;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.ICategoryListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Submit Clarification Pane.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SubmitClarificationPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6395977089692171705L;

    private Log log;  //  @jve:decl-index=0:

    private JPanel problemPane = null;

    private JComboBox<Problem> problemComboBox = null;

    private JPanel questionPane = null;

    private JTextArea questionTextArea = null;

    private JButton submitClarificationButton = null;
    
    private JCheckBox submitAnnouncement = null;
    
    private JPanel answerPane = null;
    
    private JScrollPane answerScrollPane = null;
    
    private JTextArea answerTextArea = null;
    
    private boolean isJudge = false;
    

    /**
     * This method initializes
     * 
     */
    public SubmitClarificationPane() {      
        super();
//        this.isJudge = isJudge;
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    protected void initialize() {
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
    protected JPanel getProblemPane() {
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
    private JComboBox<Problem> getProblemComboBox() {
        if (problemComboBox == null) {
            problemComboBox = new JComboBox<Problem>();
        }
        return problemComboBox;
    }
    protected JCheckBox getsubmitAnnouncement() {
        if (submitAnnouncement == null) {
            submitAnnouncement = new JCheckBox();
            submitAnnouncement.setText("Generate Announcement");
            submitAnnouncement.setBounds(19, 80, 170, 20);
            submitAnnouncement.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        getQuestionPane().setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                        getSubmitClarificationButton().setText("Submit Announcement");
                        getSubmitClarificationButton().setToolTipText("Click this button to submit your Announcement");
                    } else {
                        getQuestionPane().setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Question", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                        getSubmitClarificationButton().setText("Submit Clarification");
                        getSubmitClarificationButton().setToolTipText("Click this button to submit your Clarification");
                    }
                    
                }
            });
        }
        return submitAnnouncement;
    }

    /**
     * This method initializes questionPane
     * 
     * @return javax.swing.JPanel
     */
    protected JPanel getQuestionPane() {
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
    protected JButton getSubmitClarificationButton() {
        if (submitClarificationButton == null) {
            submitClarificationButton = new JButton();
            submitClarificationButton.setText("Submit Clarification");
            submitClarificationButton.setPreferredSize(new Dimension(200, 26));
            submitClarificationButton.setLocation(new Point(20, 219));
            submitClarificationButton.setSize(new Dimension(200, 34));
            submitClarificationButton.setToolTipText("Click this button to submit your Clarification");
            submitClarificationButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    submit();
                    
                }
            });
        }
        return submitClarificationButton;
    }
    

    private void reloadProblems(){
        
        getProblemComboBox().removeAllItems();
        Problem problemN = new Problem("Select Problem");
        getProblemComboBox().addItem(problemN);

        if (getContest().getCategories().length > 0) {
            for (Problem problem : getContest().getCategories()) {
                if (problem.isActive()) {
                    getProblemComboBox().addItem(problem);
                }
            }
        }
        
        for (Problem problem : getContest().getProblems()) {
            if (problem.isActive()){
                getProblemComboBox().addItem(problem);
            }
        }

    }
    private void populateGUI() {

        reloadProblems();
        setButtonsActive(getContest().getContestTime().isContestRunning());
    }

    /**
     * Enable or disable submission buttons, Question pane and Problem drop-down list.
     * 
     * @param turnButtonsOn
     *            if true, buttons enabled.
     */
    private void setButtonsActive(final boolean turnButtonsOn) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getProblemComboBox().setEnabled(turnButtonsOn);
                getQuestionTextArea().setEnabled(turnButtonsOn);
                getSubmitClarificationButton().setEnabled(turnButtonsOn);
            }
        });
        FrameUtilities.regularCursor(this);
    }
    
    protected void submit() {

        Problem problem = ((Problem) getProblemComboBox().getSelectedItem());

        if (getProblemComboBox().getSelectedIndex() < 1) {
            showMessage("Please select problem");
            return;
        }
        if (getsubmitAnnouncement().isSelected()) {
            submitAnnouncement(problem);
        }
        else {
            submitClarification(problem);
        }
        

    }
    
    protected void submitAnnouncement(Problem problem) {
        String answerAnnouncement = questionTextArea.getText().trim();

        if (answerAnnouncement.length() < 1) {
            showMessage("Please enter a answer for announcement");
            return;
        }
        String confirmAnswer = "<HTML><FONT SIZE=+1>Do you wish to submit a announcement clarification for<BR><BR>" + "Problem:  <FONT COLOR=BLUE>" + Utilities.forHTML(problem.toString()) + "</FONT><BR><BR>"
                + "Announcement: <FONT COLOR=BLUE>" + Utilities.forHTML(answerAnnouncement)
                + "</FONT><BR><BR></FONT>";
        
        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), confirmAnswer, "Submit Clarification Confirm");

        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            log.info("submit announcement clarification for " + problem + " " + confirmAnswer);
            
          
            ElementId elementId = getController().submitClarification(problem, "");
            
            Clarification clarificationToAnswer = getContest().getClarification(elementId);
            getController().checkOutClarification(clarificationToAnswer, false);
            
            ClarificationAnswer clarificationAnswer = new ClarificationAnswer(answerAnnouncement, getContest().getClientId(), 
                    true, getContest().getContestTime()); //getSendToAllCheckBox().isSelected() true here is to check if needs to be sent everyone
            clarificationToAnswer.addAnswer(clarificationAnswer);
            getController().submitClarificationAnswer(clarificationToAnswer);
            questionTextArea.setText("");

        } catch (Exception e) {
            // TODO need to make this cleaner
            showMessage("Error sending announcement clar, contact staff");
            log.log(Log.SEVERE, "Exception sending clarification ", e);
        }
    }
    
    protected void submitClarification(Problem problem) {
        String question = questionTextArea.getText().trim();

        if (question.length() < 1) {
            showMessage("Please enter a question");
            return;
        }
        
        String confirmQuestion = "<HTML><FONT SIZE=+1>Do you wish to submit a clarification for<BR><BR>" + "Problem:  <FONT COLOR=BLUE>" + Utilities.forHTML(problem.toString()) + "</FONT><BR><BR>"
        + "Question: <FONT COLOR=BLUE>" + Utilities.forHTML(question)
        + "</FONT><BR><BR></FONT>";
        
        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), confirmQuestion, "Submit Clarification Confirm");

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
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
        }

        public void contestTimeChanged(ContestTimeEvent event) {
        }

        public void contestStarted(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestStopped(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void refreshAll(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
            
        }
        
        /** This method exists to support differentiation between manual and automatic starts,
         * in the event this is desired in the future.
         * Currently it just delegates the handling to the contestStarted() method.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
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
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }

        public void problemRefreshAll(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    private class CategoryListenerImplementation implements ICategoryListener {

        public void categoryAdded(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }

        public void categoryChanged(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }

        public void categoryRemoved(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }

        public void categoryRefreshAll(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
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
        
        initializePermissions();
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addContestTimeListener(new ContestTimeListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addCategoryListener(new CategoryListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
//                setVisible(true);
            }
        });
        
        
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignore, doesn't affect this pane
        }

        public void accountModified(AccountEvent event) {
            // check if is this account
            Account account = event.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });
            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore, does not affect this pane
        }

        public void accountsModified(AccountEvent accountEvent) {
            // check if it included this account
            boolean theyModifiedUs = false;
            for (Account account : accountEvent.getAccounts()) {
                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    theyModifiedUs = true;
                    initializePermissions();
                }
            }
            final boolean finalTheyModifiedUs = theyModifiedUs;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (finalTheyModifiedUs) {
                        updateGUIperPermissions();
                    }
                }
            });
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                }
            });
        }
    }
    
    private void updateGUIperPermissions() {
        submitClarificationButton.setVisible(isAllowed(Permission.Type.SUBMIT_CLARIFICATION));
    }


} // @jve:decl-index=0:visual-constraint="10,10"
