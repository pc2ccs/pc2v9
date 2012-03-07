package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.csus.ecs.pc2.core.log.Log;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Parse validator results (XML) file and return results.
 * <P>
 * 
 * outcome - the judgement text<br>
 * security - the security key passed to the validator<br>
 * comment - an extra comment or more descriptive text by the validator<br>
 * 
 * Sample validator output
 * 
 * <pre>
 *  
 *  &lt;?xml version=&quot;1.0&quot;?&gt;
 *  &lt;result outcome=&quot;Yes&quot; security=&quot;resfile&quot;&gt; comment &lt;/result&gt;
 *  
 *  &lt;?xml version=&quot;1.0&quot;?&gt;
 *  &lt;result outcome=&quot;No - Compilation Error&quot; security=&quot;resfile&quot;&gt; comment &lt;/result&gt;
 *  
 *  &lt;?xml version=&quot;1.0&quot;?&gt;
 *  &lt;result outcome=&quot;No - Run-time Error&quot; security=&quot;resfile&quot;&gt; comment &lt;/result&gt;
 *  
 *  &lt;?xml version=&quot;1.0&quot;?&gt;
 *  &lt;result outcome=&quot;No - Time-limit Exceeded&quot; security=&quot;resfile&quot;&gt; comment &lt;/result&gt;
 *  
 *  &lt;?xml version=&quot;1.0&quot;?&gt;
 *  &lt;result outcome=&quot;No - Wrong Answer&quot; security=&quot;resfile&quot;&gt; comment &lt;/result&gt;
 *  
 *  &lt;?xml version=&quot;1.0&quot;?&gt;
 *  &lt;result outcome=&quot;No - Excessive Output&quot; security=&quot;resfile&quot;&gt; comment &lt;/result&gt;
 *  
 *  &lt;?xml version=&quot;1.0&quot;?&gt;
 *  &lt;result outcome=&quot;No - Output Format Error&quot; security=&quot;resfile&quot;&gt; comment &lt;/result&gt;
 *  
 *  &lt;?xml version=&quot;1.0&quot;?&gt;
 *  &lt;result outcome=&quot;No - Other - Contact Staff&quot; security=&quot;resfile&quot;&gt; comment &lt;/result&gt;
 *  
 * </pre>
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class XMLResultsParser implements IResultsParser {

    

    private Log log = null;

    private Hashtable<String, String> results = new Hashtable<String, String>();

    /**
     * @param nodeMap
     * @param attrs
     */
    private void processAttributes(NamedNodeMap nodeMap) {
        Node node;
        String item;
        for (int i = 0; i < nodeMap.getLength(); i++) {
            node = nodeMap.item(i);
            if (node != null) {
                item = node.getNodeName();
                if (item != null) {
                    results.put(item, node.getNodeValue());
                }
            }
        }
    }

    public boolean parseValidatorResultsFile(String resultsFileName) {

        if (!(new File(resultsFileName).exists())) {
            log.config("WARNING - results file " + resultsFileName + " not found");
        }

        String content = "";
        try {
            DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(resultsFileName));

            // normalize text representation
            doc.getDocumentElement().normalize();
            // root element should be result
            String rootNode = doc.getDocumentElement().getNodeName();

            if (rootNode.equals(RESULT_KEY)) {
                content = doc.getDocumentElement().getTextContent();
                if (content != null) {
                    results.put(CONTENT_KEY , content);
                }
                NamedNodeMap attr = doc.getDocumentElement().getAttributes();
                processAttributes(attr);
                if (results.containsKey(OUTCOME_KEY)) {
                    return true;
                } else {
                    log.config("Parse error - could not find '" + OUTCOME_KEY + "' attribute");
                }
            } else {
                log.config("Parse error expecting " + RESULT_KEY + " rootNode, but found node: " + rootNode);
            }
        } catch (SAXParseException spe) {
            log.log(Log.CONFIG, "XML Parse error - SAX exception", spe);
        } catch (SAXException se) {
            log.log(Log.CONFIG, "XML Parse error - SAX exception", se);
        } catch (Throwable t) {
            log.log(Log.CONFIG, "XML Parse error - exception", t);
        }
        return false;

    }

    public Hashtable<String, String> getResults() {
        return results;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }
    
    public static void main(String[] argv) {

        if (argv.length < 1) {
            System.out.println("Usage: java edu.csus.ecs.pc2.core.execute.XMLResultsParser filename");
            System.exit(1);
        }
        Log log = new Log("edu.csus.ecs.pc2", null, "logs", "parser");
        XMLResultsParser parser = new XMLResultsParser();
        parser.setLog(log);
        if (parser.parseValidatorResultsFile(argv[0])) {
            Enumeration<String> enumeration = parser.getResults().keys();
            while (enumeration.hasMoreElements()) {
                String element = enumeration.nextElement();
                System.out.println("found attribute " + element + " value=" + parser.getResults().get(element));

            }
        }
    }

}
