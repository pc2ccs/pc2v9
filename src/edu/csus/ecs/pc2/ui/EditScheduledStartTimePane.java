package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Add/Edit ContestTime Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditScheduledStartTimePane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    private boolean populatingGUI = true;

    private JPanel centerPane = null;

    private JLabel scheduledStartTimeLabel;

    private JTextField scheduledStartTimeTextBox;

    private ContestInformation contestInfo;

    private JPanel scheduledStartTimePanel;

    private JPanel startTimeButtonPanel;

    private JButton clearStartTimeButton;

    private JButton setStartToNowButton;

    private JComboBox<Integer> incrementTimeComboBox;

    private JButton incrementTimeButton;

    private JButton decrementTimeButton;

    private JLabel dropdownListLabel;

    private Component horizontalStrut;
    private Component verticalStrut;

    /**
     * This constructor creates an EditScheduledStartTimePane containing a message pane, a button pane with Update and Cancel buttons, and a center pane allowing editing of the Scheduled Start Time.
     * 
     */
    public EditScheduledStartTimePane() {
        super();
        setPreferredSize(new Dimension(650, 350));
        initialize();
    }

    /**
     * This method initializes the EditScheduledStartTimePane with a message pane, a button pane and a center pane allowing editing of the Scheduled Start Time.
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(530, 350));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
    }

    /**
     * It is the responsibility of classes using this EditScheduledStartTimePane to call this method and provide a valid {@link IInternalContest} (model) and {@link IInternalController} before using
     * the pane.
     */
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
        return "Edit ScheduledStartTime Pane";
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messagePane = new JPanel();
            messagePane.setMinimumSize(new Dimension(10, 30));
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new Dimension(25, 30));
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * This method initializes buttonPane with Update and Cancel buttons
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
     * Enable the Update button. This method should be invoked by any method which changes the state of any GUI component (such as editing the Scheduled Start Time textbox, or pressing buttons which
     * indirectly update the textbox).
     * 
     */
    private void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        setButtonStatesAndLabels(true);
    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setToolTipText("Apply the specified Scheduled Start Time and (if not \"undefined\") set the contest to automatically start at the specified time)");
            updateButton.setText("Update");
            updateButton.setEnabled(false);
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleUpdate();
                }
            });
        }
        return updateButton;
    }

    /**
     * This method is called when the Update button is pressed. It verifies that that contents of the Scheduled Start Time textbox represent a valid time (including "<undefined>" or null or the empty
     * string); if so it stores the specified Scheduled Start Time in the Contest model.
     * 
     */
    protected void handleUpdate() {

        // ignore attempts to update Scheduled Start Time if contest has already started
        if (getContest().getContestTime().isContestStarted()) {
            JOptionPane.showMessageDialog(getParentFrame(), "Contest has already started; it's too late to set a Scheduled Start time -- ignored",
                    "Contest Already Started", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // get ScheduledStartTime in the GUI textbox
        GregorianCalendar newStartTime = getScheduledStartTimeFromGUI();

        if (newStartTime == null) {
            // new scheduled start time is invalid (failed to parse correctly); just return
            // (error message issued by getScheduledStartTimeFromGUI())
            return;
        }

        // the GUI entry is valid -- it either represents "undefined" (time=0) or a valid start time

        // get the existing ContestInfo from the contest
        ContestInformation localContestInfo = getContest().getContestInformation();

        // insert the new start time into the contest info
        if (newStartTime.getTimeInMillis() == 0) {
            // the time indicates "undefined"
            localContestInfo.setScheduledStartTime(null);
            localContestInfo.setAutoStartContest(false);
        } else {
            // the time is a valid date; make sure it's in the future
            GregorianCalendar now = new GregorianCalendar();
            if (!newStartTime.after(now)) {
                showMessage("Scheduled start date/time must be in the future");
                return;
            } else {
                // time is valid and in the future
                localContestInfo.setScheduledStartTime(newStartTime);
                localContestInfo.setAutoStartContest(true);
            }
        }

        // put the updated ContestInfo back into the Controller
        getController().updateContestInformation(localContestInfo);

        cancelButton.setText("Close");
        updateButton.setEnabled(false);

        // we're done; hide the GUI
        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * Extracts the Scheduled Start Time from the GUI and returns it as a {@link GregorianCalendar} (Date). If the GUI indicates an undefined start time (e.g. "<undefined>", or the empty string), the
     * returned GregorianCalendar will have a time value of zero. If the data in the GUI is not "<undefined>" and also not the empty string, but fails to parse as a valid date, null is returned
     * 
     * @return the GregorianCalendar representing the Scheduled Start Time in the GUI, or null if the GUI string does not represent a valid Date
     */
    private GregorianCalendar getScheduledStartTimeFromGUI() {

        String guiString = getScheduledStartTimeTextBox().getText();

        // check for values that represent "no Scheduled Start Time"
        if (guiString == null || guiString.length() == 0 || guiString.trim().equals("") || guiString.equalsIgnoreCase("<undefined>")) {
            // return a date with a value of zero, indicating "undefined"
            GregorianCalendar retDate = new GregorianCalendar();
            retDate.setTimeInMillis(0);
            return retDate;

        } else {

            // the GUI doesn't contain something representing "undefined";
            // try parsing the GUI string into a GregorianCalendar
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // TODO: add support for yyyy/MM/dd being optional
            df.setLenient(false);
            Date date;
            try {
                date = df.parse(guiString);
            } catch (ParseException e1) {
                // the input text does not parse as a date matching the format (pattern)
                if (getController() != null) {
                    if (getController().getLog() != null) {
                        getController().getLog().warning("ParseException: invalid Scheduled Start Date: '" + guiString + "'; " + e1.getMessage());
                    } else {
                        // log was null
                        System.err.println("ParseException: invalid Scheduled Start Date; no log available");
                    }
                } else {
                    // controller was null
                    System.err.println("ParseException: invalid Scheduled Start Date; no controller (hence, no log) available");
                }
                showMessage("<html>Invalid Scheduled Start Time (must match yyyy-mm-dd hh:mm<br> or the string \"&lt;undefined&gt;\" or the empty string)");
                return null;
            }

            // date parses properly; put it into a Calendar object
            // TODO: should put the calendar in non-lenient mode and catch exceptions due to, e.g. month=13
            GregorianCalendar parsedDate = (GregorianCalendar) Calendar.getInstance();
            parsedDate.setTime(date);

            return parsedDate;
        }
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

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Scheduled Start Time has been modified;"
                    + "\n do you want to save the changes?\n", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                handleUpdate();

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

    public void setContestInfo(ContestInformation inContestInfo) {
        this.contestInfo = inContestInfo;
        if (contestInfo != null) {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    populateGUI(contestInfo);
                    setButtonStatesAndLabels(false); // false = "Update" button should NOT be enabled
                    showMessage("");
                }
            });

        } else {
            // we got passed a null ContestInfo; not good
            if (getController() != null) {
                Log log = getController().getLog();
                if (log != null) {
                    log.warning("EditScheduledStartTimePane: received NULL ContestInformation; cannot continue");
                } else {
                    System.err.println("EditScheduledStartTimePane: received NULL ContestInformation and cannot get Log from controller!");
                }
            } else {
                System.err.println("EditScheduledStartTimePane: received NULL ContestInformation and cannot get Controller (so, no Log)");
            }
        }
    }

    private void populateGUI(ContestInformation inContestInfo) {
        
        populatingGUI = true;

        // put the ScheduledStartTime into the GUI
        // Note that an unscheduled start time is represented in ContestInformation as <null>
        GregorianCalendar scheduledStartTime = inContestInfo.getScheduledStartTime();
        String displayStartTime = "";
        if (scheduledStartTime == null) {
            displayStartTime = "<undefined>";
        } else {
            displayStartTime = getGregorianTimeAsString(scheduledStartTime);
        }

        IInternalContest contest = getContest();
        ContestTime localContestTime = null;
        if (contest == null) {
            if (getController()!=null) {
                if (getController().getLog()!=null) {
                    getController().getLog().warning("EditScheduledStartTimePane: getContest() returned null !?");
                } else {
                    System.err.println("EditScheduledStartTimePane: getContest() returned null but no Log is available.");
                }
           } else {
               System.err.println("EditScheduledStartTimePane: getContest() returned null but no Controller is available (hence, no Log)");
           }
            
        } else {
            
            localContestTime = contest.getContestTime();
            if (localContestTime == null) {
                if (getController()!=null) {
                    if (getController().getLog()!=null) {
                        getController().getLog().warning("EditScheduledStartTimePane: getContestTime() returned null !?");
                    } else {
                        System.err.println("EditScheduledStartTimePane: getContestTime() returned null but no Log is available.");
                    }
               } else {
                   System.err.println("EditScheduledStartTimePane: getContestTime() returned null but no Controller is available (hence, no Log)");
               }
            }
        }
        
        if (localContestTime!=null) {
            
            if (localContestTime.isContestStarted()) {
                getScheduledStartTimeTextBox().setText(displayStartTime);
                getScheduledStartTimeTextBox().setToolTipText("Scheduled start time cannot be set when the contest has already started.");
                getScheduledStartTimeTextBox().setEditable(false);
            } else {
                getScheduledStartTimeTextBox().setText(displayStartTime);
                getScheduledStartTimeTextBox().setToolTipText(
                    "<html>\r\nEnter the future date/time when the contest is scheduled to start, in format yyyy-mm-dd hh:mm;"
                            + "\r\n<br>\r\nor enter \"&lt;undefined&gt;\" or an empty string to clear any scheduled start time."
                            + "\r\n<br>\r\nNote that hh:mm must be in \"24-hour\" time (e.g. 1pm = 13:00)\r\n</html>");
                getScheduledStartTimeTextBox().setEditable(true);
            }
        }

        getUpdateButton().setVisible(true);
        setButtonStatesAndLabels(false);

        populatingGUI = false;
    }

    protected void setButtonStatesAndLabels(boolean fieldsChanged) {
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
                messageLabel.setForeground(Color.RED);
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
            centerPane.setMinimumSize(new Dimension(10, 500));
            centerPane.setLayout(new BorderLayout(0, 0));
            centerPane.add(getScheduledStartTimePanel(), BorderLayout.NORTH);
            centerPane.add(getStartTimeButtonPanel(), BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * Convert a GregorianCalendar date/time to a displayable string in yyyy-mm-dd hh:mm form.
     * Note that any additional seconds/milliseconds are truncated in the returned string (although the
     * input calendar is not changed).
     */
    private String getGregorianTimeAsString(GregorianCalendar cal) {

        String retString = "<undefined>";
        if (cal != null) {
            // extract fields from input and build string
            // TODO: need to deal with the difference between displaying LOCAL time and storing UTC

            //make a copy and truncate everything below minutes
            GregorianCalendar calCopy = new GregorianCalendar();
            calCopy.setTimeInMillis(cal.getTimeInMillis());
            calCopy.set(Calendar.SECOND, 0);
            calCopy.set(Calendar.MILLISECOND, 0);
            
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            fmt.setCalendar(calCopy);
            retString = fmt.format(calCopy.getTime());
        }

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

        switch (fields.length) {
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
            scheduledStartTimeTextBox.setPreferredSize(new Dimension(200, 20));
            scheduledStartTimeTextBox.setMinimumSize(new Dimension(50, 20));
            String tooltip = "<html>\r\nEnter the future date/time when the contest is scheduled to start, in format yyyy-mm-dd hh:mm;\r\n<br>\r\nor enter \"&lt;undefined&gt;\" "
                    + "or an empty string to clear any scheduled start time.\r\n<br>\r\nNote that hh:mm must be in \"24-hour\" time (e.g. 1pm = 13:00)\r\n</html>";
            scheduledStartTimeTextBox
                    .setToolTipText(tooltip);
            scheduledStartTimeTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return scheduledStartTimeTextBox;
    }

    private JPanel getScheduledStartTimePanel() {
        if (scheduledStartTimePanel == null) {
            scheduledStartTimePanel = new JPanel();
            scheduledStartTimePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
            scheduledStartTimePanel.add(getVerticalStrut());
            scheduledStartTimePanel.add(getScheduledStartTimeLabel());
            scheduledStartTimePanel.add(getScheduledStartTimeTextBox());
        }
        return scheduledStartTimePanel;
    }

    private JPanel getStartTimeButtonPanel() {
        if (startTimeButtonPanel == null) {
            startTimeButtonPanel = new JPanel();
            startTimeButtonPanel.setBorder(new EmptyBorder(15, 10, 10, 10));
            startTimeButtonPanel.add(getClearStartTimeButton());
            startTimeButtonPanel.add(getSetStartToNowButton());
            startTimeButtonPanel.add(getHorizontalStrut());
            startTimeButtonPanel.add(getDropdownListLabel());
            startTimeButtonPanel.add(getIncrementTimeComboBox());
            startTimeButtonPanel.add(getIncrementTimeButton());
            startTimeButtonPanel.add(getDecrementTimeButton());
        }
        return startTimeButtonPanel;
    }

    private JButton getClearStartTimeButton() {
        if (clearStartTimeButton == null) {
            clearStartTimeButton = new JButton("Set to Undefined");
            clearStartTimeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setStartTimeToUndefined();
                }
            });
            clearStartTimeButton.setToolTipText("Resets the Scheduled Start Time to \"undefined\" (which in turn means the contest will not start automatically)");
        }
        return clearStartTimeButton;
    }

    /**
     * Sets the Scheduled Start Time GUI textbox to "<undefined>", and enables the "Update" button.
     */
    protected void setStartTimeToUndefined() {
        getScheduledStartTimeTextBox().setText("<undefined>");
        enableUpdateButton();
        showMessage("");
    }

    private JButton getSetStartToNowButton() {
        if (setStartToNowButton == null) {
            setStartToNowButton = new JButton("Set to Now");
            setStartToNowButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setStartTimeToNow();
                }
            });
            String tooltip = "Sets the Scheduled Start Time to the next whole minute which is at least 30 seconds from now (and schedules the contest to automatically start at that time)";
            setStartToNowButton.setToolTipText(tooltip);
        }
        return setStartToNowButton;
    }

    /**
     * Sets the Scheduled Start Time GUI textbox to the string representing the time Now/ NOTE: previously, this method also added 30 seconds, and rounded up to the next whole minute (to comply with
     * the CLICS specification that says the Start Time should not be able to be set to less than 30 seconds in the future; this specification refers to the Web /starttime interface but it seemed to
     * make sense to keep the GUI consistent with that spec). However, others violently disagreed so it was changed to set it to the actual time now, truncating downward to the current whole minute.
     */
    protected void setStartTimeToNow() {

        GregorianCalendar now = new GregorianCalendar();

        // //add 30 seconds to the current time
        // now.add(Calendar.SECOND, 30);
        //
        // //bump the minute up by one and clear lower fields to zero
        // now.roll(Calendar.MINUTE, true);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        getScheduledStartTimeTextBox().setText(getGregorianTimeAsString(now));

        enableUpdateButton();
        showMessage("");
    }

    private JComboBox<Integer> getIncrementTimeComboBox() {
        if (incrementTimeComboBox == null) {
            incrementTimeComboBox = new JComboBox<Integer>();
            incrementTimeComboBox.setMaximumRowCount(9);
            incrementTimeComboBox.setModel(new DefaultComboBoxModel<Integer>());
            String[] incrementValues = new String[] { "1", "2", "5", "10", "20", "30", "45", "60" };
            for (String str : incrementValues) {
                incrementTimeComboBox.addItem(Integer.parseInt(str));
            }
            incrementTimeComboBox.setSelectedIndex(2);
            incrementTimeComboBox.setToolTipText("Select the amount (in minutes) to be added to the Scheduled Start Time, then press \"Increment\"");
            // incrementTimeComboBox.addActionListener(new ActionListener() {
            // @Override
            // public void actionPerformed(ActionEvent e) {
            // if (getIncrementTimeComboBox().getSelectedIndex() > 0) {
            // getIncrementTimeButton().setEnabled(true);
            // getDecrementTimeButton().setEnabled(true);
            // } else {
            // getIncrementTimeButton().setEnabled(false);
            // getDecrementTimeButton().setEnabled(false);
            // }
            // showMessage("");
            // }
            // });
        }
        return incrementTimeComboBox;
    }

    private JButton getIncrementTimeButton() {
        if (incrementTimeButton == null) {
            incrementTimeButton = new JButton("Increment");
            incrementTimeButton.setEnabled(true);
            incrementTimeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    incrementGUIStartTime();
                }
            });
            incrementTimeButton.setToolTipText("Increments the Scheduled Start Time by the amount selected in the drop-down list");
        }
        return incrementTimeButton;
    }

    private JButton getDecrementTimeButton() {
        if (decrementTimeButton == null) {
            decrementTimeButton = new JButton("Decrement");
            decrementTimeButton.setEnabled(true);
            decrementTimeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    decrementGUIStartTime();
                }
            });
            decrementTimeButton.setToolTipText("Decrements the Scheduled Start Time by the amount selected in the drop-down list");
        }
        return decrementTimeButton;
    }

    /**
     * Increments the Scheduled Start Time displayed in the GUI by the amount selected in the incrementTimeComboBox.
     */
    protected void incrementGUIStartTime() {

        GregorianCalendar currentTextboxStartTime = getScheduledStartTimeFromGUI();

        if (currentTextboxStartTime == null) {
            showMessage("Invalid time in Scheduled Start Time textbox; cannot increment");
        } else {
            currentTextboxStartTime.add(Calendar.MINUTE, ((Integer) (getIncrementTimeComboBox().getSelectedItem())));
            getScheduledStartTimeTextBox().setText(getGregorianTimeAsString(currentTextboxStartTime));
            enableUpdateButton();
            showMessage("");
        }
    }

    /**
     * Decrements the Scheduled Start Time displayed in the GUI by the amount selected in the incrementTimeComboBox.
     */
    protected void decrementGUIStartTime() {

        GregorianCalendar currentTextboxStartTime = getScheduledStartTimeFromGUI();

        if (currentTextboxStartTime == null) {
            showMessage("Invalid time in Scheduled Start Time textbox; cannot decrement");
        } else {
            currentTextboxStartTime.add(Calendar.MINUTE, -(((Integer) (getIncrementTimeComboBox().getSelectedItem()))));
            getScheduledStartTimeTextBox().setText(getGregorianTimeAsString(currentTextboxStartTime));
            enableUpdateButton();
            showMessage("");
        }
    }

    private JLabel getDropdownListLabel() {
        if (dropdownListLabel == null) {
            dropdownListLabel = new JLabel("Change Minutes:");
        }
        return dropdownListLabel;
    }

    private Component getHorizontalStrut() {
        if (horizontalStrut == null) {
            horizontalStrut = Box.createHorizontalStrut(20);
        }
        return horizontalStrut;
    }
    private Component getVerticalStrut() {
        if (verticalStrut == null) {
        	verticalStrut = Box.createVerticalStrut(20);
        }
        return verticalStrut;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
