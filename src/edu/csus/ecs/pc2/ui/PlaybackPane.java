package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.playback.PlaybackEvent;
import edu.csus.ecs.pc2.core.model.playback.PlaybackEvent.Action;

/**
 * Pane for Contest Playback.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PlaybackPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -1344873174060871842L;

    private JPanel centerPane = null;

    private JPanel buttonPane = null;

    private MCLB eventsListBox = null;

    private JButton startButton = null;

    private JButton stopButton = null;

    private JButton resetButton = null;

    private JPanel topPane = null;

    private JLabel eventStatusLabel = null;

    private JTextField timeWarpTextField = null;

    private JLabel msLabel = null;

    private JLabel stopAtLabel = null;

    private JTextField stopEventNumberTextField = null;

    private JTextField timeWarpSecsTextField = null;

    private JLabel secondsLabel = null;

    private JRadioButton eventEveryMinute = null;

    private JRadioButton everyMSEventPacing = null;

    private ButtonGroup timeWarpButtonGroup = null; // @jve:decl-index=0:

    /**
     * This method initializes
     * 
     */
    public PlaybackPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(594, 282));
        this.add(getCenterPane(), BorderLayout.WEST);
        this.add(getButtonPane(), BorderLayout.SOUTH);
        this.add(getTopPane(), BorderLayout.NORTH);
        this.add(getEventsListBox(), BorderLayout.CENTER);

        getTimeWarpButtonGroup().setSelected(getEventEveryMinute().getModel(), true);
        // getTeamReadsFrombuttonGroup().setSelected(getFileRadioButton().getModel(), true);

    }

    @Override
    public String getPluginTitle() {
        return "Contest Playback";
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new GridBagLayout());
        }
        return centerPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getStartButton(), null);
            buttonPane.add(getStopButton(), null);
            buttonPane.add(getResetButton(), null);
        }
        return buttonPane;
    }

    private MCLB getEventsListBox() {
        if (eventsListBox == null) {
            eventsListBox = new MCLB();

            Object[] cols = { "Seq", "Who", "Event", "Id", "When", "State" };
            eventsListBox.addColumns(cols);
            cols = null;
        }
        return eventsListBox;
    }

    public String[] buildPlayBackRow(PlaybackEvent playbackEvent) {
        String[] strings = new String[6];

        // Object[] cols = { "Seq", "Who", "Event", "Id", "When", "State" };

        strings[0] = "" + playbackEvent.getSequenceId();
        strings[1] = playbackEvent.getClientId().getName();
        strings[2] = playbackEvent.getAction().toString();
        strings[3] = "" + playbackEvent.getId();
        strings[4] = "" + playbackEvent.getEventTime();
        strings[5] = "" + playbackEvent.getEventStatus();

        return strings;
    }

    public void addSampleEventRows() {

        ClientId clientId = new ClientId(2, Type.TEAM, 22);
        PlaybackEvent playbackEvent = new PlaybackEvent(Action.UNDEFINED, clientId);

        String[] row = buildPlayBackRow(playbackEvent);

        eventsListBox.addRow(row);

        for (int i = 0; i < eventsListBox.getColumnCount(); i++) {
            eventsListBox.autoSizeColumn(i);
        }

    }

    public void addSampleEventRows2() {

        String[][] cols = { { "1", "team 2", "RUN SUBMIT", "1", "12", "DONE" }, { "2", "judge 4", "RUN JUDGEMENT", "1", "22", "DONE" }, { "3", "team 2", "RUN SUBMIT", "2", "24", "PENDING" },
                { "4", "team 6", "MYSTERY 101", "2", "32", "PENDING" }, { "5", "team 4", "RUN SUBMIT", "3", "42", "PENDING" } };

        for (String[] colarray : cols) {
            eventsListBox.addRow(colarray);
        }
        for (int i = 0; i < eventsListBox.getColumnCount(); i++) {
            eventsListBox.autoSizeColumn(i);
        }

    }

    /**
     * This method initializes startButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartButton() {
        if (startButton == null) {
            startButton = new JButton();
            startButton.setText("Start");
        }
        return startButton;
    }

    /**
     * This method initializes stopButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStopButton() {
        if (stopButton == null) {
            stopButton = new JButton();
            stopButton.setText("Stop");
        }
        return stopButton;
    }

    /**
     * This method initializes resetButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getResetButton() {
        if (resetButton == null) {
            resetButton = new JButton();
            resetButton.setText("Reset");
        }
        return resetButton;
    }

    /**
     * This method initializes topPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPane() {
        if (topPane == null) {
            secondsLabel = new JLabel();
            secondsLabel.setBounds(new Rectangle(246, 18, 82, 24));
            secondsLabel.setText("seconds");
            stopAtLabel = new JLabel();
            stopAtLabel.setBounds(new Rectangle(40, 87, 146, 24));
            stopAtLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            stopAtLabel.setText("Stop at event");
            msLabel = new JLabel();
            msLabel.setBounds(new Rectangle(246, 52, 32, 24));
            msLabel.setText("ms");
            eventStatusLabel = new JLabel();
            eventStatusLabel.setBounds(new Rectangle(473, 44, 105, 28));
            eventStatusLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            eventStatusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            eventStatusLabel.setText("At event 2");
            topPane = new JPanel();
            topPane.setLayout(null);
            topPane.setPreferredSize(new Dimension(120, 120));
            topPane.add(eventStatusLabel, null);
            topPane.add(getTimeWarpTextField(), null);
            topPane.add(msLabel, null);
            topPane.add(stopAtLabel, null);
            topPane.add(getStopEventNumberTextField(), null);
            topPane.add(getTimeWarpSecsTextField(), null);
            topPane.add(secondsLabel, null);
            topPane.add(getEventEveryMinute(), null);
            topPane.add(getEveryMSEventPacing(), null);
        }
        return topPane;
    }

    /**
     * This method initializes timeWarpTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTimeWarpTextField() {
        if (timeWarpTextField == null) {
            timeWarpTextField = new JTextField();
            timeWarpTextField.setBounds(new Rectangle(195, 52, 38, 24));
            timeWarpTextField.setText("1000");
        }
        return timeWarpTextField;
    }

    /**
     * This method initializes stopEventNumberTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getStopEventNumberTextField() {
        if (stopEventNumberTextField == null) {
            stopEventNumberTextField = new JTextField();
            stopEventNumberTextField.setBounds(new Rectangle(197, 85, 38, 24));
            stopEventNumberTextField.setText("99");
        }
        return stopEventNumberTextField;
    }

    /**
     * This method initializes timeWarpSecsTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTimeWarpSecsTextField() {
        if (timeWarpSecsTextField == null) {
            timeWarpSecsTextField = new JTextField();
            timeWarpSecsTextField.setBounds(new Rectangle(195, 18, 38, 24));
            timeWarpSecsTextField.setText("1000");
        }
        return timeWarpSecsTextField;
    }

    /**
     * This method initializes eventEveryMinute
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getEventEveryMinute() {
        if (eventEveryMinute == null) {
            eventEveryMinute = new JRadioButton();
            eventEveryMinute.setBounds(new Rectangle(14, 23, 170, 21));
            eventEveryMinute.setToolTipText("This paces each minute in the specified seconds");
            eventEveryMinute.setText("Execute every minute in ");
        }
        return eventEveryMinute;
    }

    /**
     * This method initializes everyMSEventPacing
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getEveryMSEventPacing() {
        if (everyMSEventPacing == null) {
            everyMSEventPacing = new JRadioButton();
            everyMSEventPacing.setBounds(new Rectangle(13, 54, 163, 24));
            everyMSEventPacing.setText("Execute each event");
        }
        return everyMSEventPacing;
    }

    public ButtonGroup getTimeWarpButtonGroup() {
        if (timeWarpButtonGroup == null) {
            timeWarpButtonGroup = new ButtonGroup();
            timeWarpButtonGroup.add(getEventEveryMinute());
            timeWarpButtonGroup.add(getEveryMSEventPacing());
        }
        return timeWarpButtonGroup;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
