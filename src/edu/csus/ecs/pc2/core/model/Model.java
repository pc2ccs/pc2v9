package edu.csus.ecs.pc2.core.model;

import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.LanguageDisplayList;
import edu.csus.ecs.pc2.core.list.LanguageList;
import edu.csus.ecs.pc2.core.list.ProblemDisplayList;
import edu.csus.ecs.pc2.core.list.ProblemList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class Model implements IModel {

    public static final String SVN_ID = "$Id$";

    private ClientId clientId = null;

    private Vector<IRunListener> runListenterList = new Vector<IRunListener>();
    
    private Vector<IProblemListener> problemListenerList = new Vector <IProblemListener>();

    private Vector<SubmittedRun> runList = new Vector<SubmittedRun>();

    private AccountList accountList = new AccountList();

    private Vector<IAccountListener> accountListenerList = new Vector<IAccountListener>();
    
    private int runNumber = 0;

    private int siteNumber = 1;

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

        String[] probNames = { "Sum of Squares", "Sumit", "Hello", "GoodBye" };
        Problem problem = new Problem("None Selected");
        
        problemDisplayList.add(problem);
        problemList.add(problem);

        for (String problemNames : probNames) {
            problem = new Problem(problemNames);
            problemDisplayList.add(problem);
            problemList.add(problem);
        }

        String[] langNames = { "Java", "BASIC", "C++", "ANSI C", "APL" };
        Language language = new Language("None Selected");

        languageDisplayList.add(language);
        languageList.add(language);

        for (String languageName : langNames) {
            language = new Language(languageName);
            languageList.add(language);
            languageDisplayList.add(language);
        }
    }

    public void addRunListener(IRunListener runListener) {
        runListenterList.addElement(runListener);
    }

    public void removeRunListener(IRunListener runListener) {
        runListenterList.removeElement(runListener);
    }

    private void fireRunListener(RunEvent runEvent) {
        for (int i = 0; i < runListenterList.size(); i++) {

            if (runEvent.getAction() == Action.ADDED) {
                runListenterList.elementAt(i).runAdded(runEvent);
            } else if (runEvent.getAction() == Action.DELETED) {
                runListenterList.elementAt(i).runRemoved(runEvent);
            } else {
                runListenterList.elementAt(i).runChanged(runEvent);
            }
        }
    }
    

    private void fireProblemListener(ProblemEvent problemEvent) {
        for (int i = 0; i < runListenterList.size(); i++) {

            if (problemEvent.getAction() == ProblemEvent.Action.ADDED) {
                problemListenerList.elementAt(i).problemAdded(problemEvent);
            } else if (problemEvent.getAction() == ProblemEvent.Action.DELETED) {
                problemListenerList.elementAt(i).problemRemoved(problemEvent);
            } else {
                problemListenerList.elementAt(i).problemChanged(problemEvent);
            }
        }
        // TODO Auto-generated method stub
        
    }


    /**
     * Add a run to the contest data.
     */
    public void addRun(SubmittedRun submittedRun) {
        runList.addElement(submittedRun);
        RunEvent runEvent = new RunEvent(Action.ADDED, submittedRun);
        fireRunListener(runEvent);
    }
    
    public void addProblem (Problem problem){
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
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    /**
     * Return frame class name.
     */
    public String getFrameName() {
        String typeName = clientId.getClientType().toString();
        
        // TODO change this to a table lookup

        return typeName.charAt(0) + typeName.substring(1).toLowerCase() + "View";
    }

    public String getTitle() {
        String titleCase = clientId.getClientType().toString();
        titleCase = titleCase.charAt(0) + titleCase.substring(1);
        return titleCase + " "+clientId.getClientNumber()+" (Site "+clientId.getSiteNumber()+")";
    }

}
