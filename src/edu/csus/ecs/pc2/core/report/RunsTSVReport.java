package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
 * runs.tsv report/file output.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunsTSVReport implements IReportFile {

    /**
     * 
     */
    private static final long serialVersionUID = -674666232557526609L;

    private static final String DELIMITER = "\t";
    
    private boolean bug704WORKAROUND = true; // TODO Bug 704 SOMEDAY remove this workaround

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

    private void writeRow(PrintWriter printWriter, Run run) {

        ClientId clientId = run.getSubmitter();

        // From Bug 704
        // The file format is TSV with the following fields:
        // 1: RunId, integer
        // 2: User, integer (team number)
        // 3: Problem short name
        // 4: submission time, in ms
        // 5: result/judgement, 2 letter judgement acronym.

        // 1: RunId, integer
        printWriter.print(run.getNumber());
        printWriter.print(DELIMITER);

        // 2: User, integer (team number)
        printWriter.print(clientId.getClientNumber());
        printWriter.print(DELIMITER);

        // 3: Problem short name
        Problem problem = contest.getProblem(run.getProblemId());
        printWriter.print(problem.getShortName());
        printWriter.print(DELIMITER);

        // 4: submission time, in ms
        printWriter.print(run.getElapsedMS());
        printWriter.print(DELIMITER);

        // 5: result/judgement, 2 letter judgement acronym.
        printWriter.print(getJudgementAcronym(run));

        printWriter.println();
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

            if (bug704WORKAROUND ||acronym == null || "".equals(acronym.trim())) {
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

        // Runs
        printWriter.println();
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        if (filter.isFilterOn()) {
            printWriter.println("Filter: " + filter.toString());

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
                        try {
                            writeRow(printWriter, run);
                        } catch (Exception e) {
                            printWriter.println("Exception in report: " + e.getMessage());
                            e.printStackTrace(printWriter);
                        }
                    }
                }
            }

        } else {
            // printWriter.println("-- " + runs.length + " runs --");
            for (Run run : runs) {
                try {
                    writeRow(printWriter, run);
                } catch (Exception e) {
                    printWriter.println("Exception in report: " + e.getMessage());
                    e.printStackTrace(printWriter);
                }
            }

        }
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
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "run.tsv";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "runs.tsv Report";
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
    
    public void setBug704WORKAROUND(boolean bug704workaround) {
        bug704WORKAROUND = bug704workaround;
    }
}
