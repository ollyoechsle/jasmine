package jasmine.imaging.shapes;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.Vector;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

/**
 * <p/>
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version,
* provided that any use properly credits the author.
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details at http://www.gnu.org
* </p>
*
* @author Olly Oechsle, University of Essex, Date: 13-Feb-2007
* @version 1.0
*/
public class RadiusChart extends JFrame {

    JLabel chart;
    JFreeChart myChart;

    public RadiusChart(Vector<Double> values) {

        super();

        DefaultCategoryDataset series = new DefaultCategoryDataset();

        for (int i = 0; i < values.size(); i++) {
            series.addValue(values.elementAt(i), "row1", String.valueOf(i));
        }

        setTitle("Radius Change");

        chart = new JLabel();
        getContentPane().add(chart);

        setSize(320, 240);
        setVisible(true);

        myChart = ChartFactory.createLineChart(null, null, null, series, PlotOrientation.VERTICAL, false, false, false);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (myChart != null) updateChart();
            }
        });

    }

    public RadiusChart(double[] values) {

        super();

        DefaultCategoryDataset series = new DefaultCategoryDataset();

        for (int i = 0; i < values.length; i++) {
            series.addValue(values[i], "row1", String.valueOf(i));
        }

        setTitle("Radius Change");

        chart = new JLabel();
        getContentPane().add(chart);

        setSize(320, 240);
        setVisible(true);

        myChart = ChartFactory.createLineChart(null, null, null, series, PlotOrientation.VERTICAL, false, false, false);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (myChart != null) updateChart();
            }
        });

    }

    public void updateChart() {
        if (chart != null) {
            BufferedImage image = myChart.createBufferedImage(chart.getWidth(),chart.getHeight());
            chart.setIcon(new ImageIcon(image));
        }
    }

}
