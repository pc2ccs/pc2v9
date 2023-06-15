// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.list;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.csus.ecs.pc2.core.LanguageUtilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;
import edu.csus.ecs.pc2.ui.SubmissionSolutionList;
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
     * @param submissionList list of SubmissionSampleLocation
     * @return
     */
    public static List<File> filterByJudgingTypes(List<File> files, SubmissionSolutionList submissionSolutionList) {

        List<File> newFileList = new ArrayList<File>();

        for (File file : files) {
            submissionSolutionList.forEach((subSol) -> {
                if (file.getAbsolutePath().contains(File.separator + subSol.getShortDirectoryName() + File.separator)) {
                    newFileList.add(file);
                }
            });
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
	public static List<File> getAllCDPsubmissionFileNames(IInternalContest mycontest, String directoryName) {

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

		return files;
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
            selectedLanguageList.forEach((lang) -> {
                
                // TODO match by extention
                Language language = LanguageUtilities.matchFirstLanguage(contest,  LanguageUtilities.getExtension(file.getName()));
                
                if (lang.getDisplayName().contentEquals(lang.getDisplayName())) {
                    newFileList.add(file);
                }
            });
        }

        return newFileList;

    }
    
}
