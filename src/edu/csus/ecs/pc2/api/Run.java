package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.JudgementRecord;

/**
 * Run information.
 * <P>
 * Contains information about a run.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Run {

    /**
     * Run from internal PC&sup2; data.
     */
    private edu.csus.ecs.pc2.core.model.Run run = null;

    protected Run(edu.csus.ecs.pc2.core.model.Run inRun) {
        run = inRun;
    }

    /**
     * Get the ElementId for this run.
     * @return the ElementId for this run.
     */
    public ElementId getElementId() {
        return run.getElementId();
    }

    /**
     * Has run been judged?
     * 
     * @return true if judged, false if not judged.
     */
    public boolean isJudged() {
        return run.isJudged();
    }

    /**
     * Has the run been given a Yes/Correct judgement?.
     * 
     * @return true if run solves problem, false if does not solve problem.
     */
    public boolean isSolved() {
        return run.isSolved();
    }

    /**
     * Has run been marked as deleted?.
     * 
     * @return true if run deleted, else false.
     */
    public boolean isDeleted() {
        return run.isDeleted();
    }

    /**
     * Which team (client) submitted this run.
     * 
     * @return the ClientId (site, team number) for the team who submitted this run.
     */
    public ClientId getSubmitterId() {
        return run.getSubmitter();
    }

    /**
     * Get the ElementId for the judgement.
     *
     * @see #getJudgementTitle(IContest)
     * @return null if run not judged, else the ElementId for the judgement.
     */
    public ElementId getJudgementId() {
        JudgementRecord judgementRecord = run.getJudgementRecord();
        if (judgementRecord != null) {
            return judgementRecord.getJudgementId();
        } else {
            return null;
        }
    }

    /**
     * Get the judgement title/text for the run.
     * 
     * If the run has been judged using an automated judger (also know as a validator) this method may return text from the validator which may not match any defined judgement title/text.
     * 
     * @param contest
     *            contest is used to get the judgement title.
     * @return null if not judged, else the title for the judgement.
     */
    public String getJudgementTitle(IContest contest) {
        JudgementRecord judgementRecord = run.getJudgementRecord();
        if (judgementRecord != null) {
            if (judgementRecord.getValidatorResultString() != null) {
                return judgementRecord.getValidatorResultString();
            } else {
                return contest.getJudgementTitle(judgementRecord.getJudgementId());
            }
        } else {
            return null;
        }
    }

    /**
     * Get problem elementId.
     * 
     * @return elementId for the problem that was selected.
     */
    public ElementId getProblemId(){
        return run.getProblemId();
    }
    
    /**
     * Get language elementId.
     * 
     * @return elementId for the language which was selected.
     */
    public ElementId getLanguageId(){
        return run.getLanguageId();
    }

    /**
     * Get run number.
     * 
     * Run number is assigned on the server.  Each
     * run number is unique to its site.  A run number
     * with site number is unique in the contest.
     * 
     * @return the number for this run.
     */
    public int getNumber() {
        return run.getNumber();
    }
    
    /**
     * Get site number.
     * 
     * @return the site number for the run.
     */
    public int getSiteNumber() {
        return run.getSiteNumber();
    }

    /**
     * Get elapsed minutes when this run was submitted.
     * 
     * @return the number of minutes elapsed when this run was submitted.
     */
    public long getElapsedMins() {
        return run.getElapsedMins();
    }


}
