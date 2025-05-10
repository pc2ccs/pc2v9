// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * This class contains support routines for the CLICS commentary endpoint.
 *
 * This class is meant to be temporary to save and read commentary entries in a flat
 * file as ndjson records.  Later, a model will be created to fully implement commentary in PC2.
 * It is being done this way intially to avoid impact to the rest of the system and to keep this
 * functionality isolated (insulated?) from the rest of the system, for now.
 * This class was modeled after the EventFeedLog.java class, since it does many of the same things.
 * The "flat" ndjson file is stored in the "logs/" folder (like the EventFeed log), and has the name:
 * logs/Commentary_2023_06_SumH.ndjson  (SumH is the contestId).  Here's an example (each is on a single line
 * but have been broken here for clarity):
 *
 * {"id":"1","time":"2025-04-29T10:27:10.837-04","contest_time":"02:00:05.170",
 *     "message":"Here is a comment about team 7","tags":["submission","accepted","submission-bronze-medal"],
 *     "source_id":"admin"}<NL>
 * {"id":"2","time":"2025-04-29T10:27:24.201-04","contest_time":"02:00:05.170",
 *     "message":"Yet another comment. Should be ID 2","tags":["submission","accepted","submission-bronze-medal"],
 *     "source_id":"admin"}<NL>
 * {"id":"3","time":"2025-04-29T10:27:27.112-04","contest_time":"02:00:05.170",
 *     "message":"Yet another comment. Should be ID 3","tags":["submission","accepted","submission-bronze-medal"],
 *     "source_id":"admin"}<NL>
 * {"id":"4","time":"2025-04-29T11:16:58.493-04","contest_time":"02:00:05.170",
 *     "message":"This comment has it all.","tags":["submission","accepted","submission-bronze-medal"],
 *     "source_id":"admin","team_ids":["10","25","3"],"problem_ids":["B-hollow-rectangles-QQZIXK"],
 *     "submission_ids":["1857","2232"]}<NL>
 *
 * @author John Buck, PC^2 Team, pc2@ecs.csus.edu
 */
public class CommentaryEntries {

    private String[] fileLines = new String[0];

    private OutputStreamWriter outStream;

    private static String logsDirectory = Log.LOG_DIRECTORY_NAME;

    private String commentaryFileName = null;

    private String filename;

    private long oldFileSize;

    /**
     * Load all commentary from the ndjson flat file
     *
     * @param contest
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public CommentaryEntries(IInternalContest contest) throws FileNotFoundException, UnsupportedEncodingException {

        filename = getCommentaryFileName(contest);
        setCommentaryFileName(filename);

        // Read any existing commentary
        readCommentary();

        // open commentary file for write/append
        outStream = new OutputStreamWriter(new FileOutputStream(filename, true), "UTF8");
    }

    private void readCommentary() {
        try {
            fileLines = Utilities.loadFile(filename);
            oldFileSize = new File(filename).length();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCommentaryFileName(IInternalContest contest) {
        return logsDirectory + File.separator + "Commentary_2023_06_" + contest.getContestIdentifier() + ".ndjson";
    }

    public String[] getCommentaryLines() {
        synchronized (filename) {
            long newFileSize = new File(filename).length();
            // only need to re-read commentary if the file size changed
            if (newFileSize != oldFileSize) {
                // this will also update oldFileSize
                readCommentary();
            }
        }
        return fileLines;
    }

    /**
     * Append new ndjson comment to commentary file.
     *
     * @param commentaryJsonString
     * @throws IOException
     */
    public void writeCommentary(String commentaryJsonString) throws IOException {
        outStream.write(commentaryJsonString);
        outStream.flush();
    }

    public static void setLogsDirectory(String logsDirectory) {
        CommentaryEntries.logsDirectory = logsDirectory;
    }

    void setCommentaryFileName(String logFileName) {
        this.commentaryFileName = logFileName;
    }

    public String getCommentaryFileName() {
        return commentaryFileName;
    }

    public void close() throws IOException {
        outStream.flush();
        outStream.close();
    }

}
