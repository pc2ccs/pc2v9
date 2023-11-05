// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.io.Serializable;
import java.util.ArrayList;
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

    /**
     * Return a list of integers for input string.
     *
     * Input string is a list of comma delimited numbers or ranges.
     *
     * Invalid number conditions
     * <li> range end must be greater than or equal to start number in range
     * <li> all numbers must be greater than 0
     * <li> missing start range number, ex -12
     * <li> missing end range number, ex. 22-
     * <li> missing number, ex 1,2,,5
     * <li> too many dashes in a range, ex 1-5-6-7
     *
     * @throws RuntimeException if invalid range
     * @param numberString
     * @return raw (not sorted, not unique) list of numbers
     */
    public static int[] getNumberList(String numberString) {

        // TODO REFACTOR replace ContestSnakeYAMLLoader.getNumberList with this method

        String[] list = numberString.split(",");
        List<Integer> outList = new ArrayList<Integer>();

        for (String numberItem : list) {

            String trimmed = numberItem.replaceAll(" ", ""); // remove all spaces
            trimmed = trimmed.replaceAll("^0", ""); // remove left padding zero

            if (0 == trimmed.length()) {
                throw new RuntimeException("Invalid/missing number in range, '" + trimmed + "' input: '" + numberString + "'");
            }

            if (trimmed.indexOf('-') > -1) {
                if (trimmed.startsWith("-")) {
                    throw new RuntimeException("Invalid range, missing range start number '" + trimmed + "' input: '" + numberString + "'");
                }
                if (trimmed.endsWith("-")) {
                    throw new RuntimeException("Invalid range, missing range end number '" + trimmed + "' input: '" + numberString + "'");
                }

                String[] ranges = trimmed.split("-");
                if (ranges.length != 2) {
                    throw new RuntimeException("Invalid range format (too many dashes?) '" + trimmed + "' input: '" + numberString + "'");
                }

                int startRange = getIntegerValue(ranges[0], 0);
                int endRange = getIntegerValue(ranges[1], 0);

                if (endRange < startRange) {
                    throw new RuntimeException("Invalid range range end number must be >= start range'" + trimmed + "' input: '" + numberString + "'");
                }

                for (int i = startRange; i <= endRange; i++) {
                    outList.add(i);
                }

            } else {
                outList.add(getIntegerValue(trimmed, 0));
            }
        }
        int [] outArray = outList.stream() //
                .mapToInt(Integer::intValue) //
                .toArray();
        return outArray;
    }

    public static int getIntegerValue(String string, int defaultNumber) {
        // TODO REFACTOR replace ContestSnakeYAMLLoader.getIntegerValue with this method

        int number = defaultNumber;

        if (string != null && string.length() > 0) {
            number = Integer.parseInt(string.trim());
        }

        return number;
    }

    /**
     *  get team number from login string.
     *
     * @param user team login in form team#, team102
     * @return null if no team number found or string after team is not a number.
     */
    public static Integer getTeamNumber(String userLogin) {
        int idx = userLogin.lastIndexOf("team");
        if (idx != -1) {
            String numstring = userLogin.substring(idx + 4);
            try {
                int num = Integer.parseInt(numstring);
                return num;
            } catch (Exception e) {
                // ignore Num parse error, will return null
            }
        }
        return null;
    }

    /**
     * Removes the last character from the given String and returns the resulting String.
     * If the given String is null or empty, returns null.
     *
     * @param s the String whose last char is to be removed.
     * @return a String identical to the input String except with the last character removed, or null.
     */
    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0) ? null : (s.substring(0, s.length()-1));
    }

    public static String removeUpTo(String source, String stringToRemove) {

        int index = source.indexOf(stringToRemove);

        if (index > 0) {
            String shorterString = source.substring(index);
            return shorterString;
        } else {
            return source;
        }
    }

    /**
     * null safe compare to.  Avoids NPEs.
     *
     * @see {@link String#compareTo(String)}
     * @param s1
     * @param s2
     * @return 0 if both strings null or strings equal.
     */
    public static int nullSafeCompareTo(String s1, String s2) {

        if (s1 == null && s2 == null) {
            return 0;
        }

        if (s1 == null && s2 != null) {
            return -1;
        }
        if (s1 != null && s2 == null) {
            return 1;
        }
        return s1.compareTo(s2);

    }

    /**
     * null safe natural compare to.  Avoids NPEs.
     *
     * @param s1
     * @param s2
     * @param ignoreCase to do a case insensitive compare
     * @return 0 if both strings null or strings equal.
     */
    public static int nullSafeNaturalCompareTo(String s1, String s2, boolean ignoreCase) {

        if (s1 == null && s2 == null) {
            return 0;
        }

        if (s1 == null && s2 != null) {
            return -1;
        }
        if (s1 != null && s2 == null) {
            return 1;
        }
        return naturalCompare(s1, s2, ignoreCase);
    }

    /**
     * Compare strings as a human might.  a.1 comes before a.10, b[3] comes before b[10], etc.
     *
     * @see {@link https://stackoverflow.com/questions/1262239/natural-sort-order-string-comparison-in-java-is-one-built-in}
     * @param a
     * @param b
     * @param ignoreCase
     * @return -1, 0, 1 depending on which string is bigger.
     */
    public static int naturalCompare(String a, String b, boolean ignoreCase) {
        if (ignoreCase) {
            a = a.toLowerCase();
            b = b.toLowerCase();
        }
        int aLength = a.length();
        int bLength = b.length();
        int minSize = Math.min(aLength, bLength);
        char aChar, bChar;
        boolean aNumber, bNumber;
        boolean asNumeric = false;
        int lastNumericCompare = 0;
        for (int i = 0; i < minSize; i++) {
            aChar = a.charAt(i);
            bChar = b.charAt(i);
            aNumber = aChar >= '0' && aChar <= '9';
            bNumber = bChar >= '0' && bChar <= '9';
            if (asNumeric)
                if (aNumber && bNumber) {
                    if (lastNumericCompare == 0)
                        lastNumericCompare = aChar - bChar;
                } else if (aNumber)
                    return 1;
                else if (bNumber)
                    return -1;
                else if (lastNumericCompare == 0) {
                    if (aChar != bChar)
                        return aChar - bChar;
                    asNumeric = false;
                } else
                    return lastNumericCompare;
            else if (aNumber && bNumber) {
                asNumeric = true;
                if (lastNumericCompare == 0)
                    lastNumericCompare = aChar - bChar;
            } else if (aChar != bChar)
                return aChar - bChar;
        }
        if (asNumeric)
            if (aLength > bLength && a.charAt(bLength) >= '0' && a.charAt(bLength) <= '9') // as number
                return 1;  // a has bigger size, thus b is smaller
            else if (bLength > aLength && b.charAt(aLength) >= '0' && b.charAt(aLength) <= '9') // as number
                return -1;  // b has bigger size, thus a is smaller
            else if (lastNumericCompare == 0)
              return aLength - bLength;
            else
                return lastNumericCompare;
        else
            return aLength - bLength;
    }

    /**
     * Remove all occurrences of a char in a string
     *
     *  @param s the string to operate on
     *  @param c all occurrences of this character will be removed from
     *  @returns s without any character c's
     */
    public static String removeAllOccurrences(String s, char c) {
        StringBuilder sWork = new StringBuilder(s);
        int i, nLen = sWork.length();

        for(i = nLen-1; i >= 0; i--) {
            if(sWork.charAt(i) == c) {
                sWork.deleteCharAt(i);
            }
        }
        return(sWork.toString());
    }
}
