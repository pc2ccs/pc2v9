// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Vector;

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
import javax.swing.ToolTipManager;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.CategoryEvent;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.ICategoryListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IElementObject;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Submit Clarification Pane.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SubmitClarificationPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6395977089692171705L;
    
    public static final String CHECKBOX_GROUP_TEAM_PROPERTY = "group";
    
    public static final String CHECKBOX_TEAM_PROPERTY = "team";
    
 // the original height of the jcombobox was 22.  the groups jlist is 3 lines, so we added 46(?)
    // this makes it easier to make the groups list box bigger without having to change all the
    // control offsets below it.
    private static final int GROUPS_LIST_HEIGHT = 68;

    private Log log;  //  @jve:decl-index=0:

    private JPanel problemPane = null;
    
    private JPanel announcementDestinationPane = null;

    private JComboBox<Problem> problemComboBox = null;
    
    private JComboBox<String> announcementDestinationComboBox = null;

    private JPanel largeTextBoxPane = null;

    private JTextArea largeTextArea = null;

    private JButton submitClarificationButton = null;
    
    private JCheckBox submitAnnouncement = null;
      
    private boolean isTeam = false;
    
    private ListModel<Object> groupsListModel = new DefaultListModel<Object>();
    
    private JCheckBoxJList groupsJList = null;
    
    private JScrollPane groupsScrollPane = null;
    
    private JPanel groupsPanel = null;
    /**
     * This method initializes
     * 
     */
    public SubmitClarificationPane() {      
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    protected void initialize() {
        this.setLayout(null);
        this.setSize(new java.awt.Dimension(456, 285));
        SubmitClarificationPane current = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                isTeam = getContest().getClientId().getClientType().equals(ClientType.Type.TEAM);
                if (!isTeam) {
                    current.add(getSubmitAnnouncementCheckBox(),null);
                    current.add(getGroupsPanel(),null);
                    current.add(getAnnouncementDestinationPane(),null);
                }
                current.add(getProblemPane(), null);
                current.add(getLargeTextBoxPane(), null);
                current.add(getSubmitClarificationButton(), null);
            }
        });
                
        
        

        
    }


    private JPanel getAnnouncementDestinationPane() {
        if (announcementDestinationPane == null) {
            announcementDestinationPane = new JPanel();
            announcementDestinationPane.setLayout(new BorderLayout());
            announcementDestinationPane.setBounds(new java.awt.Rectangle(19,100 , 336, 54));
            announcementDestinationPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Destination", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            announcementDestinationPane.add(getAnnouncementDestinationComboBox(), java.awt.BorderLayout.CENTER);
        }
        return announcementDestinationPane;
    }
    /**
     * This method initializes announcementComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<String> getAnnouncementDestinationComboBox() {
        if (announcementDestinationComboBox == null) {
            announcementDestinationComboBox = new JComboBox<String>();
        }
        return announcementDestinationComboBox;
    }
    
    private JPanel getGroupsPanel() {
        if (groupsPanel == null) {
            groupsPanel = new JPanel();
            groupsPanel.setLocation(new java.awt.Point(400, 13));
            groupsPanel.setSize(new java.awt.Dimension(200, 200));
            groupsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Groups", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            groupsPanel.add(getGroupList(),null);
            groupsPanel.add(getGroupsScrollPane(),null);
            groupsPanel.setVisible(false);
        }
        
        return groupsPanel;
    }
    private JCheckBoxJList getGroupList() {
        
        if (groupsJList == null) {
            groupsJList = new JCheckBoxJList();
            
            
            ((DefaultListModel<Object>) groupsListModel).removeAllElements();
               
//            getGroupsJList().setSelectedIndex(-1); //Will need this to deselect anything
            
            groupsJList.setModel(groupsListModel);
            
            // ListSelectionListeners are called before JCheckBoxes get updated
            groupsJList.addPropertyChangeListener("change", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
//                    enableUpdateButton();
                    //TODO do something when a group is selected
                    //probably enable the submit announcement button
                }
            });
        }
        return groupsJList;
    }
    
    /**
     * This method initializes groups ScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getGroupsScrollPane() {
        if (groupsScrollPane == null) {
            groupsScrollPane = new JScrollPane();
            groupsScrollPane.setBounds(new java.awt.Rectangle(14, 291, 272, GROUPS_LIST_HEIGHT));
            groupsScrollPane.setViewportView(getGroupList());
        }
        return groupsScrollPane;
    }

    @Override
    public String getPluginTitle() {
        return "Submit Clarifications Pane";
    }

    /**
     * This method initializes problemPane
     * 
     * @return javax.swing.JPanel
     */
    protected JPanel getProblemPane() {
        if (problemPane == null) {
            problemPane = new JPanel();
            problemPane.setLayout(new BorderLayout());
            problemPane.setBounds(new java.awt.Rectangle(19, 13, 336, 54));
            problemPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Problem", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            problemPane.add(getProblemComboBox(), java.awt.BorderLayout.CENTER);
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
    protected JCheckBox getSubmitAnnouncementCheckBox() {
        if (submitAnnouncement == null) {
            submitAnnouncement = new JCheckBox();
            submitAnnouncement.setText("Generate Announcement");
            submitAnnouncement.setBounds(19, 80, 170, 20);
            ToolTipManager.sharedInstance().setDismissDelay(6000);
            submitAnnouncement.setToolTipText("Announcement clarification is a clarification that directly goes to teams with an answer but without question.");
            submitAnnouncement.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        getLargeTextBoxPane().setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                        getSubmitClarificationButton().setText("Submit Announcement");
                        getSubmitClarificationButton().setToolTipText("Click this button to submit your Announcement");
                    } else {
                        getLargeTextBoxPane().setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Question", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                        getSubmitClarificationButton().setText("Submit Clarification");
                        getSubmitClarificationButton().setToolTipText("Click this button to submit your Clarification");
                    }
                    
                }
            });
        }
        return submitAnnouncement;
    }

    /**
     * This method initializes largeTextBoxPane
     * 
     * @return javax.swing.JPanel
     */
    protected JPanel getLargeTextBoxPane() {
        if (largeTextBoxPane == null) {
            largeTextBoxPane = new JPanel();
            largeTextBoxPane.setLayout(new BorderLayout());
            if (isTeam) {
                largeTextBoxPane.setBounds(new java.awt.Rectangle(19,80,406,125));
            }
            else {
                largeTextBoxPane.setBounds(new java.awt.Rectangle(19,160,406,125));
            }
            largeTextBoxPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Question", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            largeTextBoxPane.add(getLargeTextArea(), java.awt.BorderLayout.CENTER);
        }
        return largeTextBoxPane;
    }

    /**
     * This method initializes questionTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getLargeTextArea() {
        if (largeTextArea == null) {
            largeTextArea = new JTextArea();
        }
        return largeTextArea;
    }


    /**
     * This method initializes submitClarificationButton
     * 
     * @return javax.swing.JButton
     */
    protected JButton getSubmitClarificationButton() {
        if (submitClarificationButton == null) {
            submitClarificationButton = new JButton();
            submitClarificationButton.setText("Submit Clarification");
            submitClarificationButton.setPreferredSize(new Dimension(200, 26));
            if (isTeam) {
                submitClarificationButton.setLocation(new Point(20, 219));
            }
            else {
                submitClarificationButton.setLocation(new Point(20, 299));
            }
            submitClarificationButton.setSize(new Dimension(200, 34));
            submitClarificationButton.setToolTipText("Click this button to submit your Clarification");
            submitClarificationButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    submit();
                    
                }
            });
        }
        return submitClarificationButton;
    }
    

    private void reloadProblems(){
        //TODO all problems shouldnt be listed! Selected Group effects which one that needs to be listed.
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
            if (problem.isActive()){
                getProblemComboBox().addItem(problem);
            }
        }

    }
    
    
    private void reloadAnnouncementDestinations(){
    
    getAnnouncementDestinationComboBox().removeAllItems();
    getAnnouncementDestinationComboBox().addItem("Select Destination");
    getAnnouncementDestinationComboBox().addItem("All Teams");
    
    if (getContest().doGroupsExist()){ //TODO maybe if there is only one group it should be triggered. Ask John
        getAnnouncementDestinationComboBox().addItem("Groups");
    }
    getAnnouncementDestinationComboBox().addItem("Specific Teams");
    
    getAnnouncementDestinationComboBox().addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                JComboBox<?> source = (JComboBox<?>) e.getSource();
                String selectedValue = (String) source.getSelectedItem();
                if (selectedValue.equals("Groups")){
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            reloadGroupsTeamsList();
                            getGroupsPanel().setVisible(true);
                            getGroupsPanel().setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Groups", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                        }
                    });
                }
                else if (selectedValue.equals("Specific Teams")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            reloadGroupsTeamsList();
                            getGroupsPanel().setVisible(true);
                            getGroupsPanel().setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Specific Teams", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                        }
                    });
                }
                else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            reloadGroupsTeamsList();
                            getGroupsPanel().setVisible(false);
                            getGroupsPanel().setBorder(javax.swing.BorderFactory.createTitledBorder(null, "-", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                        }
                    });
                }
                
            }
        }
    });




    }
    
    private void reloadGroupsTeamsList() {
        // TODO must be modified so that Groups,teams that shouldnt be displayed because
        // they do not have the problem selected should be implemented. 
        if (getAnnouncementDestinationComboBox().getSelectedItem().equals("Groups")){
            ((DefaultListModel<Object>) groupsListModel).removeAllElements();
            Group [] allgroups = getContest().getGroups();
            Arrays.sort(allgroups, new GroupComparator());
            for (Group group : allgroups) {
                JCheckBox checkBox = new JCheckBox(group.getDisplayName());
                checkBox.putClientProperty(CHECKBOX_GROUP_TEAM_PROPERTY, group);
                ((DefaultListModel<Object>) groupsListModel).addElement(checkBox);
            }
        }
        else if (getAnnouncementDestinationComboBox().getSelectedItem().equals("Specific Teams")) {
            ((DefaultListModel<Object>) groupsListModel).removeAllElements();
            Vector<Account> allTeams = getContest().getAccounts(ClientType.Type.TEAM);
            //TODO needs to be sorted
            for (Account teams : allTeams) {
                JCheckBox checkBox = new JCheckBox(teams.getDisplayName());
                checkBox.putClientProperty(CHECKBOX_GROUP_TEAM_PROPERTY, teams);
                ((DefaultListModel<Object>) groupsListModel).addElement(checkBox);
            }
        }
        else {
            ((DefaultListModel<Object>) groupsListModel).removeAllElements();
        }
        
        
    }

    private void populateGUI() {
  
        reloadProblems();
        reloadAnnouncementDestinations();
        reloadGroupsTeamsList();
        setButtonsActive(getContest().getContestTime().isContestRunning());
    }

    

    /**
     * Enable or disable submission buttons, Question pane and Problem drop-down list.
     * 
     * @param turnButtonsOn
     *            if true, buttons enabled.
     */
    private void setButtonsActive(final boolean turnButtonsOn) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getProblemComboBox().setEnabled(turnButtonsOn);
                getLargeTextArea().setEnabled(turnButtonsOn);
                getSubmitClarificationButton().setEnabled(turnButtonsOn);
            }
        });
        FrameUtilities.regularCursor(this);
    }
    
    protected void submit() {

        Problem problem = ((Problem) getProblemComboBox().getSelectedItem());
        String destination = (String) getAnnouncementDestinationComboBox().getSelectedItem();
        //TODO normal questions do not need to have a destination!
        //TODO treat empty groups well
        Object[] ultimateDestinationsPacked = getGroupList().getSelectedValues();
        
        
        if (getSubmitAnnouncementCheckBox().isSelected()) {
            if (getProblemComboBox().getSelectedIndex() < 1 && getAnnouncementDestinationComboBox().getSelectedIndex() < 1) {
                showMessage("Please select problem and destination");
                return;
            }
            if (getProblemComboBox().getSelectedIndex() < 1) {
                showMessage("Please select problem");
                return;
            }
            if (getAnnouncementDestinationComboBox().getSelectedIndex() < 1) {
                showMessage("Please select destination");
                return;
            }
            submitAnnouncement(problem,destination,ultimateDestinationsPacked);
        }
        else {
            if (getProblemComboBox().getSelectedIndex() < 1) {
                showMessage("Please select problem");
                return;
            }
            submitClarification(problem);
        }
        
        
        
        
        
        

    }
    
    protected void submitAnnouncement(Problem problem,String destination,Object[] ultimateDestinationsPacked) {
        //Get rid of String destination: that info is already inside Object[] ultimateDestinations
        
        String answerAnnouncement = largeTextArea.getText().trim();

        if (answerAnnouncement.length() < 1) {
            showMessage("Please enter a answer for announcement");
            return;
        }
        String[] stringArray = new String[ultimateDestinationsPacked.length];
        IElementObject[] ultimateDestinations = new IElementObject[ultimateDestinationsPacked.length];
        
        for (int i = 0; i < ultimateDestinationsPacked.length; i++) { //Converts ultimateDestinationsPacked to html ready string
            IElementObject associatedObject = (IElementObject) ((JCheckBox) ultimateDestinationsPacked[i]).getClientProperty(CHECKBOX_GROUP_TEAM_PROPERTY);
            stringArray[i] = String.valueOf(associatedObject);
            ultimateDestinations[i] = associatedObject;
            //            if (ultimateDestinations[i] instanceof Group) {
//                stringArray[i] = ((Group) ultimateDestinations[i]).toString();
//            } else if (ultimateDestinations[i] instanceof Account) {
//                stringArray[i] = ((Account) ultimateDestinations[i]).toString();
//            } else {
//                // Handle other types or use String.valueOf for generic conversion
//                stringArray[i] = String.valueOf(ultimateDestinations[i]);
//            }
        }
            
        String destinationString =  String.join(", ", stringArray);
        
        String confirmAnswer = "<HTML><FONT SIZE=+1>Do you wish to submit a announcement clarification for<BR><BR>" + "Problem:  <FONT COLOR=BLUE>" + Utilities.forHTML(problem.toString()) + "</FONT><BR><BR>"
                + "Announcement: <FONT COLOR=BLUE>" + Utilities.forHTML(answerAnnouncement)
                + "</FONT><BR><BR>"
                + "Destination: <FONT COLOR=BLUE>" + Utilities.forHTML(destination)+": "+Utilities.forHTML(destinationString) + "</FONT><BR><BR></FONT>";
        
        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), confirmAnswer, "Submit Clarification Confirm");

        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            log.info("submit announcement clarification for " + problem + " " + confirmAnswer);
            //TODO change the logs
            
            //FIXME Dont read values here since this only works for groups
            getController().submitAnnouncement(problem, answerAnnouncement,ultimateDestinations);
            largeTextArea.setText("");

        } catch (Exception e) {
            // TODO need to make this cleaner
            showMessage("Error sending announcement clar, contact staff");
            log.log(Log.SEVERE, "Exception sending announcement clarification ", e);
        }
    }
    
    protected void submitClarification(Problem problem) {
        String question = largeTextArea.getText().trim();

        if (question.length() < 1) {
            showMessage("Please enter a question");
            return;
        }
        
        String confirmQuestion = "<HTML><FONT SIZE=+1>Do you wish to submit a clarification for<BR><BR>" + "Problem:  <FONT COLOR=BLUE>" + Utilities.forHTML(problem.toString()) + "</FONT><BR><BR>"
        + "Question: <FONT COLOR=BLUE>" + Utilities.forHTML(question)
        + "</FONT><BR><BR></FONT>";
        
        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), confirmQuestion, "Submit Clarification Confirm");

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            log.info("submit clarification for " + problem + " " + question);
            getController().submitClarification(problem, question);
            largeTextArea.setText("");

        } catch (Exception e) {
            // TODO need to make this cleaner
            showMessage("Error sending clar, contact staff");
            log.log(Log.SEVERE, "Exception sending clarification ", e);
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
        
        /** This method exists to support differentiation between manual and automatic starts,
         * in the event this is desired in the future.
         * Currently it just delegates the handling to the contestStarted() method.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
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
//                setVisible(true);
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
        submitClarificationButton.setVisible(isAllowed(Permission.Type.SUBMIT_CLARIFICATION));
    }


} // @jve:decl-index=0:visual-constraint="10,10"
