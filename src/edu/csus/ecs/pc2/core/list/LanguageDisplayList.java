package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Language;

/**
 * Language Display List.
 * 
 * Contains a list of Language, in order, to display for the users.
 * 
 * @author pc2@ecs.csus.edu
 * 
 * 
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/LanguageDisplayList.java $
public class LanguageDisplayList extends ElementDisplayList {

    /**
     * 
     */
    private static final long serialVersionUID = 7150174428160449009L;

    public static final String SVN_ID = "$Id: LanguageDisplayList.java 762 2006-11-29 09:04:16Z boudreat $";

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
