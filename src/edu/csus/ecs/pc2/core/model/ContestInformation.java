package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Properties;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;

/**
 * InternalContest Wide Information.
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

    private String contestTitle = "Programming Contest";

    private String contestURL;
    
    private TeamDisplayMask teamDisplayMode = TeamDisplayMask.LOGIN_NAME_ONLY;

    private String judgesDefaultAnswer = "No response, read problem statement";
    
    private boolean preliminaryJudgementsUsedByBoard = false;
    private boolean preliminaryJudgementsTriggerNotifications = false;
    
    private boolean sendAdditionalRunStatusInformation = false;
    
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
            return true;
        } catch (Exception e) {
            // TODO log to static exception log
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
}
