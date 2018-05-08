package org.jimkast.xdm;

import org.jimkast.xdm.saxon.XdmEnvelope;

public final class XqResult extends XdmEnvelope {
    public XqResult(XQuery xq, Xdm xdm) {
        super(() -> xq.evaluate(xdm.source()));
    }
}
