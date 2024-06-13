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

        for(Language lang : allLangs) {
            if(lang.getExtensions().contains(extension)) {
                return(lang);
            }
        }
        return null;
    }

}
