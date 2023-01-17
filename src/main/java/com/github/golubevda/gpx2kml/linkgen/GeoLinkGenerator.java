package com.github.golubevda.gpx2kml.linkgen;

/**
 * @author Dmitry Golubev
 */
public interface GeoLinkGenerator {

    int GPX_COORDINATES_FRACTIONAL_DIGITS = 8;
    int DEFAULT_ZOOM_LEVEL = 14;

    String generateLink(double lat, double lon, double zoom, String name);

    default String generateLink(double lat, double lon, double zoom) {
        return generateLink(lat, lon, zoom, null);
    }
}
