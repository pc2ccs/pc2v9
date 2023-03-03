// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.ContestXML;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ConfigurationIO;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;
import edu.csus.ecs.pc2.profile.ProfileLoadException;
import edu.csus.ecs.pc2.profile.ProfileManager;

/**
 * Command Line print PC^2 Reports.
 * 
 * @author pc2@ecs.csus.edu
 */
public final class Reports {

    /**
     * Location where pc2 installed
     */
    private static String installDirectory = Utilities.getCurrentDirectory();

    private String profileName = null;

    private char[] password = null;

    private int siteNumber = 1;

    private boolean usingProfile = true;
    
    private static final String FILE_OPTION_STRING = "-F";
    
    private static final String DIR_OPTION_STRING = "--dir";
    
    private static final String RAW_OPTION_STRING = "--raw";

    public Reports(String profileName, char[] charArray) {
        super();
        this.profileName = profileName;
        this.password = charArray;
    }

    /**
     * Return list of all defined reports.
     * 
     * @return list of IReports
     */
    public static IReport[] getReports() {
        Vector<IReport> reports = new Vector<IReport>();

        reports.add(new AccountsReport());
        reports.add(new BalloonSummaryReport());

        reports.add(new AllReports());
        reports.add(new ContestSettingsReport());
        reports.add(new ContestReport());

        reports.add(new ContestAnalysisReport());
        reports.add(new SolutionsByProblemReport());
        reports.add(new ListRunLanguages());

        reports.add(new FastestSolvedSummaryReport());
        reports.add(new FastestSolvedReport());

        reports.add(new StandingsReport());
        reports.add(new LoginReport());
        reports.add(new ProfilesReport());
        reports.add(new PluginsReport());

        reports.add(new RunsReport());
        reports.add(new ClarificationsReport());
        reports.add(new ProblemsReport());
        reports.add(new LanguagesReport());

        reports.add(new JudgementReport());
        reports.add(new RunsByTeamReport());
        reports.add(new BalloonSettingsReport());
        reports.add(new ClientSettingsReport());
        reports.add(new GroupsReport());

        reports.add(new EvaluationReport());

        reports.add(new OldRunsReport());
        reports.add(new RunsReport5());

        reports.add(new AccountPermissionReport());
        reports.add(new BalloonDeliveryReport());
        reports.add(new ExtractPlaybackLoadFilesReport());

        reports.add(new RunJudgementNotificationsReport());
        reports.add(new JudgementNotificationsReport());

        reports.add(new ProfileCloneSettingsReport());

        reports.add(new SitesReport());

        reports.add(new EventFeedReport());
        reports.add(new NotificationsReport());

        reports.add(new FinalizeReport());

        reports.add(new InternalDumpReport());

        reports.add(new PasswordsReport());

        reports.add(new AccountsTSVReportTeamAndJudges());

        reports.add(new AccountsTSVReport());

        reports.add(new RunsTSVReport());

        reports.add(new JSONReport());

        reports.add(new EventFeed2013Report());

        reports.add(new UserdataTSVReport());

        reports.add(new GroupsTSVReport());

        reports.add(new TeamsTSVReport());

        reports.add(new ScoreboardTSVReport());

        reports.add(new SubmissionsTSVReport());

        reports.add(new ResolverEventFeedReport());

        reports.add(new AutoJudgingSettingsReport());

        reports.add(new JudgingAnalysisReport());

        reports.add(new JSON2016Report());
        
        reports.add(new CDPReport());
        
        reports.add(new EventFeedJSONReport());
        
        reports.add(new ProblemsGroupReport());
        
        reports.add(new ProblemGroupAssignmentReport());
        
        reports.add(new ContestCompareReport());
        
        reports.add(new StandingsNSAReport());
        
        return (IReport[]) reports.toArray(new IReport[reports.size()]);

    }

    /**
     * Print list of reports to stdout.
     */
    public static void listReports() {

        int i = 0;
        for (IReport report : getReports()) {
            i++;
            System.out.println("Report " + i + " " + report.getReportTitle());
        }

    }

