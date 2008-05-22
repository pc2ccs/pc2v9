package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Print All Filter Information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FilterReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -1515524938023295459L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    public void writeReport(PrintWriter printWriter) {

        // find any filters in contes and dump them here.

        throw new UnsupportedOperationException(); // TODO code
    }

    public void writeReportDetailed(PrintWriter printWriter, Filter inFilter) {

        filter = null;

        printWriter.println("              Filter: " + inFilter);

        printWriter.println("           Filter On: " + inFilter.isFilterOn());
        printWriter.println("     Filter problems: " + inFilter.isFilteringProblems());
        printWriter.println("    Filter languages: " + inFilter.isFilteringLanguages());
        printWriter.println("   Filter judgements: " + inFilter.isFilteringJudgements());
        printWriter.println("   Filter run states: " + inFilter.isFilteringRunStates());
        printWriter.println();

        ElementId[] elementIds = null;
        
        elementIds = inFilter.getProblemIdList();
        printWriter.println("-- " + elementIds.length + " Problems filtered --");
        for (ElementId elementId : elementIds) {
            Problem problem = contest.getProblem(elementId);
            if (problem == null) {
                printWriter.println("   Not displayed " + problem);
            } else {
                printWriter.println("   " + problem);
            }
        }

        printWriter.println();

        elementIds = inFilter.getLanguageIdList();
        printWriter.println("-- " + elementIds.length + " Languages filtered --");
        for (ElementId elementId : elementIds) {
            Language language = contest.getLanguage(elementId);
            if (language == null) {
                printWriter.println("   Not displayed " + language);
            } else {
                printWriter.println("   " + language);
            }
        }

        printWriter.println();

        elementIds = inFilter.getJudgementIdList();
        printWriter.println("-- " + elementIds.length + " Judgements filtered --");
        for (ElementId elementId : elementIds) {
            Judgement judgement = contest.getJudgement(elementId);
            if (judgement == null) {
                printWriter.println("   Not displayed " + judgement);
            } else {
                printWriter.println("   " + judgement);
            }
        }

        printWriter.println();

        ClientId[] clientIds = inFilter.getAccountList();
        printWriter.println("-- " + clientIds.length + " Accounts filtered --");
        for (ClientId clientId : clientIds) {
            Account account = contest.getAccount(clientId);
            if (account == null) {
                printWriter.println("   Not displayed " + account);
            } else {
                printWriter.println("   " + clientId + " " + account.getDisplayName());
            }
        }

        printWriter.println();

        RunStates[] runStatesList = inFilter.getRunStates();
        printWriter.println("-- " + runStatesList.length + " Run states filtered --");
        for (RunStates runStates : runStatesList) {
            printWriter.println("   " + runStates);
        }

        printWriter.println();

    }

    public void writeReport(PrintWriter printWriter, Filter inFilter) {

        // Filters
        printWriter.println();

        printWriter.println("  '" + inFilter + " " + inFilter);
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        filter = inFilter;

        try {
            printHeader(printWriter);

            try {
                writeReport(printWriter);
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printFooter(printWriter);

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

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Filters";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Filters Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
