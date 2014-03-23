package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FileDiffPanelTest extends AbstractTestCase {
    
    
    private void showFrame() {
        
        String sumitDat = getSamplesSourceFilename("sumit.dat");
        String sumitAns = getSamplesSourceFilename("sumit.ans");
        
        Log log = new Log("FileDiffPanelTest");
        FileDiffPanel fileDiffPanel = new FileDiffPanel(log);
        fileDiffPanel.showFiles(sumitDat, "Judge's Data File", sumitAns, "Judge's Answer");


//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        addBorderAndCenterComponent (frame, panel);
        
    }
    
//    private void addBorderAndCenterComponent(JFrame frame, JComponent panel) {
//        frame.setLayout(new BorderLayout());
//        frame.add(panel, BorderLayout.CENTER);
//    }

    public static void main(String[] args) {
        FileDiffPanelTest test = new FileDiffPanelTest();
        test.showFrame();
    }

}
