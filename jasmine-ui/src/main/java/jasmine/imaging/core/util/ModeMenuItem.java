package jasmine.imaging.core.util;


import jasmine.imaging.commons.Segmenter;
import jasmine.imaging.core.Jasmine;
import jasmine.imaging.core.visionsystem.VisionSystem;
import jasmine.imaging.shapes.SubObjectClassifier;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A mode menu item allows Jasmine to change its segmenter and classifier.
 *
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
 * @author Olly Oechsle, University of Essex, Date: 30-May-2007
 * @version 1.0
 */
public final class ModeMenuItem extends JCheckBoxMenuItem {

    protected Segmenter segmenter;
    protected SubObjectClassifier classifier;

    public ModeMenuItem(final Jasmine j, String text, final Segmenter s, final SubObjectClassifier c) {
        this(j, text, s, c, false);
    }

    public ModeMenuItem(final Jasmine j, String text, final Segmenter s, final SubObjectClassifier c, boolean selected) {
        super(text);

        this.segmenter = s;
        this.classifier = c;

        if (selected) {
            setSelected(true);
            j.currentlySelected = this;
        }
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                j.setStatusText("Changed mode to: " + getText());

                // change which mode menu item is selected
                if (j.currentlySelected != null) {
                    j.currentlySelected.setSelected(false);
                }

                j.project.addProperty(VisionSystem.SEGMENTER_HANDLE, s.getClass().getCanonicalName());
                j.project.addProperty(VisionSystem.OBJECT_CLASSIFIER_HANDLE, c.getClass().getCanonicalName());
                j.visionSystemPanel.onProjectChanged(j.project);

                j.currentlySelected = ModeMenuItem.this;
                setSelected(true);
            }
        });
    }


    public Segmenter getSegmenter() {
        return segmenter;
    }

    public void setSegmenter(Segmenter segmenter) {
        this.segmenter = segmenter;
    }

    public SubObjectClassifier getClassifier() {
        return classifier;
    }

    public void setClassifier(SubObjectClassifier classifier) {
        this.classifier = classifier;
    }
}