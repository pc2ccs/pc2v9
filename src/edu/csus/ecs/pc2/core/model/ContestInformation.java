package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import edu.csus.ecs.pc2.core.DateUtilities;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;

/**
 * Contest-wide Information/settings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestInformation implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -7333255582657988200L;

    /**
     * Default scoreboard freeze minutes
     */
    public static final int DEFAULT_FREEZE_MINUTES = 60;

    private String contestTitle = "Programming Contest";

    private String contestURL;
    
    private TeamDisplayMask teamDisplayMode = TeamDisplayMask.LOGIN_NAME_ONLY;

    private String judgesDefaultAnswer = "No response, read problem statement";
    
    private boolean preliminaryJudgementsUsedByBoard = false;
    private boolean preliminaryJudgementsTriggerNotifications = false;
    
    private boolean sendAdditionalRunStatusInformation = false;
    
    private String judgeCDPBasePath = null;
    
    private String adminCDPBasePath = null;
    
    /**
     * Test mode allow run submission with override elapsed time.
     * 
     */
    private boolean ccsTestMode = false;
    
    /**
     * Max output file size.
     */
    private long maxFileSize = 512000;

    /**
     * This is a list of the judgement notification end of contest control settings.
     * 
     */
    private JudgementNotificationsList judgementNotificationsList = new JudgementNotificationsList();
    
    private String externalYamlPath = null;
    
    private String rsiCommand = null;
    
    private int lastRunNumberSubmitted = 0;

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum TeamDisplayMask {
        /**
         * Show no information, just ***
         */
        NONE,
        /**
         * Show Login name, teamN.
         */
        LOGIN_NAME_ONLY,
        /**
         * Show display name, Johns Hopkins Team 1.
         */
        DISPLAY_NAME_ONLY,
        /**
         * name and number, teamN  Johns Hopkins Team 1. 
         */
        NUMBERS_AND_NAME,
        /**
         * Show alias,  shows teamM for team N.
         * This method uses an alias in Account to display
         * an alternate team name.
         */
        ALIAS,
    }
    
    private Properties scoringProperties = new Properties();
    
    /**
     * Enable team auto registration.
     * 
     */
    private boolean enableAutoRegistration = false;
    
    /**
     * The password type for the new passwords.
     */
    private PasswordType autoRegistrationPasswordType = PasswordType.RANDOM;

