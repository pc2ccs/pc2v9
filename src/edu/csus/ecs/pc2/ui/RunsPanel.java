package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.MCLB;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * 
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
        this.add(getRunsListBox(), java.awt.BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Runs Panel";
    }
    
    protected Object[] buildRunRow(Run run, ClientId judgeId) {
        // Object[] cols = {"Site","Team","Run Id","Time","Status", "Judge",
        // "Problem","Language","OS"};
        try {
            int cols = runListBox.getColumnCount();
            Object[] s = new String[cols];

            s[0] = getSiteTitle("" + run.getSubmitter().getSiteNumber());
            s[1] = getTeamDisplayName(run);
            s[2] = new Long(run.getNumber()).toString();
            s[3] = new Long(run.getElapsedMins()).toString();
            if (run.getStatus() == RunStates.JUDGED) {
                s[4] = getJudgementTitle(run.getJudgementRecord().getJudgementId());
            } else {
                s[4] = run.getStatus().toString();
            }
            if (judgeId != null) {
                s[5] = judgeId.getName();
            } else {
                s[5] = "";
            }

            s[6] = getProblemTitle(run.getProblemId());
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
        for (Language language : getModel().getLanguages()){
            if (language.getElementId().equals(languageId)) {
                return language.toString();
            }
        }
        return "Language ?";
    }

    private String getProblemTitle(ElementId problemId) {
        Problem problem = getModel().getProblem(problemId);
        System.out.println("debug getProblemTitle - "+problemId+" "+problem);
        if (problem != null){
            return problem.toString();
        }
        return "Problem ?";
    }

    private String getSiteTitle(String string) {
        // TODO Auto-generated method stub
        return "Site "+string;
    }

    private String getTeamDisplayName(Run run) {
        Account account = getModel().getAccount(run.getSubmitter());
        if (account != null ){
            return account.getDisplayName();
        }
        
        return run.getSubmitter().getName(); 
    }

    private Object getJudgementTitle(ElementId judgementId) {
        Judgement judgement = getModel().getJudgement(judgementId);
        if (judgement != null){
            return judgement.toString();
        }
        return "Judgement ?";
    }


    /**
     * 
     *
     * @author pc2@ecs.csus.edu
     */
    
    // $HeadURL$
    public class RunListenerImplementation implements IRunListener{

        public void runAdded(RunEvent event) {
            updateRunRow (event.getRun(), event.getWhoModifiedRun());
        }

        public void runChanged(RunEvent event) {
            updateRunRow (event.getRun(), event.getWhoModifiedRun());
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

            Object[] cols = { "Site", "Team", "Run Id", "Time", "Status", "Judge", "Problem", "Language", "OS" };
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

            // Judge
            runListBox.setColumnSorter(5, sorter, 6);

            // Problem
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
                Object[] objects = buildRunRow(run, whoModifiedId);
                int rowNumber = runListBox.getIndexByKey(run.getElementId());
                if (rowNumber == -1) {
                    runListBox.addRow(objects, run.getElementId());
                } else {
                    runListBox.replaceRow(objects, rowNumber);
                }
                runListBox.autoSizeAllColumns();
                runListBox.sort();
            }
        });
    }
    
    public void reloadRunList () {
        
        Problem [] l = getModel().getProblems();
        for (Problem problem : l){
            System.out.println(problem.getElementId() + "  " +problem);
        }

        Run [] runs = getModel().getRuns();
        
        // TODO bulk load these record
        
        for (Run run : runs){
            
            ClientId clientId = null;
            
            RunStates runStates =  run.getStatus();
            if ( ! (runStates.equals(RunStates.NEW) || run.isDeleted())){
                JudgementRecord judgementRecord = run.getJudgementRecord();
                if (judgementRecord != null) {
                    clientId = judgementRecord.getJudgerClientId(); 
                }
            }
            updateRunRow(run, clientId);
        }
    }
    

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);
        
        getModel().addRunListener(new RunListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadRunList();
            }
        });
    }

} // @jve:decl-index=0:visual-constraint="10,10"
