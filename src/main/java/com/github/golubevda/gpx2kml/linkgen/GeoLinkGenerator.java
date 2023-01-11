package com.github.golubevda.gpx2kml.linkgen;

/**
 * @author Dmitry Golubev
 */
public interface GeoLinkGenerator {
    String generateLink(double lat, double lon, double zoom, String name);
}
