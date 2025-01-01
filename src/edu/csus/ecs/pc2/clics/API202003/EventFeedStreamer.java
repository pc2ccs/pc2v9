// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202003;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import edu.csus.ecs.pc2.clics.API202306.JSONTool;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.execute.JudgementUtilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
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
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.IJSONTool;
import edu.csus.ecs.pc2.services.web.IEventFeedStreamer;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Event Feed Server/Streamer.
 *
 * Constructs and sends event feed JSON to client via a stream.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedStreamer extends JSON202003Utilities implements Runnable, UIPlugin, IEventFeedStreamer {

    /**
     * Steps to provide a memento for the event feed. add a stream and filter per connection
     */

    private static final long serialVersionUID = 2076470194640278897L;

    /**
     * Number of ms be sending out keep alive new line.
     */
    private static final long KEEP_ALIVE_DELAY = 100 * Constants.MS_PER_SECOND;

    /**
     * Number of seconds between checks to send keep alive.
     */
    private static final int KEEP_ALIVE_QUERY_PERIOD_SECONDS = 25;

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

    private boolean listenersInitialized = false;

    private EventFeedJSON eventFeedJSON;

    // private AwardJSON awardJSON = new AwardJSON();
    // private TeamMemberJSON teamMemberJSON = new TeamMemberJSON(); SOMEDAY add team numbers JSON

    private EventFeedLog eventFeedLog;

    /**
     * Last time event sent to stream.
     */
    private long lastSent = System.currentTimeMillis();

    /**
     * Last state json data that was sent out
     */
    private String lastStateSent = null;

    /**
     * Class contains output stream and Event Feed Filter
     *
     * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
     */
    protected class StreamAndFilter {

        private OutputStream stream;

        private EventFeedFilter filter;

        public StreamAndFilter(OutputStream outputStream, EventFeedFilter filter) {
            stream = outputStream;
            this.filter = filter;
        }

        public OutputStream getStream() {
            return stream;
        }

        public EventFeedFilter getFilter() {
            return filter;
        }
    }

    /**
     * Collection of StreamAndFilter.
     */
    private Map<OutputStream, StreamAndFilter> streamsMap = new ConcurrentHashMap<OutputStream, StreamAndFilter>();

    /**
     * Is a thread running for this class?
     */
    private boolean running = false;

    private IJSONTool jsonTool;

    private HttpServletRequest servletRequest;

    /**
     * Add stream for future events.
     *
     * Write past events to stream.
     *
     * @param outputStream
     * @param filter
     */
    public void addStream(OutputStream outputStream, EventFeedFilter filter) {
        StreamAndFilter sandf = new StreamAndFilter(outputStream, filter);
        streamsMap.put(outputStream, sandf);
        sendEventsFromEventFeedLog(outputStream, filter);
    }

    /**
     * Remove stream from list of streams to send event feed JSON.
     *
     * @param stream
     */
    public void removeStream(OutputStream stream) {

        if (isStreamActive(stream)) {
            try {
                log.log(Log.INFO, "Closing client stream " + stream);
                stream.close();
                log.log(Log.INFO, "Closed client stream.");
                streamsMap.remove(stream);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                log.log(Log.WARNING, "Problem trying to close stream", e);
            }
        }
    }

    /**
     * Check if the supplied stream is an active client
     *
     * @param outputStream
     * @return true if the stream is being sent the event feed, false if it's not
     */
    public boolean isStreamActive(OutputStream outputStream) {
        return(streamsMap.containsKey(outputStream));
    }

    public EventFeedStreamer(IInternalContest inContest, IInternalController inController, HttpServletRequest servletRequest, SecurityContext sc) {
        this.contest = inContest;
        this.log = inController.getLog();
        this.servletRequest = servletRequest;
        jsonTool = new JSONTool(inContest, inController);
        eventFeedJSON = new EventFeedJSON(jsonTool);
        registerListeners(contest);

        try {
            eventFeedLog = new EventFeedLog(contest);

            String[] lines = eventFeedLog.getLogLines();

            if (lines.length == 0) {
                // Write events to event log if no events are in log (at this time).
                String json = eventFeedJSON.createJSON(contest, servletRequest, sc);
                eventFeedLog.writeEvent(json);
                System.out.println("Event feed log not loaded, event id is " + eventFeedJSON.getEventIdSequence());
            } else {
                eventFeedJSON.setEventIdSequence(lines.length);
                System.out.println("Loaded event feed log setEventIdSequence to " + eventFeedJSON.getEventIdSequence());
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Log.WARNING, "Problem initializing event feed log", e);
        }

    }

    /**
     * Cleanup references so object can be GC'd
     * This was causing issues with the listeners being still attached to the contest model
     */
    public void close() {
        if(contest != null) {
            unRegisterListeners(contest);
        }
    }

    /**
     * Send all events from log to client.
     *
     * @param stream
     * @param filter
     * @param
     */
    private void sendEventsFromEventFeedLog(OutputStream stream, EventFeedFilter filter) {

        /**
         * Number of lines/events in log.
         */
        String[] lines = eventFeedLog.getLogLines();

        try {
            if (lines.length > 0) {

                for (String line : lines) {
                    if (line.startsWith("{\"type\":\"state\",")) {
                        int beginIndex = line.indexOf("\"data\": {", 1)+ 8;
                        lastStateSent = line.substring(beginIndex, line.length()-1);
                    }
                    if (filter.matchesFilter(line)) {
                        stream.write(line.getBytes("UTF-8"));
                        stream.write(NL.getBytes("UTF-8"));
                        stream.flush();
                    }
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
     *
     * @param secs
     */
    public void sleepForSeconds(int secs) {

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
        listenersInitialized = true;

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

    private void unRegisterListeners(IInternalContest inContest) {
        if(listenersInitialized) {
            inContest.removeAccountListener(accountListener);
            inContest.removeRunListener(runListener);
            inContest.removeClarificationListener(clarificationListener);
            inContest.removeProblemListener(problemListener);
            inContest.removeLanguageListener(languageListener);
            inContest.removeGroupListener(groupListener);
            inContest.removeJudgementListener(judgementListener);
            inContest.removeContestInformationListener(contestInformationListener);
            inContest.removeContestTimeListener(contestTimeListener);

            // SOMEDAY CLICS code CCS ensure that commented out listeners are not needed.
            // inContest.removeMessageListener(new MessageListener());
            // inContest.removeSiteListener(new SiteListener());
            // inContest.removeConnectionListener(new ConnectionListener());
            // inContest.removeChangePasswordListener(new ChangePasswordListener());
            // inContest.removeLoginListener(new LoginListener());
            // inContest.removeProfileListener(new ProfileListener());
            // inContest.removeClientSettingsListener(new ClientSettingsListener());
            // inContest.removeBalloonSettingsListener(new BalloonSettingsListener());
            // inContest.removeSecurityMessageListener(new SecurityMessageListener());
        }
    }

    /**
     * Account Listener for EventFeeder.
     *
     * @author pc2@ecs.csus.edu
     */

    protected class AccountListener implements IAccountListener {

        @Override
        public void accountAdded(AccountEvent accountEvent) {
            Account account = accountEvent.getAccount();
            if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                String json = getJSONEvent(TEAM_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(account).toString());
                sendJSON(json + NL);

                // SOMEDAY send team members info

            }
        }

        @Override
        public void accountModified(AccountEvent accountEvent) {

            Account account = accountEvent.getAccount();
            if (isTeam(account)) {
                if (contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    String json = getJSONEvent(TEAM_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(account).toString());
                    sendJSON(json + NL);
                } else {
                    String json = getJSONEvent(TEAM_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \""+account.getClientId().getClientNumber()+"\"}");
                    sendJSON(json + NL);
                }
                // SOMEDAY send team members info
            }
        }

        @Override
        public void accountsAdded(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {
                if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    String json = getJSONEvent(TEAM_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(account).toString());
                    sendJSON(json + NL);
                }
            }
        }

        @Override
        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            Arrays.sort(accounts, new AccountComparator());
            for (Account account : accounts) {
                if (isTeam(account)) {
                    if (contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                        String json = getJSONEvent(TEAM_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(account).toString());
                        sendJSON(json + NL);
                    } else {
                        String json = getJSONEvent(TEAM_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \""+account.getClientId().getClientNumber()+"\"}");
                        sendJSON(json + NL);
                    }
                }
            }
        }

        @Override
        public void accountsRefreshAll(AccountEvent accountEvent) {
            // ignore
        }

        private boolean isTeam(Account account) {
            return account.getClientId().getClientType().equals(ClientType.Type.TEAM);
        }
    }

    /**
     * Run Listener for EventFeeder.
     *
     * @author pc2@ecs.csus.edu
     */
    protected class RunListener implements IRunListener {

        @Override
        public void runAdded(RunEvent event) {
            Run run = event.getRun();
            Account account = contest.getAccount(run.getSubmitter());
            if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) && !run.isDeleted()) {

                String json = getJSONEvent(SUBMISSION_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(run, servletRequest, null).toString());
                sendJSON(json + NL);
            }
        }

        @Override
        public void runChanged(RunEvent event) {
            Run run = event.getRun();
            Account account = contest.getAccount(run.getSubmitter());
            if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                if (run.isDeleted()) {
                    String json = getJSONEvent(JUDGEMENT_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + IJSONTool.getSubmissionId(run) + "\"}");
                    sendJSON(json + NL);
                } else {
                    if (run.isJudged()) {
                        String json = getJSONEvent(JUDGEMENT_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertJudgementToJSON(run).toString());
                        sendJSON(json + NL);
                        // Now send out the runcases (test cases).  Get most recent ones for this run.
                        RunTestCase [] testCases = JudgementUtilities.getLastTestCaseArray(contest, run);
                        for (int j = 0; j < testCases.length; j++) {
                            json = getJSONEvent(RUN_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(testCases, j).toString());
                            sendJSON(json + NL);
                        }

                    } else {
                        String json = getJSONEvent(SUBMISSION_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(run, servletRequest, null).toString());
                        sendJSON(json + NL);
                    }
                }
            }
        }

        @Override
        public void runRemoved(RunEvent event) {

            Run run = event.getRun();
            Account account = contest.getAccount(run.getSubmitter());
            if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD)) {

                String json = getJSONEvent(JUDGEMENT_KEY, getNextEventId(), EventFeedOperation.DELETE, "{id: \"" + IJSONTool.getSubmissionId(run) + "}");
                sendJSON(json + NL);
            }
        }

        @Override
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

        @Override
        public void clarificationAdded(ClarificationEvent event) {
            Clarification clarification = event.getClarification();
            String json = getJSONEvent(CLARIFICATIONS_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(clarification, null).toString());
            sendJSON(json + NL);
        }

        @Override
        public void clarificationChanged(ClarificationEvent event) {
            Clarification clarification = event.getClarification();
            ClarificationAnswer[] clarAnswers = clarification.getClarificationAnswers();
            String json = getJSONEvent(CLARIFICATIONS_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(clarification, clarAnswers[clarAnswers.length - 1]).toString());
            sendJSON(json + NL);
        }

        @Override
        public void clarificationRemoved(ClarificationEvent event) {
            Clarification clarification = event.getClarification();
            String id = clarification.getElementId().toString();
            String json = getJSONEvent(CLARIFICATIONS_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + id + "\"}");
            sendJSON(json + NL);
            ClarificationAnswer[] clarAnswers = clarification.getClarificationAnswers();
            if (clarAnswers != null) {
                id = clarAnswers[clarAnswers.length - 1].getElementId().toString();
                json = getJSONEvent(CLARIFICATIONS_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + id + "\"}");
                sendJSON(json + NL);
            }
        }

        @Override
        public void refreshClarfications(ClarificationEvent event) {
            // ignore
        }
    }

    /**
     * Problem Listener for EventFeeder.
     *
     * @author pc2@ecs.csus.edu
     */

    protected class ProblemListener implements IProblemListener {

        @Override
        public void problemAdded(ProblemEvent event) {
            Problem problem = event.getProblem();
            int problemNumber = getProblemIndex(contest, problem.getElementId());
            String json = getJSONEvent(PROBLEM_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(problem, problemNumber).toString());
            sendJSON(json + NL);
        }

        @Override
        public void problemChanged(ProblemEvent event) {
            Problem problem = event.getProblem();
            String json;
            if (problem.isActive()) {
                int problemNumber = getProblemIndex(contest, problem.getElementId());
                json = getJSONEvent(PROBLEM_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(problem, problemNumber).toString());
            } else {
                json = getJSONEvent(PROBLEM_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + IJSONTool.getProblemId(problem) + "\"}");
            }
            sendJSON(json + NL);
        }

        @Override
        public void problemRemoved(ProblemEvent event) {
            Problem problem = event.getProblem();
            String json = getJSONEvent(PROBLEM_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + IJSONTool.getProblemId(problem) + "\"}");
            sendJSON(json + NL);
        }

        @Override
        public void problemRefreshAll(ProblemEvent event) {
            // ignore
        }
    }

    /**
     * Language Listener for EventFeeder.
     *
     * @author pc2@ecs.csus.edu
     */

    protected class LanguageListener implements ILanguageListener {

        @Override
        public void languageAdded(LanguageEvent event) {
            Language language = event.getLanguage();
            String json = getJSONEvent(LANGUAGE_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(language).toString());
            sendJSON(json + NL);
        }

        @Override
        public void languageChanged(LanguageEvent event) {
            Language language = event.getLanguage();
            String json;
            if (language.isActive()) {
                json = getJSONEvent(LANGUAGE_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(language).toString());
            } else {
                json = getJSONEvent(LANGUAGE_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + IJSONTool.getLanguageId(language) + "\"}");
            }
            sendJSON(json + NL);
        }

        @Override
        public void languageRemoved(LanguageEvent event) {
            Language language = event.getLanguage();
            String json = getJSONEvent(LANGUAGE_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + IJSONTool.getLanguageId(language) + "\"}");
            sendJSON(json + NL);
        }

        @Override
        public void languagesAdded(LanguageEvent event) {

            Language[] languages = event.getLanguages();
            for (Language language : languages) {
                String json = getJSONEvent(LANGUAGE_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(language).toString());
                sendJSON(json + NL);
            }
        }

        @Override
        public void languagesChanged(LanguageEvent event) {
            Language[] languages = event.getLanguages();
            for (Language language : languages) {
                String json;
                if (language.isActive()) {
                    json = getJSONEvent(LANGUAGE_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(language).toString());
                } else {
                    json = getJSONEvent(LANGUAGE_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + IJSONTool.getLanguageId(language) + "\"}");
                }
                sendJSON(json + NL);
            }
        }

        @Override
        public void languageRefreshAll(LanguageEvent event) {
            // ignore
        }
    }

    /**
     * Group/Region Listener for EventFeeder.
     *
     * @author pc2@ecs.csus.edu
     */

    protected class GroupListener implements IGroupListener {

        @Override
        public void groupAdded(GroupEvent event) {
            Group group = event.getGroup();
            String json = getJSONEvent(GROUPS_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(group).toString());
            sendJSON(json + NL);

        }

        @Override
        public void groupChanged(GroupEvent event) {
            Group group = event.getGroup();
            String json;
            if (group.isDisplayOnScoreboard()) {
                json = getJSONEvent(GROUPS_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(group).toString());
            } else {
                json = getJSONEvent(GROUPS_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + IJSONTool.getGroupId(group) + "\"}");
            }
            sendJSON(json + NL);
        }

        @Override
        public void groupRemoved(GroupEvent event) {
            Group group = event.getGroup();
            String json = getJSONEvent(GROUPS_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + IJSONTool.getGroupId(group) + "\"}");
            sendJSON(json + NL);
        }

        @Override
        public void groupsAdded(GroupEvent groupEvent) {
            Group[] groups = groupEvent.getGroups();
            for (Group group : groups) {
                String json = getJSONEvent(GROUPS_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(group).toString());
                sendJSON(json + NL);
            }
        }

        @Override
        public void groupsChanged(GroupEvent groupEvent) {
            Group[] groups = groupEvent.getGroups();
            for (Group group : groups) {
                String json;
                if (group.isDisplayOnScoreboard()) {
                    json = getJSONEvent(GROUPS_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(group).toString());
                } else {
                    json = getJSONEvent(GROUPS_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + IJSONTool.getGroupId(group) + "\"}");
                }
                sendJSON(json + NL);
            }
        }

        @Override
        public void groupRefreshAll(GroupEvent groupEvent) {
            // ignore
        }
    }

    /**
     * Judgement listener
     *
     * @author pc2@ecs.csus.edu
     */

    protected class JudgementListener implements IJudgementListener {

        @Override
        public void judgementAdded(JudgementEvent event) {
            Judgement judgement = event.getJudgement();
            String json = getJSONEvent(JUDGEMENT_TYPE_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(judgement).toString());
            sendJSON(json + NL);
        }

        @Override
        public void judgementChanged(JudgementEvent event) {
            Judgement judgement = event.getJudgement();
            String json;
            if (judgement.isActive()) {
                json = getJSONEvent(JUDGEMENT_TYPE_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(judgement).toString());
            } else {
                json = getJSONEvent(JUDGEMENT_TYPE_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(judgement).toString());
            }
            sendJSON(json + NL);
        }

        @Override
        public void judgementRemoved(JudgementEvent event) {
            Judgement judgement = event.getJudgement();
            String json = getJSONEvent(JUDGEMENT_TYPE_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(judgement).toString());
            sendJSON(json + NL);
        }

        @Override
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
            // TODO seems we should only do this for our local site times...
            String json = getJSONEvent(CONTEST_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(contest.getContestInformation()).toString());
            sendJSON(json + NL);
        }

        @Override
        public void contestTimeRemoved(ContestTimeEvent event) {
            // TODO this does not seem right, is our local time ever removed?
            String json = getJSONEvent(CONTEST_KEY, getNextEventId(), EventFeedOperation.DELETE, jsonTool.convertToJSON(contest.getContestInformation()).toString());
            sendJSON(json + NL);
        }

        @Override
        public void contestTimeChanged(ContestTimeEvent event) {
            // TODO seems we should only do this for our local site times...
            String json = getJSONEvent(CONTEST_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(contest.getContestInformation()).toString());
            sendJSON(json + NL);
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

        @Override
        public void contestInformationAdded(ContestInformationEvent event) {
            String json = getJSONEvent(CONTEST_KEY, getNextEventId(), EventFeedOperation.CREATE, jsonTool.convertToJSON(event.getContestInformation()).toString());
            sendJSON(json + NL);
            String currentState = jsonTool.toStateJSON(event.getContestInformation()).toString();
            if (lastStateSent == null) {
                json = getJSONEvent(STATE_KEY, getNextEventId(), EventFeedOperation.CREATE, currentState);
                sendJSON(json + NL);
            } else {
                if (lastStateSent != currentState) {
                    json = getJSONEvent(STATE_KEY, getNextEventId(), EventFeedOperation.UPDATE, currentState);
                    sendJSON(json + NL);
                }
            }
        }

        @Override
        public void contestInformationChanged(ContestInformationEvent event) {
            String json = getJSONEvent(CONTEST_KEY, getNextEventId(), EventFeedOperation.UPDATE, jsonTool.convertToJSON(event.getContestInformation()).toString());
            sendJSON(json + NL);
            String currentState = jsonTool.toStateJSON(event.getContestInformation()).toString();
            if (lastStateSent == null) {
                json = getJSONEvent(STATE_KEY, getNextEventId(), EventFeedOperation.CREATE, currentState);
                sendJSON(json + NL);
            } else {
                if (lastStateSent != currentState) {
                    json = getJSONEvent(STATE_KEY, getNextEventId(), EventFeedOperation.UPDATE, currentState);
                    sendJSON(json + NL);
                }
            }
        }

        @Override
        public void contestInformationRemoved(ContestInformationEvent event) {
            String json = getJSONEvent(CONTEST_KEY, getNextEventId(), EventFeedOperation.DELETE, "{\"id\": \"" + contest.getContestIdentifier() + "\"}");
            sendJSON(json + NL);
        }

        @Override
        public void contestInformationRefreshAll(ContestInformationEvent event) {
            // ignore
        }

        @Override
        public void finalizeDataChanged(ContestInformationEvent event) {
            contestInformationChanged(event);
        }
    }

    /**
     * Send JSON to stream.
     *
     * @param teamJSON
     */
    public synchronized void sendJSON(String string) {

        long newId = eventFeedJSON.nextEventIdSequence();

        string = replaceEventId (string, newId);

        /**
         * Send JSON to each
         */
        for (Map.Entry<OutputStream, StreamAndFilter> entry : streamsMap.entrySet()) {
            StreamAndFilter streamAndFilter = entry.getValue();

            try {
                if (streamAndFilter.getFilter().matchesFilter(string)) {
                    OutputStream stream = streamAndFilter.getStream();
                    stream.write(string.getBytes("UTF-8"));
                    stream.flush();
                }
            } catch (Exception e) {
                System.out.println("INFO Unable to send JSON in sendJSON: " + e.getCause().getMessage());
                log.log(Log.INFO, "Problem trying to send JSON '" + string + "'", e);
                removeStream(streamAndFilter.getStream());
            }
        }

        try {
            eventFeedLog.writeEvent(string);
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Log.WARNING, "Problem trying to write event feed log for '" + string + "'", e);
        }

        lastSent = System.currentTimeMillis();
    }

    private String replaceEventId(String string, long newId) {

        // {"type":"languages", "id":"-1",
        // {"type":"languages", "id":"pc2-14",

        String newString =string.replaceFirst("\"id\":\"-1\"","\"id\":\""+ EventFeedJSON.getEventId(newId)+"\"");
        return newString.replaceFirst("\"id\":\"pc2--1\"","\"id\":\"" + EventFeedJSON.getEventId(newId)+"\"");
    }

    /**
     * Only used be the listeners, gets the next event id
     * @return
     */
    public long getNextEventId() {
        return -1;

    }

    @Override
    public void run() {

        running = true;

        // SOMEDAY - replace the keep alive code with a Timer instance.

        /**
         * Keep alive
         */
        while (!isFinalized() && running) {

            sleepForSeconds(KEEP_ALIVE_QUERY_PERIOD_SECONDS); // sleep - give back cycles to JVM.

            if (System.currentTimeMillis() > lastSent + KEEP_ALIVE_DELAY) {

                // Send keep alive to every running stream.

                for (Iterator<OutputStream> streams = streamsMap.keySet().iterator(); streams.hasNext();) {
                    OutputStream stream = streams.next();
                    StreamAndFilter streamAndFilter = streamsMap.get(stream);

                    try {
                        // OutputStream stream = streamAndFilter.getStream();
                        stream.write(NL.getBytes());
                        stream.flush();

                    } catch (Exception e) {
                        log.log(Log.INFO, "Problem writing keep alive newline to stream to " + streamAndFilter.getFilter().getClient(), e);
                        removeStream(streamAndFilter.getStream());
                    }
                }

                lastSent = System.currentTimeMillis();
            }
        }

        running = false;
    }

    /**
     * Contest finalized ?
     *
     * @return true if finalized else false
     */
    public boolean isFinalized() {

        if (contest.getFinalizeData() != null) {
            return contest.getFinalizeData().isCertified();
        }

        return false;
    }

    /**
     * Is running on a thread?
     *
     * @see #halt()
     * @return
     */
    public boolean isRunning() {
        return running;
    }

    public void halt() {
        running = false;
    }
    /**
     * Implementation of IEventFeedStreamer for those needing just the full event feed. eg reports, etc.
     * Currently, this is called from the ui.WebServerPane.viewJSONEventFeed
     *
     * @param contest
     * @param controller
     * @param servletRequest Web request, if any null otherwise
     * @param sc - security info, if any, null otherwise
     * @return json string containing the entire event feed.
     */
    @Override
    public String getEventFeedJSON(IInternalContest contest, IInternalController controller, HttpServletRequest servletRequest, SecurityContext sc) {
        return createEventFeedJSON(contest, controller, servletRequest, sc);
    }

    /**
     * Create a snap shot of the 2020-03 JSON event feed.
     *
     * Called from interface implementation above, and JUnit tests: services.web.EventFeedStreamerTest test unit.
     */
    public static String createEventFeedJSON(IInternalContest contest, IInternalController controller, HttpServletRequest servletRequest, SecurityContext sc) {
        EventFeedStreamer streamer = new EventFeedStreamer(contest, controller, servletRequest, sc);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        streamer.addStream(stream, new EventFeedFilter());
        streamer.removeStream(stream);
        String json = new String(stream.toByteArray());
        stream = null;
        streamer.close();
        streamer = null;
        return json;
    }
}
