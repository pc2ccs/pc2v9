// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class defines the header which appears at the top of a {@link ContestStandings} object.  
 * (A {@link ContestStandings} consists of a single {@link StandingsHeader} followed by a List of {@link TeamStanding}s.)
 * This class is used as a target during conversion (deserialization) of an XML representation of a StandingsHeader into a POJO.
 * 
 * Note that the @JsonIgnoreProperties(ignoreUnknown=true) annotation is necessary because the XML returned by the 
 * DefaultScoringAlgorithm class (which is frequently converted to a StandingsHeader object using, for example, 
 * the Jackson XMLMapper class), contains an attribute "CurrentDate" which this class doesn't define.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)  
@XmlRootElement(name = "standingsHeader")
@XmlAccessorType (XmlAccessType.FIELD)
public class StandingsHeader implements Serializable {

    private static final long serialVersionUID = -1131416863941241904L;

    //    <standingsHeader currentDate="Mon Apr 19 15:10:41 PDT 2021"
    //            generatorId="$Id$"
    //            groupCount="5"
    //            medianProblemsSolved="0"
    //            problemCount="12"
    //            problemsAttempted="12"
    //            scoreboardMessage="Live (unfrozen) scoreboard"
    //            siteCount="1"
    //            systemName="CSUS Programming Contest Control System"
    //            systemURL="http://pc2.ecs.csus.edu/"
    //            systemVersion="9.8build 20210411 build 6189~develop"
    //            title="NADC Practice 2"
    //            totalAttempts="858"
    //            totalSolved="633">

//    @XmlAnyElement
//    @XmlElement
    
    @XmlAttribute
    private String generatorId;

    @XmlAttribute
    private String groupCount;

    @XmlAttribute
    private String medianProblemsSolved;

    @XmlAttribute
    private String problemCount;

    @XmlAttribute
    private String problemsAttempted;

    @XmlAttribute
    private String scoreboardMessage;

    @XmlAttribute
    private String siteCount;

    @XmlAttribute
    private String systemName;

    @XmlAttribute
    private String systemURL;

    @XmlAttribute
    private String systemVersion;

    @XmlAttribute
    private String title;

    @XmlAttribute
    private String totalAttempts;

    @XmlAttribute
    private String totalSolved;
    
    @XmlElement(name = "groupList")
    private GroupList grouplist = null;

    @XmlElement(name = "problem")
    private List<ScoringProblem> problems = null;

    public String getGeneratorId() {
        return generatorId;
    }

    public void setGeneratorId(String generatorId) {
        this.generatorId = generatorId;
    }

    public String getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(String groupCount) {
        this.groupCount = groupCount;
    }

    public String getMedianProblemsSolved() {
        return medianProblemsSolved;
    }

    public void setMedianProblemsSolved(String medianProblemsSolved) {
        this.medianProblemsSolved = medianProblemsSolved;
    }

    public String getProblemCount() {
        return problemCount;
    }

    public void setProblemCount(String problemCount) {
        this.problemCount = problemCount;
    }

    public String getProblemsAttempted() {
        return problemsAttempted;
    }

    public void setProblemsAttempted(String problemsAttempted) {
        this.problemsAttempted = problemsAttempted;
    }

    public String getScoreboardMessage() {
        return scoreboardMessage;
    }

    public void setScoreboardMessage(String scoreboardMessage) {
        this.scoreboardMessage = scoreboardMessage;
    }

    public String getSiteCount() {
        return siteCount;
    }

    public void setSiteCount(String siteCount) {
        this.siteCount = siteCount;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemURL() {
        return systemURL;
    }

    public void setSystemURL(String systemURL) {
        this.systemURL = systemURL;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(String totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public String getTotalSolved() {
        return totalSolved;
    }

    public void setTotalSolved(String totalSolved) {
        this.totalSolved = totalSolved;
    }

    public GroupList getGrouplist() {
        return grouplist;
    }

    public void setGrouplist(GroupList grouplist) {
        this.grouplist = grouplist;
    }

    public List<ScoringProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<ScoringProblem> problems) {
        this.problems = problems;
    }

    /**
     * Returns a JSON string representation of this StandingsHeader object.
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
