// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AvailableAJComparator;
import edu.csus.ecs.pc2.core.list.ProblemComparator;
import edu.csus.ecs.pc2.core.list.ProblemList;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.AvailableAJ;
import edu.csus.ecs.pc2.core.model.AvailableAJRun;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Report that shows AJ Judge list and AJ Runs list.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class AutojudgeListReport implements IReportFile {

    /**
     * 
     */
    private static final long serialVersionUID = 2354125593511939010L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    @Override
    public String getPluginTitle() {
        return "Autojudge Lists";
    }

    @Override
    public void createReportFile(String filename, Filter aFilter) throws IOException {

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

    @Override
    public String[] createReport(Filter aFilter) {
        String[] arr = { "createReport not implemented" };
        return arr;
    }

    @Override
    public String createReportXML(Filter aFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    @Override
    public void writeReport(PrintWriter printWriter) throws Exception {

        List<AvailableAJ> ajList = contest.getAvailableAutoJudges();
        Collections.sort(ajList, new AvailableAJComparator());

        printWriter.println("There are " + ajList.size() + " Judges available in Auto Judge list");

        for (AvailableAJ availableAJ : ajList) {
            printWriter.println("    " + availableAJ.getClientId() + " " + //
                    getProblemShortNames(contest, availableAJ.getProblemList()));
        }

        List<AvailableAJRun> runList = contest.getAvailableAutoJudgeRuns();
        List<Run> runs = getRuns(contest, runList);
        Collections.sort(runs, new RunComparator());

        printWriter.println("There are " + runList.size() + " Runs in Auto Judge list");

        for (Run run : runs) {
            printWriter.println("    Run " + run.getNumber() + " " + run);
        }

        if (!Type.SERVER.equals(contest.getClientId().getClientType())) {
            printWriter.println();
            printWriter.println("Note: since this report is not run on the server the lists may be empty.");
        }
    }

    private String getProblemShortNames(IInternalContest inContest, ProblemList problemList) {
        
        
        String problemListString = "";
        
        Problem[] problems = problemList.getList();
        if (problems.length > 0) {
            Arrays.sort(problems, new ProblemComparator(inContest));
//        Iterator<Problem> iter = Arrays.asList(problems).iterator();
            List<Problem> list = Arrays.asList(problems);
            
            List<String> problemStrings = list.stream() //
                    .flatMap(p -> Stream.of(p.getLetter()+" - "+p.getShortName())) //
                    .collect(Collectors.toList());
            
            problemListString = String.join(", ",  problemStrings);
        }
        
        return problemList.size() + " problems: "+problemListString;
    }

    /**
     * Extract and return a list of runs (from auto judge run list)
     * 
     * @param contest2
     * @param runList
     * @return
     */
    private List<Run> getRuns(IInternalContest contest2, List<AvailableAJRun> runs) {

        List<Run> list = new ArrayList<Run>();

        for (AvailableAJRun availableAJRun : runs) {
            Run run = contest.getRun(availableAJRun.getRunId());
            if (run == null) {
                System.err.println("Warning, could not find run in model for element id " + availableAJRun.getRunId());
            } else {
                list.add(run);
            }
        }

        return list;
    }

    @Override
    public String getReportTitle() {
        return "Autojudge Lists";
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;

    }

    @Override
    public boolean suppressHeaderFooter() {
        return false;
    }

    @Override
    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    @Override
    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

}
