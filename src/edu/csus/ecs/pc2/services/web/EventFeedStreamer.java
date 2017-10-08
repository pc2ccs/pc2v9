package edu.csus.ecs.pc2.services.web;

import java.io.OutputStream;
import java.util.Arrays;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.GroupEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IGroupListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IJudgementListener;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementEvent;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.services.core.ClarificationJSON;
import edu.csus.ecs.pc2.services.core.ContestJSON;
import edu.csus.ecs.pc2.services.core.EventFeedJSON;
import edu.csus.ecs.pc2.services.core.EventFeedOperation;
import edu.csus.ecs.pc2.services.core.GroupJSON;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.core.JudgementJSON;
import edu.csus.ecs.pc2.services.core.JudgementTypeJSON;
import edu.csus.ecs.pc2.services.core.LanguageJSON;
import edu.csus.ecs.pc2.services.core.OrganizationJSON;
import edu.csus.ecs.pc2.services.core.ProblemJSON;
import edu.csus.ecs.pc2.services.core.RunJSON;
import edu.csus.ecs.pc2.services.core.SubmissionJSON;
import edu.csus.ecs.pc2.services.core.TeamJSON;
import edu.csus.ecs.pc2.services.core.TeamMemberJSON;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Event Feed Server/Streamer.
 * 
 * Constructs and sends event feed JSON to client via a stream.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedStreamer extends JSONUtilities implements UIPlugin {

    private static final long serialVersionUID = 2076470194640278897L;

    private OutputStream os;

    private Log log;

    private IInternalContest contest;
    
    // Listeners

    private AccountListener accountListener = null;

    private RunListener runListener = null;

    private ClarificationListener clarificationListener = null;

    private ProblemListener problemListener = null;

    private LanguageListener languageListener = null;

    private GroupListener groupListener = null;

    private JudgementListener judgementListener = null;

    private ContestInformationListener contestInformationListener = null;

    private ContestTimeListener contestTimeListener = null;
    
    private EventFeedJSON eventFeedJSON = new EventFeedJSON();
    
    private JudgementTypeJSON judgementTypeJSON = new JudgementTypeJSON();

    private LanguageJSON languageJSON = new LanguageJSON();

//    private AwardJSON awardJSON = new AwardJSON();

    private ClarificationJSON clarificationJSON = new ClarificationJSON();

    private ContestJSON contestJSON = new ContestJSON();

    private GroupJSON groupJSON = new GroupJSON();

    private JudgementJSON judgementJSON = new JudgementJSON();

    private OrganizationJSON organizationJSON = new OrganizationJSON();

    private ProblemJSON problemJSON = new ProblemJSON();

    private RunJSON runJSON = new RunJSON();

    private SubmissionJSON submissionJSON = new SubmissionJSON();

    private TeamJSON teamJSON = new TeamJSON();

    private TeamMemberJSON teamMemberJSON = new TeamMemberJSON();
    
    private EventFeedLog eventFeedLog;

    public EventFeedStreamer(OutputStream outputStream, IInternalContest inContest, IInternalController inController) {
        this.os = outputStream;
        this.contest = inContest;
        this.log = inController.getLog();
        registerListeners(contest);
        
        try {
            eventFeedLog = new EventFeedLog(contest);
            sendEventsFromEventFeedLog();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
     
    }

    /**
     * Send all events from log to consumer.
     * @param 
     */
    private void sendEventsFromEventFeedLog() {
        
        /**
         * Number of lines/events in log.
         */
        String[] lines = eventFeedLog.getLogLines();
        
        try {
            if (lines.length > 0){
                eventFeedJSON.setEventIdSequence(lines.length);
                for (String line : lines) {
                    sendJSON(line);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.log = inController.getLog();
        this.contest = inContest;
    }

    @Override
    public String getPluginTitle() {
        return "Event Feed Stream Runnable";
    }

    /**
     * Sleep for a number of seconds.
     * @param secs
     */
    public void sleep(int secs) {

        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void registerListeners(IInternalContest inContest) {

        inContest.addAccountListener(accountListener);
        inContest.addRunListener(runListener);
        inContest.addClarificationListener(clarificationListener);
        inContest.addProblemListener(problemListener);
        inContest.addLanguageListener(languageListener);
        inContest.addGroupListener(groupListener);
        inContest.addJudgementListener(judgementListener);
        inContest.addContestInformationListener(contestInformationListener);
        inContest.addContestTimeListener(contestTimeListener);

        // TODO CLICS code CCS ensure that commented out listeners are not needed.
        // inContest.addMessageListener(new MessageListener());
        // inContest.addSiteListener(new SiteListener());
        // inContest.addConnectionListener(new ConnectionListener());
        // inContest.addChangePasswordListener(new ChangePasswordListener());
        // inContest.addLoginListener(new LoginListener());
        // inContest.addProfileListener(new ProfileListener());
        // inContest.addClientSettingsListener(new ClientSettingsListener());
        // inContest.addBalloonSettingsListener(new BalloonSettingsListener());
        // inContest.addSecurityMessageListener(new SecurityMessageListener());
    }

    /**
     * Account Listener for EventFeeder.
     * @author pc2@ecs.csus.edu 
     */

    protected class AccountListener implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            Account account = accountEvent.getAccount();
            if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                sendJSON(eventFeedJSON.getTeamJSON(contest, account));
            }
        }

        private boolean isTeam(Account account) {
            return account.getClientId().getClientType().equals(ClientType.Type.TEAM);
        }

        public void accountModified(AccountEvent accountEvent) {
            Account account = accountEvent.getAccount();
            if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, account));
            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {
                if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, account));
                }
            }
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            Arrays.sort(accounts, new AccountComparator());
            for (Account account : accounts) {
                if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, account));
                }
            }
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            // ignore
        }
    }

    /**
     * Run Listener for EventFeeder.
     * @author pc2@ecs.csus.edu 
     */
    protected class RunListener implements IRunListener {

        public void runAdded(RunEvent event) {
            Run run = event.getRun();
            Account account = contest.getAccount(run.getSubmitter());
            if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) && !run.isDeleted()) {
                // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, run));
            }
            // NOTE for a run auto marked as DEL there will be no 
            // XML until the judging Completed.
        }

        public void runChanged(RunEvent event) {
            Run run = event.getRun();
            RunStates status = run.getStatus();
            Account account = contest.getAccount(run.getSubmitter());
            // skip emitting xml for runs that are not completed or for an account that should not be shown
            if (!account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) || !status.equals(RunStates.JUDGED)) {
                return;
            }
            // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, run));
        }

        public void runRemoved(RunEvent event) {
            runChanged(event);
        }

        public void refreshRuns(RunEvent event) {
            // ignore
        }
    }

    /**
     * Clarification Listener for EventFeeder.
     * @author pc2@ecs.csus.edu 
     */

    protected class ClarificationListener implements IClarificationListener {

        public void clarificationAdded(ClarificationEvent event) {
//            Clarification clarification = event.getClarification();
            // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, clarification));
        }

        public void clarificationChanged(ClarificationEvent event) {
//            Clarification clarification = event.getClarification();
            // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, clarification));
        }

        public void clarificationRemoved(ClarificationEvent event) {
            // ignore
        }

        public void refreshClarfications(ClarificationEvent event) {
            // ignore
        }
    }

    /**
     * Problem Listener for EventFeeder.
     * @author pc2@ecs.csus.edu 
     */

    protected class ProblemListener implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
