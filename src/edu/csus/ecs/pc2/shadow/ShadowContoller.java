// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.URL;

import edu.csus.ecs.pc2.core.IInternalController;

public class ShadowContoller {

    private IInternalController controller;

    private URL restURL;

    private String login;

    private String password;

    public ShadowContoller(IInternalController controller) {
        // TODO Bug 1261 

    }

    public ShadowContoller(IInternalController controller, URL restURL, String login, String password) {
        // TODO Bug 1261 code
        this.controller = controller;
        this.restURL = restURL;
        this.login = login;
        this.password = password;

    }

    public void start() {

        // TODO Bug 1261 construct RunSubmitter

        RunSubmitter submitter = new RunSubmitter(controller);

        // TODO Bug 1261 construct RunMonitor

        RunMonitor monitor = new RunMonitor(restURL, login, password, submitter);

        // TODO Bug 1261 get Remote Contest - ShadowData

        RestContest restContest = new RestContest(ShadowContestComparer.getRemoteContest(restURL, login, password));

        monitor.startListening();
    }

}
