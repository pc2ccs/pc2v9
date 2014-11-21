package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * submissions.tsv report/file output.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SubmissionsTSVReport implements IReportFile {

    /**
     * 
     */
    private static final long serialVersionUID = -674666232557526609L;

    private static final String DELIMITER = "\t";
    
    /**
     * Default judgment "unknown" acronym.
     */
    private static final String DEFAULT_ACRONYM = "??";

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    // From Perl script mksumm
    // my %jhash;
    // $jhash{"No - Compilation Error" } = "CE";
    // $jhash{"No - Security Violation" } = "SV";
    // $jhash{"No - Time Limit Exceeded" } = "TLE";
    // $jhash{"No - Wrong Output" } = "WA";
    // $jhash{"Yes"} = "AC";

    private String[] acronymList = { //
            "No - Compilation Error;CE", //
            "No - Security Violation;SV", //
            "No - Time Limit Exceeded;TLE", //
            "No - Wrong Output;WA", //
            "Yes;AC", //
            "Accepted;AC", //
            "Wrong Answer;WA", //
    };
    
    String createLine(Run run) {

        StringBuffer buf = new StringBuffer();

        ClientId clientId = run.getSubmitter();

        // From Bug 704
        // The file format is TSV with the following fields:
        // 1: RunId, integer
        // 2: User, integer (team number)
        // 3: Problem short name
        // 4: submission time, in ms
        // 5: result/judgement, 2 letter judgement acronym.

        // 1: RunId, integer
        buf.append(run.getNumber());
        buf.append(DELIMITER);

        // 2: User, integer (team number)
        buf.append(clientId.getClientNumber());
        buf.append(DELIMITER);

        // 3: Problem short name
        Problem problem = contest.getProblem(run.getProblemId());
        buf.append(problem.getShortName());
        buf.append(DELIMITER);

        // 4: submission time, in ms
        buf.append(run.getElapsedMS());
        buf.append(DELIMITER);

        // 5: result/judgement, 2 letter judgement acronym.
        buf.append(getJudgementAcronym(run));

        return buf.toString();
    }

    private String getJudgementAcronym(Run run) {

        String acronym = null; // default IF

        JudgementRecord judgementRecord = run.getJudgementRecord();

        if (judgementRecord != null) {
            String judgementText = null;
            Judgement judgement = contest.getJudgement(judgementRecord.getJudgementId());
            if (judgement != null) {
                acronym = judgement.getAcronym();
                judgementText = judgement.getDisplayName();
            }

            if (acronym == null || "".equals(acronym.trim())) {
                // Not defined, do a "guess"

                if (run.isSolved()) {
                    acronym = "AC";
                } else {
                    acronym = guessAcronym(judgementText);
                }
            }
        } else {
            acronym = "NEW";
        }

        return acronym;
    }

    private String guessAcronym(String judgementText) {

        if (judgementText == null) {
            return DEFAULT_ACRONYM;
        }

        for (String line : acronymList) {

            String[] fields = line.split(";");
            String name = fields[0];
            String acro = fields[1];

            if (name.trim().toLowerCase().equals(judgementText.trim().toLowerCase())) {
                return acro;
            }
        }

        return DEFAULT_ACRONYM;
    }

    public void writeReport(PrintWriter printWriter) {

        // submissions.tsv
        String[] lines = getReportLines();
        for (String string : lines) {
            printWriter.println(string);
        }
    }

    private String[] getReportLines() {

        ArrayList<String> list = new ArrayList<String>();

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        if (filter != null && filter.isFilterOn()) {

            int count = 0;
            for (Run run : runs) {
                if (filter.matches(run)) {
                    count++;
                }
            }

            if (count > 0) {
                // printWriter.println("-- " + count + " of " + runs.length +
                // " runs (filtered) --");
                for (Run run : runs) {
                    if (filter.matches(run)) {
                        list.add(createLine(run));
                    }
                }
            }

        } else {
            // printWriter.println("-- " + runs.length + " runs --");
            for (Run run : runs) {
                // skip deleted runs
                if (run.isDeleted()) {
                    continue;
                }
                list.add(createLine(run));
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
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

                printWriter.close();
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
        }
    }

    public String[] createReport(Filter inFilter) {
        filter = inFilter;
        return getReportLines();
    }

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "submissions.tsv";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "submissions.tsv";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public boolean suppressHeaderFooter() {
        return true;
    }
    
}
