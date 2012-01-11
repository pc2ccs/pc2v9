package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

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

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IPlayBackEventListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlayBackEvent;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.playback.PlaybackManager;
import edu.csus.ecs.pc2.core.model.playback.PlaybackRecord;
import edu.csus.ecs.pc2.core.model.playback.ReplayEvent;
import edu.csus.ecs.pc2.core.model.playback.ReplayEvent.EventType;
import edu.csus.ecs.pc2.core.model.playback.ReplayEventDetails;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.ui.FrameUtilities.HorizontalPosition;
import edu.csus.ecs.pc2.ui.FrameUtilities.VerticalPosition;

/**
 * Pane for Contest Playback.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PlaybackPane extends JPanePlugin {

    // TODO 673 - add MessageListener for Area.SERVER_PROCESSING
    
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

    private JLabel currentEventLabel = null;

    private JTextField minEventsTextField = null;

    private JLabel iterateTitleLabel = null;

    private PlaybackManager manager = new PlaybackManager();
    
    private JFramePlugin messageFrame = null;

    private JButton reportButton = null;

    private PlaybackRecord currentRecord;
    
 
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
        this.setSize(new Dimension(594, 304));
        this.add(getCenterPane(), BorderLayout.WEST);
        this.add(getButtonPane(), BorderLayout.SOUTH);
        this.add(getTopPane(), BorderLayout.NORTH);
        this.add(getEventsListBox(), BorderLayout.CENTER);
        
        currentEventLabel.setText("At (start)");
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
            buttonPane.add(getReportButton(), null);
            buttonPane.add(getLoadButton(), null);
        }
        return buttonPane;
    }

    private MCLB getEventsListBox() {
        if (eventsListBox == null) {
            eventsListBox = new MCLB();

            Object[] cols = { "Seq", "Site", "Who", "Event", "Id", "When", "State", "Details" };
            eventsListBox.addColumns(cols);
            cols = null;
            
            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());
            HeapSorter accountNameSorter = new HeapSorter();
            accountNameSorter.setComparator(new AccountColumnComparator());

            int idx = 0;

            eventsListBox.setColumnSorter(idx++, numericStringSorter, 1); // Seq
            eventsListBox.setColumnSorter(idx++, sorter, 2); // Site
            eventsListBox.setColumnSorter(idx++, accountNameSorter, 3); // Who/team
            eventsListBox.setColumnSorter(idx++, sorter, 4); // Event
            eventsListBox.setColumnSorter(idx++, numericStringSorter, 5); // id
            eventsListBox.setColumnSorter(idx++, numericStringSorter, 6); // When
            eventsListBox.setColumnSorter(idx++, sorter, 7); // State
            eventsListBox.setColumnSorter(idx++, sorter, 8); // details
            
        }
        return eventsListBox;
    }
    
    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        
        initializePermissions();
        
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addPlayBackEventListener(new PlayBackEventListener());
        getContest().addRunListener(new RunListenerImplementation());
        
        manager = getContest().getPlaybackManager();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                PlaybackInfo [] infos = getContest().getPlaybackInfos();
                if (infos.length > 0) {
                    updatePlaybackInfo(getContest().getPlaybackInfos()[0]);
                }
            }
        });
    }

    public String[] buildPlayBackRow(PlaybackRecord playbackRecord) {
        String[] strings = new String[eventsListBox.getColumnCount()];

        // Object[] cols = { "Seq", "Site", "Who", "Event", "Id", "When", "State", "Details" };
        
        ReplayEvent replayEvent= playbackRecord.getReplayEvent();
        ReplayEventDetails details =  replayEvent.getEventDetails();

        strings[0] = "" + playbackRecord.getSequenceNumber();
        strings[1] = Integer.toString(replayEvent.getClientId().getSiteNumber());
        Site site = getContest().getSite(replayEvent.getClientId().getSiteNumber());
        if (site != null) {
            String name = site.getDisplayName();
            strings[1] = Integer.toString(replayEvent.getClientId().getSiteNumber()) + " " + stringElipsis(name, 11) + " ";
        }
        strings[2] = replayEvent.getClientId().getName();
        strings[3] = replayEvent.getEventType().toString();
        strings[4] = Integer.toString(playbackRecord.getId());
        strings[5] = Long.toString(replayEvent.getEventTime());
        strings[6] = "" + playbackRecord.getEventStatus();

        switch (playbackRecord.getReplayEvent().getEventType()) {
            case RUN_SUBMIT:
                strings[7] = getDetails(playbackRecord);
                break;

            case RUN_JUDGEMENT:
                JudgementRecord judgementRecord = details.getJudgementRecord();
                ElementId id = judgementRecord.getJudgementId();
                if (id != null) {
                    Judgement judgement = getContest().getJudgement(id);
                    strings[7] = judgement.getDisplayName();
                } else {
                    strings[7] = "Undefined judgement: " + id;

                }
                break;
            default:
                Arrays.fill(strings, "");
                strings[0] = "" + playbackRecord.getSequenceNumber();
                strings[3] = ReplayEvent.EventType.UNDEFINED.toString();
                break;

        }

        return strings;
    }

    protected String stringElipsis(String name, int maxlen) {
        if (name.length() > maxlen + 2 && name.length() > 4) {
            return name.substring(0, maxlen - 3) + "...";
        } else {
            return name;
        }
    }

    private String getDetails(PlaybackRecord record) {

        ReplayEvent event = record.getReplayEvent();
        Run run = event.getEventDetails().getRun();

        String probName = "?";
        ElementId problemId = run.getProblemId();
        if (problemId != null) {
            Problem problem = getContest().getProblem(problemId);
            if (problem != null) {
                probName = problem.getDisplayName();
            }
        }

        String langName = "?";
        ElementId languageId = run.getLanguageId();
        if (languageId != null) {
            Language language = getContest().getLanguage(languageId);
            if (language != null) {
                langName = language.getDisplayName();
            }
        }

        return probName + ", " + langName;
    }

    public void addSampleEventRows() {

        ClientId clientId = new ClientId(2, Type.TEAM, 22);
        ReplayEvent playbackEvent = new ReplayEvent(EventType.UNDEFINED, clientId);

        PlaybackRecord record = createNewPlayback (playbackEvent, eventsListBox.getRowCount());  
        String[] row = buildPlayBackRow(record);

        eventsListBox.addRow(row);
        eventsListBox.autoSizeAllColumns();
        for (int i = 0; i < eventsListBox.getColumnCount(); i++) {
            eventsListBox.autoSizeColumn(i);
        }

    }

    private PlaybackRecord createNewPlayback(ReplayEvent replayEvent, int sequenceNumber) {
        return new PlaybackRecord(replayEvent, sequenceNumber);
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

        if (!isAllowed(Permission.Type.START_PLAYBACK)) {
            logMessage("Not allowed to start playback");
            JOptionPane.showMessageDialog(this, "Not allowed to start playback");
            return;
        }

        final PlaybackInfo playbackInfo = manager.getPlaybackInfo();
        playbackInfo.setStarted(true);

        String intValueString = getMinEventsTextField().getText();
        int minEvents = 0;
        if (intValueString.length() > 0) {
            minEvents = Integer.parseInt(intValueString);
        }
        playbackInfo.setMinimumPlaybackRecords(minEvents);

        if (edu.csus.ecs.pc2.core.model.ClientType.isAdmin(getContest().getClientId())) {
            getController().startPlayback(playbackInfo);
            System.err.println("debug 22 started " + playbackInfo);
            return;
        }

        try {
            getController().startPlayback(playbackInfo);
            System.err.println("debug 22 started " + playbackInfo);
        } catch (Exception e1) {
            e1.printStackTrace(System.err);
            logMessage("Unable to start playback " + e1.getMessage(), e1);
            JOptionPane.showMessageDialog(this, "Unable to start playback " + e1.getMessage());
        }

        if (eventsListBox.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No events defined");
            return;
        }

        if (manager.allEventsExecuted()) {
            JOptionPane.showMessageDialog(this, "All events executed");
            return;
        }

        String lastEventString = getStopEventNumberTextField().getText();

        int lastEventToRunTo = Integer.MAX_VALUE;
        if (lastEventString.trim().length() > 0) {
            lastEventToRunTo = Integer.parseInt(lastEventString);
        }

        int currentEventNumber = manager.getSequenceNumber();

        if (currentEventNumber == lastEventToRunTo) {
            JOptionPane.showMessageDialog(this, "Already before event " + lastEventToRunTo);
            return;
        }
        if (currentEventNumber > lastEventToRunTo) {
            JOptionPane.showMessageDialog(this, "Way after event " + lastEventToRunTo + " dude (at event " + (currentEventNumber - 1) + ")");
            return;
        }

        final int waitTime = Integer.parseInt(getTimeWarpTextField().getText());

        playbackInfo.setWaitBetweenEventsMS(waitTime);

        if (lastEventToRunTo > manager.getPlaybackRecords().length) {
            lastEventToRunTo = manager.getPlaybackRecords().length;
        }

        setRunningButtons(true);

        getContest().getPlaybackManager().startPlayback(getContest(), getController(), new Runnable() {

            public void run() {
                updatePlaybackInfo(playbackInfo);
            }
        });
    }

    protected void populateGUI(PlaybackInfo info) {
        populateGUI(info, false);
    }
        
    protected void populateGUI(PlaybackInfo info, boolean forcePlaybackGridRefresh) {
        
        getTimeWarpTextField().setText(Integer.toString(info.getWaitBetweenEventsMS()));
        getMinEventsTextField().setText(Integer.toString(info.getMinimumPlaybackRecords()));
        setRunningButtons(manager.isPlaybackRunning());

        PlaybackRecord[] records = manager.getPlaybackRecords();

        if ((records.length > 0 && getEventsListBox().getRowCount() < records.length) || forcePlaybackGridRefresh) {

            int rowCount = records.length;

            Object[][] rowValues = new Object[rowCount][eventsListBox.getColumnCount()];

            for (int i = 0; i < rowCount; i++) {
                PlaybackRecord record = records[i];
                rowValues[i] = buildPlayBackRow(record);
            }

            getEventsListBox().removeAllRows();
            getEventsListBox().addRows(rowValues, records);
            getEventsListBox().autoSizeAllColumns();
            getEventsListBox().sort();
        }
    }
    
    protected void updatePlaybackInfo(PlaybackInfo info) {
        
        populateGUI(info);

        currentRecord = null;

        String eventInfo = "";
        if (info.getSequenceNumber() > 0) {
            currentRecord = manager.getPlaybackRecords()[info.getSequenceNumber() - 1];
            eventInfo = " status=" + currentRecord.getEventStatus() + " " + currentRecord.getReplayEvent();
        }

        getController().getLog().info("Playback running=" + manager.isPlaybackRunning() + " sequence " + info.getSequenceNumber() + eventInfo);
        
        

        int numleft = manager.getPlaybackInfo().getMinimumPlaybackRecords() - manager.getSequenceNumber();
        currentEventLabel.setText(numleft + " events left");

        if (currentRecord != null) {
            int rowNumber = info.getSequenceNumber() - 1;
            String[] row = buildPlayBackRow(currentRecord);
            getEventsListBox().replaceRow(row, rowNumber);
            getEventsListBox().autoSizeAllColumns();
        }
        setRunningButtons(info.isStarted());
    }

    private void logMessage(String string) {
        getController().getLog().info (string);
    }

    private void logMessage(String string, Exception exception) {
        getController().getLog().log(Log.WARNING, string, exception);
    }

    /**
     * Goes through list and rewinds all runs.
     */
    protected void resetAllEventsAndWait() {
        
        Exception ex = null;

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                
                public void run() {
                    resetAllEvents();
                }
            });
        } catch (InterruptedException e) {
            logException("Exception resettting replay", e);
            ex = e;
        } catch (InvocationTargetException e) {
            logException("Exception resettting replay", e);
            ex = e;
        }
        
        if (ex != null){
            final Exception throwed = ex;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    showMessage(null, "Exception resetting replay", "Exception resetting replay: "+throwed.getMessage());
                }
            });
        }
    }
    
    private void setRunningButtons(boolean running) {
        
        startButton.setEnabled(isAllowed(Permission.Type.START_PLAYBACK));
        stopButton.setEnabled(isAllowed(Permission.Type.STOP_PLAYBACK));
        resetButton.setEnabled(isAllowed(Permission.Type.EDIT_PLAYBACK));
        loadButton.setEnabled(isAllowed(Permission.Type.EDIT_PLAYBACK));
        
        if (isAllowed(Permission.Type.START_PLAYBACK)) {
            getStartButton().setEnabled(! running);
        }
        if (isAllowed(Permission.Type.STOP_PLAYBACK)) {
            getStopButton().setEnabled(running);
        }
        if (isAllowed(Permission.Type.EDIT_PLAYBACK)) {
            getResetButton().setEnabled(getContest().getPlaybackManager().getPlaybackInfo().getReplayList().length > 0);
        }
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
            stopButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    stopEventsRunning();
                }
            });
        }
        return stopButton;
    }

    protected void stopEventsRunning() {
        
        if (!isAllowed(Permission.Type.STOP_PLAYBACK)) {
            logMessage("Not allowed to stop playback");
            JOptionPane.showMessageDialog(this, "Not allowed to stop playback");
            return;
        }

        final PlaybackInfo playbackInfo = manager.getPlaybackInfo();
        playbackInfo.setStarted(false);

        String intValueString = getMinEventsTextField().getText();
        int minEvents = 0;
        if (intValueString.length() > 0) {
            minEvents = Integer.parseInt(intValueString);
        }
        playbackInfo.setMinimumPlaybackRecords(minEvents);

        getController().startPlayback(playbackInfo);
        System.err.println("debug 22 stopped " + playbackInfo);
        return;
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
            resetButton.setToolTipText("Reset (erase or clear) events");
            resetButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    resetAllEvents();
                    
                }
            });
        }
        return resetButton;
    }

    protected void resetAllEvents() {

        if (!isAllowed(Permission.Type.START_PLAYBACK)) {
            logMessage("Not allowed to start playback");
            JOptionPane.showMessageDialog(this, "Not allowed to start playback");
            return;
        }

        final PlaybackInfo playbackInfo = manager.getPlaybackInfo();
        playbackInfo.setStarted(false);

        String intValueString = getMinEventsTextField().getText();
        int minEvents = 0;
        if (intValueString.length() > 0) {
            minEvents = Integer.parseInt(intValueString);
        }
        playbackInfo.setMinimumPlaybackRecords(minEvents);

        if (edu.csus.ecs.pc2.core.model.ClientType.isAdmin(getContest().getClientId())) {
            getController().startPlayback(playbackInfo);
            System.err.println("debug 22 started "+playbackInfo);
            return;
        }
    }
    
    /**
     * This method initializes topPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPane() {
        if (topPane == null) {
            iterateTitleLabel = new JLabel();
            iterateTitleLabel.setText("Minimum Number of events");
            iterateTitleLabel.setBounds(new Rectangle(26, 85, 202, 24));
            iterateTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            currentEventLabel = new JLabel();
            currentEventLabel.setHorizontalAlignment(SwingConstants.CENTER);
            currentEventLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            currentEventLabel.setBounds(new Rectangle(356, 22, 115, 38));
            currentEventLabel.setText("Not started");
            stopAtLabel = new JLabel();
            stopAtLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            stopAtLabel.setBounds(new Rectangle(26, 48, 202, 24));
            stopAtLabel.setText("Stop before event");
            msLabel = new JLabel();
            msLabel.setText("ms");
            msLabel.setBounds(new Rectangle(294, 15, 32, 24));
            topPane = new JPanel();
            topPane.setLayout(null);
            topPane.setPreferredSize(new Dimension(160, 160));
            topPane.add(getTimeWarpTextField(), null);
            topPane.add(msLabel, null);
            topPane.add(stopAtLabel, null);
            topPane.add(getStopEventNumberTextField(), null);
            topPane.add(getEveryMSEventPacing(), null);
            topPane.add(getStepButton(), null);
            topPane.add(currentEventLabel, null);
            topPane.add(getMinEventsTextField(), null);
            topPane.add(iterateTitleLabel, null);
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
            timeWarpTextField.setBounds(new Rectangle(252, 13, 33, 24));
            timeWarpTextField.setDocument(new IntegerDocument());
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
            stopEventNumberTextField.setBounds(new Rectangle(252, 48, 33, 24));
            stopEventNumberTextField.setDocument(new IntegerDocument());
            stopEventNumberTextField.setText("");
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
            everyMSEventPacing.setSelected(true);
            everyMSEventPacing.setHorizontalAlignment(SwingConstants.RIGHT);
            everyMSEventPacing.setBounds(new Rectangle(15, 13, 216, 24));
            everyMSEventPacing.setText("Execute each event every");
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
            stepButton.setMnemonic(KeyEvent.VK_R);
            stepButton.setBounds(new Rectangle(252, 120, 141, 25));
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
        
        if (manager.allEventsExecuted()) {
            JOptionPane.showMessageDialog(this, "All events executed");
            return;
        }
        
        int currentEventNumber = manager.getSequenceNumber();
        
        try {
            PlaybackRecord record = manager.executeNextEvent(getContest(), getController());
            
            int numleft = manager.getPlaybackInfo().getMinimumPlaybackRecords() - manager.getSequenceNumber();
            currentEventLabel.setText(numleft + " events left");

            String[] row = buildPlayBackRow(record);

            getEventsListBox().replaceRow(row, currentEventNumber - 1);
            getEventsListBox().autoSizeAllColumns();
            
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
                    loadReplayFile();
                }
            });
        }
        return loadButton;
    }

    protected void loadReplayFile() {

        String filename = null;

        try {
            filename = getFileName();
            if (filename != null) {

                if (edu.csus.ecs.pc2.core.model.ClientType.isAdmin(getContest().getClientId())) {
                    PlaybackInfo playbackInfo = getPlaybackInfo();
                    playbackInfo.setFilename(filename);
                    int waitTime = Integer.parseInt(getTimeWarpTextField().getText());
                    playbackInfo.setWaitBetweenEventsMS(waitTime);
                    playbackInfo.setStarted(false);
                    getController().startPlayback(playbackInfo);
                    System.err.println("debug 22 started "+playbackInfo);
                    return;
                }

                PlaybackManager playbackManager = getContest().getPlaybackManager();
                playbackManager.createPlaybackInfo(filename, getContest());
                PlaybackRecord[] records = playbackManager.getPlaybackRecords();

                if (records.length == 0) {
                    JOptionPane.showMessageDialog(this, "No events found in " + filename);
                } else {

                    for (PlaybackRecord record : records) {
                        String[] row = buildPlayBackRow(record);
                        getEventsListBox().addRow(row, record);
                    }
                    getEventsListBox().autoSizeAllColumns();
                    setRunningButtons(false);
                    JOptionPane.showMessageDialog(this, "Loaded " + records.length + " events from " + filename);

                }
            }
        } catch (FileNotFoundException notFound) {
            JOptionPane.showMessageDialog(this, "No such file: " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to load file: " + filename + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get or create Playback Info.
     * @return
     */
    private PlaybackInfo getPlaybackInfo() {
        PlaybackInfo[] infos = getContest().getPlaybackInfos();
        if (infos.length > 0) {
            return infos[0];
        } else {
            return new PlaybackInfo();
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

    /**
     * This method initializes minEventsTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getMinEventsTextField() {
        if (minEventsTextField == null) {
            minEventsTextField = new JTextField();
            minEventsTextField.setBounds(new Rectangle(252, 85, 73, 24));
            minEventsTextField.setDocument(new IntegerDocument());
            minEventsTextField.setText("");
        }
        return minEventsTextField;
    }

    private void updateGUIperPermissions() {
        setRunningButtons(manager.isPlaybackRunning());
    }


    /**
     * Account Listener for Playback Pane. 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignore, doesn't affect this pane
        }

        public void accountModified(AccountEvent event) {
            // check if is this account
            Account account = event.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });
            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore, does not affect this pane
        }

        public void accountsModified(AccountEvent accountEvent) {
            // check if it included this account
            boolean theyModifiedUs = false;
            for (Account account : accountEvent.getAccounts()) {
                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    theyModifiedUs = true;
                    initializePermissions();
                }
            }
            final boolean finalTheyModifiedUs = theyModifiedUs;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (finalTheyModifiedUs) {
                        updateGUIperPermissions();
                    }
                }
            });
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                }
            });
        }
    }
    
    /**
     *  PlayBackEvent Listener.
     *  
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class PlayBackEventListener implements IPlayBackEventListener{

        public void playbackChanged(final PlayBackEvent playBackEvent) {
            System.out.println("PlayBackEvent " + playBackEvent.getAction() + " " + playBackEvent.getPlaybackInfo());
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updatePlaybackInfo(playBackEvent.getPlaybackInfo());
                }
            });
        }

        public void playbackRefreshAll(PlayBackEvent playBackEvent) {
            playbackChanged(playBackEvent);
        }

        public void playbackAdded(PlayBackEvent playBackEvent) {
            playbackChanged(playBackEvent);
        }

        public void playbackReset(PlayBackEvent playBackEvent) {
            // TODO 673 code reset/rewind
        }
        
    }
    
    public JFramePlugin getMessageFrame() {
        if (messageFrame == null) {
            messageFrame = new JFramePluginImpl(new MessageMonitorPane());
        }
        return messageFrame;
    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Messages");
            reportButton.setMnemonic(KeyEvent.VK_M);
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showMessageFrame();
                }
            });
        }
        return reportButton;
    }

    protected void showMessageFrame() {
        JFramePlugin frame = getMessageFrame();
        frame.setLocation(getX(), getY());
        FrameUtilities.setFramePosition(frame, HorizontalPosition.LEFT, VerticalPosition.NO_CHANGE);
        frame.setVisible(true);
    }
    
    /**
     * Run Listener
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            updateAtEvent(event.getRun().getPlaybackSequenceNumber());
        }

        public void runChanged(RunEvent event) {
            updateAtEvent(event.getRun().getPlaybackSequenceNumber());
        }

        public void runRemoved(RunEvent event) {

        }

        public void refreshRuns(RunEvent event) {
        }
    }

    public void updateAtEvent(int playbackSequenceNumber) {
        
        final int numleft = manager.getPlaybackInfo().getMinimumPlaybackRecords() - playbackSequenceNumber;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                currentEventLabel.setText(numleft + " events left");
            }
        });
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
