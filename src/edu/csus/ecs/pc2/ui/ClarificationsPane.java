package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import java.awt.GridLayout;
import javax.swing.JSplitPane;
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

/**
 * Shows clarifications in a list box.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ClarificationsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7483784815760107250L;

    private JPanel clarificationButtonPane = null;

    private MCLB clarificationListBox = null;

    private JButton giveButton = null;

    private JButton takeButton = null;

    private JButton editButton = null;

    private JButton generateClarificationButton = null;

    private JButton filterButton = null;

    private PermissionList permissionList = new PermissionList();

    private JButton requestButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private AnswerClarificationFrame answerClarificationFrame;

    private JPanel centerPane = null;

    private JSplitPane clarificationSplitPane = null;

    private JPanel clarificationPane = null;

    private JTextArea questionTextArea = null;

    private JPanel answerPane = null;

    private JTextArea answerTextArea = null;

    /**
     * This method initializes
     * 
     */
    public ClarificationsPane() {
        super();
        initialize();
    }

    /**
     * @author pc2@ecs.csus.edu
     * 
     */
    public class LanguageListenerImplementation implements ILanguageListener {

        public void languageAdded(LanguageEvent event) {
            // ignore, does not affect this pane
        }

        public void languageChanged(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void languageRemoved(LanguageEvent event) {
            // ignore, does not affect this pane
        }

    }

    /**
     * @author pc2@ecs.csus.edu
     * 
     */
    public class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
            // ignore, does not affect this pane
        }

        public void problemChanged(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void problemRemoved(ProblemEvent event) {
            // ignore, does not affect this pane
        }

    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(622, 229));
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getClarificationButtonPane(), java.awt.BorderLayout.SOUTH);

        answerClarificationFrame = new AnswerClarificationFrame();

    }

    @Override
    public String getPluginTitle() {
        return "Clarifications Pane";
    }

    /**
     * This method initializes clarificationButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClarificationButtonPane() {
        if (clarificationButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            clarificationButtonPane = new JPanel();
            clarificationButtonPane.setLayout(flowLayout);
            clarificationButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            clarificationButtonPane.add(getRequestButton(), null);
            clarificationButtonPane.add(getGiveButton(), null);
            clarificationButtonPane.add(getTakeButton(), null);
            clarificationButtonPane.add(getFilterButton(), null);
            clarificationButtonPane.add(getEditButton(), null);
            clarificationButtonPane.add(getGenerateClarificationButton(), null);
        }
        return clarificationButtonPane;
    }

    /**
     * This method initializes clarificationListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getClarificationListBox() {
        if (clarificationListBox == null) {
            clarificationListBox = new MCLB();

            clarificationListBox.addListboxListener(new com.ibm.webrunner.j2mclb.event.ListboxListener() {
                public void rowSelected(com.ibm.webrunner.j2mclb.event.ListboxEvent e) {
                    showSelectedClarification();
                }
                public void rowDeselected(com.ibm.webrunner.j2mclb.event.ListboxEvent e) {
                    showSelectedClarification();
                }
            });

            Object[] cols = { "Site", "Team", "Clar Id", "Time", "Status", "Judge", "Sent to", "Problem", "Question", "Answer" };
            clarificationListBox.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Site
            clarificationListBox.setColumnSorter(0, sorter, 1);

            // Team
            clarificationListBox.setColumnSorter(1, sorter, 2);

            // Clar Id
            clarificationListBox.setColumnSorter(2, numericStringSorter, 3);

            // Time
            clarificationListBox.setColumnSorter(3, numericStringSorter, 4);

            // Status
            clarificationListBox.setColumnSorter(4, sorter, 5);

            // Judge
            clarificationListBox.setColumnSorter(5, sorter, 6);

            // Sent to
            clarificationListBox.setColumnSorter(6, sorter, 7);

            // Problem
            clarificationListBox.setColumnSorter(7, sorter, 8);

            // Question
            clarificationListBox.setColumnSorter(8, sorter, 9);

            // Answer
            clarificationListBox.setColumnSorter(9, sorter, 10);

            clarificationListBox.autoSizeAllColumns();

        }
        return clarificationListBox;
    }
    

    protected void showSelectedClarification() {
        
        int selectedClarificationIndex = getClarificationListBox().getSelectedIndex();
        
        if (selectedClarificationIndex == -1){
            getAnswerPane().setVisible(false);
            getAnswerTextArea().setText("");
            getQuestionTextArea().setText("");
            String clarificationTitle = "Clarification ";
            clarificationPane.setBorder(BorderFactory.createTitledBorder(null, clarificationTitle, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            return;
        }

        ElementId clarificationId = (ElementId) getClarificationListBox().getKeys()[selectedClarificationIndex];
        
        Clarification clarification = getContest().getClarification(clarificationId);
        
        if (clarification != null){
            
            String clarificationTitle = "Clarification "+clarification.getNumber()+"  from "+getTeamDisplayName(clarification.getSubmitter())+" (Site "+clarification.getSiteNumber()+")";
            clarificationPane.setBorder(BorderFactory.createTitledBorder(null, clarificationTitle, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            
            getQuestionTextArea().setText(clarification.getQuestion());
            
            if (clarification.getAnswer() != null){
                showMessage("debug Do show answer");
                getAnswerPane().setVisible(true);
                getAnswerTextArea().setText(clarification.getAnswer());
            } else {
                // Don't show answer pane if no answer
                showMessage("debug Do not show answer");
                getAnswerPane().setVisible(false);
            }
        }

        // Show preview pane
        getClarificationSplitPane().setVisible(true);
    }

    public void updateClarificationRow(final Clarification clarification, final ClientId whoChangedId) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildClarificationRow(clarification, whoChangedId);
                int rowNumber = clarificationListBox.getIndexByKey(clarification.getElementId());
                if (rowNumber == -1) {
                    clarificationListBox.addRow(objects, clarification.getElementId());
                } else {
                    clarificationListBox.replaceRow(objects, rowNumber);
                }
                clarificationListBox.autoSizeAllColumns();
                clarificationListBox.sort();
            }
        });
    }

    private Object[] buildClarificationRow(Clarification clar, ClientId clientId) {

        int cols = clarificationListBox.getColumnCount();
        Object[] obj = new Object[cols];

        // Object[] cols = {"Site", "Team", "Clar Id", "Time", "Status", "Judge", "Sent to", "Problem", "Question", "Answer" };

        obj[0] = getSiteTitle(clar.getSubmitter().getSiteNumber());
        obj[1] = getTeamDisplayName(clar.getSubmitter());
        obj[2] = clar.getNumber();
        obj[3] = clar.getElapsedMins();

        boolean isTeam = getContest().getClientId().getClientType().equals(ClientType.Type.TEAM);

        if (isTeam) {
            obj[4] = "New";
            if (clar.isAnswered()) {
                obj[4] = "Answered";
            }
        } else {
            obj[4] = clar.getState();
        }

        obj[5] = "";
        if (clar.isAnswered()) {

            if (clar.getWhoJudgedItId() == null || isTeam) {
                obj[5] = "";
            } else {
                obj[5] = clar.getWhoJudgedItId().getName();
            }
        }

        if (clar.isSendToAll()) {
            obj[6] = "All Teams";
        } else {
            obj[6] = getTeamDisplayName(clar.getSubmitter());
        }
        obj[7] = getProblemTitle(clar.getProblemId());
        obj[8] = clar.getQuestion();
        obj[9] = clar.getAnswer();

        return obj;
    }

    void reloadListBox() {
        clarificationListBox.removeAllRows();
        Clarification[] clarifications = getContest().getClarifications();

        for (Clarification clarification : clarifications) {
            addClarificationRow(clarification);
        }
    }

    private void addClarificationRow(Clarification clarification) {
        Object[] objects = buildClarificationRow(clarification, null);
        clarificationListBox.addRow(objects, clarification.getElementId());
        clarificationListBox.autoSizeAllColumns();
        clarificationListBox.sort();
    }

    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */

    // $HeadURL$
    public class ClarificationListenerImplementation implements IClarificationListener {

        public void clarificationAdded(ClarificationEvent event) {
            updateClarificationRow(event.getClarification(), event.getWhoModifiedClarification());
        }

        public void clarificationChanged(ClarificationEvent event) {
            updateClarificationRow(event.getClarification(), event.getWhoModifiedClarification());
        }

        public void clarificationRemoved(ClarificationEvent event) {
            // TODO Auto-generated method stub
        }

    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        answerClarificationFrame.setContestAndController(inContest, inController);

        initializePermissions();

        getContest().addClarificationListener(new ClarificationListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addLanguageListener(new LanguageListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                reloadListBox();
            }
        });
    }

    private String getProblemTitle(ElementId problemId) {
        Problem problem = getContest().getProblem(problemId);
        if (problem != null) {
            return problem.toString();
        }
        return "Problem ?";
    }

    private String getSiteTitle(int siteNumber) {
        // TODO Auto-generated method stub
        return "Site " + siteNumber;
    }

    private String getTeamDisplayName(ClientId clientId) {
        // TODO code change this depending on how to display team names
        
//        Account account = getContest().getAccount(clientId);
//        if (account != null) {
//            return account.getDisplayName();
//        }
        
        return clientId.getName();
    }

    /**
     * This method initializes getButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGiveButton() {
        if (giveButton == null) {
            giveButton = new JButton();
            giveButton.setText("Give");
            giveButton.setMnemonic(java.awt.event.KeyEvent.VK_G);
            giveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // TODO code give
                    showMessage("Give not implemented");
                }
            });
        }
        return giveButton;
    }

    /**
     * This method initializes takeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getTakeButton() {
        if (takeButton == null) {
            takeButton = new JButton();
            takeButton.setText("Take");
            takeButton.setMnemonic(java.awt.event.KeyEvent.VK_T);
            takeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // TODO code take clar
                    showMessage("Take not implemented");
                }
            });
        }
        return takeButton;
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
            editButton.setMnemonic(java.awt.event.KeyEvent.VK_E);
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // TODO code edit clarification
                    showMessage("Edit not implemented");
                }
            });
        }
        return editButton;
    }

    /**
     * This method initializes generateClarificationButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGenerateClarificationButton() {
        if (generateClarificationButton == null) {
            generateClarificationButton = new JButton();
            generateClarificationButton.setText("Generate New Clar");
            generateClarificationButton.setMnemonic(java.awt.event.KeyEvent.VK_N);
            generateClarificationButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // TODO code generate new clar
                    showMessage("Generate New Clar not implemented, yet");
                }
            });
        }
        return generateClarificationButton;
    }

    /**
     * This method initializes filterButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getFilterButton() {
        if (filterButton == null) {
            filterButton = new JButton();
            filterButton.setText("Filter");
            filterButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    reloadListBox();
                    showMessage("");
                }
            });
        }
        return filterButton;
    }

    private boolean isAllowed(Permission.Type type) {
        return permissionList.isAllowed(type);
    }

    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        permissionList.clearAndLoadPermissions(account.getPermissionList());
    }

    private void updateGUIperPermissions() {

        requestButton.setVisible(isAllowed(Permission.Type.ANSWER_CLARIFICATION));
        editButton.setVisible(isAllowed(Permission.Type.EDIT_CLARIFICATION));
        giveButton.setVisible(isAllowed(Permission.Type.GIVE_CLARIFICATION));
        takeButton.setVisible(isAllowed(Permission.Type.TAKE_CLARIFICATION));
        generateClarificationButton.setVisible(isAllowed(Permission.Type.GENERATE_NEW_CLARIFICATION));

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
                        reloadListBox();
                    }
                });

            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        reloadListBox();
                    }
                });

            }
        }
    }

    /**
     * This method initializes requestButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRequestButton() {
        if (requestButton == null) {
            requestButton = new JButton();
            requestButton.setText("Request");
            requestButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            requestButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    requestSelectedClarification();
                }
            });
        }
        return requestButton;
    }

    protected void requestSelectedClarification() {

        int[] selectedIndexes = clarificationListBox.getSelectedIndexes();

        if (selectedIndexes.length < 1) {
            showMessage("Please select a run ");
            return;
        }

        try {
            ElementId elementId = (ElementId) clarificationListBox.getKeys()[selectedIndexes[0]];
            Clarification clarificationToAnswer = getContest().getClarification(elementId);

            if ((!clarificationToAnswer.getState().equals(ClarificationStates.NEW)) || clarificationToAnswer.isDeleted()) {
                showMessage("Not allowed to request run, already judged");
                return;
            }

            answerClarificationFrame.setClarification(clarificationToAnswer);
            answerClarificationFrame.setVisible(true);
        } catch (Exception e) {
            getController().getLog().log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to answer clarification, check log");
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
            messagePane.setPreferredSize(new java.awt.Dimension(32, 32));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
            }
        });
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            centerPane = new JPanel();
            centerPane.setLayout(gridLayout);
            centerPane.add(getClarificationListBox(), null);
            centerPane.add(getClarificationSplitPane(), null);
        }
        return centerPane;
    }

    /**
     * This method initializes jSplitPane
     * 
     * @return javax.swing.JSplitPane
     */
    private JSplitPane getClarificationSplitPane() {
        if (clarificationSplitPane == null) {
            clarificationSplitPane = new JSplitPane();
            clarificationSplitPane.setPreferredSize(new Dimension(120, 120));
            clarificationSplitPane.setTopComponent(getClarificationPane());
            clarificationSplitPane.setBottomComponent(getAnswerPane());
            clarificationSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        }
        return clarificationSplitPane;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClarificationPane() {
        if (clarificationPane == null) {
            clarificationPane = new JPanel();
            clarificationPane.setLayout(new BorderLayout());
            clarificationPane.setPreferredSize(new Dimension(10, 40));
            clarificationPane.setBorder(BorderFactory.createTitledBorder(null, "Clarification", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            clarificationPane.add(getQuestionTextArea(), java.awt.BorderLayout.CENTER);
        }
        return clarificationPane;
    }

    /**
     * This method initializes jTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getQuestionTextArea() {
        if (questionTextArea == null) {
            questionTextArea = new JTextArea();
        }
        return questionTextArea;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAnswerPane() {
        if (answerPane == null) {
            answerPane = new JPanel();
            answerPane.setLayout(new BorderLayout());
            answerPane.setBorder(BorderFactory.createTitledBorder(null, "Answer", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            answerPane.add(getAnswerTextArea(), java.awt.BorderLayout.CENTER);
        }
        return answerPane;
    }

    /**
     * This method initializes jTextArea1
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getAnswerTextArea() {
        if (answerTextArea == null) {
            answerTextArea = new JTextArea();
        }
        return answerTextArea;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
