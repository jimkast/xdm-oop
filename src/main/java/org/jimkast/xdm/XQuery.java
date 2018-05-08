package org.jimkast.xdm;

import javax.xml.transform.Source;
import java.io.IOException;

public interface XQuery {
    Xdm evaluate(Source source) throws IOException;

    XQuery with(CharSequence key, Xdm val);
}
