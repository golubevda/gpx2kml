package org.example.geocaching;

import net.sf.saxon.s9api.*;
import org.example.geocaching.extension.TextCoordinatesReplacer;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import static org.example.geocaching.TemplateConstants.*;

/**
 * @author Dmitry Golubev
 */
public class Gpx2KmlConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private static final XsltExecutable XSLT_EXECUTABLE;
    private static final Processor PROCESSOR;

    static {
        PROCESSOR = new Processor(false);
        PROCESSOR.registerExtensionFunction(new TextCoordinatesReplacer());
        final XsltCompiler compiler = PROCESSOR.newXsltCompiler();

        final String xsltResourcePath = "/gpx2kml.xslt";
        try (InputStream xsltStream = new BufferedInputStream(Objects.requireNonNull(Gpx2KmlConverter.class.getResourceAsStream(xsltResourcePath)))) {
            XSLT_EXECUTABLE = compiler.compile(new StreamSource(xsltStream));
        } catch (Exception e) {
            throw new RuntimeException("Failed to compile XSLT document", e);
        }
    }

    public void convert(Parameters params) throws IOException, SaxonApiException {
        final File inputFile = new File(params.getInputFile());
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("Input file " + inputFile.getAbsolutePath() + " does not exist");
        }
        if (inputFile.isDirectory()) {
            throw new IllegalArgumentException("Input file " + inputFile.getAbsolutePath() + " is a directory");
        }

        String docName = params.getDocName();
        if (docName == null || docName.trim().isBlank()) {
            docName = getDefaultDocName();
        }

        File outputFile;
        if (params.getOutputPath() != null && !params.getOutputPath().trim().isBlank()) {
            outputFile = new File(params.getOutputPath());
            if (outputFile.exists() && outputFile.isDirectory()) {
                throw new IllegalArgumentException("Output file " + outputFile.getAbsolutePath() + " is a directory");
            }
        } else {
            outputFile = new File(validFileName(docName) + ".kml");
        }

        try (InputStream gpxStream = new BufferedInputStream(new FileInputStream(inputFile))) {
            Destination destination = null;
            try {
                final Xslt30Transformer transformer = XSLT_EXECUTABLE.load30();
                transformer.setStylesheetParameters(Map.of(PARAM_DOC_NAME, XdmValue.makeValue(docName)));

                destination = PROCESSOR.newSerializer(outputFile);
                transformer.transform(new StreamSource(gpxStream), destination);
            } finally {
                if (destination != null) {
                    destination.close();
                }
            }
        }
    }

    private String getDefaultDocName() {
        return "Caches " + DATE_TIME_FORMATTER.format(LocalDateTime.now());
    }

    private String validFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