    /**
     * Prints usage to stdout.
     */
    public static void usage() {

        String[] lines = { "Usage: [options] reportName|## [[reportName|##][...]]", //
                "", //
                "-F filename    - specify command line options in filename", //
                "--profile name - profile name, default uses current profile.  name may be a ## from --listp listing", //
                "--contestPassword padd  - password needed to decrypt pc2 data", //
                "--raw          - suppress footer and header \"raw\" report content", //
                "--xml          - output only XML for report", //
                "--list         - list names of reports (and the report numbers)", //
                "--dir name     - alternate base directory name, by default uses profile dir name", //
                "--site ##      - specify the site number", //
                "--listp        - list all profile names with numbers", //
                "--noProfile - do not use profile directory use pre version 9.2 location", //
                "", //
                "reportName - name of report to print (or report number)",//
                "##         - number of report to print (numbers found using --list)", //
                "", //
                "$ pc2reports --listp", //
                "1 - Id: Contest-1526060434834405723 description: Real Contest name: Contest", //
                "2 - Id: Contest 3--613094433664018852 description: Real Contest 3 name: Contest 3", //
                "", //
                "Default name  : Contest", //
                "  Profile ID  : Contest-1526060434834405723", //
                "  Description : Real Contest", //
                "  Path        : profiles\\Pdf812e23-4234-46ee-ad3c-4011c8cb885e", //
                "", //
                "Each of these will print the same report:", //
                "$ pc2report --contestPassword newpass --profile Contest 3--613094433664018852 'Fastest Solution Summary'", //
                "$ pc2report --contestPassword newpass --profile 2 'Fastest Solution Summary'", //
                "$ pc2report --contestPassword newpass --profile Contest 3--613094433664018852 9", //
                "$ pc2report --contestPassword newpass --profile 2 9", //
                "", //

                "Precedence for directory: --dir, --profile, then default profile dir", //
                "", //
        };

        for (String s : lines) {
            System.out.println(s);
        }

        VersionInfo info = new VersionInfo();
        System.out.println(info.getSystemVersionInfo());

    }

    /**
     * Get an integer for the input string.
     * 
     * @param s
     *            a string containing an integer
     * @return 0 if can not parse string otherwise converts the string contents to an integer.
     */
    protected static int getInteger(String s) {

        try {
            return Integer.parseInt(s);

        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Gets the report for the input arg (integer).
     * 
     * Looks up the report by number and returns the report for that input integer.
     * 
     * @param arg
     *            a string containing an integer.
     * @return reporrt for input integer (from arg)
     * @throws Exception
     */
    public static IReport getReport(String arg) throws Exception {

        int reportNumber = getInteger(arg);
        IReport selectedReport = null;

        if (reportNumber > 0) {
            IReport[] reports = getReports();

            if (reportNumber > reports.length) {
                throw new Exception("No such report number " + reportNumber);
            }

            selectedReport = reports[reportNumber - 1];

        } else {

            for (IReport report : getReports()) {
                if (report.getReportTitle().startsWith(arg)) {
                    if (selectedReport == null) {
                        selectedReport = report;
                    }
                }
            }
        }

        return selectedReport;
    }

    /**
     * Returns a filename for the input report.
     * 
     * The report filename has the name of the report {@link IReport#getReportTitle()} and the current time in form MM.dd.SSS (SimpleDateFormat)
     * 
     * @param selectedReport
     * @return a filename for the input report
     */
    public String getReportFileName(IReport selectedReport) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return "report." + reportName + "." + simpleDateFormat.format(new Date()) + ".txt";

    }

    /**
     * Print a report.
     * 
     * @param arg
     *            either number or name for the report
     * @param outputXML
     */
    private void printReport(String arg, boolean suppressHeaderAndFooter, boolean outputXML) {

        String dirName = getInstallDirectory();

        try {
            dirName = locateProfileDirectory();

            dirName = dirName + File.separator + "db." + getSiteNumber();

            if (!new File(dirName).isDirectory()) {
                System.err.println("Expected config directory, not found dir expected " + dirName);
                return;
            }

            FileSecurity security = new FileSecurity(dirName);
            security.verifyPassword(getPassword());
            IStorage storage = security;

            ConfigurationIO configurationIO = new ConfigurationIO(storage);

            if (getSiteNumber() == 0) {
                setSiteNumber(1);
            }
            InternalContest contest = new InternalContest();
            Log log = new Log("pc2reports.log");
            StaticLog.setLog(log);

            contest.setStorage(storage);

            contest.initializeSubmissions(getSiteNumber(), false);

            if (!configurationIO.loadFromDisk(getSiteNumber(), contest, log)) {
                System.err.println("Unable to read contest data from disk");
                return;
            }

            ClientId clientId = new ClientId(getSiteNumber(), Type.ADMINISTRATOR, 1);
            contest.setClientId(clientId);

            IReport report = getReport(arg);
            
            if (report == null) {
                System.out.println("Unable to match/find report " + arg);
            } else {
                InternalController controller = new InternalController(contest);
                controller.setLog(log);
                String filename = getReportFileName(report);
                report.setContestAndController(contest, controller);
                
                
                if (outputXML) {
                    String xml = report.createReportXML(new Filter());
                    writeFile(filename, xml);
                } else {
                    
                    if (suppressHeaderAndFooter) {
                        // Wrap Report and suppress header and footer
                        writeReportNoHeaderFooter(report, filename, new Filter());
                    } else {
                        report.createReportFile(filename, new Filter());
                    }
                    
                }
                catfile(filename);
                
              

            }
            
        } catch (FileNotFoundException fnfe) {
            System.err.println("ERROR nothing to print, no pc2 files/profiles found under " + getInstallDirectory());
            fnfe.printStackTrace(); // debug 22
        } catch (FileSecurityException fse) {
            System.err.println("ERROR " + getFSEMsg(fse));
            System.err.println("For directory " + dirName);
        } catch (Exception e) {
            System.err.println("ERROR " + e.getMessage());
        }
    }

    private void writeReportNoHeaderFooter(IReport report, String filename, Filter filter) {

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
            report.writeReport(printWriter);
        } catch (Exception e) {
            printWriter.println("Exception in report: " + e.getMessage());
            e.printStackTrace(printWriter);
        }
    }

