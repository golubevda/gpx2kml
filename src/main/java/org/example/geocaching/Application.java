package org.example.geocaching;

import com.beust.jcommander.JCommander;

/**
 * @author Dmitry Golubev
 */
public class Application {

    public static void main(String[] args) throws Exception {
        final Parameters params = new Parameters();
        JCommander.newBuilder().addObject(params).args(args).build();
        new Gpx2KmlConverter().convert(params);
    }
}
