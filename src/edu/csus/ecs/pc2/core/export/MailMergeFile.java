package edu.csus.ecs.pc2.core.export;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;

/**
 * Mail merge TSV file generator.
 */
public class MailMergeFile {

    private static final String TAB = "\t";

    /**
     * Mail merge column names.
     */
    public static String[] COLUMN_NAMES = { "uname", "upassword", "user", "password", "name", "univname" };

    /**
     * Create mail merge lines.
     * 
     * Note first line is list of fields.
     * 
     * @param unixPasswords list of unix account passwords
     * @param accounts
     * @return tab seperated values for fields: uname, upassword, user, password, name, univname 
     */
    public static List<String> createLines(String[] unixPasswords, List<Account> accounts) {

        List<String> outList = new ArrayList<>();

        Collections.sort(accounts, new AccountComparator());
        int i = 0;

        outList.add(String.join(TAB, COLUMN_NAMES));

        for (Account account : accounts) {
            String accountName = account.getClientId().getName();

            outList.add( //
            //
            accountName + TAB + // uname
                    unixPasswords[i++] + TAB + // upassord
                    accountName + TAB + // user
                    account.getPassword() + TAB + // password
                    account.getDisplayName() + TAB + // password
                    account.getInstitutionName());
        }

        return outList;
    }

    /**
     * Write mail merge file.
     * 
     * @param outputFilename
     * @param unixPasswords list of unix account passwords
     * @param accounts
     * @throws FileNotFoundException
     */
    public static void writeFile(String outputFilename, String[] unixPasswords, List<Account> accounts)
            throws FileNotFoundException {

        if (accounts.size() <= unixPasswords.length) {
            throw new IllegalArgumentException(
                    "Expecting " + accounts.size() + " unix passwords, only " + unixPasswords.length + " found.");
        }

        PrintWriter pw = new PrintWriter(new FileOutputStream(outputFilename, false), true);

        List<String> list = createLines(unixPasswords, accounts);
        for (String string : list) {
            pw.println(string);
        }

        pw.close();
        pw = null;
    }

    /**
     * Write mail merge file.
     * 
     * @param outputFilename
     * @param unixPasswordsFile file with unix passwords
     * @param accounts
     * @throws IOException
     */
    public static void writeFile(String outputFilename, String unixPasswordsFile, List<Account> accounts)
            throws IOException {

        String[] unixPasswords = Utilities.loadFile(unixPasswordsFile);

        if (accounts.size() <= unixPasswords.length) {
            throw new IllegalArgumentException("Expecting " + accounts.size() + " unix passwords, only " + unixPasswords.length + " found in " + unixPasswordsFile);
        }

        PrintWriter pw = new PrintWriter(new FileOutputStream(outputFilename, false), true);

        List<String> list = createLines(unixPasswords, accounts);
        for (String string : list) {
            pw.println(string);
        }

        pw.close();
        pw = null;
    }

}
