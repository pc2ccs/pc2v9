// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AvailableAJ;
import edu.csus.ecs.pc2.core.model.AvailableAJRun;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.ProblemDataFilesList;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.Submission;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Load contest data from a {@link Packet} into a model.
 * 
 * The add and set methods will extract contest data from a packet and
 * load into the contet model (contest).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestLoader {

    /**
     * Unpack and add list of accounts to contest.
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addAllAccountsToModel(IInternalContest contest, IInternalController controller, Packet packet) {

        try {

            Account[] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
            if (accounts != null) {
                for (Account account : accounts) {

                    if (isServer(contest)) {
                        if (!isThisSite(contest, account)) {
                            contest.updateAccount(account);
                        }
                    } else {
                        contest.updateAccount(account);
                    }
                }
                
                for (Type type : ClientType.Type.values()) {
                    addMissingLocalAccounts(contest, accounts, type);
                }
                
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Create missing accounts if NO account exist in current contest.
     * 
     * Intended to insure that local account exist.
     * 
     * @param contest
     * @param accounts
     * @param type account type
     */
    private void addMissingLocalAccounts(IInternalContest contest, Account[] accounts, Type type) {

        if (isServer(contest)) {

            Vector<Account> vector = contest.getAccounts(type, contest.getSiteNumber());
            if (vector.size() == 0) {
                // No local accounts for account type, need to add them.

                for (Account account : accounts) {
                    if (isThisSite(contest, account)) {
                        if (account.getClientId().getClientType().equals(type)) {
                            contest.updateAccount(account);
                        }
                    }
                }
            }
        }
    }

    /**
     * Add list of clarifications to contest.
     * 
     * @param controller
     * @param contest
     * 
     * @param packet
     */
    protected void addAllClarificationsToModel(IInternalContest contest, IInternalController controller, Packet packet) {

        try {
            Clarification[] clarifications = (Clarification[]) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION_LIST);
            if (clarifications != null) {
                for (Clarification clarification : clarifications) {

                    if ((!isServer(contest)) || (!isThisSite(contest, clarification))) {
                        if (contest.getClarification(clarification.getElementId()) != null) {
                            contest.updateClarification(clarification, null);
                        } else {
                            contest.addClarification(clarification);
                        }
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Add client settings into contest.
     * 
     * @param controller
     * @param contest
     * @param packet
     */
    protected void addAllClientSettingsToModel(IInternalContest contest, IInternalController controller, Packet packet) {
        try {
            ClientSettings[] clientSettings = (ClientSettings[]) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_SETTINGS_LIST);
            if (clientSettings != null) {
                for (ClientSettings clientSettings2 : clientSettings) {

                    ClientId clientId = clientSettings2.getClientId();

                    if (isServer(contest)) {
                        if (!isThisSite(contest, clientId)) {
                            // only add settings if NOT this site.
                            contest.updateClientSettings(clientSettings2);
                        }
                    } else {
                        contest.updateClientSettings(clientSettings2);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }

    }

    /**
     * Add connection Ids into contest.
     * 
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addAllConnectionIdsToModel(IInternalContest contest, IInternalController controller, Packet packet) {

        try {
            ConnectionHandlerID[] connectionHandlerIDs = (ConnectionHandlerID[]) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID_LIST);
            if (connectionHandlerIDs != null) {
                for (ConnectionHandlerID connectionHandlerID : connectionHandlerIDs) {
                    contest.connectionEstablished(connectionHandlerID);
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Add Contest times into contest. 
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addAllContestTimesToModel(IInternalContest contest, IInternalController controller, Packet packet) {
        try {
            ContestTime[] contestTimes = (ContestTime[]) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME_LIST);
            if (contestTimes != null) {
                for (ContestTime contestTime : contestTimes) {
                    if (contest.getSiteNumber() != contestTime.getSiteNumber()) {
                        // Update other sites contestTime, do not touch ours.
                        if (contest.getContestTime(contestTime.getSiteNumber()) != null) {
                            contest.updateContestTime(contestTime);
                        } else {
                            contest.addContestTime(contestTime);
                        }
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Unpack and add list of runs to contest.
     * 
     * @param controller
     * @param contest
     * @param packet
     */
    protected void addAllRunsToModel(IInternalContest contest, IInternalController controller, Packet packet) {

        try {
            Run[] runs = (Run[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_LIST);
            if (runs != null) {
                for (Run run : runs) {
                    if ((!isServer(contest)) || (!isThisSite(contest, run))) {
                        contest.addRun(run);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Add Balloon Settings to model.
     * 
     * @param controller
     * @param contest
     * 
     * @param packet
     */
    protected void addBalloonSettingsToModel(IInternalContest contest, IInternalController controller, Packet packet) {
        try {
            BalloonSettings[] balloonSettings = (BalloonSettings[]) PacketFactory.getObjectValue(packet, PacketFactory.BALLOON_SETTINGS_LIST);
            if (balloonSettings != null) {
                for (BalloonSettings balloonSettings2 : balloonSettings) {
                    contest.updateBalloonSettings(balloonSettings2);
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Add contest info into contest.
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addContestInformationToModel(IInternalContest contest, IInternalController controller, Packet packet) {
        try {
            ContestInformation contestInformation = (ContestInformation) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_INFORMATION);
            if (contestInformation != null) {
                contest.updateContestInformation(contestInformation);
            }

        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }

    }

    protected void setFinalizeData (IInternalContest contest, IInternalController controller, Packet packet) {
        try {
            
            FinalizeData finalizeData = (FinalizeData) PacketFactory.getObjectValue(packet, PacketFactory.FINALIZE_DATA);
            if (finalizeData != null) {
                contest.setFinalizeData(finalizeData);
                if (finalizeData.isCertified()) {
                    controller.getLog().log(Log.INFO, "Contest Certified by '" + finalizeData.getComment() + "'");
                }
            }

        } catch (Exception e) {
            controller.logWarning("Exception logged in load Finalize Data ", e);
        }

    }
    
    /**
     * Add general problem into contest.
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addGeneralProblemToModel(IInternalContest contest, IInternalController controller, Packet packet) {
        try {
            Problem generalProblem = (Problem) PacketFactory.getObjectValue(packet, PacketFactory.GENERAL_PROBLEM);
            if (generalProblem != null) {
                contest.setGeneralProblem(generalProblem);
            }

        } catch (Exception e) {
            controller.logWarning("Exception logged in General Problem ", e);
        }

    }

    /**
     * Add groups into contest.
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addGroupsToModel(IInternalContest contest, IInternalController controller, Packet packet) {

        try {
            Group[] groups = (Group[]) PacketFactory.getObjectValue(packet, PacketFactory.GROUP_LIST);
            if (groups != null) {
                for (Group group : groups) {
                    if (contest.getGroup(group.getElementId()) != null) {
                        contest.updateGroup(group);
                    } else {
                        contest.addGroup(group);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }

    }

    /**
     * Add/Merge judgements into model.
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addJudgementsToModel(IInternalContest contest, IInternalController controller, Packet packet) {
        try {
            Judgement[] judgements = (Judgement[]) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_LIST);
            if (judgements != null) {
                for (Judgement judgement : judgements) {
                    if (contest.getJudgement(judgement.getElementId()) != null) {
                        contest.updateJudgement(judgement);
                    } else {
                        contest.addJudgement(judgement);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }

    }

    /**
     * Add/Merge categories into model.
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addCategoriesToModel(IInternalContest contest, IInternalController controller, Packet packet) {
        try {
            Category[] categories = (Category[]) PacketFactory.getObjectValue(packet, PacketFactory.CATEGORY_LIST);
            if (categories != null) {
                for (Category category : categories) {
                    if (contest.getCategory(category.getElementId()) != null) {
                        contest.updateCategory(category);
                    } else {
                        contest.addCategory(category);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }

    }


    
    /**
     * Add/merge languages into model.
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addLanguagesToModel(IInternalContest contest, IInternalController controller, Packet packet) {

        try {
            Language[] languages = (Language[]) PacketFactory.getObjectValue(packet, PacketFactory.LANGUAGE_LIST);
            if (languages != null) {
                for (Language language : languages) {
                    if (contest.getLanguage(language.getElementId()) != null) {
                        contest.updateLanguage(language);
                    } else {
                        contest.addLanguage(language);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }

    }

    /**
     * Add Logins into model.
     * 
     * @param controller
     * @param contest
     * 
     * @param packet
     */
    protected void addLoginsToModel(IInternalContest contest, IInternalController controller, Packet packet) {

        try {
            ClientId[] clientIds = (ClientId[]) PacketFactory.getObjectValue(packet, PacketFactory.REMOTE_LOGGED_IN_USERS);
            if (clientIds != null) {
                for (ClientId clientId : clientIds) {
                    
                    try {
                        if (isServer(contest)) {
                            // Is a server, only add remote logins

                            if (!contest.isLocalLoggedIn(clientId) && !isThisSite(contest, clientId)) {
                                // Only add into remote list on server, if they are not already logged in
                                // TODO someday soon load logins with their connectionIds
                                ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber() + clientId);

                                contest.addRemoteLogin(clientId, fakeId);
                            }

                        } else {
                            // Not a server InternalController - add everything

                            // TODO someday soon load logins with their connectionIds
                            ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber() + clientId);
                            contest.addRemoteLogin(clientId, fakeId);
                        }
                    } catch (Exception e) {
                        controller.logWarning("Exception in adding login to model ", e);
                    }
                }
            }
            
            clientIds = (ClientId[]) PacketFactory.getObjectValue(packet, PacketFactory.LOCAL_LOGGED_IN_USERS);
            if (clientIds != null) {
                for (ClientId clientId : clientIds) {
                    
                    try {
                        if (isServer(contest)) {
                            // Is a server, only add remote logins

                            if (!contest.isLocalLoggedIn(clientId) && !isThisSite(contest, clientId)) {
                                // Only add into remote list on server, if they are not already logged in
                                // TODO someday soon load logins with their connectionIds
                                ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber() + clientId);

                                contest.addRemoteLogin(clientId, fakeId);
                            }

                        } else {
                            // Not a server InternalController - add everything

                            // TODO someday soon load logins with their connectionIds
                            ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber() + clientId);
                            contest.addLocalLogin(clientId, fakeId);
                        }
                    } catch (Exception e) {
                        controller.logWarning("Exception in adding login to model ", e);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception in adding logins to model ", e);
        }

    }

    /**
     * Add/merge both problems and problem data files into contest.
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addProblemsToModel(IInternalContest contest, IInternalController controller, Packet packet) {

        // First add all the problem data files to a list.

        ProblemDataFilesList problemDataFilesList = new ProblemDataFilesList();

        try {
            ProblemDataFiles[] problemDataFiles = (ProblemDataFiles[]) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM_DATA_FILES);
            if (problemDataFiles != null) {
                for (ProblemDataFiles problemDataFile : problemDataFiles) {
                    problemDataFilesList.add(problemDataFile);
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }

        try {
            Problem[] problems = (Problem[]) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM_LIST);
            if (problems != null) {
                for (Problem problem : problems) {

                    // Now add both problem and potentially problem data files into contest.

                    ProblemDataFiles problemDataFiles = (ProblemDataFiles) problemDataFilesList.get(problem);
                    if (contest.getProblem(problem.getElementId()) != null) {
                        if (problemDataFiles == null) {
                            contest.updateProblem(problem);
                        } else {
                            contest.updateProblem(problem, problemDataFiles);
                        }
                    } else {
                        if (problemDataFiles == null) {
                            contest.addProblem(problem);
                        } else {
                            contest.addProblem(problem, problemDataFiles);
                        }
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Add Balloon Settings to model.
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addProfilesToModel(IInternalContest contest, IInternalController controller, Packet packet) {
        try {
            Profile[] profiles = (Profile[]) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE_LIST);
            if (profiles != null) {
                for (Profile profile : profiles) {
                    contest.updateProfile(profile);
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Add other sites' accounts to model.
     * 
     * @param contest
     * @param controller
     * @param packet
     * @param remoteSiteNumber
     */
    protected void addRemoteAccountsToModel(IInternalContest contest, IInternalController controller, Packet packet, int remoteSiteNumber) {
        try {

            Account[] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
            if (accounts != null) {
                for (Account account : accounts) {
                    if (remoteSiteNumber == account.getSiteNumber()) {
                        contest.updateAccount(account);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }

    }
    
    /**
     * Load accounts from packet if no accounts exist.
     * 
     * Checks whether there are any accounts of type in contest.  If
     * there are no accounts then will load all accounts of that type
     * into contest/model.
     * 
     * @param contest model/data for the contest
     * @param controller
     * @param packet input packet with accounts
     * @param type kind of accounts to check for/add
     */
    
    public void loadIfMissingAccountToModel(IInternalContest contest, IInternalController controller, Packet packet, Type type) {

        try {

            int siteNumber = contest.getSiteNumber();

            /**
             * Number of accounts of account type type :)
             */
            int count = contest.getAccounts(type, siteNumber).size();

            int numAdded = 0;

            if (count == 0) {

                /**
                 * No accounts found will replace them with ones from the packet.
                 */

                Account[] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
                if (accounts != null) {
                    for (Account account : accounts) {
                        if (siteNumber == account.getSiteNumber()) {
                            if (account.getClientId().getClientType().equals(type)) {
                                if (contest.getAccount(account.getClientId()) == null) {
                                    contest.updateAccount(account);
                                    numAdded++;
                                }
                            }
                        }
                    }
                }
                controller.getLog().log(Log.INFO, "Loaded " + numAdded + " " + type + " Accounts for site " + siteNumber);

            } else {
                controller.getLog().log(Log.INFO, "No accounts loaded " + count + " accounts exists for site " + siteNumber);
            }

        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }

    }

    /**
     * Add other sites' client settings to model.
     * 
     * @param contest
     * @param controller
     * @param packet
     * @param remoteSiteNumber
     */
    protected void addRemoteAllClientSettingsToModel(IInternalContest contest, IInternalController controller, Packet packet, int remoteSiteNumber) {

        try {
            ClientSettings[] clientSettings = (ClientSettings[]) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_SETTINGS_LIST);
            if (clientSettings != null) {
                for (ClientSettings clientSettings2 : clientSettings) {

                    if (remoteSiteNumber == clientSettings2.getSiteNumber()) {
                        contest.updateClientSettings(clientSettings2);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Add other sites' clarifications into model. 
     * 
     * @param contest
     * @param controller
     * @param packet
     * @param remoteSiteNumber
     */
    protected void addRemoteClarificationsToModel(IInternalContest contest, IInternalController controller, Packet packet, int remoteSiteNumber) {
        try {
            Clarification[] clarifications = (Clarification[]) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION_LIST);
            if (clarifications != null) {
                for (Clarification clarification : clarifications) {

                    if (remoteSiteNumber == clarification.getSiteNumber()) {
                        contest.addClarification(clarification);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Add other sites' contest time into model.
     * 
     * @param contest
     * @param controller
     * @param packet
     * @param remoteSiteNumber
     */
    protected void addRemoteContestTimesToModel(IInternalContest contest, IInternalController controller, Packet packet, int remoteSiteNumber) {
        try {
            ContestTime[] contestTimes = (ContestTime[]) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME_LIST);
            if (contestTimes != null) {
                for (ContestTime contestTime : contestTimes) {
                    if (remoteSiteNumber == contestTime.getSiteNumber()) {
                        // Update only other site's time
                        if (contest.getContestTime(contestTime.getSiteNumber()) != null) {
                            contest.updateContestTime(contestTime);
                        } else {
                            contest.addContestTime(contestTime);
                        }
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Add other sites' logins into model.
     * 
     * @param contest
     * @param controller
     * @param packet
     * @param remoteSiteNumber
     */
    protected void addRemoteLoginsToModel(IInternalContest contest, IInternalController controller, Packet packet, int remoteSiteNumber) {

        try {

            ClientId[] clientIds = (ClientId[]) PacketFactory.getObjectValue(packet, PacketFactory.LOCAL_LOGGED_IN_USERS);
            if (clientIds != null) {
                for (ClientId clientId : clientIds) {
                    try {
                        if (isServer(clientId)) {
                            if (!contest.isLocalLoggedIn(clientId)) {

                                if (!isThisSite(contest, clientId)) {

                                    // Only add server into remote list on server, if they are not already logged in

                                    // TODO someday soon load logins with their connectionIds
                                    ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber() + clientId);
                                    contest.addRemoteLogin(clientId, fakeId);
                                }
                            }
                        } else if (remoteSiteNumber == clientId.getSiteNumber()) {
                            // TODO someday soon load logins with their connectionIds
                            
                            // Add only clients from remoteServer into model as remote logins
                            
                            ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber() + "-" + clientId);
                            contest.addRemoteLogin(clientId, fakeId);
                        }
                    } catch (Exception e) {
                        controller.logWarning("Exception while adding remote login ", e);
                    }
                }
            }
            
            clientIds = (ClientId[]) PacketFactory.getObjectValue(packet, PacketFactory.REMOTE_LOGGED_IN_USERS);
            if (clientIds != null) {
                for (ClientId clientId : clientIds) {
                    try {
                        if (isServer(clientId)) {
                            if (!contest.isLocalLoggedIn(clientId)) {

                                if (!isThisSite(contest, clientId)) {

                                    // Only add site into remote list on server, if they are not already logged in

                                    // TODO someday soon load logins with their connectionIds
                                    ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber() + clientId);
                                    contest.addRemoteLogin(clientId, fakeId);
                                }
                            }
                        } else if (remoteSiteNumber == clientId.getSiteNumber()) {
                            // TODO someday soon load logins with their connectionIds
                            
                            // Add only clients from remoteServer into model as remote logins
                            
                            ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber() + "-" + clientId);
                            contest.addRemoteLogin(clientId, fakeId);
                        }
                    } catch (Exception e) {
                        controller.logWarning("Exception while adding remote login ", e);
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception while adding remote logins ", e);
        }
    }

    /**
     * Add other sites' runs into model.
     * 
     * @param contest
     * @param controller
     * @param packet
     * @param localSiteNumber this site, will not add runs from this site
     */
    protected void addRemoteRunsToModel(IInternalContest contest, IInternalController controller, Packet packet) {

        try {
            Run[] runs = (Run[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_LIST);
            if (runs != null) {
                for (Run run : runs) {
                    if (! isThisSite(contest, run.getSiteNumber())) {
                        contest.updateRun(run, packet.getSourceId());
                    }
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Add sites into model.
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    protected void addSitesToModel(IInternalContest contest, IInternalController controller, Packet packet) {
        try {
            Site[] sites = (Site[]) PacketFactory.getObjectValue(packet, PacketFactory.SITE_LIST);
            if (sites != null) {
                for (Site site : sites) {
                    contest.updateSite(site);
                }
            }
        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }
    }

    /**
     * Initialize this sites contest time.
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    protected void initializeContestTime(IInternalContest contest, IInternalController controller, Packet packet) {

        if (isServer(contest)) {
            if (contest.getContestTime() == null) {
                ContestTime contestTime = new ContestTime(contest.getSiteNumber());
                contest.addContestTime(contestTime);
            }
        }
    }

    /**
     * Is a server?.
     * @param id
     * @return true if id is a server.
     */
    private boolean isServer(ClientId id) {
        return id != null && id.getClientType().equals(ClientType.Type.SERVER);
    }

    /**
     * Is a server?.
     * @param contest
     * @return true if contest user is a server.
     */
    private boolean isServer(IInternalContest contest) {
        ClientId id = contest.getClientId();
        return id != null && id.getClientType().equals(ClientType.Type.SERVER);
    }

    /**
     * Is account from this site?.
     * 
     * @param contest
     * @param account
     * @return
     */
    private boolean isThisSite(IInternalContest contest, Account account) {
        return account.getSiteNumber() == contest.getSiteNumber();
    }

    /**
     * Is this clientId from this site?.
     * @param contest
     * @param id
     * @return
     */
    private boolean isThisSite(IInternalContest contest, ClientId id) {
        return isThisSite(contest, id.getSiteNumber());
    }

    /**
     * Is this site number the same as this sites?.
     * @param contest
     * @param siteNumber
     * @return
     */
    private boolean isThisSite(IInternalContest contest, int siteNumber) {
        return siteNumber == contest.getSiteNumber();
    }

    /**
     * Is this submission from this site?.
     * @param contest
     * @param submission
     * @return
     */
    private boolean isThisSite(IInternalContest contest, Submission submission) {
        return submission.getSiteNumber() == contest.getSiteNumber();
    }

    /**
     * Add/Update other sites' contest clocks to model.
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    protected void updateContestTimeInModel(IInternalContest contest, IInternalController controller, Packet packet) {

        try {
            ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
            if (contestTime != null) {
                if (isServer(contest)) {
                    if (isThisSite(contest, contestTime.getSiteNumber())) {
                        controller.setContestTime(contestTime);
                    } else {
                        if (contest.getContestTime(contestTime.getSiteNumber()) == null) {
                            contest.addContestTime(contestTime);
                        } else {
                            contest.updateContestTime(contestTime);
                        }
                    }
                } else {
                    GregorianCalendar serverTransmitTime = (GregorianCalendar) PacketFactory.getObjectValue(packet, PacketFactory.SERVER_CLOCK_OFFSET);
                    contestTime.calculateLocalClockOffset(serverTransmitTime);

                    if (contest.getContestTime(contestTime.getSiteNumber()) == null) {
                        contest.addContestTime(contestTime);
                    } else {
                        contest.updateContestTime(contestTime);
                    }
                }
            }

        } catch (Exception e) {
            controller.logWarning("Exception logged ", e);
        }

    }

    /**
     * Initialize and create a model from the input packets.
     * 
     * This will read a packet and load the data into the contest potentially wiping
     * out existing data. <br>
     * This should only be used in initializing/creating a contest model.
     * 
     * @param contest
     * @param controller
     * @param packet
     * @param connectionHandlerID
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     */
    public void loadDataIntoModel(IInternalContest contest, IInternalController controller, Packet packet, ConnectionHandlerID connectionHandlerID) throws IOException, ClassNotFoundException,
            FileSecurityException {

//        ClientId whoPacket = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        ClientId who = (ClientId) packet.getDestinationId();

        if (who != null) {
            contest.setClientId(who);
        }
        
        controller.setSiteNumber(who.getSiteNumber());

        setProfileIntoModel(contest, controller, packet);

        addSitesToModel(contest, controller, packet);

        if (isServer(contest)) {
            // Load local settings and initialize settings if necessary
            controller.initializeServer(contest);
        }

        addLanguagesToModel(contest, controller, packet);

        addProblemsToModel(contest, controller, packet);

        addCategoriesToModel(contest, controller, packet);

        addGroupsToModel(contest, controller, packet);

        addJudgementsToModel(contest, controller, packet);

        updateContestTimeInModel(contest, controller, packet);

        addContestInformationToModel(contest, controller, packet);

        addAllClientSettingsToModel(contest, controller, packet);

        initializeContestTime(contest, controller, packet);

        addAllContestTimesToModel(contest, controller, packet);

        addAllRunsToModel(contest, controller, packet);

        addAllClarificationsToModel(contest, controller, packet);

        addAllAccountsToModel(contest, controller, packet);

        addAllConnectionIdsToModel(contest, controller, packet);

        addLoginsToModel(contest, controller, packet);

        addBalloonSettingsToModel(contest, controller, packet);

        addProfilesToModel(contest, controller, packet);

        addGeneralProblemToModel(contest, controller, packet);
        
        setFinalizeData(contest, controller, packet);
        
        addAvailAJLists (contest, controller, packet);

    }

    /**
     * Extract from packet and add avaialbe AJ Runs and Judges.
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    @SuppressWarnings("unchecked")
    private void addAvailAJLists(IInternalContest contest, IInternalController controller, Packet packet) {
        
        try {

            List<AvailableAJ> availableJudges  = (List<AvailableAJ> ) PacketFactory.getObjectValue(packet, PacketFactory.AVAILABLE_AUTO_JUDGE_JUDGES);
            if (availableJudges != null) {
                for (AvailableAJ availableAJ : availableJudges) {
                    contest.addAvailableAutoJudge(availableAJ.getClientId());
                }
            }
            List<AvailableAJRun> availableRuns  = (List<AvailableAJRun>) PacketFactory.getObjectValue(packet, PacketFactory.AVAILABLE_AUTO_JUDGE_RUNS);
            if (availableRuns!=null) {
                for (AvailableAJRun availableAJRun : availableRuns) {
                    Run run = contest.getRun(availableAJRun.getRunId());
                    contest.addAvailableAutoJudgeRun(run);
                }
            }
            
        } catch (Exception e) {
            controller.logWarning("Unable to load available AJ lists into model ", e);
        } 
    }

    /**
     * Put new profile into model.
     * 
     * @param contest
     * @param controller
     * @param packet
     */
    protected void setProfileIntoModel(IInternalContest contest, IInternalController controller, Packet packet) {

        try {

            String contestId = (String) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_IDENTIFIER);
            contest.setContestIdentifier(contestId);

            Profile newProfile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.NEW_PROFILE);
            Profile profile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE);

            if (newProfile != null) {
                contest.setProfile(newProfile);
            } else if (profile != null) {
                contest.setProfile(profile);
            }

        } catch (Exception e) {
            controller.logWarning("Unable to load profile into model ", e);
        }
    }
}
