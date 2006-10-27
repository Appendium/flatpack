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
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.pzfilereader.ordering.OrderBy;

public interface IDataSet {

    /**
     * Goes to the top of the data set. This will put the pointer one record
     * before the first in the set. Next() will have to be called to get the
     * first record after this call.
     */
    void goTop();

    /**
     * Goes to the last record in the dataset
     */
    void goBottom();

    /**
     * Moves to the next record in the set. Returns true if move was a success,
     * false if not
     * 
     * @return boolean
     */
    boolean next();

    /**
     * Moves back to the previous record in the set return true if move was a
     * success, false if not
     * 
     * @return boolean
     */
    boolean previous();

    /**
     * Returns the string value of a specified column
     * 
     * @param column -
     *            Name of the column
     * @exception NoSuchElementException
     * @return String
     */
    String getString(final String column);

    /**
     * Returns the double value of a specified column
     * 
     * @param column -
     *            Name of the column
     * @exception NoSuchElementException
     * @exception NumberFormatException
     * @return double
     */
    double getDouble(final String column);

    /**
     * Returns the interger value of a specified column
     * 
     * @param column -
     *            Name of the column
     * @exception NoSuchElementException
     * @exception NumberFormatException
     * @return double
     */
    int getInt(final String column);

    /**
     * Returns the date value of a specified column. This assumes the date is in
     * yyyyMMdd. If your date is not in this format, see
     * getDate(String,SimpleDateFormat)
     * 
     * @param column -
     *            Name of the column
     * @exception ParseException
     * @return Date
     */
    Date getDate(final String column) throws ParseException;

    /**
     * Returns the date value of a specified column. This should be used if the
     * date is NOT in yyyyMMdd format. The SimpleDateFormat object will specify
     * what kind of format the date is in.
     * 
     * @param column -
     *            Name of the column
     * @param sdf -
     *            SimpleDateFormat of the date
     * @exception ParseException
     * @see java.text.SimpleDateFormat
     * @return Date
     */
    Date getDate(final String column, final SimpleDateFormat sdf) throws ParseException;

    /**
     * Returns a String array of column names in the DataSet. This will assume
     * 'detail' <RECORD> ID.
     * 
     * @return String[]
     */
    String[] getColumns();

    /**
     * Returns a String array of column names in the DataSet for a given
     * <RECORD> id
     * 
     * @param recordID
     * @return String[]
     */
    String[] getColumns(final String recordID);

    /**
     * Returns the line number the pointer is on. These are the actual line
     * numbers from the flat file, before any sorting.
     * 
     * @exception NoSuchElementException
     * @exception NumberFormatException
     * @return int
     */
    int getRowNo();

    /**
     * Returns A Collection Of DataErrors that happened during processing
     * 
     * @return Vector
     */
    List getErrors();

    /**
     * Removes a row from the dataset. Once the row is removed the pointer will
     * be sitting on the record previous to the deleted row.
     */
    void remove();

    /**
     * Returns the index the pointer is on for the array
     * 
     * @return int
     */
    int getIndex();

    /**
     * Returns the total number of rows parsed in from the file
     * 
     * 
     * @return int - Row Count
     */
    int getRowCount();

    /**
     * Returns total number of records which contained a parse error in the
     * file.
     * 
     * @return int - Record Error Count
     */
    int getErrorCount();

    /**
     * Returns true or false as to whether or not the line number contains an
     * error. The import will skip the line if it contains an error and it will
     * not be processed
     * 
     * @param lineNo -
     *            int line number
     * @return boolean
     */
    boolean isAnError(final int lineNo);

    /**
     * Orders the data by column(s) specified. This will reposition the cursor
     * to the top of the DataSet when executed. This is currently not supported
     * when specying <RECORD> elements in the mapping. An exception will be
     * thrown if this situation occurs
     * 
     * @param ob -
     *            OrderBy object
     * @exception Exception
     * @see net.sf.pzfilereader.ordering.OrderBy
     * @see net.sf.pzfilereader.ordering.OrderColumn
     */
    void orderRows(final OrderBy ob) throws Exception;

    List getRows();

    /**
     * Sets data in the DataSet to lowercase
     */
    void setLowerCase();

    /**
     * Sets data in the DataSet to uppercase
     */
    void setUpperCase();

    /**
     * Checks to see if the row has the given <RECORD> id
     * 
     * @param recordID
     * @return boolean
     */
    boolean isRecordID(final String recordID);

    /**
     * Sets the absolute position of the record pointer
     * 
     * @param localPointer -
     *            int
     * @exception IndexOutOfBoundsException
     */
    void absolute(final int localPointer);

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
    void setStrictNumericParse(final boolean strictNumericParse);
}