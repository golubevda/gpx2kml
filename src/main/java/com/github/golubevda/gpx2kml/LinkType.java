package com.github.golubevda.gpx2kml;

import com.github.golubevda.gpx2kml.linkgen.GeoLinkGenerator;
import com.github.golubevda.gpx2kml.linkgen.GeoLinkGeneratorFactory;

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
