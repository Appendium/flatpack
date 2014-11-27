package net.sf.flatpack.writer;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 *
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public class FixedWriterFactory extends AbstractWriterFactory {
    public static final char DEFAULT_PADDING_CHARACTER = ' ';

    private final char pad;

    public FixedWriterFactory(final Map mapping) {
        super(mapping);
        this.pad = DEFAULT_PADDING_CHARACTER;
    }

    public FixedWriterFactory(final Reader mappingSrc) throws IOException {
        this(mappingSrc, DEFAULT_PADDING_CHARACTER);
    }

    public FixedWriterFactory(final Reader mappingSrc, final char fillChar) throws IOException {
        super(mappingSrc);
        this.pad = fillChar;
    }

    @Override
    public Writer createWriter(final java.io.Writer output) throws IOException {
        return new FixedLengthWriter(this.getColumnMapping(), output, pad);
    }
}
