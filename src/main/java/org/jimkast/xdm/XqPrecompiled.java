package org.jimkast.xdm;

import java.io.IOException;

public final class XqPrecompiled extends XQuery.Envelope {
    public XqPrecompiled(XQuery xq) throws IOException {
        super(xq);
        xq.evaluate(Xdm.EMPTY.source());
    }
}
