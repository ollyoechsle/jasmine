package jasmine.imaging.core;


import jasmine.imaging.commons.StatisticsSolver;

import javax.swing.*;
import java.text.DecimalFormat;

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
public class JasmineFeatureSimilarity extends JasmineCorrelationDialog {

    protected JTable table;

    protected String[][] dataValues;

    public JasmineFeatureSimilarity(Jasmine j, int overlayType) {
        super(j, overlayType);
    }

    public void init() {
        setTitle("Feature Similarity");
        table = new JTable(dataValues, names);
        add(new JScrollPane(table));
        setSize(400, 400);
        setVisible(true);
    }

    public void processData() {

        dataValues = new String[names.length][names.length];

        DecimalFormat f = new DecimalFormat("0.00");

        for (int y = 0; y < names.length; y++) {
            for (int x = 0; x < names.length; x++) {

                StatisticsSolver sx = observed[x];
                StatisticsSolver sy = observed[y];

                dataValues[x][y] = f.format(sx.getCorrelationWith(sy));

            }

        }

    }
}


