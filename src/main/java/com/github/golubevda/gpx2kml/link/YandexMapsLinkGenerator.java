package com.github.golubevda.gpx2kml.link;

import com.github.golubevda.gpx2kml.util.NumberUtils;
import com.github.golubevda.gpx2kml.util.UrlBuilder;

/**
 * @author Dmitry Golubev
 */
public class YandexMapsLinkGenerator implements GeoLinkGenerator {

    @Override
    public String generateLink(double lat, double lon, double zoom, String name) {
        final String latStr = NumberUtils.format(lat, GPX_COORDINATES_FRACTIONAL_DIGITS, '.');
        final String lonStr = NumberUtils.format(lon, GPX_COORDINATES_FRACTIONAL_DIGITS, '.');

        return new UrlBuilder().setPath("https://yandex.ru/maps")
                .addParam("ll", lonStr + "," + latStr)
                .addParam("whatshere[point]", lonStr + "," + latStr)
                .addParam("z", DEFAULT_ZOOM_LEVEL + "")
                .build();
    }
}
