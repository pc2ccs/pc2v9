package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Language;

/**
 * Maintain a list of {@link edu.csus.ecs.pc2.core.model.Language}s to display to users.
 * 
 * Contains a list of Language, in order, to display for the users.
 *
 * @version $Id$
 * @see edu.csus.ecs.pc2.core.list.LanguageList
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class LanguageDisplayList extends ElementDisplayList {

    /**
     * 
     */
    private static final long serialVersionUID = 7150174428160449009L;

    public static final String SVN_ID = "$Id$";

    public void addElement(Language language) {
        super.addElement(language);
    }

    public void insertElementAt(Language language, int idx) {
        super.insertElementAt(language, idx);
    }

    public void update(Language language) {
        for (int i = 0; i < size(); i++) {
            Language listLanguage = (Language) elementAt(i);

            if (listLanguage.getElementId().equals(language.getElementId())) {
                setElementAt(language, i);
            }
        }
    }

    /**
     * Get sorted list of Languages.
     * @return the array of Languages
     */
    public Language[] getList() {
        if (size() == 0) {
            return new Language[0];
        } else {
            return (Language[]) this.toArray(new Language[this.size()]);
        }
    }
}
