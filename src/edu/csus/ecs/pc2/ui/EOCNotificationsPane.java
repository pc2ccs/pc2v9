package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.JudgementNotification;
import edu.csus.ecs.pc2.core.model.NotificationSetting;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * List pane for End of Contest Control (Notifications).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EOCNotificationsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 372287370869620482L;

    private JPanel buttonPane = null;

    private MCLB eocNotificationMCLB = null;

    private JButton editButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private EditJudgementNotificationFrame editJudgementNotificationFrame = null;

    private Log log;

  
    /**
     * This method initializes
     * 
     */
    public EOCNotificationsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(564, 229));
        this.add(getNotificationListBox(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getNotificationButtonPane(), java.awt.BorderLayout.SOUTH);

        editJudgementNotificationFrame = new EditJudgementNotificationFrame();

    }

    @Override
    public String getPluginTitle() {
        return "Notifications Pane";
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNotificationButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            buttonPane.add(getEditButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes notificationListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getNotificationListBox() {
        if (eocNotificationMCLB == null) {
            eocNotificationMCLB = new MCLB();

            Object[] cols = { "Letter", "Problem", "Final", "Preliminary"};

            eocNotificationMCLB.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
//            HeapSorter numericStringSorter = new HeapSorter();
//            numericStringSorter.setComparator(new NumericStringComparator());

            // Letter
            eocNotificationMCLB.setColumnSorter(0, sorter, 1);

            // Problem
            eocNotificationMCLB.setColumnSorter(1, sorter, 2);

            // Final
            eocNotificationMCLB.setColumnSorter(2, sorter, 3);
            // Preliminary
            eocNotificationMCLB.setColumnSorter(3, sorter, 4);

            eocNotificationMCLB.autoSizeAllColumns();

        }
        return eocNotificationMCLB;
    }

    public void updateNotificationRow(final Problem problem) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildNotificationRow(problem);
                int rowNumber = eocNotificationMCLB.getIndexByKey(problem);
                if (rowNumber == -1) {
                    eocNotificationMCLB.addRow(objects, problem);
                } else {
                    eocNotificationMCLB.replaceRow(objects, rowNumber);
                }
                eocNotificationMCLB.autoSizeAllColumns();
                // notificationListBox.sort();
            }
        });
    }
    
    protected String getProblemLetter(Problem problem) {
        char let = 'A';
        int count = 0;

        for (Problem problem2 : getContest().getProblems()) {
            if (problem2.equals(problem)) {
                let += count;
            }
            count++;
        }
        return "" + let;
    }

    protected Object[] buildNotificationRow(Problem problem) {

        // Object[] cols = { "Problem", "Letter", "Final", "Preliminary"};

        int numberColumns = eocNotificationMCLB.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = getProblemLetter(problem);
        c[1] = problem.getDisplayName();

        c[2] = "";
        c[3] = "";

        NotificationSetting notificationSetting = getNotificationSetting(problem);
        if (notificationSetting != null) {
            c[2] = getNotificationSettingsString(notificationSetting.getFinalNotificationYes(), notificationSetting.getFinalNotificationNo());
            c[3] = getNotificationSettingsString(notificationSetting.getPreliminaryNotificationYes(), notificationSetting.getPreliminaryNotificationNo());
        }

        return c;
    }

    public static String getClientTitle(ClientId clientId) {

        if (clientId.getSiteNumber() == 0) {
            if (clientId.getClientNumber() == 0) {
                if (clientId.getClientType().equals(Type.TEAM)) {
                    return "All teams";
                }
            }
        }

        return clientId.getName() + " Site " + clientId.getSiteNumber();
    }

    protected String getNotificationSettingsString(JudgementNotification notificationYes, JudgementNotification notificationNo) {

        String s = "";

        if (notificationYes.isNotificationSupressed()) {
            s = "Yes";
            if (notificationYes.getCuttoffMinutes() > 0) {
                s += " cutoff " + notificationYes.getCuttoffMinutes() + " min";
            }
        }
        if (notificationNo.isNotificationSupressed()) {
            s += " No";
            if (notificationNo.getCuttoffMinutes() > 0) {
                s += " cutoff " + notificationNo.getCuttoffMinutes() + " min";
            }
        }

        return s;
    }

    private void reloadListBox() {
        eocNotificationMCLB.removeAllRows();
        
        for (Problem problem : getContest().getProblems()){
            updateNotificationRow(problem);
        }
    }
    
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        editJudgementNotificationFrame.setContestAndController(inContest, inController);
        
        log = getController().getLog();

        getContest().addContestInformationListener(new ContestInformationListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }

    /**
     * This method initializes editButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditButton() {
        if (editButton == null) {
            editButton = new JButton();
            editButton.setText("Edit");
            editButton.setToolTipText("Edit existing Notification definition");
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedNotification();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedNotification() {

//        int selectedIndex = eocNotificationMCLB.getSelectedIndex();
//        if (selectedIndex == -1) {
//            showMessage("Select a notification to edit");
//            return;
//        }

        try {
            // ClientId clientId = (ClientId) eocNotificationMCLB.getKeys()[selectedIndex];
            // NotificationSetting notificationToEdit = getNotificationSettings(clientId);

            // TODO select the Problem pan that they selected

            editJudgementNotificationFrame.resetAllNotificationSettings();
            
            editJudgementNotificationFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit notification, check log");
            e.printStackTrace();
        }
    }

   
    /**
     * Get notification setting for problem.
     * 
     * @param problem
     * @return
     */
    private NotificationSetting getNotificationSetting(Problem problem) {

        ContestInformation contestInformation = getContest().getContestInformation();

        JudgementNotificationsList judgementNotificationsList = contestInformation.getJudgementNotificationsList();

        if (judgementNotificationsList == null) {
            return null;
        } else {
            return judgementNotificationsList.get(problem);
        }
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * show message to user
     * 
     * @param string
     */
    private void showMessage(final String string) {
        JOptionPane.showMessageDialog(this, string);

    }

    /**
     * Contest Info listener for Judgement Notifications.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    public class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

    }

    /**
     * EOC notification
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    private class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateNotificationRow(event.getProblem());
                }
            });
        }

        public void problemChanged(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateNotificationRow(event.getProblem());
                }
            });
        }

        public void problemRemoved(ProblemEvent event) {
            log.info("debug Problem REMOVED  " + event.getProblem());
        }

        public void problemRefreshAll(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
