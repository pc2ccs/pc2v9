package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.transport.ITransportReceiver;

/**
 * Represents functions provided by modules comprising the contest engine.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IController extends ITransportReceiver {
    
    void submitRun(int teamNumber, String problemName, String languageName, String filename) throws Exception;

}
