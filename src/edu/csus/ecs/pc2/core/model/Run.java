package edu.csus.ecs.pc2.core.model;

import java.util.Vector;

/**
 * A contest submitted run.
 * 
 * 
 * Contains the submitter, problem, language, files and other data for a run.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// TODO Should judgementList be accessed with a synchronize ??
public class Run extends ISubmission {

    /**
     * 
     */
    private static final long serialVersionUID = 4643865629642121895L;

    public static final String SVN_ID = "$Id$";

    /**
     * The states a Run can be in.
     */
    public enum RunStates {
        /**
         * Run submitted.
         */
        NEW,
        /**
         * Judge has requested run.
         */
        CHECKED_OUT,
        /**
         * Run is being judged.
         * 
         * If a run must be judged by more than one judge, then when at least one judge has judged the run this state is set.
         */
        BEING_JUDGED,

        /**
         * Run is being re-judged.
         * 
         * A run was previously judged.
         */
        BEING_RE_JUDGED,

        /**
         * A judge has checked out the run and has chosen to put this run on hold and judge another run.
         */
        HOLD,
        /**
         * A previously judged run is being rejudged.
         */
        REJUDGE,
        /**
         * All judging has been done.
         */
        JUDGED
    }

    private Vector<JudgementRecord> judgementList = new Vector<JudgementRecord>();

    // TODO Judgements

    private boolean deleted;

    /**
     * State for this run.
     */
    private Run.RunStates status = Run.RunStates.NEW;

    /**
     * Operating System where Run was instanciated.
     */
    private String systemOS = null;

    public Run(ClientId submitter, Language languageId, Problem problemId) {
        super();
        setSubmitter(submitter);
        setLanguageId(languageId.getElementId());
        setProblemId(problemId.getElementId());
        setElementId(new ElementId("Run"));
        systemOS = System.getProperty("os.name", "?");
    }

    /**
     * @return Returns the deleted.
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @return Returns the status.
     */
    public Run.RunStates getStatus() {
        return status;
    }

    /**
     * @param deleted
     *            The deleted to set.
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(Run.RunStates status) {
        this.status = status;
    }

    /**
     * @return true if judged.
     */
    public boolean isJudged() {
        return status == Run.RunStates.JUDGED;
    }

    /**
     * Get the current judgement.
     * 
     * @return return null if not judged else the judgement
     */
    public JudgementRecord getJudgementRecord() {
        if (judgementList.size() == 0) {
            // TODO should thow an Exception? JudgmentNotFound
            return null;
        }

        if (judgementList.size() == 1) {
            // TODO should thow an Exception? JudgmentNotFound
            return judgementList.elementAt(0);
        }

        // Start from end and work to beginning.

        for (int i = judgementList.size() - 1; i >= 0; i--) {
            JudgementRecord judgement = judgementList.elementAt(i);
            if (judgement.isActive()) {
                return judgement;
            }
        }

        // TODO should thow an Exception? JudgmentNotFound
        return null;
    }

    /**
     * 
     * @return true if team given a Yes.
     */
    public boolean isSolved() {
        JudgementRecord judgementRecord = getJudgementRecord();
        if (judgementRecord == null) {
            return false;
        }

        return judgementRecord.isSolved();
    }

    /**
     * Return list of all JudgementRecords. <br>
     * 
     * 
     * @return JudgementRecord[] - list of all JudgementRecord
     */
    public JudgementRecord[] getAllJudgementRecords() {
        if (judgementList.size() == 0) {
            // TODO should thow an Exception? JudgmentNotFound
            return new JudgementRecord[0];
        }

        JudgementRecord[] judgementRecordArray = new JudgementRecord[judgementList.size()];
        for (int i = judgementList.size() - 1; i >= 0; i--) {
            JudgementRecord judgementRecord = judgementList.elementAt(i);
            judgementRecordArray[i] = judgementRecord;
        }

        return judgementRecordArray;
    }

    /**
     * Add new judgement.
     * 
     * This will disable previous judgements and
     * 
     * @param judgement
     *            new judgement to add.
     */
    public void addJudgement(JudgementRecord judgement) {
        if (judgement == null) {
            throw new IllegalArgumentException("Input judgement is null");
        }

        // Deactivate last judgement

        JudgementRecord lastJudgement = getJudgementRecord();
        if (lastJudgement != null) {
            lastJudgement.setActive(false);
        }

        judgementList.addElement(judgement);

    }

    /**
     * 
     * @return number of minutes to judge run.
     */
    public long getJudgedMinutes() {
        JudgementRecord judgement = getJudgementRecord();
        if (judgement == null) {
            return 0; // TODO throw exception ??
        }

        return judgement.getJudgedMinutes();

    }

    public String getCommentsForTeam() {
        JudgementRecord judgementRecord = getJudgementRecord();
        if (judgementRecord == null) {
            return "";
        }

        if (judgementRecord.getCommentForTeam() == null) {
            return "";
        }

        return judgementRecord.getCommentForTeam().getComment();
    }

    public String getCommentsForJudge() {
        JudgementRecord judgementRecord = getJudgementRecord();
        if (judgementRecord == null) {
            return "";
        }

        if (judgementRecord.getCommentForJudge() == null) {
            return "";
        }

        return judgementRecord.getCommentForJudge().getComment();
    }

    /**
     * Show this judgement to teams ?
     * 
     * @return if the judgement can be shown to the team return true, otherwise return false
     */
    public boolean isSendToTeams() {
        JudgementRecord judgementRecord = getJudgementRecord();
        if (judgementRecord != null) {
            return judgementRecord.isSendToTeam();
        } else {
            return false;
        }
    }

    /**
     * @return Returns the oS.
     */
    public String getSystemOS() {
        return systemOS;
    }

    public String toString() {
        return "Run " + getNumber() + " " + getStatus() + " from " + getSubmitter() + " id " + getElementId().toString();
    }

    public boolean isSameAs(Run run) {
        try {
            if (getElapsedMins() != run.getElapsedMins()) {
                return false;
            }
            if (isDeleted() != run.isDeleted()) {
                return false;
            }
            if (!getSubmitter().equals(run.getSubmitter())) {
                return false;
            }
            if (!getProblemId().equals(run.getProblemId())) {
                return false;
            }
            if (!getLanguageId().equals(run.getLanguageId())) {
                return false;
            }
            if (getStatus() != run.getStatus()) {
                return false;
            }
            if (!getJudgementRecord().equals(run.getJudgementRecord())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // TODO add to static Exception Log
            return false;
        }

    }

}
