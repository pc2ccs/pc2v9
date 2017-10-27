package edu.csus.ecs.pc2.ui.team;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import edu.csus.ecs.pc2.core.IInternalController;
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
            }
            else {
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
     * @param contest
     * @param cdpConfigdir CDP config/ dir
     * @return
     */
    public List<File> getAllCDPsubmissionFileNames(IInternalContest contest, String cdpConfigdir) {

        Problem[] problems = contest.getProblems();
        List<File> files = new ArrayList<>();

        for (Problem problem : problems) {

            //            config\sumit\submissions\accepted\ISumit.java
            String probSubmissionDir = cdpConfigdir + File.separator + problem.getShortName() + File.separator + IContestLoader.SUBMISSIONS_DIRNAME;
            files.addAll(findAll(probSubmissionDir));

        }

        return files;
    }

    /**
     * submit runs for all input files.
     * 
     * Will guess langauge and problem based on path
     * 
     * @see #guessLanguage(IInternalContest, String)
     * @see #guessProblem(IInternalContest, String)
     * 
     * @param submitFiles
     */
    public void sendSubmissions(List<File> submitFiles) {

        for (File file : submitFiles) {
            try {

                Language language = guessLanguage(getContest(), file.getAbsolutePath());
                if (language == null) {
                    String ext = getExtension(file.getAbsolutePath());
                    System.err.println("Cannot identify language for ext= " + ext + " = Can not send submission for file " + file.getAbsolutePath());
                } else {
                    Problem problem = guessProblem(getContest(), file.getAbsolutePath());
                    try {
                        controller.submitRun(problem, language, file.getAbsolutePath(), null);
                        System.out.println("submitted run send with language " + language + " and problem " + problem);
                    } catch (Exception e) {
                        System.err.println("Warning problem sending run for file " + file.getAbsolutePath() + " " + e.getMessage());
                        log.log(Level.WARNING, "problem sending run for file " + file.getAbsolutePath() + " " + e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.log(Level.WARNING, "problem sending run for file " + file.getAbsolutePath() + " " + e.getMessage(), e);
            }
        }
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
            if (absolutePath.indexOf(problem.getShortName()) != -1) {
                return problem;
            }
        }
        return null;
    }

    /**
     * Get extension for file
     * @param filename
     */
    public String getExtension(String filename) {
        String extension = filename;
        int idx = filename.indexOf(".");
        if (idx != -1) {
            extension = filename.substring(idx + 1, filename.length());
        }
        return extension;
    }

    // TODO REFACTOR - move guessLanguage and guessProblem to Utilities or FileUtilities

    /**
     * Guess language based on filename.
     */
    public Language guessLanguage(IInternalContest contest, String filename) {
        String extension = getExtension(filename);
        return matchFirstLanguage(contest, extension);
    }

    private Language matchFirstLanguage(IInternalContest inContest, String extension) {
        Language[] lang = inContest.getLanguages();

        // Alas guessing 
        if ("cpp".equals(extension)) {
            extension = "C++";
        }

        if ("py".equals(extension)) {
            extension = "Python";
        }

        if ("cs".equals(extension)) {
            extension = "Mono";
        }

        if ("pl".equals(extension)) {
            extension = "Perl";
        }

        extension = extension.toLowerCase();

        for (Language language : lang) {
            if (language.getDisplayName().toLowerCase().indexOf(extension) != -1) {
                return language;
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
}
