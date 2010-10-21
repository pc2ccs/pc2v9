package edu.csus.ecs.pc2.api.implementation;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IProblemDetails;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * API generate IStandings array using DefaultScoringAlgorithm. 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class GenerateStandings {

    private ProblemDetailsComparator detailsComparator = new ProblemDetailsComparator();
    
    /**
     * Fetch string from nodes.
     * 
     * @param node
     * @return
     */
    private String[] fetchStandingRow(Node node) {

        // Object[] cols = { 0 "Rank", 1 "Name", 2 "Solved", 3 "Points", 4 teamId, 5 siteId, 6 teamKey };

        String[] outArray = new String[7];

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node standingNode = attributes.item(i);
            String value = standingNode.getNodeValue();
            String name = standingNode.getNodeName();
            if (name.equals("rank")) {
                outArray[0] = value;
            } else if (name.equals("teamName")) {
                outArray[1] = value;
            } else if (name.equals("solved")) {
                outArray[2] = value;
            } else if (name.equals("points")) {
                outArray[3] = value;
            } else if (name.equals("teamId")) {
                outArray[4] = value;
            } else if (name.equals("teamSiteId")) {
                outArray[5] = value;
            } else if (name.equals("teamKey")) {
                outArray[6] = value;
            }
        }

        return outArray;
    }

    /**
     * return IProblemDetails for client and problem from detailList.
     * 
     * @param client
     * @param problem
     * @param detailList
     * @return
     */
    IProblemDetails[] getDetails(IClient client, IProblemDetails[] detailList) {

        Vector<IProblemDetails> outvect = new Vector<IProblemDetails>();

        for (IProblemDetails d : detailList) {
            if (d.getClient().getAccountNumber() == client.getAccountNumber()) {
                if (d.getClient().getSiteNumber() == client.getSiteNumber()) {
                    outvect.add(d);
                }
            }
        }
        
        IProblemDetails[] details = (IProblemDetails[]) outvect.toArray(new IProblemDetails[outvect.size()]);
        Arrays.sort(details, detailsComparator);
        return details;
    }

    /**
     * Replace/add problem details into standings
     * 
     * @param standings
     * @param details
     */
    public void updateProblemDetails(StandingImplementation[] standingsList, IProblemDetails[] detailsList) {

        for (StandingImplementation standings : standingsList) {
            IProblemDetails[] details = getDetails(standings.getClient(), detailsList);
            standings.setProblemDetails(details);
        }
    }

    /**
     * 
     * @param contest
     * @param xmlString
     * @param log
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public IProblemDetails[] getProblemDetails(IInternalContest contest, String xmlString, Log log)
            throws ParserConfigurationException, SAXException, IOException {

        Vector<IProblemDetails> details = new Vector<IProblemDetails>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
        // skip past nodes to find teamStanding node

        NodeList list = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            Node node = (Node) list.item(i);
            String name = node.getNodeName();
            if (name.equals("teamStanding")) {
                try {
                    String[] cols = fetchStandingRow(node);

                    // Object[] cols = { "Rank", "Name", "Solved", "Points", teamId, siteId, teamKey };

                    int clientNumber = Integer.parseInt(cols[4]);
                    int siteNumber = Integer.parseInt(cols[5]);

                    ClientId clientId = new ClientId(siteNumber, Type.TEAM, clientNumber);

                    IClient client = new ClientImplementation(clientId, contest);

                    IProblemDetails[] clientProblemDetails = getClientProblemDetails(contest, client, node);
                    for (IProblemDetails problemDetails : clientProblemDetails) {
                        details.add(problemDetails);
                    }

                } catch (Exception e) {
                    e.printStackTrace(); // debug
                    log.log(Log.WARNING, "Exception while parsing/generating standings row ", e);
                }
            }
        }

        return (IProblemDetails[]) details.toArray(new IProblemDetails[details.size()]);
    }

    /**
     * Returns all IProblemDetails for all teams.
     * 
     * @param contest
     * @param log
     * @return
     */
    public IProblemDetails[] getProblemDetails(IInternalContest contest, Log log) {

        String xmlString = null;

        try {
            DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
            xmlString = defaultScoringAlgorithm.getStandings(contest, new Properties(), log);
            return getProblemDetails(contest, xmlString, log);

        } catch (Exception e) {
            e.printStackTrace(); // debug

            log.log(Log.WARNING, "Exception logged ", e);
        }

        return new IProblemDetails[0];
    }

    /**
     * 
     * @param contest
     * @param clientId
     * @param parentNode
     * @return
     */
    private IProblemDetails[] getClientProblemDetails(IInternalContest contest, IClient clientId, Node parentNode) {
        Vector<IProblemDetails> detailsList = new Vector<IProblemDetails>();

        NodeList nodeList = parentNode.getChildNodes();

        Problem[] problems = contest.getProblems();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String nodeName = node.getNodeName();

            if (nodeName.equals("problemSummaryInfo")) {

                ProblemDetailsImplementation problemDetails = getClientProblemDetails(node, clientId);

                /**
                 * A bit of code to set the IProblem via .setProblem
                 */
                if (problemDetails.getProblemId() != -1 && problemDetails.getProblemId() < problems.length - 1){
                        
                    ElementId elementId = problems[problemDetails.getProblemId()].getElementId();
                    ProblemImplementation problemImplementation = new ProblemImplementation(elementId, contest);
                    problemDetails.setProblem(problemImplementation);
                    detailsList.add(problemDetails);
                } 
                // else there was no problem data, nothing to add
            }
        }

        return (IProblemDetails[]) detailsList.toArray(new IProblemDetails[detailsList.size()]);
    }

    /**
     * 
     * @param parentNode
     * @param clientId
     * @return
     */
    ProblemDetailsImplementation getClientProblemDetails(Node parentNode, IClient clientId) {

        ProblemDetailsImplementation problemDetailsImplementation = new ProblemDetailsImplementation(clientId);

        NamedNodeMap attributes = parentNode.getAttributes();
        for (int j = 0; j < attributes.getLength(); j++) {
            Node problemNode = attributes.item(j);
            String name = problemNode.getNodeName();
            String value = problemNode.getNodeValue();
            int intvalue = 0;
            try {
                intvalue = Integer.parseInt(value);
            } catch (Exception e) {
                intvalue = -1;
            }

//            System.out.println("Found " + parentNode.getNodeName() + " > " + name + "='" + value + "'" + " i=" + intvalue);

             if (name.equals("index")) {
                problemDetailsImplementation.setProblemId(intvalue);

            } else if (name.equals("attempts")) {
                problemDetailsImplementation.setAttempts(intvalue);

            } else if (name.equals("isSolved")) {
                problemDetailsImplementation.setSolved(value.equals("true"));
            } else if (name.equals("points")) {
                problemDetailsImplementation.setPenaltyPoints(intvalue);
            } else if (name.equals("solutionTime")) {
                problemDetailsImplementation.setSolutionTime(intvalue);
            }
//              problemId is the element id not a number
//                        } else if (name.equals("problemId")) {
//                             problemDetailsImplementation.setProblemId(intvalue);
//              } else if (name.equals("isPending")) {
        }

        return problemDetailsImplementation;
    }
    


    /**
     * Return the contest standings.
     * 
     * @param contest
     * @param log
     * @return an array of the teams and their ranks and standing information.
     */
    public IStanding[] getStandings(IInternalContest contest, Log log) {

        Document document = null;
        String xmlString = null;

        Vector<IStanding> standings = new Vector<IStanding>();

        try {
            DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();

            xmlString = defaultScoringAlgorithm.getStandings(contest, new Properties(), log);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
            // skip past nodes to find teamStanding node

            NodeList list = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {
                Node node = (Node) list.item(i);
                String name = node.getNodeName();
                if (name.equals("teamStanding")) {
                    try {
                        String[] cols = fetchStandingRow(node);

                        // Object[] cols = { "Rank", "Name", "Solved", "Points", teamId, siteId, teamKey };

                        int rank = Integer.parseInt(cols[0]);
                        int numProblemsSolved = Integer.parseInt(cols[2]);
                        int penaltyPoints = Integer.parseInt(cols[3]);
                        int clientNumber = Integer.parseInt(cols[4]);
                        int siteNumber = Integer.parseInt(cols[5]);

                        ClientId clientId = new ClientId(siteNumber, Type.TEAM, clientNumber);
                        
                        IClient client = new ClientImplementation(clientId, contest);

                        IProblemDetails[] clientProblemDetails = getClientProblemDetails(contest, client, node);
                        StandingImplementation standingImplementation = new StandingImplementation(contest, clientId, rank,
                                numProblemsSolved, penaltyPoints);
                        standingImplementation.setProblemDetails(clientProblemDetails);
                        standings.addElement(standingImplementation);

                    } catch (Exception e) {
                        log.log(Log.WARNING, "Exception while parsing/generating standings row ", e);
                    }
                }
            }
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
        }
        return (StandingImplementation[]) standings.toArray(new StandingImplementation[standings.size()]);
    }
} 
