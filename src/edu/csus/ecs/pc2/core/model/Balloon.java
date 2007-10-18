/**
 * 
 */
package edu.csus.ecs.pc2.core.model;

/**
 * This object represents a generic balloon.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 * 
 */

// $HeadURL$
public class Balloon {

    private ClientId clientId;

    private ElementId problemId;

    private String problemTitle;

    private String clientTitle = "";

    private Run run;

    // TODO consider changing this to use an enum
    private String answer = ""; // yes | no | take

    // The Problems should be in ProblemDisplayList order
    private Problem[] problems;

    private BalloonSettings balloonSettings;

    /**
     * @return Returns the balloonSettings.
     */
    public BalloonSettings getBalloonSettings() {
        return balloonSettings;
    }

    /**
     * @param balloonSettings
     *            The balloonSettings to set.
     */
    public void setBalloonSettings(BalloonSettings balloonSettings) {
        this.balloonSettings = balloonSettings;
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
    }

    /**
     * @return Returns the clientId.
     */
    public ClientId getClientId() {
        return clientId;
    }

    /**
     * @param clientId
     *            The clientId to set.
     */
    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    /**
     * @return Returns the clientTitle.
     */
    public String getClientTitle() {
        return clientTitle;
    }

    /**
     * @param clientTitle
     *            The clientTitle to set.
     */
    public void setClientTitle(String clientTitle) {
        this.clientTitle = clientTitle;
    }

    /**
     * @return Returns the problemId.
     */
    public ElementId getProblemId() {
        return problemId;
    }

    /**
     * @param problemId
     *            The problemId to set.
     */
    public void setProblemId(ElementId problemId) {
        this.problemId = problemId;
    }

    /**
     * 
     */
    public Balloon(BalloonSettings theBalloonSettings, ClientId aClientId, String aClientTitle, ElementId aProblemId, String aProblemTitle, String theAnswer, Run theRun) {
        super();
        balloonSettings = theBalloonSettings;
        clientId = aClientId;
        clientTitle = aClientTitle;
        problemId = aProblemId;
        problemTitle = aProblemTitle;
        answer = theAnswer;
        run = theRun;
    }

    /**
     * @return Returns the problems.
     */
    public Problem[] getProblems() {
        return problems;
    }

    /**
     * @param problems
     *            The problems to set.
     */
    public void setProblems(Problem[] problems) {
        this.problems = problems;
    }

    /**
     * @return Returns the problemTitle.
     */
    public String getProblemTitle() {
        return problemTitle;
    }

    /**
     * @param problemTitle The problemTitle to set.
     */
    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    /**
     * May return null in case of a "take".
     * 
     * @return Returns the run.
     */
    public Run getRun() {
        return run;
    }

    /**
     * @param run The run to set.
     */
    public void setRun(Run run) {
        this.run = run;
    }

}
