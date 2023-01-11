package com.github.golubevda.gpx2kml;

import com.beust.jcommander.Parameter;

/**
 * @author Dmitry Golubev
 */
public class Parameters {

    @Parameter(names = {"-h", "-help", "--help"}, help = true, description = "Вывести эту справку и завершиться")
    private boolean help;

    @Parameter(required = true, description = "путь_до_исходного_файла_gpx")
    private String inputFile;

    @Parameter(names = {"-o", "--output"}, description = "Путь для выгрузки файла *.kml")
    private String outputPath;

    @Parameter(names = {"-dn", "--doc-name"}, description = "Имя коллекции точек")
    private String docName;

    @Parameter(names = {"-glt", "--geo-links-type"}, description = "Тип генерируемых ссылок для координат")
    private LinkType geoLinkType = LinkType.GE0;

    @Parameter(names = {"-v", "--verbose"}, description = "Вывод отладочной информации")
    private boolean verbose;

    public boolean isHelp() {
        return help;
    }

    public String getInputFile() {
        return inputFile;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getDocName() {
        return docName;
    }

    public LinkType getGeoLinkType() {
        return geoLinkType;
    }

    public boolean isVerbose() {
        return verbose;
    }
}
