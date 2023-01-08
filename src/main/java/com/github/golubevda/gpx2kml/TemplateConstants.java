package com.github.golubevda.gpx2kml;

import net.sf.saxon.s9api.QName;

/**
 * @author Dmitry Golubev
 */
public interface TemplateConstants {

    String NS_EXTENSION = "http://geocaching.example.org";

    QName QNAME_REPLACE_COORDINATES_FUNCTION = new QName(NS_EXTENSION, "replaceCoordinates");

    QName PARAM_DOC_NAME = new QName(NS_EXTENSION, "docName");
}
