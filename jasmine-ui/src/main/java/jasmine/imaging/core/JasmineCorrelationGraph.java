package jasmine.imaging.core;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import jasmine.imaging.commons.StatisticsSolver;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


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
 * @author Olly Oechsle, University of Essex, Date: 06-Aug-2007
 * @version 1.0
 */
public class JasmineCorrelationGraph extends JasmineCorrelationDialog {

    JLabel chart;
    JFreeChart myChart;

    public JasmineCorrelationGraph(Jasmine j, int overlayType) {
        super(j, overlayType);
    }

    public void init() {

        setTitle("Feature Correlation with Training Data");

        chart = new JLabel();
        add(chart);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                drawChart();
            }
        });

        setSize(750, 480);
        setVisible(true);

        drawChart();

    }

    public void processData() {
        DefaultCategoryDataset series = new DefaultCategoryDataset();

        for (int i = 0; i < observed.length; i++) {
            StatisticsSolver obs = observed[i];
            double correlation = obs.getCorrelationWith(expected);
            if (!Double.isNaN(correlation)) {
                series.addValue(correlation, "series1", names[i]);
            }
            System.out.println(names[i] + ": " + correlation);
        }

        myChart = ChartFactory.createBarChart(null, "Features", "Pearson Correlation", series, PlotOrientation.VERTICAL, false, false, false);

    }

    protected void drawChart() {
        if (myChart != null) {
            BufferedImage image = myChart.createBufferedImage(getWidth(), getHeight() - 65);
            chart.setIcon(new ImageIcon(image));
        }
    }

}
