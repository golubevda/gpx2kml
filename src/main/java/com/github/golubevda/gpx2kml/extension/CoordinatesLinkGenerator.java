package com.github.golubevda.gpx2kml.extension;

import com.github.golubevda.gpx2kml.TemplateConstants;
import com.github.golubevda.gpx2kml.linkgen.GeoLinkGenerator;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

/**
 * @author Dmitry Golubev
 */
public class CoordinatesLinkGenerator extends CoordinatesLinkFunctionBase {

    @Override
    public StructuredQName getFunctionQName() {
        return TemplateConstants.QNAME_COORDINATES_LINK_FUNCTION.getStructuredQName();
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{
                SequenceType.SINGLE_STRING,
                SequenceType.SINGLE_DOUBLE,
                SequenceType.SINGLE_DOUBLE,
                SequenceType.SINGLE_STRING
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
                final GeoLinkGenerator linkGenerator = createLinkGenerator(arguments, 0);
                final double lat = ((DoubleValue) arguments[1].materialize()).getDoubleValue();
                final double lon = ((DoubleValue) arguments[2].materialize()).getDoubleValue();
                final String name = getOptionalString(arguments, 3, null);

                return StringValue.makeStringValue(linkGenerator.generateLink(lat, lon, GeoLinkGenerator.DEFAULT_ZOOM_LEVEL, name));
            }
        };
    }

    private String getOptionalString(Sequence[] arguments, int num, String defaultValue) throws XPathException {
        return num < arguments.length ? arguments[num].head().getStringValue() : defaultValue;
    }
}
