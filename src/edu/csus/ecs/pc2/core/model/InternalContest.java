// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.exception.ClarificationUnavailableException;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.exception.ProfileCloneException;
import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.exception.UnableToUncheckoutRunException;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AvailableAJList;
import edu.csus.ecs.pc2.core.list.AvailableAJRunList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.list.BalloonSettingsList;
import edu.csus.ecs.pc2.core.list.CategoryDisplayList;
import edu.csus.ecs.pc2.core.list.CategoryList;
import edu.csus.ecs.pc2.core.list.ClarificationList;
import edu.csus.ecs.pc2.core.list.ClientSettingsList;
import edu.csus.ecs.pc2.core.list.ConnectionHandlerList;
import edu.csus.ecs.pc2.core.list.ContestTimeList;
import edu.csus.ecs.pc2.core.list.EventFeedDefinitionsList;
import edu.csus.ecs.pc2.core.list.GroupDisplayList;
import edu.csus.ecs.pc2.core.list.GroupList;
import edu.csus.ecs.pc2.core.list.JudgementDisplayList;
import edu.csus.ecs.pc2.core.list.JudgementList;
import edu.csus.ecs.pc2.core.list.LanguageDisplayList;
import edu.csus.ecs.pc2.core.list.LanguageList;
import edu.csus.ecs.pc2.core.list.LoginList;
import edu.csus.ecs.pc2.core.list.NotificationList;
import edu.csus.ecs.pc2.core.list.PlaybackInfoList;
import edu.csus.ecs.pc2.core.list.ProblemDisplayList;
import edu.csus.ecs.pc2.core.list.ProblemList;
import edu.csus.ecs.pc2.core.list.ProfilesList;
import edu.csus.ecs.pc2.core.list.RunFilesList;
import edu.csus.ecs.pc2.core.list.RunList;
import edu.csus.ecs.pc2.core.list.RunResultsFileList;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.PasswordChangeEvent.Action;
import edu.csus.ecs.pc2.core.model.ProfileChangeStatus.Status;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.playback.PlaybackManager;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.ISecurityMessageListener;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.SecurityMessageHandler;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;
import edu.csus.ecs.pc2.profile.ProfileLoadException;
import edu.csus.ecs.pc2.profile.ProfileManager;

/**
 * Implementation of IInternalContest - the contest model.
 * 
 * This model is not responsible for logic, just storage. So, for example, {@link #cancelRunCheckOut(Run, ClientId)} will simply update the Run but will not check whether the run should be cancelled.
 * The InternalController should be used to check whether a Run should be cancelled. Other logic of this sort is in the InternalController, not the InternalContest.
 * 
 * @author pc2@ecs.csus.edu
 */

public class InternalContest implements IInternalContest {

    private ClientId localClientId = null;

    private Vector<IRunListener> runListenerList = new Vector<IRunListener>();

    private Vector<IClarificationListener> clarificationListenerList = new Vector<IClarificationListener>();

    private Vector<IProblemListener> problemListenerList = new Vector<IProblemListener>();
    
    private Vector<ICategoryListener> categoryListenerList = new Vector<ICategoryListener>();
    
    private Vector<INotificationListener> notificationListenerList = new Vector<INotificationListener>();

    private Vector<ILanguageListener> languageListenerList = new Vector<ILanguageListener>();
    
    private Vector<IPlayBackEventListener> playBackEventListenerList = new Vector<IPlayBackEventListener>();
    
    private Vector<IProfileListener> profileListenerList = new Vector<IProfileListener>();

    private Vector<IChangePasswordListener> changePasswordListenerList = new Vector<IChangePasswordListener>();

    private Vector<ILoginListener> loginListenerList = new Vector<ILoginListener>();

    private Vector<IContestTimeListener> contestTimeListenerList = new Vector<IContestTimeListener>();

    private Vector<IJudgementListener> judgementListenerList = new Vector<IJudgementListener>();

    private Vector<ISiteListener> siteListenerList = new Vector<ISiteListener>();

    private Vector<IConnectionListener> connectionListenerList = new Vector<IConnectionListener>();

    private Vector<IClientSettingsListener> clientSettingsListenerList = new Vector<IClientSettingsListener>();

    private Vector<IContestInformationListener> contestInformationListenerList = new Vector<IContestInformationListener>();
    
    private Vector<IBalloonSettingsListener> balloonSettingsListenerList = new Vector<IBalloonSettingsListener>();

    private Vector<IGroupListener> groupListenerList = new Vector<IGroupListener>();

    private Vector<IMessageListener> messageListenerList = new Vector<IMessageListener>();
    
    private Vector<IEventFeedDefinitionListener> eventFeedDefinitionListenerList =new Vector<IEventFeedDefinitionListener>(); 
    
    /**
     * Contains name of client (judge or admin) who checks out the run.
     */
    private Hashtable<ElementId, ClientId> runCheckOutList = new Hashtable<ElementId, ClientId>(200);

    /**
     * Contains name of client (judge or admin) who checks out the clarification.
     */
    private Hashtable<ElementId, ClientId> clarCheckOutList = new Hashtable<ElementId, ClientId>(200);

    private AccountList accountList = new AccountList();

    private BalloonSettingsList balloonSettingsList = new BalloonSettingsList();

    private Vector<IAccountListener> accountListenerList = new Vector<IAccountListener>();
    
    private GroupList groupList = new GroupList();
    
    private Problem generalProblem = null;

    /**
     * Logins on this site.
     * 
     * These are all logins that have been authenticated by the local server.
     */
    private LoginList localLoginList = new LoginList();

    /**
     * Logins on other sites.
     * 
     * These are all the logins that have been authenticated by a remote server.
     */
    private LoginList remoteLoginList = new LoginList();

    /**
     * Connections on this site.
     */
    private ConnectionHandlerList localConnectionHandlerList = new ConnectionHandlerList();

    /**
     * Connections on other sites
     */
    private ConnectionHandlerList remoteConnectionHandlerList = new ConnectionHandlerList();

    private ContestTimeList contestTimeList = new ContestTimeList();

    private RunList runList = new RunList();

    private RunFilesList runFilesList = new RunFilesList();

    private RunResultsFileList runResultFilesList = new RunResultsFileList();

    private ClarificationList clarificationList = new ClarificationList();

    private SiteList siteList = new SiteList();

    /**
     * List of all settings for client.
     */
    private ClientSettingsList clientSettingsList = new ClientSettingsList();

    /**
     * InternalContest Information (like title).
     */
    private ContestInformation contestInformation = new ContestInformation();

    private int siteNumber = 1;

    /**
     * List of all defined problems. Contains deleted problems too.
     */
    private ProblemList problemList = new ProblemList();
    
    private CategoryList categoryList = new CategoryList();
  
    private NotificationList notificationList = new NotificationList();

    private ProblemDataFilesList problemDataFilesList = new ProblemDataFilesList();

    /**
     * List of all problems displayed to users, in order. Does not contain deleted problems.
     */
    private ProblemDisplayList problemDisplayList = new ProblemDisplayList();
    
    private CategoryDisplayList categoryDisplayList = new CategoryDisplayList();

    /**
     * List of all languages. Contains deleted problems too.
     */
    private LanguageList languageList = new LanguageList();
    
    private PlaybackInfoList playbackInfoList = new PlaybackInfoList();
    
    /**
     * List of all profiles.
     */
    private ProfilesList profileList = new ProfilesList();

    /**
     * List of all displayed languages, in order. Does not contain deleted languages.
     */
    private LanguageDisplayList languageDisplayList = new LanguageDisplayList();

    /**
     * List of all displayed judgements, in order. Does not contain deleted judgements.
     */
    private JudgementDisplayList judgementDisplayList = new JudgementDisplayList();

    /**
     * List of all groups displayed to users, in order. Does not contain deleted groups.
     */
    private GroupDisplayList groupDisplayList = new GroupDisplayList();

    /**
     * List of all judgements. Contains deleted judgements too.
     */
    private JudgementList judgementList = new JudgementList();
    
    /**
     * List of currently available AutoJudge clients (that is, Judge clients which have registered
     * as being available to AutoJudge a set of problems).
     */
    private AvailableAJList availableAJList = new AvailableAJList();
    
    /**
     * List of submitted runs currently awaiting dispatching to an available AutoJudge.
     */
    private AvailableAJRunList availableAJRunList = new AvailableAJRunList();
    
    /**
     * Locking object for synchronizing access to the lists of available AutoJudges and runs waiting to be AutoJudged
     */
    Object ajLock = new Object();
    
    private SecurityMessageHandler securityMessageHandler;
    
    private Profile profile = null;

    private String contestIdentifier = null;

    private ConfigurationIO configurationIO = null;
    
    private IStorage storage = null;

    private String contestPassword = null;

    private ProfileCloneSettings profileCloneSettings = null;

    private FinalizeData finalizeData = null;
    
    // TODO 670 add list for ReplaySetting, add load and save too
    private ReplaySetting replaySettingTemp = null;
    
    private PlaybackManager playbackManager = new PlaybackManager();

    private EventFeedDefinitionsList eventFeedDefinitionsList = null;

    private ParseArguments parseArguments = new ParseArguments();

    private Site createFakeSite(int nextSiteNumber) {
        Site site = new Site("Site " + nextSiteNumber, nextSiteNumber);
        Properties props = new Properties();
        props.put(Site.IP_KEY, "localhost");
        int port = 50002 + (nextSiteNumber - 1) * 1000;
        props.put(Site.PORT_KEY, "" + port);
        site.setConnectionInfo(props);
        site.setPassword("site" + nextSiteNumber);
        return site;
    }

    public void initializeSubmissions(int siteNum) {
        try{
            initializeSubmissions(siteNum, true);
        } catch (Exception e) {
            logException("Trouble loading clarifications from disk ", e);
        }
    }
    
    /**
     * Load submission and other information from disk.
     * 
     * @param siteNum
     * @param uncheckoutSubmissions - changes all checked out submissions to un-checkedout
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void initializeSubmissions(int siteNum, boolean uncheckoutSubmissions) throws IOException, ClassNotFoundException, FileSecurityException {

        configurationIO = new ConfigurationIO(storage);

        runList = new RunList(storage);
        runList.loadFromDisk(siteNum);
        runFilesList = new RunFilesList(storage);
        runResultFilesList = new RunResultsFileList(storage);
        clarificationList = new ClarificationList(storage);
        notificationList = new NotificationList(storage);
        clarificationList.loadFromDisk(siteNum);
        notificationList.loadFromDisk(siteNum);

        if (uncheckoutSubmissions) {

            resetRunStatus(siteNum);

            clarificationList.loadFromDisk(siteNum);
            Clarification[] clarList = clarificationList.getList();
            for (int i = 0; i < clarList.length; i++) {
                if (clarList[i].getState().equals(ClarificationStates.BEING_ANSWERED)) {
                    StaticLog.info("Changing Clarification " + clarList[i].getElementId() + " from BEING_ANSWERED by " + clarList[i].getWhoCheckedItOutId() + "to NEW");
                    clarificationList.uncheckoutClarification(clarList[i]);
                }
            }
        }
    }

    /**
     * For each run reset to a non-checked out state. 
     * @param siteNum
     */
    private void resetRunStatus(int siteNum) {

        try {
            Run[] runs = runList.getList();
            for (int i = 0; i < runs.length; i++) {
                RunStates status = runs[i].getStatus();
                if (status.equals(RunStates.BEING_COMPUTER_JUDGED)) {
                    status = RunStates.QUEUED_FOR_COMPUTER_JUDGEMENT;
                } else if (status.equals(RunStates.BEING_JUDGED)) {
                    if (runs[i].getComputerJudgementRecord() == null) {
                        status = RunStates.NEW;
                    } else {
                        status = RunStates.MANUAL_REVIEW;
                    }
                } else if (status.equals(RunStates.BEING_RE_JUDGED)) {
                    status = RunStates.JUDGED;
                }
                if (!runs[i].getStatus().equals(status)) {
                    StaticLog.info("Changing Run " + runs[i].getElementId() + " from " + runs[i].getStatus() + "to NEW");

                    runList.updateRunStatus(runs[i], status);
                }
            }
        } catch (FileNotFoundException e){
            StaticLog.getLog().log(Log.INFO, "startup: no runs found on disk.");
        } catch (IOException e) {
            logException(e);
        } catch (ClassNotFoundException e) {
            logException(e);
        } catch (FileSecurityException e) {
            logException(e);
        }
    }

    public void initializeStartupData(int siteNum) {

        if (siteList.size() == 0) {
            Site site = createFakeSite(1);
            site.setActive(true);
            siteList.add(site);
            contestInformation.setContestTitle("Default Contest Title");
            if (getGeneralProblem() == null){
                setGeneralProblem(new Problem("General"));
            }
        }

        if (getContestTime(siteNum) == null) {
            ContestTime contestTime = new ContestTime();
            contestTime.setSiteNumber(siteNum);
            addContestTime(contestTime);
        }

        // No need to generate a server account
//        if (getAccounts(Type.SERVER) == null || getAccounts(Type.SERVER, siteNum).size() == 0) {
//            generateNewAccounts(Type.SERVER.toString(), 1, true);
//        }

        if (getAccounts(Type.ADMINISTRATOR) == null || getAccounts(Type.ADMINISTRATOR, siteNum).size() == 0) {
            generateNewAccounts(ClientType.Type.ADMINISTRATOR.toString(), 1, true);
        }
        
    }

    public void addRunListener(IRunListener runListener) {
        runListenerList.addElement(runListener);
    }

    public void removeRunListener(IRunListener runListener) {
        runListenerList.removeElement(runListener);
    }

    public void addClarificationListener(IClarificationListener clarificationListener) {
        clarificationListenerList.addElement(clarificationListener);
    }

    public void removeClarificationListener(IClarificationListener clarificationListener) {
        clarificationListenerList.remove(clarificationListener);
    }

    public void addContestTimeListener(IContestTimeListener contestTimeListener) {
        contestTimeListenerList.addElement(contestTimeListener);
    }

    public void removeContestTimeListener(IContestTimeListener contestTimeListener) {
        contestTimeListenerList.removeElement(contestTimeListener);
    }

    public void addJudgementListener(IJudgementListener judgementListener) {
        judgementListenerList.addElement(judgementListener);
    }

    public void removeJudgementListener(IJudgementListener judgementListener) {
        judgementListenerList.remove(judgementListener);
    }
    
    public void addMessageListener(IMessageListener messageListener) {
        messageListenerList.addElement(messageListener);
    }

    public void removeMessageListener(IMessageListener messageListener) {
        messageListenerList.removeElement(messageListener);
    }

    private void fireRunListener(RunEvent runEvent) {
        for (int i = 0; i < runListenerList.size(); i++) {

            if (runEvent.getAction() == RunEvent.Action.ADDED) {
                runListenerList.elementAt(i).runAdded(runEvent);
            } else if (runEvent.getAction() == RunEvent.Action.DELETED) {
                runListenerList.elementAt(i).runRemoved(runEvent);
            } else if (runEvent.getAction() == RunEvent.Action.REFRESH_ALL) {
                runListenerList.elementAt(i).refreshRuns(runEvent);
            } else {
                runListenerList.elementAt(i).runChanged(runEvent);
            }
        }
    }

