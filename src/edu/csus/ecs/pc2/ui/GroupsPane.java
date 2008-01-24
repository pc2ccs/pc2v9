package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.imports.ICPCImportData;
import edu.csus.ecs.pc2.core.imports.LoadICPCData;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
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

    private JButton icpcButton = null;

    private String lastOpenedFile;

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
            groupButtonPane.add(getIcpcButton(), null);
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

            Object[] cols = { "Display Name", "PC^2 Site", "Id" , "On Scoreboard"};
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

        // Object[] cols = { "Display Name", "PC^2 Site", "Id", "On Scoreboard" };

        int numberColumns = groupListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = group.toString();
        if (group.getSite() == null) {
            c[1] = "<NONE SELECTED>";
        } else {
            c[1] = getContest().getSite(group.getSite().getSiteNumber()).toString();
        }
        c[2] = Integer.valueOf(group.getGroupId()).toString();
        c[3] = Boolean.toString(group.isDisplayOnScoreboard());
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

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
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

    /**
     * This method initializes icpcButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getIcpcButton() {
        if (icpcButton == null) {
            icpcButton = new JButton();
            icpcButton.setText("Load ICPC");
            icpcButton.setToolTipText("Load ICPC PC2_Site.tab");
            icpcButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadICPCData();
                }
            });
        }
        return icpcButton;
    }

    protected void loadICPCData() {
        try {
            JFileChooser chooser = new JFileChooser(lastOpenedFile);
            chooser.setDialogTitle("Select PC2_Contest.tab");
            chooser.setFileFilter(new TabFileFilter());
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File newFile = chooser.getSelectedFile().getCanonicalFile();
                boolean newFileProblem = true;
                if (newFile.exists()) {
                    if (newFile.isFile()) {
                        if (newFile.canRead()) {
                            lastOpenedFile = chooser.getCurrentDirectory().toString();
                            // TODO move this off the swing thread, maybe into its own class
                            ICPCImportData importData = LoadICPCData.loadSites(lastOpenedFile, getContest().getSites());
                            newFileProblem = false;
                            Group[] importedGroups = importData.getGroups();
                            Group[] modelGroups = getContest().getGroups();
                            // XXX this is a funky location, but we do not want to add a 3rd icpc load for it
                            String contestTitle = importData.getContestTitle();
                            if (contestTitle != null && contestTitle.trim().length() > 0) {
                                ContestInformation ci = getContest().getContestInformation();
                                ci.setContestTitle(contestTitle);
                                getController().updateContestInformation(ci);
                            }
                            if (importedGroups != null && importedGroups.length > 0) {
                                if (modelGroups == null || modelGroups.length == 0) {
                                    for (Group group : importedGroups) {
                                        getController().addNewGroup(group);
                                    }
                                } else {
                                    // there exists modelGroups, that we need to merge with
                                    // primary match should be based on external id
                                    // secondary match based on name
                                    HashMap<String, Group> groupMap = new HashMap<String, Group>();
                                    for (Group group : modelGroups) {
                                        groupMap.put(group.getDisplayName(), group);
                                        groupMap.put(Integer.toString(group.getGroupId()), group);
                                    }
                                    for (Group group : importedGroups) {
                                        if (groupMap.containsKey(Integer.toString(group.getGroupId()))) {
                                            mergeGroups(groupMap.get(Integer.toString(group.getGroupId())), group);
                                        } else {
                                            if (groupMap.containsKey(group.getDisplayName())) {
                                                mergeGroups(groupMap.get(group.getDisplayName()), group);
                                            } else {
                                                // new group
                                                getController().addNewGroup(group);
                                            }
                                        }
                                    }
                                }
                            } // XXX odd, but is it an error if we have no groups?
                        }
                    }
                }
                if (newFileProblem) {
                    log.warning("Problem reading PC2_Contest.tab " + newFile.getCanonicalPath() + "");
                    JOptionPane.showMessageDialog(null, "Could not open file " + newFile, "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
        
    }

    /**
     * This method merges the data read from PC2_Site.tab into the model.
     * 
     * @param destGroup
     * @param srcGroup
     */
    private void mergeGroups(Group dstGroup, Group srcGroup) {
        // no-op if the groups are the same
        if (dstGroup.isSameAs(srcGroup)) {
            return;
        }
        dstGroup.setDisplayName(srcGroup.getDisplayName());
        dstGroup.setGroupId(srcGroup.getGroupId());
        if (srcGroup.getSite() != null) { // do not overwrite this
            dstGroup.setSite(srcGroup.getSite());
        }
        getController().updateGroup(dstGroup);
    }
} // @jve:decl-index=0:visual-constraint="10,10"
