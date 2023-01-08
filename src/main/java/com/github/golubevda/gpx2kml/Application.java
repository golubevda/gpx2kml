package com.github.golubevda.gpx2kml;

import com.beust.jcommander.JCommander;
import com.github.golubevda.gpx2kml.util.LogUtils;

import java.util.logging.Level;

/**
 * @author Dmitry Golubev
 */
public class Application {

    public static void main(String[] args) throws Exception {
        final Parameters params = new Parameters();
        JCommander.newBuilder().addObject(params).args(args).build();

        LogUtils.setLevel(params.isVerbose() ? Level.ALL : Level.OFF);

        new Gpx2KmlConverter().convert(params);
    }
}
