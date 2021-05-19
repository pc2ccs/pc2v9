// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
    //    <group externalId="18475" id="4" title="ICPC North America South Division Championship"/>
    //    </groupList>

    public List<ScoringGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ScoringGroup> groups) {
        this.groups = groups;
    }

    @XmlElement(name = "group")
    private List<ScoringGroup> groups = null;

}
