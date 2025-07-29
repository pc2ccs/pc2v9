// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.CategoryEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.GroupEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.ICategoryListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IGroupListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.security.Permission;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import java.awt.Dimension;

/**
 * Displays a GUI pane which allows generating an "announcement-type" clarification and sending it to selected teams and groups. 
 * Based on work done by Kutay Karakas making similar changes to {@link SubmitClarificationPane}.
 * 
 * The GUI allows selection of a specific problem (or "General"), selection of either "All Teams" or "Specific Groups and/or Teams"
 * as the destination(s) for the announcement, and the entry of arbitrary text for the announcement.
 * 
 * @author John C., PC2 Development Team, based on work by Kutay Karakas.
 *
 */
public class GenerateAnnouncementPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    public static final String ALL_TEAMS = "All Teams";

    public static final String SPECIFIC_GROUPS_AND_TEAMS = "Specific Groups and/or Teams";

    private static final Object CHECKBOX_GROUP_OR_TEAM_PROPERTY = "GroupOrTeamId";

    private Log log;

    private JPanel problemPane = null;

    private JPanel destinationComboBoxPane = null;

    private JComboBox<Problem> problemComboBox = null;

    private JComboBox<String> destinationComboBox = null;

    private JPanel announcementTextAreaPane = null;

    private JTextArea announcementTextArea = null;

    private JButton submitAnnouncementButton = null;

    private ListModel<Object> groupsListModel = new DefaultListModel<Object>();

    private ListModel<Object> teamsListModel = new DefaultListModel<Object>();

    private JCheckBoxJList groupsJList = null;

    private JCheckBoxJList teamsJList = null;

    private JScrollPane groupsScrollPane = null;

    private JScrollPane teamsScrollPane = null;

    // the panels holding the groups and teams scrollpanes
    private JPanel groupsPanel = null;

    private JPanel teamsPanel;

    private JPanel selectorsPane;

    private JPanel destinationPane;

    private JScrollPane announcementTextAreaScrollPane;

    private JPanel announcementPane;

    private Component verticalGlue_2;
    private Component verticalGlue_3;
    private Component verticalGlue_4;
    private Component rigidArea;
    private Component rigidArea_1;
    private Component rigidArea_2;

    /**
     * Construct and initialize a new SubmitAnnouncementPane.
     */
    public GenerateAnnouncementPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this GenerateAnnouncementPane.
     * 
     */
    protected void initialize() {
        GenerateAnnouncementPane thisPane = this;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                thisPane.setLayout(new BoxLayout(thisPane, BoxLayout.Y_AXIS));

                add(getRigidArea());
                thisPane.add(getSelectorsPane());

                add(getRigidArea_1());
                thisPane.add(getAnnouncementPane());
                add(getRigidArea_2());
                
                thisPane.add(getVerticalGlue_2());
                thisPane.add(getGroupsAndTeamsPane());
                
                thisPane.add(getVerticalGlue_3());
                thisPane.add(getSubmitAnnouncementButton());
                
                thisPane.add(getVerticalGlue_4());

            }

        });
    }
    
    private JPanel getGroupsAndTeamsPane() {
        if (destinationPane == null) {
            destinationPane = new JPanel();
            destinationPane.setLayout(new FlowLayout());
            destinationPane.add(getGroupsPane());
            destinationPane.add(Box.createHorizontalStrut(20));
            destinationPane.add(getTeamsPane());
            destinationPane.setVisible(false);
        }
        return destinationPane;
    }
    
    private JPanel getSelectorsPane() {
        if (selectorsPane == null) {
            selectorsPane = new JPanel();
            selectorsPane.setLayout(new FlowLayout());
            selectorsPane.add(getProblemPane());
            selectorsPane.add(Box.createHorizontalStrut(40));
            selectorsPane.add(getDestinationComboBoxPane());
        }
        return selectorsPane;
    }

    /**
     * Initializes a pane that has the combobox to select announcement destinations such as 
     * "All Teams" or "Specific Groups and/or Teams".
     * 
     * @return a JPanel containing a JComboBox with destination choices.
     */
    private JPanel getDestinationComboBoxPane() {
        if (destinationComboBoxPane == null) {
            
            destinationComboBoxPane = new JPanel();

            destinationComboBoxPane.add(new JLabel("Announcement Destination: "));
            destinationComboBoxPane.add(getDestinationComboBox());
        }
        return destinationComboBoxPane;
    }

    /**
     * This method initializes the Announcement Destination combo box (the dropdown that 
     * allows choosing possible Announcement Destinations).
     * 
     * @return A {@link JComboBox} containing possible Announcement Destinations.
     */
    private JComboBox<String> getDestinationComboBox() {

        if (destinationComboBox == null) {

            String[] destinationChoices = getDestinationComboBoxChoices();
            DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>(destinationChoices);
            destinationComboBox = new JComboBox<String>(comboModel);

            destinationComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    JComboBox<?> source = (JComboBox<?>) e.getSource();
                    String selectedValue = (String) source.getSelectedItem();

                    switch (selectedValue) {

                        case ALL_TEAMS:

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    
                                    //clear group and team selections so that a subsequent submit doesn't use old selection data
                                    clearGroupAndTeamSelections();
                                    
                                    //the desirable approach is to always have the groups and teams pane VISIBLE, 
                                    //and here to simple DISABLE them (including disabling their checkboxes). 
                                    //However, many many hours of effort failed to produce a workable version of this,
                                    //so for now we'll just HIDE them when ALL_TEAMS is selected.
                                    getGroupsAndTeamsPane().setVisible(false);
                                }
                            });
                            break;

                        case SPECIFIC_GROUPS_AND_TEAMS:
                            
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    
                                    //the desirable approach is to always have the groups and teams pane VISIBLE, 
                                    //and here to simply ENABLE them (including enabling their checkboxes).
                                    //However, many many hours of effort failed to produce a workable version of this,
                                    //so for now we'll just make them VISIBLE again when SPECIFIC_GROUPS_AND_TEAMS is selected.
                                    getGroupsAndTeamsPane().setVisible(true);
                                }
                            });
                            break;

                        default:
                            // we have a dropdown box item that we don't recognize...
                            log.warning("Unrecognized selection from Announcement Destination ComboxBox: " + selectedValue);
                    }
                }
            });
        }

        return destinationComboBox;
    }

    
    /**
     * Returns an array of Strings containing all the destinations which should be listed in the "Select Destination" dropdown list.
     */
    private String[] getDestinationComboBoxChoices() {
        String[] destinationItems = { ALL_TEAMS, SPECIFIC_GROUPS_AND_TEAMS };
        return destinationItems;
    }

    /**
     * This panel contains JCheckBoxJlists for selecting Groups.
     * 
     * @return A JPanel containing a JScrollPane displaying a JCheckBoxJList of Groups.
     */
    private JPanel getGroupsPane() {
        if (groupsPanel == null) {
            groupsPanel = new JPanel();

            groupsPanel.setBorder(BorderFactory.createTitledBorder(null, "Groups", 
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            groupsPanel.add(getGroupsScrollPane());
        }

        return groupsPanel;
    }

    /**
     * This panel contains JCheckBoxJlists for selecting Teams.
     * 
     * @return A JPanel containing a JScrollPane displaying a JCheckBoxJList of teams.
     */
    private JPanel getTeamsPane() {
        if (teamsPanel == null) {
            teamsPanel = new JPanel();
            teamsPanel.setBorder(BorderFactory.createTitledBorder(null, "Teams", 
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            teamsPanel.add(getTeamsScrollPane());
        }

        return teamsPanel;
    }

    /**
     * This method initializes the Groups ScrollPane to hold a "checkbox list" of groups.
     *
     * @return javax.swing.JScrollPane containing groups.
     */
    private JScrollPane getGroupsScrollPane() {
        if (groupsScrollPane == null) {
            groupsScrollPane = new JScrollPane();

            groupsScrollPane.setViewportView(getGroupsList());
        }
        return groupsScrollPane;
    }

    /**
     * This method initializes the Teams ScrollPane to contain a list of all teams.
     *
     * @return A javax.swing.JScrollPane containing all teams.
     */
    private JScrollPane getTeamsScrollPane() {
        if (teamsScrollPane == null) {
            teamsScrollPane = new JScrollPane();

            teamsScrollPane.setViewportView(getTeamsList());
        }
        return teamsScrollPane;
    }

    /**
     * Initializes groupsJList to contain a "checkbox list" of groups which can see the currently-selected problem.
     * 
     * TODO:  currently there is no check to verify that groups added to the returned list are indeed allowed
     * to see the currently selected problem.
     * 
     * @return a {@link JCheckBoxJList} of groups.
     */
    private JCheckBoxJList getGroupsList() {

        if (groupsJList == null) {

            groupsJList = new JCheckBoxJList();
            Group[] allgroups = getContest().getGroups();

            // TODO: remove any groups that aren't supposed to see the currently selected PROBLEM!!

            Arrays.sort(allgroups, new GroupComparator());
            for (Group group : allgroups) {
                JCheckBox checkBox = new JCheckBox(group.getDisplayName() + "  ");
                checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, group.getElementId());
                ((DefaultListModel<Object>) groupsListModel).addElement(checkBox);
            }

            groupsJList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            groupsJList.setModel(groupsListModel);

        }

        return groupsJList;
    }

    /**
     * Initializes teamsJList to contain a "checkbox list" of teams which can see the currently-selected problem.
     * 
     * TODO: currently there is no check to verify that teams added to the JList are indeed allowed to see
     * the currently selected problem.
     * 
     * @return a {@link JCheckBoxJList} of teams.
     */
    private JCheckBoxJList getTeamsList() {

        if (teamsJList == null) {

            teamsJList = new JCheckBoxJList();
            Vector<Account> allTeamsVector = getContest().getAccounts(ClientType.Type.TEAM);

            // TODO: remove any teams that aren't supposed to see the currently selected PROBLEM!!

            Account[] allTeams = new Account[allTeamsVector.size()];
            allTeamsVector.toArray(allTeams);
            Arrays.sort(allTeams, new AccountComparator());

            for (Account team : allTeams) {
                // TODO if teams string is really wrong (meaning, long? or maybe non-ASCII chars?) it could create visual problems
                JCheckBox checkBox = new JCheckBox(team.getClientId().getClientNumber() + " " + team.getDisplayName() + " ");
                checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, team.getClientId());
                ((DefaultListModel<Object>) teamsListModel).addElement(checkBox);
            }

            teamsJList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            teamsJList.setModel(teamsListModel);

        }
        return teamsJList;
    }

    /**
     * This method initializes problemPane.
     * 
     * @return javax.swing.JPanel
     */
    protected JPanel getProblemPane() {
        if (problemPane == null) {
            problemPane = new JPanel();

            problemPane.add(new JLabel("Problem:   "));
            problemPane.add(getProblemComboBox());
        }
        return problemPane;
    }

    /**
     * This method initializes problemComboBox.
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<Problem> getProblemComboBox() {
        if (problemComboBox == null) {
            problemComboBox = new JComboBox<Problem>();
        }
        return problemComboBox;
    }

    private JPanel getAnnouncementPane() {
        if (announcementPane == null) {
            announcementPane = new JPanel();
            announcementPane.add(Box.createHorizontalStrut(20));
            announcementPane.add(getAnnouncementTextAreaPane());
            announcementPane.add(Box.createHorizontalStrut(20));            
        }
        return announcementPane;
    }
    /**
     * This method initializes largeTextBoxPane.
     * 
     * @return javax.swing.JPanel
     */
    protected JPanel getAnnouncementTextAreaPane() {
        if (announcementTextAreaPane == null) {
            announcementTextAreaPane = new JPanel();

            announcementTextAreaPane.setBorder(BorderFactory.createTitledBorder(null, "Announcement Text", 
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            
            announcementTextAreaPane.add(getAnnouncementTextAreaScrollPane());
        }
        return announcementTextAreaPane;
    }

    private JScrollPane getAnnouncementTextAreaScrollPane() {
        if (announcementTextAreaScrollPane == null) {
            announcementTextAreaScrollPane = new JScrollPane(getAnnouncementTextArea());
        }
        return announcementTextAreaScrollPane;
    }
    
    
    /**
     * This method initializes the text area for the Announcement text.
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getAnnouncementTextArea() {
        if (announcementTextArea == null) {
            announcementTextArea = new JTextArea(10, 60);
        }
        return announcementTextArea;
    }

    /**
     * This method initializes the Submit Announcement button.
     * 
     * @return javax.swing.JButton
     */
    protected JButton getSubmitAnnouncementButton() {
        if (submitAnnouncementButton == null) {
            submitAnnouncementButton = new JButton();
            submitAnnouncementButton.setText("Send Announcement");
            submitAnnouncementButton.setToolTipText("Click this button to send your Announcement");
            submitAnnouncementButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            submitAnnouncementButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    submit();
                }
            });
        }
        return submitAnnouncementButton;
    }
    
    private void reloadProblems() {
        // TODO all problems shouldnt be listed! Selected Group effects which one that needs to be listed.
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
            if (problem.isActive()) {
                getProblemComboBox().addItem(problem);
            }
        }

    }

    private void populateGUI() {

        reloadProblems();
        setButtonsActive(true);
    }

    /**
     * Enable or disable submission buttons, Announcement pane and Problem drop-down list.
     * 
     * @param turnButtonsOn
     *            if true, buttons enabled.
     */
    private void setButtonsActive(final boolean turnButtonsOn) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getProblemComboBox().setEnabled(turnButtonsOn);
                getAnnouncementTextArea().setEnabled(turnButtonsOn);
                getSubmitAnnouncementButton().setEnabled(turnButtonsOn);
            }
        });
        FrameUtilities.regularCursor(this);
    }

    /**
     * Submits announcement from judge, or displays message if judge hasn't filled in necessary data fields.
     */
    protected void submit() {

        // TODO: Announcements should have an option of including "which problem they relate to" IN THE ANNOUNCEMENT TEXT.

        //ensure a problem has been selected
        if (getProblemComboBox().getSelectedIndex() <= 0) {
            showMessage("Please select a problem");
            return;
        }

        //ensure some announcement text has been entered.
        if (getAnnouncementTextArea().getText().trim().length() <= 0) {
            showMessage("Please enter text for the announcement");
            return;
        }

        //ensure that if "Specific Groups and/or Teams" is currently selected, then at least one group or team has been checked
        if (getDestinationComboBox().getSelectedItem().equals(SPECIFIC_GROUPS_AND_TEAMS)  && getGroupsAndTeamsSelectedValues().length <= 0) {
            showMessage("You must select at least one Destination group or team (or else change \"Destination\" to \"All Teams\")");
            return;
        }
        
        Problem problem = ((Problem) getProblemComboBox().getSelectedItem());
        String destinationCategories = (String) getDestinationComboBox().getSelectedItem();
        Object[] ultimateDestinationsPacked = getGroupsAndTeamsSelectedValues();

        submitAnnouncement(problem, destinationCategories, ultimateDestinationsPacked);
    }

    /**
     * This method returns an array of Objects where each element is a Selected Value in either the Groups list 
     * or the Teams list. Note that Group Objects are ElementIds while Team objects are ClientIds.
     * 
     * @return an array containing Group ElementIds and Team ClientIds.
     */
    private Object[] getGroupsAndTeamsSelectedValues() {

        ArrayList<Object> selectedValuesArray = new ArrayList<Object>();
        
        // get all the selected groups (if any)
        for (Object obj : getGroupsList().getSelectedValues()) {
            selectedValuesArray.add(obj);
        }
        
        //add all the selected teams (if any)
        for (Object obj : getTeamsList().getSelectedValues()) {
            selectedValuesArray.add(obj);
        }
        
        //return an array containing all the selected group and team objects
        return selectedValuesArray.toArray();
    }

    /**
     * Invokes the Contest Controller to submit an "announcement clarification" as specified by the input parameters.
     * Assumes that the "large text area" textbox contains a non-zero-length announcement.
     * Asks for a confirmation in a separate dialog before sending the announcement.
     * 
     * @param problem the contest problem to which the announcement relates.
     * @param destinationCategories the category of the announcement destination (e.g. "ALL TEAMS" or "Specific Groups/Teams"))
     * @param ultimateDestinationsPacked an array whose elements identify each destination (e.g. each group or team)
     */
    protected void submitAnnouncement(Problem problem, String destinationCategories, Object[] ultimateDestinationsPacked) {

        // TODO: Announcements should have an option of including "which problem they relate to".
        // That is, there should be a mechanism for including the selected problem identification automatically
        // in the announcement text so that the user doesn't have to include that information as part of what they type 
        // for the announcement.  Maybe a checkbox "Include Problem ID in Announcement Text?" which defaults to "checked"?

        String announcement = announcementTextArea.getText().trim();

        String[] stringDestinations = new String[ultimateDestinationsPacked.length];
        ArrayList<ElementId> ultimateDestinationsGroup = new ArrayList<>();
        ArrayList<ClientId> ultimateDestinationsTeam = new ArrayList<>();

        for (int i = 0; i < ultimateDestinationsPacked.length; i++) { // Converts ultimateDestinationsPacked to html ready string

            Object groupOrTeamId = (Object) ((JCheckBox) ultimateDestinationsPacked[i]).getClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY);

            if (groupOrTeamId instanceof ClientId) { // Team
                ultimateDestinationsTeam.add((ClientId) groupOrTeamId);
                Account account = getContest().getAccount((ClientId) groupOrTeamId);
                stringDestinations[i] = String.valueOf(account.getDisplayName());
            } else {// ElementId for group
                ultimateDestinationsGroup.add((ElementId) groupOrTeamId);
                Group group = getContest().getGroup((ElementId) groupOrTeamId);
                stringDestinations[i] = String.valueOf(group.getDisplayName());
            }

        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html>" + "    <head>" + "    <style>" + "        td {" + "            padding: 0 0 8px 0; /* top right bottom left */" + "            text-align: left;"
                + "            vertical-align: top;" + "        }" + "        .no-padding {" + "            padding-top: 0px;" + "            padding-bottom: 0px;" + "        }" + "         body{"
                + "            font-size: 1.1em;" + "        }" + "    </style>" + "    </head>" + "    <body>"
                + "    <div style = \"padding-bottom: 8px\">Do you wish to submit an announcement clarification for </div>" + "    <table style=\"width:100%; max-width: 700px\">" + "        <tr>"
                + "            <td style=\"width:20%\">Problem:</td>" + "            <td style = \"width:50%\"><font color=\"blue\">" + Utilities.forHTML(problem.toString()) + "</font></td>"
                + "        </tr>" + "        <tr>" + "            <td>Announcement:</td>" + "            <td><font color=\"blue\">" +Utilities.forHTML(announcement) + "</font></td>"
                + "        </tr>" + "        <tr>");
        if (destinationCategories.equals(ALL_TEAMS)) {
            stringBuilder.append("            <td  class=\"no-padding\">Destination:</td>" + "            <td class=\"no-padding\"><font color=\"blue\">" + Utilities.forHTML(ALL_TEAMS)
                    + "</font></td>" + "        </tr>");
        } else {
            stringBuilder.append("            <td  class=\"no-padding\">Destination:</td>" + "            <td class=\"no-padding\"><font color=\"blue\">" + Utilities.forHTML(stringDestinations[0])
                    + "</font></td>" + "        </tr>");
        }
        for (int i = 1; i < stringDestinations.length; i++) {
            stringBuilder.append("        <tr>" + "            <td class=\"no-padding\"></td>" + "            <td class=\"no-padding\"><font color=\"blue\">" + Utilities.forHTML(stringDestinations[i])
                    + "</font></td>" + "        </tr>");
        }
        stringBuilder.append("    </table>" + "    </body>" + "</html>");
        
        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), stringBuilder.toString(), "Send Announcement Confirm");

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            log.info("submit announcement for " + problem + " " + stringBuilder);
            getController().submitAnnouncement(problem, announcement, 
                    ultimateDestinationsGroup.toArray(new ElementId[ultimateDestinationsGroup.size()]),
                    ultimateDestinationsTeam.toArray(new ClientId[ultimateDestinationsTeam.size()]));
            announcementTextArea.setText("");
            clearGroupAndTeamSelections();

        } catch (Exception e) {
            showMessage("Error sending announcement, contact staff");
            log.log(Log.SEVERE, "Exception sending announcement: ", e);
        }
    }

    /**
     * Forces all JCheckBoxes in both the Groups JCheckboxJList and the Teams JCheckboxJList to become unselected.
     */
    private void clearGroupAndTeamSelections() {
        
        DefaultListModel<Object> teamsModel = (DefaultListModel<Object>) getTeamsList().getModel();
        for (int i=0; i<teamsModel.getSize(); i++) {
            ((JCheckBox)teamsModel.getElementAt(i)).setSelected(false);
        }
        DefaultListModel<Object> groupsModel = (DefaultListModel<Object>) getGroupsList().getModel();
        for (int i=0; i<groupsModel.getSize(); i++) {
            ((JCheckBox)groupsModel.getElementAt(i)).setSelected(false);
        }

        //unselecting the checkboxes in the model does not automatically update the screen; need to force a repaint
        this.repaint();
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

        /**
         * This method exists to support differentiation between manual and automatic starts, in the event this is desired in the future. Currently it just delegates the handling to the
         * contestStarted() method.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
        }
    }

    private class GroupListenerImplementation implements IGroupListener {

        /**
         * Adds a group to the data model backing the Groups JList which is displayed in the Groups scrollpane.
         */
        @Override
        public void groupAdded(GroupEvent event) {

            Group addedGroup = event.getGroup();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // get the model which backs the JList which is displayed in the JScrollPane
                    DefaultListModel<Object> groupsModel = (DefaultListModel<Object>) getGroupsList().getModel();
                    // construct a checkbox for the new group
                    JCheckBox checkBox = new JCheckBox(addedGroup.getDisplayName());
                    checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, addedGroup.getElementId());
                    // add the new group checkbox to the model
                    groupsModel.addElement(checkBox);

                }
            });
        }

        /**
         * Updates a group which already exists in the data model backing the Groups JList which is displayed in the Groups scrollpane.
         */
        @Override
        public void groupChanged(GroupEvent event) {

            Group updatedGroup = event.getGroup();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    //get the model which backs the JList which is displayed in the JScrollPane
                    DefaultListModel<Object> groupsModel = (DefaultListModel<Object>) getGroupsList().getModel();
                    // find the group which needs to be updated in the list model
                    boolean found = false;
                    for (int i = 0; i < groupsModel.getSize(); i++) {
                        ElementId groupId = (ElementId) ((JCheckBox) groupsModel.getElementAt(i)).getClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY);
                        if (groupId.equals(updatedGroup.getElementId())) {
                            // found the group in the JList
                            found = true;
                            //construct a checkbox for the new group
                            JCheckBox checkBox = new JCheckBox(updatedGroup.getDisplayName());
                            checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, updatedGroup.getElementId());
                            //replace the old group with the new group in the model
                            groupsModel.set(i, checkBox);
                            //we're done with the required update
                            break;
                        }
                    }
                    if (!found) {
                        log.warning("GenerateAnnouncement.GroupListenerImplementation.groupChanged(): Unable to find updated group in groups model: " + updatedGroup);
                    }

                }
            });
        }

        /**
         * This method listens for "remove group" messages.  Note however that in the current (2/1/2025) implementation of
         * PC2 there is no supported mechanism for removing a group :( ...
         */
        @Override
        public void groupRemoved(GroupEvent event) {
            Group removedGroup = event.getGroup();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    //get the model which backs the JList which is displayed in the JScrollPane
                    DefaultListModel<Object> groupsModel = (DefaultListModel<Object>) getGroupsList().getModel();
                    // find the group which needs to be removed from the list model
                    boolean found = false;
                    for (int i = 0; i < groupsModel.getSize(); i++) {
                        ElementId groupId = (ElementId) ((JCheckBox) groupsModel.getElementAt(i)).getClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY);
                        if (groupId.equals(removedGroup.getElementId())) {
                            // found the group in the JList
                            found = true;
                            //remove the group from the model
                            groupsModel.remove(i);
                            //we're done with the required update
                            break;
                        }
                    }
                    if (!found) {
                        log.warning("GenerateAnnouncement.GroupListenerImplementation.groupRemoved(): Unable to find group to be removed from groups model:" + removedGroup);
                    }

                }
            });
        }

        /**
         * Adds a set of groups to the data model backing the Groups JList displayed in the Groups scrollpane.
         */
        @Override
        public void groupsAdded(GroupEvent event) {

            Group[] addedGroups = event.getGroups();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // get the model which backs the JList which is displayed in the JScrollPane
                    DefaultListModel<Object> groupsModel = (DefaultListModel<Object>) getGroupsList().getModel();

                    // add each of the new groups
                    for (Group newGroup : addedGroups) {
                        // construct a checkbox for the new group
                        JCheckBox checkBox = new JCheckBox(newGroup.getDisplayName());
                        checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, newGroup.getElementId());
                        // add the new group to the model
                        groupsModel.addElement(checkBox);
                    }
                }
            });
        }

        @Override
        public void groupsChanged(GroupEvent event) {
            
            Group[] changedGroups = event.getGroups();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // get the model which backs the JList which is displayed in the JScrollPane
                    DefaultListModel<Object> groupsModel = (DefaultListModel<Object>) getGroupsList().getModel();

                    ArrayList<String> groupsNotFound = new ArrayList<String>() ;
                    
                    // process each of the groups needing updating
                    for (Group groupToUpdate : changedGroups) {
                        
                        // find the current group in the list model
                        boolean currentGroupFound = false;
                        for (int i = 0; i < groupsModel.getSize(); i++) {
                            ElementId groupId = (ElementId) ((JCheckBox) groupsModel.getElementAt(i)).getClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY);
                            if (groupId.equals(groupToUpdate.getElementId())) {
                                // found the current group in the JList
                                currentGroupFound = true;
                                // construct a checkbox for the new group
                                JCheckBox checkBox = new JCheckBox(groupToUpdate.getDisplayName());
                                checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, groupToUpdate.getElementId());
                                // replace the old group with the new group in the model
                                groupsModel.set(i, checkBox);
                                // we're done with the current group
                                break;
                            }
                        }
                        
                        if (!currentGroupFound) {
                            groupsNotFound.add(groupToUpdate.toString());
                        }
                    }
                    if (groupsNotFound.size()>0) {
                        log.warning("GenerateAnnouncement.GroupListenerImplementation.groupsChanged(): Unable to find groups in model:" + groupsNotFound);
                    }

                }
            });
        }

        /**
         * Removes all existing groups from the data model backing the Groups JList displayed in the Groups scrollpane,
         * then adds to the model the groups specified in the received event.
         */
        @Override
        public void groupRefreshAll(GroupEvent groupEvent) {
        	
            Group[] addedGroups = groupEvent.getGroups();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // get the model which backs the JList which is displayed in the JScrollPane
                    DefaultListModel<Object> groupsModel = (DefaultListModel<Object>) getGroupsList().getModel();
                    
                    //clear the model
                    groupsModel.removeAllElements();

                    // add each of the new groups to the model
                    for (Group newGroup : addedGroups) {
                        // construct a checkbox for the new group
                        JCheckBox checkBox = new JCheckBox(newGroup.getDisplayName());
                        checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, newGroup.getElementId());
                        // add the new group to the model
                        groupsModel.addElement(checkBox);
                    }
                }
            });
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
        getContest().addGroupListener(new GroupListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
//                    setVisible(true);
            }
        });
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class AccountListenerImplementation implements IAccountListener {

        /**
         * If the account specified in the received AccountEvent is a Team account, adds that team to the
         * data model backing the Teams JList which is displayed in the Teams scrollpane.
         */
        public void accountAdded(AccountEvent accountEvent) {
            Account addedAccount = accountEvent.getAccount();
            if (addedAccount.isTeam()) {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // get the model which backs the JList which is displayed in the JScrollPane
                        DefaultListModel<Object> teamsModel = (DefaultListModel<Object>) getTeamsList().getModel();
                        // construct a checkbox for the new team
                        JCheckBox checkBox = new JCheckBox(addedAccount.getDisplayName());
                        checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, addedAccount.getClientId());
                        // add the new team checkbox to the model
                        teamsModel.addElement(checkBox);
                    }
                });
            } else {
                // ignore, not a team so doesn't affect this pane
            }
        }

        /**
         * If the account specified in the received AccountEvent is "this account", updates the permissions associated with the account; 
         * otherwise, if the specified account is a Team account, updates the team entry in the 
         * data model backing the Teams JList which is displayed in the Teams scrollpane, or logs a warning if the team isn't 
         * found in the data model.
         */
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
            } else if (account.isTeam()) {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // get the model which backs the JList which is displayed in the JScrollPane
                        DefaultListModel<Object> teamsModel = (DefaultListModel<Object>) getTeamsList().getModel();
                        // find the team which needs to be updated in the list model
                        boolean found = false;
                        for (int i = 0; i < teamsModel.getSize(); i++) {
                            ClientId teamId = (ClientId) ((JCheckBox) teamsModel.getElementAt(i)).getClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY);
                            if (teamId.equals(account.getClientId())) {
                                // found the team in the JList
                                found = true;
                                // construct a checkbox for the new team
                                JCheckBox checkBox = new JCheckBox(account.getDisplayName());
                                checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, account.getClientId());
                                // replace the old team with the new team in the model
                                teamsModel.set(i, checkBox);
                                // we're done with the required update
                                break;
                            }
                        }
                        if (!found) {
                            log.warning("GenerateAnnouncement.AccountListenerImplementation.accountModified(): Unable to find updated team in teams model: " + account);
                        }

                    }
                });
            }
        }

        /**
         * Examines each account in the list of accounts contained in the received AccountEvent; for every account which is a team
         * updates the data model backing the Teams JList which is displayed in the Teams scrollpane.
         */
        public void accountsAdded(AccountEvent accountEvent) {
            Account[] addedAccounts = accountEvent.getAccounts();
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // get the model which backs the JList which is displayed in the JScrollPane
                    DefaultListModel<Object> teamsModel = (DefaultListModel<Object>) getTeamsList().getModel();

                    // examine each of the new accounts
                    for (Account newAccount : addedAccounts) {
                        
                        //only update the model if the account is a team
                        if (newAccount.isTeam()) {
                            // construct a checkbox for the new team
                            JCheckBox checkBox = new JCheckBox(newAccount.getDisplayName());
                            checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, newAccount.getClientId());
                            // add the new team to the model
                            teamsModel.addElement(checkBox);
                        }
                    }
                }
            });
        }

        /**
         * Updates the data model backing the Teams JList for all team accounts specified in the received AccountEvent; logs 
         * a warning if any of the specified teams are not found in the data model.
         */
        public void accountsModified(AccountEvent accountEvent) {

            Account[] changedAccounts = accountEvent.getAccounts();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // get the model which backs the JList which is displayed in the JScrollPane
                    DefaultListModel<Object> teamsModel = (DefaultListModel<Object>) getTeamsList().getModel();

                    ArrayList<String> teamsNotFound = new ArrayList<String>() ;
                    
                    // examine each of the specified (modified) accounts
                    for (Account accountToUpdate : changedAccounts) {
                        
                        //we're only interested in updating Team account info in the data model
                        if (accountToUpdate.isTeam()) {
                            // find the current team in the list model
                            boolean currentTeamFound = false;
                            for (int i = 0; i < teamsModel.getSize(); i++) {
                                ClientId clientId = (ClientId) ((JCheckBox) teamsModel.getElementAt(i)).getClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY);
                                if (clientId.equals(accountToUpdate.getClientId())) {
                                    // found the current team in the JList
                                    currentTeamFound = true;
                                    // construct a checkbox for the new team
                                    JCheckBox checkBox = new JCheckBox(accountToUpdate.getDisplayName());
                                    checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, accountToUpdate.getClientId());
                                    // replace the old team with the new team in the model
                                    teamsModel.set(i, checkBox);
                                    // we're done with the current team
                                    break;
                                }
                            }
                            if (!currentTeamFound) {
                                teamsNotFound.add(accountToUpdate.toString());
                            } 
                        }
                    }
                    if (teamsNotFound.size()>0) {
                        log.warning("GenerateAnnouncement.AccountListenerImplementation.accountsModified(): Unable to find team accounts in model:" + teamsNotFound);
                    }

                }
            });

        }

        /**
         * Removes all accounts from the data model backing the Teams JList which is displayed in the Teams scrollpane;
         * adds to the data model each Team specified in the received AccountEvent.
         * 
         * @param accountEvent
         */
        public void accountsRefreshAll(AccountEvent accountEvent) {
            Account[] addedAccounts = accountEvent.getAccounts();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // get the model which backs the JList which is displayed in the JScrollPane
                    DefaultListModel<Object> teamsModel = (DefaultListModel<Object>) getTeamsList().getModel();

                    // clear the model
                    teamsModel.removeAllElements();

                    // check each of the added accounts
                    for (Account newAccount : addedAccounts) {

                        // we only want to add Team accounts to the team data model
                        if (newAccount.isTeam()) {
                            // construct a checkbox for the new team
                            JCheckBox checkBox = new JCheckBox(newAccount.getDisplayName());
                            checkBox.putClientProperty(CHECKBOX_GROUP_OR_TEAM_PROPERTY, newAccount.getClientId());
                            // add the new team to the model
                            teamsModel.addElement(checkBox);
                        }
                    }
                }
            });
        }
    }

    private void updateGUIperPermissions() {
        submitAnnouncementButton.setVisible(isAllowed(Permission.Type.SUBMIT_CLARIFICATION));
    }

    @Override
    public String getPluginTitle() {
        return "Generate Announcement Pane";
    }

    /**
     * This method exists just for testing purposes -- it allows generating a JFrame containing (just) this Announcement pane.
     * 
     * @param args
     */
    public static void main(String[] args) {

        JFrame f = new JFrame("Generate Announcement");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GenerateAnnouncementPane announcementPane = new GenerateAnnouncementPane();
        // need to generate a proper contest and controller...
        announcementPane.setContestAndController(null, null);
        f.getContentPane().add(announcementPane);
        f.pack();
        f.setSize(950, 750);
        f.setLocationRelativeTo(null); // center on screen
        f.setVisible(true);

    }

    private Component getVerticalGlue_2() {
        if (verticalGlue_2 == null) {
        	verticalGlue_2 = Box.createVerticalGlue();
        }
        return verticalGlue_2;
    }
    private Component getVerticalGlue_3() {
        if (verticalGlue_3 == null) {
        	verticalGlue_3 = Box.createVerticalGlue();
        }
        return verticalGlue_3;
    }
    private Component getVerticalGlue_4() {
        if (verticalGlue_4 == null) {
        	verticalGlue_4 = Box.createVerticalGlue();
        }
        return verticalGlue_4;
    }
    private Component getRigidArea() {
        if (rigidArea == null) {
        	rigidArea = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea;
    }
    private Component getRigidArea_1() {
        if (rigidArea_1 == null) {
        	rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_1;
    }
    private Component getRigidArea_2() {
        if (rigidArea_2 == null) {
        	rigidArea_2 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_2;
    }
}
