// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
import edu.csus.ecs.pc2.core.transport.ITransportManager;
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

    @Override
    public void addNewAccount(Account account) {

    }

    @Override
    public void addNewAccounts(Account[] account) {

    }

    @Override
    public void addNewBalloonSettings(BalloonSettings newBalloonSettings) {

    }

    @Override
    public void addNewCategory(Category newCategory) {

    }

    @Override
    public void addNewClientSettings(ClientSettings newClientSettings) {

    }

    @Override
    public void addNewGroup(Group group) {

    }

    @Override
    public void addNewJudgement(Judgement judgement) {

    }

    @Override
    public void addNewLanguage(Language language) {

    }

    @Override
    public void addNewProblem(Problem problem, ProblemDataFiles problemDataFiles) {

    }

    @Override
    public void addNewProblem(Problem[] problem, ProblemDataFiles[] problemDataFiles) {

    }

    @Override
    public void addNewSite(Site site) {

    }

    @Override
    public void addPacketListener(IPacketListener packetListener) {

    }

    @Override
    public void addProblem(Problem problem) {

    }

    @Override
    public void autoRegister(String teamInformation) {

    }

    @Override
    public void cancelClarification(Clarification clarification) {

    }

    @Override
    public void cancelRun(Run run) {

    }

    @Override
    public void checkOutClarification(Clarification clarification, boolean readOnly) {

    }

    @Override
    public void checkOutRejudgeRun(Run theRun) {

    }

    @Override
    public void checkOutRun(Run run, boolean readOnly, boolean computerJudge) {

    }

    @Override
    public IInternalContest clientLogin(IInternalContest internalContest, String loginName, String password) throws Exception {
        return null;
    }

    @Override
    public void cloneProfile(Profile profile, ProfileCloneSettings settings, boolean switchNow) {

    }

    @Override
    public void fetchRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException {

    }

    @Override
    public void forceConnectionDrop(ConnectionHandlerID connectionHandlerID) {

    }

    @Override
    public void generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active) {

    }

    @Override
    public void generateNewAccounts(String clientTypeName, int siteNumber, int count, int startNumber, boolean active) {

    }

    @Override
    public String getHostContacted() {
        return null;
    }

    @Override
    public Log getLog() {
        return null;
    }

    @Override
    public UIPlugin[] getPluginList() {
        return null;
    }

    @Override
    public int getPortContacted() {
        return 0;
    }

    @Override
    public ProblemDataFiles getProblemDataFiles(Problem problem) {
        return null;
    }

    @Override
    public int getSecurityLevel() {
        return 0;
    }

    @Override
    public void incomingPacket(Packet packet) {

    }

    @Override
    public void initializeServer(IInternalContest contest) throws IOException, ClassNotFoundException, FileSecurityException {

    }

    @Override
    public void initializeStorage(IStorage storage) {

    }

    @Override
    public boolean isClientAutoShutdown() {
        return false;
    }

    @Override
    public boolean isLogWindowVisible() {
        return false;
    }

    @Override
    public boolean isUsingGUI() {
        return false;
    }

    @Override
    public void login(String loginName, String password) {

    }

    @Override
    public void logoffUser(ClientId clientId) {

    }

    @Override
    public void logWarning(String string, Exception e) {

    }

    @Override
    public void outgoingPacket(Packet packet) {

    }

    @Override
    public void register(UIPlugin plugin) {

    }

    @Override
    public void removeConnection(ConnectionHandlerID connectionHandlerID) {

    }

    @Override
    public void removeJudgement(Judgement judgement) {

    }

    @Override
    public void removeLogin(ClientId clientId) {

    }

    @Override
    public void removePacketListener(IPacketListener packetListener) {

    }

    @Override
    public void requestChangePassword(String oldPassword, String newPassword) {

    }

    @Override
    public void resetContest(ClientId clientResettingContest, boolean eraseProblems, boolean eraseLanguages) {

    }

    @Override
    public void sendCompilingMessage(Run run) {

    }

    @Override
    public void sendExecutingMessage(Run run) {

    }

    @Override
    public void sendRunToSubmissionInterface(Run run, RunFiles runFiles) {

    }

    @Override
    public void sendSecurityMessage(String event, String message, ContestSecurityException contestSecurityException) {

    }

    @Override
    public void sendServerLoginRequest(int inSiteNumber) throws Exception {

    }

    @Override
    public void sendShutdownAllSites() {

    }

    @Override
    public void sendShutdownSite(int siteNumber) {

    }

    @Override
    public void sendToAdministrators(Packet packet) {

    }

    @Override
    public void sendToClient(Packet packet) {

    }

    @Override
    public void sendToJudges(Packet packet) {

    }

    @Override
    public void sendToJudgesAndOthers(Packet packet, boolean sendToServers) {

    }

    @Override
    public void sendToLocalServer(Packet packet) {

    }

    @Override
    public void sendToRemoteServer(int siteNumber, Packet packet) {

    }

    @Override
    public void sendToScoreboards(Packet packet) {

    }

    @Override
    public void sendToServers(Packet packet) {

    }

    @Override
    public void sendToSpectators(Packet packet) {

    }

    @Override
    public void sendToTeams(Packet packet) {

    }

    @Override
    public void sendValidatingMessage(Run run) {

    }

    @Override
    public void setClientAutoShutdown(boolean clientAutoShutdown) {

    }

    @Override
    public void setContest(IInternalContest newContest) {

    }

    @Override
    public void setContestTime(ContestTime contestTime) {

    }

    @Override
    public void setJudgementList(Judgement[] judgementList) {

    }

    @Override
    public void setSecurityLevel(int securityLevel) {

    }

    @Override
    public void setSiteNumber(int i) {

    }

    @Override
    public void setUsingGUI(boolean usingGUI) {

    }

    @Override
    public void showLogWindow(boolean showWindow) {

    }

    @Override
    public void shutdownRemoteServers(ClientId requestor) {

    }

    @Override
    public void shutdownServer(ClientId requestor) {

    }

    @Override
    public void shutdownServer(ClientId requestor, int siteNumber) {

    }

    @Override
    public void shutdownTransport() {

    }

    @Override
    public void start(String[] stringArray) {

    }

    @Override
    public void startAllContestTimes() {

    }

    @Override
    public void startContest(int inSiteNumber) {

    }

    @Override
    public ILogWindow startLogWindow(IInternalContest contest) {
        return null;
    }

    @Override
    public void startMainUI(ClientId clientId) {

    }

    @Override
    public void startPlayback(PlaybackInfo playbackInfo) {

    }

    @Override
    public void stopAllContestTimes() {

    }

    @Override
    public void stopContest(int inSiteNumber) {

    }

    @Override
    public void submitClarification(Problem problem, String question) {

    }

    @Override
    public void submitClarificationAnswer(Clarification clarification) {

    }

    @Override
    public void submitJudgeRun(Problem problem, Language language, String filename, SerializedFile[] otherFiles) throws Exception {

    }

    @Override
    public void submitJudgeRun(Problem problem, Language language, String mainFileName, SerializedFile[] auxFileList, long overrideSubmissionTimeMS, long overrideRunId) throws Exception {

    }

    @Override
    public void submitRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {

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
    public void switchProfile(Profile currentProfile, Profile switchToProfile, String contestPassword) {

    }

    @Override
    public void syncProfileSubmissions(Profile profile) {

    }

    @Override
    public void updateAccount(Account account) {

    }

    @Override
    public void updateAccounts(Account[] account) {

    }

    @Override
    public void updateBalloonSettings(BalloonSettings newBalloonSettings) {

    }

    @Override
    public void updateCategories(Category[] categories) {

    }

    @Override
    public void updateCategory(Category newCategory) {

    }

    @Override
    public void updateClientSettings(ClientSettings clientSettings) {

    }

    @Override
    public void updateContestController(IInternalContest inContest, IInternalController inController) {

    }

    @Override
    public void updateContestInformation(ContestInformation contestInformation) {

    }

    @Override
    public void updateContestTime(ContestTime newContestTime) {

    }

    @Override
    public void updateFinalizeData(FinalizeData data) {

    }

    @Override
    public void updateGroup(Group group) {

    }

    @Override
    public void updateJudgement(Judgement newJudgement) {

    }

    @Override
    public void updateLanguage(Language language) {

    }

    @Override
    public void updateProblem(Problem problem) {

    }

    @Override
    public void updateProblem(Problem problem, ProblemDataFiles problemDataFiles) {

    }

    @Override
    public void updateProfile(Profile profile) {

    }

    @Override
    public void updateRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {

    }

    @Override
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
        return null;
    }

    @Override
    public void submitRun(ClientId submitter, Problem problem, Language language, SerializedFile mainSubmissionFile, SerializedFile[] additionalFiles, long overrideTimeMS, long overrideRunId) {

    }

    @Override
    public void submitRun(ClientId submitter, Problem problem, Language language, String entry_point, SerializedFile mainSubmissionFile, SerializedFile[] additionalFiles, long overrideTimeMS, long overrideRunId) {

    }

    @Override
    public void submitJudgeRun(Problem problem, Language language, SerializedFile mainFile, SerializedFile[] otherFiles, long overrideSubmissionTimeMS, long overrideRunId) throws Exception {

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
