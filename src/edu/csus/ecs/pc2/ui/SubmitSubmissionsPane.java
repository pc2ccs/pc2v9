// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

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
import edu.csus.ecs.pc2.list.SubmissionSample;
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
        cdptextField.setBounds(113, 54, 315, 27);
        centerPane.add(cdptextField);
        cdptextField.setColumns(10);

        messageLabel = new JLabel("message label");
        messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(10, 11, 418, 32);
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
            @Override
            public void actionPerformed(ActionEvent e) {
                submitSampleSubmissions();
            }
        });
        submitRunButton.setToolTipText("Edit log file ");
        bottomPane.add(submitRunButton);

    }

    /**
     * Submit submissions
     */
    protected void submitSampleSubmissions() {
        showMessage("");

        boolean submitall = true;

        if (submitall) {
            List<File> files = submitter.getAllCDPsubmissionFileNames(getContest(), cdptextField.getText());

            boolean submitYesSamples = checkBoxSubmitYesSamples.isSelected();
            boolean submitNoSamples = checkBoxSubmitFailingSamples.isSelected();

            if (! submitYesSamples || ! submitNoSamples){
                files =  QuickSubmitter.filterRuns (files, submitYesSamples, submitNoSamples);
            }


            int count = 1;
            for (File file : files) {
                System.out.println("Found file " + count + " " + file.getAbsolutePath());
                count++;


            }

            int result = FrameUtilities.yesNoCancelDialog(this, "Submit " + files.size() + " sample submissions?", "Submit CDP submissions");

            if (result == JOptionPane.YES_OPTION) {

                List<SubmissionSample> subList = submitter.sendSubmissions(files);
                int numberSubmitted = 0;

                if(subList != null) {
                    numberSubmitted = subList.size();
                }

                if (numberSubmitted == files.size()) {
                    showMessage("Submitted all (" + files.size() + ") files/runs.");
                } else {
                    showMessage("Submitted " + numberSubmitted + " of " + files.size() + " files/runs.");
                }
            }

        } // else TODO provide way to send some of the runs, not just all.

    }

    @Override
    public String getPluginTitle() {
        return "Submitter Pane";
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
            @Override
            public void run() {
                messageLabel.setText(message);
                messageLabel.setToolTipText(message);
            }
        });
    }
} // @jve:decl-index=0:visual-constraint="10,10"
