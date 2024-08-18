// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.report.FinalizeReport;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

/**
 * Edit Finalize settings pane.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class FinalizePane extends JPanePlugin {

    /**
     *
     */
    private static final long serialVersionUID = 3089291613784484371L;

    private JPanel buttonPane = null;

    private JButton finalizeButton = null;

    private JButton updateButton = null;

    private JCheckBox useWFGroupRankingCheckBox = null;

    private JCheckBox customizeHonorsSolvedCountCheckBox = null;

    private JLabel customizeHonorsSolvedCountWhatsThisButton = null;

    private JPanel centerPane = null;

    private JLabel goldLabel = null;

    private JLabel silverLabel = null;

    private JLabel bronzeLabel = null;

    private JLabel certifierLabel = null;

    private JLabel highestHonorLabel = null;

    private JLabel highHonorLabel = null;

    private JLabel honorLabel = null;

    private JTextField goldCountTextField = null;

    private JTextField silverCountTextField = null;

    private JTextField bronzeCountTextField = null;

    private JTextField commentTextField = null;

    private JTextField highestHonorSolvedCountTextField = null;

    private JTextField highHonorSolvedCountTextField = null;

    private JTextField honorSolvedCountTextField = null;

    private JLabel certificationCommentLabel = null;

    private JButton reportButton = null;

    private JPanel southPanel;

    private JPanel viewResultsPane;

    private JLabel resultsFileLabel;

    private JButton viewButton;

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
        this.setSize(new Dimension(560, 239));
        this.add(getCenterPane(), BorderLayout.CENTER);
        add(getSouthPanel(), BorderLayout.SOUTH);
    }

    @Override
    public String getPluginTitle() {
        return "Finalized Pane";
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
            buttonPane.setPreferredSize(new Dimension(52, 35));
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
            @Override
            public void run() {
                reloadFrame();
            }
        });

        getContest().addContestInformationListener(new ContestInformationListenerImplementation());

    }

    protected FinalizeData getFromFields() {
        FinalizeData data = new FinalizeData();

        int ng = getIntegerValue(getGoldCountTextField());
        int ns = getIntegerValue(getSilverCountTextField());
        int nb = getIntegerValue(getBronzeCountTextField());
        data.setGoldRank(ng);
        data.setSilverRank(ng+ns);
        data.setBronzeRank(ng+ns+nb);
        data.setComment("" + getCommentTextField().getText());
        data.setUseWFGroupRanking(getUseWFGroupRankingsCheckBox().isSelected());
        if (getUseWFGroupRankingsCheckBox().isSelected() && getCustomizeHonorsSolvedCountCheckBox().isSelected()) {
            int highestHonorSolvedCount = getIntegerValue(getHighestHonorSolvedCountTextField());
            int highHonorSolvedCount = getIntegerValue(getHighHonorSolvedCountTextField());
            int honorSolvedCount = getIntegerValue(getHonorSolvedCountTextField());
            data.setCustomizeHonorsSolvedCount(getCustomizeHonorsSolvedCountCheckBox().isSelected());
            data.setHonorsSolvedCount(highestHonorSolvedCount, highHonorSolvedCount, honorSolvedCount);
        }
        return data;
    }

    private int getIntegerValue(JTextField textField) {
        String s = "0" + textField.getText();
        return Integer.parseInt(s);
    }

    private void populateDefaults() {
        getGoldCountTextField().setText("4");
        getSilverCountTextField().setText("4");
        getBronzeCountTextField().setText("4");
        getUseWFGroupRankingsCheckBox().setSelected(true);
        getCustomizeHonorsSolvedCountCheckBox().setSelected(false);
        getCustomizeHonorsSolvedCountWhatsThisButton().setEnabled(true);
        setEnableHonorsCountFields(false);
    }

    /**
     * This method Enables/Disables Honors solved count text fields
     * 
     * @param isEnabled 
     */
    private void setEnableHonorsCountFields(boolean isEnabled) {
        getHighestHonorSolvedCountTextField().setText("");
        getHighHonorSolvedCountTextField().setText("");
        getHonorSolvedCountTextField().setText("");
        getHighestHonorLabel().setEnabled(isEnabled);
        getHighHonorLabel().setEnabled(isEnabled);
        getHonorLabel().setEnabled(isEnabled);
        getHighestHonorSolvedCountTextField().setEnabled(isEnabled);
        getHighHonorSolvedCountTextField().setEnabled(isEnabled);
        getHonorSolvedCountTextField().setEnabled(isEnabled);
    }

    protected void reloadFrame() {

        FinalizeData data = getContest().getFinalizeData();
        if (data != null) {
            int gr = data.getGoldRank();
            int sr = data.getSilverRank();
            int br = data.getBronzeRank();

            getGoldCountTextField().setText(Integer.toString(gr));
            getSilverCountTextField().setText(Integer.toString(sr - gr));
            getBronzeCountTextField().setText(Integer.toString(br - sr));
            getCommentTextField().setText(data.getComment());
            getUseWFGroupRankingsCheckBox().setSelected(data.isUseWFGroupRanking());
            if (data.isUseWFGroupRanking()) {
                getCustomizeHonorsSolvedCountCheckBox().setSelected(data.isCustomizeHonorsSolvedCount());
                if (data.isCustomizeHonorsSolvedCount()) {
                    int highestHonorSolvedCount = data.getHighestHonorSolvedCount();
                    int highHonorSolvedCount = data.getHighHonorSolvedCount();
                    int honorSolvedCount = data.getHonorSolvedCount();
                    if (highestHonorSolvedCount != 0) {
                        getHighestHonorSolvedCountTextField().setText(Integer.toString(highestHonorSolvedCount));
                    }
                    if (highHonorSolvedCount != 0) {
                        getHighHonorSolvedCountTextField().setText(Integer.toString(highHonorSolvedCount));
                    }
                    if (honorSolvedCount != 0) {
                        getHonorSolvedCountTextField().setText(Integer.toString(honorSolvedCount));
                    }
                }
            }

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
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    certifyContest();
                    enableResultsLabel();
                }
            });
        }
        return finalizeButton;
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
            updateButton.setToolTipText("Update medal counts");
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateMedalCounts();
                }
            });
        }
        return updateButton;
    }

    protected void updateMedalCounts() {

        FinalizeData data = getFromFields();
        FinalizeData currentData = getContest().getFinalizeData();

        try {
            if(currentData != null && currentData.isCertified()) {
                throw new InvalidFieldValue("You can not change the medal counts on a certfied contest");
            }
            int ng = data.getGoldRank();
            int ns = data.getSilverRank() - ng;
            int nb = data.getBronzeRank() - (ns+ng);

            if (ng <= 0) {
                throw new InvalidFieldValue("The Number of Gold medals must be greater than zero");
            }
            if (ns <= 0) {
                throw new InvalidFieldValue("The number of Silver medals must be greater than zero");
            }
            if (nb <= 0) {
                throw new InvalidFieldValue("The number of Bronze medals must be greater than zero");
            }
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        data.setCertified(false);
        data.setUseWFGroupRanking(getUseWFGroupRankingsCheckBox().isSelected());
        getController().updateFinalizeData(data);
    }

    protected void certifyContest() {

        FinalizeData data = getFromFields();

        try {

            int numberUnjudgedRuns = getNumberUnjudgedRuns();
            if (numberUnjudgedRuns > 0) {
                throw new InvalidFieldValue("Cannot finalize all runs must be judged, " + numberUnjudgedRuns + " unjudged runs");
            }

            int numberUnasweredClars = getNumberUnansweredClars();
            if (numberUnasweredClars > 0) {
                throw new InvalidFieldValue("Cannot finalize all clars must be answered, " + numberUnasweredClars + " un-answered clarifications");
            }

            int numberJudgingErrorRuns = getNumberJERuns(getContest());
            if (numberJudgingErrorRuns > 0) {
                throw new InvalidFieldValue("Cannot finalize there are runs with Judging Errors (JEs), " + numberJudgingErrorRuns + " un-answered clarifications");
            }
            ContestTime contestTime = getContest().getContestTime();

            if (contestTime.isContestRunning()) {
                throw new InvalidFieldValue("Cannot finalize contest, contest clock not stopped");
            }

            if (contestTime.getRemainingSecs() > 0) {
                throw new InvalidFieldValue("Cannot finalize contest - contest not over - remaining time: " + contestTime.getRemainingTimeStr());
            }

            if (data.getBronzeRank() == 0) {
                throw new InvalidFieldValue("Cannot finalize contest - Bronze rank must be greater than zero");
            }

            if (data.getComment().trim().length() < 1) {
                throw new InvalidFieldValue("Cannot finalize contest - missing comment, enter a comment");
            }

        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        int result = FrameUtilities.yesNoCancelDialog(this, "Are you sure you want to finalize?", "Sure, really realy sure?");
        if (result == JOptionPane.YES_OPTION) {
            data.setCertified(true);
            getController().updateFinalizeData(data);
        }

    }

    /**
     * Get number of JE runs.
     *
     * @param contest
     * @return
     */
    public static int getNumberJERuns(IInternalContest contest) {
        Run[] runs = contest.getRuns();

        Filter filter = new Filter();
        filter.addRunState(RunStates.JUDGED);

        runs = filter.getRuns(runs);

        Judgement judgementJE = null;
        Judgement[] judgeList = contest.getJudgements();
        for (Judgement judgement : judgeList) {
            if (judgement.getAcronym() != null) {
                if (judgement.getAcronym().equalsIgnoreCase("JE")) {
                    judgementJE = judgement;
                }
            }
        }

        if (judgementJE == null) {
            /**
             * No JE judgement, there is no way to have any runs judged as JE.
             */

            return 0; // ------------------------ RETURN -------------
        }

        int count = 0;

        for (Run run : runs) {
            if (!run.isDeleted()) {

                ElementId id = run.getJudgementRecord().getJudgementId();
                if (judgementJE.getElementId().equals(id)) {
                    count++;
                }
            }
        }
        return count;
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
     * This method initializes centerPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            certificationCommentLabel = new JLabel();
            certificationCommentLabel.setBounds(new Rectangle(83, 19, 349, 26));
            certificationCommentLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            certificationCommentLabel.setHorizontalAlignment(SwingConstants.CENTER);
            certificationCommentLabel.setText("Contest Not Certified");
            certifierLabel = new JLabel();
            certifierLabel.setBounds(new Rectangle(64, 168, 170, 22));
            certifierLabel.setText("Who certifies");
            certifierLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            bronzeLabel = new JLabel();
            bronzeLabel.setBounds(new Rectangle(64, 131, 170, 22));
            bronzeLabel.setText("Number of Bronze Medals");
            bronzeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            silverLabel = new JLabel();
            silverLabel.setBounds(new Rectangle(64, 94, 170, 22));
            silverLabel.setText("Number of Silver Medals");
            silverLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            goldLabel = new JLabel();
            goldLabel.setBounds(new Rectangle(64, 57, 170, 22));
            goldLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            goldLabel.setText("Number of Gold Medals");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(goldLabel, null);
            centerPane.add(silverLabel, null);
            centerPane.add(bronzeLabel, null);
            centerPane.add(certifierLabel, null);
            centerPane.add(getHighestHonorLabel(), null);
            centerPane.add(getHighHonorLabel(), null);
            centerPane.add(getHonorLabel(), null);
            centerPane.add(getGoldCountTextField(), null);
            centerPane.add(getSilverCountTextField(), null);
            centerPane.add(getBronzeCountTextField(), null);
            centerPane.add(getCommentTextField(), null);
            centerPane.add(getHighestHonorSolvedCountTextField(), null);
            centerPane.add(getHighHonorSolvedCountTextField(), null);
            centerPane.add(getHonorSolvedCountTextField(), null);
            centerPane.add(certificationCommentLabel, null);
            centerPane.add(getUseWFGroupRankingsCheckBox(), null);
            centerPane.add(getCustomizeHonorsSolvedCountCheckBox(), null);
            centerPane.add(getCustomizeHonorsSolvedCountWhatsThisButton(), null);
        }
        return centerPane;
    }

    /**
     * This method initializes highestHonorLabel
     *
     * @return javax.swing.JLabel
     */
    private JLabel getHighestHonorLabel() {
        if (highestHonorLabel == null) {
            highestHonorLabel = new JLabel();
            highestHonorLabel.setBounds(new Rectangle(4, 279, 230, 22));
            highestHonorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            highestHonorLabel.setText("Highest Honors min. problems solved");
        }
        return highestHonorLabel;
    }

    /**
     * This method initializes highHonorLabel
     *
     * @return javax.swing.JLabel
     */
    private JLabel getHighHonorLabel() {
        if (highHonorLabel == null) {
            highHonorLabel = new JLabel();
            highHonorLabel.setBounds(new Rectangle(4, 316, 230, 22));
            highHonorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            highHonorLabel.setText("High Honors min. problems solved");
        }
        return highHonorLabel;
    }

    /**
     * This method initializes honorLabel
     *
     * @return javax.swing.JLabel
     */
    private JLabel getHonorLabel() {
        if (honorLabel == null) {
            honorLabel = new JLabel();
            honorLabel.setBounds(new Rectangle(4, 353, 230, 22));
            honorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            honorLabel.setText("Honors min. problems solved");
        }
        return honorLabel;
    }

    /**
     * This method initializes goldCountTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getGoldCountTextField() {
        if (goldCountTextField == null) {
            goldCountTextField = new JTextField();
            goldCountTextField.setBounds(new Rectangle(250, 57, 40, 20));
            goldCountTextField.setDocument(new IntegerDocument());
        }
        return goldCountTextField;
    }

    /**
     * This method initializes silverCountTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getSilverCountTextField() {
        if (silverCountTextField == null) {
            silverCountTextField = new JTextField();
            silverCountTextField.setBounds(new Rectangle(250, 94, 40, 20));
            silverCountTextField.setDocument(new IntegerDocument());
        }
        return silverCountTextField;
    }

    /**
     * This method initializes bronzeCountTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getBronzeCountTextField() {
        if (bronzeCountTextField == null) {
            bronzeCountTextField = new JTextField();
            bronzeCountTextField.setBounds(new Rectangle(250, 131, 40, 20));
            goldCountTextField.setDocument(new IntegerDocument());
        }
        return bronzeCountTextField;
    }

    /**
     * This method initializes commentTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getCommentTextField() {
        if (commentTextField == null) {
            commentTextField = new JTextField();
            commentTextField.setBounds(new Rectangle(250, 168, 207, 20));
        }
        return commentTextField;
    }

    /**
     * This method initializes highestHonorSolvedCountTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getHighestHonorSolvedCountTextField() {
        if (highestHonorSolvedCountTextField == null) {
            highestHonorSolvedCountTextField = new JTextField();
            highestHonorSolvedCountTextField.setBounds(new Rectangle(250, 279, 40, 20));
        }
        return highestHonorSolvedCountTextField;
    }

    /**
     * This method initializes highHonorSolvedCountTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getHighHonorSolvedCountTextField() {
        if (highHonorSolvedCountTextField == null) {
            highHonorSolvedCountTextField = new JTextField();
            highHonorSolvedCountTextField.setBounds(new Rectangle(250, 316, 40, 20));
        }
        return highHonorSolvedCountTextField;
    }

    /**
     * This method initializes honorSolvedCountTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getHonorSolvedCountTextField() {
        if (honorSolvedCountTextField == null) {
            honorSolvedCountTextField = new JTextField();
            honorSolvedCountTextField.setBounds(new Rectangle(250, 353, 40, 20));
        }
        return honorSolvedCountTextField;
    }

    /**
     * This method initializes useWFGroupRankingsCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getUseWFGroupRankingsCheckBox() {
        if(useWFGroupRankingCheckBox == null) {
            useWFGroupRankingCheckBox = new JCheckBox();
            useWFGroupRankingCheckBox.setText("Use World Finals group rankings for results");
            useWFGroupRankingCheckBox.setBounds(new Rectangle(250, 205, 300, 20));
            useWFGroupRankingCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    getCustomizeHonorsSolvedCountCheckBox().setSelected(false);
                    getCustomizeHonorsSolvedCountCheckBox().setEnabled(e.getStateChange() == ItemEvent.SELECTED);
                    getCustomizeHonorsSolvedCountWhatsThisButton().setEnabled(e.getStateChange() == ItemEvent.SELECTED);
                }
            });
        }
        return useWFGroupRankingCheckBox;
    }

    /**
     * This method initializes customizeHonorsSolvedCountCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCustomizeHonorsSolvedCountCheckBox() {
        if(customizeHonorsSolvedCountCheckBox == null) {
            customizeHonorsSolvedCountCheckBox = new JCheckBox();
            customizeHonorsSolvedCountCheckBox.setText("Customize Number of Problems solved for Honors rankings");
            customizeHonorsSolvedCountCheckBox.setBounds(new Rectangle(250, 242, 365, 20));
            customizeHonorsSolvedCountCheckBox.setSelected(false);
            customizeHonorsSolvedCountCheckBox.setEnabled(getUseWFGroupRankingsCheckBox().isSelected());
            customizeHonorsSolvedCountCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    setEnableHonorsCountFields(e.getStateChange() == ItemEvent.SELECTED);
                }
            });
        }
        return customizeHonorsSolvedCountCheckBox;
    }

    /**
     * This method initializes customizeHonorsSolvedCountWhatsThisButton
     *
     * @return javax.swing.JLabel
     */
    private JLabel getCustomizeHonorsSolvedCountWhatsThisButton() {

        if (customizeHonorsSolvedCountWhatsThisButton == null) {
            Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
            if (questionIcon == null || !(questionIcon instanceof ImageIcon)) {
                // the current PLAF doesn't have an OptionPane.questionIcon that's an ImageIcon
                customizeHonorsSolvedCountWhatsThisButton = new JLabel("<What's This?>");
                customizeHonorsSolvedCountWhatsThisButton.setForeground(Color.blue);
            } else {
                Image image = ((ImageIcon) questionIcon).getImage();
                customizeHonorsSolvedCountWhatsThisButton = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
            }

            customizeHonorsSolvedCountWhatsThisButton.setToolTipText("What's This? (click for additional information)");
            customizeHonorsSolvedCountWhatsThisButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, customizeHonorsSolvedCountWhatsThisMessage, "About Customizing different Honors Solved Count", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
            customizeHonorsSolvedCountWhatsThisButton.setBounds(new Rectangle(620, 242, 20, 20));
        }
        return customizeHonorsSolvedCountWhatsThisButton;
    }

    private String customizeHonorsSolvedCountWhatsThisMessage = //
            "\nBy default:" //
            + "\nMinimum problems solved to get Highest Honors = Num. problems solved by last ranked Bronze medalist" //
            + "\nMinimum problems solved to get High Honors = Num. problems solved by last ranked Bronze medalist - 1" //
            + "\nMinimum problems solved to get Honors = Num. problems solved by median ranked team (In case of even count of teams, avg. of both medians)" //
            + "\n\nHere we can customize the counts." //
            + "\nIf field is left blank or \"0\", then the default value is considerd." //
            + "\nIn case a team satisfies multiple Honor list problem solved counts, then always highest kind trumps" //
            + "\ni.e. Highest Honor trumps High Honor trumps Honor." //
            + "\nThis may arise in certain extreme cases of default or erroneous field entries." //
            + "\nFor eg:-" //
            + "\nOut of 100 teams, 99 solved solved 7 problems and 1 solved 6 problems." //
            + "\nThen both Highest Honors and Honors by default will be 7 problems and High Honors is 6 problems solved." //
            + "\nAll teams (except medalists) solving 7 problems will get Highest Honors and the team solving 6 problems get High Honors" //
            + " \n\n";
    
    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    /**
     * Contest Information Listener for Judgement Notifications.
     *
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    public class ContestInformationListenerImplementation implements IContestInformationListener {

        @Override
        public void contestInformationAdded(ContestInformationEvent event) {
            // not used
        }

        @Override
        public void contestInformationChanged(ContestInformationEvent event) {
            // not used
        }

        @Override
        public void contestInformationRemoved(ContestInformationEvent event) {
            // not used
        }

        @Override
        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            // not used
        }

        @Override
        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
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
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Utilities.viewReport(new FinalizeReport(), "Finalize Report", getContest(), getController());
                }
            });
        }
        return reportButton;
    }

    void enableButtons() {

        boolean finalized = false;
        if (getContest().getFinalizeData() != null) {
            finalized = getContest().getFinalizeData().isCertified();
        }
        getFinalizeButton().setEnabled(!finalized);

        // Always allow us to generate a results.tsv, even if not finalized.
        getViewButton().setEnabled(true);

    }

    /**
     * Generate results.tsv and show the generated results file name and make it visible.
     */
    private void enableResultsLabel() {

        String text;
        boolean finalized = false;
        if (getContest().getFinalizeData() != null) {
            finalized = getContest().getFinalizeData().isCertified();
        }
        if(finalized) {
            text = "Final";
        } else {
            text = "Unofficial";
        }
        text = text + " Results file generated to " + genererateResultsFile();
        getResultsFileLabel().setText(text);
        getResultsFileLabel().setToolTipText(text);

        getResultsFileLabel().setVisible(true);
    }

    private String genererateResultsFile() {

        String resultsDir = Utilities.getCurrentDirectory() + File.separator + "results";

        File dir = new File(resultsDir);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        String outfilename = resultsDir + File.separator + ResultsFile.RESULTS_FILENAME;

        ResultsFile resultsFile = new ResultsFile();
        try {
            String[] lines = resultsFile.createTSVFileLines(getContest());
            Utilities.writeLinesToFile(outfilename, lines);
            return outfilename;
        } catch (Exception e) {
            getLog().info("Unable to write results file " + outfilename);
            getLog().log(Level.WARNING, "Writing " + outfilename, e);
            return "Unable to write " + outfilename;
        }

    }

    private JPanel getSouthPanel() {
        if (southPanel == null) {
            southPanel = new JPanel();
            southPanel.setLayout(new BorderLayout(0, 0));
            southPanel.add(getButtonPane(), BorderLayout.SOUTH);
            southPanel.add(getViewResultsPane());
        }
        return southPanel;
    }

    private JPanel getViewResultsPane() {
        if (viewResultsPane == null) {
            viewResultsPane = new JPanel();
            viewResultsPane.setLayout(new BorderLayout(0, 0));
            viewResultsPane.add(getResultsFileLabel());
            viewResultsPane.add(getViewButton(), BorderLayout.EAST);
        }
        return viewResultsPane;
    }

    private JLabel getResultsFileLabel() {
        if (resultsFileLabel == null) {
            resultsFileLabel = new JLabel("results/results.tsv");
            resultsFileLabel.setHorizontalAlignment(SwingConstants.CENTER);
            resultsFileLabel.setVisible(false);
        }
        return resultsFileLabel;
    }

    private JButton getViewButton() {
        if (viewButton == null) {
            viewButton = new JButton("View");
            viewButton.setToolTipText("View results.tsv");
            viewButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableResultsLabel();
                    showResultsFile();
                }
            });
        }
        return viewButton;
    }

    protected void showResultsFile() {

        String resultsFile = genererateResultsFile();

        FrameUtilities.viewFile(resultsFile, "Results File", getLog());
    }

} // @jve:decl-index=0:visual-constraint="10,10"
