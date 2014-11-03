package edu.csus.ecs.pc2.core.model;

import java.util.Date;

/**
 * Get and Set Date.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IGetDate {
    
    /**
     * Get wall clock time for submission.
     * 
     * @return
     */
    public  Date getDate();
    
    /**
     * Set submission date.
     * 
     * This field does not affect {@link #getElapsedMS()} or {@link #getElapsedMins()}.
     * 
     * @param date Date, if null then sets Date long value to zero
     */
    public void setDate (Date date);

}
