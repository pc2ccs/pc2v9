package edu.csus.ecs.pc2.core.imports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.imports.ccs.CCSListUtilities;
import edu.csus.ecs.pc2.ui.InvalidFieldValue;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Load Runs from a submissions.tsv file.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SubmissionsTSVFile implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -967846946268411808L;

    private boolean checkRunSubmissionFiles = false;

    private Problem[] problems = new Problem[0];

    private IInternalContest internalContest;

    private Language[] languages = new Language[0];

    private String sourceDirectoryName = ".";

    public Run[] loadRuns(String filename) throws IOException {

        // TODO someday set the sourceDirectoryName based on the parent of the filename

        String[] contents = Utilities.loadFile(filename);
        String[] lines = CCSListUtilities.filterOutCommentLines(contents);
        Run[] runs = createRuns(lines);
        return runs;
    }

    public Run createRun(String line) throws FileNotFoundException {

        // The file is a 5-field TSV file.
        // Field Description Example Type
        // 1 run id 7 integer
        // 2 team number 12 integer
        // 3 short problem name railway string
        // 4 submission time in MS id 2033497 long integer
        // 5 result/judgement acronym AC string

        String[] fields = line.split("\t");
        int fieldNo = 0;
        int runNumber = Integer.parseInt(fields[fieldNo++]);
        ClientId clientId = determineClient(fields[fieldNo++]);
        Problem problem = determineProblem(fields[fieldNo++]);
        long submittedMS = Long.parseLong(fields[fieldNo++]);

        Language language = getContest().getLanguages()[0];

        if (checkRunSubmissionFiles) {
            String mainfilename = fetchSubmissionFilename(runNumber);
            language = determineLanguage(mainfilename);
        }

        Run run = new Run(clientId, language, problem);
        run.setNumber(runNumber);
        run.setElapsedMS(submittedMS);

        return run;
    }

    public Problem determineProblem(String string) {
        for (Problem problem : problems) {
            if (problem.getDisplayName().equalsIgnoreCase(string)) {
                return problem;
            }

            if (string.length() == 1) {
                /**
                 * use letter as offset
                 */
                char let = string.toUpperCase().charAt(0);
                if (Character.isLetter(let)) {
                    int offset = let - 'A';
                    if (offset >= 0 && offset < problems.length) {
                        return problems[offset];
                    }
                }
            }

            if (problem.getShortName().equalsIgnoreCase(string)) {
                return problem;
            }
        }

        throw new InvalidFieldValue("Problem " + string + " not matched/found");
    }
    
    int getSiteNumber(){
        if (getContest() != null){
            return getContest().getSiteNumber();
        } else {
            return 1;
        }
    }

    private ClientId determineClient(String string) {
        int teamNumber = Integer.parseInt(string);
        return new ClientId(getSiteNumber(), Type.TEAM, teamNumber);
    }

    private IInternalContest getContest() {
        return internalContest;
    }

    public Language determineLanguage(String mainfilename) {
        String[] langDefs = { //
        ".cpp=GNU C++", //
                ".C=GNU C++", //
                ".java=Java", //
                ".c=GNU C", //
                ".c=C", //
                ".cpp=C++", ".C=C++", //
                ".dpr=Pascal/Kylix", //
                ".pas=Pascal/Kylix", //
        };

        int lastOccurIndex = mainfilename.lastIndexOf('.');
        if (lastOccurIndex == -1) {
            // no extension
            return null;
        }
        String extension = mainfilename.substring(lastOccurIndex);

        for (String string : langDefs) {
            String[] fields = string.split("=");
            if (fields[0].trim().equals(extension)) {
                Language language = findMatchingLanguage(fields[1]);
                if (language != null) {
                    return language;
                }
            }
        }

        throw new InvalidFieldValue("Unable to determine language for file " + mainfilename);
    }

    public final Language findMatchingLanguage(String name) {

        for (Language language : languages) {
            if (language.getDisplayName().equalsIgnoreCase(name)) {
                return language;
            }
        }

        return null;
    }

    public String getSubmissionDirctoryName(int runNumber) {
        return sourceDirectoryName + File.separator + "run" + String.format("%04d", runNumber);
    }

    /**
     * Find a filename for the input run number
     * 
     * @param runNumber
     * @return name of file
     */
    public String fetchSubmissionFilename(int runNumber) throws FileNotFoundException {
        String submissonFileDirectoryname = getSubmissionDirctoryName(runNumber);
        File dir = new File(submissonFileDirectoryname);
        if (dir.isDirectory()) {
            File[] list = dir.listFiles();
            for (File file : list) {
                if (file.isFile()) {
                    return file.getAbsolutePath();
                }
            }
        } else {
            throw new FileNotFoundException("No directory for submission files found at " + submissonFileDirectoryname);
        }
        throw new FileNotFoundException("No submission files found for run " + runNumber + " in dir " + submissonFileDirectoryname);
    }

    public Run[] createRuns(String[] lines) throws FileNotFoundException {

        ArrayList<Run> runList = new ArrayList<Run>();

        for (String line : lines) {
            Run run = createRun(line);
            runList.add(run);
        }

        return (Run[]) runList.toArray(new Run[runList.size()]);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        setContest(internalContest);
        // internalController = inController;
    }

    public String fetchSubmissionFilename(Run run) throws FileNotFoundException {
        return fetchSubmissionFilename(run.getNumber());
    }

    public String getPluginTitle() {
        return "Load submissions.tsv";
    }

    public boolean isCheckRunSubmissionFiles() {
        return checkRunSubmissionFiles;
    }

    public void setCheckRunSubmissionFiles(boolean checkRunSubmissionFiles) {
        this.checkRunSubmissionFiles = checkRunSubmissionFiles;
    }

    public void setContest(IInternalContest inContest) {
        internalContest = inContest;

        languages = getContest().getLanguages();
        problems = getContest().getProblems();
    }

    public void setSourceDirectoryName(String sourceDirectoryName) {
        this.sourceDirectoryName = sourceDirectoryName;
    }

    public String getSourceDirectoryName() {
        return sourceDirectoryName;
    }

}
