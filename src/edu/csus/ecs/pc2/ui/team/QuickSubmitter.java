// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui.team;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.LanguageUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Submit runs from CDP submissions/.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class QuickSubmitter implements UIPlugin {

    private static final long serialVersionUID = 7640178138750506786L;

    private IInternalController controller;

    private IInternalContest contest;

    private Log log;

    /**
     * Returns list of all files under dir path.
     * 
     * @param dir
     * @return
     */
    public static List<File> findAll(String dirName) {

        List<File> files = new ArrayList<>();

        File dir = new File(dirName);
        File[] entries = dir.listFiles();

        if (entries == null) {
            return files;
        }

        for (File f : entries) {
            if (f.isDirectory()) {
                List<File> subList = findAll(f.getAbsolutePath());
                files.addAll(subList);
            } else {
                files.add(f);
            }
        }

        return files;
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
        log = controller.getLog();

    }

    /**
     * Get all CDP submission filenames.
     * 
     * Example files under config\sumit\submissions
     * 
     * @param mycontest
     * @param cdpConfigdir CDP config/ dir
     * @return
     */
    public List<File> getAllCDPsubmissionFileNames(IInternalContest mycontest, String cdpConfigdir) {

        Problem[] problems = mycontest.getProblems();
        List<File> files = new ArrayList<>();

        for (Problem problem : problems) {

            //            config\sumit\submissions\accepted\ISumit.java
            String probSubmissionDir = cdpConfigdir + File.separator + problem.getShortName() + File.separator + IContestLoader.SUBMISSIONS_DIRNAME;
            files.addAll(findAll(probSubmissionDir));

        }

        return files;
    }
    
    
    public File sendSubmission(File sourceFile) throws Exception {
        File submittedFile = null;

        Language language = LanguageUtilities.guessLanguage(getContest(), sourceFile.getAbsolutePath());
        if (language == null) {
            String ext = LanguageUtilities.getExtension(sourceFile.getAbsolutePath());
            throw new Exception("Cannot identify language for ext= " + ext + " = Can not send submission for file " + sourceFile.getAbsolutePath());
        } else {
            Problem problem = guessProblem(getContest(), sourceFile.getAbsolutePath());
            controller.submitJudgeRun(problem, language, sourceFile.getAbsolutePath(), null);
            System.out.println("submitted run send with language: " + language + " and problem: " + problem.getShortName() + " title:" + problem + " as " + getContest().getClientId());
            submittedFile = sourceFile;
        }

        return submittedFile;
    }


    /**
     * submit runs for all input files.  Guesses language and problem from file path and extension.
     * 
     * Will guess langauge and problem based on path
     * 
     * @see #guessLanguage(IInternalContest, String)
     * @see #guessProblem(IInternalContest, String)
     * 
     * @param a list of files to submit
     * @return count of files sucessfully submitted/added.
     */
    public int sendSubmissions(List<File> filesToSubmit) {

        int numberSubmitted = 0;
        
        for (File file : filesToSubmit) {
            try {

                Language language = LanguageUtilities.guessLanguage(getContest(), file.getAbsolutePath());
                if (language == null) {
                    String ext = LanguageUtilities.getExtension(file.getAbsolutePath());
                    log.log(Level.WARNING, "Cannot identify language for ext= " + ext + " = Can not send submission for file " + file.getAbsolutePath());
                } else {
                    Problem problem = guessProblem(getContest(), file.getAbsolutePath());
                    try {
                        controller.submitJudgeRun(problem, language, file.getAbsolutePath(), null);
                        log.log(Level.INFO, "submitted run with language " + language + " and problem " + problem);
                        numberSubmitted++;
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "problem sending run for file " + file.getAbsolutePath() + " " + e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.log(Level.SEVERE, "problem sending run for file " + file.getAbsolutePath() + " " + e.getMessage(), e);
            }
        }
        
        return numberSubmitted;
    }

    /**
     * Guess problem based on short problem name found in path.
     * 
     * @param contest2
     * @param absolutePath
     * @return
     */
    private Problem guessProblem(IInternalContest contest2, String absolutePath) {
        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            String problemPath = IContestLoader.CONFIG_DIRNAME + File.separator + problem.getShortName() + File.separator;
            if (absolutePath.indexOf(problemPath) != -1) {
                return problem;
            }
        }
        return null;
    }

    public IInternalContest getContest() {
        return contest;
    }

    @Override
    public String getPluginTitle() {
        return "Quick Submitter";
    }

    /**
     * List of files that match filter.
     * 
     * @param files
     * @param submitYesSamples output all AC/Yes sample file name
     * @param submitNoSamples output all non AC/Yes sample file name
     * @return list of files matching filter.
     */
    public static List<File> filterRuns(List<File> files, boolean submitYesSamples, boolean submitNoSamples) {
        
        List<File> outFiles = new ArrayList<>();
        for (File file : files) {
            String path = file.getAbsolutePath().replace("\\",  "/");
            boolean isYes = path.indexOf("/accepted/") != -1;
            
            if (submitYesSamples && isYes){
                outFiles.add(file);
            }
            if (submitNoSamples && !isYes){
                outFiles.add(file);
            }
        }
        
        return outFiles;
    }
    

    public List<File> getAllCDPsubmissionFileNames(IInternalContest myContest, String cdpPath, boolean submitYesSamples, boolean submitNoSamples) throws FileNotFoundException {

        File configDir = FileUtilities.findCDPConfigDirectory(new File(cdpPath));

        if (configDir == null || (!configDir.isDirectory())) {
            throw new FileNotFoundException("No such CDP directory: " + cdpPath);
        }

        List<File> files = getAllCDPsubmissionFileNames(myContest, configDir.getAbsolutePath());

        if (submitNoSamples || submitYesSamples) {
            files = filterRuns(files, submitYesSamples, submitNoSamples);
        }

        return files;
    }
}
