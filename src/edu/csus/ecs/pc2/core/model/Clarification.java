// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
public class Clarification extends Submission {

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
        /**
         * Judge has created the clarification without a question to answer itself.
         */
        ANNOUNCED,
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
    
//    public String getDestinations() {
//        if (answerList.size() == 0) {
//            return null;
//        } else {
//            return getFirstAnswer().getDestinationsToString();
//        }  
//    }
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
        if (question.equals("")) {
            state = ClarificationStates.ANNOUNCED;
        }
        else {
            state = ClarificationStates.ANSWERED;
        }
        ClarificationAnswer clarificationAnswer = new ClarificationAnswer(answer, client, sendToAll, contestTime);
        addAnswer(clarificationAnswer);
    }
    
    /**
     * 
     * @param answer The answer to set.
     * @param client
     * @param contestTime
     * @param destinationGroup
     * @param destinationTeam
     * @param sendToAll
     */
    public void setAnswer(String answer, ClientId client, ContestTime contestTime, ElementId[] destinationGroup, ClientId[] destinationTeam, boolean sendToAll) {
        if (question.equals("")) {
            state = ClarificationStates.ANNOUNCED;
        }
        else {
            state = ClarificationStates.ANSWERED;
        }
        ClarificationAnswer clarificationAnswer = new ClarificationAnswer(answer, client, sendToAll, destinationGroup, destinationTeam, contestTime);
        addAnswer(clarificationAnswer);
    }

    public boolean isAnsweredorAnnounced() {
        return state == ClarificationStates.ANSWERED || state == ClarificationStates.ANNOUNCED;
    }
    
    public boolean isAnnounced() {
        return state == ClarificationStates.ANNOUNCED;
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
    
    /**
     * Checks if clarification has destinations other than the submitter excluding is Send to All.
     * @return
     */
    public boolean hasDestinationsOtherThanSubmitterorAllTeams() {
        if (!isAnsweredorAnnounced()) {
            return false;
        }
        return getFirstAnswer().isThereDestinationOtherThanSubmitter();
    }
    
    public ElementId[] getAllDestinationsGroup() {
        return getFirstAnswer().getAllDestinationsGroup();
    }
    
    public ClientId[] getAllDestinationsTeam() {
        return getFirstAnswer().getAllDestinationsTeam();
    }

    public boolean shouldAccountReceiveThisClarification(Account account) {
        
        if (isSendToAll()) {
            return true;
        }
        if (getSubmitter().equals(account.getClientId())){
            return true;
        }
        if (getAnswer() == null) {
            //THere is no answer to this clar yet hence there are no destinations for it.
            return false;
        }
        ElementId[] destinationGroup =  getAllDestinationsGroup();
        ClientId[] destinationTeam = getAllDestinationsTeam();
        
        if (destinationTeam != null) {
            for (ClientId team: destinationTeam) {
                if (team.equals(account.getClientId())){
                    return true;
                }
            }
        }
        
        if (destinationGroup != null) {
            for (ElementId destination: destinationGroup){
                if (account.isGroupMember(destination)) {  //checks if this announcement clar was sent to a group that this account belongs to
                    return true;
                }
            }
        }
        //check if an account has a group that matches with a group in 
        return false;
        
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
        if (question.equals("")) {
            state = ClarificationStates.ANNOUNCED;
        }
        else {
            state = ClarificationStates.ANSWERED;
        }
        answerList.add(clarificationAnswer);
    }

    public ClarificationAnswer[] getClarificationAnswers() {
        return (ClarificationAnswer[]) answerList.toArray(new ClarificationAnswer[answerList.size()]);
    }
}
