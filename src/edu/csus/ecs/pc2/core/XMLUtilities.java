package edu.csus.ecs.pc2.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;
import edu.csus.ecs.pc2.core.util.XSLTransformer;

/**
 * Utilities for XML and XSLT.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class XMLUtilities {

    private XMLUtilities() {
        super();
    }

    /**
     * Add a boolean value as a child element.
     * @param mementoRoot
     * @param name element name
     * @param value element value
     * @return
     */
    public static IMemento addChild(IMemento mementoRoot, String name, boolean value) {
        return addChild(mementoRoot, name, Boolean.toString(value));
    }

    /**
     * Add a long (integer) value as a child element.
     * 
     * @param mementoRoot
     * @param name element name
     * @param value element value
     * @return
     */
    public static IMemento addChild(IMemento mementoRoot, String name, long value) {
        return addChild(mementoRoot, name, Long.toString(value));
    }
    
    /**
     * Add a string value as a child element.
     * @param mementoRoot
     * @param name element name
     * @param value element value
     * @return
     */
    public static IMemento addChild(IMemento mementoRoot, String name, String value) {
        XMLMemento memento = (XMLMemento) mementoRoot.createChildNode(name, value);
        return memento;
    }

    /**
     * Format milliseconds in decimal format.
     * 
     * @param timeInMillis
     * @return 
     */
    public static String formatSeconds(long timeInMillis) {
        long seconds = timeInMillis / 1000;
        long fraction = timeInMillis % 1000;
        return seconds + "." + fraction;
    }

    /**
     * Return the current time in seconds, with millis mattisa.
     * 
     * @see #formatSeconds(long)
     * @return 
     */
    public static String getTimeStamp() {
        return formatSeconds(System.currentTimeMillis());
    }
    
    /**
     * Name of style sheet directory
     */
    private static String styleSheetDirectoryName = null;

    public static String getStyleSheetDirectoryName() {
        if (styleSheetDirectoryName == null) {
            styleSheetDirectoryName = getDefaultSyleSheetDirectoryName();
        }
        return styleSheetDirectoryName;
    }

    private static String getDefaultSyleSheetDirectoryName() {

        String xslDir = "data" + File.separator + "xsl";
        File xslDirFile = new File(xslDir);
        if (!(xslDirFile.canRead() && xslDirFile.isDirectory())) {
            VersionInfo versionInfo = new VersionInfo();
            xslDir = versionInfo.locateHome() + File.separator + xslDir;
        }
        return xslDir;
    }

    public static String getXSLTFullPath(String baseFileName) {
        return getStyleSheetDirectoryName() + File.separator + baseFileName;
    }

    /**
     * Transform XML via XSLT and write to output file.
     * 
     * @param xmlString
     *            input XML (from DSA)
     * @param xsltFileName
     *            XSLT file name
     * @param outputFileName
     *            output file name
     * @throws Exception
     */
    public static void writeFile(String xmlString, String xsltFileName, String outputFileName) throws Exception {

        String xslDir = getStyleSheetDirectoryName();

        File inputDir = new File(xslDir);
        if (!inputDir.isDirectory()) {
            throw new Exception("Can not find xslt dir: " + xslDir);
        }

        XSLTransformer xslTransformer = new XSLTransformer();

        String fullPathFileName = xslDir + File.separator + xsltFileName;

        File xslFile = new File(fullPathFileName);

        FileOutputStream outFile = new FileOutputStream(outputFileName);

        Source inputXML = new StreamSource(new StringReader(xmlString));
        Source inputStyleSheet = new StreamSource(xslFile);

        xslTransformer.transform(inputStyleSheet, inputXML, outFile);
    }

    public static String[] transformToArray(String xmlString, String xsltFileName) throws Exception {

        if (!new File(xsltFileName).isFile()) {
            // file not there use a qualified path.

            xsltFileName = getXSLTFullPath(xsltFileName);
        }

        File xslFile = new File(xsltFileName);

        // Source inputXML = new StreamSource(new StringReader(xmlString));
        // Source inputStyleSheet = new StreamSource(xslFile);
        XSLTransformer xslTransformer = new XSLTransformer();

        String outString = xslTransformer.transformToString(xslFile, xmlString);

        String newline = System.getProperty("line.separator");

        return outString.split(newline);
    }

}