    private void fireContestTimeListener(ContestTimeEvent contestTimeEvent) {
        for (int i = 0; i < contestTimeListenerList.size(); i++) {

            if (contestTimeEvent.getAction() == ContestTimeEvent.Action.ADDED) {
                contestTimeListenerList.elementAt(i).contestTimeAdded(contestTimeEvent);
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.DELETED) {
                contestTimeListenerList.elementAt(i).contestTimeRemoved(contestTimeEvent);
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.CLOCK_STARTED) {
                contestTimeListenerList.elementAt(i).contestStarted(contestTimeEvent);
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.CLOCK_STOPPED) {
                contestTimeListenerList.elementAt(i).contestStopped(contestTimeEvent);
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.CHANGED) {
                contestTimeListenerList.elementAt(i).contestTimeChanged(contestTimeEvent);
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.REFRESH_ALL) {
                contestTimeListenerList.elementAt(i).refreshAll(contestTimeEvent);
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.CLOCK_AUTO_STARTED) {
                contestTimeListenerList.elementAt(i).contestAutoStarted(contestTimeEvent);
            } else {
                contestTimeListenerList.elementAt(i).contestTimeChanged(contestTimeEvent);
            }
        }
    }

    private void fireProblemListener(ProblemEvent problemEvent) {
        for (int i = 0; i < problemListenerList.size(); i++) {

            if (problemEvent.getAction() == ProblemEvent.Action.ADDED) {
                problemListenerList.elementAt(i).problemAdded(problemEvent);
            } else if (problemEvent.getAction() == ProblemEvent.Action.DELETED) {
                problemListenerList.elementAt(i).problemRemoved(problemEvent);
            } else if (problemEvent.getAction() == ProblemEvent.Action.REFRESH_ALL) {
                problemListenerList.elementAt(i).problemRefreshAll(problemEvent);
            } else {
                problemListenerList.elementAt(i).problemChanged(problemEvent);
            }
        }
    }
    
    private void fireEventFeedDefinitionListener(EventFeedDefinitionEvent eventFeedDefinitionEvent) {
        for (int i = 0; i < problemListenerList.size(); i++) {

            if (eventFeedDefinitionEvent.getAction() == EventFeedDefinitionEvent.Action.ADDED) {
                eventFeedDefinitionListenerList.elementAt(i).eventFeedDefinitionAdded(eventFeedDefinitionEvent);
            } else if (eventFeedDefinitionEvent.getAction() == EventFeedDefinitionEvent.Action.DELETED) {
                eventFeedDefinitionListenerList.elementAt(i).eventFeedDefinitionRemoved(eventFeedDefinitionEvent);
            } else if (eventFeedDefinitionEvent.getAction() == EventFeedDefinitionEvent.Action.REFRESH_ALL) {
                eventFeedDefinitionListenerList.elementAt(i).eventFeedDefinitionRefreshAll(eventFeedDefinitionEvent);
            } else {
                eventFeedDefinitionListenerList.elementAt(i).eventFeedDefinitionChanged(eventFeedDefinitionEvent);
            }
        }
    }
    
    private void fireCategoryListener(Category category, CategoryEvent.Action action) {
        
        CategoryEvent categoryEvent = new CategoryEvent(action, category);
        for (int i = 0; i < categoryListenerList.size(); i++) {

            if (categoryEvent.getAction() == CategoryEvent.Action.ADDED) {
                categoryListenerList.elementAt(i).categoryAdded(categoryEvent);
            } else if (categoryEvent.getAction() == CategoryEvent.Action.DELETED) {
                categoryListenerList.elementAt(i).categoryRemoved(categoryEvent);
            } else if (categoryEvent.getAction() == CategoryEvent.Action.REFRESH_ALL) {
                categoryListenerList.elementAt(i).categoryRefreshAll(categoryEvent);
            } else {
                categoryListenerList.elementAt(i).categoryChanged(categoryEvent);
            }
        }
    }
    
    private void fireNotificationListener(Notification notification, NotificationEvent.Action added) {
        
        NotificationEvent notificationEvent = new NotificationEvent(NotificationEvent.Action.CHANGED, notification);
        for (int i = 0; i < notificationListenerList.size(); i++) {

            if (notificationEvent.getAction() == NotificationEvent.Action.ADDED) {
                notificationListenerList.elementAt(i).notificationAdded(notificationEvent);
            } else if (notificationEvent.getAction() == NotificationEvent.Action.DELETED) {
                notificationListenerList.elementAt(i).notificationRemoved(notificationEvent);
            } else if (notificationEvent.getAction() == NotificationEvent.Action.REFRESH_ALL) {
                notificationListenerList.elementAt(i).notificationRefreshAll(notificationEvent);
            } else {
                notificationListenerList.elementAt(i).notificationChanged(notificationEvent);
            }
        }
    }

    private void fireLanguageListener(LanguageEvent languageEvent) {
        for (int i = 0; i < languageListenerList.size(); i++) {

            if (languageEvent.getAction() == LanguageEvent.Action.ADDED) {
                languageListenerList.elementAt(i).languageAdded(languageEvent);
            } else if (languageEvent.getAction() == LanguageEvent.Action.DELETED) {
                languageListenerList.elementAt(i).languageRemoved(languageEvent);
            } else if (languageEvent.getAction() == LanguageEvent.Action.REFRESH_ALL) {
                languageListenerList.elementAt(i).languageRefreshAll(languageEvent);
            } else if (languageEvent.getAction() == LanguageEvent.Action.ADDED_LANGUAGES) {
                languageListenerList.elementAt(i).languagesAdded(languageEvent);
            } else if (languageEvent.getAction() == LanguageEvent.Action.CHANGED_LANGUAGES) {
                languageListenerList.elementAt(i).languagesChanged(languageEvent);
            } else {
                languageListenerList.elementAt(i).languageChanged(languageEvent);
            }
        }
    }
    
    private void firePlaybackInfosListener(PlayBackEvent.Action action, PlaybackInfo playbackInfo) {
        
        PlayBackEvent playBackEvent = new PlayBackEvent(action, playbackInfo);
        
        for (int i = 0; i < playBackEventListenerList.size(); i++) {
            if (playBackEvent.getAction() == PlayBackEvent.Action.ADDED) {
                playBackEventListenerList.elementAt(i).playbackAdded(playBackEvent);
            } else if (playBackEvent.getAction() == PlayBackEvent.Action.RESET_REPLAY) {
                playBackEventListenerList.elementAt(i).playbackChanged(playBackEvent);
            } else if (playBackEvent.getAction() == PlayBackEvent.Action.START_REPLAY) {
                playBackEventListenerList.elementAt(i).playbackChanged(playBackEvent);
            } else if (playBackEvent.getAction() == PlayBackEvent.Action.STOP_REPLAY) {
                playBackEventListenerList.elementAt(i).playbackChanged(playBackEvent);
            } else if (playBackEvent.getAction() == PlayBackEvent.Action.REFRESH_ALL) {
                playBackEventListenerList.elementAt(i).playbackRefreshAll(playBackEvent);
            } else {
                playBackEventListenerList.elementAt(i).playbackChanged(playBackEvent);
            }
        }
    }

    private void fireLoginListener(LoginEvent loginEvent) {
        for (int i = 0; i < loginListenerList.size(); i++) {

            if (loginEvent.getAction() == LoginEvent.Action.NEW_LOGIN) {
                loginListenerList.elementAt(i).loginAdded(loginEvent);
            } else if (loginEvent.getAction() == LoginEvent.Action.LOGOFF) {
                loginListenerList.elementAt(i).loginRemoved(loginEvent);
            } else if (loginEvent.getAction() == LoginEvent.Action.LOGIN_DENIED) {
                loginListenerList.elementAt(i).loginDenied(loginEvent);
            } else if (loginEvent.getAction() == LoginEvent.Action.REFRESH_ALL) {
                loginListenerList.elementAt(i).loginRefreshAll(loginEvent);
                
            } else {
                throw new UnsupportedOperationException("Unknown login action " + loginEvent.getAction());
            }
        }
    }

    private void fireJudgementListener(JudgementEvent judgementEvent) {
        for (int i = 0; i < judgementListenerList.size(); i++) {

            if (judgementEvent.getAction() == JudgementEvent.Action.ADDED) {
                judgementListenerList.elementAt(i).judgementAdded(judgementEvent);
            } else if (judgementEvent.getAction() == JudgementEvent.Action.DELETED) {
                judgementListenerList.elementAt(i).judgementRemoved(judgementEvent);
            } else if (judgementEvent.getAction() == JudgementEvent.Action.REFRESH_ALL) {
                judgementListenerList.elementAt(i).judgementRefreshAll(judgementEvent);
            } else {
                judgementListenerList.elementAt(i).judgementChanged(judgementEvent);
            }
        }
    }

    private void fireSiteListener(SiteEvent siteEvent) {
        for (int i = 0; i < siteListenerList.size(); i++) {

            if (siteEvent.getAction() == SiteEvent.Action.ADDED) {
                siteListenerList.elementAt(i).siteAdded(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.DELETED) {
                siteListenerList.elementAt(i).siteRemoved(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.CHANGED) {
                siteListenerList.elementAt(i).siteChanged(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.LOGIN) {
                siteListenerList.elementAt(i).siteLoggedOn(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.LOGOFF) {
                siteListenerList.elementAt(i).siteLoggedOff(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.REFRESH_ALL) {
                siteListenerList.elementAt(i).sitesRefreshAll(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.STATUS_CHANGE) {
                siteListenerList.elementAt(i).siteProfileStatusChanged(siteEvent);
            } else {
                siteListenerList.elementAt(i).siteAdded(siteEvent);
            }
        }
    }

    private void fireConnectionListener(ConnectionEvent connectionEvent) {
        for (int i = 0; i < connectionListenerList.size(); i++) {

            if (connectionEvent.getAction() == ConnectionEvent.Action.ESTABLISHED) {
                connectionListenerList.elementAt(i).connectionEstablished(connectionEvent);
            } else if (connectionEvent.getAction() == ConnectionEvent.Action.DROPPED) {
                connectionListenerList.elementAt(i).connectionDropped(connectionEvent);
            } else if (connectionEvent.getAction() == ConnectionEvent.Action.REFRESH_ALL) {
                connectionListenerList.elementAt(i).connectionRefreshAll(connectionEvent);
            } else {
                throw new UnsupportedOperationException("Unknown connection action " + connectionEvent.getAction());
            }
        }
    }

    private void fireBalloonSettingsListener(BalloonSettingsEvent balloonSettingsEvent) {
        for (int i = 0; i < balloonSettingsListenerList.size(); i++) {

            if (balloonSettingsEvent.getAction() == BalloonSettingsEvent.Action.ADDED) {
                balloonSettingsListenerList.elementAt(i).balloonSettingsAdded(balloonSettingsEvent);
            } else if (balloonSettingsEvent.getAction() == BalloonSettingsEvent.Action.DELETED) {
                balloonSettingsListenerList.elementAt(i).balloonSettingsRemoved(balloonSettingsEvent);
            } else if (balloonSettingsEvent.getAction() == BalloonSettingsEvent.Action.REFRESH_ALL) {
                balloonSettingsListenerList.elementAt(i).balloonSettingsRefreshAll(balloonSettingsEvent);
            } else {
                balloonSettingsListenerList.elementAt(i).balloonSettingsChanged(balloonSettingsEvent);
            }
        }
    }

    private void fireMessageListener(MessageEvent messageEvent) {

        for (int i = 0; i < messageListenerList.size(); i++) {

            if (messageEvent.getAction() == MessageEvent.Action.ADDED) {
                messageListenerList.elementAt(i).messageAdded(messageEvent);
            } else if (messageEvent.getAction() == MessageEvent.Action.DELETED) {
                messageListenerList.elementAt(i).messageRemoved(messageEvent);
            }
        }
    }

    public void addLocalLogin(ClientId inClientId, ConnectionHandlerID connectionHandlerID) {
        localLoginList.add(inClientId, connectionHandlerID);
        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.NEW_LOGIN, inClientId, connectionHandlerID, "New");
        fireLoginListener(loginEvent);

    }

    public void addRemoteLogin(ClientId inClientId, ConnectionHandlerID connectionHandlerID) {
        remoteLoginList.add(inClientId, connectionHandlerID);
        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.NEW_LOGIN, inClientId, connectionHandlerID, "New");
        fireLoginListener(loginEvent);
    }

    public void addLogin(ClientId inClientId, ConnectionHandlerID connectionHandlerID) {
        if (inClientId.getSiteNumber() == siteNumber) {
            localLoginList.add(inClientId, connectionHandlerID);
        } else {
            remoteLoginList.add(inClientId, connectionHandlerID);
        }
        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.NEW_LOGIN, inClientId, connectionHandlerID, "New");
        fireLoginListener(loginEvent);
    }

    public void loginDenied(ClientId clientId, ConnectionHandlerID connectionHandlerID, String message) {
        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.LOGIN_DENIED, clientId, connectionHandlerID, message);
        fireLoginListener(loginEvent);
    }
    
    public void addLanguage(Language language) {
        languageDisplayList.add(language);
        languageList.add(language);
        LanguageEvent languageEvent = new LanguageEvent(LanguageEvent.Action.ADDED, language);
        fireLanguageListener(languageEvent);
    }

    public void addProblem(Problem problem) {

        // this will be null on the 1st server
        ClientId me = getClientId();
        if (me != null) {
            Account account = getAccount(me);
            if (account != null && account.isTeam() && account.getGroupId() != null) {
                // If is a team and has been assigned a group.
    
                Group group = getGroup(account.getGroupId());
                if (!problem.canView(group)) {
                    /**
                     * This is a team, and this team's group is not allowed to view this problem
                     */
                    // Do not add this problem to the list of problems.
                    return;
                }
            }
        }
        
        problemDisplayList.add(problem);
        problemList.add(problem);
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.ADDED, problem);
        fireProblemListener(problemEvent);
    }
    
    public void addCategory(Category category) {
       categoryDisplayList.add(category);
       categoryList.add(category);
       fireCategoryListener(category, CategoryEvent.Action.ADDED);
    }
    
    public void addNotification(Notification notification) throws IOException, ClassNotFoundException, FileSecurityException {
        notificationList.add(notification);
        fireNotificationListener(notification, NotificationEvent.Action.ADDED);
    }

    public void addJudgement(Judgement judgement) {
        if (judgement.getSiteNumber() == 0) {
            judgement.setSiteNumber(getSiteNumber());
        }
        judgementDisplayList.add(judgement);
        judgementList.add(judgement);
        JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.ADDED, judgement);
        fireJudgementListener(judgementEvent);
    }

    public void addSite(Site site) {
        siteList.add(site);
        SiteEvent siteEvent = new SiteEvent(SiteEvent.Action.ADDED, site);
        fireSiteListener(siteEvent);
    }

    public void updateSite(Site site) {
        siteList.update(site);
        SiteEvent siteEvent = new SiteEvent(SiteEvent.Action.CHANGED, site);
        fireSiteListener(siteEvent);
    }
    
    public void updateSiteStatus(Site site, Profile inProfile, Status status) {
        SiteEvent siteEvent = new SiteEvent(SiteEvent.Action.STATUS_CHANGE, site, inProfile, status);
        fireSiteListener(siteEvent);
    }

