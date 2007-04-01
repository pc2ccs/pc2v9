package edu.csus.ecs.pc2.core.model;

import java.util.Enumeration;
import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.ContestTimeList;
import edu.csus.ecs.pc2.core.list.LanguageDisplayList;
import edu.csus.ecs.pc2.core.list.LanguageList;
import edu.csus.ecs.pc2.core.list.LoginList;
import edu.csus.ecs.pc2.core.list.ProblemDisplayList;
import edu.csus.ecs.pc2.core.list.ProblemList;
import edu.csus.ecs.pc2.core.list.RunList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class Model implements IModel {

    public static final String SVN_ID = "$Id$";

    private ClientId localClientId = null;

    private Vector<IRunListener> runListenerList = new Vector<IRunListener>();

    private Vector<IProblemListener> problemListenerList = new Vector<IProblemListener>();

    private Vector<ILanguageListener> languageListenerList = new Vector<ILanguageListener>();

    private Vector<ILoginListener> loginListenerList = new Vector<ILoginListener>();
    
    private Vector<IContestTimeListener> contestTimeListenerList = new Vector<IContestTimeListener>();

    private AccountList accountList = new AccountList();

    private Vector<IAccountListener> accountListenerList = new Vector<IAccountListener>();

    private LoginList loginList = new LoginList();
    
    private ContestTimeList contestTimeList = new ContestTimeList();

    private RunList runList = new RunList();

    private int runNumber = 0;

    private int siteNumber = 6;

    /**
     * List of all defined problems. Contains deleted problems too.
     */
    private ProblemList problemList = new ProblemList();

    /**
     * List of all problems displayed to users, in order. Does not contain deleted problems.
     */
    private ProblemDisplayList problemDisplayList = new ProblemDisplayList();

    /**
     * List of all languages. Contains deleted problems too.
     */
    private LanguageList languageList = new LanguageList();

    /**
     * List of all displayed languages, in order. Does not contain deleted languages.
     */
    private LanguageDisplayList languageDisplayList = new LanguageDisplayList();

    /**
     * Initialize Model with data.
     */
    public void initializeWithFakeData() {

        String[] probNames = { "A - Sum of Squares", "B - Sumit", "C - Hello", "D - GoodBye" };

        for (String problemNames : probNames) {
            Problem problem = new Problem(problemNames);
            problemDisplayList.add(problem);
            problemList.add(problem);
        }

        String[] langNames = { "Java", "BASIC", "C++", "ANSI C", "APL" };

        for (String languageName : langNames) {
            Language language = new Language(languageName);
            languageList.add(language);
            languageDisplayList.add(language);
        }

        // Generate the server account
        generateNewAccounts(ClientType.Type.SERVER.toString(), 1, true);
        
        ContestTime contestTime = new ContestTime();
        contestTime.setElapsedMins(9);
        contestTime.startContestClock();
        
        addContestTime(contestTime, siteNumber);
    }

    public void addRunListener(IRunListener runListener) {
        runListenerList.addElement(runListener);
    }

    public void removeRunListener(IRunListener runListener) {
        runListenerList.removeElement(runListener);
    }
    
    public void addContestTimeListener(IContestTimeListener contestTimeListener) {
        contestTimeListenerList.addElement(contestTimeListener);
    }

    public void removeContestTimeListener(IContestTimeListener contestTimeListener) {
        contestTimeListenerList.removeElement(contestTimeListener);
    }
    
    private void fireRunListener(RunEvent runEvent) {
        for (int i = 0; i < runListenerList.size(); i++) {

            if (runEvent.getAction() == RunEvent.Action.ADDED) {
                runListenerList.elementAt(i).runAdded(runEvent);
            } else if (runEvent.getAction() == RunEvent.Action.DELETED) {
                runListenerList.elementAt(i).runRemoved(runEvent);
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
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.UPDATED) {
                contestTimeListenerList.elementAt(i).contestTimeChanged(contestTimeEvent);
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
            } else {
                problemListenerList.elementAt(i).problemChanged(problemEvent);
            }
        }
    }

    private void fireLanguageListener(LanguageEvent languageEvent) {
        for (int i = 0; i < languageListenerList.size(); i++) {

            if (languageEvent.getAction() == LanguageEvent.Action.ADDED) {
                languageListenerList.elementAt(i).languageAdded(languageEvent);
            } else if (languageEvent.getAction() == LanguageEvent.Action.DELETED) {
                languageListenerList.elementAt(i).languageRemoved(languageEvent);
            } else {
                languageListenerList.elementAt(i).languageChanged(languageEvent);
            }
        }
    }

    private void fireLoginListener(LoginEvent loginEvent) {
        for (int i = 0; i < loginListenerList.size(); i++) {

            if (loginEvent.getAction() == LoginEvent.Action.NEW_LOGIN) {
                loginListenerList.elementAt(i).loginAdded(loginEvent);
            } else if (loginEvent.getAction() == LoginEvent.Action.LOGOFF) {
                loginListenerList.elementAt(i).loginRemoved(loginEvent);
            } else {
                throw new UnsupportedOperationException("Unknown login action "+loginEvent.getAction());
            }
        }
    }

    /**
     * Add a run to the contest data.
     */
    public Run addRun(Run run) {
        // TODO debug remove == 0 condition when addRun(SubmittedRun) is gone.
        if (run.getNumber() == 0) {
            run.setNumber(++runNumber);
        }
        runList.add(run);
        RunEvent runEvent = new RunEvent(RunEvent.Action.ADDED, run, null);
        fireRunListener(runEvent);
        return run;
    }

    public void addLogin(ClientId inClientId, ConnectionHandlerID connectionHandlerID) {
        loginList.add(inClientId, connectionHandlerID);
        LoginEvent loginEvent = new LoginEvent(inClientId, connectionHandlerID);
        fireLoginListener(loginEvent);
    }

    public void addLanguage(Language language) {
        languageDisplayList.add(language);
        languageList.add(language);
        LanguageEvent languageEvent = new LanguageEvent(LanguageEvent.Action.ADDED, language);
        fireLanguageListener(languageEvent);
    }

    public void addProblem(Problem problem) {
        problemDisplayList.add(problem);
        problemList.add(problem);
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.ADDED, problem);
        fireProblemListener(problemEvent);
    }

    /**
     * Accept Run.
     * 
     * On Server, adds run to run list, increments runnumber.
     * 
     */
    public SubmittedRun acceptRun(SubmittedRun submittedRun) {
        runNumber++;
        submittedRun.setNumber(runNumber);
        addRun(submittedRun);
        return submittedRun;
    }

    public void generateNewAccounts(String clientTypeName, int count, boolean active) {
        ClientType.Type type = ClientType.Type.valueOf(clientTypeName.toUpperCase());
        int numberAccounts = accountList.getAccounts(type, siteNumber).size();

        accountList.generateNewAccounts(type, count, PasswordType.JOE, siteNumber, active);

        for (int i = 0; i < count; i++) {
            ClientId nextClientId = new ClientId(siteNumber, type, 1 + i + numberAccounts);
            Account account = accountList.getAccount(nextClientId);
            AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.ADDED, account);
            fireAccountListener(accountEvent);
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
            } else {
                accountListenerList.elementAt(i).accountModified(accountEvent);
            }
        }
    }

    public Problem[] getProblems() {
        return problemDisplayList.getList();
    }

    public Language[] getLanguages() {
        return languageDisplayList.getList();
    }

    public ClientId getClientId() {
        return localClientId;
    }

    public void setClientId(ClientId clientId) {
        this.localClientId = clientId;
    }

    /**
     * Return frame class name.
     */
    public String getFrameName() {
        String typeName = localClientId.getClientType().toString();

        // TODO change this to a table lookup

        return typeName.charAt(0) + typeName.substring(1).toLowerCase() + "View";
    }

    public String getTitle() {
        String titleCase = localClientId.getClientType().toString();
        titleCase = titleCase.charAt(0) + titleCase.substring(1);
        return titleCase + " " + localClientId.getClientNumber() + " (Site " + localClientId.getSiteNumber() + ")";
    }

    public Run addRun(SubmittedRun submittedRun) {

        Language runLanguage = null;
        for (Language language : languageList.getList()) {
            if (language.getDisplayName().equals(submittedRun.getLanguageName())) {
                runLanguage = language;
            }
        }

        Problem runProblem = null;
        for (Problem problem : problemList.getList()) {
            if (problem.getDisplayName().equals(submittedRun.getProblemName())) {
                runProblem = problem;
            }
        }

        Run run = new Run(submittedRun.getClientId(), runLanguage, runProblem);
        run.setNumber(submittedRun.getNumber());

        addRun(run);
        return run;
    }

    public void addProblemListener(IProblemListener problemListener) {
        problemListenerList.addElement(problemListener);
    }

    public void removeProblemListener(IProblemListener problemListener) {
        problemListenerList.remove(problemListener);
    }

    public void addLanguageListener(ILanguageListener languageListener) {
        languageListenerList.addElement(languageListener);
    }

    public void removeLanguageListener(ILanguageListener languageListener) {
        languageListenerList.remove(languageListener);
    }

    public void addLoginListener(ILoginListener loginListener) {
        loginListenerList.addElement(loginListener);
    }

    public void removeLoginListener(ILoginListener loginListener) {
        loginListenerList.remove(loginListener);
    }

    public Run getRun(ElementId id) {
        return runList.get(id);
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
        return loginList.getClientId(connectionHandlerID);
    }

    public boolean isLoggedIn(ClientId sourceId) {
        return loginList.isLoggedIn(sourceId);
    }

    public ConnectionHandlerID getConnectionHandleID(ClientId sourceId) {
        return loginList.getConnectionHandleID(sourceId);
    }

    public void removeLogin(ClientId sourceId) {
        loginList.remove(sourceId);
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(int number) {
        this.siteNumber = number;
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

    public void startContest(int inSiteNumber) {
        ContestTime contestTime = getContestTime(inSiteNumber);
        if (contestTime != null) {
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CLOCK_STARTED, contestTime,
                    inSiteNumber);
            fireContestTimeListener(contestTimeEvent);
        } else {
            throw new SecurityException("Attempted to start clock site " + inSiteNumber);
        }
    }

    public void stopContest(int inSiteNumber) {
        ContestTime contestTime = getContestTime(inSiteNumber);
        if (contestTime != null) {
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CLOCK_STOPPED, contestTime,
                    inSiteNumber);
            fireContestTimeListener(contestTimeEvent);
        } else {
            throw new SecurityException("Attempted to stop clock site " + inSiteNumber);
        }
    }

    public void addContestTime(ContestTime contestTime, int inSiteNumber) {
        if (contestTime == null) {
            throw new IllegalArgumentException("contestTime is null");
        }
        contestTimeList.add(inSiteNumber, contestTime);
        if (contestTime != null) {
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.ADDED, contestTime, inSiteNumber);
            fireContestTimeListener(contestTimeEvent);
        } else {
            throw new SecurityException("Attempted to stop clock site " + inSiteNumber);
        }
    }

    public Enumeration<ClientId> getLoggedInClients(Type type) {
        return loginList.getClients(type);
    }

}
