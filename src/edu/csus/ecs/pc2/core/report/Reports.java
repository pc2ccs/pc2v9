package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ConfigurationIO;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.profile.ProfileManager;

/**
 * Command Line print PC^2 Reports.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class Reports {

    private String profileName = null;

    private char[] password = null;

    private int siteNumber = 1;

    private String directory = null;

    public Reports(String profileName, char[] charArray) {
        super();
        this.profileName = profileName;
        this.password = charArray;
    }

    /**
     * Return list of IReports.
     * 
     * @return
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

        reports.add(new InternalDumpReport());

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

    public static void usage() {

        String[] lines = { "Usage: [options] reportName|## [[reportName|##][...]]", //
                "", // 
                "--profile name    - profile name, default uses current profile", // 
                "--contestPassword padd  - password needed to decrypt pc2 data", // 
                "--list - list names of reports (and the report numbers)", // 
                "--dir name - alternate base directory name, by default uses profile dir name", // 
                "--site ## - specify the site number", // 
                "", // 
                "reportName - name of report to print (or report number)",//
                "##         - number of report to print (numbers found using --list)", //
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

    protected static int getInteger(String s) {

        try {
            return Integer.parseInt(s);

        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

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

    public String getReportFileName(IReport selectedReport) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return "report." + reportName + "." + simpleDateFormat.format(new Date()) + ".txt";

    }

    protected class InternalControllerLog extends InternalController {

        public InternalControllerLog(IInternalContest contest, Log log) {
            super(contest);
            setLog(log);
        }

        private void setLog(Log log) {
            super.log = log;

        }
    }

    /**
     * Print a report.
     * 
     * @param arg
     */
    private void printReport(String arg) {

        String dirName = null;

        try {

            if (getDirectory() == null) {

                ProfileManager manager = new ProfileManager();
                Profile profile = manager.getDefaultProfile();
                
                System.err.println("default profile is: "+profile.getName()+" "+profile.getProfilePath());
            

                dirName = profile.getProfilePath();
            } else {
                dirName = getDirectory();
            }

            dirName = dirName + File.separator + "db." + getSiteNumber();

            if (!new File(dirName).isDirectory()) {
                System.err.println("Directory does not exist: " + dirName);
                return;
            }

            FileSecurity security = new FileSecurity(dirName);
            security.verifyPassword(getPassword());
            IStorage storage = security;

            ConfigurationIO configurationIO = new ConfigurationIO(storage);

            int siteNumber = 1;
            InternalContest contest = new InternalContest();
            Log log = new Log("pc2reports.log");

            if (!configurationIO.loadFromDisk(siteNumber, contest, log)) {
                System.err.println("Unable to read contest data from disk");
                return;
            }

            IReport report = getReport(arg);

            if (report == null) {
                System.out.println("Unable to match/find report " + arg);
            } else {
                InternalControllerLog controller = new InternalControllerLog(contest, log);
                String filename = getReportFileName(report);
                report.setContestAndController(contest, controller);
                report.createReportFile(filename, new Filter());
                catfile(filename);

            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("ERROR nothing to print, no pc2 files/profiles found under " + Utilities.getCurrentDirectory());
        } catch (FileSecurityException fse) {
            System.err.println("ERROR " + getFSEMsg(fse));
            System.err.println("For directory " + dirName);
            fse.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public static void main(String[] args) {

        String[] requireArguementArgs = { "--contestPassword", //
                "--profile", "--dir", "--site" };

        ParseArguments arguments = new ParseArguments(args, requireArguementArgs);

        if (args.length == 0) {
            usage();
            System.exit(2);
        }

        if (arguments.isOptPresent("--help")) {
            usage();
            System.exit(0);
        }

        if (arguments.isOptPresent("--list")) {
            listReports();
            System.exit(0);
        }

        if (arguments.getArgCount() == 0) {
            System.err.println("No reports specified, none printed");
            System.exit(2);

        }

        String password = arguments.getOptValue("--contestPassword");
        String profileName = arguments.getOptValue("--profile");

        int number = getInteger(arguments.getOptValue("--siteNumber"));
        if (number == 0) {
            number = 1;
        }

        if (password == null) {
            System.err.println("Contest Password (--contestPassword) is required");
            System.exit(2);
        }

        Reports reports = new Reports(profileName, password.toCharArray());

        reports.setSiteNumber(number);

        for (int i = 0; i < arguments.getArgCount(); i++) {
            String arg = arguments.getArg(i);
            reports.printReport(arg);
        }

    }
}
