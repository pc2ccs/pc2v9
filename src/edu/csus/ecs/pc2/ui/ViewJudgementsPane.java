package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * Shows all run judgements for the input run.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ViewJudgementsPane extends JPanePlugin implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 2350404181525660056L;

    public static final String SVN_ID = "$Id$";

    private JPanel centerPane = null;

    private JPanel northPane = null;

    private JPanel titlePane = null;

    private MCLB judgementsListbox = null;

    private JLabel statusLabel = null;

    private Run run;

    private JLabel titleLabel = null;

    public ViewJudgementsPane() {
        initialize();
    }

    public void setRun(Run run) {
        this.run = run;
        populateGUI();
    }

    private void populateGUI() {
        Runnable updateGrid = new Runnable() {
            public void run() {
                // Update Run title

                String deletedString = "";
                if (run.isDeleted()){
                    deletedString = " DELETED ";
                }
                
                setTitle("Run " + run.getNumber() + " elapsed " + run.getElapsedMins()+  deletedString);

                JudgementRecord judgementRecord = run.getJudgementRecord();
                judgementsListbox.removeAllRows();

                if (judgementRecord == null) {
                    showMessage("No judgements found for Run " + run.getNumber());
                } else {
                    populateJudgementRows(run);
                }

            }

        };
        SwingUtilities.invokeLater(updateGrid);

    }

    protected void setTitle(final String infoString) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                titleLabel.setText(infoString);
            }
        });
    }

    protected String getSiteTitle(int siteNumber) {
        Site site = getModel().getSites()[siteNumber - 1];
        if (site == null) {
            return "<missing>";
        }
        return site.toString();
    }

    protected void showMessage(String string) {
        statusLabel.setText(string);

    }

    private void populateJudgementRows(Run inRun) {

        JudgementRecord[] records = inRun.getAllJudgementRecords();

        for (int i = 0; i < records.length; i++) {
            Object[] objects = buildJudgmentRow(i + 1, inRun, records[i]);
            judgementsListbox.addRow(objects);
        }
        judgementsListbox.autoSizeAllColumns();
        showMessage("There are " + records.length + " judgements");
    }

    private String yesNoString(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "No";
        }
    }

    /**
     * Return the columns to populate the grid.
     * 
     * Assumes that record is NOT null.
     * 
     * @param rowNumber
     * @param inRun
     * @param record
     * @return
     */
    private Object[] buildJudgmentRow(int rowNumber, Run inRun, JudgementRecord judgementRecord) {
        // Object[] cols = { "##", "Judgement", "Judge", "Active", "Time", "TWJ", "TTJ", "Comment for team", "Comment for judge" };

        int numberColumns = judgementsListbox.getColumnCount();
        Object[] column = new Object[numberColumns];

        column[0] = new Integer(rowNumber).toString();
        column[1] = getJudgementTitle(judgementRecord.getJudgementId());
        column[2] = judgementRecord.getJudgerClientId().getName();
        column[3] = yesNoString(judgementRecord.isActive());
        column[4] = "" + inRun.getElapsedMins();
        column[5] = "" + judgementRecord.getWhenJudgedTime();
        column[6] = (judgementRecord.getHowLongToJudgeInSeconds() / 1000) + "s";

        if (judgementRecord.getCommentForTeam() != null) {
            column[7] = judgementRecord.getCommentForTeam().getComment();
        } else {
            column[7] = "(None)";
        }

        if (judgementRecord.getCommentForJudge() != null) {
            column[8] = judgementRecord.getCommentForJudge().getComment();
        } else {
            column[8] = "(None)";
        }

        return column;
    }

    private String getJudgementTitle(ElementId judgementId) {
        Judgement judgement = getModel().getJudgement(judgementId);
        if (judgement == null) {
            return "<missing>";
        }
        return judgement.toString();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(527, 195));
        this.setLayout(new BorderLayout());
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getNorthPane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getJudgementsListbox(), java.awt.BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPane() {
        if (northPane == null) {
            statusLabel = new JLabel();
            statusLabel.setText("statusLabel");
            statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            statusLabel.setName("statusLabel");
            northPane = new JPanel();
            northPane.setLayout(new BorderLayout());
            northPane.setPreferredSize(new java.awt.Dimension(35, 35));
            northPane.add(statusLabel, java.awt.BorderLayout.CENTER);
        }
        return northPane;
    }

    /**
     * This method initializes jPanel2
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (titlePane == null) {
            titleLabel = new JLabel();
            titleLabel.setText("No run chosen");
            titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            titlePane = new JPanel();
            titlePane.setLayout(new BorderLayout());
            titlePane.setPreferredSize(new java.awt.Dimension(35, 35));
            titlePane.add(titleLabel, java.awt.BorderLayout.CENTER);
        }
        return titlePane;
    }

    /**
     * This method initializes multiColumnListbox
     * 
     * @return com.ibm.webrunner.j2mclb.MCLB
     */
    private MCLB getJudgementsListbox() {
        if (judgementsListbox == null) {
            judgementsListbox = new MCLB();
            Object[] cols = { "##", "Judgement", "Judge", "Active", "Time", "TWJ", "TTJ", "Comment for team", "Comment for judge" };
            judgementsListbox.addColumns(cols);
            judgementsListbox.autoSizeAllColumns();
        }
        return judgementsListbox;
    }

    @Override
    public String getPluginTitle() {
        return "Run Judgement View";
    }
    
    public void setModelAndController(IContest inModel, IController inController) {
        super.setModelAndController(inModel, inController);
        
        getModel().addRunListener(new RunListenerImplementation());
    }
    
    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            if (run != null && event.getRun().getElementId().equals(run.getElementId())) {
                setRun(event.getRun());
            }
        }

        public void runChanged(RunEvent event) {
            if (run != null && event.getRun().getElementId().equals(run.getElementId())) {
                setRun(event.getRun());
            }
        }

        public void runRemoved(RunEvent event) {
            if (run != null && event.getRun().getElementId().equals(run.getElementId())) {
                setRun(event.getRun());
            }
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
