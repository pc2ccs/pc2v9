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
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class ContestReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 8827529273455158045L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    protected String getContestXML() throws IOException {

        XMLMemento mementoRoot = XMLMemento.createWriteRoot("contest");

        Language[] languages = contest.getLanguages();
        for (Language language : languages) {
            IMemento languageMemento = mementoRoot.createChild("language");
            languageMemento.putString("name", language.toString());
            languageMemento.putBoolean("autofill", true);
        }

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            IMemento problemMemento = mementoRoot.createChild("problem");
            problemMemento.putString("name", problem.toString());
            if (problem.isUsingPC2Validator()) {
                problemMemento.putBoolean("useInternalValidator", true);
                problemMemento.putInteger("internalValidatorOption", problem.getWhichPC2Validator());
            }
            if (problem.getDataFileName() != null) {
                problemMemento.putString("datafilename", problem.getDataFileName());
            }

            if (problem.getAnswerFileName() != null) {
                problemMemento.putString("answerfilename", problem.getAnswerFileName());
            }
        }

        Run[] runs = contest.getRuns();

        Arrays.sort(runs, new RunComparator());
        for (Run run : runs) {

            String languageName = contest.getLanguage(run.getLanguageId()).toString();
            String problemName = contest.getProblem(run.getProblemId()).toString();

            RunFiles runFiles = contest.getRunFiles(run);

            IMemento runMemento = mementoRoot.createChild("Run");
            runMemento.putInteger("site", run.getSiteNumber());
            runMemento.putInteger("number", run.getNumber());
            runMemento.putInteger("site", run.getSiteNumber());
            runMemento.putLong("elapsed", run.getElapsedMins());

            runMemento.putString("languageName", languageName);
            runMemento.putString("problemName", problemName);

            if (runFiles != null) {
                String filename = runFiles.getMainFile().getName();
                if (filename != null) {
                    runMemento.putString("filename", filename);
                }
            }
        }

        return mementoRoot.saveToString();

    }

    public  void writeReport(PrintWriter printWriter) throws IOException {

        String xmlString = getContestXML();
        printWriter.println(xmlString);

    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    public void createReportFile(String filename, Filter filter) throws IOException {

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

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public String[] createReport(Filter arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String createReportXML(Filter arg0) {
        try {
            return getContestXML();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public String getReportTitle() {
        return "Contest";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Contest Report";
    }

     public Filter getFilter() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setFilter(Filter filter) {
        // TODO Auto-generated method stub
        
    }

}
