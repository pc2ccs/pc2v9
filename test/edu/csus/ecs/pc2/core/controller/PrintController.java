package edu.csus.ecs.pc2.core.controller;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IPacketListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * A reference controller that can be used for debugging.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PrintController implements IInternalController {

    public void addNewAccount(Account account) {

        System.out.println("method addNewAccount");
        System.out.println("    account                : " + account);
    }

    public void addNewAccounts(Account[] account) {

        System.out.println("method addNewAccounts");
        System.out.println("    account                : " + account);
    }

    public void addNewBalloonSettings(BalloonSettings newBalloonSettings) {

        System.out.println("method addNewBalloonSettings");
        System.out.println("    newBalloonSettings     : " + newBalloonSettings);
    }

    public void addNewClientSettings(ClientSettings newClientSettings) {

        System.out.println("method addNewClientSettings");
        System.out.println("    newClientSettings      : " + newClientSettings);
    }

    public void addNewGroup(Group group) {

        System.out.println("method addNewGroup");
        System.out.println("    group                  : " + group);
    }

    public void addNewJudgement(Judgement judgement) {

        System.out.println("method addNewJudgement");
        System.out.println("    judgement              : " + judgement);
    }

    public void addNewLanguage(Language language) {

        System.out.println("method addNewLanguage");
        System.out.println("    language               : " + language);
    }

    public void addNewProblem(Problem problem, ProblemDataFiles problemDataFiles) {

        System.out.println("method addNewProblem");
        System.out.println("    problem                : " + problem);
        System.out.println("    problemDataFiles       : " + problemDataFiles);
    }

    public void addNewSite(Site site) {

        System.out.println("method addNewSite");
        System.out.println("    site                   : " + site);
    }

    public void addProblem(Problem problem) {

        System.out.println("method addProblem");
        System.out.println("    problem                : " + problem);
    }

    public void cancelClarification(Clarification clarification) {

        System.out.println("method cancelClarification");
        System.out.println("    clarification          : " + clarification);
    }

    public void cancelRun(Run run) {

        System.out.println("method cancelRun");
        System.out.println("    run                    : " + run);
    }

    public void checkOutClarification(Clarification clarification, boolean readOnly) {

        System.out.println("method checkOutClarification");
        System.out.println("    clarification          : " + clarification);
        System.out.println("    readOnly               : " + readOnly);
    }

    public void checkOutRejudgeRun(Run theRun) {

        System.out.println("method checkOutRejudgeRun");
        System.out.println("    theRun                 : " + theRun);
    }

    public void checkOutRun(Run run, boolean readOnly, boolean computerJudge) {

        System.out.println("method checkOutRun");
        System.out.println("    run                    : " + run);
        System.out.println("    readOnly               : " + readOnly);
        System.out.println("    computerJudge          : " + computerJudge);
    }

    public IInternalContest clientLogin(IInternalContest internalContest, String loginName, String password) throws Exception {

        System.out.println("method clientLogin");
        System.out.println("    internalContest        : " + internalContest);
        System.out.println("    loginName              : " + loginName);
        System.out.println("    password               : " + password);

        return null;
    }

    public void fetchRun(Run run) {

        System.out.println("method fetchRun");
        System.out.println("    run                    : " + run);
    }

    public void forceConnectionDrop(ConnectionHandlerID connectionHandlerID) {

        System.out.println("method forceConnectionDrop");
        System.out.println("    connectionHandlerID    : " + connectionHandlerID);
    }

    public void generateNewAccounts(String clientTypeName, int siteNumber, int count, int startNumber, boolean active) {

        System.out.println("method generateNewAccounts");
        System.out.println("    clientTypeName         : " + clientTypeName);
        System.out.println("    siteNumber             : " + siteNumber);
        System.out.println("    count                  : " + count);
        System.out.println("    startNumber            : " + startNumber);
        System.out.println("    active                 : " + active);
    }

    public void generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active) {

        System.out.println("method generateNewAccounts");
        System.out.println("    clientTypeName         : " + clientTypeName);
        System.out.println("    count                  : " + count);
        System.out.println("    startNumber            : " + startNumber);
        System.out.println("    active                 : " + active);
    }

    public String getHostContacted() {

        System.out.println("method getHostContacted");

        return null;
    }

    public Log getLog() {

        System.out.println("method getLog");

        return null;
    }

    public int getPortContacted() {

        System.out.println("method getPortContacted");

        return 0;
    }

    public ProblemDataFiles getProblemDataFiles(Problem problem) {

        System.out.println("method getProblemDataFiles");
        System.out.println("    problem                : " + problem);

        return null;
    }

    public int getSecurityLevel() {

        System.out.println("method getSecurityLevel");

        return 0;
    }

    public void initializeServer() {

        System.out.println("method initializeServer");
    }

    public boolean isClientAutoShutdown() {

        System.out.println("method isClientAutoShutdown");

        return false;
    }

    public void login(String loginName, String password) {

        System.out.println("method login");
        System.out.println("    loginName              : " + loginName);
        System.out.println("    password               : " + password);
    }

    public void logoffUser(ClientId clientId) {

        System.out.println("method logoffUser");
        System.out.println("    clientId               : " + clientId);
    }

    public void removeConnection(ConnectionHandlerID connectionHandlerID) {

        System.out.println("method removeConnection");
        System.out.println("    connectionHandlerID    : " + connectionHandlerID);
    }

    public void removeJudgement(Judgement judgement) {

        System.out.println("method removeJudgement");
        System.out.println("    judgement              : " + judgement);
    }

    public void removeLogin(ClientId clientId) {

        System.out.println("method removeLogin");
        System.out.println("    clientId               : " + clientId);
    }

    public void requestChangePassword(String oldPassword, String newPassword) {

        System.out.println("method requestChangePassword");
        System.out.println("    oldPassword            : " + oldPassword);
        System.out.println("    newPassword            : " + newPassword);
    }

    public void resetContest(ClientId clientResettingContest, boolean eraseProblems, boolean eraseLanguages) {

        System.out.println("method resetContest");
        System.out.println("    clientResettingContest : " + clientResettingContest);
        System.out.println("    eraseProblems          : " + eraseProblems);
        System.out.println("    eraseLanguages         : " + eraseLanguages);
    }

    public void sendCompilingMessage(Run run) {

        System.out.println("method sendCompilingMessage");
        System.out.println("    run                    : " + run);
    }

    public void sendExecutingMessage(Run run) {

        System.out.println("method sendExecutingMessage");
        System.out.println("    run                    : " + run);
    }

    public void sendSecurityMessage(String event, String message, ContestSecurityException contestSecurityException) {

        System.out.println("method sendSecurityMessage");
        System.out.println("    event                  : " + event);
        System.out.println("    message                : " + message);
        System.out.println("    contestSecurityException : " + contestSecurityException);
    }

    public void sendServerLoginRequest(int inSiteNumber) throws Exception {

        System.out.println("method sendServerLoginRequest");
        System.out.println("    inSiteNumber           : " + inSiteNumber);
    }

    public void sendToAdministrators(Packet packet) {

        System.out.println("method sendToAdministrators");
        System.out.println("    packet                 : " + packet);
    }

    public void sendToClient(Packet confirmPacket) {

        System.out.println("method sendToClient");
        System.out.println("    confirmPacket          : " + confirmPacket);
    }

    public void sendToJudges(Packet packet) {

        System.out.println("method sendToJudges");
        System.out.println("    packet                 : " + packet);
    }

    public void sendToLocalServer(Packet packet) {

        System.out.println("method sendToLocalServer");
        System.out.println("    packet                 : " + packet);
    }

    public void sendToRemoteServer(int siteNumber, Packet packet) {

        System.out.println("method sendToRemoteServer");
        System.out.println("    siteNumber             : " + siteNumber);
        System.out.println("    packet                 : " + packet);
    }

    public void sendToScoreboards(Packet packet) {

        System.out.println("method sendToScoreboards");
        System.out.println("    packet                 : " + packet);
    }

    public void sendToServers(Packet packet) {

        System.out.println("method sendToServers");
        System.out.println("    packet                 : " + packet);
    }

    public void sendToSpectators(Packet packet) {

        System.out.println("method sendToSpectators");
        System.out.println("    packet                 : " + packet);
    }

    public void sendToTeams(Packet packet) {

        System.out.println("method sendToTeams");
        System.out.println("    packet                 : " + packet);
    }

    public void sendValidatingMessage(Run run) {

        System.out.println("method sendValidatingMessage");
        System.out.println("    run                    : " + run);
    }

    public void setClientAutoShutdown(boolean clientAutoShutdown) {

        System.out.println("method setClientAutoShutdown");
        System.out.println("    clientAutoShutdown     : " + clientAutoShutdown);
    }

    public void setContestTime(ContestTime contestTime) {

        System.out.println("method setContestTime");
        System.out.println("    contestTime            : " + contestTime);
    }

    public void setJudgementList(Judgement[] judgementList) {

        System.out.println("method setJudgementList");
        System.out.println("    judgementList          : " + judgementList);
    }

    public void setSecurityLevel(int securityLevel) {

        System.out.println("method setSecurityLevel");
        System.out.println("    securityLevel          : " + securityLevel);
    }

    public void setSiteNumber(int i) {

        System.out.println("method setSiteNumber");
        System.out.println("    i                      : " + i);
    }

    public void shutdownTransport() {

        System.out.println("method shutdownTransport");
    }

    public void start(String[] stringArray) {

        System.out.println("method start");
        System.out.println("    stringArray            : " + stringArray);
    }

    public void startAllContestTimes() {

        System.out.println("method startAllContestTimes");
    }

    public void startContest(int inSiteNumber) {

        System.out.println("method startContest");
        System.out.println("    inSiteNumber           : " + inSiteNumber);
    }

    public void startMainUI(ClientId clientId) {

        System.out.println("method startMainUI");
        System.out.println("    clientId               : " + clientId);
    }

    public void stopAllContestTimes() {

        System.out.println("method stopAllContestTimes");
    }

    public void stopContest(int inSiteNumber) {

        System.out.println("method stopContest");
        System.out.println("    inSiteNumber           : " + inSiteNumber);
    }

    public void submitClarification(Problem problem, String question) {

        System.out.println("method submitClarification");
        System.out.println("    problem                : " + problem);
        System.out.println("    question               : " + question);
    }

    public void submitClarificationAnswer(Clarification clarification) {

        System.out.println("method submitClarificationAnswer");
        System.out.println("    clarification          : " + clarification);
    }

    public void submitRun(Problem problem, Language language, String filename, SerializedFile[] otherFiles) throws Exception {

        System.out.println("method submitRun");
        System.out.println("    problem                : " + problem);
        System.out.println("    language               : " + language);
        System.out.println("    filename               : " + filename);
        System.out.println("    otherFiles             : " + otherFiles);
    }

    public void submitRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {

        System.out.println("method submitRunJudgement");
        System.out.println("    run                    : " + run);
        System.out.println("    judgementRecord        : " + judgementRecord);
        System.out.println("    runResultFiles         : " + runResultFiles);
    }

    public void updateAccount(Account account) {

        System.out.println("method updateAccount");
        System.out.println("    account                : " + account);
    }

    public void updateAccounts(Account[] account) {

        System.out.println("method updateAccounts");
        System.out.println("    account                : " + account);
    }

    public void updateBalloonSettings(BalloonSettings newBalloonSettings) {

        System.out.println("method updateBalloonSettings");
        System.out.println("    newBalloonSettings     : " + newBalloonSettings);
    }

    public void updateClientSettings(ClientSettings clientSettings) {

        System.out.println("method updateClientSettings");
        System.out.println("    clientSettings         : " + clientSettings);
    }

    public void updateContestInformation(ContestInformation contestInformation) {

        System.out.println("method updateContestInformation");
        System.out.println("    contestInformation     : " + contestInformation);
    }

    public void updateContestTime(ContestTime newContestTime) {

        System.out.println("method updateContestTime");
        System.out.println("    newContestTime         : " + newContestTime);
    }

    public void updateGroup(Group group) {

        System.out.println("method updateGroup");
        System.out.println("    group                  : " + group);
    }

    public void updateLanguage(Language language) {

        System.out.println("method updateLanguage");
        System.out.println("    language               : " + language);
    }

    public void updateProblem(Problem problem) {

        System.out.println("method updateProblem");
        System.out.println("    problem                : " + problem);
    }

    public void updateProblem(Problem problem, ProblemDataFiles problemDataFiles) {

        System.out.println("method updateProblem");
        System.out.println("    problem                : " + problem);
        System.out.println("    problemDataFiles       : " + problemDataFiles);
    }

    public void updateRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {

        System.out.println("method updateRun");
        System.out.println("    run                    : " + run);
        System.out.println("    judgementRecord        : " + judgementRecord);
        System.out.println("    runResultFiles         : " + runResultFiles);
    }

    public void updateSite(Site newSite) {

        System.out.println("method updateSite");
        System.out.println("    newSite                : " + newSite);
    }

    public void updateJudgement(Judgement newJudgement) {

        System.out.println("method addNewJudgement");
        System.out.println("    judgement              : " + newJudgement);
        
    }

    public void initializeStorage(IStorage storage) {
        System.out.println("method initializeStorage");
        System.out.println("    storage                : " + storage);
    }

    public void cloneProfile(Profile profile, ProfileCloneSettings settings, boolean switchNow) {
        System.out.println("method cloneProfile");
        System.out.println("    profile                : " + profile);
        System.out.println("    settings               : " + settings);
        System.out.println("    switchNow              : " + switchNow);
    }

    public void switchProfile(Profile currentProfile, Profile switchToProfile, String contestPassword) {
        System.out.println("method switchProfile");
        System.out.println("    profile                : " + currentProfile);
        System.out.println("    new profile            : " + switchToProfile);
        System.out.println("    contestPassword        : " + contestPassword);
    }

    public void updateProfile(Profile profile) {
        System.out.println("method updateProfile");
        System.out.println("    profile                : " + profile);
    }

    public void setContest(IInternalContest newContest) {
        System.out.println("method setContest");
        System.out.println("    contest                : " + newContest);
    }

    public UIPlugin[] getPluginList() {
        System.out.println("method getPluginList");
        return null;
    }

    public void register(UIPlugin plugin) {
        System.out.println("method register "+plugin.getPluginTitle());
    }

    public void updateContestController(IInternalContest inContest, IInternalController inController) {
        System.out.println("method updateContestController");
        System.out.println("    contest                : " + inContest);
        System.out.println("    controller             : " + inController);
        
    }

    public void addPacketListener(IPacketListener packetListener) {
        System.out.println("method addPacketListener");
    }

    public void incomingPacket(Packet packet) {
        System.out.println("method incomingPacket");
        System.out.println("    packet                : " + packet);
    }

    public void outgoingPacket(Packet packet) {
        System.out.println("method outgoingPacket");
        System.out.println("    packet                : " + packet);
    }

    public void removePacketListener(IPacketListener packetListener) {
        System.out.println("method removePacketListener");
    }

    public boolean isUsingGUI() {
        return false;
    }

    public LogWindow startLogWindow(IInternalContest contest) {
        System.out.println("method startLogWindow");
        System.out.println("    contest                : " + contest);
        return null;
    }

    public void showLogWindow(boolean showWindow) {
        System.out.println("method showLogWindow");
        System.out.println("    showWindow            : " + showWindow);
        
    }

    public boolean isLogWindowVisible() {
        System.out.println("method isLogWindowVisible");
        return false;
    }

    public void logWarning(String string, Exception e) {
        System.out.println("method logWarning");
        System.out.println("    message               : " + string);
        System.out.println("    exception             : " + e.getMessage());
        e.printStackTrace();
        
    }
}
