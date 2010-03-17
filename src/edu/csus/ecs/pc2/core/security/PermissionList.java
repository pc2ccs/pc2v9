package edu.csus.ecs.pc2.core.security;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;

/**
 * List of user permissions
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PermissionList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4160597559679295578L;
    
    private Hashtable<Permission.Type, Date> hash = new Hashtable<Permission.Type, Date>();

    /**
     * Add a permission to list of permissions.
     * @param type
     */
    public void addPermission(Permission.Type type) {
        hash.put(type, new Date());
    }
    
    /**
     * Return date when permission added.
     * @param type
     * @return Date when permission was added
     */
    public Date getWhenPermissionAdded (Permission.Type type){
        if (hash.containsKey(type)) {
            return hash.get(type);
        } else {
            return null;
        }
    }

    /**
     * remove a permission from the list.
     * @param type
     */
    public void removePermission(Permission.Type type) {

        if (hash.containsKey(type)) {
            hash.remove(type);
        }
    }
    
    /**
     * Is user allowed to do this permission?.
     * 
     * 
     * @param type
     * @return true if ok to do task, false otherwise.
     */
    public boolean isAllowed (Permission.Type type) {
        return hash.containsKey(type);
    }
    
    /**
     * Clear then copy list of permissions from input list. 
     * @param permissionList
     */
    public void clearAndLoadPermissions(PermissionList permissionList){
        hash = new Hashtable<Permission.Type, Date>();
        
        for (Permission.Type type  : Permission.Type.values()){
            if (permissionList.isAllowed(type)){
                addPermission(type);
            }
        }
    }
    
    public Permission.Type [] getList() {
        return (Permission.Type[]) hash.keySet().toArray(new Permission.Type[hash.keySet().size()]);
    }
    
    public boolean isSameAs(PermissionList permissionList) {
        if (permissionList.hash.size() != hash.size()) {
            return false;
        }
        
        for (Permission.Type type  : Permission.Type.values()){
            // compare this PermissionList with the passed in List
            // they should either both be false, or both be true
            if (isAllowed(type) != permissionList.isAllowed(type)) {
                return false;
            }
        }
        
        return true;
    }
    
}
