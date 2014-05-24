package net.sf.flatpack.writer;

import java.io.IOException;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public interface Writer {
    /** Export header */
	Writer printHeader() throws IOException;

    /** Export footer */
	Writer printFooter() throws IOException;

	Writer addRecordEntry(String columnName, Object value);

	Writer nextRecord() throws IOException;

	Writer flush() throws IOException;

	Writer close() throws IOException;
}
