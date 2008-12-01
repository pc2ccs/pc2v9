/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package edu.csus.ecs.pc2.core.util;

import java.util.List;

/**
 * Interface to a memento used for saving the important state of an object in a form that can be persisted in the file system.
 * <p>
 * Mementos were designed with the following requirements in mind:
 * <ol>
 * <li>Certain objects need to be saved and restored across platform sessions. </li>
 * <li>When an object is restored, an appropriate class for an object might not be available. It must be possible to skip an object in this case.</li>
 * <li>When an object is restored, the appropriate class for the object may be different from the one when the object was originally saved. If so, the new class should still be able to read the old
 * form of the data.</li>
 * </ol>
 * </p>
 * <p>
 * Mementos meet these requirements by providing support for storing a mapping of arbitrary string keys to primitive values, and by allowing mementos to have other mementos as children (arranged into
 * a tree). A robust external storage format based on XML is used.
 * </p>
 * <p>
 * The key for an attribute may be any alpha numeric value. However, the value of <code>TAG_ID</code> is reserved for internal use.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IMemento {
    /**
     * Special reserved key used to store the memento id (value <code>"org.eclipse.ui.id"</code>).
     * 
     * @see #getId
     */
    String TAG_ID = "IMemento.internal.id"; //$NON-NLS-1$

    /**
     * Creates a new child of this memento with the given type.
     * <p>
     * The <code>getChild</code> and <code>getChildren</code> methods are used to retrieve children of a given type.
     * </p>
     * 
     * @param type
     *            the type
     * @return a new child memento
     * @see #getChild
     * @see #getChildren
     */
    IMemento createChild(String type);

    /**
     * Creates a new child of this memento with the given type and id. The id is stored in the child memento (using a special reserved key, <code>TAG_ID</code>) and can be retrieved using
     * <code>getId</code>.
     * <p>
     * The <code>getChild</code> and <code>getChildren</code> methods are used to retrieve children of a given type.
     * </p>
     * 
     * @param type
     *            the type
     * @param id
     *            the child id
     * @return a new child memento with the given type and id
     * @see #getId
     */
    IMemento createChild(String type, String id);

    /**
     * Returns the first child with the given type id.
     * 
     * @param type
     *            the type id
     * @return the first child with the given type
     */
    IMemento getChild(String type);

    /**
     * Returns all children with the given type id.
     * 
     * @param type
     *            the type id
     * @return the list of children with the given type
     */
    IMemento[] getChildren(String type);

    /**
     * Returns the floating point value of the given key.
     * 
     * @param key
     *            the key
     * @return the value, or <code>null</code> if the key was not found or was found but was not a floating point number
     */
    Float getFloat(String key);

    /**
     * Returns the id for this memento.
     * 
     * @return the memento id, or <code>null</code> if none
     * @see #createChild(java.lang.String,java.lang.String)
     */
    String getId();

    /**
     * Returns the name for this memento.
     * 
     * @return the memento name, or <code>null</code> if none
     * @see #createChild(java.lang.String,java.lang.String)
     */
    String getName();

    /**
     * Returns the integer value of the given key.
     * 
     * @param key
     *            the key
     * @return the value, or <code>null</code> if the key was not found or was found but was not an integer
     */
    Integer getInteger(String key);

    /**
     * Returns the long value of the given key.
     * 
     * @param key
     *            the key
     * @return the value, or <code>null</code> if the key was not found or was found but was not an integer
     */
    Long getLong(String key);

    /**
     * Returns the string value of the given key.
     * 
     * @param key
     *            the key
     * @return the value, or <code>null</code> if the key was not found or was found but was not an integer
     */
    String getString(String key);

    /**
     * Returns the boolean value of the given key.
     * 
     * @param key
     *            the key
     * @return the value, or <code>null</code> if the key was not found or was found but was not a boolean
     */
    Boolean getBoolean(String key);

    /**
     * Return the list of names.
     * 
     * @return a possibly empty list of names
     */
    List<String> getNames();

    /**
     * Sets the value of the given key to the given floating point number.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     */
    void putFloat(String key, float value);

    /**
     * Sets the value of the given key to the given integer.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     */
    void putInteger(String key, int value);

    /**
     * Sets the value of the given key to the given long.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     */
    void putLong(String key, long value);

    /**
     * Sets the value of the given key to the given boolean value.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     */
    void putBoolean(String key, boolean value);

    /**
     * Copy the attributes and children from <code>memento</code> to the receiver.
     * 
     * @param memento
     *            the IMemento to be copied.
     */
    void putMemento(IMemento memento);

    /**
     * Sets the value of the given key to the given string.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     */
    void putString(String key, String value);
}
