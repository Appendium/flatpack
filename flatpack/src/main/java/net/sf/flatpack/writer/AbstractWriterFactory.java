package net.sf.flatpack.writer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.flatpack.InitialisationException;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.xml.MapParser;


/**
 *
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public abstract class AbstractWriterFactory implements WriterFactory {
    private Map mapping;

    protected AbstractWriterFactory() {
        super();

        mapping = new HashMap<String, Object>();
        mapping.put(FPConstants.DETAIL_ID, new ArrayList());
        mapping.put(FPConstants.COL_IDX, new HashMap());
    }

    protected AbstractWriterFactory(final Map<String, ?> mapping) {
        super();
        this.mapping = mapping;
    }

    protected AbstractWriterFactory(final Reader mappingSrc) throws IOException {
        this();

        try {
            mapping = MapParser.parse(mappingSrc, null);
        } catch (final Exception jde) {
            throw new InitialisationException(jde);
        }
    }

    protected Map<String, Object> getColumnMapping() {
        // TODO DO: return deep mutable clone here or better: make the Map a
        // first class
        // citizen of the library
        return Collections.unmodifiableMap(mapping);
    }

    @Override
    public abstract Writer createWriter(java.io.Writer out) throws IOException;
}
