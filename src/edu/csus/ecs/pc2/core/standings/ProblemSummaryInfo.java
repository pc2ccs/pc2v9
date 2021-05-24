// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * scoreboard XML group element 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
@XmlRootElement(name = "c")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProblemSummaryInfo {

    // <problemSummaryInfo attempts="0" index="12" isPending="false"
    // isSolved="false" points="0" problemId="sodaslurper--6927480297132897573"
    // solutionTime="0"/>

    @XmlAttribute
    private String attempts;

    @XmlAttribute
    private String index;

    @XmlAttribute
    private String isPending;

    @XmlAttribute
    private String isSolved;

    @XmlAttribute
    private String points;

    @XmlAttribute
    private String problemId;

    @XmlAttribute
    private String solutionTime;

    public String getAttempts() {
        return attempts;
    }

    public void setAttempts(String attempts) {
        this.attempts = attempts;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIsPending() {
        return isPending;
    }

    public void setIsPending(String isPending) {
        this.isPending = isPending;
    }

    public String getIsSolved() {
        return isSolved;
    }

    public void setIsSolved(String isSolved) {
        this.isSolved = isSolved;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    public String getSolutionTime() {
        return solutionTime;
    }

    public void setSolutionTime(String solutionTime) {
        this.solutionTime = solutionTime;
    }

}
