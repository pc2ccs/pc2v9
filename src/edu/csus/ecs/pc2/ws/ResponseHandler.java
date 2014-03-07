package edu.csus.ecs.pc2.ws;

import java.util.Map;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Takes action and returns response for HTTP request.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ResponseHandler implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6618712539578482961L;

    private static final String START_CLOCK_PATH = "/ccs/start_clock";

    private static final String STOP_CLOCK_PATH = "/ccs/stop_clock";

    private static final String SET_CLOCK_PATH = "/ccs/set_clock";

    private static final String GET_CLOCK_START_TIME_PATH = "/ccs/contest_starttime";

    private static final String CLOCK_STARTED_PATH = "/ccs/contest_started";

    private IInternalContest contest;

    private IInternalController controller;

    private String startTime;

    public String getResponse(String path, Map<String, String> parameters) {

        if (path == null) {
            return get404Response(path);
        } else if (START_CLOCK_PATH.equals(path)) {
            controller.startAllContestTimes();
            return tag("pre", "Started Contest");
        } else if (STOP_CLOCK_PATH.equals(path)) {
            controller.stopAllContestTimes();
            return  tag("pre", "Stopped/Paused Contest");
        } else if (GET_CLOCK_START_TIME_PATH.equals(path)) {
            return tag("start_time", startTime);
        } else if (SET_CLOCK_PATH.equals(path)) {
            return set_start_time(getDefaultParameter(parameters));
        } else if (CLOCK_STARTED_PATH.equals(path)) {
            return tag("contest_started", "" + contest.getContestTime().isContestRunning());
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

    private String getDefaultParameter(Map<String, String> parameters) {

        String value = parameters.get("");
        if (value == null) {
            value = "";
        }
        return value;
    }

    private String get404Response(String path) {
        return tag("center", "404 error - no page found "+path);
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
    }

    @Override
    public String getPluginTitle() {
        return "RequestHandler";
    }

}
