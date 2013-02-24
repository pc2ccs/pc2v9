package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import javax.swing.JCheckBox;

/**
 * Add/Edit ContestTime Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditContestTimePane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -1060536964672397704L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    private ContestTime contestTime = null;

    private boolean populatingGUI = true;

    private JPanel centerPane = null;

    private JLabel remaingingTimeLabel = null;

    private JTextField remainingTimeTextBox = null;

    private JLabel elapsedTimeLabel = null;

    private JTextField elapsedTimeTextBox = null;

    private JLabel contestLengthLabel = null;

    private JTextField contestLengthTextBox = null;

    private JCheckBox stopAtEndofContestCheckBox = null;

    /**
     * This method initializes
     * 
     */
    public EditContestTimePane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(418, 243));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        addWindowCloserListener();
    }

    private void addWindowCloserListener() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCancelButton();
                        }
                    });
                } 
            }
        });
    }

    public String getPluginTitle() {
        return "Edit ContestTime Pane";
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
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
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * Enable or disable Update button based on comparison of run to fields.
     * 
     */
    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;

        if (contestTime != null) {

            try {
                ContestTime changedContestTime = getContestTimeFromFields(null);

                enableButton |= contestTime.isHaltContestAtTimeZero() == changedContestTime.isHaltContestAtTimeZero();
                enableButton |= contestTime.getElapsedTimeStr().equals(changedContestTime.getElapsedTimeStr());
                enableButton |= contestTime.getContestLengthStr().equals(changedContestTime.getContestLengthStr());
                enableButton |= contestTime.getRemainingTimeStr().equals(changedContestTime.getRemainingTimeStr());

            } catch (InvalidFieldValue e) {
                // invalid field, but that is ok as they are entering data
                // will be caught and reported when they hit update or add.
                StaticLog.getLog().log(Log.DEBUG, "Input ContestTime (but not saving) ", e);
                enableButton = true;
            }
        }

        enableUpdateButtons(enableButton);
    }

    /**
     * Create a ContestTime from the fields.
     * 
     * This also populates newContestTimeDataFiles for the data files.
     * 
     * @param checkContestTime
     *            will update this ContestTime if supplied, if null creates a new ContestTime
     * @return ContestTime based on fields
     * @throws InvalidFieldValue
     */
    public ContestTime getContestTimeFromFields(ContestTime checkContestTime) {

        long secs = stringToLongSecs(getElapsedTimeTextBox().getText());
        if (secs == -1) {
            throw new InvalidFieldValue("Invalid elapsed time");
        }

        long elapsedTime = secs;

        secs = stringToLongSecs(getRemainingTimeTextBox().getText());
        if (secs == -1) {
            throw new InvalidFieldValue("Invalid remaining time");
        }

        long remainingTime = secs;

        secs = stringToLongSecs(getContestLengthTextBox().getText());
        if (secs == -1) {
            throw new InvalidFieldValue("Invalid contest length");
        }

        long contestLength = secs;

        long actualRemaining = contestLength - elapsedTime;

        if (actualRemaining != remainingTime) {
            throw new InvalidFieldValue("Invalid contest times, set remaining to " + ContestTime.formatTime(actualRemaining));
        }

        if (checkContestTime == null) {
            checkContestTime = new ContestTime(0);
        }
        checkContestTime.setContestLengthSecs(contestLength);
        checkContestTime.setRemainingSecs(remainingTime);
        // elapsed is calculate in setRemainingSecs

        return checkContestTime;
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
            updateButton.setEnabled(false);
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateContestTime();
                }
            });
        }
        return updateButton;
    }

    protected void updateContestTime() {

        if (!validateContestTimeFields()) {
            // new contestTime is invalid, just return, message issued by validateContestTimeFields
            return;
        }

        ContestTime newContestTime = null;

        try {
            newContestTime = getContestTimeFromFields(contestTime);
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        getController().updateContestTime(newContestTime);

        cancelButton.setText("Close");
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * Validate that all contestTime fields are ok.
     * 
     * @return
     */
    private boolean validateContestTimeFields() {

        long secs = stringToLongSecs(getElapsedTimeTextBox().getText());
        if (secs == -1) {
            showMessage("Invalid elapsed time");
            return false;
        }

        secs = stringToLongSecs(getRemainingTimeTextBox().getText());
        if (secs == -1) {
            showMessage("Invalid remaining time");
            return false;
        }

        secs = stringToLongSecs(getContestLengthTextBox().getText());
        if (secs == -1) {
            showMessage("Invalid contest length");
            return false;
        }

        return true;
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    protected void handleCancelButton() {

        if (getUpdateButton().isEnabled()) {
            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "ContestTime modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                updateContestTime();

                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            } else if (result == JOptionPane.NO_OPTION) {
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }
        } else {
            // Close
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    public ContestTime getContestTime() {
        return contestTime;
    }

    public void setContestTime(final ContestTime contestTime) {

        this.contestTime = contestTime;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(contestTime);
                enableUpdateButtons(false);
                showMessage("");
            }
        });
    }

    private void populateGUI(ContestTime inContestTime) {

        populatingGUI = true;

        getRemainingTimeTextBox().setText(inContestTime.getRemainingTimeStr());
        getElapsedTimeTextBox().setText(inContestTime.getElapsedTimeStr());
        getContestLengthTextBox().setText(inContestTime.getContestLengthStr());

        getUpdateButton().setVisible(true);
        enableUpdateButtons(false);

        populatingGUI = false;
    }

    protected void enableUpdateButtons(boolean fieldsChanged) {
        if (fieldsChanged) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        updateButton.setEnabled(fieldsChanged);
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            contestLengthLabel = new JLabel();
            contestLengthLabel.setBounds(new java.awt.Rectangle(60, 103, 110, 23));
            contestLengthLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            contestLengthLabel.setText("Contest Length");
            elapsedTimeLabel = new JLabel();
            elapsedTimeLabel.setBounds(new java.awt.Rectangle(60, 60, 110, 23));
            elapsedTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            elapsedTimeLabel.setText("Elapsed Time");
            remaingingTimeLabel = new JLabel();
            remaingingTimeLabel.setBounds(new java.awt.Rectangle(60, 17, 110, 23));
            remaingingTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            remaingingTimeLabel.setText("Remaining Time");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(remaingingTimeLabel, null);
            centerPane.add(getRemainingTimeTextBox(), null);
            centerPane.add(elapsedTimeLabel, null);
            centerPane.add(getElapsedTimeTextBox(), null);
            centerPane.add(contestLengthLabel, null);
            centerPane.add(getContestLengthTextBox(), null);
            centerPane.add(getStopAtEndofContestCheckBox(), null);
        }
        return centerPane;
    }

    /**
     * This method initializes remainingTimeTextBox
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getRemainingTimeTextBox() {
        if (remainingTimeTextBox == null) {
            remainingTimeTextBox = new JTextField();
            remainingTimeTextBox.setBounds(new java.awt.Rectangle(191, 14, 115, 29));
            remainingTimeTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return remainingTimeTextBox;
    }

    /**
     * This method initializes elapsedTimeTextBox
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getElapsedTimeTextBox() {
        if (elapsedTimeTextBox == null) {
            elapsedTimeTextBox = new JTextField();
            elapsedTimeTextBox.setBounds(new java.awt.Rectangle(191, 57, 115, 29));
            elapsedTimeTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return elapsedTimeTextBox;
    }

    /**
     * This method initializes contestLenthTextBox
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getContestLengthTextBox() {
        if (contestLengthTextBox == null) {
            contestLengthTextBox = new JTextField();
            contestLengthTextBox.setBounds(new java.awt.Rectangle(191, 100, 115, 29));
            contestLengthTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return contestLengthTextBox;
    }

    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getStopAtEndofContestCheckBox() {
        if (stopAtEndofContestCheckBox == null) {
            stopAtEndofContestCheckBox = new JCheckBox();
            stopAtEndofContestCheckBox.setBounds(new java.awt.Rectangle(194, 143, 202, 21));
            stopAtEndofContestCheckBox.setText("Stop Clock at end of contest");
            stopAtEndofContestCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return stopAtEndofContestCheckBox;
    }

    /**
     * Convert String to second. Expects input in form: ss or mm:ss or hh:mm:ss
     * 
     * @param s
     *            string to be converted to seconds
     * @return -1 if invalid time string, 0 or >0 if valid
     */
    public long stringToLongSecs(String s) {

        if (s == null || s.trim().length() == 0) {
            return -1;
        }

        String[] fields = s.split(":");
        long hh = 0;
        long mm = 0;
        long ss = 0;
        
        switch (fields.length ) {
            case 3:
                hh = stringToLong(fields[0]);
                mm = stringToLong(fields[1]);
                ss = stringToLong(fields[2]);
                break;
            case 2:
                mm = stringToLong(fields[0]);
                ss = stringToLong(fields[1]);
                break;
            case 1:
                ss = stringToLong(fields[0]);
                break;

            default:
                break;
        }

        // System.out.println(" values "+hh+":"+mm+":"+ss);

        long totsecs = 0;
        if (hh != -1) {
            totsecs = hh;
        }
        if (mm != -1) {
            totsecs = (totsecs * 60) + mm;
        }
        if (ss != -1) {
            totsecs = (totsecs * 60) + ss;
        }

        // System.out.println(" values "+hh+":"+mm+":"+ss+" secs="+totsecs);

        if (hh == -1 || mm == -1 || ss == -1) {
            return -1;
        }

        return totsecs;
    }

    /**
     * Parse and return positive long.
     * 
     * @param s1
     * @return -1 if non-long string, else long value
     */
    private long stringToLong(String s1) {
        if (s1 == null) {
            return -1;
        }
        try {
            return Long.parseLong(s1);
        } catch (Exception e) {
            return -1;
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
