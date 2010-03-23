package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileStorage;

/**
 * JUnit for Clarification List.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClarificationListTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testClear() throws Exception {

        String dirname = "testing" + File.separator + "ClarListTest";

        new File(dirname).mkdirs();

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(2, 12, 22, 12, true);

        FileStorage storage = new FileStorage(dirname);

        ClarificationList list = new ClarificationList(storage);

        Vector<Account> accountList = contest.getAccounts(ClientType.Type.TEAM);
        Problem problem = contest.getProblems()[2];

        Account[] accounts = (Account[]) accountList.toArray(new Account[accountList.size()]);
        
        for (Account account : accounts ){
            Clarification clarification = new Clarification(account.getClientId(), problem, "Why? from "+account);
            list.addNewClarification(clarification);
        }
        
        int numClars = accounts.length;
        
        assertEquals("Number of clars ", numClars, list.getNextClarificationNumber() - 1);
        
        list.clear();
        assertEquals("Number of clars ", 1, list.getNextClarificationNumber());
        
        ClarificationList list2 = new ClarificationList(storage);
        
        assertEquals("Cleared number of clars ", 1, list2.getNextClarificationNumber());
    }
}
