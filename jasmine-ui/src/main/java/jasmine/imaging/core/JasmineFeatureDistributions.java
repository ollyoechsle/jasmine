package jasmine.imaging.core;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;

import jasmine.imaging.commons.Pixel;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.StatisticsSolver;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Vector;
import java.io.IOException;


/**
 * Plots the distribution of the output of a particular function on the training data.
 * One line should be plotted for the distribution corresponding to each class.
 *
 * @author Olly Oechsle, University of Essex, Date: 06-Aug-2007
 * @version 1.0
 */
public class JasmineFeatureDistributions extends JDialog {

    JLabel chart;
    JFreeChart myChart;
    JComboBox list;

    protected Jasmine j;

    protected int currentIndex = 0;

    public JasmineFeatureDistributions(Jasmine j) {
        super(j);
        this.j = j;
        init();
    }

    public void init() {

        list = new JComboBox(JasmineCorrelationDialog.names);
        chart = new JLabel();
        add(chart, BorderLayout.CENTER);
        add(list, BorderLayout.NORTH);

        list.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentIndex = list.getSelectedIndex();
                processData();
                drawChart();
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                drawChart();
            }
        });

        setSize(400, 400);
        setVisible(true);

        processData();
        drawChart();

    }

    public void processData() {

        try {

            XYSeriesCollection data = new XYSeriesCollection();

            StatisticsSolver s = new StatisticsSolver();

            Vector<StatisticsSolver> classSolvers = new Vector<StatisticsSolver>();

            // Calculate a statistics solver for each pixel class
            Vector<JasmineClass> pixelClasses = j.project.getClasses(j.segmentationPanel.mode);
            for (int classIndex = 0; classIndex < pixelClasses.size(); classIndex++) {
                JasmineClass c = pixelClasses.elementAt(classIndex);

                // Create a solver for this class
                StatisticsSolver classSolver = new StatisticsSolver();
                classSolvers.add(classSolver);

                // Calculate correlations for this feature
                for (int i = 0; i < j.project.getImages().size(); i++) {
                    JasmineImage image = j.project.getImages().elementAt(i);
                    PixelLoader img = new PixelLoader(image.getBufferedImage());
                    Vector<Pixel> pixels = image.getOverlayPixels(JasmineClass.MATERIAL);

                    if (pixels != null) {
                        for (int k = 0; k < pixels.size(); k++) {
                            if (k % 2 == 0) continue;
                            Pixel pixel = pixels.elementAt(k);

                            if (pixel.value == c.classID)  {
                                double value = JasmineCorrelationDialog.getValue(img, pixel, currentIndex);
                                classSolver.addData(value);
                                s.addData(value);
                            }

                        }
                    }

                }

            }

            setTitle("Distribution for feature: " + JasmineCorrelationDialog.names[currentIndex]);

            // get the min and max
            float min = s.getMin();
            float max = s.getMax();
            float range = max - min;
            float step = range / 40;

            for (int i = 0; i < classSolvers.size(); i++) {

                StatisticsSolver classSolver =  classSolvers.elementAt(i);
                XYSeries series = new XYSeries(pixelClasses.elementAt(i).name);
                data.addSeries(series);
                
                for (float x = min; x <= max; x += step) {
                    int total = classSolver.countDataInRange(x, x + step);
                    series.add(x + (step / 2), total);
                }

            }

            myChart = ChartFactory.createXYLineChart(
                    JasmineCorrelationDialog.names[currentIndex] + " distribution",
                    "Value", "Frequency",
                    data,
                    PlotOrientation.VERTICAL,
                    true, // legend
                    false, // tooltips
                    false // urls
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void drawChart() {
        if (myChart != null) {
            BufferedImage image = myChart.createBufferedImage(getWidth(), getHeight() - 65);
            chart.setIcon(new ImageIcon(image));
        }
    }

}
