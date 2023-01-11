package com.github.golubevda.gpx2kml.linkgen;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Dmitry Golubev
 */
public abstract class Ge0BasedLinkGenerator implements GeoLinkGenerator {

    private static final int K_MAX_POINT_BYTES = 10;
    private static final int K_MAX_COORD_BITS = K_MAX_POINT_BYTES * 3;
    private static final int LINK_COORDS_PART_LENGTH = 9;

    private final String schemaSuffix;

    public Ge0BasedLinkGenerator(String schemaSuffix) {
        this.schemaSuffix = schemaSuffix;
    }

    @Override
    public String generateLink(double lat, double lon, double zoom, String name) {
        final StringBuilder result = new StringBuilder(schemaSuffix);

        final int zoomI = zoom <= 4 ? 0 : (zoom >= 19.75 ? 63 : (int) ((zoom - 4) * 4));
        result.append(base64Char(zoomI));

        final String coords = latLonToString(lat, lon);
        result.append(coords);

        if (!name.isEmpty()) {
            try {
                result.append('/').append(URLEncoder.encode(transformName(name), "UTF-8"));
            } catch (UnsupportedEncodingException ignored) {
            }
        }

        return result.toString();
    }

    private char base64Char(int x) {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".charAt(x);
    }

    private String latLonToString(double lat, double lon) {
        int latI = latToInt(lat, (1 << K_MAX_COORD_BITS) - 1);
        int lonI = lonToInt(lon, (1 << K_MAX_COORD_BITS) - 1);

        char[] result = new char[LINK_COORDS_PART_LENGTH];
        for (int i = 0, shift = K_MAX_COORD_BITS - 3; i < LINK_COORDS_PART_LENGTH; ++i, shift -= 3) {
            int latBits = latI >> shift & 7;
            int lonBits = lonI >> shift & 7;

            int nextByte =
                    (latBits >> 2 & 1) << 5 |
                            (lonBits >> 2 & 1) << 4 |
                            (latBits >> 1 & 1) << 3 |
                            (lonBits >> 1 & 1) << 2 |
                            (latBits & 1) << 1 |
                            (lonBits & 1);

            result[i] = base64Char(nextByte);
        }
        return new String(result);
    }

    // Map latitude: [-90, 90] -> [0, maxValue]
    private int latToInt(double lat, int maxValue) {
        // M = maxValue, L = maxValue-1
        // lat: -90                        90
        //   x:  0     1     2       L     M
        //       |--+--|--+--|--...--|--+--|
        //       000111111222222...LLLLLMMMM

        double x = (lat + 90.0) / 180.0 * maxValue;
        return x < 0 ? 0 : (x > maxValue ? maxValue : (int) (x + 0.5));
    }

    // Map longitude: [-180, 180) -> [0, maxValue]
    private int lonToInt(double lon, int maxValue) {
        double x = (lonIn180180(lon) + 180.0) / 360.0 * (maxValue + 1.0) + 0.5;
        return x <= 0 || x >= maxValue + 1 ? 0 : (int) x;
    }

    // Make lon in [-180, 180)
    private double lonIn180180(double lon) {
        if (lon >= 0) {
            return lon + 180.0 % 360.0 - 180.0;
        }

        // Handle the case of l = -180
        double l = (lon - 180.0) % 360.0 + 180.0;
        return l < 180.0 ? l : l - 360.0;
    }

    // Replaces ' ' with '_' and vice versa.
    private String transformName(String name) {
        final char[] result = name.toCharArray();
        for (int i = 0; i < result.length; ++i) {
            if (result[i] == ' ') {
                result[i] = '_';
            } else if (result[i] == '_') {
                result[i] = ' ';
            }
        }
        return new String(result);
    }
}
