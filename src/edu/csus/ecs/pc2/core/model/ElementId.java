package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Random;

/**
 * A contest-wide unique identifier/key.
 * 
 * This identifier is used as a unique key for most
 * of the data classes in PC&sup2;.
 * <P>
 * 
 * @see Contest#getRunIds()
 * @see Contest#getProblemIds()
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

    private static Random r;

    private static long counter = 0;

    static {
        // Seed random once per instance to help make it more random.

        r = new Random();
        r.setSeed(System.nanoTime());
    }

    /**
     * A generated psuedo-random large number.
     */
    private long num = 0;

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
        // TODO Auto-generated constructor stub
        this.name = name.toCharArray();
        initialize();
    }

    void initialize() {
        if (name == null) {
            name = "genericElement".toCharArray();
        }

        num = r.nextLong();

        synchronized (name) {
            savedCounter = counter++;
        }

    }

    public String toString() {
        return new String(name) + "-" + num;
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
            boolean eq = otherId.num == num
                    && savedCounter == otherId.savedCounter;
            if (eq) {
                eq = name == name;
            }

            return eq;
        } else {
            return false;
        }
    }

    /**
     * @return Returns the siteNumber.
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
