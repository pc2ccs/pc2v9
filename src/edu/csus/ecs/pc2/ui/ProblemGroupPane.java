// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Edit groups that can view this Problem.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ProblemGroupPane extends JPanePlugin {

    /**
     *
     */
    private static final long serialVersionUID = 6334530401574768488L;

    protected JRadioButton allGroupsRadioButton = null;

    protected JRadioButton selectGroupsRadioButton = null;

    /**
     * Model containing checkboxes
     */
    private DefaultListModel<WrapperJCheckBox> groupListModel = new DefaultListModel<WrapperJCheckBox>();

    private JCheckBoxJList groupListBox = null;

    private JScrollPane groupsScrollPane = null;

    private ButtonGroup groupsSelectedButtonGroup = null;

    private EditProblemPane parentPane;

    /**
     * This method initializes groupListBox
     *
     * @return javax.swing.JList
     */
    public JCheckBoxJList getGroupListBox() {
        if (groupListBox == null) {
            groupListBox = new JCheckBoxJList(groupListModel);
        }
        return groupListBox;
    }

    public JScrollPane getGroupsScroll() {

        if (groupsScrollPane == null) {
            groupsScrollPane = new JScrollPane();
            groupsScrollPane.setBounds(149, 92, 396, 369);
            groupsScrollPane.setViewportView(getGroupListBox());
        }

        return groupsScrollPane;
    }

    public ProblemGroupPane() {
        setLayout(null);

        allGroupsRadioButton = new JRadioButton("Show to all groups");
        allGroupsRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentPane.enableUpdateButton();
                enableGroupCheckBoxes(false);
            }
        });

        allGroupsRadioButton.setBounds(125, 26, 275, 23);
        add(allGroupsRadioButton);

        selectGroupsRadioButton = new JRadioButton("Show to only these groups");
        selectGroupsRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentPane.enableUpdateButton();
                enableGroupCheckBoxes(true);
            }
        });
        selectGroupsRadioButton.setBounds(125, 62, 263, 23);
        add(selectGroupsRadioButton);

        add(getGroupsScroll());
        getValidatorChoiceButtonGroup();
    }


    /**
     * Enable or disable Group Checkboxs by enabling/disabling the JCheckboxJList
     */
    protected void enableGroupCheckBoxes(boolean enable) {

        if (!enable) {
            getGroupListBox().clearSelection();
            getGroupListBox().setForeground(Color.GRAY);
        } else {
            getGroupListBox().setForeground(Color.BLACK);
        }
        getGroupListBox().setEnabled(enable);

        // This loop is wrong and does not do what you think it does.
        // Apparently, enabling/disabling a checkbox within a JCheckboxJList has no effect (it appears to always be enabled
        // if the JCheckboxJList is enabled.)
        // Instead you disable the entire JCheckboxJList component as we do above.  Leaving
        // this here for code review, but should be removed.
        // If this code is enabled, it causes lots and lots of re-reads of the problem data files
        // since it would trigger a checkbox changed listener for each setEnabled().  This causes
        // the check for the update button to be done which checks every data file and takes quite
        // some time on huge data files and/or large numbers of files.
//        ListModel<Object> list = getGroupListBox().getModel();
//
//        for (int i = 0; i < list.getSize(); i++) {
//            Object ele = list.getElementAt(i);
//            if (ele instanceof WrapperJCheckBox) {
//                WrapperJCheckBox box = (WrapperJCheckBox) ele;
//
//                box.setEnabled(enable);
//
//            }
//
//        }
    }

    @Override
    public String getPluginTitle() {
        return "Problem Group Pane";
    }

    public void setProblem(Problem problem) {

        groupListModel.removeAllElements(); // clear checkbox

        populateProblemGroups(problem);

    }

    private void populateProblemGroups(Problem problem) {

        boolean allGroupsSelected = true;
        if (problem != null) {
            allGroupsSelected = problem.isAllView();
        }

        if (allGroupsSelected) {
            allGroupsRadioButton.setSelected(true);
        } else {
            selectGroupsRadioButton.setSelected(true);
        }

        // I don't know what this is for and why it's here - This list is always empty at this point - JB 3/20/2024
        // Why always enable the checkboxes?  Makes no sense.
        // enableGroupCheckBoxes(true);

        // This makes more sense - to color the background of the list box depending on whether "all groups" can view the problem
        // and enable/disable the container of the checkboxes (JCheckBoxJList)
        enableGroupCheckBoxes(!allGroupsSelected);

        Group[] groups = getContest().getGroups();
        for (Group group : groups) {

            WrapperJCheckBox wrapperJCheckBox = new WrapperJCheckBox(group);
            wrapperJCheckBox.setSelected(allGroupsSelected || problem.canView(group));
            wrapperJCheckBox.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    parentPane.enableUpdateButton();
                }
            });

            groupListModel.addElement(wrapperJCheckBox);
        }
    }

    public List<Group> getGroups() {

        List<Group> groups = new ArrayList<Group>();

        if (selectGroupsRadioButton.isSelected()) {

            Enumeration<WrapperJCheckBox> enumeration = groupListModel.elements();
            while (enumeration.hasMoreElements()) {
                WrapperJCheckBox element = enumeration.nextElement();
                if (element.isSelected()) {
                    Group group = (Group) element.getContents();
                    groups.add(group);
                }
            }
        }

        return groups;
    }

    private ButtonGroup getValidatorChoiceButtonGroup() {
        if (groupsSelectedButtonGroup == null) {
            groupsSelectedButtonGroup = new ButtonGroup();
            groupsSelectedButtonGroup.add(allGroupsRadioButton);
            groupsSelectedButtonGroup.add(selectGroupsRadioButton);
        }
        return groupsSelectedButtonGroup;
    }



    public void setParentPane(EditProblemPane editProblemPane) {
        parentPane = editProblemPane;


    }
}
