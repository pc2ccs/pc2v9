// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountNameCaseComparator;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.list.StringToNumberComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.DisplayTeamName;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunUtilities;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.report.ExtractRuns;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.ui.EditFilterPane.ListNames;
import edu.csus.ecs.pc2.ui.judge.JudgeView;
import edu.csus.ecs.pc2.util.OSCompatibilityUtilities;

/**
 * View runs panel.
 *
 * @author pc2@ecs.csus.edu
 */
public class RunsTablePane extends JPanePlugin {

    /**
     *
     */
    private static final long serialVersionUID = 114647004580210428L;

    private static final int VERT_PAD = 2;
    private static final int HORZ_PAD = 20;


    private JTableCustomized runTable = null;
    private DefaultTableModel runTableModel = null;

    private JPanel messagePanel = null;

    private JPanel buttonPanel = null;

    private JButton requestRunButton = null;

    private JButton filterButton = null;

    private JButton editRunButton = null;

    private JButton extractButton = null;

    private JButton giveButton = null;

    private JButton takeButton = null;

    private JButton rejudgeRunButton = null;

    private JButton viewJudgementsButton = null;

    private EditRunFrame editRunFrame = null;

    private ViewJudgementsFrame viewJudgementsFrame = null;

    private SelectJudgementFrame selectJudgementFrame = null;

    private JLabel messageLabel = null;

    private Log log = null;

    private JLabel rowCountLabel = null;

    private JScrollPane scrollPane = null;

    /**
     * Show huh
     */
    private boolean showNewRunsOnly = false;

    /**
     * Filter that does not change.
     *
     * Used to do things like insure only New runs or Judged runs
     */
    private Filter requiredFilter = new Filter();

    /**
     * Show who is judging column and status.
     *
     */
    private boolean showJudgesInfo = true;

    /**
     * User filter
     */
    private Filter filter = new Filter();

    private JButton autoJudgeButton = null;

    private AutoJudgingMonitor autoJudgingMonitor = new AutoJudgingMonitor(true);

    private boolean usingTeamColumns = false;

    private boolean usingFullColumns = false;

    private DisplayTeamName displayTeamName = null;

    private boolean makeSoundOnOneRun = false;

    private boolean bUseAutoJudgemonitor = true;

    private EditFilterFrame editFilterFrame = null;

    private String filterFrameTitle = "Run filter";

    private ExtractRuns extractRuns = null;

    private boolean teamClient = true;

    private JudgementNotificationsList judgementNotificationsList = null;

    private boolean displayConfirmation = true;
    private JButton viewSourceButton;

    private boolean serverReplied;

    private Run fetchedRun;

    private RunFiles fetchedRunFiles;

    private Run requestedRun;

    private boolean showSourceActive = false;

    protected int viewSourceThreadCounter;

    /**
     * This method initializes
     *
     */
    public RunsTablePane() {
        super();
        initialize();
    }

    /**
     * @param useAutoJudgeMonitor
     *            if true use
     */
    public RunsTablePane(boolean useAutoJudgeMonitor) {
        super();
        bUseAutoJudgemonitor = useAutoJudgeMonitor;
        initialize();
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(744, 216));
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        this.add(getScrollPane(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);

        editRunFrame = new EditRunFrame();
        viewJudgementsFrame = new ViewJudgementsFrame();
        selectJudgementFrame = new SelectJudgementFrame();
    }

    @Override
    public String getPluginTitle() {
        return "Runs Table Panel";
    }

    protected Object[] buildRunRow(Run run, ClientId judgeId) {

        try {
            boolean autoJudgedRun = isAutoJudgedRun(run);

            int cols = runTableModel.getColumnCount();
            Object[] s = new Object[cols];

            int idx = 0;

            if (usingFullColumns) {
//              Object[] fullColumns = { "Site", "Team", "Run Id", "Time", "Status", "Suppressed", "Problem", "Judge", "Balloon", "Language", "OS" };
                s[idx++] = getSiteTitle("" + run.getSubmitter().getSiteNumber());
                s[idx++] = getTeamDisplayName(run);
                s[idx++] = Integer.toString(run.getNumber());
                s[idx++] = Long.toString(run.getElapsedMins());
                s[idx++] = getJudgementResultString(run);
                s[idx++] = isJudgementSuppressed(run);
                s[idx++] = getProblemTitle(run.getProblemId());
                s[idx++] = getJudgesTitle(run, judgeId, showJudgesInfo, autoJudgedRun);
                s[idx++] = getBalloonColor(run);
                s[idx++] = getLanguageTitle(run.getLanguageId());
                s[idx++] = run.getSystemOS();
            } else if (showJudgesInfo) {
//              Object[] fullColumnsNoJudge = { "Site", "Team", "Run Id", "Time", "Status", "Problem", "Balloon", "Language", "OS" };
                s[idx++] = getSiteTitle("" + run.getSubmitter().getSiteNumber());
                s[idx++] = getTeamDisplayName(run);
                s[idx++] = Integer.toString(run.getNumber());
                s[idx++] = Long.toString(run.getElapsedMins());
                s[idx++] = getJudgementResultString(run);
                s[idx++] = getProblemTitle(run.getProblemId());
                s[idx++] = getBalloonColor(run);
                s[idx++] = getLanguageTitle(run.getLanguageId());
                s[idx++] = run.getSystemOS();

            } else if (usingTeamColumns) {
//              Object[] teamColumns = { "Site", "Run Id", "Problem", "Time", "Status", "Balloon", "Language" };
                s[idx++] = getSiteTitle("" + run.getSubmitter().getSiteNumber());
                s[idx++] = Integer.toString(run.getNumber());
                s[idx++] = getProblemTitle(run.getProblemId());
                s[idx++] = Long.toString(run.getElapsedMins());
                s[idx++] = getJudgementResultString(run);
                s[idx++] = getBalloonColor(run);
                s[idx++] = getLanguageTitle(run.getLanguageId());
            } else {
                log.log(Log.INFO, "In RunsTablePane no table columns set");
            }
            // Unique key - this column is not displayed (invisible)
            s[idx++] = run.getElementId();
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildRunRow()", exception);
        }
        return null;
    }

    /**
     * Return balloon color if problem solved.
     *
     * @param run
     * @return
     */
    private String getBalloonColor(Run run) {

        if (run.isSolved()) {
            BalloonSettings balloonSettings = getContest().getBalloonSettings(run.getSiteNumber());

            if (balloonSettings != null) {
                String colorName = balloonSettings.getColor(run.getProblemId());
                if (colorName != null) {
                    return colorName;
                }
            }
        }

        return "";
    }

    /**
     * Was the active judgement suppressed on the team?
     * @param run
     * @return "" or "Yes" or "Yes (EOC)"
     */
    private String isJudgementSuppressed(Run run) {
        String result = "";

        // requires run to be judged
        if (run.isJudged()) {
            if (!run.getJudgementRecord().isSendToTeam()) {
                result= "Yes";
            } else {
                if (RunUtilities.supppressJudgement(judgementNotificationsList, run, getContest().getContestTime())) {
                    result = "Yes (EOC)";
                } else {
                    if (run.getJudgementRecord().isPreliminaryJudgement()) {
                        Problem prob = getContest().getProblem(run.getProblemId());
                        if (!prob.isPrelimaryNotification()) {
                            result = "Yes";
                        }
                    }
                }
            }
        }
        return result;
    }

    private String getJudgesTitle(Run run, ClientId judgeId, boolean showJudgesInfo2, boolean autoJudgedRun) {

        String result = "";

        if (showJudgesInfo) {
            if (judgeId != null) {
                if (judgeId.equals(getContest().getClientId())) {
                    result = "Me";
                } else {
                    result = judgeId.getName() + " S" + judgeId.getSiteNumber();
                }
                if (autoJudgedRun) {
                    result = result + "/AJ";
                }
            } else {
                result = "";
            }
        }
        return result;
    }

