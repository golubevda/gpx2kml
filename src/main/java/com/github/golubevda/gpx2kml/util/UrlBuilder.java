package com.github.golubevda.gpx2kml.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Golubev
 */
public class UrlBuilder {

    private String path = "";
    private List<String> params = new ArrayList<>();

    public UrlBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public UrlBuilder addParam(String name, String value) {
        params.add(StringUtils.encodeUrl(name) + "=" + StringUtils.encodeUrl(value));
        return this;
    }

    public String build() {
        return params.isEmpty() ? path : path + "?" + String.join("&", params);
    }
}
