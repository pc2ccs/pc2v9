package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * A contest-wide unique identifier/key.
 * <br>
 * This identifier is used as a unique key for most
 * of the data classes in PC&sup2;
 * <P>
 * Internally there are 3 components for each id: a name (String),
 * a counter (long) and random value (UUID).   While the random
 * value insures randomness, the counter (incremented with each
 * instantiation) further insures randomness.  The counter is also
 * used with {@link #equals(Object)} as the first comparison to 
 * optimize the equals condition.  This optimization was done because
 * of the huge number of equals comparisons done within the system.  
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$

public class ElementId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4019714577379393756L;

    private int siteNumber = 0;

    private int versionNumber = 1;
    
    private UUID uuid = UUID.randomUUID();

    private static long counter = 0;

    private long savedCounter = 0;

    /**
     * A name for this element.
     */
    private char[] name = null;

    /**
     * Create id with name.
     *
     * @param name
     */
    public ElementId(String name) {
        super();
        this.name = name.toCharArray();
        initialize();
    }

    void initialize() {
        if (name == null) {
            name = "genericElement".toCharArray();
        }

        synchronized (name) {
            savedCounter = counter++;
        }

    }

    public String toString() {
        return new String(name) + "-" + uuid;
    }

    /**
     * @see Object#equals(java.lang.Object).
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof ElementId) {
            ElementId otherId = (ElementId) obj;
            
            /**
             * This is optimized by comparing the savedCounter (a long)
             * first then the UUID.
             */
            boolean eq = savedCounter == otherId.savedCounter
                    && otherId.uuid.equals(uuid);
            if (eq) {
                eq = name == name;
            }

            return eq;
        } else {
            return false;
        }
    }

    /**
     * 
     * @return Returns the site number where this element was created.
     */
    public int getSiteNumber() {
        return siteNumber;
    }

    /**
     * @return Returns the versionNumber.
     */
    public int getVersionNumber() {
        return versionNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * @param siteNumber
     *            The siteNumber to set.
     */
    protected void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;
    }

    /**
     * Increments the versionNumber.
     */
    public void incrementVersionNumber() {
        versionNumber++;
    }

}
