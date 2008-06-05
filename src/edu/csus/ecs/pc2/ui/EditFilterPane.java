package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FilterFormatter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

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

    private JPanel problemsPane = null;

    private JPanel bottomPanel = null;

    private JPanel languagesPane = null;

    private JPanel teamsPane = null;

    private JPanel judgementsPane = null;

    private JPanel listsPanel = null;

    private JPanel mainPane = null;

    private JScrollPane judgementsScroll = null;

    private JScrollPane teamsScroll = null;

    private JScrollPane problemsScroll = null;

    private JScrollPane languagesScroll = null;

    private JCheckBoxJList judgementListBox = null;

    private DefaultListModel judgementListModel = new DefaultListModel();

    private JCheckBoxJList teamListBox = null;

    private DefaultListModel teamListModel = new DefaultListModel();

    private JCheckBoxJList problemsListBox = null;

    private DefaultListModel problemListModel = new DefaultListModel();

    private JCheckBoxJList languagesListBox = null;

    private DefaultListModel languageListModel = new DefaultListModel();

    private Filter filter = new Filter();

    private JScrollPane jScrollPane = null;

    private JPanel runStatesPane = null;

    private JCheckBoxJList runStatesListBox = null;

    private DefaultListModel runStatesListModel = new DefaultListModel();

    private JPanel timeRangePane = null;

    private JLabel fromTimeLabel = null;

    private JTextField fromTimeTextField = null;

    private JLabel toTimeLabel = null;

    private JTextField toTimeTextField = null;

    /**
     * JList names in EditFilterPane.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    public enum ListNames {
        /**
         * Language Jlist.
         */
        LANGUAGES,
        /**
         * Problem Jlist.
         */
        PROBLEMS,
        /**
         * Run States JList.
         */
        RUN_STATES,
        /**
         * Accounts JList.
         */
        ACCOUNTS,
        /**
         * Elapsed Time (both From and To) 
         */
        TIME_RANGE,
    }

    public EditFilterPane() {
        super();
        initialize();
    }

    /**
     * Wrapper class for JCheckBox object.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class WrapperJCheckBox extends JCheckBox {

        /**
         * 
         */
        private static final long serialVersionUID = 991427730095971274L;

        private Object contents;

        public WrapperJCheckBox(Object object) {
            this(object, object.toString());
        }

        public WrapperJCheckBox(Object object, String text) {
            super();
            contents = object;
            setText(text);
        }

        public Object getContents() {
            return contents;
        }
    }

    public EditFilterPane(Filter filter) {
        super();
        initialize();
        this.filter = filter;
    }

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
            filterOnCheckBox.setMnemonic(java.awt.event.KeyEvent.VK_F);
        }
        return filterOnCheckBox;
    }

    /**
     * This method initializes problemFrame
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProblemsPane() {
        if (problemsPane == null) {
            problemsPane = new JPanel();
            problemsPane.setLayout(new BorderLayout());
            problemsPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Problems", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            problemsPane.setName("problemFrame");
            problemsPane.add(getProblemsScroll(), java.awt.BorderLayout.CENTER);
        }
        return problemsPane;
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
    private JPanel getLanguagesPane() {
        if (languagesPane == null) {
            languagesPane = new JPanel();
            languagesPane.setLayout(new BorderLayout());
            languagesPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Languages", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            languagesPane.setName("languagePane");
            languagesPane.add(getLanguagesScroll(), java.awt.BorderLayout.CENTER);
        }
        return languagesPane;
    }

    /**
     * This method initializes teamFrame
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTeamsPane() {
        if (teamsPane == null) {
            teamsPane = new JPanel();
            teamsPane.setLayout(new BorderLayout());
            teamsPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Teams", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    null, null));
            teamsPane.setName("teamFrame");
            teamsPane.add(getTeamsScroll(), java.awt.BorderLayout.CENTER);
        }
        return teamsPane;
    }

    /**
     * This method initializes judgementFrame
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJudgementsPane() {
        if (judgementsPane == null) {
            judgementsPane = new JPanel();
            judgementsPane.setLayout(new BorderLayout());
            judgementsPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Judgements", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            judgementsPane.setName("judgementFrame");
            judgementsPane.add(getJudgementsScroll(), java.awt.BorderLayout.CENTER);
        }
        return judgementsPane;
    }

    /**
     * This method initializes otherPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getListsPanel() {
        if (listsPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            listsPanel = new JPanel();
            listsPanel.setLayout(gridLayout);
            listsPanel.add(getProblemsPane(), null);
            listsPanel.add(getJudgementsPane(), null);
            listsPanel.add(getTeamsPane(), null);
            listsPanel.add(getLanguagesPane(), null);
            listsPanel.add(getLanguagesPane(), null);
            listsPanel.add(getRunStatesPane(), null);
            listsPanel.add(getTimeRangePane(), null);
        }
        return listsPanel;
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
            mainPane.add(getListsPanel(), java.awt.BorderLayout.CENTER);
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
    private JCheckBoxJList getJudgementListBox() {
        if (judgementListBox == null) {
            judgementListBox = new JCheckBoxJList(judgementListModel);
        }
        return judgementListBox;
    }

    /**
     * This method initializes teamListBox
     * 
     * @return javax.swing.JTextArea
     */
    private JCheckBoxJList getTeamListBox() {
        if (teamListBox == null) {
            teamListBox = new JCheckBoxJList(teamListModel);
        }
        return teamListBox;
    }

    /**
     * This method initializes problemsListBox
     * 
     * @return javax.swing.JList
     */
    private JCheckBoxJList getProblemsListBox() {
        if (problemsListBox == null) {
            problemsListBox = new JCheckBoxJList(problemListModel);
        }
        return problemsListBox;
    }

    /**
     * This method initializes languagesListBox
     * 
     * @return javax.swing.JList
     */
    private JCheckBoxJList getLanguagesListBox() {
        if (languagesListBox == null) {
            languagesListBox = new JCheckBoxJList(languageListModel);
        }
        return languagesListBox;
    }

    /**
     * Populate
     * 
     */
    public void populateFields() {

        getFilterOnCheckBox().setSelected(filter.isFilterOn());

        problemListModel.removeAllElements();
        for (Problem problem : getContest().getProblems()) {
            WrapperJCheckBox wrapperJCheckBox = new WrapperJCheckBox(problem);
            if (filter.isFilteringProblems()) {
                wrapperJCheckBox.setSelected(filter.matchesProblem(problem.getElementId()));
            }
            problemListModel.addElement(wrapperJCheckBox);
        }

        languageListModel.removeAllElements();
        for (Language language : getContest().getLanguages()) {
            WrapperJCheckBox wrapperJCheckBox = new WrapperJCheckBox(language);
            if (filter.isFilteringLanguages()) {
                wrapperJCheckBox.setSelected(filter.matchesProblem(language.getElementId()));
            }
            languageListModel.addElement(wrapperJCheckBox);
        }

        judgementListModel.removeAllElements();
        for (Judgement judgement : getContest().getJudgements()) {
            WrapperJCheckBox wrapperJCheckBox = new WrapperJCheckBox(judgement);
            if (filter.isFilteringJudgements()) {
                wrapperJCheckBox.setSelected(filter.matchesProblem(judgement.getElementId()));
            }
            judgementListModel.addElement(wrapperJCheckBox);
        }

        Vector<Account> vector = getContest().getAccounts(ClientType.Type.TEAM);
        Account[] accounts = (Account[]) vector.toArray(new Account[vector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        teamListModel.removeAllElements();
        for (Account account : accounts) {
            WrapperJCheckBox wrapperJCheckBox = new WrapperJCheckBox(account.getClientId());
            if (filter.isFilteringAccounts()) {
                wrapperJCheckBox.setSelected(filter.matchesAccount(account.getClientId()));
            }
            teamListModel.addElement(wrapperJCheckBox);
        }

        runStatesListModel.removeAllElements();
        RunStates[] runStates = RunStates.values();
        for (RunStates runState : runStates) {
            WrapperJCheckBox wrapperJCheckBox = new WrapperJCheckBox(runState);
            if (filter.isFilteringRunStates()) {
                wrapperJCheckBox.setSelected(filter.matchesRunState(runState));
            }
            runStatesListModel.addElement(wrapperJCheckBox);
        }
        
        getFromTimeTextField().setText("");
        getToTimeTextField().setText("");
        if (filter.isFilteringElapsedTime()) {
            if (filter.getStartElapsedTime() >= 0) {
                getFromTimeTextField().setText("" + filter.getStartElapsedTime());
            }
            if (filter.getEndElapsedTime() >= 0) {
                getToTimeTextField().setText("" + filter.getEndElapsedTime());
            }
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

    public Filter getFilter() {

        // Derive filter based on settings

        filter.setFilter(getFilterOnCheckBox().isSelected());

        filter.clearProblemList();
        Enumeration enumeration = problemListModel.elements();
        while (enumeration.hasMoreElements()) {
            WrapperJCheckBox element = (WrapperJCheckBox) enumeration.nextElement();
            if (element.isSelected()) {
                Object object = element.getContents();
                filter.addProblem((Problem) object);
            }
        }

        filter.clearLanguageList();
        enumeration = languageListModel.elements();
        while (enumeration.hasMoreElements()) {
            WrapperJCheckBox element = (WrapperJCheckBox) enumeration.nextElement();
            if (element.isSelected()) {
                Object object = element.getContents();
                filter.addLanguage((Language) object);
            }
        }

        filter.clearAccountList();
        enumeration = teamListModel.elements();
        while (enumeration.hasMoreElements()) {
            WrapperJCheckBox element = (WrapperJCheckBox) enumeration.nextElement();
            if (element.isSelected()) {
                Object object = element.getContents();
                filter.addAccount((ClientId) object);
            }
        }

        filter.clearRunStatesList();
        enumeration = runStatesListModel.elements();
        while (enumeration.hasMoreElements()) {
            WrapperJCheckBox element = (WrapperJCheckBox) enumeration.nextElement();
            if (element.isSelected()) {
                Object object = element.getContents();
                filter.addRunState((RunStates) object);
            }
        }

        filter.clearJudgementList();
        enumeration = judgementListModel.elements();
        while (enumeration.hasMoreElements()) {
            WrapperJCheckBox element = (WrapperJCheckBox) enumeration.nextElement();
            if (element.isSelected()) {
                Object object = element.getContents();
                filter.addJudgement((Judgement) object);
            }
        }

        filter.clearElapsedTime();
        if (getFromTimeTextField().getText().length() > 0){
            filter.setStartElapsedTime(Long.parseLong(getFromTimeTextField().getText()));
        }
        
        if (getToTimeTextField().getText().length() > 0){
            filter.setEndElapsedTime(Long.parseLong(getToTimeTextField().getText()));
        }
        
        printAllSpecifiers("getFilter", getContest(), filter);

        return filter;
    }

    /**
     * Assigns filter and repopulates fields.
     * 
     * @param filter
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateFields();
            }
        });
    }

    protected void printAllSpecifiers(String prefix, IInternalContest contest, Filter inFilter) {
        String[] names = { FilterFormatter.ACCOUNT_SPECIFIER, FilterFormatter.JUDGMENTS_SPECIFIER, FilterFormatter.LANGUAGES_SPECIFIER, FilterFormatter.NUMBER_ACCOUNTS_SPECIFIER,
                FilterFormatter.NUMBER_JUDGEMENTS_SPECIFIER, FilterFormatter.NUMBER_LANGUAGES_SPECIFIER, FilterFormatter.NUMBER_PROBLEMS_SPECIFIER, FilterFormatter.PROBLEMS_SPECIFIER,
                FilterFormatter.SHORT_ACCOUNT_NAMES_SPECIFIER, FilterFormatter.TEAM_LIST_SPECIFIER, FilterFormatter.TEAM_LONG_LIST_SPECIFIER, };

        Arrays.sort(names);

        FilterFormatter filterFormatter = new FilterFormatter();
        for (String string : names) {
            System.out.println(prefix + " " + string + " '" + filterFormatter.format(string, contest, inFilter) + "'");
        }

    }

    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getRunStatesListBox());
        }
        return jScrollPane;
    }

    /**
     * This method initializes runStatesPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRunStatesPane() {
        if (runStatesPane == null) {
            runStatesPane = new JPanel();
            runStatesPane.setLayout(new BorderLayout());
            runStatesPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Run States", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            runStatesPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return runStatesPane;
    }

    /**
     * This method initializes runStatesListBox
     * 
     * @return edu.csus.ecs.pc2.ui.JCheckBoxJList
     */
    private JCheckBoxJList getRunStatesListBox() {
        if (runStatesListBox == null) {
            runStatesListBox = new JCheckBoxJList(runStatesListModel);
        }
        return runStatesListBox;
    }

    /**
     * Show or hide list on edit filter frame.
     * 
     * @param listNames
     *            list to show or hid
     * @param showList
     *            true show list, false do not show list.
     */
    public void showJList(ListNames listNames, boolean showList) {
        switch (listNames) {
            case ACCOUNTS:
                getTeamsPane().setVisible(showList);
                break;
            case LANGUAGES:
                getLanguagesPane().setVisible(showList);
                break;
            case PROBLEMS:
                getProblemsPane().setVisible(showList);
                break;
            case RUN_STATES:
                getRunStatesPane().setVisible(showList);
                break;
            case TIME_RANGE:
                getTimeRangePane().setVisible(showList);
            default:
                throw new InvalidParameterException("Invalid listNames: " + listNames);
        }

        // TODO tighten up layout somehow

        this.doLayout();
    }

    /**
     * This method initializes timeRangePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTimeRangePane() {
        if (timeRangePane == null) {
            toTimeLabel = new JLabel();
            toTimeLabel.setText("To");
            fromTimeLabel = new JLabel();
            fromTimeLabel.setText("From");
            timeRangePane = new JPanel();
            timeRangePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Time Range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            timeRangePane.add(fromTimeLabel, null);
            timeRangePane.add(getFromTimeTextField(), null);
            timeRangePane.add(toTimeLabel, null);
            timeRangePane.add(getToTimeTextField(), null);
        }
        return timeRangePane;
    }

    /**
     * This method initializes fromTimeTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFromTimeTextField() {
        if (fromTimeTextField == null) {
            fromTimeTextField = new JTextField();
            fromTimeTextField.setDocument(new IntegerDocument());
            fromTimeTextField.setPreferredSize(new java.awt.Dimension(60, 20));
        }
        return fromTimeTextField;
    }

    /**
     * This method initializes toTimeTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getToTimeTextField() {
        if (toTimeTextField == null) {
            toTimeTextField = new JTextField();
            toTimeTextField.setPreferredSize(new java.awt.Dimension(60, 20));
        }
        return toTimeTextField;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