    private boolean isAutoJudgedRun(Run run) {
        if (run.isJudged()) {
            if (!run.isSolved()) {
                JudgementRecord judgementRecord = run.getJudgementRecord();
                if (judgementRecord != null && judgementRecord.getJudgementId() != null) {
                    return judgementRecord.isUsedValidator();
                }
            }
        }
        return false;
    }

    /**
     * Return the judgement/status for the run.
     *
     * @param run
     * @return a string that represents the state of the run
     */
    protected String getJudgementResultString(Run run) {

        String result = "";

        if (run.isJudged()) {

            if (run.isSolved()) {
                result = getContest().getJudgements()[0].getDisplayName();
                if (run.getStatus().equals(RunStates.MANUAL_REVIEW)) {
                    if (!isTeam(getContest().getClientId())) {
                        result = RunStates.MANUAL_REVIEW + " (" + result + ")";
                    } else {
                        // Only show to team
                        if (showPreliminaryJudgementToTeam(run)) {
                            result = "PRELIMINARY (" + result + ")";
                        } else {
                            result = RunStates.NEW.toString();
                        }
                    }
                }

                // only consider changing the state to new if we are a team
                if (isTeam(getContest().getClientId()) && !run.getJudgementRecord().isSendToTeam()) {
                    result = RunStates.NEW.toString();
                }

            } else {
                result = "No";

                JudgementRecord judgementRecord = run.getJudgementRecord();
                if (judgementRecord != null && judgementRecord.getJudgementId() != null) {
                    if (judgementRecord.isUsedValidator() && judgementRecord.getValidatorResultString() != null) {
                        result = judgementRecord.getValidatorResultString();
                    } else {
                        Judgement judgement = getContest().getJudgement(judgementRecord.getJudgementId());
                        if (judgement != null) {
                            result = judgement.toString();
                        }
                    }

                    if (run.getStatus().equals(RunStates.MANUAL_REVIEW)) {
                        if (!isTeam(getContest().getClientId())) {
                            result = RunStates.MANUAL_REVIEW + " (" + result + ")";
                        } else {

                            // Only show to team
                            if (showPreliminaryJudgementToTeam(run)) {
                                result = "PRELIMINARY (" + result + ")";
                            } else {
                                result = RunStates.NEW.toString();
                            }
                        }
                    }

                    if (isTeam(getContest().getClientId())) {
                        if (!judgementRecord.isSendToTeam()) {
                            result = RunStates.NEW.toString();
                        }
                    } else {
                        if (run.getStatus().equals(RunStates.BEING_RE_JUDGED)) {
                            result = RunStates.BEING_RE_JUDGED.toString();
                        }
                    }
                }
            }

            /**
             *
             */

            if (teamClient || isAllowed(Permission.Type.RESPECT_EOC_SUPPRESSION)){
                if (RunUtilities.supppressJudgement(judgementNotificationsList, run, getContest().getContestTime())){
                    result = RunStates.NEW.toString();
                }
            }

        } else {
            if (showJudgesInfo) {
                result = run.getStatus().toString();
            } else {
                result = RunStates.NEW.toString();
            }
        }

        if (run.isDeleted()) {
            result = "DEL " + result;
        }


        return result;
    }

    private boolean showPreliminaryJudgementToTeam(Run run) {

        try {
            Problem problem = getContest().getProblem(run.getProblemId());
            return problem.isPrelimaryNotification();
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception trying to get Problem  ", e);
            return false;
        }
    }

    private boolean isTeam(ClientId clientId) {
        return clientId == null || clientId.getClientType().equals(Type.TEAM);
    }

//    private boolean isServer(ClientId clientId) {
//        return clientId == null || clientId.getClientType().equals(Type.SERVER);
//    }


    private boolean isJudge(ClientId clientId) {
        return clientId == null || clientId.getClientType().equals(Type.JUDGE);
    }

    private boolean isJudge() {
        return isJudge(getContest().getClientId());
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

        if (isJudge() && isTeam(run.getSubmitter())) {

            return displayTeamName.getDisplayName(run.getSubmitter());
        }
        return run.getSubmitter().getName();
    }

