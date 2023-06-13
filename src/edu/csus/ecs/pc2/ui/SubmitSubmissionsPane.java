// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.team.QuickSubmitter;

/**
 * A UI that to submit files found in a CDP.
 * 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class SubmitSubmissionsPane extends JPanePlugin {

    private static final long serialVersionUID = -8862440024499524533L;

    private JTextField cdptextField;

    private JLabel messageLabel;
    
    private JCheckBox checkBoxSubmitYesSamples;

    private JCheckBox checkBoxSubmitFailingSamples;

    private QuickSubmitter submitter = new QuickSubmitter();

    public SubmitSubmissionsPane() {
        super();
        setLayout(new BorderLayout(0, 0));

        JPanel centerPane = new JPanel();
        add(centerPane, BorderLayout.CENTER);
        centerPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("CDP Path");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel.setBounds(10, 60, 86, 14);
        centerPane.add(lblNewLabel);

        cdptextField = new JTextField();
        cdptextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        cdptextField.setBounds(113, 54, 404, 27);
        centerPane.add(cdptextField);
        cdptextField.setColumns(10);

        messageLabel = new JLabel("message label");
        messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(10, 11, 738, 32);
        centerPane.add(messageLabel);
        
        
        checkBoxSubmitYesSamples = new JCheckBox("Submit Yes Samples");
        checkBoxSubmitYesSamples.setSelected(true);
        checkBoxSubmitYesSamples.setToolTipText("Only submit AC sample source");
        checkBoxSubmitYesSamples.setBounds(48, 101, 265, 23);
        centerPane.add(checkBoxSubmitYesSamples);
        
        checkBoxSubmitFailingSamples = new JCheckBox("Submit Failing (non-AC) Samples");
        checkBoxSubmitFailingSamples.setSelected(true);
        checkBoxSubmitFailingSamples.setToolTipText("Submt all non-AC (Yes) submissions");
        checkBoxSubmitFailingSamples.setBounds(48, 137, 216, 23);
        centerPane.add(checkBoxSubmitFailingSamples);

        JPanel bottomPane = new JPanel();
        FlowLayout flowLayout = (FlowLayout) bottomPane.getLayout();
        flowLayout.setHgap(125);
        add(bottomPane, BorderLayout.SOUTH);

        JButton submitRunButton = new JButton("Submit");
        submitRunButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    submitSampleSubmissions();
                } catch (Exception e2) {
                    showMessage("Cannot submit samples, "+e2.getMessage());
                }
            }
        });
        submitRunButton.setToolTipText("Edit log file ");
        bottomPane.add(submitRunButton);

    }

    /**
     * Submit submissions
     * @throws FileNotFoundException 
     */
    protected void submitSampleSubmissions() throws FileNotFoundException {
        showMessage("");
        
        if (!getContest().getContestTime().isContestRunning()) {
            FrameUtilities.showMessage(this, "Contest not started", "Cannot submit, contest not started");
            return;
        }

        boolean submitall = true;

        String cdpPath = cdptextField.getText();
        if (submitall) {

            boolean submitYesSamples = checkBoxSubmitYesSamples.isSelected();
            boolean submitNoSamples = checkBoxSubmitFailingSamples.isSelected();

            List<File> files = submitter.getAllCDPsubmissionFileNames(getContest(), cdpPath, submitYesSamples, submitNoSamples);
            if (files.size() == 0) {
                FrameUtilities.showMessage(this, "No samples to submit", "No samples found under: " + cdpPath);
                return;
            }

            int result = FrameUtilities.yesNoCancelDialog(this, "Submit " + files.size() + " sample submissions?", "Submit CDP submissions");

            if (result == JOptionPane.YES_OPTION) {
                List<File> submittedFiles = new ArrayList<File>();

                for (File subFile : files) {
                    try {
                        submitter.sendSubmission(subFile);
                        submittedFiles.add(subFile);
                        System.out.println("Submitted file: "+subFile.getAbsolutePath());
                    } catch (Exception e) {
                        getLog().log(Level.WARNING, "Could not submit sample "+subFile.getAbsolutePath()+" "+e.getMessage(), e);
                        System.out.println("Could not submit sample "+subFile.getAbsolutePath()+" "+e.getMessage());
                    }
                }

                if (submittedFiles.size() == files.size()) {
                    showMessage("Submitted all " + submittedFiles.size() + " runs.");
                } else {
                    int failureCount = files.size() - submittedFiles.size();
                    showMessage("Only submitted " + submittedFiles.size() + " of " + files.size() + " runs, check log for details of " + failureCount + " failures.");
                }
            }
        }
    }

    @Override
    public String getPluginTitle() {
        return "Submit Samples Pane";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        String cdpPath = inContest.getContestInformation().getJudgeCDPBasePath();
        if (cdpPath == null) {
            cdpPath = "";
        }

        cdptextField.setText(cdpPath);

        showMessage("");

        submitter.setContestAndController(inContest, inController);

    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
                messageLabel.setToolTipText(message);
            }
        });
    }
} // @jve:decl-index=0:visual-constraint="10,10"
