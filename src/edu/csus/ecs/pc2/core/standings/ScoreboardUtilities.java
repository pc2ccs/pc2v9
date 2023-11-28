// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JSONObjectMapper;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.standings.json.ScoreboardJsonModel;

public class ScoreboardUtilities {

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

    /**
     * Creates the XML scoreboard for admins (does not obey freeze time)
     *
     * @param contest The contest
     * @return unfrozen xml scoreboard
     * @throws IllegalContestState
     */
    public static String createScoreboardXML(IInternalContest contest) throws IllegalContestState {

        return(createScoreboardXML(contest, false));
    }

    /**
     * Creates the XML scoreboard, optionally obeying the freeze time
     *
     * @param contest The contest
     * @param obeyFreeze if true, obey freeze time from properties
     * @return The XML scoreboard
     * @throws IllegalContestState
     */
    public static String createScoreboardXML(IInternalContest contest, boolean obeyFreeze) throws IllegalContestState {

        DefaultScoringAlgorithm scoringAlgorithm = new DefaultScoringAlgorithm();
        scoringAlgorithm.setObeyFreeze(obeyFreeze);

        Properties properties = getScoringProperties(contest);
        String xml = scoringAlgorithm.getStandings(contest, properties, StaticLog.getLog());
        return xml;
    }

    public static ContestStandings createContestStandings(IInternalContest contest) throws JAXBException, IllegalContestState, JsonParseException, JsonMappingException, IOException {
        String xmlString = ScoreboardUtilities.createScoreboardXML(contest);
        return createContestStandings(xmlString);
    }

    public static ContestStandings createContestStandings(IInternalContest contest, boolean obeyFreeze) throws JAXBException, IllegalContestState, JsonParseException, JsonMappingException, IOException {
        String xmlString = ScoreboardUtilities.createScoreboardXML(contest, obeyFreeze);
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
        String[] keys = defProperties.keySet().toArray(new String[defProperties.keySet().size()]);
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

            return theDivisionTeamRuns.toArray(new Run[theDivisionTeamRuns.size()]);

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

        return theDivisionTeamRuns.toArray(new Run[theDivisionTeamRuns.size()]);

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
     * TODO To be deprecated when multiple groups are fully implemented.  Although, we have to see if anyone still
     * uses this.
     *
     * @param contest
     * @param submitter
     * @return null if no division, else a digit
     */
    public static String getDivision(IInternalContest contest, ClientId submitter) {

        HashSet<ElementId> groups = contest.getAccount(submitter).getGroupIds();
        String groupName = null;

        if(groups != null) {
            for(ElementId elementId: groups) {
                Group group = contest.getGroup(elementId);
                if(group != null) {
                    groupName = getDivision(group.getDisplayName());
                    if(groupName != null) {
                        break;
                    }
                }
            }
        }

        return groupName;
    }

    /**
     * Return division number from groupName
     * @param groupName
     * @return null if no division number found, else the division number
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
     * Get the runs that are only for the desired groups.
     * If null, then all runs are returned.
     *
     * @param theContest (used for getting accounts and runs for the contest)
     * @param wantedGroups
     * @return array of Run filtered by division and groups
     */
    public static Run [] getGroupFilteredRuns(IInternalContest theContest, List<Group> wantedGroups) {

        Run [] runs = theContest.getRuns();
        if(wantedGroups != null && wantedGroups.size() > 0) {

            // hash map to speed up looking up Account from client id.  theContest.getAccount() is grossly inefficient
            HashMap<String, Account> clientToAccount = new HashMap<String, Account>();
            ArrayList<Run> newruns = new ArrayList<Run>();
            ClientId runClient;
            String cKey;
            Account runAccount;

            // build a new ArrayList of runs that satisify the wanted group filter
            for(Run r : runs) {
                runClient = r.getSubmitter();
                cKey = runClient.getTripletKey();
                runAccount = clientToAccount.get(cKey);
                if(runAccount == null) {
                    // not in hash table, so we must look it up, then add it to hash table
                    runAccount = theContest.getAccount(runClient);
                    if(runAccount == null) {
                        // sanity check - there better be an account for the run, or we'll just ignore the run.
                        continue;
                    }
                    clientToAccount.put(cKey, runAccount);
                }
                for(Group group : wantedGroups) {
                    if(runAccount.isGroupMember(group.getElementId())) {
                        newruns.add(r);
                    }
                }
            }
            // convert to Run [] */
            runs = newruns.toArray(new Run [0]);
        }
        return(runs);
    }

    /**
     * Checks if the supplied account is a member of any of the groups in the supplied List of groups
     *
     * @param account to check
     * @param wantedGroups
     * @return true if the account is in one of the wanted groups, false otherwise
     */
    public static boolean isWantedTeam(Account account, List<Group> wantedGroups) {
        // Assume we want this account
        boolean ret = true;
        if(wantedGroups != null && wantedGroups.size() > 0) {
            boolean found = false;
            // restricted to these groups only
            for(Group group : wantedGroups) {
                if(account.isGroupMember(group.getElementId())) {
                    found = true;
                    break;
                }
            }
            ret = found;
        }
        return(ret);
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
