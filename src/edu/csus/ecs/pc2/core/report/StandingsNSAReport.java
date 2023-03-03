// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
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
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.IScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.NewScoringAlgorithm;

/**
 * Print Standings XML from NewScore.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class StandingsNSAReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -914245980900707408L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    void formatXML(PrintWriter printWriter, String xmlString) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));

        formatDocument(printWriter, document);
//        String rootNodeName = document.getDocumentElement().getNodeName();

    }

    public void writeReport(PrintWriter printWriter) throws ParserConfigurationException, SAXException, IOException, IllegalContestState {

        printWriter.println();
        IScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
        String xmlString = scoringAlgorithm.getStandings(contest, new Properties(), controller.getLog());
        printWriter.println("-- Start XML --");
        printWriter.println(xmlString);
        printWriter.println();
        printWriter.println("-- End XML --");

        printWriter.println();
        formatXML(printWriter, xmlString);
        printWriter.println();
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        filter = inFilter;

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

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) throws IOException {

        if (contest != null) {
            try {
                IScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
                return scoringAlgorithm.getStandings(contest, new Properties(), controller.getLog());
            } catch (IllegalContestState e) {
                return Reports.notImplementedXML(this, "Exception " + e.getMessage());
            }
        } else {
            return Reports.notImplementedXML(this, "contest is null");
        }
    }

    public String getReportTitle() {
        return "Standings XML ";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Standings XML (NSA) Report";
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
        for (int i = 0; i < list.getLength(); i++) {
            formatLoop(printWriter, list.item(i), indent + "   ");
        }
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
