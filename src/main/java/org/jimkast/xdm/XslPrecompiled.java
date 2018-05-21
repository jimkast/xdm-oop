package org.jimkast.xdm;

import java.io.IOException;

public final class XslPrecompiled extends Xsl.Envelope {
    public XslPrecompiled(Xsl xsl) throws IOException {
        super(xsl);
        xsl.apply(Xdm.EMPTY.source());
    }
}
