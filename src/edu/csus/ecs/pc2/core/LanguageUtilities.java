// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;

/**
 * Utility methods for languages
 *
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class LanguageUtilities {

    /**
     * Legacy list of language extensions and their full or partial language display name
     * This should be deprecated and languages defined in system.pc2.yaml should be required to
     * include the CLICS ID (as shown above) and optionally a list of extensions supported by the
     * language.  The list below is inadequate and out of date.  For the moment, it is included for
     * backward compatibility.  JB
     */
    public static final  String[][] LANGUAGE_LIST = { //
            { "java", "Java" }, //
            { "kt", "Kotlin" }, //
            { "cpp", "C++" }, //
            { "cc", "C++" }, //
            { "C", "C++" }, //
            { "c++", "C++" }, //
            { "py", "Python" }, //
            { "cs", "Mono" }, //
            { "pl", "Perl" }, //
            // match c extension last, otherwisse would match C for C++ languages
            { "c", "C" }, //
    };

   /**
     * get file extension.
     *
     * @param filename
     * @return file extension if no period found returns null
     */
    public static String getExtension(String filename) {
        int idx = filename.lastIndexOf(".");
        if (idx != -1) {
            return filename.substring(idx + 1, filename.length());
        }
        return null;
    }

    /**
     * Match a contest Language for the input extension.
     *
     * @param myContest contest model
     * @param filename base name file or full path name.
     * @return
     */
    public static Language guessLanguage(IInternalContest myContest, String filename) {
        String extension = getExtension(filename);
        return matchFirstLanguage(myContest, extension);
    }

    /**
     * Match a contest Language for the input extension.
     *
     * @param inContest contest model
     * @param extension the extension without period, ex cpp
     * @return null if does not match any language title
     */
    public static Language matchFirstLanguage(IInternalContest inContest, String extension) {
        Language[] allLangs = inContest.getLanguages();

        // first try the new way
        for(Language lang : allLangs) {
            if(lang.getExtensions().contains(extension)) {
                return(lang);
            }
        }

        // if all else fails, try the old way.  This should be deprecated. JB
        /**
         * Partial or complete display name
         */
        String displayName = extension;

        for (String[] row : LANGUAGE_LIST) {
            String fileExtension = row[0];
            String dispName = row[1];

            if (fileExtension.equals(extension)) {
                displayName = dispName;
                break;
            }
        }

        displayName = displayName.toLowerCase();

        for (Language language : allLangs) {
            if (language.getDisplayName().toLowerCase().indexOf(displayName) != -1) {
                return language;
            }
        }
        return null;
    }

}
