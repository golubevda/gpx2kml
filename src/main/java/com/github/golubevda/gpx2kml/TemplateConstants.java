package com.github.golubevda.gpx2kml;

import net.sf.saxon.s9api.QName;

/**
 * @author Dmitry Golubev
 */
public interface TemplateConstants {

    String NS_EXTENSION = "https://github.com/golubevda/gpx2kml";

    QName QNAME_REPLACE_COORDINATES_FUNCTION = new QName(NS_EXTENSION, "replaceCoordinates");
    QName QNAME_COORDINATES_LINK_FUNCTION = new QName(NS_EXTENSION, "coordinatesLink");

    QName PARAM_DOC_NAME = QName.fromClarkName("docName");
    QName PARAM_GEO_LINK_TYPE = QName.fromClarkName("geoLinkType");
}
