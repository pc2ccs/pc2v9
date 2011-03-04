package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Utilities for XML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class XMLUtilities {

    private XMLUtilities() {
        super();
    }

    /**
     * Add a boolean value as a child element.
     * @param mementoRoot
     * @param name element name
     * @param value element value
     * @return
     */
    public static IMemento addChild(IMemento mementoRoot, String name, boolean value) {
        return addChild(mementoRoot, name, Boolean.toString(value));
    }

    /**
     * Add a long (integer) value as a child element.
     * 
     * @param mementoRoot
     * @param name element name
     * @param value element value
     * @return
     */
    public static IMemento addChild(IMemento mementoRoot, String name, long value) {
        return addChild(mementoRoot, name, Long.toString(value));
    }
    
    /**
     * Add a string value as a child element.
     * @param mementoRoot
     * @param name element name
     * @param value element value
     * @return
     */
    public static IMemento addChild(IMemento mementoRoot, String name, String value) {
        XMLMemento memento = (XMLMemento) mementoRoot.createChildNode(name, value);
        return memento;
    }

    /**
     * Format milliseconds in decimal format.
     * 
     * @param timeInMillis
     * @return 
     */
    public static String formatSeconds(long timeInMillis) {
        long seconds = timeInMillis / 1000;
        long fraction = timeInMillis % 1000;
        return seconds + "." + fraction;
    }

    /**
     * Return the current time in seconds, with millis mattisa.
     * 
     * @see #formatSeconds(long)
     * @return 
     */
    public static String getTimeStamp() {
        return formatSeconds(System.currentTimeMillis());
    }

}
