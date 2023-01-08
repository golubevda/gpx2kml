package org.example.geocaching;

import com.beust.jcommander.Parameter;

/**
 * @author Dmitry Golubev
 */
public class Parameters {

    @Parameter(required = true, description = "Путь до исходного файла *.gpx")
    private String inputFile;

    @Parameter(names = {"-o", "--output"}, description = "Путь для выгрузки файла *.kml")
    private String outputPath;

    @Parameter(names = {"-dn", "--doc-name"}, description = "Имя коллекции точек")
    private String docName;

    @Parameter(names = {"-v", "--verbose"}, description = "Вывод отладочной информации")
    private boolean verbose;

    public String getInputFile() {
        return inputFile;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getDocName() {
        return docName;
    }

    public boolean isVerbose() {
        return verbose;
    }
}
