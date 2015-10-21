package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.ClientSettingsComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Print all Auto Judging Settings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ClientSettingsReport.java 3070 2015-07-15 00:40:21Z boudreat $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/report/ClientSettingsReport.java $
public class AutoJudgingSettingsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();
    
    private int autoJudgeCount = 0;

    /**
     * Return a list of comma delimited problem names.
     * 
     * <P>
     * returns "none selected" if no problems in filter <br>
     * returns "none active selected" if problems in the filter are all deactivated <br>
     * 
     * @param filter
     * @return
     */
    private String getProblemlist(Filter inFilter) {
        ElementId[] elementIds = inFilter.getProblemIdList();

        if (elementIds.length == 0) {
            return "(none selected)";
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (Problem problem : contest.getProblems()) {
            for (ElementId elementId : elementIds) {
                if (problem.getElementId().equals(elementId)) {
                    stringBuffer.append(problem.getDisplayName());
                    stringBuffer.append(", ");
                }
            }
        }

        if (stringBuffer.length() > 0) {
            // stringBuffer.length() - 2 used to strip off trailing ", "
            return new String(stringBuffer).substring(0, stringBuffer.length() - 2);
        } else {
            return "(none active selected)";
        }
    }

    private String enabledString(boolean b) {
        if (b) {
            return "is ENABLED";
        } else {
            return "is not enabled";
        }
    }
    
    protected String getProblemLetter(Problem problem) {
        char let = 'A';
        int count = 0;

        for (Problem problem2 : contest.getProblems()) {
            if (problem2.equals(problem)) {
                let += count;
            }
            count++;
        }
        return "" + let;
    }

    public void writeRow(PrintWriter printWriter, ClientSettings clientSettings) {
        

        if (isJudge(clientSettings.getClientId())) {
            printWriter.print(clientSettings.getClientId());

            Filter clientFilter = clientSettings.getAutoJudgeFilter();

            printWriter.println(" auto judge " + enabledString(clientSettings.isAutoJudging()));
            if (clientFilter != null) {
                ElementId[] elementIds = clientFilter.getProblemIdList();
                printWriter.println("     Auto judging " + elementIds.length + " problems : " + getProblemlist(clientFilter));
            }
            
            writeProblemList("         ", printWriter, clientFilter);
            autoJudgeCount ++;
            
            printWriter.println();
            printWriter.println();
        }
    }

    private boolean isJudge(ClientId id) {
        return id != null && id.getClientType().equals(ClientType.Type.JUDGE);
    }

    public void writeReport(PrintWriter printWriter) {
        // ClientSettings
        printWriter.println();
        ClientSettings[] clientSettingsList = contest.getClientSettingsList();

        Arrays.sort(clientSettingsList, new ClientSettingsComparator());
        
        autoJudgeCount  = 0;

        for (ClientSettings clientSettings : clientSettingsList) {
            try {
                writeRow(printWriter, clientSettings);
            } catch (Exception e) {
                e.printStackTrace(printWriter);
            }
        }
        
        printWriter.println("There are "+autoJudgeCount+" auto judges");
        printWriter.println();
        
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

    void writeProblemList(String pad, PrintWriter printWriter, Filter problemList){

        ElementId[] elementIds = problemList.getProblemIdList();

        if (elementIds.length == 0) {
            printWriter.println(pad+"(none selected)");
        }

        for (Problem problem : contest.getProblems()) {
            for (ElementId elementId : elementIds) {
                if (problem.getElementId().equals(elementId)) {
                    printWriter.println(pad + getProblemLetter(problem)+" - "+problem.getDisplayName());
                }
            }
        }
    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "Auto Judging Settings";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Auto Judging Settings Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
