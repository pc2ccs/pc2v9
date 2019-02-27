package edu.csus.ecs.pc2.tools;

import java.util.ArrayList;
import java.util.List;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.core.ClientUtility;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.Plugin;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.export.MailMergeFile;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Print mail merge program.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class PrintMergeFile {

    // TODO REFACTOR debug 22 on merge move these constants to  edu.csus.ecs.pc2.core.Constant 

    public static final String HELP_OPTION_STRING = "--help";

    public static final String DEBUG_OPTION_STRING = "--debug";

    public static final String LOGIN_OPTION_STRING = "--login";

    public static final String PASSWORD_OPTION_STRING = "--password";

    public static final String DEFAULT_TEAM_MERGE_OUTPUT_FILENAME = "all.teams.merge.tsv";
    
    public static final String DEFAULT_OTHER_MERGE_OUTPUT_FILENAME = "all.others.merge.tsv";

    private static final String NON_TEAMS_OPTION_STRING = "--non-teams";

    private ParseArguments arguments = new ParseArguments();

    // TODO REFACTOR debug 22 - move haltIfFileMissing to Utilities class
    private void haltIfFileMissing(String filename, String fileDescription) {
        if (!Utilities.fileExists(filename)) {
            System.err.println("Missing required file "+fileDescription+" '" + filename + "'");
            System.err.println("halting program");
            System.exit(4);
        }
    }


    /**
     * Login and print merge file.
     * 
     * @param outputFilename
     * @param passwordFileName 
     */
    private void loginPrintMergeFile(String outputFilename, String passwordFileName) {

        // Get login
        String loginName = "";
        if (arguments.isOptPresent(LOGIN_OPTION_STRING)) {
            loginName = arguments.getOptValue(LOGIN_OPTION_STRING);
        }

        // get password (optional if joe password)
        String password = "";
        if (arguments.isOptPresent(PASSWORD_OPTION_STRING)) {
            password = arguments.getOptValue(PASSWORD_OPTION_STRING);
        } 

        Plugin plugin = null;

        try {
            
            plugin = ClientUtility.logInToContest(loginName, password);
            
        } catch (LoginFailureException e) {
            Utilities.printStackTrace(System.err, e, "csus");
            System.err.println("Uable to login " + e.getMessage());
            System.exit(4);
        }

        printMergeFile(outputFilename, passwordFileName, plugin.getContest());
        
    }

    /**
     * Print merge file.
     * 
     * @param outputFilename
     * @param contest
     */
    public void printMergeFile(String outputFilename,  String unixPasswordsFile, IInternalContest contest) {

        List<Account> accounts = new ArrayList<>();

        if (arguments.isOptPresent(NON_TEAMS_OPTION_STRING)) {
            accounts.addAll(ClientUtility.getAccounts(contest, Type.JUDGE));
            accounts.addAll(ClientUtility.getAccounts(contest, Type.FEEDER));
            accounts.addAll(ClientUtility.getAccounts(contest, Type.SCOREBOARD));
            accounts.addAll(ClientUtility.getAccounts(contest, Type.OTHER));
            accounts.addAll(ClientUtility.getAccounts(contest, Type.SPECTATOR));
            
        } else {
            accounts.addAll(ClientUtility.getTeamAccounts(contest));
        }

        haltIfFileMissing(unixPasswordsFile, "Unix password list");

        try {

            MailMergeFile.writeFile(outputFilename, unixPasswordsFile, accounts);


        } catch (Exception e) {
            Utilities.printStackTrace(System.err, e, "csus");
            System.err.println("Unable to write to file " + outputFilename + " " + e.getMessage());
        }
    }
    
    public static void usage() {

        String columnNames = String.join(",", MailMergeFile.COLUMN_NAMES);

        String[] lines = {
                // 
                "Usage: PrintMergeFile [options] --login LOGIN [--password PASSWORD] ", //
                "", //
                "Purpose:  write a team merge file to file " + DEFAULT_TEAM_MERGE_OUTPUT_FILENAME, //
                "Writes a TSV file, fields " + columnNames + ".  ", // 
                "First line of file contains field names", //
                "", //
                "Input:  list of OS passwords in file " + MailMergeFile.PASSWORD_LIST_FILENNAME, //
                "        The login, team name and pc2 password are pulled from a running pc2 server",
                "Output: writes merge file to "+DEFAULT_TEAM_MERGE_OUTPUT_FILENAME, //
                "", //
                "--passfile filename  - use alternative password file, default uses "+MailMergeFile.PASSWORD_LIST_FILENNAME, //
                "--mergefile outfilename - use outfilename, default output written to "+DEFAULT_TEAM_MERGE_OUTPUT_FILENAME, //
                "--login LOGIN ", //   xx
                "--password PASSWORD ", //
                "", //
        };

        for (String s : lines) {
            System.out.println(s);
        }

        VersionInfo info = new VersionInfo();
        System.out.println(info.getSystemVersionInfo());
    }

    public void run(String[] args) {

        String[] requiredArguments = { "--login", "--password", "--passfile", "--mergfile" };

        arguments = new ParseArguments(args, requiredArguments);

        arguments.dumpArgs(System.out); // debug 22
        
        if (args.length == 0 || arguments.isOptPresent("--help")) {
            usage();
        } else {

            String passwordFileName = MailMergeFile.PASSWORD_LIST_FILENNAME;
            if (arguments.getOptValue("--passfile") != null) {
                passwordFileName = arguments.getOptValue("--passfile");
            }
            
            String outputFilename = DEFAULT_TEAM_MERGE_OUTPUT_FILENAME;
            if (arguments.getOptValue("--mergefile") != null){
                outputFilename = arguments.getOptValue("--mergefile");
            }
            
            String loginName = arguments.getOptValue("--login");
            String password = arguments.getOptValue("--password");

            if (loginName != null) {
                ClientId clientId = InternalController.loginShortcutExpansion(0, loginName);
                if (clientId != null) {
                    loginName = clientId.getName();
                }
            }

            if (password == null) {
                password = loginName;
            }

            haltIfFileMissing(passwordFileName, "Unix password file");
            if (arguments.isOptPresent(LOGIN_OPTION_STRING)) {
                

                loginPrintMergeFile(outputFilename, passwordFileName);

                System.err.println("Wrote merge file to " + outputFilename);
                System.exit(0);
            } else {

                System.err.println("Misssing " + LOGIN_OPTION_STRING + " option");
                System.exit(4);
            }
        }
    }
    
    public static void main(String[] args) {
        new PrintMergeFile().run(args);
    }
}
