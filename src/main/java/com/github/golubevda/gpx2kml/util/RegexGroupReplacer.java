package com.github.golubevda.gpx2kml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dmitry Golubev
 */
public class RegexGroupReplacer {

    private final Pattern pattern;
    private final Function<List<String>, String> replaceFunction;

    public RegexGroupReplacer(Pattern pattern, Function<List<String>, String> replaceFunction) {
        this.pattern = pattern;
        this.replaceFunction = replaceFunction;
    }

    public String replace(String original) {
        final Matcher matcher = pattern.matcher(original);

        final StringBuilder result = new StringBuilder();
        int lastMatchEnd = 0;
        String lastPart = original;
        while (matcher.find()) {
            final List<String> groups = new ArrayList<>(matcher.groupCount() + 1);
            groups.add(matcher.group(0));
            for (int groupNum = 1; groupNum <= matcher.groupCount(); ++groupNum) {
                groups.add(matcher.group(groupNum));
            }

            final int start = matcher.start();
            final int end = matcher.end();

            result.append(original.substring(lastMatchEnd, start))
                    .append(replaceFunction.apply(groups));

            lastPart = original.substring(end);
            lastMatchEnd = end;
        }

        return result.append(lastPart).toString();
    }

}
