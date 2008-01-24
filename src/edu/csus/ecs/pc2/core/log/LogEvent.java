package edu.csus.ecs.pc2.core.log;

import java.io.Serializable;
import java.util.Date;

import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.packet.Packet;

/**
 * A single Log Event.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

public class LogEvent implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7120130020057851256L;

    /**
     * Severity of a log event.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum Severity {
        /**
         * Regular log event.
         */
        NORMAL,
        /**
         * A severe error is being logged.
         */
        SEVERE
    };

    /**
     * The severity or urgency of the event.
     */
    private Severity severity = Severity.NORMAL;

    /**
     * The message which contains details of the event
     */
    private String message;

    /**
     * A description of the type of log event.
     */
    private String eventType;

    /**
     * When event ocurred
     */
    private Date whenOcurred;

    /**
     * Create a Log Event for a packet.
     * 
     * @param packet
     * @param severity
     */
    public LogEvent(Severity severity, InternalContest contest, Packet packet) {
        this.severity = severity;
        eventType = packet.getType().toString();
        message = MessageFactory.createMessage(contest, packet);
    }

    public LogEvent(Severity severity, Packet packet) {
        this.severity = severity;
        eventType = packet.getType().toString();
        message = MessageFactory.createMessage(null, packet);
    }

    /**
     * @param severity
     * @param message
     * @param eventType
     */
    public LogEvent(Severity severity, String eventType, String message) {
        super();
        this.severity = severity;
        this.message = message;
        this.eventType = eventType;
    }

    public String toString() {
        return whenOcurred + " " + severity + " " + eventType + " " + message;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Date getWhenOcurred() {
        return whenOcurred;
    }

    public void setWhenOcurred(Date whenOcurred) {
        this.whenOcurred = whenOcurred;
    }
}
