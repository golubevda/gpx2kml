package com.github.golubevda.gpx2kml;

import com.github.golubevda.gpx2kml.extension.Coordinates2LinksReplacer;
import net.sf.saxon.s9api.*;

import javax.xml.transform.stream.StreamSource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        PROCESSOR.registerExtensionFunction(new Coordinates2LinksReplacer());
        final XsltCompiler compiler = PROCESSOR.newXsltCompiler();

        final String xsltResourcePath = "/gpx2kml.xslt";
        try (InputStream xsltStream = new BufferedInputStream(Objects.requireNonNull(Gpx2KmlConverter.class.getResourceAsStream(xsltResourcePath)))) {
            XSLT_EXECUTABLE = compiler.compile(new StreamSource(xsltStream));
        } catch (Exception e) {
            throw new RuntimeException("Failed to compile XSLT document", e);
        }
    }

    public void convert(Parameters params) throws IOException, SaxonApiException {
        final File inputFile = getInputFile(params);
        final File outputFile = getOutputFile(params, inputFile);

        try (InputStream gpxStream = new BufferedInputStream(Files.newInputStream(inputFile.toPath()))) {
            Destination destination = null;
            try {
                final Xslt30Transformer transformer = XSLT_EXECUTABLE.load30();
                transformer.setStylesheetParameters(createTemplateParams(params));

                destination = PROCESSOR.newSerializer(outputFile);
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

    private File getOutputFile(Parameters params, File inputFile) {
        File outputFile;
        if (params.getOutputPath() != null && !params.getOutputPath().trim().isEmpty()) {
            outputFile = new File(params.getOutputPath());
            if (outputFile.exists() && outputFile.isDirectory()) {
                throw new IllegalArgumentException("Output file " + outputFile.getAbsolutePath() + " is a directory");
            }
        } else {
            String outFileName = inputFile.getName();
            final String lowerCaseName = outFileName.toLowerCase();
            if (lowerCaseName.endsWith(".gpx") || lowerCaseName.endsWith(".xml")) {
                outFileName = outFileName.substring(0, outFileName.length() - 4);
            }
            outputFile = new File(outFileName + ".kml");
        }
        return outputFile;
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
