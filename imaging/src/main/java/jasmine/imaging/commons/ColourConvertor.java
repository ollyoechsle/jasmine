package jasmine.imaging.commons;

import java.awt.*;

/**
 * Converts between different colour spaces
 */
public class ColourConvertor {

    public static int[] RGB2HSL(int rgb) {
        // get a colour object, which saves us having to shift bits and other stuff.
        int value = 0xff000000 | rgb;

        // extract the colours
        int red = (value >> 16) & 0xFF;
        int green = (value >> 8) & 0xFF;
        int blue = value & 0xFF;

        return RGB2HSL(red, green, blue);
    }


    public static int[] RGB2HSL(int r, int g, int b) {

        float var_R = (r / 255f);
        float var_G = (g / 255f);
        float var_B = (b / 255f);

        float var_Min;    //Min. value of RGB
        float var_Max;    //Max. value of RGB
        float del_Max;    //Delta RGB value

        if (var_R > var_G) {
            var_Min = var_G;
            var_Max = var_R;
        } else {
            var_Min = var_R;
            var_Max = var_G;
        }

        if (var_B > var_Max) var_Max = var_B;
        if (var_B < var_Min) var_Min = var_B;

        del_Max = var_Max - var_Min;

        float H = 0, S, L;
        L = (var_Max + var_Min) / 2f;

        if (del_Max == 0) {
            H = 0;
            S = 0;
        } // gray
        else {                                //Chroma
            if (L < 0.5)
                S = del_Max / (var_Max + var_Min);
            else
                S = del_Max / (2 - var_Max - var_Min);

            float del_R = (((var_Max - var_R) / 6f) + (del_Max / 2f)) / del_Max;
            float del_G = (((var_Max - var_G) / 6f) + (del_Max / 2f)) / del_Max;
            float del_B = (((var_Max - var_B) / 6f) + (del_Max / 2f)) / del_Max;

            if (var_R == var_Max)
                H = del_B - del_G;
            else if (var_G == var_Max)
                H = (1 / 3f) + del_R - del_B;
            else H = (2 / 3f) + del_G - del_R;
            if (H < 0) H += 1;
            if (H > 1) H -= 1;
        }

        int hsl[] = new int[3];
        hsl[0] = (int) (255 * H);
        hsl[1] = (int) (S * 255);
        hsl[2] = (int) (L * 255);

        return hsl;

    }

}

