package edu.csus.ecs.pc2.core.model;

import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * InternalContest Time Information.
 * 
 * Methods used to access contest time as well as start and stop contest time. <br>
 * Start clock: {@link #startContestClock()}. <br>
 * Stop contest clock {@link #stopContestClock()}. <br>
 * <br>
 * Get remaining time (formatted) {@link #getRemainingTimeStr()} <br>
 * <br>
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class ContestTime implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = 6967329985187819728L;

    public static final String SVN_ID = "$Id$";

    /**
     * Default contest length.
     */
    public static final long DEFAULT_CONTEST_LENGTH_SECONDS = 18000; // 5 * 60 * 60

    /**
     * Resume time, used in calculating elapsed time.
     * 
     */
    private GregorianCalendar resumeTime = null;

    /**
     * serverTransmitTime is set by the server and is used to calculate clock differential between client & server clocks
     */
    private GregorianCalendar serverTransmitTime = null;

    private long localClockOffset = 0;

    private boolean haltContestAtTimeZero = false;

    /**
     * Elapsed seconds since start of contest.
     */
    private long elapsedSecs = 0;

    private long contestLengthSecs = DEFAULT_CONTEST_LENGTH_SECONDS;

    /**
     * Is contest clock running (counting down) ?
     */
    private boolean contestRunning = false;

    private int siteNumber;

    private ElementId elementId;

    public ContestTime() {
        this("InternalContest Time");
    }

    /**
     * Create contest time with given site.
     * 
     * @param siteNumber
     */
    public ContestTime(int siteNumber) {
        this("InternalContest Time Site " + siteNumber);
        this.siteNumber = siteNumber;
    }

    private ContestTime(String idString) {
        elementId = new ElementId(idString);
    }

    /**
     * 
     * @return contest length in minutes.
     */
    public long getConestLengthMins() {
        return contestLengthSecs / 60;
    }

    /**
     * 
     * @return contest length formatted.
     */
    public String getContestLengthStr() {
        return formatTime(contestLengthSecs);
    }

    /**
     * 
     * @return true if contest clock running, false if clock stopped.
     */
    public boolean isContestRunning() {
        return contestRunning;
    }

    /**
     * 
     * @see #formatTime(long)
     * @return elapsed time formatted
     */
    public String getElapsedTimeStr() {
        return formatTime(getElapsedSecs());
    }

    /**
     * @see #formatTime(long)
     * @return returns remaining time formatted.
     */
    public String getRemainingTimeStr() {
        return formatTime(getRemainingSecs());
    }

    /**
     * @see #formatTime(long)
     * @return returns remaining time formatted, HH:MM
     */
    public String getRemainingMinStr() {
        String timeStr = formatTime(getRemainingSecs());
        int lastIndex = timeStr.lastIndexOf(":");
        if (lastIndex != -1) {
            return timeStr.substring(0, lastIndex);
        } else {
            return timeStr;
        }
    }

    /**
     * Format the input seconds in the form HH:MM:SS.
     * 
     * @param seconds
     * @return formatted string in form HH:MM:SS
     */
    public static String formatTime(long seconds) {

        boolean negative = seconds < 0;

        if (negative) {
            seconds = seconds * -1; // absolute value it ..
        }

        long hours = seconds / 3600;
        long mins = (seconds / 60) % 60;
        long secs = (seconds % 60);

        String hourStr = new Long(hours).toString();
        // if (hours < 10)
        // hourStr = '0' + hourStr;

        String minStr = new Long(mins).toString();
        if (mins < 10) {
            minStr = '0' + minStr;
        }

        String secStr = new Long(secs).toString();
        if (secs < 10) {
            secStr = '0' + secStr;
        }

        if (negative) {
            hourStr = "-" + hourStr;
        }

        return (hourStr + ':' + minStr + ':' + secStr);
    }

    public long getContestLengthSecs() {
        return contestLengthSecs;
    }

    /**
     * @return elapsed time in minute.
     */
    public long getElapsedMins() {
        return getElapsedSecs() / 60;
    }

    public long getElapsedSecs() {
        return elapsedSecs + secsSinceContestStart();
    }

    /**
     * Get minutes since contest start.
     * 
     * @return minutes since start of contest.
     */
    public long getElapsedTime() {
        return getElapsedMins();
    }

    /**
     * 
     * @return remainging seconds from contest clock.
     */
    public long getRemainingSecs() {
        // compute remaining time.
        return contestLengthSecs - (elapsedSecs + secsSinceContestStart());
    }

    /**
     * 
     * @return halt the contest at time zero ?
     */
    public boolean isHaltContestAtTimeZero() {
        return haltContestAtTimeZero;
    }

    /**
     * 
     * @return true if remaining seconds <= 0, false if more time left.
     */
    public boolean isPastEndOfContest() {

        return getRemainingSecs() <= 0;
    }

    /**
     * 
     * @see #formatTime(long)
     * @return formatted remaining time
     */
    public String remTimeStr() {
        long secsLeft = getRemainingSecs();
        return formatTime(secsLeft);
    }

    private long secsSinceContestStart() {
        if (contestRunning) {
            TimeZone tz = TimeZone.getTimeZone("GMT");
            GregorianCalendar cal = new GregorianCalendar(tz);

            long milliDiff = cal.getTime().getTime() - resumeTime.getTime().getTime();
            long totalSeconds = milliDiff / 1000;
            return totalSeconds - localClockOffset;
        } else {
            return 0;
        }
    }

    public void setContestLengthSecs(long newSecs) {
        contestLengthSecs = newSecs;
    }

    public void setElapsedMins(long minutes) {
        elapsedSecs = minutes * 60;
    }

    public void setElapsedSecs(long eSecs) {
        elapsedSecs = eSecs;
    }

    public void setHaltContestAtTimeZero(boolean newHaltContestAtTimeZero) {
        haltContestAtTimeZero = newHaltContestAtTimeZero;
    }

    public void setRemainingSecs(long remSecs) {
        setElapsedSecs(contestLengthSecs - remSecs);
    }

    /**
     * Sets start time for contest to local machine's time.
     * 
     */
    public void forceContestStartTimeResync() {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        resumeTime = new GregorianCalendar(tz);
    }

    public void startContestClock() {
        if (!contestRunning) {
            forceContestStartTimeResync();
            contestRunning = true;
        }
    }

    /**
     * Stop contest clock.
     * 
     */
    public void stopContestClock() {
        if (contestRunning) {
            elapsedSecs = elapsedSecs + secsSinceContestStart();
            contestRunning = false;
        }
    }



    /**
     * @return Returns the resumeTime.
     */
    public GregorianCalendar getResumeTime() {
        return resumeTime;
    }

    /**
     * @param startTime
     *            The resumeTime to set.
     */
    public void setResumeTime(GregorianCalendar startTime) {
        this.resumeTime = startTime;
    }

    /**
     * Convert a String of format 00:00:00 to a long
     * 
     * @param intTime
     * @return the time
     */
    public long convertStringToLong(String intTime) {

        StringTokenizer stringTokenizer = new StringTokenizer(intTime, ":");

        String s1 = stringTokenizer.nextToken();
        String s2 = stringTokenizer.nextToken();
        String s3 = stringTokenizer.nextToken();

        long hh = Long.parseLong(s1);
        long mm = Long.parseLong(s2);
        long ss = Long.parseLong(s3);

        long totalSeconds = 0;
        if (hh != -1) {
            totalSeconds = hh;
        }
        if (mm != -1) {
            totalSeconds = (totalSeconds * 60) + mm;
        }
        if (ss != -1) {
            totalSeconds = (totalSeconds * 60) + ss;
        }

        if (hh == -1 || mm == -1 || ss == -1) {
            return -1;
        }

        return totalSeconds;
    }

    public long getLocalClockOffset() {
        return localClockOffset;
    }

    public void setLocalClockOffset(long localClockOffset) {
        this.localClockOffset = localClockOffset;
    }

    public GregorianCalendar getServerTransmitTime() {
        return serverTransmitTime;
    }

    /**
     * Set serverTransmitTime to local Time.
     * 
     * Should be called by the server before Transmitting a ContestTime packet. The serverTransmitTime is used to calculate the
     * localClockOffset on client machines.
     */
    public void setServerTransmitTime() {
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);

        this.serverTransmitTime = gregorianCalendar;
    }

    /**
     * Calculate Local Clock Offset.
     * 
     * Should be callaed by the client after a ContestTime packet is received to adjust the local clock.
     * 
     */
    public void calculateLocalClockOffset() {
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);

        calculateLocalClockOffset(gregorianCalendar);
    }

    public void calculateLocalClockOffset(GregorianCalendar localClock) {

        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);

        long milliDiff = gregorianCalendar.getTime().getTime() - serverTransmitTime.getTime().getTime();

        localClockOffset = milliDiff / 1000;
    }

    public ElementId getElementId() {
        return elementId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;

    }
}
