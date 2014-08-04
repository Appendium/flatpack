package net.sf.flatpack.brparse;

import net.sf.flatpack.DefaultDataSet;
import net.sf.flatpack.structure.Row;

/**
 * All buffered reader parsers should implement this interface and provide
 * an implementation for the buildRow
 *
 * @author Paul Zepernick
 */
public interface InterfaceBuffReaderParse {

    /**
     * Builds a row into the DataSet using the current record from the File
     *
     * @param ds
     * @return Row object
     */
    public Row buildRow(final DefaultDataSet ds);

}
