// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SA XML scoreboard  problem element.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
@XmlRootElement(name = "scoringGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScoringProblem {

    // <problem attempts="78" bestSolutionTime="2" id="2" lastSolutionTime="83" numberSolved="53" title="Candle Box"/>

    @XmlAttribute
    private String attempts;

    @XmlAttribute
    private String bestSolutionTime;

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String lastSolutionTime;

    @XmlAttribute
    private String numberSolved;

    @XmlAttribute
    private String title;

    public String getAttempts() {
        return attempts;
    }

    public void setAttempts(String attempts) {
        this.attempts = attempts;
    }

    public String getBestSolutionTime() {
        return bestSolutionTime;
    }

    public void setBestSolutionTime(String bestSolutionTime) {
        this.bestSolutionTime = bestSolutionTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastSolutionTime() {
        return lastSolutionTime;
    }

    public void setLastSolutionTime(String lastSolutionTime) {
        this.lastSolutionTime = lastSolutionTime;
    }

    public String getNumberSolved() {
        return numberSolved;
    }

    public void setNumberSolved(String numberSolved) {
        this.numberSolved = numberSolved;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
