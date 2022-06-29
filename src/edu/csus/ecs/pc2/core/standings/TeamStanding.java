// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * This class defines an entry (one for each team) which appears in a List of TeamStandings in a {@link ContestStandings} object.  
 * (A {@link ContestStandings} consists of a single {@link StandingsHeader} followed by a List of {@link TeamStanding}s.)
 * This class is used as a target during conversion (deserialization) of an XML representation of a TeamStanding into a POJO.
 * 
 * Note that the @JsonIgnoreProperties(ignoreUnknown=true) annotation is supplied in the event the XML returned by the 
 * DefaultScoringAlgorithm class (which is frequently converted to a ContestStandings object using, for example, 
 * the Jackson XMLMapper class), contains attributes which this class doesn't define.
 *
 * @author Douglas A. Lane, John Clevenger, <pc2@ecs.csus.edu>
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@XmlRootElement(name = "teamStanding")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamStanding {

    @XmlElement(name = "problemSummaryInfo")
    @JacksonXmlProperty(localName = "problemSummaryInfo")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ProblemSummaryInfo> problemSummaryInfos = null;

    @XmlAttribute
    private String firstSolved;

    @XmlAttribute
    private String groupRank;

    @XmlAttribute
    private String index;

    @XmlAttribute
    private String lastSolved;

    @XmlAttribute
    private String points;

    @XmlAttribute
    private String problemsAttempted;

    @XmlAttribute
    private String rank;

    @XmlAttribute
    private String scoringAdjustment;

    @XmlAttribute
    private String shortSchoolName;

    @XmlAttribute
    private String solved;

    @XmlAttribute
    private String teamAlias;

    @XmlAttribute
    private String teamExternalId;

    @XmlAttribute
    private String teamGroupExternalId;

    @XmlAttribute
    private String teamGroupId;

    @XmlAttribute
    private String teamGroupName;

    @XmlAttribute
    private String teamId;

    @XmlAttribute
    private String teamKey;

    @XmlAttribute
    private String teamName;

    @XmlAttribute
    private String teamSiteId;

    @XmlAttribute
    private String totalAttempts;

//    @XmlAttribute
//    @XmlElement(name = "problemSummaryInfos")
//    @JacksonXmlProperty(localName = "problemSummaryInfos")
//    @JacksonXmlElementWrapper(useWrapping = false)
    public List<ProblemSummaryInfo> getProblemSummaryInfos() {
        return problemSummaryInfos;
    }

    public void setProblemSummaryInfos(List<ProblemSummaryInfo> problemSummaryInfos) {
        this.problemSummaryInfos = problemSummaryInfos;
    }

    public String getFirstSolved() {
        return firstSolved;
    }

    public void setFirstSolved(String firstSolved) {
        this.firstSolved = firstSolved;
    }

    public String getGroupRank() {
        return groupRank;
    }

    public void setGroupRank(String groupRank) {
        this.groupRank = groupRank;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getLastSolved() {
        return lastSolved;
    }

    public void setLastSolved(String lastSolved) {
        this.lastSolved = lastSolved;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getProblemsAttempted() {
        return problemsAttempted;
    }

    public void setProblemsAttempted(String problemsAttempted) {
        this.problemsAttempted = problemsAttempted;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getScoringAdjustment() {
        return scoringAdjustment;
    }

    public void setScoringAdjustment(String scoringAdjustment) {
        this.scoringAdjustment = scoringAdjustment;
    }

    public String getShortSchoolName() {
        return shortSchoolName;
    }

    public void setShortSchoolName(String shortSchoolName) {
        this.shortSchoolName = shortSchoolName;
    }

    public String getSolved() {
        return solved;
    }

    public void setSolved(String solved) {
        this.solved = solved;
    }

    public String getTeamAlias() {
        return teamAlias;
    }

    public void setTeamAlias(String teamAlias) {
        this.teamAlias = teamAlias;
    }

    public String getTeamExternalId() {
        return teamExternalId;
    }

    public void setTeamExternalId(String teamExternalId) {
        this.teamExternalId = teamExternalId;
    }

    public String getTeamGroupExternalId() {
        return teamGroupExternalId;
    }

    public void setTeamGroupExternalId(String teamGroupExternalId) {
        this.teamGroupExternalId = teamGroupExternalId;
    }

    public String getTeamGroupId() {
        return teamGroupId;
    }

    public void setTeamGroupId(String teamGroupId) {
        this.teamGroupId = teamGroupId;
    }

    public String getTeamGroupName() {
        return teamGroupName;
    }

    public void setTeamGroupName(String teamGroupName) {
        this.teamGroupName = teamGroupName;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamSiteId() {
        return teamSiteId;
    }

    public void setTeamSiteId(String teamSiteId) {
        this.teamSiteId = teamSiteId;
    }

    public String getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(String totalAttempts) {
        this.totalAttempts = totalAttempts;
    }
    



}
