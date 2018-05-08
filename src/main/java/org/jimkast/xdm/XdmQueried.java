package org.jimkast.xdm;

import org.jimkast.xdm.saxon.XdmEnvelope;

public final class XdmQueried extends XdmEnvelope {
    public XdmQueried(Xdm origin, CharSequence q) {
        super(() -> origin.query(q));
    }
}
