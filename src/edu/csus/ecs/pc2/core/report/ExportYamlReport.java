package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.export.ExportYAML;
import edu.csus.ecs.pc2.core.list.FileComparator;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Write All YAML files (report).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ExportYamlReport.java 218 2011-08-30 02:59:15Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/report/ExportYamlReport.java $
public class ExportYamlReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -6693261389633976350L;

    private IInternalContest contest;

    private IInternalController controller;

    private Filter filter = new Filter();

    private String directoryName;

    private String outputfilename;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
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
            controller.logWarning("Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }

    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public void writeReport(PrintWriter printWriter) throws Exception {

        ExportYAML exportYAML = new ExportYAML();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.ss.SSS");

        if (directoryName == null) {
            directoryName = "reports" + File.separator + "yaml" + simpleDateFormat.format(new Date());
        }

        printWriter.println();
        printWriter.println("Writing YAML files to " + directoryName);

        if (!new File(directoryName).isDirectory()) {
            new File(directoryName).mkdirs();
        }

        if (outputfilename == null) {
            outputfilename = directoryName + File.separator + ExportYAML.CONTEST_FILENAME;
        }

        exportYAML.writeContestYAMLFiles(contest, directoryName, outputfilename);

        listFiles(printWriter, "  file ", directoryName);

        printWriter.println();

    }

    private void listFiles(PrintWriter printWriter, String prefix, String directory) throws Exception {

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

    public String getPluginTitle() {
        return "Export Contest YAML files";
    }

    public String getReportTitle() {
        return "Export Contest YAML files";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
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

    private void writeContestTime(PrintWriter printWriter) {
        printWriter.println();
        GregorianCalendar resumeTime = contest.getContestTime().getResumeTime();
        if (resumeTime == null) {
            printWriter.println("Contest date/time: never started");
        } else {
            printWriter.println("Contest date/time: " + resumeTime.getTime());
        }
    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setOutputfilename(String outputfilename) {
        this.outputfilename = outputfilename;
    }
}
