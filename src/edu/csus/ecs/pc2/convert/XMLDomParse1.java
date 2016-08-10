package edu.csus.ecs.pc2.convert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML Parse utiles.
 * 
 * @author Douglas A. Lane
 * 
 */
public class XMLDomParse1 {

    /**
     * Get a list of notes from document, filter by path.
     */
    NodeList getNodes(Document document, String xpath) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(xpath, document.getDocumentElement(), XPathConstants.NODESET);
        return nodes;
    }

    /**
     * Returns a name, value map for the input XML document at the XPath path.
     * 
     * @throws XPathExpressionException
     */
    protected Map<String, String> getMap(Document document, String path) throws XPathExpressionException {
        NodeList nodes = getNodes(document, path);
        Map<String, String> map = new HashMap<>();
        if (nodes.getLength() > 0) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = (Node) nodes.item(i);
                String name = node.getNodeName();
                String value = node.getTextContent();
                map.put(name, value);
            }
        }

        return map;
    }

    protected Document create(String filename) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new File(filename));

        // normalize text representation
        document.getDocumentElement().normalize();

        return document;

    }

    /**
     * From input elements (elementTag) load Properties.
     * 
     * elementTag is control break.
     * 
     * @param nodes
     *            nodes to search
     * @param elementTag
     *            tagname to start/end elements for each Properties
     * @return
     */
    public Properties[] create(NodeList nodes, String elementTag) {
        List<Properties> list = new ArrayList<>();

        String runId = null;

        Properties properties = new Properties();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = (Node) nodes.item(i);
            String name = node.getNodeName();
            String value = node.getTextContent();

            if (name.equals(elementTag)) {
                runId = properties.getProperty(elementTag);
                if (runId != null) {
                    list.add(properties);

                }
                properties = new Properties();
            }
            properties.put(name, value);
        }

        runId = properties.getProperty(elementTag);
        if (runId != null) {
            list.add(properties);
        }

        return (Properties[]) list.toArray(new Properties[list.size()]);
    }

}
