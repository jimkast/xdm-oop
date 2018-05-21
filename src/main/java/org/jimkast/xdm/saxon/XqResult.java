package org.jimkast.xdm.saxon;

import org.jimkast.xdm.XQuery;
import org.jimkast.xdm.Xdm;

public final class XqResult extends XdmEnvelope {
    public XqResult(XQuery xq, Xdm xdm) {
        super(() -> xq.evaluate(xdm.source()));
    }
}
