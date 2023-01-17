package com.github.golubevda.gpx2kml.linkgen;

import com.github.golubevda.gpx2kml.util.NumberUtils;

/**
 * @author Dmitry Golubev
 */
public class GoogleMapsLinkGenerator implements GeoLinkGenerator {

    @Override
    public String generateLink(double lat, double lon, double zoom, String name) {
        return String.format("https://maps.google.com/maps?q=%s,%s",
                NumberUtils.format(lat, GPX_COORDINATES_FRACTIONAL_DIGITS, '.'),
                NumberUtils.format(lon, GPX_COORDINATES_FRACTIONAL_DIGITS, '.')
        );
    }
}
