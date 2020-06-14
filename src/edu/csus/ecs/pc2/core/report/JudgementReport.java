// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;

/**
 * Print All Judgement Information including reject.ini file
 * 
 * @author pc2@ecs.csus.edu
 */

public class JudgementReport implements IReport {

    public static final String NO_JUDGEMENT_PREFIX = "No - ";

    /**
     * 
     */
    private static final long serialVersionUID = -287199138291014045L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;
    
    private Filter filter;

    public void writeReport(PrintWriter printWriter) {
        
        // Active Judgements
        printWriter.println();
        Judgement [] judgements = contest.getJudgements();
        
        printWriter.println("-- " + judgements.length + " Judgements --");
        printWriter.println("     Active Judgements");
        for (Judgement judgement : judgements) {
            if (! judgement.isActive()){
                continue;
            }
            printWriter.print("  '" + judgement + "'");
            printWriter.print(" acronym=" + judgement.getAcronym());
            printWriter.print(" id=" + judgement.getElementId());
            printWriter.println();
        }
        
        printWriter.println("     All Judgements");
        for (Judgement judgement : judgements) {
            String hiddenText = "";
            if (!judgement.isActive()){
                hiddenText = "[HIDDEN] ";
            }
            printWriter.print("  '" + judgement );
            printWriter.print("' ");
            printWriter.print(" acronym=" + judgement.getAcronym());
            printWriter.print(hiddenText+" id=" + judgement.getElementId());
            printWriter.println();
        }
        
        printWriter.println();
        printWriter.println("# reject.ini");
        printWriter.println("#");
        printRejectINI(printWriter, contest);
        printWriter.println();
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }
    
    public void printRejectINI(PrintWriter printWriter, IInternalContest inContest) {
        
        Judgement [] judgements = inContest.getJudgements();
        for (Judgement judgement : judgements) {
            
//            Compilation Error|CE
//            Run-time Error|RTE

            String judgmentName = judgement.getDisplayName();
            if  (judgmentName.startsWith(NO_JUDGEMENT_PREFIX)){
                judgmentName = judgmentName.substring(5);
            }
            
            printWriter.println(judgmentName + "|" + judgement.getAcronym());
        }
    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

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

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "Judgements";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Judgements Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
