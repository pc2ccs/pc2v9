// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
 * scoreboard XML group element.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
@XmlRootElement(name = "groupList")
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupList {

    //    <groupList>
    //    <group externalId="18475" id="4" included = "1" title="ICPC North America South Division Championship"/>
    //    </groupList>

    public List<ScoringGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ScoringGroup> groups) {
        this.groups = groups;
    }

    @XmlElement(name = "group")
    @JacksonXmlProperty(localName = "group")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ScoringGroup> groups = null;
    
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
