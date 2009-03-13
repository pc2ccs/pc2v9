package edu.csus.ecs.pc2.api;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;
import edu.csus.ecs.pc2.ui.AccountColumnComparator;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.MCLB;

/**
 * API Runs Grid.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunsFrame extends JFrame {

    private IContest contest = null;

    /**
     * 
     */
    private static final long serialVersionUID = 2069577760789317376L;

    private JPanel mainPane = null;

    private JPanel buttonPane = null;

    private JButton closeButton = null;

    private MCLB runsListBox = null;

    public RunsFrame(IContest contest) throws HeadlessException {
        super();
        initialize();
        this.contest = contest;

        reloadRows();
        contest.addRunListener(new RunEventListenerImplementation());
        contest.addContestConfigurationUpdateListener(new ConfigurationUpdateListenerImplementation());
        
        IClient client = contest.getMyClient();
        setTitle("API Runs Frame - "+client.getLoginName()+" (Site "+client.getSiteNumber()+")");
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(575, 274));
        this.setContentPane(getMainPane());
        this.setTitle("Runs List");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                setVisible(false);
            }
        });
        
        FrameUtilities.centerFrame(this);
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
            mainPane.add(getButtonPane(), BorderLayout.SOUTH);
            mainPane.add(getRunsListBox(), BorderLayout.CENTER);
        }
        return mainPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout());
            buttonPane.add(getCloseButton(), null);
        }
        return buttonPane;
    }

    protected Object[] buildRunRow(IRun run) {
//        Object[] cols = { "Site", "Team", "Id", "Time", "Status", "Problem", "Language"};

         int cols = runsListBox.getColumnCount();
         Object[] s = new String[cols];
         
         int idx = 0;
         s[idx++] = run.getSiteNumber()+"";
         s[idx++] = run.getTeam().getLoginName();
         s[idx++] = run.getNumber()+"";
         s[idx++] = run.getSubmissionTime()+"";
         
         String judgementName = run.getJudgementName();
         if (judgementName.length() == 0){
             judgementName = "NEW";
         } else if (isPreliminary(run)) {
             judgementName = "PRELIM ("+judgementName+")";
         }
         s[idx++] = judgementName;
         
         s[idx++] = run.getProblem().getName();
         s[idx++] = run.getLanguage().getName();
         
         return s;
    }

    private boolean isPreliminary(IRun run) {
        IRunJudgement [] judgements = run.getRunJudgements();
        
        if (judgements.length > 0){
            return judgements[judgements.length-1].isPreliminaryJudgement();
        } else{
            return false;
        }
    }

    protected String getRunKey (IRun run){
        return run.getSiteNumber()+":"+run.getNumber();
    }
    
    protected void reloadRows(){
        for (IRun run : contest.getRuns()){
            updateRunRow(run);
        }
    }
    
    protected void updateRunRow(final IRun run){
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildRunRow(run);
                int rowNumber = runsListBox.getIndexByKey(getRunKey(run));
                if (rowNumber == -1) {
                    runsListBox.addRow(objects, getRunKey(run));
                } else {
                    runsListBox.replaceRow(objects, rowNumber);
                }

                runsListBox.autoSizeAllColumns();
                runsListBox.sort();
            }
        });
    }
    
    /**
     * This method initializes closeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setText("Close");
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    setVisible(false);
                }
            });
        }
        return closeButton;
    }

    /**
     * This method initializes runsListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getRunsListBox() {
        if (runsListBox == null) {
            runsListBox = new MCLB();

            runsListBox.removeAllRows();

            Object[] cols = { "Site", "Team", "Id", "Time", "Status", "Problem", "Language"};
            runsListBox.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());
            HeapSorter accountNameSorter = new HeapSorter();
            accountNameSorter.setComparator(new AccountColumnComparator());

            int idx = 0;

            runsListBox.setColumnSorter(idx++, numericStringSorter, 4); // Site
            runsListBox.setColumnSorter(idx++, accountNameSorter, 3); // Team
            runsListBox.setColumnSorter(idx++, numericStringSorter, 1); // Id
            runsListBox.setColumnSorter(idx++, numericStringSorter, 2); // Time
            runsListBox.setColumnSorter(idx++, sorter, 5); // Status
            runsListBox.setColumnSorter(idx++, sorter, 6); // Problem
            runsListBox.setColumnSorter(idx++, sorter, 7); // Language
        }
        
        return runsListBox;
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class ConfigurationUpdateListenerImplementation implements IConfigurationUpdateListener {

        public void configurationItemAdded(ContestEvent contestEvent) {
            reloadRows();
        }

        public void configurationItemRemoved(ContestEvent contestEvent) {
            reloadRows();
        }

        public void configurationItemUpdated(ContestEvent contestEvent) {
            reloadRows();
        }
    }

    /**
     * RunsFrames - Run Event Listener implementation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class RunEventListenerImplementation implements IRunEventListener {

        public void runCheckedOut(IRun run, boolean isFinal) {
            runUpdated(run, isFinal);
        }

        public void runCompiling(IRun run, boolean isFinal) {
            runUpdated(run, isFinal);
        }

        public void runDeleted(IRun run) {
            runUpdated(run, false);
        }

        public void runExecuting(IRun run, boolean isFinal) {
            runUpdated(run, isFinal);
        }

        public void runJudged(IRun run, boolean isFinal) {
            runUpdated(run, isFinal);
        }

        public void runJudgingCanceled(IRun run, boolean isFinal) {
            runUpdated(run, isFinal);
        }

        public void runSubmitted(IRun run) {
            runUpdated(run, false);
        }

        public void runUpdated(IRun run, boolean isFinal) {
            updateRunRow(run);
        }

        public void runValidating(IRun run, boolean isFinal) {
            runUpdated(run, isFinal);
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
