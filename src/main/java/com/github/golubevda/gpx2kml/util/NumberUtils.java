package com.github.golubevda.gpx2kml.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * @author Dmitry Golubev
 */
public class NumberUtils {

    public static String format(double value, int fractionalDigits, char decimalSeparator) {
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        final String pattern = "#." + new String(new char[fractionalDigits]).replace('\0', '#');
        return new DecimalFormat(pattern, dfs).format(value);
    }
}
