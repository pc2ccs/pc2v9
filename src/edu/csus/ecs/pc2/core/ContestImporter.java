package edu.csus.ecs.pc2.core;

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
        // TODO CCS

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
                    // updatedSite.setConnectionDisplayInfo(site.getConnectionDisplayInfo());
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

        HashMap<String, Problem> probHash = new HashMap<String, Problem>();
        for (Problem existingProblem : theContest.getProblems()) {
            if (existingProblem.isActive()) {
                probHash.put(existingProblem.getDisplayName(), existingProblem);
            }
        }
        for (Problem problem : inContest.getProblems()) {
            ProblemDataFiles problemDataFiles = inContest.getProblemDataFile(problem);
            if (probHash.containsKey(problem.getDisplayName())) {
                Problem newProblem = probHash.get(problem.getDisplayName());
                if (!newProblem.isSameAs(problem)) {
                    newProblem.setAnswerFileName(problem.getAnswerFileName());
                    newProblem.setComputerJudged(problem.isComputerJudged());
                    newProblem.setDataFileName(problem.getDataFileName());
                    newProblem.setHideOutputWindow(problem.isHideOutputWindow());
                    newProblem.setIgnoreSpacesOnValidation(problem.isIgnoreSpacesOnValidation());
                    newProblem.setInternationalJudgementReadMethod(problem.isInternationalJudgementReadMethod());
                    newProblem.setManualReview(problem.isManualReview());
                    newProblem.setPrelimaryNotification(problem.isPrelimaryNotification());
                    newProblem.setReadInputDataFromSTDIN(problem.isReadInputDataFromSTDIN());
                    try {
                        newProblem.setShortName(problem.getShortName());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    newProblem.setShowCompareWindow(problem.isShowCompareWindow());
                    newProblem.setShowValidationToJudges(problem.isShowValidationToJudges());
                    newProblem.setTimeOutInSeconds(problem.getTimeOutInSeconds());
                    newProblem.setUsingPC2Validator(problem.isUsingPC2Validator());
                    newProblem.setValidatedProblem(problem.isValidatedProblem());
                    newProblem.setValidatorCommandLine(problem.getValidatorCommandLine());
                    newProblem.setValidatorProgramName(problem.getValidatorProgramName());
                    newProblem.setWhichPC2Validator(problem.getWhichPC2Validator());
                }
                if (problemDataFiles != null) {
                    theController.updateProblem(newProblem, problemDataFiles);
                } else {
                    theController.updateProblem(newProblem);
                }
            } else {
                if (problemDataFiles != null) {
                    theController.addNewProblem(problem, problemDataFiles);
                } else {
                    theController.addProblem(problem);
                }
            }
        }
        
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

    }
}
