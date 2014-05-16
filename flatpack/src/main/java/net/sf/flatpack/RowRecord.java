package net.sf.flatpack;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.FPStringUtils;
import net.sf.flatpack.util.ParserUtils;
import net.sf.flatpack.xml.MetaData;

public class RowRecord implements Record {
	private Row row;
	private boolean columnCaseSensitive;
	private MetaData metaData;
	private Properties pzConvertProps = null;
	private boolean strictNumericParse;
	private boolean upperCase;
	private boolean lowerCase;
	private boolean nullEmptyString;

	public RowRecord(Row row, MetaData metaData, boolean columnCaseSensitive,
			Properties pzConvertProps, boolean strictNumericParse,
			boolean upperCase, boolean lowerCase, boolean nullEmptyString) {
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

	public boolean isRecordID(final String recordID) {
		String rowID = row.getMdkey();
		if (rowID == null) {
			rowID = FPConstants.DETAIL_ID;
		}

		return rowID.equals(recordID);
	}

	public int getRowNo() {
		return row.getRowNumber();
	}

	public boolean isRowEmpty() {
		return row.isEmpty();
	}

	public boolean contains(final String column) {
		final Iterator<ColumnMetaData> cmds = ParserUtils.getColumnMetaData(
				row.getMdkey(), metaData).iterator();
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
	public String[] getColumns() {
		ColumnMetaData column = null;
		String[] array = null;

		if (/* columnMD != null || */metaData != null) {
			final List cmds = metaData.getColumnsNames();// ParserUtils.getColumnMetaData(PZConstants.DETAIL_ID,
															// columnMD);

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
	 * @see net.sf.flatpack.DataSet#getColumns(java.lang.String)
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

	public Date getDate(final String column) throws ParseException {
		return getDate(column, new SimpleDateFormat("yyyyMMdd"));
	}

	public Date getDate(final String column, final SimpleDateFormat sdf)
			throws ParseException {
		final String s = getStringValue(column);
		if (FPStringUtils.isBlank(s)) {
			// don't do the parse on empties
			return null;
		}
		return sdf.parse(s);
	}

	public double getDouble(final String column) {
		final StringBuffer newString = new StringBuffer();
		final String s = getStringValue(column);

		if (!strictNumericParse) {
			newString.append(ParserUtils.stripNonDoubleChars(s));
		} else {
			newString.append(s);
		}

		return Double.parseDouble(newString.toString());
	}

	public int getInt(final String column) {
		final String s = getStringValue(column);

		if (!strictNumericParse) {
			return Integer.parseInt(ParserUtils.stripNonLongChars(s));
		}

		return Integer.parseInt(s);
	}

	public long getLong(final String column) {
		final String s = getStringValue(column);

		if (!strictNumericParse) {
			return Long.parseLong(ParserUtils.stripNonLongChars(s));
		}

		return Long.parseLong(s);
	}

	private String getStringValue(final String column) {
		return row.getValue(ParserUtils.getColumnIndex(row.getMdkey(),
				metaData, column, columnCaseSensitive));
	}

	public Object getObject(final String column, final Class classToConvertTo) {
		final String s = getStringValue(column);
		return ParserUtils.runPzConverter(pzConvertProps, s, classToConvertTo);
	}

	public BigDecimal getBigDecimal(final String column) {
		final String s = getStringValue(column);

		return new BigDecimal(s);
	}

	public String getString(final String column) {
		String s = getStringValue(column);

		if (nullEmptyString && FPStringUtils.isBlank(s)) {
			s = null;
		} else if (upperCase) {
			// convert data to uppercase before returning
			// return row.getValue(ParserUtils.findColumn(column,
			// cmds)).toUpperCase(Locale.getDefault());
			s = s.toUpperCase(Locale.getDefault());
		} else if (lowerCase) {
			// convert data to lowercase before returning
			// return row.getValue(ParserUtils.findColumn(column,
			// cmds)).toLowerCase(Locale.getDefault());
			s = s.toLowerCase(Locale.getDefault());
		}

		// return value as how it is in the file
		return s;
	}

	public String getRawData() {
		return row.getRawData();
	}

}
