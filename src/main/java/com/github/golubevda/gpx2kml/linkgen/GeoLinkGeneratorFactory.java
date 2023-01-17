package com.github.golubevda.gpx2kml.linkgen;

import com.github.golubevda.gpx2kml.LinkType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Golubev
 */
public class GeoLinkGeneratorFactory {

    private static final Map<LinkType, GeoLinkGenerator> GENERATORS = new HashMap<>();
    static {
        GENERATORS.put(LinkType.OM, new OmLinkGenerator());
        GENERATORS.put(LinkType.GE0, new Ge0LinkGenerator());
        GENERATORS.put(LinkType.OMAPS, new OmapsLinkGenerator());
        GENERATORS.put(LinkType.YM, new YandexMapsLinkGenerator());
        GENERATORS.put(LinkType.GM, new GoogleMapsLinkGenerator());
    }

    public static GeoLinkGenerator createGenerator(LinkType linkType) {
        final GeoLinkGenerator instance = GENERATORS.get(linkType);
        if (instance == null) {
            throw new IllegalArgumentException("No generator is registered to generate links of type: " + linkType);
        }
        return instance;
    }
}
