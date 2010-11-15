package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IGroupListener;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.GroupEvent;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;

/**
 * View Groups pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public class GroupsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5837850150714301616L;

    private JPanel groupButtonPane = null;

    private MCLB groupListBox = null;

    private JButton addButton = null;

    private JButton editButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;
    
    private EditGroupFrame editGroupFrame = null;
    
    private Log log;
    
    private PermissionList permissionList = new PermissionList();

    /**
     * This method initializes
     * 
     */
    public GroupsPane() {
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
        this.add(getGroupListBox(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getGroupButtonPane(), java.awt.BorderLayout.SOUTH);
        
        editGroupFrame = new EditGroupFrame();

    }

    @Override
    public String getPluginTitle() {
        return "Groups Pane";
    }

    /**
     * This method initializes groupButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupButtonPane() {
        if (groupButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            groupButtonPane = new JPanel();
            groupButtonPane.setLayout(flowLayout);
            groupButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            groupButtonPane.add(getAddButton(), null);
            groupButtonPane.add(getEditButton(), null);
        }
        return groupButtonPane;
    }

    /**
     * This method initializes groupListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getGroupListBox() {
        if (groupListBox == null) {
            groupListBox = new MCLB();

            Object[] cols = { "Id", "Display Name", "PC^2 Site", "External Id" , "On Scoreboard"};
            groupListBox.addColumns(cols);
            
            groupListBox.autoSizeAllColumns();
        }
        return groupListBox;
    }

    public void updateGroupRow(final Group group) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int rowNumber = groupListBox.getIndexByKey(group.getElementId());
                if (rowNumber == -1) {
                    rowNumber = groupListBox.getRowCount() + 1;
                    Object[] objects = buildGroupRow(group, rowNumber);
                    groupListBox.addRow(objects, group.getElementId());
                } else {
                    Object[] objects = buildGroupRow(group, rowNumber);
                    groupListBox.replaceRow(objects, rowNumber);
                }
                groupListBox.autoSizeAllColumns();
                // groupListBox.sort();
                getEditButton().setEnabled(groupListBox.getRowCount() > 0);
            }
        });
    }

    protected Object[] buildGroupRow(Group group, int rowNumber) {

//        Object[] cols = { "Id", "Display Name", "PC^2 Site", "External Id" , "On Scoreboard"};

        int numberColumns = groupListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = "" + rowNumber; 
        c[1] = group.toString();
        if (group.getSite() == null) {
            c[2] = "<NONE SELECTED>";
        } else {
            c[2] = getContest().getSite(group.getSite().getSiteNumber()).toString();
        }
        c[3] = Integer.valueOf(group.getGroupId()).toString();
        c[4] = Boolean.toString(group.isDisplayOnScoreboard());
        return c;
    }

    private void reloadListBox() {
        groupListBox.removeAllRows();
        Group[] groups = getContest().getGroups();

        int rowNumber = 1;
        for (Group group : groups) {
            addGroupRow(group, rowNumber);
            rowNumber ++;
        }
        getEditButton().setEnabled(groupListBox.getRowCount() > 0);
    }

    private void addGroupRow(Group group, int rowNumber) {
        Object[] objects = buildGroupRow(group, rowNumber);
        groupListBox.addRow(objects, group.getElementId());
        groupListBox.autoSizeAllColumns();
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        editGroupFrame.setContestAndController(inContest, inController);
        
        getContest().addGroupListener(new GroupListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());
        
        initializePermissions();
        
        log = getController().getLog();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                reloadListBox();
            }
        });
    }
    
    private boolean isAllowed(Permission.Type type) {
        return permissionList.isAllowed(type);
    }

    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        permissionList.clearAndLoadPermissions(account.getPermissionList());
    }

    private void updateGUIperPermissions() {
        addButton.setVisible(isAllowed(Permission.Type.ADD_GROUPS));
        editButton.setVisible(isAllowed(Permission.Type.EDIT_GROUPS));
    }

    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */
    public class GroupListenerImplementation implements IGroupListener {

        public void groupAdded(GroupEvent event) {
            updateGroupRow(event.getGroup());
        }

        public void groupChanged(GroupEvent event) {
            updateGroupRow(event.getGroup());
        }

        public void groupRemoved(GroupEvent event) {
            // TODO Auto-generated method stub
        }

        public void groupRefreshAll(GroupEvent groupEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }
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
            addButton.setToolTipText("Add a group");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addNewGroup();
                }
            });
        }
        return addButton;
    }

    protected void addNewGroup() {
        editGroupFrame.setGroup();
        editGroupFrame.setVisible(true);
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
            editButton.setToolTipText("Edit selected group");
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedGroup();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedGroup() {
        
        int selectedIndex = groupListBox.getSelectedIndex();
        if(selectedIndex == -1){
            showMessage("Select a group to edit");
            return;
        }
        
        try {
            ElementId elementId = (ElementId) groupListBox.getKeys()[selectedIndex];
            Group groupToEdit = getContest().getGroup(elementId);

            editGroupFrame.setGroup(groupToEdit, selectedIndex + 1);
            editGroupFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit group, check log");
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
            messagePane.setPreferredSize(new java.awt.Dimension(25,25));
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

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(getParentFrame(), string, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

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
