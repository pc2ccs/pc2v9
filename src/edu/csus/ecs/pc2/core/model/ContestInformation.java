// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import edu.csus.ecs.pc2.core.DateUtilities;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.util.ScoreboardVariableReplacer;

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
    
    //Shadow Mode settings
    private boolean shadowMode = false;
    private String primaryCCS_URL = null;
    private String primaryCCS_user_login = "";
    private String primaryCCS_user_pw = "";
    private String lastShadowEventID = "";
    
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
     * Display string for team display on standings.
     * 
     * @see ScoreboardVariableReplacer#substituteDisplayNameVariables(String, Account, Group)
     */
    private String teamScoreboardDisplayFormat = ScoreboardVariableReplacer.TEAM_NAME;

    /**
     * 
     * @author pc2@ecs.csus.edu
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
    private String freezeTime="01:00:00";

    private String contestShortName;

    /**
     * Whether the contest is thawed(unfrozen).  Meaning the final scoreboard
     * can be revealed (despite the scoreboad freeze).
     * 
     * Typically a contest is not thawed until the awards ceremony.
     */
    private Date thawed = null;

    private boolean allowMultipleLoginsPerTeam;
    
    
    /**
     * stop-on-first-failed-test-case
     * 
     */
    private boolean stopOnFirstFailedtestCase = false;

    private String overrideLoadAccountsFilename = null;
    
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
            if (!teamScoreboardDisplayFormat.equals(contestInformation.getTeamScoreboardDisplayFormat())) {
                return false;
            }
            if (! scoringProperties.equals(contestInformation.getScoringProperties())){
                return false;
            }
            if (ccsTestMode != contestInformation.isCcsTestMode()) {
                return false;
            }
            if (shadowMode != contestInformation.isShadowMode()) {
                return false;
            }
            if (! StringUtilities.stringSame(externalYamlPath, contestInformation.externalYamlPath)) {
                return false;
            }
            if (!StringUtilities.stringSame(freezeTime, contestInformation.freezeTime)) {
                return false;
            }
            if (! StringUtilities.stringSame(rsiCommand, contestInformation.rsiCommand)) {
                return false;
            }
            if (enableAutoRegistration != contestInformation.isEnableAutoRegistration()) {
                return false;
            }            
            if (! StringUtilities.stringSame(primaryCCS_URL, contestInformation.primaryCCS_URL)) {
                return false;
            }            
            if (! StringUtilities.stringSame(primaryCCS_user_login, contestInformation.primaryCCS_user_login)) {
                return false;
            }
            if (! StringUtilities.stringSame(primaryCCS_user_pw, contestInformation.primaryCCS_user_pw)) {
                return false;
            }
            if ((thawed == null && contestInformation.getThawed() != null) || (thawed != null && !thawed.equals(contestInformation.getThawed()))) {
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
            } 
            
            //both scheduledStartTime and contestInformation.getScheduledStartTime() must be null (hence, "same")
            //continue;

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
            if (autoStopContest != contestInformation.isAutoStopContest()) {
                return false;
            }
            
            if (allowMultipleLoginsPerTeam != contestInformation.isAllowMultipleLoginsPerTeam()) {
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
    
    public boolean isShadowMode() {
        return shadowMode;
    }

    public void setShadowMode(boolean shadowMode) {
        this.shadowMode = shadowMode;
    }

    public void setRsiCommand(String rsiCommand) {
        this.rsiCommand = rsiCommand;
    }
    
    /**
     * Returns the String representation for a "Primary CCS" URL (that is, the URL of a Remote CCS being shadowed); 
     * only relevant when operating this instance of the PC2 CCS as a "Shadow CCS".
     * @return a String containing the URL of the Primary CCS which we're shadowing
     */
    public String getPrimaryCCS_URL() {
        return primaryCCS_URL;
    }

    /**
     * Sets the String representation for a "Primary CCS" URL (that is, the URL of a Remote CCS being shadowed); 
     * only relevant when operating this instance of the PC2 CCS as a "Shadow CCS".
     * @param primaryCCS_URL a String giving the URL of the Primary (remote) CCS (the CCS being shadowed)
     */
    public void setPrimaryCCS_URL(String primaryCCS_URL) {
        this.primaryCCS_URL = primaryCCS_URL;
    }

    /**
     * Returns a String containing the user login account to be used when connecting 
     * to a Primary CCS (only useful when operating this instance of PC2 as a "Shadow CCS").
     * @return a String containing the Primary CCS user account name
     */
    public String getPrimaryCCS_user_login() {
        return primaryCCS_user_login;
    }

    /**
     * Sets the value of the user login (account) for the Primary CCS (only useful
     * when operating this instance of PC2 as a "Shadow CCS").
     * @param primaryCCS_user_login the primary CCS login account name
     */
    public void setPrimaryCCS_user_login(String primaryCCS_user_login) {
        this.primaryCCS_user_login = primaryCCS_user_login;
    }

    /**
     * Returns a String containing the password used for logging in to the 
     * Primary CCS (only useful when operating this instance of PC2 as a
     * "Shadow CCS").
     * @return a String containing a password
     */
    public String getPrimaryCCS_user_pw() {
        //TODO: consider some method of encrypting the password
        return primaryCCS_user_pw;
    }

    /**
     * Sets the value of the password to be used when connecting to a Primary CCS
     * (only useful when operating this instance of PC2 as a "Shadow CCS").
     * @param primaryCCS_user_pw a String containing a password
     */
    public void setPrimaryCCS_user_pw(String primaryCCS_user_pw) {
        this.primaryCCS_user_pw = primaryCCS_user_pw;
    }

    /**
     * Returns a String containing the "CLICS ID" for the last event retrieved from
     * a remote CCS being shadowed (only useful when operating this instance of PC2 as a
     * "Shadow CCS").
     * @return a String containing a remote event id
     */
    public String getLastShadowEventID() {
        return lastShadowEventID;
    }

    /**
     * Sets the value of the String containing the "CLICS since_id" for the last event retrieved from
     * a remote CCS being shadowed; that is, the id from which reconnections to the remote CCS event
     * feed should proceed (only useful when operating this instance of PC2 as a
     * "Shadow CCS").
     * @param lastShadowEventID a String identifying the last event from the remote CCS
     */
     public void setLastShadowEventID(String lastShadowEventID) {
        this.lastShadowEventID = lastShadowEventID;
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

    public boolean isUnfrozen() {
        if (thawed == null) {
            return false;
        }
        return true;
    }

    /**
     * Returns the date the was thawed, or null if not thawed.
     * 
     * @return Date the contest was thawed, else null
     */
    public Date getThawed() {
        return thawed;
    }
    
    /**
     * @param thawed when/if the contest is thawed
     */
    public void setThawed(Date date) {
        thawed = date;
    }
    
    /**
     * @param thawed whether the contest is thawed
     */
    public void setThawed(boolean thawed) {
        if (thawed) {
            this.thawed = new GregorianCalendar().getTime();
        } else {
            this.thawed = null;
        }
    }

    /**
     * Sets the boolean flag indicating whether or not teams are allowed to have multiple simultaneous logins.
     * Note that this is a GLOBAL setting, configured on the Admin's "Configure Contest>Settings" screen (or via YAML); 
     * either ALL teams are allowed to have multiple simultaneous logins, or NO team is allowed to have multiple simultaneous logins.
     * 
     * @param allowMultipleLoginsPerTeam whether or not a team is allowed to have multiple simultaneous login sessions.
     */
    public void setAllowMultipleLoginsPerTeam(boolean allowMultipleLoginsPerTeam) {
        this.allowMultipleLoginsPerTeam = allowMultipleLoginsPerTeam;
    }
    
    /**
     * Returns a boolean indicating whether or not the Contest Settings allow teams to have multiple simultaneous logins.
     * Note that this is a GLOBAL setting, configured on the Admin's "Configure Contest>Settings" screen; either ALL teams
     * are allowed to have multiple simultaneous logins, or NO team is allowed to have multiple simultaneous logins.
     * 
     * @return a boolean indicating the current "allow multiple simultaneous logins" setting for teams.
     */
    public boolean isAllowMultipleLoginsPerTeam() {
        return this.allowMultipleLoginsPerTeam;   
    }

    public boolean isStopOnFirstFailedtestCase() {
        return stopOnFirstFailedtestCase;
    }
    
    public void setStopOnFirstFailedtestCase(boolean stopOnFirstFailedtestCase) {
        this.stopOnFirstFailedtestCase = stopOnFirstFailedtestCase;
    }
    
    
    /**
     * Returns a string which defines which fields will be dispayed on the scoreboard.
     * 
     * @see ScoreboardVariableReplacer#substituteDisplayNameVariables(String, Account, Group)
     * @return
     */
    public String getTeamScoreboardDisplayFormat() {
        // Handle case where deserializing old contest objects sets teamScoreboardDisplayFormat to null.
        // In this case, just set it to the default (i361 - DSA causes NPE)
        if(teamScoreboardDisplayFormat == null) {
            teamScoreboardDisplayFormat = ScoreboardVariableReplacer.TEAM_NAME;
        }
        return teamScoreboardDisplayFormat;
    }

    public void setTeamScoreboardDisplayFormat(String teamScoreboardDisplayFormat) {
        this.teamScoreboardDisplayFormat = teamScoreboardDisplayFormat;
    }
    
    public String getOverrideLoadAccountsFilename() {
        return overrideLoadAccountsFilename;
    }
    
    public void setOverrideLoadAccountsFilename(String overrideLoadAccountsFilename) {
        this.overrideLoadAccountsFilename = overrideLoadAccountsFilename;
    }
}
