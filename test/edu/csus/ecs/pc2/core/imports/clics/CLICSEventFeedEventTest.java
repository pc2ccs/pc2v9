package edu.csus.ecs.pc2.core.imports.clics;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class CLICSEventFeedEventTest extends AbstractTestCase {

    private ObjectMapper mapperField;

    public void testTestNullData() throws Exception {

        ObjectMapper mapper = getMapper();
        String json = "{\"type\": \"problems\", \"id\": \"542cc6e2-104a-49bc-9fea-04752f9af5ad\", \"op\": \"delete\", \"data\": null }";
        CLICSEventFeedEvent eventFeedEntry = (CLICSEventFeedEvent) mapper.readValue(json, CLICSEventFeedEvent.class);
        assertNull(eventFeedEntry.getData());

    }

    public void testTestWithData() throws Exception {

        String eventLines[] = { "{\"type\": \"organizations\", \"id\": \"1add73b6-c66f-46de-a81e-a38773613394\", \"op\": \"delete\", \"data\": {\"id\": \"rsu_ac_in\"}}", //
                "{\"type\": \"organizations\", \"id\": \"1add73b6-c66f-46de-a81e-a38773613394\", \"op\": \"delete\", \"data\": {\"id\": \"rsu_ac_in\"}}", //
                "{\"type\": \"problems\", \"id\": \"542cc6e2-104a-49bc-9fea-04752f9af5ad\", \"op\": \"delete\", \"data\": {\"id\": \"emptyingbaltic\"}}", //
                "{\"type\": \"teams\", \"id\": \"ad403f4d-0068-44c3-a527-b0db86a991c0\", \"op\": \"delete\", \"data\": {\"id\": \"409905\"}}", //
                "{\"type\":\"start-status\",\"id\":\"cdR8682\",\"op\":\"delete\",\"data\":{\"id\":\"Stroopkoeken \ud83c\udf6a\"}}", //
        };

        ObjectMapper mapper = getMapper();

        for (String json : eventLines) {
            CLICSEventFeedEvent eventFeedEntry = (CLICSEventFeedEvent) mapper.readValue(json, CLICSEventFeedEvent.class);
//            System.out.println("debug data type " + eventFeedEntry.getData().getClass().getName());
            assertNotNull(eventFeedEntry.getData());

            assertTrue(eventFeedEntry.getData() instanceof HashMap<?, ?>);
        }

    }
    
    /**
     * Test problem statement element from CLICS event feed
     * 
     * @throws Exception
     */
    public void testCLICSProblem() throws Exception {

        String filename = getTestFilename("event-feed.4probs.nljson");

        assertFileExists(filename);
//        editFile(filename);

        String[] eventLines = Utilities.loadFile(filename);

        ObjectMapper mapper = getMapper();

        for (String json : eventLines) {
            CLICSEventFeedEvent eventFeedEntry = (CLICSEventFeedEvent) mapper.readValue(json, CLICSEventFeedEvent.class);

            assertNotNull("Expecting data in JSON " + json, eventFeedEntry.getData());

            if (CLICSEventType.PROBLEMS.toString().equals(eventFeedEntry.getType())) {

                CLICSProblem problem = mapper.convertValue(eventFeedEntry.getData(), CLICSProblem.class);
                assertNotNull(problem);

                assertNotNull(problem.getId());

                List<CLICSStatement> statements = problem.getStatement();
                assertNotNull(statements);

                if ("imperfectimperialunits".equals(problem.getId())) {

                    // test statement parse
                    CLICSStatement statement = statements.get(0);

//                    System.out.println("debug statement "+statement.toJSON());

                    assertEquals("getHref", "contests/bapc2022/problems/imperfectimperialunits/statement", statement.getHref());
                    assertEquals("getMime", "application/pdf", statement.getMime());
                    assertEquals("getFilename", "I.pdf", statement.getFilename());
                }
            }
        }
    }

    /**
     * Get an object mapper that ignores unknown properties.
     * 
     * @return an object mapper that ignores unknown properties
     */
    public ObjectMapper getMapper() {
        if (mapperField != null) {
            return mapperField;
        }

        mapperField = new ObjectMapper();
        mapperField.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapperField;
    }

}
