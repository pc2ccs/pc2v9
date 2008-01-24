package edu.csus.ecs.pc2.core.security;

import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.InternalContest;
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

    public void testTeamDefaultPermissions() {
        InternalContest model = new InternalContest();
        model.generateNewAccounts(ClientType.Type.TEAM.toString(), 1, true);
        Vector<Account> accountVect = model.getAccounts(ClientType.Type.TEAM);
        Account account = accountVect.get(0);
        assertTrue("Team is allowed DISPLAY_ON_SCOREBOARD", account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD));
    }
}
