package org.jimkast.xdm.saxon;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.io.InputStreamOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.IoCheckedScalar;
import org.cactoos.scalar.StickyScalar;
import org.jimkast.xdm.Xdm;
import org.jimkast.xdm.Xsl;

public final class XslSaxon implements Xsl {
    private static final XsltCompiler XSLT_COMPILER = XdmSaxon.PROCESSOR.newXsltCompiler();
    private final IoCheckedScalar<XsltExecutable> stylesheet;
    private final Map<CharSequence, SXdm> params;

    public XslSaxon(Input input) {
        this(new InputStreamOf(input));
    }

    public XslSaxon(InputStream input) {
        this(new StreamSource(input));
    }

    public XslSaxon(CharSequence systemId) {
        this(new StreamSource(systemId.toString()));
    }

    public XslSaxon(Reader source) {
        this(new StreamSource(source));
    }

    public XslSaxon(Source source) {
        this(new StickyScalar<>(() -> XSLT_COMPILER.compile(source)));
    }

    public XslSaxon(XsltExecutable stylesheet) {
        this(() -> stylesheet);
    }

    public XslSaxon(Scalar<XsltExecutable> stylesheet) {
        this(new IoCheckedScalar<>(stylesheet));
    }

    public XslSaxon(IoCheckedScalar<XsltExecutable> stylesheet) {
        this(stylesheet, new HashMap<>());
    }

    public XslSaxon(IoCheckedScalar<XsltExecutable> stylesheet, Map<CharSequence, SXdm> params) {
        this.stylesheet = stylesheet;
        this.params = params;
    }

    @Override
    public Writer apply(Source source, Writer writer) throws IOException {
        try {
            XsltTransformer xsl = stylesheet.value().load();
            xsl.setSource(source);
            xsl.setDestination(XdmSaxon.PROCESSOR.newSerializer(writer));
            params.forEach((key, val) -> xsl.setParameter(new QName(key.toString()), val.xdm()));
            xsl.transform();
            return writer;
        } catch (SaxonApiException e) {
            throw new IOException(e);
        }
    }

    @Override
    public SXdm apply(Source source) throws IOException {
        try {
            XdmDestination dest = new XdmDestination();
            XsltTransformer xsl = stylesheet.value().load();
            xsl.setSource(source);
            xsl.setDestination(dest);
            params.forEach((key, val) -> xsl.setParameter(new QName(key.toString()), val.xdm()));
            xsl.transform();
            return new XdmSaxon(dest.getXdmNode());
        } catch (SaxonApiException e) {
            throw new IOException(e);
        }
    }

    public SXdm apply(Xdm xdm) throws IOException {
        return apply(xdm.source());
    }

    public XslSaxon with(CharSequence key, Xdm val) {
        return with(key, (SXdm) val);
    }

    public XslSaxon with(CharSequence key, SXdm val) {
        return new XslSaxon(stylesheet, new MapOf<>(params, new MapEntry<>(key, val)));
    }
}
