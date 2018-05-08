package org.jimkast.xdm;

import org.jimkast.xdm.saxon.XdmEnvelope;

public final class XslResult extends XdmEnvelope {
    public XslResult(Xsl xsl, Xdm xdm) {
        super(() -> xsl.apply(xdm.source()));
    }
}
