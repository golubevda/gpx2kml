package com.github.golubevda.gpx2kml.extension;

import com.github.golubevda.gpx2kml.LinkType;
import com.github.golubevda.gpx2kml.TemplateConstants;
import com.github.golubevda.gpx2kml.linkgen.GeoLinkGenerator;
import com.github.golubevda.gpx2kml.linkgen.GeoLinkGeneratorFactory;
import com.github.golubevda.gpx2kml.util.LogUtils;
import com.github.golubevda.gpx2kml.util.RegexGroupReplacer;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Dmitry Golubev
 */
public class Coordinates2LinksReplacer extends ExtensionFunctionDefinition {

    private static final Logger logger = LogUtils.getLogger(Coordinates2LinksReplacer.class);

    private static final String DEG_FRAG = "\\d{1,2}";
    public static final String DEG_SIGN_FRAG = "(?:[°0]|гр)?";
    private static final String MIN_FRAG = "\\d{1,2}[.,]\\d+";
    private static final String MIN_SIGN_FRAG = "(?:'|мин)";

    private static final LinkType DEFAULT_LINK_TYPE = LinkType.OM;

    public static final Pattern WGS84_COORDS_PATTERN = Pattern.compile(
            /* фрагмент широты */
            "([NS])\\s*0*(" + DEG_FRAG + ")\\s*" + DEG_SIGN_FRAG + "\\s*0*(" + MIN_FRAG + ")\\s*" + MIN_SIGN_FRAG + "?" +
                    /* фрагмент в тексте между широтой и долготой */
                    "\\s*,?\\s*(?:<\\s*br\\s*/?\\s*>)?\\s*" +
                    /* фрагмент долготы */
                    "([EW])\\s*0*(" + DEG_FRAG + ")\\s*" + DEG_SIGN_FRAG + "\\s*0*(" + MIN_FRAG + ")(?:\\s*" + MIN_SIGN_FRAG + ")?",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    static {
        logger.fine("WGS84 coordinates pattern is: " + WGS84_COORDS_PATTERN);
    }

    @Override
    public StructuredQName getFunctionQName() {
        return TemplateConstants.QNAME_REPLACE_COORDINATES_FUNCTION.getStructuredQName();
    }

    @Override
    public int getMinimumNumberOfArguments() {
        return 1;
    }

    @Override
    public int getMaximumNumberOfArguments() {
        return getArgumentTypes().length;
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{
                SequenceType.SINGLE_STRING,
                SequenceType.OPTIONAL_STRING
        };
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
                final GeoLinkGenerator linkGenerator = createLinkGenerator(arguments);

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

                    // В формате для имени в ссылке опущен символ минут ('), т.к. такие имена не работают в https://omaps.app
                    final String hrefLabel = String.format("%s %02d° %.3f %s %02d° %.3f",
                            latChar.toUpperCase(), latDeg, latMin,
                            lonChar.toUpperCase(), lonDeg, lonMin
                    );
                    final String textLabel = String.format("%s %02d° %.3f' %s %02d° %.3f'",
                            latChar.toUpperCase(), latDeg, latMin,
                            lonChar.toUpperCase(), lonDeg, lonMin
                    );
                    final String href = linkGenerator.generateLink(coordsDD.lat, coordsDD.lon, 20, hrefLabel);
                    return String.format("<a href=\"%s\">%s</a>", href, textLabel);
                }).replace(text);

                return StringValue.makeStringValue(processedText);
            }
        };
    }

    private GeoLinkGenerator createLinkGenerator(Sequence[] arguments) throws XPathException {
        LinkType linkType = DEFAULT_LINK_TYPE;
        if (arguments.length > 1) {
            final String linkTypeString = arguments[1].head().getStringValue();
            try {
                linkType = LinkType.valueOf(linkTypeString.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown link type: " + linkTypeString);
            }
        }
        return GeoLinkGeneratorFactory.createGenerator(linkType);
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
