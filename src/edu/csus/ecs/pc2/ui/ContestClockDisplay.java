package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;

/**
 * Maintains a number of contest clock displays/labels.
 * <P>
 * This provides methods to dynamically update JLabels with contest 
 * remaining or elapsed times.  This also will update a single JFrame
 * title (when frame is minimized).
 * <P>
 * 
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ContestClockDisplay implements ActionListener, UIPlugin {

    public static final String SVN_ID = "$Id$";

    /**
     * Labels to be updated with contest time.
     */
    private Vector<JLabel> elapsedTimeLabelList = new Vector<JLabel>();

    private Vector<JLabel> remainingtimeLabelList = new Vector<JLabel>();

    private Hashtable<Integer, Vector<JLabel>> sitesElapsedTimeLabelList = new Hashtable<Integer, Vector<JLabel>>();

    private Hashtable<Integer, Vector<JLabel>> sitesRemainingtimeLabelList = new Hashtable<Integer, Vector<JLabel>>();

    private Hashtable<Integer, ContestTime> contestTimes = new Hashtable<Integer, ContestTime>();

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
     * @author pc2@ecs.csus.edu
     */

    private IController controller;

    @SuppressWarnings("unused")
    private IContest contest;
    
    public enum DisplayTimes {
        /**
         * Show elapsed time.
         */
        ELAPSED_TIME,
        /**
         * Show remaining time.
         */
        REMAINING_TIME
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
        if (whichTime == DisplayTimes.ELAPSED_TIME) {
            Vector<JLabel> list = sitesElapsedTimeLabelList.get(new Integer(siteNumber));
            if (list == null) {
                list = new Vector<JLabel>();
            }
            list.addElement(labelToUpdate);
            sitesElapsedTimeLabelList.put(new Integer(siteNumber), list);
        } else {

            Vector<JLabel> list = sitesRemainingtimeLabelList.get(new Integer(siteNumber));

            if (list == null) {
                list = new Vector<JLabel>();
            }
            list.addElement(labelToUpdate);
            sitesRemainingtimeLabelList.put(new Integer(siteNumber), list);
        }

        updateTimeLabels();
        
    }

    public void removeLabelFromAllUpdateLists(JLabel labelToUpdate) {
        try {
            Enumeration enumeration = contestTimes.keys();

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

                for (int i = 0; i < list.size(); i++) {
                    list.elementAt(i).setForeground(Color.BLACK);
                }

                list = sitesRemainingtimeLabelList.get(new Integer(siteNumber));
                for (int i = 0; i < list.size(); i++) {
                    list.elementAt(i).setForeground(Color.BLACK);
                }
            }
        });
    }

    /**
     * Stop the display updating.
     * 
     */
    private void stopClockDisplay(int siteNumber) {
        timer.stop();

        Vector<JLabel> list = sitesElapsedTimeLabelList.get(new Integer(siteNumber));
        for (int i = 0; i < list.size(); i++) {
            list.elementAt(i).setForeground(Color.RED);
        }
        list = sitesRemainingtimeLabelList.get(new Integer(siteNumber));
        for (int i = 0; i < list.size(); i++) {
            list.elementAt(i).setForeground(Color.RED);
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
                    String clockText = "There no time like the present";

                    if (contestTime == null) {
                        clockText = "***";
                    } else {
                        if (contestTime.isContestRunning()) {
                            if (teamDisplayMode && contestTime.getRemainingSecs() < 120) {
                                clockText = "< 2 mins";
                            } else {
                                if (teamDisplayMode) {
                                    clockText = contestTime.getRemainingMinStr();

                                } else {
                                    long secsLeft = contestTime.getRemainingSecs();
                                    if (secsLeft < 60 && secsLeft > -1) {
                                        clockText = secsLeft + " seconds ";
                                    } else {
                                        clockText = contestTime.getRemainingTimeStr();
                                    }
                                }
                            }

                        } else {
                            if (teamDisplayMode) {
                                clockText = "STOPPED";
                            } else {
                                clockText = contestTime.getRemainingTimeStr();
                            }
                        }
                    }

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
                    e.printStackTrace(); // TODO delete this line
                }
            }
        } );

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

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
        
        contest.addContestTimeListener(new ContestTimeListenerImplementation());
        
    }

    public String getPluginTitle() {
        return "Contest Clock Display";
    }
    
    public class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            fireClockStateChange(event.getContestTime(), event.getContestTime().getSiteNumber());
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            // TODO Auto-generated method stub
            
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
}  //  @jve:decl-index=0:visual-constraint="96,54"
