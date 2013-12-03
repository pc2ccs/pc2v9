package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.report.FinalizeReport;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FinalizePane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 3089291613784484371L;

    private JPanel buttonPane = null;

    private JButton updateButton = null;

    private JButton finalizeButton = null;

    private JPanel centerPane = null;

    private JLabel goldLabel = null;

    private JLabel silverLabel = null;

    private JLabel bronzeLabel = null;

    private JLabel certifierLabel = null;

    private JTextField goldRankTextField = null;

    private JTextField silverRankTextField = null;

    private JTextField bronzeRankTextField = null;

    private JTextField commentTextField = null;

    private JLabel certificationCommentLabel = null;

    private JButton reportButton = null;

    /**
     * This method initializes
     * 
     */
    public FinalizePane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(457, 239));
        this.add(getButtonPane(), BorderLayout.SOUTH);
        this.add(getCenterPane(), BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Profile Status Pane";
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(45);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new Dimension(35, 35));
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getFinalizeButton(), null);
            buttonPane.add(getReportButton(), null);
        }
        return buttonPane;
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadFrame();
            }
        });

        getContest().addContestInformationListener(new ContestInformationListenerImplementation());

    }

    protected FinalizeData getFromFields() {
        FinalizeData data = new FinalizeData();

        data.setGoldRank(getIntegerValue(getGoldRankTextField()));
        data.setSilverRank(getIntegerValue(getSilverRankTextField()));
        data.setBronzeRank(getIntegerValue(getBronzeRankTextField()));
        data.setComment("" + getCommentTextField().getText());

        return data;
    }

    private int getIntegerValue(JTextField textField) {
        String s = "0" + textField.getText();
        return Integer.parseInt(s);
    }

    private void populateDefaults() {
        getGoldRankTextField().setText("4");
        getSilverRankTextField().setText("8");
        getBronzeRankTextField().setText("12");
    }

    protected void reloadFrame() {

        FinalizeData data = getContest().getFinalizeData();
        if (data != null) {
            getGoldRankTextField().setText(Integer.toString(data.getGoldRank()));
            getSilverRankTextField().setText(Integer.toString(data.getSilverRank()));
            getBronzeRankTextField().setText(Integer.toString(data.getBronzeRank()));
            getCommentTextField().setText(data.getComment());

            if (data.isCertified()) {
                certificationCommentLabel.setText("Contest Finalized (Certified done)");
                certificationCommentLabel.setToolTipText("Certified at: " + data.getCertificationDate());
            }

        } else {
            certificationCommentLabel.setText("Contest not finalized");
            certificationCommentLabel.setToolTipText("");
            populateDefaults();
        }
        
        enableButtons();

    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.setMnemonic(KeyEvent.VK_U);
            updateButton.setToolTipText("Update data do not finalize");
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateFinalizeData();
                }
            });
        }
        return updateButton;
    }

    /**
     * Just update the data, do not certify/finalize.
     */
    protected void updateFinalizeData() {

        FinalizeData data = getFromFields();

        FinalizeData contestFinalizedata = getContest().getFinalizeData();

        if (contestFinalizedata != null) {
            data.setCertified(contestFinalizedata.isCertified());
        }

        getController().updateFinalizeData(data);
    }

    /**
     * This method initializes finalizeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getFinalizeButton() {
        if (finalizeButton == null) {
            finalizeButton = new JButton();
            finalizeButton.setText("Finalize");
            finalizeButton.setMnemonic(KeyEvent.VK_Z);
            finalizeButton.setToolTipText("Certify Contest Results");
            finalizeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    certifyContest();
                }
            });
        }
        return finalizeButton;
    }

    protected void certifyContest() {

        FinalizeData data = getFromFields();

        int numberUnjudgedRuns = getNumberUnjudgedRuns();
        if (numberUnjudgedRuns > 0) {
            showMessage("Warning " + numberUnjudgedRuns + " unjudged runs");
        }

        int numberUnasweredClars = getNumberUnansweredClars();
        if (numberUnasweredClars > 0) {
            showMessage("Warning " + numberUnasweredClars + " un-answered clarifications");
        }

        ContestTime contestTime = getContest().getContestTime();

        if (contestTime.isContestRunning()) {

            showMessage("Warning - contest not stopped");
        }

        if (contestTime.getRemainingSecs() > 0) {
            showMessage("Warning - contest not over - remaining time: " + contestTime.getRemainingTimeStr());
        }

        try {
            validateData(data);
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        data.setCertified(true);

        int result = FrameUtilities.yesNoCancelDialog(this, "Are you sure you want to finalize?", "Sure, really realy sure?");
        if (result == JOptionPane.YES_OPTION) {
            getController().updateFinalizeData(data);
        }

    }

    private int getNumberUnjudgedRuns() {
        Run[] runs = getContest().getRuns();

        Filter filter = new Filter();
        filter.addRunState(RunStates.JUDGED);

        int deletedRuns = 0;
        for (Run run : runs) {
            if (run.isDeleted()) {
                deletedRuns++;
            }
        }

        return runs.length - deletedRuns - filter.getRuns(runs).length;
    }

    private int getNumberUnansweredClars() {
        Clarification[] clarifications = getContest().getClarifications();

        int count = 0;
        for (Clarification clarification : clarifications) {
            if (!clarification.isAnswered()) {
                count++;
            }
        }

        return count;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Validate and error check data.
     * 
     * @param data
     */
    private void validateData(FinalizeData data) {

        if (data.getGoldRank() == 0) {
            throw new InvalidFieldValue("Gold rank must be greater than zero");
        }
        
        if (data.getSilverRank() == 0) {
            throw new InvalidFieldValue("Silver rank must be greater than zero");
        }
        
        if (data.getBronzeRank() == 0) {
            throw new InvalidFieldValue("Bronze rank must be greater than zero");
        }

        if (data.getComment().trim().length() < 1) {
            throw new InvalidFieldValue("Missing comment, enter a comment");
        }

    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            certificationCommentLabel = new JLabel();
            certificationCommentLabel.setBounds(new Rectangle(30, 19, 402, 26));
            certificationCommentLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            certificationCommentLabel.setHorizontalAlignment(SwingConstants.CENTER);
            certificationCommentLabel.setText("Contest Not Certified");
            certifierLabel = new JLabel();
            certifierLabel.setBounds(new Rectangle(53, 171, 125, 22));
            certifierLabel.setText("Who certifies");
            certifierLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            bronzeLabel = new JLabel();
            bronzeLabel.setBounds(new Rectangle(53, 134, 125, 22));
            bronzeLabel.setText("Last Bronze Rank");
            bronzeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            silverLabel = new JLabel();
            silverLabel.setBounds(new Rectangle(53, 97, 125, 22));
            silverLabel.setText("Last Silver Rank");
            silverLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            goldLabel = new JLabel();
            goldLabel.setBounds(new Rectangle(53, 60, 125, 22));
            goldLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            goldLabel.setText("Last Gold Rank");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(goldLabel, null);
            centerPane.add(silverLabel, null);
            centerPane.add(bronzeLabel, null);
            centerPane.add(certifierLabel, null);
            centerPane.add(getGoldRankTextField(), null);
            centerPane.add(getSilverRankTextField(), null);
            centerPane.add(getBronzeRankTextField(), null);
            centerPane.add(getCommentTextField(), null);
            centerPane.add(certificationCommentLabel, null);
        }
        return centerPane;
    }

    /**
     * This method initializes goldRankTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGoldRankTextField() {
        if (goldRankTextField == null) {
            goldRankTextField = new JTextField();
            goldRankTextField.setBounds(new Rectangle(196, 61, 40, 20));
            goldRankTextField.setDocument(new IntegerDocument());
        }
        return goldRankTextField;
    }

    /**
     * This method initializes silverRankTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSilverRankTextField() {
        if (silverRankTextField == null) {
            silverRankTextField = new JTextField();
            silverRankTextField.setBounds(new Rectangle(196, 98, 40, 20));
            silverRankTextField.setDocument(new IntegerDocument());
        }
        return silverRankTextField;
    }

    /**
     * This method initializes bronzeRankTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getBronzeRankTextField() {
        if (bronzeRankTextField == null) {
            bronzeRankTextField = new JTextField();
            bronzeRankTextField.setBounds(new Rectangle(196, 135, 40, 20));
            goldRankTextField.setDocument(new IntegerDocument());
        }
        return bronzeRankTextField;
    }

    /**
     * This method initializes commentTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCommentTextField() {
        if (commentTextField == null) {
            commentTextField = new JTextField();
            commentTextField.setBounds(new Rectangle(196, 172, 207, 20));
        }
        return commentTextField;
    }

    /**
     * Contest Information Listener for Judgement Notifications.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    public class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            // not used
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            // not used
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // not used
        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            // not used
        }

        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadFrame();
                }
            });
        }

    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Report");
            reportButton.setMnemonic(KeyEvent.VK_R);
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Utilities.viewReport(new FinalizeReport(), "Finalize Report", getContest(), getController());
                }
            });
        }
        return reportButton;
    }
    
    void enableButtons(){
        
        boolean certified = false;
//        
//        FinalizeData finalizeData = getContest().getFinalizeData();
//        
//        if (finalizeData != null){
//            certified = finalizeData.isCertified();
//        }
        
        getUpdateButton().setEnabled(! certified);
        getFinalizeButton().setEnabled(! certified);
        
    }

} // @jve:decl-index=0:visual-constraint="10,10"