//    /** replaced with scheduledStartTime; see below
//     * Contest Start/Date Time.
//     */
//    private Date startDate;
    
    /**
     * The date/time when the contest is scheduled (intended) to start.
     * This value is null (undefined) if no scheduled start time has been set.
     * This value ONLY applies BEFORE THE CONTEST STARTS; once 
     * any "start contest" operation (e.g. pushing the "Start Button") has occurred,
     * this value no longer has meaning.
     */
    private GregorianCalendar scheduledStartTime = null ;
    
    private boolean autoStartContest = false;

    private boolean autoStopContest = false ;
    
    /**
     * Scoreboard freeze time.
     */
    private String freezeTime="null";

    private String contestShortName;


    /**
     * Returns the date/time when the contest is scheduled (intended) to start.
     * This value is null if no scheduled start time has been set,
     * or if the contest has already started.  
     * @see ContestTime#getContestStartTime()
     */
    public GregorianCalendar getScheduledStartTime() {
        return scheduledStartTime;
    }
    
    /**
     * Receives a {@link GregorianCalendar} object specifying a (future) instant in time;
     * sets the specified date/time as the scheduled (intended) start time for the
     * contest.  Note that it is an error to invoke this method with a value other than "null"
     * after the contest has started, since the value of "scheduled start time" is meaningless after
     * the contest is under way.  It is the responsibility of clients to insure
     * this method is only invoked with a non-null value before the contest has been started.
     * 
     */
    public void setScheduledStartTime(GregorianCalendar newScheduledStartTime) {
        scheduledStartTime = newScheduledStartTime; 
    }

    public String getContestTitle() {
        return contestTitle;
    }

    public void setContestTitle(String contestTitle) {
        this.contestTitle = contestTitle;
    }

    public String getContestURL() {
        return contestURL;
    }

    public void setContestURL(String contestURL) {
        this.contestURL = contestURL;
    }

    public TeamDisplayMask getTeamDisplayMode() {
        return teamDisplayMode;
    }

    public void setTeamDisplayMode(TeamDisplayMask teamDisplayMask) {
        this.teamDisplayMode = teamDisplayMask;
    }

    public String getJudgesDefaultAnswer() {
        return judgesDefaultAnswer ;
    }

    /**
     * judgesDefaultAnswer must be a non-zero length trimmed string.
     * 
     * @param judgesDefaultAnswer The judgesDefaultAnswer to set.
     */
    public void setJudgesDefaultAnswer(String judgesDefaultAnswer) {
        if (judgesDefaultAnswer != null && judgesDefaultAnswer.trim().length() > 0) {
            this.judgesDefaultAnswer = judgesDefaultAnswer.trim();
        }
    }
    
    public boolean isSameAs(ContestInformation contestInformation) {
        try {
            if (contestTitle == null) {
                if (contestInformation.getContestTitle() != null) {
                    return false;
                }
            } else {
                if (!contestTitle.equals(contestInformation.getContestTitle())) {
                    return false;
                }
            }
            if (!judgesDefaultAnswer.equals(contestInformation.getJudgesDefaultAnswer())) {
                return false;
            }
            if (!teamDisplayMode.equals(contestInformation.getTeamDisplayMode())) {
                return false;
            }
            if (preliminaryJudgementsTriggerNotifications != contestInformation.isPreliminaryJudgementsTriggerNotifications()) {
                return false;
            }
            if (preliminaryJudgementsUsedByBoard != contestInformation.isPreliminaryJudgementsUsedByBoard()) {
                return false;
            }
            if (sendAdditionalRunStatusInformation != contestInformation.isSendAdditionalRunStatusInformation()) {
                return false;
            }
            if (maxFileSize != contestInformation.getMaxFileSize()){
                return false;
            }
            if (! scoringProperties.equals(contestInformation.getScoringProperties())){
                return false;
            }
            if (ccsTestMode != contestInformation.isCcsTestMode()) {
                return false;
            }
            if (! StringUtilities.stringSame(externalYamlPath, contestInformation.externalYamlPath)) {
                return false;
            }
            if (! StringUtilities.stringSame(rsiCommand, contestInformation.rsiCommand)) {
                return false;
            }
            if (enableAutoRegistration != contestInformation.isEnableAutoRegistration()) {
                return false;
            }
            
            //old code:
//            if (!DateUtilities.dateSame(startDate, contestInformation.startDate)) {
//                return false;
//            }

            //new code:
            //DateUtilities.dateSame() expects Date objects but ContestInformation now maintains
            // scheduledStartTime (formerly "StartDate") as a GregorianCalendar; need to convert.
            //Also need to first check for null references (to avoid NPEs on fetch of Date from GregorianCalendar)
            
            //If the references to scheduledStartTime in the two ContestInfos are such that one is null and the other 
            // is not, the ContestInfos are not the same so return false.  
            // Note that "one is null and the other is not null" can be computed with the ^ (XOR) operator:
            //   "A XOR B" = true iff A != B
            if (scheduledStartTime==null ^ contestInformation.getScheduledStartTime()==null) {
                 return false;
            }
            //at this point either both scheduledStartTime references are null, or both are non-null
            //If both are null, this test for equality passes and we fall through to other cases
            //If both non-null, get Dates from both and compare them
            if (scheduledStartTime!=null /*and therefore contestInformation.getScheduledStartTime() also != null*/) {
                if (!DateUtilities.dateSame(scheduledStartTime.getTime(), 
                        contestInformation.getScheduledStartTime().getTime())) {
                    return false;
                }
            } else {
                //both scheduledStartTime and contestInformation.getScheduledStartTime() must be null (hence, "same")
                //continue;
            }

            if (autoStartContest != contestInformation.autoStartContest) {
                return false;
            }

            if (!StringUtilities.stringSame(freezeTime, contestInformation.freezeTime)) {
                return false;
            }

            if (!StringUtilities.stringSame(contestShortName, contestInformation.contestShortName)) {
                return false;
            }
            if (!StringUtilities.stringSame(judgeCDPBasePath, contestInformation.getJudgeCDPBasePath())) {
                return false;
            }
            
            if (!StringUtilities.stringSame(adminCDPBasePath, contestInformation.getAdminCDPBasePath())) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace(System.err); // TODO log this exception 
            return false;
        }
    }

    /**
     * @param preliminaryJudgementsUsedByBoard the preliminaryJudgementsUsedByBoard to set
     */
    public void setPreliminaryJudgementsUsedByBoard(boolean preliminaryJudgementsUsedByBoard) {
        this.preliminaryJudgementsUsedByBoard = preliminaryJudgementsUsedByBoard;
    }

    /**
     * The Scoring Algorithm should use this to determine whether to count preliminary judgements
     * as scoreable.
     * 
     * @return the preliminaryJudgementsUsedByBoard
     */
    public boolean isPreliminaryJudgementsUsedByBoard() {
        return preliminaryJudgementsUsedByBoard;
    }

    /**
     * @param preliminaryJudgementsTriggerNotifications the preliminaryJudgementsTriggerNotifications to set
     */
    public void setPreliminaryJudgementsTriggerNotifications(boolean preliminaryJudgementsTriggerNotifications) {
        this.preliminaryJudgementsTriggerNotifications = preliminaryJudgementsTriggerNotifications;
    }

    /**
     * @return the preliminaryJudgementsTriggerNotifications
     */
    public boolean isPreliminaryJudgementsTriggerNotifications() {
        return preliminaryJudgementsTriggerNotifications;
    }

    public boolean isSendAdditionalRunStatusInformation() {
        return sendAdditionalRunStatusInformation;
    }

    public void setSendAdditionalRunStatusInformation(boolean sendAdditionalRunStatusInformation) {
        this.sendAdditionalRunStatusInformation = sendAdditionalRunStatusInformation;
    }
    
    public JudgementNotificationsList getJudgementNotificationsList() {
        return judgementNotificationsList;
    }

    public void setJudgementNotificationsList(JudgementNotificationsList judgementNotificationsList) {
        this.judgementNotificationsList = judgementNotificationsList;
    }
    
    public void updateJudgementNotification (NotificationSetting notificationSetting ){
        judgementNotificationsList.update(notificationSetting);
    }

    /**
     * Maximum file size for output files.
     * 
     * @return file size in bytes
     */
    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    
    /**
     * Scoring Properties.
     * 
     * Minute penalties, etc.
     * 
     * @return
     */
    public Properties getScoringProperties() {
        return scoringProperties;
    }

    public void setScoringProperties(Properties scoringProperties) {
        this.scoringProperties = scoringProperties;
    }

    public boolean isCcsTestMode() {
        return ccsTestMode;
    }
    
    public void setCcsTestMode(boolean ccsTestMode) {
        this.ccsTestMode = ccsTestMode;
    }
    
    public void setRsiCommand(String rsiCommand) {
        this.rsiCommand = rsiCommand;
    }
    
    /**
     * Get the Run Submission Interface (RSI) command.
     * 
     * @return the command string to run when each run is submitted.
     */
    public String getRsiCommand() {
        return rsiCommand;
    }
    
    public void setExternalYamlPath(String externalYamlPath) {
        this.externalYamlPath = externalYamlPath;
    }
    /**
     * Get the external YAML path.
     * 
     * @return the location of contest.yaml and problem data files.
     */
    public String getExternalYamlPath() {
        return externalYamlPath;
    }
    
    public void setLastRunNumberSubmitted(int lastRunNumberSubmitted) {
        this.lastRunNumberSubmitted = lastRunNumberSubmitted;
    }
   
    /**
     * Get the last run that was sent to the RSI.
     * 
     * @see #getRsiCommand()
     * @return
     */
    public int getLastRunNumberSubmitted() {
        return lastRunNumberSubmitted;
    }

    public boolean isEnableAutoRegistration() {
        return enableAutoRegistration;
    }

    public void setEnableAutoRegistration(boolean enableAutoRegistration) {
        this.enableAutoRegistration = enableAutoRegistration;
    }

    public PasswordType getAutoRegistrationPasswordType() {
        return autoRegistrationPasswordType;
    }

    public void setAutoRegistrationPasswordType(PasswordType autoRegistrationPasswordType) {
        this.autoRegistrationPasswordType = autoRegistrationPasswordType;
    }

    /**
     * Sets the contest scheduled start time from the specified Date.
     * Note: previously, ContestInformation stored "startDate" as an object of
     * class {@link Date}.  It nows stores the scheduled start time as a 
     * {@link GregorianCalendar}; however, this method is maintained for compatibility.
     * The method converts the given {@link Date} into an equivalent {@link GregorianCalendar}
     * and invokes {@link #scheduledStartTime} with the resulting {@link GregorianCalendar} object.
     * 
     * @param startDate - the date at which the contest is scheduled to start; 
     *      specifying "null" as the start date causes the scheduled start time to become undefined
     */
    public void setScheduledStartDate(Date startDate) {
        if (startDate == null) {
            setScheduledStartTime(null);
        } else {
            GregorianCalendar newStartDate = new GregorianCalendar();
            newStartDate.setTime(startDate);
            setScheduledStartTime(newStartDate);
        }
    }
    
    /**
     * Returns a {@link Date} object representing the scheduled start time for the contest,
     * or null if no scheduled start time has been set.
     * @return the scheduled start time as a Date
     * @see #setScheduledStartDate(Date)
     * @see #getScheduledStartTime()
     */
    public Date getScheduledStartDate() {
        if (scheduledStartTime == null) {
            return null;
        } else {
            return scheduledStartTime.getTime();            
        }

    }
    
    public boolean isAutoStartContest() {
        return autoStartContest;
    }
    
    public void setAutoStartContest(boolean autoStartContest) {
        this.autoStartContest = autoStartContest;
    }
    
    public void setAutoStopContest(boolean autoStopContest) {
        this.autoStopContest = autoStopContest;
    }

    public boolean isAutoStopContest() {
        return autoStopContest;
    }
    
    public String getFreezeTime() {
        return freezeTime;
    }
    
    public void setFreezeTime(String freezeTime) {
        this.freezeTime = freezeTime;
    }

    public String getContestShortName() {
        return contestShortName;
    }
    
    public void setContestShortName(String contestShortName) {
        this.contestShortName = contestShortName;
    }
    
    /**
     * Set base path for CDP/external files on judge.
     * 
     * This is the base path location where the problem data files are located.
     * 
     * @param judgeCDPBasePath
     */
    public void setJudgeCDPBasePath(String judgeCDPBasePath) {
        this.judgeCDPBasePath = judgeCDPBasePath;
    }

    /**
     * Get base path for CDP/external files on judge.
     * @return
     */
    public String getJudgeCDPBasePath() {
        return judgeCDPBasePath;
    }
    
    public void setAdminCDPBasePath(String adminCDPBasePath) {
        this.adminCDPBasePath = adminCDPBasePath;
    }
    
    /**
     * Get base path for CDP on Admin.
     */
    public String getAdminCDPBasePath() {
        return adminCDPBasePath;
    }
}
