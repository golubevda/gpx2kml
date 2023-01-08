package org.example.geocaching.util;

import org.example.geocaching.extension.TextCoordinatesReplacer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Dmitry Golubev
 */
class RegexGroupReplacerTest {
    public static final String TEST_TEXT;
    public static final String EXPECTED_TEXT;
    static {
        final List<String> variants = new ArrayList<>();
        variants.add("N50008.714'  E85013.870'");
        variants.add("N50008.714  E85013.870");
        variants.add("N50008.714'  E85013.870");
        variants.add("N50008.714 '  E85013.870'");
        variants.add("N50 008.714 '  E85  013.870'");
        variants.add("N050 008.714 '  E085  013.870'");
        variants.add("S050 008.714 '  W085 013.870'");
        variants.add("S 050 008.714 '  W 085 013.870'");
        variants.add("N50°04.782' E85°08.041'");
        variants.add("N50°04.782', E85°08.041'");
        variants.add("N50°04.782'\nE85°08.041'");
        variants.add("N50°04.782' <br> E85°08.041'");
        variants.add("N50°04.782' <br/> E85°08.041'");
        variants.add("N50 гр 04.782 мин <br/> E85гр08.041мин");
        variants.add("N 51гр 21.318, E84 гр 33.507");
        variants.add("N 50 21.768   E 87 38.000");
        variants.add("N50°4.782' E85°8.041'");
        variants.add("S4°4.782' W7°8.041'");

        TEST_TEXT = buildString(variants);
        EXPECTED_TEXT = buildString(variants.stream().map(variant -> "[" + variant + "]").collect(Collectors.toList()));
    }

    private static String buildString(List<String> variants) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < variants.size(); ++i) {
            builder.append(i).append(": ").append(variants.get(i));
            if (i < variants.size() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    @Test
    void replace() {
        final RegexGroupReplacer replacer = new RegexGroupReplacer(TextCoordinatesReplacer.WGS84_COORDS_PATTERN, groups -> "[" + groups.get(0) + "]");

        Assertions.assertEquals(EXPECTED_TEXT, replacer.replace(TEST_TEXT));
    }
}