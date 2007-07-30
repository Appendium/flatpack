/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting PZFileReader.
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import net.sf.flatpack.ordering.OrderBy;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.FPStringUtils;
import net.sf.flatpack.util.ParserUtils;
import net.sf.flatpack.xml.MetaData;

/**
 * @author Benoit Xhenseval
 * @author Paul Zepernick
 * 
 */
public class DefaultDataSet implements DataSet {
    private final List rows = new ArrayList();

    private final List errors = new ArrayList();

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
     * @see net.sf.flatpack.IDataSet#getColumns()
     */
    public String[] getColumns() {
        ColumnMetaData column = null;
        String[] array = null;

        if (/*columnMD != null || */metaData != null) {
            final List cmds = metaData.getColumnsNames();// ParserUtils.getColumnMetaData(PZConstants.DETAIL_ID, columnMD);

            array = new String[cmds.size()];
            for (int i = 0; i < cmds.size(); i++) {
                column = (ColumnMetaData) cmds.get(i);
                array[i] = column.getColName();
            }
        }

        return array;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#getColumns(java.lang.String)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#getDate(java.lang.String)
     */
    public Date getDate(final String column) throws ParseException {
        return getDate(column, new SimpleDateFormat("yyyyMMdd"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#getDate(java.lang.String,
     *      java.text.SimpleDateFormat)
     */
    public Date getDate(final String column, final SimpleDateFormat sdf) throws ParseException {
        final String s = getStringValue(column);
        if (FPStringUtils.isBlank(s)) {
            //don't do the parse on empties
            return null;
        }
        return sdf.parse(s);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#getDouble(java.lang.String)
     */
    public double getDouble(final String column) {
        final StringBuffer newString = new StringBuffer();
        final String s = getStringValue(column);
        //        final String s = row.getValue(ParserUtils.getColumnIndex(row.getMdkey(), columnMD, column, pzparser));

        if (!strictNumericParse) {
            newString.append(ParserUtils.stripNonDoubleChars(s));
        } else {
            newString.append(s);
        }

        return Double.parseDouble(newString.toString());
    }

    private String getStringValue(final String column) {
        final Row row = (Row) rows.get(pointer);
        return row.getValue(ParserUtils.getColumnIndex(row.getMdkey(), metaData, column, parser));
    }

    public Object getObject(final String column, final Class classToConvertTo) {
        final String s = getStringValue(column);
        return ParserUtils.runPzConverter(pzConvertProps, s, classToConvertTo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#getErrorCount()
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
     * @see net.sf.flatpack.IDataSet#getErrors()
     */
    public List getErrors() {
        return errors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#getIndex()
     */
    public int getIndex() {
        return pointer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#getInt(java.lang.String)
     */
    public int getInt(final String column) {
        final String s = getStringValue(column);

        if (!strictNumericParse) {
            return Integer.parseInt(ParserUtils.stripNonLongChars(s));
        }

        return Integer.parseInt(s);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#getRowCount()
     */
    public int getRowCount() {
        return rows.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#getRowNo()
     */
    public int getRowNo() {
        return ((Row) rows.get(pointer)).getRowNumber();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#getString(java.lang.String)
     */
    public String getString(final String column) {
        final String s = getStringValue(column);

        if (parser.isNullEmptyStrings() && FPStringUtils.isBlank(s)) {
            return null;
        }

        if (upperCase) {
            // convert data to uppercase before returning
            // return row.getValue(ParserUtils.findColumn(column,
            // cmds)).toUpperCase(Locale.getDefault());
            return s.toUpperCase(Locale.getDefault());
        }

        if (lowerCase) {
            // convert data to lowercase before returning
            // return row.getValue(ParserUtils.findColumn(column,
            // cmds)).toLowerCase(Locale.getDefault());
            return s.toLowerCase(Locale.getDefault());
        }

        // return value as how it is in the file
        return s;
    }

    public void setValue(final String column, final String value) {
        final Row row = (Row) rows.get(pointer);
        final int colIndex = ParserUtils.getColumnIndex(row.getMdkey(), metaData, column, parser);

        row.setValue(colIndex, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#goBottom()
     */
    public void goBottom() {
        pointer = rows.size() - 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#goTop()
     */
    public void goTop() {
        pointer = -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#isAnError(int)
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
     * @see net.sf.flatpack.IDataSet#next()
     */
    public boolean next() {
        if (pointer < rows.size() && pointer + 1 != rows.size()) {
            pointer++;
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#orderRows(net.sf.flatpack.ordering.OrderBy)
     */
    public void orderRows(final OrderBy ob) throws Exception {
        // PZ try to handle other <records> by sending them to
        // the bottom of the sort
        // if (columnMD.size() > 1) {
        // throw new Exception("orderRows does not currently support ordering
        // with <RECORD> mappings");
        // }
        if (ob != null && rows != null) {
            final List cmds = metaData.getColumnsNames();
            //            final List cmds = ParserUtils.getColumnMetaData(PZConstants.DETAIL_ID, columnMD);
            ob.setColumnMD(cmds);
            Collections.sort(rows, ob);
            goTop();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.flatpack.IDataSet#previous()
     */
    public boolean previous() {
        if (pointer <= 0) {
            return false;
        }
        pointer--;
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
        String rowID = ((Row) rows.get(pointer)).getMdkey();
        if (rowID == null) {
            rowID = FPConstants.DETAIL_ID;
        }

        return rowID.equals(recordID);
    }

    /**
     * Sets the absolute position of the record pointer
     * 
     * @param localPointer -
     *            int
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
     * @see net.sf.flatpack.IDataSet#remove()
     */
    public void remove() {
        rows.remove(pointer);
        pointer--;
    }

    public void setPZConvertProps(final Properties props) {
        this.pzConvertProps = props;
    }

    /**
     * @param pointer the pointer to set
     */
    protected void setPointer(final int pointer) {
        this.pointer = pointer;
    }


    public void clearRows() {
        pointer = -1; //set the pointer back to -1 directly just in case this instance is a BuffReaderDataSet.
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
        final StringBuffer buf = new StringBuffer();
        buf.append("Errors:").append(errors.size()).append(System.getProperty("line.separator"));
        buf.append("Rows:").append(rows.size()).append(System.getProperty("line.separator"));
        buf.append("Position:").append(pointer).append(System.getProperty("line.separator"));
        buf.append("Conversion Props:").append(pzConvertProps).append(System.getProperty("line.separator"));
        buf.append("MetaData:").append(metaData).append(System.getProperty("line.separator"));
        return buf.toString();
    }
}
