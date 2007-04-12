package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;

/**
 * Maintains a list of {@link edu.csus.ecs.pc2.core.model.Clarification}s.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ClarificationList implements Serializable {
    public static final String SVN_ID = "$Id$";

    /**
     * 
     */
    private static final long serialVersionUID = 8506339011148953151L;

    private Hashtable<String, Clarification> clarHash = new Hashtable<String, Clarification>();

    /**
     * Directory where files are written
     */
    private String directoryName = "db";

    /**
     * Save runHash to disk for every update/add.
     */
    private boolean saveToDisk = false;

    /**
     * Next clarification Number for this site.
     */
    private int nextClarificationNumber = 1;

    public ClarificationList() {
        saveToDisk = false;
    }

    /**
     * Create list and save list to disk on update/add.
     * 
     * @param directoryName directory name for clarification file.
     */
    private ClarificationList(String directoryName) {
        this.directoryName = directoryName;
        Utilities.insureDir(directoryName);
        saveToDisk = true;
    }

    public ClarificationList(int siteNumber, boolean saveToDisk) {
        this("db." + siteNumber);
        this.saveToDisk = saveToDisk;
    }

    /**
     * Add clarification and assign number.
     * 
     * @param clarification
     */
    public Clarification add(Clarification clarification) {
        clarification.setNumber(nextClarificationNumber++);
        clarHash.put(getClarificationKey(clarification), clarification);
        if (saveToDisk) {
            writeToDisk();
        }
        return clarification;
    }

    /**
     * Get a clarification from the list.
     */
    public Clarification get(Clarification clarification) {
        return clarHash.get(getClarificationKey(clarification));
    }

    /**
     * Get a clarification from the list.
     */
    public Clarification get(ElementId elementId) {
        return clarHash.get(elementId.toString());
    }

    /**
     * Get a key for an input clarification.
     * 
     * This key is used to save a Clarification into the list.
     * 
     * @param clarification
     * @return String which uniquely identifies a clarification.
     */

    public String getClarificationKey(Clarification clarification) {
        return clarification.getElementId().toString();
    }

    /**
     * Mark clarification as deleted.
     * 
     * Does not remove from list simply marks clarification as deleted.
     * 
     * @param clarification
     * @return true if clar deleted, false if not deleted or not found.
     */
    public boolean delete(Clarification clarification) {
        Clarification fetchedClarification = get(clarification);
        if (fetchedClarification != null) {
            fetchedClarification.setDeleted(true);
            writeToDisk();
            return true;
        }
        return false;
    }

    /**
     * Remove all items from list.
     */
    public void clear() {
        clarHash = new Hashtable<String, Clarification>();
        writeToDisk();
    }

    /**
     * Add an array of clarifications.
     * 
     * @param clarList
     */
    public void add(Clarification[] clarList) {
        for (Clarification clarification : clarList) {
            add(clarification);
        }
    }

    public int size() {
        return clarHash.size();
    }

    public Clarification[] getList() {
        Clarification[] list = new Clarification[clarHash.size()];

        if (clarHash.size() == 0) {
            return list;
        }

        return clarHash.values().toArray(new Clarification[size()]);
    }

    /**
     * Update (unconditionally add) clarificaiton.
     * 
     * @param clarification
     */
    public void updateClarification(Clarification clarification) {
        clarification.getElementId().incrementVersionNumber();
        clarHash.put(getClarificationKey(clarification), clarification);
        writeToDisk();
    }

    public void updateClarification(Clarification clarification, ClarificationStates newState, ClientId sourceId) {
        Clarification fetchedClarification = get(clarification);

        if (fetchedClarification != null) {
            fetchedClarification.getElementId().incrementVersionNumber();
            fetchedClarification.setState(newState);
            writeToDisk();
        } else {
            // TODO LOG could not find clarification in list
            System.err.println("debug - could not find clarification " + clarification);

        }
    }

    public void updateClarification(Clarification clarification, ClarificationStates newState, ClientId sourceId, String answer,
            boolean sendToAll) {
        Clarification fetchedClarification = get(clarification);
        if (fetchedClarification != null) {
            fetchedClarification.getElementId().incrementVersionNumber();
            fetchedClarification.setState(newState);
            fetchedClarification.setAnswer(answer);
            fetchedClarification.setWhoJudgedItId(sourceId);
            fetchedClarification.setSendToAll(sendToAll);
            writeToDisk();
        } else {
            // TODO LOG could not find clarification in list
            System.err.println("debug - could not find clarification " + clarification);

        }
    }

    public Enumeration getClarList() {
        return clarHash.elements();
    }

    private String getFileName() {
        return getDirectoryName() + File.separator + "clarlist.dat";
    }

    /**
     * Load Clarifications from disk.
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * 
     */
    @SuppressWarnings("unchecked")
    public void loadFromDisk(int siteNumber) throws IOException, ClassNotFoundException {
        String filename = getFileName();
        if (Utilities.isFileThere(filename)) {
            clarHash = (Hashtable<String, Clarification>) Utilities.readObjectFromFile(filename);
            nextClarificationNumber = lastClarificationNumber(siteNumber) + 1;
        }
    }

    /**
     * @return Returns the directoryName.
     */
    public String getDirectoryName() {
        return directoryName;
    }

    private boolean writeToDisk() {
        if (!isSaveToDisk()) {
            return false;
        }

        try {
            return Utilities.writeObjectToFile(getFileName(), clarHash);
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Exception writing RunList to disk ");
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Loop through list and determine the last clarification number for input site.
     * 
     * @param siteNumber
     */
    private int lastClarificationNumber(int siteNumber) {
        int lastNumber = 0;

        for (Clarification clarification : getList()) {
            if (clarification.getSiteNumber() == siteNumber) {
                lastNumber = Math.max(lastNumber, clarification.getNumber());
            }
        }

        return lastNumber;
    }

    /**
     * @return Returns the saveToDisk.
     */
    public boolean isSaveToDisk() {
        return saveToDisk;
    }

    /**
     * @param saveToDisk
     *            The saveToDisk to set.
     */
    public void setSaveToDisk(boolean saveToDisk) {
        this.saveToDisk = saveToDisk;
    }
}
