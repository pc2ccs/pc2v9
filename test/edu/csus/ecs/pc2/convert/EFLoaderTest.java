package edu.csus.ecs.pc2.convert;

import java.util.List;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class EFLoaderTest extends AbstractTestCase
{

	@SuppressWarnings("unused")
    public void testLoadSamp() throws Exception
	{
		EFLoader loader = new EFLoader();

//		ensureDirectory(SampleCDP.getDir());
//		startExplorer(SampleCDP.getDir());

		String filename = SampleCDP.getEventFeedFilename();
		assertFileExists(filename);

		List<EventFeedRun> runs = loader.loadFile(filename);

		assertEquals("Expecting runs", 1494, runs.size());

		String submissionsBasePath = SampleCDP.getSubmissionsDir();

		int runFileCount = loader.countRunsWithSubmissions(runs, submissionsBasePath);

		assertEquals("Expecting runs ", 1494, runFileCount);

		IInternalContest contest = new InternalContest();
		List<String> errors2 = loader.findAnyErrors(contest, SampleCDP.getDir(), runs);

		// TODO dry run add runs

	}

	//
	//    public void testLoad() throws Exception {
	//
	//        EFLoader loader = new EFLoader();
	//
	//        String ifn = "testdata/ef01.xml";
	//        List<EventFeedRun> runs = loader.loadFile(ifn);
	//
	//        String submissDir = "testdata/cdp/submissions";
	//        int fileCount = loader.countRunsWithSubmissions(runs, submissDir);
	//
	//        assertEquals("Expecting all runs to have submssions", runs.size(), fileCount);
	//
	//        Collections.sort(runs, new ComparatorById());
	//
	////        for (EventFeedRun run : runs) {
	////
	////            List<String> subfiles = loader.fetchRunFileNames(submissDir, run.getId());
	////            System.out.println("Run " + run.getId() + " team" + run.getTeam() + " " + run.getLanguage() + " " + run.getProblem());
	////            System.out.println("   files = " + subfiles.get(0));
	////        }
	//        
	//    }
	//
	//    public void testCheckForErrors() throws Exception {
	//        EFLoader loader = new EFLoader();
	//
	//        String ifn = "testdata/ef01.xml";
	//        List<EventFeedRun> runs = loader.loadFile(ifn);
	//        
	//        IInternalContest contest = createContest(runs);
	//
	//        String submissDir = "testdata/cdp/submissions";
	//
	//        List<String> errorList = checkForErrors(contest, runs);
	//        
	//        for (String error : errorList) {
	//            System.err.println(error);
	//        }
	//        
	//        assertTrue("Expecting no errors, found "+errorList.size(), errorList.isEmpty());
	//        
	//    }
	//
	//    
	//    private void fillLanguage(Language language, String[] values) {
	//        // values array
	//        // 0 Title for Language
	//        // 1 Compiler Command Line
	//        // 2 Executable Identifier Mask
	//        // 3 Execute command line
	//        // 4 is the Title again????
	//        // 5 isInterpreted
	//
	//        language.setCompileCommandLine(values[1]);
	//        language.setExecutableIdentifierMask(values[2]);
	//        language.setProgramExecuteCommandLine(values[3]);
	//        if (LanguageAutoFill.INTERPRETER_VALUE.equals(values[5])) {
	//            language.setInterpreted(true);
	//        } else {
	//            language.setInterpreted(false);
	//        }
	//    }
	//    
	//    /**
	//     * Create contest based on event feed runs.
	//     * 
	//     * @param runs
	//     * @return
	//     */
	//    private IInternalContest createContest(List<EventFeedRun> runs) {
	//     
	//        IInternalContest contest = new InternalContest();
	//        
	//        String[] languages = { "Java", "C++", "C",  LanguageAutoFill.JAVATITLE, LanguageAutoFill.DEFAULTTITLE, LanguageAutoFill.GNUCPPTITLE, LanguageAutoFill.PERLTITLE, LanguageAutoFill.MSCTITLE, "APL" };
	////        String[] problems = { "Sumit", "Quadrangles", "Routing", "Faulty Towers", "London Bridge", "Finnigans Bluff" };
	//        
	//        int numberOfTeams = 128;
	//        
	//        contest.generateNewAccounts(Type.TEAM.toString(), numberOfTeams, true);
	//
	//        for (String langName : languages) {
	//            Language language = new Language(langName);
	//            String[] values = LanguageAutoFill.getAutoFillValues(langName);
	//            if (values[0].trim().length() != 0) {
	//                fillLanguage(language, values);
	//            }
	//            contest.addLanguage(language);
	//        }
	//        
	//        // find max problem
	//        int maxProblem = 0;
	//        for (EventFeedRun eventFeedRun : runs) {
	//            int prob = Integer.parseInt(eventFeedRun.getProblem());
	//            maxProblem = Math.max(maxProblem,  prob);
	//        }
	//        
	//        for (int i = 0; i < maxProblem; i++) {
	//            Problem problem = new Problem("Problem "+Utilities.getProblemLetter(i+1));
	//            contest.addProblem(problem);
	////            System.out.println("Added "+problem);
	//        }
	//
	//        return contest;
	//    }
	//
	//    /**
	//     * Check for missing config items for input list of event feed runs.
	//     * 
	//     * @param contest
	//     * @param runs
	//     * @return
	//     */
	//    public List<String> checkForErrors (IInternalContest contest, List<EventFeedRun> runs)
	//    {
	//        List<String> list = new ArrayList<>();
	//        
	//        Map<String, Language> languageMap = new HashMap<String, Language>();
	//        
	//        Problem[] problems = contest.getProblems();
	//        
	//        Vector<Account> teams = contest.getAccounts(Type.TEAM);
	//        int totTeams = teams.size();
	//        
	//        Language[] languages = contest.getLanguages();
	//        for (Language language : languages) {
	//            languageMap.put(language.getDisplayName(), language);
	//        }
	//        
	//        for (EventFeedRun run : runs) {
	//            
	////            System.out.println("Run is "+run.getId());
	//            
	//            // Language
	//            
	//            if (languageMap.get(run.getLanguage()) == null) {
	//                list.add("Run " + run.getId() + " language not defined in contest: " + run.getLanguage());
	//            }
	//
	//            // Problem
	//            
	//            int problemNum = Integer.parseInt(run.getProblem());
	//            if (problemNum > problems.length || problemNum == 0) {
	//                list.add("Run " + run.getId() + " problem number not defined not in contest: " + problemNum);
	//
	//            }
	//            
	//            // Team
	//            
	//            int teamNum = Integer.parseInt(run.getTeam());
	//            if (teamNum > totTeams || teamNum == 0) {
	//                list.add("Run " + run.getId() + " team (id) not defined not in contest: " + teamNum);
	//            }
	//        }
	//        return list;
	//    }

}
