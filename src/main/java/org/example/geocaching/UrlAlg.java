package org.example.geocaching;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Переписанный на коленке алгоритм https://github.com/organicmaps/organicmaps/blob/master/ge0/url_generator.cpp
 * TODO: Отрефакторить поприличнее
 */
public class UrlAlg {

    public static final int kMaxPointBytes = 10;
    public static final int kMaxCoordBits = kMaxPointBytes * 3;

    public String generateShortShowMapUrl(double lat, double lon, double zoom, String name) {
        int schemaLength = 5;  // strlen("om://")
        char[] urlSample = "om://ZCoordba64".toCharArray();

        int zoomI = (zoom <= 4 ? 0 : (zoom >= 19.75 ? 63 : (int) ((zoom - 4) * 4)));
        urlSample[schemaLength] = Base64Char(zoomI);

        LatLonToString(lat, lon, urlSample, schemaLength + 1, 9);

        String url = new String(urlSample);

        if (!name.isEmpty()) {
            url += '/';
            url += UrlEncodeString(TransformName(name));
        }

        return url;
    }

    private char Base64Char(int x) {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".charAt(x);
    }

    void LatLonToString(double lat, double lon, char[] s, int offset, int nBytes) {
        if (nBytes > kMaxPointBytes)
            nBytes = kMaxPointBytes;

        int latI = LatToInt(lat, (1 << kMaxCoordBits) - 1);
        int lonI = LonToInt(lon, (1 << kMaxCoordBits) - 1);

        int i;
        int shift;
        for (i = 0, shift = kMaxCoordBits - 3; i < nBytes; ++i, shift -= 3) {
            int latBits = latI >> shift & 7;
            int lonBits = lonI >> shift & 7;

            int nextByte =
                    (latBits >> 2 & 1) << 5 |
                            (lonBits >> 2 & 1) << 4 |
                            (latBits >> 1 & 1) << 3 |
                            (lonBits >> 1 & 1) << 2 |
                            (latBits & 1) << 1 |
                            (lonBits & 1);

            s[i + offset] = Base64Char(nextByte);
        }
    }

    // Map latitude: [-90, 90] -> [0, maxValue]
    private int LatToInt(double lat, int maxValue) {
        // M = maxValue, L = maxValue-1
        // lat: -90                        90
        //   x:  0     1     2       L     M
        //       |--+--|--+--|--...--|--+--|
        //       000111111222222...LLLLLMMMM

        double x = (lat + 90.0) / 180.0 * maxValue;
        return x < 0 ? 0 : (x > maxValue ? maxValue : (int) (x + 0.5));
    }

    // Map longitude: [-180, 180) -> [0, maxValue]
    private int LonToInt(double lon, int maxValue) {
        double x = (LonIn180180(lon) + 180.0) / 360.0 * (maxValue + 1.0) + 0.5;
        return (x <= 0 || x >= maxValue + 1) ? 0 : (int) (x);
    }

    // Make lon in [-180, 180)
    private double LonIn180180(double lon) {
        if (lon >= 0)
            return lon + 180.0 % 360.0 - 180.0;

        // Handle the case of l = -180
        double l = (lon - 180.0) % 360.0 + 180.0;
        return l < 180.0 ? l : l - 360.0;
    }

    private String UrlEncodeString(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    // Replaces ' ' with '_' and vice versa.
    String TransformName(String s) {
        char[] result = s.toCharArray();
        for (int i = 0; i < result.length; ++i) {
            if (result[i] == ' ')
                result[i] = '_';
            else if (result[i] == '_')
                result[i] = ' ';
        }
        return new String(result);
    }
}
