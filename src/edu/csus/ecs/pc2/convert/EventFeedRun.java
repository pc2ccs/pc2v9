package edu.csus.ecs.pc2.convert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Represents an Event Feed Run.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class EventFeedRun {
    public static final String ID_TAG_NAME = "id";

    public static final String JUDGED_TAG_NAME = "judged";

    public static final String LANGUAGE_TAG_NAME = "language";

    public static final String PENALTY_TAG_NAME = "penalty";

    public static final String PROBLEM_TAG_NAME = "problem";

    public static final String RESULT_TAG_NAME = "result";

    public static final String SOLVED_TAG_NAME = "solved";

    public static final String STATUS_TAG_NAME = "status";

    public static final String TEAM_TAG_NAME = "team";

    public static final String TIME_TAG_NAME = "time";

    public static final String TIMESTAMP_TAG_NAME = "timestamp";

    private String id;

    private String judged;

    private String language;

    private String penalty;

    private String problem;

    private String result;

    private String solved;

    private String status;

    private String team;

    private String time;

    private String timestamp;

    private long elapsedMS;

    public EventFeedRun(Properties properties) {

        // <run>
        // <id>1512</id>
        // <judged>True</judged>
        // <language>C++</language>
        // <penalty>True</penalty>
        // <problem>6</problem>
        // <result>TLE</result>
        // <solved>False</solved>
        // <status>done</status>
        // <team>114</team>
        // <time>17996.926534</time>
        // <timestamp>1432134006.808320</timestamp>
        // </run>

        this.id = properties.getProperty(ID_TAG_NAME);
        this.judged = properties.getProperty(JUDGED_TAG_NAME);
        this.language = properties.getProperty(LANGUAGE_TAG_NAME);
        this.penalty = properties.getProperty(PENALTY_TAG_NAME);
        this.problem = properties.getProperty(PROBLEM_TAG_NAME);
        this.result = properties.getProperty(RESULT_TAG_NAME);
        this.solved = properties.getProperty(SOLVED_TAG_NAME);
        this.status = properties.getProperty(STATUS_TAG_NAME);
        this.team = properties.getProperty(TEAM_TAG_NAME);
        this.time = properties.getProperty(TIME_TAG_NAME);
        this.elapsedMS = EventFeedUtilities.toMS(time);
        this.timestamp = properties.getProperty(TIMESTAMP_TAG_NAME);
    }

    /**
     * Creates a list of EventFeedRuns from an input event feed xml file.
     * 
     * @param filename
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    public static List<EventFeedRun> loadFile(String filename) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {

        XMLDomParse1 parser = new XMLDomParse1();

        // load into Document
        Document document = parser.create(filename);

        String path = "/contest/run/*";
        // Load <run> nodes
        NodeList nodes = parser.getNodes(document, path);

        // Separate each run into a Properties
        Properties[] runPropertyList = parser.create(nodes, ID_TAG_NAME);

        // convert list of properties into EF Run.
        List<EventFeedRun> efs = EventFeedRun.toRuns(runPropertyList, true);

        return efs;
    }

    public static List<EventFeedRun> toRuns(Properties[] runPropertyList, boolean judgedOnly) {
        List<EventFeedRun> list = new ArrayList<EventFeedRun>();
        for (Properties properties : runPropertyList) {
            EventFeedRun run = new EventFeedRun(properties);
            if (judgedOnly) {
                if ("done".equals(run.getStatus())) {
                    list.add(run);
                }
            } else {
                list.add(run);
            }
        }
        return list;
    }

    public String getResult() {
        return result;
    }

    public String getId() {
        return id;
    }

    public String getJudged() {
        return judged;
    }

    public String getLanguage() {
        return language;
    }

    public String getPenalty() {
        return penalty;
    }

    public String getProblem() {
        return problem;
    }

    public String getSolved() {
        return solved;
    }

    public String getStatus() {
        return status;
    }

    public String getTeam() {
        return team;
    }

    public String getTime() {
        return time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setElapsedMS(long elapsedMS) {
        this.elapsedMS = elapsedMS;
    }

    public long getElapsedMS() {
        return elapsedMS;
    }

}
