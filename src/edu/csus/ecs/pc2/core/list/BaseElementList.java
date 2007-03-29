package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IElementObject;

/**
 * Maintains a list of {@link edu.csus.ecs.pc2.core.model.IElementObject}
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public abstract class BaseElementList implements Serializable {

    // No ID string, two reasons, causes compiler warnings, but 
    // more importantly this class is never instanciated.

    private Hashtable<String, IElementObject> hash = new Hashtable<String, IElementObject>(
            200);

    /**
     * Override this object generating a unique String key
     *
     * @param elementObject
     * @return the object key
     */
    public abstract String getKey(IElementObject elementObject);

    /**
     * Add object into list.
     *
     * @param elementObject
     */
    public void add(IElementObject elementObject) {

        hash.put(getKey(elementObject), elementObject);

    }

    public void addList(Map<String, IElementObject> moreItems) {
        hash.putAll(moreItems);
    }

    /**
     * delete
     *
     * @param elementObject
     *            elementObject to delete
     */
    public void delete(IElementObject elementObject) {

        hash.remove(getKey(elementObject));

    }

    /**
     *
     * @param elementId
     *            element to be deleted.
     */
    public void delete(ElementId elementId) {

        String key = elementId.toString();
        if (hash.containsKey(key)) {
            hash.remove(key);
        }

    }

    /**
     * Get elementObject from list.
     *
     * @param id
     * @return the IElementObject
     */
    public IElementObject get(ElementId id) {

        return hash.get(id.toString());

    }

    /**
     * Get a elementObject from the list.
     */
    public IElementObject get(IElementObject elementObject) {
        return hash.get(getKey(elementObject));
    }

    /**
     * Remove all items from list.
     */
    public void clear() {

        hash = new Hashtable<String, IElementObject>(200);

    }

    /**
     * Updates existing item in list.
     *
     * If item is not in list then will add to list
     *
     * @param elementObject
     * @return if the object was updated return true, otherwise return false
     */
    public boolean update(IElementObject elementObject) {
        if (hash.containsKey(getKey(elementObject))) {
            elementObject.getElementId().incrementVersionNumber();
            hash.put(getKey(elementObject), elementObject);
            return true;
        } else {
            // TODO design better way to handle this ?
            System.err.println("BaseElementList.update - did not find "
                    + elementObject.getElementId() + " added to list");
            hash.put(getKey(elementObject), elementObject);
            return false;
        }
    }

    /**
     *
     * @return Enumeration of all elements in list.
     */
    public Enumeration elements() {

        return hash.elements();

    }

    /**
     * Number of elements in list.
     *
     * @return number of elements in list.
     */
    public int size() {

        return hash.size();

    }

    /**
     *
     * @return Collection
     */
    public Collection values() {
        return hash.values();
    }

}
