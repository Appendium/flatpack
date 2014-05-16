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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import net.sf.flatpack.ordering.OrderBy;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.FPException;
import net.sf.flatpack.util.FPInvalidUsageException;
import net.sf.flatpack.util.FPStringUtils;
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

    private Parser parser;

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
    public String[] getColumns() {
        String[] array = null;

        if (/* columnMD != null || */metaData != null) {
            final List<ColumnMetaData> cmds = metaData.getColumnsNames();
            array = new String[cmds.size()];
            for (int i = 0; i < cmds.size(); i++) {
                ColumnMetaData column = cmds.get(i);
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
    public String[] getColumns(final String recordID) {
        String[] array = null;

        if (metaData != null) {
            final List<ColumnMetaData> cmds = ParserUtils.getColumnMetaData(recordID, metaData);
            array = new String[cmds.size()];
            for (int i = 0; i < cmds.size(); i++) {
                final ColumnMetaData column = (ColumnMetaData) cmds.get(i);
                array[i] = column.getColName();
            }
        }

        return array;
    }

    public Date getDate(final String column) throws ParseException {
        return currentRecord.getDate(column);
    }

    public Date getDate(final String column, final SimpleDateFormat sdf) throws ParseException {
        return currentRecord.getDate(column, sdf);
    }

    public double getDouble(final String column) {
        return currentRecord.getDouble(column);
    }

    public BigDecimal getBigDecimal(final String column) {
        return currentRecord.getBigDecimal(column);
    }

    public Object getObject(final String column, final Class<?> classToConvertTo) {
        return currentRecord.getObject(column, classToConvertTo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.DataSet#getErrorCount()
     */
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
    public List getErrors() {
        return errors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.DataSet#getIndex()
     */
    public int getIndex() {
        return pointer;
    }

    public int getInt(final String column) {
        return currentRecord.getInt(column);
    }

    public long getLong(final String column) {
        return currentRecord.getLong(column);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.DataSet#getRowCount()
     */
    public int getRowCount() {
        return rows.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.DataSet#getRowNo()
     */
    public int getRowNo() {
        return currentRecord.getRowNo();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.DataSet#getString(java.lang.String)
     */
    public String getString(final String column) {
        return currentRecord.getString(column);
    }

    public void setValue(final String column, final String value) {
        final Row row = (Row) rows.get(pointer);
        final int colIndex = ParserUtils.getColumnIndex(row.getMdkey(), metaData, column, parser.isColumnNamesCaseSensitive());

        row.setValue(colIndex, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.DataSet#goBottom()
     */
    public void goBottom() {
        pointer = rows.size() - 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.DataSet#goTop()
     */
    public void goTop() {
        pointer = -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.DataSet#isAnError(int)
     */
    public boolean isAnError(final int lineNo) {
        for (int i = 0; i < errors.size(); i++) {
            if (((DataError) errors.get(i)).getLineNo() == lineNo && ((DataError) errors.get(i)).getErrorLevel() > 1) {
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
    public boolean next() {
        if (pointer < rows.size() && pointer + 1 != rows.size()) {
            pointer++;
            currentRecord = new RowRecord((Row) rows.get(pointer), metaData, parser.isColumnNamesCaseSensitive(), pzConvertProps, strictNumericParse,
                    upperCase, lowerCase, parser.isNullEmptyStrings());
            return true;
        }
        currentRecord = null;
        return false;
    }

    public Record getRecord() {
        return currentRecord;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.DataSet#orderRows(net.sf.flatpack.ordering.OrderBy)
     */
    public void orderRows(final OrderBy ob) {
        // PZ try to handle other <records> by sending them to
        // the bottom of the sort
        // if (columnMD.size() > 1) {
        // throw new Exception("orderRows does not currently support ordering
        // with <RECORD> mappings");
        // }
        if (ob != null && rows != null) {
            // final List cmds = metaData.getColumnsNames();
            // final List cmds =
            // ParserUtils.getColumnMetaData(PZConstants.DETAIL_ID, columnMD);
            // ob.setColumnMD(cmds);
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
    public boolean previous() {
        if (pointer <= 0) {
            currentRecord = null;
            return false;
        }
        pointer--;
        currentRecord = new RowRecord((Row) rows.get(pointer), metaData, parser.isColumnNamesCaseSensitive(), pzConvertProps, strictNumericParse,
                upperCase, lowerCase, parser.isNullEmptyStrings());
        return true;
    }

    /**
     * Sets data in the DataSet to lowercase
     */
    public void setLowerCase() {
        upperCase = false;
        lowerCase = true;
    }

    /**
     * Sets data in the DataSet to uppercase
     */
    public void setUpperCase() {
        upperCase = true;
        lowerCase = false;
    }

    /**
     * Checks to see if the row has the given <RECORD> id
     *
     * @param recordID
     * @return boolean
     */
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
    public void absolute(final int localPointer) {
        if (localPointer < 0 || localPointer > rows.size() - 1) {
            throw new IndexOutOfBoundsException("INVALID POINTER LOCATION: " + localPointer);
        }

        pointer = localPointer;
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
    public void setStrictNumericParse(final boolean strictNumericParse) {
        this.strictNumericParse = strictNumericParse;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.DataSet#remove()
     */
    public void remove() {
        rows.remove(pointer);
        pointer--;
    }

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

    public void clearRows() {
        pointer = -1; // set the pointer back to -1 directly just in case this
                      // instance is a BuffReaderDataSet.
        rows.clear();
    }

    public void clearAll() {
        clearRows();
        clearErrors();
    }

    public void clearErrors() {
        errors.clear();
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(final MetaData metaData) {
        this.metaData = metaData;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("Errors:").append(errors.size()).append(NEW_LINE);
        buf.append("Rows:").append(rows.size()).append(NEW_LINE);
        buf.append("Position:").append(pointer).append(NEW_LINE);
        buf.append("Conversion Props:").append(pzConvertProps).append(NEW_LINE);
        buf.append("MetaData:").append(metaData).append(NEW_LINE);
        return buf.toString();
    }

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
    public boolean isRowEmpty() {
        if (!parser.isFlagEmptyRows()) {
            // flag empty rows needs to be set for this functionality
            // throw an exception
            throw new FPInvalidUsageException("Parser.isFlagEmptyRows(true) must be set before using isRowEmpty()");
        }

        if (pointer < 0) {
            throw new FPException("Cursor on invalid row..  Make sure next() is called and returns true");
        }

        return ((Row) rows.get(pointer)).isEmpty();
    }

    /**
     * @throws FPInvalidUsageException
     * @throws FPException
     *             if cursor is on an invalid row
     */
    public String getRawData() {
        if (!parser.isStoreRawDataToDataSet()) {
            // option needs to be set for this functionality
            // throw an exception
            throw new FPInvalidUsageException("Parser.isStoreRawDataToDataSet(true) must be set before using getRawData()");
        }

        if (pointer < 0) {
            throw new FPException("Cursor on invalid row.. Make sure next() is called and returns true");
        }

        return ((Row) rows.get(pointer)).getRawData();
    }
}
