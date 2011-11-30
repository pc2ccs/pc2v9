package edu.csus.ecs.pc2.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * Contest Loader.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestImporter {
    
    /**
     * Load contest information to the server.
     * 
     * Updates the current contest with data from the input contest.
     * 
     * @param theController
     * @param inContest
     */
    public void sendContestSettingsToServer(IInternalController theController, IInternalContest theContest, IInternalContest inContest) {
        
        // TODO CCS validate incoming contest data
        // validation has been done
        // in the past via UI or other code, invalid data should be be allowed
        // to be added into the contest.
        
        // TODO CCS move all merging code outside of sendContestSettingsToServer method, this is
        // _send_ to server not merge then send to server.
        
        if (inContest.getContestInformation().getContestTitle() != null) {
            ContestInformation contestInformation = inContest.getContestInformation();
            contestInformation.setContestTitle(inContest.getContestInformation().getContestTitle());

            theController.updateContestInformation(contestInformation);
            // TODO CCS update contest title
        }

        // TODO CCS need to update all properties from YAML contest header
        // TODO CCS update contest length
        // TODO CCS update contest freeze time

        for (Site site : inContest.getSites()) {
            Site existingSite = theContest.getSite(site.getSiteNumber());
            if (existingSite != null) {
                Site updatedSite = existingSite.clone();
                if (!site.isSameAs(updatedSite)) {
                    updatedSite.setDisplayName(site.getDisplayName());
                    updatedSite.setPassword(site.getPassword());
                    updatedSite.setConnectionInfo(site.getConnectionInfo());
                    // TODO this field is not in use...
                    // TODO CCS what does "not in use" mean specifically?
                    /**
                     * The ContestLoader loads these value, if they are not being loaded <br>
                     * (if that is what is meant by "no used") then it should be because <br>
                     * having invalid (aka missing) site connection is invalid and will <br>
                     * cause exceptions elsewhere when the invalid data is processed.  No <br>
                     * code may be in place to report this error. 
                     */
                    updatedSite.setConnectionDisplayInfo(site.getConnectionDisplayInfo());
                    theController.updateSite(updatedSite);
                }
            } else {
                theController.addNewSite(site);
            }
        }

        HashMap<String, Language> langHash = new HashMap<String, Language>();
        for (Language existingLanguage : theContest.getLanguages()) {
            if (existingLanguage.isActive()) {
                langHash.put(existingLanguage.getDisplayName(), existingLanguage);
            }
        }
        for (Language language : inContest.getLanguages()) {
            if (langHash.containsKey(language.getDisplayName())) {
                Language newLanguage = langHash.get(language.getDisplayName());
                if (!newLanguage.isSameAs(language)) {
                    newLanguage.setCompileCommandLine(language.getCompileCommandLine());
                    newLanguage.setExecutableIdentifierMask(language.getExecutableIdentifierMask());
                    newLanguage.setProgramExecuteCommandLine(language.getProgramExecuteCommandLine());
                    theController.updateLanguage(newLanguage);
                }
            } else {
                theController.addNewLanguage(language);
            }
        }
        
        // TODO CCS design decision, de facto augment/ensure accounts, there should be an option to overwrite

        
        // account yaml lacks detail, so all we are doing here is ensuring
        // that the accounts exist.
        HashMap<String,Account> accountHash = new HashMap<String, Account>();
        for (Account existingAccount : theContest.getAccounts()) {
            accountHash.put(existingAccount.getClientId().toString(), existingAccount);
        }
        Vector<Account> addAccountsVector = new Vector<Account>();
        for (Account account : inContest.getAccounts()) {
            if (!accountHash.containsKey(account.getClientId().toString())) {
                addAccountsVector.add(account);
            }
        }
        if (addAccountsVector.size() > 0) {
            theController.addNewAccounts(addAccountsVector.toArray(new Account[addAccountsVector.size()]));
        }
        
        // XXX it does not appear that PacketHandler looks at Categories.
        // TODO CCS Add categories, add updateSettings to be a more
        // generic way to update multiple settings
        Category[] existingCategories = theContest.getCategories();
        HashMap<String,Category> cats = new HashMap<String, Category>();
        for (int i = 0; i < existingCategories.length; i++) {
            Category category = existingCategories[i];
            cats.put(category.getDisplayName(), category);
        }
        Category [] categories = inContest.getCategories();
        Vector<Category> catAdds = new Vector<Category>();
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            if (!cats.containsKey(category.getDisplayName())) {
                catAdds.add(category);
            }
        }
        theController.updateCategories((Category[])catAdds.toArray(new Category[catAdds.size()]));
        
        // TODO CCS design decision, de facto augment/ensure problems, there should be an option to overwrite

        HashMap<String, Problem> probHash = new HashMap<String, Problem>();
        for (Problem existingProblem : theContest.getProblems()) {
            if (existingProblem.isActive()) {
                probHash.put(existingProblem.getDisplayName(), existingProblem);
            }
        }
        
        ArrayList<Problem> problemsToAddList = new ArrayList<Problem> ();
        
        for (Problem problem : inContest.getProblems()) {
            if (probHash.containsKey(problem.getDisplayName())) {
                try {
                    Problem newProblem = probHash.get(problem.getDisplayName());
                    if (!newProblem.isSameAs(problem)) {
                        updateProblemFields (newProblem, problem);
                    }
                    problemsToAddList.add(newProblem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        
            } else {
                problemsToAddList.add(problem);
            }
        }
        
        if (problemsToAddList.size()> 0){
            Problem [] list = new Problem[problemsToAddList.size()];
            problemsToAddList.toArray(list);
            ProblemDataFiles [] datafiles = new ProblemDataFiles[list.length];
            int idx = 0;
            for (Problem problem : list ){
                datafiles[idx] = inContest.getProblemDataFile(problem);
                idx ++;
            }
            theController.addNewProblem(list, datafiles);
        }
       
        // TODO CCS redesign/write this method, validate at top, add to contest (via controller) at bottom
        /**
         * Only send to server via controller when all fields are validated, aka no Exceptions. <br>
         * Move all theController down to bottom of this method. <br>
         * Create a single packet to update/add on server.
         */
        
    }

    private void updateProblemFields(Problem newProblem, Problem problem) throws Exception {
        newProblem.setAnswerFileName(problem.getAnswerFileName());
        newProblem.setComputerJudged(problem.isComputerJudged());
        newProblem.setDataFileName(problem.getDataFileName());
        newProblem.setHideOutputWindow(problem.isHideOutputWindow());
        newProblem.setIgnoreSpacesOnValidation(problem.isIgnoreSpacesOnValidation());
        newProblem.setInternationalJudgementReadMethod(problem.isInternationalJudgementReadMethod());
        newProblem.setManualReview(problem.isManualReview());
        newProblem.setPrelimaryNotification(problem.isPrelimaryNotification());
        newProblem.setReadInputDataFromSTDIN(problem.isReadInputDataFromSTDIN());
        newProblem.setShortName(problem.getShortName());
        newProblem.setShowCompareWindow(problem.isShowCompareWindow());
        newProblem.setShowValidationToJudges(problem.isShowValidationToJudges());
        newProblem.setTimeOutInSeconds(problem.getTimeOutInSeconds());
        newProblem.setUsingPC2Validator(problem.isUsingPC2Validator());
        newProblem.setValidatedProblem(problem.isValidatedProblem());
        newProblem.setValidatorCommandLine(problem.getValidatorCommandLine());
        newProblem.setValidatorProgramName(problem.getValidatorProgramName());
        newProblem.setWhichPC2Validator(problem.getWhichPC2Validator());
    }
}
