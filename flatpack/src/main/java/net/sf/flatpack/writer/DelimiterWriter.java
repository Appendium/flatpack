package net.sf.flatpack.writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FPConstants;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 * @since 4.0 public methods are more fluent
 */
public class DelimiterWriter extends AbstractWriter {
	private char delimiter;
	private char qualifier;
	private List<String> columnTitles = null;
	private boolean columnTitlesWritten = false;

	protected DelimiterWriter(final Map columnMapping,
			final java.io.Writer output, final char delimiter,
			final char qualifier, final WriterOptions options)
			throws IOException {
		super(output);
		this.delimiter = delimiter;
		this.qualifier = qualifier;

		columnTitles = new ArrayList<String>();
		final List<ColumnMetaData> columns = (List<ColumnMetaData>) columnMapping
				.get(FPConstants.DETAIL_ID);
		for (ColumnMetaData cmd : columns) {
			columnTitles.add(cmd.getColName());
		}
		// write the column headers
		if (options.isAutoPrintHeader()) {
			printHeader();
		}
	}

	protected void writeWithDelimiter(final Object value) throws IOException {
		this.write(value);
		this.write(delimiter);
	}

	protected void write(final Object value) throws IOException {
		String stringValue = "";

		if (value != null) {
			// TODO DO: format the value
			if (value instanceof BigDecimal) {
				final BigDecimal bd = (BigDecimal) value;
				stringValue = bd.signum() == 0 ? "0" : bd.toPlainString();
			} else {
				stringValue = value.toString();
			}
		}

		final boolean needsQuoting = stringValue.indexOf(delimiter) != -1
				|| (qualifier != FPConstants.NO_QUALIFIER && stringValue
						.indexOf(qualifier) != -1);

		if (needsQuoting) {
			super.write(qualifier);
		}

		super.write(stringValue);

		if (needsQuoting) {
			super.write(qualifier);
		}
	}

	protected void addColumnTitle(final String string) {
		if (string == null) {
			throw new IllegalArgumentException("column title may not be null");
		}
		columnTitles.add(string);
	}

	protected void writeColumnTitles() throws IOException {
		final Iterator<String> titleIter = columnTitles.iterator();
		while (titleIter.hasNext()) {
			final String title = titleIter.next();

			if (titleIter.hasNext()) {
				this.writeWithDelimiter(title);
			} else {
				this.write(title);
			}
		}
	}

	protected void writeRow() throws IOException {
		final Iterator<String> titlesIter = columnTitles.iterator();
		while (titlesIter.hasNext()) {
			final String columnTitle = titlesIter.next();
			if (getRowMap() != null) {
				if (titlesIter.hasNext()) {
					writeWithDelimiter(getRowMap().get(columnTitle));
				} else {
					write(getRowMap().get(columnTitle));
				}
			}
		}
	}

	public final Writer nextRecord() throws IOException {
		// if (!columnTitlesWritten) {
		// this.writeColumnTitles();
		// columnTitlesWritten = true;
		// } else {
		this.writeRow();
		// }

		return super.nextRecord();
	}

	public Writer printFooter() throws IOException {
        // TODO DO: implement footer handling
    	return this;
    }

	public Writer printHeader() throws IOException {
		if (!columnTitlesWritten) {
			this.writeColumnTitles();
			columnTitlesWritten = true;
			return super.nextRecord();
		}
		return this;
	}

	protected boolean validateColumnTitle(final String columnTitle) {
		return columnTitles.contains(columnTitle);
	}
}