    /**
     * Find the profile directory where config files are present.
     * 
     * @see #getInstallDirectory()
     * @return the profile/config directory (which contains db.1)
     */
    protected String locateProfileDirectory() throws Exception {

        String dirName = null;
        
        String profileIndexFilename = getInstallDirectory() + File.separator + ProfileManager.PROFILE_INDEX_FILENAME;

        ProfileManager manager = new ProfileManager(profileIndexFilename);
        Profile profile = null;

        if (getProfileName() == null) {
            // No profile name specified, use default profile
            
            profile = manager.getDefaultProfile();
            System.err.println("Using default profile is: " + profile.getName() + " " + profile.getProfilePath());
            
        } else {
            
            // find/match profile
            
            Profile[] profiles = manager.load();
            for (Profile checkProfile : profiles) {
                if (checkProfile.getContestId().equals(getProfileName())) {
                    profile = checkProfile;
                }
            }
            if (profile == null) {
                throw new Exception("No profile named " + getProfileName() + " in " + profileIndexFilename); // profileIndexFilename
            }
        }

        System.err.println("Using profile " + profile.getName() + " " + profile.getProfilePath());
        dirName = getInstallDirectory() + File.separator + profile.getProfilePath();

        return dirName;
    }

    private void writeFile(String filename, String s) throws IOException {
        FileOutputStream fis = new FileOutputStream(filename, false);
        fis.write(s.getBytes());
        fis.close();
        fis = null;
    }

    /**
     * Get a more English name for the FileSecurityException.
     * 
     * @param fse
     * @return a more English name for the input fse.
     */
    private String getFSEMsg(FileSecurityException fse) {

        if (FileSecurity.FAILED_TO_DECRYPT.equals(fse.getLocalizedMessage())) {
            return "Invalid password (password does not match)";
        }

        return fse.getLocalizedMessage();
    }

    public String getProfileName() {
        return profileName;
    }

    public char[] getPassword() {
        return password;
    }

    /**
     * Write filename to stdout.
     * 
     * @param filename
     *            filename to echo to stdout
     */
    private void catfile(String filename) {

        String[] lines;
        try {
            lines = Utilities.loadFile(filename);
            for (String s : lines) {
                System.out.println(s);
            }
        } catch (IOException e) {
            System.err.println("Unable to write to file " + filename);
            e.printStackTrace();
        }
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;
    }

    /**
     * Fatal error - log error and show user message before exiting.
     * 
     * @param message
     * @param ex
     */
    protected static void fatalError(String message, Exception ex) {
        
        System.err.println(message);
        if (ex != null){
            ex.printStackTrace(System.err);
        }
        System.exit(2);
    }
    

    /**
     * 
     * @see #fatalError(String, Exception)
     * @param message
     */
    protected static void fatalError(String message) {
        fatalError(message, null);
    }


