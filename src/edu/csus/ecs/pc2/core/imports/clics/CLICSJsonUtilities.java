// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.list.RunComparatorByElapsedRunIdSite;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.security.Permission.Type;
import edu.csus.ecs.pc2.core.standings.ContestStandings;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilites;
import edu.csus.ecs.pc2.core.standings.json.ScoreboardJsonModel;
import edu.csus.ecs.pc2.core.standings.json.StandingScore;
import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;

/**
 * A set of methods for CLICS API JSON
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class CLICSJsonUtilities {

    public static final String ID_WINNER = "winner";

    public static final String ID_BRONZE_MEDAL = "bronze-medal";

    public static final String ID_SILVER_MEDAL = "silver-medal";

    public static final String ID_GOLD_MEDAL = "gold-medal";

    private static ObjectMapper mapperField;

    // {"id":"group-winner-4","citation":"Winner(s) of group Eindhoven University of Technology","team_ids":["28"]},

//  "citation": "Winner(s) of group Delft University of Technology", 
// "citation": "Winner(s) of group Eindhoven University of Technology", 
//
//
// "citation": "First to solve problem crashingcompetitioncomputer", 
// "citation": "First to solve problem grindinggravel", 
// "citation": "First to solve problem housenumbering", 
//
// "citation": "Contest winner",     
    
    /**
     * Return a list of CLICS Awards for a contest.
     * 
     * @param contest
     * @return
     * @throws IOException
     * @throws IllegalContestState
     * @throws JAXBException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static List<CLICSAward> createAwardsList(IInternalContest contest) throws JsonParseException, JsonMappingException, JAXBException, IllegalContestState, IOException {
        List<CLICSAward> list = new ArrayList<CLICSAward>();

        Run[] runs = contest.getRuns();

        if (runs.length == 0) {
            return list;
        }
        
        addWinner(contest, list);

        addFirstToSolve(contest, runs, list);

        addGroupWinners(contest, runs, list);
        
        addMedals(contest, list);

        return list;
    }

    public static void addGroupWinners(IInternalContest contest, Run[] runs, List<CLICSAward> list) {

        Arrays.sort(runs, new RunComparatorByElapsedRunIdSite());

        Group[] groups = contest.getGroups();

        if (groups.length == 0) {
            // no groups defined, no group winners
            return;
        }

        /**
         * Group Id and Client/Team Id
         */
        Map<Group, ClientId> groupWinners = new HashMap<Group, ClientId>();

        for (Run run : runs) {
            if (run.isSolved()) {
                Group teamGroup = getGroupForTeam(contest, run.getSubmitter());

                if (teamGroup != null) {
                    ClientId clientId = groupWinners.get(teamGroup);
                    if (clientId == null && isActive(contest, run.getSubmitter())) {
                        groupWinners.put(teamGroup, run.getSubmitter());
                    }
                }
            }
        }

        Set<Group> problemElementIds = groupWinners.keySet();
        for (Group group : problemElementIds) {
            // first to solve for group
            ClientId clientId = groupWinners.get(group);
            if (clientId != null) {
                Account account = contest.getAccount(clientId);

//            "citation": "Winner(s) of group University of Luxembourg", 
//            "id": "group-winner-17"

                String awardId = "group-winner-" + group.getGroupId();
                String citation = "Winner(s) of group " + account.getDisplayName();

                CLICSAward groupWinner = new CLICSAward(awardId, citation, "" + clientId.getClientNumber());
                list.add(groupWinner);
            }

        }
    }

    private static boolean isActive(IInternalContest contest, ClientId clientId) {
        Account account = contest.getAccount(clientId);
        if (account != null && account.isAllowed(Type.DISPLAY_ON_SCOREBOARD)) {
            return true;
        }
        return false;
    }

    /**
     * Get group for team/account.
     * 
     * @param contest
     * @param submitter clientId for team
     * @return null if not found, else the group
     */
    public static Group getGroupForTeam(IInternalContest contest, ClientId submitter) {
        Account account = contest.getAccount(submitter);
        if (account != null) {
            ElementId groupElementId = account.getGroupId();
            if (groupElementId != null) {
                Group group = contest.getGroup(groupElementId);
                if (group != null) {
                    return group;
                }
            }
        }
        return null;
    }
    
    
    public static void addMedals(IInternalContest contest, List<CLICSAward> list) throws JsonParseException, JsonMappingException, JAXBException, IllegalContestState, IOException {
     
        
        ContestStandings contestStandings = ScoreboardUtilites.createContestStandings(contest);
        ScoreboardJsonModel model = new ScoreboardJsonModel(contestStandings);
        
//        List<TeamScoreRow> rows = model.getRows();
//        for (TeamScoreRow teamScoreRow : rows) {
//            System.out.println("debug  srow "+getStandingsRow(teamScoreRow));
//        }
        
        TeamScoreRow teamRow = model.getRows().get(0);
        if (teamRow.getScore().getNum_solved() > 0) {
            
            List<TeamScoreRow> scoreRows = model.getRows();
            
            // only assign medals to those who have solved a problem

            List<String> teamList = getTeamids (0, 3, scoreRows);
            if (teamList.size () > 0) {
                //  "citation": "Gold Medalist", 
                //  "id": "gold-medal"
                CLICSAward firstToSolveAward = new CLICSAward(ID_GOLD_MEDAL,"Gold Medalist" , teamList);
                list.add(firstToSolveAward);
            }


            teamList = getTeamids (4, 7, scoreRows);
            if (teamList.size () > 0) {
                CLICSAward firstToSolveAward = new CLICSAward(ID_SILVER_MEDAL,"Silver Medalist" , teamList);
                list.add(firstToSolveAward);
            }

            /**
             * number of bronzes beyond rank 12
             */
            int b = 0; 
            FinalizeData finalizeData = contest.getFinalizeData();
            if (finalizeData != null) {
                b = finalizeData.getBronzeRank();
            }

            teamList = getTeamids (8, scoreRows.size()-8, 12 + b, scoreRows);
            if (teamList.size () > 0) {
                CLICSAward firstToSolveAward = new CLICSAward(ID_BRONZE_MEDAL,"Bronze Medalist" , teamList);
                list.add(firstToSolveAward);
            }
        }
    }
        

    /**
     * get team list for reams who have solved at least one problem.
     * @param startIdx
     * @param endidx
     * @param maxrank only include teams ids where their rank is <= maxrank
     * @param teamRows
     * @return
     */
    private static List<String>  getTeamids(int startIdx, int endidx, int maxrank, List<TeamScoreRow> teamRows) {

        
        List<String> list = new ArrayList<String>();

        for (int rowidx = startIdx; rowidx <= endidx; rowidx++) {

            if (rowidx < teamRows.size()) {
                TeamScoreRow row = teamRows.get(rowidx);

                if (row.getScore().getNum_solved() > 0) {
                    // must solve at least one to be medal winner

                    if (row.getRank() <= maxrank ) {
                        list.add("" + row.getTeam_id());
                    }


                }
            }
        }
        
        return list;
    }

    /**
     * get team list for reams who have solved at least one problem.
     * @param startIdx
     * @param endidx
     * @param teamRows
     * @return
     */
    private static List<String> getTeamids(int startIdx, int endidx, List<TeamScoreRow> teamRows) {
        return getTeamids(  startIdx,   endidx, Integer.MAX_VALUE, teamRows);
    }


    /**
     * Add winner award.
     * 
     * @param contest
     * @param list
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws JAXBException
     * @throws IllegalContestState
     * @throws IOException
     */
    public static void addWinner(IInternalContest contest, List<CLICSAward> list) throws JsonParseException, JsonMappingException, JAXBException, IllegalContestState, IOException {
        
        ContestStandings contestStandings = ScoreboardUtilites.createContestStandings(contest);
        ScoreboardJsonModel model = new ScoreboardJsonModel(contestStandings);
        
        String winnerId = null;
        
        /**
         * Get teams in rank order.
         */
        List<TeamScoreRow> rows = model.getRows();
        int site = contest.getSiteNumber();
        
        for (TeamScoreRow teamRow : rows) {
            int clientNumber = teamRow.getTeam_id();
            ClientId clientId = new ClientId(site, ClientType.Type.TEAM, clientNumber);
            if (teamRow.getScore().getNum_solved() > 0 && 
            winnerId == null & isActive(contest, clientId ) ) {
                winnerId = Integer.toString( teamRow.getTeam_id());
                
            }
        }

        if (winnerId != null) {
            CLICSAward firstToSolveAward = new CLICSAward(ID_WINNER, "Contest winner", winnerId);
            list.add(firstToSolveAward);
        }
        
    }

    /**
     * Add first to solve awards to list
     * 
     * @param contest
     * @param runs 
     * @param runs
     * @param list
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws JAXBException
     * @throws IllegalContestState
     * @throws IOException
     */
    private static void addFirstToSolve(IInternalContest contest, Run[] runs, List<CLICSAward> list) throws JsonParseException, JsonMappingException, JAXBException, IllegalContestState, IOException {

        Arrays.sort(runs, new RunComparatorByElapsedRunIdSite());
        Map<ElementId, ClientId> firstToSolveTeamId = new HashMap<ElementId, ClientId>();

        for (Run run : runs) {
            if (run.isSolved()) {
                ClientId clientId = firstToSolveTeamId.get(run.getProblemId());
                if (clientId == null && isActive(contest, run.getSubmitter())) {;
                    firstToSolveTeamId.put(run.getProblemId(), run.getSubmitter());
                }
            }
        }

        Set<ElementId> problemElementIds = firstToSolveTeamId.keySet();
        for (ElementId eId : problemElementIds) {
            // first to solve for problem.

            Problem problem = contest.getProblem(eId);
            ClientId clientId = firstToSolveTeamId.get(eId);

            String awardId = "first-to-solve-" + problem.getShortName();
            String citation = "First to solve problem " + problem.getShortName();

            CLICSAward firstToSolveAward = new CLICSAward(awardId, citation, "" + clientId.getClientNumber());
            list.add(firstToSolveAward);

        }
    }

    static String getStandingsRow(TeamScoreRow row) {
        StandingScore scoreRow = row.getScore();

        return row.getRank() + " " + //
        row.getTeam_id() + " - " + //
        scoreRow.getNum_solved() + " " + //
        scoreRow.getTotal_time();
    }

    /**
     * Load list of CLICS awards json from file.
     * 
     * @param filename
     * @return
     * @throws IOException
     */
    public static List<CLICSAward> readAwardsList(String filename) throws IOException {
        List<CLICSAward> list = new ArrayList<CLICSAward>();

        String[] lines = Utilities.loadFile(filename);
        String json = String.join(" ", lines);

        List<CLICSAward> inList = getMapper().readValue(json, new TypeReference<List<CLICSAward>>() {});
        if (inList != null) {
            list = inList;
        }

        return list;
    }

    /**
     * Get an object mapper that ignores unknown properties.
     * 
     * @return an object mapper that ignores unknown properties
     */
    public static final ObjectMapper getMapper() {

        if (mapperField != null) {
            return mapperField;
        }

        mapperField = new ObjectMapper();
        mapperField.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapperField;
    }

    /**
     * Writes awards elements to file.
     * 
     * @param filename
     * @param awards
     * @return numnber of award elements written
     * @throws Exception
     */
    public static int writeAwardsJSONFile(String filename, List<CLICSAward> awards) throws Exception {
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        return writeAwardsJSONFile(printWriter, awards);
    }
    
    /**
     * Writes awards elements to printWriter
     * 
     * @param printWriter
     * @param awards
     * @return number of award elements written
     * @throws Exception
     */
    public static int writeAwardsJSONFile(PrintWriter printWriter, List<CLICSAward> awards) throws Exception {

        int rowsWritten = 0;
        
        int numrows = awards.size();
        
        Exception ex = null;
        try {
            
            printWriter.print("[");
            for (int i = 0; i < numrows; i++) {
                CLICSAward clicsAward = awards.get(i);
                try {
                    printWriter.print(clicsAward.toJSON());
                    rowsWritten++;
                    if (rowsWritten < numrows) {
                        printWriter.print(",");
                    }
                } catch (Exception e) {
                    if (ex == null) {
                        ex = e;
                    }
                }
            }
            printWriter.print("]");

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            if (ex == null) {
                ex = e;
            }
        }

        if (ex != null) {
            throw ex;
        }
        
        return rowsWritten;
    }

}
