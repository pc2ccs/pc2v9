package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class CommandVariableReplacerTest extends AbstractTestCase {

//    private boolean debugMode = false;

    private SampleContest sample = new SampleContest();

    private IInternalContest contest;

    private CommandVariableReplacer commandVariableReplacer = new CommandVariableReplacer();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contest = sample.createContest(1, 1, 34, 12, true);
    }

    public void testMostFields() throws Exception {
        
        String [] dataLines = {
                "{:basename}::Sumit", //
                "{:clientid}::9", //
                "{:clienttype}::TEAM", //
                "{:elapsedmins}::10", //
                "{:elapsedms}::612345", //
                "{:elapsedsecs}::612", //
                "{:filelist}::Sumit.java", //
                "{:irunid}::45", //
                "{:language}::4", //
                "{:languagename}::Perl", //
                "{:mainfile}::Sumit.java", //
                "{:options}:: -p probShort -l Perl -u 9 -m Sumit.java -i 45 -t 612345", //
                "{:problemshort}::probShort", //
                "{:runid}::45", //
                "{:teamid}::9", //
                "{:ansfile}::-", //
                "{:executetime}::{:executetime}", //
                "{:exitvalue}::{:exitvalue}", //
                "{:infile}::-", //
                "{:languageletter}::D", //
                "{:outfile}::{:outfile}", //
//                "{:pc2home}::C:\workspace.helios\pc2v9", //
                "{:problemletter}::C", //
                "{:problem}::3", //
                "{:siteid}::1", //
                "{:timelimit}::30", //
                "{:validator}::{:validator}", //
        };

        long elapsed = 612345;
        Problem problem = contest.getProblems()[2];
        Language language = contest.getLanguages()[3];
        Account account = sample.getTeamAccounts(contest)[8];
        ClientId teamId = account.getClientId();
        problem.setShortName("probShort");

        Run run = sample.createRun(contest, teamId, language, problem, 0);
        run.setNumber(45);
        run.setElapsedMS(elapsed);
        
        ExecutionData executionData = null;
        String filename = getSamplesSourceFilename("Sumit.java");
        
        RunFiles runFiles = new RunFiles(run, filename);
        ProblemDataFiles problemDataFiles = null;

//        // print names
//        for (String name : CommandVariableReplacer.VARIABLE_NAMES) {
//            String result = commandVariableReplacer.substituteAllStrings(contest, run, runFiles, name, executionData, problemDataFiles);
//            System.out.println("\""+name+"::"+result+"\", //");
//        }
        
        for (String line : dataLines) {
            String [] list = line.split("::");
            String name = list[0];
            String expectedString = list[1];
            String actual = commandVariableReplacer.substituteVariables(name, contest, run, runFiles, null, executionData, problemDataFiles);
            
            assertEquals(name+" variable ", expectedString, actual);
        }
    }

    public void testOtherFields() throws Exception {
        
        String [] dataLines = {
                "{:basename}::Sumit", //
                "{:clientid}::9", //
                "{:clienttype}::TEAM", //
                "{:elapsedmins}::10", //
                "{:elapsedms}::612345", //
                "{:elapsedsecs}::612", //
                "{:filelist}::Sumit.java", //
                "{:irunid}::45", //
                "{:language}::4", //
                "{:languagename}::Perl", //
                "{:mainfile}::Sumit.java", //
                "{:options}:: -p probShort -l Perl -u 9 -m Sumit.java -i 45 -t 612345", //
                "{:problemshort}::probShort", //
                "{:runid}::45", //
                "{:teamid}::9", //
                "{:ansfile}::-", //
                "{:executetime}::112233", //
                "{:exitvalue}::999", //
                "{:infile}::-", //
                "{:languageletter}::D", //
                "{:outfile}::{:outfile}", //
//                "{:pc2home}::C:\workspace.helios\pc2v9", //
                "{:problemletter}::C", //
                "{:problem}::3", //
                "{:siteid}::1", //
                "{:timelimit}::3412", //
                "{:validator}::{:validator}", //
        };

        long elapsed = 612345;
        Problem problem = contest.getProblems()[2];
        Language language = contest.getLanguages()[3];
        Account account = sample.getTeamAccounts(contest)[8];
        ClientId teamId = account.getClientId();
        problem.setShortName("probShort");

        Run run = sample.createRun(contest, teamId, language, problem, 0);
        run.setNumber(45);
        run.setElapsedMS(elapsed);
        
        problem.setTimeOutInSeconds(3412);
        
        ExecutionData executionData = new ExecutionData();
        executionData.setExecuteExitValue(999);
        executionData.setExecuteTimeMS(112233);
        
        String filename = getSamplesSourceFilename("Sumit.java");
        
        RunFiles runFiles = new RunFiles(run, filename);
        ProblemDataFiles problemDataFiles = null;

        for (String line : dataLines) {
            String [] list = line.split("::");
            String name = list[0];
            String expectedString = list[1];
            String actual = commandVariableReplacer.substituteVariables(name, contest, run, runFiles, null, executionData, problemDataFiles);
            
            assertEquals(name+" variable ", expectedString, actual);
        }
    }
}
