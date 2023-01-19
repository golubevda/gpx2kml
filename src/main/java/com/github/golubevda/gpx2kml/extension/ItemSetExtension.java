package com.github.golubevda.gpx2kml.extension;

import com.github.golubevda.gpx2kml.TemplateConstants;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Dmitry Golubev
 */
public
class ItemSetExtension extends ExtensionFunctionDefinition {

    private final Map<String, Set<Object>> sets = new HashMap<>();

    @Override
    public StructuredQName getFunctionQName() {
        return TemplateConstants.QNAME_ADD_TO_SET_FUNCTION.getStructuredQName();
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{SequenceType.SINGLE_STRING, SequenceType.SINGLE_ITEM};
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.SINGLE_BOOLEAN;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                final Set<Object> set = sets.computeIfAbsent(arguments[0].head().getStringValue(), key -> new HashSet<>());
                final String value = arguments[1].head().getStringValue();
                return BooleanValue.get(set.add(value));
            }
        };
    }
}
