package edu.csus.ecs.pc2.services.eventFeed;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.GroupEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IEventFeedRunnable;
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
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.XMLMemento;
import edu.csus.ecs.pc2.exports.ccs.ResolverEventFeedXML;

/**
 * Send live event feed XML.
 * 
 * Create XML event feed elements and writes them to an outputstream
 * or sends them to the class implementing {@link IEventFeedRunnable}.
 * 
 * To create a "freeze" event feed, use the {@link #setFreezeTimeMinutes(long)}
 * method, see that method for details.
 * 
 *  Each time an event feed XML element is triggered will send that
 *  XML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeeder implements Runnable {

    private IInternalContest contest;
    
    private Filter filter = null;
    
    /**
     * Runnable used to send XML to socket or other output.
     */
    private IEventFeedRunnable eventFeedRunnable = null;

    private OutputStreamWriter out;

    /**
     * Class that creates Event Feed which is sent to client.
     */
//    private EventFeedXML eventFeedXML = new EventFeedXML();
//    private EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
  private ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
    
    private AccountListener accountListener = null;

    private RunListener runListener = null;

    private ClarificationListener clarificationListener = null;

    private ProblemListener problemListener = null;

    private LanguageListener languageListener = null;

    private GroupListener groupListener = null;

    private JudgementListener judgementListener = null;

    private ContestInformationListener contestInformationListener = null;

    private ContestTimeListener contestTimeListener = null;
    
    EventFeeder() {
        initListeners();
    }
    
    private void initListeners() {

        accountListener = new AccountListener();
        runListener = new RunListener();
        clarificationListener = new ClarificationListener();
        problemListener = new ProblemListener();
        languageListener = new LanguageListener();
        groupListener = new GroupListener();
        judgementListener = new JudgementListener();
        contestInformationListener = new ContestInformationListener();
        contestTimeListener = new ContestTimeListener();
    }

    /**
     * Write Event Feed XML to output stream.
     * 
     * @param contest
     * @param out
     * @param contestTimeWhenJudgementsNoLongerSentMinutes 0 fro no filtered free, else the freeze time.
     */
    public EventFeeder(IInternalContest contest, OutputStreamWriter out, long contestTimeWhenJudgementsNoLongerSentMinutes) {
        this();
        
        this.contest = contest;
        this.out = out;
        this.eventFeedRunnable = new EventFeedRunner();
        setFreezeTimeMinutes(contestTimeWhenJudgementsNoLongerSentMinutes);
    }
    
    /**
     * Invoke send method with Event Feed XML.
     * 
     * @see IEventFeedRunnable#send(String).
     * 
     * @param contest
     * @param runnable
     */
    public EventFeeder(IInternalContest contest, IEventFeedRunnable runnable) {
        this();

        this.contest = contest;
        this.eventFeedRunnable = runnable;
    }
    
    /**
     * Default event feed runner.
     * 
     * @author Doug
     *
     */
    private class EventFeedRunner implements IEventFeedRunnable {
        public void send(String xmlString) {
            sendToSocket(xmlString);
        }
    }

    public void run() {

        /**
         * Write startup xml to socket
         */
        String xml = eventFeedXML.createStartupXML(contest);
        eventFeedRunnable.send(xml);

        FinalizeData finalizeData = contest.getFinalizeData();

        if (finalizeData != null && contest.getFinalizeData().isCertified()) {
            xml = eventFeedXML.createFinalizeXML(contest, finalizeData);
            eventFeedRunnable.send(xml);
            /**
             * Since no listeners registered, this will close the socket/connection.
             */
            
            // Halt this thread, close the socket.
//            Thread.currentThread().interrupt();
            halt();
            
        } else {
            /**
             * Register to listen to events. This keeps the event feeder feeding.
             */
            registerListeners(contest);
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

        // TODO CCS ensure that commented out listeners are not needed.
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

    // stubs for listener classes
    // protected class AccountListener implements IAccountListener {}
    // protected class RunListener implements IRunListener {}
    // protected class ClarificationListener implements IClarificationListener {}
    // protected class ProblemListener implements IProblemListener {}
    // protected class LanguageListener implements ILanguageListener {}
    // protected class ChangePasswordListener implements IChangePasswordListener {}
    // protected class LoginListener implements ILoginListener {}
    // protected class MessageListener implements IMessageListener {}
    // protected class SiteListener implements ISiteListener {}
    // protected class ConnectionListener implements IConnectionListener {}
    // protected class GroupListener implements IGroupListener {}
    // protected class ProfileListener implements IProfileListener {}
    // protected class ClientSettingsListener implements IClientSettingsListener {}
    // protected class BalloonSettingsListener implements IBalloonSettingsListener {}
    // protected class SecurityMessageListener implements ISecurityMessageListener {}
    
    public void dispose(){

        contest.removeAccountListener(accountListener);
        contest.removeRunListener(runListener);
        contest.removeClarificationListener(clarificationListener);
        contest.removeProblemListener(problemListener);
        contest.removeLanguageListener(languageListener);
        contest.removeGroupListener(groupListener);
        contest.removeJudgementListener(judgementListener);
        contest.removeContestInformationListener(contestInformationListener);

    }

    /**
     * This sets the minute when the contest freezes.
     * <br><br>
     * 
     * @param contestTimeWhenJudgementsNoLongerSentMinutes
     */
    public void setFreezeTimeMinutes(long contestTimeWhenJudgementsNoLongerSentMinutes) {
        filter = new Filter();
        if (contestTimeWhenJudgementsNoLongerSentMinutes != 0) {
            filter.setStartElapsedTime(contestTimeWhenJudgementsNoLongerSentMinutes);
        }
    }

    /**
     * Halt this thread.
     */
    public void halt() {
        
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();  // TODO CCS log this exception
        }
        
        dispose();
    }

    /**
     * Send XML to socket/client.
     * 
     * @param memento
     */
    public void sendToSocket(String xmlString) {
        
        try {
            out.write(xmlString);
            out.flush();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send XML to socket/client.
     * 
     * @param memento
     */
    private void sendXML(XMLMemento memento) {
        try {
            eventFeedRunnable.send(memento.saveToString(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Account Listener for EventFeeder.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class AccountListener implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            Account account = accountEvent.getAccount();
            if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                sendXML(eventFeedXML.createElement(contest, account));
            }
        }

        private boolean isTeam(Account account) {
            return account.getClientId().getClientType().equals(ClientType.Type.TEAM);
        }

        public void accountModified(AccountEvent accountEvent) {
            Account account = accountEvent.getAccount();
            if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                sendXML(eventFeedXML.createElement(contest, account));
            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {
                if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    sendXML(eventFeedXML.createElement(contest, account));
                }
            }
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            Arrays.sort(accounts, new AccountComparator());
            for (Account account : accounts) {
                if (isTeam(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    sendXML(eventFeedXML.createElement(contest, account));
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
     * @version $Id$
     */

    // $HeadURL$
    protected class RunListener implements IRunListener {

        public void runAdded(RunEvent event) {
            Run run = event.getRun();
            Account account = contest.getAccount(run.getSubmitter());
            if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) && ! run.isDeleted()){
                sendXML(eventFeedXML.createElement(contest, run));
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
            sendXML(eventFeedXML.createElement(contest, run));
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
     * @version $Id$
     */

    // $HeadURL$
    protected class ClarificationListener implements IClarificationListener {

        public void clarificationAdded(ClarificationEvent event) {
            Clarification clarification = event.getClarification();
            sendXML(eventFeedXML.createElement(contest, clarification));
        }

        public void clarificationChanged(ClarificationEvent event) {
            Clarification clarification = event.getClarification();
            sendXML(eventFeedXML.createElement(contest, clarification));
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
     * @version $Id$
     */

    // $HeadURL$
    protected class ProblemListener implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
            Problem problem = event.getProblem();
            int problemNumber = getProblemNumber(problem);
            sendXML(eventFeedXML.createElement(contest, problem, problemNumber));
        }

        private int getProblemNumber(Problem inProblem) {
            int idx = 1;
            for (Problem problem : contest.getProblems()) {
                if (problem.equals(inProblem)) {
                    return idx;
                }
                idx++;
            }
            return 0;
        }

        public void problemChanged(ProblemEvent event) {
            Problem problem = event.getProblem();
            int problemNumber = getProblemNumber(problem);
            sendXML(eventFeedXML.createElement(contest, problem, problemNumber));
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
     * @version $Id$
     */

    // $HeadURL$

    protected class LanguageListener implements ILanguageListener {

        public void languageAdded(LanguageEvent event) {
            Language language = event.getLanguage();
            int languageNumber = getLanguageNumber(language);
            sendXML(eventFeedXML.createElement(contest, language, languageNumber));
        }

        private int getLanguageNumber(Language inLanguage) {
            int idx = 1;
            for (Language language : contest.getLanguages()) {
                if (language.equals(inLanguage)) {
                    return idx;
                }
                idx++;
            }
            return 0;
        }

        public void languageChanged(LanguageEvent event) {
            Language language = event.getLanguage();
            int languageNumber = getLanguageNumber(language);
            sendXML(eventFeedXML.createElement(contest, language, languageNumber));
        }

        public void languageRemoved(LanguageEvent event) {
            // ignore
        }

        public void languageRefreshAll(LanguageEvent event) {
            // ignore
        }

        @Override
        public void languagesAdded(LanguageEvent event) {
            Language[] languages = event.getLanguages();
            for (Language language : languages) {
                int languageNumber = getLanguageNumber(language);
                sendXML(eventFeedXML.createElement(contest, language, languageNumber));
            }
        }

        @Override
        public void languagesChanged(LanguageEvent event) {
            Language[] languages = event.getLanguages();
            for (Language language : languages) {
                int languageNumber = getLanguageNumber(language);
                sendXML(eventFeedXML.createElement(contest, language, languageNumber));
            }
        }
    }

    /**
     * Group/Region Listener for EventFeeder.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class GroupListener implements IGroupListener {

        public void groupAdded(GroupEvent event) {
            Group group = event.getGroup();
            sendXML(eventFeedXML.createElement(contest, group));
        }

        public void groupChanged(GroupEvent event) {
            Group group = event.getGroup();
            sendXML(eventFeedXML.createElement(contest, group));
        }

        public void groupRemoved(GroupEvent event) {
            // ignore
        }

        public void groupsAdded(GroupEvent groupEvent) {
            Group[] groups = groupEvent.getGroups();
            for (Group group : groups) {
                sendXML(eventFeedXML.createElement(contest, group));
            }
        }

        public void groupsChanged(GroupEvent groupEvent) {
            Group[] groups = groupEvent.getGroups();
            for (Group group : groups) {
                sendXML(eventFeedXML.createElement(contest, group));
            }
        }
        
        public void groupRefreshAll(GroupEvent groupEvent) {
            // ignore
        }
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class JudgementListener implements IJudgementListener {

        public void judgementAdded(JudgementEvent event) {
            Judgement judgement = event.getJudgement();
            int judgementSequence = getJudgementNumber (contest, judgement);
            sendXML(eventFeedXML.createElement(contest, judgement, judgementSequence));
        }

        public void judgementChanged(JudgementEvent event) {
            Judgement judgement = event.getJudgement();
            int judgementSequence = getJudgementNumber (contest, judgement);
            sendXML(eventFeedXML.createElement(contest, judgement, judgementSequence));
        }

        public void judgementRemoved(JudgementEvent event) {
            // ignored

        }

        public void judgementRefreshAll(JudgementEvent judgementEvent) {
            // SOMEDAY refresh all judgements on event feed
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
            ContestInformation info = contest.getContestInformation();
            sendXML(eventFeedXML.createInfoElement(contest, info));
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
     * @version $Id$
     */

    // $HeadURL$
    protected class ContestInformationListener implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            ContestInformation info = event.getContestInformation();
            sendXML(eventFeedXML.createInfoElement(contest, info));
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            ContestInformation info = event.getContestInformation();
            sendXML(eventFeedXML.createInfoElement(contest, info));
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // ignored
        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            // SOMEDAY refresh all
        }

        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            FinalizeData data = contestInformationEvent.getFinalizeData();
            eventFeedRunnable.send(eventFeedXML.createFinalizeXML(contest, data));
        }
    }

    public int getJudgementNumber(IInternalContest contest2, Judgement inJudgement) {

        int count = 0;
        for (Judgement judgement : contest2.getJudgements()) {
            count ++;
            if (judgement.getElementId().equals(inJudgement.getElementId())) {
                return count;
            }
        }
        
        return count;
    }
}
