package jasmine.classify;


import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 24-Apr-2009
 * Time: 15:08:14
 * To change this template use File | Settings | File Templates.
 */
public class ICSListenerGraphical extends ICSListener {

    protected JFrame window;
    protected GPProgressBar b;
    protected JLabel status;
    protected JLabel results;
    protected int classesLearned = 0;

    DecimalFormat f = new DecimalFormat("0.00");

    public void onStart() {
        window = new JFrame("Intelligent Classification System");
        b = new GPProgressBar(20,120,200);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        p.add(Box.createVerticalStrut(10));
        p.add(b);
        p.add(Box.createVerticalGlue());
        window.add(b, BorderLayout.CENTER);
        status = new JLabel("");
        results = new JLabel("");
        JPanel bottom = new JPanel(new GridLayout(1,2));
        bottom.add(status);
        bottom.add(results);
        window.add(bottom, BorderLayout.SOUTH);
        window.setSize(360, 100);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public void setNumClasses(int numClasses) {
        super.setNumClasses(numClasses);
    }

    public void onStatusUpdate(String message) {
        status.setText(message);
    }

    public void onLearnNewClass(int classID, double fitness) {
        classesLearned++;
        b.setValue(classesLearned / (double) numClasses);
    }

    public void onClassifierUpdated(float trainingHits, float testingHits) {
        results.setText("Accuracy: " + f.format(trainingHits*100) + "%");
    }

    public void onFinish() {
        window.dispose();
    }
}
