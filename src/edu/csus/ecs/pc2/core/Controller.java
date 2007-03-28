package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Model;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.SubmittedRun;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.transport.StaticTransport;

/**
 * 
 * @author pc2@ecs.csus.edu *
 */
// $HeadURL$
public class Controller implements IController {

    public static final String SVN_ID = "$Id$";

    private IModel model;

    public Controller(IModel model) {
        super();
        this.model = model;
    }

    /**
     * Recieve a run and add it to the run list.
     */
    public void receiveSubmittedRun(SubmittedRun submittedRun) {

        try {
            System.out.println("Controller.recieveSubmittedRun - got - " + submittedRun);

            SubmittedRun nextSubmittedRun = model.acceptRun(submittedRun);

            StaticTransport.sendToClient(nextSubmittedRun);

        } catch (Exception e) {
            // TODO: handle exception maybe someday !! :)
            e.printStackTrace();
        }

    }

    public void receiveNewRun(SubmittedRun submittedRun) {
        model.addRun(submittedRun);
    }

    /**
     * Submit a run to the server.
     */
    public void submitRun(int teamNumber, String problemName, String languageName, String filename) throws Exception {

        SerializedFile serializedFile = new SerializedFile(filename);

        SubmittedRun submittedRun = new SubmittedRun(teamNumber, problemName, languageName, serializedFile);

        // If we want to immediately populate the run on the GUI without
        // the run number we can invoke: model.addRun(submittedRun);

        StaticTransport.sendToServer(submittedRun);

        System.out.println("Controller.submitRun - submitted - " + submittedRun);
    }

    private static int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    public static IModel login(String id, String password) {

        // TODO Start Transport

        ClientId clientId;

        if (id.startsWith("site")) {
            clientId = new ClientId(0, Type.SERVER, 0);
        } else if (id.startsWith("team")) {
            int number = getIntegerValue(id.substring(4));
            clientId = new ClientId(0, Type.TEAM, number);
        } else {
            throw new SecurityException("No such account " + id);
        }

        Model model = new Model();
        model.setClientId(clientId);
        model.initializeWithFakeData();

        return model;
    }

}
