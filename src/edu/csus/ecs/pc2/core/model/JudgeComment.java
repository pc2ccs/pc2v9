package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * A comment from a Judge.
 * 
 * A comment either to a team or judge from a judge. Typically used for commenting on Runs.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class JudgeComment implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5621859886959743359L;

    public static final String SVN_ID = "$Id$";

    /**
     * Judge who made the comment.
     */
    private ClientId judgeId;

    /**
     * The run id for this comment.
     */
    private ElementId runId;

    /**
     * The actual comment.
     */
    private String comment;

    public JudgeComment(ClientId judgeId, ElementId runId, String comment) {
        super();
        this.judgeId = judgeId;
        this.runId = runId;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public ClientId getJudgeId() {
        return judgeId;
    }

    public ElementId getRunId() {
        return runId;
    }
}
