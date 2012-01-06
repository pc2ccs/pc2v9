package edu.csus.ecs.pc2.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import edu.csus.ecs.pc2.api.exceptions.LoadContestDataException;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * Send (Import) Contest settings to server.
 * 
 * Sends packets to server with contest configuration information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestImporter {

    private NoteList noteList = new NoteList();

    /**
     * Load contest information to the server.
     * 
     * Updates the current contest with data from the input contest.
     * 
     * If an Exception is thrown then a list of the errors can be retrieved
     * suing {@link #getNoteList()}.
     * 
     * @param theController
     * @param inContest
     * @throws LoadContestDataException on exception {@link #getNoteList()} will have errors as well. 
     */
    public void sendContestSettingsToServer(IInternalController theController, IInternalContest theContest, IInternalContest inContest) throws LoadContestDataException { 

        // TODO CCS validate incoming contest data
        
        // validation has been done in the past via UI or other code, invalid data should be be allowed
        // to be added into the contest.

        // TODO CCS move all merging code outside of sendContestSettingsToServer method, this is
        // _send_ to server not merge then send to server.
        
        // TODO CCS need to update all properties from YAML contest header
        // TODO CCS update contest length
        // TODO CCS update contest freeze time


        ContestInformation contestInformation = null;

        try {
            if (inContest.getContestInformation().getContestTitle() != null) {
                contestInformation = inContest.getContestInformation();
                contestInformation.setContestTitle(inContest.getContestInformation().getContestTitle());
            }

        } catch (Exception e) {
            noteList.logError("Error saving Contest Information", e);
        }


        try {
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
                         * cause exceptions elsewhere when the invalid data is processed. No <br>
                         * code may be in place to report this error.
                         */
                        updatedSite.setConnectionDisplayInfo(site.getConnectionDisplayInfo());
                        theController.updateSite(updatedSite);
                    }
                } else {
                    theController.addNewSite(site);
                }
            }

        } catch (Exception e) {
            noteList.logError("Error saving Site Information", e);
        }

        try {
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

        } catch (Exception e) {
            noteList.logError("Error saving Language Information", e);
        }

        // TODO CCS design decision, de facto augment/ensure accounts, there should be an option to overwrite

        // account yaml lacks detail, so all we are doing here is ensuring
        // that the accounts exist.

        Vector<Account> addAccountsVector = new Vector<Account>();

        try {
            HashMap<String, Account> accountHash = new HashMap<String, Account>();
            for (Account existingAccount : theContest.getAccounts()) {
                accountHash.put(existingAccount.getClientId().toString(), existingAccount);
            }
            for (Account account : inContest.getAccounts()) {
                if (!accountHash.containsKey(account.getClientId().toString())) {
                    addAccountsVector.add(account);
                }
            }

        } catch (Exception e) {
            noteList.logError("Error saving Account Information", e);
        }

        // XXX it does not appear that PacketHandler looks at Categories.
        
        // TODO CCS Add categories, add updateSettings to be a more
        // generic way to update multiple settings

        try {
            Category[] existingCategories = theContest.getCategories();
            HashMap<String, Category> cats = new HashMap<String, Category>();
            for (int i = 0; i < existingCategories.length; i++) {
                Category category = existingCategories[i];
                cats.put(category.getDisplayName(), category);
            }
            Category[] categories = inContest.getCategories();
            Vector<Category> catAdds = new Vector<Category>();
            for (int i = 0; i < categories.length; i++) {
                Category category = categories[i];
                if (!cats.containsKey(category.getDisplayName())) {
                    catAdds.add(category);
                }
            }
            theController.updateCategories((Category[]) catAdds.toArray(new Category[catAdds.size()]));

            // TODO CCS design decision, de facto augment/ensure problems, there should be an option to overwrite

        } catch (Exception e) {
            noteList.logError("Error saving Category Information", e);
        }

        Problem[] problemList = new Problem[0];
        ProblemDataFiles[] problemDataFiles = null;

        try {
            HashMap<String, Problem> probHash = new HashMap<String, Problem>();
            for (Problem existingProblem : theContest.getProblems()) {
                if (existingProblem.isActive()) {
                    probHash.put(existingProblem.getDisplayName(), existingProblem);
                }
            }

            ArrayList<Problem> problemsToAddList = new ArrayList<Problem>();

            for (Problem problem : inContest.getProblems()) {
                if (probHash.containsKey(problem.getDisplayName())) {
                    try {
                        Problem newProblem = probHash.get(problem.getDisplayName());
                        if (!newProblem.isSameAs(problem)) {
                            updateProblemFields(newProblem, problem);
                        }
                        problemsToAddList.add(newProblem);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    problemsToAddList.add(problem);
                }
            }

            if (problemsToAddList.size() > 0) {
                problemList = new Problem[problemsToAddList.size()];
                problemsToAddList.toArray(problemList);
                problemDataFiles = new ProblemDataFiles[problemList.length];
                int idx = 0;
                for (Problem problem : problemList) {
                    problemDataFiles[idx] = inContest.getProblemDataFile(problem);
                    idx++;
                }
            }

        } catch (Exception e) {
            noteList.logError("Error saving Problem Information", e);
        }
        
        ClientSettings [] settings = inContest.getClientSettingsList();
        
        PlaybackInfo[] infos = inContest.getPlaybackInfos();
        PlaybackInfo playbackInfo = null;
        if (infos.length > 0) {
            playbackInfo = infos[0];
        }
        
        if (noteList.size() > 0){
            /**
             * If there are elements in noteList that means there has been
             * one or more Exceptions during the preparing of the data to load
             * into the contest.
             */
            
            NoteMessage noteMessage = noteList.getAll()[0];
            throw new LoadContestDataException(noteList.size()+" errors in loading contest configuration data, "+noteMessage.getComment());
            
        }

        /**
         * Send/update contest information
         */

        try {

            if (contestInformation != null) {
                theController.updateContestInformation(contestInformation);
            }

            if (problemList.length > 0) {
                theController.addNewProblem(problemList, problemDataFiles);
            }

            if (addAccountsVector.size() > 0) {
                theController.addNewAccounts(addAccountsVector.toArray(new Account[addAccountsVector.size()]));
            }
            
            if (settings.length > 0) {
                Thread.sleep(2000);  // kludge wait for accounts to be created on server
//                System.out.println("debug 22 - ContestImporer there are "+settings.length+" settings found");
                for (ClientSettings setting : settings) {
//                    dumpAJSettings(setting.getClientId(), setting.isAutoJudging(), setting.getAutoJudgeFilter());
                    theController.addNewClientSettings(setting);
                }
            }

            if (playbackInfo != null) {
                Thread.sleep(2000);  // kludge wait for accounts and auto judge settings
                theController.startPlayback(playbackInfo);
            }
            
        } catch (Exception e) {
            noteList.logError("Error storing configuration Information", e);
            throw new LoadContestDataException(noteList.size()+" errors in sending contest configuration data");
        }
    }

    // TODO 669 remove after debugged
    @SuppressWarnings("unused")
    private void dumpAJSettings(ClientId clientId, boolean autoJudging, Filter autoJudgeFilter) {
        ElementId[] ids = autoJudgeFilter.getProblemIdList();
        System.out.println("-----------------------------------------------------------------");
        System.out.println("debug 22 Auto Judge for " + clientId + "  " + ids.length + " problems, aj on = " + autoJudging);

        System.out.print("debug 22     sent      ");
        for (ElementId id : ids) {
            System.out.print(id + " ");
        }
        System.out.println();
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

    /**
     * Return list of notes/errors.
     * 
     * 
     * 
     * @return list of notes/errors
     */
    public NoteList getNoteList() {
        return noteList;
    }

}

