package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.playback.PlaybackEvent;
import edu.csus.ecs.pc2.core.model.playback.PlaybackManager;
import edu.csus.ecs.pc2.core.model.playback.PlaybackEvent.Action;
import java.awt.event.KeyEvent;

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

    private JTextField timeWarpTextField = null;

    private JLabel msLabel = null;

    private JLabel stopAtLabel = null;

    private JTextField stopEventNumberTextField = null;

    private JRadioButton everyMSEventPacing = null;

    private ButtonGroup timeWarpButtonGroup = null; // @jve:decl-index=0:

    private JButton stepButton = null;

    private JButton loadButton = null;

    private PlaybackManager playbackManager = new PlaybackManager(); // @jve:decl-index=0:

    private JLabel currentEventLabel = null;

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
        
        currentEventLabel.setText("At (start)");

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
            buttonPane.add(getLoadButton(), null);
        }
        return buttonPane;
    }

    private MCLB getEventsListBox() {
        if (eventsListBox == null) {
            eventsListBox = new MCLB();

            Object[] cols = { "Seq", "Who", "Event", "Id", "When", "State", "Details" };
            eventsListBox.addColumns(cols);
            cols = null;
        }
        return eventsListBox;
    }

    public String[] buildPlayBackRow(PlaybackEvent playbackEvent) {
        String[] strings = new String[7];
        
        if (playbackEvent.getAction().equals(PlaybackEvent.Action.RUN_SUBMIT)) {

            // Object[] cols = { "Seq", "Who", "Event", "Id", "When", "State", "Details"};

            strings[0] = "" + playbackEvent.getSequenceId();
            strings[1] = playbackEvent.getClientId().getName();
            strings[2] = playbackEvent.getAction().toString();
            strings[3] = "" + playbackEvent.getId();
            strings[4] = "" + playbackEvent.getEventTime();
            strings[5] = "" + playbackEvent.getEventStatus();
        } else if (playbackEvent.getAction().equals(PlaybackEvent.Action.RUN_JUDGEMENT)) {

            // Object[] cols = { "Seq", "Who", "Event", "Id", "When", "State", "Details" };

            strings[0] = "" + playbackEvent.getSequenceId();
            strings[1] = playbackEvent.getClientId().getName();
            strings[2] = playbackEvent.getAction().toString();
            strings[3] = "" + playbackEvent.getId();
            strings[4] = "" + playbackEvent.getEventTime();
            strings[5] = "" + playbackEvent.getEventStatus();
            JudgementRecord judgementRecord = playbackEvent.getJudgementRecord();
            ElementId id =  judgementRecord.getJudgementId();
            if (id != null){
                Judgement judgement = getContest().getJudgement(id);
                strings[6] = judgement.getDisplayName();
            }
            else {
                strings[6] = "Undefined judgement: "+ id;

            }
        } else {
            strings[0] = "" + playbackEvent.getSequenceId();
            strings[1] = "";
            strings[2] = PlaybackEvent.Action.UNDEFINED.toString();
            strings[3] = "";
            strings[4] = "";
            strings[5] = ""; 
        }

        return strings;
    }

    public void addSampleEventRows() {

        ClientId clientId = new ClientId(2, Type.TEAM, 22);
        PlaybackEvent playbackEvent = new PlaybackEvent(Action.UNDEFINED, clientId);

        playbackEvent.setSequenceId(eventsListBox.getRowCount());
        String[] row = buildPlayBackRow(playbackEvent);

        eventsListBox.addRow(row);

        for (int i = 0; i < eventsListBox.getColumnCount(); i++) {
            eventsListBox.autoSizeColumn(i);
        }

    }

    private void autoSizeColumns() {

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
            startButton.setMnemonic(KeyEvent.VK_S);
            startButton.setToolTipText("Start running events");
            startButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startRunningEvents();
                }
            });
        }
        return startButton;
    }

    protected void startRunningEvents() {

        if (eventsListBox.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No events defined");
            return;
        }

        final int currentEventNumber = playbackManager.getSequenceNumber();

        if (currentEventNumber > eventsListBox.getRowCount()) {
            JOptionPane.showMessageDialog(this, "All events executed");
            return;
        }

        int lastEventToRunTo = Integer.parseInt(getStopEventNumberTextField().getText());

        if (currentEventNumber == lastEventToRunTo) {
            JOptionPane.showMessageDialog(this, "Already before event " + lastEventToRunTo);
            return;
        }
        if (currentEventNumber > lastEventToRunTo) {
            JOptionPane.showMessageDialog(this, "Way after event " + lastEventToRunTo + " dude (at event " + (currentEventNumber - 1) + ")");
            return;
        }

        final int waitTime = Integer.parseInt(getTimeWarpTextField().getText());

        if (lastEventToRunTo > getEventsListBox().getRowCount()) {
            lastEventToRunTo = getEventsListBox().getRowCount();
        }

        final int runToStep = lastEventToRunTo;

        new Thread(new Runnable() {

            public void run() {
                for (int i = currentEventNumber; i < runToStep; i++) {

                    PlaybackEvent playbackEvent = (PlaybackEvent) eventsListBox.getKeys()[i - 1];
                    try {
                        currentEventLabel.setText("At event " + playbackManager.getSequenceNumber());
                        playbackManager.executeEvent(playbackEvent, getContest(), getController());

                        final String[] row = buildPlayBackRow(playbackEvent);
                        final int rowNumber = i;

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                getEventsListBox().replaceRow(row, rowNumber - 1);
                                autoSizeColumns();
                            }
                        });

                        if (waitTime > 0) {
                            Thread.sleep(waitTime);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
            stopButton.setEnabled(false);
            stopButton.setMnemonic(KeyEvent.VK_UNDEFINED);
            stopButton.setToolTipText("Stop running events");
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
            resetButton.setEnabled(false);
            resetButton.setToolTipText("Reset (erase) runs");
            resetButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    deleteAllRuns();
                }
            });
        }
        return resetButton;
    }

    protected void deleteAllRuns() {
        // TODO code deleteAllRuns
    }

    /**
     * This method initializes topPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPane() {
        if (topPane == null) {
            currentEventLabel = new JLabel();
            currentEventLabel.setBounds(new Rectangle(297, 24, 115, 38));
            currentEventLabel.setHorizontalAlignment(SwingConstants.CENTER);
            currentEventLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            currentEventLabel.setText("At ###");
            stopAtLabel = new JLabel();
            stopAtLabel.setBounds(new Rectangle(26, 46, 146, 24));
            stopAtLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            stopAtLabel.setText("Stop before event");
            msLabel = new JLabel();
            msLabel.setBounds(new Rectangle(248, 13, 32, 24));
            msLabel.setText("ms");
            topPane = new JPanel();
            topPane.setLayout(null);
            topPane.setPreferredSize(new Dimension(120, 120));
            topPane.add(getTimeWarpTextField(), null);
            topPane.add(msLabel, null);
            topPane.add(stopAtLabel, null);
            topPane.add(getStopEventNumberTextField(), null);
            topPane.add(getEveryMSEventPacing(), null);
            topPane.add(getStepButton(), null);
            topPane.add(currentEventLabel, null);
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
            timeWarpTextField.setBounds(new Rectangle(190, 13, 38, 24));
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
            stopEventNumberTextField.setBounds(new Rectangle(190, 44, 38, 24));
            stopEventNumberTextField.setText("99");
        }
        return stopEventNumberTextField;
    }

    /**
     * This method initializes everyMSEventPacing
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getEveryMSEventPacing() {
        if (everyMSEventPacing == null) {
            everyMSEventPacing = new JRadioButton();
            everyMSEventPacing.setBounds(new Rectangle(15, 13, 163, 24));
            everyMSEventPacing.setSelected(true);
            everyMSEventPacing.setText("Execute each event");
        }
        return everyMSEventPacing;
    }

    public ButtonGroup getTimeWarpButtonGroup() {
        if (timeWarpButtonGroup == null) {
            timeWarpButtonGroup = new ButtonGroup();
            // timeWarpButtonGroup.add(getEventEveryMinute());
            timeWarpButtonGroup.add(getEveryMSEventPacing());
        }
        return timeWarpButtonGroup;
    }

    /**
     * This method initializes stepButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStepButton() {
        if (stepButton == null) {
            stepButton = new JButton();
            stepButton.setBounds(new Rectangle(187, 81, 141, 25));
            stepButton.setMnemonic(KeyEvent.VK_R);
            stepButton.setText("Run one event");

            stepButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    executeNextStep();
                }

            });
        }
        return stepButton;
    }

    protected void executeNextStep() {

        if (eventsListBox.getRowCount() == 0){
            JOptionPane.showMessageDialog(this, "No events defined");
            return;
        }
        
        int currentEventNumber = playbackManager.getSequenceNumber();
        
        if (currentEventNumber > eventsListBox.getRowCount()){
            JOptionPane.showMessageDialog(this, "All events executed");
            return;
        }
        
        PlaybackEvent playbackEvent = (PlaybackEvent) eventsListBox.getKeys()[currentEventNumber - 1];
        try {
            currentEventLabel.setText("At event " + playbackManager.getSequenceNumber());
            playbackManager.executeEvent(playbackEvent, getContest(), getController());

            String[] row = buildPlayBackRow(playbackEvent);

            getEventsListBox().replaceRow(row, currentEventNumber - 1);
            autoSizeColumns();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method initializes loadButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadButton() {
        if (loadButton == null) {
            loadButton = new JButton();
            loadButton.setText("Load");
            loadButton.setMnemonic(KeyEvent.VK_L);
            loadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectAndloadEventFile();
                }
            });
        }
        return loadButton;
    }

    protected void selectAndloadEventFile() {

        String filename = null;

        try {
            filename = getFileName();
            if (filename != null) {

                PlaybackEvent[] playbackEvents = playbackManager.loadPlayback(filename, getContest());
                if (playbackEvents == null || playbackEvents.length == 0) {
                    JOptionPane.showMessageDialog(this, "No events found in " + filename);
                } else {
                    for (PlaybackEvent playbackEvent : playbackEvents) {
                        playbackEvent.setSequenceId(eventsListBox.getRowCount()+1);
                        String[] row = buildPlayBackRow(playbackEvent);
                        getEventsListBox().addRow(row, playbackEvent);
                    }
                    autoSizeColumns();
                    JOptionPane.showMessageDialog(this, "Loaded " + playbackEvents.length + " events from " + filename);

                }
            }
        } catch (FileNotFoundException notFound) {
            JOptionPane.showMessageDialog(this, "No such file: " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to load file: " + filename+ " "+e.getMessage());
            e.printStackTrace();
        }
    }

    private String getFileName() throws IOException {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getCanonicalFile().toString();
        }
        chooser = null;
        return null;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
