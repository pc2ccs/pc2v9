// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
        if (question.equals("") || question.equalsIgnoreCase("announcement")) {
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
     * @param destinationGroups An array of ElementIds indicating the group(s) to which this answer should be sent.
     * @param destinationTeams  An array of ClientIds indicating individual clients (teams) to which this answer should be sent.
     * @param sendToAll
     */
    public void setAnswer(String answer, ClientId client, ContestTime contestTime, ElementId[] destinationGroups, ClientId[] destinationTeams, boolean sendToAll) {
        if (question.equals("") || question.equalsIgnoreCase("announcement")) {
            state = ClarificationStates.ANNOUNCED;
        }
        else {
            state = ClarificationStates.ANSWERED;
        }
        ClarificationAnswer clarificationAnswer = new ClarificationAnswer(answer, client, sendToAll, destinationGroups, destinationTeams, contestTime);
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

    /**
     * Returns an indication of whether the specified account should be allowed to see this Clarification.
     * Note that "Announcements" are a type of Clarification, and are treated as such by this method.
     * 
     * @param account The PC2 account about which access to this clarification is being sought.
     * @return true if the specified account should be allowed to see this clarification; false if not.
     */
    public boolean shouldAccountReceiveThisClarification(Account account) {
        
        //check if this clar goes to everyone (in which case it is allowed to be seen by the specified account)
        if (isSendToAll()) {
            return true;
        }
        
        //check if this clar was submitted by the specified account 
        // (in which case it is allowed to be seen by that account) 
        if (getSubmitter().equals(account.getClientId())){
            return true;
        }
        
        //check to see if the specified account is in the list of "destination teams" to which 
        //this clar is targeted (in which case the clar is allowed to be seen by that account) 
        ClientId[] destinationTeam = getAllDestinationsTeam();
        if (destinationTeam != null) {
            for (ClientId team: destinationTeam) {
                if (team.equals(account.getClientId())){
                    return true;
                }
            }
        }
        
        //check to see if the specified account is in the list of "destination groups" to which
        //this clar is targeted (in which case the clar is allowed to be seen by that account) 
        ElementId[] destinationGroups =  getAllDestinationsGroup();
        if (destinationGroups != null) {
            for (ElementId destinationGroup: destinationGroups){
                //check if this clar was sent to a group that this account belongs to
                if (account.isGroupMember(destinationGroup)) {  
                    return true;
                }
            }
        }
        
        //there isn't any rule indicating that this clar should be allowed to go to the specified account
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
        if (question.equals("") || question.equalsIgnoreCase("announcement")) {
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
