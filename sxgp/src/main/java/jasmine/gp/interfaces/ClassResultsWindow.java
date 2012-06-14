package jasmine.gp.interfaces;


import jasmine.gp.multiclass.ClassResult;
import jasmine.gp.multiclass.ClassResults;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Displays the results of a classification experiment.
 */
class ClassResultsWindow extends JDialog {

    public static final int NAME = 0;
    public static final int TOTAL = 1;
    public static final int CORRECT = 2;
    public static final int PERCENT = 3;

    private JLabel[][] classResults;
    private GraphicalListener graphicalListener;

    public ClassResultsWindow(GraphicalListener graphicalListener, ClassResults results) {

        super(graphicalListener.window);
        this.graphicalListener = graphicalListener;

        setTitle("Classification Status");

        Container c = getContentPane();

        classResults = new JLabel[results.classes.size() + 1][4];

        c.setLayout(new GridLayout(results.classes.size() + 2, 5));

        c.add(new JLabel(" Class"));
        c.add(new JLabel("Total", JLabel.RIGHT));
        c.add(new JLabel("Correct", JLabel.RIGHT));
        c.add(new JLabel("Percent %", JLabel.RIGHT));
        // padding
        c.add(new JLabel(" "));        

        for (int row = 0; row < results.classes.size(); row++) {
            ClassResult r = results.classes.elementAt(row);

            classResults[row][NAME] = new JLabel(" " + r.name);
            c.add(classResults[row][NAME]);

            classResults[row][TOTAL] = new JLabel("?", JLabel.RIGHT);
            c.add(classResults[row][TOTAL]);

            classResults[row][CORRECT] = new JLabel("?", JLabel.RIGHT);
            c.add(classResults[row][CORRECT]);

            classResults[row][PERCENT] = new JLabel("?", JLabel.RIGHT);
            c.add(classResults[row][PERCENT]);

            // padding
            c.add(new JLabel(" "));

        }

        int finalRow = results.classes.size();
        classResults[finalRow][NAME] = new JLabel(" Total");
        c.add(classResults[finalRow][NAME]);        
        classResults[finalRow][TOTAL] = new JLabel("?", JLabel.RIGHT);
        c.add(classResults[finalRow][TOTAL]);
        classResults[finalRow][CORRECT] = new JLabel("?", JLabel.RIGHT);
        c.add(classResults[finalRow][CORRECT]);
        classResults[finalRow][PERCENT] = new JLabel("?", JLabel.RIGHT);
        c.add(classResults[finalRow][PERCENT]);

        setSize(300, (20 * results.classes.size()) + 40);
        setVisible(true);

        update(results);

    }

    public void update(ClassResults results) {

        DecimalFormat f = new DecimalFormat("0.0");

        int totalTP = 0;
        int totalHits = 0;

        for (int row = 0; row < results.classes.size(); row++) {
            ClassResult c = results.classes.elementAt(row);

            totalTP += c.total;
            totalHits += c.correct;

            Color col = Color.BLACK;
            if (c.correct == 0) col = Color.RED;

            classResults[row][TOTAL].setText(String.valueOf(c.total));

            classResults[row][CORRECT].setText(String.valueOf(c.correct));

            classResults[row][PERCENT].setText(f.format((c.correct / (double) c.total) * 100));

            for (int i = NAME; i <= PERCENT; i++) {
                classResults[row][i].setForeground(col);
            }

        }

        int row = results.classes.size();
        classResults[row][TOTAL].setText(String.valueOf(totalTP));
        classResults[row][CORRECT].setText(String.valueOf(totalHits));
        classResults[row][PERCENT].setText(f.format((totalHits / (double) totalTP) * 100));
        

    }

}
