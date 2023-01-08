package org.example.geocaching;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Dmitry Golubev
 */
class UrlAlgTest {

    @Test
    void generateShortShowMapUrl() {
        final String url = new UrlAlg().generateShortShowMapUrl(51.506303, 85.947871, 20, "test_1 2 3");
        Assertions.assertEquals("om://_5dMhVLO7K/test+1_2_3", url);
    }
}