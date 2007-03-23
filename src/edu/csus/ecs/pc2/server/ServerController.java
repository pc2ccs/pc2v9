package edu.csus.ecs.pc2.server;

import edu.csus.ecs.pc2.core.Controller;
import edu.csus.ecs.pc2.core.IModel;
import edu.csus.ecs.pc2.core.SubmittedRun;
import edu.csus.ecs.pc2.transport.StaticTransport;
import edu.csus.ecs.pc2.transport.TransportReceiver;

/**
 *  Represents the collection of modules comprising a contest server engine.
 * 
 * 
 * @author Douglas A. Lane
 * 
 */
public class ServerController extends Controller implements IServerController , TransportReceiver {

    private IModel model = null;

    public ServerController(IModel model) {
        super();
        this.model = model;
    }

    public void receiveSubmittedRun(SubmittedRun submittedRun) {

        try {
            System.out.println("ServerController.recieveSubmittedRun - got - " + submittedRun);

            SubmittedRun nextSubmittedRun = model.acceptRun(submittedRun);

            StaticTransport.sendToClient(nextSubmittedRun);

        } catch (Exception e) {
            // TODO: handle exception maybe someday !! :)
            e.printStackTrace();
        }

    }

    public void submitRun(int teamNumber, String problemName, String languageName, String filename) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
