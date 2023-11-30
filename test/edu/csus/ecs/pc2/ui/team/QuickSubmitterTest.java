// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui.team;

import java.io.File;
import java.util.List;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.LanguageUtilities;
import edu.csus.ecs.pc2.core.MockController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

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
            Language lang = LanguageUtilities.guessLanguage(contest, string);
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

    /**
     * Test QuickSubmitter send/add submissions.
     * 
     * @throws Exception
     */
    public void testsendSubmissions() throws Exception {

        ContestSnakeYAMLLoader snake = new ContestSnakeYAMLLoader();

        /**
         * Sample contest to submit from.
         */
        String sampleContestName = "tenprobs";

        String cdpRootPath = getTestSampleContestDirectory(sampleContestName);
        String cdpConfig = cdpRootPath + File.separator + IContestLoader.CONFIG_DIRNAME;

        IInternalContest contest = snake.fromYaml(null, cdpConfig);
        loadFullSampleContest(contest, sampleContestName);

        QuickSubmitter submitter = new QuickSubmitter();

        MockController controller = new MockController();
        contest.setClientId(new ClientId(contest.getSiteNumber(), Type.TEAM, 1));
        controller.setContest(contest);

        submitter.setContestAndController(contest, controller);

        int expectedFiles = 40;

        List<File> files = submitter.getAllCDPsubmissionFileNames(contest, cdpConfig);
        assertEquals(expectedFiles, files.size());

        int totSubmit = submitter.sendSubmissions(files);
        assertEquals(expectedFiles, totSubmit);
    }

}
