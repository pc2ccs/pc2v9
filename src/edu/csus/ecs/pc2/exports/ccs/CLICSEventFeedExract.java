// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.exports.ccs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.EventFeedLoader;
import edu.csus.ecs.pc2.core.imports.clics.TeamAccount;
import edu.csus.ecs.pc2.core.list.TeamAccountComparator;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.shadow.IRemoteContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.RemoteContestAPIAdapter;

/**
 * Extract data from event feed, output data to file
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class CLICSEventFeedExract {

    private static final String OUTPUT_FILE_FORMAT_LOAD_ACCOUNTS_OPTION = "--offal";

    private static final String FILE_OPTION = "--file";

    private static final String NEW_ACCOUNTS_LOAD_TSV_FILENAME = "new.accounts_load.tsv";

    private static final String NEW_TEAMS_TSV_FILENAME = "new.teams.tsv";

    private static final String TAB = "\t";

    private static final String CDP_OPTION = "--cdp";

    private ParseArguments arguments;

    enum OUTPUT_FILE_FORMAT {
        //
        /**
         * team.tsv output format
         */
        TEAM_TSV, //
        /**
         * pc2 load accounts format
         */
        ACCOUNT_LOAD_TSV, //
    };

    public static void usage() {

        String[] lines = {
                //
                "Usage: CLICSEventFeedExract [options] ", //
                "", //
                "Purpose: to write " + NEW_TEAMS_TSV_FILENAME + " file from data from Event Feed ", //
                "", //
                "--offal -    output file format account_load to file " + NEW_ACCOUNTS_LOAD_TSV_FILENAME + " ", //
                "--cdp cdpDir - Read Event Feed API read ccsurl, login and password from yaml in cdpDir/config ", //
                "OR", //
                "--ccsurl URL - Primary CCS URL ", //
                "--login LOGIN - Primary CCS login ", //
                "--password PASSWORD - Primary CCS password", //
                "OR, //", //
                "--file EventFeedJSONFile  -  input CLICS event feed", //
                "", //
        };

        for (String s : lines) {
            System.out.println(s);
        }

        VersionInfo info = new VersionInfo();
        System.out.println(info.getSystemVersionInfo());
    }

    /**
     * Fetch list of TeamAccount from event feed.
     * 
     * @param url
     * @param login
     * @param password
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public List<TeamAccount> fetchFromEventFeed(URL url, String login, String password) throws InterruptedException, IOException {

        IRemoteContestAPIAdapter remoteContestAPIAdapter = new RemoteContestAPIAdapter(url, login, password);

        String eventFeedURLString = url.toString() + "/" + "event-feed";

        String json = remoteContestAPIAdapter.getRemoteJSON(eventFeedURLString);
        String[] lines = json.split("\n");

        List<TeamAccount> list = EventFeedLoader.createTeamAccounts(lines);

        return list;
    }

    /**
     * Read events from stream
     * 
     * @param remoteContestAPIAdapter
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    @SuppressWarnings("unused")
    private List<String> readEventFeedStream(IRemoteContestAPIAdapter remoteContestAPIAdapter) throws InterruptedException, IOException {

        List<String> list = new ArrayList<String>();

        InputStream remoteInputStream = remoteContestAPIAdapter.getRemoteEventFeedInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(remoteInputStream));

        // read the next event from the event feed stream
        String event = reader.readLine();

        // process the next event
        while ((event != null)) {

            // if the remote system feeds events too fast (which happens when testing with a completed contest/eventfeed, and could
            // in theory happen during a live contest), this RemoteEventFeedMonitor thread overwhelms the JVM scheduler, causing
            // things like AWT Dispatch thread GUI updates to become hung.
            // Let's sleep for a moment to allow other threads to run.
            // Note1: it might seem more logical to put this sleep at the END of the while-loop; however, there are multiple places
            // within the loop which invoke "continue", which would SKIP the sleep if it was at the end of the loop...
            // Note2: the value "10ms" was experimentally determined using the NADC21 Kattis event feed, which contains over 53,300
            // events (lines). Any value below 10ms caused the GUI to freeze up.
            // Note3: an attempt was made to change the priority of this thread to a higher number (so, lower priority) than that
            // assigned to the AWT Event Dispatch thread, and then to use Thread.yield() here instead of Thread.sleep(). In theory
            // this should have allowed the AWT thread to gain the CPU when it was ready to run, but in practice GUI lockups were
            // still seen. This could be because the AWT thread has multiple blocking conditions and each one of these allows this
            // Event Feed Monitor thread to get back to the CPU (and use it for a full scheduling timeslice).
            // So it was determined that the following was the best solution, at least in the short term...
            // See also GitHub Issue 267: https://github.com/pc2ccs/pc2v9/issues/267
            Thread.sleep(10);

            // skip blank lines and any that do not start/end with "{...}"
            if (event.length() > 0 && event.trim().startsWith("{") && event.trim().endsWith("}")) {
                list.add(event);
            }

            event = reader.readLine();
        }

        reader.close();
        reader = null;

        return list;
    }

    /**
     * Print teams.tsv file.
     * 
     * @param filename
     * @param teamAccounts
     * @throws FileNotFoundException
     */
    public void printTeamsTsvFile(String filename, List<TeamAccount> teamAccounts) throws FileNotFoundException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        printWriter.println("teams" + TAB + "1");

        // Field Description Example Type
        // 1 Team number 22 integer
        // 2 Reservation ID 24314 integer
        // 3 Group ID 4 integer
        // 4 Team name Hoos string
        // 5 Institution name University of Virginia string
        // 6 Institution short name U Virginia string
        // 7 Country USA string ISO 3166-1 alpha-3

        Collections.sort(teamAccounts, new TeamAccountComparator());

        for (TeamAccount teamAccount : teamAccounts) {

            String[] fields = { //
                    teamAccount.getId(), //
                    teamAccount.getIcpc_id(), //
                    teamAccount.getOrganization_id(), //
                    teamAccount.getName(), //
                    teamAccount.getDisplay_name(), //
                    teamAccount.getDisplay_name(), //
                    Constants.DEFAULT_COUNTRY_CODE //
            };

            printWriter.println(String.join(TAB, fields));
        }

        printWriter.close();
        printWriter = null;

    }

    public void run(String[] args) {

        try {

            String[] requiredArguments = { "--login", "--password", "--ccsurl", CDP_OPTION, FILE_OPTION };

            arguments = new ParseArguments(args, requiredArguments);

            OUTPUT_FILE_FORMAT format = OUTPUT_FILE_FORMAT.TEAM_TSV;
            if (arguments.isOptPresent(OUTPUT_FILE_FORMAT_LOAD_ACCOUNTS_OPTION)) {
                format = OUTPUT_FILE_FORMAT.ACCOUNT_LOAD_TSV;
            }

            if (args.length == 0 || arguments.isOptPresent("--help")) {
                usage();
            } else if (arguments.isOptPresent(FILE_OPTION)) {

                String filename = arguments.getOptValue(FILE_OPTION);
                
                if (null == filename){
                    fatalError(8, "Missing file after "+FILE_OPTION);
                }
                if (!new File(filename).isFile()) {
                    fatalError(8, "No such file "+filename);
                }
                
                System.out.println("In: "+filename);
                String[] lines = Utilities.loadFile(filename);
                List<TeamAccount> teams = EventFeedLoader.createTeamAccounts(lines);

                if (format == OUTPUT_FILE_FORMAT.ACCOUNT_LOAD_TSV) {
                    printAccountLoadFile(NEW_ACCOUNTS_LOAD_TSV_FILENAME, teams);
                    System.out.println("Wrote " + teams.size() + " teams to " + NEW_ACCOUNTS_LOAD_TSV_FILENAME);
                } else {
                    printTeamsTsvFile(NEW_TEAMS_TSV_FILENAME, teams);
                    System.out.println("Wrote " + teams.size() + " teams to " + NEW_TEAMS_TSV_FILENAME);
                }

            } else {

                String loginName;
                String password;
                String CCSUrl;
                URL url;

                if (arguments.isOptPresent(CDP_OPTION)) {
                    String cdpDir = arguments.getOptValue(CDP_OPTION);
                    url = locateCDPConfigDir(cdpDir);

                    if (url == null) {
                        fatalError(8, "Cannot find CDP config directory for " + cdpDir);
                    }

                    Map<String, String> map = getCDPValues(url);

                    loginName = map.get("login");
                    password = map.get("password");
                    CCSUrl = map.get("Primary CCS URL");

                    noArgInCDPYaml("login", loginName, cdpDir);
                    noArgInCDPYaml("password", password, cdpDir);
                    noArgInCDPYaml("Primary CCS URL", CCSUrl, cdpDir);

                } else {
                    loginName = arguments.getOptValue("--login");
                    password = arguments.getOptValue("--password");
                    CCSUrl = arguments.getOptValue("--ccsurl");

                    argPresentOrHalt("login", loginName);
                    argPresentOrHalt("password", password);
                    argPresentOrHalt("Primary CCS URL", CCSUrl);
                    url = new URL(CCSUrl);
                }

//                System.out.println("debug 22 login " + loginName);
//                System.out.println("debug 22 password " + password);
//                System.out.println("debug 22 Primary CCS URL " + CCSUrl);

                List<TeamAccount> teams = fetchFromEventFeed(url, loginName, password);

                if (format == OUTPUT_FILE_FORMAT.ACCOUNT_LOAD_TSV) {
                    printAccountLoadFile(NEW_ACCOUNTS_LOAD_TSV_FILENAME, teams);
                    System.out.println("Wrote " + teams.size() + " teams to " + NEW_ACCOUNTS_LOAD_TSV_FILENAME);
                } else {
                    printTeamsTsvFile(NEW_TEAMS_TSV_FILENAME, teams);
                    System.out.println("Wrote " + teams.size() + " teams to " + NEW_TEAMS_TSV_FILENAME);
                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
            fatalError(4, e.getMessage());
        }

    }

    /**
     * print pc2 load accounts file.
     * 
     * @param outputAccountsLoadFilename
     * @param teams
     * @throws FileNotFoundException
     */
    public void printAccountLoadFile(String outputAccountsLoadFilename, List<TeamAccount> teams) throws FileNotFoundException {

        String[] headerCols = { "site", "account", "displayname" };

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(outputAccountsLoadFilename, false), true);

        printWriter.println(String.join(TAB, headerCols));

        for (TeamAccount teamAccount : teams) {
            String[] cols = { "1", "team" + teamAccount.getId(), teamAccount.getDisplay_name() };

            printWriter.println(String.join(TAB, cols));
        }

        printWriter.close();
        printWriter = null;
    }

    /**
     * Load map with values from CDP yaml files.
     * 
     * @param url
     * @return
     */
    protected Map<String, String> getCDPValues(URL url) {

        Map<String, String> map = new HashMap<String, String>();

        String configDir = url.getFile().toString();
        // Load maps
        IInternalContest contest = new ContestSnakeYAMLLoader().fromYaml(null, configDir);

        ContestInformation info = contest.getContestInformation();

        String value = info.getPrimaryCCS_URL();
        if (StringUtilities.isEmpty(value)) {
            noArgInCDPYaml("Primary CCS URL", value, configDir);
        }
        map.put("Primary CCS URL", value);

        value = info.getPrimaryCCS_user_login();
        if (StringUtilities.isEmpty(value)) {
            noArgInCDPYaml("login", value, configDir);
        }
        map.put("login", value);

        value = info.getPrimaryCCS_user_pw();
        if (StringUtilities.isEmpty(value)) {
            noArgInCDPYaml("password", value, configDir);
        }
        map.put("password", value);

        return map;
    }

    private void noArgInCDPYaml(String name, String value, String cdpDir) {
        if (StringUtilities.isEmpty(value)) {
            fatalError(4, "Missing value in " + cdpDir + ": " + value);
        }

    }

    private void argPresentOrHalt(String name, String value) {

        if (StringUtilities.isEmpty(value)) {
            fatalError(4, "Missing required option: " + name);
        }
    }

    private void fatalError(int exitCode, String message) {
        System.err.println("Fatal error = " + message);
        System.exit(exitCode);
    }

    /**
     * Find and return CDP config directory.
     * 
     * 
     * @param cdpDir
     * @return null if not found, else the CDP URL
     */
    protected URL locateCDPConfigDir(String cdpDir) {

        try {
            URL url = FileUtilities.findCDPConfigDirectoryURL(new File(cdpDir));
            return url;
        } catch (Exception e) {
            fatalError(5, "Error " + e.getMessage() + " for url " + cdpDir);
        }
        return null;
    }

    public static void main(String[] args) {
        new CLICSEventFeedExract().run(args);
    }
}
