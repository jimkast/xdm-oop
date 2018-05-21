package org.jimkast.xdm.saxon;

import org.jimkast.xdm.Xdm;

public final class XdmWrapped extends XdmEnvelope {
    private static final XqSaxon XQ = new XqSaxon("declare variable $contents external; declare variable $name external; element {$name} {$contents}");

    public XdmWrapped(CharSequence name, Xdm origin) {
        super(() -> XQ.with("contents", origin).with("name", new XdmSaxon(name)).evaluate());
    }
}
