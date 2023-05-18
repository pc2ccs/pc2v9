// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
     * List of language extensions and their full or partial language display name
     */
    public static final  String[][] LANGUAGE_LIST = { //
            { "cpp", "C++" }, //
            { "cc", "C++" }, //
            { "C", "C++" }, //
            { "c++", "C++" }, //
            { "py", "Python" }, //
            { "cs", "Mono" }, //
            { "pl", "Perl" }, //

    };


    /**
     * get file extension.
     * 
     * @param filename
     * @return file extension if no period found returns null
     */
    public static String getExtension(String filename) {
        int idx = filename.indexOf(".");
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
        Language[] lang = inContest.getLanguages();

        /**
         * Partial or complete display name
         */
        String displayName = extension;

        for (String[] row : LANGUAGE_LIST) {
            String fileExtension = row[0];
            String dispName = row[1];
            
            if (fileExtension.equals(extension)) {
                displayName = dispName;
            }
        }

        displayName = displayName.toLowerCase();

        for (Language language : lang) {
            if (language.getDisplayName().toLowerCase().indexOf(displayName) != -1) {
                return language;
            }
        }
        return null;
    }

}
