package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Category;

/**
 * Maintain a list of {@link edu.csus.ecs.pc2.core.model.Category}.
 * 
 * @version $Id: CategoryList.java 2092 2010-06-25 17:17:56Z laned $
 * @author pc2@ecs.csus.edu
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/list/CategoryList.java $
public class CategoryList extends ElementList {

    /**
     * 
     */
    private static final long serialVersionUID = 109927474664416674L;

    // private Comparator probCompare = new CategoryComparator();

    public void add(Category category) {
        super.add(category);
    }

    /**
     * Get an array of categorys in random/hash order.
     */
    public Category[] getList() {
        Category[] theList = new Category[size()];

        if (theList.length == 0) {
            return theList;
        }

        theList = (Category[]) values().toArray(new Category[size()]);
        return theList;

    }
}
