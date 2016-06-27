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

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.util.GregorianCalendar;

/**
 * Add/Edit ContestTime Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditContestTimePane extends JPanePlugin {

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
    private JLabel scheduledStartTimeLabel;
    private JTextField scheduledStartTimeTextBox;
    private JCheckBox chckbxStartContestAutomatically;

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
        this.setSize(new Dimension(479, 291));

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

    public void setContestTime(final ContestTime contestTime, final GregorianCalendar scheduledStartTime) {

        this.contestTime = contestTime;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(contestTime, scheduledStartTime);
                enableUpdateButtons(false);
                showMessage("");
            }
        });
    }

    private void populateGUI(ContestTime inContestTime, GregorianCalendar scheduledStartTime) {

        populatingGUI = true;

        getRemainingTimeTextBox().setText(inContestTime.getRemainingTimeStr());
        getElapsedTimeTextBox().setText(inContestTime.getElapsedTimeStr());
        getContestLengthTextBox().setText(inContestTime.getContestLengthStr());
        
        String displayStartTime = "";
        if (scheduledStartTime == null) {
            displayStartTime += "<undefined>";
        } else {
            displayStartTime += getDisplayableDateTime(scheduledStartTime);
        }
        getScheduledStartTimeTextBox().setText(displayStartTime);

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
            centerPane = new JPanel();
            GridBagLayout gbl_centerPane = new GridBagLayout();
            gbl_centerPane.columnWidths = new int[] {30, 110, 205, 20};
            gbl_centerPane.rowHeights = new int[] {29, 29, 29, 29, 20, 0, 0, 0};
            gbl_centerPane.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
            gbl_centerPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
            centerPane.setLayout(gbl_centerPane);
            GridBagConstraints gbc_scheduledStartTimeLabel = new GridBagConstraints();
            gbc_scheduledStartTimeLabel.anchor = GridBagConstraints.EAST;
            gbc_scheduledStartTimeLabel.insets = new Insets(0, 0, 5, 10);
            gbc_scheduledStartTimeLabel.gridx = 1;
            gbc_scheduledStartTimeLabel.gridy = 1;
            centerPane.add(getScheduledStartTimeLabel(), gbc_scheduledStartTimeLabel);
            GridBagConstraints gbc_scheduledStartTimeTextBox = new GridBagConstraints();
            gbc_scheduledStartTimeTextBox.insets = new Insets(0, 0, 5, 0);
            gbc_scheduledStartTimeTextBox.fill = GridBagConstraints.HORIZONTAL;
            gbc_scheduledStartTimeTextBox.gridx = 2;
            gbc_scheduledStartTimeTextBox.gridy = 1;
            centerPane.add(getScheduledStartTimeTextBox(), gbc_scheduledStartTimeTextBox);
            contestLengthLabel = new JLabel();
            contestLengthLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            contestLengthLabel.setText("Contest Length");
            GridBagConstraints gbc_contestLengthLabel = new GridBagConstraints();
            gbc_contestLengthLabel.fill = GridBagConstraints.BOTH;
            gbc_contestLengthLabel.insets = new Insets(0, 0, 5, 10);
            gbc_contestLengthLabel.gridx = 1;
            gbc_contestLengthLabel.gridy = 2;
            centerPane.add(contestLengthLabel, gbc_contestLengthLabel);
            GridBagConstraints gbc_contestLengthTextBox = new GridBagConstraints();
            gbc_contestLengthTextBox.fill = GridBagConstraints.BOTH;
            gbc_contestLengthTextBox.insets = new Insets(0, 0, 5, 0);
            gbc_contestLengthTextBox.gridx = 2;
            gbc_contestLengthTextBox.gridy = 2;
            centerPane.add(getContestLengthTextBox(), gbc_contestLengthTextBox);
            elapsedTimeLabel = new JLabel();
            elapsedTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            elapsedTimeLabel.setText("Current Elapsed Time");
            GridBagConstraints gbc_elapsedTimeLabel = new GridBagConstraints();
            gbc_elapsedTimeLabel.fill = GridBagConstraints.BOTH;
            gbc_elapsedTimeLabel.insets = new Insets(0, 0, 5, 10);
            gbc_elapsedTimeLabel.gridx = 1;
            gbc_elapsedTimeLabel.gridy = 3;
            centerPane.add(elapsedTimeLabel, gbc_elapsedTimeLabel);
            GridBagConstraints gbc_elapsedTimeTextBox = new GridBagConstraints();
            gbc_elapsedTimeTextBox.fill = GridBagConstraints.BOTH;
            gbc_elapsedTimeTextBox.insets = new Insets(0, 0, 5, 0);
            gbc_elapsedTimeTextBox.gridx = 2;
            gbc_elapsedTimeTextBox.gridy = 3;
            centerPane.add(getElapsedTimeTextBox(), gbc_elapsedTimeTextBox);
            remaingingTimeLabel = new JLabel();
            remaingingTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            remaingingTimeLabel.setText("Current Remaining Time");
            GridBagConstraints gbc_remaingingTimeLabel = new GridBagConstraints();
            gbc_remaingingTimeLabel.fill = GridBagConstraints.BOTH;
            gbc_remaingingTimeLabel.insets = new Insets(0, 0, 5, 10);
            gbc_remaingingTimeLabel.gridx = 1;
            gbc_remaingingTimeLabel.gridy = 4;
            centerPane.add(remaingingTimeLabel, gbc_remaingingTimeLabel);
            GridBagConstraints gbc_remainingTimeTextBox = new GridBagConstraints();
            gbc_remainingTimeTextBox.fill = GridBagConstraints.BOTH;
            gbc_remainingTimeTextBox.insets = new Insets(0, 0, 5, 0);
            gbc_remainingTimeTextBox.gridx = 2;
            gbc_remainingTimeTextBox.gridy = 4;
            centerPane.add(getRemainingTimeTextBox(), gbc_remainingTimeTextBox);
            GridBagConstraints gbc_chckbxStartContestAutomatically = new GridBagConstraints();
            gbc_chckbxStartContestAutomatically.insets = new Insets(0, 0, 0, 5);
            gbc_chckbxStartContestAutomatically.gridx = 1;
            gbc_chckbxStartContestAutomatically.gridy = 6;
            centerPane.add(getChckbxStartContestAutomatically(), gbc_chckbxStartContestAutomatically);
            GridBagConstraints gbc_stopAtEndofContestCheckBox = new GridBagConstraints();
            gbc_stopAtEndofContestCheckBox.fill = GridBagConstraints.VERTICAL;
            gbc_stopAtEndofContestCheckBox.gridx = 2;
            gbc_stopAtEndofContestCheckBox.gridy = 6;
            centerPane.add(getStopAtEndofContestCheckBox(), gbc_stopAtEndofContestCheckBox);
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
            elapsedTimeTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return elapsedTimeTextBox;
    }

    /**
     * This method initializes contestLengthTextBox
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getContestLengthTextBox() {
        if (contestLengthTextBox == null) {
            contestLengthTextBox = new JTextField();
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
            stopAtEndofContestCheckBox.setToolTipText("Check to cause PC2 to automatically stop the contest (cease accepting submissions) when the contest clock (remaining time) reaches zero");
            stopAtEndofContestCheckBox.setText("Stop contest automatically");
            stopAtEndofContestCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return stopAtEndofContestCheckBox;
    }
    
    /**
     * Convert a GregorianCalendar date/time to a displayable string in yyyy:mm:dd:hh:mm form.
     */
    private String getDisplayableDateTime(GregorianCalendar inDate) {
        
        String retString = "<undefined>";
        if (inDate != null) {
            //extract fields from inDate and build string
            //TODO:  need to deal with the difference between displaying LOCAL time and storing UTC
            int year = inDate.get(GregorianCalendar.YEAR);
            int month = inDate.get(GregorianCalendar.MONTH);
            int day = inDate.get(GregorianCalendar.DAY_OF_MONTH);
            int hour = inDate.get(GregorianCalendar.HOUR_OF_DAY);
            int min = inDate.get(GregorianCalendar.MINUTE);
            retString = "" + year + ":" + month + ":" + day + ":" + hour + ":" + min;
        }

        System.out.println ("EditContestTimePane.getDisplayableDateTime(): received GregorianCalendar value \n'"
                + inDate + "'; returning display representation \n'" + retString + "'");
        return retString;
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

    private JLabel getScheduledStartTimeLabel() {
        if (scheduledStartTimeLabel == null) {
        	scheduledStartTimeLabel = new JLabel("Scheduled Start Time");
        	scheduledStartTimeLabel.setToolTipText("The date/time when the contest is scheduled to start; must be a time in the future.");
        }
        return scheduledStartTimeLabel;
    }
    
    private JTextField getScheduledStartTimeTextBox() {
        if (scheduledStartTimeTextBox == null) {
        	scheduledStartTimeTextBox = new JTextField();
        	scheduledStartTimeTextBox.setToolTipText("Enter the future date/time when the contest is scheduled to start, in format yyyy:mm:dd:hh:mm (yyyy, mm, and dd default to \"today\" if omitted).");
        	scheduledStartTimeTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return scheduledStartTimeTextBox;
    }
    
    private JCheckBox getChckbxStartContestAutomatically() {
        if (chckbxStartContestAutomatically == null) {
        	chckbxStartContestAutomatically = new JCheckBox("Start contest automatically");
        	chckbxStartContestAutomatically.setToolTipText("Check to cause PC2 to automatically start the contest when the specified \"Scheduled Start Time\" is reached");
        }
        return chckbxStartContestAutomatically;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
