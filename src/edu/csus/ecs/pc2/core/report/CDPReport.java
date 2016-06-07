package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.export.ExportYAML;
import edu.csus.ecs.pc2.core.list.FileComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.exports.ccs.ResolverEventFeedXML;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;
import edu.csus.ecs.pc2.imports.ccs.ContestYAMLLoader;

/**
 * Contest Data Package Report
 * 
 * @author $Author$
 * @version $Id$
 */
public class CDPReport implements IReport {

    private static final long serialVersionUID = 3516176895449881260L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    private String directoryName = null;

    private String outputfilename;

    private static final String PAD4 = "    ";

    private static final String PAD2 = "  ";

    // TODO move to Utility class
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss z";

    // TODO MOVE TO ContestYAMLLoader.PROBLEM_SET_KEY
    private static final String PROBLEM_SET_KEY = "problemset";

    private SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);

    private void writeContestTime(PrintWriter printWriter) {
        printWriter.println();
        GregorianCalendar resumeTime = contest.getContestTime().getResumeTime();
        if (resumeTime == null) {
            printWriter.println("Contest date/time: never started");
        } else {
            printWriter.println("Contest date/time: " + resumeTime.getTime());

        }
    }

    /**
     * Create a problem short name.
     * 
     * @param name
     *            Problem full name
     * @return
     */
    private String createProblemShortName(String name) {
        String newName = name.trim().split(" ")[0].trim().toLowerCase(); // + (System.nanoTime() % 1000);
        return newName;
    }

    private String getProblemBalloonColor(IInternalContest aContest, Problem problem) {
        BalloonSettings balloonSettings = aContest.getBalloonSettings(aContest.getSiteNumber());
        String name = null;
        if (balloonSettings != null) {
            name = balloonSettings.getColor(problem);
        }
        return name;
    }

    /**
     * Surround by a single quote
     * 
     * @param string
     * @return
     */
    private String quote(String string) {
        return "'" + string + "'";
    }

    protected static StringBuffer join(String delimiter, List<String> list) {

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < list.size() - 1; i++) {
            buffer.append(list.get(i));
            buffer.append(delimiter);
        }
        if (list.size() > 0) {
            buffer.append(list.get(list.size() - 1));
        }
        return buffer;
    }

    /**
     * Get problem letter for input integer.
     * 
     * getProblemLetter(1) is 'A'
     * 
     * @param id
     *            a one based problem number.
     * @return
     */
    protected String getProblemLetter(int id) {
        char let = 'A';
        let += (id - 1);
        return Character.toString(let);
    }

    /**
     * Return date/time string for now.
     * 
     * Uses format {@value #FORMAT_YYYY_MM_DD_HH_MM_SS}.
     * 
     * @return
     */
    // TODO move to utility class
    public String getDateTimeString() {
        return formatter.format(new Date());
    }

    // TODO move to ExportYAML class
    public void writeProblemYaml(PrintWriter printWriter) {
        writeProblemYaml(printWriter, true);
    }

    // TODO move to ExportYAML class
    public void writeProblemYaml(PrintWriter printWriter, boolean useLatestProblemKey) {

        int id = 1;

        printWriter.println("# Problem Set Configuration, version 1.0 ");
        printWriter.println("# PC^2 Version: " + new VersionInfo().getSystemVersionInfo());
        printWriter.println("# Created: " + getDateTimeString());
        printWriter.println("--- ");

        printWriter.println();

        String problemKey = ContestYAMLLoader.PROBLEMS_KEY;
        if (useLatestProblemKey) {
            // printWriter.println(ContestYAMLLoader.PROBLEM_SET_KEY + ":");
            problemKey = PROBLEM_SET_KEY;

        }
        printWriter.println(problemKey + ":");

        Problem[] problems = contest.getProblems();

        for (Problem problem : problems) {

            String name = problem.getDisplayName();

            String letter = getProblemLetter(id);
            if (problem.getLetter() != null) {
                letter = problem.getLetter();
            }
            printWriter.println(PAD2 + "- letter: " + letter);
            String shortName = createProblemShortName(name);
            if (problem.getShortName() != null && problem.getShortName().trim().length() > 0) {
                shortName = problem.getShortName();
            }
            printWriter.println(PAD4 + "short-name: " + shortName);
            printWriter.println(PAD4 + "name: " + quote(name));

            String colorName = getProblemBalloonColor(contest, problem);
            if (colorName != null) {
                printWriter.println(PAD4 + "color: " + colorName);
            }
            // else no color, nothing to print.

            id++;

            printWriter.println();
        }
    }

    // TODO move/promote writeLinesToFile to Utilities class

    public void writeLinesToFile(String filename, String[] lines) throws FileNotFoundException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        for (String line : lines) {
            printWriter.println(line);
        }
        printWriter.close();
    }

    public void writeReport(PrintWriter printWriter) throws Exception {

        // TODO check whether finalized ?

        ExportYAML exportYAML = new ExportYAML();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.ss.SSS");

        if (directoryName == null) {
            directoryName = "reports" + File.separator + "yaml" + simpleDateFormat.format(new Date());
        }

        printWriter.println();
        printWriter.println("Writing CDP files to " + directoryName);

        // ensure directory
        if (!new File(directoryName).isDirectory()) {
            new File(directoryName).mkdirs();
        }

        if (outputfilename == null) {
            outputfilename = directoryName + File.separator + ExportYAML.CONTEST_FILENAME;
        }

        /**
         * Write userdata.tsv
         */

        // TODO create ExportYAML.USERDATA_FILENAME
        // String userDataFilename =directoryName + File.separator + ExportYAML.USERDATA_FILENAME;
        String userDataFilename = directoryName + File.separator + "userdata.tsv";

        try {
            writeUserDataFile(userDataFilename);
        } catch (Exception e) {
            printWriter.println("Error creating " + userDataFilename);
            e.printStackTrace(printWriter);
        }

        String resultsdir = directoryName + File.separator + "results";
        ensureDirectory(resultsdir);

        String resultsTSVFilename = directoryName + File.separator + "results.tsv";

        try {
            writeResults(resultsTSVFilename);
        } catch (Exception e) {
            printWriter.println("Error creating " + resultsTSVFilename);
            e.printStackTrace(printWriter);
        }

        String scorboardTSVFilename = directoryName + File.separator + "scoreboard.tsv";
        try {
            writeScoreboardTSV(scorboardTSVFilename);
        } catch (Exception e) {
            printWriter.println("Error creating " + scorboardTSVFilename);
            e.printStackTrace(printWriter);
        }

        try {
            writeScoeboardHTML(resultsdir);

        } catch (Exception e) {
            printWriter.println("Error creating HTML in " + resultsdir);
            e.printStackTrace(printWriter);
        }

        String groupsFilename = directoryName + File.separator + "groups.tsv";
        try {
            writeGroupsTSV(groupsFilename);
        } catch (Exception e) {
            printWriter.println("Exception writing " + groupsFilename);
            e.printStackTrace(printWriter);
        }

        String teamsFilename = directoryName + File.separator + "teams.tsv";
        try {
            writeteamsTSV(teamsFilename);

        } catch (Exception e) {
            printWriter.println("Exception writing " + teamsFilename);
            e.printStackTrace(printWriter);
        }

        /**
         * This writes contest.yaml.
         */
        try {
            exportYAML.writeContestYAMLFiles(contest, directoryName, outputfilename);
        } catch (Exception e) {
            printWriter.println("Exception writing yaml files " + e.getMessage());
            e.printStackTrace(printWriter);
        }

        /**
         * Write problemset.yaml
         */

        String problemSetYamlFilename = directoryName + File.separator + ExportYAML.PROBLEM_SET_FILENAME;
        try {
            writeProblemYaml(problemSetYamlFilename);
        } catch (Exception e) {
            printWriter.println("Exception writing yaml files " + e.getMessage());
            e.printStackTrace(printWriter);
        }

        /**
         * Write run submissions
         */

        try {
            writeSubmissions(printWriter, directoryName);
        } catch (Exception e) {
            printWriter.println("Exception writing runs or run files " + e.getMessage());
            e.printStackTrace(printWriter);
        }

        /**
         * Write events.xml (event feed)
         */

        String eventFeedDirectory = directoryName + File.separator + "eventFeed";
        ensureDirectory(eventFeedDirectory);

        String eventFeedfilename = eventFeedDirectory + File.separator + "events.xml";

        try {

            ResolverEventFeedXML resolverEventFeedXML = new ResolverEventFeedXML();
            resolverEventFeedXML.setLog(controller.getLog());
            String efxml = resolverEventFeedXML.toXML(contest);

            // TODO add ExportYAML.EVENT_FEED_FILENAME
            // File.separator + ExportYAML.EVENT_FEED_FILENAME;

            String[] lines = { efxml };

            writeLinesToFile(eventFeedfilename, lines);
        } catch (Exception e) {
            printWriter.println("Exception writing " + teamsFilename);
            e.printStackTrace(printWriter);
        }

        listFiles(printWriter, "  file ", directoryName);

        printWriter.println();
        printWriter.println("contest.yaml contents");
        printWriter.println();
        Utilities.catFile(printWriter, outputfilename);
        printWriter.println();

        printWriter.println();
        printWriter.println("problemset.yaml contents");
        printWriter.println();
        Utilities.catFile(printWriter, problemSetYamlFilename);
        printWriter.println();
    }

    public void writeProblemYaml(String filename) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        writeProblemYaml(writer);
        writer.close();
    }

    private void writeteamsTSV(String filename) throws Exception {

        TeamsTSVReport teamsTSVReport = new TeamsTSVReport();
        teamsTSVReport.setContestAndController(contest, controller);

        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        teamsTSVReport.writeReport(writer);
        writer.close();
    }

    private void writeGroupsTSV(String filename) throws Exception {
        GroupsTSVReport groupsTSVReport = new GroupsTSVReport();
        groupsTSVReport.setContestAndController(contest, controller);

        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        groupsTSVReport.writeReport(writer);
        writer.close();

    }

    private void writeScoeboardHTML(String resultsdir) throws IllegalContestState {

        ScoreboardPlugin plugin = new ScoreboardPlugin();
        plugin.setContestAndController(contest, controller);

        plugin.writeHTML(resultsdir);
    }

    private void writeScoreboardTSV(String filename) throws Exception {
        ScoreboardTSVReport scoreboardTSVReport = new ScoreboardTSVReport();
        scoreboardTSVReport.setContestAndController(contest, controller);

        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        scoreboardTSVReport.writeReport(writer);
        writer.close();
    }

    private void writeResults(String filename) throws Exception {
        ResultsFile resultsFile = new ResultsFile();
        String[] lines = resultsFile.createTSVFileLines(contest);
        writeLinesToFile(filename, lines);
    }

    /**
     * Get Runs Files.
     * 
     * If needed will wait for all files to be retrieved.
     * 
     * @param runs
     * @return
     */
    private Map<String, RunFiles> getRunFiles(Run[] runs) {

        HashMap<String, RunFiles> list = new HashMap<>();

        /**
         * Request set of runs from server. Wait for runs to arrive. Consider caching runs. return runs.
         */

        // TODO Check that if on server whether grabs runs directly.
        // This may be a server only feature, for now.

        // RunFiles [] files = controller.fetchRuns(runs);

        /**
         * Fetch run files for
         */

        String filename = "pc2v9.ini";

        for (Run run : runs) {
            RunFiles files = new RunFiles(run, filename);
            String key = run.getElementId().toString();
            list.put(key, files);
        }

        return list;
    }

    public void writeSubmissions(PrintWriter writer, String dir) throws IOException {

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        Map<String, RunFiles> files = getRunFiles(runs);

        for (Run run : runs) {
            try {

                String runDirectory = getRunDirectory(dir, run);
                ensureDirectory(runDirectory);

                RunFiles runFiles = getRunFiles(files, run);

                writeSubmissionInfo(contest, runDirectory, run, runFiles);

                SerializedFile mainFile = runFiles.getMainFile();

                mainFile.writeFile(runDirectory + File.separator + mainFile.getName());

                SerializedFile[] otherfiles = runFiles.getOtherFiles();

                if (otherfiles != null && otherfiles.length > 0) {

                    for (SerializedFile serializedFile : otherfiles) {
                        serializedFile.writeFile(runDirectory + File.separator + serializedFile.getName());
                    }
                }

            } catch (Exception e) {
                writer.println("Problem while writing submission " + run);
                e.printStackTrace(writer);
            }
        }

    }

    public static String getRunDirectory(String baseDirectory, Run run) {

        if (run.getSiteNumber() > 1) {
            return baseDirectory + File.separator + "s" + run.getSiteNumber() + "r" + run.getNumber();
        } else {
            return baseDirectory + File.separator + run.getNumber();
        }
    }

    private void writeSubmissionInfo(IInternalContest inContest, String runDirectory, Run run, RunFiles files) throws FileNotFoundException {

        String infoFilename = runDirectory + File.separator + "run.properties";
        // TODO add ExportYAML.RUN_PROPERTIES_FILENAME
        // String infoFilename = runDirectory + File.separator + ExportYAML.RUN_PROPERTIES_FILENAME;

        PrintWriter writer = new PrintWriter(new FileOutputStream(infoFilename, false), true);

        writer.println("# ");
        writer.println("# Created on " + new Date());
        writer.println("# ");
        writer.println("# Created by " + new VersionInfo().getSystemVersionInfo());
        writer.println("# ");

        ArrayList<String> list = new ArrayList<>();

        Problem problem = inContest.getProblem(run.getProblemId());
        Language language = inContest.getLanguage(run.getLanguageId());

        list.add("problem=" + problem.getShortName());
        list.add("language=" + language.getDisplayName());
        list.add("submittedby=" + run.getSubmitter().getName());
        list.add("contesttime=" + run.getElapsedMS());
        list.add("solved=" + run.isSolved());
        list.add("site=" + run.getSiteNumber());

        list.add("mainfile=" + files.getMainFile().getName());

        SerializedFile[] others = files.getOtherFiles();
        if (others != null) {

            list.add("sourcecount=" + others.length);

            int count = 1;
            for (SerializedFile file : others) {
                list.add("source" + count + "=" + file.getName());
                count++;
            }
        }

        for (String string : list) {
            writer.println(string);
        }
        writer.close();

    }

    private RunFiles getRunFiles(Map<String, RunFiles> files, Run run) {
        return files.get(run.getElementId().toString());
    }

    private boolean ensureDirectory(String dir) {
        if (!new File(dir).isDirectory()) {
            new File(dir).mkdirs();
        }

        return new File(dir).isDirectory();
    }

    private void writeUserDataFile(String filename) throws Exception {

        UserdataTSVReport userdataTSVReport = new UserdataTSVReport();
        userdataTSVReport.setContestAndController(contest, controller);

        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        userdataTSVReport.writeReport(writer);
        writer.close();

    }

    public void listFiles(PrintWriter printWriter, String prefix, String directory) throws Exception {

        File[] entries = new File(directory).listFiles();
        Arrays.sort(entries, new FileComparator());

        for (File entry : entries) {
            if (entry.isFile()) {
                printWriter.println(prefix + directory + File.separator + entry.getName());
            }
        }

        for (File entry : entries) {
            if (entry.isDirectory()) {
                listFiles(printWriter, prefix, directory + File.separator + entry.getName());
            }
        }

    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");

        writeContestTime(printWriter);
        printWriter.println();
    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {

            try {
                printHeader(printWriter);

                writeReport(printWriter);

                printFooter(printWriter);

            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }
    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "Contest Data Package";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Contest Data Package Reportt";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

}
