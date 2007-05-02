package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
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
                s[4] = run.getStatus().toString();
            }

            if (run.isDeleted()) {
                s[4] = "DEL " + s[4];
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
        Account account = getContest().getAccount(run.getSubmitter());
        if (account != null) {
            return account.getDisplayName();
        }

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
        }

        public void runChanged(RunEvent event) {
            updateRunRow(event.getRun(), event.getWhoModifiedRun());
        }

        public void runRemoved(RunEvent event) {
            // TODO Auto-generated method stub
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

            Object[] cols = { "Site", "Team", "RunId", "Time", "Status", "Problem", "Judge", "Language", "OS" };
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

    public void updateRunRow(final Run run, final ClientId whoModifiedId) {

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
                rowCountLabel.setText(""+runListBox.getRowCount());
                rowCountLabel.setToolTipText("There are "+runListBox.getRowCount()+" runs");
                runListBox.autoSizeAllColumns();
                runListBox.sort();
            }
        });
    }

    public void reloadRunList() {

        Run[] runs = getContest().getRuns();

        // TODO bulk load these record

        for (Run run : runs) {

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
        
        requestRunButton.setVisible(isAllowed(Permission.Type.JUDGE_RUN));
        editRunButton.setVisible(isAllowed(Permission.Type.EDIT_RUN));
        extractButton.setVisible(isAllowed(Permission.Type.EXTRACT_RUNS));
        giveButton.setVisible(isAllowed(Permission.Type.GIVE_RUN));
        takeButton.setVisible(isAllowed(Permission.Type.TAKE_RUN));
        rejudgeRunButton.setVisible(isAllowed(Permission.Type.REJUDGE_RUN));
        viewJudgementsButton.setVisible(isAllowed(Permission.Type.VIEW_RUN_JUDGEMENT_HISTORIES));
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        
        log = getController().getLog();
        
        editRunFrame.setContestAndController(getContest(), getController());
        viewJudgementsFrame.setContestAndController(getContest(), getController());
        selectJudgementFrame.setContestAndController(getContest(), getController());

        getContest().addRunListener(new RunListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());

        initializePermissions();
        
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
            showMessage("Unable to edit run, check log");
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
            showMessage("Unable to edit run, check log");
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
            showMessage("Unable to edit run, check log");
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
                    }
                });
                
            }
            
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

} // @jve:decl-index=0:visual-constraint="10,10"
