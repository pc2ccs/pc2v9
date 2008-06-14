package edu.csus.ecs.pc2.core.model;

/**
 * Pluralize a string/word.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class Pluralize {

    protected Pluralize() {

    }

    /**
     * Make input string plural, if necessary.
     * 
     * Since there is a count, this method will determine whether the string needs to be pluralized.
     * 
     * This uses simple ending rules, like change words ending in oy, like toy to toys.
     * 
     * <pre>
     *   simplePluralize(&quot;toy&quot;, 1)  returns toy
     *   simplePluralize(&quot;toy&quot;, 2)  returns toys
     *   simplePluralize(&quot;beggar&quot;, 3)  returns beggars
     *   simplePluralize(&quot;ourself&quot;, 3)  returns ourselves
     * </pre>
     * 
     * Use {@link #pluralize(String, int)} for more complex plurals like: lives, mice, oxen.
     * 
     * @param s
     *            string to pluralize
     * @param count
     *            number of items
     * @return original string or string with plural suffix
     */
    public static String simplePluralize(String s, int count) {

        if (count == 1 || s == null || s.length() == 0) {
            return s;
        }

        char lastChar = s.charAt(s.length() - 1);

        String itemName = s.substring(0, s.length() - 1);
        String suffix = "";

        if (s.endsWith("oy")) {
            return s + "s"; // toy -> toys
        } else if (s.endsWith("self")) {
            return itemName + "ves"; // ourself -> ourselves
        } else if (lastChar == 'y') {
            suffix = "ies"; // 
        } else {
            itemName = s;
            suffix = "s";
        }

        return itemName + suffix;
    }

    /**
     * Make input string plural, if necessary.
     * 
     * If count = 1, returns the original string. <br>
     * If count != 1 will return a plural string.
     * 
     * <pre>
     *   pluralize(&quot;is&quot;, 2) returns: are
     *   pluralize(&quot;brother-in-law&quot;, 2) returns: brothers-in-law
     *   pluralize(&quot;foot&quot;, 2) returns: feet
     *   pluralize(&quot;child&quot;, 2) returns: children
     *   
     *   pluralize(&quot;reindeer&quot;, 2) returns: reindeer
     *   pluralize(&quot;swine&quot;, 2) returns: swine
     * </pre>
     * 
     * If the word is not found in the list of plural names then will use {@link #simplePluralize(String, int)} to make string plural.
     * 
     * @param s
     *            string to pluralize
     * @param count
     *            number of items
     * @return original string or string with plural suffix
     * 
     */
    public static String pluralize(String s, int count) {

        /**
         * List of special singlular and plural names
         */
        String[][] nameList = { { "is", "are" }, { "attorney-general", "attorneys-general" }, { "brother-in-law", "brothers-in-law" }, { "child", "children" }, { "father-in-law", "fathers-in law" },
                { "foot", "feet" }, { "louse", "lice" }, { "mailman", "mailmen" }, { "man", "men" }, { "mother-in-law", "mothers-in-law" }, { "mouse", "mice" }, { "ox", "oxen" },
                { "secretary-general", "secretaries general" }, { "sister-in-law", "sisters-in-law" }, { "tooth", "teeth" }, { "woman", "women" }, { "calf", "calves" }, { "elf", "elves" },
                { "half", "halves" }, { "knife", "knives" }, { "life", "lives" }, { "loaf", "loaves" }, { "shelf", "shelves" }, { "thief", "thieves" }, { "wife", "wives" }, };

        /**
         * Special words that are both singular and plural
         */
        String[] pluralWords = { "antelope", "bison", "cod", "deer", "elk", "fish", "flounder", "grouse", "herring", "moose", "quail", "reindeer", "salmon", "sheep", "shrimp", "swine", "trout", };

        if (count == 1) {
            return s;
        }
        for (int i = 0; i < nameList.length; i++) {
            if (nameList[i][0].equals(s)) {
                return nameList[i][1];
            }
        }

        for (String word : pluralWords) {
            if (word.equals(s)) {
                return s;
            }
        }

        return simplePluralize(s, 2);
    }
}
