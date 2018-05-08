package org.jimkast.xdm.saxon;

import net.sf.saxon.s9api.XdmValue;
import org.jimkast.xdm.Xdm;

import javax.xml.transform.Source;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

public interface SXdm extends Xdm {
    XdmValue xdm();

    @Override
    SXdm query(CharSequence xpath) throws IOException;

    @Override
    String text();

    @Override
    String serialize();

    @Override
    Source source();

    @Override
    Iterator<Xdm> iterator();

    XdmValue EMPTY_ARRAY = new XdmValue(Collections.emptyList());
    SXdm EMPTY = new SXdm() {
        @Override
        public XdmValue xdm() {
            return EMPTY_ARRAY;
        }

        @Override
        public SXdm query(CharSequence xpath) {
            return this;
        }

        @Override
        public String text() {
            return Xdm.EMPTY.text();
        }

        @Override
        public String serialize() {
            return Xdm.EMPTY.serialize();
        }

        @Override
        public Source source() {
            return Xdm.EMPTY.source();
        }

        @Override
        public Iterator<Xdm> iterator() {
            return Xdm.EMPTY.iterator();
        }
    };
}
