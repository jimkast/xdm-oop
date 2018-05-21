package org.jimkast.xdm.saxon;

import org.jimkast.xdm.Xdm;
import org.jimkast.xdm.Xsl;

public final class XslResult extends XdmEnvelope {
    public XslResult(Xsl xsl, Xdm xdm) {
        super(() -> xsl.apply(xdm.source()));
    }
}
