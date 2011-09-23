package edu.csus.ecs.pc2.core.transport;

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
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.GroupEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
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
import edu.csus.ecs.pc2.core.report.EventFeedXML;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Event Feeder runner.
 * 
 * When run as a Thread will send event XML to client/socket.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO CCS EF <info>

// TODO CCS EF <testcase>
// TODO CCS EF <notification>
// TODO CCS EF <finalized>

// $HeadURL$
class EventFeeder implements Runnable {

    private IInternalContest contest;

    private OutputStreamWriter out;

    private boolean running = true;

    private EventFeedXML eventFeedXML = new EventFeedXML();

    public EventFeeder(IInternalContest contest, OutputStreamWriter out) {
        this.contest = contest;
        this.out = out;
    }

    public void run() {
        
        String xml = eventFeedXML.createStartupXML(contest);
        sendToSocket(xml);
        registerListeners(contest);

        while (running) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        System.out.println("EventFeeder done");
    }

    private void registerListeners(IInternalContest inContest) {

        inContest.addAccountListener(new AccountListener());
        inContest.addRunListener(new RunListener());
        inContest.addClarificationListener(new ClarificationListener());
        inContest.addProblemListener(new ProblemListener());
        inContest.addLanguageListener(new LanguageListener());
        inContest.addGroupListener(new GroupListener());
        inContest.addJudgementListener(new JudgementListener());
        inContest.addContestInformationListener(new ContestInformationListener());

        // TODO CCS insure that commented out listeners are not needed.
        // inContest.addContestTimeListener(new ContestTimeListener());
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
    // protected class ContestTimeListener implements IContestTimeListener {}
    // protected class MessageListener implements IMessageListener {}
    // protected class SiteListener implements ISiteListener {}
    // protected class ConnectionListener implements IConnectionListener {}
    // protected class GroupListener implements IGroupListener {}
    // protected class ProfileListener implements IProfileListener {}
    // protected class ClientSettingsListener implements IClientSettingsListener {}
    // protected class BalloonSettingsListener implements IBalloonSettingsListener {}
    // protected class SecurityMessageListener implements ISecurityMessageListener {}

    /**
     * Halt this thread.
     */
    public void halt() {
        running = false;
        
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();  // TODO CCS 
        }
    }

    /**
     * Send XML to socket/client.
     * 
     * @param memento
     */
    protected void sendToSocket(String xmlString) {

        try {
            out.write(xmlString);
            out.flush();
            
//            System.err.println();
//            System.err.println("debug 22 " + xmlString);
            
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
            sendToSocket(memento.saveToString(true));
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
            if (isTeam(account)) {
                sendXML(eventFeedXML.createElement(contest, account));
            }
        }

        private boolean isTeam(Account account) {
            return account.getClientId().getClientType().equals(ClientType.Type.TEAM);
        }

        public void accountModified(AccountEvent accountEvent) {
            Account account = accountEvent.getAccount();
            if (isTeam(account)) {
                sendXML(eventFeedXML.createElement(contest, account));
            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            Account account = accountEvent.getAccount();
            if (isTeam(account)) {
                sendXML(eventFeedXML.createElement(contest, account));
            }
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            Arrays.sort(accounts, new AccountComparator());
            for (Account account : accounts) {
                if (isTeam(account)) {
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
            // ignore
        }

        public void runChanged(RunEvent event) {
            Run run = event.getRun();
            sendXML(eventFeedXML.createElement(contest, run));
        }

        public void runRemoved(RunEvent event) {
            Run run = event.getRun();
            sendXML(eventFeedXML.createElement(contest, run));
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
            sendXML(eventFeedXML.createElement(contest, judgement));
        }

        public void judgementChanged(JudgementEvent event) {
            Judgement judgement = event.getJudgement();
            sendXML(eventFeedXML.createElement(contest, judgement));
        }

        public void judgementRemoved(JudgementEvent event) {
            // TODO Auto-generated method stub

        }

        public void judgementRefreshAll(JudgementEvent judgementEvent) {
            // TODO Auto-generated method stub

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
            //
        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            // TODO Auto-generated method stub

        }

        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            FinalizeData data = contestInformationEvent.getFinalizeData();
            sendToSocket(eventFeedXML.createFinalizeXML(contest, data));
        }
    }
}
