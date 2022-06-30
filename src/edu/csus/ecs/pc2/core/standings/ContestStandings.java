// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * This class encapsulates a collection of data representing the current contest standings.  
 * A ContestStandings consists of a single {@link StandingsHeader} along with a List of {@link TeamStanding}s.)
 * This class is used as a target during conversion (deserialization) of an XML representation of contest standings into a POJO.
 * Such deserialization is commonly applied to the XML contest standing string produced by {@link DefaultScoringAlgorithm#getStandings()}.
 * 
 * @author Douglas A. Lane, John Clevenger <pc2@ecs.csus.edu>
 *
 */
@XmlRootElement(name = "contestStandings")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContestStandings {

    @XmlElement(name = "standingsHeader")
    private StandingsHeader standingsHeader;

    @XmlElement(name = "teamStanding")
    @JacksonXmlProperty(localName = "teamStanding")
    @JacksonXmlElementWrapper(useWrapping = false)
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
