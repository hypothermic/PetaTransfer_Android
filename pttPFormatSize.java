package nl.hypothermic.android.petatransfer;

/**
 * Created by hypothermic on 1/25/2018.
 * @author hypothermic
 * https://hypothermic.nl
 * https://github.com/hypothermic
 */

import java.text.DecimalFormat;

public class pttPFormatSize {

    public static String formatDecimaal(int size) {
        String hrSize = null;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" bytes");
        }

        return hrSize;
    }
    public static String formatBinair(int size) {
        String hrSize = null;

        double b = size;
        double k = size/1000.0;
        double m = ((size/1000.0)/1000.0);
        double g = (((size/1000.0)/1000.0)/1000.0);
        double t = ((((size/1000.0)/1000.0)/1000.0)/1000.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }
}
