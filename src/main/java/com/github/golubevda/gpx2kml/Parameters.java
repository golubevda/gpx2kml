package com.github.golubevda.gpx2kml;

import com.beust.jcommander.Parameter;
import com.github.golubevda.gpx2kml.link.LinkType;
import com.github.golubevda.gpx2kml.output.OutputType;

/**
 * @author Dmitry Golubev
 */
public class Parameters {

    @Parameter(names = {"-h", "-help", "--help"}, help = true, description = "Вывести эту справку и завершиться")
    private boolean help;

    @Parameter(required = true, description = "<Путь до исходного файла GPX или директории с файлами *.gpx для преобразования>")
    private String inputFile;

    @Parameter(names = {"-o", "--output"}, description = "Путь для выгрузки файла *.kml")
    private String outputPath;

    @Parameter(names = {"-dn", "--doc-name"}, description = "Имя коллекции точек")
    private String docName;

    @Parameter(names = {"-glt", "--geo-links-type"}, description = "Тип генерируемых ссылок для координат")
    private LinkType geoLinkType = LinkType.GE0;

    @Parameter(names = {"-ot", "--output-type"}, description = "Способ вывода результата конвертации")
    private OutputType outputType = OutputType.KMZ;

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

    public OutputType getOutputType() {
        return outputType;
    }

    public boolean isVerbose() {
        return verbose;
    }
}
