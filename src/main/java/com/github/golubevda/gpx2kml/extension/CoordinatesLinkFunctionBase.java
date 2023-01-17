package com.github.golubevda.gpx2kml.extension;

import com.github.golubevda.gpx2kml.LinkType;
import com.github.golubevda.gpx2kml.linkgen.GeoLinkGenerator;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;

/**
 * @author Dmitry Golubev
 */
public abstract class CoordinatesLinkFunctionBase extends ExtensionFunctionDefinition {

    protected static final LinkType DEFAULT_LINK_TYPE = LinkType.GE0;

    protected GeoLinkGenerator createLinkGenerator(Sequence[] arguments, int linkTypeArgNum) throws XPathException {
        LinkType linkType = DEFAULT_LINK_TYPE;
        if (linkTypeArgNum < arguments.length) {
            final String linkTypeString = arguments[linkTypeArgNum].head().getStringValue();
            try {
                linkType = LinkType.valueOf(linkTypeString.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown link type: " + linkTypeString);
            }
        }
        return linkType.getGenerator();
    }
}
