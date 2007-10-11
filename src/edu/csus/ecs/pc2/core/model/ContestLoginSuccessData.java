/**
 * 
 */
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Used to pass arrays from PacketHandler to PacketFactory.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL
public class ContestLoginSuccessData implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 8280409708616920206L;
    private Account [] accounts;
    private BalloonSettings [] balloonSettingsArray;
    private Clarification[] clarifications;
    private ClientId[] loggedInUsers;
    private ClientSettings[] clientSettings;
    private ConnectionHandlerID[] connectionHandlerIDs;
    private ContestTime[] contestTimes;
    private Group[] groups;
    private Judgement[] judgements;
    private Language[] languages;
    private Problem[] problems;
    private ProblemDataFiles[] problemDataFiles;
    private Run[] runs;
    private Site[] sites;
    /**
     * @return Returns the accounts.
     */
    public Account[] getAccounts() {
        return accounts;
    }
    /**
     * @param accounts The accounts to set.
     */
    public void setAccounts(Account[] accounts) {
        this.accounts = accounts;
    }
    /**
     * @return Returns the balloonSettingsArray.
     */
    public BalloonSettings[] getBalloonSettingsArray() {
        return balloonSettingsArray;
    }
    /**
     * @param balloonSettingsArray The balloonSettingsArray to set.
     */
    public void setBalloonSettingsArray(BalloonSettings[] balloonSettingsArray) {
        this.balloonSettingsArray = balloonSettingsArray;
    }
    /**
     * @return Returns the clarifications.
     */
    public Clarification[] getClarifications() {
        return clarifications;
    }
    /**
     * @param clarifications The clarifications to set.
     */
    public void setClarifications(Clarification[] clarifications) {
        this.clarifications = clarifications;
    }
    /**
     * @return Returns the clientSettings.
     */
    public ClientSettings[] getClientSettings() {
        return clientSettings;
    }
    /**
     * @param clientSettings The clientSettings to set.
     */
    public void setClientSettings(ClientSettings[] clientSettings) {
        this.clientSettings = clientSettings;
    }
    /**
     * @return Returns the connectionHandlerIDs.
     */
    public ConnectionHandlerID[] getConnectionHandlerIDs() {
        return connectionHandlerIDs;
    }
    /**
     * @param connectionHandlerIDs The connectionHandlerIDs to set.
     */
    public void setConnectionHandlerIDs(ConnectionHandlerID[] connectionHandlerIDs) {
        this.connectionHandlerIDs = connectionHandlerIDs;
    }
    /**
     * @return Returns the contestTimes.
     */
    public ContestTime[] getContestTimes() {
        return contestTimes;
    }
    /**
     * @param contestTimes The contestTimes to set.
     */
    public void setContestTimes(ContestTime[] contestTimes) {
        this.contestTimes = contestTimes;
    }
    /**
     * @return Returns the groups.
     */
    public Group[] getGroups() {
        return groups;
    }
    /**
     * @param groups The groups to set.
     */
    public void setGroups(Group[] groups) {
        this.groups = groups;
    }
    /**
     * @return Returns the judgements.
     */
    public Judgement[] getJudgements() {
        return judgements;
    }
    /**
     * @param judgements The judgements to set.
     */
    public void setJudgements(Judgement[] judgements) {
        this.judgements = judgements;
    }
    /**
     * @return Returns the languages.
     */
    public Language[] getLanguages() {
        return languages;
    }
    /**
     * @param languages The languages to set.
     */
    public void setLanguages(Language[] languages) {
        this.languages = languages;
    }
    /**
     * @return Returns the loggedInUsers.
     */
    public ClientId[] getLoggedInUsers() {
        return loggedInUsers;
    }
    /**
     * @param loggedInUsers The loggedInUsers to set.
     */
    public void setLoggedInUsers(ClientId[] loggedInUsers) {
        this.loggedInUsers = loggedInUsers;
    }
    /**
     * @return Returns the problemDataFiles.
     */
    public ProblemDataFiles[] getProblemDataFiles() {
        return problemDataFiles;
    }
    /**
     * @param problemDataFiles The problemDataFiles to set.
     */
    public void setProblemDataFiles(ProblemDataFiles[] problemDataFiles) {
        this.problemDataFiles = problemDataFiles;
    }
    /**
     * @return Returns the problems.
     */
    public Problem[] getProblems() {
        return problems;
    }
    /**
     * @param problems The problems to set.
     */
    public void setProblems(Problem[] problems) {
        this.problems = problems;
    }
    /**
     * @return Returns the runs.
     */
    public Run[] getRuns() {
        return runs;
    }
    /**
     * @param runs The runs to set.
     */
    public void setRuns(Run[] runs) {
        this.runs = runs;
    }
    /**
     * @return Returns the sites.
     */
    public Site[] getSites() {
        return sites;
    }
    /**
     * @param sites The sites to set.
     */
    public void setSites(Site[] sites) {
        this.sites = sites;
    }
}