    /**
     * Main starting point for Reports
     * 
     * @param args
     *            command line arguments
     */
    public static void main(String[] args) {

        String[] requireArguementArgs = { "--contestPassword", //
                "--profile", DIR_OPTION_STRING, "--site", FILE_OPTION_STRING };

        ParseArguments arguments = new ParseArguments(args, requireArguementArgs);

        if (args.length == 0) {
            usage();
            System.exit(2);
        }

        if (arguments.isOptPresent("--help")) {
            usage();
            System.exit(0);
        }
        
        if (arguments.isOptPresent(FILE_OPTION_STRING)) {
            String propertiesFileName = arguments.getOptValue(FILE_OPTION_STRING);

            if (!(new File(propertiesFileName).exists())) {
                fatalError(propertiesFileName + " does not exist (pwd: " + Utilities.getCurrentDirectory() + ")", null);
            }

            try {
                arguments.overRideOptions(propertiesFileName);
            } catch (IOException e) {
                fatalError("Unable to read file " + propertiesFileName, e);
            }
        }
        
        if (arguments.isOptPresent(DIR_OPTION_STRING)) {
            String dir = arguments.getOptValue(DIR_OPTION_STRING);
            
            if (!(new File(dir).isDirectory())) {
                fatalError("No such directory '"+dir+"'");
            }
            setInstallDirectory(dir);
        }
        
        /**
         * Raw - do not output header or footer, just contents.
         */
        boolean rawOutput = arguments.isOptPresent(RAW_OPTION_STRING);

        if (arguments.isOptPresent("--list")) {
            listReports();
            System.exit(0);
        }

        if (arguments.isOptPresent("--listp")) {
            listProfiles();
            System.exit(0);
        }

        if (arguments.getArgCount() == 0) {
            fatalError("No reports specified, none printed");
        }

        boolean xmlOutputOption = arguments.isOptPresent("--xml");

        String password = arguments.getOptValue("--contestPassword");
        String profileName = arguments.getOptValue("--profile");

        int number = getInteger(arguments.getOptValue("--site"));
        if (number == 0) {
            number = 1;
        }

        if (password == null) {
            fatalError("Contest Password (--contestPassword) is required");
        }

        profileName = lookupProfileName(profileName);

        Reports reports = new Reports(profileName, password.toCharArray());

        reports.setUsingProfile(!arguments.isOptPresent("--noProfile"));
        reports.setSiteNumber(number);

        for (int i = 0; i < arguments.getArgCount(); i++) {
            String arg = arguments.getArg(i);
            reports.printReport(arg, rawOutput, xmlOutputOption);
        }

    }

    /**
     * Looks up profile # in profile list.
     * 
     * If the input name is a integer only will return the profile id (ContestId) that cooresponds to the profile in the profiles.properties list.
     * 
     * @param name
     * @return
     */
    private static String lookupProfileName(String name) {

        if (name != null && name.matches("^\\d+$")) {

            // If only a digit, look it up in the profiles list

            try {
                
                String profileIndexFilename = getInstallDirectory() + File.separator + ProfileManager.PROFILE_INDEX_FILENAME;
                
                Profile[] list = new ProfileManager(profileIndexFilename).load();

                int profileNumber = getInteger(name);
                if (list.length > 0 && profileNumber - 1 < list.length) {
                    return list[profileNumber - 1].getContestId();
                }

            } catch (Exception e) {
                return name;
            }
        }
        return name;
    }

    public boolean isUsingProfile() {
        return usingProfile;
    }

    public void setUsingProfile(boolean usingProfile) {
        this.usingProfile = usingProfile;
    }

    /**
     * List all profiles to stdout.
     * 
     */
    private static void listProfiles() {
        
        String profileIndexFilename = getInstallDirectory() + File.separator + ProfileManager.PROFILE_INDEX_FILENAME;

        try {

            if (!new File(profileIndexFilename).exists()) {
                System.err.println("No profiles exist (Profile properties files does not exist: " + profileIndexFilename + " )");
                return;
            }
            Profile[] list = new ProfileManager(profileIndexFilename).load();

            if (list.length > 0) {
                int i = 1;
                for (Profile profile : list) {
                    System.out.println(i + " - Id: " + profile.getContestId() + " description: " + profile.getDescription() + " name: " + profile.getName());
                    i++;
                }
                System.out.println();
            }

            Profile profile = new ProfileManager(profileIndexFilename).getDefaultProfile();
            if (profile != null) {
                System.out.println("Default name  : " + profile.getName() + "\n  Profile ID  : " + profile.getContestId() + "\n  Description : " + profile.getDescription() + "\n  Path        : "
                        + profile.getProfilePath());
            }

        } catch (IOException e) {
            e.printStackTrace(System.err);
        } catch (ProfileLoadException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Directory where pc2 is installed
     */
    private static String getInstallDirectory() {
        return installDirectory;
    }
    
    public static void setInstallDirectory(String installDirectory) {
        Reports.installDirectory = installDirectory;
    }

    /**
     * Create an empty XML element/report.
     * 
     * @param report
     * @return
     * @throws IOException
     */
    public static String notImplementedXML(IReport report) throws IOException {
        return notImplementedXML(report, "");
    }

    public static String notImplementedXML(IReport report, String message) throws IOException {

        ContestXML contestXML = new ContestXML();

        XMLMemento mementoRoot = XMLMemento.createWriteRoot("report");

        IMemento memento = mementoRoot.createChild("message");
        memento.putString("name", "Not implemented");
        if (message != null && message.length() > 0) {
            memento.putString("info", message);
        }
        memento.putString("reportName", report.getReportTitle());

        contestXML.addVersionInfo(mementoRoot, null);

        contestXML.addFileInfo(mementoRoot);

        return mementoRoot.saveToString();
    }
}
