package com.github.golubevda.gpx2kml.output;

import com.github.golubevda.gpx2kml.Parameters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class OutputFactory {

    public static OutputStream createOutputStream(Parameters params) {
        final OutputStream out;
        switch (params.getOutputType()) {
            case KML:
                out = getKmlOutputStream(params);
                break;
            case KMZ:
                out = getKmzOutputStream(params);
                break;
            case STDOUT:
                out = System.out;
                break;
            default:
                throw new IllegalArgumentException("Unknown output type: " + params.getOutputType());
        }
        return out;
    }

    private static OutputStream getKmlOutputStream(Parameters params) {
        try {
            return new BufferedOutputStream(Files.newOutputStream(getOutputFile(params, "kml").toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create output file", e);
        }
    }

    private static OutputStream getKmzOutputStream(Parameters params) {
        File outputFile = getOutputFile(params, "kmz");

        ZipOutputStream zos = null;
        try {
            zos = new SingleEntryZipOutputStream(Files.newOutputStream(outputFile.toPath()));
            zos.setLevel(Deflater.BEST_COMPRESSION);
            zos.putNextEntry(new ZipEntry("points.kml"));
            return zos;
        } catch (IOException e) {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException ignored) {
                }
            }
            throw new RuntimeException("Failed to prepare writable ZIP", e);
        }
    }

    private static class SingleEntryZipOutputStream extends ZipOutputStream {

        public SingleEntryZipOutputStream(OutputStream out) {
            super(out);
        }

        public SingleEntryZipOutputStream(OutputStream out, Charset charset) {
            super(out, charset);
        }

        @Override
        public void close() throws IOException {
            closeEntry();
            super.close();
        }
    }

    private static File getOutputFile(Parameters params, String extSuffix) {
        File outputFile;
        if (params.getOutputPath() != null && !params.getOutputPath().trim().isEmpty()) {
            outputFile = new File(params.getOutputPath());
            if (outputFile.exists() && outputFile.isDirectory()) {
                throw new IllegalArgumentException("Output file " + outputFile.getAbsolutePath() + " is a directory");
            }
        } else {
            String outFileName = new File(params.getInputFile()).getName();
            final String lowerCaseName = outFileName.toLowerCase();
            if (lowerCaseName.endsWith(".gpx") || lowerCaseName.endsWith(".xml")) {
                outFileName = outFileName.substring(0, outFileName.length() - 4);
            }
            outputFile = new File(outFileName + "." + extSuffix.toLowerCase());
        }
        return outputFile;
    }
}
