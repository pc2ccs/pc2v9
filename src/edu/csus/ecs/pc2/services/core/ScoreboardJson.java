// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.core;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.standings.ContestStandings;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilites;
import edu.csus.ecs.pc2.core.standings.json.ScoreboardJsonUtility;

/**
 * Create Scoreboard JSON
 * 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class ScoreboardJson {

    /**
     * Create JSON from a contest model.
     * 
     * @param contest
     * @return
     * @throws IllegalContestState
     * @throws JAXBException
     * @throws JsonProcessingException 
     */
    public String createJSON(IInternalContest contest) throws IllegalContestState, JAXBException, JsonProcessingException {
        return createJSON(contest, null);
    }

    /**
     * Create JSON from a contest model.
     * 
     * @param contest
     * @param log
     * @throws IllegalContestState
     * @return
     * @throws JAXBException
     * @throws JsonProcessingException 
     */
    public String createJSON(IInternalContest contest, Log log) throws IllegalContestState, JAXBException, JsonProcessingException {

        DefaultScoringAlgorithm scoringAlgorithm = new DefaultScoringAlgorithm();

        Properties properties = ScoreboardUtilites.getScoringProperties(contest);

        if (log == null) {
            log = StaticLog.getLog();
        }

        String xml = scoringAlgorithm.getStandings(contest, properties, log);
        return createJSON(xml);

    }

    /**
     * Create JSON from an XML string (Standings DSA XML)
     * 
     * @param xml
     * @return
     * @throws JAXBException
     * @throws JsonProcessingException 
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public String createJSON(String xml) throws JAXBException, JsonProcessingException {
        ContestStandings contestStandings = ScoreboardUtilites.createContestStandings(xml);
        String json = ScoreboardJsonUtility.createScoreboardJSON(contestStandings);
        return json;
    }

}
