package net.sf.flatpack;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

/**
 * @since 3.4
 */
public interface Record {
	/**
	 * Returns the string value of a specified column
	 *
	 * @param column
	 *            - Name of the column
	 * @exception NoSuchElementException
	 * @return String
	 */
	String getString(String column);

	/**
	 * Returns the double value of a specified column
	 *
	 * @param column
	 *            - Name of the column
	 * @exception NoSuchElementException
	 * @exception NumberFormatException
	 * @return double
	 */
	double getDouble(String column);

	/**
	 * Returns the BigDecimal value of a specified column
	 *
	 * @param column
	 *            - Name of the column
	 * @exception NoSuchElementException
	 * @exception NumberFormatException
	 * @return BigDecimal
	 */
	BigDecimal getBigDecimal(String column);

	/**
	 * Returns the interger value of a specified column
	 *
	 * @param column
	 *            - Name of the column
	 * @exception NoSuchElementException
	 * @exception NumberFormatException
	 * @return double
	 */
	int getInt(String column);

	/**
	 * Returns the long value of a specified column
	 *
	 * @param column
	 *            - Name of the column
	 * @exception NoSuchElementException
	 * @exception NumberFormatException
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
	 * @exception ParseException
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
	 * @exception ParseException
	 * @see java.text.SimpleDateFormat
	 * @return Date
	 */
	Date getDate(String column, SimpleDateFormat sdf) throws ParseException;

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
     * Does this DataSet contain a column with the given name?
     * 
     * @param column
     *          Column name to check for
     * @return boolean
     */
    boolean contains(String column);
    
    /**
     * Checks to see if there was no data on the row which was parsed.  This
     * will thrown an exception if Parser.FlagEmptyRows() is not set to true.
     * 
     * @return
     */
    boolean isRowEmpty();
    
    /**
     * 
     * @return the raw data used to create this Row in the DataSet.  Parser.setStoreRawDataToDataSet(true)
     * must be specified before calling this method.  
     */
    String getRawData();
}
