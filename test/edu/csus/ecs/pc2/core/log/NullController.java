// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.log;

import java.io.IOException;
import java.util.logging.Level;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
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
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.transport.ITransportManager;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;
import edu.csus.ecs.pc2.ui.ILogWindow;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * A controller that does nothing.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NullController implements IInternalController{


    private Log log = null;

    protected NullController() {

    }

    public NullController(String logFilename) {
        log = new Log(logFilename);
    }

    @Override
    public void updateSite(Site newSite) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateProfile(Profile profile) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateProblem(Problem problem) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateLanguage(Language language) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateJudgement(Judgement newJudgement) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateGroup(Group group) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateFinalizeData(FinalizeData data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateContestTime(ContestTime newContestTime) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateContestInformation(ContestInformation contestInformation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateContestController(IInternalContest inContest, IInternalController inController) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateClientSettings(ClientSettings clientSettings) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateCategory(Category newCategory) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateCategories(Category[] categories) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateBalloonSettings(BalloonSettings newBalloonSettings) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateAccounts(Account[] account) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateAccount(Account account) {
        // TODO Auto-generated method stub

    }

    @Override
    public void syncProfileSubmissions(Profile profile) {
        // TODO Auto-generated method stub

    }

    @Override
    public void switchProfile(Profile currentProfile, Profile switchToProfile, String contestPassword) {
        // TODO Auto-generated method stub

    }

    @Override
    public void submitRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {
        // TODO Auto-generated method stub

    }

    public void submitJudgeRun(Problem problem, Language language, String mainFileName, SerializedFile[] auxFileList, long overrideSubmissionTimeMS) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void submitClarificationAnswer(Clarification clarification) {
        // TODO Auto-generated method stub

    }

    @Override
    public void submitClarification(Problem problem, String question) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopContest(int inSiteNumber) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopAllContestTimes() {
        // TODO Auto-generated method stub

    }

    @Override
    public void startPlayback(PlaybackInfo playbackInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startMainUI(ClientId clientId) {
        // TODO Auto-generated method stub

    }

    @Override
    public ILogWindow startLogWindow(IInternalContest contest) {
        getLog().log(Level.WARNING, "Attempted to use startLogWindow ", new Exception("Attempted to use startLogWindow"));
        return null;
    }

    @Override
    public void startContest(int inSiteNumber) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startAllContestTimes() {
        // TODO Auto-generated method stub

    }

    @Override
    public void start(String[] stringArray) {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutdownTransport() {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutdownServer(ClientId requestor, int siteNumber) {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutdownServer(ClientId requestor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutdownRemoteServers(ClientId requestor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showLogWindow(boolean showWindow) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUsingGUI(boolean usingGUI) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSiteNumber(int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSecurityLevel(int securityLevel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setJudgementList(Judgement[] judgementList) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setContestTime(ContestTime contestTime) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setContest(IInternalContest newContest) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setClientAutoShutdown(boolean clientAutoShutdown) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendValidatingMessage(Run run) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendToTeams(Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendToSpectators(Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendToServers(Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendToScoreboards(Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendToRemoteServer(int siteNumber, Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendToLocalServer(Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendToJudges(Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendToClient(Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendToAdministrators(Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendShutdownSite(int siteNumber) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendShutdownAllSites() {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendServerLoginRequest(int inSiteNumber) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendSecurityMessage(String event, String message, ContestSecurityException contestSecurityException) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendExecutingMessage(Run run) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendCompilingMessage(Run run) {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetContest(ClientId clientResettingContest, boolean eraseProblems, boolean eraseLanguages) {
        // TODO Auto-generated method stub

    }

    @Override
    public void requestChangePassword(String oldPassword, String newPassword) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePacketListener(IPacketListener packetListener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLogin(ClientId clientId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeJudgement(Judgement judgement) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeConnection(ConnectionHandlerID connectionHandlerID) {
        // TODO Auto-generated method stub

    }

    @Override
    public void register(UIPlugin plugin) {
        // TODO Auto-generated method stub

    }

    @Override
    public void outgoingPacket(Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void logoffUser(ClientId clientId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void login(String loginName, String password) {
        // TODO Auto-generated method stub

    }

    @Override
    public void logWarning(String string, Exception e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isUsingGUI() {
        return false;
    }

    @Override
    public boolean isLogWindowVisible() {
        return false;
    }

    @Override
    public boolean isClientAutoShutdown() {
        return false;
    }

    @Override
    public void initializeStorage(IStorage storage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initializeServer(IInternalContest contest) throws IOException, ClassNotFoundException, FileSecurityException {
        // TODO Auto-generated method stub

    }

    @Override
    public void incomingPacket(Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getSecurityLevel() {
        return 0;
    }

    @Override
    public ProblemDataFiles getProblemDataFiles(Problem problem) {
        return null;
    }

    @Override
    public int getPortContacted() {
        return 0;
    }

    @Override
    public UIPlugin[] getPluginList() {
        return null;
    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public String getHostContacted() {
        return null;
    }

    @Override
    public void generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active) {
        // TODO Auto-generated method stub

    }

    @Override
    public void generateNewAccounts(String clientTypeName, int siteNumber, int count, int startNumber, boolean active) {
        // TODO Auto-generated method stub

    }

    @Override
    public void forceConnectionDrop(ConnectionHandlerID connectionHandlerID) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fetchRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException {
        // TODO Auto-generated method stub

    }

    @Override
    public void cloneProfile(Profile profile, ProfileCloneSettings settings, boolean switchNow) {
        // TODO Auto-generated method stub

    }

    @Override
    public IInternalContest clientLogin(IInternalContest internalContest, String loginName, String password) throws Exception {
        return null;
    }

    @Override
    public void checkOutRun(Run run, boolean readOnly, boolean computerJudge) {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkOutRejudgeRun(Run theRun) {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkOutClarification(Clarification clarification, boolean readOnly) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelRun(Run run) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelClarification(Clarification clarification) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addProblem(Problem problem) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addPacketListener(IPacketListener packetListener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewSite(Site site) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewProblem(Problem[] problem, ProblemDataFiles[] problemDataFiles) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewLanguage(Language language) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewJudgement(Judgement judgement) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewGroup(Group group) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewClientSettings(ClientSettings newClientSettings) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewCategory(Category newCategory) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewBalloonSettings(BalloonSettings newBalloonSettings) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewAccounts(Account[] account) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNewAccount(Account account) {
        // TODO Auto-generated method stub

    }

    @Override
    public void submitJudgeRun(Problem problem, Language language, String filename, SerializedFile[] otherFiles) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void submitJudgeRun(Problem problem, Language language, String mainFileName, SerializedFile[] auxFileList, long overrideSubmissionTimeMS, long overrideRunId) throws Exception {
        // TODO Auto-generated method stub

    }
    @Override
    public void submitJudgeRun(Problem problem, Language language, String mainFileName, SerializedFile[] otherFiles, boolean overrideStopOnFailure) throws Exception {

    }

    @Override
    public void submitJudgeRun(Problem problem, Language language, String mainFileName, SerializedFile[] otherFiles,
            long overrideSubmissionTimeMS, long overrideRunId, boolean overrideStopOnFailure) throws Exception {

    }

    @Override
    public void submitJudgeRun(Problem problem, Language language, SerializedFile mainFile, SerializedFile[] otherFiles,
            long overrideSubmissionTimeMS, long overrideRunId, boolean overrideStopOnFailure) throws Exception {
    }

    @Override
    public void sendRunToSubmissionInterface(Run run, RunFiles runFiles) {
        // TODO Auto-generated method stub

    }

    @Override
    public void autoRegister(String teamInformation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendToJudgesAndOthers(Packet packet, boolean sendToServers) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setConnectionManager(ITransportManager connectionManager) {
        // TODO Auto-generated method stub

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

    @Override
    public void updateAutoStartInformation(IInternalContest aContest, IInternalController aController) {
        // TODO Auto-generated method stub

    }

    @Override
    public IInternalContest getContest() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void submitRun(ClientId submitter, Problem problem, Language language, SerializedFile mainSubmissionFile, SerializedFile[] additionalFiles, long overrideTimeMS, long overrideRunId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void submitRun(ClientId submitter, Problem problem, Language language, String entry_point, SerializedFile mainFile, SerializedFile[] auxFiles, long overrideTimeMS,
            long overrideSubmissionID) {
        // TODO Auto-generated method stub

    }

    @Override
    public void submitJudgeRun(Problem problem, Language language, SerializedFile mainFile, SerializedFile[] otherFiles, long overrideSubmissionTimeMS, long overrideRunId) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isSuppressConnectionsPaneDisplay() {
        return false;
    }

    @Override
    public boolean isSuppressLoginsPaneDisplay() {
        return false;
    }
}
