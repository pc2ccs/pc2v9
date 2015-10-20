package edu.csus.ecs.pc2.ui;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * 
 * @author ICPC
 *
 */
public class TestCaseableModelTest extends AbstractTestCase{

    public static void main(String[] args) {

        Problem problem = new Problem("A1");

        ProblemDataFiles files = new ProblemDataFiles(problem);

        addSampleFiles(files, 12);

        TestCaseTableModel model = new TestCaseTableModel();

        JTable table = new JTable(model);
        
        model.setFiles(files);

        // Font font = table.getFont();
        table.setFont(new Font("Serif", Font.BOLD, 18));
        FrameUtilities.updateRowHeights(table);

        // TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
        // table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        JFrame frame = new JFrame("Test Case Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(scrollPane);
        frame.setSize(300, 200);
        FrameUtilities.centerFrame(frame);
        frame.setVisible(true);
    }



    private static ProblemDataFiles addSampleFiles(ProblemDataFiles files, int count) {

        SerializedFile[] datafiles = new SerializedFile[count];
        SerializedFile[] ansfiles = new SerializedFile[count];

        for (int i = 0; i < ansfiles.length; i++) {
            int num = i + 1;
            SerializedFile serializedFile = new SerializedFile("file" + num + ".in", true);
            datafiles[i] = serializedFile;
            serializedFile = new SerializedFile("file" + num + ".in", true);
            ansfiles[i] = serializedFile;
        }

        files.setJudgesDataFiles(datafiles);
        files.setJudgesAnswerFiles(ansfiles);

        return files;
    }
}
