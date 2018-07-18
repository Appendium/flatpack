package net.sf.flatpack;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Supplier;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.FPStringUtils;
import net.sf.flatpack.util.ParserUtils;
import net.sf.flatpack.xml.MetaData;

public class RowRecord implements Record {
    private final Row row;
    private final boolean columnCaseSensitive;
    private final MetaData metaData;
    private Properties pzConvertProps = null;
    private final boolean strictNumericParse;
    private final boolean upperCase;
    private final boolean lowerCase;
    private final boolean nullEmptyString;
    private String[] columns = null;

    public RowRecord(final Row row, final MetaData metaData, final boolean columnCaseSensitive, final Properties pzConvertProps,
            final boolean strictNumericParse, final boolean upperCase, final boolean lowerCase, final boolean nullEmptyString) {
        super();
        this.row = row;
        this.metaData = metaData;
        this.columnCaseSensitive = columnCaseSensitive;
        this.pzConvertProps = pzConvertProps;
        this.strictNumericParse = strictNumericParse;
        this.upperCase = upperCase;
        this.lowerCase = lowerCase;
        this.nullEmptyString = nullEmptyString;
    }

    @Override
    public String getRecordID() {
        String rowID = row.getMdkey();
        if (rowID == null) {
            rowID = FPConstants.DETAIL_ID;
        }
        return rowID;
    }

    @Override
    public boolean isRecordID(final String recordID) {
        final String rowID = getRecordID();

        return rowID.equals(recordID);
    }

    @Override
    public int getRowNo() {
        return row.getRowNumber();
    }

    @Override
    public boolean isRowEmpty() {
        return row.isEmpty();
    }

