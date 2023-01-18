package com.github.golubevda.gpx2kml.link;

/**
 * @author Dmitry Golubev
 */
public enum LinkType {
    GE0,
    OM,
    OMAPS,
    YM,
    GM;

    public GeoLinkGenerator getGenerator() {
        return GeoLinkGeneratorFactory.createGenerator(this);
    }
}
