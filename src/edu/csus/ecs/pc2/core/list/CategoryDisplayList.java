package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Category;

/**
 * Maintain a list of {@link edu.csus.ecs.pc2.core.model.Category}s which are displayed to the users.
 * 
 * Contains a list of Category, in order, to display for the users.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: CategoryDisplayList.java 2094 2010-06-25 17:39:52Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/list/CategoryDisplayList.java $
public class CategoryDisplayList extends ElementDisplayList {

    /**
     * 
     */
    private static final long serialVersionUID = 6741753112664300598L;

    public void addElement(Category category) {
        super.addElement(category);
    }

    public void insertElementAt(Category category, int idx) {
        super.insertElementAt(category, idx);
    }

    public void update(Category category) {
        for (int i = 0; i < size(); i++) {
            Category listCategory = (Category) elementAt(i);

            if (listCategory.getElementId().equals(category.getElementId())) {
                setElementAt(category, i);
            }
        }
    }

    /**
     * Get a sorted list of Categories.
     * 
     * @return the array of Categories
     */
    public Category[] getList() {
        if (size() == 0) {
            return new Category[0];
        } else {
            return (Category[]) this.toArray(new Category[this.size()]);
        }
    }

}
