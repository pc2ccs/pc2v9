package edu.csus.ecs.pc2.core.security;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * Tssts for permission class.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class PermissionTest extends TestCase {

    /**
     * Insures that for every Permission type there is a description.
     */
    public void testDescriptionList() {

        int invalidCount = 0;

        Type failedType = null;

        Permission permissionCl = new Permission();
        for (Type type : Type.values()) {
            String description = permissionCl.getDescription(type);
            if (description == null) {
                invalidCount++;
                failedType = type;
            } else {
                if (description.equals("todo")) {
                    invalidCount++;
                    failedType = type;
                }
            }
        }

        if (invalidCount > 0) {
            System.out.println("Invalid description for type " + failedType);
            assertFalse("There are " + invalidCount + " missing proper descriptions ", invalidCount > 0);
        }

    }

}
