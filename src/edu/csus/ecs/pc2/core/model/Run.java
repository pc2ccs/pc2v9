package edu.csus.ecs.pc2.core.model;

import java.util.Vector;

/**
 * Submitted Run info.
 * 
 * Contains the submitter, problem, language, files and other data for a run.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
// TODO Should judgementList be accessed with a synchronize ??
public class Run extends ISubmission {

    /**
     * 
     */
    private static final long serialVersionUID = 4643865629642121895L;

    /**
     * The states a Run can be in.
     */
    public enum RunStates {
        /**
         * Initial state before a run is assigned a real state
         */
        INITIAL,
        
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
        JUDGED,
        
        /**
         * Queued for Computer Judged "New"
         */
        QUEUED_FOR_COMPUTER_JUDGEMENT,
        
        /**
         *  Computer Judged "BEING_JUDGED"
         */
        BEING_COMPUTER_JUDGED,
        
        /**
         * NEW, shown on New
         */
        MANUAL_REVIEW
    }

    private Vector<JudgementRecord> judgementList = new Vector <JudgementRecord>();

    private Vector<RunTestCase> testcases = new Vector <RunTestCase>();
    
    private boolean deleted;
    
    /**
     * State for this run.
     */
    private Run.RunStates status = Run.RunStates.INITIAL;

    /**
     * Operating System where Run was instantiated.
     */
    private String systemOS = null;

    /**
     * The original elapsed time
     */
    private long originalElapsedMS = -1;

    private long overRideElapsedTimeMS;

    private int overrideNumber = 0;

    public Run(ClientId submitter, Language languageId, Problem problemId) {
        super();
        setSubmitter(submitter);
        setLanguageId(languageId.getElementId());
        setProblemId(problemId.getElementId());

        // check if problem is to be "computer judged" or manual judged 
        if (problemId.isComputerJudged()) {
            status = Run.RunStates.QUEUED_FOR_COMPUTER_JUDGEMENT;
        } else {
            status = Run.RunStates.NEW;
        }
        
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
     * Returns true if judged (or is being re-judged, or Manual_review.
     * @return true if judged.
     */
    public boolean isJudged() {
        return status == RunStates.JUDGED || status == RunStates.BEING_RE_JUDGED || status == RunStates.MANUAL_REVIEW;
    }

    /**
     * Get the current judgement.
     * 
     * @return return null if not judged else the judgement
     */
    public JudgementRecord getJudgementRecord() {
        if (judgementList.size() == 0) {
            return null;
        }

        if (judgementList.size() == 1) {
            return judgementList.elementAt(0);
        }

        // Start from end and work to beginning.

        for (int i = judgementList.size() - 1; i >= 0; i--) {
            JudgementRecord judgement = judgementList.elementAt(i);
            if (judgement.isActive()) {
                return judgement;
            }
        }

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
     * Return list of all JudgementRecords.
     *  
     * @return JudgementRecord[] - list of all active and inactive JudgementRecord
     */
    public JudgementRecord[] getAllJudgementRecords() {
        return (JudgementRecord[]) judgementList.toArray(new JudgementRecord[judgementList.size()]);
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
            if (lastJudgement.isComputerJudgement()){
                if (! judgement.isComputerJudgement()){
                    judgement.setPreviousComputerJudgementId (judgement.getElementId());
                }
            }
        }

        judgementList.addElement(judgement);

    }
    
    /**
     * 
     * @return null if no judgement found, else the judgement
     */
    public JudgementRecord getComputerJudgementRecord(){

        if (judgementList.size() == 0) {
            return null;
        }

        if (judgementList.size() == 1) {
            JudgementRecord judgementRecord = judgementList.elementAt(0);
            if (judgementRecord.isComputerJudgement()) {
                return judgementRecord;
            } else {
                return null;
            }
        }
        
        for (int i = judgementList.size() - 1; i >= 0; i--) {
            JudgementRecord judgement = judgementList.elementAt(i);
            if (judgement.isActive() && judgement.isComputerJudgement()) {
                return judgement;
            }
        }

        return null;
    }

    /**
     * 
     * @return 0 if not judged, else number of minutes to judge run.
     */
    public long getJudgedMinutes() {
        JudgementRecord judgement = getJudgementRecord();
        if (judgement == null) {
            return 0;
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

    /**
     * 
     * @return empty string if no judgement, else comment
     */
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
     * @return true if the judgement can be shown to the team, else false
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
        return "Run " + getNumber() + " " + getStatus() + " " + getElapsedMins()+ " min " + getElapsedMS() +"ms from " + getSubmitter() + " id " + getElementId().toString();
    }

    /**
     * The  elapsed time when this run was added.
     * 
     * This is always the actual server submission time. The submission
     * time can be 'overridden' using {@link #setElapsedMS(long)} and
     * that elapsed MS will be used for scoring, esp. in CCS Test Mode.
     * 
     * @return the server timestamp for this submission
     */
    public long getOriginalElapsedMS() {
        return originalElapsedMS;
    }
    
    @Override
    public void setElapsedMS(long elapsedMS) {
        if (getOriginalElapsedMS() == -1) {
            /**
             * This code only gets exercised the first time elapsed time is
             * set, so that subsequent calls to setElapsedMS do not overwrite
             * the original elapsed time. 
             */
            originalElapsedMS = elapsedMS;
            if (overRideElapsedTimeMS > 0) {
                elapsedMS = overRideElapsedTimeMS;
            }
        }
        super.setElapsedMS(elapsedMS);
    }

    /**
     * Set an override time for the elapsed time.
     * 
     * When {@link #setElapsedMS(long)} is invoked will save that
     * elapsed time (fetched by {@link #getOverRideElapsedTimeMS()})
     * and set the elapsed time to this override time.  
     * 
     * @param overRideElapsedTimeMS
     */
    public void setOverRideElapsedTimeMS(long overRideElapsedTimeMS) {
        this.overRideElapsedTimeMS = overRideElapsedTimeMS;
    }

    public long getOverRideElapsedTimeMS() {
        return overRideElapsedTimeMS;
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
            // TODO logger.debug("Exception while comparing runs", e);
            return false;
        }
    }
    
    @Override
    public int getNumber() {
        if (overrideNumber > 0) {
            return overrideNumber;
        } else {
            return super.getNumber();
        }
    }
    
    /**
     * The run id assigned by a server. 
     * 
     * @return
     */
    public int internalRunId() {
        return super.getNumber();
    }

    public void setOverRideNumber(int number) {
        overrideNumber = number;
    }

    /**
     * 
     * @return a list of the testcase results
     */
    public RunTestCase[] getRunTestCases() {
        return (RunTestCase[]) testcases.toArray(new RunTestCase[testcases.size()]);
    }

    public void addTestCase(RunTestCase runTestCase) {
        testcases.add(runTestCase);
    }

}
