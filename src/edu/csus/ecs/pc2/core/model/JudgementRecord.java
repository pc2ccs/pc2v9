package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * A single Judgement from a judge for a Run.
 * 
 * This is has the judgement, who did the judgement, and other data about the judgement.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class JudgementRecord implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2043715842465711645L;

    public static final String SVN_ID = "$Id$";

    /**
     * Judgement Id.
     */
    private ElementId judgementId = null;

    /**
     * Who did this judgement.
     */
    private ClientId judgerClientId;

    /**
     * use the validator.
     */
    private boolean usedValidator = false;

    /**
     * Is this judgement active.
     */
    private boolean active = true;

    /**
     * Number of second it took the judge to judge this run.
     */
    private long judgedSeconds = 0;

    private JudgeComment commentForTeam = null;

    private JudgeComment commentForJudge = null;

    /**
     * The time when the judgement was received by the server.
     */
    private long whenJudgedTime;

    /**
     * The number of seconds it took to judge.
     * 
     * This time is the number of seconds between the time when the Select Judgement dialog appears and when the Judge (person)
     * selects a judgement.
     */
    private long howLongToJudgeInSeconds;

    // TODO create a amount of time it took for the program to execute

    private boolean solved = false;

    private boolean sendToTeam = true;

    public JudgementRecord(ElementId judgementId, ClientId judgerClientId, boolean solved, boolean usedValidator) {
        this.judgementId = judgementId;
        this.judgerClientId = judgerClientId;
        this.usedValidator = usedValidator;
        this.solved = solved;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return Returns the judgedSeconds.
     */
    public long getJudgedSeconds() {
        return judgedSeconds;
    }

    /**
     * @param judgedSeconds
     *            the number of seconds to judge run.
     */
    public void setJudgedSeconds(long judgedSeconds) {
        this.judgedSeconds = judgedSeconds;
    }

    /**
     * @return number of minutes to judge run.
     */
    public long getJudgedMinutes() {
        return getJudgedSeconds() / 60;
    }

    public ElementId getJudgementId() {
        return judgementId;
    }

    public void setJudgementId(ElementId judgementId) {
        this.judgementId = judgementId;
    }

    public ClientId getJudgerClientId() {
        return judgerClientId;
    }

    public JudgeComment getCommentForJudge() {
        return commentForJudge;
    }

    public void setCommentForJudge(JudgeComment commentForJudge) {
        this.commentForJudge = commentForJudge;
    }

    public JudgeComment getCommentForTeam() {
        return commentForTeam;
    }

    public void setCommentForTeam(JudgeComment commentForTeam) {
        this.commentForTeam = commentForTeam;
    }

    /**
     * Elapsed time on server when this judgement was registered.
     * 
     * @return the time
     */
    public long getWhenJudgedTime() {
        return whenJudgedTime;
    }

    /**
     * 
     * @param whenJudgedTime
     */
    public void setWhenJudgedTime(long whenJudgedTime) {
        this.whenJudgedTime = whenJudgedTime;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean isSendToTeam() {
        return sendToTeam;
    }

    public void setSendToTeam(boolean sendToTeams) {
        this.sendToTeam = sendToTeams;
    }

    public void setJudgerClientId(ClientId judgerClientId) {
        this.judgerClientId = judgerClientId;
    }

    // TODO code equals
    // public boolean equals(JudgementRecord judgementRecord) {
    //
    // try {
    // if (judgementId != judgementRecord.getJudgementId()) {
    // return false;
    // }
    // if (!judgerClientId.equals(judgementRecord.getJudgerClientId())) {
    // return false;
    // }
    // if (usedValidator != judgementRecord.usedValidator) {
    // return false;
    // }
    // if (solved != judgementRecord.isSolved()) {
    // return false;
    // }
    // if (active != judgementRecord.isActive()) {
    // return false;
    // }
    // if (judgedSeconds != judgementRecord.getJudgedSeconds()) {
    // return false;
    // }
    // if (whenJudgedTime != judgementRecord.getWhenJudgedTime()) {
    // return false;
    // }
    // if (sendToTeam != judgementRecord.isSendToTeam()) {
    // return false;
    // }
    // if (!commentForJudge.equals(judgementRecord.getCommentForJudge())) {
    // return false;
    // }
    // if (!commentForTeam.equals(judgementRecord.getCommentForTeam())) {
    // return false;
    // }
    //
    // return true;
    // } catch (Exception e) {
    // // TODO print to static Exception Log
    // return false;
    // }
    // }

    public long getHowLongToJudgeInSeconds() {
        return howLongToJudgeInSeconds;
    }

    public void setHowLongToJudgeInSeconds(long howLongToJudgeInSeconds) {
        this.howLongToJudgeInSeconds = howLongToJudgeInSeconds;
    }

    public boolean isUsedValidator() {
        return usedValidator;
    }

    public void setUsedValidator(boolean usedValidator) {
        this.usedValidator = usedValidator;
    }

}
