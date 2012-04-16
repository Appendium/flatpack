package net.sf.flatpack.writer;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FPConstants;

import org.jdom.JDOMException;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public class DelimiterWriterFactory extends AbstractWriterFactory {
    public static final char DEFAULT_DELIMITER = ';';
    public static final char DEFAULT_QUALIFIER = '"';

    private final char delimiter;
    private final char qualifier;

    public DelimiterWriterFactory(final char delimiter, final char qualifier) {
        super();
        this.delimiter = delimiter;
        this.qualifier = qualifier;
    }

    public DelimiterWriterFactory(final Reader mappingSrc) throws IOException, JDOMException {
        this(mappingSrc, DEFAULT_DELIMITER);
    }

    public DelimiterWriterFactory(final Reader mappingSrc, final char delimiter) throws IOException, JDOMException {
        this(mappingSrc, delimiter, DEFAULT_QUALIFIER);
    }

    public DelimiterWriterFactory(final Reader mappingSrc, final char delimiter, final char qualifier) throws IOException {
        super(mappingSrc);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
    }

    public DelimiterWriterFactory(final Map mapping) {
        this(mapping, DEFAULT_DELIMITER, DEFAULT_QUALIFIER);
    }

    public DelimiterWriterFactory(final Map mapping, final char delimiter) {
        this(mapping, delimiter, DEFAULT_QUALIFIER);
    }

    public DelimiterWriterFactory(final Map mapping, final char delimiter, final char qualifier) {
        super(mapping);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public char getQualifier() {
        return qualifier;
    }

    public Writer createWriter(final java.io.Writer out, final WriterOptions options) throws IOException {
        return new DelimiterWriter(this.getColumnMapping(), out, delimiter, qualifier, options);
    }

    public Writer createWriter(final java.io.Writer out) throws IOException {
        return new DelimiterWriter(this.getColumnMapping(), out, delimiter, qualifier, WriterOptions.getInstance());
    }

    // TODO DO: check that no column titles can be added after first nextRecord
    public void addColumnTitle(final String columnTitle) {
        final Map columnMapping = this.getColumnMapping();
        final List columnMetaDatas = (List) columnMapping.get(FPConstants.DETAIL_ID);
        final Map columnIndices = (Map) columnMapping.get(FPConstants.COL_IDX);

        final ColumnMetaData metaData = new ColumnMetaData();
        metaData.setColName(columnTitle);
        columnMetaDatas.add(metaData);

        final Integer columnIndex = Integer.valueOf(columnMetaDatas.indexOf(metaData));
        columnIndices.put(columnIndex, columnTitle);
    }
}
