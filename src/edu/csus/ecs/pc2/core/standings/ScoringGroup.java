// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScoringGroup {

    // <group externalId="18474" id="1" included="1" title="..." teamCount="10" divisionWide="false"/>
    // Attribute id is the scoreboard group-list ordinal (dumpGroupList), not External Id.

    @XmlAttribute
    private String externalId;

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String included; //TODO could be boolean

    @XmlAttribute
    private String title;

    @XmlAttribute
    private String pc2Site;

    @XmlAttribute
    private String teamCount;

    /**
     * Whether this group is a division-wide scoreboard cohort
     * ({@code Group.isDivisionWideScoreboard()}).
     */
    @XmlAttribute
    private String divisionWide;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIncluded() {
        return included;
    }

    public void setIncluded(String included) {
        this.included = included;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getpc2Site() {
        return pc2Site;
    }

    public void setpc2Site(String id) {
        this.pc2Site = pc2Site;
    }

    public String getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(String teamCount) {
        this.teamCount = teamCount;
    }

    public String getDivisionWide() {
        return divisionWide;
    }

    public void setDivisionWide(String divisionWide) {
        this.divisionWide = divisionWide;
    }

}
