package edu.csus.ecs.pc2.core.model;

import java.util.Date;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Unit tests for ClarificationAnswer.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClarificationAnswerTest extends TestCase {

    private boolean debugMode = false;

    public void testOne() throws Exception {

        Vector<Account> accounts = new AccountList().generateNewAccounts(Type.JUDGE, 12, 1, PasswordType.JOE, 1, true);

        String answer = "Yes we do that";
        Account answerClient = accounts.elementAt(4);
        
        long elapsed = 6 * 60;
        ContestTime contestTime = new ContestTime(1);
        contestTime.setElapsedSecs(elapsed);

        ClarificationAnswer clarAnswer = new ClarificationAnswer(answer, answerClient.getClientId(), false, contestTime);

        assertEquals(answer, clarAnswer.getAnswer());

        Date beforeDate = new Date();
        clarAnswer.setDate(contestTime);

        assertEquals("Failed to assigning elapsed", elapsed, clarAnswer.getElapsedMS() / 1000);
        assertEquals("Failed to assigning elapsed", contestTime.getElapsedMS(), clarAnswer.getElapsedMS());
        
        assertSame("Failed assigning judge id", clarAnswer.getAnswerClient(), answerClient.getClientId());

        assertNotSame("Failed to assign date", beforeDate, clarAnswer.getDate());

        try {
            clarAnswer = new ClarificationAnswer(null, answerClient.getClientId(), false, contestTime);
            assertNotNull("Expected ClarificationAnswer to throw IllegalArgumentException for null answer parameter", clarAnswer);

        } catch (IllegalArgumentException e) {
            ok("Found IllegalArgumentException");
        } catch (Exception e) {
            assertNotNull("Expected ClarificationAnswer to throw IllegalArgumentException for null answer parameter", clarAnswer);
        }

    }

    private void ok(String comment) {
        if (debugMode) {
            System.out.println("Test passed - "+comment);
        }
    }
}
