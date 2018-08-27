package edu.csus.ecs.pc2.ui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

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

        allGroupsRadioButton.setBounds(125, 26, 275, 23);
        add(allGroupsRadioButton);

        selectGroupsRadioButton = new JRadioButton("Show to only these groups");
        selectGroupsRadioButton.setBounds(125, 62, 263, 23);
        add(selectGroupsRadioButton);
        

        add(getGroupsScroll());
        getValidatorChoiceButtonGroup();
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
        
        boolean allGroupsSelected = problem.isAllView();
        
        System.out.println("debug 22 ProblemGroupPane.setProblem allGroupsSelected = "+allGroupsSelected);
        
        selectGroupsRadioButton.setSelected(true);
        if (allGroupsSelected){
            allGroupsRadioButton.setSelected(allGroupsSelected);
        }

        Group[] groups = getContest().getGroups();
        for (Group group : groups) {
            WrapperJCheckBox wrapperJCheckBox = new WrapperJCheckBox(group);
            if (allGroupsSelected){
                wrapperJCheckBox.setSelected(true);
            }
            else if (problem.canView(group)){
                System.out.println("debug 22  selected "+group);
                wrapperJCheckBox.setSelected(true);
            }
            groupListModel.addElement(wrapperJCheckBox);
        }
    }

    public List<Group> getGroups() {

        List<Group> groups = new ArrayList<Group>();

        if (selectGroupsRadioButton.isSelected()) {

            Enumeration<WrapperJCheckBox> enumeration = groupListModel.elements();
            while (enumeration.hasMoreElements()) {
                WrapperJCheckBox element = (WrapperJCheckBox) enumeration.nextElement();
                if (element.isSelected()) {
                    Group group = (Group) element.getContents();
                    groups.add(group);
                    System.out.println("debug 22  getGroups "+group);
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

}
