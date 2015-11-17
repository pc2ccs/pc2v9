package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * Maintains a list of {@link edu.csus.ecs.pc2.core.model.Clarification}s.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClarificationList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8506339011148953151L;

    private Hashtable<String, Clarification> clarHash = new Hashtable<String, Clarification>();

    /**
     * Save runHash to disk for every update/add.
     */
    private boolean saveToDisk = false;

    /**
     * Next clarification Number for this site.
     */
    private int nextClarificationNumber = 1;


    private IStorage storage = null;

    private LinkedList<String> backupList = new LinkedList<String>();

    public ClarificationList() {
        saveToDisk = false;
    }

    public ClarificationList(IStorage storage) {
        this.storage = storage;
        saveToDisk = true;
    }

    /**
     * Add clarification and assign number.
     * 
     * @param clarification
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public Clarification addNewClarification (Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException {
        clarification.setNumber(nextClarificationNumber++);
        add(clarification);
        return clarification;
    }

    /**
     * Add clarification to list.
     * @param clarification
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void add (Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException {
        clarHash.put(getClarificationKey(clarification), clarification);
        if (saveToDisk) {
            writeToDisk();
        }
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
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public boolean delete(Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException {
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
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void clear() throws IOException, ClassNotFoundException, FileSecurityException {
        clarHash = new Hashtable<String, Clarification>();
        nextClarificationNumber = 1;
        writeToDisk();
    }

    /**
     * Add an array of clarifications.
     * 
     * @param clarList
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void add(Clarification[] clarList) throws IOException, ClassNotFoundException, FileSecurityException {
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
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void updateClarification(Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException {
        clarification.getElementId().incrementVersionNumber();
        clarHash.put(getClarificationKey(clarification), clarification);
        writeToDisk();
    }

    public void updateClarification(Clarification clarification, ClarificationStates newState, ClientId sourceId) throws IOException, ClassNotFoundException, FileSecurityException {
        Clarification fetchedClarification = get(clarification);

        if (fetchedClarification != null) {
            fetchedClarification.getElementId().incrementVersionNumber();
            fetchedClarification.setState(newState);
            writeToDisk();
       } else {
           throw new SecurityException("Unable to find/update clarifications "+clarification);
        }
    }

    public Clarification updateClarification(Clarification clarification, ClarificationStates newState, ClarificationAnswer answer) throws IOException, ClassNotFoundException, FileSecurityException {
        Clarification fetchedClarification = get(clarification);
        if (fetchedClarification != null) {
            fetchedClarification.getElementId().incrementVersionNumber();
            fetchedClarification.setState(newState);
            if (answer != null){
                fetchedClarification.addAnswer(answer);
            }
            writeToDisk();
            return fetchedClarification;
        } else {
            throw new SecurityException("Unable to find/update clarifications "+clarification);
        }
    }

    public Enumeration<Clarification> getClarList() {
        return clarHash.elements();
    }

    private String getFileName() {
        return storage.getDirectoryName() + File.separator + "clarlist.dat";
    }
    
    public String getBackupFilename() {
        return storage.getDirectoryName() + File.separator + "clarlist" + Utilities.getDateTime() + "." + System.nanoTime() + ".dat";
    }


    /**
     * Load Clarifications from disk.
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws FileSecurityException 
     * 
     */
    @SuppressWarnings("unchecked")
    public void loadFromDisk(int siteNumber) throws IOException, ClassNotFoundException, FileSecurityException {
        String filename = getFileName();
        if (Utilities.isFileThere(filename)) {
            clarHash = (Hashtable<String, Clarification>) storage.load(filename);
            nextClarificationNumber = lastClarificationNumber(siteNumber) + 1;
        }
    }

    private synchronized boolean writeToDisk() throws IOException, ClassNotFoundException, FileSecurityException {
        if (!isSaveToDisk()) {
            return false;
        }
        
        boolean stored = storage.store(getFileName(), clarHash);
        
        String backupFilename = getBackupFilename();
        storage.store(backupFilename, clarHash);
        backupList.add(backupFilename);
        while(backupList.size() > 100) {
            String removeBackupFile = backupList.removeFirst();
            File file = new File(removeBackupFile);
            if (file.exists()) {
                file.delete();
            }
        }


        return stored;
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

    public void clone(IStorage storage2) {
        try {
            if (saveToDisk) {
                storage.store(getFileName(), clarHash);
            }
        } catch (Exception e) {
            logException("Unable to copy clarification  " + getFileName() + " to " + storage2.getDirectoryName(), e);
        }
    }

    private void logException(String string, Exception e) {
        if (StaticLog.getLog() == null) {
            StaticLog.getLog().log(Log.WARNING, string, e);
        } else {
            System.err.println(string + " " +e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public int getNextClarificationNumber() {
        return nextClarificationNumber;
    }

    /**
     * Un-checkout clarification 
     * @param clarification
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public Clarification uncheckoutClarification(Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException {
        
        Clarification fetchedClarification = get(clarification);
        if (fetchedClarification != null) {
            fetchedClarification.getElementId().incrementVersionNumber();
            fetchedClarification.setState(ClarificationStates.NEW);
            fetchedClarification.setWhoCheckedItOutId(null);
            writeToDisk();
            return fetchedClarification;
        } else {
            throw new SecurityException("Unable to find/update clarification "+clarification);
        }
        
    }

    
}
