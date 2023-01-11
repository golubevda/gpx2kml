package com.github.golubevda.gpx2kml.linkgen;

/**
 * Переписанный на коленке алгоритм https://github.com/organicmaps/organicmaps/blob/master/ge0/url_generator.cpp
 */
public class OmLinkGenerator extends Ge0BasedLinkGenerator {

    public OmLinkGenerator() {
        super("om://");
    }
}
