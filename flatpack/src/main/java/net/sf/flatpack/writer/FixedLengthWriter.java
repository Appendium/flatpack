package net.sf.flatpack.writer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FPConstants;

/**
 *
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public class FixedLengthWriter extends AbstractWriter {
    private static final int MAX_CHAR_TO_USE_LOOP = 8;
    private final Map<?, ?> columnMapping;
    private final char padChar;

    protected FixedLengthWriter(final Map parsedMapping, final java.io.Writer output, final char padChar) throws IOException {
        super(output);
        this.columnMapping = parsedMapping;
        this.padChar = padChar;
    }

    @Override
    public Writer addRecordEntry(final String columnName, final Object value) {
        if (value != null) {
            final ColumnMetaData metaData = this.getColumnMetaData(columnName);

            final String valueString = value.toString();
            if (valueString.length() > metaData.getColLength()) {
                throw new IllegalArgumentException(valueString + " exceeds the maximum length for column " + columnName + "("
                        + metaData.getColLength() + ")");
            }
        }
        return super.addRecordEntry(columnName, value);
    }

    @Override
    public Writer nextRecord() throws IOException {
        for (final ColumnMetaData element : getColumnMetaData()) {
            final Object value = this.getRowMap().get(element.getColName());
            this.write(this.formattedValue(value, element));
        }

        return super.nextRecord();
    }

    protected char[] formattedValue(final Object val, final ColumnMetaData element) {
        Object value = val;
        if (value == null) {
            // TODO DO: maybe have a way to substitute default values here?
            value = "";
        }

        String stringValue;
        if (value instanceof BigDecimal) {
            final BigDecimal bd = (BigDecimal) value;
            stringValue = bd.signum() == 0 ? "0" : bd.toPlainString();
        } else {
            stringValue = value.toString();
        }

        final int stringLength = stringValue.length();

        final int columnLength = element.getColLength();
        final char[] formattedValue = new char[columnLength];

        /*
         * Copy the contents of the original string; for Strings up to ~8-10
         * characters this loop is consistently faster than String.getChars().
         * Short Strings are usually the majority of values in fixed-width data
         * columns.
         */
        final int numCharacters = Math.min(stringLength, columnLength);
        if (numCharacters < MAX_CHAR_TO_USE_LOOP) {
            for (int i = 0; i < numCharacters; i++) {
                formattedValue[i] = stringValue.charAt(i);
            }
        } else {
            stringValue.getChars(0, numCharacters, formattedValue, 0);
        }

        if (stringLength < columnLength) {
            // pad if necessary
            Arrays.fill(formattedValue, stringLength, columnLength, padChar);
        }

        return formattedValue;
    }

    @Override
    protected boolean validateColumnTitle(final String columnTitle) {
        final Map<String, Integer> columnNameToIndex = (Map<String, Integer>) columnMapping.get(FPConstants.COL_IDX);
        return columnNameToIndex.keySet().contains(columnTitle);
    }

    @Override
    public Writer printFooter() {
        return this;
    }

    @Override
    public Writer printHeader() {
        return this;
    }

    /**
     * @return List of ColumnMetaData objects describing the mapping defined in
     *         the XML file.
     */
    private List<ColumnMetaData> getColumnMetaData() {
        return (List<ColumnMetaData>) columnMapping.get(FPConstants.DETAIL_ID);
    }

    private ColumnMetaData getColumnMetaData(final String columnName) {
        for (final ColumnMetaData element : getColumnMetaData()) {
            if (element.getColName().equals(columnName)) {
                return element;
            }
        }

        throw new IllegalArgumentException("Column \"" + columnName + "\" unknown");
    }
}
