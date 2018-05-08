package org.jimkast.xdm;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Iterator;

public interface Xdm extends Iterable<Xdm> {
    Xdm query(CharSequence xpath) throws IOException;

    String text();

    String serialize();

    Source source();

    @Override
    Iterator<Xdm> iterator();

    Xdm EMPTY = new Xdm() {
        @Override
        public Xdm query(CharSequence xpath) {
            return EMPTY;
        }

        @Override
        public String text() {
            return "";
        }

        @Override
        public String serialize() {
            return "";
        }

        @Override
        public Source source() {
            return new StreamSource(new StringReader("<dummy/>"));
        }

        @Override
        public Iterator<Xdm> iterator() {
            return Collections.<Xdm>emptyList().iterator();
        }
    };
}
