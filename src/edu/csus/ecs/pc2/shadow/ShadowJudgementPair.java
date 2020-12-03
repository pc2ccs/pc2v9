// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

/**
 * This class encapsulates a pair of judgement values, one from a remote CCS and the other from the local PC2 system.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowJudgementPair {
    
    private String pc2Judgement ;
    private String remoteCCSJudgement ;
    private String submissionID;
    
    /**
     * Constructs a ShadowJudgementPair containing the judgements assigned by the local PC2 system and by the
     * remote CCS to the specified Submission.
     * 
     * @param submissionID the ID of the submission to which this judgement pair applies
     * @param pc2Judgement the judgement assigned by PC2 to the submission
     * @param remoteCCSJudgement the judgement assigned by the remote CCS to the submission
     */
    public ShadowJudgementPair(String submissionID, String pc2Judgement, String remoteCCSJudgement) {
        this.setSubmissionID(submissionID) ;
        this.setPc2Judgement(pc2Judgement);
        this.setRemoteCCSJudgement(remoteCCSJudgement);
    }

    /**
     * @return the pc2Judgement
     */
    public String getPc2Judgement() {
        return pc2Judgement;
    }

    /**
     * @param pc2Judgement the pc2Judgement to set
     */
    public void setPc2Judgement(String pc2Judgement) {
        this.pc2Judgement = pc2Judgement;
    }

    /**
     * @return the remoteCCSJudgement
     */
    public String getRemoteCCSJudgement() {
        return remoteCCSJudgement;
    }

    /**
     * @param remoteCCSJudgement the remoteCCSJudgement to set
     */
    public void setRemoteCCSJudgement(String remoteCCSJudgement) {
        this.remoteCCSJudgement = remoteCCSJudgement;
    }

    /**
     * @return the submissionID
     */
    public String getSubmissionID() {
        return submissionID;
    }

    /**
     * @param submissionID the submissionID to set
     */
    public void setSubmissionID(String submissionID) {
        this.submissionID = submissionID;
    }

}
