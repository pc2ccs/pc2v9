package edu.csus.ecs.pc2.core.util;

import java.util.Vector;

/**
 * Tab delimited parser.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class TabSeparatedValueParser {

    private static final char TAB_CHAR = 9;

    private static final String TAB_STRING = String.valueOf(TAB_CHAR);

    /**
     * Return array of containing the Tab Separated Values from the input string.
     * 
     * @return java.lang.String[]
     * @param line
     *            java.lang.String
     * @throws Exception
     *             if there was a problem parsing the line
     */
    public static String[] parseLine(String line) throws Exception {
        String[] array = new String[1];
        array[0] = "";
        int field = 1;
        String currentField = "";
        Vector<String> v = new Vector<String>();
        int i;
        boolean inQuote = false;
        char current;
        char next;
        int length = line.length();
        int ignoredTerminatingQuote = 0;
        for (i = 0; i < line.length(); i++) {
            current = line.charAt(i);
            if (current == '"') {
                if (inQuote) {
                    if (i + 1 >= length) {
                        // terminating quote at end of line, do nothing
                        ignoredTerminatingQuote++;
                    } else {
                        next = line.charAt(i + 1);
                        if (next == TAB_CHAR) {
                            // terminating quote of field
                            inQuote = false;
                        } else {
                            if (next == '"') {
                                // store quote
                                currentField = currentField.concat("\"");
                                i++;
                            } else {
                                new Exception("found unexpected quote at position " + i + ", but next character '" + String.valueOf(next) + "'");
                            }
                        }
                    }
                } else { // not inquote
                    inQuote = true;
                }
            } else { // not a doublequote
                if (current == TAB_CHAR) {
                    if (inQuote) {
                        currentField = currentField.concat(TAB_STRING);
                    } else {
                        // store
                        v.addElement(currentField);
                        field++;
                        currentField = "";
                    }
                } else { // not a comma
                    currentField = currentField.concat(new Character(current).toString());
                }
            }
        }
        // store the last field
        v.addElement(currentField);
        array = new String[field];
        if (field != v.size()) {
            // TODO review this Exception
            new Exception("Incorrect number of fields (found " + field + ", but expected " + v.size() + ")");
        }
        Object o;
        for (i = 0; i < v.size(); i++) {
            o = v.elementAt(i);
            if (o != null) {
                array[i] = (String) o;
            } else {
                array[i] = "";
            }
        }
        return array;
    }

    /**
     * Return String of the Tab Separated Values made from the input array
     * 
     * @return java.lang.String
     * @param array
     *            java.lang.String[]
     */
    public static String toString(String[] array) {
        String s = "";
        String field;
        String newField;
        int i;
        int start;
        boolean needsQuote;
        for (i = 0; i < array.length; i++) {
            newField = "";
            start = 0;
            needsQuote = false;
            field = array[i];
            if (field.indexOf("\"") > -1) {
                needsQuote = true;
                while (field.indexOf("\"", start) > -1) {
                    newField = newField.concat(field.substring(start, field.indexOf("\"", start) + 1) + "\"");
                    start = field.indexOf("\"", start) + 1;
                }
                field = newField + field.substring(start);
            }
            if (field.indexOf(String.valueOf(TAB_STRING)) > -1 || needsQuote) {
                s = s.concat("\"" + field + "\",");
            } else {
                s = s.concat(field + TAB_STRING);
            }
        }
        s = s.substring(0, s.length() - 1); // trim trailing comma
        return s;
    }

    /**
     * 
     */
    private TabSeparatedValueParser() {
        super();
    }
}
