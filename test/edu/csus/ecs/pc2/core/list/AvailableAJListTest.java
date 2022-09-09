package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.AvailableAJ;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class AvailableAJListTest extends AbstractTestCase {
    
    /**
     * Test Add/remove from list.
     * @throws Exception
     */
    public void testAddRemove() throws Exception {
        
        AvailableAJList list = new AvailableAJList();
        
        ProblemList probList = new ProblemList();
        
        for (int i = 1; i < 22; i++) {
            ClientId judgeClient = new ClientId(3, Type.JUDGE, i);
            
            AvailableAJ aj = new AvailableAJ(judgeClient, probList);
            list.add(aj);
        }

        assertEquals("Expecting count in list", 21, list.size());
        
        for (int i = 1; i < 22; i++) {
            
            ClientId newjudgeClient = new ClientId(3, Type.JUDGE, i);
            AvailableAJ newAJ = new AvailableAJ(newjudgeClient, probList);
            list.remove(newAJ);
        }
        
        assertEquals("Expecting count in list", 0, list.size());
        
    }
    

}
