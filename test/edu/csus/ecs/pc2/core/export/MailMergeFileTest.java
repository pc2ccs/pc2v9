package edu.csus.ecs.pc2.core.export;

import java.util.Arrays;
import java.util.List;

import edu.csus.ecs.pc2.core.ClientUtility;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class MailMergeFileTest extends AbstractTestCase {

    /**
     * Test create lines.
     */
    public void testCreateLines() throws Exception {

        /**
         * Prefix for unix passwords.
         */
        String prefix = "unix";
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();

        Account[] teams = getTeamAccounts(contest);
        List<Account> accounts = Arrays.asList(teams);
        Arrays.sort(teams, new AccountComparator());
        
        teams[9].setDisplayName("Team 10");
        
        String[] unixPasswords = generateJoePasswords(prefix,accounts);

        int lastAccountIndex = accounts.size() - 1;
        String accountLogin = accounts.get(lastAccountIndex).getClientId().getName();
        assertEquals("Expecting  password for account at index " + lastAccountIndex, prefix + accountLogin, unixPasswords[lastAccountIndex]);

        List<String> lines = MailMergeFile.createLines(unixPasswords, accounts);
        
        assertEquals("Expecting number of lines ",+teams.length + 1, lines.size());
        
        String team10line = lines.get(10);
        String[] fields = team10line.split("\t");
        
        int i = 0;
        
        String unameStr = fields[i++];
        assertEquals("team10", unameStr);
        
        String upasswordStr = fields[i++];
        assertEquals("unixteam10", upasswordStr);
        
        String userStr = fields[i++];
        assertEquals("team10", userStr);
        
        String passwordStr = fields[i++];
        assertEquals("team10", passwordStr);
        
        String nameStr = fields[i++];
        assertEquals("Team 10", nameStr);
        
        String univnameStr = fields[i++];
        assertEquals("undefined", univnameStr);
    }

    private String[] generateJoePasswords(String prefix, List<Account> accounts) {
        String[] pass = new String[accounts.size()];
        
        if (prefix == null){
            prefix = "";
        }
        
        for (int i = 0; i < accounts.size(); i++) {
            pass[i] = prefix + accounts.get(i).getClientId().getName();
        }
        return pass;
    }

    public void testprintMergeFile() throws Exception {

        String dir = getOutputDataDirectory();
        String passFilename = getOutputTestFilename(MailMergeFile.PASSWORD_LIST_FILENNAME);
        String outFileName = getOutputTestFilename(MailMergeFile.DEFAULT_MERGE_OUTPUT_FILENAME);

        ensureDirectory(dir);

        removeFile(passFilename);
        removeFile(outFileName);

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();

        String[] unixPasswords = generateJoePasswords("unix", ClientUtility.getTeamAccounts(contest));

        writeFileContents(passFilename, unixPasswords);
        
        writeFileContents(MailMergeFile.PASSWORD_LIST_FILENNAME, unixPasswords);

        // Must have password list
        assertFileExists(passFilename);

        new MailMergeFile().printMergeFile(outFileName, contest);

        assertFileExists(outFileName);
    }

}
