package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContest;
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
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import javax.swing.JLabel;

/**
 * View runs.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$$
public class RunsPanel extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 114647004580210428L;

    private MCLB runListBox = null;

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
    
    private PermissionList permissionList = new PermissionList();
    
    private EditRunFrame editRunFrame = null;
    
    private ViewJudgementsFrame viewJudgementsFrame = null;
    
    private SelectJudgementFrame selectJudgementFrame = null;

    private JLabel messageLabel = null;
    
    private Log log = null;

    private JLabel rowCountLabel = null;
    
    private boolean showNewRunsOnly = false;
    
    /**
     * Show who is judging column and status.
     * 
     */
    private boolean showJudgesInfo = true;
    
    private Filter filter = null;

    /**
     * This method initializes
     * 
     */
    public RunsPanel() {
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
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        this.add(getRunsListBox(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        
        editRunFrame = new EditRunFrame();
        viewJudgementsFrame = new ViewJudgementsFrame();
        selectJudgementFrame = new SelectJudgementFrame();

    }

    @Override
    public String getPluginTitle() {
        return "Runs Panel";
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
                    if (judgement != null){
                        s[4] = judgement.toString();
                    }
                }

            } else {
                if (showJudgesInfo){
                    s[4] = run.getStatus().toString();
                } else {
                    s[4] = RunStates.NEW.toString();
                }
            }

            if (run.isDeleted()) {
                s[4] = "DEL " + s[4];
            }

            s[5] = getProblemTitle(run.getProblemId());
            
            int idx = 6;
            
            if (showJudgesInfo){
                if (judgeId != null) {
                    if (judgeId.equals(getContest().getClientId())) {
                        s[6] = "Me";
                    } else {
                        s[6] = judgeId.getName();
                    }
                } else {
                    s[6] = "";
                }
                idx++;
            }                
            s[idx++] = getLanguageTitle(run.getLanguageId());
            s[idx++] = run.getSystemOS();

            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildRunRow()", exception);
        }
        return null;
    }

    private String getLanguageTitle(ElementId languageId) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return "Site " + string;
    }

    private String getTeamDisplayName(Run run) {
        // TODO when team display configuration is in a GUI somewhere
        // modify this code to show the configured way of showing the team.
        
//        Account account = getContest().getAccount(run.getSubmitter());
//        if (account != null) {
//            return account.getDisplayName();
//        }

        return run.getSubmitter().getName();
    }

    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */

    // $HeadURL$
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            updateRunRow(event.getRun(), event.getWhoModifiedRun());
            //check if this is a team; if so, pop up a confirmation dialog
            if (getContest().getClientId().getClientType()==ClientType.Type.TEAM) {
                showResponseToTeam(event);
            }
        }

        public void runChanged(RunEvent event) {
            updateRunRow(event.getRun(), event.getWhoModifiedRun());
            
            //check if this is a team; if so, pop up a response dialog
            if (getContest().getClientId().getClientType()==ClientType.Type.TEAM) {
                showResponseToTeam(event);
            }
        }
                
        public void runRemoved(RunEvent event) {
            // TODO Auto-generated method stub
        }
        
        /**
         * Checks the run in the specified run event and (potentially) displays a results
         * dialog.  If the run has been judged,
         * has a valid judgement record, and is the "active" judgement for scoring
         * purposes, displays a modal MessageDialog to the Team containing the judgement
         * results.  This method assumes the caller has already verified this is a TEAM
         * client; failure to do that on the caller's part will cause other clients to
         * see the Run Response dialog...
         */
        private void showResponseToTeam (RunEvent event) {
            
            Run theRun = event.getRun();
            String problemName = getContest().getProblem(theRun.getProblemId()).toString() ;
            String languageName = getContest().getLanguage(theRun.getLanguageId()).toString() ;
            int runId = theRun.getNumber() ;
            //build an HTML tag that sets the font size and color for the answer
            String responseFormat = "";

            //check if the run has been judged 
            if (theRun.isJudged()) {
                
                //check if there's a legit judgement
                JudgementRecord judgementRecord = theRun.getJudgementRecord();
                if (judgementRecord != null) {
                    
                    //check if this is the scoreable judgement
                    boolean isActive = judgementRecord.isActive ();
                    if (isActive) {
                        
                        //it's a valid judging response (presumably to a team); 
                        //  get the info from the run and display it in a modal popup
                        if (judgementRecord.isSolved()) {
                            responseFormat += "<FONT COLOR=\"00FF00\" SIZE=+2>" ;  //green, larger
                        } else {
                            responseFormat += "<FONT COLOR=RED>";               //red, current size
                        }
                        String response = getContest().getJudgement(judgementRecord.getJudgementId()).toString() ;
                        String judgeComment = theRun.getCommentsForTeam();
                       try {
                            String displayString =
                                "<HTML><FONT SIZE=+1>Judge's Response<BR><BR>"
                                + "Problem: <FONT COLOR=BLUE>" +  problemName + "</FONT><BR><BR>" 
                                + "Language: <FONT COLOR=BLUE>" + languageName   + "</FONT><BR><BR>" 
                                + "Run Id: <FONT COLOR=BLUE>" + runId + "</FONT><BR><BR><BR>" 
                                + "Judge's Response: " + responseFormat + response + "</FONT><BR><BR><BR>" ;
    
                            if (judgeComment!=null) {
                                if (judgeComment.length() > 0) {
                                    displayString += "Judge's Comment: " + judgeComment + "<BR><BR><BR>";
                                }
                            }
                                
                            displayString += "</FONT></HTML>" ;

                            JOptionPane.showMessageDialog(null, displayString, 
                                        "Run Judgement Received", JOptionPane.INFORMATION_MESSAGE );
    
                        } catch (Exception e) {
                            // TODO need to make this cleaner
                            JOptionPane.showMessageDialog(null, "Exception handling Run Response: " + e.getMessage());
                            log.warning("Exception handling Run Response: " + e.getMessage());
                        }
                    }
                }
            } else {
                // not judged
                try {
                    String displayString =
                        "<HTML><FONT SIZE=+1>Confirmation of Run Receipt<BR><BR>"
                        + "Problem: <FONT COLOR=BLUE>" +  problemName + "</FONT><BR><BR>" 
                        + "Language: <FONT COLOR=BLUE>" + languageName   + "</FONT><BR><BR>" 
                        + "Run Id: <FONT COLOR=BLUE>" + runId + "</FONT><BR><BR><BR>";
                        
                    displayString += "</FONT></HTML>" ;

                    JOptionPane.showMessageDialog(null, displayString, 
                                "Run Received", JOptionPane.INFORMATION_MESSAGE );

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Exception handling Run Confirmation: " + e.getMessage());
                    log.warning("Exception handling Run Confirmation: " + e.getMessage());
                }
            }
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

            runListBox.addListboxListener(new com.ibm.webrunner.j2mclb.event.ListboxListener() {
                public void rowSelected(com.ibm.webrunner.j2mclb.event.ListboxEvent e) {
                    if (isAllowed(Permission.Type.JUDGE_RUN) && e.getClickCount() >= 2) {
                        requestSelectedRun();
                    }
                }
                public void rowDeselected(com.ibm.webrunner.j2mclb.event.ListboxEvent e) {
                }
            });
         
            resetRunsListBoxColumns ();
        }
        return runListBox;
    }

    private void resetRunsListBoxColumns() {
        
        runListBox.removeAllRows();
        runListBox.removeAllColumns();
        
        runListBox.addColumn("Site");
        runListBox.addColumn("Team");
        runListBox.addColumn("Run Id");
        runListBox.addColumn("Time");
        runListBox.addColumn("Status");
        runListBox.addColumn("Problem");
        if (showJudgesInfo) {
            runListBox.addColumn("Judge");
        }
        runListBox.addColumn("Language");
        runListBox.addColumn("OS");

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

        if (showJudgesInfo) {
            // Judge
            runListBox.setColumnSorter(6, sorter, 7);
            
            // Language
            runListBox.setColumnSorter(7, sorter, 8);
            
            // OS
            runListBox.setColumnSorter(8, sorter, 9);
            
        } else {
            
            // Language
            runListBox.setColumnSorter(6, sorter, 7);
            
            // OS
            runListBox.setColumnSorter(7, sorter, 8);
        }

        runListBox.autoSizeAllColumns();
    }

    /**
     * Remove run from grid.
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

    /**
     * This updates the rowCountlabel & toolTipText.
     * It should be called only while on the swing thread.
     */
    private void updateRowCount() {
        rowCountLabel.setText(""+runListBox.getRowCount());
        rowCountLabel.setToolTipText("There are "+runListBox.getRowCount()+" runs");
    }
    
    public void updateRunRow(final Run run, final ClientId whoModifiedId) {
        
        if (filter != null){
            
            if (! filter.matches(run)){
                // if run does not match filter, be sure to remove it from grid
                // This applies when a run is New then BEING_JUDGED and other conditions.
                removeRunRow(run);
                return;
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                ClientId whoJudgedId = whoModifiedId;
                if (run.isJudged()) {
                    JudgementRecord judgementRecord = run.getJudgementRecord();
                    if (judgementRecord != null) {
                        whoJudgedId = judgementRecord.getJudgerClientId();
                    }
                }
                
                Object[] objects = buildRunRow(run, whoJudgedId);
                int rowNumber = runListBox.getIndexByKey(run.getElementId());
                if (rowNumber == -1) {
                    runListBox.addRow(objects, run.getElementId());
                } else {
                    runListBox.replaceRow(objects, rowNumber);
                }
                updateRowCount();
                runListBox.autoSizeAllColumns();
                runListBox.sort();
                if (selectJudgementFrame != null) {
                    // return focus to the other frame if it is around
                    /*
                     * XXX this is hack which causes the frame to flicker
                     * when coming from the listbox double click
                     * as it comes in front, goes behind, then comes back in front. 
                     * But could not find another way to keep focus on the selectJudgementFrame,
                     * otherwise it would be beind the RunsPanel
                     */
                    selectJudgementFrame.requestFocus();
                }
            }
        });
    }

    public void reloadRunList() {

        Run[] runs = getContest().getRuns();

        // TODO bulk load these record

        for (Run run : runs) {
            
            if (filter != null){
                if (! filter.matches(run)){
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
        if (runs.length < 1) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    rowCountLabel.setText(""+runListBox.getRowCount());
                    rowCountLabel.setToolTipText("There are "+runListBox.getRowCount()+" runs");
                }
            });
        }
    }
    
    private boolean isAllowed (Permission.Type type){
        return permissionList.isAllowed(type);
    }
    
    
    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        if (account != null){
            permissionList.clearAndLoadPermissions(account.getPermissionList());
        }
    }

    private void updateGUIperPermissions() {
        
        if (showNewRunsOnly){

            requestRunButton.setVisible(true);
            requestRunButton.setEnabled(isAllowed(Permission.Type.JUDGE_RUN));
            editRunButton.setVisible(false);
            extractButton.setVisible(false);
            giveButton.setVisible(false);
            takeButton.setVisible(false);
            rejudgeRunButton.setVisible(false);
            viewJudgementsButton.setVisible(false);

        } else {
        
            requestRunButton.setVisible(isAllowed(Permission.Type.JUDGE_RUN));
            editRunButton.setVisible(isAllowed(Permission.Type.EDIT_RUN));
            extractButton.setVisible(isAllowed(Permission.Type.EXTRACT_RUNS));
            giveButton.setVisible(isAllowed(Permission.Type.GIVE_RUN));
            takeButton.setVisible(isAllowed(Permission.Type.TAKE_RUN));
            rejudgeRunButton.setVisible(isAllowed(Permission.Type.REJUDGE_RUN));
            viewJudgementsButton.setVisible(isAllowed(Permission.Type.VIEW_RUN_JUDGEMENT_HISTORIES));
            
        }
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        
        log = getController().getLog();

        initializePermissions();
        
        editRunFrame.setContestAndController(getContest(), getController());
        viewJudgementsFrame.setContestAndController(getContest(), getController());
        if (isAllowed(Permission.Type.JUDGE_RUN)) {
            selectJudgementFrame.setContestAndController(getContest(), getController());
        }

        getContest().addRunListener(new RunListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addLanguageListener(new LanguageListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                reloadRunList();
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
            rowCountLabel.setText("###");
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new java.awt.Dimension(30,30));
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
            buttonPanel.setPreferredSize(new java.awt.Dimension(35,35));
            buttonPanel.add(getGiveButton(), null);
            buttonPanel.add(getTakeButton(), null);
            buttonPanel.add(getEditRunButton(), null);
            buttonPanel.add(getViewJudgementsButton(), null);
            buttonPanel.add(getRequestRunButton(), null);
            buttonPanel.add(getRejudgeRunButton(), null);
            buttonPanel.add(getFilterButton(), null);
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
            requestRunButton.setMnemonic(java.awt.event.KeyEvent.VK_R);
            requestRunButton.addActionListener(new java.awt.event.ActionListener() {
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
        int [] selectedIndexes = runListBox.getSelectedIndexes();
        
        if (selectedIndexes.length < 1){
            showMessage("Please select a run ");
            return;
        }
        
        try {
            ElementId elementId = (ElementId) runListBox.getKeys()[selectedIndexes[0]];
            Run runToEdit = getContest().getRun(elementId);
            
            if ((! runToEdit.getStatus().equals(RunStates.NEW)) || runToEdit.isDeleted()){
                showMessage("Not allowed to request run, already judged");
                return;
            }

            selectJudgementFrame.setRun(runToEdit);
            selectJudgementFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to request run, check log");
        }   
    }
    
    protected void rejudgeSelectedRun () {
        
        int [] selectedIndexes = runListBox.getSelectedIndexes();
        
        if (selectedIndexes.length < 1){
            showMessage("Please select a run ");
            return;
        }
        
        try {
            ElementId elementId = (ElementId) runListBox.getKeys()[selectedIndexes[0]];
            Run runToEdit = getContest().getRun(elementId);
            
            if (! runToEdit.isJudged()){
                showMessage("Judge run before attempting to re-judge run");
                return;
            }
            
            if (runToEdit.isDeleted()){
                showMessage("Not allowed to rejudge deleted run ");
                return;
            }

            showMessage("Would have rejudged run "+runToEdit.getNumber());
//            selectJudgementFrame.setRun(runToEdit);
//            selectJudgementFrame.setVisible(true);
            
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
            filterButton.setMnemonic(java.awt.event.KeyEvent.VK_F);
            filterButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    filterRuns();
                }
            });
        }
        return filterButton;
    }

    protected void filterRuns() {
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadRunList();
                rowCountLabel.setText(""+runListBox.getRowCount());
                rowCountLabel.setToolTipText("There are "+runListBox.getRowCount()+" runs");
            }
        });
        
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
            editRunButton.setMnemonic(java.awt.event.KeyEvent.VK_E);
            editRunButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedRun();
                }
            });
        }
        return editRunButton;
    }

    protected void editSelectedRun() {
        
        int [] selectedIndexes = runListBox.getSelectedIndexes();
        
        if (selectedIndexes.length < 1){
            showMessage("Please select a run ");
            return;
        }
        
        try {
            ElementId elementId = (ElementId) runListBox.getKeys()[selectedIndexes[0]];
            Run runToEdit = getContest().getRun(elementId);

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
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return extractButton;
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
            giveButton.setMnemonic(java.awt.event.KeyEvent.VK_G);
            giveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
            rejudgeRunButton.addActionListener(new java.awt.event.ActionListener() {
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
            viewJudgementsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewSelectedRunJudgements();
                }
            });
        }
        return viewJudgementsButton;
    }
    
    protected void viewSelectedRunJudgements() {
        
        int [] selectedIndexes = runListBox.getSelectedIndexes();
        
        if (selectedIndexes.length < 1){
            showMessage("Please select a run ");
            return;
        }
        
        try {
            ElementId elementId = (ElementId) runListBox.getKeys()[selectedIndexes[0]];
            Run theRun = getContest().getRun(elementId);
            
            if (theRun != null){
                viewJudgementsFrame.setRun(theRun);
                viewJudgementsFrame.setVisible(true);
            } else {
                showMessage("Can not display judgements for Run");
            }

            
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to view run, check log");
        }
        
        

        
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignore doesn't affect this pane
        }

        public void accountModified(AccountEvent event) {
            // check if is this account
            Account account = event.getAccount();
            /**
             * If this is the account then update the GUI display per
             * the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                        reloadRunList();
                    }
                });
                
            } else {
                // not us, but update the grid anyways
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        reloadRunList();
                    }
                });

            }
            
        }
        
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
            // ignore does not affect this pane
        }

        public void problemChanged(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadRunList();
                }
            });
        }

        public void problemRemoved(ProblemEvent event) {
            // ignore does not affect this pane
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class LanguageListenerImplementation implements ILanguageListener {

        public void languageAdded(LanguageEvent event) {
            // ignore does not affect this pane
        }

        public void languageChanged(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadRunList();
                }
            });
        }

        public void languageRemoved(LanguageEvent event) {
            // ignore does not affect this pane
        }
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
            }
        });

    }

    public boolean isShowNewRunsOnly() {
        return showNewRunsOnly;
    }

    public void setShowNewRunsOnly(boolean showNewRunsOnly) {
        this.showNewRunsOnly = showNewRunsOnly;

        if (showNewRunsOnly){
            if (filter == null){
                filter = new Filter();
            }
            filter.addRunState(RunStates.NEW);
        }
        // TODO code handle if they turn off the show new clarifications only.
        
    }

    public boolean isShowJudgesInfo() {
        return showJudgesInfo;
    }

    public void setShowJudgesInfo(boolean showJudgesInfo) {
        this.showJudgesInfo = showJudgesInfo;
        resetRunsListBoxColumns();
    }

} // @jve:decl-index=0:visual-constraint="10,10"
