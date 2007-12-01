package edu.csus.ecs.pc2.core.security;

import edu.csus.ecs.pc2.core.security.Permission.Type;
import junit.framework.TestCase;

/**
 * JUnit test for PermissionList.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PermissionListTest extends TestCase {

    public void testSameAs() {
        
        PermissionList permissionList = new PermissionList();
        
        PermissionList permissionList2 = new PermissionList();
        
        assertTrue ("Empty same as ", permissionList.isSameAs(permissionList2));
        
        permissionList.addPermission(Type.ALLOWED_TO_AUTO_JUDGE);
        assertFalse("Should not be same", permissionList.isSameAs(permissionList2));
        
        permissionList.removePermission(Type.ALLOWED_TO_AUTO_JUDGE);
        assertTrue ("Empty same as ", permissionList.isSameAs(permissionList2));
        
        // Add all 
        for (Type type : Type.values()){
            permissionList.addPermission(type);
            permissionList2.addPermission(type);
        }
        
        assertTrue ("Full same as ", permissionList.isSameAs(permissionList2));
        
        permissionList.removePermission(Type.ALLOWED_TO_AUTO_JUDGE);
        assertFalse ("Full same as ", permissionList.isSameAs(permissionList2));
        
        permissionList2.removePermission(Type.ALLOWED_TO_AUTO_JUDGE);
        assertTrue ("Full minus ALLOWED_TO_AUTO_JUDGE same as ", permissionList.isSameAs(permissionList2));

        permissionList.clearAndLoadPermissions(new PermissionList());
        permissionList2.clearAndLoadPermissions(new PermissionList());
        
        permissionList.addPermission(Type.ALLOWED_TO_RECONNECT_SERVER);
        permissionList2.addPermission(Type.ALLOWED_TO_AUTO_JUDGE);
        assertFalse ("One but different same as", permissionList.isSameAs(permissionList2));
    }
}
