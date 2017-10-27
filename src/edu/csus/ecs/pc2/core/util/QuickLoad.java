package edu.csus.ecs.pc2.core.util;

import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Load contest with testing config info.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ProfilesPane.java 2275 2010-11-30 03:39:24Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/ProfilesPane.java $
public class QuickLoad implements UIPlugin {

    private static final long serialVersionUID = -4248229783309059230L;

    private IInternalContest contest;

    private IInternalController controller;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;

        loadContest();
    }

    /**
     * Load contest values.
     */
    public void loadContest() {

        if (contest.getLanguages().length == 0) {

            Language language = createLanguage(LanguageAutoFill.JAVATITLE);
            if (language != null) {
                controller.addNewLanguage(language);
            }

            System.out.println("quickLoad: add " + language);

            language = createLanguage(LanguageAutoFill.GNUCPPTITLE);
            if (language != null) {
                controller.addNewLanguage(language);
            }

            System.out.println("quickLoad: add " + language);

            language = createLanguage(LanguageAutoFill.PERLTITLE);
            if (language != null) {
                controller.addNewLanguage(language);
            }

            System.out.println("quickLoad: add " + language);

        }

        // TODO set groupIds

        if (contest.getProblems().length == 0) {
            Problem problem = new Problem("Sumit");
            Problem problem2 = new Problem("Hello");

            ProblemDataFiles files = new ProblemDataFiles(problem);
            controller.addNewProblem(problem, files);
            System.out.println("quickLoad: add " + problem);

            files = new ProblemDataFiles(problem2);
            controller.addNewProblem(problem2, files);
            System.out.println("quickLoad: add " + problem);
        }

        if (siteLoggedIn(1)) {
            generateAccounts(1, Type.TEAM, 22);
            generateAccounts(1, Type.JUDGE, 12);
            generateAccounts(1, Type.SCOREBOARD, 12);
        }

        if (siteLoggedIn(2)) {
            generateAccounts(2, Type.TEAM, 22);
            generateAccounts(2, Type.JUDGE, 2);
        }
        if (siteLoggedIn(3)) {
            generateAccounts(3, Type.TEAM, 33);
        }

        assignGroupAndExternalIds();

    }

    private void assignGroupAndExternalIds() {

        Account[] accounts = null;

        // Wait for a second to get accounts that might not be generated otherwise.

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            accounts = null; // NOP put here to avoid CC warning
        }

        accounts = getTeamAccounts(contest);
        for (Account account : accounts) {
            account.setCountryCode("UK");
            String externalId = account.getSiteNumber() + "00" + account.getClientId().getClientNumber();
            account.setExternalId(externalId);
            // TODO set groupId
        }

        controller.updateAccounts(accounts);
    }

    /**
     * Get all sites' teams.
     * 
     * @param contest
     * @return
     */
    public Account[] getTeamAccounts(IInternalContest inContest) {
        Vector<Account> accountVector = inContest.getAccounts(ClientType.Type.TEAM);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }

    /**
     * Create language based on auto fill.
     * 
     * @see LanguageAutoFill
     * @param languageName
     *            - name from {#link {@link LanguageAutoFill} .
     * @return
     */
    private Language createLanguage(String languageName) {
        for (String langName : LanguageAutoFill.getLanguageList()) {
            if (langName.equals(languageName)) {
                // Use auto fill values
                String[] values = LanguageAutoFill.getAutoFillValues(langName);
                Language language = new Language(langName);
                fillLanguage(language, values, langName);
                return language;
            }
        }
        return null;
    }

    private void fillLanguage(Language language, String[] values, String fullLanguageName) {
        // values array
        // 0 Title for Language
        // 1 Compiler Command Line
        // 2 Executable Identifier Mask
        // 3 Execute command line
        // 5 "interpreted" if interpreter.
        // 6 ID for Contest API

        language.setCompileCommandLine(values[1]);
        language.setExecutableIdentifierMask(values[2]);
        language.setProgramExecuteCommandLine(values[3]);
        boolean isScript = LanguageAutoFill.isInterpretedLanguage(fullLanguageName);
        language.setInterpreted(isScript);
        language.setID(values[6]);
    }

    /**
     * Is this siteNumber connected/logged in.
     * 
     * @param siteNumber
     * @return
     */
    private boolean siteLoggedIn(int siteNumber) {

        if (siteNumber == contest.getSiteNumber()) {
            return true;
        }

        ClientId remoteServerId = new ClientId(siteNumber, ClientType.Type.SERVER, 0);
        return contest.isLocalLoggedIn(remoteServerId) || contest.isRemoteLoggedIn(remoteServerId);
    }

    /**
     * Insure that there are count accounts, generate more if needed.
     * 
     * @param siteNumber
     * @param type
     * @param count
     */
    private void generateAccounts(int siteNumber, Type type, int count) {

        Vector<Account> accounts = contest.getAccounts(type, siteNumber);

        int numToGenerate = count - accounts.size();

        if (numToGenerate > 0) {
            System.out.println("quickLoad: added " + numToGenerate + " " + type.toString() + " at site " + siteNumber);
            controller.generateNewAccounts(type.toString(), siteNumber, numToGenerate, 1, true);
        }
    }

    public String getPluginTitle() {
        return "Quick Load Contest";
    }

}
