package edu.csus.ecs.pc2.ui.team;

import java.io.File;
import java.util.List;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class QuickSubmitterTest extends AbstractTestCase {

    void addOtherLangugages(IInternalContest contest)
    {

        String[] languages = { LanguageAutoFill.MONOCSHARPTITLE, LanguageAutoFill.PYTHON3TITLE, LanguageAutoFill.PYTHONTITLE, LanguageAutoFill.RUBYTITLE };

        for (String langName : languages) {
            Language language = new Language(langName);
            String[] values = LanguageAutoFill.getAutoFillValues(langName);
            if (values[0].trim().length() != 0) {
                fillLanguage(language, values);
            }
            contest.addLanguage(language);
        }

    }

    private void fillLanguage(Language language, String[] values) {
        // values array
        // 0 Title for Language
        // 1 Compiler Command Line
        // 2 Executable Identifier Mask
        // 3 Execute command line
        // 4 is the Title again????
        // 5 isInterpreted
        // 6 is the ID

        language.setCompileCommandLine(values[1]);
        language.setExecutableIdentifierMask(values[2]);
        language.setProgramExecuteCommandLine(values[3]);
        if (LanguageAutoFill.INTERPRETER_VALUE.equals(values[5])) {
            language.setInterpreted(true);
        } else {
            language.setInterpreted(false);
        }
        language.setID(values[6]);
    }

    public void testMatchLanguage() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        IInternalController controller = sample.createController(contest, true, false);

        addOtherLangugages(contest);

        QuickSubmitter submitter = new QuickSubmitter();
        submitter.setContestAndController(contest, controller);

        String[] filenames = {
                "hello.c",
                "hello.java",
                "hello.pl",
                "hello.cpp",
                "hello.py",
                "hello.mono",
                //                "/test/cdps/ka/config/sumit/submissions/accepted/isumit.dpr",
                "/test/cdps/ka/config/sumit/submissions/accepted/isumit.pl",
                "/test/cdps/ka/config/sumit/submissions/accepted/isumit.py",
                "/test/cdps/ka/config/sumit/submissions/run_time_error/sumitRTE.c",
                "/test/cdps/ka/config/sumit/submissions/run_time_error/sumitRTE.cpp",
                //                "/test/cdps/ka/config/sumit/submissions/run_time_error/sumitRTE.dpr",
                "/test/cdps/ka/config/sumit/submissions/run_time_error/sumitRTE.pl",
                "/test/cdps/ka/config/sumit/submissions/security_violation/SumitSVFileWrite.c",
                "/test/cdps/ka/config/sumit/submissions/security_violation/SumitSVFileWrite.cpp",
                "/test/cdps/ka/config/sumit/submissions/security_violation/SumitSVFileWrite.cs",
                //                "/test/cdps/ka/config/sumit/submissions/security_violation/SumitSVFileWrite.dpr",

        };

        for (String string : filenames) {
            Language lang = submitter.guessLanguage(contest, string);
            assertNotNull("Expected to match extension for file " + string, lang);
        }

    }

    public void testfilter() throws Exception {

        List<File> files = QuickSubmitter.findAll("samps/contests/mini/config/sumit/submissions");
//
//        int count = 1;
//        for (File file : files) {
//            System.out.println("debug " + count + " " + file.getAbsolutePath());
//            count++;
//        }

        List<File> matchingFiles = QuickSubmitter.filterRuns(files, true, false);
        assertEquals("Expected number of yes submission files ", 7, matchingFiles.size());

        matchingFiles = QuickSubmitter.filterRuns(files, false, true);
        assertEquals("Expected number of no submission files ", 26, matchingFiles.size());

        matchingFiles = QuickSubmitter.filterRuns(files, true, true);
        assertEquals("Expected number of all submission files ", 33, matchingFiles.size());

    }

    //    /**
    //     * Actually submit runs from cdp path
    //     * @throws Exception
    //     */
    //    public void testSubmit() throws Exception {
    //        
    //        ContestSnakeYAMLLoader snake = new ContestSnakeYAMLLoader();
    //        
    //        String  cdpPath = "/test/cdps/ka/config";
    //        IInternalContest contest = snake.fromYaml(null, cdpPath);
    //        
    //        QuickSubmitter submitter = new QuickSubmitter();
    //        submitter.setContestAndController(contest, null);
    //        
    //        List<File> files = submitter.getAllCDPsubmissionFileNames(contest,cdpPath);
    //        
    //        submitter.sendSubmissions(files);
    //    }

}
