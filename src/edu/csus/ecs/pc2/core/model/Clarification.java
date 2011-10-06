package edu.csus.ecs.pc2.core.model;

import java.util.ArrayList;

/**
 * Clarification.
 * 
 * A request for clarification from the judges.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO CLEANUP - need to deprecate setAnswer

// $HeadURL$
public class Clarification extends ISubmission {

    /**
     * 
     */
    private static final long serialVersionUID = -6913818225948370496L;

    /**
     * Clarification States.
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum ClarificationStates {
        /**
         * A newly submitted Clar
         */
        NEW,
        /**
         * Checked out, being answered.
         */
        BEING_ANSWERED,
        /**
         * Put on hold, so judge can answer another clar.
         */
        HOLD,
        /**
         * Judge has answered clarifications/question.
         */
        ANSWERED,
    }
    
    private boolean deleted = false;

    private ClientId whoCheckedItOutId = null;

    private String question = null;

    private ArrayList<ClarificationAnswer> answerList = new ArrayList<ClarificationAnswer>();

    private ClarificationStates state = ClarificationStates.NEW;

    public Clarification(ClientId submitter, Problem problemId, String question) {
        super();

        setSubmitter(submitter);
        setProblemId(problemId.getElementId());
        this.question = question;
        setElementId(new ElementId("Clarification"));

    }

    /**
     * @return Returns the deleted.
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted
     *            The deleted to set.
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return Returns the answer.
     */
    public String getAnswer() {
        if (answerList.size() == 0) {
            return null;
        } else {
            return getFirstAnswer().getAnswer();
        }
    }

    private ClarificationAnswer getFirstAnswer() {
        return answerList.get(0);
    }

    /**
     * @param answer
     *            The answer to set.
     * @param client
     * @param contestTime
     * @param sendToAll
     */
    public void setAnswer(String answer, ClientId client, ContestTime contestTime, boolean sendToAll) {
        state = ClarificationStates.ANSWERED;
        ClarificationAnswer clarificationAnswer = new ClarificationAnswer(answer, client, sendToAll, contestTime);
        addAnswer(clarificationAnswer);
    }

    public boolean isAnswered() {
        return state == ClarificationStates.ANSWERED;
    }

    public boolean isNew() {
        return state == ClarificationStates.NEW;
    }

    public String getQuestion() {
        return question;
    }

    public ClarificationStates getState() {
        return state;
    }

    public void setState(ClarificationStates state) {
        this.state = state;
    }

    public boolean isSendToAll() {
        if (answerList.size() == 0) {
            return false;
        } else {
            return getFirstAnswer().isSendToAll();
        }
    }

    public String toString() {
        return "Clarification " + getNumber() + " " + getState() + " from " + getSubmitter() + " at " + getElapsedMins() + " id=" + getElementId();
    }

    public ClientId getWhoJudgedItId() {
        if (answerList.size() == 0) {
            return null;
        } else {
            return getFirstAnswer().getAnswerClient();
        }
    }

    public boolean isSameAs(Clarification clarification) {
        try {
            if (deleted != clarification.isDeleted()) {
                return false;
            }

            if (!getWhoJudgedItId().equals(clarification.getWhoJudgedItId())) {
                return false;
            }

            if (!question.equals(clarification.getQuestion())) {
                return false;
            }

            if (!getAnswer().equals(clarification.getAnswer())) {
                return false;
            }

            if (state != clarification.getState()) {
                return false;
            }

            if (isSendToAll() != clarification.isSendToAll()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            // TODO CLEANUP log to static Exception log
            return false;
        }
    }

    public ClientId getWhoCheckedItOutId() {
        return whoCheckedItOutId;
    }

    public void setWhoCheckedItOutId(ClientId whoCheckedItOut) {
        this.whoCheckedItOutId = whoCheckedItOut;
    }

    /**
     * Add answer to list of answers.
     * 
     * @param clarificationAnswer
     */
    public void addAnswer(ClarificationAnswer clarificationAnswer) {
        state = ClarificationStates.ANSWERED;
        answerList.add(clarificationAnswer);
    }

    public ClarificationAnswer[] getClarificationAnswers() {
        return (ClarificationAnswer[]) answerList.toArray(new ClarificationAnswer[answerList.size()]);
    }
}
