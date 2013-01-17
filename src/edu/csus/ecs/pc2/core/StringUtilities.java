package edu.csus.ecs.pc2.core;


/**
 * String utilities.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id: Utilities.java 2244 2010-10-28 03:35:33Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/Utilities.java $
public final class StringUtilities {
    
    /**
     * A constant containing three dots.
     */
    private static final String ELLIPSIS = "...";

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

    /**
     * Join a set of string together delimited by delimit.
     * 
     * @param delimit delimiter inserted between strings. 
     * @param strings list of names
     * @return
     */
    public static String join(String delimit, String[] strings) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < strings.length; i++) {
            buffer.append(strings[i]);
            if (i < strings.length - 1) {
                buffer.append(delimit);
            }
        }
        return buffer.toString();
    }

    /**
     * Clone array and add string as last element.
     * 
     * @param originalArray
     * @param string
     * @return
     */
    public static String [] appendString (String [] originalArray, String string){
        String[] newArray = new String[originalArray.length + 1];
        System.arraycopy(originalArray, 0, newArray, 0, originalArray.length);
        newArray[originalArray.length] = string;
        return newArray;
    }

    public static String trunc(String string, int maxlen) {

        if (string.length() <= maxlen ){
            return string;
        } else {
            return string.substring(0,maxlen-ELLIPSIS.length())+ELLIPSIS;
        }
    }

}
