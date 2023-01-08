package org.example.geocaching.extension;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import org.example.geocaching.UrlAlg;
import org.example.geocaching.util.RegexGroupReplacer;

import java.util.regex.Pattern;

/**
 * @author Dmitry Golubev
 */
public class TextCoordinatesReplacer extends ExtensionFunctionDefinition {

    public static final String DEGREES_FRAGMENT = "(?:[°0]|гр)?";
    public static final String MINUTES_FRAGMENT = "(?:'|мин)";

    public static final Pattern WGS84_COORDS_PATTERN = Pattern.compile(
            /* фрагмент широты */
            "([NS])\\s*(\\d{2})\\s*" + DEGREES_FRAGMENT + "\\s*0*(\\d{2}[.,]\\d{3,})\\s*" + MINUTES_FRAGMENT + "?" +
                    /* фрагмент в тексте между широтой и долготой */
                    "\\s*,?\\s*(?:<\\s*br\\s*/?\\s*>)?\\s*" +
                    /* фрагмент долготы */
                    "([EW])\\s*0*(\\d{2})\\s*" + DEGREES_FRAGMENT + "\\s*(\\d{2}[.,]\\d{3,})(?:\\s*" + MINUTES_FRAGMENT + ")?",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );
    static {
        System.out.println("WGS84 coordinates pattern is: " + WGS84_COORDS_PATTERN);
    }

    private final UrlAlg alg = new UrlAlg();

    @Override
    public StructuredQName getFunctionQName() {
        return StructuredQName.fromClarkName("{http://geocaching.example.org}replaceCoordinates");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{SequenceType.SINGLE_STRING};
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.SINGLE_STRING;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                final String text = arguments[0].head().getStringValue();
                final String processedText = new RegexGroupReplacer(WGS84_COORDS_PATTERN, groups -> {
                    final String latChar = groups.get(1);
                    final int latDeg = Integer.parseInt(groups.get(2));
                    final double latMin = Double.parseDouble(groups.get(3).replaceAll(",", "."));

                    final String lonChar = groups.get(4);
                    final int lonDeg = Integer.parseInt(groups.get(5));
                    final double lonMin = Double.parseDouble(groups.get(6).replaceAll(",", "."));

                    final CoordsDD coordsDD = wgs84toDD(
                            latChar, latDeg, latMin,
                            lonChar, lonDeg, lonMin
                    );

                    // В формате опущен символ минут ('), т.к. такие имена не работают в https://omaps.app
                    final String label = String.format("%s %02d° %.3f %s %02d° %.3f",
                            latChar.toUpperCase(), latDeg, latMin,
                            lonChar.toUpperCase(), lonDeg, lonMin
                    );
                    final String href = alg.generateShortShowMapUrl(coordsDD.lat, coordsDD.lon, 20, label);
                    return String.format("<a href=\"%s\">%s</a>", href, label);
                }).replace(text);

                return StringValue.makeStringValue(processedText);
            }
        };
    }

    private CoordsDD wgs84toDD(String latChar, long latDeg, double latMin, String lonChar, long lonDeg, double lonMin) {
        final double latDD = latDeg + (latMin / 60);
        final double lonDD = lonDeg + (lonMin / 60);
        return new CoordsDD(
                "S".equalsIgnoreCase(latChar) ? -latDD : latDD,
                "W".equalsIgnoreCase(lonChar) ? -lonDD : lonDD
        );
    }

    private static class CoordsDD {
        private final double lat;
        private final double lon;

        public CoordsDD(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}
