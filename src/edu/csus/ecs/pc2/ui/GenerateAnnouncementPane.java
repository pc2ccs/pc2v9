// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
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

/**
 * Displays a GUI pane which allows generating an "announcement-type" clarification and sending it to selected teams and groups. Based on work done by Kutay Karakas making similar changes to
 * {@link SubmitClarificationPane}.
 * 
 * @author John C., PC2 Development Team, based on work by Kutay Karakas.
 *
 */
public class GenerateAnnouncementPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    public static final String ALL_TEAMS = "All Teams";

    public static final String GROUPS_AND_TEAMS = "Specific Groups and/or Teams";

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

    /**
     * This method initializes the SubmitAnnouncementPane.
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

                thisPane.add(Box.createVerticalStrut(20));
                thisPane.add(getSelectorsPane());
                
                thisPane.add(Box.createVerticalStrut(20));
                thisPane.add(getAnnouncementPane());
                
                thisPane.add(Box.createVerticalStrut(20));
                thisPane.add(getGroupsAndTeamsPane());

                thisPane.add(Box.createVerticalStrut(20));
                thisPane.add(getSubmitAnnouncementButton());

                thisPane.add(Box.createVerticalStrut(20));

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
     * Specifies the GridBagLout Constraints for the Teams pane.
     * @return a GridBagConstraint object for the Teams pane.
     */
    private GridBagConstraints getTeamsPaneConstraints() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 1;       //which "grid column" the object occupies (X position of desired column)
        gc.gridy = 2;       //which "grid row" the object occupies (Y position of desired row)
        gc.gridwidth = 1;   //the number of "grid columns" the object spans (default is 1...)
        gc.gridheight = 1;  //the number of "grid rows" the object spans (default is 1...)
        gc.insets = new Insets(2,2,2,2);  //minimum pixels outside the component but inside the grid display area
                                            //(format is top, left, bottom, right)
        gc.weightx = 0.4;   //medium priority for distributing space between columns
        gc.weighty = 0.4;   //medium priority for distributing space between columns
        return gc;
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

//            destinationComboBoxPane.setBorder(BorderFactory.createTitledBorder(null, "Announcement Destination(s)",
//                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

            destinationComboBoxPane.add(new JLabel("Announcement Destination(s):  "));
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

//                                TODO: need to make "Groups" scrollpane invisible if there are no groups; 
//                                Need to CHANGE the CONTENTS of the scrollpane if groups get added or removed dynamically (so, need to listen for the addition of a Group.class..)
//                                Need to support "All Teams", "Groups", "Teams", and "Groups and Teams" in the dropdown, and make the JScrollPane visibility match.
//                                if (getContest().doGroupsExist() && getContest().getNumberofGroups() != 1){
//                                    getAnnouncementDestinationComboBox().addItem(GROUPS);
//                                }

                        case ALL_TEAMS:

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    getGroupsPane().setVisible(false);
                                    getTeamsPane().setVisible(false);
                                }
                            });
                            break;

                        case GROUPS_AND_TEAMS:
                            
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    getGroupsPane().setVisible(true);
                                    getTeamsPane().setVisible(true);
