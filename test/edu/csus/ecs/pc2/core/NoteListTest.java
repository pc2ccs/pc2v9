package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.NoteMessage.Type;
import junit.framework.TestCase;

/**
 * Test NoteList.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: NoteListTest.java 211 2011-07-22 16:19:36Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/core/NoteListTest.java $
public class NoteListTest extends TestCase {

    private boolean debugMode = false;

    private void dumpNoteList(NoteList list) {
        System.out.println();
        System.out.println("Line Type        Message");
        for (NoteMessage message : list.getAll()) {
            System.out.printf("%4d %-12s", message.getLineNumber(), message.getType().toString());
            System.out.println(message.getComment());
        }

        System.out.println();
        System.out.println("### Type");
        for (NoteMessage.Type type : NoteMessage.Type.values()) {
            System.out.printf("%3d %-12s", list.getCount(type), type.toString());
            System.out.println();
        }
    }

    public void testSimple() throws Exception {

        NoteList list = new NoteList();

        list.logInfo("foo.file", 4, "Found line 4");
        list.logError("foo.file", 22, "Missing ;");
        list.logInfo("foo.file", 28, "EOF found");

        if (debugMode) {
            dumpNoteList(list);
        }
        
        assertEquals("Number of messages", 3, list.getCount());
        assertEquals("Number of messages", 1, list.getCount(Type.ERROR));
        assertEquals("Number of messages", 2, list.getCount(Type.INFORMATION));
        assertEquals("Number of messages", 0, list.getCount(Type.WARNING));

    }

}
