package edu.csus.ecs.pc2.core.list;

import java.util.Comparator;

import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * Sort by permission description. 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PermissionByDescriptionComparator implements Comparator<Type> {
    
    private Permission permission = new Permission();

    public int compare(Type type1, Type type2) {
        
        String name1 = permission.getDescription(type1);
        String name2 = permission.getDescription(type2);
        
        return name1.compareTo(name2);
    }
}
