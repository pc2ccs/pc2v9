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

        // Alas guessing 
        if ("cpp".equals(extension)) {
            extension = "C++";
        }

        if ("cc".equals(extension)) {
            extension = "C++";
        }

        if ("C".equals(extension)) {
            extension = "C++";
        }

        if ("c++".equals(extension)) {
            extension = "C++";
        }

        if ("py".equals(extension)) {
            extension = "Python";
        }

        if ("cs".equals(extension)) {
            extension = "Mono";
        }

        if ("pl".equals(extension)) {
            extension = "Perl";
        }

        extension = extension.toLowerCase();

        for (Language language : lang) {
            if (language.getDisplayName().toLowerCase().indexOf(extension) != -1) {
                return language;
            }
        }
        return null;
    }

}
