package jasmine.classify;


import jasmine.classify.classifier.Classifier;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Listener for the ISland fusion clasifier method
 */
public class EnsembleListener extends JDialog {

    JLabel fitness;
    JLabel status;

//    public static void main(String[] args) {
//        new EnsembleListener(null);
//    }

    public EnsembleListener(JFrame parent) {

        Container c = getContentPane();
        c.setLayout(new BoxLayout(c, BoxLayout.LINE_AXIS));

        fitness = new JLabel();
        fitness.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        status = new JLabel();

        c.add(Box.createHorizontalStrut(10));
        c.add(status);
        c.add(fitness);
        c.add(Box.createHorizontalStrut(10));

        setSize(400, 150);
        setLocationRelativeTo(parent);
        setTitle("Ensemble Creation");
        setVisible(true);

        onBetterFitness(0.8, null);

    }

    public double lowestFitness = Double.MAX_VALUE;

    public void onBetterFitness(final double error, final Classifier c) {
        if (error < lowestFitness) {
            lowestFitness = error;
        } else {
            return;
        }
        System.out.println("Error: " + error);
        SwingUtilities.invokeLater(new Thread() {
            public void run() {
                String text = "<html><b> Creating ensemble classifier...</b>";
                if (c != null) {
                    text += "<br> " + c.toString() + "</html>";
                }
                double percent = (1 - error) * 100;
                DecimalFormat f = new DecimalFormat("0.00");
                fitness.setText(f.format(percent) + "%");
                status.setText(text);
                requestFocus();
            }
        });
    }
}
