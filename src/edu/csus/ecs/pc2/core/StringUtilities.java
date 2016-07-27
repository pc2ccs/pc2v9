package edu.csus.ecs.pc2.core;

import java.io.Serializable;

/**
 * String utilities.
 *
 * @author pc2@ecs.csus.edu
 */

public final class StringUtilities implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -4197938292232525730L;
    /**
     * A constant containing three dots.
     */
    private static final String ELLIPSIS = "...";

    private StringUtilities() {
        super();
    }
    
    /**
     * null-safe string compare.
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
     * null-safe string arrays compare.
     * 
     * @param s1[]
     * @param s2[]
     * @return true if both null or equal, false otherwise
     */
    public static boolean stringArraySame (String[] s1, String[] s2){
        if (s1 == null && s2 == null) {
            return true;
        }
        
        if (s1 == null && s2 != null){
            return false;
        }
        
        if (s1.length != s2.length) {
            return false;
        }
        for (int i = 0; i < s1.length; i++) {
            if (!stringSame(s1[i], s2[i])) {
                return false;
            }
        }
        return true;
            
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

    /**
     * Is string empty or null?.
     * 
     * @param name
     * @return true if string is null or trimmed string is length 0
     */
    public static boolean isEmpty(String name) {
        return name == null || "".equals(name.trim());
    }

    /**
     * Create new array for input strings, creates new String array elements instances.
     * 
     * Uses {@link #cloneString(String)}
     * 
     * @param strings
     * @return exact clone/copy of input
     */
    public static String[] cloneStringArray(String[] strings) {
        
        String [] names = new String[strings.length];
        int i = 0;
        for (String name : strings) {
            names[i] = cloneString(name);
            i++;
        }
        
        return names;
    }
    
}
