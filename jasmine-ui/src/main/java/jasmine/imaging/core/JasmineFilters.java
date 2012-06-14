package jasmine.imaging.core;

import javax.swing.filechooser.FileFilter;
import java.io.File;

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
 * @author Olly Oechsle, University of Essex, Date: 15-Dec-2006
 * @version 1.0
 */
public class JasmineFilters {

    public static FileFilter getJasmineFilter() {
        return new FileFilter() {
            public boolean accept(File f) {
                String extension = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
                if (f.isDirectory()) return true;
                if (extension.equals("jasmine")) return true;
                return false;
            }

            public String getDescription() {
                return "Jasmine Projects, *.jasmine";
            }
        };
    }

    public static FileFilter getOverlayFilter() {
        return new FileFilter() {
            public boolean accept(File f) {
                String extension = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
                if (extension.equals("overlay")) return true;
                return false;
            }

            public String getDescription() {
                return "Segmentation Overlays, *.overlay";
            }
        };
    }

    public static FileFilter getMaskFilter() {
        return new FileFilter() {
            public boolean accept(File f) {
                String extension = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
                if (extension.equals("mask")) return true;                
                return false;
            }

            public String getDescription() {
                return "Segmentation Overlays, *.overlay";
            }
        };
    }

    public static FileFilter getCSVFilter() {
        return new FileFilter() {
            public boolean accept(File f) {
                String extension = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
                if (f.isDirectory()) return true;
                if (extension.equals("csv")) return true;
                return false;
            }

            public String getDescription() {
                return "Comma Separated Values, *.csv";
            }
        };
    }

    public static FileFilter getClassFileFilter() {
        return new FileFilter() {
            public boolean accept(File f) {
                String extension = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
                if (f.isDirectory()) return true;
                if (extension.equals("class")) return true;
                return false;
            }

            public String getDescription() {
                return "Java Class Files, *.class";
            }
        };
    }

    public static FileFilter getClassAndFileFileFilter() {
        return new FileFilter() {
            public boolean accept(File f) {
                String extension = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
                if (f.isDirectory()) return true;
                if (extension.equals("class")) return true;
                if (extension.equals("solution")) return true;
                return false;
            }

            public String getDescription() {
                return "SXGP Solutions, *.solution; Java Class Files, *.class";
            }
        };
    }

    public static FileFilter getImageFilter() {
        return new FileFilter() {
            public boolean accept(File f) {
                String extension = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
                if (f.isDirectory()) return true;
                if (extension.equals("bmp")) return true;
                if (extension.equals("jpg")) return true;
                if (extension.equals("jpeg")) return true;
                if (extension.equals("png")) return true;
                if (extension.equals("gif")) return true;
                return false;
            }

            public String getDescription() {
                return "Image Files: jpg, png, gif, bmp";
            }
        };
    }


}
