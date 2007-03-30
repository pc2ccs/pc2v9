package edu.csus.ecs.pc2.core.model;

import java.util.Vector;

import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Specifies methods used to manipulate contest data.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IModel {

    /**
     * Add Run into TeamModel's data or receive run from Server.
     * 
     * @param submittedRun
     */
    void addRun(SubmittedRun submittedRun);
    
    void addLanguage (Language language);
    
    void addProblem (Problem problem);
    
    /**
     * Add a run into the contest data, return updated Submitted Run.
     * 
     * @param submittedRun
     * @return Submitted Run with id and timestamps
     */
    SubmittedRun acceptRun(SubmittedRun submittedRun) throws Exception;

    /**
     * Add new accounts.
     * 
     * @param clientTypeName
     *            name of client type, "team", "judge", etc.
     * @param count
     *            number of accounts to add
     * @param active
     *            set to True if the accounts are active
     */
    void generateNewAccounts(String clientTypeName, int count, boolean active);

    /**
     * Add a new account listener.
     * 
     * @param accountListener
     */
    void addAccountListener(IAccountListener accountListener);

    /**
     * Remove a account listener.
     * 
     * @param accountListener
     */
    void removeAccountListener(IAccountListener accountListener);

    /**
     * Fetch all defined problems.
     * 
     * @return array of Problem
     */
    Problem[] getProblems();

    /**
     * Fetch all defined languages.
     * 
     * @return array of Language
     */
    Language[] getLanguages();

    /**
     * The name of the class to display after login.
     * 
     * @return class name
     */
    String getFrameName();

    /**
     * get title for this logged in client.
     * 
     * @return
     */
    String getTitle();

    /**
     * 
     * @return the ClientId for the logged in client.
     */
    ClientId getClientId();

    void addRunListener(IRunListener runListener);

    void removeRunListener(IRunListener runListener);

    void addProblemListener(IProblemListener problemListener);

    void removeProblemListener(IProblemListener problemListener);

    void addLanguageListener(ILanguageListener languageListener);

    void removeLanguageListener(ILanguageListener languageListener);

    void addLoginListener(ILoginListener loginListener);

    void removeLoginListener(ILoginListener loginListener);
    
    Run getRun (ElementId id);
    
    Vector<Account> getAccounts(Type type, int siteNumber);
    
    Vector<Account> getAccounts(Type type);

}
