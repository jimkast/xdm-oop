package org.jimkast.xdm;

import javax.xml.transform.Source;
import java.io.IOException;
import java.io.Writer;

public interface Xsl {
    Writer apply(Source source, Writer writer) throws IOException;

    Xdm apply(Source source) throws IOException;

    Xsl with(CharSequence key, Xdm val);


    class Envelope implements Xsl {
        private final Xsl xsl;

        public Envelope(Xsl xsl) {
            this.xsl = xsl;
        }

        @Override
        public final Writer apply(Source source, Writer writer) throws IOException {
            return xsl.apply(source, writer);
        }

        @Override
        public final Xdm apply(Source source) throws IOException {
            return xsl.apply(source);
        }

        @Override
        public final Xsl with(CharSequence key, Xdm val) {
            return xsl.with(key, val);
        }
    }
}
