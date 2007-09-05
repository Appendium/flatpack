package net.sf.flatpack.writer;

import java.io.IOException;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public interface WriterFactory {
    Writer createWriter(java.io.Writer out) throws IOException;
}
