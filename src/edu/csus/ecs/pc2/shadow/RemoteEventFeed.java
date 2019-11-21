// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.URL;

public class RemoteEventFeed implements Runnable {

    public RemoteEventFeed(URL restURL, String login, String password, RunSubmitter submitter) {
        // TODO Bug 1261 code
    }

    @Override
    public void run() {
        // TODO Bug 1261 code

        /**
         * EF endpoint accept
         * 
         * Read event feed (loop) find run
         *   create pc2 Run
         *   fetch submission file
         *   call submitter
         * 
         */

    }
}
