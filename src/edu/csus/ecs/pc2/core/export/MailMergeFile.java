// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.export;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.core.ClientUtility;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.Plugin;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Mail merge TSV file generator.
 */
public class MailMergeFile {

    // TODO REFACTOR debug 22 move TAB to Constants

    private static final String TAB = "\t";

    // TODO REFACTOR debug 22 on merge move these constants to  edu.csus.ecs.pc2.core.Constant 

    public static final String HELP_OPTION_STRING = "--help";

    public static final String DEBUG_OPTION_STRING = "--debug";

    public static final String LOGIN_OPTION_STRING = "--login";

    public static final String PASSWORD_OPTION_STRING = "--password";

    public static final String PASSWORD_LIST_FILENNAME = "passwords.txt";

    public static final String DEFAULT_MERGE_OUTPUT_FILENAME = "all.teams.merge.tsv";

    private static final String NON_TEAMS_OPTION_STRING = "--non-teams";

    /**
     * Mail merge column names.
     */
    public static final String[] COLUMN_NAMES = { "uname", "upassword", "user", "password", "name", "univname" };

    private ParseArguments arguments = new ParseArguments();

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
        
        int numMissingPasswords = accounts.size() - unixPasswords.length;
        if (numMissingPasswords > 0){
            throw new RuntimeException("Too few passwords in list, need "+accounts.size()+" there were "+unixPasswords.length);
        }

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

        if (accounts.size() > unixPasswords.length) {
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

    protected void loginPrintMergeFile(String loginName, String password) throws IOException {

        Plugin plugin = null;

        try {
            plugin = ClientUtility.logInToContest(loginName, password);
            String outputFilename = DEFAULT_MERGE_OUTPUT_FILENAME;
            printMergeFile(outputFilename, plugin.getContest());
        } catch (LoginFailureException e) {
            Utilities.printStackTrace(System.err, e, "csus");
            System.err.println("Uable to login " + e.getMessage());
            System.exit(4);
        }
    }

   protected void loginPrintMergeFile() throws IOException {

       // Get loginId
       String loginName = "";
       if (arguments.isOptPresent(LOGIN_OPTION_STRING)) {
           loginName = arguments.getOptValue(LOGIN_OPTION_STRING);
       }

       // get password (optional if joe password)
       String password = "";
       if (arguments.isOptPresent(PASSWORD_OPTION_STRING)) {
           password = arguments.getOptValue(PASSWORD_OPTION_STRING);
       }

       loginPrintMergeFile(loginName, password);

    }

    protected void printMergeFile(String outputFilename, IInternalContest contest) {

        List<Account> accounts = new ArrayList<>();

        if (arguments.isOptPresent(NON_TEAMS_OPTION_STRING)) {
            // TODO TODAY implment for non-teams
            System.out.println("TODO - implement merge option "+NON_TEAMS_OPTION_STRING);
        } else {
            accounts.addAll(ClientUtility.getTeamAccounts(contest));
        }

        // TODO add --file and -of to override  outputFilename

        String unixPasswordsFile = PASSWORD_LIST_FILENNAME;

        haltIfFileMissing(unixPasswordsFile);

        try {
            writeFile(outputFilename, unixPasswordsFile, accounts);
            System.err.println("Wrote merge file to " + outputFilename);
        } catch (Exception e) {
            Utilities.printStackTrace(System.err, e, "csus");
            System.err.println("Unable to write to file " + outputFilename + " " + e.getMessage());
        }
    }

    // TODO REFACTOR debug 22 - move haltIfFileMissing to Utilities class
    private void haltIfFileMissing(String filename) {
        if (!Utilities.fileExists(filename)) {
            System.err.println("Missing required file '" + filename + "'");
            System.err.println("halting program");
            System.exit(4);
        }
    }

    public static void usage() {

        String columnNames = String.join(",", COLUMN_NAMES);

        String[] lines = {
                // 
                "Usage: MailMergeFile [options] ", //
                "", //
                "Purpose:  write a merge file to stdout ", //

                "", //
                "Writes a TSV file, fields " + columnNames + ".  ", // 
                "First line of file contains field names", //
                "", //
        };

        for (String s : lines) {
            System.out.println(s);
        }

        VersionInfo info = new VersionInfo();
        System.out.println(info.getSystemVersionInfo());
    }
    

    public void main(String[] args) {

        try {
            String[] requireArguementArgs = { LOGIN_OPTION_STRING, PASSWORD_OPTION_STRING, "-w", "-u" };

            arguments = new ParseArguments(args, requireArguementArgs);

            if (args.length == 0) {
                usage();
                System.exit(2);
            }

            if (arguments.isOptPresent("--help")) {
                usage();
                System.exit(0);
            }

            String passwordFileName = PASSWORD_LIST_FILENNAME;

            if (!Utilities.fileExists(passwordFileName)) {
                throw new FileNotFoundException(passwordFileName);
            }

            if (arguments.isOptPresent(LOGIN_OPTION_STRING)) {

                loginPrintMergeFile();

            } else {

                System.err.println("Misssing " + LOGIN_OPTION_STRING + " option");
            }

        } catch (Exception e) {
            Utilities.printStackTrace(System.err, e, "csus");
        }
    }

}
