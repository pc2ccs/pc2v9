// Copyright (C) 1989-2020 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.io.IOException;

import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IPacketListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.transport.ITransportManager;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;
import edu.csus.ecs.pc2.ui.ILogWindow;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Mock Controller.
 * 
 * Some data is mock data some methods implement the controller/contest.
 * 
 */

public class MockController implements IInternalController {

    private IInternalContest contest;
    
    private PacketHandler handler = null;
    
    private Log log;

    public void addNewAccount(Account account) {
        contest.addAccount(account);
    }

    public void addNewAccounts(Account[] accounts) {
        contest.addAccounts(accounts);
    }

    public void addNewBalloonSettings(BalloonSettings newBalloonSettings) {

    }

    public void addNewCategory(Category newCategory) {

    }

    public void addNewClientSettings(ClientSettings newClientSettings) {
        contest.addClientSettings(newClientSettings);
    }

    public void addNewGroup(Group group) {
        contest.addGroup(group);
    }

    public void addNewJudgement(Judgement judgement) {
        contest.addJudgement(judgement);
    }

    public void addNewLanguage(Language language) {
        contest.addLanguage(language);
    }

    public void addNewProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        contest.addProblem(problem, problemDataFiles);
    }

    public void addNewProblem(Problem[] problem, ProblemDataFiles[] problemDataFiles) {
        for (int i = 0; i < problemDataFiles.length; i++) {
            contest.addProblem(problem[i], problemDataFiles[i]);
        }
    }

    public void addNewSite(Site site) {
        contest.addSite(site);
    }

    public void addPacketListener(IPacketListener packetListener) {

    }

    public void addProblem(Problem problem) {
        contest.addProblem(problem);
    }

    public void autoRegister(String teamInformation) {

    }

    public void cancelClarification(Clarification clarification) {

    }

    public void cancelRun(Run run) {

    }

    public void checkOutClarification(Clarification clarification, boolean readOnly) {

    }

    public void checkOutRejudgeRun(Run theRun) {

    }

    public void checkOutRun(Run run, boolean readOnly, boolean computerJudge) {

    }

    public IInternalContest clientLogin(IInternalContest internalContest, String loginName, String password) throws Exception {
        return null;
    }

    public void cloneProfile(Profile profile, ProfileCloneSettings settings, boolean switchNow) {

    }

    public void fetchRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException {

    }

    public void forceConnectionDrop(ConnectionHandlerID connectionHandlerID) {

    }

    public void generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active) {

    }

    public void generateNewAccounts(String clientTypeName, int siteNumber, int count, int startNumber, boolean active) {

    }

    public String getHostContacted() {
        return null;
    }

    public Log getLog() {
        return log;
    }

    public UIPlugin[] getPluginList() {
        return null;
    }

    public int getPortContacted() {
        return 0;
    }

    public ProblemDataFiles getProblemDataFiles(Problem problem) {
        return null;
    }

    public int getSecurityLevel() {
        return 0;
    }

    public void incomingPacket(Packet packet) {

    }

    public void initializeServer(IInternalContest contest) throws IOException, ClassNotFoundException, FileSecurityException {
        
        setContest(contest);
    }

    public void initializeStorage(IStorage storage) {

    }

    public boolean isClientAutoShutdown() {
        return false;
    }

    public boolean isLogWindowVisible() {
        return false;
    }

    public boolean isUsingGUI() {
        return false;
    }

    public void login(String loginName, String password) {

    }

    public void logoffUser(ClientId clientId) {

    }

    public void logWarning(String string, Exception e) {

    }

    public void outgoingPacket(Packet packet) {

    }

    public void register(UIPlugin plugin) {

    }

    public void removeConnection(ConnectionHandlerID connectionHandlerID) {

    }

    public void removeJudgement(Judgement judgement) {

    }

    public void removeLogin(ClientId clientId) {

    }

    public void removePacketListener(IPacketListener packetListener) {

    }

    public void requestChangePassword(String oldPassword, String newPassword) {

    }

    public void resetContest(ClientId clientResettingContest, boolean eraseProblems, boolean eraseLanguages) {

    }

    public void sendCompilingMessage(Run run) {

    }

    public void sendExecutingMessage(Run run) {

    }

    public void sendRunToSubmissionInterface(Run run, RunFiles runFiles) {

    }

    public void sendSecurityMessage(String event, String message, ContestSecurityException contestSecurityException) {

    }

    public void sendServerLoginRequest(int inSiteNumber) throws Exception {

    }

    public void sendShutdownAllSites() {

    }

    public void sendShutdownSite(int siteNumber) {

    }

    public void sendToAdministrators(Packet packet) {

    }

    public void sendToClient(Packet packet) {

    }

    public void sendToJudges(Packet packet) {

    }

    public void sendToJudgesAndOthers(Packet packet, boolean sendToServers) {

    }

    public void sendToLocalServer(Packet packet) {

    }

    public void sendToRemoteServer(int siteNumber, Packet packet) {

    }

    public void sendToScoreboards(Packet packet) {

    }

    public void sendToServers(Packet packet) {

    }

    public void sendToSpectators(Packet packet) {

    }

    public void sendToTeams(Packet packet) {

    }

    public void sendValidatingMessage(Run run) {

    }

    public void setClientAutoShutdown(boolean clientAutoShutdown) {

    }

    public void setContest(IInternalContest newContest) {
        this.contest = newContest;
        log = new Log("log", getLogName(contest.getClientId()));
        handler = new PacketHandler(this, contest);
    }

    private String getLogName(ClientId clientId) {
        return "mock." + stripChar(clientId.toString(), ' ');
    }

    public void setContestTime(ContestTime contestTime) {
        
    }

    public void setJudgementList(Judgement[] judgementList) {

    }

    public void setSecurityLevel(int securityLevel) {

    }

    public void setSiteNumber(int i) {

    }

    public void setUsingGUI(boolean usingGUI) {

    }

    public void showLogWindow(boolean showWindow) {

    }

    public void shutdownRemoteServers(ClientId requestor) {

    }

    public void shutdownServer(ClientId requestor) {

    }

    public void shutdownServer(ClientId requestor, int siteNumber) {

    }

    public void shutdownTransport() {

    }

    public void start(String[] stringArray) {

    }

    public void startAllContestTimes() {

    }

    public void startContest(int inSiteNumber) {

    }

    public ILogWindow startLogWindow(IInternalContest contest) {
        return null;
    }

    public void startMainUI(ClientId clientId) {

    }

    public void startPlayback(PlaybackInfo playbackInfo) {

    }

    public void stopAllContestTimes() {

    }

    public void stopContest(int inSiteNumber) {

    }

    public void submitClarification(Problem problem, String question) {

    }

    public void submitClarificationAnswer(Clarification clarification) {

    }

    public void submitJudgeRun(Problem problem, Language language, String filename, SerializedFile[] otherFiles) throws Exception {
        submitJudgeRun(problem, language, filename, otherFiles, 0, 0);
    }

    public void submitJudgeRun(Problem problem, Language language, String mainFileName, SerializedFile[] auxFileList, long overrideSubmissionTimeMS, long overrideRunId) throws Exception {
        
        Run submittedRun = new Run(contest.getClientId(), language, problem);
        submittedRun.setOverRideElapsedTimeMS(overrideSubmissionTimeMS);
        submittedRun.setOverRideNumber(new Long(overrideRunId).intValue());
        RunFiles runFiles = new RunFiles(submittedRun, mainFileName);
        contest.acceptRun(submittedRun, runFiles);
    }

    @Override
    public void submitJudgeRun(Problem problem, Language language, SerializedFile mainFile, SerializedFile[] otherFiles, long overrideSubmissionTimeMS, long overrideRunId) throws Exception {
        String mainFileName = mainFile.getName();
        submitJudgeRun(problem, language, mainFileName, otherFiles, overrideSubmissionTimeMS, overrideRunId);
    }
    
    public void submitRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {

    }

    public void switchProfile(Profile currentProfile, Profile switchToProfile, String contestPassword) {

    }

    public void syncProfileSubmissions(Profile profile) {

    }

    public void updateAccount(Account account) {

    }

    public void updateAccounts(Account[] account) {

    }

    public void updateBalloonSettings(BalloonSettings newBalloonSettings) {

    }

    public void updateCategories(Category[] categories) {

    }

    public void updateCategory(Category newCategory) {

    }

    public void updateClientSettings(ClientSettings clientSettings) {

    }

    public void updateContestController(IInternalContest inContest, IInternalController inController) {

    }

    public void updateContestInformation(ContestInformation contestInformation) {

    }

    public void updateContestTime(ContestTime newContestTime) {

    }

    public void updateFinalizeData(FinalizeData data) {

    }

    public void updateGroup(Group group) {

    }

    public void updateJudgement(Judgement newJudgement) {

    }

    public void updateLanguage(Language language) {

    }

    public void updateProblem(Problem problem) {

    }

    public void updateProblem(Problem problem, ProblemDataFiles problemDataFiles) {

    }

    public void updateProfile(Profile profile) {

    }

    public void updateRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {

    }

    public void updateSite(Site newSite) {

    }

    @Override
    public void setConnectionManager(ITransportManager connectionManager) {
        
    }

    @Override
    public void addNewLanguages(Language[] languages) {
        
    }

    @Override
    public void updateLanguages(Language[] languages) {
        
    }

    @Override
    public void addNewGroups(Group[] groups) {
        
    }

    @Override
    public void updateGroups(Group[] groups) {
        
    }

    /**
     * Creates an {@link AutoStarter} if none exists, and then instructs the AutoStarter to update its Scheduled Start Task to correspond to the Scheduled Start Time information in the
     * {@link ContestInformation} object in the received {@link IInternalContest}.
     * 
     * @param theContest
     *            - the Contest (Model) containing the Scheduled Start Time information
     * @param theController
     *            - the Controller to which this request applies
     */
    @Override
    public void updateAutoStartInformation(IInternalContest aContest, IInternalController aController) {

    }

    @Override
    public IInternalContest getContest() {
       return contest;
    }

    @Override
    public void submitRun(ClientId submitter, Problem problem, Language language, SerializedFile mainSubmissionFile, SerializedFile[] additionalFiles, long overrideTimeMS, long overrideRunId) {
        System.out.println("submitRun "+submitter+" "+problem+" "+language+" " +mainSubmissionFile.getName()+" aux file count "+additionalFiles.length+" time ="+overrideTimeMS+" run id ="+overrideRunId);
        
        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Run run = new Run(submitter, language, problem);

        try {
        
            RunFiles runFiles = new RunFiles(run, mainSubmissionFile, additionalFiles);
            Packet packet = PacketFactory.createSubmittedRun(contest.getClientId(), serverClientId, run, runFiles, overrideTimeMS, overrideRunId);
            handler.handlePacket(packet, null);

        } catch (Exception e) {
            rethrow (e);
        }
//          sendToLocalServer(packet);
        
    }

    private void rethrow(Exception e) {
        throw new RuntimeException(e);
    }
    
    protected String stripChar(String s, char ch) {
        int idx = s.indexOf(ch);
        while (idx > -1) {
            StringBuffer sb = new StringBuffer(s);
            idx = sb.indexOf(ch + "");
            while (idx > -1) {
                sb.deleteCharAt(idx);
                idx = sb.indexOf(ch + "");
            }
            return sb.toString();
        }
        return s;
    }

}
