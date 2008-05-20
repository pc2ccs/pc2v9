package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Edit Filter GUI.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditFilterPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1866852944568248601L;

    private JCheckBox filterOnCheckBox = null;

    private JPanel problemFrame = null;

    private JPanel bottomPanel = null;

    private JPanel languagePane = null;

    private JPanel teamFrame = null;

    private JPanel judgementFrame = null;

    private JPanel otherPanel = null;

    private JPanel mainPane = null;

    private JScrollPane judgementsScroll = null;

    private JScrollPane teamsScroll = null;

    private JScrollPane problemsScroll = null;

    private JScrollPane languagesScroll = null;

    private JList judgementListBox = null;

    private DefaultListModel judgementListModel = new DefaultListModel();

    private JList teamListBox = null;

    private DefaultListModel teamListModel = new DefaultListModel();

    private JList problemsListBox = null;

    private DefaultListModel problemListModel = new DefaultListModel();

    private JList languagesListBox = null;

    private DefaultListModel languageListModel = new DefaultListModel();

    // TODO
    // private Filter filter = new Filter();

    public EditFilterPane() {
        super();
        initialize();
    }

    // TODO 
//    public EditFilterPane(Filter filter) {
//        super();
//        initialize();
//        this.filter = filter;
//    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(493, 337));
        this.add(getMainPane(), java.awt.BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Edit Filter";
    }

    /**
     * This method initializes filterOnCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getFilterOnCheckBox() {
        if (filterOnCheckBox == null) {
            filterOnCheckBox = new JCheckBox();
            filterOnCheckBox.setText("Filter On");
        }
        return filterOnCheckBox;
    }

    /**
     * This method initializes problemFrame
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProblemFrame() {
        if (problemFrame == null) {
            problemFrame = new JPanel();
            problemFrame.setLayout(new BorderLayout());
            problemFrame.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Problems", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            problemFrame.setName("problemFrame");
            problemFrame.add(getProblemsScroll(), java.awt.BorderLayout.CENTER);
        }
        return problemFrame;
    }

    /**
     * This method initializes bottomPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getBottomPanel() {
        if (bottomPanel == null) {
            bottomPanel = new JPanel();
            bottomPanel.add(getFilterOnCheckBox(), null);
        }
        return bottomPanel;
    }

    /**
     * This method initializes languagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getLanguagePane() {
        if (languagePane == null) {
            languagePane = new JPanel();
            languagePane.setLayout(new BorderLayout());
            languagePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Languages", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            languagePane.setName("languagePane");
            languagePane.add(getLanguagesScroll(), java.awt.BorderLayout.CENTER);
        }
        return languagePane;
    }

    /**
     * This method initializes teamFrame
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTeamFrame() {
        if (teamFrame == null) {
            teamFrame = new JPanel();
            teamFrame.setLayout(new BorderLayout());
            teamFrame.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Teams", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    null, null));
            teamFrame.setName("teamFrame");
            teamFrame.add(getTeamsScroll(), java.awt.BorderLayout.CENTER);
        }
        return teamFrame;
    }

    /**
     * This method initializes judgementFrame
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJudgementFrame() {
        if (judgementFrame == null) {
            judgementFrame = new JPanel();
            judgementFrame.setLayout(new BorderLayout());
            judgementFrame.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Judgements", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            judgementFrame.setName("judgementFrame");
            judgementFrame.add(getJudgementsScroll(), java.awt.BorderLayout.CENTER);
        }
        return judgementFrame;
    }

    /**
     * This method initializes otherPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getOtherPanel() {
        if (otherPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            otherPanel = new JPanel();
            otherPanel.setLayout(gridLayout);
            otherPanel.add(getProblemFrame(), null);
            otherPanel.add(getJudgementFrame(), null);
            otherPanel.add(getTeamFrame(), null);
            otherPanel.add(getLanguagePane(), null);
        }
        return otherPanel;
    }

    /**
     * This method initializes mainPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPane() {
        if (mainPane == null) {
            mainPane = new JPanel();
            mainPane.setLayout(new BorderLayout());
            mainPane.add(getBottomPanel(), java.awt.BorderLayout.SOUTH);
            mainPane.add(getOtherPanel(), java.awt.BorderLayout.CENTER);
        }
        return mainPane;
    }

    /**
     * This method initializes judgementsScroll
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJudgementsScroll() {
        if (judgementsScroll == null) {
            judgementsScroll = new JScrollPane();
            judgementsScroll.setViewportView(getJudgementListBox());
        }
        return judgementsScroll;
    }

    /**
     * This method initializes teamsScroll
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getTeamsScroll() {
        if (teamsScroll == null) {
            teamsScroll = new JScrollPane();
            teamsScroll.setViewportView(getTeamListBox());
        }
        return teamsScroll;
    }

    /**
     * This method initializes problemsScroll
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getProblemsScroll() {
        if (problemsScroll == null) {
            problemsScroll = new JScrollPane();
            problemsScroll.setViewportView(getProblemsListBox());
        }
        return problemsScroll;
    }

    /**
     * This method initializes languagesScroll
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getLanguagesScroll() {
        if (languagesScroll == null) {
            languagesScroll = new JScrollPane();
            languagesScroll.setViewportView(getLanguagesListBox());
        }
        return languagesScroll;
    }

    /**
     * This method initializes judgementListBox
     * 
     * @return javax.swing.JList
     */
    private JList getJudgementListBox() {
        if (judgementListBox == null) {
            judgementListBox = new JList();
        }
        return judgementListBox;
    }

    /**
     * This method initializes teamListBox
     * 
     * @return javax.swing.JTextArea
     */
    private JList getTeamListBox() {
        if (teamListBox == null) {
            teamListBox = new JList();
        }
        return teamListBox;
    }

    /**
     * This method initializes problemsListBox
     * 
     * @return javax.swing.JList
     */
    private JList getProblemsListBox() {
        if (problemsListBox == null) {
            problemsListBox = new JList(problemListModel);
        }
        return problemsListBox;
    }

    /**
     * This method initializes languagesListBox
     * 
     * @return javax.swing.JList
     */
    private JList getLanguagesListBox() {
        if (languagesListBox == null) {
            languagesListBox = new JList();
        }
        return languagesListBox;
    }

    /**
     * Populate
     * 
     */
    public void populateFields() {

        // TODO populate problem list
        // problemsListBox

        problemListModel.removeAllElements();
        for (Problem problem : getContest().getProblems()) {
            problemListModel.addElement(problem);
        }

        // TODO populate language list
        // languagesListBox

        languageListModel.removeAllElements();
        for (Language language : getContest().getLanguages()) {
            languageListModel.addElement(language);
        }

        // TODO populate judgement list
        // judgementListBox

        judgementListModel.removeAllElements();
        for (Judgement judgement : getContest().getJudgements()) {
            judgementListModel.addElement(judgement);
        }

        // TODO populate team list
        // teamListBox

        Vector<Account> vector = getContest().getAccounts(ClientType.Type.TEAM);
        Account[] accounts = (Account[]) vector.toArray(new Account[vector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        teamListModel.removeAllElements();
        for (Account account : accounts) {
            teamListModel.addElement(account.getClientId());
        }

    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {

        super.setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateFields();
            }
        });
    }

} // @jve:decl-index=0:visual-constraint="10,10"
