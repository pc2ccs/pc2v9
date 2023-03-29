// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.DisplayTeamName;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.ui.EditFilterPane.ListNames;

/**
 * Quick Judge Pane.
 * 
 * This pane shows the list of runs in the system and
 * allows the user to multi select and judge those runs.
 * Also allows the user to rejudge runs. 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class QuickJudgePane extends JPanePlugin implements UIPlugin {

    private static final String RANDOM_YES_OR_NO_ENTRY = "Random Yes or No";

    private static final String RANDOM_NO_ENTRY = "Random No";

    /**
     * 
     */
    private static final long serialVersionUID = 114647004580210428L;

    /**
     * Run list box.
     */
    // TODO REFACTOR LATER replace MCLB 
    private MCLB runListBox = null;

    private JPanel buttonPanel = null;

    private JPanel statusPanel = null;

    private JLabel statusLabel = null;

    private JLabel judgementLabel;

    private JComboBox<Judgement> judgementComboBox = null;

    private JButton judgementSubmittedButton = null;

    private JCheckBox showNewRunsCheckBox = null;

    private Random randomVar = null;

    /**
     * A list of pending judgements for runs.
     * 
     * When the run is checked out to this client, use the judgement in this list to assign a judgement.  
     */
    public Hashtable<ElementId, JudgementRecord> pendingJudgements = new Hashtable<ElementId, JudgementRecord>();

    private ViewJudgementsPane viewJudgementsPane = null;

    private JButton rejudgeButton;

    private JButton filterButton = null;

    private EditFilterFrame editFilterFrame = null;

    /**
     * User filter
     */
    private Filter filter = new Filter();

    private DisplayTeamName displayTeamName = null;

    private String filterFrameTitle = "Run filter";

    private Filter requiredFilter = new Filter();

    private JLabel rowCountLabel = new JLabel("###");

    /**
     * This method initializes
     * 
     */
    public QuickJudgePane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(625, 216));
        this.add(getRunsListBox(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        this.add(getStatusPanel(), java.awt.BorderLayout.NORTH);
    }

    @Override
    public String getPluginTitle() {
        return "Quick Judge Panel";
    }

    protected Object[] buildRunRow(Run run, ClientId judgeId) {
        //        Object[] cols = { "Site", "Team", "Run Id", "Time", "Status", "Problem", "Judge", "Language", "OS" };

        try {
            int cols = runListBox.getColumnCount();
            Object[] s = new String[cols];

            s[0] = getSiteTitle("" + run.getSubmitter().getSiteNumber());
            s[1] = getTeamDisplayName(run);
            s[2] = new Long(run.getNumber()).toString();
            s[3] = new Long(run.getElapsedMins()).toString();

            if (run.isJudged()) {

                if (run.isSolved()) {
                    s[4] = run.getStatus().toString() + " Yes";

                } else {
                    s[4] = run.getStatus().toString() + " No";
                }

                JudgementRecord judgementRecord = run.getJudgementRecord();
                if (judgementRecord != null && judgementRecord.getJudgementId() != null) {
                    Judgement judgement = getContest().getJudgement(judgementRecord.getJudgementId());
                    if (judgement != null) {
                        s[4] = judgement.toString();
                    }
                }

            } else {
                s[4] = run.getStatus().toString();
            }

            if (run.isDeleted()) {
                s[4] = Constants.DEL_RUN_PREFIX + s[4];
            }

            s[5] = getProblemTitle(run.getProblemId());

            if (judgeId != null) {
                if (judgeId.equals(getContest().getClientId())) {
                    s[6] = "Me";
                } else {
                    s[6] = judgeId.getName();
                }
            } else {
                s[6] = "";
            }

            s[7] = getLanguageTitle(run.getLanguageId());
            s[8] = run.getSystemOS();

            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildRunRow()", exception);
        }
        return null;
    }

    private String getLanguageTitle(ElementId languageId) {
        for (Language language : getContest().getLanguages()) {
            if (language.getElementId().equals(languageId)) {
                return language.toString();
            }
        }
        return "Language ?";
    }

    private String getProblemTitle(ElementId problemId) {
        Problem problem = getContest().getProblem(problemId);
        if (problem != null) {
            return problem.toString();
        }
        return "Problem ?";
    }

    private String getSiteTitle(String string) {
        return "Site " + string;
    }

    private String getTeamDisplayName(Run run) {
        Account account = getContest().getAccount(run.getSubmitter());
        if (account != null) {
            return account.getDisplayName();
        }

        return run.getSubmitter().getName();
    }

    @SuppressWarnings("unused")
    private String getJudgementTitle(ElementId judgementId) {
        Judgement judgement = getContest().getJudgement(judgementId);
        if (judgement != null) {
            return judgement.toString();
        }
        return "Judgement ?";
    }

    /**
     * Run Listener.
     * 
     * @author Douglas A. Lane <pc2@ecs.csus.edu>
     *
     */
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            updateRunRow(event.getRun(), event.getWhoModifiedRun());
        }

        public void runChanged(RunEvent event) {
            updateRunRow(event.getRun(), event.getWhoModifiedRun());

            if (event.getSentToClientId().equals(getContest().getClientId())) {
                // we checked out the run, let's try to judge it.

                Run run = event.getRun();
                JudgementRecord judgementRecord = pendingJudgements.get(run.getElementId());
                if (judgementRecord != null) {
                    
                    // Found a judgement for this run in pendingJudgements, send the judgement to the server.
                    
                    getController().submitRunJudgement(run, judgementRecord, null);
                    pendingJudgements.remove(run.getElementId());
                }
            }
        }

        public void runRemoved(RunEvent event) {
            // not used
        }

        @Override
        public void refreshRuns(RunEvent event) {
            // not used

        }
    }

    /**
     * This method initializes runListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getRunsListBox() {
        if (runListBox == null) {
            runListBox = new MCLB();

            runListBox.setMultipleSelections(true);
            runListBox.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() > 1) {
                        showJudgementRecords(runListBox.getSelectedIndex());
                    }
                }
            });

            Object[] cols = { "Site", "Team", "Id", "Time", "Status", "Problem", "Judge", "Language", "OS" };
            runListBox.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Site
            runListBox.setColumnSorter(0, sorter, 3);

            // Team
            runListBox.setColumnSorter(1, sorter, 2);

            // Run Id
            runListBox.setColumnSorter(2, numericStringSorter, 1);

            // Time
            runListBox.setColumnSorter(3, numericStringSorter, 4);

            // Status
            runListBox.setColumnSorter(4, sorter, 5);

            // Problem
            runListBox.setColumnSorter(5, sorter, 6);

            // Judge
            runListBox.setColumnSorter(6, sorter, 7);

            // Language
            runListBox.setColumnSorter(7, sorter, 8);

            // OS
            runListBox.setColumnSorter(8, sorter, 9);

            cols = null;

            runListBox.autoSizeAllColumns();

        }
        return runListBox;
    }

    protected void showJudgementRecords(int selectedIndex) {

        if (selectedIndex != -1) {
            // Lookup Run

            ElementId runId = (ElementId) runListBox.getKeys()[selectedIndex];
            Run run = getContest().getRun(runId);

            if (viewJudgementsPane != null) {
                viewJudgementsPane.setRun(run);
            } else {
                /**
                 * This means that the viewJudgementPane was never set.
                 */
                StaticLog.info("Unable to show judgements, no View Judgement Pane");
            }
        }

    }

    public void updateRunRow(final Run run, final ClientId whoModifiedId) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildRunRow(run, whoModifiedId);
                int rowNumber = runListBox.getIndexByKey(run.getElementId());

                boolean showNewOnly = showNewRunsCheckBox.isSelected();

                boolean canBeJudged = run.getStatus().equals(RunStates.NEW) //
                        || run.getStatus().equals(RunStates.QUEUED_FOR_COMPUTER_JUDGEMENT);

                if (showNewOnly && (!canBeJudged)) {

                    if (rowNumber != -1) {
                        runListBox.removeRow(rowNumber);
                    }
                    return;
                }

                if (rowNumber == -1) {
                    runListBox.addRow(objects, run.getElementId());
                } else {
                    runListBox.replaceRow(objects, rowNumber);
                }
                runListBox.autoSizeAllColumns();
                runListBox.sort();
                updateRowCount();
            }
        });

    }

    /**
     * This updates the rowCountlabel & toolTipText. It should be called only while on the swing thread.
     */
    private void updateRowCount() {
        if (filter.isFilterOn()) {
            int totalRuns = getContest().getRuns().length;
            rowCountLabel.setText(runListBox.getRowCount() + " of " + totalRuns);
            rowCountLabel.setToolTipText(runListBox.getRowCount() + " filtered runs");
        } else {
            rowCountLabel.setText("" + runListBox.getRowCount());
            rowCountLabel.setToolTipText(runListBox.getRowCount() + " runs");
        }
    }

    public void reloadRunList() {

        Run[] runs = getContest().getRuns();

        if (filter.isFilterOn()) {
            getFilterButton().setForeground(Color.BLUE);
            getFilterButton().setToolTipText("Edit filter - filter ON");
            rowCountLabel.setForeground(Color.BLUE);
        } else {
            getFilterButton().setForeground(Color.BLACK);
            getFilterButton().setToolTipText("Edit filter");
            rowCountLabel.setForeground(Color.BLACK);
        }

        for (Run run : runs) {

            if (requiredFilter != null) {
                if (!requiredFilter.matches(run)) {
                    removeRunRow(run);
                    continue;
                }
            }

            if (filter != null) {
                if (!filter.matches(run)) {
                    removeRunRow(run);
                    continue;
                }
            }

            ClientId clientId = null;

            RunStates runStates = run.getStatus();
            if (!(runStates.equals(RunStates.NEW) || run.isDeleted())) {
                JudgementRecord judgementRecord = run.getJudgementRecord();
                if (judgementRecord != null) {
                    clientId = judgementRecord.getJudgerClientId();
                }
            }
            updateRunRow(run, clientId);
        }

        updateRowCount();
    }

    /**
     * Remove run from grid.
     * 
     * @param run
     */
    private void removeRunRow(final Run run) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                int rowNumber = runListBox.getIndexByKey(run.getElementId());
                if (rowNumber != -1) {
                    runListBox.removeRow(rowNumber);
                    updateRowCount();
                }
            }
        });
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        setContest(inContest);
        setController(inController);

        getContest().addRunListener(new RunListenerImplementation());

        getEditFilterFrame().setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadJudgements();
                reloadRunList();
                enableButtons();
            }
        });
    }

    protected void reloadJudgements() {

        judgementComboBox.addItem(new Judgement(RANDOM_YES_OR_NO_ENTRY));
        judgementComboBox.addItem(new Judgement(RANDOM_NO_ENTRY));
        judgementComboBox.setSelectedIndex(1);

        if (getContest().isLoggedIn()) {

            Judgement[] judgements = getContest().getJudgements();
            for (Judgement judgement : judgements) {
                judgementComboBox.addItem(judgement);
            }
        }
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            judgementLabel = new JLabel();
            judgementLabel.setText("Judgement");
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.setPreferredSize(new java.awt.Dimension(35, 35));
            buttonPanel.add(getRejudgeButton());
            buttonPanel.add(getFilterButton());
            buttonPanel.add(getShowNewRunsCheckBox(), null);
            buttonPanel.add(judgementLabel, null);
            buttonPanel.add(getJudgementComboBox(), null);
            buttonPanel.add(getJudgementSubmittedButton(), null);
        }
        return buttonPanel;
    }

    /**
     * Enable/disable submit button
     */
    void enableButtons() {

        getJudgementSubmittedButton().setEnabled(isAllowed(Permission.Type.JUDGE_RUN));
        getRejudgeButton().setEnabled(isAllowed(Permission.Type.EDIT_RUN));
    }

    /**
     * This method initializes statusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            statusLabel = new JLabel();
            statusLabel.setText("");
            statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            statusPanel = new JPanel();
            statusPanel.setLayout(new BorderLayout());
            statusPanel.setPreferredSize(new java.awt.Dimension(35, 35));
            statusPanel.add(statusLabel, java.awt.BorderLayout.CENTER);
            statusPanel.add(rowCountLabel, BorderLayout.EAST);
        }
        return statusPanel;
    }

    /**
     * This method initializes judgementComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<Judgement> getJudgementComboBox() {
        if (judgementComboBox == null) {
            judgementComboBox = new JComboBox<Judgement>();
            judgementComboBox.setPreferredSize(new java.awt.Dimension(201, 25));

        }
        return judgementComboBox;
    }

    /**
     * This method initializes judgementSubmittedButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJudgementSubmittedButton() {
        if (judgementSubmittedButton == null) {
            judgementSubmittedButton = new JButton();
            judgementSubmittedButton.setText("Submit");
            judgementSubmittedButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    judgeSelectedRuns();
                }
            });
        }
        return judgementSubmittedButton;
    }

    protected void judgeSelectedRuns() {

        String separator = System.getProperty("line.separator");

        showMessage("");

        int[] selectedIndexes = runListBox.getSelectedIndexes();

        if (selectedIndexes.length == 0) {
            showMessage("Please select run to be judged");
            return;
        }

        int numSelected = selectedIndexes.length;

        Judgement judgement = (Judgement) judgementComboBox.getSelectedItem();

        int result = FrameUtilities.yesNoCancelDialog(this, "Are you sure you want to judge " + numSelected + " runs as " + separator + judgement, "Judge said runs?");
        if (result != JOptionPane.YES_OPTION) {
            showMessage("Ok - nothing changed");
            return;
        }

        int numJudged = 0;

        for (int idx = 0; idx < selectedIndexes.length; idx++) {
            int judgementIndex = getNextJudgementIndex();
            judgement = (Judgement) judgementComboBox.getItemAt(judgementIndex);
            ElementId id = (ElementId) runListBox.getRowKey(selectedIndexes[idx]);

            Object[] cells = runListBox.getRow(selectedIndexes[idx]);
            String state = (String) cells[4];
            if (state.startsWith(Constants.DEL_RUN_PREFIX)) {
                state = state.replace(Constants.DEL_RUN_PREFIX, "");
            }
            if (state.equals(RunStates.NEW.toString()) || state.equals(RunStates.QUEUED_FOR_COMPUTER_JUDGEMENT.toString())) {

                slog("Quick Judge run " + id + " with judgement '" + judgement + "' ");

                boolean judgedAsSolved = judgementIndex == 2;

                Run run = getContest().getRun(id);

                // Create judgement and save it in pendingJudgements
                JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), getContest().getClientId(), judgedAsSolved, false);
                pendingJudgements.put(run.getElementId(), judgementRecord);
                getLog().info("Stored Run in list of runs pending judgement " + run+" judgement="+judgement);

                getController().checkOutRun(run, false, false);

                numJudged++;

            } else {
                slog("Ignoring " + id + " already judged (or in wrong run state) (State: " + state + ")");
            }
        }

        judgement = (Judgement) judgementComboBox.getSelectedItem();

        if (numJudged != numSelected) {
            slog("Sent " + numJudged + " (of " + numSelected + ")" + " runs with judgement " + judgement, true);
        } else {
            slog("Sent " + numJudged + " runs with judgement " + judgement, true);
        }

    }

    private void slog(String string) {
        slog(string, false);
    }

    private void slog(String string, Exception e, boolean isShowMessage) {
        
        // TODO REFACTOR replace this method with a future pc2 standard log method. 

        if (e != null) {
            getLog().log(Level.WARNING, string, e);
        } else {
            getLog().info(string);
        }
        if (isShowMessage) {
            showMessage(string);
        }
    }

    /**
     * Log to log and system out
     * @param string
     */
    private void slog(String string, boolean isShowMessage) {

        getLog().info(string);
        if (isShowMessage) {
            showMessage(string);
        }

    }

    private int getNextJudgementIndex() {

        int comboIndex = getJudgementComboBox().getSelectedIndex();
        int comboSize = getJudgementComboBox().getItemCount();

        Judgement judgement = (Judgement) getJudgementComboBox().getSelectedItem();

        if (RANDOM_NO_ENTRY.contentEquals(judgement.getDisplayName())) {
            // Random No
            int index = getRandom().nextInt(comboSize - 3) + 3;
            return index;
        } else if (RANDOM_YES_OR_NO_ENTRY.contentEquals(judgement.getDisplayName())) {
            // Random Yes or No
            int index = getRandom().nextInt(comboSize - 2) + 2;
            return index;
        } else {
            // Yes or specific judgement
            return comboIndex;
        }
    }

    private void showMessage(String string) {
        statusLabel.setText(string);
    }

    /**
     * This method initializes showUnJudgedCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowNewRunsCheckBox() {
        if (showNewRunsCheckBox == null) {
            showNewRunsCheckBox = new JCheckBox();

            showNewRunsCheckBox.setText("View Unjudged only");
            showNewRunsCheckBox.setToolTipText("View New or Queued for computer judgement");
            showNewRunsCheckBox.setSelected(true);
            showNewRunsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateRunListDisplay(showNewRunsCheckBox.isSelected());
                }
            });
        }
        return showNewRunsCheckBox;
    }

    /**
     * 
     * @param b
     */
    protected void updateRunListDisplay(boolean showUnjudgedOnly) {

        showMessage("");

        getRunsListBox().removeAllRows();

        Run[] runs = getContest().getRuns();

        for (Run run : runs) {
            updateRunRow(run, null);
        }

        updateRowCount();

    }

    public ViewJudgementsPane getViewJudgementsPane() {
        return viewJudgementsPane;
    }

    public void setViewJudgementsPane(ViewJudgementsPane viewJudgementsPane) {
        this.viewJudgementsPane = viewJudgementsPane;
    }

    private JButton getRejudgeButton() {
        if (rejudgeButton == null) {
            rejudgeButton = new JButton("Rejudge");
            rejudgeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    try {
                        rejudgeSelectedRuns();
                    } catch (Exception e2) {
                        getLog().log(Level.WARNING, "Exception while rejudging", e2);
                        showMessage("Exception while rejudging " + e2.getMessage());
                    }
                }
            });
        }
        return rejudgeButton;
    }

    protected void rejudgeSelectedRuns() {

        showMessage("");

        int[] selectedIndexes = runListBox.getSelectedIndexes();

        if (selectedIndexes.length == 0) {
            showMessage("Please select run to be re-judged");
            return;
        }

        int numSelected = selectedIndexes.length;

        /**
         * list of runs and which states to change them to.
         */
        Map<Run, Run.RunStates> rejudgeChangeStatus = new HashMap<Run, Run.RunStates>();

        // find number of runs that can be rejudged

        for (int idx = 0; idx < selectedIndexes.length; idx++) {
            ElementId runId = (ElementId) runListBox.getRowKey(selectedIndexes[idx]);

            Run run = getContest().getRun(runId);

            Problem problem = getContest().getProblem(run.getProblemId());
            if (problem.isComputerJudged()) {
                rejudgeChangeStatus.put(run, RunStates.QUEUED_FOR_COMPUTER_JUDGEMENT);

            } else if (problem.isManualReview()) {
                rejudgeChangeStatus.put(run, RunStates.NEW);
            }

            getLog().info("Run " + run.getNumber() + " change to " + rejudgeChangeStatus.get(run) + " for " + run);
        }

        int result = FrameUtilities.yesNoCancelDialog(this,
                "Are you sure you want to force re-judge " + numSelected + " run?", "Re-judge said runs?");
        if (result != JOptionPane.YES_OPTION) {
            showMessage("Ok - nothing changes, such is thw world.");
            return;
        }

        Set<Run> rRuns = rejudgeChangeStatus.keySet();

        for (Run run : rRuns) {
            RunStates runstatus = rejudgeChangeStatus.get(run);
            try {
                slog("Updating run " + run.getNumber() + " status to " + runstatus + " for run " + run, false);
                updateRunStatus(run, rejudgeChangeStatus.get(run));
            } catch (Exception e) {
                slog("Problem updating to " + runstatus + " for run " + run, e, true);
            }
        }

        showMessage("Rejudged " + numSelected + " runs.");
    }

    private void updateRunStatus(Run run, RunStates runStates) {
        run.setStatus(runStates);
        // just update the run status
        getController().updateRun(run, null, null);
    }

    public Random getRandom() {
        if (randomVar == null) {
            randomVar = new Random(System.currentTimeMillis());
        }
        return randomVar;
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
            filterButton.setToolTipText("Edit Filter");
            filterButton.setMnemonic(java.awt.event.KeyEvent.VK_F);
            filterButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showFilterRunsFrame();
                }
            });
        }
        return filterButton;
    }

    protected void showFilterRunsFrame() {
        getEditFilterFrame().addList(ListNames.PROBLEMS);
        getEditFilterFrame().addList(ListNames.JUDGEMENTS);
        getEditFilterFrame().addList(ListNames.LANGUAGES);

        getEditFilterFrame().addList(ListNames.TEAM_ACCOUNTS);
        getEditFilterFrame().addList(ListNames.RUN_STATES);

        getEditFilterFrame().addList(ListNames.SITES);
        getEditFilterFrame().setFilter(filter);
        getEditFilterFrame().validate();
        getEditFilterFrame().setVisible(true);
    }

    public EditFilterFrame getEditFilterFrame() {
        if (editFilterFrame == null) {
            Runnable callback = new Runnable() {
                public void run() {
                    reloadRunList();
                }
            };
            editFilterFrame = new EditFilterFrame(filter, filterFrameTitle, callback);
            if (displayTeamName != null) {
                editFilterFrame.setDisplayTeamName(displayTeamName);
            }
        }
        return editFilterFrame;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
