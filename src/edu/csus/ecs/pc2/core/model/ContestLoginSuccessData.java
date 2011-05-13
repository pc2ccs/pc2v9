package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Contest Data sent to clients from server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestLoginSuccessData implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 8280409708616920206L;
    private Account [] accounts;
    private BalloonSettings [] balloonSettingsArray;
    private Clarification[] clarifications;
    private ClientId[] localLoggedInUsers;
    private ClientId[] remoteLoggedInUsers;
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
    private Problem generalProblem;
    private String contestSecurityPassword;
    private String contestIdentifier;
    private Profile profile;
    private Profile [] profiles;
    private ContestTime contestTime;
    private int siteNumber;
    private ContestInformation information; 
    private FinalizeData finalizeData;

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
    public Problem getGeneralProblem() {
        return generalProblem;
    }
    public void setGeneralProblem(Problem generalProblem) {
        this.generalProblem = generalProblem;
    }
    public void setContestSecurityPassword(String contestSecurityPassword) {
        this.contestSecurityPassword = contestSecurityPassword;
    }
    public String getContestSecurityPassword() {
        return contestSecurityPassword;
    }
    public String getContestIdentifier() {
        return contestIdentifier;
    }
    public void setContestIdentifier(String contestIdentifier) {
        this.contestIdentifier = contestIdentifier;
    }
    public Profile getProfile() {
        return profile;
    }
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Profile[] getProfiles() {
        return profiles;
    }
    public void setProfiles(Profile[] profiles) {
        this.profiles = profiles;
    }
    public ContestTime getContestTime() {
        return contestTime;
    }
    public void setContestTime(ContestTime contestTime) {
        this.contestTime = contestTime;
    }
    public int getSiteNumber() {
        return siteNumber;
    }
    public void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;
    }
    public ContestInformation getContestInformation() {
        return information;
    }
    public void setContestInformation(ContestInformation information2) {
        this.information = information2;
    }
    
    public ClientId[] getLocalLoggedInUsers() {
        return localLoggedInUsers;
    }
    public void setLocalLoggedInUsers(ClientId[] localLoggedInUsers) {
        this.localLoggedInUsers = localLoggedInUsers;
    }
    public ClientId[] getRemoteLoggedInUsers() {
        return remoteLoggedInUsers;
    }
    public void setRemoteLoggedInUsers(ClientId[] remoteLoggedInUsers) {
        this.remoteLoggedInUsers = remoteLoggedInUsers;
    }
    public FinalizeData getFinalizeData() {
        return finalizeData;
    }
    public void setFinalizeData(FinalizeData finalizeData) {
        this.finalizeData = finalizeData;
    }
    
}
