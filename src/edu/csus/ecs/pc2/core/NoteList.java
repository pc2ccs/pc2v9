package edu.csus.ecs.pc2.core;

import java.util.ArrayList;
import java.util.Vector;

import edu.csus.ecs.pc2.core.NoteMessage.Type;

/**
 * A list of NoteMessages, not thread-safe.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: NoteList.java 205 2011-06-30 02:49:12Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/core/NoteList.java $
public class NoteList {

    private ArrayList<NoteMessage> notes = new ArrayList<NoteMessage>();

    public NoteList() {

    }

    public NoteList(NoteMessage[] list) {
        super();
        for (NoteMessage noteMessage : list) {
            notes.add(noteMessage);
        }
    }

    /**
     * Get a list of messages for the input type.
     * 
     * @see #getAll()
     * 
     * @param type
     * @return messages for the input type
     */
    public NoteMessage[] get(Type type) {
        Vector<NoteMessage> outVector = new Vector<NoteMessage>();

        for (int i = 0; i < notes.size(); i++) {
            NoteMessage noteMessage = notes.get(i);
            if (noteMessage.getType().equals(type)) {
                outVector.add(noteMessage);
            }
        }

        return (NoteMessage[]) outVector.toArray(new NoteMessage[outVector.size()]);
    }

    /**
     * Get all messages.
     * 
     * @return a list of all messages.
     */
    public NoteMessage[] getAll() {
        return (NoteMessage[]) notes.toArray(new NoteMessage[notes.size()]);
    }

    /**
     * Get number of messages.
     * 
     * @see #getAll()
     * @return number of messages.
     */
    public int getCount() {
        return notes.size();
    }

    /**
     * Get count of messages that match type.
     * 
     * @param type
     *            message type
     * @return number of matching messages
     */
    public int getCount(Type type) {
        if (notes.size() == 0) {
            return 0;
        }

        return get(type).length;
    }

    public void log(NoteMessage message) {
        notes.add(message);
    }

    public void log(Type type, String filename, int lineNumber, String comment) {
        NoteMessage noteMessage = new NoteMessage(type, filename, lineNumber, comment);
        notes.add(noteMessage);
    }

    public void log(Type type, String filename, int lineNumber, String comment, Exception exception) {
        NoteMessage noteMessage = new NoteMessage(type, filename, lineNumber, comment, exception);
        notes.add(noteMessage);
    }

    public void logError(String filename, int lineNumber, String comment) {
        NoteMessage noteMessage = new NoteMessage(Type.ERROR, filename, lineNumber, comment);
        notes.add(noteMessage);
    }

    public void logError(String filename, int lineNumber, String comment, Exception exception) {
        NoteMessage noteMessage = new NoteMessage(Type.ERROR, filename, lineNumber, comment, exception);
        notes.add(noteMessage);
    }

    public void logInfo(String filename, int lineNumber, String comment) {
        NoteMessage noteMessage = new NoteMessage(Type.INFORMATION, filename, lineNumber, comment);
        notes.add(noteMessage);

    }

    public void logInfo(String filename, int lineNumber, String comment, Exception exception) {
        NoteMessage noteMessage = new NoteMessage(Type.INFORMATION, filename, lineNumber, comment, exception);
        notes.add(noteMessage);
    }

    public void logWarning(String filename, int lineNumber, String comment) {
        NoteMessage noteMessage = new NoteMessage(Type.WARNING, filename, lineNumber, comment);
        notes.add(noteMessage);
    }

    public void logWarning(String filename, int lineNumber, String comment, Exception exception) {
        NoteMessage noteMessage = new NoteMessage(Type.WARNING, filename, lineNumber, comment, exception);
        notes.add(noteMessage);
    }

    /**
     * Remove all messages from list.
     */
    public void removeAll() {
        notes = new ArrayList<NoteMessage>();
    }
    
}
