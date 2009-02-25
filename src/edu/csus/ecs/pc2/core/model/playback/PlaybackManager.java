package edu.csus.ecs.pc2.core.model.playback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.playback.PlaybackEvent.Action;

/**
 * Playback manager.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PlaybackManager {

    /**
     * 
     * @param filename
     * @param internalContest
     * @return
     * @throws IOException
     */
    public PlaybackEvent[] loadPlayback(String filename, IInternalContest internalContest) throws IOException {

        Vector<PlaybackEvent> events = new Vector<PlaybackEvent>();

        if (!new File(filename).exists()) {
            throw new FileNotFoundException(filename);
        }

//        String[] lines = Utilities.loadFile(filename);

        ClientId clientId = new ClientId(1, Type.TEAM, 1);

        Language language = internalContest.getLanguages()[0];
        Problem problem = internalContest.getProblems()[0];
//        SerializedFile file = new SerializedFile(filename);
        Run run = new Run(clientId, language, problem);

        PlaybackEvent playbackEvent = new PlaybackEvent(Action.RUN_SUBMIT, clientId, run);

        events.add(playbackEvent);
        
        // int invalidLines = 0;
        //        
        // for (String s : lines){
        // try {
        // playbackEvent = createPlayBackEvent(s);
        // if (playbackEvent != null){
        // events.add(playbackEvent);
        // } else {
        // invalidLines++;
        // System.out.println("unable to parse line: "+s);
        // }
        //   
        // } catch (Exception e) {
        // invalidLines++;
        // System.out.println("Line: "+s);
        // System.out.println("    : exception = "+e.getMessage());
        // }
        // }

        return (PlaybackEvent[]) events.toArray(new PlaybackEvent[events.size()]);
    }

    protected PlaybackEvent createPlayBackEvent(String s) throws PlaybackParseException {

        String[] fields = s.split("[|]");

        if (fields.length < 3) {
            throw new PlaybackParseException("line must have 3 or more fields");
        }
/*
        // Field 0 = [submitrun|judgement##]
        // Field 1 = run number or clarification number
        // Field 2 = submitting clientid or judge clientid

        String command = fields[0].toLowerCase();

        Action action = null;

        if (command.equals("submitrun") || command.equals("submit_run")) {
            action = PlaybackEvent.Action.RUN_SUBMIT;
        } else {
            throw new PlaybackParseException("Unknown event: " + command);
        }

        int number = Integer.parseInt(fields[1]);

        if (number < 1) {
            throw new PlaybackParseException("invalid run/clar number: " + fields[1]);
        }
*/
        // for runs/clars
        // Filed 3 - problem name or ##

        // for runs
        // Field 4 - language
        // Field 5 - list of files comma delimited, main file first

        return null;
    }

}
