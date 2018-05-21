package org.jimkast.xdm.saxon;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.io.InputStreamOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.IoCheckedScalar;
import org.cactoos.scalar.StickyScalar;
import org.jimkast.xdm.XQuery;
import org.jimkast.xdm.Xdm;

public final class XqSaxon implements XQuery {
    private static final XQueryCompiler XQUERY_COMPILER = XdmSaxon.PROCESSOR.newXQueryCompiler();
    private final IoCheckedScalar<XQueryExecutable> query;
    private final Map<CharSequence, SXdm> params;

    public XqSaxon(CharSequence text) {
        this(new StickyScalar<>(() -> XQUERY_COMPILER.compile(text.toString())));
    }

    public XqSaxon(Input input) {
        this(new InputStreamOf(input));
    }

    public XqSaxon(InputStream source) {
        this(new StickyScalar<>(() -> XQUERY_COMPILER.compile(source)));
    }

    public XqSaxon(Reader source) {
        this(new StickyScalar<>(() -> XQUERY_COMPILER.compile(source)));
    }

    public XqSaxon(XQueryExecutable stylesheet) {
        this(() -> stylesheet);
    }

    public XqSaxon(Scalar<XQueryExecutable> stylesheet) {
        this(new IoCheckedScalar<>(stylesheet));
    }

    public XqSaxon(IoCheckedScalar<XQueryExecutable> stylesheet) {
        this(stylesheet, new HashMap<>());
    }

    public XqSaxon(IoCheckedScalar<XQueryExecutable> query, Map<CharSequence, SXdm> params) {
        this.query = query;
        this.params = params;
    }

    public XdmSaxon evaluate(Xdm xdm) throws IOException {
        return evaluate(xdm.source());
    }

    @Override
    public XdmSaxon evaluate(Source source) throws IOException {
        try {
            XQueryEvaluator ev = query.value().load();
            ev.setSource(source);
            params.forEach((key, val) -> ev.setExternalVariable(new QName(key.toString()), val.xdm()));
            return new XdmSaxon(ev.evaluate());
        } catch (SaxonApiException e) {
            throw new IOException(e);
        }
    }

    public XdmSaxon evaluate() throws IOException {
        try {
            XQueryEvaluator ev = query.value().load();
            params.forEach((key, val) -> ev.setExternalVariable(new QName(key.toString()), val.xdm()));
            return new XdmSaxon(ev.evaluate());
        } catch (SaxonApiException e) {
            throw new IOException(e);
        }
    }

    @Override
    public XqSaxon with(CharSequence key, Xdm val) {
        return with(key, (SXdm) val);
    }

    public XqSaxon with(CharSequence key, SXdm val) {
        return new XqSaxon(query, new MapOf<>(params, new MapEntry<>(key, val)));
    }
}
