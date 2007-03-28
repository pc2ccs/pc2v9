package edu.csus.ecs.pc2.core.model;


/**
 * Specifies methods used to manipulate contest data.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IModel {

    void addRunListener(RunListener runListener);

    void removeRunListener(RunListener runListener);

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
     * @return
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
    void addAccountListener(AccountListener accountListener);

    /**
     * Remove a account listener.
     * 
     * @param accountListener
     */
    void removeAccountListener(AccountListener accountListener);
    
    /**
     * Fetch all defined problems.
     * @return array of Problem
     */
    Problem [] getProblems();
    
    /**
     * Fetch all defined languages.
     * @return array of Language
     */
    Language [] getLanguages();

}
