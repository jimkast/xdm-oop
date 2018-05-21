package org.jimkast.xdm.saxon;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.io.InputStreamOf;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.scalar.StickyScalar;
import org.cactoos.scalar.UncheckedScalar;
import org.jimkast.xdm.Xdm;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public final class XdmSaxon implements SXdm {
    public static final SXdm EMPTY = SXdm.EMPTY;
    static final Processor PROCESSOR = new Processor(false);
    private static final XQueryCompiler XQ_COMPILER = PROCESSOR.newXQueryCompiler();
    private static final DocumentBuilder DOC_BUILDER = PROCESSOR.newDocumentBuilder();

    private final XQueryCompiler compiler;
    private final UncheckedScalar<XdmValue> val;

    public XdmSaxon() {
        this(new XdmValue(Collections.emptyList()));
    }

    public XdmSaxon(Object o) {
        this(() -> XdmValue.makeValue(o));
    }

    public XdmSaxon(Text o) {
        this(() -> XdmValue.makeValue(o.asString()));
    }

    public XdmSaxon(CharSequence o) {
        this(() -> XdmValue.makeValue(o.toString()));
    }

    public XdmSaxon(Input input) {
        this(new InputStreamOf(input));
    }

    public XdmSaxon(InputStream input) {
        this(new StreamSource(input));
    }

    public XdmSaxon(Reader reader) {
        this(new StreamSource(reader));
    }

    public XdmSaxon(Source source) {
        this(() -> DOC_BUILDER.build(source));
    }

    public XdmSaxon(Xdm... many) {
        this(Arrays.asList(many));
    }

    public XdmSaxon(Iterable<Xdm> many) {
        this(() -> new XdmValue(new Joined<>(new Mapped<>(SXdm::xdm, new Mapped<>(SXdm.class::cast, many)))));
    }

    public XdmSaxon(XdmValue val) {
        this(() -> val);
    }

    public XdmSaxon(Scalar<XdmValue> val) {
        this(XQ_COMPILER, val);
    }

    public XdmSaxon(XQueryCompiler compiler, Scalar<XdmValue> val) {
        this(compiler, new UncheckedScalar<>(new StickyScalar<>(val)));
    }

    public XdmSaxon(XQueryCompiler compiler, UncheckedScalar<XdmValue> val) {
        this.compiler = compiler;
        this.val = val;
    }

    @Override
    public XdmSaxon query(CharSequence xpath) throws IOException {
        XQueryExecutable xq;
        try {
            xq = compiler.compile(xpath.toString());
        } catch (SaxonApiException e) {
            throw new IOException(e);
        }
        return new XdmSaxon(
            new XdmValue(
                new Joined<>(
                    new Mapped<>(
                        input -> {
                            XQueryEvaluator ev = xq.load();
                            ev.setContextItem(input);
                            return ev.evaluate();
                        },
                        val.value()
                    )
                )
            )
        );
    }

    @Override
    public String text() {
        Iterator<XdmItem> iterator = val.value().iterator();
        return iterator.hasNext() ? iterator.next().getStringValue() : "";
//        return String.join("\n", new Mapped<>(XdmItem::getStringValue, val.value()));
    }

    @Override
    public String serialize() {
        return String.join("\n", new Mapped<>(XdmValue::toString, val.value()));
//        return val.value().toString();
    }

    @Override
    public Source source() {
        XdmValue xdm = val.value();
        if (xdm instanceof XdmNode) {
            return ((XdmNode) xdm).asSource();
        }
        if (xdm.size() == 1 && xdm.itemAt(0) instanceof XdmNode) {
            return ((XdmNode) xdm.itemAt(0)).asSource();
        }
        throw new IllegalStateException("Xml is not a node!");
    }

    @Override
    public Iterator<Xdm> iterator() {
        return new org.cactoos.iterator.Mapped<>(XdmSaxon::new, val.value().iterator());
    }

    @Override
    public XdmValue xdm() {
        return val.value();
    }
}