//            Problem problem = event.getProblem();
//            int problemNumber = getProblemNumber(problem);
            // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, problem, problemNumber));
        }

        public void problemChanged(ProblemEvent event) {
//            Problem problem = event.getProblem();
//            int problemNumber = getProblemNumber(problem);
            // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, problem, problemNumber));
        }

        public void problemRemoved(ProblemEvent event) {
            // ignore
        }

        public void problemRefreshAll(ProblemEvent event) {
            // ignore
        }
    }

    /**
     * Language Listener for EventFeeder.
     * @author pc2@ecs.csus.edu 
     */

    protected class LanguageListener implements ILanguageListener {

        public void languageAdded(LanguageEvent event) {
//            Language language = event.getLanguage();
//            int languageNumber = getLanguageNumber(language);
            // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, language, languageNumber));
        }

        public void languageChanged(LanguageEvent event) {
//            Language language = event.getLanguage();
//            int languageNumber = getLanguageNumber(language);
            // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, language, languageNumber));
        }

        public void languageRemoved(LanguageEvent event) {
            // ignore
        }

        public void languageRefreshAll(LanguageEvent event) {
            // ignore
        }

        @Override
        public void languagesAdded(LanguageEvent event) {
//            Language[] languages = event.getLanguages();
//            for (Language language : languages) {
////                int languageNumber = getLanguageNumber(language);
//                // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, language, languageNumber));
//            }
        }

        @Override
        public void languagesChanged(LanguageEvent event) {
//            Language[] languages = event.getLanguages();
//            for (Language language : languages) {
////                int languageNumber = getLanguageNumber(language);
//                // TODO CLICS code sendJSON(eventFeedXML.createElement(contest, language, languageNumber));
//            }
        }
    }

    /**
     * Group/Region Listener for EventFeeder.
     * @author pc2@ecs.csus.edu 
     */

    protected class GroupListener implements IGroupListener {

        public void groupAdded(GroupEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Group group = event.getGroup();
                String json = getJSONEvent(GROUPS_KEY, getEventId(), EventFeedOperation.CREATE, groupJSON.createJSON(contest, group));
                sendJSON(json + NL);
            }

        }

        public void groupChanged(GroupEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Group group = event.getGroup();
                String json = getJSONEvent(GROUPS_KEY, getEventId(), EventFeedOperation.UPDATE, groupJSON.createJSON(contest, group));
                sendJSON(json + NL);
            }
        }

        public void groupRemoved(GroupEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Group group = event.getGroup();
                String json = getJSONEvent(GROUPS_KEY, getEventId(), EventFeedOperation.DELETE, groupJSON.createJSON(contest, group));
                sendJSON(json + NL);
            }
        }

        public void groupsAdded(GroupEvent groupEvent) {
            Group[] groups = groupEvent.getGroups();
            for (Group group : groups) {
                if (eventFeedJSON.isPastStartEvent()) {
                    String json = getJSONEvent(GROUPS_KEY, getEventId(), EventFeedOperation.CREATE, groupJSON.createJSON(contest, group));
                    sendJSON(json + NL);
                }
            }
        }

        public void groupsChanged(GroupEvent groupEvent) {
            Group[] groups = groupEvent.getGroups();
            for (Group group : groups) {
                if (eventFeedJSON.isPastStartEvent()) {
                    String json = getJSONEvent(GROUPS_KEY, getEventId(), EventFeedOperation.UPDATE, groupJSON.createJSON(contest, group));
                    sendJSON(json + NL);
                }
            }
        }

        public void groupRefreshAll(GroupEvent groupEvent) {
            // ignore
        }
    }

    /**
     * Judgement listener 
     * @author pc2@ecs.csus.edu 
     */

    protected class JudgementListener implements IJudgementListener {

        public void judgementAdded(JudgementEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Judgement judgement = event.getJudgement();
                String json = getJSONEvent(JUDGEMENT_TYPE_KEY, getEventId(), EventFeedOperation.CREATE, judgementTypeJSON.createJSON(contest, judgement));
                sendJSON(json + NL);
            }
        }

        public void judgementChanged(JudgementEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Judgement judgement = event.getJudgement();
                String json = getJSONEvent(JUDGEMENT_TYPE_KEY, getEventId(), EventFeedOperation.UPDATE, judgementTypeJSON.createJSON(contest, judgement));
                sendJSON(json + NL);
            }
        }

        public void judgementRemoved(JudgementEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Judgement judgement = event.getJudgement();
                String json = getJSONEvent(JUDGEMENT_TYPE_KEY, getEventId(), EventFeedOperation.UPDATE, judgementTypeJSON.createJSON(contest, judgement));
                sendJSON(json + NL);
            }
        }

        public void judgementRefreshAll(JudgementEvent judgementEvent) {
            // ignore
        }
    }

    /**
     * Listener.
     * 
     * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
     */
    protected class ContestTimeListener implements IContestTimeListener {

        @Override
        public void contestTimeAdded(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        @Override
        public void contestTimeRemoved(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        @Override
        public void contestTimeChanged(ContestTimeEvent event) {
//            ContestInformation info = contest.getContestInformation();
            // TODO CLICS code sendJSON(eventFeedXML.createInfoElement(contest, info));
        }

        @Override
        public void contestStarted(ContestTimeEvent event) {
            contestTimeChanged(event);

        }

        @Override
        public void contestStopped(ContestTimeEvent event) {
            contestTimeChanged(event);

        }

        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestTimeChanged(event);

        }

        @Override
        public void refreshAll(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    protected class ContestInformationListener implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
//            ContestInformation info = event.getContestInformation();
            // TODO CLICS code sendJSON(eventFeedXML.createInfoElement(contest, info));
        }

        public void contestInformationChanged(ContestInformationEvent event) {
//            ContestInformation info = event.getContestInformation();
            // TODO CLICS code sendJSON(eventFeedXML.createInfoElement(contest, info));
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // ignored
        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            // SOMEDAY refresh all
        }

        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
//            FinalizeData data = contestInformationEvent.getFinalizeData();
            // TODO CLICS code            eventFeedRunnable.send(eventFeedXML.createFinalizeXML(contest, data));
        }
    }

    /**
     * Send JSON to stream.
     * 
     * @param teamJSON
     */
    public void sendJSON(String string) {
        
        try {
            os.write(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            // TODO: handle exception
        }
        
        try {
            eventFeedLog.writeEvent(string);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        } 
        
    }

    public long getEventId() {
        return eventFeedJSON.getEventIdSequence();
    }

    /**
     * Write startup events.
     */
    public void writeStartupEvents() {
        
        try {
            String json = eventFeedJSON.createJSON(contest);
            os.write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