//                                    getTeamsScrollPane().setVisible(true);
//                                    getGroupsScrollPane().setVisible(true);
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
        String[] destinationItems = { ALL_TEAMS, GROUPS_AND_TEAMS };
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
            groupsPanel.setVisible(false);
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
            teamsPanel.setVisible(false);
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
     * @return
     */
    private JCheckBoxJList getGroupsList() {

        if (groupsJList == null) {

            groupsJList = new JCheckBoxJList();
            Group[] allgroups = getContest().getGroups();

            // TODO: remove any groups that aren't supposed to see the currently selected PROBLEM!!

            Arrays.sort(allgroups, new GroupComparator());
            for (Group group : allgroups) {
                JCheckBox checkBox = new JCheckBox(group.getDisplayName());
                ((DefaultListModel<Object>) groupsListModel).addElement(checkBox);
            }

            groupsJList.setModel(groupsListModel);

        }

        return groupsJList;
    }

    /**
     * Initializes teamsJList to contain a "checkbox list" of groups which can see the currently-selected problem.
     * 
     * @return
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
                // However, the JScrollPane should at least take care of "long" names...
                JCheckBox checkBox = new JCheckBox(team.getClientId().getClientNumber() + " " + team.getDisplayName());
                ((DefaultListModel<Object>) teamsListModel).addElement(checkBox);
            }

            teamsJList.setModel(teamsListModel);

        }
        return teamsJList;
    }

    /**
     * This method initializes problemPane
     * 
     * @return javax.swing.JPanel
     */
    protected JPanel getProblemPane() {
        if (problemPane == null) {
            problemPane = new JPanel();

//            problemPane.setBorder(BorderFactory.createTitledBorder(null, "Problem", 
//                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            problemPane.add(new JLabel("Problem:   "));
            problemPane.add(getProblemComboBox());
        }
        return problemPane;
    }

    /**
     * This method initializes problemComboBox
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
     * This method initializes largeTextBoxPane
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

        if (getProblemComboBox().getSelectedIndex() <= 0) {
            showMessage("Please select a problem");
            return;
        }

        if (getAnnouncementTextArea().getText().trim().length() <= 0) {
            showMessage("Please enter text for the announcement");
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
        throw new UnsupportedOperationException("method getGroupsAndTeamsSelectedValues() not implemented");

        // return selectedValuesArray;
    }

    /**
     * Invokes the Contest Controller to submit a announcement clarification as specified by the input parameters.
     * Assumes that the "large text area" textbox contains a non-zero-length announcement.
     * Asks for a confirmation in a separate frame before sending the announcement.
     * 
     * @param problem
     * @param destinationCategories
     * @param ultimateDestinationsPacked
     */
    protected void submitAnnouncement(Problem problem, String destinationCategories, Object[] ultimateDestinationsPacked) {

        // TODO: Announcements should have an option of including "which problem they relate to".

        String announcement = announcementTextArea.getText().trim();

        String[] stringDestinations = new String[ultimateDestinationsPacked.length];
        ArrayList<ElementId> ultimateDestinationsGroup = new ArrayList<>();
        ArrayList<ClientId> ultimateDestinationsTeam = new ArrayList<>();

        for (int i = 0; i < ultimateDestinationsPacked.length; i++) { // Converts ultimateDestinationsPacked to html ready string
            Object associatedObject = (Object) ((JCheckBox) ultimateDestinationsPacked[i]);

            if (associatedObject instanceof ClientId) { // Team
                ultimateDestinationsTeam.add((ClientId) associatedObject);
                Account account = getContest().getAccount((ClientId) associatedObject);
                stringDestinations[i] = String.valueOf(account.getDisplayName());
            } else {// ElementId for group
                ultimateDestinationsGroup.add((ElementId) associatedObject);
                Group group = getContest().getGroup((ElementId) associatedObject);
                stringDestinations[i] = String.valueOf(group.getDisplayName());
            }

        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html>" + "    <head>" + "    <style>" + "        td {" + "            padding: 0 0 8px 0; /* top right bottom left */" + "            text-align: left;"
                + "            vertical-align: top;" + "        }" + "        .no-padding {" + "            padding-top: 0px;" + "            padding-bottom: 0px;" + "        }" + "         body{"
                + "            font-size: 1.1em;" + "        }" + "    </style>" + "    </head>" + "    <body>"
                + "    <div style = \"padding-bottom: 8px\">Do you wish to submit an announcement clarification for </div>" + "    <table style=\"width:100%; max-width: 700px\">" + "        <tr>"
                + "            <td style=\"width:20%\">Problem:</td>" + "            <td style = \"width:50%\"><font color=\"blue\">" + Utilities.forHTML(problem.toString()) + "</font></td>"
                + "        </tr>" + "        <tr>" + "            <td>Announcement:</td>" + "            <td><font color=\"blue\">" + Utilities.forHTML(announcement) + "</font></td>"
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
        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), stringBuilder.toString(), "Submit Clarification Confirm");

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            log.info("submit announcement for " + problem + " " + stringBuilder);
            getController().submitAnnouncement(problem, announcement, 
                    ultimateDestinationsGroup.toArray(new ElementId[ultimateDestinationsGroup.size()]),
                    ultimateDestinationsTeam.toArray(new ClientId[ultimateDestinationsTeam.size()]));
            announcementTextArea.setText("");

        } catch (Exception e) {
            showMessage("Error sending announcement, contact staff");
            log.log(Log.SEVERE, "Exception sending announcement: ", e);
        }
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

        @Override
        public void groupAdded(GroupEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void groupChanged(GroupEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void groupRemoved(GroupEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void groupsAdded(GroupEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void groupsChanged(GroupEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void groupRefreshAll(GroupEvent groupEvent) {
            // TODO Auto-generated method stub

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

        public void accountAdded(AccountEvent accountEvent) {
            // ignore, doesn't affect this pane
        }

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
            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore, does not affect this pane
        }

        public void accountsModified(AccountEvent accountEvent) {
            // check if it included this account
            boolean theyModifiedUs = false;
            for (Account account : accountEvent.getAccounts()) {
                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    theyModifiedUs = true;
                    initializePermissions();
                }
            }
            final boolean finalTheyModifiedUs = theyModifiedUs;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (finalTheyModifiedUs) {
                        updateGUIperPermissions();
                    }
                }
            });
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
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

}
