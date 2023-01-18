package com.github.golubevda.gpx2kml;

import com.github.golubevda.gpx2kml.extension.Coordinates2LinksReplacer;
import com.github.golubevda.gpx2kml.extension.CoordinatesLinkGenerator;
import com.github.golubevda.gpx2kml.output.OutputFactory;
import net.sf.saxon.s9api.*;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.golubevda.gpx2kml.TemplateConstants.PARAM_DOC_NAME;
import static com.github.golubevda.gpx2kml.TemplateConstants.PARAM_GEO_LINK_TYPE;

/**
 * @author Dmitry Golubev
 */
public class Gpx2KmlConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private static final XsltExecutable XSLT_EXECUTABLE;
    private static final Processor PROCESSOR;

    static {
        PROCESSOR = new Processor(false);
        registerExtensions();

        final XsltCompiler compiler = PROCESSOR.newXsltCompiler();
        final String xsltResourcePath = "/gpx2kml.xslt";
        try (InputStream xsltStream = new BufferedInputStream(Objects.requireNonNull(Gpx2KmlConverter.class.getResourceAsStream(xsltResourcePath)))) {
            XSLT_EXECUTABLE = compiler.compile(new StreamSource(xsltStream));
        } catch (Exception e) {
            throw new RuntimeException("Failed to compile XSLT document", e);
        }
    }

    private static void registerExtensions() {
        PROCESSOR.registerExtensionFunction(new Coordinates2LinksReplacer());
        PROCESSOR.registerExtensionFunction(new CoordinatesLinkGenerator());
    }

    public void convert(Parameters params) throws IOException, SaxonApiException {
        final File inputFile = getInputFile(params);

        try (InputStream gpxStream = new BufferedInputStream(Files.newInputStream(inputFile.toPath()))) {
            final Xslt30Transformer transformer = XSLT_EXECUTABLE.load30();
            transformer.setStylesheetParameters(createTemplateParams(params));

            Destination destination = null;
            try (OutputStream os = OutputFactory.createOutputStream(params)) {
                destination = PROCESSOR.newSerializer(os);
                transformer.transform(new StreamSource(gpxStream), destination);
            } finally {
                if (destination != null) {
                    destination.close();
                }
            }
        }
    }

    private File getInputFile(Parameters params) {
        final File inputFile = new File(params.getInputFile());
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("Input file " + inputFile.getAbsolutePath() + " does not exist");
        }
        if (inputFile.isDirectory()) {
            throw new IllegalArgumentException("Input file " + inputFile.getAbsolutePath() + " is a directory");
        }
        return inputFile;
    }

    private Map<QName, XdmValue> createTemplateParams(Parameters params) {
        final Map<QName, XdmValue> result = new HashMap<>();

        String docName = params.getDocName();
        if (docName == null || docName.trim().isEmpty()) {
            docName = getDefaultDocName();
        }
        result.put(PARAM_DOC_NAME, XdmValue.makeValue(docName));
        result.put(PARAM_GEO_LINK_TYPE, XdmValue.makeValue(params.getGeoLinkType().toString()));

        return result;
    }

    private String getDefaultDocName() {
        return "Caches " + DATE_TIME_FORMATTER.format(LocalDateTime.now());
    }
}
