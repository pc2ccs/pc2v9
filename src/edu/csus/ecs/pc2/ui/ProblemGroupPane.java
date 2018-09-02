package edu.csus.ecs.pc2.ui;

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
    
    private List<WrapperJCheckBox> groupCheckBoxList = new ArrayList<>();

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
            public void actionPerformed(ActionEvent e) {
                parentPane.enableUpdateButton();
                enableGroupCheckBoxes(false);
            }
        });

        allGroupsRadioButton.setBounds(125, 26, 275, 23);
        add(allGroupsRadioButton);

        selectGroupsRadioButton = new JRadioButton("Show to only these groups");
        selectGroupsRadioButton.addActionListener(new ActionListener() {
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


    protected void enableGroupCheckBoxes(boolean enable) {

        //        groupsScrollPane.setEnabled(enable);

        //        groupListBox.setEnabled(enable);

        for (WrapperJCheckBox wrapperJCheckBox : groupCheckBoxList) {
            wrapperJCheckBox.setEnabled(enable);
            System.out.println("debug 22  enableGroupCheckBoxes " + enable + " " + wrapperJCheckBox.getText());
        }
    }

    @Override
    public String getPluginTitle() {
        return "Problem Group Pane";
    }

    public void setProblem(Problem problem) {
        

        groupListModel.removeAllElements(); // clear checkbox
        groupCheckBoxList.clear();
        
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
        
        System.out.println("debug 22 populateProblemGroups "+problem.isAllView());
        
        enableGroupCheckBoxes(true);

        Group[] groups = getContest().getGroups();
        for (Group group : groups) {
            System.out.println("debug 22 populateProblemGroups "+problem.canView(group)+" "+group.getDisplayName());
            
            WrapperJCheckBox wrapperJCheckBox = new WrapperJCheckBox(group);
            if (allGroupsSelected) {
                wrapperJCheckBox.setSelected(true);
            } else if (problem.canView(group)) {
                wrapperJCheckBox.setSelected(true);
            } else {
                wrapperJCheckBox.setSelected(false);
            }
            
            wrapperJCheckBox.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    parentPane.enableUpdateButton();
                }
            });

            groupListModel.addElement(wrapperJCheckBox);
            groupCheckBoxList.add(wrapperJCheckBox);
        }
        
        enableGroupCheckBoxes(! allGroupsSelected);
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
                }
            }
        }
        
        System.out.println("debug 22 group length "+groups.size());
        for (Group group : groups) {
            System.out.println("debug 22 group  "+group.getDisplayName());
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
