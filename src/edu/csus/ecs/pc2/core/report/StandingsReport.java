package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Print Standings XML from DefaultScoringAlgorithm.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

public class StandingsReport implements IReport {

    private IContest contest;

    private IController controller;

    private Log log;

    void formatXML(PrintWriter printWriter, String xmlString) throws ParserConfigurationException, SAXException, IOException {
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));

        formatDocument(printWriter, document);
//        String rootNodeName = document.getDocumentElement().getNodeName();
        
    }

    private void writeReport(PrintWriter printWriter) throws ParserConfigurationException, SAXException, IOException, IllegalContestState  {

        printWriter.println();
        DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
        String xmlString = defaultScoringAlgorithm.getStandings(contest, new Properties(), controller.getLog());
        printWriter.println(xmlString);
        printWriter.println();
        formatXML(printWriter, xmlString);
        printWriter.println();
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + new Date());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter filter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {
            printHeader(printWriter);

            try {
                writeReport(printWriter);
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printFooter(printWriter);

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }
    }

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
        try {
            return defaultScoringAlgorithm.getStandings(contest, new Properties(), controller.getLog());
        } catch (IllegalContestState e) {
            e.printStackTrace();
            return "Exception in report: " + e.getMessage();
        }
    }

    public String getReportTitle() {
        return "Standings XML ";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Standings XML Report";
    }

    public void formatDocument(PrintWriter printWriter, Document doc) {
        formatLoop(printWriter, (Node) doc, "");
    }

    private void formatLoop(PrintWriter printWriter, Node node, String indent) {
        String name = node.getNodeName();
        String prefix = indent + " " + name + " ";
        switch (node.getNodeType()) {
            case Node.CDATA_SECTION_NODE:
                printWriter.println(prefix + "CDATA_SECTION_NODE");
                break;
            case Node.COMMENT_NODE:
                printWriter.println(prefix + "COMMENT_NODE");
                break;
            case Node.DOCUMENT_FRAGMENT_NODE:
                printWriter.println(prefix + "DOCUMENT_FRAGMENT_NODE");
                break;
            case Node.DOCUMENT_NODE:
                printWriter.println(prefix + "DOCUMENT_NODE");
                break;
            case Node.DOCUMENT_TYPE_NODE:
                printWriter.println(prefix + "DOCUMENT_TYPE_NODE");
                break;
            case Node.ELEMENT_NODE:
                printWriter.println(prefix + "ELEMENT_NODE");
                break;
            case Node.ENTITY_NODE:
                printWriter.println(prefix + "ENTITY_NODE");
                break;
            case Node.ENTITY_REFERENCE_NODE:
                printWriter.println(prefix + "ENTITY_REFERENCE_NODE");
                break;
            case Node.NOTATION_NODE:
                printWriter.println(prefix + "NOTATION_NODE");
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                printWriter.println(prefix + "PROCESSING_INSTRUCTION_NODE");
                break;
            case Node.TEXT_NODE:
                printWriter.println(prefix + "TEXT_NODE");
                break;
            default:
                printWriter.println(prefix + "Unknown node");
                break;
        }
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++){
            formatLoop(printWriter, list.item(i), indent + "   ");
        }
    }

}
