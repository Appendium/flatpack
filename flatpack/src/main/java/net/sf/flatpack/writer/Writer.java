package net.sf.flatpack.writer;

import java.io.IOException;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public interface Writer {

	void addRecordEntry(String columnName, Object value);

    void nextRecord() throws IOException;

    void flush() throws IOException;

    void close() throws IOException;
    
	/**
	 * Sets the record ID for the record that is being written.  This should be used when mapping <record> elements.  
	 * The "id" attribute of the record element should be specified here and needs to be called before calling addRecordEntry().
	 * This will throw an exception if addRecordEntry() has been called for the record currently being processed.
	 * 
	 * @param recordId the recordId to set
	 */
    void setRecordId(String recordId);
}
