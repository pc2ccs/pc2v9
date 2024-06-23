// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.list;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.csus.ecs.pc2.core.LanguageUtilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;
import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidator;

/**
 * A set of utilities that operate on lists.
 *
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class ListUtilities {

    /**
     * Returns list of files that match the SubmissionSolutionList (judging types location/list)
     *
     * @param files list of files to be filtered
     * @param submissionSolutionList List of types to accept
     * @return
     */
    public static List<File> filterByJudgingTypes(List<File> files, List<String> submissionSolutionList) {

        List<File> newFileList = new ArrayList<File>();
        int idx;

        for (File file : files) {
            String filePath = file.getAbsolutePath();
            for(String subSol : submissionSolutionList) {
                idx = subSol.indexOf(':');
                if(idx > 0) {
                    subSol = subSol.substring(0, idx).trim();
                }
                if (filePath.contains(File.separator + subSol + File.separator)) {
                    newFileList.add(file);
                }
            }
        }

        return newFileList;
    }

    /**
     * Returns list of all files under dir path.
     *
     * @param dirName location to fetch files from
     * @return list of files in and under dirName
     */
    // TODO REFACTOR replace findAll from QuickSubmitter with this method.
    public static List<File> findAllFiles(String dirName) {

        List<File> files = new ArrayList<>();

        File dir = new File(dirName);
        File[] entries = dir.listFiles();

        if (entries == null) {
            return files;
        }

        for (File f : entries) {
            if (f.isDirectory()) {
                List<File> subList = findAllFiles(f.getAbsolutePath());
                files.addAll(subList);
            } else {
                files.add(f);
            }
        }

        return files;
    }

    /**
     * Get all CDP judge's sample submission filenames.
     *
     * Example files under config\sumit\submissions
     *
     * @param mycontest
     * @param directoryName CDP config/ dir or CDP base directory
     * @return list of judges sample submissions
     */
    // TODO REFACTOR remove getAllCDPsubmissionFileNames from QuickSubmitter
    public static List<File> getAllJudgeSampleSubmissionFilenamesFromCDP(IInternalContest mycontest, String directoryName) {

        Problem[] problems = mycontest.getProblems();
        List<File> files = new ArrayList<>();

        String configDir = directoryName + File.separator + IContestLoader.CONFIG_DIRNAME;
        if (new File(configDir).isDirectory()) {
            directoryName = configDir;
        }

        for (Problem problem : problems) {

            // config\sumit\submissions\accepted\ISumit.java
            String probSubmissionDir = directoryName + File.separator + problem.getShortName() + File.separator
                    + IContestLoader.SUBMISSIONS_DIRNAME;
            files.addAll(findAllFiles(probSubmissionDir));

        }

        HashSet<String> allExts = new HashSet<String>();
        String ext, srcFile;
        int ridx;

        // make up hashset of all language extensions
        for(Language lang : mycontest.getLanguages()) {
            allExts.addAll(lang.getExtensions());
        }
        // Reverse scan for uninteresting files and remove them.
        for(int i = files.size(); --i >= 0; ) {
            srcFile = files.get(i).getName();
            ridx = srcFile.lastIndexOf('.');
            // if no extension on the file, or, it's not in our list of src extensions, drop the file.
            if(ridx == -1 || !allExts.contains(srcFile.substring(ridx+1))) {
                files.remove(i);
            }
        }

        return files;
    }

    /**
     * Gets a list of all the different types of judges' submissions by scanning the submissions folder for
     * each problem.  ex. accepted, wrong_answer, time_limit_exceeded, other, run_time_exception, etc.
     * To qualify as a valid type, the particular folder must contain at least one source file with a known
     * extension for the configurationed languages.
     *
     * @param mycontest The contest
     * @param directoryName Where to start looking (config folder)
     * @return List<String> of folder basenames
     */
    public static List<String> getAllCDPSubmissionTypes(IInternalContest mycontest, String directoryName) {
        List <File> files = getAllJudgeSampleSubmissionFilenamesFromCDP(mycontest, directoryName);
        String srcName;
        int idx;
        ArrayList<String> types = new ArrayList<String>();
        HashSet<String> uniqTypes = new HashSet<String>();

        for(File src : files ) {
            srcName = src.getParent();
            idx = srcName.lastIndexOf(File.separator);
            if(idx != -1) {
                srcName = srcName.substring(idx+1);
            }
            if(!uniqTypes.contains(srcName)) {
                types.add(srcName);
                uniqTypes.add(srcName);
            }
        }
        types.sort(null);
        return(types);
    }

    /**
     * Find files (in CDP) that contain one of the Problems in the list.
     *
     */
    public static List<File> filterByProblems(List<File> files, List<Problem> selectedProblemList) {

        List<File> newFileList = new ArrayList<File>();

        for (File file : files) {
            selectedProblemList.forEach((prob) -> {
                if (file.getAbsolutePath().contains(File.separator + prob.getShortName() + File.separator)) {
                    newFileList.add(file);
                }
            });
        }

        return newFileList;

    }

    /**
     * Find all accepted/Yes filenames with  "accepted" in file list.
     *
     * @param files list of files
     * @return list of files that have a directory name of "accepted" embedded in their path.
     */
    public static List<File> filterYesJudgingType (List<File> files) {

        List<File> newFileList = new ArrayList<File>();

        for (File file : files) {
            if (file.getAbsolutePath().contains(File.separator + ClicsValidator.CLICS_CORRECT_ANSWER_MSG + File.separator)) {
                newFileList.add(file);
            }
        }

        return newFileList;
    }

    public static List<File> filterByLanguages(List<File> files, IInternalContest contest, List<Language> selectedLanguageList) {

        List<File> newFileList = new ArrayList<File>();

        for (File file : files) {
           Language language = LanguageUtilities.guessLanguage(contest,  file.getName());

           if(language != null) {
               selectedLanguageList.forEach((lang) -> {

                    // Should use clics_id, but, I suppose it may not be there - is it required? -- JB
                    if (lang.getDisplayName().contentEquals(language.getDisplayName())) {
                        newFileList.add(file);
                    }
                });
           }
        }

        return newFileList;

    }
}
