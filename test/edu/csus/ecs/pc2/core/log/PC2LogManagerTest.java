// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.log;

import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class PC2LogManagerTest extends AbstractTestCase {

    public void testSingleLog() throws Exception {

        IInternalContest contest = new SampleContest().createStandardContest();

        String outDir = getOutputDataDirectory(getName());
        ensureDirectory(outDir);
        //        startExplorer(outDir);

        new PC2LogManager(LogType.ONE_LOG_FOR_ALL_CLIENTS);
        PC2LogManager.setDirectoryName(outDir);

        Log singleLog = PC2LogManager.createSingleLog();

        Vector<Account> vect = contest.getAccounts(Type.TEAM);

        assertEquals("Expected number of teams", 120, vect.size());

        // All 120 teams have the same log filename
        for (int i = 0; i < vect.size(); i++) {
            Account account = vect.get(i);
            Log newLog = PC2LogManager.createLog(account.getClientId());
            assertEquals("Expecting the same log name ", singleLog, newLog);
            newLog.info("single log for " + account.getClientId());
        }
    }

    /**
     * Test for multiple logs per team.
     * @throws Exception
     */
    public void testMultiLog() throws Exception {

        String outDir = getOutputDataDirectory(getName());
        ensureDirectory(outDir);
        //        startExplorer(outDir);

        IInternalContest contest = new SampleContest().createStandardContest();

        Log singleLog = PC2LogManager.createSingleLog();

        new PC2LogManager(LogType.ONE_LOG_PER_CLIENT);
        PC2LogManager.setDirectoryName(outDir);

        Vector<Account> vect = contest.getAccounts(Type.TEAM);

        assertEquals("Expected number of teams", 120, vect.size());

        // All 120 teams have the same log filename
        for (int i = 0; i < vect.size(); i++) {
            Account account = vect.get(i);
            Log newLog = PC2LogManager.createLog(account.getClientId());
            assertTrue("Expecting different log name ", !singleLog.equals(newLog));
            newLog.info("log for " + account.getClientId());
        }
    }
}
