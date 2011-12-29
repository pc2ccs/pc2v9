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
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IPlayBackEventListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlayBackEvent;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.playback.EventStatus;
import edu.csus.ecs.pc2.core.model.playback.PlaybackManager;
import edu.csus.ecs.pc2.core.model.playback.ReplayEvent;
import edu.csus.ecs.pc2.core.model.playback.ReplayEvent.Action;
import edu.csus.ecs.pc2.core.security.Permission;

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

    private static final String RUN_MESSAGE_SUFFIX = " total events/runs)";

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

    private JTextField playbackIterationTextField = null;

    private JLabel iterateTitleLabel = null;

    private JLabel totalRunsLabel = null;
    
    private boolean stillRunning = false;
    
    private PlaybackInfo playbackInfo = new PlaybackInfo();
    
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
        
        updateTotalRuns();
        
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
        
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addPlayBackEventListener(new PlayBackEventListener());
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
            }
        });
    }

    public String[] buildPlayBackRow(ReplayEvent playbackEvent) {
        String[] strings = new String[eventsListBox.getColumnCount()];

        // Object[] cols = { "Seq", "Site", "Who", "Event", "Id", "When", "State", "Details" };

        strings[0] = "" + playbackEvent.getSequenceId();
        strings[1] = Integer.toString(playbackEvent.getClientId().getSiteNumber());
        Site site = getContest().getSite(playbackEvent.getClientId().getSiteNumber());
        if (site != null) {
            String name = site.getDisplayName();
            strings[1] = Integer.toString(playbackEvent.getClientId().getSiteNumber()) + " " + stringElipsis(name, 11) + " ";
        }
        strings[2] = playbackEvent.getClientId().getName();
        strings[3] = playbackEvent.getAction().toString();
        strings[4] = Integer.toString(playbackEvent.getId());
        strings[5] = Long.toString(playbackEvent.getEventTime());
        strings[6] = "" + playbackEvent.getEventStatus();

        if (playbackEvent.getAction().equals(ReplayEvent.Action.RUN_SUBMIT)) {

            strings[7] = getDetails(playbackEvent);

        } else if (playbackEvent.getAction().equals(ReplayEvent.Action.RUN_JUDGEMENT)) {

            JudgementRecord judgementRecord = playbackEvent.getJudgementRecord();
            ElementId id = judgementRecord.getJudgementId();
            if (id != null) {
                Judgement judgement = getContest().getJudgement(id);
                strings[7] = judgement.getDisplayName();
            } else {
                strings[7] = "Undefined judgement: " + id;

            }
        } else {
            Arrays.fill(strings, "");
            strings[0] = "" + playbackEvent.getSequenceId();
            strings[3] = ReplayEvent.Action.UNDEFINED.toString();
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

    private String getDetails(ReplayEvent event) {

        Run run = event.getRun();

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
        ReplayEvent playbackEvent = new ReplayEvent(Action.UNDEFINED, clientId);

        playbackEvent.setSequenceId(eventsListBox.getRowCount());
        String[] row = buildPlayBackRow(playbackEvent);

        eventsListBox.addRow(row);
        eventsListBox.autoSizeAllColumns();
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
        
        if (edu.csus.ecs.pc2.core.model.ClientType.isAdmin(getContest().getClientId())) {
            playbackInfo.setSiteNumber(getContest().getSiteNumber());
            playbackInfo.setStarted(true);
            getController().startPlayback(playbackInfo);
            return;
        }
        getController().startPlayback(playbackInfo);
        getContest().startReplayPlaybackInfo(playbackInfo);

        if (eventsListBox.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No events defined");
            return;
        }

        final int currentEventNumber = playbackManager.getSequenceNumber();

        if (currentEventNumber > eventsListBox.getRowCount()) {
            JOptionPane.showMessageDialog(this, "All events executed");
            return;
        }

        String lastEventString = getStopEventNumberTextField().getText();
        
        int lastEventToRunTo = Integer.MAX_VALUE;
        if (lastEventString.trim().length() > 0) {
            lastEventToRunTo = Integer.parseInt(lastEventString);
        }
        
        String setIteratorCount = playbackIterationTextField.getText();
        int playbackIteratorMax = 1;
        if (setIteratorCount.length() > 0) {
            playbackIteratorMax = Integer.parseInt(setIteratorCount);
        }

        if (currentEventNumber == lastEventToRunTo) {
            JOptionPane.showMessageDialog(this, "Already before event " + lastEventToRunTo);
            return;
        }
        if (currentEventNumber > lastEventToRunTo) {
            JOptionPane.showMessageDialog(this, "Way after event " + lastEventToRunTo + " dude (at event " + (currentEventNumber - 1) + ")");
            return;
        }

        final int waitTime = Integer.parseInt(getTimeWarpTextField().getText());
        
        final int maxEventSetCount = playbackIteratorMax;

        if (lastEventToRunTo > getEventsListBox().getRowCount()) {
            lastEventToRunTo = getEventsListBox().getRowCount();
        }

        final int runToStep = lastEventToRunTo;
        
        setRunningButtons (true);

        new Thread(new Runnable() {

            public void run() {
                
                int startEventNumber = currentEventNumber;
                
                setStillRunning(true);

                for (int setIteratorCount = 0; setIteratorCount < maxEventSetCount && isStillRunning(); setIteratorCount++) {
                    
                    if (setIteratorCount > 0){
                        resetAllEventAndWait();
                        startEventNumber = 1;
                    }
                    
                    for (int i = startEventNumber; i <= runToStep && isStillRunning(); i++) {

                        final ReplayEvent playbackEvent = (ReplayEvent) eventsListBox.getKeys()[i - 1];
                        final int currentEventNumber = playbackManager.getSequenceNumber() + (setIteratorCount * runToStep);
                        
                        try {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    Integer setInfo = playbackManager.getSequenceNumber();
                                    if (maxEventSetCount > 1){
                                        setInfo = currentEventNumber;
                                    }
                                    currentEventLabel.setText("At event " + setInfo);
                                }
                            });

                            playbackManager.executeEvent(playbackEvent, getContest(), getController());

                            final int rowNumber = i;

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    final String[] row = buildPlayBackRow(playbackEvent);
                                    getEventsListBox().replaceRow(row, rowNumber - 1);
                                    getEventsListBox().autoSizeAllColumns();
                                }
                            });

                            if (waitTime > 0) {
                                Thread.sleep(waitTime);
                            }

                        } catch (Exception e) {
                            setStillRunning(false);
                            e.printStackTrace();
                        }
                        
                    }
                }
                
                setStillRunning(false);
                setRunningButtons (false);

            }
        }).start();
    }

    /**
     * Goes through list and rewinds all runs.
     */
    protected void resetAllEventAndWait() {
        
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
            getResetButton().setEnabled(getEventsListBox().getRowCount() > 0);
        }
    }

    public boolean isStillRunning() {
        return stillRunning;
    }
    
    public void setStillRunning(boolean stillRunning) {
        this.stillRunning = stillRunning;
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
        setStillRunning(false);
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
        
        int rowCount = getEventsListBox().getRowCount();

        ReplayEvent [] playbacks = new ReplayEvent[rowCount];
        Object [][] rowValues = new Object[rowCount][eventsListBox.getColumnCount()];

        for (int i = 0; i < rowCount; i++) {
            ReplayEvent playbackEvent = (ReplayEvent) eventsListBox.getKeys()[i];
            playbackEvent.setSequenceId(i+1);
            playbackEvent.setEventStatus(EventStatus.PENDING);
            playbacks[i] = playbackEvent;
            rowValues[i] = buildPlayBackRow(playbackEvent);
        }
        
        getEventsListBox().removeAllRows();
        getEventsListBox().addRows(rowValues, playbacks);
        
        playbackManager.rewind();
    }

    /**
     * This method initializes topPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPane() {
        if (topPane == null) {
            totalRunsLabel = new JLabel();
            totalRunsLabel.setText("(no runs)");
            totalRunsLabel.setBounds(new Rectangle(344, 85, 162, 24));
            iterateTitleLabel = new JLabel();
            iterateTitleLabel.setText("Number of times to load events");
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
            topPane.add(getPlaybackIterationTextField(), null);
            topPane.add(iterateTitleLabel, null);
            topPane.add(totalRunsLabel, null);
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
        
        int currentEventNumber = playbackManager.getSequenceNumber();
        
        if (currentEventNumber > eventsListBox.getRowCount()){
            JOptionPane.showMessageDialog(this, "All events executed");
            return;
        }
        
        ReplayEvent playbackEvent = (ReplayEvent) eventsListBox.getKeys()[currentEventNumber - 1];
        try {
            currentEventLabel.setText("At event " + playbackManager.getSequenceNumber());
            playbackManager.executeEvent(playbackEvent, getContest(), getController());

            String[] row = buildPlayBackRow(playbackEvent);

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

                ReplayEvent[] playbackEvents = playbackManager.loadPlayback(filename, getContest());
                if (playbackEvents == null || playbackEvents.length == 0) {
                    JOptionPane.showMessageDialog(this, "No events found in " + filename);
                } else {
                    
                    playbackInfo.setSiteNumber(getContest().getSiteNumber());
                    playbackInfo.setFilename(filename);
                    playbackInfo.setPlaybackList(playbackEvents);
                    
                    for (ReplayEvent playbackEvent : playbackEvents) {
                        playbackEvent.setSequenceId(eventsListBox.getRowCount()+1);
                        String[] row = buildPlayBackRow(playbackEvent);
                        getEventsListBox().addRow(row, playbackEvent);
                    }
                    getEventsListBox().autoSizeAllColumns();
                    setRunningButtons(false);
                    JOptionPane.showMessageDialog(this, "Loaded " + playbackEvents.length + " events from " + filename);

                }
            }
        } catch (FileNotFoundException notFound) {
            JOptionPane.showMessageDialog(this, "No such file: " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to load file: " + filename+ " "+e.getMessage());
            e.printStackTrace();
        }
        
        updateTotalRuns();
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
     * This method initializes playbackIterationTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPlaybackIterationTextField() {
        if (playbackIterationTextField == null) {
            playbackIterationTextField = new JTextField();
            playbackIterationTextField.setBounds(new Rectangle(252, 85, 73, 24));
            playbackIterationTextField.setDocument(new IntegerDocument());
            playbackIterationTextField.setText("1");
            playbackIterationTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    super.keyReleased(e);
                    updateTotalRuns();
                }
            });
        }
        return playbackIterationTextField;
    }

    protected void updateTotalRuns() {
        int loadedCount = getEventsListBox().getRowCount();
        String message = "(0 " + RUN_MESSAGE_SUFFIX;
        String countString = getPlaybackIterationTextField().getText();
        int iterationCount = 0;
        if (countString.length() > 0) {
            iterationCount = Integer.parseInt(countString);
        }
        int numRuns = loadedCount * iterationCount;
        if (numRuns > 0) {
            message = "(" + numRuns + RUN_MESSAGE_SUFFIX;
        }
        totalRunsLabel.setText(message);
    }
    
    private void updateGUIperPermissions() {
   
        setRunningButtons(isStillRunning());
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

        public void playbackChanged(PlayBackEvent playBackEvent) {
            System.out.println("PlayBackEvent " + playBackEvent.getAction() + " " + playBackEvent.getPlaybackInfo());
        }

        public void playbackRefreshAll(PlayBackEvent playBackEvent) {
            playbackChanged(playBackEvent);
        }

        public void playbackEvent(PlayBackEvent playBackEvent) {
            playbackChanged(playBackEvent);
        }

        public void playbackAdded(PlayBackEvent playBackEvent) {
            playbackChanged(playBackEvent);
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
