package net.sf.flatpack.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.flatpack.util.FPConstants;

/**
 * This class encapsulates the writer that's used to output the data.
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public abstract class AbstractWriter extends Object implements Writer {
    private final BufferedWriter writer;
    
    private Map rowMap; //used when using column headers
    
    //the record ID that is currently being written. The default is the DETAIL_ID which are <columns> that are mapped outside of <record> elements
    private String recordId = FPConstants.DETAIL_ID;

    public AbstractWriter(final java.io.Writer output) {
        super();
        writer = new BufferedWriter(output);
    }

    public void addRecordEntry(final String columnName, final Object value) {
        if (rowMap == null) {
            rowMap = new HashMap();
        }

        if (!validateColumnTitle(columnName)) {
            throw new IllegalArgumentException("unknown column: \"" + columnName + "\"");
        }
        rowMap.put(columnName, value);
    }
    
    //TODO implement writing for no column titles
//    public void addRecordEntry(final Object value) {
//    	if (
//    	
//        if (rowMap == null) {
//            rowMap = new HashMap();
//        }
//
//        if (!validateColumnTitle(columnName)) {
//            throw new IllegalArgumentException("unknown column: \"" + columnName + "\"");
//        }
//        rowMap.put(columnName, value);
//    }

    /**
     * Subclasses must implement this method to perform validation of
     * <code>columnTitle</code>.
     * 
     * @param columnTitle title of the column to be filled
     * @return <code>true</code> if the column title is valid else return
     *         <code>false</code>.
     */
    protected abstract boolean validateColumnTitle(String columnTitle);

    /**
     * Writes a newline to the output and discards the <code>rowMap</code>.
     * <p>
     * This method must be overridden by subclasses to write out the record data
     * stored in <code>rowMap</code>. Overriders <b>must</b> call
     * <code>super.nextRecord()</code> as the last call in their implementation.
     */
    public void nextRecord() throws IOException {
        // the row should have been written out by the subclass so it's safe to
        // discard it here
        rowMap = null;
        //default the recordId when we go to the next record
        recordId = FPConstants.DETAIL_ID;
        writer.newLine();
    }

    protected void write(final Object val) throws IOException {
        Object value = val;
        if (value == null) {
            value = "";
        }
        // TODO DO: converter/formatter for converting value to string?
        writer.write(value.toString());
    }

    protected void write(final char character) throws IOException {
        writer.write(character);
    }

    protected void write(final char[] characters) throws IOException {
        writer.write(characters);
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
    

    protected Map getRowMap() {
        return rowMap;
    }

	/**
	 * @return the recordId
	 */
	public String getRecordId() {
		return recordId;
	}

	/**
	 * Sets the record ID for the record that is being written.  This should be used when mapping <record> elements.  
	 * The "id" attribute of the record element should be specified here and needs to be called before calling addRecordEntry().
	 * This will throw an exception if addRecordEntry() has been called for the record currently being processed.
	 * 
	 * @param recordId the recordId to set
	 */
	public void setRecordId(String recordId) {
		if (rowMap != null && !rowMap.isEmpty()) {
			throw new RuntimeException("addRecordEntry() has already been called for this row.  Please set the record id prior to adding data to this row.");
		}
		this.recordId = recordId;
	}
}
