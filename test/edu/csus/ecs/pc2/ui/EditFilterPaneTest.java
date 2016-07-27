package edu.csus.ecs.pc2.ui;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.ui.EditFilterPane.ListNames;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditFilterPaneTest extends TestCase {
    
    public void testNull() throws Exception {
        
    }

    public void showFrame(EditFilterFrame frame, Filter filter, ListNames[] panenames) {

        for (ListNames name : panenames) {
            frame.addList(name);
        }

        frame.setFilter(filter);
        frame.doLayout();
        FrameUtilities.centerFrame(frame);
        frame.setVisible(true);
    }
    
    public static ListNames[] getNames(int value) {

        ListNames[] nameOne = { ListNames.PROBLEMS, //
                ListNames.SITES, //
                ListNames.CLARIFICATION_STATES, //
                ListNames.PERMISSIONS, //
                ListNames.ALL_ACCOUNTS };

        ListNames[] nameTwo = { 
                ListNames.SITES, //
                ListNames.CLIENT_TYPES, //
                ListNames.PERMISSIONS, //
                ListNames.ALL_ACCOUNTS };
        
//      ListNames[] names = { ListNames.SITES, //
//      ListNames.PERMISSIONS 
//};        

        switch (value) {
            case 2:
                return nameTwo;
            default:
                return nameOne;
        }

    }

    public static void main(String[] args) {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(3, 3, 12, 12, true);
        
//        contest = new InternalContest();
//        contest.setClientId(new ClientId(1, Type.EXECUTOR, 1));
        
        IInternalController controller = sample.createController(contest, true, false);

        EditFilterFrame frame = new EditFilterFrame();
       
        frame.setContestAndController(contest, controller);

        ListNames[] names = getNames(2);
        
        new EditFilterPaneTest().showFrame(frame, new Filter(), names);
    }
}
