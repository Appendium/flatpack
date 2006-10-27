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
package net.sf.pzfilereader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.pzfilereader.ordering.OrderBy;
import net.sf.pzfilereader.structure.ColumnMetaData;
import net.sf.pzfilereader.structure.Row;
import net.sf.pzfilereader.util.PZConstants;
import net.sf.pzfilereader.util.ParserUtils;

/**
 * @author xhensevb
 * 
 */
public class DefaultDataSet implements IDataSet {
    private final List rows = new ArrayList();

    private final List errors = new ArrayList();

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

    private Map columnMD;

    public DefaultDataSet(final Map columnMD2) {
        this.columnMD = columnMD2;
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
     * @see net.sf.pzfilereader.IDataSet#getColumns()
     */
    public String[] getColumns() {
        ColumnMetaData column = null;
        String[] array = null;

        if (columnMD != null) {
            final List cmds = ParserUtils.getColumnMetaData(PZConstants.DETAIL_ID, columnMD);

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
     * @see net.sf.pzfilereader.IDataSet#getColumns(java.lang.String)
     */
    public String[] getColumns(final String recordID) {
        String[] array = null;

        if (columnMD != null) {
            final List cmds = ParserUtils.getColumnMetaData(recordID, columnMD);
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
     * @see net.sf.pzfilereader.IDataSet#getDate(java.lang.String)
     */
    public Date getDate(final String column) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final Row row = (Row) rows.get(pointer);
        final String s = row.getValue(ParserUtils.getColumnIndex(row.getMdkey(), columnMD, column));
        return sdf.parse(s);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#getDate(java.lang.String,
     *      java.text.SimpleDateFormat)
     */
    public Date getDate(final String column, final SimpleDateFormat sdf) throws ParseException {
        final Row row = (Row) rows.get(pointer);
        final String s = row.getValue(ParserUtils.getColumnIndex(row.getMdkey(), columnMD, column));
        return sdf.parse(s);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#getDouble(java.lang.String)
     */
    public double getDouble(final String column) {
        final StringBuffer newString = new StringBuffer();
        final Row row = (Row) rows.get(pointer);

        final String s = row.getValue(ParserUtils.getColumnIndex(row.getMdkey(), columnMD, column));

        if (!strictNumericParse) {
            if (s.trim().length() == 0) {
                return 0;
            }
            for (int i = 0; i < s.length(); i++) {
                final char c = s.charAt(i);
                if (c >= '0' && c <= '9' || c == '.' || c == '-') {
                    newString.append(c);
                }
            }
            if (newString.length() == 0 || (newString.length() == 1 && newString.toString().equals("."))
                    || (newString.length() == 1 && newString.toString().equals("-"))) {
                newString.append("0");
            }
        } else {
            newString.append(s);
        }

        return Double.parseDouble(newString.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#getErrorCount()
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
     * @see net.sf.pzfilereader.IDataSet#getErrors()
     */
    public List getErrors() {
        return errors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#getIndex()
     */
    public int getIndex() {
        return pointer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#getInt(java.lang.String)
     */
    public int getInt(final String column) {
        final StringBuffer newString = new StringBuffer();
        final Row row = (Row) rows.get(pointer);
        final String s = row.getValue(ParserUtils.getColumnIndex(row.getMdkey(), columnMD, column));

        if (!strictNumericParse) {
            if (s.trim().length() == 0) {
                return 0;
            }
            for (int i = 0; i < s.length(); i++) {
                final char c = s.charAt(i);
                if (c >= '0' && c <= '9' || c == '-') {
                    newString.append(c);
                }
            }
            // check to make sure we do not have a single length string with
            // just a minus sign
            if (newString.length() == 0 || (newString.length() == 1 && newString.toString().equals("-"))) {
                newString.append("0");
            }
        } else {
            newString.append(s);
        }

        return Integer.parseInt(newString.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#getRowCount()
     */
    public int getRowCount() {
        return rows.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#getRowNo()
     */
    public int getRowNo() {
        return ((Row) rows.get(pointer)).getRowNumber();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#getRows()
     */
    public List getRows() {
        return rows;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#getString(java.lang.String)
     */
    public String getString(final String column) {
        final Row row = (Row) rows.get(pointer);
        final String s = row.getValue(ParserUtils.getColumnIndex(row.getMdkey(), columnMD, column));

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

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#goBottom()
     */
    public void goBottom() {
        pointer = rows.size() - 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#goTop()
     */
    public void goTop() {
        pointer = -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#isAnError(int)
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
     * @see net.sf.pzfilereader.IDataSet#next()
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
     * @see net.sf.pzfilereader.IDataSet#orderRows(net.sf.pzfilereader.ordering.OrderBy)
     */
    public void orderRows(final OrderBy ob) throws Exception {
        // PZ try to handle other <records> by sending them to
        // the bottom of the sort
        // if (columnMD.size() > 1) {
        // throw new Exception("orderRows does not currently support ordering
        // with <RECORD> mappings");
        // }
        if (ob != null && rows != null) {
            final List cmds = ParserUtils.getColumnMetaData(PZConstants.DETAIL_ID, columnMD);
            ob.setColumnMD(cmds);
            Collections.sort(rows, ob);
            goTop();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.IDataSet#previous()
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
            rowID = PZConstants.DETAIL_ID;
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
     * @see net.sf.pzfilereader.IDataSet#remove()
     */
    public void remove() {
        rows.remove(pointer);
        pointer--;
    }

    void setColumnMD(final Map columnMD) {
        this.columnMD = columnMD;
    }

}
