package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Print all problems info.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 977130815676827828L;

    private IContest contest;

    private IController controller;

    private Log log;

    private void writeRow(PrintWriter printWriter, Problem problem, ProblemDataFiles problemDataFiles) {
        printWriter.println("  Problem '" + problem + "' ver="+ problem.getElementId().getVersionNumber()+" id=" + problem.getElementId());
        printWriter.println("       Data file name   : " + problem.getDataFileName());
        printWriter.println("       Answer file name : " + problem.getAnswerFileName());
        printWriter.print("   Execution time limit : " + problem.getTimeOutInSeconds() + " seconds");
        if (problem.getTimeOutInSeconds() == 0) {
            printWriter.print(" (no time limit when zero seconds)");
        }
        printWriter.println();
        
        printWriter.println("        Using validator : " + problem.isValidatedProblem());
        printWriter.println("         Validator name : " + problem.getValidatorProgramName());
        
        printWriter.println("     Validator cmd line : " + problem.getValidatorCommandLine());
        printWriter.println("     Validator option # : " + problem.getWhichPC2Validator());
        printWriter.println("    Using pc2 validator : " + problem.isUsingPC2Validator());
        
        if (problem.getAnswerFileName() != null) {
            if (problemDataFiles != null) {
                if (problemDataFiles.getJudgesAnswerFiles().length == 0) {
                    printWriter.println("                          Warning - no answer files defined (no contents) ");
                }
            } else {
                printWriter.println("                          Warning - no data/answer files defined (null problemDataFiles) ");
            }
        }

        if (problem.getAnswerFileName() != null) {
            if (problemDataFiles != null) {
                if (problemDataFiles.getJudgesDataFiles().length == 0) {
                    printWriter.println("                          Warning - no judges data files defined (no contents) ");
                }
            } else {
                printWriter.println("                          Warning - no data/judge files defined (null problemDataFiles) ");
            }
        }

        writeProblemDataFiles(printWriter, problemDataFiles);
    }

    private void writeProblemDataFiles(PrintWriter printWriter, ProblemDataFiles problemDataFiles) {
        if (problemDataFiles != null) {
            SerializedFile[] judgesDataFiles = problemDataFiles.getJudgesDataFiles();
            SerializedFile[] judgesAnswerFiles = problemDataFiles.getJudgesAnswerFiles();
            SerializedFile validatorFile = problemDataFiles.getValidatorFile();

            if (judgesDataFiles != null) {

                printWriter.println("                  " + judgesDataFiles.length + " judge data files");

                if (judgesDataFiles.length > 0) {
                    for (SerializedFile serializedFile : judgesDataFiles) {
                        int bytes = 0;
                        if (serializedFile.getBuffer() != null) {
                            bytes = serializedFile.getBuffer().length;
                        }
                        printWriter.println("                    judge data file '" + serializedFile.getName() + "' " + bytes + " bytes");
                    }
                }
            } else {
                printWriter.println("                  * No judge's data files *");
            }

            if (judgesAnswerFiles != null) {

                printWriter.println("                  " + judgesAnswerFiles.length + " judge answer files");
                if (judgesAnswerFiles.length > 0) {
                    for (SerializedFile serializedFile : judgesAnswerFiles) {
                        int bytes = 0;
                        if (serializedFile.getBuffer() != null) {
                            bytes = serializedFile.getBuffer().length;
                        }
                        printWriter.println("                    judge ans. file '" + serializedFile.getName() + "' " + bytes + " bytes");
                    }
                }
            } else {
                printWriter.println("                  * No judge's answer files *");
            }
            
            if (validatorFile != null) {
                printWriter.println("                  " + 1 + " validator file");
                int bytes = 0;
                if (validatorFile.getBuffer() != null) {
                    bytes = validatorFile.getBuffer().length;
                }
                printWriter.println("                    validator file '" + validatorFile.getName() + "' " + bytes + " bytes");
            } else {
                printWriter.println("                  * No validator files *");
            }
        } else {
            printWriter.println("                  * No judge's files *");
        }

    }

    private void writeReport(PrintWriter printWriter) {

        // Problem
        printWriter.println();
        printWriter.println("-- " + contest.getProblems().length + " problems --");
        for (Problem problem : contest.getProblems()) {
            printWriter.println();
            ProblemDataFiles problemDataFiles = contest.getProblemDataFile(problem);
            writeRow(printWriter, problem, problemDataFiles);
        }

        // ProblemDataFiles
        printWriter.println();
        printWriter.println("-- " + contest.getProblemDataFiles().length + " problem data file sets --");
        for (ProblemDataFiles problemDataFile : contest.getProblemDataFiles()) {
            printWriter.println();
            Problem problem = contest.getProblem(problemDataFile.getProblemId());
            printWriter.println("  Problem Data File set for " + problem + " id=" + problemDataFile.getProblemId());
            writeProblemDataFiles(printWriter, problemDataFile);
        }
        
        if (contest.getGeneralProblem() == null){
            printWriter.println(" General Problem: (not defined) ");
        } else {
            printWriter.println(" General Problem: "+contest.getGeneralProblem().getElementId());
        }
        
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + new Date());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
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

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Problems";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Problems Report";
    }

}
