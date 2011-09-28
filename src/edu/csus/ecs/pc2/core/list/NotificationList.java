package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Notification;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * Maintains a list of {@link Notification}s.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ClarificationList.java 2062 2010-03-23 04:09:10Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/list/ClarificationList.java $
public class NotificationList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5437388419721390123L;

    private Hashtable<String, Notification> notifHash = new Hashtable<String, Notification>();

    /**
     * Save runHash to disk for every update/add.
     */
    private boolean saveToDisk = false;

    /**
     * Next notification Number for this site.
     */
    private int nextNotificationNumber = 1;

    private IStorage storage = null;

    public NotificationList() {
        saveToDisk = false;
    }

    public NotificationList(IStorage storage) {
        this.storage = storage;
        saveToDisk = true;
    }

    /**
     * Add notification and assign number.
     * 
     * @param notification
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Notification addNewNotification(Notification notification) throws IOException, ClassNotFoundException, FileSecurityException {
        notification.setNumber(nextNotificationNumber++);
        add(notification);
        return notification;
    }

    /**
     * Add notification to list.
     * 
     * @param notification
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void add(Notification notification) throws IOException, ClassNotFoundException, FileSecurityException {
        notifHash.put(getNotificationKey(notification), notification);
        if (saveToDisk) {
            writeToDisk();
        }
    }

    /**
     * Get a notification from the list.
     */
    public Notification get(Notification notification) {
        return notifHash.get(getNotificationKey(notification));
    }

    /**
     * Get a notification from the list.
     */
    public Notification get(ElementId elementId) {
        return notifHash.get(elementId.toString());
    }

    /**
     * Get a key for an input notification.
     * 
     * This key is used to save a Notification into the list.
     * 
     * @param notification
     * @return String which uniquely identifies a notification.
     */

    public String getNotificationKey(Notification notification) {
        return notification.getElementId().toString();
    }

    /**
     * Mark notification as deleted.
     * 
     * Does not remove from list simply marks notification as deleted.
     * 
     * @param notification
     * @return true if notif deleted, false if not deleted or not found.
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public boolean delete(Notification notification) throws IOException, ClassNotFoundException, FileSecurityException {
        Notification fetchedNotification = get(notification);
        if (fetchedNotification != null) {
            fetchedNotification.setDeleted(true);
            writeToDisk();
            return true;
        }
        return false;
    }

    /**
     * Remove all items from list.
     * 
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void clear() throws IOException, ClassNotFoundException, FileSecurityException {
        notifHash = new Hashtable<String, Notification>();
        nextNotificationNumber = 1;
        writeToDisk();
    }

    /**
     * Add an array of notifications.
     * 
     * @param clarList
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void add(Notification[] clarList) throws IOException, ClassNotFoundException, FileSecurityException {
        for (Notification notification : clarList) {
            add(notification);
        }
    }

    public int size() {
        return notifHash.size();
    }

    public Notification[] getList() {
        Notification[] list = new Notification[notifHash.size()];

        if (notifHash.size() == 0) {
            return list;
        }

        return notifHash.values().toArray(new Notification[size()]);
    }

    public Notification updateNotification(Notification notification) throws IOException, ClassNotFoundException, FileSecurityException {
        Notification fetchedNotification = get(notification);
        if (fetchedNotification != null) {
            fetchedNotification.getElementId().incrementVersionNumber();
            writeToDisk();
            return fetchedNotification;
        } else {
            throw new SecurityException("Unable to find/update notifications " + notification);
        }
    }

    public Enumeration<Notification> getClarList() {
        return notifHash.elements();
    }

    private String getFileName() {
        return storage.getDirectoryName() + File.separator + "notifs.dat";
    }

    /**
     * Load Notifications from disk.
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
            notifHash = (Hashtable<String, Notification>) storage.load(filename);
            nextNotificationNumber = lastNotificationNumber(siteNumber) + 1;
        }
    }

    private boolean writeToDisk() throws IOException, ClassNotFoundException, FileSecurityException {
        if (!isSaveToDisk()) {
            return false;
        }

        return storage.store(getFileName(), notifHash);
    }

    /**
     * Loop through list and determine the last notification number for input site.
     * 
     * @param siteNumber
     */
    private int lastNotificationNumber(int siteNumber) {
        int lastNumber = 0;

        for (Notification notification : getList()) {
            if (notification.getSiteNumber() == siteNumber) {
                lastNumber = Math.max(lastNumber, notification.getNumber());
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
                storage.store(getFileName(), notifHash);
            }
        } catch (Exception e) {
            logException("Unable to copy notification  " + getFileName() + " to " + storage2.getDirectoryName(), e);
        }
    }

    private void logException(String string, Exception e) {
        if (StaticLog.getLog() == null) {
            StaticLog.getLog().log(Log.WARNING, string, e);
        } else {
            System.err.println(string + " " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public int getNextNotificationNumber() {
        return nextNotificationNumber;
    }

    /**
     * Get notification for submitter and problem, if exists.
     * 
     * @param submitter
     * @param problemId
     * @return if no notification then
     */
    public Notification get(ClientId submitter, ElementId problemId) {

        for (Notification notification : getList()) {
            if (notification.getSubmitter().getClientNumber() == submitter.getClientNumber()) {
                if (problemId.equals(notification.getProblemId())) {
                    if (notification.getSubmitter().equals(submitter)) {
                        return notification;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get all notifications for submitter
     * 
     * @param submitter
     * @return empty array or list of notificatiion.
     */
    public Notification[] getList(ClientId submitter) {

        Vector<Notification> list = new Vector<Notification>();
        for (Notification notification : getList()) {
            if (notification.getSubmitter().equals(submitter)) {
                list.addElement(notification);
            }
        }
        return (Notification[]) list.toArray(new Notification[list.size()]);

    }

}
