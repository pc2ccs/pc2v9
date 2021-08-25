// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Root for the Default Scoring Algorithm XML maraling from XML into POJOs. contestStanding element.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
@XmlRootElement(name = "contestStandings")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContestStandings {

    @XmlElement(name = "standingsHeader")
    private StandingsHeader standingsHeader;

    @XmlElement(name = "teamStanding")
    private List<TeamStanding> teamStandings = null;

    public StandingsHeader getStandingsHeader() {
        return standingsHeader;
    }

    public void setStandingsHeader(StandingsHeader standingsHeader) {
        this.standingsHeader = standingsHeader;
    }

    public List<TeamStanding> getTeamStandings() {
        return teamStandings;
    }

    public void setTeamStandings(List<TeamStanding> teamStandings) {
        this.teamStandings = teamStandings;
    }
    
    /**
     * Returns a JSON string representation of this ContestStandings object.
     */
    @Override
    public String toString() {
        
        String retStr = "Undefined";
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            retStr = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // TODO pass a log into this class so we can do proper logging
            e.printStackTrace();
        }
        
        return retStr;
    }
}
