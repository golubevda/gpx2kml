package com.github.golubevda.gpx2kml;

import com.github.golubevda.gpx2kml.extension.Coordinates2LinksReplacer;
import com.github.golubevda.gpx2kml.extension.CoordinatesLinkGenerator;
import com.github.golubevda.gpx2kml.extension.ItemSetExtension;
import com.github.golubevda.gpx2kml.output.OutputFactory;
import net.sf.saxon.s9api.*;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.golubevda.gpx2kml.TemplateConstants.*;

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
        PROCESSOR.registerExtensionFunction(new ItemSetExtension());
    }

    public void convert(Parameters params) throws IOException, SaxonApiException {
        final Collection<URI> inputUris = getInputUris(params);

        final Xslt30Transformer transformer = XSLT_EXECUTABLE.load30();
        final Map<QName, XdmValue> templateParams = createTemplateParams(params);
        templateParams.put(PARAM_INPUT_FILES, XdmValue.makeSequence(inputUris));
        transformer.setStylesheetParameters(templateParams);

        Destination destination = null;
        try (OutputStream os = OutputFactory.createOutputStream(params)) {
            destination = PROCESSOR.newSerializer(os);
            transformer.callTemplate(ENTRY_TEMPLATE_NAME, destination);
        } finally {
            if (destination != null) {
                destination.close();
            }
        }
    }

    private Collection<URI> getInputUris(Parameters params) {
        final File input = new File(params.getInputFile());
        if (!input.exists()) {
            throw new IllegalArgumentException("Input file " + input.getAbsolutePath() + " does not exist");
        }
        if (!input.isDirectory()) {
            return Collections.singleton(input.toURI());
        }

        final File[] gpxFiles = input.listFiles(pathname -> !pathname.isDirectory() && pathname.getName().toLowerCase().endsWith(".gpx"));
        if (gpxFiles == null || gpxFiles.length == 0) {
            throw new IllegalArgumentException("No *.gpx files were found in input directory " + params.getInputFile());
        }

        return Arrays.stream(gpxFiles).map(File::toURI).collect(Collectors.toSet());
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
