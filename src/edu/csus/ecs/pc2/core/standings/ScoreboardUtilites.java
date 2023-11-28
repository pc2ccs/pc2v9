// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JSONObjectMapper;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.standings.json.ScoreboardJsonModel;

public class ScoreboardUtilites {

    /**
     * Create x from XML StringContestStandings
     * 
     * @param xmlString
     * @return
     * @throws JAXBException
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    public static ContestStandings createContestStandings(String xmlString) throws JAXBException, JsonParseException, JsonMappingException, IOException {

        //JAXB was deprecated in Java 9 and removed entirely in Java 11; don't use it...
//        JAXBContext jaxbContext = JAXBContext.newInstance(ContestStandings.class);
//        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//        ContestStandings contestStandings = (ContestStandings) jaxbUnmarshaller.unmarshal(new InputSource(new StringReader(xmlString)));
        
        XmlMapper xmlMapper = new XmlMapper();
        ContestStandings standings = xmlMapper.readValue(xmlString, ContestStandings.class);
        return standings;
    }

    /**
     * Create ContestStandings from file
     * 
     * @param xmlFile
     * @return
     * @throws JAXBException
     */
    public static ContestStandings createContestStandings(File xmlFile) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(ContestStandings.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ContestStandings contestStandings = (ContestStandings) jaxbUnmarshaller.unmarshal(xmlFile);
        return contestStandings;
    }

    public static String createScoreboardXML(IInternalContest contest) throws IllegalContestState {

        DefaultScoringAlgorithm scoringAlgorithm = new DefaultScoringAlgorithm();
        Properties properties = getScoringProperties(contest);
        String xml = scoringAlgorithm.getStandings(contest, properties, StaticLog.getLog());
        return xml;
    }
    
    public static ContestStandings createContestStandings(IInternalContest contest) throws JAXBException, IllegalContestState, JsonParseException, JsonMappingException, IOException {
        String xmlString = ScoreboardUtilites.createScoreboardXML(contest);
        return createContestStandings(xmlString);
    }
    
    
    public static List <StandingsRecord> createStandingsRecords (String jsonString, String source) throws JsonProcessingException, IOException{
        List<StandingsRecord> list = new ArrayList<StandingsRecord>();
        
        ObjectMapper mapper = JSONObjectMapper.getObjectMapper();
        JsonNode tree = mapper.readTree(jsonString);
        
        for (JsonNode jsonNode : tree) {
            if (jsonNode.isArray())
            {
                /**
                 * Array of team score rows, with "rank" as first field.
                 */
                ArrayNode arrayNode = (ArrayNode) jsonNode;
                for (int i = 0; i < arrayNode.size(); i++) {
                    JsonNode node = jsonNode.get(i);

                    StandingsRecord record = new StandingsRecord();
                    record.setSource(source);

                    // OBJECT {"rank":4,"team_id":325950,"score":{"num_solved":12,"total_time":200},"

                    record.setRank(node.get("rank").asInt());
                    record.setTeamId(node.get("team_id").asInt());

                    JsonNode scoreNode = node.get("score");

                    record.setSolved(scoreNode.get("num_solved").asInt());
                    record.setPoints(scoreNode.get("total_time").asInt());
                    
                    if (record.getTeamId() != 0) {
                        list.add(record);
                    }
                }
            }
        }
        
        return list;
    }

    /**
     * Get the scoring properties from the model.
     * 
     * @return scoring properties
     */
    public static Properties getScoringProperties(IInternalContest contest) {

        Properties properties = contest.getContestInformation().getScoringProperties();
        if (properties == null) {
            properties = new Properties();
        }

        Properties defProperties = DefaultScoringAlgorithm.getDefaultProperties();

        /**
         * Fill in with default properties if not using them.
         */
        String[] keys = (String[]) defProperties.keySet().toArray(new String[defProperties.keySet().size()]);
        for (String key : keys) {
            if (!properties.containsKey(key)) {
                properties.put(key, defProperties.get(key));
            }
        }

        return properties;
    }

