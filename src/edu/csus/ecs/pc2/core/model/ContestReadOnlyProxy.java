package edu.csus.ecs.pc2.core.model;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import edu.csus.ecs.pc2.core.exception.IllegalModelAccessException;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * 
 * @author pc2@ecs.csus.edu
 *
 */

// $HeadURL$
public final class ContestReadOnlyProxy implements IContest {
    private IContest theRealModel;
    
    public ContestReadOnlyProxy (IContest contest) {
        theRealModel = contest;
    }

    public void addLanguage(Language language) {
        try {
            throw new IllegalModelAccessException("addLanguage");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addProblem(Problem problem) {
        try {
            throw new IllegalModelAccessException("addProblem");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addContestTime(ContestTime contestTime) {
        try {
            throw new IllegalModelAccessException("addContestTime");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addJudgement(Judgement judgement) {
        try {
            throw new IllegalModelAccessException("addJudgement");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addSite(Site site) {
        try {
            throw new IllegalModelAccessException("addJudgement");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
   }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID) {
        try {
            throw new IllegalModelAccessException("connectionEstablished");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {
        try {
            throw new IllegalModelAccessException("connectionDropped");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void updateSite(Site site) {
        try {
            throw new IllegalModelAccessException("updateSite");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void updateLanguage(Language language) {
        try {
            throw new IllegalModelAccessException("updateLanguage");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void updateProblem(Problem problem) {
        try {
            throw new IllegalModelAccessException("updateProblem");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void updateContestTime(ContestTime contestTime, int inSiteNumber) {
        try {
            throw new IllegalModelAccessException("updateContestTime");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addAccount(Account account) {
        try {
            throw new IllegalModelAccessException("addAccount");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void updateContestTime(ContestTime contestTime) {
        try {
            throw new IllegalModelAccessException("updateContestTime");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void updateJudgement(Judgement judgement) {
        try {
            throw new IllegalModelAccessException("updateJudgement");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void changeSite(Site site) {
        try {
            throw new IllegalModelAccessException("changeSite");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void updateAccount(Account account) {
        try {
            throw new IllegalModelAccessException("updateAccount");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void startContest(int siteNumber) {
        try {
            throw new IllegalModelAccessException("startContest");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void stopContest(int siteNumber) {
        try {
            throw new IllegalModelAccessException("stopContest");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public Run acceptRun(Run submittedRun, RunFiles runFiles) {
        try {
            throw new IllegalModelAccessException("acceptRun");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRun(Run run) {
        try {
            throw new IllegalModelAccessException("addRun");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addRun(Run run, RunFiles runFiles, ClientId whoCheckedOutRunId) {
        try {
            throw new IllegalModelAccessException("addRun");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public Vector<Account> generateNewAccounts(String clientTypeName, int count, boolean active) {
        try {
            throw new IllegalModelAccessException("generateNewAccounts");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Vector<Account> generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active) {
        try {
            throw new IllegalModelAccessException("generateNewAccounts");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void generateNewSites(int count, boolean active) {
        try {
            throw new IllegalModelAccessException("generateNewSites");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addAccountListener(IAccountListener accountListener) {
        try {
            throw new IllegalModelAccessException("addAccountListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeAccountListener(IAccountListener accountListener) {
        try {
            throw new IllegalModelAccessException("removeAccountListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public Problem[] getProblems() {
        return theRealModel.getProblems() ;
    }

    public Judgement[] getJudgements() {
        return theRealModel.getJudgements();
    }

    public Site[] getSites() {
        return theRealModel.getSites();
    }

    public Language[] getLanguages() {
        return theRealModel.getLanguages();
    }

    public String getTitle() {
        return theRealModel.getTitle();
    }

    public ClientId getClientId() {
        return theRealModel.getClientId();
    }

    public void addRunListener(IRunListener runListener) {
        try {
            throw new IllegalModelAccessException("addRunListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeRunListener(IRunListener runListener) {
        try {
            throw new IllegalModelAccessException("removeRunListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addClarificationListener(IClarificationListener clarificationListener) {
        try {
            throw new IllegalModelAccessException("addClarificationListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeClarificationListener(IClarificationListener clarificationListener) {
        try {
            throw new IllegalModelAccessException("removeClarificationListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addProblemListener(IProblemListener problemListener) {
        try {
            throw new IllegalModelAccessException("addProblemListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeProblemListener(IProblemListener problemListener) {
        try {
            throw new IllegalModelAccessException("removeProblemListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addLanguageListener(ILanguageListener languageListener) {
        try {
            throw new IllegalModelAccessException("addLanguageListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeLanguageListener(ILanguageListener languageListener) {
        try {
            throw new IllegalModelAccessException("removeLanguageListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addLoginListener(ILoginListener loginListener) {
        try {
            throw new IllegalModelAccessException("addLoginListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeLoginListener(ILoginListener loginListener) {
        try {
            throw new IllegalModelAccessException("removeLoginListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addContestTimeListener(IContestTimeListener contestTimeListener) {
        try {
            throw new IllegalModelAccessException("addContestTimeListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeContestTimeListener(IContestTimeListener contestTimeListener) {
        try {
            throw new IllegalModelAccessException("removeContestTimeListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addJudgementListener(IJudgementListener judgementListener) {
        try {
            throw new IllegalModelAccessException("addJudgementListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeJudgementListener(IJudgementListener judgementListener) {
        try {
            throw new IllegalModelAccessException("removeJudgementListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addSiteListener(ISiteListener siteListener) {
        try {
            throw new IllegalModelAccessException("addSiteListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeSiteListener(ISiteListener siteListener) {
        try {
            throw new IllegalModelAccessException("removeSiteListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void addConnectionListener(IConnectionListener connectionListener) {
        try {
            throw new IllegalModelAccessException("addConnectionListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeConnectionListener(IConnectionListener connectionListener) {
        try {
            throw new IllegalModelAccessException("removeConnectionListener");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public Run getRun(ElementId id) {
        return theRealModel.getRun(id);
    }

    public Vector<Account> getAccounts(Type type, int siteNumber) {
        return theRealModel.getAccounts(type, siteNumber);
    }

    public Vector<Account> getAccounts(Type type) {
        return theRealModel.getAccounts(type);
    }

    public Site getSite(int siteNumber) {
        return theRealModel.getSite(siteNumber);
    }

    public boolean isValidLoginAndPassword(ClientId clientId, String password) {
        return theRealModel.isValidLoginAndPassword(clientId, password);
    }

    public void addLogin(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        try {
            throw new IllegalModelAccessException("addLogin");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public ClientId getLoginClientId(ConnectionHandlerID connectionHandlerID) {
        return theRealModel.getLoginClientId(connectionHandlerID);
    }

    public boolean isLocalLoggedIn(ClientId sourceId) {
        return theRealModel.isLocalLoggedIn(sourceId);
    }

    public boolean isLoggedIn() {
        return theRealModel.isLoggedIn();
    }

    public ConnectionHandlerID getConnectionHandleID(ClientId clientId) {
        return theRealModel.getConnectionHandleID(clientId);
    }

    public void removeLogin(ClientId clientId) {
        try {
            throw new IllegalModelAccessException("removeLogin");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public int getSiteNumber() {
        return theRealModel.getSiteNumber();
    }

    public ContestTime getContestTime() {
        return theRealModel.getContestTime();
    }

    public ContestTime getContestTime(int siteNumber) {
        return theRealModel.getContestTime(siteNumber);
    }

    public void setClientId(ClientId clientId) {
        try {
            throw new IllegalModelAccessException("setClientId");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void setSiteNumber(int number) {
        try {
            throw new IllegalModelAccessException("setSiteNumber");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public Enumeration<ClientId> getLoggedInClients(Type type) {
        return theRealModel.getLoggedInClients(type);
    }

    public void loginDenied(ClientId clientId, ConnectionHandlerID connectionHandlerID, String message) {
        try {
            throw new IllegalModelAccessException("loginDenied");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void initializeStartupData() {
        try {
            throw new IllegalModelAccessException("initializeWithFakeData");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public Run[] getRuns() {
        return theRealModel.getRuns();
    }

    public void runUpdated(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoUpdatedRun) {
        try {
            throw new IllegalModelAccessException("runUpdated");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void runNotAvailable(Run run) {
        try {
            throw new IllegalModelAccessException("runNotAvailable");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void updateRun(Run run, RunStates newState, ClientId whoChangedRun) {
        try {
            throw new IllegalModelAccessException("updateRun");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public RunFiles getRunFiles(Run run) {
        return theRealModel.getRunFiles(run);
    }

    public void addRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId judgeId) {
        try {
            throw new IllegalModelAccessException("addRunJudgement");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void cancelRunCheckOut(Run run, ClientId fromId) {
        try {
            throw new IllegalModelAccessException("cancelRunCheckOut");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public ClientId getRunCheckedOutBy(Run run) {
        return theRealModel.getRunCheckedOutBy(run);
    }

    public void availableRun(Run run) {
        try {
            throw new IllegalModelAccessException("availableRun");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public Clarification[] getClarifications() {
        return theRealModel.getClarifications();
    }

    public void addClarification(Clarification clarification) {
        try {
            throw new IllegalModelAccessException("addClarification");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public Clarification acceptClarification(Clarification clarification) {
        try {
            throw new IllegalModelAccessException("acceptClarification");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeClarification(Clarification clarification) {
        try {
            throw new IllegalModelAccessException("removeClarification");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public void changeClarification(Clarification clarification) {
        try {
            throw new IllegalModelAccessException("changeClarification");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public Language getLanguage(ElementId elementId) {
        return theRealModel.getLanguage(elementId);
    }

    public Problem getProblem(ElementId elementId) {
        return theRealModel.getProblem(elementId);
    }

    public Judgement getJudgement(ElementId elementId) {
        return theRealModel.getJudgement(elementId);
    }

    public Account getAccount(ClientId id) {
        return theRealModel.getAccount(id);
    }

    public ContestTime[] getContestTimes() {
        return theRealModel.getContestTimes();
    }

    public ConnectionHandlerID[] getConnectionHandleIDs() {
        return theRealModel.getConnectionHandleIDs();
    }

    public ContestTime getContestTime(ElementId elementId) {
        return theRealModel.getContestTime(elementId);
    }

    public void addConnectionHandlerID(ConnectionHandlerID connectionHandlerID) {
        // TODO Auto-generated method stub
        
    }

    public void addConnectionHandlerID(ConnectionHandlerID connectionHandlerID, Date connectDate) {
        try {
            throw new IllegalModelAccessException("addConnectionHandlerID");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
    }

    public ConnectionHandlerID[] getConnectionHandlerIDs() {
        try {
            throw new IllegalModelAccessException("getConnectionHandlerIDs");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeConnectionHandlerID(ConnectionHandlerID connectionHandlerID) {
        try {
            throw new IllegalModelAccessException("removeConnectionHandlerID");
        } catch (IllegalModelAccessException e) {
            e.printStackTrace();
        }
        
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID, Date connectDate) {
        // TODO Auto-generated method stub
        
    }

    public Clarification[] getClarifications(ClientId clientId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Run[] getRuns(ClientId clientId) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        // TODO Auto-generated method stub
        
    }

    public void updateProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        // TODO Auto-generated method stub
        
    }

    public ProblemDataFiles getProblemDataFiles(Problem problem) {
        // TODO Auto-generated method stub
        return null;
    }

    public ProblemDataFiles getProblemDataFile(Problem problem) {
        // TODO Auto-generated method stub
        return null;
    }

    public ProblemDataFiles[] getProblemDataFiles() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isRemoteLoggedIn(ClientId clientId) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isConnected(ConnectionHandlerID connectionHandlerID) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isConnectedToRemoteSite(ConnectionHandlerID connectionHandlerID) {
        // TODO Auto-generated method stub
        return false;
    }

    public void updateRun(Run run, ClientId whoChangedRun) {
        // TODO Auto-generated method stub
    }

    public Enumeration<ClientId> getLocalLoggedInClients(Type type) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addLocalLogin(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        // TODO Auto-generated method stub
        
    }

    public void addRemoteLogin(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        // TODO Auto-generated method stub
        
    }

    public Clarification getClarification(ElementId id) {
        // TODO Auto-generated method stub
        return null;
    }

    public void initializeSubmissions() {
        // TODO Auto-generated method stub
        
    }

    public void answerClarification(Clarification clarification, String answer, ClientId whoAnsweredIt, boolean sendToAll) {
        // TODO Auto-generated method stub
        
    }

    public void cancelClarificationCheckOut(Clarification clarification, ClientId whoCancelledIt) {
        // TODO Auto-generated method stub
        
    }

    public void addClarification(Clarification clarification, ClientId whoCheckedOutId) {
        // TODO Auto-generated method stub
        
    }

    public void updateClarification(Clarification clarification, ClientId whoChangedIt) {
        // TODO Auto-generated method stub
        
    }

    public void addClientSettings(ClientSettings clientSettings) {
        // TODO Auto-generated method stub
        
    }

    public void updateClientSettings(ClientSettings clientSettings) {
        // TODO Auto-generated method stub
        
    }

    public void addClientSettingsListener(IClientSettingsListener clientSettingsListener) {
        // TODO Auto-generated method stub
        
    }

    public void removeClientSettingsListener(IClientSettingsListener clientSettingsListener) {
        // TODO Auto-generated method stub
        
    }

    public void addContestInformation(ContestInformation contestInformation) {
        // TODO Auto-generated method stub
        
    }

    public void updateContestInformation(ContestInformation contestInformation) {
        // TODO Auto-generated method stub
        
    }

    public void addContestInformationListener(IContestInformationListener contestInformationListener) {
        // TODO Auto-generated method stub
        
    }

    public void removeContestInformationListener(IContestInformationListener contestInformationListener) {
        // TODO Auto-generated method stub
        
    }

    public ContestInformation getContestInformation() {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientSettings getClientSettings() {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientSettings getClientSettings(ClientId clientId) {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientSettings[] getClientSettingsList() {
        // TODO Auto-generated method stub
        return null;
    }

}
