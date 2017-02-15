package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.DateDifferizer;
import edu.csus.ecs.pc2.core.util.DateDifferizer.DateFormat;

/**
 * Maintains a number of contest clock displays/labels.
 * <P>
 * For each JLabel added will dynamically update the text
 * with a countdown timer text string.   The label will
 * also dynamically update when contest/clock state config
 * changes.   For example, if the contest length is changed
 * then the remaining or elapsed time will automatically
 * reflect that change.
 * <P>
 * Add a label and the format of the time {@link #addLabeltoUpdateList(JLabel, DisplayTimes, int)} 
 * <P>
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestClockDisplay implements ActionListener, UIPlugin {

    private static final long serialVersionUID = 8137635697344335832L;

    private static final String NO_SCHEDULE_START_TIME = "No scheduled start";

    /**
     * Labels to be updated with contest time.
     */
    private Vector<JLabel> elapsedTimeLabelList = new Vector<JLabel>();

    private Vector<JLabel> remainingtimeLabelList = new Vector<JLabel>();

    /**
     * List of elapsed JLabels to update
     */
    private Hashtable<Integer, Vector<JLabel>> sitesElapsedTimeLabelList = new Hashtable<Integer, Vector<JLabel>>();

    /**
     * List of remaining time JLabels to update
     */
    private Hashtable<Integer, Vector<JLabel>> sitesRemainingtimeLabelList = new Hashtable<Integer, Vector<JLabel>>();
    
    /**
     * List of scheduled start time JLabels to update.
     */
    private List<JLabel> scheduledStartTimeLabelList = new ArrayList<>();

    /**
     * 
     */
    private List<JLabel> schedAndRemainingTimeLabelList = new ArrayList<>();

    private Hashtable<Integer, ContestTime> contestTimes = new Hashtable<Integer, ContestTime>();
    
    /**
     * The date/time when the contest is scheduled (intended) to start.
     * This value is null (undefined) if no scheduled start time has been set.
     * This value ONLY applies BEFORE THE CONTEST STARTS; once 
     * any "start contest" operation (e.g. pushing the "Start Button") has occurred,
     * this value no longer has meaning.
     */
    private GregorianCalendar scheduledStartTime = null ;
    
    private Timer timer = new Timer(500, this);

    private JFrame clientFrame = null;

    /**
     * Special display for team.
     * 
     * Primarily if there are < 2 minutes in the contest or
     * beyond, will display < 2 minutes.
     */
    private boolean teamDisplayMode = false;

    private boolean alwaysUpdateTitle = false;

    private DisplayTimes titleTimeToDisplay = DisplayTimes.REMAINING_TIME;

    private String savedFrameTitle = null;

    private boolean frameTitleSet = false;

    private Integer localSiteNumber = new Integer(0);

    private Log log = null;
    /**
     * Ways to display contest time.
     * 
     */

    private IInternalController controller;

    private IInternalContest contest;
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     * 
     */
    public enum DisplayTimes {
        /**
         * Show elapsed time.
         */
        ELAPSED_TIME,
        /**
         * Show remaining time.
         */
        REMAINING_TIME,
        /**
         * Show time to start of contest, per scheduled start time.
         */
        TO_SCHEDULED_START_TIME,
        /**
         * Show countdown to scheduled then remaining when contest starts.
         */
        SCHEDULED_THEN_REMAINING_TIME,
    };


    /**
     * Update JLabels with contest time.
     * 
     * If ContestTime.isRunning() is true, will start countdown timer, set label color to Color.BLACK. <br>
     * When contestTimer is not running, will stop countdown and set label color to Color.RED. <br>
     * isTeamDisplay == true, if clock is stopped shows STOPPED in label, if &lt; 2 minutes remaining will display "&lt; 2 mins".
     * <br>
     * isTeamDisplay == false, always shows remaining time.
     * 
     * 
     * @param contestTime
     *            contest time (running and values)
     * @param isTeamDisplay
     *            display for team
     * @param clientFrame
     *            frame to put time in when minimized
     */
    public ContestClockDisplay(Log log, ContestTime contestTime, int localSiteNum, boolean isTeamDisplay, JFrame clientFrame) {
        super();
        this.log = log;

        localSiteNumber = localSiteNum;
        contestTimes.put(localSiteNumber, contestTime);
        sitesElapsedTimeLabelList.put(localSiteNumber, elapsedTimeLabelList);
        sitesRemainingtimeLabelList.put(localSiteNumber, remainingtimeLabelList);

        setClientFrame (clientFrame);
        
        this.teamDisplayMode = isTeamDisplay;
        this.clientFrame = clientFrame;
        fireClockStateChange(contestTime);

    }
    
    
    /**
     * Update clock times with scheduled information.
     * 
     * @param contestInformation
     */
    public void fireClockStateChange(final ContestInformation contestInformation) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setScheduledStartTime(contestInformation);
                updateTimeLabels();
            }
        });
        
    }


    /**
     * Update clock display using contestTime.
     * 
     * Uses contestTime state (running or not) to determine whether to update clock every second.
     * 
     * The display is different for {@link #teamDisplayMode}.
     * 
     * @param contestTime
     */
    public void fireClockStateChange(final ContestTime contestTime) {
        
        contestTimes.put(contestTime.getSiteNumber(), contestTime);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (contestTime.isContestRunning()) {
                    startClockDisplay(contestTime.getSiteNumber());
                } else {
                    stopClockDisplay(contestTime.getSiteNumber());
                }

                updateTimeLabels();
            }
        });

    }

    private void updateTimeLabels() {

        Enumeration<Integer> enumeration = contestTimes.keys();

        while (enumeration.hasMoreElements()) {
            Integer element = (Integer) enumeration.nextElement();
            updateTimeLabel(element.intValue());

        }
        
        updateScheduledStartLabels();
        
    }

    /**
     * Update clock display using contestTime.
     * 
     * Uses contestTime state (running or not) to determine whether to update clock every second.
     * 
     * The display is different for {@link #teamDisplayMode}.
     * 
     * @param contestTime
     * @param siteNumber
     */
    private void fireClockStateChange(ContestTime contestTime, final int siteNumber) {

        contestTimes.put(new Integer(siteNumber), contestTime);

        final ContestTime theContestTime = contestTimes.get(new Integer(siteNumber));

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (theContestTime.isContestRunning()) {
                    startClockDisplay(siteNumber);
                } else {
                    stopClockDisplay(siteNumber);
                }

                updateTimeLabels();
            }
        });
    }
    
    

    /**
     * Add a label to be the update list.
     * <P>
     * This adds a label to update
     * 
     * @param labelToUpdate the label to update
     * @param whichTime the remaining or elapsed time form
     * @param siteNumber the site number for this contest time.
     */
    public void addLabeltoUpdateList(JLabel labelToUpdate, DisplayTimes whichTime, int siteNumber) {
        
        Vector<JLabel> list = null;
        
        switch (whichTime) {
            case ELAPSED_TIME:
                list = sitesElapsedTimeLabelList.get(new Integer(siteNumber));
                if (list == null) {
                    list = new Vector<JLabel>();
                }
                list.addElement(labelToUpdate);
                sitesElapsedTimeLabelList.put(new Integer(siteNumber), list);
                break;
                
            case REMAINING_TIME:
                
                list = sitesRemainingtimeLabelList.get(new Integer(siteNumber));
                
                if (list == null) {
                    list = new Vector<JLabel>();
                }
                list.addElement(labelToUpdate);
                sitesRemainingtimeLabelList.put(new Integer(siteNumber), list);
                break;
                
            case TO_SCHEDULED_START_TIME:
                
                scheduledStartTimeLabelList.add(labelToUpdate);
                break;
                
            case SCHEDULED_THEN_REMAINING_TIME:
                schedAndRemainingTimeLabelList.add(labelToUpdate);
                break;

            default:
                break;
        }

        updateTimeLabels();
        
    }

    public void removeLabelFromAllUpdateLists(JLabel labelToUpdate) {
        try {
            Enumeration<Integer> enumeration = contestTimes.keys();

            while (enumeration.hasMoreElements()) {
                Integer element = (Integer) enumeration.nextElement();
                removeLabelFromUpdateList(labelToUpdate, element.intValue());
            }
        } catch (Exception e) {
            System.out.println("removeLabelFromAllUpdateLists Exception ");
        }
    }

    public void removeLabelFromUpdateList(JLabel labelToUpdate, int siteNumber) {
        Vector<JLabel> list = null;

        try {
            list = sitesElapsedTimeLabelList.get(siteNumber);
            list.removeElement(labelToUpdate);
            sitesElapsedTimeLabelList.put(siteNumber, list);
        } catch (Exception e) {
            log.config("removeLabelFromUpdateList: Elapsed attempted to remove label that was not there");
        }

        try {
            list = sitesRemainingtimeLabelList.get(siteNumber);
            list.removeElement(labelToUpdate);
            sitesRemainingtimeLabelList.put(siteNumber, list);
        } catch (Exception e) {
            log.config("removeLabelFromUpdateList: Remaining attempted to remove label that was not there");
        }

    }

    /**
     * Start the clock updating.
     * 
     */
    private void startClockDisplay(final int siteNumber) {
        // public void schedule(TimerTask task, long delay, long period)
        // delay and period are miliseconds.

        timer.start();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Vector<JLabel> list = sitesElapsedTimeLabelList.get(siteNumber);

                if (list == null) {
                    log.fine("sitesElapsedTimeLabelList for "+ siteNumber+ " is null.");
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        list.elementAt(i).setForeground(Color.BLACK);
                    }
                }

                list = sitesRemainingtimeLabelList.get(new Integer(siteNumber));
                if (list == null) {
                    log.fine("sitesRemainingtimeLabelList for "+ siteNumber+ " is null.");
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        list.elementAt(i).setForeground(Color.BLACK);
                    }
                }
                
                for (JLabel jLabel : scheduledStartTimeLabelList) {
                    jLabel.setForeground(Color.BLACK);
                }
            }
        });
    }

    /**
     * Stop the display updating.
     * 
     */
    private void stopClockDisplay(int siteNumber) {
        if (scheduledStartTime == null){
            timer.stop();
        }

        Vector<JLabel> list = sitesElapsedTimeLabelList.get(new Integer(siteNumber));
        if (list == null) {
            log.fine("sitesElapsedTimeLabelList for " + siteNumber + " is null");
        } else {
            for (int i = 0; i < list.size(); i++) {
                list.elementAt(i).setForeground(Color.RED);
            }
        }
        list = sitesRemainingtimeLabelList.get(new Integer(siteNumber));
        if (list == null) {
            log.fine("sitesRemainingtimeLabelList for " + siteNumber + " is null");
        } else {
            for (int i = 0; i < list.size(); i++) {
                list.elementAt(i).setForeground(Color.RED);
            }
        }
    }

    /**
     * Update the label (display).
     * 
     */
    private void updateTimeLabel(final int siteNumber) {
        final ContestTime contestTime = contestTimes.get(new Integer(siteNumber));

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {
                    String clockText = getRemainingTimeClockText(contestTime);

                    Vector<JLabel> list = sitesElapsedTimeLabelList.get(new Integer(siteNumber));

                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            list.elementAt(i).setText(contestTime.getElapsedTimeStr());
                        }
                    }

                    list = sitesRemainingtimeLabelList.get(new Integer(siteNumber));
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            list.elementAt(i).setText(clockText);
                        }
                    }
                    
                    updateScheduledStartLabels();

                    if (siteNumber == localSiteNumber.intValue() && clientFrame != null) { // only update Title bar if the local site
                        if ((clientFrame.getState() == JFrame.ICONIFIED) || (isAlwaysUpdateTitle())) {
                            if (getTitleTimeToDisplay() == DisplayTimes.REMAINING_TIME) {
                                clientFrame.setTitle(clockText + " " + savedFrameTitle);
                            } else {
                                clientFrame.setTitle(contestTime.getElapsedTimeStr() + " " + savedFrameTitle);
                            }
                            frameTitleSet = true;
                        } else if (frameTitleSet) {
                            frameTitleSet = false;
                            clientFrame.setTitle(savedFrameTitle);
                        }
                    }
                } catch (Exception e) {
                    log.throwing("Something in here", "Exception in clock label display method", e);
                }
            }
        } );

    }

    /**
     * Adjust contest time display if after end of contest.
     * 
     * If input time, ex remaining time, is -HHM:MM:SS will
     * change string to +HH:MMSS, else returns string unchanged.  
     * 
     * @param string
     * @return +HH:MM:SS if input is -HH:MM:SS, else returns string unchanged.
     */
    protected String adjustForPostContest(String string) {
        if (string.startsWith("-")){
            return string.replace('-', '+');
        } else {
            return string;
        }
    }


    /**
     * Update Scheduled Time labels.
     */
    protected void updateScheduledStartLabels() {

        if (scheduledStartTimeLabelList.size() > 0) {

            String text = NO_SCHEDULE_START_TIME;
            String hint = "Still no scheduled start";

            if (scheduledStartTime != null) {

                text = getScheduledTimeClockText();
                hint = getScheduledTimeClockHint();
            }

            for (JLabel jLabel : scheduledStartTimeLabelList) {
                jLabel.setText(text);
                jLabel.setToolTipText(hint);
            }
        }

        if (schedAndRemainingTimeLabelList.size() > 0) {

//            String hint = "Still no scheduled start";

            ContestTime contestTime = contest.getContestTime();

            String text = getScheduleOrRemainingTime(contestTime);

            for (JLabel jLabel : schedAndRemainingTimeLabelList) {
                jLabel.setText(text);
                jLabel.setToolTipText(text);
            }
        }
    }

    public String getScheduleOrRemainingTime(ContestTime contestTime) {
        
        String text = NO_SCHEDULE_START_TIME; 
        
        boolean showRemainingTime = true;
        
        if (scheduledStartTime  != null && ! contestTime.isContestStarted()){
            showRemainingTime = false;
        }
        
        if (showRemainingTime){
            
            /**
             * Show Remaining time count down. 
             */
            text = contestTime.getRemainingTimeStr();
            text = adjustForPostContest(text);
            
        } else {
            /**
             * Show schedule start count down
             */
            Date now = GregorianCalendar.getInstance().getTime();
            DateDifferizer differizer = new DateDifferizer(now, scheduledStartTime.getTime());
            differizer.setFormat(DateFormat.COUNT_DOWN);
            text = differizer.toString();
        }
        
        return text;
    }


    public String getScheduledTimeClockHint() {
        Date now = GregorianCalendar.getInstance().getTime();
        DateDifferizer differizer = new DateDifferizer(now, scheduledStartTime.getTime());
        String hint = differizer.formatTime(DateFormat.LONG_FORMAT);
        return hint;
    }

    public String getScheduledTimeClockText() {
        Date now = GregorianCalendar.getInstance().getTime();
        DateDifferizer differizer = new DateDifferizer(now, scheduledStartTime.getTime());
        differizer.setFormat(DateFormat.COUNT_DOWN);
        String text = differizer.toString();
        return text;
    }

    public void actionPerformed(ActionEvent arg0) {
        ContestTime contestTime = contestTimes.get(localSiteNumber);
        fireClockStateChange(contestTime);
    }

    /**
     * Sets the default site and contest time.
     * 
     * @param contestTime
     * @param siteNumber
     */
    public void setContestTime(ContestTime contestTime, int siteNumber) {
        contestTimes.put(new Integer(siteNumber), contestTime);
    }

    public boolean isAlwaysUpdateTitle() {
        return alwaysUpdateTitle;
    }

    public void setAlwaysUpdateTitle(boolean alwaysUpdateTitle) {
        this.alwaysUpdateTitle = alwaysUpdateTitle;
    }

    public DisplayTimes getTitleTimeToDisplay() {
        return titleTimeToDisplay;
    }

    public void setTitleTimeToDisplay(DisplayTimes titleTimeToDisplay) {
        this.titleTimeToDisplay = titleTimeToDisplay;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
        
        contest.addContestTimeListener(new ContestTimeListenerImplementation());
        contest.addContestInformationListener(new ContestInformationListenerImplementation());
        
        setScheduledStartTime(inContest.getContestInformation());
    }

    public String getPluginTitle() {
        return "Contest Clock Display";
    }
    
    
    /**
     * Listener.
     * 
     * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
     */
    public class ContestInformationListenerImplementation implements IContestInformationListener {

        @Override
        public void contestInformationAdded(ContestInformationEvent event) {
            fireClockStateChange(event.getContestInformation());
            
        }

        @Override
        public void contestInformationChanged(ContestInformationEvent event) {
            fireClockStateChange(event.getContestInformation());
            
        }

        @Override
        public void contestInformationRemoved(ContestInformationEvent event) {
            fireClockStateChange(event.getContestInformation());
            
        }

        @Override
        public void contestInformationRefreshAll(ContestInformationEvent event) {
            fireClockStateChange(event.getContestInformation());
            
        }

        @Override
        public void finalizeDataChanged(ContestInformationEvent event) {
            fireClockStateChange(event.getContestInformation());
            
        }
        
    }
    
    
    /**
     * Implementor.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            fireClockStateChange(event.getContestTime(), event.getContestTime().getSiteNumber());
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            // no action 
            
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            fireClockStateChange(event.getContestTime(), event.getContestTime().getSiteNumber());
        }

        public void contestStarted(ContestTimeEvent event) {
            fireClockStateChange(event.getContestTime(), event.getContestTime().getSiteNumber());
            
        }

        public void contestStopped(ContestTimeEvent event) {
            fireClockStateChange(event.getContestTime(), event.getContestTime().getSiteNumber());
            
        }

        public void refreshAll(ContestTimeEvent event) {
            fireClockStateChange(event.getContestTime(), event.getContestTime().getSiteNumber());
        }
        
        /** This method exists to support differentiation between manual and automatic starts,
         * in the event this is desired in the future.
         * Currently it just delegates the handling to the contestStarted() method.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
        }
    }

    /**
     * Set Client Frame that when minimized changes to a countdown timer.
     * 
     * This uses the default contest time when it updates the title.
     * The original frame title is saved, when frame is minimized will show
     * contest time, when non-minimized will show the saved frame title.
     * <P>
     * Uses the contest time set using {@link #setContestTime(ContestTime, int)}. 
     * 
     * @param clientFrame
     */
    public void setClientFrame(JFrame clientFrame) {
        if (this.clientFrame == null && clientFrame != null){
            this.clientFrame = clientFrame;
            savedFrameTitle = clientFrame.getTitle();
        }
    }

    public GregorianCalendar getScheduledStartTime() {
        return scheduledStartTime;
    }
    
    public void setScheduledStartTime(GregorianCalendar scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
        if (scheduledStartTime != null){
            timer.start();
        }
    }
    
    public void setScheduledStartTime(ContestInformation info) {
        if (info != null){
            setScheduledStartTime(info.getScheduledStartTime());
        }
    }

    /**
     * Get remaining time text.
     * @param contestTime
     * @return
     */
    public String getRemainingTimeClockText(ContestTime contestTime) {

        String clockText = "There no time like the present";

        if (contestTime == null) {
            clockText = "***";
        } else {
            if (contestTime.isContestRunning()) {
                if (teamDisplayMode && contestTime.getRemainingSecs() < 120) {
                    clockText = "< 2 mins";
                } else {
                    if (teamDisplayMode) {
                        /**
                         * This will only be displayed whent there are more than 120 seconds left in the contest, so no handling of scheduled time is needed.
                         */
                        // Since this is a team there will be no
                        clockText = contestTime.getRemainingMinStr();

                    } else {
                        long secsLeft = contestTime.getRemainingSecs();
                        if (secsLeft < 60 && secsLeft > -1) {
                            /**
                             * Show time in seconds just before contest ends.
                             */
                            clockText = secsLeft + " seconds ";
                        } else {
                            clockText = contestTime.getRemainingTimeStr();
                            clockText = adjustForPostContest(clockText);
                        }
                    }
                }

            } else {
                if (teamDisplayMode) {
                    clockText = "STOPPED";
                } else {
                    clockText = contestTime.getRemainingTimeStr();
                    clockText = adjustForPostContest(clockText);
                }
            }
        }
        return clockText;

    }
    public JFrame getClientFrame() {
        return clientFrame;
    }
}  //  @jve:decl-index=0:visual-constraint="96,54"

