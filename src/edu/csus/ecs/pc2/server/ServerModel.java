package edu.csus.ecs.pc2.server;

import java.util.Vector;

import edu.csus.ecs.pc2.core.Account;
import edu.csus.ecs.pc2.core.AccountEvent;
import edu.csus.ecs.pc2.core.AccountList;
import edu.csus.ecs.pc2.core.AccountListener;
import edu.csus.ecs.pc2.core.ClientId;
import edu.csus.ecs.pc2.core.ClientType;
import edu.csus.ecs.pc2.core.IModel;
import edu.csus.ecs.pc2.core.RunEvent;
import edu.csus.ecs.pc2.core.RunListener;
import edu.csus.ecs.pc2.core.SubmittedRun;
import edu.csus.ecs.pc2.core.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.RunEvent.Action;

/**
 * Represents the collection of contest server data.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ServerModel implements IModel {

    public static final String SVN_ID = "$Id$";

    private int runNumber = 0;

    private Vector<RunListener> runListenterList = new Vector<RunListener>();

    private Vector<SubmittedRun> runList = new Vector<SubmittedRun>();

    private AccountList accountList = new AccountList();
    
    private Vector<AccountListener> accountListenerList = new Vector<AccountListener>();

    private int siteNumber = 1;

    public SubmittedRun acceptRun(SubmittedRun submittedRun) {

        runNumber++;
        submittedRun.setNumber(runNumber);
        addRun(submittedRun);
        return submittedRun;
    }

    public void addRunListener(RunListener runListener) {
        runListenterList.addElement(runListener);

    }

    public void removeRunListener(RunListener runListener) {
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

    public void addRun(SubmittedRun submittedRun) {
        runList.addElement(submittedRun);
        RunEvent runEvent = new RunEvent(Action.ADDED, submittedRun);
        fireRunListener(runEvent);
    }
    
    private void fireAccountListener (AccountEvent accountEvent) {
        for (int i = 0; i < accountListenerList.size(); i++) {

            if (accountEvent.getAction() == AccountEvent.Action.ADDED) {
                accountListenerList.elementAt(i).accountAdded(accountEvent);
            } else {
                accountListenerList.elementAt(i).accountModified(accountEvent);
            }
        }
    }
    
    
    public void addAccountListener (AccountListener accountListener) {
        accountListenerList.addElement(accountListener);

    }

    public void removeAccountListener(AccountListener accountListener) {
        accountListenerList.removeElement(accountListener);
    }


    public void generateNewAccounts(String clientTypeName, int count, boolean active) {
        ClientType.Type type = ClientType.Type.valueOf(clientTypeName.toUpperCase());
        int numberAccounts = accountList.getAccounts(type, siteNumber).size();
        
        accountList.generateNewAccounts(type, count, PasswordType.JOE, siteNumber, active);
        

        for (int i = 0; i < count; i++) {
            ClientId clientId = new ClientId(siteNumber,type, 1 + i + numberAccounts);
            Account account = accountList.getAccount(clientId);
            AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.ADDED, account);
            fireAccountListener (accountEvent);
        }
    }
}