    public void addAccount(Account account) {
        accountList.add(account);
        AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.ADDED, account);
        fireAccountListener(accountEvent);
    }

    public void addAccounts(Account[] accounts) {
        for (Account account : accounts) {
            accountList.add(account);
        }
        AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.ADDED_ACCOUNTS, accounts);
        fireAccountListener(accountEvent);
    }

    public Judgement[] getJudgements() {
        return judgementDisplayList.getList();
    }

    /**
     * Accept Run, add new run into server.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public Run acceptRun(Run run, RunFiles runFiles) throws IOException, ClassNotFoundException, FileSecurityException {
        run.setElapsedMS(getContestTime().getElapsedMS());
        run.setSiteNumber(getSiteNumber());
        Run newRun = runList.addNewRun(run); // this set the run number.
        if (runFiles != null) {
            runFilesList.add(newRun, runFiles);
//            SerializedFile file = runFiles.getMainFile();
//            if (file != null){
//                newRun.setEntryPoint(file.getName());
//                addRun(newRun);
//            }
        }
        
        //check whether the problem for this run has been assigned for Auto-Judging
        if (getProblem(newRun.getProblemId()).isComputerJudged()) {
            
            //yes, it's a run to be auto-judged; add it to the list of runs awaiting auto-judging
            addRunToAJRunList(newRun);
        }
        
        //attempt to assign any and all available runs to available AutoJudges
        dispatchRunsToAutoJudges();
                
        return newRun;
    }

    /**
     * This method adds the specified Run to the list of runs awaiting AutoJudging.
     * 
     * @param run the Run to be added to the waiting-for-AutoJudge list.
     */
    private void addRunToAJRunList(Run run) {
        
        //construct an AvailableAJRun from the specified input run
        AvailableAJRun availableAJRun = new AvailableAJRun(run.getElementId(), run.getElapsedMS(), run.getProblemId());
        
        //ensure that we don't mess with the available-runs list while some other thread is doing so (for example, running inside method dispatchRunsToAutoJudges())
        synchronized (ajLock) {
            
            //put the availableAJRun in the list of runs available for auto-judging
            availableAJRunList.add(availableAJRun);
        }
    }
    
    /**
     * This method checks every run in the list of runs waiting for AutoJudging; if an available AJ can be found for the run
     * then the run is "dispatched" to that AJ -- meaning, the method performs a "run checkout", sending the run to the chosen AJ.
     */
    private void dispatchRunsToAutoJudges() {
        boolean done = false;
        
        while (!done) {
            if ( availableAJList.size()<=0  || availableAJRunList.size()<=0 ) {
                // there are either no available AJs or no runs waiting for AJs; nothing to dispatch
                done = true;
            } else {
                
                //there are available runs, or available AJs (or both); 
                //ensure only one thread at a time can manipulate the available AJ and AJRuns lists
                synchronized (ajLock) {
                    
                    AvailableAJ chosenJudge = null;
                    AvailableAJRun chosenRun = null;
                    //a list to keep track of runs on the "available AJ runs" list which actually get assigned to an AJ
                    // (see comments below, where runs get added to this list)
                    AvailableAJRunList assignedRunsList = new AvailableAJRunList();
                    
                    //check each run which is awaiting an AJ
                    for (AvailableAJRun currentAJRun : availableAJRunList) {
                        
                        //try to find an AJ which can judge the current run (i.e. is configured for that problem)
                        boolean foundJudge = false;
                        for (AvailableAJ currentAJ : availableAJList) {
                            
                            //check if the problem for the current run can be judged by the current AJ
                            if (currentAJ.canJudge(currentAJRun.getProblemId())) {
                                
                                //we found an AJ which can judge the current run
                                foundJudge = true;
                                chosenJudge = currentAJ;
                                break;
                            }
                        }
                        
                        if (foundJudge) {
                            
                            //we found an available AJ that can judge the run; dispatch the run to that AJ
                            assignRunToAutoJudge(chosenRun, chosenJudge);
                            
                            //remove the assigned judge from the list of available AJs
                            availableAJList.remove(chosenJudge);
                            
                            //add the run to a list of runs which need to be removed from the "availableAJRuns" list.
                            // (This is done rather than just immediately removing the run from the list in order to avoid 
                            // "concurrent modification" exceptions which could occur if the run
                            // was removed while the corresponding iterator is still active.)
                            assignedRunsList.add(currentAJRun);
                            
                        }
                        
                    }//end for each availableRun
                    
                    // remove any runs which got dispatched to judges from the "available runs" list (see comment above)
                    for (AvailableAJRun assignedRun : assignedRunsList) {
                        availableAJRunList.remove(assignedRun);
                    }
                    
                }//end synchronized block
            }
        }
        
    }

    /**
     * This method performs a "run checkout", sending the specified run to the specified AJ.
     * 
     * @param run the Run which is to be assigned to the specified AJ.
     * @param judge the available AutoJudge to which the run is to be dispatched.
     */
    private void assignRunToAutoJudge(AvailableAJRun run, AvailableAJ judge) {
        throw new UnsupportedOperationException("Method InternalContest.assignRunToAutoJudge() has not yet been implemented...");
        
    }

    /**
     * Accept Clarification, add clar into this server.
     * 
     * @param clarification
     * @return the accepted Clarification
     */
    public Clarification acceptClarification(Clarification clarification) {
        clarification.setElapsedMS(getContestTime().getElapsedMS());
        clarification.setSiteNumber(getSiteNumber());
        Clarification newClarification;
        try {
            newClarification = clarificationList.addNewClarification(clarification);
            addClarification(clarification);
            return newClarification;
        } catch (IOException e) {
            logException(e);
        } catch (ClassNotFoundException e) {
            logException(e);
        } catch (FileSecurityException e) {
            logException(e);
        }
        
        return null;
    }

    public void answerClarification(Clarification clarification, String answer, ClientId whoAnsweredIt, boolean sendToAll) {

        if (clarificationList.get(clarification) != null) {
            Clarification answerClarification;
            try {
                ClarificationAnswer clarificationAnswer = new ClarificationAnswer(answer, whoAnsweredIt, sendToAll, getContestTime());
                answerClarification = clarificationList.updateClarification(clarification, ClarificationStates.ANSWERED, clarificationAnswer);
                ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.ANSWERED_CLARIFICATION, answerClarification);
                clarificationEvent.setWhoModifiedClarification(whoAnsweredIt);
                fireClarificationListener(clarificationEvent);
            } catch (IOException e) {
                logException(e);
            } catch (ClassNotFoundException e) {
                logException(e);
            } catch (FileSecurityException e) {
                logException(e);
            }
        
        } else {
            try {
                clarificationList.add(clarification);
                Clarification updatedClarification = clarificationList.get(clarification);
                ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.ANSWERED_CLARIFICATION, updatedClarification);
                clarificationEvent.setWhoModifiedClarification(whoAnsweredIt);
                fireClarificationListener(clarificationEvent);
            } catch (IOException e) {
                logException(e);
            } catch (ClassNotFoundException e) {
                logException(e);
            } catch (FileSecurityException e) {
                logException(e);
            }
        }
    }

    public void updateClarification(Clarification clarification, ClientId whoChangedIt) {
        try {
            clarificationList.updateClarification(clarification);
            ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.CHANGED, clarificationList.get(clarification));
            if (whoChangedIt != null){
                clarificationEvent.setWhoModifiedClarification(whoChangedIt);
            }
            clarificationEvent.setSentToClientId(whoChangedIt);
            fireClarificationListener(clarificationEvent);
        } catch (IOException e) {
            logException(e);
        } catch (ClassNotFoundException e) {
            logException(e);
        } catch (FileSecurityException e) {
            logException(e);
        }
    }

    public void clarificationNotAvailable(Clarification clar) {
        ClarificationEvent clarEvent = new ClarificationEvent(ClarificationEvent.Action.CLARIFICATION_NOT_AVAILABLE, clar);
        fireClarificationListener(clarEvent);
    }

    /**
     * Add a run to run list, notify listeners.
     * 
     * @param run
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void addRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException {
        runList.add(run);
        RunEvent runEvent = new RunEvent(RunEvent.Action.ADDED, run, null, null);
        fireRunListener(runEvent);
    }

    public void addRun(Run run, RunFiles runFiles, ClientId whoCheckedOutRunId) throws IOException, ClassNotFoundException, FileSecurityException {
        runList.add(run);
        runFilesList.add(run, runFiles);
        RunEvent runEvent = new RunEvent(RunEvent.Action.CHECKEDOUT_RUN, run, runFiles, null);
        runEvent.setSentToClientId(whoCheckedOutRunId);
        fireRunListener(runEvent);
    }

    public void addRun(Run run, RunFiles runFiles) throws IOException, ClassNotFoundException, FileSecurityException {
        runList.add(run);
        runFilesList.add(run, runFiles);
        RunEvent runEvent = new RunEvent(RunEvent.Action.ADDED, run, runFiles, null);
        fireRunListener(runEvent);
    }
    
    public void availableRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException {
        runList.add(run);
        RunEvent runEvent = new RunEvent(RunEvent.Action.RUN_AVAILABLE, run, null, null);
        fireRunListener(runEvent);
    }

    /**
     * Generate accounts.
     * 
     * @see edu.csus.ecs.pc2.core.model.IInternalContest#generateNewAccounts(java.lang.String, int, int, boolean)
     * @param clientTypeName
     * @param count
     * @param active
     */
    public Vector<Account> generateNewAccounts(String clientTypeName, int count, boolean active) {
        return generateNewAccounts(clientTypeName, count, 1, active);
    }

    /**
     * @see edu.csus.ecs.pc2.core.model.IInternalContest#generateNewAccounts(java.lang.String, int, int, boolean)
     */
    public Vector<Account> generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active) {
        ClientType.Type type = ClientType.Type.valueOf(clientTypeName.toUpperCase());

        Vector<Account> newAccounts = accountList.generateNewAccounts(type, count, startNumber, PasswordType.JOE, siteNumber, active);

        for (int i = 0; i < newAccounts.size(); i++) {
            Account account = newAccounts.elementAt(i);
            AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.ADDED, account);
            fireAccountListener(accountEvent);
        }
        return newAccounts;
    }

    /**
     * Generate new sites
     * 
     * @param count
     * @param active
     */
    public void generateNewSites(int count, boolean active) {

        int numSites = siteList.size();

        for (int i = 0; i < count; i++) {
            int nextSiteNumber = i + numSites + 1;
            Site site = new Site("Site " + nextSiteNumber, nextSiteNumber);
            site.setPassword("site" + nextSiteNumber); // JOE password
            site.setActive(active);
            addSite(site);
        }
    }

    public void addAccountListener(IAccountListener accountListener) {
        accountListenerList.addElement(accountListener);

    }

    public void removeAccountListener(IAccountListener accountListener) {
        accountListenerList.removeElement(accountListener);
    }

    private void fireAccountListener(AccountEvent accountEvent) {
        for (int i = 0; i < accountListenerList.size(); i++) {

            if (accountEvent.getAction() == AccountEvent.Action.ADDED) {
                accountListenerList.elementAt(i).accountAdded(accountEvent);
            } else if(accountEvent.getAction() == AccountEvent.Action.ADDED_ACCOUNTS) {
                accountListenerList.elementAt(i).accountsAdded(accountEvent);
            } else if(accountEvent.getAction() == AccountEvent.Action.CHANGED_ACCOUNTS) {
                accountListenerList.elementAt(i).accountsModified(accountEvent);
            } else if(accountEvent.getAction() == AccountEvent.Action.REFRESH_ALL) {
                accountListenerList.elementAt(i).accountsRefreshAll(accountEvent);
            } else {
                accountListenerList.elementAt(i).accountModified(accountEvent);
            }
        }
    }

    public Problem[] getProblems() {
        return problemDisplayList.getList();
    }
    
    public Category[] getCategories() {
        return categoryDisplayList.getList();
    }

    public Language[] getLanguages() {
        return languageDisplayList.getList();
    }

    public ClientId getClientId() {
        return localClientId;
    }

    public void setClientId(ClientId clientId) {
        this.localClientId = clientId;
        // if (isServer()){

        /**
         * Now that this model know its site number, we can now create the site specific database directories and files, or load them as needed.
         */

        // runList = new RunList(clientId.getSiteNumber(), true);
        // runFilesList = new RunFilesList(clientId.getSiteNumber());
        // clarificationList = new ClarificationList(clientId.getSiteNumber(), true);
        // }
        
        try {
            securityMessageHandler = new SecurityMessageHandler(clientId);
        } catch (Exception e) {
            logException("Trouble establishing SecurityMessageHandler", e);
        }
        
        if (isAllowed(localClientId, Permission.Type.ALLOWED_TO_FETCH_RUN)){
            // This client can cache run files
            if ( ! runFilesList.isWriteToDisk() ){
                // This client is not writing run files cache to disk already
                if ( ! runFilesList.isCacheRunFiles()){
                    // This client is not already caching run files.
                    runFilesList.setCacheRunFiles(true);
                }
            }
        }
    }
    
    public Log getSecurityAlertLog() {
        return securityMessageHandler.getLog();
    }

    public Site[] getSites() {
        return siteList.getList();
    }

    public String getTitle() {
        ClientId id = getClientId();
        if (id == null) {
            return "(Client not logged in)";
        }
        String titleCase = id.getClientType().toString();
        titleCase = titleCase.charAt(0) + titleCase.substring(1);
        return titleCase + " " + id.getClientNumber() + " (Site " + id.getSiteNumber() + ")";
    }

    public void addProblemListener(IProblemListener problemListener) {
        problemListenerList.addElement(problemListener);
    }

    public void removeProblemListener(IProblemListener problemListener) {
        problemListenerList.remove(problemListener);
    }

    public void addCategoryListener(ICategoryListener categoryListener) {
        categoryListenerList.addElement(categoryListener);
    }

    public void removeCategoryListener(ICategoryListener categoryListener) {
        categoryListenerList.remove(categoryListener);
    }

    public void addLanguageListener(ILanguageListener languageListener) {
        languageListenerList.addElement(languageListener);
    }

    public void removeLanguageListener(ILanguageListener languageListener) {
        languageListenerList.remove(languageListener);
    }
    
    public void addPlayBackEventListener(IPlayBackEventListener playBackEventListener) {
        playBackEventListenerList.addElement(playBackEventListener);
    }

    public void removePlayBackEventListener(IPlayBackEventListener playBackEventListener) {
        playBackEventListenerList.remove(playBackEventListener);
    }
    
    public void addChangePasswordListener(IChangePasswordListener changePasswordListener){
        changePasswordListenerList.add(changePasswordListener);
    }

    public void removeChangePasswordListener(IChangePasswordListener changePasswordListener){
        changePasswordListenerList.remove(changePasswordListener);
    }

    public void addLoginListener(ILoginListener loginListener) {
        loginListenerList.addElement(loginListener);
    }

    public void removeLoginListener(ILoginListener loginListener) {
        loginListenerList.remove(loginListener);
    }

    public void addSiteListener(ISiteListener siteListener) {
        siteListenerList.add(siteListener);
    }

    public void removeSiteListener(ISiteListener siteListener) {
        siteListenerList.remove(siteListener);
    }

    public void addConnectionListener(IConnectionListener connectionListener) {
        connectionListenerList.addElement(connectionListener);
    }

    public void removeConnectionListener(IConnectionListener connectionListener) {
        connectionListenerList.remove(connectionListener);
    }
    
    public void addBalloonSettingsListener(IBalloonSettingsListener balloonSettingsListener) {
        balloonSettingsListenerList.addElement(balloonSettingsListener);
    }
    
    public void removeBalloonSettingsListener(IBalloonSettingsListener balloonSettingsListener) {
        balloonSettingsListenerList.remove(balloonSettingsListener);
    }

    public Run getRun(ElementId id) {
        return runList.get(id);
    }

    public Clarification getClarification(ElementId id) {
        return clarificationList.get(id);
    }

    public Vector<Account> getAccounts(Type type, int inSiteNumber) {
        return accountList.getAccounts(type, inSiteNumber);
    }

    public Vector<Account> getAccounts(Type type) {
        return accountList.getAccounts(type);
    }

    public boolean isValidLoginAndPassword(ClientId inClientId, String password) {
        return accountList.isValidLoginAndPassword(inClientId, password);
    }

    public ClientId getLoginClientId(ConnectionHandlerID connectionHandlerID) {
        ClientId clientId = localLoginList.getClientId(connectionHandlerID);
        if (clientId == null) {
            clientId = remoteLoginList.getClientId(connectionHandlerID);
        }
        return clientId;
    }

    public boolean isLoggedIn() {
        return localClientId != null;
    }

    public boolean isRemoteLoggedIn(ClientId clientId) {
        return remoteLoginList.isLoggedIn(clientId);
    }

    public boolean isLocalLoggedIn(ClientId clientId) {
        return localLoginList.isLoggedIn(clientId);
    }
    
    public Date getLocalLoggedInDate (ClientId clientId){
        return localLoginList.getLoggedInDate(clientId);
    }

    public Enumeration<ConnectionHandlerID> getConnectionHandlerIDs(ClientId sourceId) {
        Enumeration<ConnectionHandlerID> connectionHandlerIDs = localLoginList.getConnectionHandlerIDs(sourceId);
        
        //TODO: what is the logic behind returning REMOTE connections if and only if there aren't any LOCAL connections?
        // It seems like either this method should return ALL connections (Local AND Remote), or should always only return Local...
        
        if (connectionHandlerIDs == null || !connectionHandlerIDs.hasMoreElements()) {
            connectionHandlerIDs = remoteLoginList.getConnectionHandlerIDs(sourceId);
        }
        return connectionHandlerIDs;
    }

    public ClientId getClientId(ConnectionHandlerID connectionHandlerID){
        return localLoginList.getClientId(connectionHandlerID);
    }
    
    public boolean isConnected(ConnectionHandlerID connectionHandlerID) {
        return localConnectionHandlerList.get(connectionHandlerID) != null;
    }

    public boolean isConnectedToRemoteSite(ConnectionHandlerID connectionHandlerID) {
        // TODO enh - the remoteConnectionHandlerList is never populated?!? used ?!?
        return remoteConnectionHandlerList.get(connectionHandlerID) != null;
    }

    public ConnectionHandlerID[] getConnectionHandleIDs() {

        ConnectionHandlerID[] localList = localConnectionHandlerList.getList();
        ConnectionHandlerID[] remoteList = remoteConnectionHandlerList.getList();

        if (localList.length > 0 && remoteList.length > 0) {
            // Add both list together
            ConnectionHandlerID[] allConnections = new ConnectionHandlerID[localList.length + remoteList.length];
            System.arraycopy(localList, 0, allConnections, 0, localList.length);
            System.arraycopy(remoteList, 0, allConnections, localList.length, remoteList.length);
            return allConnections;
        } else if (localList.length > 0) {
            return localList;
        } else { // must either be only remoteList
            return remoteList;
        }
    }

    /**
     * @throws IllegalArgumentException if the specified ClientId has a null ConnectionHandlerID.
     */
    public void removeRemoteLogin(ClientId clientIdToRemove) {
        ConnectionHandlerID connectionHandlerID = clientIdToRemove.getConnectionHandlerID();
        
        //this method should never be called with a ClientId containing a null ConnectionHandlerID; however, the addition of 
        // "multiple login" support may have left some place where this is inadvertently true.
        //The following is an effort to catch/identify such situations.
        if (connectionHandlerID==null) {
            IllegalArgumentException e = new IllegalArgumentException("InternalController.removeRemoteLogin() called with null ConnectionHandlerID in ClientId " + clientIdToRemove);
            e.printStackTrace();
            logException("InternalController.removeRemoteLogin() called with null ConnectionHandlerID in ClientId " + clientIdToRemove, e);
            throw e;
        }

        boolean removeWasSuccessful ;
        if (isRemoteLoggedIn(clientIdToRemove)) {
            removeWasSuccessful = remoteLoginList.remove(clientIdToRemove);
        } else {
            removeWasSuccessful = false;
        }
                
        //check the return value from remove() and log the results
        if (removeWasSuccessful) {
            logMessage(Level.INFO, "removed " + clientIdToRemove + " from remote login list");
        } else {
            logMessage(Level.WARNING, "attempt to remove " + clientIdToRemove + " from remote login list failed");
        }

        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.LOGOFF, clientIdToRemove, connectionHandlerID, "Remote Logoff");
        fireLoginListener(loginEvent);
    }

    /**
     * @throws IllegalArgumentException if the specified ClientId has a null ConnectionHandlerID.
     */
    public void removeLogin(ClientId clientIdToRemove) {
        ConnectionHandlerID connectionHandlerID = clientIdToRemove.getConnectionHandlerID();

        //this method should never be called with a ClientId containing a null ConnectionHandlerID; however, the addition of 
        // "multiple login" support may have left some place where this is inadvertently true.
        //The following is an effort to catch/identify such situations.
        if (connectionHandlerID==null) {
            IllegalArgumentException e = new IllegalArgumentException("InternalContest.removeLogin() called with null ConnectionHandlerID in ClientId " + clientIdToRemove);
            e.printStackTrace();
            logException("InternalContest.removeLogin() called with null ConnectionHandlerID in ClientId " + clientIdToRemove, e);
            throw e;
        }
        
        boolean clientToRemoveIsLocal = isLocalLoggedIn(clientIdToRemove) ;
        String removalTargetList = clientToRemoveIsLocal ? "local" : "remote";
        boolean removeWasSuccessful ;

        if (clientToRemoveIsLocal){
            removeWasSuccessful = localLoginList.remove(clientIdToRemove);
        } else {
            removeWasSuccessful = remoteLoginList.remove(clientIdToRemove);
        }
        
        //check the return value from remove() and log the results
        if (removeWasSuccessful) {
            logMessage(Level.INFO, "removed " + clientIdToRemove + " from " + removalTargetList + " login list");
        } else {
            logMessage(Level.WARNING, "attempt to remove " + clientIdToRemove + " from " + removalTargetList + " login list failed");
        }

        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.LOGOFF, clientIdToRemove, connectionHandlerID, "Logoff");
        fireLoginListener(loginEvent);
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(int number) {
        this.siteNumber = number;
    }
    
 
    public boolean isAllowed(Permission.Type type) {
        return isAllowed(getClientId(), type);
    }

    public boolean isAllowed(ClientId clientId, Permission.Type type) {
        Account account = getAccount(clientId);
        if (account == null) {
            ClientType.Type clientType = clientId.getClientType();
            if (clientType.equals(Type.SERVER)) {
                return new PermissionGroup().getPermissionList(clientType).isAllowed(type);
            }
            return false;
        } else {
            return account.isAllowed(type);
        }
    }

    /**
     * Get this site's contest time.
     */
    public ContestTime getContestTime() {
        return getContestTime(getSiteNumber());
    }

    public ContestTime getContestTime(int inSiteNumber) {
        return contestTimeList.get(inSiteNumber);
    }

    public ContestTime[] getContestTimes() {
        return contestTimeList.getList();
    }

    public void startContest(int inSiteNumber) {
        ContestTime contestTime = getContestTime(inSiteNumber);
        if (contestTime != null) {
            contestTime.startContestClock();
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CLOCK_STARTED, contestTime, inSiteNumber);
            fireContestTimeListener(contestTimeEvent);
        } else {
            throw new SecurityException("Unable to start clock site " + inSiteNumber + " not found");
        }
    }

    public void stopContest(int inSiteNumber) {
        ContestTime contestTime = getContestTime(inSiteNumber);
        if (contestTime != null) {
            contestTime.stopContestClock();
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CLOCK_STOPPED, contestTime, inSiteNumber);
            fireContestTimeListener(contestTimeEvent);            
        } else {
            throw new SecurityException("Unable to stop clock site " + inSiteNumber + " not found");
        }
    }

    public void addContestTime(ContestTime contestTime) {
        if (contestTime == null) {
            throw new IllegalArgumentException("contestTime is null");
        }
        // only do this if we do not already have this contesTime in the list
        // otherwise the add will throw a IllegalArguementException
        if (contestTimeList.get(contestTime.getSiteNumber()) == null) {
            contestTimeList.add(contestTime);
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.ADDED, contestTime, contestTime.getSiteNumber());
            fireContestTimeListener(contestTimeEvent);
        }
    }

    public ClientId[] getLocalLoggedInClients(Type type) {
        Enumeration<ClientId> localClients = localLoginList.getClients(type);

        Vector<ClientId> v = new Vector<ClientId>();

        while (localClients.hasMoreElements()) {
            ClientId element = (ClientId) localClients.nextElement();
            v.addElement(element);
        }
        return (ClientId[]) v.toArray(new ClientId[v.size()]);
    }

    public ClientId[] getRemoteLoggedInClients(Type type) {

        Enumeration<ClientId> remoteClients = remoteLoginList.getClients(type);

        Vector<ClientId> v = new Vector<ClientId>();

        while (remoteClients.hasMoreElements()) {
            ClientId element = (ClientId) remoteClients.nextElement();
            v.addElement(element);
        }

        return (ClientId[]) v.toArray(new ClientId[v.size()]);
    }

    /**
     * Returns an array containing all logins in this contest matching the specified {@link ClientType}.
     * 
     * Note that if multiple simultaneous logins are allowed for the specified ClientType and there are currently
     * multiple logins for a given clientId, the returned array will contain one element for each such login.
     * 
     * @param type the type of client for which logins are sought.
     * @return array of all logged in clients of the specified type.
     */

    public ClientId[] getAllLoggedInClients(Type type) {
        Enumeration<ClientId> localClients = localLoginList.getClients(type);
        Enumeration<ClientId> remoteClients = remoteLoginList.getClients(type);

        Vector<ClientId> v = new Vector<ClientId>();

        while (localClients.hasMoreElements()) {
            ClientId element = (ClientId) localClients.nextElement();
            v.addElement(element);
        }

        while (remoteClients.hasMoreElements()) {
            ClientId element = (ClientId) remoteClients.nextElement();
            v.addElement(element);
        }

        return (ClientId[]) v.toArray(new ClientId[v.size()]);
    }

    public Run[] getRuns() {
        return runList.getList();
    }

    public void runUpdated(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoUpdatedRun) throws IOException, ClassNotFoundException, FileSecurityException {
        // TODO handle run RunResultsFiles

        boolean manualReview = getProblem(run.getProblemId()).isManualReview();

        runList.updateRun(run, judgementRecord, manualReview);
        Run newRun = runList.get(run.getElementId());
        RunEvent runEvent = new RunEvent(RunEvent.Action.CHANGED, newRun, null, null);
        runEvent.setWhoModifiedRun(whoUpdatedRun);
        fireRunListener(runEvent);
    }
    
    public void updateRunStatus(Run run, RunExecutionStatus status, ClientId whoUpdatedRun) {

        RunEvent.Action action = null;
        switch (status) {
            case EXECUTING:
                action = RunEvent.Action.RUN_EXECUTING;
                break;
            case VALIDATING:
                action = RunEvent.Action.RUN_VALIDATING;
                break;
            case COMPILING:
            default:
                action = RunEvent.Action.RUN_COMPILING;
                break;
        }

        RunEvent runEvent = new RunEvent(action, run, null, null);
        runEvent.setWhoModifiedRun(whoUpdatedRun);
        fireRunListener(runEvent);
    }

    public void runNotAvailable(Run run) {
        RunEvent runEvent = new RunEvent(RunEvent.Action.RUN_NOT_AVAILABLE, run, null, null);
        fireRunListener(runEvent);
    }
    
    public Run checkoutRun(Run run, ClientId whoChangedRun, boolean reCheckoutRun, boolean computerJudge) throws RunUnavailableException, IOException, ClassNotFoundException, FileSecurityException {
        
        synchronized (runCheckOutList) {
            ClientId clientId = runCheckOutList.get(run.getElementId());

            if (clientId != null) {
                // Run checked out
                throw new RunUnavailableException("Client " + clientId + " already checked out run " + run.getNumber() + " (site " + run.getSiteNumber() + ")");
            }
            
            Run newRun = runList.get(run.getElementId());
            
            if (newRun == null){
                throw new RunUnavailableException("Run "+ run.getNumber() + " (site " + run.getSiteNumber() + ") not found");
            }
            
            boolean canBeCheckedOut = newRun.getStatus().equals(RunStates.NEW) || newRun.getStatus().equals(RunStates.QUEUED_FOR_COMPUTER_JUDGEMENT)
            || newRun.getStatus().equals(RunStates.MANUAL_REVIEW);
            
            if (reCheckoutRun && run.isJudged()){
                canBeCheckedOut = true;
            }
            
            if (canBeCheckedOut){
                runCheckOutList.put(newRun.getElementId(), whoChangedRun);
                newRun.setStatus(RunStates.BEING_JUDGED);
                
                if (reCheckoutRun){
                    newRun.setStatus(RunStates.BEING_RE_JUDGED);
                }
                runList.updateRun(newRun);
                return runList.get(run.getElementId());
            } else {
                throw new RunUnavailableException("Client " + whoChangedRun + " can not check out run " + run.getNumber() + " (site " + run.getSiteNumber() + ")");
            }
        
        }

    }
    
    public void updateRun(Run run, ClientId whoChangedRun) throws IOException, ClassNotFoundException, FileSecurityException {
        updateRun (run, null, whoChangedRun, null);
    }

    public void updateRun(Run run, RunFiles runFiles, ClientId whoChangedRun, RunResultFiles[] runResultFiles) throws IOException, ClassNotFoundException, FileSecurityException {

        /**
         * Should this run be un-checked out (removed from the checked out list) ? 
         */
        boolean unCheckoutRun = false;
        
        /**
         * Should this run be added to the checkout list ?
         */
        boolean checkOutRun = false;
        
        switch (run.getStatus()) {
            case CHECKED_OUT:
            case BEING_JUDGED:
            case BEING_RE_JUDGED:
            case HOLD:
                checkOutRun = true;
                break;
            case JUDGED:
            case NEW:
            case REJUDGE:
                unCheckoutRun = true;
            default:
                break; // put here to avoid Checkclipse warning
        }

        if (checkOutRun) {
            synchronized (runCheckOutList) {
                runCheckOutList.put(run.getElementId(), whoChangedRun);
            }
        } else if (unCheckoutRun) {
            synchronized (runCheckOutList) {
                ClientId clientId = runCheckOutList.get(run.getElementId());
                if (clientId != null){
                    runCheckOutList.remove(run.getElementId());
                }
            }
        }
        
        runList.updateRun(run);
        
        if (runFilesList.isCacheRunFiles()){
            runFilesList.add(run, runFiles);
        }
        
        if (! runResultFilesList.isWriteToDisk() && runResultFiles != null && runResultFiles.length > 0){
            runResultFilesList.add(run, run.getJudgementRecord(), runResultFiles[runResultFiles.length-1]);
        }
        
        RunEvent runEvent = new RunEvent(RunEvent.Action.CHANGED, runList.get(run), runFiles, runResultFiles);
        runEvent.setWhoModifiedRun(whoChangedRun);
        if (run.getStatus().equals(RunStates.BEING_JUDGED) || run.getStatus().equals(RunStates.BEING_RE_JUDGED) ){
            runEvent.setDetailedAction(RunEvent.Action.CHECKEDOUT_RUN);
        }
        
        if (checkOutRun) {
            runEvent.setSentToClientId(whoChangedRun);
        }
        fireRunListener(runEvent);
    }
    
    public RunFiles getRunFiles(Run run) throws IOException, ClassNotFoundException, FileSecurityException {
        return runFilesList.getRunFiles(run);
    }
    
    public boolean isRunFilesPresent(Run run) throws IOException, ClassNotFoundException, FileSecurityException {
        return runFilesList.isRunFilesPresent(run);
    }

    public void addRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoJudgedItId) throws IOException, ClassNotFoundException, FileSecurityException {

        Run theRun = runList.get(run);
        
        if (theRun != null ) {
            
            if (judgementRecord != null) {
                
                //at this point we know we have a non-null Run and a non-null JudgementRecord;
                // get the Ids of who's trying to add the judgement to the run and who (if anyone) has checked out the run
                ClientId whoIsChangingItId = judgementRecord.getJudgerClientId();
                ClientId whoCheckedItOutId = runCheckOutList.get(run.getElementId());
                
                //assume it's NOT ok to add the judgement, until we verify who is requesting the add
                boolean okToAddJudgement = false ;
                
                //see if it's a superuser trying to add the judgement
                if (whoIsChangingItId.getClientType().equals(Type.ADMINISTRATOR) || whoIsChangingItId.getClientType().equals(Type.FEEDER) ) {
                    //TODO: the above test should probably be consolidated into a single method something like "isSuperUser()".  
                    // The PROBLEM with this is that there are different PERMISSIONS involved.  For example, a Feeder invokes this method when it
                    // has the "EDIT_RUN" permission -- but there exists another Permission named "ADD_JUDGEMENTS"; such a method would have to
                    // figure out what permission(s) need to be required to be a "superuser".  Work deferred for now.  jlc 8/2/21
                    
                    okToAddJudgement = true;
                    
                } else {
                    
                    //it's not a superuser trying to add the judgement; see if anyone has checked out the run
                    if (whoCheckedItOutId == null) {
                        
                        // No one has checked out this run and it's not a "super-user" (Admin or Feeder) that's trying to add a judgement to it -- that's not logical...
                        Exception ex = new Exception("addRunJudgement - run not in checkedout list ('whoCheckedOut' is null) and requestor is not Admin or Feeder ");
                        logException("Odd that. ", ex);
                        
                        okToAddJudgement = false;     //note that this variable should already be false here anyway.... this is for insurance
        
                    } else {
                        
                        //someone has checked out the run; see if it is the client who's trying to change it that has it checked out
                        if (whoIsChangingItId.equals(whoCheckedItOutId)) {
                            
                            //the changer is the client who checked it out; we'll allow that
                            okToAddJudgement = true;
                            
                        } else {
                        
                            // The client who submitted this add-judgement request is different than the client who checked out the run (and we already know the requestor is not a superuser);
                            //  that's not a situation we're willing to allow
                            Exception ex = new Exception("addRunJudgement - the client who checked this run out is not the same as the client who's changing it, "
                                                             + "and the client who's changing it is not a 'superuser' (Admin or Feeder)" );
                            logException(ex);
                            
                            okToAddJudgement = false;     //note that this variable should already be false here anyway.... this is for insurance
        
                        }
                    } 
                }         
                
                if (okToAddJudgement) {
                
                    boolean manualReview = getProblem(theRun.getProblemId()).isManualReview();
                    
                    // pass in the original run which has the testCases
                    runList.updateRun(run, judgementRecord, manualReview); // this sets run to JUDGED and saves the testCases
                    runResultFilesList.add(theRun, judgementRecord, runResultFiles);
                    if (whoCheckedItOutId != null) {
                        runCheckOutList.remove(run.getElementId());
                    }
                    // the new run with testCases
                    theRun = runList.get(run);
                    RunEvent runEvent = new RunEvent(RunEvent.Action.CHANGED, theRun, null, null);
                    fireRunListener(runEvent);
                }
                    
            } else {
                
                //We've been asked to add a null judgementRecord to a run, which we can't do...
                //TODO: need to log this as an error, but there's no local log, no getLog(), etc...
                // use StaticLog??
            }
            
        } else {
             
            //We've been asked to add a judgement to a null run, which we can't do...
            //TODO: need to log this as an error, but there's no local log, no getLog(), etc...
            // use StaticLog??
        }
    }

    public void cancelRunCheckOut(Run run, ClientId fromId) throws UnableToUncheckoutRunException, IOException, ClassNotFoundException, FileSecurityException {
        
        // TODO Security check, code needed to insure that only
        // certain accounts can cancel a checkout

        ClientId whoCheckedOut = runCheckOutList.get(run.getElementId());

        if (whoCheckedOut == null) {
            throw new UnableToUncheckoutRunException(fromId + " can not checkout " + run + " not checked out");
        }

        /**
         * This is the user who checked out the run.
         */
        boolean userCheckedOutRun = fromId.equals(whoCheckedOut);

        if (isAdministrator(fromId) && isAllowed(fromId, Permission.Type.EDIT_RUN)) {
            // Allow admin to override if they have permission to edit a run
            userCheckedOutRun = true;
        }

        if (!userCheckedOutRun) {
            throw new UnableToUncheckoutRunException(fromId + " can not checkout " + run + " checked out to " + whoCheckedOut);
        }

        Run theRun = getRun(run.getElementId());

        if (theRun.getStatus().equals(RunStates.BEING_JUDGED)) {
            if (theRun.getComputerJudgementRecord() == null) {
                theRun.setStatus(RunStates.NEW);
            } else {
                theRun.setStatus(RunStates.MANUAL_REVIEW);
            }
        } else if (theRun.getStatus().equals(RunStates.BEING_RE_JUDGED)) {
            theRun.setStatus(RunStates.JUDGED);
        }
        
        synchronized (runCheckOutList) {
            runCheckOutList.remove(theRun.getElementId());
        }
        
        runList.updateRun(theRun);
        theRun = runList.get(theRun);

        RunEvent runEvent = new RunEvent(RunEvent.Action.RUN_AVAILABLE, theRun, null, null);
        fireRunListener(runEvent);
    }

    protected boolean isAdministrator(ClientId clientId) {
        return clientId.getClientType().equals(ClientType.Type.ADMINISTRATOR);
    }

    public ClientId getRunCheckedOutBy(Run run) {
        return runCheckOutList.get(run.getElementId());
    }

    public ElementId[] getRunIdsCheckedOutBy(ClientId judgeID) {
        ElementId runId;
        ClientId cID;
        
        Vector<ElementId> v = new Vector<ElementId>();
        Enumeration<ElementId> runIDs = runCheckOutList.keys();
        
        while (runIDs.hasMoreElements()) {
            
           runId = (ElementId) runIDs.nextElement();
            
           cID = runCheckOutList.get(runId);
           
           if (cID.equals(judgeID)) {
               v.addElement(runId);
           }
        }
        
        return (ElementId[]) v.toArray(new ElementId[v.size()]);
    }
    
    public Clarification[] getClarifications() {
        return clarificationList.getList();
    }
    
    public void passwordChanged(boolean success, ClientId clientId, String message) {
        Action action = Action.PASSWORD_NOT_CHANGED;
        if (success) {
            action = Action.PASSWORD_CHANGED;
        }
        PasswordChangeEvent passwordChangeEvent = new PasswordChangeEvent(clientId, action, message);
        firePasswordChangeListener(passwordChangeEvent);
    }

    private void firePasswordChangeListener(PasswordChangeEvent passwordChangeEvent) {

        for (int i = 0; i < changePasswordListenerList.size(); i++) {

            if (passwordChangeEvent.getAction() == PasswordChangeEvent.Action.PASSWORD_CHANGED) {
                changePasswordListenerList.elementAt(i).passwordChanged(passwordChangeEvent);
            } else if (passwordChangeEvent.getAction() == PasswordChangeEvent.Action.PASSWORD_NOT_CHANGED) {
                changePasswordListenerList.elementAt(i).passwordNotChanged(passwordChangeEvent);
            } else {
                throw new IllegalArgumentException("Unhandled  ");
            }
        }
    }

    public void updateLanguage(Language language) {
        languageList.update(language);
        languageDisplayList.update(language);
        LanguageEvent languageEvent = new LanguageEvent(LanguageEvent.Action.CHANGED, language);
        fireLanguageListener(languageEvent);
    }

    public void updateProblem(Problem problem) {
        problemList.update(problem);
        problemDisplayList.update(problem);
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.CHANGED, problem);
        fireProblemListener(problemEvent);
    }
    

    public void updateCategory(Category category) {
        categoryList.update(category);
        categoryDisplayList.update(category);
        fireCategoryListener(category, CategoryEvent.Action.CHANGED);
    }

    public void updateContestTime(ContestTime contestTime, int inSiteNumber) {
        if (contestTime == null) {
            throw new IllegalArgumentException("contestTime is null");
        }
        if (inSiteNumber != contestTime.getSiteNumber()) {
            throw new IllegalArgumentException("contestTime site number (" + contestTime + ") does not match " + inSiteNumber);
        }
        contestTimeList.update(contestTime);
        ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CHANGED, contestTime, contestTime.getSiteNumber());
        fireContestTimeListener(contestTimeEvent);

    }

    public void updateContestTime(ContestTime contestTime) {
        if (contestTime == null) {
            throw new IllegalArgumentException("contestTime is null");
        }
        contestTimeList.update(contestTime);
        ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CHANGED, contestTime, contestTime.getSiteNumber());
        fireContestTimeListener(contestTimeEvent);
    }

    public void updateJudgement(Judgement judgement) {
        judgementDisplayList.update(judgement);
        judgementList.update(judgement);
        JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.CHANGED, judgement);
        fireJudgementListener(judgementEvent);
    }

    public void changeSite(Site site) {
        siteList.update(site);
        SiteEvent siteEvent = new SiteEvent(SiteEvent.Action.CHANGED, site);
        fireSiteListener(siteEvent);
    }

    public void updateAccount(Account account) {
        accountList.update(account);
        AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.CHANGED, account);
        fireAccountListener(accountEvent);
    }
    
    public void updateAccounts(Account[] accounts) {
        for (Account account : accounts) {
            accountList.update(account);
        }
        AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.CHANGED_ACCOUNTS, accounts);
        fireAccountListener(accountEvent);
    }

    public Language getLanguage(ElementId elementId) {
        return (Language) languageList.get(elementId);
    }

    public ContestTime getContestTime(ElementId elementId) {
        return (ContestTime) contestTimeList.get(elementId);
    }

    public Problem getProblem(ElementId elementId) {
        
        // TODO eliminate generalProblem
        if (generalProblem != null && generalProblem.getElementId().equals(elementId)){
            return generalProblem;
        } else {
            Problem problem = (Problem) problemList.get(elementId);
            if (problem == null){
                problem = getCategory(elementId);
            }
            return problem;
        }
    }
    
    public Category getCategory(ElementId elementId) {
        return (Category) categoryList.get(elementId);
    }



    public Judgement getJudgement(ElementId elementId) {
        return (Judgement) judgementList.get(elementId);
    }

    public Account getAccount(ClientId inClientId) {
        return (Account) accountList.getAccount(inClientId);
    }

    public Site getSite(int number) {
        Site[] sites = siteList.getList();
        for (Site site : sites){
            if (site.getSiteNumber() == number){
                return site;
            }
        }
        return null;
    }

    private void fireClarificationListener(ClarificationEvent clarificationEvent) {
        for (int i = 0; i < clarificationListenerList.size(); i++) {

            if (clarificationEvent.getAction() == ClarificationEvent.Action.ADDED) {
                clarificationListenerList.elementAt(i).clarificationAdded(clarificationEvent);
            } else if (clarificationEvent.getAction() == ClarificationEvent.Action.REFRESH_ALL) {
                clarificationListenerList.elementAt(i).refreshClarfications(clarificationEvent);
            } else if (clarificationEvent.getAction() == ClarificationEvent.Action.DELETED) {
                clarificationListenerList.elementAt(i).clarificationRemoved(clarificationEvent);
            } else {
                clarificationListenerList.elementAt(i).clarificationChanged(clarificationEvent);
            }
        }
    }

    public void addClarification(Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException {
        clarificationList.add(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.ADDED, clarification);
        fireClarificationListener(clarificationEvent);
    }

    public void addClarification(Clarification clarification, ClientId whoCheckedOutId) throws IOException, ClassNotFoundException, FileSecurityException {
        clarificationList.add(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.CHECKEDOUT_CLARIFICATION, clarification);
        clarificationEvent.setSentToClientId(whoCheckedOutId);
        fireClarificationListener(clarificationEvent);
    }

    public void removeClarification(Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException {
        clarificationList.delete(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.DELETED, clarification);
        fireClarificationListener(clarificationEvent);
    }

    public void changeClarification(Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException {
        clarificationList.updateClarification(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.CHANGED, clarification);
        fireClarificationListener(clarificationEvent);
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID) {
        connectionEstablished(connectionHandlerID, new Date());
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID, Date connectDate) {
        localConnectionHandlerList.add(connectionHandlerID, connectDate);
        ConnectionEvent connectionEvent = new ConnectionEvent(ConnectionEvent.Action.ESTABLISHED, connectionHandlerID);
        fireConnectionListener(connectionEvent);
    }

    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {
        if (connectionHandlerID != null){
            localConnectionHandlerList.remove(connectionHandlerID);
        }
        ConnectionEvent connectionEvent = new ConnectionEvent(ConnectionEvent.Action.DROPPED, connectionHandlerID);
        fireConnectionListener(connectionEvent);
    }

    public ConnectionHandlerID[] getConnectionHandlerIDs() {
        return localConnectionHandlerList.getList();
    }

    public Clarification[] getClarifications(ClientId clientId) {

        Vector<Clarification> clientClarifications = new Vector<Clarification>();
        Enumeration<Clarification> enumeration = clarificationList.getClarList();
        while (enumeration.hasMoreElements()) {
            Clarification clarification = (Clarification) enumeration.nextElement();

            if (clarification.isSendToAll() || clientId.equals(clarification.getSubmitter())) {
                clientClarifications.add(clarification);
            }
        }
        return (Clarification[]) clientClarifications.toArray(new Clarification[clientClarifications.size()]);
    }

    public Run[] getRuns(ClientId clientId) {
        Vector<Run> clientRuns = new Vector<Run>();
        Enumeration<Run> enumeration = runList.getRunList();
        while (enumeration.hasMoreElements()) {
            Run run = (Run) enumeration.nextElement();

            if (clientId.equals(run.getSubmitter())) {
                clientRuns.add(run);
            }
        }
        return (Run[]) clientRuns.toArray(new Run[clientRuns.size()]);
    }

    public void addProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        problemDisplayList.add(problem);
        problemList.add(problem);
        if (problemDataFiles != null) {
            problemDataFilesList.add(problemDataFiles);
        }

        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.ADDED, problem, problemDataFiles);
        fireProblemListener(problemEvent);
    }

    public void updateProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        problemList.update(problem);
        problemDataFilesList.update(problemDataFiles);
        problemDisplayList.update(problem);
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.CHANGED, problem, problemDataFiles);
        fireProblemListener(problemEvent);
    }

    public ProblemDataFiles getProblemDataFile(Problem problem) {
        return (ProblemDataFiles) problemDataFilesList.get(problem);
    }

    public ProblemDataFiles[] getProblemDataFiles() {
        return problemDataFilesList.getList();
    }

    public void cancelClarificationCheckOut(Clarification clarification, ClientId whoCancelledIt) throws IOException, ClassNotFoundException, FileSecurityException {
        // TODO verify the canceler has permissions to cancel this clar
        clarificationList.updateClarification(clarification, ClarificationStates.NEW, whoCancelledIt);
        synchronized (clarCheckOutList) {
            clarCheckOutList.remove(clarification.getElementId());
        }
        Clarification theClarification = clarificationList.get(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.CLARIFICATION_AVAILABLE, theClarification);
        fireClarificationListener(clarificationEvent);
    }

    private void fireClientSettingsListener(ClientSettingsEvent clientSettingsEvent) {
        for (int i = 0; i < clientSettingsListenerList.size(); i++) {

            if (clientSettingsEvent.getAction() == ClientSettingsEvent.Action.ADDED) {
                clientSettingsListenerList.elementAt(i).clientSettingsAdded(clientSettingsEvent);
            } else if (clientSettingsEvent.getAction() == ClientSettingsEvent.Action.DELETED) {
                clientSettingsListenerList.elementAt(i).clientSettingsRemoved(clientSettingsEvent);
            } else if (clientSettingsEvent.getAction() == ClientSettingsEvent.Action.REFRESH_ALL) {
                clientSettingsListenerList.elementAt(i).clientSettingsRefreshAll(clientSettingsEvent);
            } else {
                clientSettingsListenerList.elementAt(i).clientSettingsChanged(clientSettingsEvent);
            }
        }
    }

    public void addClientSettings(ClientSettings clientSettings) {
        clientSettingsList.add(clientSettings);
        ClientSettingsEvent clientSettingsEvent = new ClientSettingsEvent(ClientSettingsEvent.Action.ADDED, clientSettings.getClientId(), clientSettings);
        fireClientSettingsListener(clientSettingsEvent);
    }

    public ClientSettings getClientSettings() {
        return (ClientSettings) clientSettingsList.get(getClientId());
    }

    public ClientSettings getClientSettings(ClientId clientId) {
        return (ClientSettings) clientSettingsList.get(clientId);
    }

    public ClientSettings[] getClientSettingsList() {
        return clientSettingsList.getList();
    }

    public void updateClientSettings(ClientSettings clientSettings) {
        clientSettingsList.update(clientSettings);
        ClientSettingsEvent clientSettingsEvent = new ClientSettingsEvent(ClientSettingsEvent.Action.CHANGED, clientSettings.getClientId(), clientSettings);
        fireClientSettingsListener(clientSettingsEvent);
    }

    public void addClientSettingsListener(IClientSettingsListener clientSettingsListener) {
        clientSettingsListenerList.addElement(clientSettingsListener);
    }

    public void removeClientSettingsListener(IClientSettingsListener clientSettingsListener) {
        clientSettingsListenerList.remove(clientSettingsListener);
    }

    private void fireContestInformationListener(ContestInformationEvent contestInformationEvent) {
        for (int i = 0; i < contestInformationListenerList.size(); i++) {

            if (contestInformationEvent.getAction() == ContestInformationEvent.Action.ADDED) {
                contestInformationListenerList.elementAt(i).contestInformationAdded(contestInformationEvent);
            } else if (contestInformationEvent.getAction() == ContestInformationEvent.Action.DELETED) {
                contestInformationListenerList.elementAt(i).contestInformationRemoved(contestInformationEvent);
            } else if (contestInformationEvent.getAction() == ContestInformationEvent.Action.REFRESH_ALL) {
                contestInformationListenerList.elementAt(i).contestInformationRefreshAll(contestInformationEvent);
            } else if (contestInformationEvent.getAction() == ContestInformationEvent.Action.CHANGED_FINALIZED) {
                contestInformationListenerList.elementAt(i).finalizeDataChanged(contestInformationEvent);
            } else {
                contestInformationListenerList.elementAt(i).contestInformationChanged(contestInformationEvent);
            }
        }
    }

    public void addContestInformation(ContestInformation inContestInformation) {
        this.contestInformation = inContestInformation;
        ContestInformationEvent contestInformationEvent = new ContestInformationEvent(ContestInformationEvent.Action.ADDED, contestInformation);
        fireContestInformationListener(contestInformationEvent);
    }

    public void updateContestInformation(ContestInformation inContestInformation) {
        this.contestInformation = inContestInformation;
        ContestInformationEvent contestInformationEvent = new ContestInformationEvent(ContestInformationEvent.Action.CHANGED, contestInformation);
        fireContestInformationListener(contestInformationEvent);
    }
           
    public void addContestInformationListener(IContestInformationListener contestInformationListener) {
        contestInformationListenerList.addElement(contestInformationListener);
    }

    public void removeContestInformationListener(IContestInformationListener contestInformationListener) {
        contestInformationListenerList.remove(contestInformationListener);
    }

    public ContestInformation getContestInformation() {
        return contestInformation;
    }

    public void setJudgementList(Judgement[] judgements) {

        // Remove all judgements from display list and fire listeners
        for (Judgement judgement : judgementDisplayList.getList()) {
            JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.DELETED, judgement);
            fireJudgementListener(judgementEvent);
        }

        judgementDisplayList = new JudgementDisplayList();

        for (Judgement judgement : judgements) {
            Judgement judgementFromList = (Judgement) judgementList.get(judgement.getElementId());
            if (judgementFromList == null) {
                // if not in list add to list.
                judgementList.add(judgement);
            }
            judgementDisplayList.add(judgement);
            JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.ADDED, judgement);
            fireJudgementListener(judgementEvent);
        }
    }

    public void removeJudgement(Judgement judgement) {

        int idx = judgementDisplayList.indexOf(judgement);
        if (idx != -1) {
            judgementDisplayList.remove(idx);
            JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.DELETED, judgement);
            fireJudgementListener(judgementEvent);
        }
    }

    public int getMaxRetryMSecs() {
        return 700;
    }

    public int getMaxConnectionRetries() {
        return 5;
    }

    public void addBalloonSettings(BalloonSettings balloonSettings) {
        balloonSettingsList.add(balloonSettings);
        BalloonSettingsEvent balloonSettingsEvent = new BalloonSettingsEvent(BalloonSettingsEvent.Action.ADDED, balloonSettings);
        fireBalloonSettingsListener(balloonSettingsEvent);
    }

    public void updateBalloonSettings(BalloonSettings balloonSettings) {
        balloonSettingsList.update(balloonSettings);
        BalloonSettingsEvent balloonSettingsEvent = new BalloonSettingsEvent(BalloonSettingsEvent.Action.CHANGED, balloonSettings);
        fireBalloonSettingsListener(balloonSettingsEvent);
    }

    public BalloonSettings getBalloonSettings(int siteNum) {
        for (BalloonSettings balloonSettings : balloonSettingsList.getList()) {
            if (siteNum == balloonSettings.getSiteNumber()) {
                return balloonSettings;
            }
        }
        return null;
    }

    public BalloonSettings[] getBalloonSettings() {
        return balloonSettingsList.getList();
    }

    public BalloonSettings getBalloonSettings(ElementId elementId) {
        return balloonSettingsList.get(elementId);
    }

    public void addGroup(Group group) {
        groupDisplayList.add(group);
        groupList.add(group);
        GroupEvent groupEvent = new GroupEvent(GroupEvent.Action.ADDED, group);
        fireGroupListener(groupEvent);
    }

    private void fireGroupListener(GroupEvent groupEvent) {
        for (int i = 0; i < groupListenerList.size(); i++) {

            if (groupEvent.getAction() == GroupEvent.Action.ADDED) {
                groupListenerList.elementAt(i).groupAdded(groupEvent);
            } else if (groupEvent.getAction() == GroupEvent.Action.DELETED) {
                groupListenerList.elementAt(i).groupRemoved(groupEvent);
            } else if (groupEvent.getAction() == GroupEvent.Action.REFRESH_ALL) {
                groupListenerList.elementAt(i).groupRefreshAll(groupEvent);
            } else if (groupEvent.getAction() == GroupEvent.Action.ADDED_GROUPS) {
                groupListenerList.elementAt(i).groupRefreshAll(groupEvent);
            } else if (groupEvent.getAction() == GroupEvent.Action.CHANGED_GROUPS) {
                groupListenerList.elementAt(i).groupRefreshAll(groupEvent);
            } else {
                groupListenerList.elementAt(i).groupChanged(groupEvent);
            }
        }
    }

    public void removeGroup(Group group) {
        int idx = groupDisplayList.indexOf(group);
        if (idx != -1) {
            groupDisplayList.remove(idx);
            GroupEvent groupEvent = new GroupEvent(GroupEvent.Action.DELETED, group);
            fireGroupListener(groupEvent);
        }
    }

    public void updateGroup(Group group) {
        groupList.update(group);
        groupDisplayList.update(group);
        GroupEvent groupEvent = new GroupEvent(GroupEvent.Action.CHANGED, group);
        fireGroupListener(groupEvent);
    }

    public Group[] getGroups() {
        return groupDisplayList.getList();
    }

    public void addGroupListener(IGroupListener groupListener) {
        groupListenerList.addElement(groupListener);
    }

    public void removeGroupListener(IGroupListener groupListener) {
        groupListenerList.remove(groupListener);
    }

    public Group getGroup(ElementId elementId) {
        return (Group) groupList.get(elementId);
    }

    public Problem getGeneralProblem() {
        return generalProblem;
    }

    public void setGeneralProblem(Problem generalProblem) {
        this.generalProblem = generalProblem;
    }

    /* (non-Javadoc)
     * @see edu.csus.ecs.pc2.core.model.IInternalContest#checkoutClarification(edu.csus.ecs.pc2.core.model.Clarification, edu.csus.ecs.pc2.core.model.ClientId)
     */
    public Clarification checkoutClarification(Clarification clar, ClientId whoChangedClar) throws ClarificationUnavailableException, IOException, ClassNotFoundException, FileSecurityException {
        synchronized (clarCheckOutList) {
            ClientId clientId = clarCheckOutList.get(clar.getElementId());

            if (clientId != null) {
                // Run checked out
                throw new ClarificationUnavailableException("Client " + clientId + " already checked out clar " + clar.getNumber() + " (site " + clar.getSiteNumber() + ")");
            }
            
            Clarification newClar = clarificationList.get(clar.getElementId());
            
            if (newClar == null){
                throw new ClarificationUnavailableException("Run "+ clar.getNumber() + " (site " + clar.getSiteNumber() + ") not found");
            }
            
            if (newClar.getState().equals(ClarificationStates.NEW)){
                clarCheckOutList.put(newClar.getElementId(), whoChangedClar);
                newClar.setState(ClarificationStates.BEING_ANSWERED);
                newClar.setWhoCheckedItOutId(whoChangedClar);
                clarificationList.updateClarification(newClar);
                return clarificationList.get(clar.getElementId());
            } else {
                throw new ClarificationUnavailableException("Client " + whoChangedClar + " can not check out clar " + clar.getNumber() + " (site " + clar.getSiteNumber() + ")");
            }
        
        }
    }

    /**
     * Issue a security message, fire listeners.
     * 
     */
    public void newSecurityMessage(ClientId clientId, String message, String eventName, ContestSecurityException contestSecurityException) {
        securityMessageHandler.newMessage(clientId, eventName, message, contestSecurityException);
    }
    
    /**
     * Add security message Listener.
     * 
     * This listener will be given an {@link edu.csus.ecs.pc2.core.security.SecurityMessageEvent}
     * when a security message is added/logged. 
     * 
     * @param securityMessageListener
     */
    public void addSecurityMessageListener(ISecurityMessageListener securityMessageListener) {
        securityMessageHandler.addSecurityMessageListener(securityMessageListener);
    }

    /**
     * Remove security message listener.
     * @param securityMessageListener
     */
    public void removeSecurityMessageListener(ISecurityMessageListener securityMessageListener) {
        securityMessageHandler.addSecurityMessageListener(securityMessageListener);
    }

    public RunResultFiles[] getRunResultFiles(Run run) throws IOException, ClassNotFoundException, FileSecurityException {
        
        return runResultFilesList.getRunResultFiles(run);
    }

    public void resetSubmissionData() {
        
        synchronized (runCheckOutList){
            // FIXME on all client Run listeners (elsewhere) need to uncheckout too 
            runCheckOutList = new Hashtable<ElementId, ClientId>();
        }

        /**
         * Clear list of runs.
         */
        try {
            runList.clear();
        } catch (IOException e) {
            logException(e);
        } catch (ClassNotFoundException e) {
            logException(e);
        } catch (FileSecurityException e) {
            logException(e);
        }
        
        /**
         * Clear submitted files list 
         */
        runFilesList.clearCache();
        
        runResultFilesList.clear();

        synchronized (clarCheckOutList) {
            // FIXME on all client Clar listeners (elsewhere) need to uncheckout too 
            clarCheckOutList = new Hashtable<ElementId, ClientId>();
        }
        
        /**
         * Clear all clarifications
         */
        try {
            clarificationList.clear();
        } catch (IOException e) {
            logException(e);
        } catch (ClassNotFoundException e) {
            logException(e);
        } catch (FileSecurityException e) {
            logException(e);
        }
    }
    
    public void resetConfigurationData() {

        // GROUPS
        groupList = new GroupList();
        groupDisplayList = new GroupDisplayList();

        // ACCOUNTS
        accountList = new AccountList();

        // JUDGEMENTS
        judgementDisplayList = new JudgementDisplayList();
        judgementList = new JudgementList();

        // PROBLEMS
        problemList = new ProblemList();
        problemDataFilesList = new ProblemDataFilesList();
        problemDisplayList = new ProblemDisplayList();

        // LANGUAGES
        languageList = new LanguageList();
        languageDisplayList = new LanguageDisplayList();

        // CONTEST TIME
        contestTimeList = new ContestTimeList();

        // CONTEST INFORMATION
        contestInformation = new ContestInformation();

        // CLIENT SETTINGS
        clientSettingsList = new ClientSettingsList();

        // BALLOON SETTINGS
        balloonSettingsList = new BalloonSettingsList();

        // General Clarification

        generalProblem = null;

        // Clarification Categories
        categoryList = new CategoryList();
        categoryDisplayList = new CategoryDisplayList();
        
        // SITES
        siteList = new SiteList();

        // PROFILES
        profileList = new ProfilesList();
        
        // OTHER SETTINGS
        profileCloneSettings = null;

        // not runs - use resetSubmissionsData
        // not clars - use resetSubmissionsData

        // LoginList localLoginList = new LoginList();
        // LoginList remoteLoginList = new LoginList();
        // ConnectionHandlerList localConnectionHandlerList = new ConnectionHandlerList();
        // ConnectionHandlerList remoteConnectionHandlerList = new ConnectionHandlerList();

    }

    public boolean isSendAdditionalRunStatusMessages() {
        return contestInformation.isSendAdditionalRunStatusInformation();
    }

    public void deleteProblem(Problem problem) {
        problemDisplayList.removeElement(problem);
        problemList.delete(problem);
        problemDataFilesList.delete(problem);
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.DELETED, problem);
        fireProblemListener(problemEvent);
    }
    
    public void deleteCategory(Category category) {
        categoryDisplayList.removeElement(category);
        categoryList.delete(category);
        fireCategoryListener(category, CategoryEvent.Action.DELETED);
    }

    public void deleteLanguage(Language language) {
        languageDisplayList.removeElement(language);
        languageList.delete(language);
        LanguageEvent languageEvent = new LanguageEvent(LanguageEvent.Action.DELETED, language);
        fireLanguageListener(languageEvent);
    }

    public boolean contestIdMatches(String identifier) {
        if (profile == null){
            return false;
        }
        return profile.matchesIdentifier(identifier);
    }

    public String getContestIdentifier() {
        if (profile != null){
            return profile.getContestId();
        } else {
            return contestIdentifier;
        }
            
    }

    public Profile getProfile() {
        return profile;
    }
 
    public Profile getProfile(ElementId id) {
        return (Profile) profileList.get(id);
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        if (profileList.size() == 0){
            addProfile(profile);
        } else {
            updateProfile(profile);
        }
        
        setContestIdentifier(profile.getContestId());
        
    }

    public void setContestIdentifier(String contestId) {
        contestIdentifier = contestId;
    }

    public Profile addProfile(Profile theProfile) {
        profileList.add(theProfile);
        ProfileEvent profileEvent = new ProfileEvent(ProfileEvent.Action.ADDED, theProfile);
        fireProfileListener(profileEvent);
        return theProfile;
    }

    private void fireProfileListener(ProfileEvent profileEvent) {
        for (int i = 0; i < profileListenerList.size(); i++) {

            if (profileEvent.getAction() == ProfileEvent.Action.ADDED) {
                profileListenerList.elementAt(i).profileAdded(profileEvent);
            } else if (profileEvent.getAction() == ProfileEvent.Action.DELETED) {
                profileListenerList.elementAt(i).profileRemoved(profileEvent);
            } else if (profileEvent.getAction() == ProfileEvent.Action.REFRESH_ALL) {
                profileListenerList.elementAt(i).profileRefreshAll(profileEvent);
            } else {
                profileListenerList.elementAt(i).profileChanged(profileEvent);
            }
        }
    }

    public void addProfileListener(IProfileListener profileListener) {
        profileListenerList.add(profileListener);
    }

    public void deleteProfile(Profile theProfile) {
        profileList.delete(theProfile);
        ProfileEvent profileEvent = new ProfileEvent(ProfileEvent.Action.DELETED, theProfile);
        fireProfileListener(profileEvent);
    }

    public Profile[] getProfiles() {
        return profileList.getList();
    }

    public void removeProfileListener(IProfileListener profileListener) {
        profileListenerList.remove(profileListener);
    }

    public Profile updateProfile(Profile theProfile) {
        profileList.update(theProfile);
        ProfileEvent profileEvent = new ProfileEvent(ProfileEvent.Action.CHANGED, theProfile);
        fireProfileListener(profileEvent);
        return theProfile;
    }
    
    public boolean storeConfiguration(Log log) throws IOException, ClassNotFoundException, FileSecurityException {
        return configurationIO.store(this, log);
    }

    public boolean readConfiguration(int siteNum, Log log, boolean uncheckoutSubmissions) throws IOException, ClassNotFoundException, FileSecurityException {
        
        String settingsFilename = configurationIO.getFileName();
        
        if (! new File(settingsFilename).exists()){
            throw new FileNotFoundException("Profile/config file missing "+settingsFilename);
        }
        
        boolean loadedConfiguration = configurationIO.loadFromDisk(siteNum, this, log);
        initializeSubmissions(siteNum, uncheckoutSubmissions);
        // Initialize contest time if necessary
        ContestTime contestTime = getContestTime(siteNum);
        if (contestTime == null) {
            contestTime = new ContestTime(siteNum);
            addContestTime(contestTime);
        }
        return loadedConfiguration;
    }
    
    public boolean readConfiguration(int siteNum, Log log) throws IOException, ClassNotFoundException, FileSecurityException {
        return readConfiguration(siteNum, log, true);
    }

    public void setStorage(IStorage storage) {
        this.storage = storage;
        configurationIO = new ConfigurationIO(storage);
    }

    public void setupDefaultCategories() {
        if (getCategories().length == 0){
            String []catNames = {"General"};
            for (String name : catNames){
                addCategory(new Category(name));
            }
        }
    }

    public String getContestPassword() {
        return contestPassword;
    }

    public void setContestPassword(String contestPassword) {
        this.contestPassword = contestPassword;
    }

    public IStorage getStorage() {
        return storage;
    }

    public IInternalContest clone(IInternalContest contest, Profile newProfile, ProfileCloneSettings settings) throws ProfileCloneException {

        // TODO check for existing profile

        String profilePath = newProfile.getProfilePath();

        try {
            new File(profilePath).mkdirs();
        } catch (Exception e) {
            throw new ProfileCloneException("Unable to create profile dir " + profilePath, e);
        }

        if (!new File(profilePath).isDirectory()) {
            throw new ProfileCloneException("Unable to use profile dir " + profilePath);
        }
        
        String databaseDirectoryName = profilePath + File.separator + "db." + contest.getSiteNumber();

        try {
            new File(databaseDirectoryName).mkdirs();
        } catch (Exception e) {
            throw new ProfileCloneException("Unable to create DB dir " + profilePath, e);
        }
        
        String logDirectoryName = profilePath + File.separator + Log.LOG_DIRECTORY_NAME;

        try {
            new File(logDirectoryName).mkdirs();
        } catch (Exception e) {
            throw new ProfileCloneException("Unable to create log dir " + profilePath, e);
        }

        FileSecurity fileSecurity = new FileSecurity(databaseDirectoryName);

        try {
            fileSecurity.saveSecretKey(settings.getContestPassword());
        } catch (Exception e) {
            throw new ProfileCloneException(e);
        }

        contest.setStorage(fileSecurity);

        // Save settings that created this new contest/profile so we can
        // send them to other servers/clients as needed.
        contest.setProfileCloneSettings(settings);
        
        if (contest.getAccounts(Type.SERVER) == null) {
            contest.addAccount(getAccount(getClientId()));
        }

        contest.setClientId(getClientId());
        contest.setSiteNumber(getClientId().getSiteNumber());
        
        if (contest.getAccounts(Type.ADMINISTRATOR) != null) {
            ClientId adminId = new ClientId(getClientId().getSiteNumber(), Type.ADMINISTRATOR, 1);
            Account account = getAccount(adminId);
            if (account != null) {
                contest.addAccount(account);
            } else {
                contest.generateNewAccounts(ClientType.Type.ADMINISTRATOR.toString(), 1, true);
            }
            // without this AJ throws an null pointer exception
            ClientSettings clientSettings = getClientSettings(adminId); 
            if (clientSettings != null) {
                contest.addClientSettings(clientSettings);
            }
        }

        for (Site site : getSites()) {
            contest.updateSite(site);
        }

        newProfile.setName(settings.getName());
        newProfile.setDescription(settings.getDescription());

        contest.setProfile(newProfile); // This also sets the contest identifier
        
        for (Profile profile2 : getProfiles()) {
            if (!profile2.equals(newProfile)) {
                contest.addProfile(profile2);
            }
        }

        contest.setContestPassword(new String(settings.getContestPassword()));
        
        if (settings.isResetContestTimes()){
            for (ContestTime contestTime : getContestTimes()) {
                ContestTime contestTime2 = clone(contestTime);
                if (settings.isResetContestTimes()) {
                    contestTime2.setElapsedMins(0);
                }
                contest.updateContestTime(contestTime2);
            }
        }

        if (settings.isCopyAccounts()) {

            if (settings.isCopyGroups()) {
                for (Group group : getGroups()) {
                    contest.addGroup(group);
                }
            }

            contest.addAccounts(getAccounts());
            for (Account account : getAccounts()) {
                ClientSettings clientSettings = getClientSettings(account.getClientId());
                if (clientSettings != null) {
                    contest.addClientSettings(clientSettings);
                }
            }

            if (! settings.isCopyGroups()) {
                for (Account account : contest.getAccounts()) {
                    account.setGroupId(null);
                }
            }
        }  else {
//             TODO Bug 70
//             Must create an admin account
             contest.generateNewAccounts(ClientType.Type.ADMINISTRATOR.toString(), 1, true);
         }

        if (settings.isCopyContestSettings()) {
            /**
             * Clone: ClientSettings BalloonSettings
             */
            cloneContestSettings(contest, settings);
        }

        if (settings.isCopyJudgements()) {
            contest.setJudgementList(getJudgements());
        }
        
        if (contest.getJudgements().length == 0){
            Judgement judgement = new Judgement("Yes");
            contest.addJudgement(judgement);
        }

        if (settings.isCopyLanguages()) {
            for (Language language : getLanguages()) {
                contest.addLanguage(language);
            }
        }

        if (settings.isCopyProblems()) {
            for (Problem problem : getProblems()) {
                ProblemDataFiles problemDataFiles = getProblemDataFile(problem);
                contest.addProblem(problem, problemDataFiles);
            }
            contest.setGeneralProblem(getGeneralProblem());
        } else {
            contest.setGeneralProblem(new Problem("General"));
        }

        if (settings.isCopyCategories()) {
            for (Category category : getCategories()) {
                contest.addCategory(category);
            }
        } else {
            contest.setupDefaultCategories();
        }
        if (settings.isCopyRuns()) {

            /**
             * Check if cloned problems and languages, if not throw exception.
             */

            if (settings.isCopyProblems() && settings.isCopyLanguages() && settings.isCopyAccounts()) {
                cloneRunsAndRunFiles(contest, newProfile);
            } else {
                if (!settings.isCopyAccounts()) {
                    throw new ProfileCloneException("Can not copy runs if accounts not copied too");
                } else if (!settings.isCopyProblems()) {
                    throw new ProfileCloneException("Can not copy runs if problems not copied too");
                } else { // must be no copy languages
                    throw new ProfileCloneException("Can not copy runs if languages not copied too");
                }
            }
        }

        if (settings.isCopyClarifications()) {
            if (settings.isCopyProblems() && settings.isCopyAccounts()) {

                cloneClarifications(contest, newProfile);
            } else {
                if (!settings.isCopyProblems()) {
                    throw new ProfileCloneException("Can not copy clarifications if problems not copied too");
                } else { // must be no copy accounts
                    throw new ProfileCloneException("Can not copy runs if accounts not copied too");
                }
            }
        }

        ContestInformation newContestInformation;
        if (settings.isCopyContestSettings()) {
            newContestInformation =  getContestInformation();
        } else {
            newContestInformation = new ContestInformation();
        }

        newContestInformation.setContestTitle(settings.getContestTitle());
        contest.updateContestInformation(newContestInformation);
        
        Log tempLog = new Log("profileClone");
        try {

            // Store new configuration -- SAVE IT.

            contest.storeConfiguration(tempLog);
            
        } catch (IOException e) {
            tempLog.log(Log.SEVERE, "Exception storing new config for " + newProfile.getName(), e);
            throw new ProfileCloneException("Exception storing new config for " + newProfile.getName(), e);
        } catch (ClassNotFoundException e) {
            tempLog.log(Log.SEVERE, "Exception storing new config for " + newProfile.getName(), e);
            throw new ProfileCloneException("Exception storing new config for " + newProfile.getName(), e);
        } catch (FileSecurityException e) {
            tempLog.log(Log.SEVERE, "Exception storing new config for " + newProfile.getName(), e);
            throw new ProfileCloneException("Exception storing new config for " + newProfile.getName(), e);
        }
        tempLog = null;

        /**
         * Updated profiles in .properties/text file.
         * 
         */
        try {
            ProfileManager profileManager = new ProfileManager();
            profileManager.mergeProfiles(this);
            profileManager.store(getProfiles(), getProfile());
        } catch (IOException e) {
            ProfileCloneException exception = new ProfileCloneException("Unable to store profiles in " + ProfileManager.PROFILE_INDEX_FILENAME);
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        } catch (ProfileLoadException e) {
            ProfileCloneException exception = new ProfileCloneException("Unable to merge profiles");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

        return contest;
    }


    private ContestTime clone(ContestTime contestTime) {
        ContestTime time = new ContestTime(contestTime.getSiteNumber());
        
        time.setContestLengthSecs(contestTime.getContestLengthSecs());
        time.setElapsedSecs(time.getElapsedSecs());
        time.setHaltContestAtTimeZero(contestTime.isHaltContestAtTimeZero());
        
        return time;    
    }

    public Account[] getAccounts() {
        return accountList.getList();
    }

    private void cloneContestSettings(IInternalContest contest, ProfileCloneSettings settings) {

        for (ClientSettings clientSettings : contest.getClientSettingsList()) {
            if (settings.isCopyProblems()){
                contest.addClientSettings(clientSettings);
                // FIXME set auto judge filter 
                // FIXME set balloon list
            } else { // clear client problem settings
                clientSettings.setBalloonList(new Hashtable<String, BalloonDeliveryInfo>());
                clientSettings.setAutoJudgeFilter(new Filter());
            }
        }
        
        for (BalloonSettings balloonSettings : getBalloonSettings()){
            contest.addBalloonSettings(balloonSettings);
        }
        
        // FIXME ClientSettings: properties
        // FIXME ClientSettings: notificationSetting
        // FIXME ClientSettings: balloonList

    }

    public ProfileCloneSettings getProfileCloneSettings() {
        return profileCloneSettings;
    }

    public ProfileCloneSettings setProfileCloneSettings(ProfileCloneSettings inProfileCloneSettings) {
        this.profileCloneSettings = inProfileCloneSettings;
        return profileCloneSettings;
    }

    public void cloneClarifications(IInternalContest targetContest, Profile newProfile) throws ProfileCloneException {
        clarificationList.clone(targetContest.getStorage());
    }

    public void cloneRunsAndRunFiles(IInternalContest targetContest, Profile newProfile) throws ProfileCloneException {

        runList.clone(targetContest.getStorage());

        runFilesList.clone(targetContest.getStorage());

        runResultFilesList.clone(targetContest.getStorage());
    }
    
    private void logException(String message, Exception e) {

        if (StaticLog.getLog() != null) {
            StaticLog.getLog().log(Log.WARNING, message, e);
        } else {
            System.err.println(message);
            e.printStackTrace(System.err);
        }
    }
    
    private void logException(Exception e) {

        if (StaticLog.getLog() != null) {
            StaticLog.getLog().log(Log.WARNING, "Exception", e);
        } else {
            e.printStackTrace(System.err);
        }
    }
    
    private void logMessage(java.util.logging.Level level, String message) {
        if (StaticLog.getLog() != null) {
            StaticLog.getLog().log(level, message);
        } else {
            System.err.println(message);
        }
        
    }

    public void removeAllListeners() {
        accountListenerList.removeAllElements();
        balloonSettingsListenerList.removeAllElements();
        categoryListenerList.removeAllElements();
        changePasswordListenerList.removeAllElements();
        clarificationListenerList.removeAllElements();
        clientSettingsListenerList.removeAllElements();
        connectionListenerList.removeAllElements();
        contestInformationListenerList.removeAllElements();
        contestTimeListenerList.removeAllElements();
        groupListenerList.removeAllElements();
        judgementListenerList.removeAllElements();
        languageListenerList.removeAllElements();
        loginListenerList.removeAllElements();
        problemListenerList.removeAllElements();
        profileListenerList.removeAllElements();
        runListenerList.removeAllElements();
        siteListenerList.removeAllElements();
        messageListenerList.removeAllElements();
    }

    public void fireAllRefreshEvents() {

        // FIXME double check that all Events are being refreshed
        
        ProfileEvent profileEvent = new ProfileEvent(ProfileEvent.Action.REFRESH_ALL, null);
        fireProfileListener(profileEvent);
        
        RunEvent runEvent = new RunEvent(RunEvent.Action.REFRESH_ALL, null, null, null);
        fireRunListener(runEvent);

        ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.REFRESH_ALL, getContestTime(), getSiteNumber());
        fireContestTimeListener(contestTimeEvent);

        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.REFRESH_ALL, null);
        fireProblemListener(problemEvent);

        LanguageEvent languageEvent = new LanguageEvent(LanguageEvent.Action.REFRESH_ALL, getLanguages());
        fireLanguageListener(languageEvent);

        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.REFRESH_ALL, null, null);
        fireLoginListener(loginEvent);

        JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.REFRESH_ALL, null);
        fireJudgementListener(judgementEvent);

        SiteEvent siteEvent = new SiteEvent(SiteEvent.Action.REFRESH_ALL, null);
        fireSiteListener(siteEvent);

        ConnectionEvent connectionEvent = new ConnectionEvent(ConnectionEvent.Action.REFRESH_ALL, null);
        fireConnectionListener(connectionEvent);

        BalloonSettingsEvent balloonSettingsEvent = new BalloonSettingsEvent(BalloonSettingsEvent.Action.REFRESH_ALL, null);
        fireBalloonSettingsListener(balloonSettingsEvent);

        AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.REFRESH_ALL, getAccounts());
        fireAccountListener(accountEvent);

        // Keep firePasswordChangeListener for reference, unused, unneeded.
