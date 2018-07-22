package net.sf.flatpack;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * @since 3.4
 */
public interface Record {
    /**
     * Returns the string value of a specified column
     *
     * @param column
     *            - Name of the column
     * @return String
     */
    String getString(String column);

    /**
     * Returns the string value of a specified column
     *
     * @param column
     *            - Name of the column
     * @param defaultSupplier for default value if result in column is null/empty
     * @exception NoSuchElementException if col does not exist
     * @return String
     * @since 4.0
     */
    String getString(String column, Supplier<String> defaultSupplier);

    /**
     * Returns the double value of a specified column
     *
     * @param column
     *            - Name of the column
     * @param defaultSupplier for default value if result in column is null/empty
     * @exception NoSuchElementException if no such record
     * @exception NumberFormatException if wrong number format
     * @return double
     * @since 4.0
     */
    double getDouble(String column, Supplier<Double> defaultSupplier);

    /**
     * Returns the double value of a specified column
     *
     * @param column
     *            - Name of the column
     * @exception NoSuchElementException if no such record
     * @exception NumberFormatException if wrong number format
     * @return double
     */
    double getDouble(String column);

    /**
     * Returns the BigDecimal value of a specified column
     *
     * @param column
     *            - Name of the column
     * @param defaultSupplier for default value if result in column is null/empty
     * @exception NoSuchElementException if no such record
     * @exception NumberFormatException if wrong number format
     * @return BigDecimal
     * @since 4.0
     */
    BigDecimal getBigDecimal(String column, Supplier<BigDecimal> defaultSupplier);

    /**
     * Returns the BigDecimal value of a specified column
     *
     * @param column
     *            - Name of the column
     * @exception NoSuchElementException if no such record
     * @exception NumberFormatException if wrong number format
     * @return BigDecimal
     */
    BigDecimal getBigDecimal(String column);

    /**
     * Returns the integer value of a specified column
     *
     * @param column
     *            - Name of the column
     * @param defaultSupplier for default value if result in column is null/empty
     * @exception NoSuchElementException if no such record
     * @exception NumberFormatException if wrong number format
     * @return double
     * @since 4.0
     */
    int getInt(String column, Supplier<Integer> defaultSupplier);

    /**
     * Returns the integer value of a specified column
     *
     * @param column
     *            - Name of the column
     * @exception NoSuchElementException if no such record
     * @exception NumberFormatException if wrong number format
     * @return double
     */
    int getInt(String column);

    /**
     * Returns the long value of a specified column
     *
     * @param column
     *            - Name of the column
     * @param defaultSupplier for default value if result in column is null/empty
     * @exception NoSuchElementException if no such record
     * @exception NumberFormatException if wrong number format
     * @return long
     * @since 4.0
     */
    long getLong(String column, Supplier<Long> defaultSupplier);

    /**
     * Returns the long value of a specified column
     *
     * @param column
     *            - Name of the column
     * @exception NoSuchElementException if no such record
     * @exception NumberFormatException if wrong number format
     * @return long
     */
    long getLong(String column);

    /**
     * Returns the date value of a specified column. This assumes the date is in
     * yyyyMMdd. If your date is not in this format, see
     * getDate(String,SimpleDateFormat)
     *
     * Will return "null" on empty Strings
     *
     * @param column
     *            - Name of the column
     * @param defaultSupplier for default value if result in column is null/empty
     * @exception ParseException if date format incorect
     * @return Date
     * @since 4.0
     */
    Date getDate(String column, Supplier<Date> defaultSupplier) throws ParseException;

    /**
     * Returns the date value of a specified column. This assumes the date is in
     * yyyyMMdd. If your date is not in this format, see
     * getDate(String,SimpleDateFormat)
     *
     * Will return "null" on empty Strings
     *
     * @param column
     *            - Name of the column
     * @exception ParseException if date format incorrect
     * @return Date
     */
    Date getDate(String column) throws ParseException;

    /**
     * Returns the date value of a specified column. This should be used if the
     * date is NOT in yyyyMMdd format. The SimpleDateFormat object will specify
     * what kind of format the date is in.
     *
     * Will return "null" on empty Strings
     *
     * @param column
     *            - Name of the column
     * @param sdf
     *            - SimpleDateFormat of the date
     * @param defaultSupplier for default value if result in column is null/empty
     * @exception ParseException
     * @see java.text.SimpleDateFormat
     * @return Date
     * @since 4.0
     */
    Date getDate(String column, SimpleDateFormat sdf, Supplier<Date> defaultSupplier) throws ParseException;

