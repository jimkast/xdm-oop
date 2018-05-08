package org.jimkast.xdm.saxon;

import org.jimkast.xdm.Xdm;

public final class XdmAppend extends XdmEnvelope {
    private static final XqSaxon XQ = new XqSaxon("declare variable $child external; element {name(.)} {./@*, ./*, $child}");

    public XdmAppend(Xdm origin, Xdm child) {
        super(() -> XQ.with("child", child).evaluate(origin));
    }
}
