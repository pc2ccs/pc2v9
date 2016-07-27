package edu.csus.ecs.pc2.ui;

import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.core.log.Log;
import junit.framework.TestCase;

/**
 * MultipleFileViewer Testing.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class MultipleFileViewerTest extends TestCase {
    
    public void testNull() throws Exception {
        
    }

    public static void main(String[] args) {

        Log log = new Log("MultipleFileViewerTest.log");

        MultipleFileViewer viewer = new MultipleFileViewer(log);

        String s = JOptionPane.showInputDialog(null, "Enter Test number");
        int testNumber = Integer.parseInt(s);

        String filename = "pc2v9.ini";

        switch (testNumber) {
            case 1:
                viewer.setTitle("show single file");
                viewer.addFilePane(filename, filename);

                viewer.setVisible(true);
                break;

            case 2:
                viewer.setTitle("Compare identical files");
                viewer.setCompareFileNames(filename, filename);
                viewer.enableCompareButton(true);
                viewer.addFilePane(filename, filename);

                viewer.setVisible(true);
                break;

            case 3:
                viewer.setTitle("Error text test");
                viewer.addFilePane(filename, filename);
                long returnValue = 0x124;
                viewer.setInformationLabelText("<html><font size='+1' color='red'>Team program exit code = 0x" + Long.toHexString(returnValue).toUpperCase() + "</font>");

                viewer.setVisible(true);
                break;

            case 4:
                viewer.setTitle("Empty form test");

                viewer.setVisible(true);
                break;
                

            case 5:
                viewer.setTitle("Empty form test");
                filename = "stuf1"; viewer.addFilePane(filename, filename);
                filename = "stuf2"; viewer.addFilePane(filename, filename);
                filename = "stuf3"; viewer.addFilePane(filename, filename);
                
                viewer.setVisible(true);
                break;

            default:
                viewer.showMessage("No test number selected in MultipleFileViewerTest");
        }
    }
}
