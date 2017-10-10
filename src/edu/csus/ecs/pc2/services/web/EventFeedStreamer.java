package edu.csus.ecs.pc2.services.web;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.Clarification;
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
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.Run;
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
import edu.csus.ecs.pc2.services.core.ProblemJSON;
import edu.csus.ecs.pc2.services.core.SubmissionJSON;
import edu.csus.ecs.pc2.services.core.TeamJSON;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Event Feed Server/Streamer.
 * 
 * Constructs and sends event feed JSON to client via a stream.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedStreamer extends JSONUtilities implements Runnable, UIPlugin {

    private static final long serialVersionUID = 2076470194640278897L;

    /**
     * Number of ms be sending out keep alive new line.
     */
    private static final long KEEP_ALIVE_DELAY = 120 * Constants.MS_PER_SECOND;

    private OutputStream os;

    private Log log;

    private IInternalContest contest;
    
    // Listeners

    private AccountListener accountListener = new AccountListener();

    private RunListener runListener = new RunListener();

    private ClarificationListener clarificationListener = new ClarificationListener();

    private ProblemListener problemListener = new ProblemListener();

    private LanguageListener languageListener = new LanguageListener();

    private GroupListener groupListener = new GroupListener();

    private JudgementListener judgementListener = new JudgementListener();

    private ContestInformationListener contestInformationListener = new ContestInformationListener();

    private ContestTimeListener contestTimeListener = new ContestTimeListener();
    
    private EventFeedJSON eventFeedJSON = new EventFeedJSON();
    
    private JudgementTypeJSON judgementTypeJSON = new JudgementTypeJSON();

    private LanguageJSON languageJSON = new LanguageJSON();

//    private AwardJSON awardJSON = new AwardJSON();

    private ClarificationJSON clarificationJSON = new ClarificationJSON();

    private ContestJSON contestJSON = new ContestJSON();

    private GroupJSON groupJSON = new GroupJSON();

// TODO     private OrganizationJSON organizationJSON = new OrganizationJSON();

    private ProblemJSON problemJSON = new ProblemJSON();
    
    /**
     * Run Judgement JSON.
     */
    private JudgementJSON judgementJSON = new JudgementJSON();

    /**
     * Test Case JSON
     */
//  private RunJSON runJSON = new RunJSON(); // TODO add test case JSON

    /**
     * New Run JSON
     */
    private SubmissionJSON submissionJSON = new SubmissionJSON();

    private TeamJSON teamJSON = new TeamJSON();

//    private TeamMemberJSON teamMemberJSON = new TeamMemberJSON(); TODO add team numbers JSON
    
    private EventFeedLog eventFeedLog;

    /**
     * Last time4 event sent to stream.
     */
    private long lastSent;

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
            log.log(Log.WARNING, "Problem initializing event feed log", e);
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
            log.log(Log.WARNING, "Problem sending JSON from event feed log", e);

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

        // SOMEDAY CLICS code CCS ensure that commented out listeners are not needed.
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
            if (eventFeedJSON.isPastStartEvent()) {
                Account account = accountEvent.getAccount();
                if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    String json = getJSONEvent(TEAM_KEY, getEventId(), EventFeedOperation.CREATE, teamJSON.createJSON(contest, account));
                    sendJSON(json + NL);
                    
                    // TODO send team members info
                    
                }
            }
        }

    

        public void accountModified(AccountEvent accountEvent) {

            if (eventFeedJSON.isPastStartEvent()) {
                Account account = accountEvent.getAccount();
                if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    String json = getJSONEvent(TEAM_KEY, getEventId(), EventFeedOperation.UPDATE, teamJSON.createJSON(contest, account));
                    sendJSON(json + NL);
                    
                    // TODO send team members info
                }
            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {
                if (eventFeedJSON.isPastStartEvent()) {
                    if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                        String json = getJSONEvent(TEAM_KEY, getEventId(), EventFeedOperation.CREATE, teamJSON.createJSON(contest, account));
                        sendJSON(json + NL);
                    }
                }
            }
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            Arrays.sort(accounts, new AccountComparator());
            for (Account account : accounts) {
                if (eventFeedJSON.isPastStartEvent()) {
                    if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                        String json = getJSONEvent(TEAM_KEY, getEventId(), EventFeedOperation.UPDATE, teamJSON.createJSON(contest, account));
                        sendJSON(json + NL);
                    }
                }
            }
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            // ignore
        }
        
        private boolean isTeam(Account account) {
            return account.getClientId().getClientType().equals(ClientType.Type.TEAM);
        }
    }

    /**
     * Run Listener for EventFeeder.
     * @author pc2@ecs.csus.edu 
     */
    protected class RunListener implements IRunListener {

        public void runAdded(RunEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Run run = event.getRun();
                Account account = contest.getAccount(run.getSubmitter());
                if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) && !run.isDeleted()) {
                    
                    String json = getJSONEvent(SUBMISSION_KEY, getEventId(), EventFeedOperation.CREATE, submissionJSON.createJSON(contest, run));
                    sendJSON(json + NL);
                }
            }
        }

        public void runChanged(RunEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Run run = event.getRun();
                Account account = contest.getAccount(run.getSubmitter());
                if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) && !run.isDeleted()) {
                    
                    if (run.isJudged()){
                        String json = getJSONEvent(JUDGEMENT_KEY, getEventId(), EventFeedOperation.UPDATE, judgementJSON.createJSON(contest, run));
                        sendJSON(json + NL);
                    } else {
                        String json = getJSONEvent(SUBMISSION_KEY, getEventId(), EventFeedOperation.UPDATE, submissionJSON.createJSON(contest, run));
                        sendJSON(json + NL);
                    }
                }
            }
        }

        public void runRemoved(RunEvent event) {
            
            if (eventFeedJSON.isPastStartEvent()) {
                Run run = event.getRun();
                Account account = contest.getAccount(run.getSubmitter());
                if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    
                    String json = getJSONEvent(JUDGEMENT_KEY, getEventId(), EventFeedOperation.DELETE, judgementJSON.createJSON(contest, run));
                    sendJSON(json + NL);
                }
            }
        }

        public void refreshRuns(RunEvent event) {
            // ignore
        }
    }

    /**
     * Clarification Listener.
     * 
     * @author pc2@ecs.csus.edu 
     */
    protected class ClarificationListener implements IClarificationListener {

        public void clarificationAdded(ClarificationEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Clarification clarification = event.getClarification();
                String json = getJSONEvent(CLARIFICATIONS_KEY, getEventId(), EventFeedOperation.CREATE, clarificationJSON.createJSON(contest, clarification));
                sendJSON(json + NL);
            }
        }

        public void clarificationChanged(ClarificationEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Clarification clarification = event.getClarification();
                String json = getJSONEvent(CLARIFICATIONS_KEY, getEventId(), EventFeedOperation.UPDATE, clarificationJSON.createJSON(contest, clarification));
                sendJSON(json + NL);
            }
        }

        public void clarificationRemoved(ClarificationEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Clarification clarification = event.getClarification();
                String json = getJSONEvent(CLARIFICATIONS_KEY, getEventId(), EventFeedOperation.DELETE, clarificationJSON.createJSON(contest, clarification));
                sendJSON(json + NL);
            }
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
            if (eventFeedJSON.isPastStartEvent()) {
                Problem problem = event.getProblem();
                int problemNumber = getProblemIndex(contest, problem.getElementId());
                String json = getJSONEvent(PROBLEM_KEY, getEventId(), EventFeedOperation.CREATE, problemJSON.createJSON(contest, problem, problemNumber));
                sendJSON(json + NL);
            }
        }

        public void problemChanged(ProblemEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Problem problem = event.getProblem();
                int problemNumber = getProblemIndex(contest, problem.getElementId());
                String json = getJSONEvent(PROBLEM_KEY, getEventId(), EventFeedOperation.UPDATE, problemJSON.createJSON(contest, problem, problemNumber));
                sendJSON(json + NL);
            }
        }

        public void problemRemoved(ProblemEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Problem problem = event.getProblem();
                int problemNumber = getProblemIndex(contest, problem.getElementId());
                String json = getJSONEvent(PROBLEM_KEY, getEventId(), EventFeedOperation.DELETE, problemJSON.createJSON(contest, problem, problemNumber));
                sendJSON(json + NL);
            }
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
            if (eventFeedJSON.isPastStartEvent()) {
                Language language = event.getLanguage();
                int languageNumber = getLanguageIndex(contest, language.getElementId());
                String json = getJSONEvent(LANGUAGE_KEY, getEventId(), EventFeedOperation.CREATE, languageJSON.createJSON(contest, language, languageNumber));
                sendJSON(json + NL);
            }
        }

        public void languageChanged(LanguageEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Language language = event.getLanguage();
                int languageNumber = getLanguageIndex(contest, language.getElementId());
                String json = getJSONEvent(LANGUAGE_KEY, getEventId(), EventFeedOperation.UPDATE, languageJSON.createJSON(contest, language, languageNumber));
                sendJSON(json + NL);
            }
        }

        public void languageRemoved(LanguageEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                Language language = event.getLanguage();
                int languageNumber = getLanguageIndex(contest, language.getElementId());
                String json = getJSONEvent(LANGUAGE_KEY, getEventId(), EventFeedOperation.DELETE, languageJSON.createJSON(contest, language, languageNumber));
                sendJSON(json + NL);
            }
        }


        @Override
        public void languagesAdded(LanguageEvent event) {

            Language[] languages = event.getLanguages();
            for (Language language : languages) {
                if (eventFeedJSON.isPastStartEvent()) {
                    int languageNumber = getLanguageIndex(contest, language.getElementId());
                    String json = getJSONEvent(LANGUAGE_KEY, getEventId(), EventFeedOperation.CREATE, languageJSON.createJSON(contest, language, languageNumber));
                    sendJSON(json + NL);
                }
            }
        }

        @Override
        public void languagesChanged(LanguageEvent event) {
            Language[] languages = event.getLanguages();
            for (Language language : languages) {
                if (eventFeedJSON.isPastStartEvent()) {
                    int languageNumber = getLanguageIndex(contest, language.getElementId());
                    String json = getJSONEvent(LANGUAGE_KEY, getEventId(), EventFeedOperation.UPDATE, languageJSON.createJSON(contest, language, languageNumber));
                    sendJSON(json + NL);
                }
            }
        }
        

        public void languageRefreshAll(LanguageEvent event) {
            // ignore
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
     * Contest Time Listener.
     * 
     * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
     */
    protected class ContestTimeListener implements IContestTimeListener {

        @Override
        public void contestTimeAdded(ContestTimeEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                String json = getJSONEvent(CONTEST_KEY, getEventId(), EventFeedOperation.CREATE, contestJSON.createJSON(contest));
                sendJSON(json + NL);
            }
        }

        @Override
        public void contestTimeRemoved(ContestTimeEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                String json = getJSONEvent(CONTEST_KEY, getEventId(), EventFeedOperation.DELETE, contestJSON.createJSON(contest));
                sendJSON(json + NL);
            }
        }

        @Override
        public void contestTimeChanged(ContestTimeEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                String json = getJSONEvent(CONTEST_KEY, getEventId(), EventFeedOperation.UPDATE, contestJSON.createJSON(contest));
                sendJSON(json + NL);
            }
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
     * Listener. 
     * 
     * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
     */
    protected class ContestInformationListener implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                String json = getJSONEvent(CONTEST_KEY, getEventId(), EventFeedOperation.CREATE, contestJSON.createJSON(contest));
                sendJSON(json + NL);
            }
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                String json = getJSONEvent(CONTEST_KEY, getEventId(), EventFeedOperation.UPDATE, contestJSON.createJSON(contest));
                sendJSON(json + NL);
            }
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            if (eventFeedJSON.isPastStartEvent()) {
                String json = getJSONEvent(CONTEST_KEY, getEventId(), EventFeedOperation.DELETE, contestJSON.createJSON(contest));
                sendJSON(json + NL);
            }
        }

        public void contestInformationRefreshAll(ContestInformationEvent event) {
            // ignore 
        }

        public void finalizeDataChanged(ContestInformationEvent event) {
            contestInformationChanged(event);
        }
    }

    /**
     * Send JSON to stream.
     * 
     * @param teamJSON
     */
    public void sendJSON(String string) {
        
        System.out.println(new Date() + " debug 22 Sending "+string);
        
        try {
            os.write(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            log.log(Log.WARNING, "Problem trying to send JSON '"+string+"'", e);
        }
        
        try {
            eventFeedLog.writeEvent(string);
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Log.WARNING, "Problem trying to write event feed log for '"+string+"'", e);
        } 
        
        lastSent = System.currentTimeMillis();
        
//        System.out.println(new Date() + " debug 22 Sent at "+lastSent);
        
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
            log.log(Log.WARNING, "Problem writing startup events to stream", e);
        }
    }

    @Override
    public void run() {
        
        System.out.println("debug 22 run()");
        while (!isFinalized()) {
            
//            System.out.println(new Date() + " debug 22 sleep 30 "+lastSent);
            sleep(30);
            
//            long diff = System.currentTimeMillis() - lastSent;
//            System.out.println("debug 22 diff = "+diff+" cond = "+(lastSent + KEEP_ALIVE_DELAY < System.currentTimeMillis())+ " " + (lastSent + KEEP_ALIVE_DELAY));
//            System.out.println("debug 22 diff = "+diff+" "+lastSent + " " + KEEP_ALIVE_DELAY +" <"+ System.currentTimeMillis());
            
            if (System.currentTimeMillis() > lastSent + KEEP_ALIVE_DELAY) {
                try {
//                    System.out.println(new Date() + " debug 22 wrote keep alive");
                    os.write(NL.getBytes());
                    lastSent = System.currentTimeMillis();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.log(Log.WARNING, "Problem writing keep alive newline to stream", e);
                }
            }
        }
    }

    /**
     * Contest finalized ?
     * @return true if finalized else false
     */
    private boolean isFinalized() {
        
        if (contest.getFinalizeData() != null){
            return contest.getFinalizeData().isCertified();
        }
        
        return false;
    }


}