    /**
     * Run Listener
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    public class RunListenerImplementation implements IRunListener {

        @Override
        public void runAdded(RunEvent event) {
            updateRunRow(event.getRun(), event.getWhoModifiedRun(), true);
            // check if this is a team; if so, pop up a confirmation dialog
            if (getContest().getClientId().getClientType() == ClientType.Type.TEAM) {
                showResponseToTeam(event);
            }
        }

        @Override
        public void refreshRuns(RunEvent event) {
            reloadRunList();
        }

        @Override
        public void runChanged(RunEvent event) {
            updateRunRow(event.getRun(), event.getWhoModifiedRun(), true);

            // check if this is a team; if so, pop up a response dialog
            if (getContest().getClientId().getClientType() == ClientType.Type.TEAM) {
                showResponseToTeam(event);
            }

            //code copied from FetchRunService.RunListenerImplementation.runChanged():

                Action action = event.getAction();
                Action details = event.getDetailedAction();
                Run aRun = event.getRun();
                RunFiles aRunFiles = event.getRunFiles();
                String msg = event.getMessage();

                getController().getLog().log(Log.INFO, "RunsPane.RunListener: Action=" + action + "; DetailedAction=" + details + "; msg=" + msg
                                        + "; run=" + aRun + "; runFiles=" + aRunFiles);

                if (aRun != null) {
                    // make local reference for consistency in case requestedRun gets cleared - avoids synchronization
                    Run locRun = requestedRun;

                    // we are only interested in the run we may have requested from View Source - all other run changes are ignored.
                    if(locRun != null && aRun.getNumber() == locRun.getNumber() && aRun.getSiteNumber() == locRun.getSiteNumber()) {

                        // RUN_NOT_AVAILABLE is undirected (sentToClient is null)
                        if (event.getAction().equals(Action.RUN_NOT_AVAILABLE)) {

                            getController().getLog().log(Log.WARNING, "Reply from server: requested run not available");
                            serverReplied = true;
                        } else {
                            // Only interested in the first reply (we don't want fetchedRun changing once its been set - it really shouldn't)
                            if(fetchedRun == null) {
                                ClientId toClient = event.getSentToClientId() ;
                                ClientId myID = getContest().getClientId();

                                // see if the event was directed to me explicitly, which it should be (see PacketHandler.handleFetchedRun())
                                // but isn't due to the way updateRun() works.  We'll leave this code here in case someday that changes.
                                if (toClient != null && toClient.equals(myID)) {

                                    getController().getLog().log(Log.INFO, "Reply from server: " + "Run Status=" + event.getAction()
                                                            + "; run=" + event.getRun() + ";  runFiles=" + event.getRunFiles());

                                    fetchedRun = aRun;
                                    fetchedRunFiles = aRunFiles;
                                    serverReplied = true;

                                } else {

                                    // The FETCHED_REQUESTED_RUN reply is sent with a a SentToClientID of null as found in
                                    // InternalContest.updateRun().  the RunEvent's sentToClientId member is only set when the run
                                    // is being checked out for BEING_JUDGED, BEING_RE_JUDGED, CHECKED_OUT, HOLD, otherwise it is
                                    // is not set (null), and winds up here in the case of "View Source" but not from a being-judged dialog.

                                    getController().getLog().log(Log.INFO, "Event not directed to me: sent to " + toClient + " but my ID is " + myID);

                                    if(toClient == null) {
                                        fetchedRun = aRun;
                                        fetchedRunFiles = aRunFiles;
                                        serverReplied = true;
                                    }
                                }
                            }
                        }
                    } else {
                        // changed run was not the one we wanted
                        getController().getLog().log(Log.INFO, "Run event not for requested run: " + "Run Status=" + event.getAction()
                            + "; run=" + aRun + " requested=" + locRun);
                    }
                } else {
                    //run from server was null
                    getController().getLog().log(Log.WARNING, "Run received from server was null");
                    fetchedRun = null;
                    fetchedRunFiles = null;
                }



        }

        @Override
        public void runRemoved(RunEvent event) {
            updateRunRow(event.getRun(), event.getWhoModifiedRun(), true);
        }

        /**
         * Show the Run Judgement.
         *
         * Checks the run in the specified run event and (potentially) displays a results dialog. If the run has been judged, has a valid judgement record, and is the "active" judgement for scoring
         * purposes, displays a modal MessageDialog to the Team containing the judgement results. This method assumes the caller has already verified this is a TEAM client; failure to do that on the
         * caller's part will cause other clients to see the Run Response dialog...
         */
        private void showResponseToTeam(RunEvent event) {

            if (! displayConfirmation){
                return;
            }

            Run theRun = event.getRun();
            String problemName = getContest().getProblem(theRun.getProblemId()).toString();
            String languageName = getContest().getLanguage(theRun.getLanguageId()).toString();
            int runId = theRun.getNumber();
            // build an HTML tag that sets the font size and color for the answer
            String responseFormat = "";

            // check if the run has been judged
            if (theRun.isJudged() && (! theRun.isDeleted())) {

                // check if there's a legit judgement
                JudgementRecord judgementRecord = theRun.getJudgementRecord();
                if (judgementRecord != null) {

                    if (! judgementRecord.isSendToTeam()){

                        /**
                         * Do not show this judgement to the team, the judge indicated
                         * that the team should not be notified.
                         */
                        return; // ------------------------------------------------------ RETURN
                    }

                    // check if this is the scoreable judgement
                    boolean isActive = judgementRecord.isActive();
                    if (isActive) {

                        String response = getContest().getJudgement(judgementRecord.getJudgementId()).toString();

                        // it's a valid judging response (presumably to a team);
                        // get the info from the run and display it in a modal popup
                        if (judgementRecord.isSolved()) {
                            responseFormat += "<FONT COLOR=\"00FF00\" SIZE=+2>"; // green, larger
                        } else {
                            responseFormat += "<FONT COLOR=RED>"; // red, current size

                            String validatorJudgementName = judgementRecord.getValidatorResultString();
                            if (judgementRecord.isUsedValidator() && validatorJudgementName != null) {
                                if (validatorJudgementName.trim().length() == 0) {
                                    validatorJudgementName = "undetermined";
                                }
                                response = validatorJudgementName;
                            }
                        }

                        String judgeComment = theRun.getCommentsForTeam();
                        try {
                            String displayString = "<HTML><FONT SIZE=+1>Judge's Response<BR><BR>" + "Problem: <FONT COLOR=BLUE>" + Utilities.forHTML(problemName) + "</FONT><BR><BR>"
                                    + "Language: <FONT COLOR=BLUE>" + Utilities.forHTML(languageName) + "</FONT><BR><BR>" + "Run Id: <FONT COLOR=BLUE>" + runId + "</FONT><BR><BR><BR>"
                                    + "Judge's Response: " + responseFormat + Utilities.forHTML(response) + "</FONT><BR><BR><BR>";

                            if (theRun.getStatus().equals(RunStates.MANUAL_REVIEW)) {
                                displayString += "<FONT SIZE='+2'>NOTE: This is a <FONT COLOR='RED'>Preliminary</FONT> Judgement</FONT><BR><BR><BR>";
                            }

                            if (judgeComment != null) {
                                if (judgeComment.length() > 0) {
                                    displayString += "Judge's Comment: " + Utilities.forHTML(judgeComment) + "<BR><BR><BR>";
                                }
                            }

                            displayString += "</FONT></HTML>";

                            FrameUtilities.showMessage(getParentFrame(), "Run Judgement Received", displayString);

                        } catch (Exception e) {
                            // TODO need to make this cleaner
                            JOptionPane.showMessageDialog(getParentFrame(), "Exception handling Run Response: " + e.getMessage());
                            log.warning("Exception handling Run Response: " + e.getMessage());
                        }
                    }
                }
            } else if (! theRun.isDeleted()) {
                // not judged
                try {
                    String displayString = "<HTML><FONT SIZE=+1>Confirmation of Run Receipt<BR><BR>" + "Problem: <FONT COLOR=BLUE>" + Utilities.forHTML(problemName) + "</FONT><BR><BR>"
                            + "Language: <FONT COLOR=BLUE>" + Utilities.forHTML(languageName) + "</FONT><BR><BR>" + "Run Id: <FONT COLOR=BLUE>" + runId + "</FONT><BR><BR><BR>";

                    displayString += "</FONT></HTML>";

                    FrameUtilities.showMessage(getParentFrame(), "Run Received", displayString);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(getParentFrame(), "Exception handling Run Confirmation: " + e.getMessage());
                    log.warning("Exception handling Run Confirmation: " + e.getMessage());
                }
            }
        }
    }

    /**
     * This method initializes scrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getRunTable());
        }
        return scrollPane;
    }

    /**
     * This method initializes the runTable
     *
     * @return JTableCustomized
     */
    private JTableCustomized getRunTable() {
        if (runTable == null) {
            runTable = new JTableCustomized();

            runTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent me) {
                   // If double-click see if we can select the run
                   if (me.getClickCount() == 2) {
                      JTable target = (JTable)me.getSource();
                      if(target.getSelectedRow() != -1 && isAllowed(Permission.Type.JUDGE_RUN)) {
                          requestSelectedRun();
                      }
                   }
                }
             });
        }
        return runTable;
    }

    public void clearAllRuns() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(runTableModel != null) {
                    // All rows are discarded - the TM will notify the Table
                    runTableModel.setRowCount(0);
                }
            }
        });
    }

    private void resetRunsListBoxColumns() {

        runTable.removeAll();

        Object[] fullColumns = { "Site", "Team", "Run Id", "Time", "Status", "Suppressed", "Problem", "Judge", "Balloon", "Language", "OS", "ElementID" };
        Object[] fullColumnsNoJudge = { "Site", "Team", "Run Id", "Time", "Status", "Problem", "Balloon", "Language", "OS", "ElementID" };
        Object[] teamColumns = { "Site", "Run Id", "Problem", "Time", "Status", "Balloon", "Language", "ElementID" };
        Object[] columns;

        usingTeamColumns = false;
        usingFullColumns = false;

        // Determine which headers we want based on type of account making request.
        if (isTeam(getContest().getClientId())) {
            usingTeamColumns = true;
            columns = teamColumns;
        } else if (!showJudgesInfo) {
            columns = fullColumnsNoJudge;
        } else {
            usingFullColumns = true;
            columns = fullColumns;
        }
        runTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        runTable.setModel(runTableModel);
        TableColumnModel tcm = runTable.getColumnModel();
        // Remove ElementID from display - this does not REMOVE the column, just makes it so it doesn't show
        tcm.removeColumn(tcm.getColumn(columns.length - 1));

        // Sorters
        TableRowSorter<DefaultTableModel> trs = new TableRowSorter<DefaultTableModel>(runTableModel);

        runTable.setRowSorter(trs);
        runTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        ArrayList<SortKey> sortList = new ArrayList<SortKey>();


        /*
         * Column headers left justified
         */
        ((DefaultTableCellRenderer)runTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        runTable.setRowHeight(runTable.getRowHeight() + VERT_PAD);


        StringToNumberComparator numericStringSorter = new StringToNumberComparator();
        AccountNameCaseComparator accountNameSorter = new AccountNameCaseComparator();

        int idx = 0;

        if (isTeam(getContest().getClientId())) {

//            Object[] teamColumns = { "Site", "Run Id", "Problem", "Time", "Status", "Balloon", "Language" };

            // These are in column order - omitted ones are straight string compare
            trs.setComparator(0, accountNameSorter);
            trs.setComparator(1, numericStringSorter);
            trs.setComparator(3, numericStringSorter);
            // These are in sort order
            sortList.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(5, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(6, SortOrder.ASCENDING));

        } else if (showJudgesInfo) {

//            Object[] fullColumns = { "Site", "Team", "Run Id", "Time", "Status", "Suppressed", "Problem", "Judge", "Balloon", "Language", "OS" };

            // These are in column order - omitted ones are straight string compare
            trs.setComparator(0, accountNameSorter);
            trs.setComparator(1, accountNameSorter);
            trs.setComparator(2, numericStringSorter);
            trs.setComparator(3, numericStringSorter);
            trs.setComparator(7, accountNameSorter);
            // These are in sort order
            sortList.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(5, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(6, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(7, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(8, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(9, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(10, SortOrder.ASCENDING));

        } else {

//            Object[] fullColumnsNoJudge = { "Site", "Team", "Run Id", "Time", "Status", "Problem", "Balloon", "Language", "OS" };

            // These are in column order - omitted ones are straight string compare
            trs.setComparator(0, accountNameSorter);
            trs.setComparator(1, accountNameSorter);
            trs.setComparator(2, numericStringSorter);
            trs.setComparator(3, numericStringSorter);
            // These are in sort order
            sortList.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(5, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(6, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(7, SortOrder.ASCENDING));
            sortList.add(new RowSorter.SortKey(8, SortOrder.ASCENDING));
        }
        trs.setSortKeys(sortList);
        resizeColumnWidth(runTable);
    }

    private void resizeColumnWidth(JTableCustomized table) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TableColumnAdjuster tca = new TableColumnAdjuster(table, HORZ_PAD);
                tca.adjustColumns();
            }
        });
    }

    /**
     * Find row that contains the supplied key (in last column)
     * @param value - unique key - really, the ElementId of run
     * @return index of row, or -1 if not found
     */
    private int getRowByKey(Object value) {
        Object o;

        if(runTableModel != null) {
            int col = runTableModel.getColumnCount() - 1;
            for (int i = runTableModel.getRowCount() - 1; i >= 0; --i) {
                o = runTableModel.getValueAt(i, col);
                if (o != null && o.equals(value)) {
                    return i;
                }
            }
        }
        return(-1);
    }

    /**
     * Remove run from grid by removing the data row from the TableModel
     *
     * @param run
     */
    private void removeRunRow(final Run run) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                int rowNumber = getRowByKey(run.getElementId());
                if (rowNumber != -1) {
                    runTableModel.removeRow(rowNumber);
                    updateRowCount();
                }
            }
        });
    }

    /**
     * This updates the rowCountlabel & toolTipText. It should be called only while on the swing thread.
     */
    private void updateRowCount() {
        if (filter.isFilterOn()){
            int totalRuns = getContest().getRuns().length;
            rowCountLabel.setText(runTable.getRowCount()+" of "+totalRuns);
            rowCountLabel.setToolTipText(runTable.getRowCount() + " filtered runs");
        } else {
            rowCountLabel.setText("" + runTable.getRowCount());
            rowCountLabel.setToolTipText(runTable.getRowCount() + " runs");
        }
    }

    public void updateRunRow(final Run run, final ClientId whoModifiedId, final boolean autoSizeAndSort) {

        if (filter != null) {
            if (!filter.matches(run)) {
                // if run does not match filter, be sure to remove it from grid
                // This applies when a run is New then BEING_JUDGED and other conditions.
                removeRunRow(run);
                return;
            }
        }

        if (requiredFilter != null) {
            if (!requiredFilter.matches(run)) {
                // if run does not match requiredFilter, be sure to remove it from grid
                // This applies when a run is New then BEING_JUDGED and other conditions.
                removeRunRow(run);
                return;
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                ClientId whoJudgedId = whoModifiedId;
                if (run.isJudged()) {
                    JudgementRecord judgementRecord = run.getJudgementRecord();
                    if (judgementRecord != null) {
                        whoJudgedId = judgementRecord.getJudgerClientId();
                    }
                }

                Object[] objects = buildRunRow(run, whoJudgedId);
                int rowNumber = getRowByKey(run.getElementId());
                if (rowNumber == -1) {
                    // No row with this key - add new one
                    runTableModel.addRow(objects);
                } else {
                    // Update all fields
                    for(int j = runTableModel.getColumnCount()-1; j >= 0; j--) {
                        runTableModel.setValueAt(objects[j], rowNumber, j);
                    }
                }

                if (isAllowed(Permission.Type.JUDGE_RUN)) {
                    if (runTableModel.getRowCount() == 1) {
                        emitSound();
                    }
                }

                if (autoSizeAndSort) {
                    updateRowCount();
                    resizeColumnWidth(runTable);
                }

//                if (selectJudgementFrame != null) {
                        //TODO the selectJudgementFrame should be placed above all PC2 windows, not working when dblClicking in Windows OS
//                }
            }
        });
    }

    public void reloadRunList() {

        Run[] runs = getContest().getRuns();

        ContestInformation contestInformation = getContest().getContestInformation();
        judgementNotificationsList = contestInformation.getJudgementNotificationsList();

        if (isJudge()) {
            displayTeamName.setTeamDisplayMask(contestInformation.getTeamDisplayMode());
        }

        // TODO bulk load these records, this is closer only do the count,size,sort at end

        if (filter.isFilterOn()){
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
            updateRunRow(run, clientId, false);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateRowCount();
                resizeColumnWidth(runTable);
            }
        });
    }

    private void emitSound() {
        if (isMakeSoundOnOneRun()) {
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
    }

    private void updateGUIperPermissions() {

        if (showNewRunsOnly) {

            // Show New Runs

            getViewSourceButton().setVisible(false);
            requestRunButton.setVisible(true);
            requestRunButton.setEnabled(isAllowed(Permission.Type.JUDGE_RUN));
            editRunButton.setVisible(false);
            extractButton.setVisible(false);
            giveButton.setVisible(false);
            takeButton.setVisible(false);
            rejudgeRunButton.setVisible(false);
            viewJudgementsButton.setVisible(false);
            autoJudgeButton.setVisible(false);
        } else {

            // Show ALL Runs

            getViewSourceButton().setVisible(isAllowed(Permission.Type.ALLOWED_TO_FETCH_RUN));
            requestRunButton.setVisible(isAllowed(Permission.Type.JUDGE_RUN));
            editRunButton.setVisible(isAllowed(Permission.Type.EDIT_RUN));
            extractButton.setVisible(isAllowed(Permission.Type.EXTRACT_RUNS));
            giveButton.setVisible(isAllowed(Permission.Type.GIVE_RUN));

            takeButton.setVisible(false);
            // takeButton.setVisible(isAllowed(Permission.Type.TAKE_RUN));
            rejudgeRunButton.setVisible(isAllowed(Permission.Type.REJUDGE_RUN));
            viewJudgementsButton.setVisible(isAllowed(Permission.Type.VIEW_RUN_JUDGEMENT_HISTORIES));
            autoJudgeButton.setVisible(isAllowed(Permission.Type.ALLOWED_TO_AUTO_JUDGE));

        }

        filterButton.setVisible(true);
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        if (bUseAutoJudgemonitor) {
            autoJudgingMonitor.setContestAndController(getContest(), getController());
        }

        log = getController().getLog();

        displayTeamName = new DisplayTeamName();
        displayTeamName.setContestAndController(inContest, inController);

        teamClient = isTeam(inContest.getClientId());

        ContestInformation contestInformation = getContest().getContestInformation();
        judgementNotificationsList = contestInformation.getJudgementNotificationsList();

        initializePermissions();

        extractRuns = new ExtractRuns(inContest);

        getContest().addRunListener(new RunListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addLanguageListener(new LanguageListenerImplementation());
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());
        getContest().addBalloonSettingsListener(new BalloonSettingsListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                editRunFrame.setContestAndController(getContest(), getController());
                viewJudgementsFrame.setContestAndController(getContest(), getController());
                if (isAllowed(Permission.Type.JUDGE_RUN)) {
                    selectJudgementFrame.setContestAndController(getContest(), getController());
                }


                getEditFilterFrame().setContestAndController(getContest(), getController());

                updateGUIperPermissions();
                resetRunsListBoxColumns();
                reloadRunList();

                if (isAllowed(Permission.Type.EXTRACT_RUNS)){
                    getRunTable().setRowSelectionAllowed(true);
                    getRunTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                }
                // this will do the checking and take us off the AWT thread as needed
                // check this to prevent the admin has turned off aj message
            }
        });
    }

    /**
     * This method initializes messagePanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            rowCountLabel = new JLabel();
            rowCountLabel.setText("### of ###");
            rowCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            rowCountLabel.setPreferredSize(new java.awt.Dimension(100,16));
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new java.awt.Dimension(30, 30));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
            messagePanel.add(rowCountLabel, java.awt.BorderLayout.EAST);
        }
        return messagePanel;
    }

    /**
     * This method initializes buttonPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(5);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.setPreferredSize(new java.awt.Dimension(35, 35));
            buttonPanel.add(getGiveButton(), null);
            buttonPanel.add(getTakeButton(), null);
            buttonPanel.add(getEditRunButton(), null);
            buttonPanel.add(getViewSourceButton());
            buttonPanel.add(getViewJudgementsButton(), null);
            buttonPanel.add(getRequestRunButton(), null);
            buttonPanel.add(getRejudgeRunButton(), null);
            buttonPanel.add(getFilterButton(), null);
            buttonPanel.add(getAutoJudgeButton(), null);
            buttonPanel.add(getExtractButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes requestRunButton
     *
     * @return javax.swing.JButton
     */
    private JButton getRequestRunButton() {
        if (requestRunButton == null) {
            requestRunButton = new JButton();
            requestRunButton.setText("Request Run");
            requestRunButton.setToolTipText("Request the selected Run for Judging");
            requestRunButton.setMnemonic(java.awt.event.KeyEvent.VK_R);
            requestRunButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    requestSelectedRun();
                }
            });
        }
        return requestRunButton;
    }

    protected void requestSelectedRun() {
        if (!isAllowed(Permission.Type.JUDGE_RUN)) {
            log.log(Log.WARNING, "Account does not have permission to JUDGE_RUN, cannot requestSelectedRun.");
            showMessage("Unable to request run, check log");
            return;
        }
        int[] selectedIndexes = runTable.getSelectedRows();

        if (selectedIndexes.length < 1) {
            showMessage("Please select a run ");
            return;
        }

        /*
         *  we may be competing with the AJ, ensure we have the lock before
         *  checking & setting the alreadyJudgingRun boolean.
         */
        Boolean alreadyJudgingRun = JudgeView.getAlreadyJudgingRun();
        synchronized (alreadyJudgingRun) {
            if (JudgeView.isAlreadyJudgingRun()) {
                JOptionPane.showMessageDialog(this, "Already judging run");
                return;
            }

            JudgeView.setAlreadyJudgingRun(true);
        }

        try {
            Run runToEdit = getContest().getRun(runTable.getElementIdFromTableRow(selectedIndexes[0]));

            if ((!(runToEdit.getStatus().equals(RunStates.NEW) || runToEdit.getStatus().equals(RunStates.MANUAL_REVIEW)))
                    || runToEdit.isDeleted()) {
                showMessage("Not allowed to request run (run not status NEW) ");
                JudgeView.setAlreadyJudgingRun(false);
                return;
            }

            selectJudgementFrame.setRun(runToEdit, false);
            selectJudgementFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to request run, check log");
            JudgeView.setAlreadyJudgingRun(false);
        }
    }

    protected void rejudgeSelectedRun() {

        int[] selectedIndexes = runTable.getSelectedRows();

        if (selectedIndexes.length < 1) {
            showMessage("Please select a run ");
            return;
        }

        try {
            Run runToEdit = getContest().getRun(runTable.getElementIdFromTableRow(selectedIndexes[0]));

            if (!runToEdit.isJudged()) {
                showMessage("Judge run before attempting to re-judge run");
                return;
            }

            if (runToEdit.isDeleted()) {
                showMessage("Not allowed to rejudge deleted run ");
                return;
            }

            /*
             *  we may be competing with the AJ, ensure we have the lock before
             *  checking & setting the alreadyJudgingRun boolean.
             */
            Boolean alreadyJudgingRun = JudgeView.getAlreadyJudgingRun();
            synchronized (alreadyJudgingRun) {
                if (JudgeView.isAlreadyJudgingRun()) {
                    JOptionPane.showMessageDialog(this, "Already judging run");
                    return;
                }

                JudgeView.setAlreadyJudgingRun(true);
            }

            selectJudgementFrame.setRun(runToEdit, true);
            selectJudgementFrame.setVisible(true);

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to rejudge run, check log");
        }
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
                @Override
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

        if (! usingTeamColumns) {
            getEditFilterFrame().addList(ListNames.TEAM_ACCOUNTS);
            getEditFilterFrame().addList(ListNames.RUN_STATES);
        }

        getEditFilterFrame().addList(ListNames.SITES);
        getEditFilterFrame().setFilter(filter);
        getEditFilterFrame().validate();
        getEditFilterFrame().setVisible(true);
    }

    /**
     * This method initializes editRunButton
     *
     * @return javax.swing.JButton
     */
    private JButton getEditRunButton() {
        if (editRunButton == null) {
            editRunButton = new JButton();
            editRunButton.setText("Edit");
            editRunButton.setToolTipText("Edit the selected Run");
            editRunButton.setMnemonic(java.awt.event.KeyEvent.VK_E);
            editRunButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedRun();
                }
            });
        }
        return editRunButton;
    }

    protected void editSelectedRun() {

        int[] selectedIndexes = runTable.getSelectedRows();

        if (selectedIndexes.length < 1) {
            showMessage("Please select a run ");
            return;
        }

        try {
            Run runToEdit = getContest().getRun(runTable.getElementIdFromTableRow(selectedIndexes[0]));

            editRunFrame.setRun(runToEdit);
            editRunFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit run, check log");
        }

    }

    /**
     * This method initializes extractButton
     *
     * @return javax.swing.JButton
     */
    private JButton getExtractButton() {
        if (extractButton == null) {
            extractButton = new JButton();
            extractButton.setText("Extract");
            extractButton.setMnemonic(java.awt.event.KeyEvent.VK_X);
            extractButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    extractRuns(getRunTable());

                }
            });
        }
        return extractButton;
    }

    protected void extractRuns(JTableCustomized runs) {

        if (runs.getRowCount() < 1) {
            showMessageToUser("No runs to extract");
            return;
        }

        int[] selectedRows = runs.getSelectedRows();

        try {
            if (selectedRows.length < 1) {
                // Extract all runs

                int numRuns = runs.getRowCount();
                String confirmMessage = "Extract ALL (" + numRuns + ") runs listed?";
                int response = FrameUtilities.yesNoCancelDialog(this, confirmMessage, "Extract Runs");

                if (response == JOptionPane.YES_OPTION) {
                    // They said Yes, go for it.
                    FrameUtilities.waitCursor(this);
                    int numberRuns = runs.getRowCount();
                    int numExtracted = extractSelectedRuns(runs, getRunKeys(runs));
                    FrameUtilities.regularCursor(this);
                    showMessageToUser("Extracted " + numExtracted + " of " + numberRuns + " runs to \"" + extractRuns.getExtractDirectory() + "\" dir.");
                }

            } else {

                String confirmMsg = "Extract " + selectedRows.length + " runs?";
                int response = JOptionPane.showConfirmDialog(this, confirmMsg);

                if (response == JOptionPane.YES_OPTION) {
                    // They said Yes, go for it.
                    FrameUtilities.waitCursor(this);
                    int numExtracted = extractSelectedRuns(runs, getRunKeys(runs, selectedRows));
                    FrameUtilities.regularCursor(this);
                    showMessageToUser("Extracted " + numExtracted + " of " + selectedRows.length + " runs to \"" + extractRuns.getExtractDirectory() + "\" dir.");
                }
            }
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
        } finally{
            FrameUtilities.regularCursor(this);
        }
    }

    private ElementId[] getRunKeys(JTableCustomized runs) {
        Vector<ElementId> vector = new Vector<ElementId>();
        int totalRows = runs.getRowCount();
        for (int rowNumber = 0; rowNumber < totalRows; rowNumber++) {
            vector.addElement(runs.getElementIdFromTableRow(rowNumber));
        }
        return vector.toArray(new ElementId[vector.size()]);
    }

    private ElementId[] getRunKeys(JTableCustomized runs, int[] selectedRows) {
        Vector<ElementId> vector = new Vector<ElementId>();
        for (int rowNumber : selectedRows) {
            vector.addElement(runs.getElementIdFromTableRow(rowNumber));
        }
        return vector.toArray(new ElementId[vector.size()]);
    }


    private int extractSelectedRuns(JTableCustomized runs, ElementId[] runKeys) {
        int extractCount = 0;

        int totalRows = runKeys.length;

        for (int i = 0; i < runKeys.length; i++) {
            try {
                boolean extracted = extractRuns.extractRun(runKeys[i]);

                if (extracted){
                    extractCount ++;
                }

                updateRunCount (extractCount, totalRows);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileSecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return extractCount;
    }

    private void updateRunCount(int extractCount, int totalRows) {
        System.out.println("Extracted "+extractCount+" of "+totalRows);
    }

    private void showMessageToUser(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * This method initializes giveButton
     *
     * @return javax.swing.JButton
     */
    private JButton getGiveButton() {
        if (giveButton == null) {
            giveButton = new JButton();
            giveButton.setText("Give");
            giveButton.setToolTipText("Give the selected Run back to Judges");
            giveButton.setMnemonic(java.awt.event.KeyEvent.VK_G);
            giveButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    giveSelectedRun();
                }
            });
        }
        return giveButton;
    }

    protected void giveSelectedRun() {

        int[] selectedIndexes = runTable.getSelectedRows();

        if (selectedIndexes.length < 1) {
            showMessage("Please select a run ");
            return;
        }

        try {
            Run runToEdit = getContest().getRun(runTable.getElementIdFromTableRow(selectedIndexes[0]));

            if (runToEdit.getStatus().equals(RunStates.BEING_JUDGED) || runToEdit.getStatus().equals(RunStates.NEW) || runToEdit.getStatus().equals(RunStates.BEING_RE_JUDGED)) {
                getController().cancelRun(runToEdit);
                showMessage("Gave run " + runToEdit);

            } else {
                showMessage("Can not give run with state: " + runToEdit.getStatus());
            }

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to give run, check log");
        }

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
            takeButton.setToolTipText("Take the selected Run from the Judges");
            takeButton.setMnemonic(java.awt.event.KeyEvent.VK_T);
            takeButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("TODO RunsTable.getTakeButton actionPerformed()");
                    // TODO code Take Run
                }
            });
        }
        return takeButton;
    }

    /**
     * This method initializes rejudgeRunButton
     *
     * @return javax.swing.JButton
     */
    private JButton getRejudgeRunButton() {
        if (rejudgeRunButton == null) {
            rejudgeRunButton = new JButton();
            rejudgeRunButton.setText("Rejudge");
            rejudgeRunButton.setToolTipText("Rejudge the selected Run");
            rejudgeRunButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    rejudgeSelectedRun();
                }
            });
        }
        return rejudgeRunButton;
    }

    /**
     * This method initializes viewJudgementsButton
     *
     * @return javax.swing.JButton
     */
    private JButton getViewJudgementsButton() {
        if (viewJudgementsButton == null) {
            viewJudgementsButton = new JButton();
            viewJudgementsButton.setText("View Judgements");
            viewJudgementsButton.setToolTipText("View Judgements for the selected Run");
            viewJudgementsButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewSelectedRunJudgements();
                }
            });
        }
        return viewJudgementsButton;
    }

    protected void viewSelectedRunJudgements() {

        int[] selectedIndexes = runTable.getSelectedRows();

        if (selectedIndexes.length < 1) {
            showMessage("Please select a run ");
            return;
        }

        try {
            Run theRun = getContest().getRun(runTable.getElementIdFromTableRow(selectedIndexes[0]));

            if (theRun != null) {
                viewJudgementsFrame.setRun(theRun);
                viewJudgementsFrame.setVisible(true);
            } else {
                showMessage("Cannot display judgements for Run");
            }

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to view run, check log");
        }

    }

    public boolean isDisplayConfirmation() {
        return displayConfirmation;
    }

    public void setDisplayConfirmation(boolean displayConfirmation) {
        this.displayConfirmation = displayConfirmation;
    }

    /**
     * Account Listener.
     *
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class AccountListenerImplementation implements IAccountListener {

        @Override
        public void accountAdded(AccountEvent accountEvent) {
            // ignore doesn't affect this pane
        }

        @Override
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
                    @Override
                    public void run() {
                        updateGUIperPermissions();
                        reloadRunList();
                    }
                });

            } else {
                // not us, but update the grid anyways
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        reloadRunList();
                    }
                });

            }

        }

        @Override
        public void accountsAdded(AccountEvent accountEvent) {
            // ignore, this does not affect this class

        }

        @Override
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
                @Override
                public void run() {
                    if (finalTheyModifiedUs) {
                        updateGUIperPermissions();
                    }
                    reloadRunList();
                }
            });
        }

        @Override
        public void accountsRefreshAll(AccountEvent accountEvent) {
            accountsModified(accountEvent);
        }
    }

    /**
     * Problem Listener.
     *
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class ProblemListenerImplementation implements IProblemListener {

        @Override
        public void problemAdded(ProblemEvent event) {
            // ignore does not affect this pane
        }

        @Override
        public void problemChanged(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reloadRunList();
                }
            });
        }

        @Override
        public void problemRemoved(ProblemEvent event) {
            // ignore does not affect this pane
        }

        @Override
        public void problemRefreshAll(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reloadRunList();
                }
            });
        }
    }

    /**
     * Language Listener.
     *
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class LanguageListenerImplementation implements ILanguageListener {

        @Override
        public void languageAdded(LanguageEvent event) {
            // ignore does not affect this pane
        }

        @Override
        public void languageChanged(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reloadRunList();
                }
            });
        }

        @Override
        public void languageRemoved(LanguageEvent event) {
            // ignore does not affect this pane
        }

        @Override
        public void languageRefreshAll(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reloadRunList();
                }
            });
        }

        @Override
        public void languagesAdded(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reloadRunList();
                }
            });
        }

        @Override
        public void languagesChanged(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reloadRunList();
                }
            });
        }
    }

    /**
     * Contest Information Listener.
     *
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    public class ContestInformationListenerImplementation implements IContestInformationListener {

        @Override
        public void contestInformationAdded(ContestInformationEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reloadRunList();
                }
            });
        }

        @Override
        public void contestInformationChanged(ContestInformationEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reloadRunList();
                }
            });
        }

        @Override
        public void contestInformationRemoved(ContestInformationEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reloadRunList();
                }
            });
        }

        @Override
        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reloadRunList();
                }
            });
        }
        @Override
        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            // Not used
        }


    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
            }
        });

    }

    public boolean isShowNewRunsOnly() {
        return showNewRunsOnly;
    }

    /**
     * Shows only runs that are RunStates.NEW and RunStates.MANUAL_REVIEW.
     *
     * @param showNewRunsOnly
     */
    public void setShowNewRunsOnly(boolean showNewRunsOnly) {
        this.showNewRunsOnly = showNewRunsOnly;

        if (showNewRunsOnly) {
            if (requiredFilter == null) {
                requiredFilter = new Filter();
            }
            requiredFilter.addRunState(RunStates.NEW);
            requiredFilter.addRunState(RunStates.MANUAL_REVIEW);
        } else {
            requiredFilter = new Filter();
        }
    }

    public boolean isShowJudgesInfo() {
        return showJudgesInfo;
    }

    public void setShowJudgesInfo(boolean showJudgesInfo) {
        this.showJudgesInfo = showJudgesInfo;
    }

    /**
     * This method initializes autoJudgeButton
     *
     * @return javax.swing.JButton
     */
    private JButton getAutoJudgeButton() {

        if (autoJudgeButton == null) {
            autoJudgeButton = new JButton();
            autoJudgeButton.setText("Auto Judge");
            autoJudgeButton.setToolTipText("Enable Auto Judging");
            autoJudgeButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // Turn auto judging on
                    if (!bUseAutoJudgemonitor) {
                        log.info("Can not show Auto Judge Monitor, not enabled");
                        return;
                    }
                    log.info("Starting Auto Judge monitor");
                    autoJudgingMonitor.setAutoJudgeDisabledLocally(false);
                    startAutoJudging();
                }
            });
        }
        return autoJudgeButton;
    }

    /**
     * Is Auto Judging turned On for this judge ?
     *
     * @return
     */
    private boolean isAutoJudgingEnabled() {
        ClientSettings clientSettings = getContest().getClientSettings();
        if (clientSettings != null && clientSettings.isAutoJudging()) {
            return true;
        }

        return false;
    }

    public void startAutoJudging() {
        if (!bUseAutoJudgemonitor) {
            return;
        }

        if (isAutoJudgingEnabled()) {
            showMessage("");

            // make sure the OS supports judging of all problems this judge
            // is set up to autojudge BEFORE starting autojudging.
            List<Problem> plist = autoJudgingMonitor.getOSUnsupportedAutojudgeProblemList();
            if(!plist.isEmpty()) {
                StringBuffer message = new StringBuffer();
                message.append("Cannot perform autojudging for the following problems due to missing OS features:\n");
                for(Problem prob: plist) {
                    message.append("   Problem " + prob.getLetter() + " - " + prob.getDisplayName() + "\n");
                }
                message.append("You must either remove these problems from this autojudge's list of problems or\n");
                message.append("you must make sure your OS supports features needed to judge these problems.\n");
                message.append("One possiblity is the problems require a sandbox and your OS does not support it (cgroups?).");
                FrameUtilities.showMessage(this,  message.toString(), "Judging Not Supported");
                return;
            }
            // Keep this off the AWT thread.
            new Thread(new Runnable() {
                @Override
                public void run() {
//                  RE-enable local auto judge flag
                    autoJudgingMonitor.startAutoJudging();
                }
            }).start();
        } else {
            showMessage("Administrator has turned off Auto Judging");

            List<Problem> plist = OSCompatibilityUtilities.getUnableToJudgeList(getContest(), log);
            if(!plist.isEmpty()) {
                StringBuffer message = new StringBuffer();
                message.append("Cannot perform judging for the following problems due to missing OS features:\n");
                for(Problem prob: plist) {
                    message.append("   Problem " + prob.getLetter() + " - " + prob.getDisplayName() + "\n");
                }
                message.append("In order to judge these problems, you must make sure your OS supports features\n");
                message.append("needed to judge these problems.\n");
                message.append("One possiblity is the problems require a sandbox and your OS does not support it (cgroups?).");
                FrameUtilities.showMessage(this, "Judging Not Availalbe",  message.toString());
            }

        }
    }
    public boolean isMakeSoundOnOneRun() {
        return makeSoundOnOneRun;
    }

    public void setMakeSoundOnOneRun(boolean makeSoundOnOneRun) {
        this.makeSoundOnOneRun = makeSoundOnOneRun;
    }

    public EditFilterFrame getEditFilterFrame() {
        if (editFilterFrame == null){
            Runnable callback = new Runnable(){
                @Override
                public void run() {
                    reloadRunList();
                }
            };
            editFilterFrame = new EditFilterFrame(filter, filterFrameTitle,  callback);
            if (displayTeamName != null){
                editFilterFrame.setDisplayTeamName(displayTeamName);
            }
        }
        return editFilterFrame;
    }

    /**
     * Set title for the Filter Frame.
     *
     * @param title
     */
    public void setFilterFrameTitle (String title){
        filterFrameTitle = title;
        if (editFilterFrame != null){
            editFilterFrame.setTitle(title);
        }
    }

    /**
     * @author pc2@ecs.csus.edu
     *
     */
    public class BalloonSettingsListenerImplementation implements IBalloonSettingsListener {

        @Override
        public void balloonSettingsAdded(BalloonSettingsEvent event) {
            reloadRunList();
        }

        @Override
        public void balloonSettingsChanged(BalloonSettingsEvent event) {
            reloadRunList();
        }

        @Override
        public void balloonSettingsRemoved(BalloonSettingsEvent event) {
            reloadRunList();
        }

        @Override
        public void balloonSettingsRefreshAll(BalloonSettingsEvent balloonSettingsEvent) {
            reloadRunList();
        }
    }

    /**
     * @author pc2@ecs.csus.edu
     *
     * Don't let any more View Source's to be issued
     */
    private void BlockViewSource()
    {
        showSourceActive = true;
    }

    /**
     * @author pc2@ecs.csus.edu
     *
     * Allow View Source to be issued
     */
    private void AllowViewSource()
    {
        showSourceActive = false;
    }

    private boolean IsAllowedViewSource()
    {
        return (showSourceActive == false);
    }

    private JButton getViewSourceButton() {
        if (viewSourceButton == null) {
            viewSourceButton = new JButton("View Source");
            viewSourceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // For now, only allow one View Source to be outstanding at-a-time
                    // TODO: this has to be re-evaluated so that multiple outstanding View Source
                    //       requests will work.
                    if(!IsAllowedViewSource()) {
                        if(requestedRun != null) {
                            showMessage("There is already a View Source pending for Run " + requestedRun.getNumber() + " at site " + requestedRun.getSiteNumber());
                            getController().getLog().log(Log.INFO, "There is already a View Source pending for Run " + requestedRun.getNumber() + " at site " + requestedRun.getSiteNumber());
                       } else {
                            // This could mean that the requestedRun's info just came in.
                            showMessage("There is already a View Source pending");
                            getController().getLog().log(Log.INFO, "There is already a View Source pending but no requestedRun");
                        }
                        return;
                    }

                    // make sure we're allowed to fetch a run
                    if (!isAllowed(Permission.Type.ALLOWED_TO_FETCH_RUN)) {
                        getController().getLog().log(Log.WARNING, "Account does not have the permission ALLOWED_TO_FETCH_RUN; cannot view run source.");
                        showMessage("Unable to fetch run, check log");
                        return;
                    }

                    // make sure there's exactly one run selected in the grid
                    int[] selectedIndexes = runTable.getSelectedRows();

                    if (selectedIndexes.length < 1) {
                        showMessage("Please select a run ");
                        return;
                    } else if (selectedIndexes.length > 1) {
                        showMessage("Please select exactly ONE run in order to view source ");
                        return;
                    }

//                    SwingUtilities.invokeLater(new Runnable() {
//                        public void run() {
//                            showSourceForSelectedRun();
//                        }
//                    });

                    Thread viewSourceThread = new Thread() {
                        @Override
                        public void run() {
                            showSourceForSelectedRun(selectedIndexes[0]);
                        }
                    };
                    // only one View Source active at-a-time
                    BlockViewSource();
                    viewSourceThread.setName("ViewSourceThread-" + viewSourceThreadCounter++);
                    viewSourceThread.start();
                }
            });
            viewSourceButton.setToolTipText("Displays a read-only view of the source code for the currently selected run");
        }
        return viewSourceButton;
    }

    /**
     * Displays a {@link MultipleFileViewer} containing the source code for the run (submission) which is currently selected in the Runs grid.
     *
     * If no run is selected, or more than one run is selected, prompts the user to select just one run (row) in the grid
     * and does nothing else.
     */
    private void showSourceForSelectedRun(int nSelectedRunIndex) {

        boolean bFetchError = false;

        // we are allowed to view source and there's exactly one run selected; try to obtain the run source and display it in a MFV
        try {

            Run run = getContest().getRun(runTable.getElementIdFromTableRow(nSelectedRunIndex));

            // make sure we found the currently selected run
            if (run != null) {

                showMessage("Preparing to display source code for run " + run.getNumber() + " at site " + run.getSiteNumber());
                getController().getLog().log(Log.INFO, "Preparing to display source code for run " + run.getNumber() + " at site " + run.getSiteNumber());

                //the following forces a (read-only) checkout from the server; it makes more sense to first see if we already have the
                // necessary RunFiles and then if not to issue a "Fetch" request rather than a "checkout" request
//                getController().checkOutRun(run, true, false); // checkoutRun(run, isReadOnlyRequest, isComputerJudgedRequest)

                //check if we already have the RunFiles for the run
                if (!getContest().isRunFilesPresent(run)) {

                    // this is the run we're after
                    requestedRun = run;
                    // reset this each time so we be sure to get the first new reply
                    fetchedRun = null;

                    //we don't have the files; request them from the server (this is NOT a checkout, but a read-only fetch)
                    getController().fetchRun(run);

                    // wait for the server to reply (i.e., to make a callback to the run listener) -- but only for up to 30 sec
                    int waitedMS = 0;
                    serverReplied = false;
                    while (!serverReplied && waitedMS < 30000) {
                        Thread.sleep(100);
                        waitedMS += 100;
                    }
                    // no longer interested in getting updates for this run.
                    requestedRun = null;

                    //check if we got a reply from the server
                    if (serverReplied) {

                        //the server replied; see if we got some RunFiles
                        if (fetchedRunFiles!=null) {

                            //we got some RunFiles from the server; put them into the contest model
                            getContest().updateRunFiles(run, fetchedRunFiles);
                        } else {

                            //we got a reply from the server but we didn't get any RunFiles
                            getController().getLog().log(Log.WARNING, "Server failed to return RunFiles in response to fetch run request");
                            bFetchError = true;
                        }

                    } else {

                        // the server failed to reply to the fetchRun request within the time limit
                        getController().getLog().log(Log.WARNING, "No response from server to fetch run request after " + waitedMS + "ms");
                        bFetchError = true;
                    }
                }

                // OK to now start another view source
                AllowViewSource();

                // if any type of error occurred requesting the run to view, log a summary message and finish
                if(bFetchError) {
                    getController().getLog().log(Log.WARNING, "Unable to fetch run " + run.getNumber() + " at site " + run.getSiteNumber() + " from server");
                    showMessage("Unable to fetch selected run; check log");
                } else {

                    //if we get here we know there should be RunFiles in the contest model -- but let's sanity-check that
                    if (!getContest().isRunFilesPresent(run)) {

                        //something bad happened -- we SHOULD have RunFiles at this point!
                        getController().getLog().log(Log.SEVERE, "Unable to find RunFiles for run " + run.getNumber() + " at site " + run.getSiteNumber() + " -- server error?");
                        showMessage("Unable to fetch selected run; check log");
                    } else {

                        //get the RunFiles
                        RunFiles runFiles = getContest().getRunFiles(run);

                        if (runFiles != null) {

                            // get the (serialized) source files out of the RunFiles
                            SerializedFile mainFile = runFiles.getMainFile();
                            SerializedFile[] otherFiles = runFiles.getOtherFiles();

                            // create a MultiFileViewer in which to display the runFiles
                            // Note: previously used 'fetchedRun' here for site/number; it is possible that those values are not
                            // correct if the run files are already present; in that case, we would have never asked for them to be
                            // retrieved, and would have used whatever was in fetchedRun.  An NPE was also possible if fetchedRun was null;
                            // that is, the runfiles were already present from an edit run or judge run on the current client, so no
                            // server request was ever made for a run's files.
                            MultipleFileViewer mfv = new MultipleFileViewer(log, "Source files for Site " + run.getSiteNumber() + " Run " + run.getNumber());
                            mfv.setContestAndController(getContest(), getController());

                            // if entry point was specified, add a tab for it
                            if(run.getEntryPoint() != null) {
                                mfv.addTextPane("Entry Point", run.getEntryPoint());
                            }
                            // add any other files to the MFV (these are added first so that the mainFile will appear at index 0)
                            boolean otherFilesPresent = false;
                            boolean otherFilesLoadedOK = false;
                            if (otherFiles != null) {
                                otherFilesPresent = true;
                                otherFilesLoadedOK = true;
                                for (SerializedFile otherFile : otherFiles) {
                                    otherFilesLoadedOK &= mfv.addFilePane(otherFile.getName(), otherFile);
                                }
                            }

                            // add the mainFile to the MFV
                            boolean mainFilePresent = false;
                            boolean mainFileLoadedOK = false;
                            if (mainFile != null) {
                                mainFilePresent = true;
                                mainFileLoadedOK = mfv.addFilePane("Main File" + " (" + mainFile.getName() + ")", mainFile);
                            }

                            // if we successfully added all files, show the MFV
                            if ((!mainFilePresent || (mainFilePresent && mainFileLoadedOK))
                                    && (!otherFilesPresent || (otherFilesPresent && otherFilesLoadedOK))) {
                                mfv.setSelectedIndex(0);  //always make leftmost selected; normally this will be MainFile
                                mfv.setVisible(true);
                                showMessage("");
                            } else {
                                getController().getLog().log(Log.WARNING, "Unable to load run source files into MultiFileViewer");
                                showMessage("Unable to load run source files into MultiFileViewer");
                            }

                        } else {
                            // runfiles is null
                            getController().getLog().log(Log.WARNING, "Unable to obtain RunFiles for Site " + run.getSiteNumber() + " run " + run.getNumber());
                            showMessage("Unable to obtain RunFiles for selected run");
                        }

                    }
                }
                return;
            } else {
                // getContest().getRun() returned null
                getController().getLog().log(Log.WARNING, "Selected run not found");
                showMessage("Selected run not found");
            }

        } catch (Exception e) {
            getController().getLog().log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to show run source, check log");

            //make sure this is clear in case of exception
            requestedRun = null;
        }

        // OK to now start another view source now
        AllowViewSource();
    }

}
