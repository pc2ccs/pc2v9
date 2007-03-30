package edu.csus.ecs.pc2.core.model;

/**
 * Specifies methods used to manipulate contest data.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IModel {

    void addRunListener(IRunListener runListener);

    void removeRunListener(IRunListener runListener);

    /**
     * Add Run into TeamModel's data or receive run from Server.
     * 
     * @param submittedRun
     */
    void addRun(SubmittedRun submittedRun);

    /**
     * Add a run into the contest data, return updated Submitted Run.
     * 
     * @param submittedRun
     * @return Sumitted Run with id and timestamps
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
    
    String getTitle();
    
    /**
     * 
     * @return the ClientId for the logged in client.
     */
    ClientId getClientId();

}
