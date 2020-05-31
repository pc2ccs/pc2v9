// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IFile;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * This class is used to submit runs, obtained from a remote CCS being shadowed by this instance of PC2, 
 * to the local PC2 server.
 * 
 * The class is instantiated with a PC2 Controller ({@link IInternalController}) which it uses to submit
 * the run to the local server.
 * 
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class RemoteRunSubmitter {

    private IInternalController controller;

    private IInternalContest contest;

    private int thisSiteNumber;

    public RemoteRunSubmitter(IInternalController controller) {
        this.controller = controller;
        this.contest = controller.getContest();
        this.thisSiteNumber = contest.getSiteNumber();
    }

    /**
     * Submit a run to the server.
     * 
     * @param clientIdString - "teamN" client name
     * @param problemID - problem id
     * @param languageID - CLICS language id
     * @param mainFile - main file name
     * @param auxFiles - other submittted files 
     * @param overrideTimeMS - override submission time in MS
     * @param overrideRunId - override run id
     */
    public void submitRun(String clientIdString, String problemID, String languageID, IFile mainFile, List<IFile> auxFiles, long overrideTimeMS, long overrideRunId) {

        isEmptyString(clientIdString, "ClientId parameter is null");
        isEmptyString(problemID, "Parameter problemID is null or empty");
        isEmptyString(languageID, "Parameter languageID is null or empty");
        isEmpty(mainFile, "Parameter mainFile is null");

        if (mainFile == null)
        {
            throw new InvalidParameterException("Parameter mainFile is null");
        }

        if (overrideRunId <= 0)
        {
            throw new InvalidParameterException("Parameter overrideRunId has invalid id.  " + overrideRunId + " <= 0");
        }

        /**
         * Verify input fields
         */

        if (contest == null) {
            throw new RuntimeException("contest field is null");
        }

        Account account = getSubmitterId(contest, clientIdString.trim());
        if (account == null) {
            throw new InvalidParameterException("Parameter clientIdString does not match any pc2 account: '" + clientIdString + "'");
        }
        ClientId submitter = account.getClientId();

        Problem problem = getProblemByName(contest, problemID);

        isEmpty(problem, "Parameter problemID does not match any pc2 problem: '" + problemID + "'");

//        Language language = getLanguageByName(contest, languageID);
//        Language language = getLanguageByID(contest, languageID);
        Language language = getLanguageByCLICSID(contest, languageID);
        if (language == null){
            language = getLanguageByName(contest, languageID);
        }
        

        isEmpty(language, "Parameter languageID does not match any pc2 language: '" + languageID + "'");

        SerializedFile mainSubmissionFile = new SerializedFile(mainFile);

        SerializedFile[] additionalFiles = new SerializedFile[0];

        if (auxFiles != null) {
            additionalFiles = new SerializedFile[auxFiles.size()];
            for (int i = 0; i < auxFiles.size(); i++) {
                isEmpty(auxFiles.get(i), "Aux file in list is empty, index = " + i);
                additionalFiles[i] = new SerializedFile(auxFiles.get(i));
            }
        }

        controller.submitRun(submitter, problem, language, mainSubmissionFile, additionalFiles, overrideTimeMS, overrideRunId);
    }

    private Language getLanguageByName(IInternalContest contest2, String languageName) {
        Language outLang = null;
        Language[] languages = contest.getLanguages();
        for (Language language : languages) {
            if (language.getDisplayName().trim().equals(languageName.trim())) {
                outLang = language;
            }
        }
        return outLang;
    }
    
    
    private Language getLanguageByCLICSID(IInternalContest contest2, String languageName) {
        Language outLang = null;
        Language[] languages = contest.getLanguages();
        for (Language language : languages) {
            if (language.getID().trim().equals(languageName.trim())) {
                outLang = language;
            }
        }
        return outLang;
    }
    
    private Problem getProblemByName(IInternalContest contest2, String problemName) {
        Problem outProblem = null;
        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            if (problem.getShortName().trim().equals(problemName.trim())) {
                outProblem = problem;
            }
        }
        return outProblem;
    }

    /**
     * Return account for input client id.
     * 
     * Searches local site's accounts first, then all site's accounts.
     * Will prepend "team" onto client id if client id string not found.
     * 
     * @param contest2
     * @param clientIdString
     * @return null if no account found, else the account.
     */
    private Account getSubmitterId(IInternalContest internalContest, String clientIdString) {

        Account[] accounts = internalContest.getAccounts();

        // search for this site's logins first
        for (Account account : accounts) {
            if (account.getSiteNumber() == thisSiteNumber) {
                if (account.getClientId().getName().equals(clientIdString)) {
                    return account;
                }
                if (account.getClientId().getName().equals("team" + clientIdString)) {
                    return account;
                }
            }
        }

        // Sort by site, type then id.
        Arrays.sort(accounts, new AccountComparator());

        // serach all site's logins
        for (Account account : accounts) {
            if (account.getClientId().getName().equals(clientIdString)) {
                return account;
            }
            if (account.getClientId().getName().equals("team" + clientIdString)) {
                return account;
            }
        }
        return null;
    }

    private void isEmpty(Object obj, String message) {
        if (obj == null)
        {
            throw new InvalidParameterException(message);
        }
    }

    private void isEmptyString(String s, String message) {
        if (s == null || s.trim().length() == 0)
        {
            throw new InvalidParameterException(message);
        }
    }
}
