package org.jimkast.xdm;

import javax.xml.transform.Source;
import java.io.IOException;
import java.io.Writer;

public interface Xsl {
    Writer apply(Source source, Writer writer) throws IOException;

    Xdm apply(Source source) throws IOException;
}
