// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.util.ArrayList;

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
     * NB These should be part of the language definitions
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
                break;
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
    
    /**
     * Return best guess at source code extensions for the supplied languageid
     * TODO This should be removed when the language definition (eg. system.yaml) and/or the
     * GUI allow the specification of extensions.
     * 
     * @param languageId
     * @return String array of all extensions for this language
     */
    public static String [] getExtensionsForLanguage(String languageId, String name) {
        
        ArrayList<String> elist = new ArrayList<String>();
        String dispName;
        String rowMatch = null;
        
        languageId = languageId.toUpperCase();
        name = name.toUpperCase();
        
        for (String[] row : LANGUAGE_LIST) {
            dispName = row[1].toUpperCase();
            if(languageId.indexOf(dispName) != -1 || name.indexOf(dispName) != -1) {
                if(rowMatch == null) {
                    rowMatch = row[1];
                } else if(!rowMatch.equals(row[1])) {
                    // If we previously matched a language, and the language just changed, don't add  any more
                    // This prevents "C" extensions from being found for "C++".
                    break;
                }
                elist.add(row[0]);
            }
        }
        return(elist.toArray(new String [0]));
    }

}
