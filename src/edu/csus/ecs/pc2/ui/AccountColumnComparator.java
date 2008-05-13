/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import java.io.Serializable;

import com.ibm.webrunner.j2mclb.util.Comparator;

import edu.csus.ecs.pc2.core.list.AccountNameComparator;

/**
 * This is a translator between java.util.Comparator and com.ibm.webrunner.j2mclb.util.Comparator
 * that uses the guts from AccountNameCompator
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 *
 */
public class AccountColumnComparator extends Object implements Comparator, Serializable {

    private AccountNameComparator realComparator = new AccountNameComparator();
    
    /**
     * 
     */
    private static final long serialVersionUID = 2475774815053519682L;

    /* (non-Javadoc)
     * @see com.ibm.webrunner.j2mclb.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object arg0, Object arg1) {
        return realComparator.compare(arg0.toString(), arg1.toString());
    }
}
