package edu.csus.ecs.pc2.api.implementation;

import java.io.StringReader;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
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
            } else if (name.equals("points")) {
                outArray[3] = value;
            } else if (name.equals("teamId")) {
                outArray[4] = value;
            } else if (name.equals("siteId")) {
                outArray[5] = value;
            } else if (name.equals("teamKey")) {
                outArray[6] = value;
            }
        }

        return outArray;
    }

    /**
     * Parse output of DefaultScoringAlgorithm and return standings in order.
     *  
     * @param contest
     * @param log
     * @return
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

                        StandingImplementation standingImplementation = new StandingImplementation(contest, clientId, rank, numProblemsSolved, penaltyPoints);
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
