package net.sf.flatpack.writer;

import java.io.IOException;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public interface Writer {
    /** put writer into header mode. TODO: how to handle multiple header lines? */
    void printHeader();

    /** put writer into footer mode. TODO: how to handle multiple footer lines? */
    void printFooter();

    void addRecordEntry(String columnName, Object value);

    void nextRecord() throws IOException;

    void flush() throws IOException;

    void close() throws IOException;
}
