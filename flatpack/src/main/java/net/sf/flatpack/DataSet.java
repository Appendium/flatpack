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

import java.util.List;
import java.util.Properties;

import net.sf.flatpack.ordering.OrderBy;

public interface DataSet extends Record, RecordDataSet {

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
     * Moves back to the previous record in the set return true if move was a
     * success, false if not
     *
     * @return boolean
     */
    boolean previous();

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
    boolean isAnError(int lineNo);

    /**
     * Orders the data by column(s) specified. This will reposition the cursor
     * to the top of the DataSet when executed. This is currently not supported
     * when specifying &lt;RECORD&gt; elements in the mapping. An exception will be
     * thrown if this situation occurs
     *
     * @param ob -
     *            OrderBy object
     * @see net.sf.flatpack.ordering.OrderBy
     * @see net.sf.flatpack.ordering.OrderColumn
     */
    void orderRows(OrderBy ob);

    /**
     * Sets data in the DataSet to lowercase
     */
    void setLowerCase();

    /**
     * Sets data in the DataSet to uppercase
     */
    void setUpperCase();

    /**
     * Sets the absolute position of the record pointer
     *
     * @param localPointer -
     *            int
     * @exception IndexOutOfBoundsException
     */
    void absolute(int localPointer);

    /**
     * Setting this to True will parse text as is and throw a
     * NumberFormatException. Setting to false, which is the default, will
     * remove any non numeric character from the field. The remaining numeric
     * chars's will be returned. If it is an empty string,or there are no
     * numeric chars, 0 will be returned for getInt() and getDouble()
     *
     * @param strictNumericParse
     *            The strictNumericParse to set.
     */
    void setStrictNumericParse(boolean strictNumericParse);

    /**
     * Sets the properties from the pzconvert.properties file.
     * This file specifies the PZConverter implementation to use
     * for a particular class
     *
     * @param props
     *          Property mapping for String to Object conversion
     */
    void setPZConvertProps(Properties props);

    /**
     * Changes the value of the given column only for the
     * given row which the pointer is currently sitting on.
     *
     * @param column
     *          Column name to set the value for
     * @param value
     *          Value to change the column to
     */
    void setValue(String column, String value);

    /**
     * Clears out the rows in memory from the last parse.
     *
     */
    void clearRows();

    /**
     * Clears out the parse errors from memory
     *
     */
    void clearErrors();

    /**
     * Clears both the errors and rows from memory
     *
     */
    void clearAll();
}