    @Override
    public boolean contains(final String column) {
        final Iterator<ColumnMetaData> cmds = ParserUtils.getColumnMetaData(row.getMdkey(), metaData).iterator();
        while (cmds.hasNext()) {
            final ColumnMetaData cmd = cmds.next();
            if (cmd.getColName().equalsIgnoreCase(column)) {
                return true;
            }
        }

        return false;

    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#getColumns()
     */
    @Override
    public String[] getColumns() {
        if (metaData != null && columns == null) {
            final List<ColumnMetaData> cmds = metaData.getColumnsNames();

            columns = new String[cmds.size()];
            for (int i = 0; i < cmds.size(); i++) {
                columns[i] = cmds.get(i).getColName();
            }
        }

        return columns;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#getColumns(java.lang.String)
     */
    @Override
    public String[] getColumns(final String recordID) {
        String[] array = null;

        if (metaData != null) {
            final List cmds = ParserUtils.getColumnMetaData(recordID, metaData);
            array = new String[cmds.size()];
            for (int i = 0; i < cmds.size(); i++) {
                final ColumnMetaData column = (ColumnMetaData) cmds.get(i);
                array[i] = column.getColName();
            }
        }

        return array;
    }

    @Override
    public Date getDate(final String column, final SimpleDateFormat sdf, final Supplier<Date> defaultSupplier) throws ParseException {
        final Date d = getDate(column, sdf);
        if (d == null) {
            return defaultSupplier.get();
        }
        return d;
    }

    @Override
    public Date getDate(final String column, final Supplier<Date> defaultSupplier) throws ParseException {
        final Date d = getDate(column);
        if (d == null) {
            return defaultSupplier.get();
        }
        return d;
    }

    @Override
    public Date getDate(final String column) throws ParseException {
        return getDate(column, new SimpleDateFormat("yyyyMMdd"));
    }

    @Override
    public Date getDate(final String column, final SimpleDateFormat sdf) throws ParseException {
        final String s = getStringValue(column);
        if (FPStringUtils.isBlank(s)) {
            // don't do the parse on empties
            return null;
        }
        return sdf.parse(s);
    }

    @Override
    public LocalDate getLocalDate(final String column, final String dateFormat, final Supplier<LocalDate> defaultSupplier) throws ParseException {
        final LocalDate d = getLocalDate(column, dateFormat);
        if (d == null) {
            return defaultSupplier.get();
        }
        return d;
    }

    @Override
    public LocalDate getLocalDate(final String column, final Supplier<LocalDate> defaultSupplier) throws ParseException {
        final LocalDate d = getLocalDate(column);
        if (d == null) {
            return defaultSupplier.get();
        }
        return d;
    }

    @Override
    public LocalDate getLocalDate(final String column) throws ParseException {
        return getLocalDate(column, "yyyy-mm-dd");
    }

    @Override
    public LocalDate getLocalDate(final String column, final String dateFormat) throws ParseException {
        final String s = getStringValue(column);
        if (FPStringUtils.isBlank(s)) {
            // don't do the parse on empties
            return null;
        }
        return LocalDate.parse(s, DateTimeFormatter.ofPattern(dateFormat));
    }

    @Override
    public double getDouble(final String column, final Supplier<Double> defaultSupplier) {
        final String s = getStringValue(column);
        if (FPStringUtils.isBlank(s)) {
            return defaultSupplier.get();
        }
        return getDouble(column);
    }

    @Override
    public double getDouble(final String column) {
        final StringBuilder newString = new StringBuilder();
        final String s = getStringValue(column);

        if (!strictNumericParse) {
            newString.append(ParserUtils.stripNonDoubleChars(s));
        } else {
            newString.append(s);
        }

        return Double.parseDouble(newString.toString());
    }

    @Override
    public int getInt(final String column, final Supplier<Integer> defaultSupplier) {
        final String s = getStringValue(column);
        if (FPStringUtils.isBlank(s)) {
            return defaultSupplier.get();
        }
        return getInt(column);
    }

    @Override
    public int getInt(final String column) {
        final String s = getStringValue(column);

        if (!strictNumericParse) {
            return Integer.parseInt(ParserUtils.stripNonLongChars(s));
        }

        return Integer.parseInt(s);
    }

    @Override
    public long getLong(final String column, final Supplier<Long> defaultSupplier) {
        final String s = getStringValue(column);
        if (FPStringUtils.isBlank(s)) {
            return defaultSupplier.get();
        }
        return getLong(column);
    }

    @Override
    public long getLong(final String column) {
        final String s = getStringValue(column);

        if (!strictNumericParse) {
            return Long.parseLong(ParserUtils.stripNonLongChars(s));
        }

        return Long.parseLong(s);
    }

    private String getStringValue(final String column) {
        return row.getValue(ParserUtils.getColumnIndex(row.getMdkey(), metaData, column, columnCaseSensitive));
    }

    @Override
    public Object getObject(final String column, final Class<?> classToConvertTo) {
        final String s = getStringValue(column);
        return ParserUtils.runPzConverter(pzConvertProps, s, classToConvertTo);
    }

    @Override
    public BigDecimal getBigDecimal(final String column, final Supplier<BigDecimal> defaultSupplier) {
        final BigDecimal bd = getBigDecimal(column);
        if (bd == null) {
            return defaultSupplier.get();
        }
        return bd;
    }

    @Override
    public BigDecimal getBigDecimal(final String column) {
        String s = getStringValue(column);
        if (FPStringUtils.isBlank(s)) {
            // don't do the parse on empties
            return null;
        }

        s = s.replace(",", "").trim();

        if (FPStringUtils.isBlank(s)) {
            // don't do the parse on empties
            return null;
        }
        return new BigDecimal(s);
    }

    @Override
    public String getString(final String column, final Supplier<String> defaultSupplier) {
        final String s = getString(column);
        if (FPStringUtils.isBlank(s)) {
            return defaultSupplier.get();
        }
        return s;
    }

    @Override
    public String getString(final String column) {
        String s = getStringValue(column);

        if (nullEmptyString && FPStringUtils.isBlank(s)) {
            s = null;
        } else if (upperCase) {
            s = s.toUpperCase(Locale.getDefault());
        } else if (lowerCase) {
            s = s.toLowerCase(Locale.getDefault());
        }

        // return value as how it is in the file
        return s;
    }

    @Override
    public String getRawData() {
        return row.getRawData();
    }

}
