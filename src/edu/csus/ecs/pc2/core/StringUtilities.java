// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.io.Serializable;
import java.util.List;

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
     * Join a list of items
     * @param delimit
     * @param objects
     * @return
     */
    public static String join(String delimit, List<?> objects) {
     // REFACTOR for Java 1.8 replace with String.join
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < objects.size(); i++) {
            buffer.append(objects.get(i).toString());
            if (i < objects.size() - 1) {
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

    /**
     * Truncate string with ellipsis.
     * @param string
     * @param maxlen
     * @return
     */
    public static String trunc(String string, int maxlen) {

        if (string.length() <= maxlen) {
            return string;
        } else {
            return string.substring(0, maxlen - ELLIPSIS.length()) + ELLIPSIS;
        }
    }

    /**
     * Add padding on left side of string to insure minFieldLength.
     * 
     * @param padChar
     * @param minFieldLength
     * @param value
     * @return
     */
    public static String lpad(char padChar, int minFieldLength, int value) {
        String v = Integer.toString(value);
        int addchars = minFieldLength - v.length();
        if (addchars > 0) {
            v = new String(new char[addchars]).replace('\0', padChar) + v;
        }
        return v;
    }
    
    /**
     * Add padding on left side of string to insure minFieldLength.
     * 
     * @param padChar
     * @param minFieldLength
     * @param value
     * @return
     */
    public static String lpad(char padChar, int minFieldLength, String string) {
        int addchars = minFieldLength - string.length();
        if (addchars > 0) {
            string = new String(new char[addchars]).replace('\0', padChar) + string;
        }
        return string;
    }
    
    /**
     * Add padding on end of string to insure minFieldLength.
     * 
     * @param padChar
     * @param minFieldLength
     * @param value
     * @return
     */
    public static String rpad(char padChar, int minFieldLength, int value) {
        return rpad(padChar, minFieldLength, Integer.toString(value));
    }
    
    /**
     * Add padding on end of string to insure minFieldLength.
     * 
     * @param padChar
     * @param minFieldLength
     * @param value
     * @return
     */
    public static String rpad(char padChar, int minFieldLength, String string) {
        int addchars = minFieldLength - string.length();
        if (addchars > 0) {
            string = string + new String(new char[addchars]).replace('\0', padChar);
        }
        return string;
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

    
    /**
     * Returns a boolean value for the input string.
     * 
     * <li> If string is null or empty returns defaultValue.
     * <li> If string is yes or true (case insensitive) returns true
     * <li> If string is no or false (case insensitive) returns false
     * <li> If string not true or false, returns  defaultBoolean.
     * 
     * @param string a string containing a word
     * @param defaultBoolean default value if stirng is null or does no
     * @return true or false
     */
    public static boolean getBooleanValue(String string, boolean defaultBoolean) {

        boolean value = defaultBoolean;

        if (string != null && string.trim().length() > 0) {
            string = string.trim();
            if ("yes".equalsIgnoreCase(string)) {
                value = true;
            } else if ("no".equalsIgnoreCase(string)) {
                value = false;
            } else if ("true".equalsIgnoreCase(string)) {
                value = true;
            } else if ("false".equalsIgnoreCase(string)) {
                value = false;
            }
        }

        return value;
    }
    
}
