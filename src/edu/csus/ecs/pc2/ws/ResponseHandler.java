package edu.csus.ecs.pc2.ws;

import java.util.Map;

import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.implementation.APIPlugin;

/**
 * Takes action and returns response for HTTP request.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ResponseHandler implements APIPlugin {

    private static final String START_CLOCK_PATH = "/ccs/start_clock";

    private static final String STOP_CLOCK_PATH = "/ccs/stop_clock";

    private static final String SET_CLOCK_PATH = "/starttime";

    private static final String GET_CLOCK_START_TIME_PATH = "/ccs/contest_starttime";

    private static final String CLOCK_STARTED_PATH = "/ccs/contest_started";

    private String startTime = "undefined";

    private IContest contest;

    private ServerConnection serverConnection;

    public String getResponse(String path, Map<String, String> parameters) throws Exception {

        if (path == null) {
            return get404Response(path);
        } else if (START_CLOCK_PATH.equals(path)) {
            serverConnection.startContestClock();
            return tag("pre", "Started Contest");
        } else if (STOP_CLOCK_PATH.equals(path)) {
            serverConnection.stopContestClock();
            return  tag("pre", "Stopped/Paused Contest");
        } else if (GET_CLOCK_START_TIME_PATH.equals(path)) {
            return tag("start_time", startTime);
        } else if (SET_CLOCK_PATH.equals(path)) {
            return set_start_time(getAbsoluteParameter(parameters));
        } else if (CLOCK_STARTED_PATH.equals(path)) {
            return tag("contest_started", "" + contest.isContestClockRunning());
        } else {
            return get404Response(path);
        }
    }

    private String tag(String name, String value) {
        return "<" + name + ">" + value + "</" + name + ">";
    }

    private String set_start_time(String newTime) {
        startTime = newTime;
        return tag("start_time", startTime);
    }

    private String getAbsoluteParameter(Map<String, String> parameters) {

        String value = parameters.get("absolute");
        if (value == null) {
            value = "";
        }
        return value;
    }

    private String get404Response(String path) {
        return tag("center", "404 error - no page found \""+path+"\"");
    }


    @Override
    public String getPluginTitle() {
        return "RequestHandler";
    }

    @Override
    public void setContestAndServerConnection(ServerConnection inServerConnection, IContest inContest) {
        this.serverConnection = inServerConnection;
        this.contest = inContest;
        
    }

}