    public Document createDocument(String xml) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));
        return document;

    }

    public static String loadFileContents(File file) throws IOException {
        
        String[] lines = Utilities.loadFile(file.getAbsolutePath());
        
        String contents = lines[0];
        if (lines.length > 1) {
            contents = String.join("", lines);
        }

        return contents;
    }

    public static ScoreboardJsonModel createContestStandingsFromJSON(File jsonFile) throws JAXBException, JsonParseException, JsonMappingException, IOException  {
        ObjectMapper mapper = JSONObjectMapper.getObjectMapper();
        ScoreboardJsonModel scoreJsonModel = mapper.readValue(jsonFile, ScoreboardJsonModel.class);
        return scoreJsonModel;
    }

    public static int toInt(String string, int defaultNumber) {
        return Utilities.nullSafeToInt(string, defaultNumber);
    }

    public static Run[] getRunsForUserDivision(ClientId clientId, IInternalContest contest) {

        String division = getDivision(contest, clientId);
        
//        System.out.println("debug 22 getRunsForUserDivision for "+clientId+" div is "+division);

        if (ClientType.Type.TEAM.equals(clientId.getClientType())) {

            List<Run> theDivisionTeamRuns = new ArrayList<Run>();
            for (Run run : contest.getRuns()) {

                ClientId runClientId = run.getSubmitter();

                if (runClientId.equals(clientId)) {
                    // add team/client's own runs
                    theDivisionTeamRuns.add(run);
                } else {
                    // add if submitting team in same division
                    if (matchDivsion(contest, division, run.getSubmitter())) {
                        theDivisionTeamRuns.add(run);
//                        System.out.println("debug 22 Added run " + run);
                    }
                }
            }

            return (Run[]) theDivisionTeamRuns.toArray(new Run[theDivisionTeamRuns.size()]);
            
        } else {
            return contest.getRuns();
        }
    }
    
    public static Run[] getRunsForDivision(IInternalContest contest, String division) {

        List<Run> theDivisionTeamRuns = new ArrayList<Run>();
        for (Run run : contest.getRuns()) {

            // add if submitting team in same division
            if (matchDivsion(contest, division, run.getSubmitter())) {
                theDivisionTeamRuns.add(run);
            }
        }

        return (Run[]) theDivisionTeamRuns.toArray(new Run[theDivisionTeamRuns.size()]);

    }

    /**
     * Is the submitter in the inputDivision?
     * @param contest
     * @param inputDivision
     * @param submitter
     * @return true if submitter division matches inputDivision, else false
     */
    protected static boolean matchDivsion(IInternalContest contest, String inputDivision, ClientId submitter) {

        String division = getDivision(contest, submitter);

        if (inputDivision == null && division == null) {
            return true;
        }
        if (inputDivision == null) {
            return false;
        } else {
            return inputDivision.equals(division);
        }
    }

    /**
     * Return division for input clientId.
     * 
     * @param contest
     * @param submitter
     * @return null if no division, else a digit
     */
    public static String getDivision(IInternalContest contest, ClientId submitter) {
        
        ElementId groupId = contest.getAccount(submitter).getGroupId();
        if (groupId == null) {
            return null;
        }
        
        Group group = contest.getGroup(groupId);
        if (group == null) {
            return null;
        }
        
        String groupName = group.getDisplayName().trim();
        
        return getDivision(groupName);
    }

    /**
     * Return division number from groupName
     * @param groupName
     * @return null if no division nubmer found, else the division number
     */
    // TODO REFACTOR i689 redesign how divisions are identified. 
    public static String getDivision(String groupName) {

        int idx = groupName.lastIndexOf('D');
        if (idx != -1) {
            // expecting D# at end of string
            if (idx == groupName.length() - 2) {
                return groupName.substring(idx+1);
            }
        }
        return null;
    }

    /**
     * Return Group for input clientId.
     * 
     * @param contest
     * @param submitter
     * @return null if no group, else the Group
     */
    public static Group getGroup(IInternalContest contest, ClientId submitter) {
        
        ElementId groupId = contest.getAccount(submitter).getGroupId();
        if (groupId == null) {
            return null;
        }
        
        return(contest.getGroup(groupId));
    }

}
