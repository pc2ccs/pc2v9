package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Language;

/**
 * Maintain a list of Lanugages.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/LanguageList.java $
public class LanguageList extends ElementList {

    /**
     * 
     */
    private static final long serialVersionUID = -7236148308850761761L;

    public static final String SVN_ID = "$Id: LanguageList.java 765 2006-11-29 10:03:12Z boudreat $";

    // private LanguageComparator languageComparator = new LanguageComparator();

    /**
     * 
     * @param language
     *            {@link Language} to be added.
     */
    public void add(Language language) {
        super.add(language);

    }

    /**
     * Return list of Languages.
     * 
     * @return list of {@link Language}.
     */
    @SuppressWarnings("unchecked")
    public Language[] getList() {
        Language[] theList = new Language[size()];

        if (theList.length == 0) {
            return theList;
        }

        theList = (Language[]) values().toArray(new Language[size()]);
        return theList;
    }
}
