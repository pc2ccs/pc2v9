package edu.csus.ecs.pc2.core.model;

/**
 * Clarification.
 * 
 * A request for clarification from the judges.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class Clarification extends ISubmission {
    public static final String SVN_ID = "$Id$";

    /**
     * 
     */
    private static final long serialVersionUID = -6913818225948370496L;

    private boolean deleted = false;

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

    private ClientId whoJudgedItId = null;

    private String question = null;

    private String answer = null;

    private ClarificationStates state = ClarificationStates.NEW;

    private boolean sendToAll = false;

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
        return answer;
    }

    /**
     * @param answer
     *            The answer to set.
     */
    public void setAnswer(String answer) {
        this.answer = answer;
        state = ClarificationStates.ANSWERED;
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
        return sendToAll;
    }

    public void setSendToAll(boolean sendToAll) {
        this.sendToAll = sendToAll;
    }

    public String toString() {
        return "Clarification " + getNumber() + " " + getState() + " from " + getSubmitter() + " at " + getElapsedMins() + " id="
                + getElementId();
    }

    public ClientId getWhoJudgedItId() {
        return whoJudgedItId;
    }

    public void setWhoJudgedItId(ClientId whoJudgedItId) {
        this.whoJudgedItId = whoJudgedItId;
    }

    public boolean isSameAs(Clarification clarification) {
        try {
            if (deleted != clarification.isDeleted()) {
                return false;
            }

            if (!whoJudgedItId.equals(clarification.getWhoJudgedItId())) {
                return false;
            }

            if (!question.equals(clarification.getQuestion())) {
                return false;
            }

            if (!answer.equals(clarification.getAnswer())) {
                return false;
            }

            if (state != clarification.getState()) {
                return false;
            }

            if (sendToAll != clarification.isSendToAll()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            // TODO log to static Exception log
            return false;
        }
    }
}
