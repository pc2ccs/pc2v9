package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IJudgementListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementEvent;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Show Judgements, allow add and edit.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JudgementsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 36368747620026978L;

    private MCLB judgementListBox = null;

    private JPanel buttonsPane = null;

    private JButton addButton = null;

    private JPanel statusPanel = null;

    private JButton editButton = null;

    private EditJudgementFrame editJudgementFrame = null;
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    private class JudgementListenerImplementation implements IJudgementListener {

        public void judgementAdded(JudgementEvent event) {
            reloadJudgementList();
        }

        public void judgementChanged(JudgementEvent event) {
            reloadJudgementList();
        }

        public void judgementRemoved(JudgementEvent event) {
            reloadJudgementList();
        }

        public void judgementRefreshAll(JudgementEvent judgementEvent) {
            reloadJudgementList(); 
        }
    }

    /**
     * This method initializes
     * 
     */
    public JudgementsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(584, 211));
        this.add(getButtonsPane(), java.awt.BorderLayout.SOUTH);
        this.add(getJudgementListBox(), java.awt.BorderLayout.CENTER);

        editJudgementFrame = new EditJudgementFrame();
    }

    @Override
    public String getPluginTitle() {
        return "Judgements Panel";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        getContest().addJudgementListener(new JudgementListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());

        getEditJudgementFrame().setContestAndController(inContest, inController);

        initializePermissions();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                reloadJudgementList();
            }
        });
    }
    
    private void updateGUIperPermissions() {
        addButton.setVisible(isAllowed(Permission.Type.ADD_JUDGEMENTS));
        editButton.setVisible(isAllowed(Permission.Type.EDIT_JUDGEMENTS));
    }

    protected void reloadJudgementList() {

        getJudgementListBox().removeAllRows();

        for (Judgement judgement : getContest().getJudgements()) {
            updateJudgementRow(judgement);
        }

    }

    /**
     * This method initializes JudgementListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getJudgementListBox() {
        if (judgementListBox == null) {
            judgementListBox = new MCLB();

            judgementListBox.add(getStatusPanel(), java.awt.BorderLayout.NORTH);
            Object[] cols = { "Judgement", "Acronym" };
            judgementListBox.addColumns(cols);
        }
        return judgementListBox;
    }

    /**
     * This method initializes buttonsPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonsPane() {
        if (buttonsPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(45);
            buttonsPane = new JPanel();
            buttonsPane.setLayout(flowLayout);
            buttonsPane.add(getAddButton(), null);
            buttonsPane.add(getEditButton(), null);
        }
        return buttonsPane;
    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setMnemonic(KeyEvent.VK_A);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addJudgement();
                }
            });
        }
        return addButton;
    }

    protected void addJudgement() {

        editJudgementFrame.setJudgement(null);
        editJudgementFrame.setDeleteCheckBoxEnabled(true);
        editJudgementFrame.setVisible(true);
    }

    /**
     * This method initializes statusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new JPanel();
            statusPanel.setLayout(new BorderLayout());
            statusPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        }
        return statusPanel;
    }

    private void showMessage(final String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void updateJudgementRow(final Judgement judgement) {
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildJudgementRow(judgement);
                int rowNumber = judgementListBox.getIndexByKey(judgement.getElementId());
                if (rowNumber == -1) {
                    judgementListBox.addRow(objects, judgement.getElementId());
                } else {
                    judgementListBox.replaceRow(objects, rowNumber);
                }
                judgementListBox.autoSizeAllColumns();
            }
        });
    }

    private Object[] buildJudgementRow(Judgement judgement) {

        // Object[] cols = { "Judgement", "Acronym"};

        try {
            int cols = judgementListBox.getColumnCount();
            Object[] s = new String[cols];

            s[0] = judgement.toString();
            if (!judgement.isActive()) {
                s[0] = "[HIDDEN] " + judgement.toString();
            }
            
            s[1] = judgement.getAcronym();
            
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildJudgementRow()", exception);
        }
        return null;

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
            editButton.setMnemonic(KeyEvent.VK_E);
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedJudgement();
                }
            });
        }
        return editButton;
    }

    /**
     * Number of runs that match this judgement.
     * 
     * @param judgement
     * @return count of runs that match.
     */
    int numberOfRuns(Judgement judgement) {

        int count = 0;
        ElementId elementId = judgement.getElementId();

        for (Run run : getContest().getRuns()) {
            if (run.isDeleted()) {
                continue;
            }
            if (run.isJudged()) {
                if (run.getJudgementRecord().getJudgementId().equals(elementId)) {
                    count++;
                }
            }
        }
        return count;
    }

    protected void editSelectedJudgement() {

        int selectedIndex = judgementListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select a judgement to edit");
            return;
        }

        try {
            ElementId elementId = (ElementId) judgementListBox.getKeys()[selectedIndex];
            Judgement judgementToEdit = getContest().getJudgement(elementId);
            
            if (isYesJudgement(judgementToEdit)){
                editJudgementFrame.setDeleteCheckBoxEnabled(false);
            } else {
                editJudgementFrame.setDeleteCheckBoxEnabled(true);
            }

            int numberRuns = numberOfRuns(judgementToEdit);
            if (numberRuns > 0) {
                JOptionPane.showMessageDialog(this, "There are " + numberRuns + " runs which will be changed if this judgement is changed", "Runs may be changed", JOptionPane.WARNING_MESSAGE);
            }

            editJudgementFrame.setJudgement(judgementToEdit);
            editJudgementFrame.setVisible(true);
        } catch (Exception e) {
            getController().getLog().log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit judgement, check log");
        }

    }

    private boolean isYesJudgement(Judgement judgementToEdit) {
        return getContest().getJudgements()[0].equals(judgementToEdit); 
    }

    protected EditJudgementFrame getEditJudgementFrame() {
        if (editJudgementFrame == null) {
            editJudgementFrame = new EditJudgementFrame();
        }
        return editJudgementFrame;
    }
    

    /**
     * Account Listener Implementation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignored
        }

        public void accountModified(AccountEvent accountEvent) {
            // check if is this account
            Account account = accountEvent.getAccount();
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
            // ignore
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {

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
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {

            initializePermissions();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                }
            });
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
