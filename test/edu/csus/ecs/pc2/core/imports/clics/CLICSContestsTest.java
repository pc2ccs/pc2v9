// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.ContestCompareModel;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class CLICSContestsTest extends AbstractTestCase {

    private ObjectMapper mapperField;

    public void testContestsElement() throws Exception {

        String json = "{\"token\":\"1567\",\"id\":null,\"type\":\"contest\"," + //
                "\"data\":{\"formal_name\":\"Benelux Algorithm Programming Contest 2022\",\"penalty_time\":20," + //
                "\"start_time\":\"2022-10-22T12:30:00+02:00\",\"end_time\":\"2022-10-22T17:30:00+02:00\",\"duration\":\"5:00:00.000\",\"scoreboard_freeze_duration\":\"1:00:00.000\",\"id\":\"bapc2022\",\"external_id\":\"bapc2022\",\"name\":\"Benelux Algorithm Programming Contest 2022\",\"shortname\":\"bapc2022\"},\"time\":\"2022-10-21T12:35:37.218+02:00\"}";

        ObjectMapper mapper = getMapper();
//        String json = "{\"type\": \"problems\", \"id\": \"542cc6e2-104a-49bc-9fea-04752f9af5ad\", \"op\": \"delete\", \"data\": null }";

        CLICSEventFeedEvent eventFeedEntry = (CLICSEventFeedEvent) mapper.readValue(json, CLICSEventFeedEvent.class);
        assertNotNull(eventFeedEntry.getData());

        CLICSContests clicsContests = mapper.convertValue(eventFeedEntry.getData(), CLICSContests.class);

        String cdpPath = getDataDirectory(getName());
        
//        ensureDirectory(cdpPath + File.separator + IContestLoader.CONFIG_DIRNAME);
//        startExplorer(cdpPath);
        
        IInternalContest contest = loadContest(cdpPath);
        
        ContestInformation info = contest.getContestInformation();
     
        ContestTime contestTime = contest.getContestTime();
        assertNotNull("Contest Time is NULL", contestTime);

        String CCS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
        SimpleDateFormat formatter = new SimpleDateFormat(CCS_DATE_FORMAT);
        // formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar startTime = info.getScheduledStartTime();
        String startTimeStr = formatter.format(startTime.getTime());

        assertEquals("formal_name", info.getContestTitle(), clicsContests.getFormal_name());
        assertEquals("shortname", info.getContestShortName(), clicsContests.getShortname());

        assertEquals("duration", contestTime.getContestLengthStr(), ContestCompareModel.clipMs(clicsContests.getDuration()));

        String penaltyTime = info.getScoringProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO);

        // TODO CCS penalty-time not loaded from contest.yaml
//        assertEquals("penalty_time", penaltyTime, clicsContests.getPenalty_time());
 
        assertEquals("scoreboard_freeze_duration", info.getFreezeTime().substring(1), ContestCompareModel.clipMs(clicsContests.getScoreboard_freeze_duration()));


        // TODO uncomment this test when the logic to handle timezones in start time is fixed.
        // from contest.yaml - 2022-10-22T03:30:00-0700
        // from getStart_time - 2022-10-22T12:30:00+02:00
//        assertEquals("start_time", startTimeStr, clicsContests.getStart_time());
        
        assertEquals("2022-10-22T12:30:00+02:00", clicsContests.getStart_time());
        
        assertNotNull(clicsContests);

    }

    private IInternalContest loadContest(String cdpPath) {

//      File cdpConfigDir = new File(cdpPath); 
        File cdpConfigDir = FileUtilities.findCDPConfigDirectory(new File(cdpPath));

        assertDirectoryExists(cdpConfigDir.getAbsolutePath(), "Expected contest CDP directory to exist ");
        ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();
        IInternalContest contest = loader.fromYaml(null, cdpConfigDir.getAbsolutePath());
        return contest;
    }

    /**
     * Get an object mapper that ignores unknown properties.
     * 
     * @return an object mapper that ignores unknown properties
     */
    // TODO REFACTOR move into JSONUtilities
    public ObjectMapper getMapper() {
        if (mapperField != null) {
            return mapperField;
        }

        mapperField = new ObjectMapper();
        mapperField.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapperField;
    }

}