//        PasswordChangeEvent passwordChangeEvent = null;
//        firePasswordChangeListener(passwordChangeEvent);

        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.REFRESH_ALL, null);
        fireClarificationListener(clarificationEvent);

        ClientSettingsEvent clientSettingsEvent = new ClientSettingsEvent(ClientSettingsEvent.Action.REFRESH_ALL, null, null);
        fireClientSettingsListener(clientSettingsEvent);

        ContestInformationEvent contestInformationEvent = new ContestInformationEvent (ContestInformationEvent.Action.REFRESH_ALL, null);
        fireContestInformationListener(contestInformationEvent);

        GroupEvent groupEvent = new GroupEvent(GroupEvent.Action.REFRESH_ALL, getGroups());
        fireGroupListener(groupEvent);

        fireCategoryListener(null, CategoryEvent.Action.REFRESH_ALL);

        // there is no REFRESH_ALL for MessageEvent
//        MessageEvent messageEvent = new MessageEvent(MessageEvent.Action.REFRESH_ALL);
//        fireMessageListener(messageEvent);

    }

    /**
     * Copies all the current login and connectionHandler data, for both local and remote logins and connectionHandlers,
     * from the this contest to the specified newContest.
     * 
     *  @param newContest the contest into which the login and connection information will be copied.
     */
    public void cloneAllLoginAndConnections(IInternalContest newContest) throws CloneException {
        
        CloneException ex = null;
        
        /**
         * Local Logins
         */

        for (ClientId clientId : localLoginList.getClientIdList()) {
            try {
                //get a list of (one or possibly more) ConnectionHandlerIDs associated with the current locally logged-in client in the current contest
                List<ConnectionHandlerID> connectionList = Collections.list(localLoginList.getConnectionHandlerIDs(clientId));
                
                //add each of the ConnectionHandlerIDs for the current local client to the list of local connections for the specified client in the new contest
                for (ConnectionHandlerID connHID : connectionList) {
                    newContest.addLocalLogin(clientId, connHID);   
                }
            } catch (Exception e) {
                ex = new CloneException(e.getMessage(), e.getCause());
            }
        }

        /**
         * Remote Logins
         */

        for (ClientId clientId : remoteLoginList.getClientIdList()) {
            try {
                
                //get a list of (one or possibly more) ConnectionHandlerIDs associated with the current remotely-logged in client in the current contest
                List<ConnectionHandlerID> connectionList = Collections.list(remoteLoginList.getConnectionHandlerIDs(clientId));
                
                //add each of the ConnectionHandlerIDs for the current remote client to the list of remote connections for the specified client in the new contest
                for (ConnectionHandlerID connHID : connectionList) {
                    newContest.addRemoteLogin(clientId, connHID);
                }
            } catch (Exception e) {
                ex = new CloneException(e.getMessage(), e.getCause());
            }
        }

        /**
         * Local connection handlers
         */

        for (ConnectionHandlerID connectionHandlerID : localConnectionHandlerList.getList()) {
            try {
                newContest.connectionEstablished(connectionHandlerID, localConnectionHandlerList.get(connectionHandlerID));
            } catch (Exception e) {
                ex = new CloneException(e.getMessage(), e.getCause());
            }
        }

        /**
         * Connections
         */

        for (ConnectionHandlerID connectionHandlerID : remoteConnectionHandlerList.getList()) {
            try {
                newContest.connectionEstablished(connectionHandlerID, remoteConnectionHandlerList.get(connectionHandlerID));
            } catch (Exception e) {
                ex = new CloneException(e.getMessage(), e.getCause());
            }
        }
        
        if (ex != null){
            throw ex;
        }
        
    }

    public void addMessage(MessageEvent.Area area, ClientId source, ClientId destination, String message) {
        MessageEvent messageEvent = new MessageEvent(MessageEvent.Action.ADDED, area, message, source, destination);
        fireMessageListener(messageEvent);
    }

    public void updateRunFiles(Run run, RunFiles runFiles) throws IOException, ClassNotFoundException, FileSecurityException {
        runFilesList.add(run, runFiles);
    }

    public FinalizeData getFinalizeData() {
        return finalizeData;
    }

    public void setFinalizeData(FinalizeData data) {
        finalizeData = data;
        ContestInformationEvent event = new ContestInformationEvent(contestInformation, data);
        fireContestInformationListener(event);
    }

    public Notification[] getNotifications() {
        return notificationList.getList();
    }

    public void addNotificationListener(INotificationListener notificationListener) {
        notificationListenerList.add(notificationListener);
    }

    public void removeNotificationListener(INotificationListener notificationListener) {
        notificationListenerList.remove(notificationListener);
    }

    public Notification getNotification(ElementId elementId) {
        return notificationList.get(elementId);
    }

    public void updateNotification(Notification notification) throws IOException, ClassNotFoundException, FileSecurityException {
        notificationList.updateNotification(notification);
    }

    public Notification getNotification(ClientId submitter, ElementId problemId) {
        return notificationList.get(submitter, problemId);
    }

    public Notification acceptNotification(Notification notification) {

        notification.setElapsedMS(getContestTime().getElapsedMS());
        notification.setSiteNumber(getSiteNumber());
        
        try {
            Notification newNotif = notificationList.addNewNotification(notification);
            addNotification(newNotif);
            return newNotif;
        } catch (IOException e) {
            logException(e);
        } catch (ClassNotFoundException e) {
            logException(e);
        } catch (FileSecurityException e) {
            logException(e);
        }

        return null;

    }

    public void addReplaySetting(ReplaySetting replaySetting) {
        replaySettingTemp = replaySetting;
        fireReplaySettingListener(ReplaySettingEvent.Action.ADDED, replaySetting);
    }

    public void deleteReplaySetting(ReplaySetting replaySetting) {
        fireReplaySettingListener(ReplaySettingEvent.Action.DELETED, replaySetting);
        replaySettingTemp = null;
    }

    public void updateReplaySetting(ReplaySetting replaySetting) {
        replaySettingTemp = replaySetting;
        fireReplaySettingListener(ReplaySettingEvent.Action.CHANGED, replaySetting);
    }
    
    private void fireReplaySettingListener(ReplaySettingEvent.Action action, ReplaySetting replaySetting) {  
        
        // TODO 670 add listener for ReplaySetting
        
//        ReplaySettingEvent event = new ReplaySettingEvent(action, replaySetting);
           
    }

    public ReplaySetting[] getReplaySettings() {
        
        // TODO 670 add list for ReplaySetting
        
        if (replaySettingTemp == null) {
            // TODO 6670 remove this debugging code
            replaySettingTemp = new ReplaySetting("Sample Replay Title");
//            replaySettingTemp.setAutoStart(true);
//            replaySettingTemp.setIterationCount(42);
//            replaySettingTemp.setSiteNumber(2);
//            replaySettingTemp.setStartSequenceNumber(24);
//            replaySettingTemp.setLoadFileName("c:\\tmp\\site1.replay.txt");
        }
        
        if (replaySettingTemp == null) {
            return new ReplaySetting[0];
        } else {
            ReplaySetting [] list = new ReplaySetting[1];
            list[0] = replaySettingTemp;
            return list;
        }
    }

    public void addPlaybackInfo(PlaybackInfo playbackInfo) {
        playbackInfoList.add(playbackInfo);
        firePlaybackInfosListener(PlayBackEvent.Action.ADDED, playbackInfo);
    }

    public void deletePlaybackInfo(PlaybackInfo playbackInfo) {
        playbackInfoList.delete(playbackInfo);
        firePlaybackInfosListener(PlayBackEvent.Action.DELETE, playbackInfo);
    }

    public void updatePlaybackInfo(PlaybackInfo playbackInfo) {
        playbackInfoList.update(playbackInfo);
        firePlaybackInfosListener(PlayBackEvent.Action.REFRESH_ALL, playbackInfo);
    }
    
    public void resetPlaybackInfo(PlaybackInfo playbackInfo) {
        playbackInfo.rewind();
        firePlaybackInfosListener(PlayBackEvent.Action.RESET_REPLAY, playbackInfo);
    }

    public void stopReplayPlaybackInfo(PlaybackInfo playbackInfo) {
        playbackInfoList.delete(playbackInfo);
        firePlaybackInfosListener(PlayBackEvent.Action.STOP_REPLAY, playbackInfo);
    }
    
    public void startReplayPlaybackInfo(PlaybackInfo playbackInfo) {
        playbackInfoList.delete(playbackInfo);
        firePlaybackInfosListener(PlayBackEvent.Action.START_REPLAY, playbackInfo);
    }

    public PlaybackInfo[] getPlaybackInfos() {
        return playbackInfoList.getList();
    }

    public PlaybackInfo getPlaybackInfo(ElementId elementId) {
        return (PlaybackInfo) playbackInfoList.get(elementId);
    }

    public PlaybackManager getPlaybackManager() {
        return playbackManager;
    }

    public void setPlaybackManager(PlaybackManager playbackManager) {
        this.playbackManager = playbackManager;
    }

    public Account autoRegisterTeam(String displayName, String[] memberNames, String password) {
        Account account = accountList.assignNewTeam (siteNumber, displayName, memberNames, password);
        addAccount(account);
        return account;
    }

    public EventFeedDefinition[] getEventFeedDefinitions() {
        return eventFeedDefinitionsList.getList();
    }

    public void addEventFeedDefinition(EventFeedDefinition eventFeedDefinition) {

        eventFeedDefinitionsList.add(eventFeedDefinition);
        EventFeedDefinitionEvent eventFeedDefinitionEvent = new EventFeedDefinitionEvent(EventFeedDefinitionEvent.Action.ADDED, eventFeedDefinition);
        fireEventFeedDefinitionListener(eventFeedDefinitionEvent);
    }

    public void deleteEventFeedDefinition(EventFeedDefinition eventFeedDefinition) {
        eventFeedDefinitionsList.delete(eventFeedDefinition);
        EventFeedDefinitionEvent eventFeedDefinitionEvent = new EventFeedDefinitionEvent(EventFeedDefinitionEvent.Action.DELETED, eventFeedDefinition);
        fireEventFeedDefinitionListener(eventFeedDefinitionEvent);
    }

    public void updateEventFeedDefinition(EventFeedDefinition eventFeedDefinition) {
        eventFeedDefinitionsList.update(eventFeedDefinition);
        EventFeedDefinitionEvent problemEvent = new EventFeedDefinitionEvent(EventFeedDefinitionEvent.Action.CHANGED, eventFeedDefinition);
        fireEventFeedDefinitionListener(problemEvent);
    }

    public EventFeedDefinition getEventFeedDefinition(ElementId elementId) {
        return (EventFeedDefinition) eventFeedDefinitionsList.get(elementId);
    }

    @Override
    public void addLanguages(Language[] languages) {
        for (Language language : languages) {
            languageDisplayList.add(language);
            languageList.add(language);
        }        
        LanguageEvent languageEvent = new LanguageEvent(LanguageEvent.Action.ADDED_LANGUAGES, languages);
        fireLanguageListener(languageEvent);
    }

    @Override
    public void updateLanguages(Language[] languages) {
        for (Language language : languages) {
            languageList.update(language);
            languageDisplayList.update(language);
        }
        LanguageEvent languageEvent = new LanguageEvent(LanguageEvent.Action.CHANGED_LANGUAGES, languages);
        fireLanguageListener(languageEvent);
    }

    @Override
    public void addGroups(Group[] groups) {
        for (Group group : groups) {
            groupDisplayList.add(group);
            groupList.add(group);
        }
        GroupEvent groupEvent = new GroupEvent(GroupEvent.Action.ADDED_GROUPS, groups);
        fireGroupListener(groupEvent);
    }

    @Override
    public void updateGroups(Group[] groups) {
        for (Group group : groups) {
            groupList.update(group);
            groupDisplayList.update(group);
        }
        GroupEvent groupEvent = new GroupEvent(GroupEvent.Action.CHANGED_GROUPS, groups);
        fireGroupListener(groupEvent);
    }
    
    @Override
    public boolean isCommandLineOptionPresent(String optionName) {
        return parseArguments.isOptPresent(optionName);
    }
    
    @Override
    public void setCommandLineArguments(ParseArguments aParseArguments) {
        this.parseArguments = aParseArguments;
    }

    @Override
    public boolean doesCommandLineArgumentHaveParameter(String optionName) {
        return parseArguments.optHasValue(optionName);
    }

    @Override
    public String getCommandLineOptionValue(String optionNaeme) {
        return parseArguments.getOptValue(optionNaeme);
    }

    @Override
    public void addAvailableAJ(ConnectionHandlerID connectionHandlerID, ClientId fromId) {
        throw new UnsupportedOperationException("Method InternalContest.addAvailableAJ(ConnHandler,ClientId) not implemented yet.");
        
    }
}
