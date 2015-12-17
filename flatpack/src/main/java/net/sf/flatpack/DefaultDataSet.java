/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting FlatPack.
 *
 * Based in London, we are world leaders in the design and development
 * of bespoke applications for the securities financing markets.
 *
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id: ColorProvider.java 74 2006-10-24 22:19:05Z benoitx $
 *
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.flatpack;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import net.sf.flatpack.ordering.OrderBy;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.util.FPException;
import net.sf.flatpack.util.FPInvalidUsageException;
import net.sf.flatpack.util.ParserUtils;
import net.sf.flatpack.xml.MetaData;

/**
 * @author Benoit Xhenseval
 * @author Paul Zepernick
 *
 */
public class DefaultDataSet implements DataSet {
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final List<Row> rows = new ArrayList<Row>();

    private final List<DataError> errors = new ArrayList<DataError>();

    private Properties pzConvertProps = null;

    /** Pointer for the current row in the array we are on */
    private int pointer = -1;

    /** flag to indicate if data should be pulled as lower case */
    private boolean lowerCase = false;

    /** flag to inidicate if data should be pulled as upper case */
    private boolean upperCase = false;

    /**
     * flag to indicate if a strict parse should be used when getting doubles
     * and ints
     */
    private boolean strictNumericParse = false;

    private MetaData metaData;

    private final Parser parser;

    private Record currentRecord;

    public DefaultDataSet(final MetaData pzMetaData, final Parser pzparser) {
        this.metaData = pzMetaData;
        this.parser = pzparser;
    }

    public void addRow(final Row row) {
        rows.add(row);
    }

    public void addError(final DataError dataError) {
        errors.add(dataError);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#getColumns()
     */
    @Override
    public String[] getColumns() {
        String[] array = null;

        if (/* columnMD != null || */metaData != null) {
            final List<ColumnMetaData> cmds = metaData.getColumnsNames();
            array = new String[cmds.size()];
            for (int i = 0; i < cmds.size(); i++) {
                final ColumnMetaData column = cmds.get(i);
                array[i] = column.getColName();
            }
        }

        return array;
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
            final List<ColumnMetaData> cmds = ParserUtils.getColumnMetaData(recordID, metaData);
            array = new String[cmds.size()];
            for (int i = 0; i < cmds.size(); i++) {
                final ColumnMetaData column = cmds.get(i);
                array[i] = column.getColName();
            }
        }

        return array;
    }

    @Override
    public Date getDate(final String column) throws ParseException {
        return currentRecord.getDate(column);
    }

    @Override
    public Date getDate(final String column, final SimpleDateFormat sdf) throws ParseException {
        return currentRecord.getDate(column, sdf);
    }

    @Override
    public double getDouble(final String column) {
        return currentRecord.getDouble(column);
    }

    @Override
    public BigDecimal getBigDecimal(final String column) {
        return currentRecord.getBigDecimal(column);
    }

    @Override
    public Object getObject(final String column, final Class<?> classToConvertTo) {
        return currentRecord.getObject(column, classToConvertTo);
    }

    @Override
    public BigDecimal getBigDecimal(final String column, final Supplier<BigDecimal> defaultSupplier) {
        return currentRecord.getBigDecimal(column, defaultSupplier);
    }

    @Override
    public Date getDate(final String column, final SimpleDateFormat sdf, final Supplier<Date> defaultSupplier) throws ParseException {
        return currentRecord.getDate(column, sdf, defaultSupplier);
    }

    @Override
    public Date getDate(final String column, final Supplier<Date> defaultSupplier) throws ParseException {
        return currentRecord.getDate(column, defaultSupplier);
    }

    @Override
    public double getDouble(final String column, final Supplier<Double> defaultSupplier) {
        return currentRecord.getDouble(column, defaultSupplier);
    }

    @Override
    public int getInt(final String column, final Supplier<Integer> defaultSupplier) {
        return currentRecord.getInt(column, defaultSupplier);
    }

