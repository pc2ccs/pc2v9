package edu.csus.ecs.pc2.core;

/**
 * String utilities.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id: Utilities.java 2244 2010-10-28 03:35:33Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/Utilities.java $
public final class StringUtilities {
    
    private StringUtilities() {
        super();
    }
    
    /**
     * Compares string, handles if either string is null.
     * 
     * @param s1
     * @param s2
     * @return true if both null or equal, false otherwise
     */
    public static boolean stringSame (String s1, String s2){
        if (s1 == null && s2 == null) {
            return true;
        }
        
        if (s1 == null && s2 != null){
            return false;
        }
        
        return s1.equals(s2);
            
    }

    /**
     * Clone a string.
     * @param s
     * @return
     */
    public static String cloneString(String s) {
        if (s != null) {
            return new String(s);
        } else {
            return s;
        }
    }
}
