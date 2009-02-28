package edu.csus.ecs.pc2.core.model.playback;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.playback.PlaybackEvent.Action;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;

/**
 * Playback manager.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PlaybackManager {

    public static final String ACTION_KEY = "action";

    public static final String ID_KEY = "id";

    public static final String SITE_KEY = "site";

    public static final String PROBLEM_KEY = "problem";

    public static final String LANGUAGE_KEY = "language";

    public static final String MAINFILE_KEY = "mainfile";

    public static final String SUBMIT_CLIENT_KEY = "submitclient";

    public static final String ELAPSED_KEY = "elapsed";

    private static final String DELIMITER = "";

    private int sequenceNumber = 1;

    /**
     * 
     * @param filename
     * @param internalContest
     * @return
     * @throws Exception 
     */
    public PlaybackEvent[] loadPlayback(String filename, IInternalContest contest) throws Exception {

        Vector<PlaybackEvent> events = new Vector<PlaybackEvent>();

        if (!new File(filename).exists()) {
            throw new FileNotFoundException(filename);
        }

//        for (int i = 0; i < 13; i++) {
//
//            ClientId clientId = new ClientId(1, Type.TEAM, i + 1);
//
//            Language language = internalContest.getLanguages()[0];
//            Problem problem = internalContest.getProblems()[0];
//            // SerializedFile file = new SerializedFile(filename);
//            Run run = new Run(clientId, language, problem);
//            run.setElapsedMins(i + 45);
//
//            PlaybackEvent playbackEvent = new PlaybackEvent(Action.RUN_SUBMIT, clientId, run);
//
//            events.add(playbackEvent);
//        }

         String[] lines = Utilities.loadFile(filename);
         
         String sourceDirectory = Utilities.dirname(filename);

        int invalidLines = 0;
        int lineNumber = 0;
        Exception savedException = null;
        
        for (String s : lines) {
            try {
                lineNumber ++;
                
                if (s.trim().length() == 0){
                    continue;
                }
                
                if (s.trim().startsWith("#")){
                    continue;
                }
                
                PlaybackEvent playbackEvent = createPlayBackEvent(lineNumber, contest, s, "[|]", sourceDirectory);
                if (playbackEvent != null) {
                    events.add(playbackEvent);
                } else {
                    invalidLines++;
                    System.out.println("Line "+lineNumber+": unable to parse line: " + s);
                }

            } catch (Exception e) {
                invalidLines++;
                if (invalidLines == 1){
                    savedException = e;
                }
                System.out.println("Line "+lineNumber+" : " + s);
                System.out.println("Line "+lineNumber+" : Exception = " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        if (savedException != null){
            System.out.println("Errors on "+invalidLines+" lines, loading "+filename);
            throw savedException;
        }
        
        return (PlaybackEvent[]) events.toArray(new PlaybackEvent[events.size()]);
    }

    /**
     * Return int for input string
     * 
     * @param s
     * @return zero if error, otherwise returns value.
     */
    private static int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Create playback event from input string.
     * 
     * The string is delimited by |
     * 
     * <pre>
     * action=submitrun|judgement##
     * site=##
     * submitClient=team##|judge##|admin##
     * id=##
     * problem=&lt;name&gt;|#
     * language=&lt;name&gt;
     * [elapsed=##]
     * mainfile=&lt;filename&gt;
     * auxfiles=&lt;filename1&gt;[,&lt;filename2&gt;
     * eventClient=judge#|admin#
     * </pre>
     * @param lineNumber 
     * 
     * @param contest
     * @param sourceDir
     * @param s
     * @param delimit
     * @return
     * @throws PlaybackParseException
     */
    protected PlaybackEvent createPlayBackEvent(int lineNumber, IInternalContest contest, String s, String delimit, String sourceDir) throws PlaybackParseException {

        String[] fields = s.split(delimit);

        if (fields.length < 3) {
            throw new PlaybackParseException("line must have 3 or more fields");
        }

        PlaybackEvent playbackEvent = null;

        Properties properties = mapFieldsNameValuePairs(fields);

        String command = getAndCheckValue(properties, ACTION_KEY, "action name/value", lineNumber);

        Action action = Action.UNDEFINED;
        
        if (command.equalsIgnoreCase(Action.RUN_SUBMIT.toString())){
            action = PlaybackEvent.Action.RUN_SUBMIT;
            String problemName = getAndCheckValue(properties, PROBLEM_KEY, "Problem name", lineNumber);
            String languageName = getAndCheckValue(properties, LANGUAGE_KEY, "Language name", lineNumber);
            String mainfileName = getAndCheckValue(properties, MAINFILE_KEY, "Main filename", lineNumber);
            String siteId = getAndCheckValue(properties, SITE_KEY, "Site number", lineNumber);
            String submitClientName = getAndCheckValue(properties, SUBMIT_CLIENT_KEY, "Client id", lineNumber);
            
            String elapsedTimeStr = getAndCheckValue(properties, ELAPSED_KEY, "Elapsed time", false, lineNumber);

            Language language = findLanguage(contest, languageName);
            Problem problem = findProblem(contest, problemName);
            ClientId clientId = findClient(contest, siteId, submitClientName);

            Run run = new Run(clientId, language, problem);

            SerializedFile[] files = new SerializedFile[1];
            try {
                SerializedFile file = new SerializedFile(sourceDir + File.separator + mainfileName);

                if (file == null || file.getBuffer() == null) {
                    throw new PlaybackParseException(lineNumber, "Could not read/find " + mainfileName);
                }
                if (file.getBuffer().length == 0) {
                    throw new PlaybackParseException(lineNumber, "No bytes for file " + mainfileName);
                }
                files[0] = file;

            } catch (Exception e) {
                e.printStackTrace();
                throw new PlaybackParseException(e);
            }
            
            String idStr = getAndCheckValue(properties, ID_KEY, "run/clar number", lineNumber);
            int number = Integer.parseInt(idStr);

            if (number < 1) {
                throw new PlaybackParseException(lineNumber, "invalid run/clar number: " + idStr);
            }

            run.setElapsedMins(getIntegerValue(elapsedTimeStr));
            run.setNumber(number);
            
            playbackEvent= new PlaybackEvent(action, clientId, run);
            playbackEvent.setClientId(clientId);
            playbackEvent.setFiles(files);

            // TODO aux files, someday, maybe
        } else {
            throw new PlaybackParseException(lineNumber, "Unknown event: " + command);
        }

        return playbackEvent;
    }


    private ClientId findClient(IInternalContest contest, String siteId, String loginName) throws PlaybackParseException {

        int number;

        int siteNumber = getIntegerValue(siteId);

        if (loginName.startsWith("team") && loginName.length() > 4) {
            number = getIntegerValue(loginName.substring(4));
            return new ClientId(siteNumber, Type.TEAM, number);
        } else if (loginName.startsWith("t") && loginName.length() > 1) {
            number = getIntegerValue(loginName.substring(1));
            return new ClientId(siteNumber, Type.TEAM, number);
        } else if (loginName.startsWith("judge") && loginName.length() > 5) {
            number = getIntegerValue(loginName.substring(5));
            return new ClientId(siteNumber, Type.JUDGE, number);
        }

        throw new PlaybackParseException("Could not find/match client: " + loginName);
    }

    private Problem findProblem(IInternalContest contest, String problemName) throws PlaybackParseException {

        for (Problem problem : contest.getProblems()) {
            if (problem.getDisplayName().trim().equalsIgnoreCase(problemName.trim())) {
                return problem;
            }
        }

        throw new PlaybackParseException("Could not find/match problem: " + problemName);
    }

    private Language findLanguage(IInternalContest contest, String languageName) throws PlaybackParseException {

        for (Language language : contest.getLanguages()) {
            if (language.getDisplayName().trim().equalsIgnoreCase(languageName.trim())) {
                return language;
            }
        }

        throw new PlaybackParseException("Could not find/match language: " + languageName);
    }

    private String getAndCheckValue(Properties properties, String key, String message, int lineNumber) throws PlaybackParseException {
        return getAndCheckValue(properties, key, message, true, lineNumber);
    }

    /**
     * 
     * @param properties
     * @param key
     * @param string
     * @param message
     * @param requiredOption true means throw exception if value is missing
     * @return
     * @throws PlaybackParseException 
     */
    private String getAndCheckValue(Properties properties, String key, String message, boolean requiredOption, int lineNumber) throws PlaybackParseException {
        String value = properties.getProperty(key);
        if (value == null && requiredOption) {
            throw new PlaybackParseException(lineNumber, message + " value missing (key = " + key + ")");
        }
        return value;
    }

    private Properties mapFieldsNameValuePairs(String[] fields) throws PlaybackParseException {

        Properties properties = new Properties();

        int index;
        
        int fieldNumber = 0;

        for (String field : fields) {
            
            fieldNumber ++;
            
            if (field.trim().length() == 0){
                continue;
            }

            index = field.indexOf("=");
            if (index == -1) {
                throw new PlaybackParseException("Missing = in name/value pair, field "+fieldNumber+ ": " + field);
            } else {
                String key = field.substring(0, index).trim().toLowerCase();
                String value = field.substring(index + 1);
                properties.put(key, value);
            }
        }

        return properties;
    }

    public void sendToJudgesAndOthers(IInternalController controller, Packet packet, boolean sendToServers) {

        controller.sendToAdministrators(packet);
        controller.sendToJudges(packet);
        controller.sendToScoreboards(packet);
        if (sendToServers) {
            controller.sendToServers(packet);
        }
    }
    
    private void writeValues(String key, long number) {
        System.out.print(key + "=" + number + DELIMITER + " ");

    }

    private void writeValues(String key, String value) {
        System.out.print(key + "=" + value + DELIMITER + " ");
    }

    private void dump(String message, PlaybackEvent playbackEvent) {

        Run run = playbackEvent.getRun();

        System.out.println(message);
        writeValues(PlaybackManager.ACTION_KEY, playbackEvent.getAction().toString());
        writeValues(PlaybackManager.ID_KEY, run.getNumber());
        writeValues(PlaybackManager.ELAPSED_KEY, run.getElapsedMins());
        writeValues(PlaybackManager.LANGUAGE_KEY, run.getLanguageId().toString());
        writeValues(PlaybackManager.PROBLEM_KEY, run.getProblemId().toString());
        writeValues(PlaybackManager.SITE_KEY, run.getSiteNumber());
        writeValues(PlaybackManager.SUBMIT_CLIENT_KEY, run.getSubmitter().getName());
        writeValues("File size", playbackEvent.getFiles()[0].getBuffer().length);
        System.out.println();
    }

    public void executeEvent(PlaybackEvent playbackEvent, IInternalContest contest, IInternalController controller) throws Exception {

        if (Utilities.isDebugMode()){
          dump("in executeEvent", playbackEvent);
        }

        switch (playbackEvent.getAction()) {
            case RUN_SUBMIT:
                SerializedFile file = playbackEvent.getFiles()[0];
                SerializedFile[] files = new SerializedFile[1];
                files[0] = file;

                RunFiles runFiles = new RunFiles(playbackEvent.getRun(), file, new SerializedFile[0]);
                Run theRun = playbackEvent.getRun();

                long savedElapsed = theRun.getElapsedMins();
                Run newRun = contest.acceptRun(theRun, runFiles);

                if (savedElapsed > 0) {
                    newRun.setElapsedMins(savedElapsed);
                }

                sequenceNumber++;
                playbackEvent.setEventStatus(EventStatus.COMPLETED);
                
                ClientId fromId = contest.getClientId();
                // Send to team
                Packet confirmPacket = PacketFactory.createRunSubmissionConfirm(contest.getClientId(), fromId, newRun);
                controller.sendToClient(confirmPacket);

                // Send to clients and servers
                sendToJudgesAndOthers(controller, confirmPacket, true);


                break;

            default:
                throw new Exception(playbackEvent.getAction().toString());
        }

    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

}
