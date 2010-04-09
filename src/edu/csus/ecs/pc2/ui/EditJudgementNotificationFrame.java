package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.JudgementNotification;
import edu.csus.ecs.pc2.core.model.NotificationSetting;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;

/**
 * Edit Judgement Notifications for all problems.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditJudgementNotificationFrame extends JFrame implements UIPlugin, IEditChangeCallback {

    /**
     * 
     */
    private static final long serialVersionUID = -2280462320664391007L;

    private JPanel mainPanel = null;

    private JPanel buttonPane = null;

    private JButton updateButton = null;

    private JTabbedPane problemTabbedPane = null;

    private JButton closeButton = null;

    private IInternalController controller;

    private IInternalContest contest;  //  @jve:decl-index=0:

    private Log log = null;

    private JLabel remainingTimeLabel = null;

    /**
     * This method initializes
     * 
     */
    public EditJudgementNotificationFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(750, 349));
        this.setTitle("End of Contest Notification Control");
        this.setContentPane(getMainPanel());

        FrameUtilities.centerFrame(this);
    }

    public String getPluginTitle() {
        return "Edit Judgement Notifications Frame";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {

        this.contest = inContest;
        this.controller = inController;

        log = controller.getLog();

        contest.addProblemListener(new ProblemListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateUI();
            }
        });

    }

    protected void populateUI() {
        for (Problem problem : contest.getProblems()) {
            addProblemTab(problem);
        }
        
        updateRemainingTime();
    }

    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(getButtonPane(), BorderLayout.SOUTH);
            mainPanel.add(getProblemTabbedPane(), BorderLayout.CENTER);
        }
        return mainPanel;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            remainingTimeLabel = new JLabel();
            remainingTimeLabel.setText("XXX min remaining in contest");
            remainingTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            remainingTimeLabel.setPreferredSize(new Dimension(200, 20));
            remainingTimeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() > 1) {
                        updateRemainingTime();
                    }
                }
            });
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(55);
            flowLayout.setAlignment(FlowLayout.LEFT);
            buttonPane = new JPanel();
            buttonPane.setPreferredSize(new Dimension(540, 36));
            buttonPane.setLayout(flowLayout);
            buttonPane.add(remainingTimeLabel, null);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCloseButton(), null);
       
        }
        return buttonPane;
    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateEOCSettings();
                }
            });
        }
        return updateButton;
    }

    protected void dumpNotification(String title, NotificationSetting notificationSetting2) {

        System.out.println();
        System.out.println("Dump for: " + title);
        if (notificationSetting2 == null) {
            System.out.println("          No delivery notification settings defined.");

        } else {
            JudgementNotification judgementNotification = null;

            judgementNotification = notificationSetting2.getPreliminaryNotificationYes();
            System.out.println("          Prelim Yes suppress " + judgementNotification.isNotificationSupressed() + " cuttoff at " + judgementNotification.getCuttoffMinutes());

            judgementNotification = notificationSetting2.getPreliminaryNotificationNo();
            System.out.println("          Prelim No  suppress " + judgementNotification.isNotificationSupressed() + " cuttoff at " + judgementNotification.getCuttoffMinutes());

            judgementNotification = notificationSetting2.getFinalNotificationYes();
            System.out.println("          Final  Yes suppress " + judgementNotification.isNotificationSupressed() + " cuttoff at " + judgementNotification.getCuttoffMinutes());

            judgementNotification = notificationSetting2.getFinalNotificationNo();
            System.out.println("          Final  No  suppress " + judgementNotification.isNotificationSupressed() + " cuttoff at " + judgementNotification.getCuttoffMinutes());
        }
    }
    
    protected JudgementNotificationsList getJudgementNotificationFromFields() {

        JudgementNotificationsList newJudgementNotificationsList = new JudgementNotificationsList();

        for (Problem problem : contest.getProblems()) {
            EditJudgementNotificationPane pane = getPaneForProblem(problem);
            if (pane != null) {
                NotificationSetting notificationSetting = pane.getNotificationSettingFromFields();
                newJudgementNotificationsList.update(notificationSetting);
            }
        }

        return newJudgementNotificationsList;
    }

    /**
     * Save the EOC settings.
     * 
     */
    protected void updateEOCSettings() {

        try {

            ContestInformation contestInformation = contest.getContestInformation();
            
            JudgementNotificationsList judgementNotificationsList = getJudgementNotificationFromFields();

            contestInformation.setJudgementNotificationsList(judgementNotificationsList);

            controller.updateContestInformation(contestInformation);

            updateButtonState(false);
            setVisible(false);

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception updating/saving notifications", e);
            JOptionPane.showMessageDialog(this, "Internal Exception, check log. "+e.getMessage());
            
            e.printStackTrace(); // TODO debug take this line out
        }

    }

    /**
     * This method initializes problemTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getProblemTabbedPane() {
        if (problemTabbedPane == null) {
            problemTabbedPane = new JTabbedPane();
            problemTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                   updateRemainingTime();
                }
            });
       
        }
        return problemTabbedPane;
    }

    /**
     * This method initializes closeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setText("Close");
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return closeButton;
    }

    protected void handleCancelButton() {
        
        updateRemainingTime();

        if (getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog(this, "Notifications modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                updateEOCSettings();
                setVisible(false);
            } else if (result == JOptionPane.NO_OPTION) {
                setVisible(false);
            }

        } else {
            setVisible(false);
        }
    }

    private EditJudgementNotificationPane getPaneForProblem(Problem problem) {

        String tabName = getTabName(problem);

        for (Component component : getProblemTabbedPane().getComponents()) {
            // System.out.println("component is '" + component.getName() + "' '" + tabName + "'");
            if (tabName.equals(component.getName())) {
                return (EditJudgementNotificationPane) component;
            }
        }
        return null;
    }

    // TODO used when adding/updating tabs
    // private boolean isProblemInTab(Problem problem) {
    // return getPaneForProblem(problem) != null;
    // }

    private String getTabName(Problem problem) {
        return "Problem " + getProblemLetter(problem);
    }

    public void addProblemTab(final Problem problem) {

        final NotificationSetting notificationSetting = new NotificationSetting(problem.getElementId());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EditJudgementNotificationPane editJudgementNotificationPane = new EditJudgementNotificationPane(notificationSetting, getThis());
                editJudgementNotificationPane.setContestAndController(contest, null);
                String tabName = getTabName(problem);
                editJudgementNotificationPane.setName(tabName);
                getProblemTabbedPane().add(tabName, editJudgementNotificationPane);
                
                itemChanged(null);
            }
        });
    }

    /**
     * Reload all settings from server/contest model.
     * 
     */
    public void resetAllNotificationSettings() {

        for (Problem problem : contest.getProblems()) {
            EditJudgementNotificationPane pane = getPaneForProblem(problem);
            if (pane == null) {

                // No pane found so we add it.

                addProblemTab(problem);

            } else {

                NotificationSetting notificationSetting = getNotificationSetting(problem);
                if (notificationSetting == null){
                    notificationSetting = new NotificationSetting(problem.getElementId());
                }
                pane.setNotificationSetting(notificationSetting);
            }
        }
        
        updateButtonState(false);
        updateRemainingTime();
    }

    /**
     * Get notification setting for problem.
     * 
     * @param problem
     * @return
     */
    private NotificationSetting getNotificationSetting(Problem problem) {

        ContestInformation contestInformation = contest.getContestInformation();

        JudgementNotificationsList judgementNotificationsList = contestInformation.getJudgementNotificationsList();

        if (judgementNotificationsList == null) {
            return null;
        } else {
            return judgementNotificationsList.get(problem);
        }
    }

    /**
     * Update Problem title only.
     * 
     * @param problem
     */
    protected void updateProblemTab(Problem problem) {
        EditJudgementNotificationPane pane = getPaneForProblem(problem);
        if (pane != null) {
            pane.setTitle(problem.getDisplayName());
        }
    }

    protected String getProblemLetter(Problem problem) {
        char let = 'A';
        int count = 0;

        for (Problem problem2 : contest.getProblems()) {
            if (problem2.equals(problem)) {
                let += count;
            }
            count++;
        }
        return "" + let;
    }
    
    protected void updateRemainingTime() {
        ContestTime contestTime = contest.getContestTime();
        long minLeft = contestTime.getRemainingSecs() / 60;
        remainingTimeLabel.setText(minLeft + " mins from end of contest");
        remainingTimeLabel.setToolTipText(contestTime.getElapsedTimeStr() + " elapsed minutes");
    }

    /**
     * EOC notification
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    private class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    addProblemTab(event.getProblem());
                }
            });
        }

        public void problemChanged(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateProblemTab(event.getProblem());
                }
            });
        }

        public void problemRemoved(ProblemEvent event) {
            // TODO code remove problem tab logic.
            problemChanged(event); // fake statement ot satisfy check style
        }

        public void problemRefreshAll(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // FIXME must remove all previously added Problems
                    populateUI();
                }
            }); 
        }
    }

    public void itemChanged(JComponent component) {
        
        /**
         * Item value has changed on sub pane component 
         */
        
        ContestInformation contestInformation = contest.getContestInformation();
        JudgementNotificationsList originalJudgementNotificationsList = contestInformation.getJudgementNotificationsList();
        
        boolean changed = false;
        
        if (originalJudgementNotificationsList != null){
            JudgementNotificationsList judgementNotificationsList = getJudgementNotificationFromFields();
            
            if (judgementNotificationsList.getList().length != originalJudgementNotificationsList.getList().length){
                changed = true;
            } else {
                for (NotificationSetting setting : judgementNotificationsList.getList()){
                    ElementId elementId = setting.getElementId();
                    NotificationSetting setting2 = (NotificationSetting) originalJudgementNotificationsList.get(elementId);
                    if (setting2 == null){
                        changed = true;
                        break;
                    }
                    if (! setting.isSameAs(setting2)){
                        changed = true;
                    }
                }
            }
        }
        updateButtonState(changed);
    }
    
    protected void updateButtonState(boolean changed) {
        getUpdateButton().setEnabled(changed);

        if (changed) {
            getCloseButton().setText("Cancel");
        } else {
            getCloseButton().setText("Close");
        }
    }
    
    protected IEditChangeCallback getThis(){
        return this;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
