package net.sf.flatpack.writer;

import java.io.IOException;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public interface Writer {
    /** Export header */
    void printHeader() throws IOException;

    /** Export footer */
    void printFooter() throws IOException;

    void addRecordEntry(String columnName, Object value);

    void nextRecord() throws IOException;

    void flush() throws IOException;

    void close() throws IOException;
}