    @Override
    public long getLong(final String column, final Supplier<Long> defaultSupplier) {
        return currentRecord.getLong(column, defaultSupplier);
    }

    @Override
    public String getString(final String column, final Supplier<String> defaultSupplier) {
        return currentRecord.getString(column, defaultSupplier);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#getErrorCount()
     */
    @Override
    public int getErrorCount() {
        if (getErrors() != null) {
            return getErrors().size();
        }

        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#getErrors()
     */
    @Override
    public List getErrors() {
        return errors;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#getIndex()
     */
    @Override
    public int getIndex() {
        return pointer;
    }

    @Override
    public int getInt(final String column) {
        return currentRecord.getInt(column);
    }

    @Override
    public long getLong(final String column) {
        return currentRecord.getLong(column);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#getRowCount()
     */
    @Override
    public int getRowCount() {
        return rows.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#getRowNo()
     */
    @Override
    public int getRowNo() {
        return currentRecord.getRowNo();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#getString(java.lang.String)
     */
    @Override
    public String getString(final String column) {
        return currentRecord.getString(column);
    }

    @Override
    public void setValue(final String column, final String value) {
        final Row row = rows.get(pointer);
        final int colIndex = ParserUtils.getColumnIndex(row.getMdkey(), metaData, column, parser.isColumnNamesCaseSensitive());

        row.setValue(colIndex, value);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#goBottom()
     */
    @Override
    public void goBottom() {
        pointer = rows.size() - 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#goTop()
     */
    @Override
    public void goTop() {
        pointer = -1;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#isAnError(int)
     */
    @Override
    public boolean isAnError(final int lineNo) {
        for (int i = 0; i < errors.size(); i++) {
            if (errors.get(i).getLineNo() == lineNo && errors.get(i).getErrorLevel() > 1) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#next()
     */
    @Override
    public boolean next() {
        if (pointer < rows.size() && pointer + 1 != rows.size()) {
            pointer++;
            currentRecord = new RowRecord(rows.get(pointer), metaData, parser.isColumnNamesCaseSensitive(), pzConvertProps, strictNumericParse,
                    upperCase, lowerCase, parser.isNullEmptyStrings());
            return true;
        }
        currentRecord = null;
        return false;
    }

    @Override
    public Optional<Record> getRecord() {
        return Optional.ofNullable(currentRecord);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#orderRows(net.sf.flatpack.ordering.OrderBy)
     */
    @Override
    public void orderRows(final OrderBy ob) {
        if (ob != null && rows != null) {
            ob.setMetaData(getMetaData());
            ob.setParser(parser);
            Collections.sort(rows, ob);
            goTop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#previous()
     */
    @Override
    public boolean previous() {
        if (pointer <= 0) {
            currentRecord = null;
            return false;
        }
        pointer--;
        currentRecord = new RowRecord(rows.get(pointer), metaData, parser.isColumnNamesCaseSensitive(), pzConvertProps, strictNumericParse,
                upperCase, lowerCase, parser.isNullEmptyStrings());
        return true;
    }

    /**
     * Sets data in the DataSet to lowercase
     */
    @Override
    public void setLowerCase() {
        upperCase = false;
        lowerCase = true;
    }

    /**
     * Sets data in the DataSet to uppercase
     */
    @Override
    public void setUpperCase() {
        upperCase = true;
        lowerCase = false;
    }

    /**
     * Checks to see if the row has the given &lt;RECORD&gt; id
     *
     * @param recordID
     * @return boolean
     */
    @Override
    public boolean isRecordID(final String recordID) {
        return currentRecord.isRecordID(recordID);
    }

    /**
     * Sets the absolute position of the record pointer
     *
     * @param localPointer
     *            - int
     * @exception IndexOutOfBoundsException
     */
    @Override
    public void absolute(final int localPointer) {
        if (localPointer < 0 || localPointer >= rows.size()) {
            throw new IndexOutOfBoundsException("INVALID POINTER LOCATION: " + localPointer);
        }

        pointer = localPointer;
        currentRecord = new RowRecord(rows.get(pointer), metaData, parser.isColumnNamesCaseSensitive(), pzConvertProps, strictNumericParse,
                upperCase, lowerCase, parser.isNullEmptyStrings());
    }

    /**
     * Setting this to True will parse text as is and throw a
     * NumberFormatException. Setting to false, which is the default, will
     * remove any non numeric charcter from the field. The remaining numeric
     * chars's will be returned. If it is an empty string,or there are no
     * numeric chars, 0 will be returned for getInt() and getDouble()
     *
     * @param strictNumericParse
     *            The strictNumericParse to set.
     */
    @Override
    public void setStrictNumericParse(final boolean strictNumericParse) {
        this.strictNumericParse = strictNumericParse;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.DataSet#remove()
     */
    @Override
    public void remove() {
        rows.remove(pointer);
        pointer--;
    }

    @Override
    public void setPZConvertProps(final Properties props) {
        this.pzConvertProps = props;
    }

    /**
     * @param pointer
     *            the pointer to set
     */
    protected void setPointer(final int pointer) {
        this.pointer = pointer;
    }

    @Override
    public void clearRows() {
        pointer = -1; // set the pointer back to -1 directly just in case this
        // instance is a BuffReaderDataSet.
        rows.clear();
    }

    @Override
    public void clearAll() {
        clearRows();
        clearErrors();
    }

    @Override
    public void clearErrors() {
        errors.clear();
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(final MetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("Errors:").append(errors.size()).append(NEW_LINE);
        buf.append("Rows:").append(rows.size()).append(NEW_LINE);
        buf.append("Position:").append(pointer).append(NEW_LINE);
        buf.append("Conversion Props:").append(pzConvertProps).append(NEW_LINE);
        buf.append("MetaData:").append(metaData).append(NEW_LINE);
        return buf.toString();
    }

    @Override
    public boolean contains(final String column) {
        if (pointer == -1) {
            throw new IndexOutOfBoundsException("dataset on invalid row. need to call next()");
        }
        return currentRecord.contains(column);
    }

    /**
     * @throws FPInvalidUsageException
     *             Parser.isFlagEmptyRows() must be set to true before using
     *             this
     * @throws FPException
     *             if cursor is on an invalid row
     */
    @Override
    public boolean isRowEmpty() {
        if (!parser.isFlagEmptyRows()) {
            // flag empty rows needs to be set for this functionality
            // throw an exception
            throw new FPInvalidUsageException("Parser.isFlagEmptyRows(true) must be set before using isRowEmpty()");
        }

        if (pointer < 0) {
            throw new FPException("Cursor on invalid row..  Make sure next() is called and returns true");
        }

        return rows.get(pointer).isEmpty();
    }

    /**
     * @throws FPInvalidUsageException
     * @throws FPException
     *             if cursor is on an invalid row
     */
    @Override
    public String getRawData() {
        if (!parser.isStoreRawDataToDataSet()) {
            // option needs to be set for this functionality
            // throw an exception
            throw new FPInvalidUsageException("Parser.isStoreRawDataToDataSet(true) must be set before using getRawData()");
        }

        if (pointer < 0) {
            throw new FPException("Cursor on invalid row.. Make sure next() is called and returns true");
        }

        return rows.get(pointer).getRawData();
    }

    @Override
    public LocalDate getLocalDate(String column, Supplier<LocalDate> defaultSupplier) throws ParseException {
        return currentRecord.getLocalDate(column, defaultSupplier);
    }

    @Override
    public LocalDate getLocalDate(String column) throws ParseException {
        return currentRecord.getLocalDate(column);
    }

    @Override
    public LocalDate getLocalDate(String column, String dateFormat, Supplier<LocalDate> defaultSupplier) throws ParseException {
        return currentRecord.getLocalDate(column, dateFormat, defaultSupplier);
    }

    @Override
    public LocalDate getLocalDate(String column, String dateFormat) throws ParseException {
        return currentRecord.getLocalDate(column, dateFormat);
    }
}
