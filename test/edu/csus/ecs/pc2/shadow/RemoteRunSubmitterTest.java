package edu.csus.ecs.pc2.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.MockController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IFile;
import edu.csus.ecs.pc2.core.model.IFileImpl;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

public class RemoteRunSubmitterTest extends AbstractTestCase{
    
    
    /**
     * Test submitrun 
     * @throws Exception
     */
    public void testOneSubmit() throws Exception {
        
        String contestDBdir = getOutputDataDirectory(getName());
        
        ensureDirectory(contestDBdir);
        IStorage storage = new FileStorage(contestDBdir);
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        
        contest.setStorage(storage);
        
        contest.generateNewAccounts(Type.FEEDER.toString(), 2, true);
        Account feeder = getRandomAccount(contest, Type.FEEDER);
        setCCSMode (contest, feeder);
        
        assertTrue("Expecting shadow for feeder ",contest.isAllowed(feeder.getClientId(), Permission.Type.SHADOW_PROXY_TEAM));
        assertTrue("Not CCS Test ", contest.getContestInformation().isCcsTestMode());
        
        IInternalController controller = new MockController();
        controller.initializeServer(contest);
        
        Account[] teams = getTeamAccounts(contest);
        Arrays.sort(teams, new AccountComparator());

        assertEquals("Number of teams", 120, teams.length);
        
        assertEquals("Expected languages in contest ", 6, contest.getLanguages().length);
        Account account = getRandomAccount(contest, Type.TEAM);
        ClientId team = account.getClientId();
        Language language = getRandomLanguage(contest);
        Problem problem = getRandomProblem(contest);
        
        assertNotNull("Language should not be null ",language);
        assertNotNull("Language name should not be null ",language.getDisplayName());
        
        assertNotNull(feeder);
        contest.setClientId(feeder.getClientId());

        RemoteRunSubmitter sub = new RemoteRunSubmitter(controller);
        
//        sub.submitRun(account.getClientId(), problemName, languageName, mainFile, auxFiles, overrideTimeMS, overrideRunId);
        
        ClientId submitter = team;
        SerializedFile mainSubmissionFile = new SerializedFile(getSamplesSourceFilename(SUMIT_SOURCE_FILENAME));
        SerializedFile[] additionalFiles = new SerializedFile[0];
        
        long overrideTimeMS = 300;
        long overrideRunId = 44;
        controller.submitRun(submitter, problem, language, mainSubmissionFile, additionalFiles, overrideTimeMS, overrideRunId);
        
        assertEquals("Expecting number of runs ", 1,         contest.getRuns().length);
        printruns(contest);
        
        String[] src_lines = Utilities.loadFile(getSamplesSourceFilename(SUMIT_SOURCE_FILENAME));
        String source = String.join("", src_lines);
        
        String base64String = Base64.getEncoder().encodeToString(source.getBytes());
        IFile mainFile = new IFileImpl("sumit.java", base64String);
                
        List<IFile> auxFiles = new ArrayList<IFile>();
        
        sub.submitRun(submitter.getName().toLowerCase(), problem.getShortName(), language.getID(), mainFile, auxFiles, overrideTimeMS, overrideRunId);
        
        assertEquals("Expecting number of runs ", 2,         contest.getRuns().length);
        
        printruns(contest);
        
    }

    /**
     * Set CCS Mode and set user to be proxy for other teams.
     * @param contest
     * @param account
     */
    private void setCCSMode(IInternalContest contest, Account account) {
        
        account.addPermission(Permission.Type.SHADOW_PROXY_TEAM);
        contest.updateAccount(account);
        
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setCcsTestMode(true);
        contest.updateContestInformation(contestInformation);
    }

    private void printruns(IInternalContest contest) {
        
        Run[] runs = contest.getRuns();
        System.out.println("");
        System.out.println("There are "+runs.length+" runs");
        for (Run run : runs) {
            System.out.println("Run = "+run.getNumber()+" is "+run);
        }
        

        
    }

  
}
