package org.example.geocaching;

import net.sf.saxon.s9api.*;
import org.example.geocaching.extension.TextCoordinatesReplacer;

import javax.xml.transform.stream.StreamSource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Dmitry Golubev
 */
public class Application {

    public static void main(String[] args) throws Exception {
        final Processor processor = new Processor(false);
        processor.registerExtensionFunction(new TextCoordinatesReplacer());
        final XsltCompiler compiler = processor.newXsltCompiler();
        XsltExecutable xslt;
        try (InputStream xsltStream = new BufferedInputStream(Application.class.getResourceAsStream("/gpx2kml.xslt"))) {
            xslt = compiler.compile(new StreamSource(xsltStream));
        }
        final Xslt30Transformer transformer = xslt.load30();

        try (InputStream gpxStream = new BufferedInputStream(new FileInputStream("D:\\devel\\geocaching\\points.gpx"))) {
            Destination destination = null;
            try {
                destination = processor.newSerializer(new File("D:\\devel\\geocaching\\points_final.kml"));
//                transformer.setStylesheetParameters(Map.of(QName.fromClarkName("docName"), XdmValue.makeValue("Caches!")));
                transformer.transform(new StreamSource(gpxStream), destination);
            } finally {
                if (destination != null) {
                    destination.close();
                }
            }
        }
    }
}