    /**
     * Returns the date value of a specified column. This should be used if the
     * date is NOT in yyyyMMdd format. The SimpleDateFormat object will specify
     * what kind of format the date is in.
     *
     * Will return "null" on empty Strings
     *
     * @param column
     *            - Name of the column
     * @param sdf
     *            - SimpleDateFormat of the date
     * @exception ParseException if date format does not match the SimpleDateFormat
     * @see java.text.SimpleDateFormat
     * @return Date
     */
    Date getDate(String column, SimpleDateFormat sdf) throws ParseException;

    /**
     * Returns the date value of a specified column. This assumes the date is in
     * yyy-mm-dd. If your date is not in this format, see
     * getDate(String,SimpleDateFormat)
     *
     * Will return "null" on empty Strings
     *
     * @param column
     *            - Name of the column
     * @param defaultSupplier for default value if result in column is null/empty
     * @exception ParseException if date format does not match
     * @return Date
     * @since 4.0
     */
    LocalDate getLocalDate(String column, Supplier<LocalDate> defaultSupplier) throws ParseException;

    /**
     * Returns the local date value of a specified column. This assumes the date is in
     * yyyy-MM-dd. If your date is not in this format, see
     * getLocalDate(String,SimpleDateFormat)
     *
     * Will return "null" on empty Strings
     *
     * @param column
     *            - Name of the column
     * @exception ParseException if date format does not match
     * @return Date
     */
    LocalDate getLocalDate(String column) throws ParseException;

    /**
     * Returns the lcoal date value of a specified column. This should be used if the
     * date is NOT in yyyyMMdd format. The SimpleDateFormat object will specify
     * what kind of format the date is in.
     *
     * Will return "null" on empty Strings
     *
     * @param column
     *            - Name of the column
     * @param dateFormat
     *            - dateFormat of the date
     * @param defaultSupplier for default value if result in column is null/empty
     * @exception ParseException if date format does not match
     * @see java.text.SimpleDateFormat
     * @return Date
     * @since 4.0
     */
    LocalDate getLocalDate(String column, String dateFormat, Supplier<LocalDate> defaultSupplier) throws ParseException;

    /**
     * Returns the lcoal date value of a specified column. This should be used if the
     * date is NOT in yyy-mm-dd format. The SimpleDateFormat object will specify
     * what kind of format the date is in.
     *
     * Will return "null" on empty Strings
     *
     * @param column
     *            - Name of the column
     * @param dateFormat
     *            - dateFormat of the date
     * @exception ParseException if date format does not match
     * @see java.text.SimpleDateFormat
     * @return Date
     */
    LocalDate getLocalDate(String column, String dateFormat) throws ParseException;

    /**
     * Returns the value of the column with the type of object specified
     *
     * @param column
     *            Name of the column
     * @param classToConvertTo
     *            Class type to convert to
     * @return Object Value of the column in the specified object
     */
    Object getObject(String column, Class<?> classToConvertTo);

    /**
     * Returns a String array of column names in the DataSet. This will assume
     * 'detail' &lt;RECORD&gt; ID.
     *
     * @return String[]
     */
    String[] getColumns();

    /**
     * Returns a String array of column names in the DataSet for a given
     *  &lt;RECORD&gt; id
     *
     * @param recordID
     * @return String[]
     */
    String[] getColumns(String recordID);

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
     * Checks to see if the row has the given  &lt;RECORD&gt; id
     *
     * @param recordID
     * @return boolean
     */
    boolean isRecordID(String recordID);

    /**
     * Show the record ID (default is 'detail')
     * @return the record ID (default is 'detail')
     */
    String getRecordID();

    /**
     * Does this DataSet contain a column with the given name?
     *
     * @param column
     *          Column name to check for
     * @return boolean true if the row contain that column name.
     */
    boolean contains(String column);

    /**
     * Checks to see if there was no data on the row which was parsed.  This
     * will thrown an exception if Parser.FlagEmptyRows() is not set to true.
     *
     * @return true if the row has no data
     */
    boolean isRowEmpty();

    /**
     *
     * @return the raw data used to create this Row in the DataSet.  Parser.setStoreRawDataToDataSet(true)
     * must be specified before calling this method.
     */
    String getRawData();
}
