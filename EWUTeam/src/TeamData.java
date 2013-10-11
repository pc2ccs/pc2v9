import java.util.LinkedList;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IRun;

/**
 * Team Data 
 *
 * @author EWU Senior Project Team 2013
 * @version $Id$
 */

// $HeadURL$
public class TeamData
{
    private IClient team;
    //no longer need newClar or newRun, instead pop of the queues 
    //private boolean newClar;
    //private boolean newRun;
    private LinkedList<IClarification> clars;
    private LinkedList<IRun> runs;
    
    public TeamData(IClient t)
    {
        this.team = t;
        //newClar = false;
        //newRun = false;
        clars = new LinkedList<IClarification>();
        runs = new LinkedList<IRun>();
    }
    

    public int getTeamHash()
    {
        return team.hashCode();
    }
    
    public String getTeamName()
    {
        return team.getLoginName();
    }

    
    public IClarification getNewClar()
    {
        return (IClarification)clars.pop(); //returns null if nothing in list
        //return newClar; // for old way return type would be boolean
    }
    public IRun getNewRun()
    {
        return (IRun)runs.pop(); //returns null if nothing in list
        //return newRun;// for old way return type would be boolean
    }


    public void setNewClar(IClarification c)//boolean a) old way
    {
        clars.addLast(c);
        //newClar = a;
    }

    public void setNewRun(IRun r)//boolean a) old way
    {
        runs.addLast(r);
        //newRun = a;
    }
    

    
}
