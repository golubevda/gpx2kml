package com.github.golubevda.gpx2kml;

import net.sf.saxon.s9api.QName;

/**
 * @author Dmitry Golubev
 */
public interface TemplateConstants {

    String NS_EXTENSION = "http://gpx2kml.golubevda.github.com";

    QName QNAME_REPLACE_COORDINATES_FUNCTION = new QName(NS_EXTENSION, "replaceCoordinates");

    QName PARAM_DOC_NAME = QName.fromClarkName("docName");
}
