package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IGroupListener;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.GroupEvent;

/**
 * View Groups pane.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

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

            Object[] cols = { "Display Name", "PC^2 Site", "Id" };
            groupListBox.addColumns(cols);
            
            groupListBox.autoSizeAllColumns();
        }
        return groupListBox;
    }

    public void updateGroupRow(final Group group) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildGroupRow(group);
                int rowNumber = groupListBox.getIndexByKey(group.getElementId());
                if (rowNumber == -1) {
                    groupListBox.addRow(objects, group.getElementId());
                } else {
                    groupListBox.replaceRow(objects, rowNumber);
                }
                groupListBox.autoSizeAllColumns();
//                groupListBox.sort();
            }
        });
    }

    protected Object[] buildGroupRow(Group group) {

        // Object[] cols = { "Display Name", "PC^2 Site", "Id" };

        int numberColumns = groupListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = group.toString();
        if (group.getSite() == null) {
            c[1] = "<NONE SELECTED>";
        } else {
            c[1] = getContest().getSite(group.getSite().getSiteNumber()).toString();
        }
        c[2] = Integer.valueOf(group.getGroupId()).toString();
        return c;
    }

    private void reloadListBox() {
        groupListBox.removeAllRows();
        Group[] groups = getContest().getGroups();

        for (Group group : groups) {
            addGroupRow(group);
        }
    }

    private void addGroupRow(Group group) {
        Object[] objects = buildGroupRow(group);
        groupListBox.addRow(objects, group.getElementId());
        groupListBox.autoSizeAllColumns();
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        editGroupFrame.setContestAndController(inContest, inController);
        
        getContest().addGroupListener(new GroupListenerImplementation());
        
        log = getController().getLog();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
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
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addNewGroup();
                }
            });
        }
        return addButton;
    }

    protected void addNewGroup() {
        editGroupFrame.setGroup(null);
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

            editGroupFrame.setGroup(groupToEdit);
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
                messageLabel.setText(string);
            }
        });

    }
} // @jve:decl-index=0:visual-constraint="10,10"
