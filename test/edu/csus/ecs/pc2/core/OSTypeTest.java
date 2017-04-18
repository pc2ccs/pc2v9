package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class OSTypeTest extends AbstractTestCase{

    /**
     * Test OS Type.
     */
    public void testGetType() {
        
        OSType type = Utilities.getOSType();
        
        switch (type) {
            case UNCLASSIFIED:
                /**
                 * OS type must not be classified
                 */
                fail ("Expecting OS Type to not be "+OSType.UNCLASSIFIED);
                break;
                
            case UNDEFINED:
                /**
                 * OS type must not be undefined.
                 */
                fail ("Expecting OS Type to not be "+OSType.UNDEFINED);
                break;
                
            default:
                break;
        }
        
//        System.out.println("OS Type is: "+type);
    }

}
