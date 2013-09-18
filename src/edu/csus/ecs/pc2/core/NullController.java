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
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;
import edu.csus.ecs.pc2.ui.ILogWindow;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Null Controller, does nothing, nada, zip.
 * 
 * A skeleton implementation of the IInternalController.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NullController implements IInternalController {

    public void addNewAccount(Account account) {

    }

    public void addNewAccounts(Account[] account) {

    }

    public void addNewBalloonSettings(BalloonSettings newBalloonSettings) {

    }

    public void addNewCategory(Category newCategory) {

    }

    public void addNewClientSettings(ClientSettings newClientSettings) {

    }

    public void addNewGroup(Group group) {

    }

    public void addNewJudgement(Judgement judgement) {

    }

    public void addNewLanguage(Language language) {

    }

    public void addNewProblem(Problem problem, ProblemDataFiles problemDataFiles) {

    }

    public void addNewProblem(Problem[] problem, ProblemDataFiles[] problemDataFiles) {

    }

    public void addNewSite(Site site) {

    }

    public void addPacketListener(IPacketListener packetListener) {

    }

    public void addProblem(Problem problem) {

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
        return null;
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

    public void submitRun(Problem problem, Language language, String filename, SerializedFile[] otherFiles) throws Exception {

    }

    public void submitRun(Problem problem, Language language, String mainFileName, SerializedFile[] auxFileList, long overrideSubmissionTimeMS, long overrideRunId) throws Exception {

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
}
