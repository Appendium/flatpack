package net.sf.flatpack.writer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.xml.XMLRecordElement;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public class DelimiterWriter extends AbstractWriter {
	private char delimiter;
	
	private char qualifier;
	
	//private List columnTitles = null;
	
	private boolean columnTitlesWritten = false;
	
	private Map columnMapping;
	
	private DelimiterWriterOptions writerOptions;
	
	//this is used when using addRecordEntry() and no column names
	private boolean delimitNextEntry = false;

	protected DelimiterWriter(final Map columnMapping,
			final java.io.Writer output, final char delimiter,
			final char qualifier) throws IOException {
		
		this(columnMapping, output, delimiter, qualifier, new DelimiterWriterOptions());
	}
	
	protected DelimiterWriter(final Map columnMapping,
			final java.io.Writer output, final char delimiter,
			final char qualifier, DelimiterWriterOptions writerOptions) throws IOException {
		super(output);
		this.delimiter = delimiter;
		this.qualifier = qualifier;

//		columnTitles = new ArrayList();
//		final List columns = (List) columnMapping.get(FPConstants.DETAIL_ID);
//		final Iterator columnIter = columns.iterator();
//		while (columnIter.hasNext()) {
//			final ColumnMetaData element = (ColumnMetaData) columnIter.next();
//			columnTitles.add(element.getColName());
//		}
		this.columnMapping = columnMapping;
		this.writerOptions = writerOptions;
		if (!writerOptions.isNoColumnMappings()) {
			// write the column headers
			this.nextRecord();
		} else {
			//flag the headers as written so that we don't try to write them when calling nextRecord()
			columnTitlesWritten = true;
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

		final boolean needsQuoting = stringValue.indexOf(delimiter) != -1 || (qualifier != FPConstants.NO_QUALIFIER && stringValue.indexOf(qualifier) != -1);

		if (needsQuoting) {
			super.write(qualifier);
		}

		super.write(stringValue);

		if (needsQuoting) {
			super.write(qualifier);
		}
	}
	
	public void addRecordEntry(String columnName, Object value) {
		if (writerOptions.isNoColumnMappings()) {
			throw new UnsupportedOperationException("Column Names Cannot Be Bound To Values When Column Mappings Have Been Turned Off...");
		}
		super.addRecordEntry(columnName, value);
	}
	
	/**
	 * Adds a record entry when not using column names in the file
	 * 
	 * @param value
	 * @throws IOException
	 */
	public void addRecordEntry(Object value) throws IOException{
		if (!writerOptions.isNoColumnMappings()) {
			throw new UnsupportedOperationException("Must use addRecordEntry(String,Object) When Using Column Names...");
		}
		
		if (delimitNextEntry) {
			//need to use the super write here.  The write in this class will try and qualify the delimiter.  
			//we also cannot use the writeWithDelimiter() here.  It puts the delimiter after the value.  We don't 
			//know the last column to be written so we cannot utilize that here as we did with the mappings
			super.write(this.delimiter);
			
		}
		
		this.write(value.toString());		
		delimitNextEntry = true;
	}

	//TODO find out if these are needed since the titles can already be added from the Factory
//	protected void addColumnTitle(final String string) {
//		addColumnTitle(string, FPConstants.DETAIL_ID);
//	}
//	
//	protected void addColumnTitle(final String string, final String recordId) {
//		if (string == null) {
//			throw new IllegalArgumentException("column title may not be null");
//		}
//		List cols = null;
//		if (FPConstants.DETAIL_ID.equals(recordId)) {
//			//the key for the detail key contains a List in the Map
//			cols = (List)this.columnMapping.get(FPConstants.DETAIL_ID);
//			if (cols == null) {
//				cols = new ArrayList();
//				this.columnMapping.put(FPConstants.DETAIL_ID, cols);
//			}
//			
//		} else {
//			//the map contains an XMLRecord element and then the list is contained under that
//			XMLRecordElement xmlRec = (XMLRecordElement)this.columnMapping.get(recordId);
//			if (xmlRec == null) {
//				cols = new ArrayList();
//				xmlRec = new XMLRecordElement();
//				xmlRec.setColumns(cols, null);
//				this.columnMapping.put(recordId, xmlRec);
//			} else {
//				cols = xmlRec.getColumns();
//			}
//		}
//		
//		cols.add(string);
//	}

	protected void writeColumnTitles() throws IOException {
		final Iterator titleIter = ((List)this.columnMapping.get(FPConstants.DETAIL_ID)).iterator();
		while (titleIter.hasNext()) {
			final String title = ((ColumnMetaData)titleIter.next()).getColName();

			if (titleIter.hasNext()) {
				this.writeWithDelimiter(title);
			} else {
				this.write(title);
			}
		}
	}

	protected void writeRow() throws IOException {
		Iterator titlesIter = null;
		if (FPConstants.DETAIL_ID.equals(getRecordId())) {
			titlesIter = ((List)this.columnMapping.get(FPConstants.DETAIL_ID)).iterator();
		} else {
			final XMLRecordElement xmlRec = (XMLRecordElement)this.columnMapping.get(getRecordId());
			titlesIter = xmlRec.getColumns().iterator();
		}
		while (titlesIter.hasNext()) {
			final String columnTitle = ((ColumnMetaData)titlesIter.next()).getColName();
			if (getRowMap() != null) {
				if (titlesIter.hasNext()) {
					writeWithDelimiter(getRowMap().get(columnTitle));
				} else {
					write(getRowMap().get(columnTitle));
				}
			}
		}
	}
	

	public final void nextRecord() throws IOException {
		if (!writerOptions.isNoColumnMappings()) {
			if (!columnTitlesWritten) {
				this.writeColumnTitles();
				columnTitlesWritten = true;
			} else {
				this.writeRow();
			}
		} 
		
		delimitNextEntry = false;		
		super.nextRecord();
	}

	protected boolean validateColumnTitle(final String columnTitle) {
		//return columnTitles.contains(columnTitle);
		
		//the column index for <record> elements is stored in the Map as the COL_IDX constant_<record id attribute>
		final Map columnNameToIndex = (Map) columnMapping
				.get(getRecordId().equals(FPConstants.DETAIL_ID) ? FPConstants.COL_IDX : FPConstants.COL_IDX + "_" + getRecordId());
		if (columnNameToIndex == null) {
			//TODO this needs to be moved to the AbstractWriter, but the columnMapping is not exposed to it currently
			//This should only happen if the getRecordId() contained an invalid record id
			throw new RuntimeException("The record ID[" + getRecordId() + "] Is Not Mapped");
		}
		return columnNameToIndex.keySet().contains(columnTitle);
	}
}
