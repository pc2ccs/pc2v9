// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.log;

import java.util.HashMap;
import java.util.Map;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ClientId;

/**
 * A log manager that creates and closes pc2 client (esp teams) log files.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class PC2LogManager {

    /**
     * Default name for single pc2 log file.
     */
    public static final String PC2_SINGLE_LOG_FILENAME = "pc2log.log";

    /**
     * The single log
     */
    private static Log log = null;

    /**
     * Storage for if there is more than one log per client.
     */
    private static Map<ClientId, Log> clientLogs = new HashMap<ClientId, Log>();

    private static LogType logType = LogType.ONE_LOG_PER_CLIENT;

    private static String directoryName = Log.LOG_DIRECTORY_NAME;

    private static String singleLogFileName = PC2_SINGLE_LOG_FILENAME;

    /**
     * Create a single log per application
     */
    public PC2LogManager(String baseLogFileName) {
        this(LogType.ONE_LOG_FOR_ALL_CLIENTS);
    }

    /**
     * Create logs based on logType
     * 
     * @param logType 
     */
    public PC2LogManager(LogType logType) {
        this.logType = logType;

        Utilities.insureDir(directoryName);
    }

    public static Log createLog(ClientId clientId) {

        switch (logType) {
            case ONE_LOG_FOR_ALL_CLIENTS:
                return createSingleLog();

            default:
                return craateClientLog(clientId);
        }
    }

    /**
     * Create 
     * @param clientId
     * @return
     */
    private static Log craateClientLog(ClientId clientId) {

        // Does not check for a log in the clientLogs at this time.

        // TODO handle potential memory leak when there may already be a log in the clientLogs hash

        //       ex. ADMINISTRATOR1.site1-0.log
        String logFileName = clientId.getClientType().toString() + clientId.getClientNumber()+ ".site-" + clientId.getSiteNumber() + ".log";
        log = new Log(directoryName, logFileName);
        clientLogs.put(clientId, log);
        return log;
    }

    /**
     * Create the single log.
     * 
     */
    public static Log createSingleLog() {
        if (log != null) {
            return log;
        } else {

            String logFileName = getSingleLogFileName() + ".log";
            log = new Log(directoryName, logFileName);
            return log;
        }
    }

    /**
     * Close log only if LogType is ONE_LOG_PER_CLIENT.
     * 
     * @param clientId
     * @throws Throwable
     */
    public static void closeLog(ClientId clientId) throws Throwable {
        Log log = clientLogs.get(clientId);
        if (log != null) {
            log.close();
            clientLogs.remove(clientId);
        }
    }

    /**
     * Change single log to new name, defaults to {{value #PC2_SINGLE_LOG_FILENAME}.
     * @param singleLogFileName
     */
    public static void setSingleLogFileName(String singleLogFileName) {
        PC2LogManager.singleLogFileName = singleLogFileName;
    }

    public static String getSingleLogFileName() {
        return singleLogFileName;
    }
    
    protected static void resetLogs() {
         singleLogFileName = PC2_SINGLE_LOG_FILENAME;
         log = null;
         clientLogs = null;
         clientLogs = new HashMap<ClientId, Log>();       
    }

    /**
     * the output log directory.
     * @param directoryName by default is Log/
     */
    public static void setDirectoryName(String directoryName) {
        PC2LogManager.directoryName = directoryName;
    }
    
    public static LogType getLogType() {
        return logType;
    }
}