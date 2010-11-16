package edu.csus.ecs.pc2.core.list;

import java.util.Comparator;

import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * Sort by permission enum name (toString()).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PermissionComparator implements Comparator<Type> {

    public int compare(Type type1, Type type2) {
        
        String name1 = type1.toString();
        String name2 = type2.toString();
        
        return name1.compareTo(name2);
    }
}
